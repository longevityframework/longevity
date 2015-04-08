package emblem

import org.scalatest._

/** [[stringUtil string util]] specifications */
class StringUtilSpec extends FlatSpec with GivenWhenThen with Matchers {

  import stringUtil._

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

  // TODO pt-86950732: specs for camelToUnderscores, underscoreToCamel

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
