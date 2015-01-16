package emblem

import scala.reflect.runtime.universe._
import emblem.stringUtil._

/** a little container for all things related to emblem generation */
object emblemGenerator {

  /** an exception indicating you broke the contract of [[emblem.emblemFor]] */
  class EmblemGeneratorException(message: String) extends Exception(message)

  class TypeIsNotCaseClassException(
    val key: TypeKey[_ <: HasEmblem])
  extends EmblemGeneratorException(
    s"emblems for non-case classes is currently not supported: $key")

  class CaseClassHasMultipleParamListsException(
    val key: TypeKey[_ <: HasEmblem])
  extends EmblemGeneratorException(
    s"emblems for case classes with extra param lists currently not supported: $key")

  // TODO single pass refactor

  @throws[EmblemGeneratorException]
  private[emblem] def emblemFor[A <: HasEmblem : TypeKey]: Emblem[A] = {
    val key = implicitly[TypeKey[A]]
    val tpe = key.tpe
    verifyIsCaseClass(tpe, key)

    val symbol = tpe.typeSymbol.asClass
    val constructorSymbol = symbol.primaryConstructor.asMethod
    verifySingleParamList(constructorSymbol, key)
      
    // case classes guaranteed to have at least one param list
    val params: List[TermSymbol] = constructorSymbol.paramLists.head.map(_.asTerm)
    val propNames = params.map(_.name)

    val props = propNames.map(emblemProp[A](tpe, _))
    // TODO
    val propDefaults = null
    val creator = null

    new Emblem[A](typeNamePrefix(tpe), typeName(tpe), props, propDefaults, creator)
  }

  @throws[TypeIsNotCaseClassException]
  private def verifyIsCaseClass(tpe: Type, key: TypeKey[_ <: HasEmblem]): Unit = {
    val symbol = tpe.typeSymbol
    if (!symbol.isClass || !symbol.asClass.isCaseClass) {
      throw new TypeIsNotCaseClassException(key)
    }
  }

  @throws[CaseClassHasMultipleParamListsException]
  private def verifySingleParamList(constructorSymbol: MethodSymbol, key: TypeKey[_ <: HasEmblem]): Unit = {
    if (constructorSymbol.paramLists.size != 1) {
      throw new CaseClassHasMultipleParamListsException(key)
    }
  }

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
