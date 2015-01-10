package emblem

import scala.reflect.runtime.universe._

/** TODO scaladoc
 */
case class TypeKey[A](val tag: TypeTag[A]) {

  def tpe: Type = tag.tpe

  override def equals(that: Any): Boolean = {
    def eee = that.isInstanceOf[AnyRef] && {
    (this eq that.asInstanceOf[AnyRef]) || {
      that.isInstanceOf[TypeKey[_]] && {
        val thatTypeKey = that.asInstanceOf[TypeKey[_]]
        if (this.tag eq thatTypeKey.tag) true

        // this line is both a tentative performance optimization and a sanity check for me. if it turns out
        // hashCode is reporting different values for what should be the same type, then this will show up as
        // the type keys being unequal, rather than as erratic set/map behavior.
        else if (this.hashCode != that.hashCode) false

        else this.tpe =:= thatTypeKey.tpe
      }
    }
  }
    if ((this.toString == that.toString) != eee)
      println(s"EQUALS? $this $that $eee")
    eee
  }

  override lazy val hashCode = {
    def symbolToString(s: Symbol):String = {
      val fullName = s.fullName
      fullName.substring(fullName.lastIndexWhere(c => c == '.' || c == '$') + 1)
    }
    val declNames = tag.tpe.decls.map(symbolToString _).toSet
    declNames.hashCode
  }

  override def toString = s"TypeKey($tpe)"

}
