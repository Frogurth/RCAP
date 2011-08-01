/*
 * Copyright (C) 2011 the original author or authors.
 * See the license.txt file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.fhworms.ztt.rcap.actor
import akka.actor.Actor
import akka.actor.Actor._
import scala.collection.mutable.ListBuffer
import akka.actor.ActorRef
import de.fhworms.ztt.rcap.messages._
import java.net.InetAddress
import de.fhworms.ztt.rcap._
import de.fhworms.ztt.rcap.clipboard.TextClipboard
import de.fhworms.ztt.rcap.logger.Logger

class RcapActor extends Actor {
  var bots = new ListBuffer[ActorRef]

  def receive = {
    case ClipboardChangedRemote(text) => TextClipboard.content(text)

    case ClipboardChanged(text) =>
      Logger.debug("Publishing text:\n\t" + text)
      bots.foreach(bot => bot ! ClipboardChangedRemote(text))

    case ConnectTo(host, port) =>
      val bot = remote.actorFor(host + "-rcap", host, port)
      val result = bot !! PublishMe(HOST, PORT, TOKEN)
      result match {
        case Some(true) =>
          bots += bot
          Logger.info("Registered " + host)

        case Some(false) =>
          Logger.warning(host + " Access denied: Tokens are different")

        case None => Logger.error("PublishMe response from " + host + " timed out.")
      }

    case PublishMe(host, port, token) =>
      if (token == TOKEN) {
        self reply true
        bots.foreach(_ ! NewBot(host, port))
        bots += remote.actorFor(host + "-rcap", host, port)
        Logger.info("Registered " + host)
      } else {
        Logger.warning(host + " Access denied: Tokens are different")
        self reply false
      }

    case NewBot(host, port) =>
      val bot = remote.actorFor(host + "-rcap", host, port)
      bot ! RegisterMe(HOST, PORT)
      bots += bot
      Logger.info("Registered " + host)

    case RegisterMe(host, port) =>
      bots += remote.actorFor(host + "-rcap", host, port)
      Logger.info("Registered " + host)

    case Shutdown =>
      bots.foreach(_ ! UnregisterMe(HOST, PORT))
      self reply true

    case UnregisterMe(host, port) =>
      bots = bots.filter(_.getId != (host + "-rcap"))
      Logger.info("Unregistered: " + host)
      val hosts = bots.map(_.id)
      if (!hosts.isEmpty)
        Logger.info("Still in registered: " + hosts.reduceLeft(_ + ", " + _))

    case Stop => self.stop

    case Ping(token) => if (token == TOKEN) self reply Pong
  }
}