package longevity.integration.model

import cats.effect.IO
import longevity.TestLongevityConfigs
import longevity.context.LongevityContext
import longevity.effect.Blocking
import longevity.effect.cats.ioEffect
import longevity.model.annotations.domainModel
import org.scalatest.Suite
import scala.concurrent.Future

/** covers a persistent with attributes of every supported basic type */
package object basics {

  @domainModel trait DomainModel

  // we use basics to cover all config possibilities. other model tests use a sparse context matrix
  val blockingContexts = TestLongevityConfigs.contextMatrix[Blocking, DomainModel]()
  val futureContexts = TestLongevityConfigs.contextMatrix[Future, DomainModel]()
  val catsIoContexts = TestLongevityConfigs.contextMatrix[IO, DomainModel]()

  def repoCrudSpecs[F[_], M](contexts: Seq[LongevityContext[F, M]]): Seq[Suite] = contexts.map(_.repoCrudSpec)

}
