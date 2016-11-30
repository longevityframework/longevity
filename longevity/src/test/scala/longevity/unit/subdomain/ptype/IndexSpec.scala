package longevity.unit.subdomain.ptype

import longevity.subdomain.PType
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers
import org.scalatest.OptionValues

/** sample domain for the IndexSpec tests */
object IndexSpec {

  case class IndexSampler(
    boolean: Boolean,
    char: Char,
    double: Double,
    float: Float,
    int: Int,
    long: Long) 

  object IndexSampler extends PType[IndexSampler] {
    object props {
      val boolean = prop[Boolean]("boolean")
      val char = prop[Char]("char")
      val double = prop[Double]("double")
    }
    val keySet = emptyKeySet
    override val indexSet = Set(
      index(props.boolean, props.char),
      index(props.boolean, props.char, props.double))
  }

}

/** unit tests for the proper construction of [[Index indexs]] */
class IndexSpec extends FlatSpec with GivenWhenThen with Matchers with OptionValues {

  import IndexSpec.IndexSampler
  import IndexSpec.IndexSampler.props

  "Index.props" should "produce the same sequence of properties that was used to create the index" in {
    IndexSampler.indexSet.size should equal (2)

    val double = IndexSampler.indexSet.find(_.props.size == 2).value
    double.props should equal (Seq(props.boolean, props.char))

    val triple = IndexSampler.indexSet.find(_.props.size == 3).value
    triple.props should equal (Seq(props.boolean, props.char, props.double))
  }

}
