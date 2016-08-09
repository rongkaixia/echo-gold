package com.echo.gold

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future}
import scala.async.Async.{async, await}

import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.{Logger, LazyLogging}
import org.slf4j.LoggerFactory
import com.trueaccord.scalapb.json.JsonFormat

import org.mongodb.scala._
import com.mongodb.connection.ClusterSettings
import org.mongodb.scala.model.Projections._
import org.bson.types.ObjectId

import com.echo.gold.utils.LazyConfig
import com.echo.gold.protocol._
// import org.mongodb.scala.bson._

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
              realPayAmt = realPayAmt)
  }

  private def saveToMongo(orderInfo: OrderInfo): Unit = {
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
  override def order(req: OrderRequest) = {
    logger.debug(s"recieve request: ${req}")
    // check request
    
    // generate order id
    val id = new ObjectId

    // pricing
    val orderInfo = pricing(req)

    // write to db
    orderInfo.withOrderId(id.toString)
    saveToMongo(orderInfo)

    // send response
    val header = ResponseHeader(ResultCode.SUCCESS, "")
    val reply = OrderResponse().withHeader(header).withOrderInfo(orderInfo)
    Future.successful(reply)
  }
}