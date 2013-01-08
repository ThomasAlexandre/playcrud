package utilities

import utilities.StringUtil._

object camelifytest {
  val tablenames = Set("SUPPLIERS", "COFFEES","A")//> tablenames  : scala.collection.immutable.Set[String] = Set(SUPPLIERS, COFFEE
                                                  //| S, A)
                                                  

val result = tablenames map(x=>camelify(x.toLowerCase))
                                                  //> result  : scala.collection.immutable.Set[String] = Set(Suppliers, Coffees, A
                                                  //| )
                                                  //

val result2 = s"/app/controllers/${camelify(tablenames.head.toLowerCase)}Controller.scala"
                                                  //> result2  : String = /app/controllers/SuppliersController.scala


 val entityNames = tablenames map (DBUtil.nameMapping(_))
                                                  //> entityNames  : scala.collection.immutable.Set[String] = Set(Supplier, Coffee
                                                  //| , entityA)
}