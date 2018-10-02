import java.io.{File, PrintWriter}

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.feature.{Word2Vec, Word2VecModel}

/**
  * Created by Mayanka on 19-06-2017.
  */
object W2V {
  def main(args: Array[String]): Unit = {

    System.setProperty("hadoop.home.dir", "D:\\winutils")

    val sparkConf = new SparkConf().setAppName("SparkWordCount").setMaster("local[*]")
      .set("spark.driver.memory", "6g").set("spark.executor.memory", "6g")

    val sc = new SparkContext(sparkConf)

    val in = sc.textFile("output/TF_IDF_Out.txt")

    val compWords = in.flatMap(a =>{
      val r = a.substring(1, a.indexOf(",")).split(" ")
      r
    })

    val input = sc.wholeTextFiles("abstracts").map(line => line._2.split(" ").toSeq)
    //var compareWord = "MRI"


    val modelFolder = new File("W2V/Model")

    var s:String=""

    if (modelFolder.exists()) {

      val sameModel = Word2VecModel.load(sc, "W2V/Model")

      compWords.collect().foreach(w => {
        val synonyms = sameModel.findSynonyms(w, 3)
        println("Synonyms for word: " + w )
        s+="\nSynonyms for word: " + w + "\n"
        for ((synonym, cosineSimilarity) <- synonyms) {
          println(s"$synonym $cosineSimilarity")
          s+=s"$synonym $cosineSimilarity" + "\n"
        }
      })
    }
    else {
      val word2vec = new Word2Vec().setVectorSize(1000).setMinCount(1)

      val model = word2vec.fit(input)
      compWords.collect().foreach(w => {
        val synonyms = model.findSynonyms(w, 3)
        println("Synonyms for word: " + w )
        s+="\nSynonyms for word: " + w + "\n"
        for ((synonym, cosineSimilarity) <- synonyms) {
          println(s"$synonym $cosineSimilarity")
          s+=s"$synonym $cosineSimilarity" + "\n"
        }
        //model.getVectors.foreach(f => println(f._1 + ":" + f._2.length))
        // Save and load model
        if(!modelFolder.exists()) {
          model.save(sc, "W2V/Model")
        }
      })
    }
    val pw = new PrintWriter(new File("output/W2V.txt"))
    pw.write(s)
    pw.close()


  }
}
