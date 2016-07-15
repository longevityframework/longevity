package longevity.unit.context

import longevity.context.LongevityContext
import longevity.context.Mongo
import longevity.subdomain.KeyVal
import longevity.subdomain.Subdomain
import longevity.subdomain.persistent.Root
import longevity.subdomain.ptype.PTypePool
import longevity.subdomain.ptype.RootType
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers
import scala.concurrent.ExecutionContext.Implicits.global

/** provides a sample [[LongevityContext]] to use in testing */
object LongevityContextSpec {

  object sample {

    case class AId(id: String) extends KeyVal[A, AId](A.keys.id)

    case class A(id: AId) extends Root
    object A extends RootType[A] {
      object props {
        val id = prop[AId]("id")
      }
      object keys {
        val id = key(props.id)
      }
      object indexes {
      }
    }

    val subdomain = Subdomain("subtypePropType", PTypePool(A))
    val context = LongevityContext(subdomain, Mongo)
  }

}

/** unit tests for the proper [[LongevityContext]] construction */
class LongevityContextSpec extends FlatSpec with GivenWhenThen with Matchers {

  val context = LongevityContextSpec.sample.context

  behavior of "LongevityContext creation"

  it should "produce a context with the right subdomain" in {
    context.subdomain should equal (LongevityContextSpec.sample.subdomain)
  }

  it should "produce a context with the right persistence strategy" in {
    context.persistenceStrategy should equal (Mongo)
  }

  it should "produce repo pools of the right size" in {
    context.repoPool.values.size should equal (1)
    context.testRepoPool.values.size should equal (1)
    context.inMemTestRepoPool.values.size should equal (1)
  }

  it should "provide RepoCrudSpecs against both test repo pools" in {
    context.repoCrudSpec should not be (null)
    context.inMemRepoCrudSpec should not be (null)
  }

}
