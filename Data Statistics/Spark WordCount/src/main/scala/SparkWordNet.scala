import java.io.{File, PrintWriter}

import org.apache.spark.{SparkConf, SparkContext}
import rita.RiWordNet

object SparkWordNet {

  def main(args: Array[String]) {
    System.setProperty("hadoop.home.dir", "D:\\winutils")

    val sparkConf = new SparkConf().setAppName("SparkWordCount").setMaster("local[*]")
    val sc = new SparkContext(sparkConf)

    val inputf = sc.wholeTextFiles("data/abstracts", 4)

    val wordNetCount = inputf.flatMap(data =>{
      data._2.replaceAll("\\.", "").split(" ")
    }).map(word=>{
      word.replaceAll(",","")
    }).map(word=>{
      word.replaceAll("\\(","")
    }).map(word=>{
      word.replaceAll("\\)","")
    }).map(word=>{
      word.replaceAll(":","")
    }).map(word => if (new RiWordNet("D:\\WordNet-3.0").exists(word)) (word, 1) else (word, 0)).filter(word => !(word._2==0)).reduceByKey(_ + _)//

    wordNetCount.saveAsTextFile("output/WordNet")

    val wnC=wordNetCount.collect()

    var s:String="Words\tCount \n"

    var c = 0
    wnC.foreach{case(word,count)=>{
      s+=word+"\t"+count+"\n"
      c+=count.toInt

    }}

    print(s)
    print(c+"\n")
    val pw = new PrintWriter(new File("output/wordNetTotal.txt"))
    pw.write("Total WordNet Words:\t"+c+"\n")
    pw.write(s)
    pw.close()
  }
}
