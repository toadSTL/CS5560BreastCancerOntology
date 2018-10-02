import java.io.{File, PrintWriter}

import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable.ListBuffer
import scala.io.Source

object SparkMedWords {

  def main(args: Array[String]) {

    System.setProperty("hadoop.home.dir","D:\\winutils");

    val sparkConf = new SparkConf().setAppName("SparkWordCount").setMaster("local[*]").set("spark.executor.memory","4g")

    val sc = new SparkContext(sparkConf)

    if (args.length < 2) {
      System.out.println("\n$ java RESTClientGet [Bioconcept] [Inputfile] [Format]")
      System.out.println("\nBioconcept: We support five kinds of bioconcepts, i.e., Gene, Disease, Chemical, Species, Mutation. When 'BioConcept' is used, all five are included.\n\tInputfile: a file with a pmid list\n\tFormat: PubTator (tab-delimited text file), BioC (xml), and JSON\n\n")
    }
    else
    {
      val Bioconcept = args(0)
      val Inputfile = args(1)
      var Format = "PubTator"
      if (args.length > 2) {
        Format = args(2)
      }
      val medWords = ListBuffer.empty[(String, String)]

      for (line <- Source.fromFile(Inputfile).getLines) {
        val data = scala.io.Source.fromURL("https://www.ncbi.nlm.nih.gov/CBBresearch/Lu/Demo/RESTful/tmTool.cgi/" + Bioconcept + "/" + line + "/" + Format + "/").getLines()

        val lines = data.flatMap(line => {line.split("\n")}).drop(2)
        //lines.foreach{l =>print(l+"\n")}

        val words = lines.flatMap(word => {word.split("\t").drop(3).dropRight(1)}).toArray
        //words.foreach{w =>print(w+"\n")}
        for(i <- 0 until words.length by 2)
        {
          if(i < words.length - 1)
          {
            val work = (words(i), words(i+1))
            medWords += work
          }
        }
      }

      val medWordsOut = sc.parallelize(medWords.toList).map(word => (word._1, 1)).reduceByKey(_+_)
      medWordsOut.saveAsTextFile("output/MedWords")
      val mwO=medWordsOut.collect()

      var s:String="Words\tCount \n"
      var j:String=""
      var c = 0;
      mwO.foreach{case(word,count)=>{

        s+=word+"\t"+count+"\n"
        c+=count.toInt
        j+=word+"\n"
      }}
      print(s)
      print(c+"\n")
      val pw = new PrintWriter(new File("output/medWordsTotal.txt"))
      pw.write("Total Medical Words:\t"+c+"\n")
      pw.write(s)
      pw.close()

      val pw2 = new PrintWriter(new File("output/justMedWords.txt"))
      pw2.write(j)
      pw2.close()
    }

  }

}
