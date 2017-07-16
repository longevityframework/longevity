package longevity.unit.manual.poly.persistents.ex2

import longevity.model.annotations.domainModel
@domainModel trait DomainModel

case class Email()
case class Markdown()
case class Uri()
case class Username()

// end prelude

import longevity.model.CType
import longevity.model.DerivedPType
import longevity.model.PEv
import longevity.model.PolyPType

case class UserProfile(
  tagline: String,
  imageUri: Uri,
  description: Markdown)

object UserProfile extends CType[DomainModel, UserProfile]

sealed trait User {
  val username: Username
  val email: Email
}

object User extends PolyPType[DomainModel, User] {
  implicit val pEv: PEv[DomainModel, User] = {
    import org.scalacheck.ScalacheckShapeless._
    implicit val arbJoda = com.fortysevendeg.scalacheck.datetime.joda.ArbitraryJoda.arbJoda
    new PEv
  }
  object props {
    // ...
  }
}

case class Member(
  username: Username,
  email: Email,
  profile: UserProfile)
extends User

object Member extends DerivedPType[DomainModel, Member, User] {
  implicit val pEv: PEv[DomainModel, Member] = {
    import org.scalacheck.ScalacheckShapeless._
    implicit val arbJoda = com.fortysevendeg.scalacheck.datetime.joda.ArbitraryJoda.arbJoda
    new PEv
  }
  object props {
    // ...
  }
}

case class Commenter(
  username: Username,
  email: Email)
extends User

object Commenter extends DerivedPType[DomainModel, Commenter, User] {
  implicit val pEv: PEv[DomainModel, Commenter] = {
    import org.scalacheck.ScalacheckShapeless._
    implicit val arbJoda = com.fortysevendeg.scalacheck.datetime.joda.ArbitraryJoda.arbJoda
    new PEv
  }
  object props {
    // ...
  }
}
