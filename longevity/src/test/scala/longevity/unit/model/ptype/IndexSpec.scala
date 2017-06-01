package longevity.unit.model.ptype

import longevity.model.ModelEv
import longevity.model.PType
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers
import org.scalatest.OptionValues

/** sample domain for the IndexSpec tests */
object IndexSpec {

  trait DomainModel
  object DomainModel {
    private[IndexSpec] implicit object modelEv extends ModelEv[DomainModel]
  }

  case class IndexSampler(
    boolean: Boolean,
    char: Char,
    double: Double,
    float: Float,
    int: Int,
    long: Long) 

  object IndexSampler extends PType[DomainModel, IndexSampler] {
    object props {
      val boolean = prop[Boolean]("boolean")
      val char = prop[Char]("char")
      val double = prop[Double]("double")
    }
    lazy val keySet = emptyKeySet
    override lazy val indexSet = Set(
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
