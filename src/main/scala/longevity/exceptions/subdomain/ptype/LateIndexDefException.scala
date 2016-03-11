package longevity.exceptions.subdomain.ptype

import longevity.exceptions.subdomain.SubdomainException

/** an exception involving an attempt to create an index after
 * [[longevity.subdomain.Subdomain]] initilization
 */
class LateIndexDefException extends SubdomainException(
  "cannot create new indexes after the subdomain has been initialized")
