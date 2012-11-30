package utilities

/*
 * Copyright (C) 2012 Thomas Alexandre
 */

import java.sql.{ Connection, DriverManager, ResultSet, ResultSetMetaData }
import java.io.{ FileOutputStream, FileWriter, PrintWriter, StringWriter }
import scala.collection.immutable.IndexedSeq
import StringUtil.{ camelify, camelifyMethod }
import scala.collection._
import scala.util.logging.Logged
import org.dbunit.database.search.TablesDependencyHelper
import org.dbunit.database.DatabaseConnection
import org.dbunit.dataset.xml.FlatXmlDataSet
import java.io.File

// Uses only 6 of all the 23 available property attributes
case class TableProperty(
  name: String,
  propertyType: String,
  nullable: Boolean,
  isPrimaryKey: Boolean,
  length: Int,
  dbColumnName: String,
  formattedType: String,
  default: String,
  isForeignKey: Boolean,
  fKeyInfo: Vector[String])

class DBReader(val config: Config) extends Logged {

  // Load the driver
  val driver = classOf[com.mysql.jdbc.Driver]

  // Setup the connection
  val conn = DriverManager.getConnection(config.dburl, config.username, config.password)
  val metadata = conn.getMetaData()

  def getTables() = {
    val tables: ResultSet = metadata.getTables(null, null, null, Array[String]("TABLE")) // catalog, schemaPattern, tablenamePattern, types
    DBUtil.eachRowWithDefault(tables) groupBy (_(2).toString())
  }

  val tablenames = getTables().keys.toList

  def getPrimaryKeys(tablename: String) = {
    val primaryKeys: ResultSet = metadata.getPrimaryKeys(null, null, tablename) // catalog, schema, table
    DBUtil.eachRowWithDefault(primaryKeys) groupBy (_(3).toString())
  }

  //  def getForeignKeys2(tablename: String) = {
  //    val foreignKeys: ResultSet = metadata.getImportedKeys(null, null, tablename) // catalog, schema, table
  //    DBUtil.eachRowWithDefault(foreignKeys)
  //  }

  def getForeignKeys(tablename: String): Map[String, List[Vector[String]]] = {
    val foreignKeys: ResultSet = metadata.getImportedKeys(null, null, tablename) // catalog, schema, table
    DBUtil.eachRowWithDefault(foreignKeys).map(x => x.toVector.map { item =>
      if (item == null) "" else item.toString
    }) groupBy (_(3).toString())
  }

  def foreignKeys =
    for {
      table <- tablenames
      importedKeys = DBUtil.eachRowWithDefault(metadata.getImportedKeys(null, null, table)) if importedKeys.nonEmpty
    } yield importedKeys

  def getProperties() = {
    val columns = metadata.getColumns(null, null, null, null)
    val metaClosure = { rsmd: ResultSetMetaData =>
      val columnCount = rsmd.getColumnCount
      log("column names: " + (1 to columnCount).map { rsmd.getColumnName(_) })
      columnCount
    }
    val result = DBUtil.eachRowWithDefault(columns, metaClosure)
    log("Properties= " + result)
    result groupBy (_(2).toString())
  }

  def createDirectoryIfNeeded(filePath: String): Unit = {
    val fileDirectory = filePath.split("/").reverse.tail.reverse.mkString("/")
    val theDir = new File(fileDirectory)

    // if the directory does not exist, create it
    if (!theDir.exists()) {
      log("creating directory: " + fileDirectory)
      theDir.mkdir()
    }
  }

  def saveTestData(filepath: String, dependency: String): Unit = {
    val connection = new DatabaseConnection(conn)
    val depTableNames = TablesDependencyHelper.getAllDependentTables(connection, dependency)
    val depDataset = connection.createDataSet(depTableNames)
    createDirectoryIfNeeded(filepath);
    FlatXmlDataSet.write(depDataset, new FileOutputStream(filepath));
  }

}
