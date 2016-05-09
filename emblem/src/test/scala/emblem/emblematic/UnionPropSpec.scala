package emblem.emblematic

import emblem.exceptions.NoSuchPropertyException
import emblem.testData.exhaustive.Specialization1
import emblem.testData.exhaustive.Specialization2
import emblem.testData.exhaustive.Specialization3
import emblem.testData.exhaustive.Specialization4
import emblem.testData.exhaustive.Specialization5
import emblem.testData.exhaustive.Specialization6
import emblem.testData.exhaustive.Specialization7
import emblem.testData.exhaustive.Specialization8
import emblem.testData.exhaustive.classWithAbstractPropUnion
import emblem.testData.exhaustive.classWithConcretePropUnion
import emblem.testData.exhaustive.traitWithAbstractPropUnion
import emblem.testData.exhaustive.traitWithConcretePropUnion
import emblem.typeKey
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

/** [[UnionProp union property]] specifications */
class UnionPropSpec extends FlatSpec with GivenWhenThen with Matchers {

  private val prop1 = traitWithAbstractPropUnion.prop[String]("common")
  private val spec1 = Specialization1("common 12", "special 123")
  private val spec2 = Specialization2("common 23", "special 216")

  private val prop2 = traitWithConcretePropUnion.prop[String]("common")
  private val spec3 = Specialization3("special 343")
  private val spec4 = Specialization4("common 45", "special 420")

  private val prop3 = classWithAbstractPropUnion.prop[String]("common")
  private val spec5 = Specialization5("common 52", "special 512")
  private val spec6 = Specialization6("common 63", "special 612")

  private val prop4 = classWithConcretePropUnion.prop[String]("common")
  private val spec7 = Specialization7("special 729")
  private val spec8 = Specialization8("common 85", "special 888")

  "UnionProp.name" should "return the simple name of the property" in {
    prop1.name should equal ("common")
    prop2.name should equal ("common")
    prop3.name should equal ("common")
    prop4.name should equal ("common")
  }

  "UnionProp.typeKey" should "return the property value type" in {
    prop1.typeKey should equal (typeKey[String])
    prop2.typeKey should equal (typeKey[String])
    prop3.typeKey should equal (typeKey[String])
    prop4.typeKey should equal (typeKey[String])
  }

  "UnionProp.toString" should "return a string containing the name and the type" in {
    prop1.toString should equal ("common: String")
    prop2.toString should equal ("common: String")
    prop3.toString should equal ("common: String")
    prop4.toString should equal ("common: String")
  }

  "UnionProp.get" should "return the property value for the supplied instance" in {
    prop1.get(spec1) should equal (spec1.common)
    prop1.get(spec2) should equal (spec2.common)
    prop2.get(spec3) should equal (spec3.common)
    prop2.get(spec4) should equal (spec4.common)
    prop3.get(spec5) should equal (spec5.common)
    prop3.get(spec6) should equal (spec6.common)
    prop4.get(spec7) should equal (spec7.common)
    prop4.get(spec8) should equal (spec8.common)
  }

}
