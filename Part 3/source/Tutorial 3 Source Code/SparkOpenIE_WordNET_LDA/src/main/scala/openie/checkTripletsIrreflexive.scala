package openie

import java.io._

import scala.io.Source

object checkTripletsIrreflexive {
  def main(args: Array[String]) {
    val triplets = readFile("finalOutput/Triplets")

    var predicates = triplets.map(s => {
      val arr = s.split(",")
      arr(1)
    }).distinct

    var results = ""
    triplets.map(s => {
      val arr = s.split(",")
      (arr(0), arr(1), arr(2))
    }).foreach(a =>{
      var sub = a._1
      var pred = a._2
      var obj = a._3
//      if(sub==obj)
      if(sub==obj){
        println((sub, pred, obj))
        results+=pred+"\n"
      }
    })

    var ref = results.split("\n").distinct.mkString("\n")

    var fin = predicates.filter(p => {
      !results.split("\n").distinct.contains(p)
    }).mkString("\n")

    println("reflexive props:")
    println(ref)

    println("irreflexive props: ")
    println(fin)

    val pw = new PrintWriter(new File("output\\IrreflexiveCandidates.txt"))
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
