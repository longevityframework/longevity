package longevity.test

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import longevity.model.query.Query
import streamadapter.akka.akkaSourceToChunkerator

/** extends [[QuerySpec]] to add tests for 
 * [[longevity.persistence.streams.AkkaStreamsRepo.queryToAkkaStream AkkaStreamsRepo.queryToAkkaStream]]
 * 
 * @tparam F the effect
 * @tparam M the model
 * @tparam P the persistent type
 */
trait ExerciseAkkaStreams[F[_], M, P] extends QuerySpec[F, M, P] {

  override protected def exerciseStreams(query: Query[P], expected: Set[P]): Unit = {
    super.exerciseStreams(query, expected)
    exerciseAkkaStream(query, expected)
  }

  private def exerciseAkkaStream(query: Query[P], expected: Set[P]): Unit = {
    implicit val system = ActorSystem("QuerySpec")
    implicit val materializer = ActorMaterializer()
    val source = effect.run(repo.queryToAkkaStream(query))
    val results = akkaSourceToChunkerator(materializer).adapt(source).toVector.map(_.get).toSet
    val actual = pStates.map(_.get).toSet intersect results
    system.terminate
    exerciseStream(query, actual, expected)
  }

}
