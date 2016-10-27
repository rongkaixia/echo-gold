package com.echo.gold

import java.util.concurrent.TimeUnit
import java.util.logging.{Level, Logger}

import io.grpc.{StatusRuntimeException, ManagedChannelBuilder, ManagedChannel}
import com.echo.protocol.gold._

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
      client.order()
      client.notifyy()
      client.queryOrderInfo()
      client.deliver()
      client.queryOrderInfo()
      client.deliverConfirm()
      client.queryOrderInfo()

      client.queryOrderInfoWithUser()
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

  var orderInfo: OrderInfo = null
  def shutdown(): Unit = {
    channel.shutdown.awaitTermination(5, TimeUnit.SECONDS)
  }

  def order(): Unit = {
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
      orderInfo = response.orderInfo.get
      logger.info("OrderResponse: " + response)
    }
    catch {
      case e: StatusRuntimeException =>
        logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus)
    }
  }

  def notifyy(): Unit = {
    logger.info("Will try to send notify request...")
    val request = NotifyRequest().withOrderId(orderInfo.orderId)
    try {
      val response = blockingStub.notify(request)
      logger.info("NotifyResponse: " + response)
    }
    catch {
      case e: StatusRuntimeException =>
        logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus)
    }
  }

  def queryOrderInfo(): Unit = {
    logger.info("Will try to send queryOrderInfo request...")
    val request = QueryOrderRequest().withOrderId(orderInfo.orderId)
    try {
      val response = blockingStub.queryOrder(request)
      logger.info("QueryOrderResponse: " + response)
    }
    catch {
      case e: StatusRuntimeException =>
        logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus)
    }
  }

  def queryOrderInfoWithUser(): Unit = {
    logger.info("Will try to send queryOrderInfoWithUser request...")
    val request = QueryOrderWithUserRequest().withUserId(orderInfo.userId)
    try {
      val response = blockingStub.queryOrderWithUser(request)
      logger.info("QueryOrderWithUserResponse: " + response)
    }
    catch {
      case e: StatusRuntimeException =>
        logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus)
    }
  }

  def deliver(): Unit = {
    logger.info("Will try to send deliver request...")
    val request = DeliverRequest().withOrderId(orderInfo.orderId)
    try {
      val response = blockingStub.deliver(request)
      logger.info("DeliverResponse: " + response)
    }
    catch {
      case e: StatusRuntimeException =>
        logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus)
    }
  }

  def deliverConfirm(): Unit = {
    logger.info("Will try to send deliverConfirm request...")
    val request = DeliverConfirmRequest().withOrderId(orderInfo.orderId)
    try {
      val response = blockingStub.deliverConfirm(request)
      logger.info("DeliverConfirmResponse: " + response)
    }
    catch {
      case e: StatusRuntimeException =>
        logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus)
    }
  }

}