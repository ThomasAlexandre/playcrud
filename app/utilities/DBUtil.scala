package utilities

/*
 * Copyright (C) 2012-2013 Thomas Alexandre
 */

import java.sql.{ ResultSet, ResultSetMetaData }
import java.sql.DriverManager
import scala.util.logging.Logged
import StringUtil._
import scala.annotation.tailrec

object DBUtil extends Logged {

  def getConnection(configuration: Config) = {
    Class.forName(configuration.driver).newInstance
    DriverManager.getConnection(configuration.dburl, configuration.username, configuration.password)
  }

  /**
   * By default the metaclosure returns the column count (as Int)
   * and the default closure returns all objects from the result set.
   * Example usage with default metaclosure but non default closure:
   *    val metadata = DBUtil.getMetadata(configuration)
   *    val resultSet = metadata.getTables(configuration.getCatalog, configuration.getSchema, null, null)
   *    def theClosure = (rs:ResultSet, mc:Int) => (1 to mc) map{ rs.getObject(_) }
   *    eachRowWithDefault(resultSet,closure=theClosure)
   */
  def eachRowWithDefault[T, U](
    resultSet: ResultSet,
    metaClosure: ResultSetMetaData => T = (rsmd: ResultSetMetaData) => rsmd.getColumnCount,
    closure: (ResultSet, T) => U = (rs: ResultSet, mc: Int) => (1 to mc).map { rs.getObject(_) }): List[U] = {

    val mcResult: T = metaClosure(resultSet.getMetaData)

    @tailrec
    def nextRow(result: List[U]): List[U] = {
      if (!resultSet.next()) result
      else nextRow(result :+ closure(resultSet, mcResult))
    }
    nextRow(List[U]())
  }

  def getMetadata(configuration: Config): java.sql.DatabaseMetaData = {
    getConnection(configuration).getMetaData
  }

  // TODO: Make the mapping depending on Config (i.e. Database Type)
  // def mapping(configuration: Config)(propertyType: String): String = {
  def mapping(propertyType: String): String = {
    propertyType match {
      case "BIT" => "Boolean" // find right type
      case "BIGINT" => "Long"
      case "CHAR" | "LONGVARCHAR" | "VARCHAR" => "String"
      case "DECIMAL" => "Int" // find right type (BigDecimal)
      case "NUMERIC" => "BigDecimal" // find right type
      case "SMALLINT" => "Int" // find right type (Short)
      case "INTEGER" | "INT" => "Int"
      case "DOUBLE" => "Int" // find right type (Double)
      case "DATE" => "Date"
      case "DATETIME" => "Date"
      case "FLOAT" => "Double" // find right type (Float or Double)  
      case "REAL" => "Double" // find right type
      case "TIME" => "Time" // find right type (java.sql.Time)
      case "TIMESTAMP" => "Date" // find right type (java.sql.Timestamp)
      case "TINYINT" => "Byte" // find right type
      case "int4" => "Int"
      case "int8" => "Long" 
      case "bool" => "Boolean"
      case "varchar" => "String"
      case "timestamp" => "Date"
      case _ => val message = "No mapping for: " + propertyType; log(message); message
    }
  }

  def formMapping(property: TableProperty): String = {
    val int8 = new java.lang.String("int8")
    val mapping = property.propertyType.trim match {
      case ("Date" | "Timestamp" | "DATETIME") => "sqlDate(\"yyyy-MM-dd\")"
      case "String" => "nonEmptyText"
      case "Long" => "longNumber"
      case "Int" => "number"
      case "Boolean" => "boolean"
      case int8 => "longNumber"
    }
    if (property.isPrimaryKey || property.nullable) "optional(%s)".format(mapping)
    else mapping
  }
  
  // Gives a chance to rename some tables in the migration
  // Method that will rename eronate DB tablenames to entities with better meaningfull names
  def nameMapping(tablename:String): String = {
    val entityNames = utilities.EntityNames()
    entityNames.getOrElse(tablename,
        camelify(tablename.reverse.tail.reverse.toLowerCase)
        //"The"+camelify(tablename.toLowerCase)
        ) 
  }
  
}
