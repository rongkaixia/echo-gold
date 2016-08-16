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

trait DeliverConfirmImpl extends AbstractOrderService with LazyLogging{
  /**
   * deliverConfirm interface
   *
   * @param  DeliverConfirmRequest
   * @return DeliverConfirmResponse
   */
  override def deliverConfirm(req: DeliverConfirmRequest): Future[DeliverConfirmResponse] = {
    async{
      var reply = DeliverConfirmResponse()
      logger.debug(s"recieve deliverConfirm request: ${req}")
      // check request
      
      // change state
      val ret = await(changeState(req.orderId, OrderState.DELIVER, OrderState.DELIVER_CONFIRM))
      if (!ret) { 
        val currentState = queryState(req.orderId)
        logger.error(s"cannot change order state from ${currentState} to ${OrderState.DELIVER_CONFIRM.toString}" +
                     ", order state MUST BE DELIVER_CONFIRM for deliverConfirm method.")
        val header = ResponseHeader(ResultCode.INVALID_ORDER_STATE, "invalid order state")
        reply = reply.withHeader(header)
      }else{
        val header = ResponseHeader(ResultCode.SUCCESS, "ok")
        reply = reply.withHeader(header)
      }
      reply
    }
  }
}
