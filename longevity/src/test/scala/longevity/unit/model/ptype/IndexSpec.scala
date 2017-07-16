package longevity.unit.model.ptype

import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers
import org.scalatest.OptionValues

/** unit tests for the proper construction of [[Index indexs]] */
class IndexSpec extends FlatSpec with GivenWhenThen with Matchers with OptionValues {

  import indexSpec.IndexSampler
  import indexSpec.IndexSampler.props

  "Index.props" should "produce the same sequence of properties that was used to create the index" in {
    IndexSampler.indexSet.size should equal (2)

    val double = IndexSampler.indexSet.find(_.props.size == 2).value
    double.props should equal (Seq(props.boolean, props.char))

    val triple = IndexSampler.indexSet.find(_.props.size == 3).value
    triple.props should equal (Seq(props.boolean, props.char, props.double))
  }

}
