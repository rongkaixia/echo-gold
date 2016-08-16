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

trait QueryOrderImpl extends AbstractOrderService with LazyLogging{
  /**
   * queryOrder interface
   *
   * @param  QueryOrderRequest
   * @return QueryOrderResponse
   */
  override def queryOrder(req: QueryOrderRequest): Future[QueryOrderResponse] = {
    val replyPromise = Promise[QueryOrderResponse]()
    logger.debug(s"recieve queryOrder request: ${req}")
    val fut = async{
      var res = QueryOrderResponse()
      // check request
      
      // get order id
      val id = req.orderId

      // query
      val orderInfo = await(queryOrderInfo(id))
      
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
        replyPromise success QueryOrderResponse().withHeader(header)
    }

    // send response
    replyPromise.future
  }
}
