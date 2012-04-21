package cz.fimtit.eval.database

import cz.filmtit.core.Factory
import java.net.ConnectException
import cz.filmtit.core.model.TranslationMemory
import cz.filmtit.share.Language
import cz.filmtit.core.Utils.chunkFromString


/**
 * @author Joachim Daiber
 *
 */

object SimpleTMQuery {
  def main(args: Array[String]) {

    println("Starting translation memory...")
    
    val tm: TranslationMemory = try {
      Factory.createTM()
    } catch {
      case e: ConnectException => {
        println("Error: " + e.getMessage)
        System.exit(1)
        null
      }
    }
    println("Done.")

    
    
    println(tm.nBest("I love you!", Language.EN, null))
    println(tm.nBest("What did the minister tell you about his intentions?",
      Language.EN, null))
    println(tm.nBest("Call the police, Peter", Language.EN, null))
    println(tm.nBest("Peter opened the door.", Language.EN, null))
    println(tm.nBest("Watch out!", Language.EN, null))


  }
}