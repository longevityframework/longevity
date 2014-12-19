package longevity.domain

import scala.language.implicitConversions

object Assoc {

  /** wraps an entity in a AssocWithUnpersisted when needed */
  implicit def apply[E](e: E): Assoc[E] = AssocWithUnpersisted(e)
}

/** an association between two domain entities */
trait Assoc[E]

// TODO rename to AssocWithUnpersisted 
case class AssocWithUnpersisted[E](unpersisted: E) extends Assoc[E]

