package cz.filmtit.core.io.data

import org.json.JSONObject
import io.Source
import java.net.URLEncoder
import collection.mutable.ListBuffer


/**
 * Utilities for retrieving movie/TV show information from IMDB.
 *
 * @author Joachim Daiber
 */

object IMDB {

  def queryFirstBest(title: String, year: String): JSONObject = {
    new JSONObject(queryAll(title, year).next())
  }

  def queryNBest(title: String, year: String, n: Int = 10): List[JSONObject] = {
    val nbest = ListBuffer[JSONObject]()
    val all = queryAll(title, year)

    var i = 0
    while( all.hasNext && i < n )
      nbest += new JSONObject(all.next())

    nbest.toList
  }

  def queryAll(title: String, year: String): Iterator[String] = {
    val tvShowPattern = "\"(.+)\" .+".r

    val response = title match {
      case tvShowPattern(titleShow) => {
        Source.fromURL("http://www.imdbapi.com/?t=%s".format(
          URLEncoder.encode(titleShow, "utf-8"))).getLines()
      }
      case _ => {
        Source.fromURL("http://www.imdbapi.com/?t=%s&y=%s".format(
          URLEncoder.encode(title, "utf-8"), year)).getLines()
      }
    }
    response
  }

}
