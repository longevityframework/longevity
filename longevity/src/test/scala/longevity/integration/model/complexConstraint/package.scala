package longevity.integration.model

import longevity.TestLongevityConfigs
import longevity.effect.Blocking
import longevity.model.annotations.domainModel
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.ScalacheckShapeless._
import org.scalacheck._

/** covers a persistent with a simple shorthand constraint */
package object complexConstraint {

  @domainModel trait DomainModel

  def genEmail = for {
    lhs <- arbitrary[String]
    rhs <- arbitrary[String]
  } yield Email(s"$lhs@$rhs.com")

  implicit val arbitraryEmail = Arbitrary(genEmail)

  def genComplexConstraint = for {
    id <- arbitrary[ComplexConstraintId]
    primary <- arbitrary[Email]
    secondaries <- arbitrary[Set[Email]]
  } yield ComplexConstraint(id, primary, secondaries + primary)

  implicit val arbitraryComplexConstraint = Arbitrary(genComplexConstraint)

  val contexts = TestLongevityConfigs.sparseContextMatrix[Blocking, DomainModel]()

}
