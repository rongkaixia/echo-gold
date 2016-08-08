// Generated by the Scala Plugin for the Protocol Buffer Compiler.
// Do not edit!
//
// Protofile syntax: PROTO3

package com.echo.gold.protocol



@SerialVersionUID(0L)
final case class OrderInfo(
    userId: String = "",
    title: String = "",
    productId: String = "",
    num: Int = 0,
    payMethod: com.echo.gold.protocol.PayMethod = com.echo.gold.protocol.PayMethod.PAY_METHOD_EMPTY,
    deliverMethod: com.echo.gold.protocol.DeliverMethod = com.echo.gold.protocol.DeliverMethod.DELIVER_METHOD_EMPTY,
    recipientsName: String = "",
    recipientsPhone: String = "",
    recipientsAddress: String = "",
    recipientsPostcode: String = "",
    comment: String = "",
    price: Double = 0.0,
    realPrice: Double = 0.0,
    discount: Double = 0.0,
    payAmt: Double = 0.0,
    realPayAmt: Double = 0.0
    ) extends com.trueaccord.scalapb.GeneratedMessage with com.trueaccord.scalapb.Message[OrderInfo] with com.trueaccord.lenses.Updatable[OrderInfo] {
    @transient
    lazy val serializedSize: Int = {
      var __size = 0
      if (userId != "") { __size += com.google.protobuf.CodedOutputStream.computeStringSize(1, userId) }
      if (title != "") { __size += com.google.protobuf.CodedOutputStream.computeStringSize(2, title) }
      if (productId != "") { __size += com.google.protobuf.CodedOutputStream.computeStringSize(3, productId) }
      if (num != 0) { __size += com.google.protobuf.CodedOutputStream.computeInt32Size(4, num) }
      if (payMethod != com.echo.gold.protocol.PayMethod.PAY_METHOD_EMPTY) { __size += com.google.protobuf.CodedOutputStream.computeEnumSize(5, payMethod.value) }
      if (deliverMethod != com.echo.gold.protocol.DeliverMethod.DELIVER_METHOD_EMPTY) { __size += com.google.protobuf.CodedOutputStream.computeEnumSize(6, deliverMethod.value) }
      if (recipientsName != "") { __size += com.google.protobuf.CodedOutputStream.computeStringSize(7, recipientsName) }
      if (recipientsPhone != "") { __size += com.google.protobuf.CodedOutputStream.computeStringSize(8, recipientsPhone) }
      if (recipientsAddress != "") { __size += com.google.protobuf.CodedOutputStream.computeStringSize(9, recipientsAddress) }
      if (recipientsPostcode != "") { __size += com.google.protobuf.CodedOutputStream.computeStringSize(10, recipientsPostcode) }
      if (comment != "") { __size += com.google.protobuf.CodedOutputStream.computeStringSize(11, comment) }
      if (price != 0.0) { __size += com.google.protobuf.CodedOutputStream.computeDoubleSize(20, price) }
      if (realPrice != 0.0) { __size += com.google.protobuf.CodedOutputStream.computeDoubleSize(21, realPrice) }
      if (discount != 0.0) { __size += com.google.protobuf.CodedOutputStream.computeDoubleSize(22, discount) }
      if (payAmt != 0.0) { __size += com.google.protobuf.CodedOutputStream.computeDoubleSize(23, payAmt) }
      if (realPayAmt != 0.0) { __size += com.google.protobuf.CodedOutputStream.computeDoubleSize(24, realPayAmt) }
      __size
    }
    def writeTo(output: com.google.protobuf.CodedOutputStream): Unit = {
      {
        val __v = userId
        if (__v != "") {
          output.writeString(1, __v)
        }
      };
      {
        val __v = title
        if (__v != "") {
          output.writeString(2, __v)
        }
      };
      {
        val __v = productId
        if (__v != "") {
          output.writeString(3, __v)
        }
      };
      {
        val __v = num
        if (__v != 0) {
          output.writeInt32(4, __v)
        }
      };
      {
        val __v = payMethod
        if (__v != com.echo.gold.protocol.PayMethod.PAY_METHOD_EMPTY) {
          output.writeEnum(5, __v.value)
        }
      };
      {
        val __v = deliverMethod
        if (__v != com.echo.gold.protocol.DeliverMethod.DELIVER_METHOD_EMPTY) {
          output.writeEnum(6, __v.value)
        }
      };
      {
        val __v = recipientsName
        if (__v != "") {
          output.writeString(7, __v)
        }
      };
      {
        val __v = recipientsPhone
        if (__v != "") {
          output.writeString(8, __v)
        }
      };
      {
        val __v = recipientsAddress
        if (__v != "") {
          output.writeString(9, __v)
        }
      };
      {
        val __v = recipientsPostcode
        if (__v != "") {
          output.writeString(10, __v)
        }
      };
      {
        val __v = comment
        if (__v != "") {
          output.writeString(11, __v)
        }
      };
      {
        val __v = price
        if (__v != 0.0) {
          output.writeDouble(20, __v)
        }
      };
      {
        val __v = realPrice
        if (__v != 0.0) {
          output.writeDouble(21, __v)
        }
      };
      {
        val __v = discount
        if (__v != 0.0) {
          output.writeDouble(22, __v)
        }
      };
      {
        val __v = payAmt
        if (__v != 0.0) {
          output.writeDouble(23, __v)
        }
      };
      {
        val __v = realPayAmt
        if (__v != 0.0) {
          output.writeDouble(24, __v)
        }
      };
    }
    def mergeFrom(__input: com.google.protobuf.CodedInputStream): com.echo.gold.protocol.OrderInfo = {
      var __userId = this.userId
      var __title = this.title
      var __productId = this.productId
      var __num = this.num
      var __payMethod = this.payMethod
      var __deliverMethod = this.deliverMethod
      var __recipientsName = this.recipientsName
      var __recipientsPhone = this.recipientsPhone
      var __recipientsAddress = this.recipientsAddress
      var __recipientsPostcode = this.recipientsPostcode
      var __comment = this.comment
      var __price = this.price
      var __realPrice = this.realPrice
      var __discount = this.discount
      var __payAmt = this.payAmt
      var __realPayAmt = this.realPayAmt
      var _done__ = false
      while (!_done__) {
        val _tag__ = __input.readTag()
        _tag__ match {
          case 0 => _done__ = true
          case 10 =>
            __userId = __input.readString()
          case 18 =>
            __title = __input.readString()
          case 26 =>
            __productId = __input.readString()
          case 32 =>
            __num = __input.readInt32()
          case 40 =>
            __payMethod = com.echo.gold.protocol.PayMethod.fromValue(__input.readEnum())
          case 48 =>
            __deliverMethod = com.echo.gold.protocol.DeliverMethod.fromValue(__input.readEnum())
          case 58 =>
            __recipientsName = __input.readString()
          case 66 =>
            __recipientsPhone = __input.readString()
          case 74 =>
            __recipientsAddress = __input.readString()
          case 82 =>
            __recipientsPostcode = __input.readString()
          case 90 =>
            __comment = __input.readString()
          case 161 =>
            __price = __input.readDouble()
          case 169 =>
            __realPrice = __input.readDouble()
          case 177 =>
            __discount = __input.readDouble()
          case 185 =>
            __payAmt = __input.readDouble()
          case 193 =>
            __realPayAmt = __input.readDouble()
          case tag => __input.skipField(tag)
        }
      }
      com.echo.gold.protocol.OrderInfo(
          userId = __userId,
          title = __title,
          productId = __productId,
          num = __num,
          payMethod = __payMethod,
          deliverMethod = __deliverMethod,
          recipientsName = __recipientsName,
          recipientsPhone = __recipientsPhone,
          recipientsAddress = __recipientsAddress,
          recipientsPostcode = __recipientsPostcode,
          comment = __comment,
          price = __price,
          realPrice = __realPrice,
          discount = __discount,
          payAmt = __payAmt,
          realPayAmt = __realPayAmt
      )
    }
    def withUserId(__v: String): OrderInfo = copy(userId = __v)
    def withTitle(__v: String): OrderInfo = copy(title = __v)
    def withProductId(__v: String): OrderInfo = copy(productId = __v)
    def withNum(__v: Int): OrderInfo = copy(num = __v)
    def withPayMethod(__v: com.echo.gold.protocol.PayMethod): OrderInfo = copy(payMethod = __v)
    def withDeliverMethod(__v: com.echo.gold.protocol.DeliverMethod): OrderInfo = copy(deliverMethod = __v)
    def withRecipientsName(__v: String): OrderInfo = copy(recipientsName = __v)
    def withRecipientsPhone(__v: String): OrderInfo = copy(recipientsPhone = __v)
    def withRecipientsAddress(__v: String): OrderInfo = copy(recipientsAddress = __v)
    def withRecipientsPostcode(__v: String): OrderInfo = copy(recipientsPostcode = __v)
    def withComment(__v: String): OrderInfo = copy(comment = __v)
    def withPrice(__v: Double): OrderInfo = copy(price = __v)
    def withRealPrice(__v: Double): OrderInfo = copy(realPrice = __v)
    def withDiscount(__v: Double): OrderInfo = copy(discount = __v)
    def withPayAmt(__v: Double): OrderInfo = copy(payAmt = __v)
    def withRealPayAmt(__v: Double): OrderInfo = copy(realPayAmt = __v)
    def getField(__field: com.google.protobuf.Descriptors.FieldDescriptor): scala.Any = {
      __field.getNumber match {
        case 1 => {
          val __t = userId
          if (__t != "") __t else null
        }
        case 2 => {
          val __t = title
          if (__t != "") __t else null
        }
        case 3 => {
          val __t = productId
          if (__t != "") __t else null
        }
        case 4 => {
          val __t = num
          if (__t != 0) __t else null
        }
        case 5 => {
          val __t = payMethod.valueDescriptor
          if (__t.getNumber() != 0) __t else null
        }
        case 6 => {
          val __t = deliverMethod.valueDescriptor
          if (__t.getNumber() != 0) __t else null
        }
        case 7 => {
          val __t = recipientsName
          if (__t != "") __t else null
        }
        case 8 => {
          val __t = recipientsPhone
          if (__t != "") __t else null
        }
        case 9 => {
          val __t = recipientsAddress
          if (__t != "") __t else null
        }
        case 10 => {
          val __t = recipientsPostcode
          if (__t != "") __t else null
        }
        case 11 => {
          val __t = comment
          if (__t != "") __t else null
        }
        case 20 => {
          val __t = price
          if (__t != 0.0) __t else null
        }
        case 21 => {
          val __t = realPrice
          if (__t != 0.0) __t else null
        }
        case 22 => {
          val __t = discount
          if (__t != 0.0) __t else null
        }
        case 23 => {
          val __t = payAmt
          if (__t != 0.0) __t else null
        }
        case 24 => {
          val __t = realPayAmt
          if (__t != 0.0) __t else null
        }
      }
    }
    override def toString: String = com.trueaccord.scalapb.TextFormat.printToUnicodeString(this)
    def companion = com.echo.gold.protocol.OrderInfo
}

