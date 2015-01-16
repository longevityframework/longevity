package emblem

import scala.reflect.runtime.universe._
import emblem.stringUtil._

// TODO single pass refactor

private[emblem] object emblemGenerator {

  @throws[TypeIsNotCaseClassException[_]]
  @throws[CaseClassHasMultipleParamListsException[_]]
  def emblemFor[A <: HasEmblem : TypeKey]: Emblem[A] = {
    val key = implicitly[TypeKey[A]]
    val tpe = key.tpe

    verifyIsCaseClass(tpe)
    val symbol = classSymbol(tpe)
    val constructorSymbol = symbol.primaryConstructor.asMethod

    // TODO: handle extra param lists situation
    if (constructorSymbol.paramLists.size != 1) {
      throw new CaseClassHasMultipleParamListsException[A]
    }
      
    // case classes guaranteed to have at least one param list
    val params: List[TermSymbol] = constructorSymbol.paramLists.head.map(_.asTerm)
    val propNames = params.map(_.name)

    val props = propNames.map(emblemProp[A](tpe, _))
    // TODO
    val propDefaults = null
    val creator = null

    new Emblem[A](typeNamePrefix(tpe), typeName(tpe), props, propDefaults, creator)
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

  private def emblemProp[A <: HasEmblem : TypeKey](tpe: Type, name: TermName): EmblemProp[A, _] = {
    val memberTerm = tpe.member(name).asTerm.accessed.asTerm
    val propType = memberTerm.typeSignature
    val getter = memberTerm.getter

    val propTypeTag = makeTypeTag[Any](propType)
    val propTypeKey = TypeKey(propTypeTag)

    EmblemProp(name.toString, null, null)(typeKey[A], propTypeKey)
  }

  private def makeTypeTag[A](tpe: Type): TypeTag[A] = {
    import scala.reflect.api.Mirror
    import scala.reflect.api.TypeCreator
    import scala.reflect.api.Universe
    import scala.reflect.runtime.currentMirror
    val typeCreator = new TypeCreator {
      def apply[U <: Universe with Singleton](m: Mirror[U]): U # Type =
        if (m eq currentMirror)
          tpe.asInstanceOf[U # Type]
        else
          throw new IllegalArgumentException(
            s"Type tag defined in $currentMirror cannot be migrated to other mirrors.")
    }
    TypeTag[A](currentMirror, typeCreator)
  }

  private def makeEmblemProp[T <: HasEmblem : TypeKey, U : TypeKey](
    name: String,
    get: (T) => U,
    set: (T, U) => T,
    tag: TypeTag[U]
  ) = EmblemProp[T, U](name, get, set)  
  
}
