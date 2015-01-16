package emblem

class TypeIsNotCaseClassException[A <: HasEmblem : TypeKey]
extends Exception(s"emblems for non-case classes is currently not supported: ${typeKey[A]}") {

  val key = typeKey[A]
}
