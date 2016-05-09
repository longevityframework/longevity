package emblem.emblematic

import emblem.TypeKey
import emblem.TypeKeyMap
import emblem.typeKey

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

  /** `scala.math.Ordering` objects for all the basic types */
  val basicTypeOrderings = TypeKeyMap[Any, Ordering]() +
    Ordering.Boolean +
    Ordering.Char +
    com.github.nscala_time.time.OrderingImplicits.DateTimeOrdering +
    Ordering.Double +
    Ordering.Float +
    Ordering.Int +
    Ordering.Long +
    Ordering.String
  
}
