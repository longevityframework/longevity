package emblem

import org.scalatest._
import org.scalatest.OptionValues._

/** [[stringUtil string util]] specifications */
class StringUtilSpec extends FlatSpec with GivenWhenThen with Matchers {

  // TODO specs for uncapitalize, camelToUnderscores, underscoreToCamel

  case class Foo()

  behavior of "emblem.stringUtil.typeFullname"
  it should "return the fullname for a type" in {
    stringUtil.typeFullname(typeKey[String].tpe) should equal ("java.lang.String")
    stringUtil.typeFullname(typeKey[Foo].tpe) should equal ("emblem.StringUtilSpec.Foo")
  }

  behavior of "emblem.stringUtil.typeName"
  it should "return the simple name for a type" in {
    stringUtil.typeName(typeKey[String].tpe) should equal ("String")
    stringUtil.typeName(typeKey[Foo].tpe) should equal ("Foo")
  }

  behavior of "emblem.stringUtil.typeNamePrefix"
  it should "return the name prefix for a type" in {
    stringUtil.typeNamePrefix(typeKey[String].tpe) should equal ("java.lang")
    stringUtil.typeNamePrefix(typeKey[Foo].tpe) should equal ("emblem.StringUtilSpec")
  }

}
