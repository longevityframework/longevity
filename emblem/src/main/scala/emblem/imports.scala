package emblem

import scala.reflect.runtime.universe.TypeTag

/** standard set of imports for emblem. this will bring in all you need for basic emblem usage, and won't
 * pollute your namespace the way that `import emblem._` will
 */
object imports {

  /** a type alias for [[emblem.Emblem]] */
  type Emblem[A <: HasEmblem] = emblem.Emblem[A]

  /** an alias for object [[emblem.Emblem]] */
  val Emblem = emblem.Emblem

  /** a type alias for [[emblem.EmblemPool]] */
  type EmblemPool = emblem.EmblemPool

  /** an alias for object [[emblem.EmblemPool]] */
  val EmblemPool = emblem.EmblemPool

  /** a type alias for [[emblem.EmblemProp]] */
  type EmblemProp[A <: HasEmblem, B] = emblem.EmblemProp[A, B]

  /** an alias for object [[emblem.EmblemProp]] */
  val EmblemProp = emblem.EmblemProp

  /** a type alias for [[emblem.Extractor]] */
  type Extractor[Domain, Range] = emblem.Extractor[Domain, Range]

  /** an alias for object [[emblem.Extractor]] */
  val Extractor = emblem.Extractor

  /** a type alias for [[emblem.ExtractorPool]] */
  type ExtractorPool = emblem.ExtractorPool

  /** an alias for object [[emblem.ExtractorPool]] */
  val ExtractorPool = emblem.ExtractorPool

  /** a type alias for [[emblem.HasEmblem]] */
  type HasEmblem = emblem.HasEmblem

  /** a type alias for [[emblem.TypeBoundMap]] */
  type TypeBoundMap[TypeBound, Key[_ <: TypeBound], Val[_ <: TypeBound]] =
    emblem.TypeBoundMap[TypeBound, Key, Val]

  /** an alias for object [[emblem.TypeBoundMap]] */
  val TypeBoundMap = emblem.TypeBoundMap

  /** a type alias for [[emblem.TypeKey]] */
  type TypeKey[A] = emblem.TypeKey[A]

  /** an alias for object [[emblem.TypeKey]] */
  val TypeKey = emblem.TypeKey

  /** a type alias for [[emblem.TypeKeyMap]] */
  type TypeKeyMap[TypeBound, Val[_ <: TypeBound]] = emblem.TypeKeyMap[TypeBound, Val]

  /** an alias for object [[emblem.TypeKeyMap]] */
  val TypeKeyMap = emblem.TypeKeyMap

  /** delegates to [[emblem.typeKey]] */
  def typeKey[A : TypeKey]: TypeKey[A] = emblem.typeKey[A]

  /** delegates to [[emblem.typeKeyFromTag]] */
  implicit def typeKeyFromTag[A : TypeTag]: TypeKey[A] = emblem.typeKeyFromTag[A]

}
