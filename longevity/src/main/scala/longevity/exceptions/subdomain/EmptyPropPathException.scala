package longevity.exceptions.subdomain

class EmptyPropPathException(cause: emblem.exceptions.EmptyPropPathException)
extends InvalidPropPathException("empty prop path", cause)

