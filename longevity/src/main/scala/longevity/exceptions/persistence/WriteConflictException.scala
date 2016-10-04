package longevity.exceptions.persistence

import longevity.persistence.PState
import longevity.subdomain.Persistent

/** an exception thrown due to optimistic locking detection of a conflicting write */
class WriteConflictException[P <: Persistent](val state: PState[P])
extends PersistenceException(
  s"attempt to persist ${state.get} was aborted due to a conflicting write from another transaction")
