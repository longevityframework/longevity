package longevity.repo

import scala.language.implicitConversions

import longevity.domain.Assoc

object Id {

  implicit def assocToId[E](assoc: Assoc[E]): Id[E] = assoc.asInstanceOf[Id[E]]

}

trait Id[E] extends Assoc[E] {

  def retrieve: RetrieveResult[E]

}
