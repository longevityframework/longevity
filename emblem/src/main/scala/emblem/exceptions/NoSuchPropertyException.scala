package emblem.exceptions

import emblem.Emblem


class NoSuchPropertyException(val emblem: Emblem[_], val propName: String)
extends EmblemException(s"no such property '$propName' in emblem $emblem")
