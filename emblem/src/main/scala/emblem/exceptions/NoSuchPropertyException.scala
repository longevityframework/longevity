package emblem.exceptions

import emblem.Emblem
import emblem.HasEmblem

class NoSuchPropertyException(val emblem: Emblem[_ <: HasEmblem], val propName: String)
extends EmblemException(s"no such property '$propName' in emblem $emblem")
