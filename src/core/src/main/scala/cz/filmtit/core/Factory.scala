package cz.filmtit.core

import concurrency.TranslationPairSearcherWrapper
import cz.filmtit.core.tm.BackoffTranslationMemory
import cz.filmtit.core.model.names.NERecognizer

import cz.filmtit.core.model.TranslationMemory
import opennlp.tools.namefind.{TokenNameFinderModel, NameFinderME}
import cz.filmtit.core.Utils.t2mapper

import java.sql.{SQLException, DriverManager, Connection}
import cz.filmtit.core.names.OpenNLPNameFinder
import opennlp.tools.tokenize.{WhitespaceTokenizer, Tokenizer}
import cz.filmtit.core.search.postgres.impl.{NEStorage, FirstLetterStorage}
import cz.filmtit.share.annotations.AnnotationType
import cz.filmtit.core.rank.{FuzzyNERanker, ExactRanker}
import search.external.MyMemorySearcher
import cz.filmtit.share.{Language, TranslationSource}
import cz.filmtit.core.Factory._
import collection.mutable.HashMap
import java.io.{File, FileInputStream}

/**
 * Factories for default implementations of various classes
 *
 * @author Joachim Daiber
 *
 */

object Factory {

  def createInMemoryConnection(): Connection = {
    Class.forName("org.hsqldb.jdbcDriver")
    DriverManager.getConnection("jdbc:hsqldb:mem:filmtitdb", "sa", "")
  }

  def createConnection(configuration: Configuration, readOnly: Boolean = true): Connection = {
    Class.forName("org.postgresql.Driver")

    val connection:Connection = try {
      DriverManager.getConnection(
        configuration.dbConnector,
        configuration.dbUser,
        configuration.dbPassword)
    } catch {
      case e: SQLException => {
        System.err.println("I could not connect to database %s. Please check if the DBMS is running and database exists.".format(configuration.dbConnector))
        println(e);
        System.exit(1)
        null
      }
    }

    //Assure the database is in read-only mode if required.
    if (readOnly == true)
      connection.setReadOnly(true)

    connection
  }

  def createNERecognizersFromConfiguration(configuration: Configuration) = {
    (configuration.l1, configuration.l2) map { l:Language=> createNERecognizers(l, configuration) }
  }

  def createInMemoryTM(configuration: Configuration): TranslationMemory = {
    val connection = createInMemoryConnection()

    createTM(
      configuration.l1,
      configuration.l2,
      connection,
      configuration,
      useInMemoryDB = true,
      maxNumberOfConcurrentSearchers = configuration.maxNumberOfConcurrentSearchers,
      searcherTimeout = configuration.searcherTimeout)
  }

  def createTMFromConfiguration(
    configuration: Configuration,
    readOnly: Boolean = true,
    useInMemoryDB: Boolean = false
    ): TranslationMemory = {
    createTM(
    configuration.l1, configuration.l2,
    { if (useInMemoryDB) createInMemoryConnection() else createConnection(configuration, readOnly) },
    configuration,
    useInMemoryDB,
    configuration.maxNumberOfConcurrentSearchers,
    configuration.searcherTimeout
    )
  }

  def createTM(
    l1: Language, l2: Language,
    connection: Connection,
    configuration: Configuration,
    useInMemoryDB: Boolean = false,
    maxNumberOfConcurrentSearchers: Int,
    searcherTimeout: Int
    ): TranslationMemory = {

    //Third level: Google translate
    val mtTM = new BackoffTranslationMemory(
      new MyMemorySearcher(
        Language.EN,
        Language.CS,
        allowedSources = Set(TranslationSource.EXTERNAL_MT)
      ),
      threshold = 0.7
    )

    val neSearchers = (1 to maxNumberOfConcurrentSearchers).map { _ =>
      val recognizers = createNERecognizersFromConfiguration(configuration)
      new NEStorage(Language.EN, Language.CS, connection, recognizers._1, recognizers._2, useInMemoryDB)
    }.toList

    //Second level fuzzy matching with NER:
    val neTM = new BackoffTranslationMemory(
      new TranslationPairSearcherWrapper(neSearchers, searcherTimeout),
      Some(new FuzzyNERanker()),
      threshold = 0.2,
      backoff = Some(mtTM)
    )

    //First level exact matching with backoff to fuzzy matching:
    new BackoffTranslationMemory(
      new FirstLetterStorage(Language.EN, Language.CS, connection, createTokenizer(Language.EN), createTokenizer(Language.CS), useInMemoryDB),
      Some(new ExactRanker()),
      threshold = 0.8,
      backoff = Some(neTM)
    )
  }


  val neModels = HashMap[String, TokenNameFinderModel]()

  /**
   * Build all NE recognizers specified for the language in
   * [[org.scalatest.prop.Configuration]].
   *
   * @param language the language the NE recognizers work on
   * @return
   */
  def createNERecognizers(language: Language, configuration: Configuration): List[NERecognizer] = {
    configuration.neRecognizers.get(language) match {
      case Some(recognizers) => recognizers map {
        pair => {
          val (neType, modelFile) = pair

          val model: TokenNameFinderModel = neModels.getOrElseUpdate(
            modelFile,
            new TokenNameFinderModel(new FileInputStream(modelFile))
          )

          new OpenNLPNameFinder(
            neType,
            new NameFinderME(model),
            createTokenizer(language)
          )
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
  def createNERecognizer(language: Language, neType: AnnotationType, configuration: Configuration): NERecognizer =
    createNERecognizers(language, configuration).filter( _.neClass == neType ).head


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
