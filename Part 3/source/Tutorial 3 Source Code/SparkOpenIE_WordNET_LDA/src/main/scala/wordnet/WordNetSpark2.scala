package wordnet

import java.io.{File, PrintWriter}

import org.apache.spark.{SparkConf, SparkContext}
import rita.RiWordNet

/**
  * Created by Mayanka on 26-06-2017.
  */
object WordNetSpark2 {
  def main(args: Array[String]): Unit = {
    System.setProperty("hadoop.home.dir", "D:\\winutils")
    val conf = new SparkConf().setAppName("WordNetSpark").setMaster("local[*]").set("spark.driver.memory", "4g").set("spark.executor.memory", "4g")
    val sc = new SparkContext(conf)

//    var i = 0
//    for (i <- 1 to 10){

//    val data = sc.textFile("data\\" + i + ".txt")
    val data = sc.textFile("output/TFIDF.txt")

    val dd = data.map(f = line => {
      val wordnet = new RiWordNet("D:\\WordNet-3.0")
      val wordWithScore = line.replaceAll("\\(", "").replaceAll("\\)", "").split(",")
      //      val wordSet = line.split(",")

      val word = wordWithScore(0)
      println(word)
      //used to ensure each word is being considered
      //wordSet.foreach(println)
//      val synarr = word.map(w => {
//        if (wordnet.exists(word))
//          (word, getSynoymns(wordnet, word))
//        else
//          (word, null)
//      })
//      synarr

      var arr:Array[String] = null
      if(wordnet.exists(word)){
        arr = getSynoymns(wordnet,word)
      }
      (word,arr)
    })
      val pw = new PrintWriter(new File("output/synTFIDF.txt"))

    dd.collect().foreach(linesyn => {
      if(linesyn._2!=null){
        pw.println(linesyn._1 + "," + linesyn._2.mkString(","))
      }else{
        pw.println(linesyn._1)
      }
      //      linesyn.foreach(wordssyn => {
//        if (wordssyn._2 != null)
//          pw.println(wordssyn._1 + ":" + wordssyn._2.mkString(","))
//      })
    })
      pw.close
//    }
  }
  def getSynoymns(wordnet:RiWordNet,word:String): Array[String] ={
    println(word)
    val pos=wordnet.getPos(word)
    // println(pos.mkString(" "))
    val syn=wordnet.getAllSynonyms(word, pos(0), 100)
    syn
  }

}
