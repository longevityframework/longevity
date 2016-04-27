package emblem.exceptions

/** an exception thrown when the user attempts to build an [[EmblematicPropPath]] with an empty path */
class EmptyPropPathException extends EmblematicPropPathException("cannot construct an empty prop path")
