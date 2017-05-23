package longevity.model

/** evidence for a domain model.
 *
 * the model class `M` is intended to be a phantom class, available in the root package of the
 * package structure where the domain model elements are defined. the `ModelEv` is intended to be
 * implicitly available within the domain model's companion object, but *private to that package*.
 * this comes for free if you use the `longevity.model.annotations.domainModel` annotation on your
 * model class `M`. the model evidence should be package private to prevent the introduction of any
 * persistent classes that are not discoverable by the `ModelType`, whose default behavior is to find
 * persistent classes in the domain model's package and sub-packages.
 *
 * @tparam M the model
 *
 * @see longevity.model.annotations.domainModel
 * @see longevity.model.ModelType
 */
class ModelEv[M]
