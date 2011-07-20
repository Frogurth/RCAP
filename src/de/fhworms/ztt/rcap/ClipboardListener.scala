/**
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
package de.fhworms.ztt.rcap
import akka.actor.ActorRef
import akka.actor.Actor._
import java.net.InetAddress
import de.fhworms.ztt.rcap.messages._
import de.fhworms.ztt.rcap.util._
import de.fhworms.ztt.rcap.logger._
import de.fhworms.ztt.rcap.actor.ListenerActor

object ClipboardListener {
  private var sleepTime = 1000

  def main(args: Array[String]): Unit = {
    val env = System.getenv
    if (env.containsKey("RCAP_HOME"))
      HOME = env.get("RCAP_HOME")

    Logger(HOME + "/listener.log")

    initializeConfiguration(args)

    //    val server = getServer
    val listener = actorOf(new ListenerActor(sleepTime)).start
    listener ! "Repeat"

    addShutDownHook {
      Logger.stop
      listener ! Stop
    }

    //    Logger.info("Connected to Actor: " + HOST + "-rcap")
    //    var prevContent = de.fhworms.ztt.rcap.clipboard.TextClipboard.content()
    //    while (true) {
    //      Thread.sleep(sleepTime)
    //      try {
    //        val content = de.fhworms.ztt.rcap.clipboard.TextClipboard.content()
    //        if (content != prevContent) {
    //          server ! ClipboardChanged(content)
    //          prevContent = content
    //          Logger.debug("Content of the clipboard has changed to:\n\t" + content)
    //        }
    //      } catch {
    //        case e: Exception => Logger warning e.getStackTraceString
    //      }
    //    }
  }

  private def getServer = remote.actorFor(HOST + "-rcap",
    HOST,
    PORT)

  private def initializeConfiguration(args: Array[String]) = {
    HOST = InetAddress.getLocalHost.getHostName
    val configFileIterator =
      if (args.indexOf("--conf") >= 0) {
        readConfFile(args(args.indexOf("--conf") + 1)) _
      } else {
        readConfFile(HOME + "/rcap.conf") _
      }

    configFileIterator {
      case Array("localhost", host) => HOST = host; Logger.info("Host: " + host)

      case Array("port", port) => PORT = port.toInt; Logger.info("Port: " + port)

      case Array("loglevel", level) => level.toLowerCase match {
        case "debug" => Logger.Level = Debug
        case "info" => Logger.Level = Info
        case "warning" => Logger.Level = Warning
        case "error" => Logger.Level = Error
      }
      case Array("listenersleeptime", time) => sleepTime = time.toInt
      case _ =>
    }

    if (args.indexOf("-h") >= 0) {
      HOST = args(args.indexOf("-h") + 1)
    }

    if (args.indexOf("-p") >= 0) {
      PORT = args(args.indexOf("-p") + 1).toInt
    }

    if (args.indexOf("-t") >= 0) {
      sleepTime = args(args.indexOf("-t") + 1).toInt
    }
  }
}