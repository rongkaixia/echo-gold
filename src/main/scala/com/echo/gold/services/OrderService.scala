package com.echo.gold

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future}
import scala.async.Async.{async, await}

import com.typesafe.scalalogging.{Logger, LazyLogging}
import com.typesafe.config.{Config, ConfigFactory}
import com.trueaccord.scalapb.json.JsonFormat

import org.mongodb.scala._
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Projections._
import org.mongodb.scala.model.Updates._
import com.mongodb.connection.ClusterSettings
import com.echo.protocol.gold._
import com.echo.protocol.common._

class OrderServiceException(message: String = null, cause: Throwable = null) extends
  RuntimeException(OrderServiceException.defaultMessage(message, cause), cause)

object OrderServiceException {
  def defaultMessage(message: String, cause: Throwable) = {
    if (message != null) message
    else if (cause != null) cause.toString()
    else null
  }

  class OrderNotExist(id: String) extends OrderServiceException(s"order not existed for orderId[${id}]")
}


trait AbstractOrderService extends OrderServiceGrpc.OrderService with LazyLogging{
  implicit val ec: ExecutionContext
  val mongo: MongoClient
  val cfg: Config
  
  // we need this, because scalapb JsonFormat.fromJsonString[OrderInfo] cannot parse MongoDB Extended JSON format
  def doc2OrderInfo(doc: Document): OrderInfo = {
      val createAtColumn = cfg.getString("echo.gold.mongo.order.columns.create_at")
      val updateAtColumn = cfg.getString("echo.gold.mongo.order.columns.update_at")
      val expireAtColumn = cfg.getString("echo.gold.mongo.order.columns.expire_at")
      val payAtColumn = cfg.getString("echo.gold.mongo.order.columns.pay_at")
      val deliverAtColumn = cfg.getString("echo.gold.mongo.order.columns.deliver_at")
      val deliverconfirmAtColumn = cfg.getString("echo.gold.mongo.order.columns.deliver_confirm_at")
      val refundAtColumn = cfg.getString("echo.gold.mongo.order.columns.refund_at")
      val refundConfirmAtColumn = cfg.getString("echo.gold.mongo.order.columns.refund_confirm_at")
      val cancelAtColumn = cfg.getString("echo.gold.mongo.order.columns.cancel_at")
      val newDoc = doc - createAtColumn - updateAtColumn - expireAtColumn - payAtColumn -
                   deliverAtColumn - deliverconfirmAtColumn - refundAtColumn - refundConfirmAtColumn - 
                   cancelAtColumn
      JsonFormat.fromJsonString[OrderInfo](newDoc.toJson)
      .withCreateAt(doc.getOrElse(createAtColumn, new bson.BsonDateTime(0l)).asDateTime.getValue)
      .withUpdateAt(doc.getOrElse(updateAtColumn, new bson.BsonDateTime(0l)).asDateTime.getValue)
      .withExpireAt(doc.getOrElse(expireAtColumn, new bson.BsonDateTime(0l)).asDateTime.getValue)
      .withPayAt(doc.getOrElse(payAtColumn, new bson.BsonDateTime(0l)).asDateTime.getValue)
      .withDeliverAt(doc.getOrElse(deliverAtColumn, new bson.BsonDateTime(0l)).asDateTime.getValue)
      .withDeliverConfirmAt(doc.getOrElse(deliverconfirmAtColumn, new bson.BsonDateTime(0l)).asDateTime.getValue)
      .withRefundAt(doc.getOrElse(refundAtColumn, new bson.BsonDateTime(0l)).asDateTime.getValue)
      .withRefundConfirmAt(doc.getOrElse(refundConfirmAtColumn, new bson.BsonDateTime(0l)).asDateTime.getValue)
      .withCancelAt(doc.getOrElse(cancelAtColumn, new bson.BsonDateTime(0l)).asDateTime.getValue)
  } 

