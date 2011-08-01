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
import de.fhworms.ztt.rcap.messages.{ Stop, ClipboardChanged }
import akka.actor.Actor._
import de.fhworms.ztt.rcap._
import logger.Logger

class ListenerActor(sleepTime: Int) extends Actor {
  var prevContent = ""
  val server = remote.actorFor(HOST + "-rcap", HOST, PORT)
  Logger.info("Connected to Actor: " + HOST + "-rcap")
  def receive = {
    case "Repeat" => {
      try {
        val content = de.fhworms.ztt.rcap.clipboard.TextClipboard.content()
        if (content != prevContent) {
          server ! ClipboardChanged(content)
          prevContent = content
          Logger.debug("Content of the clipboard has changed to:\n\t" + content)
        }
      } catch {
        case e: Exception => Logger warning e.getStackTraceString
      }
      Thread.sleep(1000)
      self ! "Repeat"
    }
    case Stop => self.stop
  }
}