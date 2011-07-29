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
import java.io.File
import de.fhworms.ztt.rcap.logger._
import scala.io.Source
package object util {
  implicit def any2PipelineSyntax[A](a: A) = new {
    def |>[B](f: A => B): B = f(a)
  }

  def addShutDownHook(hook: => Unit) =
    Runtime.getRuntime().addShutdownHook(new Thread {
      override def run = hook
    })

  def readConfFile(filename: String)(f: Array[String] => Unit) {
    val configFile = new File(filename)
    if (!(configFile.exists && configFile.canRead))
      Logger error ("Cannot load configuration file " + filename + "!")
    else {
      val lines = Source.fromFile(filename).getLines

      lines.foreach { line =>
        line.replace(" ", "").split("=") |> f
      }
    }
  }
}