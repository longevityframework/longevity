package longevity.persistence.mongo

import com.mongodb.client.MongoCursor
import com.mongodb.client.model.Filters
import longevity.persistence.PState
import longevity.model.ptype.Prop
import longevity.model.query.AndOp
import longevity.model.query.Ascending
import longevity.model.query.ConditionalFilter
import longevity.model.query.Descending
import longevity.model.query.FilterAll
import longevity.model.query.OrOp
import longevity.model.query.Query
import longevity.model.query.QueryFilter
import longevity.model.query.QueryOrderBy
import longevity.model.query.RelationalFilter
import org.bson.BsonDocument
import org.bson.BsonInt32
import org.bson.BsonString
import org.bson.BsonValue
import org.bson.conversions.Bson
import scala.collection.immutable.VectorBuilder
import streamadapter.CloseableChunkIter
import streamadapter.Chunkerator

/** implementation of MongoRepo.retrieveByQuery and MongoRepo.streamByQuery */
private[mongo] trait MongoQuery[M, P] {
  repo: MongoRepo[M, P] =>

  protected def queryToChunkerator(query: Query[P]) = {
    logger.debug(s"calling MongoRepo.queryToChunkerator: $query")
    val c = new Chunkerator[PState[P]] {
      def apply = new CloseableChunkIter[PState[P]] {
        private val cursor = queryCursor(query)
        def hasNext = cursor.hasNext
        def next = {
          var i = 0
          val builder = new VectorBuilder[PState[P]]()
          while (i < 20 && cursor.hasNext) {
            builder += bsonToState(cursor.next)
            i += 1
          }
          builder.result
        }
        def close = cursor.close
      }
    }
    logger.debug(s"done calling MongoRepo.queryToChunkerator")
    c
  }

  private def queryCursor(query: Query[P]): MongoCursor[BsonDocument] = {
    val filter         = mongoFilter(query.filter)
    val filteredCursor = mongoCollection.find(filter)
    val orderBy        = mongoOrderBy(query.orderBy)
    val orderByCursor  = orderBy.map(o => filteredCursor.sort(o)).getOrElse(filteredCursor)
    val offsetCursor   = query.offset.map(orderByCursor.skip).getOrElse(orderByCursor)
    val limitCursor    = query.limit.map(offsetCursor.limit).getOrElse(offsetCursor)
    logger.debug(
      s"calling MongoCollection.find: filter = $filter orderBy = $orderBy " +
      s"offset = ${query.offset} limit = ${query.limit}")
    limitCursor.iterator
  }

  protected def mongoFilter(filter: QueryFilter[P]): Bson = {
    filter match {
      case FilterAll() => doc("$comment", new BsonString("matching FilterAll"))
      case RelationalFilter(prop, op, value) => mongoRelationalFilter(prop, op, value)
      case ConditionalFilter(lhs, op, rhs) => op match {
        case AndOp => Filters.and(mongoFilter(lhs), mongoFilter(rhs))
        case OrOp  => Filters.or(mongoFilter(lhs), mongoFilter(rhs))
      }
    }
  }

  private def doc(key: String, value: BsonValue) = new BsonDocument(key, value)

  private def mongoOrderBy(orderBy: QueryOrderBy[P]): Option[BsonDocument] = {
    if (orderBy == QueryOrderBy.empty) None else {
      val document = new BsonDocument()
      orderBy.sortExprs.foreach { sortExpr =>
        val direction = sortExpr.direction match {
          case Ascending => 1
          case Descending => -1
        }
        val prop = sortExpr.prop
        def appendProp(prop: Prop[_, _]) = document.append(prop.path, new BsonInt32(direction))
        realizedPType.primaryKey match {
          case Some(key) if key.prop.prop == prop && !key.fullyPartitioned =>
            key.props.map(_.prop).map(appendProp)
          case _ =>
            appendProp(prop)
        }
      }
      Some(document)
    }
  }

}
