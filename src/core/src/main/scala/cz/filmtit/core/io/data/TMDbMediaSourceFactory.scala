/*Copyright 2012 FilmTit authors - Karel Bílek, Josef Čech, Joachim Daiber, Jindřich Libovický, Rudolf Rosa, Jan Václ
Copyright 2017 Matúš Námešný

This file is part of FilmTit.

FilmTit is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 2.0 of the License, or
(at your option) any later version.

FilmTit is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with FilmTit.  If not, see <http://www.gnu.org/licenses/>.*/

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
      case _ => {
        logger.warn("Could not reach The Movie Database server.")
        return new ArrayList[MediaSource]()
      }
    }

    logger.error(apiResponse)

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
