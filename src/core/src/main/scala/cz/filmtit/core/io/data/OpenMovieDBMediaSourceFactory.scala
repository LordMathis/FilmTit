/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.filmtit.core.io.data

import _root_.java.util
import cz.filmtit.core.model.MediaSourceFactory
import java.net.{ UnknownHostException, URLEncoder }
import io.Source
import org.apache.commons.logging.LogFactory
import org.json.{ JSONArray, JSONObject }
import cz.filmtit.share.MediaSource
import java.util.ArrayList
import collection.mutable.ListBuffer

class OpenMovieDBMediaSourceFactory extends MediaSourceFactory {
  val url = "http://www.omdbapi.com/?";
  val logger = LogFactory.getLog(this.getClass.getSimpleName)

  def getSuggestion(title: String, year: String): MediaSource = getSuggestions(title, year).get(0)

  def getSuggestions(title: String, year: String): java.util.List[MediaSource] = {
    val suggestions = getSuggestions(title)
    val filtered = new ArrayList[MediaSource]()
    for (i <- 0 to suggestions.size()) {
      if (suggestions.get(i).getYear() == year) {
        filtered.add(suggestions.get(i))
      }
    }
    
    filtered
  }

  def getSuggestions(title: String): java.util.List[MediaSource] = {
    val apiResponse = try {

      val params = URLEncoder.encode(title, "utf-8")

      new JSONObject(
        Source.fromURL(
        url + "s=" + params
      ).mkString
      )

    } catch {
      case e: UnknownHostException => {
        logger.warn(url + "?\n" + e + "\nCould not reach Open Movie Database server.")
        return new ArrayList[MediaSource]()
      }
    }
    
    val suggestions: JSONArray = try {
      apiResponse.getJSONArray("Search")
    } catch {
      case e: org.json.JSONException => new JSONArray()
    }
    
    val mediaSources = new ArrayList[MediaSource]()

    for (i <- 0 to suggestions.length() - 1) {
      val source = suggestions.getJSONObject(i)
      val mediaSource = new MediaSource(source.getString("Title"), source.getString("Year"))
      mediaSource.setThumbnailURL(source.getString("Poster"))

      mediaSources.add(mediaSource)

    }
    mediaSources
  }
}
