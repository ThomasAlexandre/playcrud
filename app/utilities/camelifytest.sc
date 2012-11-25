package utilities

import utilities.StringUtil._

object camelifytest {
  val tablenames = Set("SUPPLIERS", "COFFEES")    //> tablenames  : scala.collection.immutable.Set[String] = Set(SUPPLIERS, COFFEE
                                                  //| S)
                                                  

val result = tablenames map(x=>camelify(x.toLowerCase))
                                                  //> result  : scala.collection.immutable.Set[String] = Set(Suppliers, Coffees)
                                                  //

val result2 = s"/app/controllers/${camelify(tablenames.head.toLowerCase)}Controller.scala"
                                                  //> result2  : String = /app/controllers/SuppliersController.scala
}