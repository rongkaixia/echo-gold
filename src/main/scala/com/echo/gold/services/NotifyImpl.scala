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

trait NotifyImpl extends AbstractOrderService with LazyLogging{
  /**
   * pay interface
   *
   * @param  NotifyRequest
   * @return NotifyResponse
   */
  override def notify(req: NotifyRequest): Future[NotifyResponse] = {
    logger.debug(s"recieve pay request: ${req}")
    // check request
    
    // pay
    
    // send response
    val header = ResponseHeader(ResultCode.SUCCESS, "ok")
    val reply = NotifyResponse().withHeader(header)
    Future.successful(reply)
  }
}
