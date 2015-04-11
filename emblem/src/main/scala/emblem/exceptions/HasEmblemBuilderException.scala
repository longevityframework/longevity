package emblem.exceptions

/** an exception indicating a failure to build a [[HasEmblem]] with a [[HasEmblemBuilder]] */
abstract class HasEmblemBuilderException(message: String) extends EmblemException(message)
