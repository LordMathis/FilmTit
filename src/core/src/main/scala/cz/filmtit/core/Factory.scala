package cz.filmtit.core

import cz.filmtit.core.tm.BackoffTranslationMemory
import cz.filmtit.core.model.names.NERecognizer

import cz.filmtit.core.model.TranslationMemory
import opennlp.tools.namefind.{TokenNameFinderModel, NameFinderME}

import cz.filmtit.core.names.OpenNLPNameFinder
import java.io.FileInputStream
import opennlp.tools.tokenize.{WhitespaceTokenizer, Tokenizer}
import cz.filmtit.core.search.postgres.impl.{NEStorage, FirstLetterStorage}
import cz.filmtit.core.model.annotation.ChunkAnnotation
import cz.filmtit.core.rank.{FuzzyNERanker, ExactRanker}
import search.external.MyMemorySearcher
import cz.filmtit.share.{Language, TranslationSource}

/**
 * @author Joachim Daiber
 *
 */

object Factory {

  /**
   * Create the default implementation of a TranslationMemory.
   *
   * @return the TM
   */
  def createTM(readOnly: Boolean = true): TranslationMemory = {

    //Third level: Google translate
    val mtTM = new BackoffTranslationMemory(
      new MyMemorySearcher(
        Language.EN,
        Language.CS,
        allowedSources = Set(TranslationSource.EXTERNAL_MT)
      ),
      threshold = 0.7
    )

    //Second level fuzzy matching with NER:
    val neTM = new BackoffTranslationMemory(
      new NEStorage(Language.EN, Language.CS, readOnly=readOnly),
      Some(new FuzzyNERanker()),
      threshold = 0.2,
      backoff = Some(mtTM)
    )

    //First level exact matching with backoff to fuzzy matching:
    new BackoffTranslationMemory(
      new FirstLetterStorage(Language.EN, Language.CS, readOnly=readOnly),
      Some(new ExactRanker()),
      threshold = 0.8,
      backoff = Some(neTM)
    )

  }


  /**
   * Build a NE recognizer for NE type #neType and language #language with the
   * model specified in #modelFile .
   *
   * @param neType the type of NE, the recognizer detects
   * @param language the language that is recognized
   * @param modelFile the external model file for the NE recognizer
   * @return
   */
  def createNERecognizer(
    neType: ChunkAnnotation,
    language: Language,
    modelFile: String
  ): NERecognizer = {

    val tokenNameFinderModel = new TokenNameFinderModel(
      new FileInputStream(modelFile)
    )

    new OpenNLPNameFinder(
      neType,
      new NameFinderME(tokenNameFinderModel),
      createTokenizer(language)
    )
  }

  /**
   * Build all NE recognizers specified for the language in
   * [[org.scalatest.prop.Configuration]].
   *
   * @param language the language the NE recognizers work on
   * @return
   */
  def createNERecognizers(language: Language): List[NERecognizer] = {
    Configuration.neRecognizers.get(language) match {
      case Some(recognizers) => recognizers map {
        pair => {
          val (neType, modelFile) = pair
          Factory.createNERecognizer(neType, language, modelFile)
        }
      }
      case None => List()
    }
  }


  /**
   * Build a NE recognizer from [[cz.filmtit.core.Configuration]].
   *
   * @param language the language the NE recognizers works on
   * @param neType the type of NE, the recognizer detects
   * @return
   */
  def createNERecognizer(language: Language, neType: ChunkAnnotation): NERecognizer =
    createNERecognizers(language).filter( _.neClass == neType ).head


  /**
   * Build the default Tokenizer for a language.
   *
   * @param language language to be tokenized
   * @return
   */
  def createTokenizer(language: Language): Tokenizer = {
    WhitespaceTokenizer.INSTANCE
  }
}
