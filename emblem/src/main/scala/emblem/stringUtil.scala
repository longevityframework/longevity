package emblem

import scala.reflect.runtime.universe.Type

/** generally useful utility functions for working with strings */
object stringUtil {

  /** returns this string with first character converted to lower case.
   * if the first character of the string is lower case, the string is returned unchanged.
   * 
   * based on function capitalize in scala library StringLike.scala
   */
  def uncapitalize(s: String): String = {
    if (s == null) null
    else if (s.length == 0) ""
    else if (s.charAt(0).isLower) toString
    else {
      val chars = s.toCharArray
      chars(0) = chars(0).toLower
      new String(chars)
    }
  }

  /** takes a camel cased identifier name and returns an underscore separated
   * name.
   *
   * example:
   *     camelToUnderscores("ThisIsA1Test") == "this_is_a_1_test"
   *
   * copied from https://gist.github.com/sidharthkuruvila/3154845
   */
  def camelToUnderscore(name: String) = "[A-Z\\d]".r.replaceAllIn(uncapitalize(name), { m =>
    "_" + m.group(0).toLowerCase()
  })
 
  /** takes an underscore separated identifier name and returns a camel cased one.
   *
   * example:
   *    underscoreToCamel("this_is_a_1_test") == "thisIsA1Test"
   *
   * copied from https://gist.github.com/sidharthkuruvila/3154845
   */
  def underscoreToCamel(name: String) = "_([a-z\\d])".r.replaceAllIn(uncapitalize(name), { m =>
    m.group(1).toUpperCase()
  })

  /** returns a full type name for a type */
  def typeFullname(tpe: Type) = tpe.typeSymbol.fullName.toString

  /** returns a simple type name for a type */
  def typeName(tpe: Type) = tpe.typeSymbol.name.decodedName.toString

  /** returns a type name prefix for a type */
  def typeNamePrefix(tpe: Type) = {
    val fullname = typeFullname(tpe)
    fullname.substring(0, fullname.lastIndexOf('.'))
  }

}
