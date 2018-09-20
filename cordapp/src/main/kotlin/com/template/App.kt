package com.template

import co.paralleluniverse.fibers.Suspendable
import io.vertx.core.Future
import net.corda.core.flows.*
import net.corda.core.messaging.CordaRPCOps
import net.corda.core.node.AppServiceHub
import net.corda.core.serialization.SerializationWhitelist
import net.corda.webserver.services.WebServerPluginRegistry
import java.util.function.Function
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response


// *********
// * Flows *
// *********
@InitiatingFlow
@StartableByRPC
@StartableByService
class EchoFlow(private val message: String) : FlowLogic<String>() {
  @Suspendable
  override fun call(): String {
    return message
  }
}

/**
 * A simple braid service
 */
class SimpleService(private val serviceHub: AppServiceHub) {
  fun echo(msg: String) : Future<String> {
    return serviceHub.startFlow(EchoFlow(msg)).returnValue.toVertxFuture()
  }
}