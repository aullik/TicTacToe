package tictactoe.persistence.databaseAccess.file

import java.io.{File, PrintWriter}

import grizzled.slf4j.Logging

import scala.io.Source

/**
  * Helper-object to write and read into and from files.
  */
private[file] object FilePersistenceHelper extends Logging {

  def writeFile(file: File, content: String): Unit = {
    info("Write File: " + file.getPath)
    val writer = new PrintWriter(file)
    writer.write(content)
    writer.close()
  }

  def readFile(file: File): String = {
    info("Read File: " + file.getPath)
    val source = Source.fromFile(file)
    val content = source.mkString
    source.close()
    content
  }

}
