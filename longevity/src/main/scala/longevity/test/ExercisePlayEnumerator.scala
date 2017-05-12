package longevity.test

import longevity.model.query.Query
import streamadapter.play.playEnumeratorToChunkerator

/** extends [[QuerySpec]] to add tests for 
 * [[longevity.persistence.streams.PlayRepo.queryToPlay PlayRepo.queryToPlay]]
 * 
 * @tparam M the model
 *
 * @tparam P the persistent type
 */
trait ExercisePlayEnumerator[M, P] extends QuerySpec[M, P] {

  override protected def exerciseStreams(query: Query[P], expected: Set[P]): Unit = {
    super.exerciseStreams(query, expected)
    exercisePlayEnumerator(query, expected)
  }

  private def exercisePlayEnumerator(query: Query[P], expected: Set[P]): Unit = {
    val source = repo.queryToPlay(query)
    val results = playEnumeratorToChunkerator.adapt(source).toVector.map(_.get).toSet
    val actual = pStates.map(_.get).toSet intersect results
    exerciseStream(query, actual, expected)
  }

}
