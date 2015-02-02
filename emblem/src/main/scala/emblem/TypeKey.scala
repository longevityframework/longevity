package emblem

import scala.reflect.runtime.universe._
import emblem.reflectionUtil.makeTypeTag

/** behaves much like a `scala.reflect.runtime.universe.TypeTag`, except that it can also be safely used
 * as a key in a hash or a set. Two type keys will be equal if and only if their underlying types are equivalent
 * according to method `=:=` in `scala.reflect.api.Types.Type`. The [[hashCode]] method does its best to
 * produce unique hash values, and always produces values compatible with [[equals]].
 *
 * Type keys are provided by an implicit method in [[emblem package emblem]], so you can get one implicitly
 * like so:
 *
 * {{{
 * def foo[A : TypeKey]() = {
 *   val key = implicitly[TypeKey[A]]
 * }
 * }}}
 *
 * Or you can get one explicitly like so:
 *
 * {{{
 * val key = emblem.typeKey[List[String]]
 * }}}
 *
 * Or if you already have a `TypeTag` at hand:
 *
 * {{{
 * val tag: TypeTag[A] = ???
 * val key = TypeKey(tag)
 * }}}
 *
 * @tparam A the type that we are keying one
 * @param tag the scala-reflect `TypeTag` for type `A`
 */
case class TypeKey[A](val tag: TypeTag[A]) {

  /** The scala-reflect `Type` for type `A` */
  def tpe: Type = tag.tpe

  lazy val typeArgs: List[TypeKey[_]] = tpe.typeArgs map { tpe => TypeKey(makeTypeTag(tpe)) }

  override def equals(that: Any): Boolean = that.isInstanceOf[AnyRef] && {
    (this eq that.asInstanceOf[AnyRef]) || {
      that.isInstanceOf[TypeKey[_]] && {
        val thatTypeKey = that.asInstanceOf[TypeKey[_]]
        if (this.tag eq thatTypeKey.tag) true

        // this line is both a tentative performance optimization and a sanity check for me. if it turns out
        // hashCode is reporting different values for what should be the same type, then this will show up as
        // the type keys being unequal, rather than as erratic set/map behavior.
        else if (this.hashCode != that.hashCode) false

        else this.tpe =:= thatTypeKey.tpe
      }
    }
  }

  // TODO: this will map types such as List[Int] and List[String] to the same hash value. include considerations
  // for the type arguments to account for this
  override lazy val hashCode = {
    def symbolToString(s: Symbol):String = {
      val fullName = s.fullName
      fullName.substring(fullName.lastIndexWhere(c => c == '.' || c == '$') + 1)
    }
    val declNames = tag.tpe.decls.map(symbolToString _).toSet
    declNames.hashCode
  }

  override def toString = s"TypeKey($tpe)"

}
