import scala.reflect.runtime.universe._

import scala.language.implicitConversions

/** a collection of utilities for reflecting on types */
package object emblem {

  /** returns a [[TypeKey]] for the specified type `A`. this method will only work where a `TypeTag` is
   * implicitly available. */
  def typeKey[A : TypeKey]: TypeKey[A] = implicitly[TypeKey[A]]

  /** an implicit method for producing a [[TypeKey]]. this method allows type keys to be available implicitly
   * anywhere that the corresponding `TypeTag` is implicitly available. */
  implicit def typeKeyFromTag[A : TypeTag]: TypeKey[A] = TypeKey(implicitly[TypeTag[A]])

  def emblemFor[A <: HasEmblem : TypeKey]: Emblem[A] = {
    val key = implicitly[TypeKey[A]]
    val tpe = key.tpe

    verifyIsCaseClass(tpe)
    val symbol = classSymbol(tpe)

    val namePrefix = stringUtil.typeNamePrefix(tpe)
    val name = stringUtil.typeName(tpe)
    // TODO
    val props = Seq()
    val propDefaults = null
    val creator = null

    new Emblem[A](namePrefix, name, props, propDefaults, creator)
  }

  private def verifyIsCaseClass[A <: HasEmblem : TypeKey](tpe: Type): Unit = {
    val symbol = tpe.typeSymbol
    if (!symbol.isClass) {
      throw new TypeIsNotCaseClassException[A]
    }
    if (!symbol.asClass.isCaseClass) {
      throw new TypeIsNotCaseClassException[A]
    }
  }

  private def classSymbol(tpe: Type): ClassSymbol = tpe.typeSymbol.asClass

  private def emblemProp[A <: HasEmblem](): EmblemProp[A, _] = {
    ???
  }

}
