package longevity.domain

import scala.language.implicitConversions

object Assoc {

  /** wraps an entity in a SimpleAssoc when needed */
  implicit def apply[E](e: E): Assoc[E] = SimpleAssoc(e)
}

/** an association between two domain entities */
trait Assoc[E]

// TODO rename to AssocWithUnpersisted 
case class SimpleAssoc[E](e: E) extends Assoc[E]

