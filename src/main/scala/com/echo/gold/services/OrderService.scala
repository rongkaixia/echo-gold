package com.echo.gold

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future}

import com.typesafe.config.{Config, ConfigFactory}

import org.mongodb.scala._
import com.mongodb.connection.ClusterSettings
import com.echo.gold.protocol.OrderServiceGrpc

trait AbstractOrderService extends OrderServiceGrpc.OrderService {
  implicit val ec: ExecutionContext
  val mongo: MongoClient
  val cfg: Config
}

class OrderService() 
  extends OrderImpl 
  with PayImpl{

  // execution context
  val ec = ExecutionContext.Implicits.global
  // config
  val cfg = ConfigFactory.load()
  // init mongodb
  val mongo = initMongo
  
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
}