package emblem.traversors

import emblem.TypeKey

/** TODO scaladoc
 */
trait CustomVisitor[A] {

  /** Visits an element of type B
   * @tparam B the type of element to generate. a subtype of A
   * @param visitor the [[Visitor]] that is delegating this call to us
   * @param input the element to visit
   */
  def apply[B <: A : TypeKey](visitor: Visitor, input: B): Unit

}
