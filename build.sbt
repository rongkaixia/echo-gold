import com.trueaccord.scalapb.{ScalaPbPlugin => PB}

PB.protobufSettings

PB.runProtoc in PB.protobufConfig := (args =>
  com.github.os72.protocjar.Protoc.runProtoc("-v300" +: args.toArray))

version in PB.protobufConfig := "3.0.0-beta-2"
PB.flatPackage in PB.protobufConfig := true
sourceDirectory in PB.protobufConfig := file("./lib/echo-common/protobuf")
scalaSource in PB.protobufConfig := file("./src/main/compiled_protobuf/")

libraryDependencies ++= Seq(
    "io.grpc" % "grpc-all" % "0.15.0",
    "com.trueaccord.scalapb" %% "scalapb-runtime-grpc" % (PB.scalapbVersion in PB.protobufConfig).value
)
