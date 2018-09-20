package com.template

import io.bluebank.braid.corda.BraidConfig
import io.vertx.core.Handler
import io.vertx.core.http.HttpServerOptions
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import net.corda.core.node.AppServiceHub
import net.corda.core.node.services.CordaService
import net.corda.core.serialization.SingletonSerializeAsToken
import net.corda.core.utilities.loggerFor

@CordaService
class BraidServer(private val services: AppServiceHub) : SingletonSerializeAsToken() {
  companion object {
    private val log = loggerFor<BraidServer>()
  }
  init {
    val configText = ClassLoader.getSystemClassLoader().getResource("braid-config.json").readText()
    val configs = JsonObject(configText)
    val identity = services.myInfo.legalIdentities.first().name.toString()
    val config = configs.getJsonObject(identity)
    if (config == null) {
      log.info("could not find braid config for $identity. not starting braid")
    } else {
      Json.decodeValue(config.encode(), BraidConfig::class.java)
        .withHttpServerOptions(HttpServerOptions().setSsl(false))
        .withService("echoService", SimpleService(services))
        .withFlow(EchoFlow::class)
        .bootstrapBraid(services, Handler { result ->
          if (result.failed()) {
            log.error("failed to start up braid service for $identity", result.cause())
          } else {
            log.info("started braid service for $identity")
          }
        })
    }
  }
}