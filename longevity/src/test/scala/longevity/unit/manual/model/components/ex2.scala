package longevity.unit.manual.model.components.ex2

import longevity.model.CType

case class FullName(
  firstName: String,
  lastName: String)

object FullName extends CType[FullName]
