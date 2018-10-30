package wordnet

import java.io.{File, PrintWriter}

import org.apache.spark.{SparkConf, SparkContext}
import rita.RiWordNet

/**
  * Created by Mayanka on 26-06-2017.
  */
object WordNetSpark {
  def main(args: Array[String]): Unit = {
    System.setProperty("hadoop.home.dir", "D:\\winutils")
    val conf = new SparkConf().setAppName("WordNetSpark").setMaster("local[*]").set("spark.driver.memory", "4g").set("spark.executor.memory", "4g")
    val sc = new SparkContext(conf)

    var i = 0
    for (i <- 1 to 10){

    val data = sc.textFile("data\\" + i + ".txt")

    val dd = data.map(line => {
      val wordnet = new RiWordNet("D:\\WordNet-3.0")
      val wordSet = line.split(" ")

      //used to ensure each word is being considered
      //wordSet.foreach(println)
      val synarr = wordSet.map(word => {
        if (wordnet.exists(word))
          (word, getSynoymns(wordnet, word))
        else
          (word, null)
      })
      synarr
    })
      val pw = new PrintWriter(new File("data\\syn" + i + ".txt"))

    dd.collect().foreach(linesyn => {
      linesyn.foreach(wordssyn => {
        if (wordssyn._2 != null)
          pw.println(wordssyn._1 + ":" + wordssyn._2.mkString(","))
      })
    })
      pw.close
    }
  }
  def getSynoymns(wordnet:RiWordNet,word:String): Array[String] ={
    // println(word)
    val pos=wordnet.getPos(word)
    // println(pos.mkString(" "))
    val syn=wordnet.getAllSynonyms(word, pos(0), 10)
    syn
  }

}
