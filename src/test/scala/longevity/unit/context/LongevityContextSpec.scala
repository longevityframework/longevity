package longevity.unit.context

import longevity.context.Cassandra
import longevity.context.LongevityContext
import longevity.context.Mongo
import longevity.subdomain.KeyVal
import longevity.subdomain.Subdomain
import longevity.subdomain.persistent.Root
import longevity.subdomain.ptype.PTypePool
import longevity.subdomain.ptype.RootType
import org.json4s.JsonAST.JObject
import org.json4s.JsonAST.JString
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
    }

    val subdomain = Subdomain("subtypePropType", PTypePool(A))
    val mongoContext = LongevityContext(subdomain, Mongo)
    val cassandraContext = LongevityContext(subdomain, Cassandra)
  }

}

/** unit tests for the proper [[LongevityContext]] construction */
class LongevityContextSpec extends FlatSpec with GivenWhenThen with Matchers {

  val contextStratPairs = Seq(
    (LongevityContextSpec.sample.mongoContext, Mongo),
    (LongevityContextSpec.sample.cassandraContext, Cassandra))

  for ((context, strat) <- contextStratPairs) {

    behavior of s"LongevityContext creation for ${context.persistenceStrategy}"

    it should "produce a context with the right subdomain" in {
      context.subdomain should equal (LongevityContextSpec.sample.subdomain)
    }

    it should "produce a context with the right persistence strategy" in {
      context.persistenceStrategy should equal (strat)
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

    it should "provide a working JSON marshaller" in {
      import LongevityContextSpec.sample.AId
      val marshaller = context.jsonMarshaller
      val jvalue = marshaller.marshall(AId("foo"))
      jvalue shouldBe a [JObject]
      val jobject = jvalue.asInstanceOf[JObject]
      jobject.values.size should equal (1)
      jobject.values.contains("id") should be (true)
      jobject.values("id") should equal ("foo")
    }

    it should "provide a working JSON unmarshaller" in {
      import LongevityContextSpec.sample.AId
      val unmarshaller = context.jsonUnmarshaller
      val aid = unmarshaller.unmarshall[AId](JObject(("id", JString("foo")) :: Nil))
      aid should equal (AId("foo"))
    }

  }

}
