package longevity.repo

import longevity.domain._
import com.mongodb.casbah.Imports._
import emblem._
import emblem.traversors.Visitor
import emblem.traversors.Visitor.emptyCustomVisitors
import emblem.traversors.Visitor.CustomVisitors

/** TODO scaladoc */
private[repo] class EntityToCasbah {

  def generate[E <: Entity](e: E): MongoDBObject = {
    ???
  }

  private val entityVisitor = new Visitor {

    override protected val emblemPool: EmblemPool = EmblemPool()

    override protected val shorthandPool: ShorthandPool = ShorthandPool()

    override protected val customVisitors: CustomVisitors = emptyCustomVisitors

    override protected def visitBoolean(input: Boolean): Unit = {}

    override protected def visitChar(input: Char): Unit = {}

    override protected def visitDouble(input: Double): Unit = {}

    override protected def visitFloat(input: Float): Unit = {}

    override protected def visitInt(input: Int): Unit = {}

    override protected def visitLong(input: Long): Unit = {}

    override protected def visitString(input: String): Unit = {}

  }

}
