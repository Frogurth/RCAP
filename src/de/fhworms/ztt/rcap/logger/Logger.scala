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
package de.fhworms.ztt.rcap.logger
import akka.actor.Actor
import java.io.FileWriter
import java.util.Date
import akka.actor.ActorRef
import java.text.SimpleDateFormat

class Logger(filename: String) extends Actor {
  val logfile = new FileWriter(filename, true);
  val timeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS")
  def receive = {
    case "stop" => logfile.close; self.stop
    case s: String => {
      val time = new Date
      val date = timeFormat format time
      logfile write (date + " " + s + "\n")
      logfile.flush
    }
  }
}

object Logger {
  private var logger: ActorRef = null
  private var level: LogLevel = Info
  private def log(s: String) = logger ! s
  def debug(s: String) = if (level == Debug) log("[DEBUG] | " + s)
  def info(s: String) = if (level == Debug || level == Info) log("[INFO] | " + s)
  def warning(s: String) = if (level == Debug || level == Info || level == Warning) log("[WARNING] | " + s)
  def error(s: String) = log("[ERROR] | " + s)
  def stop(): Unit = logger ! "stop"
  def apply(filename: String) {
    logger = Actor.actorOf(new Logger(filename))
    logger.start
  }

  def Level_=(level: LogLevel): Unit = this.level = level
  def Level: LogLevel = level
}

case class LogLevel
case object Debug extends LogLevel
case object Info extends LogLevel
case object Warning extends LogLevel
case object Error extends LogLevel