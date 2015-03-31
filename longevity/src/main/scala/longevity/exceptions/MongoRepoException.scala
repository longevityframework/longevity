package longevity.exceptions

import emblem.TypeKey
import emblem.exceptions.CouldNotTraverseException

/** an exception thrown by a [[longevity.persistence.MongoRepo MongoRepo]] */
class MongoRepoException(message: String, cause: Exception)
extends LongevityException(message, cause)
