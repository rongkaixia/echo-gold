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
import com.echo.gold.protocol._

trait OrderImpl extends AbstractOrderService with LazyLogging{

  private def pricing(req: OrderRequest): OrderInfo = {
    val price = 1000.00
    val realPrice = 1000.00
    val discount = 0.0
    val payAmt = realPrice * req.num
    val realPayAmt = payAmt + discount

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
      val orderInfo = pricing(req).withOrderId(id.toString)

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