  def queryOrderInfo(orderId: String): Future[OrderInfo] = {
    async{
      val dbName = cfg.getString("echo.gold.mongo.order.db")
      val collectionName = cfg.getString("echo.gold.mongo.order.collection")
      val orderIdColumn = cfg.getString("echo.gold.mongo.order.columns.order_id")
      val stateColumn = cfg.getString("echo.gold.mongo.order.columns.state")
      val createAtColumn = cfg.getString("echo.gold.mongo.order.columns.create_at")
      val updateAtColumn = cfg.getString("echo.gold.mongo.order.columns.update_at")
      val database: MongoDatabase = mongo.getDatabase(dbName)
      val collection = database.getCollection(collectionName)

      val filterOp = equal(orderIdColumn, orderId)
      val projectionOp = exclude("_id")
      val result = await(collection.find(filterOp).projection(projectionOp).first().toFuture)
      if (result.size != 1) {
        logger.debug(s"queryOrderInfo error: order not exist for orderId[${orderId}]")
        throw new OrderServiceException.OrderNotExist(orderId)
      }
      logger.debug(s"orderInfo: ${result.head}")
      // remove createAt and updateAt column, since JsonFormat.fromJsonString[OrderInfo]
      // cannot parse MongoDB Extended JSON format
      val doc = result.head
      doc2OrderInfo(doc)
    }
  }

  def queryState(orderId: String): Future[OrderState] = {
    async{
      val dbName = cfg.getString("echo.gold.mongo.order.db")
      val collectionName = cfg.getString("echo.gold.mongo.order.collection")
      val orderIdColumn = cfg.getString("echo.gold.mongo.order.columns.order_id")
      val stateColumn = cfg.getString("echo.gold.mongo.order.columns.state")
      val database: MongoDatabase = mongo.getDatabase(dbName)
      val collection = database.getCollection(collectionName)

      val filterOp = equal(orderIdColumn, orderId)
      val projectionOp = include(stateColumn)
      val result = await(collection.find(filterOp).projection(projectionOp).first().toFuture)
      if (result.size != 1) {
        logger.debug(s"queryState error: order not exist for orderId[${orderId}]")
        throw new OrderServiceException.OrderNotExist(orderId)
      }
      if (!result.head.get(stateColumn).isDefined) {
        logger.error(s"queryState error: order state not exist for orderId[${orderId}]")
        throw new RuntimeException(s"queryState error: order state not exist for orderId[${orderId}]")
      }
      result.head.get(stateColumn).get.asString.getValue match {
        case v if v == OrderState.UNPAY.toString => OrderState.UNPAY
        case v if v == OrderState.PAY_SUCCESS.toString => OrderState.PAY_SUCCESS
        case v if v == OrderState.PAY_ERROR.toString => OrderState.PAY_ERROR
        case v if v == OrderState.DELIVER.toString => OrderState.DELIVER
        case v if v == OrderState.DELIVER_CONFIRM.toString => OrderState.DELIVER_CONFIRM
        case v if v == OrderState.REFUND.toString => OrderState.REFUND
        case v if v == OrderState.REFUND_CONFIRM.toString => OrderState.REFUND_CONFIRM
        case v if v == OrderState.CANCELLED.toString => OrderState.CANCELLED
      }
    }
  }

  def changeState(orderId: String, oldState: OrderState, newState: OrderState): Future[Boolean] = {
    async{
      val dbName = cfg.getString("echo.gold.mongo.order.db")
      val collectionName = cfg.getString("echo.gold.mongo.order.collection")
      val stateColumn = cfg.getString("echo.gold.mongo.order.columns.state")
      val orderIdColumn = cfg.getString("echo.gold.mongo.order.columns.order_id")
      val database: MongoDatabase = mongo.getDatabase(dbName)
      val collection = database.getCollection(collectionName)

      val updateOp = and(equal(stateColumn, oldState.toString), equal(orderIdColumn, orderId))
      val setOp = set(stateColumn, newState.toString)
      val result = await(collection.updateOne(updateOp, setOp).toFuture)
      if (result.head.getModifiedCount == 1) {
        true
      }else{
        false
      }
    }
  }

}

class OrderService() 
  extends QueryOrderImpl
  with QueryOrderWithUserImpl
  with OrderImpl 
  with NotifyImpl
  with DeliverImpl
  with DeliverConfirmImpl{

  // execution context
  val ec = ExecutionContext.Implicits.global
  // config
  val cfg = ConfigFactory.load()
  // init mongodb
  val mongo = initMongo
  
  private def initMongo(): MongoClient = {
    val host = cfg.getString("echo.gold.mongo.host")
    val port = cfg.getInt("echo.gold.mongo.port")
    logger.info(s"mongodb[host=${host}, port=${port}")
    val clusterSettings: ClusterSettings = 
      ClusterSettings.builder().hosts(List(new ServerAddress(host, port)).asJava).build()
    val settings: MongoClientSettings = 
      MongoClientSettings.builder().clusterSettings(clusterSettings).build()
    MongoClient(settings)
  }
}