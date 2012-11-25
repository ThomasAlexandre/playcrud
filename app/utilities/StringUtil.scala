/*
 * Copyright (C) 2012 Thomas Alexandre
 */
package utilities

object StringUtil {

  /**
   * Turns a string of format "foo_bar" into camel case "FooBar"
   *
   * Functional code courtesy of Jamie Webb (j@jmawebb.cjb.net) 2006/11/28
   * @param name the String to CamelCase
   *
   * @return the CamelCased string
   */
  def camelify(name: String): String = {
    def loop(x: List[Char]): List[Char] = (x: @unchecked) match {
      case '_' :: '_' :: rest => loop('_' :: rest)
      case '_' :: c :: rest => Character.toUpperCase(c) :: loop(rest)
      case '_' :: Nil => Nil
      case c :: rest => c :: loop(rest)
      case Nil => Nil
    }
    if (name == null)
      ""
    else
      loop('_' :: name.toList).mkString
  }

  /**
   * Turn a string of format "foo_bar" or "FOO_BAR" into camel case with the first letter in lower case: "fooBar"
   * This function is especially used to camelCase method names.
   *
   * @param name the String to CamelCase
   *
   * @return the CamelCased string
   */
  def camelifyMethod(name: String): String = {
    val tmp: String = camelify(name.toLowerCase)
    if (tmp.length == 0)
      ""
    else
      tmp.substring(0, 1).toLowerCase + tmp.substring(1)
  }

  def getAsCommaSeparatedStrings(strings: Iterable[String]): String = {
    strings.map("\"" + _ + "\"").reduceLeft[String]((y, z) => y + "," + z)
  }

}
