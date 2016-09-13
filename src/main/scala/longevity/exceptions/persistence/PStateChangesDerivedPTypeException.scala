package longevity.exceptions.persistence

import longevity.exceptions.UnrecoverableLongevityException

/** an exception thrown when attempting to modify a `PState` for a `PolyPType`
 * from one `DerivedPType` type to another
 */
class PStateChangesDerivedPTypeException(origTypeName: String, currTypeName: String)
extends PersistenceException(
  s"you provided a PState for a PolyPType where the derived type of the " +
  s"original Persistent ($origTypeName) is different from the derived type " +
  s"of the current Persistent ($currTypeName). we do not currently support " +
  s"modifying a PState from one derived type to another")
with UnrecoverableLongevityException
