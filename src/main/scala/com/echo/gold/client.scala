package com.echo.gold

import java.util.concurrent.TimeUnit
import java.util.logging.{Level, Logger}

import io.grpc.{StatusRuntimeException, ManagedChannelBuilder, ManagedChannel}
import com.echo.gold.protocol._

/**
 * [[https://github.com/grpc/grpc-java/blob/v0.13.2/examples/src/main/java/io/grpc/examples/helloworld/HelloWorldClient.java]]
 */
object HelloWorldClient {
  def apply(host: String, port: Int): HelloWorldClient = {
    val channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext(true).build
    val blockingStub = OrderServiceGrpc.blockingStub(channel)
    new HelloWorldClient(channel, blockingStub)
  }

  def main(args: Array[String]): Unit = {
    val client = HelloWorldClient("localhost", 50051)
    try {
      val user = args.headOption.getOrElse("world")
      client.greet(user)
    } finally {
      client.shutdown()
    }
  }
}

class HelloWorldClient private(
  private val channel: ManagedChannel,
  private val blockingStub: OrderServiceGrpc.OrderServiceBlockingStub
) {
  private[this] val logger = Logger.getLogger(classOf[HelloWorldClient].getName)

  def shutdown(): Unit = {
    channel.shutdown.awaitTermination(5, TimeUnit.SECONDS)
  }

  /** Say hello to server. */
  def greet(name: String): Unit = {
    logger.info("Will try to send order request...")
    val request = OrderRequest().withUserId("12345")
                                .withTitle("测试商品")
                                .withProductId("00001")
                                .withNum(1)
                                .withPayMethod(PayMethod.ONLINE)
                                .withDeliverMethod(DeliverMethod.EXPRESS)
                                .withRecipientsName("rk")
                                .withRecipientsPhone("15002029322")
                                .withRecipientsAddress("15002029322")
                                .withRecipientsPostcode("518400")
    try {
      val response = blockingStub.order(request)
      logger.info("Response: " + response)
    }
    catch {
      case e: StatusRuntimeException =>
        logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus)
    }
  }
}