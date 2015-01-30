package longevity.exceptions

import emblem.TypeKey

/** an exception thrown when [[TestDataGenerator]] cannot generate requested data due to encountering an
 * unsupported type. */
class CouldNotGenerateException(val typeKey: TypeKey[_])
extends Exception(s"don't know how to generate test data for type $typeKey")