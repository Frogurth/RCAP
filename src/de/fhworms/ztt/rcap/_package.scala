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
package de.fhworms.ztt

/**
 * Global getter and setter methods for variables
 */
package object rcap {
  private var home = "."
  private var port = 9999
  private var host = ""
  private var token = ""
  private var remoteHosts: List[String] = null

  def remoteHostNames = remoteHosts

  def addRemoteHostName(hostName: String) = {
    if (remoteHosts == null)
      remoteHosts = Nil
    remoteHosts = hostName :: remoteHosts
  }

  def HOME = home
  def HOME_=(value: String) = home = value

  def PORT = port
  def PORT_=(value: Int) = port = value

  def HOST = host
  def HOST_=(value: String) = host = value

  def TOKEN = token
  def TOKEN_=(value: String) = token = value
}