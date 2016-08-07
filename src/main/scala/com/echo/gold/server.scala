package com.echo.gold

import java.util.logging.Logger

import io.grpc.{Server, ServerBuilder}
import scala.concurrent.{ExecutionContext, Future}
import com.typesafe.config.ConfigFactory
import com.echo.gold.protocol._

import org.mongodb.scala.MongoClient

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

class OrderServer(executionContext: ExecutionContext) { self =>
  private[this] var server: Server = null
  val cfg = ConfigFactory.load()
  val log = Logger.getLogger(classOf[OrderServer].getName)
  val mongoClient: MongoClient = MongoClient("mongodb://localhost")

  private def start(): Unit = {
    server = ServerBuilder.forPort(OrderServer.port).addService(OrderServiceGrpc.bindService(new OrderImpl, executionContext)).build.start
    log.info("Server started, listening on " + OrderServer.port)
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

  private class OrderImpl extends OrderServiceGrpc.OrderService {
    override def order(req: OrderRequest) = {
      log.info(s"recieve request: ${req}")
      val header = ResponseHeader(ResultCode.SUCCESS, "")
      val reply = OrderResponse().withHeader(header)
      Future.successful(reply)
    }
  }

}
