package emblem

class TypeIsNotCaseClassException[A <: HasEmblem : TypeKey]
extends Exception("I only know how to generate emblems and shorthands for case classes") {

  val key = typeKey[A]
}
