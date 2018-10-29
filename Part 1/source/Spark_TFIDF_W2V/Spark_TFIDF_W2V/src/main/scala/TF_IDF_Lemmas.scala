import java.io.{File, PrintWriter}

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.feature.{HashingTF, IDF}

import scala.collection.immutable.HashMap

//Stopwords from https://www.ranks.nl/stopwords

/**
  * Updated by Greg on 30-09-2018
  * Created by Mayanka on 19-06-2017.
  */
object TF_IDF_Lemmas {
  def main(args: Array[String]): Unit = {

    System.setProperty("hadoop.home.dir", "D:\\winutils")

    val sparkConf = new SparkConf().setAppName("SparkWordCount").setMaster("local[*]")

    val sc = new SparkContext(sparkConf)

    //Reading the Text File
    val documents = sc.wholeTextFiles("abstracts") //WholeTextFiles

    val stopword = sc.textFile("data/stopwords.txt")

    val sw = stopword.flatMap(x => x.split(",")).map(_.trim)

    val broadcastSW = sc.broadcast(sw.collect.toSet)

    //Getting the Lemmatised form of the words in TextFile
    val documentseq = documents.map(f => {
      println("original:")
      println(f._2)
      val lemmatised = CoreNLP.returnLemma(f._2)
      println("lemmatised:")
      println(lemmatised)
      val splitString = lemmatised.split(" ").filter(!broadcastSW.value.contains(_)).filter( w => !w.contains("="))
      splitString.toSeq
    })

    val temp = documentseq

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
    dd1.take(20).foreach(f => {
      println(f)
      s+=f+"\n"
    })

    //Output the data to text file
    val pw = new PrintWriter(new File("output/TF_IDF_Lemmas.txt"))
    pw.write(s)
    pw.close()

  }

}
