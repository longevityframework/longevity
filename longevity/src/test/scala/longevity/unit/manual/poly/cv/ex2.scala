package longevity.unit.manual.poly.cv.ex2

@longevity.model.annotations.domainModel trait DomainModel

// end prelude

import longevity.model.annotations.persistent

@persistent[DomainModel]
case class Account(
  name: String,
  accountStatus: AccountStatus)

import longevity.model.annotations.polyComponent
import longevity.model.annotations.derivedComponent

@polyComponent[DomainModel]
sealed trait AccountStatus

@derivedComponent[DomainModel, AccountStatus]
case object Active extends AccountStatus

@derivedComponent[DomainModel, AccountStatus]
case object Suspended extends AccountStatus

@derivedComponent[DomainModel, AccountStatus]
case object Cancelled extends AccountStatus
