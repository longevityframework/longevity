package longevity.unit.context

import longevity.TestLongevityConfigs.cassandraConfig
import longevity.TestLongevityConfigs.mongoConfig
import longevity.config.Cassandra
import longevity.context.LongevityContext
import longevity.config.MongoDB
import longevity.model.KeyVal
import longevity.model.ModelType
import longevity.model.PTypePool
import longevity.model.PType
import org.json4s.JsonAST.JObject
import org.json4s.JsonAST.JString
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers
import scala.concurrent.ExecutionContext.Implicits.global

/** provides a sample [[LongevityContext]] to use in testing */
object LongevityContextSpec {

  object sample {

    case class AId(id: String) extends KeyVal[A]

    case class A(id: AId)
    object A extends PType[A] {
      object props {
        val id = prop[AId]("id")
      }
      val keySet = Set(key(props.id))
    }

    trait DomainModel

    object DomainModel {
      implicit object modelType extends ModelType[DomainModel](PTypePool(A))
    }

    val mongoContext = new LongevityContext[DomainModel](mongoConfig)
    val cassandraContext = new LongevityContext[DomainModel](cassandraConfig)
  }

}

/** unit tests for the proper [[LongevityContext]] construction */
class LongevityContextSpec extends FlatSpec with GivenWhenThen with Matchers {

  val contextBackEndPairs = Seq(
    (LongevityContextSpec.sample.mongoContext, MongoDB),
    (LongevityContextSpec.sample.cassandraContext, Cassandra))

  for ((context, backEnd) <- contextBackEndPairs) {

    behavior of s"LongevityContext creation for ${context.config.backEnd}"

    it should "produce a context with the right model type" in {
      context.modelType should equal (LongevityContextSpec.sample.DomainModel.modelType)
    }

    it should "produce a context with the right back end" in {
      context.config.backEnd should equal (backEnd)
    }

    it should "provide RepoCrudSpecs against both test repos" in {
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
