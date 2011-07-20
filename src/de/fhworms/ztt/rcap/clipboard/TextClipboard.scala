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
package de.fhworms.ztt.rcap.clipboard
import java.awt.datatransfer.ClipboardOwner
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.Transferable
import java.awt.datatransfer.StringSelection
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.UnsupportedFlavorException

object TextClipboard extends ClipboardOwner {

  override def lostOwnership(aClipboard: Clipboard, aContents: Transferable) {
    //
  }

  def content(text: String) {
    val stringSelection = new StringSelection(text);
    val clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    clipboard.setContents(stringSelection, this);
  }

  def content(): String = {
    var result = ""
    val clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    val contents = clipboard.getContents(null);
    val hasTransferableText = (contents != null) && contents.isDataFlavorSupported(DataFlavor.stringFlavor);
    if (hasTransferableText)
      result = clipboard.getData(DataFlavor.stringFlavor).asInstanceOf[String] //(contents.getTransferData(DataFlavor.stringFlavor)).asInstanceOf[String]
    result
  }
  override def toString = content()
}