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

trait CancelImpl extends AbstractOrderService with LazyLogging{
  /**
   * cancel interface
   *
   * @param  CancelRequest
   * @return CancelResponse
   */
  override def cancel(req: CancelRequest): Future[CancelResponse] = {
    var replyPromise = Promise[CancelResponse]()
    logger.debug(s"recieve cancel request: ${req}")
    val fut = async{
      var res = CancelResponse()
      // check request
      
      // cancel
      
      // change state
      val currentState = await(queryState(req.orderId))
      if (currentState != OrderState.UNPAY && currentState != OrderState.PAY_ERROR) {
        logger.info(s"cannot cancel order with state ${currentState}" +
                        s", order state MUST BE UNPAY or PAY_ERROR for cancel method.")
        val header = ResponseHeader(ResultCode.INVALID_ORDER_STATE, "invalid order state")
        res = res.withHeader(header)
      } else {
        await(changeState(req.orderId, currentState, OrderState.CANCELLED))
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
        replyPromise success CancelResponse().withHeader(header)
      case error: Throwable => 
        logger.error(s"cancel error: ${error}")
        val header = ResponseHeader(ResultCode.INTERNAL_SERVER_ERROR, error.toString)
        replyPromise success CancelResponse().withHeader(header)
    }

    // send response
    replyPromise.future
  }
}
