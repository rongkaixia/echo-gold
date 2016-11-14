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

trait DeliverImpl extends AbstractOrderService with LazyLogging{
  /**
   * deliver interface
   *
   * @param  DeliverRequest
   * @return DeliverResponse
   */
  override def deliver(req: DeliverRequest): Future[DeliverResponse] = {
    var replyPromise = Promise[DeliverResponse]()
    logger.debug(s"recieve deliver request: ${req}")
    val fut = async{
      var res = DeliverResponse()
      // check request
      
      // deliver
      
      // change state
      val currentState = await(queryState(req.orderId))
      val ret = await(changeState(req.orderId, OrderState.PAY_SUCCESS, OrderState.DELIVER))
      if (!ret) { 
        logger.error(s"cannot change order state from ${currentState} to ${OrderState.DELIVER.toString}" +
                     ", order state MUST BE PAY_SUCCESS for deliver method.")
        val header = ResponseHeader(ResultCode.INVALID_ORDER_STATE, "invalid order state")
        res = res.withHeader(header)
      }else{
        val header = ResponseHeader(ResultCode.SUCCESS, "ok")
        res = res.withHeader(header)
      }
      // response
      replyPromise success res
    }

    // exception, because await must not be used under a try/catch.
    fut.onFailure {
      case x: OrderServiceException.OrderNotExist =>
        logger.debug(x.toString)
        val header = ResponseHeader(ResultCode.ORDER_NOT_EXISTED, x.toString)
        replyPromise success DeliverResponse().withHeader(header)
      case error: Throwable => 
        logger.error(s"deliver error: ${error}")
        val header = ResponseHeader(ResultCode.INTERNAL_SERVER_ERROR, error.toString)
        replyPromise success DeliverResponse().withHeader(header)
    }

    // send response
    replyPromise.future
  }
}
