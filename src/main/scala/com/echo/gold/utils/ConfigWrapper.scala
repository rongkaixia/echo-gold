package com.echo.gold.utils

import com.typesafe.config.ConfigFactory

trait LazyConfig{
  lazy val cfg = ConfigFactory.load()
}