package longevity.exceptions.subdomain.root

import longevity.exceptions.subdomain.SubdomainException

/** an exception involving an attempt to create a key after
 * [[longevity.subdomain.Subdomain]] initilization
 */
class LateKeyDefException extends SubdomainException(
  "cannot create new keys after the subdomain has been initialized")
