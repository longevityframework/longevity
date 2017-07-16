package longevity.integration.model

import longevity.TestLongevityConfigs
import longevity.model.annotations.domainModel
import longevity.effect.Blocking
import org.scalacheck._
import org.scalacheck.Arbitrary.arbitrary

/** covers a persistent with a simple shorthand constraint */
package object simpleConstraint {

  @domainModel trait DomainModel

  def genEmail = for {
    lhs <- arbitrary[String]
    rhs <- arbitrary[String]
  } yield Email(s"$lhs@$rhs.com")

  implicit val arbitraryEmail = Arbitrary(genEmail)

  val contexts = TestLongevityConfigs.sparseContextMatrix[Blocking, DomainModel]()

}
