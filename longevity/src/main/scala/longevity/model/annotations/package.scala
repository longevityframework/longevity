package longevity.model

import scala.language.experimental.macros

/** macro annotations for defining your domain model */
package object annotations {

  /** gathers all the `A`s in the same package as the macro was called into a list.
   *
   * this def macro is used by the `@domainModel` annotation macro to gather all the `PTypes` and
   * `CTypes` together to construct the `ModelType`. it is a little out of place in a package named
   * `longevity.model.annotations`, but it does not seem appropriate to create a new project or
   * package to contain a single def macro.
   * 
   * @see domainModel
   * @see longevity,model.PType
   * @see longevity,model.CType
   * @see longevity,model.ModelType
   */
  def packscanToList[A]: List[A] = macro PackscanToList.packscanToList[A]

}
