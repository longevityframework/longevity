package longevity.exceptions.subdomain

import longevity.subdomain.root.Prop

/** an exception that occurs when a user attempts to use an ordered query operator with an unordered prop */
class PropNotOrderedException(prop: Prop[_, _])
extends QueryException(s"attempt to use unordered prop $prop with an ordered query operator")
