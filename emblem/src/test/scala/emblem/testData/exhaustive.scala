package emblem.testData

//import emblem.Emblem
//import emblem.HasEmblem
import com.github.nscala_time.time.Imports.richDateTime
import emblem.EmblemPool
import emblem.Emblematic
import emblem.Extractor
import emblem.ExtractorPool
import org.joda.time.DateTime

/** an attempt at an exhaustive set of data to cover emblematic traversal logic */
object exhaustive {

  case class Email(email: String)
  case class Markdown(markdown: String)
  case class Uri(uri: String)

  val emailExtractor = Extractor[Email, String]
  val markdownExtractor = Extractor[Markdown, String]
  val uriExtractor = Extractor[Uri, String]

  lazy val extractorPool = ExtractorPool(emailExtractor, markdownExtractor, uriExtractor)
  lazy val emblemPool = EmblemPool()
  lazy val emblematic = Emblematic(extractorPool, emblemPool)

  object basics {
    val boolean: Boolean = true
    val char: Char = '*'
    val dateTime: DateTime = DateTime.now
    val double: Double = 0.234d
    val float: Float = 0.2341234f
    val int: Int = 876324
    val long: Long = -187346
    val string: String = "asdjhfbsvy9248ywjfsdczada;;;;"
  }

  object extractors {
    val email = Email("f@g.z")
    val markdown = Markdown("sfgsfdg")
    val uri = Uri("httpcolonslashslash")
  }

}
