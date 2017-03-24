package longevity.test

import longevity.model.query.Query
import streamadapter.play.playEnumeratorToChunkerator

/** extends [[QuerySpec]] to add tests for 
 * [[longevity.persistence.PlayRepo.queryToPlay PlayRepo.queryToPlay]]
 */
trait ExercisePlayEnumerator[P] extends QuerySpec[P] {

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
