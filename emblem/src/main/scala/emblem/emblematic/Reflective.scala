package emblem.emblematic

import emblem.TypeKey
import emblem.exceptions.NoSuchPropertyException
import emblem.stringUtil

/** a prototype for [[Emblem]] and [[Union]] */
trait Reflective[A] {

  /** the kind of reflective property this reflective has. either an
   * [[EmblemProp]] or a [[UnionProp]]
   */
  type PropType[B, C] <: ReflectiveProp[B, C]

  /** a [[TypeKey type key]] for the type that we reflect upon */
  val typeKey: TypeKey[A]

  private val tpe = typeKey.tpe

  /** a dot-separated identifier of the enclosing scope of the type */
  val namePrefix: String = stringUtil.typeNamePrefix(tpe)

  /** the unqualified type name */
  val name: String = stringUtil.typeName(tpe)

  /** the fully qualified type name */
  val fullname: String = stringUtil.typeFullname(tpe)

  /** the [[ReflectiveProp reflective properties */
  val props: Seq[PropType[A, _]]

  /** a map of the [[props]], keyed by name */
  val propMap: Map[String, PropType[A, _]] = props.view.map(prop => prop.name -> prop).toMap

  /** retrieves a [[ReflectiveProp reflective property]] by name */
  def apply(name: String): PropType[A, _] =
    propMap.getOrElse(name, throw new NoSuchPropertyException(this.toString, name))

  /** retrieves the reflective property with the specified property type by name */
  def prop[B : TypeKey](name: String): PropType[A, B] = {
    val typeKey = implicitly[TypeKey[B]]
    val prop = apply(name)
    if (typeKey != prop.typeKey) {
      throw new ClassCastException(
        s"requested property $name with type ${typeKey.tpe}, but this property has type ${prop.typeKey.tpe}")
    }
    prop.asInstanceOf[PropType[A, B]]
  }

  /** a string describing the reflective in full detail */
  lazy val debugInfo = {
    val builder = new StringBuilder()
    builder ++= s"$fullname {\n"
    props.foreach {
      prop => builder ++= s"  $prop\n"
    }
    builder ++= s"}"
    builder.toString
  }

  override def toString = fullname

}
