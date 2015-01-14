package emblem

class EmblemPropToValueMap[T <: HasEmblem] private(
  private val map: Map[EmblemProp[T, _], Any] = Map[EmblemProp[T, _], Any]()) {

  @throws[EmblemPropToValueMap.NoValueForEmblemProp]("when there is no value set for the supplied prop")
  def get[U](prop: EmblemProp[T, U]): U = map.get(prop) match {
    case Some(u) => u.asInstanceOf[U]
    case None => throw new EmblemPropToValueMap.NoValueForEmblemProp(prop, this)
  }

  def +[U](pair: (EmblemProp[T, U], U)): EmblemPropToValueMap[T] =
    new EmblemPropToValueMap(map + pair)
  
}

object EmblemPropToValueMap {

  def apply[T <: HasEmblem](): EmblemPropToValueMap[T] = new EmblemPropToValueMap[T]()

  class NoValueForEmblemProp(val prop: EmblemProp[_, _], val map: EmblemPropToValueMap[_])
  extends Exception(s"no value for prop $prop in map")

}
