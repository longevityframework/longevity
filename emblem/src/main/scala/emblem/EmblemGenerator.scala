package emblem

import scala.reflect.ClassTag
import scala.reflect.runtime.currentMirror
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
    // TODO move the prop defaults into the props themselves
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
    val key = typeKey[A]
    val memberTerm = tpe.member(name).asTerm.accessed.asTerm
    val propType = memberTerm.typeSignature
    val propTypeTag = makeTypeTag[Any](propType) // the Any here is bogus
    val propKey = TypeKey(propTypeTag)
    makeEmblemProp(tpe, name)(key, propKey)
  }

  // following FixedMirrorTypeCreator in
  // https://github.com/scala/scala/blob/2.11.x/src/reflect/scala/reflect/internal/StdCreators.scala
  private def makeTypeTag[A](tpe: Type): TypeTag[A] = {
    import scala.reflect.api.Mirror
    import scala.reflect.api.TypeCreator
    import scala.reflect.api.Universe
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
    tpe: Type, name: TermName)(
    key: TypeKey[T], propKey: TypeKey[U]
  ): EmblemProp[T, U] = {

    val getter = tpe.decl(name).asMethod
    val getFunction = makeGetFunction[T, U](getter)(key, propKey)

    val copy: MethodSymbol = tpe.decl(TermName("copy")).asMethod
    val setFunction = makeSetFunction[T, U](name, copy)(key, propKey)
    
    EmblemProp[T, U](name.toString, getFunction, setFunction)(key, propKey)
  }

  private def makeGetFunction[T <: HasEmblem : TypeKey, U : TypeKey](getter: MethodSymbol): (T) => U = {
    implicit val typeTag = typeKey[T].tag
    implicit val classTag = typeTagToClassTag[T]
    val getFunction = { t: T =>
      val instanceMirror = currentMirror.reflect(t)
      val methodMirror = instanceMirror.reflectMethod(getter)
      methodMirror().asInstanceOf[U]
    }
    getFunction
  }

  private def typeTagToClassTag[T: TypeTag]: ClassTag[T] = {
    ClassTag[T](typeTag[T].mirror.runtimeClass(typeTag[T].tpe))
  }

  private def makeSetFunction[T <: HasEmblem : TypeKey, U : TypeKey](
    name: TermName, copy: MethodSymbol
  ): (T, U) => T = {
    val key = typeKey[T]
    val copyParams = singleParamList(copy, key)
    implicit val typeTag = key.tag
    implicit val classTag = typeTagToClassTag[T]
    val setFunction = { (t: T, u: U) =>
      val instanceMirror = currentMirror.reflect(t)
      val copyMirror = instanceMirror.reflectMethod(copy)
      val copyArgs = copyParams.map { param =>
        if (param.name == name) {
          u
        }
        else {
          val getter = typeTag.tpe.decl(param.name).asMethod
          val getterMirror = instanceMirror.reflectMethod(getter)
          getterMirror()
        }
      }
      copyMirror(copyArgs: _*).asInstanceOf[T]
    }
    setFunction
  }

  private def singleParamList(method: MethodSymbol, key: TypeKey[_ <: HasEmblem]) = {
    val methodParamLists = method.paramLists
    if (methodParamLists.size != 1) {
      throw new CaseClassHasMultipleParamListsException(key)
    }
    methodParamLists.head
  }

}
