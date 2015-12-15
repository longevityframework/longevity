package longevity.exceptions.subdomain.root

import longevity.subdomain.Root
import longevity.subdomain.root.Key

/** an exception indicating an attempt to create a key value using the wrong number of property values
 *
 * @param key the key for which a value was attempting to be built
 * @param numProps the number of properties the key has
 * @param numPropVals the number of property values used to try to create the key value
 */
class NumPropValsException[R <: Root](key: Key[R], numProps: Int, numPropVals: Int)
extends KeyValException(
  s"key $key has $numProps properties, but you attempted to build a key value with $numPropVals values")
