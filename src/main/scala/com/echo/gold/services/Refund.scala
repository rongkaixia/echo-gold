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

trait RefundImpl extends AbstractOrderService with LazyLogging{
  /**
   * refund interface
   *
   * @param  RefundRequest
   * @return RefundResponse
   */
  override def refund(req: RefundRequest): Future[RefundResponse] = {
    var replyPromise = Promise[RefundResponse]()
    logger.debug(s"recieve refund request: ${req}")
    val fut = async{
      var res = RefundResponse()
      // check request
      
      // refund
      
      // change state
      val currentState = await(queryState(req.orderId))
      if (currentState != OrderState.PAY_SUCCESS && currentState != OrderState.DELIVER) {
        logger.info(s"cannot refund order with state ${currentState}" +
                        s", order state MUST BE PAY_SUCCESS or DELIVER for refund method.")
        val header = ResponseHeader(ResultCode.INVALID_ORDER_STATE, "invalid order state")
        res = res.withHeader(header)
      } else {
        await(changeState(req.orderId, currentState, OrderState.REFUND))
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
        replyPromise success RefundResponse().withHeader(header)
      case error: Throwable => 
        logger.error(s"refund error: ${error}")
        val header = ResponseHeader(ResultCode.INTERNAL_SERVER_ERROR, error.toString)
        replyPromise success RefundResponse().withHeader(header)
    }

    // send response
    replyPromise.future
  }
}
