// Generated by the Scala Plugin for the Protocol Buffer Compiler.
// Do not edit!
//
// Protofile syntax: PROTO3

package com.echo.gold.protocol



sealed trait DeliverMethod extends com.trueaccord.scalapb.GeneratedEnum {
  type EnumType = DeliverMethod
  def isDeliverMethodEmpty: Boolean = false
  def isExpress: Boolean = false
  def isDtd: Boolean = false
  def isUnrecognized: Boolean = false
  def companion: com.trueaccord.scalapb.GeneratedEnumCompanion[DeliverMethod] = DeliverMethod
}

object DeliverMethod extends com.trueaccord.scalapb.GeneratedEnumCompanion[DeliverMethod] {
  implicit def enumCompanion: com.trueaccord.scalapb.GeneratedEnumCompanion[DeliverMethod] = this
  @SerialVersionUID(0L)
  case object DELIVER_METHOD_EMPTY extends DeliverMethod {
    val value = 0
    val index = 0
    val name = "DELIVER_METHOD_EMPTY"
    override def isDeliverMethodEmpty: Boolean = true
  }
  
  @SerialVersionUID(0L)
  case object EXPRESS extends DeliverMethod {
    val value = 1
    val index = 1
    val name = "EXPRESS"
    override def isExpress: Boolean = true
  }
  
  @SerialVersionUID(0L)
  case object DTD extends DeliverMethod {
    val value = 2
    val index = 2
    val name = "DTD"
    override def isDtd: Boolean = true
  }
  
  @SerialVersionUID(0L)
  case class Unrecognized(value: Int) extends DeliverMethod {
    val name = "UNRECOGNIZED"
    val index = -1
    override def isUnrecognized: Boolean = true
  }
  
  lazy val values = Seq(DELIVER_METHOD_EMPTY, EXPRESS, DTD)
  def fromValue(value: Int): DeliverMethod = value match {
    case 0 => DELIVER_METHOD_EMPTY
    case 1 => EXPRESS
    case 2 => DTD
    case __other => Unrecognized(__other)
  }
  def descriptor: com.google.protobuf.Descriptors.EnumDescriptor = ProtocolProto.descriptor.getEnumTypes.get(1)
}