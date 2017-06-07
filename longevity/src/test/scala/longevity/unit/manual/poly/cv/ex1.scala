package longevity.unit.manual.poly.cv.ex1

sealed trait AccountStatus
case object Active extends AccountStatus
case object Suspended extends AccountStatus
case object Cancelled extends AccountStatus
