import { Component } from '@angular/core';
import { Proxy } from 'braid-client';

// NOTE: I'm not too familiar with the more recent versions of angular
// the following should probably go somewhere else!

const proxy = new Proxy({url: 'http://localhost:8081/api/'}, onOpen, onClose, onError, {strictSSL: false})

function onOpen() {
  write("proxy connected");

  // cache the proxy at the global level so that we can play with it in the console
  window.proxy = proxy;
  write("let's call the echo flow");
  const testMessage = "hello, world";
  proxy.flows.echoFlow(testMessage)
    .then(result => {
      if (result === testMessage) {
        write("echo flow returned the test message");
      } else {
        write("echo flow failed to return the test message and instead returned", testMessage)
      }
    })
    .catch(err => {
      write("error: failed to call the echo flow", err);
    })
    .then(() => {
      write("let's call the echo service")
      return proxy.echoService.echo(testMessage);
    })
    .then(result => {
      if (result === testMessage) {
        write("echo service returned the test message")
      } else {
        write("echo service failed to return the test message and instead returned", testMessage)
      }
    })
    .catch(err => {
      write("error: failed to call the echo service", err);
    })
  ;
}

function onClose() {
  console.log("closed")
}

function onError(err) {
  console.error(err)
}

function write(...args: any[]) {
  const c = document.getElementById("braid-console");
  if (args && args.length) {
    args.forEach(item => {
      c.innerText += (item + ' ');
    });
    c.innerText += '\n';
  }
  console.log(...args);
}

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'web';
}
