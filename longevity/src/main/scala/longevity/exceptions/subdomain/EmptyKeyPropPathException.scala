package longevity.exceptions.subdomain

import emblem.exceptions.EmptyPropPathException

class EmptyKeyPropPathException(e: EmptyPropPathException)
extends InvalidKeyPropPathException("empty nat key prop path", e)

