package emblem

import scala.reflect.runtime.universe.TypeTag
import stringUtil._

class Emblem[T <: HasEmblem : TypeTag](
  val namePrefix: String,
  val name: String,
  val props: Seq[EmblemProp[T, _]]
) {

  lazy val typeTag: TypeTag[T] = scala.reflect.runtime.universe.typeTag[T]
  lazy val fullname = s"$namePrefix.$name"

  val propMap: Map[String, EmblemProp[T, _]] = props.view.map(prop => prop.name -> prop).toMap

  def apply[U](name: String) = propMap(name).asInstanceOf[EmblemProp[T, U]]

  override def toString = fullname

  def debugInfo = {
    val builder = new StringBuilder()
    builder ++= s"$fullname {\n"
    props.foreach {
      prop => builder ++= s"  ${prop}\n"
    }
    builder ++= s"}"
    builder.toString
  }

}
