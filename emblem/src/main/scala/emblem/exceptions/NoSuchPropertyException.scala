package emblem.exceptions

// TODO: rework super here
// TODO: scaladoc

class NoSuchPropertyException(reflectiveName: String, propName: String)
extends EmblemException(s"no such property '$propName' in $reflectiveName")
