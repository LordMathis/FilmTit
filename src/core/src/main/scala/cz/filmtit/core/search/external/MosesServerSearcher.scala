package cz.filmtit.core.search.external

import java.net.{UnknownHostException, URLEncoder}
import io.Source
import org.apache.commons.logging.LogFactory
import org.json.{JSONArray, JSONObject}
import cz.filmtit.core.model.TranslationPairSearcher
import collection.mutable.ListBuffer
import cz.filmtit.share.{Language, TranslationPair, TranslationSource, Chunk}
import opennlp.tools.tokenize.Tokenizer
import org.apache.xmlrpc.client.XmlRpcClient
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl
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
 * @author Karel Bílek
 */

object MosesServerSearcher {
  val spaceBefDiaRegex = """\s([.!?,])""".r
  val apoRegex = """^\p{Lu}""".r
  val capitalLetterRegex = """^\p{Lu}""".r
  val apostropheAfterRegex = """(\S)&apos;""".r
  val apostropheRegex = """'(\S)""".r
  val spaceRegex = """\s+(\s)""".r
  val unkRegex = """\|UNK""".r
  val ntRegex = """ n &apos; t""".r


  

}

class MosesServerSearcher(
  l1: Language,
  l2: Language,
  url: java.net.URL
  ) extends TranslationPairSearcher(l1, l2) {

  val config = new XmlRpcClientConfigImpl();
  config.setServerURL(url);
  val client = new XmlRpcClient();
  client.setConfig(config);

  def getRawTranslation(source:String):String = {
    val mosesParams = new java.util.HashMap[String,String]()
    mosesParams.put("text", source)
    mosesParams.put("align", "false")
    mosesParams.put("report-all-factors", "false")
    val params = Array[Object](null)
    params(0) = mosesParams


    val result:java.util.HashMap[String, Object] = client.execute("translate", params) match {
        case m:java.util.HashMap[String, Object]=>m
        case _ => throw new ClassCastException("Wrong type of result from moses")
    }

    val translation = result.get("text") match {
        case s:String=> s
        case _ => throw new ClassCastException("Wrong type of result from moses")
    }
    translation;
  }

  def prepareAndSendToMoses(sourceTokens:Array[String]):String = {
   
    import MosesServerSearcher._

    if (sourceTokens.size ==0) {
        return "";
    }

    println("MOJZIS - chci "+sourceTokens);
// capital letter at the beginning /can cause trouble in named entities, but what are you gonna do/
     val (wasCapitalizedSource, uncapitalizedTokens) = 
        if (MosesServerSearcher.capitalLetterRegex.findFirstIn(sourceTokens(0)).isDefined) {
            (true, sourceTokens(0).toLowerCase +: sourceTokens.tail)
        } else {
           (false, sourceTokens)
        }

     val joinedSource = uncapitalizedTokens.reduceLeftOption(_+" "+_).get
     
     //scala sucks with regexes
     val toSend = ntRegex.replaceAllIn(
                    apostropheAfterRegex.replaceAllIn(
                      apostropheRegex.replaceAllIn(joinedSource, """&apos; $1"""), 
                    """$1 &apos;"""), 
                  "n &apos; t");

     val translation = getRawTranslation(toSend) 

	 val translationWithoutSpaces = spaceBefDiaRegex.replaceAllIn(spaceRegex.replaceAllIn(translation, " "), "$1")
     
     val res = if (wasCapitalizedSource) {
        translationWithoutSpaces.capitalize
     } else {
        translationWithoutSpaces
     }
     val resUnk = unkRegex.replaceAllIn(res, "")
     resUnk
 
  }
  
  def candidates(chunk: Chunk, language: Language): List[TranslationPair] = { 
    if (language != l1) {
      throw new Exception("Moses can translate from "+l1.getName+" to "+l2.getName+", you requested "+language.getName);
    }
    List[TranslationPair] (
       new TranslationPair(
          chunk,
          new Chunk(prepareAndSendToMoses(chunk.getTokens)),
          TranslationSource.EXTERNAL_TM,
          1
        )
    )
  }
  
  

}