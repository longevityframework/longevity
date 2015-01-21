package emblem.exceptions

// TODO revise scaladoc
/** an exception thrown when looking up a value for a property that is not in the map */
class RequiredPropertyNotSetException(val propName: String)
extends Exception(s"required propery $propName was not set")
