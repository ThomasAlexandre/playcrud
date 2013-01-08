package utilities

/*
 * Temporary: slick case classes will probably be later generated out of type macros
 * (See Macro Paradise feature in Scala 2.11)
 * Mapping table names to case class names:
 * TABLENAME -> case class name used in slick
 * 
 * Note: if tablename is "BASIC_PROCESS" or "BASICPROCESS", case class name MUST NOT be BasicProcess,
 * otherwise it will generate compilation errors.
 */
object EntityNames {
  def apply() = Map(
      "A"->"caseClassA",
      "COFFEES"->"TheCoffee",
      "BASIC_PROCESS"->"TheBasicProcess")
}