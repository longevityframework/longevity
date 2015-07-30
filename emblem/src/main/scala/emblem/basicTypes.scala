package emblem

/** the basic types are the leaf-level types that emblem knows how to process. currently, the following
 * basic type are supported:
 *
 *   - `Boolean`
 *   - `Char`
 *   - `org.joda.time.DateTime`
 *   - `Double`
 *   - `Float`
 *   - `Int`
 *   - `Long`
 *   - `String`
 */
object basicTypes {

  /** a set of [[TypeKey type keys]] for all the basic types */
  val basicTypeKeys = Set[TypeKey[_]](
    typeKey[Boolean],
    typeKey[Char],
    typeKey[org.joda.time.DateTime],
    typeKey[Double],
    typeKey[Float],
    typeKey[Int],
    typeKey[Long],
    typeKey[String])

  /** returns `true` whenever the type `A` is a basic type
   * @tparam A the type we wish to know is basic or not
   */
  def isBasicType[A : TypeKey]: Boolean = basicTypeKeys.contains(typeKey[A])

}
