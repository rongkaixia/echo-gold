// Generated by the Scala Plugin for the Protocol Buffer Compiler.
// Do not edit!
//
// Protofile syntax: PROTO3

package com.echo.gold.protocol



@SerialVersionUID(0L)
final case class NotifyRequest(
    orderId: String = ""
    ) extends com.trueaccord.scalapb.GeneratedMessage with com.trueaccord.scalapb.Message[NotifyRequest] with com.trueaccord.lenses.Updatable[NotifyRequest] {
    @transient
    lazy val serializedSize: Int = {
      var __size = 0
      if (orderId != "") { __size += com.google.protobuf.CodedOutputStream.computeStringSize(1, orderId) }
      __size
    }
    def writeTo(output: com.google.protobuf.CodedOutputStream): Unit = {
      {
        val __v = orderId
        if (__v != "") {
          output.writeString(1, __v)
        }
      };
    }
    def mergeFrom(__input: com.google.protobuf.CodedInputStream): com.echo.gold.protocol.NotifyRequest = {
      var __orderId = this.orderId
      var _done__ = false
      while (!_done__) {
        val _tag__ = __input.readTag()
        _tag__ match {
          case 0 => _done__ = true
          case 10 =>
            __orderId = __input.readString()
          case tag => __input.skipField(tag)
        }
      }
      com.echo.gold.protocol.NotifyRequest(
          orderId = __orderId
      )
    }
    def withOrderId(__v: String): NotifyRequest = copy(orderId = __v)
    def getField(__field: com.google.protobuf.Descriptors.FieldDescriptor): scala.Any = {
      __field.getNumber match {
        case 1 => {
          val __t = orderId
          if (__t != "") __t else null
        }
      }
    }
    override def toString: String = com.trueaccord.scalapb.TextFormat.printToUnicodeString(this)
    def companion = com.echo.gold.protocol.NotifyRequest
}

object NotifyRequest extends com.trueaccord.scalapb.GeneratedMessageCompanion[NotifyRequest] {
  implicit def messageCompanion: com.trueaccord.scalapb.GeneratedMessageCompanion[NotifyRequest] = this
  def fromFieldsMap(__fieldsMap: Map[com.google.protobuf.Descriptors.FieldDescriptor, scala.Any]): com.echo.gold.protocol.NotifyRequest = {
    require(__fieldsMap.keys.forall(_.getContainingType() == descriptor), "FieldDescriptor does not match message type.")
    val __fields = descriptor.getFields
    com.echo.gold.protocol.NotifyRequest(
      __fieldsMap.getOrElse(__fields.get(0), "").asInstanceOf[String]
    )
  }
  def descriptor: com.google.protobuf.Descriptors.Descriptor = ProtocolProto.descriptor.getMessageTypes.get(2)
  def messageCompanionForField(__field: com.google.protobuf.Descriptors.FieldDescriptor): com.trueaccord.scalapb.GeneratedMessageCompanion[_] = throw new MatchError(__field)
  def enumCompanionForField(__field: com.google.protobuf.Descriptors.FieldDescriptor): com.trueaccord.scalapb.GeneratedEnumCompanion[_] = throw new MatchError(__field)
  lazy val defaultInstance = com.echo.gold.protocol.NotifyRequest(
  )
  implicit class NotifyRequestLens[UpperPB](_l: com.trueaccord.lenses.Lens[UpperPB, NotifyRequest]) extends com.trueaccord.lenses.ObjectLens[UpperPB, NotifyRequest](_l) {
    def orderId: com.trueaccord.lenses.Lens[UpperPB, String] = field(_.orderId)((c_, f_) => c_.copy(orderId = f_))
  }
  final val ORDER_ID_FIELD_NUMBER = 1
}
