package longevity.emblem.reflectionUtil

import typekey.TypeKey
import longevity.emblem.exceptions.CaseClassIsInnerClassException
import longevity.emblem.exceptions.TypeIsNotCaseClassException
import scala.reflect.runtime.universe.ClassSymbol
import scala.reflect.runtime.universe.ModuleMirror
import scala.reflect.runtime.universe.ModuleSymbol
import scala.reflect.runtime.universe.Symbol
import scala.reflect.runtime.universe.TermName
import scala.reflect.runtime.universe.TermSymbol

/** utilities for reflecting on a type */
private[emblem] abstract class TypeReflector[A : TypeKey] {

  /** the type key */
  protected val key = implicitly[TypeKey[A]]

  /** the type */
  protected val tpe = key.tpe

  /** the type tag */
  protected implicit val tag = key.tag

  /** the class tag */
  protected implicit val classTag = typeTagToClassTag[A]

  /** the type sybol */
  protected val symbol = tpe.typeSymbol.asClass

  /** reflections on the companion object of our type */
  protected object module {

    /** the companion object instance mirror */
    val instanceMirror = {
      val classSymbol: ClassSymbol = tpe.typeSymbol.asClass
      val moduleSymbol: ModuleSymbol = classSymbol.companion.asModule
      val moduleMirror: ModuleMirror = tag.mirror.reflectModule(moduleSymbol)
      val instance: Any = moduleMirror.instance
      tag.mirror.reflect(instance)
    }

    /** the companion object type signature */
    val typeSignature = instanceMirror.symbol.typeSignature

    /** a mirror for the companion object apply method */
    val applyMirror = {
      val applyMethod = typeSignature.member(TermName("apply")).asMethod
      instanceMirror.reflectMethod(applyMethod)
    }

  }

  /** all the public val members of the type */
  protected def publicVals: Seq[TermSymbol] = {
    def publicTerm(s: Symbol) = s.isTerm && s.isPublic
    def isValue(s: TermSymbol) = s.isGetter && s.isStable
    tpe.members.filter(publicTerm).map(_.asTerm).filter(isValue).toSeq
  }

  /** all the abstract public val members of the type */
  protected def abstractPublicVals: Seq[TermSymbol] = publicVals.filter(_.isAbstract)

  /** a function that acts like a getter for the supplied term name */
  protected def getFunction[U : TypeKey](name: TermName): (A) => U = {
    val getter = tpe.decl(name).asMethod
    val getFunction = { a: A =>
      val instanceMirror = tag.mirror.reflect(a)
      val methodMirror = instanceMirror.reflectMethod(getter)
      methodMirror().asInstanceOf[U]
    }
    getFunction
  }

  /** throw exception if the type is not a case class */
  protected def verifyIsCaseClass(): Unit = {
    if (!symbol.isClass || !symbol.asClass.isCaseClass) {
      throw new TypeIsNotCaseClassException(key)
    }
  }

  /** throw exception if the type is an inner class */
  protected def verifyIsNotInnerClass(): Unit = {
    if (!symbol.isStatic) {
      throw new CaseClassIsInnerClassException(key)
    }
  }

}