object OrderInfo extends com.trueaccord.scalapb.GeneratedMessageCompanion[OrderInfo] {
  implicit def messageCompanion: com.trueaccord.scalapb.GeneratedMessageCompanion[OrderInfo] = this
  def fromFieldsMap(__fieldsMap: Map[com.google.protobuf.Descriptors.FieldDescriptor, scala.Any]): com.echo.gold.protocol.OrderInfo = {
    require(__fieldsMap.keys.forall(_.getContainingType() == descriptor), "FieldDescriptor does not match message type.")
    val __fields = descriptor.getFields
    com.echo.gold.protocol.OrderInfo(
      __fieldsMap.getOrElse(__fields.get(0), "").asInstanceOf[String],
      __fieldsMap.getOrElse(__fields.get(1), "").asInstanceOf[String],
      __fieldsMap.getOrElse(__fields.get(2), "").asInstanceOf[String],
      __fieldsMap.getOrElse(__fields.get(3), 0).asInstanceOf[Int],
      com.echo.gold.protocol.PayMethod.fromValue(__fieldsMap.getOrElse(__fields.get(4), com.echo.gold.protocol.PayMethod.PAY_METHOD_EMPTY.valueDescriptor).asInstanceOf[com.google.protobuf.Descriptors.EnumValueDescriptor].getNumber),
      com.echo.gold.protocol.DeliverMethod.fromValue(__fieldsMap.getOrElse(__fields.get(5), com.echo.gold.protocol.DeliverMethod.DELIVER_METHOD_EMPTY.valueDescriptor).asInstanceOf[com.google.protobuf.Descriptors.EnumValueDescriptor].getNumber),
      __fieldsMap.getOrElse(__fields.get(6), "").asInstanceOf[String],
      __fieldsMap.getOrElse(__fields.get(7), "").asInstanceOf[String],
      __fieldsMap.getOrElse(__fields.get(8), "").asInstanceOf[String],
      __fieldsMap.getOrElse(__fields.get(9), "").asInstanceOf[String],
      __fieldsMap.getOrElse(__fields.get(10), "").asInstanceOf[String],
      __fieldsMap.getOrElse(__fields.get(11), 0.0).asInstanceOf[Double],
      __fieldsMap.getOrElse(__fields.get(12), 0.0).asInstanceOf[Double],
      __fieldsMap.getOrElse(__fields.get(13), 0.0).asInstanceOf[Double],
      __fieldsMap.getOrElse(__fields.get(14), 0.0).asInstanceOf[Double],
      __fieldsMap.getOrElse(__fields.get(15), 0.0).asInstanceOf[Double]
    )
  }
  def descriptor: com.google.protobuf.Descriptors.Descriptor = ProtocolProto.descriptor.getMessageTypes.get(11)
  def messageCompanionForField(__field: com.google.protobuf.Descriptors.FieldDescriptor): com.trueaccord.scalapb.GeneratedMessageCompanion[_] = throw new MatchError(__field)
  def enumCompanionForField(__field: com.google.protobuf.Descriptors.FieldDescriptor): com.trueaccord.scalapb.GeneratedEnumCompanion[_] = {
    require(__field.getContainingType() == descriptor, "FieldDescriptor does not match message type.")
    __field.getNumber match {
      case 5 => com.echo.gold.protocol.PayMethod
      case 6 => com.echo.gold.protocol.DeliverMethod
    }
  }
  lazy val defaultInstance = com.echo.gold.protocol.OrderInfo(
  )
  implicit class OrderInfoLens[UpperPB](_l: com.trueaccord.lenses.Lens[UpperPB, OrderInfo]) extends com.trueaccord.lenses.ObjectLens[UpperPB, OrderInfo](_l) {
    def userId: com.trueaccord.lenses.Lens[UpperPB, String] = field(_.userId)((c_, f_) => c_.copy(userId = f_))
    def title: com.trueaccord.lenses.Lens[UpperPB, String] = field(_.title)((c_, f_) => c_.copy(title = f_))
    def productId: com.trueaccord.lenses.Lens[UpperPB, String] = field(_.productId)((c_, f_) => c_.copy(productId = f_))
    def num: com.trueaccord.lenses.Lens[UpperPB, Int] = field(_.num)((c_, f_) => c_.copy(num = f_))
    def payMethod: com.trueaccord.lenses.Lens[UpperPB, com.echo.gold.protocol.PayMethod] = field(_.payMethod)((c_, f_) => c_.copy(payMethod = f_))
    def deliverMethod: com.trueaccord.lenses.Lens[UpperPB, com.echo.gold.protocol.DeliverMethod] = field(_.deliverMethod)((c_, f_) => c_.copy(deliverMethod = f_))
    def recipientsName: com.trueaccord.lenses.Lens[UpperPB, String] = field(_.recipientsName)((c_, f_) => c_.copy(recipientsName = f_))
    def recipientsPhone: com.trueaccord.lenses.Lens[UpperPB, String] = field(_.recipientsPhone)((c_, f_) => c_.copy(recipientsPhone = f_))
    def recipientsAddress: com.trueaccord.lenses.Lens[UpperPB, String] = field(_.recipientsAddress)((c_, f_) => c_.copy(recipientsAddress = f_))
    def recipientsPostcode: com.trueaccord.lenses.Lens[UpperPB, String] = field(_.recipientsPostcode)((c_, f_) => c_.copy(recipientsPostcode = f_))
    def comment: com.trueaccord.lenses.Lens[UpperPB, String] = field(_.comment)((c_, f_) => c_.copy(comment = f_))
    def price: com.trueaccord.lenses.Lens[UpperPB, Double] = field(_.price)((c_, f_) => c_.copy(price = f_))
    def realPrice: com.trueaccord.lenses.Lens[UpperPB, Double] = field(_.realPrice)((c_, f_) => c_.copy(realPrice = f_))
    def discount: com.trueaccord.lenses.Lens[UpperPB, Double] = field(_.discount)((c_, f_) => c_.copy(discount = f_))
    def payAmt: com.trueaccord.lenses.Lens[UpperPB, Double] = field(_.payAmt)((c_, f_) => c_.copy(payAmt = f_))
    def realPayAmt: com.trueaccord.lenses.Lens[UpperPB, Double] = field(_.realPayAmt)((c_, f_) => c_.copy(realPayAmt = f_))
  }
  final val USER_ID_FIELD_NUMBER = 1
  final val TITLE_FIELD_NUMBER = 2
  final val PRODUCT_ID_FIELD_NUMBER = 3
  final val NUM_FIELD_NUMBER = 4
  final val PAY_METHOD_FIELD_NUMBER = 5
  final val DELIVER_METHOD_FIELD_NUMBER = 6
  final val RECIPIENTS_NAME_FIELD_NUMBER = 7
  final val RECIPIENTS_PHONE_FIELD_NUMBER = 8
  final val RECIPIENTS_ADDRESS_FIELD_NUMBER = 9
  final val RECIPIENTS_POSTCODE_FIELD_NUMBER = 10
  final val COMMENT_FIELD_NUMBER = 11
  final val PRICE_FIELD_NUMBER = 20
  final val REAL_PRICE_FIELD_NUMBER = 21
  final val DISCOUNT_FIELD_NUMBER = 22
  final val PAY_AMT_FIELD_NUMBER = 23
  final val REAL_PAY_AMT_FIELD_NUMBER = 24
}
