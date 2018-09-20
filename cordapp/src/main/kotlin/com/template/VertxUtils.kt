package com.template

import io.vertx.core.Future
import net.corda.core.concurrent.CordaFuture
import net.corda.core.utilities.getOrThrow

fun <T> CordaFuture<T>.toVertxFuture() : Future<T> {
  val result = Future.future<T>()
  this.then { f ->
    try {
      result.complete(f.getOrThrow())
    } catch (err: Throwable) {
      result.fail(err)
    }
  }
  return result
}