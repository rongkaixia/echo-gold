package com.echo.gold

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future}
import scala.async.Async.{async, await}

import io.grpc.{Server, ServerBuilder}
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import com.trueaccord.scalapb.json.JsonFormat

import org.mongodb.scala._
import com.mongodb.connection.ClusterSettings
import org.mongodb.scala.model.Projections._
import org.bson.types.ObjectId

import com.echo.gold.utils.LazyConfig
import com.echo.protocol.gold._
// import org.mongodb.scala.bson._

/**
 * [[https://github.com/grpc/grpc-java/blob/v0.13.2/examples/src/main/java/io/grpc/examples/helloworld/OrderServer.java]]
 */
object OrderServer {
  def main(args: Array[String]): Unit = {
    val server = new OrderServer(ExecutionContext.global)
    server.start()
    server.blockUntilShutdown()
  }

  private val port = 50051
}

class OrderServer(executionContext: ExecutionContext) extends LazyLogging{ self =>
  private[this] var server: Server = null
  val cfg = ConfigFactory.load()
  System.setProperty("log4j.configuration", "log4j.xml");

  private def start(): Unit = {
    server = ServerBuilder.forPort(OrderServer.port)
                          .addService(OrderServiceGrpc.bindService(new OrderService, executionContext))
                          .build
                          .start
    logger.info("Server started, listening on " + OrderServer.port)
    Runtime.getRuntime.addShutdownHook(new Thread() {
      override def run(): Unit = {
        System.err.println("*** shutting down gRPC server since JVM is shutting down")
        self.stop()
        System.err.println("*** server shut down")
      }
    })
  }

  private def stop(): Unit = {
    if (server != null) {
      server.shutdown()
    }
  }

  private def blockUntilShutdown(): Unit = {
    if (server != null) {
      server.awaitTermination()
    }
  }
}
