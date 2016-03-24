package longevity.subdomain

import emblem.basicTypes.isBasicType
import emblem.imports._
import longevity.exceptions.subdomain.ptype.NoIndexesForPTypeException
import longevity.exceptions.subdomain.ptype.NoKeysForPTypeException
import longevity.subdomain.ptype._

// TODO: packages persistent and ptype
// TODO: unit tests s/root/ptype/

/** a type class for a domain entity that is stored in a persistent collection */
abstract class PType[
  P <: Persistent](
  implicit private val pTypeKey: TypeKey[P],
  implicit private val shorthandPool: ShorthandPool = ShorthandPool.empty)
extends EntityType[P] {

  // TODO: should these scans recurse into child inner objects?

  /** the keys for this persistent type */
  lazy val keySet: Set[Key[P]] = kscan("keys")

  /** the indexes for this persistent type */
  lazy val indexSet: Set[Index[P]] = iscan("indexes")

  /** constructs a [[longevity.subdomain.ptype.Prop Prop]] from a path
   * 
   * @throws longevity.exceptions.subdomain.ptype.PropException if any step along
   * the path does not exist, or any non-final step along the path is not an
   * entity, or the final step along the path is not a [[Shorthand]], an
   * [[Assoc]] or a basic type.
   * 
   * @see `emblem.basicTypes`
   */
  def prop[A : TypeKey](path: String): Prop[P, A] = Prop(path, emblem, entityTypeKey, shorthandPool)

  /** constructs a key for this persistent type based on the supplied set of key props
   * 
   * @param propsHead the first of the properties that define this key
   * @param propsTail any remaining properties that define this key
   */
  def key(propsHead: Prop[P, _], propsTail: Prop[P, _]*): Key[P] =
    Key(propsHead :: propsTail.toList)

  /** constructs an index for this persistent type based on the supplied set of index props
   * 
   * @param propsHead the first of the properties that define this index
   * @param propsTail any remaining properties that define this index
   */
  def index(propsHead: Prop[P, _], propsTail: Prop[P, _]*): Index[P] =
    Index(propsHead :: propsTail.toList)

  /** contains implicit imports to make the query DSL work */
  lazy val queryDsl = new QueryDsl[P]

  private def kscan(containerName: String): Set[Key[P]] = {
    val keys: Any = innerModule(this, "keys").getOrElse {
      throw new NoKeysForPTypeException
    }
    implicit val tag = pTypeKey.tag
    termsWithType[Key[P]](keys)
  }

  private def iscan(containerName: String): Set[Index[P]] = {
    val indexes: Any = innerModule(this, "indexes").getOrElse {
      throw new NoIndexesForPTypeException
    }
    implicit val tag = pTypeKey.tag
    termsWithType[Index[P]](indexes)
  }

  // TODO: these go in a reflectionUtil

  def innerModule(container: Any, moduleName: String): Option[Any] = {
    import scala.ScalaReflectionException
    import scala.reflect.runtime.currentMirror
    import scala.reflect.runtime.universe.ModuleMirror
    import scala.reflect.runtime.universe.ModuleSymbol
    import scala.reflect.runtime.universe.InstanceMirror
    import scala.reflect.runtime.universe.Symbol
    import scala.reflect.runtime.universe.TermName
    val instanceMirror: InstanceMirror = currentMirror.reflect(container)
    if (instanceMirror.symbol.isStatic) {
      try {
        val symbol: ModuleSymbol = currentMirror.staticModule(s"${instanceMirror.symbol.fullName}.$moduleName")
        val mirror: ModuleMirror = currentMirror.reflectModule(symbol)
        Some(mirror.instance)
      } catch {
        case e: ScalaReflectionException => None
      }
    } else {
      val symbol: Symbol = instanceMirror.symbol.selfType.decl(TermName(s"$moduleName$$"))
      if (!symbol.isModule) {
        None
      }
      else {
        val mirror: ModuleMirror = instanceMirror.reflectModule(symbol.asModule)
        Some(mirror.instance)
      }
    }
  }

  private def termsWithType[A : TypeKey](instance: Any): Set[A] = {
    import scala.reflect.runtime.currentMirror
    import scala.reflect.runtime.universe.typeTag
    import scala.reflect.runtime.universe.InstanceMirror
    import scala.reflect.runtime.universe.Symbol
    import scala.reflect.runtime.universe.TermName
    import scala.reflect.runtime.universe.TermSymbol
    import scala.reflect.runtime.universe.Type
    val instanceMirror: InstanceMirror = currentMirror.reflect(instance)
    val symbols: Set[Symbol] = instanceMirror.symbol.selfType.decls.toSet
    val termSymbols: Set[TermSymbol] = symbols.collect {
      case s if s.isTerm => s.asTerm
    }
    val valOrVarSymbols: Set[TermSymbol] = termSymbols.filter {
      s => s.isVal || s.isVar
    }
    val matchingSymbols: Set[TermSymbol] = valOrVarSymbols.filter { symbol =>
      val tpe: Type = symbol.typeSignature
      tpe <:< typeKey[A].tpe
    }
    matchingSymbols.map { symbol =>
      val fieldMirror = instanceMirror.reflectField(symbol)
      fieldMirror.get.asInstanceOf[A]
    }
  }

}
