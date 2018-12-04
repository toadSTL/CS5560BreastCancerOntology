package openie

import java.io._

import scala.io.Source

object checkTripletsTransitive {
  def main(args: Array[String]) {
    val triplets = readFile("finalOutput/Triplets")

    val trips1 = triplets.map(s => {
      val arr = s.split(",")
      (arr(0), arr(1), arr(2))
    })

    val trips2 = triplets.map(s => {
      val arr = s.split(",")
      (arr(0), arr(1), arr(2))
    })

    var results = ""
    triplets.map(s => {
      val arr = s.split(",")
      (arr(0),arr(1),arr(2))
    }).foreach(a =>{
      var sub1 = a._1
      var pred1 = a._2
      var obj1 = a._3
      trips1.foreach(ar =>{
        var sub2 = ar._1
        var pred2 = ar._2
        var obj2 = ar._3
        trips2.foreach(arr => {
          var sub3 = arr._1
          var pred3 = arr._2
          var obj3 = arr._3
          if(obj1==sub2&&obj2==sub3&&pred1==pred2&&pred2==pred3){
            println((sub1, pred1, obj1))
            println((sub2, pred2, obj2))
            println((sub3, pred3, obj3))
            results+=pred1+"\n"
          }
        })
      })
    })

    var fin = results.split("\n").distinct.mkString("\n")

    println("results go here: ")
    println(fin)

    val pw = new PrintWriter(new File("output\\TransitiveCandidates.txt"))
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
