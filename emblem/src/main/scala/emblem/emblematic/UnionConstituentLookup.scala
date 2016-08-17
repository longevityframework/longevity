package emblem.emblematic

import emblem.TypeKey

private[emblematic] class UnionConstituentLookup[A](
  val constituents: Set[Emblem[_ <: A]]) {

  val emblems: EmblemPool = constituents.foldLeft(EmblemPool()) { (pool, emblem) =>
    def addToPool[A](emblem: Emblem[A]) = pool + (emblem.typeKey -> emblem)
    addToPool(emblem)
  }

  val constituentKeys: Set[TypeKey[_ <: A]] = constituents.map(_.typeKey)

  /** @see [[Union.typeKeyForInstance]] */
  def typeKeyForInstance(a: A): Option[TypeKey[_ <: A]] = {
    typeKeyForName(a.getClass.getSimpleName)
  }
 
  /** @see [[Union.typeKeyForName]] */
  def typeKeyForName(name: String): Option[TypeKey[_ <: A]] = {
    // trimming the "$" is a special-case for handling case objects
    val trimmed = if (name.endsWith("$")) name.dropRight(1) else name
    constituentKeysByName.get(trimmed)
  }

  def emblemForInstance(a: A): Option[Emblem[_ <: A]] = {
    emblemForName(a.getClass.getSimpleName)
  }
 
  def emblemForName(name: String): Option[Emblem[_ <: A]] =
    emblemsByName.get(name)

  private val constituentKeysByName: Map[String, TypeKey[_ <: A]] =
    constituentKeys.map(c => (c.name, c)).toMap

  private val emblemsByName: Map[String, Emblem[_ <: A]] =
    constituentKeysByName.mapValues(emblems(_))

}
