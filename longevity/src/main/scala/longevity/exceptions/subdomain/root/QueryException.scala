package longevity.exceptions.subdomain.root

import longevity.exceptions.subdomain.SubdomainException

/** an exception that occurred while using the Query API */
class QueryException(message: String) extends SubdomainException(message)
