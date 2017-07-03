package longevity

/** contains the [[Effect]] type class and instances */
package object effect {

  /** a blocking effect. this effect will evaluate immediately and block until the result is
   * available. this is essentially an `Id` type; `Blocking[A]` is equivalent to `A`.
   */
  type Blocking[A] = A

}
