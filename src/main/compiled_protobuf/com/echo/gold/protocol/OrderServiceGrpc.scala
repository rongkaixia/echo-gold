package com.echo.gold.protocol

object OrderServiceGrpc {

  val METHOD_ORDER: _root_.io.grpc.MethodDescriptor[com.echo.gold.protocol.OrderRequest, com.echo.gold.protocol.OrderResponse] =
    _root_.io.grpc.MethodDescriptor.create(
      _root_.io.grpc.MethodDescriptor.MethodType.UNARY,
      _root_.io.grpc.MethodDescriptor.generateFullMethodName("com.echo.gold.OrderService", "Order"),
      new com.trueaccord.scalapb.grpc.Marshaller(com.echo.gold.protocol.OrderRequest),
      new com.trueaccord.scalapb.grpc.Marshaller(com.echo.gold.protocol.OrderResponse))
  
  trait OrderService {
    def order(request: com.echo.gold.protocol.OrderRequest): scala.concurrent.Future[com.echo.gold.protocol.OrderResponse]

  }
  
  trait OrderServiceBlockingClient {
    def order(request: com.echo.gold.protocol.OrderRequest): com.echo.gold.protocol.OrderResponse

  }
  
  class OrderServiceBlockingStub(channel: _root_.io.grpc.Channel, options: _root_.io.grpc.CallOptions = _root_.io.grpc.CallOptions.DEFAULT) extends _root_.io.grpc.stub.AbstractStub[OrderServiceBlockingStub](channel, options) with OrderServiceBlockingClient {
    override def order(request: com.echo.gold.protocol.OrderRequest): com.echo.gold.protocol.OrderResponse = {
      _root_.io.grpc.stub.ClientCalls.blockingUnaryCall(channel.newCall(METHOD_ORDER, options), request)
    }
    
    override def build(channel: _root_.io.grpc.Channel, options: _root_.io.grpc.CallOptions): OrderServiceBlockingStub = new OrderServiceBlockingStub(channel, options)
  }
  
  class OrderServiceStub(channel: _root_.io.grpc.Channel, options: _root_.io.grpc.CallOptions = _root_.io.grpc.CallOptions.DEFAULT) extends _root_.io.grpc.stub.AbstractStub[OrderServiceStub](channel, options) with OrderService {
    override def order(request: com.echo.gold.protocol.OrderRequest): scala.concurrent.Future[com.echo.gold.protocol.OrderResponse] = {
      com.trueaccord.scalapb.grpc.Grpc.guavaFuture2ScalaFuture(_root_.io.grpc.stub.ClientCalls.futureUnaryCall(channel.newCall(METHOD_ORDER, options), request))
    }
    
    override def build(channel: _root_.io.grpc.Channel, options: _root_.io.grpc.CallOptions): OrderServiceStub = new OrderServiceStub(channel, options)
  }
  
  def bindService(serviceImpl: OrderService, executionContext: scala.concurrent.ExecutionContext): _root_.io.grpc.ServerServiceDefinition =
    _root_.io.grpc.ServerServiceDefinition.builder("com.echo.gold.OrderService")
    .addMethod(
      METHOD_ORDER,
      _root_.io.grpc.stub.ServerCalls.asyncUnaryCall(new _root_.io.grpc.stub.ServerCalls.UnaryMethod[com.echo.gold.protocol.OrderRequest, com.echo.gold.protocol.OrderResponse] {
        override def invoke(request: com.echo.gold.protocol.OrderRequest, observer: _root_.io.grpc.stub.StreamObserver[com.echo.gold.protocol.OrderResponse]): Unit =
          serviceImpl.order(request).onComplete(com.trueaccord.scalapb.grpc.Grpc.completeObserver(observer))(
            executionContext)
      }))
    .build()
  
  def blockingStub(channel: _root_.io.grpc.Channel): OrderServiceBlockingStub = new OrderServiceBlockingStub(channel)
  
  def stub(channel: _root_.io.grpc.Channel): OrderServiceStub = new OrderServiceStub(channel)
}