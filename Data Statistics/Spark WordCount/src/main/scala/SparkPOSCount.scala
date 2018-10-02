import java.io.{File, PrintWriter}

import org.apache.spark.{SparkConf, SparkContext}

//Below import at recommendation of https://stackoverflow.com/questions/16162090/how-to-convert-a-java-util-list-to-a-scala-list/26210263
import scala.collection.JavaConversions._

object SparkPOSCount {

  def main(args: Array[String]) {

    System.setProperty("hadoop.home.dir","D:\\winutils")

    val sparkConf = new SparkConf().setAppName("SparkWordCount").setMaster("local[*]")
    val sc = new SparkContext(sparkConf)

    val inputf = sc.wholeTextFiles("data/abstracts", 4)

    val output = inputf.map(line =>  CoreNLP.getPOS(line._2)).flatMap(list => list).map(word => (word._2,1)).reduceByKey(_+_)

    output.saveAsTextFile("output/POS")

    val o=output.collect()

    var s:String="POS\tCount \n"
    var vb,nn,ot = 0
    o.foreach{case(pos,count)=>{

      s+=pos+"\t"+count+"\n"
      if(pos.contains("NN")){
        nn += count.toInt
      }else if(pos.contains("VB")){
        vb += count.toInt
      }else{
        ot += count.toInt
      }
    }}
    print(s +"\n")
    print("Total nouns:" + nn +"\n")
    print("Total verbs:" + vb +"\n")
    print("Total other:" + ot +"\n")

    val pw = new PrintWriter(new File("output/posTotal.txt"))
    pw.write("Total nouns:" + nn +"\n")
    pw.write("Total verbs:" + vb +"\n")
    pw.write("Total other:" + ot +"\n")
    pw.close()

  }

}

