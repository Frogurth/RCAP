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
package messages

case class Message
case class ClipboardChanged(content: String) extends Message
case class ClipboardChangedRemote(content: String) extends Message
case class ConnectTo(host: String, port: Int) extends Message
case class RegisterMe(host: String, port: Int) extends Message
case class UnregisterMe(host: String, port: Int) extends Message
case class PublishMe(host: String, port: Int, token: String) extends Message
case class NewBot(host: String, bot: Int) extends Message
case class Ping(token: String) extends Message
case object Stop extends Message
case object Shutdown extends Message
case object Pong extends Message
