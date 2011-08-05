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
package de.fhworms.ztt.rcap

import de.fhworms.ztt.rcap.util._
import de.fhworms.ztt.rcap.messages._
import de.fhworms.ztt.rcap.actor.RcapActor
import scala.collection.mutable.ListBuffer
import java.net.InetAddress
import akka.actor.ActorRef
import akka.actor.Actor
import scala.util.matching.Regex
import akka.AkkaException
import de.fhworms.ztt.rcap.logger._

/**
 * @author Waldemar Artes
 */
object ScalaMain {
  def main(args: Array[String]): Unit = {
    if (System.getenv.containsKey("RCAP_HOME"))
      HOME = System.getenv.get("RCAP_HOME")
    else
      Logger info "The environment variable RCAP_HOME was not defined. '.' is now the home directory of this application"
    Logger(HOME + "/rcap.log")

    initializeConfiguration(args)

    registerActor

    val me = Actor.remote.actorFor(HOST + "-rcap",
      HOST, PORT)

    Logger.info("Actor " + HOST + "-rcap registered")

    addShutDownHook {
      me !! Shutdown
      me ! Stop
      Actor.remote.unregister(me)
      Actor.remote.shutdown
      Logger.stop
    }

    if (args.indexOf("-c") >= 0) {
      me ! ConnectTo(args(args.indexOf("-c") + 1), PORT)
    } else if (remoteHosts != null) {
      connectToRemoteHosts(me, remoteHosts)
    } else {
      Logger.info(HOST + " starts as first node.")
    }
  }

  def connectToRemoteHosts(me: ActorRef, hosts: List[String]) {
    hosts match {
      case head :: tail =>
        Logger.info("Trying connecting to " + head + "-rcap")
        val rm = Actor.remote.actorFor(head + "-rcap", head, PORT)
        val result = rm !! Ping(TOKEN)
        result match {
          case Some(Pong) => me ! ConnectTo(head, PORT)
          case None => connectToRemoteHosts(me, tail)
        }
      case Nil =>
        Logger.info("None of the hosts given in the configuration file are online.")
        Logger.info(HOST + " starts as first node.")
    }
  }

  def registerActor = {
    Actor.remote.start(HOST, PORT)
    Logger.info("Remote started at " + HOST + " " + PORT)
    Actor.remote.register(HOST + "-rcap", Actor.actorOf[RcapActor])
  }

  def initializeConfiguration(args: Array[String]) = {
    HOST = InetAddress.getLocalHost.getHostName
    val configFileIterator =
      if (args.indexOf("--conf") >= 0) {
        readConfFile(args(args.indexOf("--conf") + 1)) _
      } else
        readConfFile(HOME + "/rcap.conf") _ //TODO: Log to info, if the default configfile could not be found.

    configFileIterator {
      case Array("localhost", host) => HOST = host; Logger.info("Host: " + host)

      case Array("port", port) => PORT = port.toInt; Logger.info("Port: " + port)

      case Array("token", token) => TOKEN = token; Logger.info("Token: " + token)

      case Array("loglevel", level) => level.toLowerCase match {
        case "debug" => Logger.Level = Debug
        case "info" => Logger.Level = Info
        case "warning" => Logger.Level = Warning
        case "error" => Logger.Level = Error
      }

      case Array("remotehosts", hosts) =>
        hosts.split(",").foreach { host =>
          if (host != HOST)
            addRemoteHostName(host)
        }
      case _ =>
    }

    if (args.indexOf("-p") >= 0) {
      PORT = args(args.indexOf("-p") + 1).toInt
    }

    if (args.indexOf("-h") >= 0) {
      HOST = args(args.indexOf("-h") + 1)
    }
  }
}

