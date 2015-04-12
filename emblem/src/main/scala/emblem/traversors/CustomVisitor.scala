package emblem.traversors

import emblem.TypeKey

/** a custom visitor for elements of type `A`. the `apply` method takes a type parameter that is tighter than
 * `A`, and acquires a [[TypeKey]] for that type, so it can customize its behavior based on the type requested.
 *
 * the apply method also takes a [[Visitor]] as argument, so that it can call back into to the calling
 * `Visitor` to visit complex values.
 *
 * example usage:
 *
 * {{{
 * // only visit the first five elements of a list
 * val listCustomVisitor = new CustomVisitor[List[Any]] {
 *   def apply[B <: List[_] : TypeKey](visitor: Visitor, list: B): Unit = {
 *     val elementTypeKey = typeKey[B].typeArgs.head
 *     def visitFive[C : TypeKey]: Unit =
 *       list.asInstanceOf[List[C]].take(5).foreach { element => visitor.visit(element) }
 *     visitFive(elementTypeKey)
 *   }
 * }
 *
 * val visitor = new Visitor {
 *   override protected val customVisitors: CustomVisitorPool = CustomVisitorPool.empty + listCustomVisitor
 * }
 * }}}
 *
 * @tparam the type of things this custom visitor visits
 */
trait CustomVisitor[A] {

  /** visits an element of type B
   * @tparam B the type of element to visit. a subtype of A
   * @param visitor the [[Visitor]] that is delegating this call to us
   * @param input the element to visit
   */
  def apply[B <: A : TypeKey](visitor: Visitor, input: B): Unit

}
