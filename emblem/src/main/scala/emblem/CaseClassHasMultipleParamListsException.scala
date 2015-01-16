package emblem

class CaseClassHasMultipleParamListsException[A <: HasEmblem : TypeKey]
extends Exception(s"emblems for case classes with extra param lists currently not supported: ${typeKey[A]}") {

  val key = typeKey[A]
}
