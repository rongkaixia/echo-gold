package com.echo.gold

import java.util.logging.Logger

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._

import io.grpc.{Server, ServerBuilder}
import scala.concurrent.{ExecutionContext, Future}
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import com.echo.gold.protocol._

import org.mongodb.scala.{MongoClient, 
                          MongoDatabase,
                          MongoClientSettings, 
                          ServerAddress}
import com.mongodb.connection._

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
    // init mongodb
    implicit val mongoClient: MongoClient = initMongo()
    server = ServerBuilder.forPort(OrderServer.port)
                          .addService(OrderServiceGrpc.bindService(new OrderImpl, executionContext))
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

  private def initMongo(): MongoClient = {
    val host = cfg.getString("echo.gold.mongo.host")
    val port = cfg.getInt("echo.gold.mongo.port")
    logger.info(s"mongodb[host=${host}, port=${port}")
    val clusterSettings: ClusterSettings = 
      ClusterSettings.builder().hosts(List(new ServerAddress(host, port)).asJava).build()
    val settings: MongoClientSettings = 
      MongoClientSettings.builder().clusterSettings(clusterSettings).build()
    MongoClient(settings)
  }

  private class OrderImpl()(implicit mongo: MongoClient) 
    extends OrderServiceGrpc.OrderService with LazyLogging {

    private def pricing(req: OrderRequest): OrderInfo = {
      val price = 1000.00
      val realPrice = 1000.00
      val discount = 0.0
      val payAmt = realPrice * req.num
      val realPayAmt = payAmt + discount

      OrderInfo(userId = req.userId,
                title = req.title,
                productId = req.productId,
                num = req.num,
                payMethod = req.payMethod,
                deliverMethod = req.deliverMethod,
                recipientsName = req.recipientsName,
                recipientsPhone = req.recipientsPhone,
                recipientsAddress = req.recipientsAddress,
                recipientsPostcode = req.recipientsPostcode,
                comment = req.comment,
                price = price,
                realPrice = realPrice,
                discount = discount,
                payAmt = payAmt,
                realPayAmt = realPayAmt)
    }

    private def saveToMongo(): Unit = {
      val database: MongoDatabase = mongo.getDatabase("mydb")
      logger.debug(s"mongo database: ${database}")
    }

    override def order(req: OrderRequest) = {
      logger.debug(s"recieve request: ${req}")
      // check request
      
      // generate order id
      val id = 
      // pricing
      val orderInfo = pricing(req)

      // write to db
      saveToMongo(orderInfo)

      // send response
      val header = ResponseHeader(ResultCode.SUCCESS, "")
      val reply = OrderResponse().withHeader(header)
      Future.successful(reply)
    }
  }

}
