import java.io.{File, PrintWriter}

import org.apache.spark.mllib.feature.{Word2Vec, Word2VecModel}
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Updated by Greg on 30-09-2018
  * Created by Mayanka on 19-06-2017.
  */
object W2V_MedWords {
    def main(args: Array[String]): Unit = {

      System.setProperty("hadoop.home.dir", "D:\\winutils")

      val sparkConf = new SparkConf().setAppName("SparkWordCount").setMaster("local[*]")
        .set("spark.driver.memory", "6g").set("spark.executor.memory", "6g")

      val sc = new SparkContext(sparkConf)

      val in = sc.textFile("data/justMedWords.txt")

      val medWords = in.flatMap(a =>{
        val r = a.split("\n")
        r
      })//.filter(w => !w.contains(" "))

      val input = sc.wholeTextFiles("abstracts").map(line => line._2.split(" ").toSeq)

      val modelFolder = new File("W2V/MedWordModel")

      var s:String=""

      var n:String="Medical Words Not in Model: \n"

      if (modelFolder.exists()) {

        val sameModel = Word2VecModel.load(sc, "W2V/MedWordModel")
        medWords.collect().foreach(w => {
          if(sameModel.getVectors.keys.exists(k => k==w)){
            val synonyms = sameModel.findSynonyms(w, 3)
            println("Synonyms for MedWord: " + w )
            s+="\nSynonyms for MedWord: " + w + "\n"
            for ((synonym, cosineSimilarity) <- synonyms) {
              println(s"$synonym $cosineSimilarity")
              s+=s"$synonym $cosineSimilarity" + "\n"
            }
          } else {
            n += w + "\n"
          }
        })
      }
      else {
        val word2vec = new Word2Vec().setVectorSize(1000).setMinCount(1)

        val model = word2vec.fit(input)
        medWords.collect().foreach(w => {
          if(model.getVectors.keys.exists(k => k==w)){
            val synonyms = model.findSynonyms(w, 3)
            println("Synonyms for MedWord: " + w )
            s+="\nSynonyms for MedWord: " + w + "\n"
            for ((synonym, cosineSimilarity) <- synonyms) {
              println(s"$synonym $cosineSimilarity")
              s+=s"$synonym $cosineSimilarity" + "\n"
            }
            //model.getVectors.foreach(f => println(f._1 + ":" + f._2.length))
            // Save and load model
            if(!modelFolder.exists()) {
              model.save(sc, "W2V/MedWordModel")
            }
          } else {
            n += w + "\n"
          }
        })
      }

      val pw = new PrintWriter(new File("output/W2V_MedWords.txt"))
      pw.write(s)
      pw.close()

    }
  }
