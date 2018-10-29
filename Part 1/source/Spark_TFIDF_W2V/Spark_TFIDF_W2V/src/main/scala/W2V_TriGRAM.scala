import java.io.{File, PrintWriter}

import TF_IDF_NGRAM.getNGrams
import org.apache.spark.mllib.feature.{Word2Vec, Word2VecModel}
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Updated by Greg on 30-09-2018
  * Created by Mayanka on 19-06-2017.
  */
object W2V_TriGRAM {
    def main(args: Array[String]): Unit = {

      System.setProperty("hadoop.home.dir", "D:\\winutils")

      val sparkConf = new SparkConf().setAppName("SparkWordCount").setMaster("local[*]")
        .set("spark.driver.memory", "6g").set("spark.executor.memory", "6g")

      val sc = new SparkContext(sparkConf)

      val in = sc.textFile("output/TF_IDF_Trigram.txt")

      val compWords = in.flatMap(a =>{
        val r = a.substring(1, a.indexOf(",")).split("\n")
        r
      })


      val input = sc.wholeTextFiles("abstracts")

      val trigrams = input.map(f=>{
        val dn = getNGrams(f._2, 3)
        val ret = dn.map(a=>{
          var s = a.mkString(" ")
          s.replaceAll(",", "")
        })
        ret.toSeq
      })

      //var compareWord = "MRI"

      val modelFolder = new File("W2V/TrigramModel")

      var s:String=""

      if (modelFolder.exists()) {

        val sameModel = Word2VecModel.load(sc, "W2V/TrigramModel")
        compWords.collect().foreach(w => {
          val synonyms = sameModel.findSynonyms(w, 3)
          println("Synonyms for trigram: " + w )
          s+="\nSynonyms for lemma: " + w + "\n"
          for ((synonym, cosineSimilarity) <- synonyms) {
            println(s"$synonym $cosineSimilarity")
            s+=s"$synonym $cosineSimilarity" + "\n"
          }
        })
      }
      else {
        val word2vec = new Word2Vec().setVectorSize(1000).setMinCount(1)

        val model = word2vec.fit(trigrams)
        compWords.collect().foreach(w => {
          val synonyms = model.findSynonyms(w, 3)
          println("Synonyms for trigram: " + w )
          s+="\nSynonyms for lemma: " + w + "\n"
          for ((synonym, cosineSimilarity) <- synonyms) {
            println(s"$synonym $cosineSimilarity")
            s+=s"$synonym $cosineSimilarity" + "\n"
          }
          //model.getVectors.foreach(f => println(f._1 + ":" + f._2.length))
          // Save and load model
          if(!modelFolder.exists()) {
            model.save(sc, "W2V/TrigramModel")
          }
        })
      }
      val pw = new PrintWriter(new File("output/W2V_trigram.txt"))
      pw.write(s)
      pw.close()

    }
  }
