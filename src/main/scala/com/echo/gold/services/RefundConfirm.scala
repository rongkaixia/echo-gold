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

trait RefundConfirmImpl extends AbstractOrderService with LazyLogging{
  /**
   * refundConfirm interface
   *
   * @param  RefundConfirmRequest
   * @return RefundConfirmResponse
   */
  override def refundConfirm(req: RefundConfirmRequest): Future[RefundConfirmResponse] = {
    var replyPromise = Promise[RefundConfirmResponse]()
    logger.debug(s"recieve refundConfirm request: ${req}")
    val fut = async{
      var res = RefundConfirmResponse()
      // check request
      
      // refund
      
      // change state
      val currentState = await(queryState(req.orderId))
      val ret = await(changeState(req.orderId, OrderState.REFUND, OrderState.REFUND_CONFIRM))
      if (!ret) { 
        logger.error(s"cannot change order state from ${currentState} to ${OrderState.REFUND_CONFIRM.toString}" +
                     ", order state MUST BE REFUND for refundConfirm method.")
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
        replyPromise success RefundConfirmResponse().withHeader(header)
      case error: Throwable => 
        logger.error(s"refundConfirm error: ${error}")
        val header = ResponseHeader(ResultCode.INTERNAL_SERVER_ERROR, error.toString)
        replyPromise success RefundConfirmResponse().withHeader(header)
    }

    // send response
    replyPromise.future  
  }
}
