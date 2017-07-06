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
import java.io.File
import org.apache.commons.logging.LogFactory
import org.json.{ JSONArray, JSONObject }
import cz.filmtit.share.MediaSource
import java.util.ArrayList
import collection.mutable.ListBuffer

/**
 * A [[cz.filmtit.core.model.MediaSourceFactory]] based on data from Freebase.com.
 *
 * @author Matúš Námešný
 */

class TMDbMediaSourceFactory(val apiKey: String) extends MediaSourceFactory {
  val url = "https://api.themoviedb.org/3/search/movie?api_key="+apiKey+"&language=en-US"
  val logger = LogFactory.getLog(this.getClass.getSimpleName)

  def getSuggestion(title: String, year: String): MediaSource = getSuggestions(title, year).get(0)

  def getSuggestions(title: String, year: String): java.util.List[MediaSource] = {
    val suggestions = getSuggestions(title)
    val filtered = new ArrayList[MediaSource]()
    for (i <- 0 to suggestions.size() - 1) {
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
        url + "&query=" + params + "&page=1&include_adult=false"
      ).mkString
      )

    } catch {
      case e: UnknownHostException => {
        logger.warn(url + "?\n" + e + "\nCould not reach The Movie Database server.")
        return new ArrayList[MediaSource]()
      }
    }

    val suggestions: JSONArray = try {
      apiResponse.getJSONArray("results")
    } catch {
      case e: org.json.JSONException => new JSONArray()
    }

    val mediaSources = new ArrayList[MediaSource]()

    for (i <- 0 to suggestions.length() - 1) {
      val source = suggestions.getJSONObject(i)
      val mediaSource = new MediaSource(source.getString("title"), source.getString("release_date").slice(0,4))
      mediaSource.setThumbnailURL("https://image.tmdb.org/t/p/w150" + source.getString("poster_path"))

      mediaSources.add(mediaSource)

    }
    mediaSources
  }
}
