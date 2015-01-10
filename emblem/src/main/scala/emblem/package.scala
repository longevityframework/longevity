import scala.reflect.runtime.universe.TypeTag

import scala.language.implicitConversions

/** TODO package level scaladoc */
package object emblem {

  def typeKey[A : TypeKey]: TypeKey[A] = implicitly[TypeKey[A]]

  implicit def typeKeyFromTag[A : TypeTag]: TypeKey[A] = TypeKey(implicitly[TypeTag[A]])

}
