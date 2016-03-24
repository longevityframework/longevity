package longevity.unit.subdomain.ptype

import emblem.imports._
import longevity.subdomain._
import org.scalatest._

/** sample domain for the IndexSpec tests */
object IndexSpec {

  case class IndexSampler(
    boolean: Boolean,
    char: Char,
    double: Double,
    float: Float,
    int: Int,
    long: Long)
  extends Root

  object IndexSampler extends RootType[IndexSampler] {
    val booleanProp = IndexSampler.prop[Boolean]("boolean")
    val charProp = IndexSampler.prop[Char]("char")
    val doubleProp = IndexSampler.prop[Double]("double")

    object keys {
    }
    object indexes {
      val double = index(booleanProp, charProp)
      val triple = index(booleanProp, charProp, doubleProp)
    }
  }

}

/** unit tests for the proper construction of [[Index indexs]] */
class IndexSpec extends FlatSpec with GivenWhenThen with Matchers {

  import IndexSpec._
  import IndexSpec.IndexSampler._

  behavior of "Index.props"
  it should "produce the same sequence of properties that was used to create the index" in {
    IndexSampler.indexes.double.props should equal (Seq(booleanProp, charProp))
    IndexSampler.indexes.triple.props should equal (Seq(booleanProp, charProp, doubleProp))
  }

}
