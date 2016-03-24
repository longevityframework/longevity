package longevity.exceptions.subdomain.ptype

/** an exception thrown when [[PType persistent type]] neither overrides `keySet`, nor defines
 * an inner object `keys`
 */
class NoKeysForPTypeException extends PTypeException(
  "a PType must either override `keySet`, or define an inner object `keys`")
