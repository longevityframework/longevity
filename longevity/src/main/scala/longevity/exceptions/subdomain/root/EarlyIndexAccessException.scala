package longevity.exceptions.subdomain.root

import longevity.exceptions.subdomain.SubdomainException

/** an exception involving an attempt to access an index before
 * [[longevity.subdomain.Subdomain]] initilization
 */
class EarlyIndexAccessException extends SubdomainException(
  "cannot access RootEntityType.indexes until after the subdomain has been initialized")
