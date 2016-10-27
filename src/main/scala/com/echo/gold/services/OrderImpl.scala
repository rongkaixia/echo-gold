package com.echo.gold

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.async.Async.{async, await}

import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.{Logger, LazyLogging}
import org.slf4j.LoggerFactory
import com.trueaccord.scalapb.json.JsonFormat

import org.mongodb.scala._
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Projections._
import org.mongodb.scala.model.Updates._
import org.bson.types.ObjectId

import com.echo.gold.utils.LazyConfig
import com.echo.protocol.gold._
import com.echo.protocol.common._

// TODO: 切换到统一批价服务器比较好，现在的架构前端跟后端的批价是分开的
trait OrderImpl extends AbstractOrderService with LazyLogging{

  // 因为mongoose的bug，不能20.00被当成Int保存了
  private def _toDouble(n: bson.BsonValue): Double = {
    if (n.isInt32) {
      n.asInt32.getValue.toDouble
    } else if (n.isInt64) {
      n.asInt64.getValue.toDouble
    } else if (n.isDouble) {
      n.asDouble.getValue
    } else {
      throw new RuntimeException(s"cannot convert ${n} to double")
    }
  }

  private def pricing(req: OrderRequest): Future[OrderInfo] = {
    async {
      val dbName = cfg.getString("echo.gold.mongo.product.db")
      val collectionName = cfg.getString("echo.gold.mongo.product.collection")
      val productIdColumn = cfg.getString("echo.gold.mongo.product.columns.product_id")
      val priceColumn = cfg.getString("echo.gold.mongo.product.columns.price")
      val realPriceColumn = cfg.getString("echo.gold.mongo.product.columns.real_price")
      logger.debug(s"mongo database = ${dbName}, collection = ${collectionName}")
      val database: MongoDatabase = mongo.getDatabase(dbName)
      val collection = database.getCollection(collectionName)

      val filterOp = equal(productIdColumn, new ObjectId(req.productId))
      val result = await(collection.find(filterOp).first().toFuture)
      if (result.size != 1) {
        logger.debug(s"pricing error: product not exist for productId[${req.productId}]")
        throw new RuntimeException(s"pricing error: product not exist for productId[${req.productId}]")
      }
      if (!result.head.get(priceColumn).isDefined) {
        logger.error(s"pricing error: priceColumn[${priceColumn}] not exists")
        throw new RuntimeException(s"pricing error: priceColumn[${priceColumn}] not exists")
      }
      if (!result.head.get(realPriceColumn).isDefined) {
        logger.error(s"pricing error: realPriceColumn[${realPriceColumn}] not exists")
        throw new RuntimeException(s"pricing error: realPriceColumn[${realPriceColumn}] not exists")
      }

      val price = _toDouble(result.head.get(priceColumn).get)
      val realPrice = _toDouble(result.head.get(priceColumn).get)
      val discount = 0.0
      val payAmt = realPrice * req.num
      val realPayAmt = payAmt + discount
      logger.debug(s"pricing result: price=${price}, realPrice=${realPrice}, discount=${discount}" + 
                s", payAmt =${payAmt}, realPayAmt=${realPayAmt}")

      OrderInfo(userId = req.userId,
                title = req.title,
                productId = req.productId,
                num = req.num,
                payMethod = req.payMethod,
                deliverMethod = req.deliverMethod,
                recipientsName = req.recipientsName,
                recipientsPhone = req.recipientsPhone,
                recipientsAddress = req.recipientsAddress,
                recipientsPostcode = req.recipientsPostcode,
                comment = req.comment,
                price = price,
                realPrice = realPrice,
                discount = discount,
                payAmt = payAmt,
                realPayAmt = realPayAmt,
                state = OrderState.UNPAY)
    }
  }

  private def saveToMongo(orderInfo: OrderInfo): Future[Unit] = {
    async{
      val dbName = cfg.getString("echo.gold.mongo.order.db")
      val collectionName = cfg.getString("echo.gold.mongo.order.collection")
      logger.debug(s"mongo database = ${dbName}, collection = ${collectionName}")
      val database: MongoDatabase = mongo.getDatabase(dbName)
      val collection = database.getCollection(collectionName)
      val doc = Document(JsonFormat.toJsonString(orderInfo))
      logger.debug(s"Document=${doc}")
      await(collection.insertOne(doc).toFuture)
    }
  }

  /**
   * order interface
   *
   * @type  req OrderRequest
   * @return OrderResponse
   */
  override def order(req: OrderRequest): Future[OrderResponse] = {
    val replyPromise = Promise[OrderResponse]()
    logger.debug(s"recieve order request: ${req}")
    val fut = async{
      var res = OrderResponse()
      // check request
      
      // generate order id
      val id = new ObjectId

      // pricing
      val orderInfo = await(pricing(req)).withOrderId(id.toString)

      // write to db
      await(saveToMongo(orderInfo))

      // response
      val header = ResponseHeader(ResultCode.SUCCESS, "ok")
      res = res.withHeader(header).withOrderInfo(orderInfo)
      replyPromise success res
    }

    // exception, because await must not be used under a try/catch.
    fut.onFailure {
      case error: Throwable => 
        logger.error(s"order error: ${error}")
        val header = ResponseHeader(ResultCode.INTERNAL_SERVER_ERROR, error.toString)
        replyPromise success OrderResponse().withHeader(header)
    }

    // send response
    replyPromise.future
  }
}