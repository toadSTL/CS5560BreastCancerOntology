package openie

import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkConf, SparkContext}
import java.io._
import scala.io.Source
import scala.collection.mutable.ListBuffer
import java.lang.String._

import org.apache.spark.{SparkConf, SparkContext}

object checkTripletsInverseOf {
  def main(args: Array[String]) {
//    System.setProperty("hadoop.home.dir", "D:\\winutils")
//
//    val sparkConf = new SparkConf().setAppName("SparkWordCount").setMaster("local[*]")
//
//    val sc = new SparkContext(sparkConf)

//    val triplets = sc.textFile("ICP11/Triplets")

    val triplets = readFile("finalOutput/Triplets")


    val trips = triplets.map(s => {
      val arr = s.split(",")
      (arr(0), arr(1), arr(2))
    })
//      .collect.toSet
//    var res = Vector.empty[(String,String)]
//    var test = res :+ ("test1","test2")
//    var acc = Vector.empty[(String,String)]
    var results = ""
//    val temp2 =
      triplets.map(s => {
      val arr = s.split(",")
      (arr(0),arr(1),arr(2))
    }).foreach(a =>{
      var sub1 = a._1
      var pred1 = a._2
      var obj1 = a._3
//      var res = Seq.empty[(String,String)]
      trips.foreach(arr =>{
        var sub2 = arr._1
        var pred2 = arr._2
        var obj2 = arr._3
        var res = Vector.empty[(String,String)]
        if(sub1==obj2&&sub2==obj1&&pred1!=pred2){
          println( (sub1, pred1, obj1) )
          println( (sub2, pred2, obj2) )
          results+=pred1+","+pred2+"\n"
        }
      })

//      println(res)
//      var test = res.toArray
//      println(test.length)
//      test.foreach(println)
//      test
    })
//    println("res here: ")
//    test.foreach(println)

//    temp2.foreach(println)

//    val pwdummy = new PrintWriter(new File("output\\dumb"))
//    pwdummy.write(temp2.mkString("\n")) //temp2.collect().mkString("\n")
//    pwdummy.close

    var fin = results.split("\n").distinct.mkString("\n")

    println("results go here: ")
    println(fin)

    val pw = new PrintWriter(new File("output\\InverseCandidates.txt"))
    pw.write(fin)
    pw.close

  }// end inverse

  def readFile(filename: String): Seq[String] = {
    val bufferedSource = Source.fromFile(filename)
    val lines = (for (line <- bufferedSource.getLines()) yield line).toList
    bufferedSource.close
    lines
  }

}
