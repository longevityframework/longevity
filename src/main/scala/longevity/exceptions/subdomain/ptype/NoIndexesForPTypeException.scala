package longevity.exceptions.subdomain.ptype

// TODO: name the type in this and equivalent keys exception

/** an exception thrown when [[PType persistent type]] neither overrides
 * `indexSet` nor defines an inner object `indexes`
 */
class NoIndexesForPTypeException extends PTypeException(
  "a PType must either override `indexSet`, or define an inner object `indexes`")
