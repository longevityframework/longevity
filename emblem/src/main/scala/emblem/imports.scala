package emblem

import scala.reflect.runtime.universe.TypeTag

/** standard set of imports for emblem. this will bring in all you need for basic emblem usage, and won't
 * pollute your namespace the way that `import emblem._` will
 */
object imports {

  type Emblem[A <: HasEmblem] = emblem.Emblem[A]

  val Emblem = emblem.Emblem

  type EmblemPool = emblem.EmblemPool

  val EmblemPool = emblem.EmblemPool

  type EmblemProp[A <: HasEmblem, B] = emblem.EmblemProp[A, B]

  val EmblemProp = emblem.EmblemProp

  type Extractor[Domain, Range] = emblem.Extractor[Domain, Range]

  val Extractor = emblem.Extractor

  type ExtractorPool = emblem.ExtractorPool

  val ExtractorPool = emblem.ExtractorPool

  type HasEmblem = emblem.HasEmblem

  type TypeBoundMap[TypeBound, Key[_ <: TypeBound], Val[_ <: TypeBound]] =
    emblem.TypeBoundMap[TypeBound, Key, Val]

  val TypeBoundMap = emblem.TypeBoundMap

  type TypeKey[A] = emblem.TypeKey[A]

  val TypeKey = emblem.TypeKey

  type TypeKeyMap[TypeBound, Val[_ <: TypeBound]] = emblem.TypeKeyMap[TypeBound, Val]

  val TypeKeyMap = emblem.TypeKeyMap

  def typeKey[A : TypeKey]: TypeKey[A] = emblem.typeKey[A]

  implicit def typeKeyFromTag[A : TypeTag]: TypeKey[A] = emblem.typeKeyFromTag[A]

}
