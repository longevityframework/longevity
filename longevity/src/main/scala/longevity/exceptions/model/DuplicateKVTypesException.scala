package longevity.exceptions.model

/** thrown on attempt to construct a [[longevity.model.ModelType ModelType]] with more than one
 * [[longevity.model.KVType KVType]] for a single kind of component
 */
class DuplicateKVTypesException
extends ModelTypeException("a ModelType cannot contain multiple KVTypes for the same key value")
