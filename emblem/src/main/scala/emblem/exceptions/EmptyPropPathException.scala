package emblem.exceptions

/** an exception thrown when the user attempts to build an [[EmblemPropPath]] with an empty path */
class EmptyPropPathException extends EmblemPropPathException("cannot construct an empty prop path")
