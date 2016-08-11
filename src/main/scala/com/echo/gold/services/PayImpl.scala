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
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Projections._
import org.mongodb.scala.model.Updates._
import org.bson.types.ObjectId

import com.echo.gold.utils.LazyConfig
import com.echo.gold.protocol._

trait PayImpl extends AbstractOrderService with LazyLogging{

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
        throw new NoSuchElementException(s"queryState error: order not exist for orderId[${orderId}]")
      }
      if (!result.head.get(stateColumn).isDefined) {
        throw new NoSuchElementException(s"queryState error: order state not exist for orderId[${orderId}]")
      }
      result.head.get(stateColumn).get.asString.getValue match {
        case v if v == OrderState.PAY_ERROR.toString => OrderState.PAY_ERROR
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
  /**
   * pay interface
   *
   * @type  req PayRequest
   * @return PayResponse
   */
  override def pay(req: PayRequest): Future[PayResponse] = {
    logger.debug(s"recieve pay request: ${req}")
    // check request
    
    // pay
    
    // send response
    val header = ResponseHeader(ResultCode.SUCCESS, "ok")
    val reply = PayResponse().withHeader(header)
    Future.successful(reply)
  }
}
