package emblem

import scala.reflect.runtime.universe.TypeTag

package object stringUtil {

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
  def camelToUnderscores(name: String) = "[A-Z\\d]".r.replaceAllIn(uncapitalize(name), { m =>
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

  /** returns a simple type name for a type tag */
  def typeName(typeTag: TypeTag[_]) = typeTag.tpe.typeSymbol.name.decodedName.toString

}
