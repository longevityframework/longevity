package emblem

import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers
import stringUtil.camelToUnderscore
import stringUtil.typeFullname
import stringUtil.typeName
import stringUtil.typeNamePrefix
import stringUtil.uncapitalize
import stringUtil.underscoreToCamel

/** [[stringUtil]] specifications */
class StringUtilSpec extends FlatSpec with GivenWhenThen with Matchers {

  behavior of "emblem.stringUtil.uncapitalize"

  it should "convert the first character of a string from uppercase to lowercase" in {
    uncapitalize("Foo") should equal ("foo")
    uncapitalize("FooBar") should equal ("fooBar")
  }

  it should "leave a string be if the first character is not uppercase" in {
    uncapitalize(null) should equal (null)
    uncapitalize("") should equal ("")
    uncapitalize("foo") should equal ("foo")
    uncapitalize("fooBar") should equal ("fooBar")
    uncapitalize("7Foo") should equal ("7Foo")
    uncapitalize("_Foo") should equal ("_Foo")
    uncapitalize("@Foo") should equal ("@Foo")
    uncapitalize(" Foo") should equal (" Foo")
  }

  behavior of "emblem.stringUtil.camelToUnderscore"

  it should "convert from CamelCase to snake_case" in {
    camelToUnderscore("") should equal ("")
    camelToUnderscore("foo") should equal ("foo")
    camelToUnderscore("Foo") should equal ("foo")
    camelToUnderscore("fooBar") should equal ("foo_bar")
    camelToUnderscore("FooBar") should equal ("foo_bar")
    camelToUnderscore("FooBarXYZ") should equal ("foo_bar_x_y_z")
  }

  behavior of "emblem.stringUtil.underscoreToCamel"

  it should "convert from snake_case to CamelCase" in {
    underscoreToCamel("") should equal ("")
    underscoreToCamel("foo") should equal ("foo")
    underscoreToCamel("Foo") should equal ("foo")
    underscoreToCamel("foo_bar") should equal ("fooBar")
    underscoreToCamel("Foo_Bar") should equal ("fooBar")
    underscoreToCamel("foo_bar_x_y_z") should equal ("fooBarXYZ")
    underscoreToCamel("Foo_Bar_X_Y_Z") should equal ("fooBarXYZ")
  }

  case class Foo()

  behavior of "emblem.stringUtil.typeFullname"
  it should "return the fullname for a type" in {
    typeFullname(typeKey[String].tpe) should equal ("java.lang.String")
    typeFullname(typeKey[Foo].tpe) should equal ("emblem.StringUtilSpec.Foo")
  }

  behavior of "emblem.stringUtil.typeName"
  it should "return the simple name for a type" in {
    typeName(typeKey[String].tpe) should equal ("String")
    typeName(typeKey[Foo].tpe) should equal ("Foo")
  }

  behavior of "emblem.stringUtil.typeNamePrefix"
  it should "return the name prefix for a type" in {
    typeNamePrefix(typeKey[String].tpe) should equal ("java.lang")
    typeNamePrefix(typeKey[Foo].tpe) should equal ("emblem.StringUtilSpec")
  }

}
