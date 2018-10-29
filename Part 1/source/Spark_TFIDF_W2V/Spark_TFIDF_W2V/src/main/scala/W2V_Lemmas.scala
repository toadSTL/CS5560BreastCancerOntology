import java.io.{File, PrintWriter}

import org.apache.spark.mllib.feature.{Word2Vec, Word2VecModel}
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Updated by Greg on 30-09-2018
  * Created by Mayanka on 19-06-2017.
  */
object W2V_Lemmas {
    def main(args: Array[String]): Unit = {

      System.setProperty("hadoop.home.dir", "D:\\winutils")

      val sparkConf = new SparkConf().setAppName("SparkWordCount").setMaster("local[*]")
        .set("spark.driver.memory", "6g").set("spark.executor.memory", "6g")

      val sc = new SparkContext(sparkConf)

      val in = sc.textFile("output/TF_IDF_Lemmas.txt")

      val compWords = in.flatMap(a =>{
        val r = a.substring(1, a.indexOf(",")).split(" ")
        r
      })


      val input = sc.wholeTextFiles("abstracts")

      val stopword = sc.textFile("data/stopwords.txt")

      val sw = stopword.flatMap(x => x.split(",")).map(_.trim)

      val broadcastSW = sc.broadcast(sw.collect.toSet)

      val lemmas = input.map(f => {
          val lemmatised = CoreNLP.returnLemma(f._2)
          val splitString = lemmatised.split(" ").filter(
            w => !w.contains(",")).filter(
            w => !w.contains(".")).filter(
            w => !w.contains(";")).filter(
            w => !w.contains(":")).filter(
            w => !w.contains("<")).filter(
            w => !w.contains("%")).filter(
            w => !w.contains("=")).filter(!broadcastSW.value.contains(_))
          splitString.toSeq
        })

      //var compareWord = "MRI"

      val modelFolder = new File("W2V/LemmaModel")

      var s:String=""

      if (modelFolder.exists()) {

        val sameModel = Word2VecModel.load(sc, "W2V/LemmaModel")
        compWords.collect().foreach(w => {
          val synonyms = sameModel.findSynonyms(w, 3)
          println("Synonyms for lemma: " + w )
          s+="\nSynonyms for lemma: " + w + "\n"
          for ((synonym, cosineSimilarity) <- synonyms) {
            println(s"$synonym $cosineSimilarity")
            s+=s"$synonym $cosineSimilarity" + "\n"
          }
        })
      }
      else {
        val word2vec = new Word2Vec().setVectorSize(1000).setMinCount(1)

        val model = word2vec.fit(lemmas)
        compWords.collect().foreach(w => {
          val synonyms = model.findSynonyms(w, 3)
          println("Synonyms for lemma: " + w )
          s+="\nSynonyms for lemma: " + w + "\n"
          for ((synonym, cosineSimilarity) <- synonyms) {
            println(s"$synonym $cosineSimilarity")
            s+=s"$synonym $cosineSimilarity" + "\n"
          }
          //model.getVectors.foreach(f => println(f._1 + ":" + f._2.length))
          // Save and load model
          if(!modelFolder.exists()) {
            model.save(sc, "W2V/LemmaModel")
          }
        })
      }
      val pw = new PrintWriter(new File("output/W2V_lemmas.txt"))
      pw.write(s)
      pw.close()

    }
  }
