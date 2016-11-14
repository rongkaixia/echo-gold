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

trait QueryOrderWithUserImpl extends AbstractOrderService with LazyLogging{

  def queryOrders(userId: String): Future[Seq[OrderInfo]] = {
    async{
      val dbName = cfg.getString("echo.gold.mongo.order.db")
      val collectionName = cfg.getString("echo.gold.mongo.order.collection")
      val userIdColumn = cfg.getString("echo.gold.mongo.order.columns.user_id")
      val stateColumn = cfg.getString("echo.gold.mongo.order.columns.state")
      val createAtColumn = cfg.getString("echo.gold.mongo.order.columns.create_at")
      val updateAtColumn = cfg.getString("echo.gold.mongo.order.columns.update_at")
      val database: MongoDatabase = mongo.getDatabase(dbName)
      val collection = database.getCollection(collectionName)

      val filterOp = equal(userIdColumn, userId)
      val projectionOp = exclude("_id")
      val result = await(collection.find(filterOp).projection(projectionOp).toFuture)
      if (result.size < 1) {
        logger.debug(s"order not exist for userId[${userId}]")
        Seq[OrderInfo]()
      } else {
        logger.debug(s"orderInfo: ${result}")
        result.map(doc => {
          // remove createAt and updateAt column, since JsonFormat.fromJsonString[OrderInfo]
          // cannot parse MongoDB Extended JSON format
          val createAt = doc.get(createAtColumn).get.asInt64.getValue
          val updateAt = doc.get(updateAtColumn).get.asInt64.getValue
          val orderInfo = JsonFormat.fromJsonString[OrderInfo]((doc - createAtColumn - updateAtColumn).toJson)
          // append updateAt and createAt info
          orderInfo.withCreateAt(createAt).withUpdateAt(updateAt)
        })
      }
    }
  }

  /**
   * queryOrderWithUser interface
   *
   * @param  QueryOrderWithUserRequest
   * @return QueryOrderWithUserResponse
   */
  override def queryOrderWithUser(req: QueryOrderWithUserRequest): Future[QueryOrderWithUserResponse] = {
    val replyPromise = Promise[QueryOrderWithUserResponse]()
    logger.debug(s"recieve queryOrderWithUser request: ${req}")
    val fut = async{
      var res = QueryOrderWithUserResponse()
      // check request
      
      // get user id
      val id = req.userId

      // query
      val orders = await(queryOrders(id))
      
      // response
      val header = ResponseHeader(ResultCode.SUCCESS, "ok")
      res = res.withHeader(header).withOrderInfo(orders)
      replyPromise success res
    }

    // exception, because await must not be used under a try/catch.
    fut.onFailure {
      case x: OrderServiceException.OrderNotExist =>
        logger.debug(x.toString)
        val header = ResponseHeader(ResultCode.ORDER_NOT_EXISTED, x.toString)
        replyPromise success QueryOrderWithUserResponse().withHeader(header)
      case error: Throwable => 
        logger.error(s"queryOrderWithUser error: ${error}")
        val header = ResponseHeader(ResultCode.INTERNAL_SERVER_ERROR, error.toString)
        replyPromise success QueryOrderWithUserResponse().withHeader(header)
    }

    // send response
    replyPromise.future
  }
}
