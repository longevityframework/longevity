package longevity.exceptions.subdomain

import emblem.exceptions.EmptyPropPathException

class EmptyNatKeyPropPathException(e: EmptyPropPathException)
extends InvalidNatKeyPropPathException("empty nat key prop path", e)

