/*Copyright 2012 FilmTit authors - Karel Bílek, Josef Čech, Joachim Daiber, Jindřich Libovický, Rudolf Rosa, Jan Václ

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

package cz.filmtit.core.search.external

import java.net.{ UnknownHostException, URLEncoder }
import io.Source
import org.apache.commons.logging.LogFactory
import org.json.{ JSONArray, JSONObject }
import cz.filmtit.core.model.TranslationPairSearcher
import collection.mutable.ListBuffer
import cz.filmtit.share.{ Language, TranslationPair, TranslationError, TranslationSource, Chunk }

/**
 * Translation pair searcher using standard Moses server
 *
 * The server doesn't require any identification.
 *
 * Both the input and the output have to be very slightly
 * adjusted.
 *
 * The server translate only in one way and returns just
 * one translation result.
 *
 * @author Karel Bílek, Matúš Námešný
 */

/**
 * Moses searcher, that sends the sentence tokenized to remote moses server.
 * @constructor Creates a MosesServerSearcher.
 * @param l1 Source language
 * @param l2 Target language
 * @param regexesBeforeServer regexes that are run before sending to moses
 * @param regexesAfterServer regexes, that are run after moses (don't )
 * @param url Url of the remote Moses server (url:port)
 * @param genericTranslationScore The score with which the moses searcher returns the sentences
 * @param numberOfTries How many times is the Moses server called
 * @param repeatAfterMilliseconds After how many milliseconds is server contacted again?
 */
class MosesServerSearcher(
  l1: Language,
  l2: Language,
  url: java.net.URL,

  genericTranslationScore: Double = 0.7,
  numberOfTries: Int = 4,
  repeatAfterMilliseconds: Long = 1000
) extends TranslationPairSearcher(l1, l2) {

  val logger = LogFactory.getLog(this.getClass.getSimpleName)
  val limit = 5

  def candidates(chunk: Chunk, language: Language): List[TranslationPair] = {

    val apiResponse = try {

      val params = "action=translate&model=0&sourceLang=" + language.getCode + "&targetLang=" + { if (language == l1) l2.getCode else l1.getCode } + "&nBestSize=5" + "&text=" + URLEncoder.encode(chunk.getSurfaceForm, "utf-8")

      new JSONObject(
        Source.fromURL(
        url + "?" + params
      ).mkString
      )

    } catch {
      case e: UnknownHostException => {
        logger.warn(url + "?\n" + e + "\nCould not reach Moses server.")
        return List[TranslationPair]()
      }
    }
    val candidates = ListBuffer[TranslationPair]()


    if (apiResponse.getInt("errorCode") != 0) {
      //logger.info("Moses server error: " + apiResponse.getString("errorMessage"))
      val result = new TranslationError(apiResponse.getInt("errorCode"), apiResponse.getString("errorMessage"))
      candidates += result
      return candidates.toList
    }

    val matches: JSONArray = try {
      apiResponse.getJSONArray("translation").getJSONObject(0).getJSONArray("translated")
    } catch {
      case e: org.json.JSONException => new JSONArray()
    }

    //Retrieve all matches:
    for (i <- 0 to math.min(matches.length() - 1, limit)) {
      val translation = matches.getJSONObject(i)

      //Set the chunks for the resulting translation pair
      val chunkL1 = if (language == l1) chunk else new Chunk(translation.getString("text"))
      val chunkL2 = if (language == l1) new Chunk(translation.getString("text")) else chunk

      //Check the source of the translation:
      val source = TranslationSource.EXTERNAL_MT

      val quality = 1.0

      candidates += new TranslationPair(
        chunkL1,
        chunkL2,
        source,
        quality
      )

    }
    candidates.toList
  }

  /**
   * Does nothing.
   */
  def close() {}

  def requiresTokenization = false

  /**
   * Name of searcher.
   * @return "Moses" string.
   */
  override def toString = "Moses"
}
