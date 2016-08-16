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

trait DeliverImpl extends AbstractOrderService with LazyLogging{
  /**
   * deliver interface
   *
   * @param  DeliverRequest
   * @return DeliverResponse
   */
  override def deliver(req: DeliverRequest): Future[DeliverResponse] = {
    async{
      var reply = DeliverResponse()
      logger.debug(s"recieve deliver request: ${req}")
      // check request
      
      // change state
      val ret = await(changeState(req.orderId, OrderState.PAY_SUCCESS, OrderState.DELIVER))
      if (!ret) { 
        val currentState = queryState(req.orderId)
        logger.error(s"cannot change order state from ${currentState} to ${OrderState.DELIVER.toString}" +
                     ", order state MUST BE PAY_SUCCESS for deliver method.")
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
