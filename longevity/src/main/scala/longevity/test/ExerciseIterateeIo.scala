package longevity.test

import cats.Eval
import longevity.model.query.Query
import streamadapter.iterateeio.iterateeIoEnumeratorToChunkerator

/** extends [[QuerySpec]] to add tests for 
 * [[longevity.persistence.streams.IterateeIoRepo.queryToIterateeIo IterateeIoRepo.queryToIterateeIo]]
 */
trait ExerciseIterateeIo[P] extends QuerySpec[P] {

  override protected def exerciseStreams(query: Query[P], expected: Set[P]): Unit = {
    super.exerciseStreams(query, expected)
    exerciseIterateeIo(query, expected)
  }

  private def exerciseIterateeIo(query: Query[P], expected: Set[P]): Unit = {
    val source = repo.queryToIterateeIo[P, Eval](query)
    val results = iterateeIoEnumeratorToChunkerator[Eval].adapt(source).toVector.map(_.get).toSet
    val actual = pStates.map(_.get).toSet intersect results
    exerciseStream(query, actual, expected)
  }

}
