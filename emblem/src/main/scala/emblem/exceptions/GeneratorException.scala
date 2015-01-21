package emblem.exceptions

import emblem.TypeKey

/** an exception indicating you broke the contract of [[emblem.emblemFor]] or [[emblem.shorthandFor]] */
class GeneratorException(val key: TypeKey[_], message: String) extends Exception(message)
