package cz.filmtit.core.model.data

import collection.mutable.ListBuffer
import cz.filmtit.core.model.annotation.ChunkAnnotation
import cz.filmtit.share.Chunk


/**
 * Wrapper class for chunks in the parallel data. In the most basic case,
 * a chunk only consists of the the surface form in a particular language.
 * Chunks can also have annotations, e.g. Named Entities.
 *
 * @author Joachim Daiber
 */

class AnnotatedChunk(val surfaceform: String) extends Chunk(surfaceform) {

  /**
   * Annotations are stored in a list but are only instantiated
   * if they are required (lazy).
   */
  lazy val annotations = ListBuffer[Triple[ChunkAnnotation, Int, Int]]()

  /**
   * Return the Chunk with its annotations formatted by the function
   * #format. If no function is specified, annotations are returned in
   * the following format:
   *  "Peter is from London." => "<Person> is from <Place>."
   *
   * @param format function to output a single annotation in the string
   * @return
   */
  def toAnnotatedString(
    format: (ChunkAnnotation, String) => String = { (t, _) => "<" + t + ">" }
  ): String = {

    var lastOffset = 0
    (this.annotations.toList map {
      triple => {
        val (annotation, from, to) = triple
        "%s%s".format(
        surfaceform.substring(lastOffset, from),
        format(annotation,
          surfaceform.substring(from, math.min(surfaceform.size, to))), {
          lastOffset = to
        }
        )
      }
    }).mkString + surfaceform.substring(math.min(surfaceform.size, lastOffset))

  }


  /**
   * Add an annotation to the Chunk. Annotations must be
   * of type ChunkAnnotation.
   *
   * @param annotation the annotation to be added
   * @param from begin index (inclusive)
   * @param to end index (exclusive)
   */
  def addAnnotation(annotation: ChunkAnnotation, from: Int, to: Int) {
    annotations += ((annotation, from, to))
  }

  override def toString = this.toAnnotatedString( (_, s) => "[" + s + "]" )
  override def hashCode = this.surfaceform.hashCode()
  override def equals(other: Any) = this.surfaceform.equals(other.toString)

}

object AnnotatedChunk {
  implicit def fromChunk(chunk: Chunk) = new AnnotatedChunk(chunk.getSurfaceForm)
  implicit def fromString(chunk: String) = new AnnotatedChunk(chunk)
}