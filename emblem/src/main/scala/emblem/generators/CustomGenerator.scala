package emblem.generators

import scala.reflect.runtime.universe.typeOf
import emblem._
import emblem.reflectionUtil.makeTypeTag
import emblem.exceptions.CouldNotGenerateException
import TestDataGenerator._

object CustomGenerator {

  // TODO: you could make a CustomGenerator take a lower type bound as well, and then you could make
  // simpleGenerator type-tight. what you want to do is:
  // - replace single TP to CustomGenerator with 2: UpperB and LowerB
  // - change TestDataGenerator.CustomGenerators to map by both key bounds
  //   type CustomGenerators = TypeKeyMap[Any, CustomGenerator]
  // - in TestDataGenerator.customOption, and add in a castToUpperBound in this line:
  //   val keyOpt: Option[TypeKey[_ >: A]] = customGenerators.keys.map(_.castToLowerBound[A]).flatten.headOption
  // - simpleGenerator provides same type as upper and lower bound


  /** Creates a simple [[CustomGenerator]] from a regular function. The simple generator wraps the function
   * in a type-check, to make sure the type requested is equivalent to the return type of the underlying
   * function. Example usage:
   *
   * {{{
   * class IntHolder(val i: Int)
   * val intHolderGen: CustomGenerator[IntHolder] =
   *   simpleGenerator((generator: TestDataGenerator) => new IntHolder(generator.int))
   * val generator = new TestDataGenerator(customGenerators = emptyCustomGenerators + intHolderGen)
   * }}}
   * 
   * @tparam A the return type of the underlying function
   * @param underlying the regular function backing the custom generator
   * @return a simple custom generator backed by the provided underlying function
   */
  def simpleGenerator[A : TypeKey](underlying: (TestDataGenerator) => A) = new CustomGenerator[A] {
    def apply[B <: A : TypeKey](generator: TestDataGenerator): B = {
      if (typeKey[A].tpe <:< typeKey[B].tpe) // A and B are the same
        underlying(generator).asInstanceOf[B]
      else
        throw new UnsupportedOperationException
    }
  }

}

/** A custom generator for things of type A. The apply method takes a type parameter that is tighter than
 * A, and acquires a [[TypeKey]] for that type, so it can customize its behavior based on the type requested.
 *
 * The apply method also takes a [[TestDataGenerator]] as argument, so that it can call back into to the calling
 * TestDataGenerator to generate complex values.
 *
 * Example usage:
 *
 * {{{
 * // always generates a 5-element list
 * val listCustomGenerator = new CustomGenerator[List[Any]] {
 *   def apply[B <: List[_] : TypeKey](generator: TestDataGenerator): B = {
 *     val eltTypeKey = typeKey[B].typeArgs.head
 *     val eltList = List.fill(5) { generator.any(eltTypeKey) }
 *     eltList.asInstanceOf[B]
 *   }
 * }
 * val generator = new TestDataGenerator(customGenerators = emptyCustomGenerators + listCustomGenerator)
 * }}}
 *
 * @tparam the type of things this custom generator generates
 */
trait CustomGenerator[A] {

  /** Generates an element of type B
   * @tparam B the type of element to generate. a subtype of A
   * @param generator the [[TestDataGenerator]] that is delegating this call to us
   */
  def apply[B <: A : TypeKey](generator: TestDataGenerator): B

}
