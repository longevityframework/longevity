package longevity.unit.subdomain.root

import emblem.imports._
import longevity.exceptions.subdomain.root.EarlyIndexAccessException
import longevity.exceptions.subdomain.root.LateIndexDefException
import longevity.exceptions.subdomain.root.NumPropValsException
import longevity.exceptions.subdomain.root.PropValTypeException
import longevity.exceptions.subdomain.SubdomainException
import longevity.subdomain._
import org.scalatest._

/** sample domain for the IndexSpec tests */
object IndexSpec {

  object earlyIndexAccess {
    case class Early() extends Root
    object Early extends RootType[Early]
    Early.indexSet.foreach { k => println(k) }
    val entityTypes = EntityTypePool(Early)
    val subdomain = Subdomain("early index access", entityTypes)
  }

  object shorthands {
    implicit val shorthandPool = ShorthandPool.empty
  }
  import shorthands._

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

    val doubleIndex = IndexSampler.index(booleanProp, charProp)
    val tripleIndex = IndexSampler.index(booleanProp, charProp, doubleProp)
  }

  val entityTypes = EntityTypePool(IndexSampler)
  val subdomain = Subdomain("Index Spec", entityTypes)(shorthandPool)

}

/** unit tests for the proper construction of [[Index indexs]] */
class IndexSpec extends FlatSpec with GivenWhenThen with Matchers {

  import IndexSpec._
  import IndexSpec.IndexSampler._

  behavior of "RootType.indexes"
  it should "throw exception when called before subdomain initialization" in {
    // this is an artifact of the un-artful way i constructed the test
    val e = intercept[ExceptionInInitializerError] {
      val x = earlyIndexAccess.subdomain
    }
    e.getCause shouldBe a [EarlyIndexAccessException]
  }

  behavior of "RootType.index factory methods"

  they should "throw exception when called after subdomain initialization" in {

    // trigger subdomain initialization
    import longevity.context._
    val longevityContext = LongevityContext(IndexSpec.subdomain, Mongo)

    intercept[LateIndexDefException] {
      IndexSampler.index(booleanProp, charProp)
    }
  }

  behavior of "Index.props"
  it should "produce the same sequence of properties that was used to create the index" in {
    IndexSampler.tripleIndex.props should equal (Seq(booleanProp, charProp, doubleProp))
  }

}
