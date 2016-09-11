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

trait NotifyImpl extends AbstractOrderService with LazyLogging{
  /**
   * notify interface
   *
   * @type  req PayRequest
   * @return NotifyResponse
   */
  override def notify(req: NotifyRequest): Future[NotifyResponse] = {
    var replyPromise = Promise[NotifyResponse]()
    logger.debug(s"recieve notify request: ${req}")
    val fut = async{
      var res = NotifyResponse()
      // check request
      
      // validate notify request
      
      // change state
      val currentState = await(queryState(req.orderId))
      val ret = await(changeState(req.orderId, OrderState.UNPAY, OrderState.PAY_SUCCESS))
      if (!ret) { 
        logger.error(s"cannot change order state from ${currentState} to ${OrderState.PAY_SUCCESS.toString}" +
                     ", order state MUST BE UNPAY for pay method.")
        val header = common.ResponseHeader(common.ResultCode.INVALID_ORDER_STATE, "invalid order state")
        res = res.withHeader(header)
      }else{
        val header = common.ResponseHeader(common.ResultCode.SUCCESS, "ok")
        res = res.withHeader(header)
      }
      // response
      replyPromise success res
    }

    // exception, because await must not be used under a try/catch.
    fut.onFailure {
      case x: OrderServiceException.OrderNotExist =>
        logger.debug(x.toString)
        val header = common.ResponseHeader(common.ResultCode.ORDER_NOT_EXISTED, x.toString)
        replyPromise success NotifyResponse().withHeader(header)
      case error: Throwable => 
        logger.error(s"pay error: ${error}")
        val header = common.ResponseHeader(common.ResultCode.INTERNAL_SERVER_ERROR, error.toString)
        replyPromise success NotifyResponse().withHeader(header)
    }

    // send response
    replyPromise.future
  }
}
