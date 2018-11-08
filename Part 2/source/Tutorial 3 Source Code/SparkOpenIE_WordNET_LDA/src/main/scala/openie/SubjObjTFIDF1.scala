package openie

import java.io.{File, PrintWriter}

import org.apache.log4j.{Level, Logger}
import org.apache.spark.mllib.feature.{HashingTF, IDF}
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.immutable.HashMap

object SubjObjTFIDF1 {
  def main(args: Array[String]): Unit ={
    // Configuration
    System.setProperty("hadoop.home.dir", "D:\\winutils")

    val sparkConf = new SparkConf().setAppName("SparkWordCount").setMaster("local[*]")

    val sc = new SparkContext(sparkConf)

    // Turn off Info Logger for Console
    Logger.getLogger("org").setLevel(Level.OFF)
    Logger.getLogger("akka").setLevel(Level.OFF)

    val input = sc.wholeTextFiles("data")
    val docSansPreds = input.map(doc =>{
      val test = doc._2.replaceAll("\\)\\]", "").replaceAll("\\[\\(", "").replaceAll(":", "").replaceAll("%", "percecnt")
      val sen =  CoreNLP.returnTriplets(doc._2)//test)
      val a = sen.split("\\), \\(|\\)\\]\\[\\(")
      val finSen = a.map(sen => {
        val c = sen.replaceAll("\\)\\]", "").replaceAll("\\[\\(", "").replaceAll(":", "").replaceAll("%", "percecnt")
        val t = c.split(",")
        val ret: String = t(0)+" "+t(2)+" "
        ret
      }).mkString
//      println(doc._1)
//      println(finSen)
      (doc._1, finSen)
    })

    val stopword = sc.textFile("IDS/stopwords.txt")
    val sw = stopword.flatMap(x => x.split(",")).map(_.trim)
    val broadcastSW = sc.broadcast(sw.collect.toSet)

    //RDD of sequence of string
    //Getting the Lemmatised form of the words in TextFile
    val documentseq = docSansPreds.map(f => {
      val splitString = f._2.split(" ").filter(!broadcastSW.value.contains(_))
      val temp = splitString.map(word => {
        val cw = word.replaceAll(",","").replaceAll("\\.", "").replaceAll(":", "").replaceAll("%", "percecnt")
        cw
      }).filter(_.nonEmpty)
      temp.toSeq
    })
//    documentseq.foreach(println)
    //Creating an object of HashingTF Class
    val hashingTF = new HashingTF()

    //Creating Term Frequency of the document
    val tf = hashingTF.transform(documentseq)
    tf.cache()

    val idf = new IDF().fit(tf)

    //Creating Inverse Document Frequency
    val tfidf = idf.transform(tf)

    val tfidfvalues = tfidf.flatMap(f => {
      val ff: Array[String] = f.toString.replace(",[", ";").split(";")
      val values = ff(2).replace("]", "").replace(")", "").split(",")
      values
    })

    val tfidfindex = tfidf.flatMap(f => {
      val ff: Array[String] = f.toString.replace(",[", ";").split(";")
      val indices = ff(1).replace("]", "").replace(")", "").split(",")
      indices
    })

    val tfidfData = tfidfindex.zip(tfidfvalues)

    var hm = new HashMap[String, Double]

    tfidfData.collect().foreach(f => {
      hm += f._1 -> f._2.toDouble
    })

    val mapp = sc.broadcast(hm)

    val documentData = documentseq.flatMap(_.toList)
    val dd = documentData.map(f => {
      val i = hashingTF.indexOf(f)
      val h = mapp.value
      (f, h(i.toString))
    })

    var s:String=""

    val dd1 = dd.distinct().sortBy(_._2, false)
    dd1.take(50).foreach(f => {
//      println(f)
      s+=f+"\n"
    })

    //Output the data to text files
    val pw = new PrintWriter(new File("output/TF_IDF_Out.txt"))
    pw.write(s)
    pw.close()

//    val pwDoc = new PrintWriter(new File("output\\DocsSansPreds.txt"))
//    pwDoc.write(docSansPreds.collect().mkString("\n"))
//    pwDoc.close
  }
}
