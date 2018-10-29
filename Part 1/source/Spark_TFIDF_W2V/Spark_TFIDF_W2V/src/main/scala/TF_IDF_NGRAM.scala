import java.io.{File, PrintWriter}

import org.apache.spark.mllib.feature.{HashingTF, IDF}
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.immutable.HashMap

/**
  * Created by Mayanka on 19-06-2017.
  */
object TF_IDF_NGRAM {

  def main(args: Array[String]): Unit = {
    //val a = getNGrams("the bee is the bee of the bees",2)
    //a.foreach(f=>println(f.mkString(" ")))

    System.setProperty("hadoop.home.dir", "D:\\winutils")

    val sparkConf = new SparkConf().setAppName("SparkWordCount").setMaster("local[*]")

    val sc = new SparkContext(sparkConf)

    //Reading the Text File
    val documents = sc.wholeTextFiles("abstracts") //WholeTextFiles

    //This is so that we don't get weird strings like "  ", "p  ", and " = " as ngrams
    val cleanDocs = documents.map(f => {
      val c = f._2.replaceAll("=", "").replaceAll("p ", "").replaceAll("  ", " ").replaceAll("  ", " ")
      (f._1,c)
    })

    val docs2grams = cleanDocs.map(f =>{
      val dn = getNGrams(f._2, 2)
      val ret = dn.map(a=>{
        val s = a.mkString(" ")
        println(s)
        s
      })
      ret.toSeq
    })

    val docs3grams = cleanDocs.map(f=>{
      val dn = getNGrams(f._2, 3)
      val ret = dn.map(a=>{
        var s = a.mkString(" ")
        println(s)
        s
      })
      ret.toSeq
    })

    //here are the 2grams

   val hashingTF2 = new HashingTF()

   val tf2 = hashingTF2.transform(docs2grams)
   tf2.cache()

    val idf2 = new IDF().fit(tf2)

    //Creating Inverse Document Frequency
    val tfidf2 = idf2.transform(tf2)

    val tfidf2values = tfidf2.flatMap(f => {
      val ff: Array[String] = f.toString.replace(",[", ";").split(";")
      val values = ff(2).replace("]", "").replace(")", "").split(",")
      values
    })

    val tfidf2index = tfidf2.flatMap(f => {
      val ff: Array[String] = f.toString.replace(",[", ";").split(";")
      val indices = ff(1).replace("]", "").replace(")", "").split(",")
      indices
    })


    val tfidfData = tfidf2index.zip(tfidf2values)

    var hm2 = new HashMap[String, Double]

    tfidfData.collect().foreach(f => {
      hm2 += f._1 -> f._2.toDouble
    })

    val mapp2 = sc.broadcast(hm2)

    val documentData2 = docs2grams.flatMap(_.toList)
    val dd2 = documentData2.map(f => {
      val i = hashingTF2.indexOf(f)
      val h = mapp2.value
      (f, h(i.toString))
    })

    var s2:String=""
    val dd12 = dd2.distinct().sortBy(_._2, false)
    dd12.take(20).foreach(f => {
      println(f)
      s2+=f+"\n"
    })

    val pw2 = new PrintWriter(new File("output/TF_IDF_Bigram.txt"))
    pw2.write(s2)
    pw2.close()

    // and here are the 3grams
    val temp3 = docs3grams

    val hashingTF3 = new HashingTF()

    val tf3 = hashingTF3.transform(docs3grams)
    tf3.cache()

    val idf3 = new IDF().fit(tf3)

    //Creating Inverse Document Frequency
    val tfidf3 = idf3.transform(tf3)

    val tfidf3values = tfidf3.flatMap(f => {
      val ff: Array[String] = f.toString.replace(",[", ";").split(";")
      val values = ff(2).replace("]", "").replace(")", "").split(",")
      values
    })

    val tfidf3index = tfidf3.flatMap(f => {
      val ff: Array[String] = f.toString.replace(",[", ";").split(";")
      val indices = ff(1).replace("]", "").replace(")", "").split(",")
      indices
    })

    val tfidf3Data = tfidf3index.zip(tfidf3values)

    var hm3 = new HashMap[String, Double]

    tfidf3Data.collect().foreach(f => {
      hm3 += f._1 -> f._2.toDouble
    })

    val mapp3 = sc.broadcast(hm3)

    val documentData3 = docs3grams.flatMap(_.toList)
    val dd3 = documentData3.map(f => {
      val i = hashingTF3.indexOf(f)
      val h = mapp3.value
      (f, h(i.toString))
    })

    var s3:String=""
    val dd13 = dd3.distinct().sortBy(_._2, false)
    dd13.take(20).foreach(f => {
      println(f)
      s3+=f+"\n"
    })

    val pw3 = new PrintWriter(new File("output/TF_IDF_Trigram.txt"))
    pw3.write(s3)
    pw3.close()

  }

  def getNGrams(sentence: String, n:Int): Array[Array[String]] = {
    val words = sentence
    val ngrams = words.split(' ').sliding(n)
    ngrams.toArray
  }



}


