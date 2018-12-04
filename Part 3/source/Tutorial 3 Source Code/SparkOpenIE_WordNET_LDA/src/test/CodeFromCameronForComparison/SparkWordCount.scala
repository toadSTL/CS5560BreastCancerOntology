package openie.CodeFromCameronForComparison

import java.util.Properties

import edu.stanford.nlp.ling.CoreAnnotations.{LemmaAnnotation, PartOfSpeechAnnotation, SentencesAnnotation, TokensAnnotation}
import edu.stanford.nlp.pipeline.{Annotation, StanfordCoreNLP}
import org.apache.spark.mllib.feature.{HashingTF, IDF}
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import rita.RiWordNet

import scala.collection.immutable.HashMap
import scala.collection.mutable.ListBuffer
import scala.io.Source

object SparkWordCount {

  def main(args: Array[String]) {

    System.setProperty("hadoop.home.dir","D:\\winutils")

    val sparkConf = new SparkConf().setAppName("SparkWordCount").setMaster("local[*]")
    val sc = new SparkContext(sparkConf)

    // retrieve data
    val inputf = sc.wholeTextFiles("data", 4)
    val stopwords = sc.textFile("IDS/stopwords.txt").collect()

    // lemmatize the data
    val lemmatized = inputf.map(line => lemmatize(line._2, stopwords))
    val flatLemma = lemmatized.flatMap(list => list)
    val lemmatizedSeq = flatLemma.map(list => List(list._1))
    val ngram2LemmaSeq = lemmatized.map(line => {
      val ngrams = getNGrams(line.map(line => line._1).mkString(" "), 2).map(list => list.mkString(" ")).toList
      ngrams
    })

    val ngram3LemmaSeq = lemmatized.map(line => {
      val ngrams = getNGrams(line.map(line => line._1).mkString(" "), 3).map(list => list.mkString(" ")).toList
      ngrams
    })

    val ngram2Num = ngram2LemmaSeq.map(line => ("total", line.length))
    val ngram2Total = ngram2Num.reduceByKey(_+_)

    val ngram3Num = ngram3LemmaSeq.map(line => ("total", line.length))
    val ngram3Total = ngram3Num.reduceByKey(_+_)

    // count for all words and count for all parts of speech
    val wc = flatLemma.map(word =>(word._1 + "," + word._2, 1))
    val wcTotal = wc.count()
    val posCount = flatLemma.map(word => (word._2, 1))

    // count for wordnet words
    val wordnetCount = flatLemma.map(word => if(new RiWordNet("D:\\WordNet-3.0").exists(word._1)) (word._1 + "," + word._2, 1) else (word._1 + "," + word._2, 0)).reduceByKey(_+_)
    val wordnetCountTotal = wordnetCount.count()
    var medWordTotal: Long = 0

    val medThread = new Thread(new Runnable {
      def run(): Unit = {
        // medical word retrieval
        if (args.length < 2) {
          System.out.println("\n$ java RESTClientGet [Bioconcept] [Inputfile] [Format]")
          System.out.println("\nBioconcept: We support five kinds of bioconcepts, i.e., Gene, Disease, Chemical, Species, Mutation. When 'BioConcept' is used, all five are included.\n\tInputfile: a file with a pmid list\n\tFormat: PubTator (tab-delimited text file), BioC (xml), and JSON\n\n")
        }
        else
        {
          val Bioconcept = args(0)
          val Inputfile = args(1)
          var Format = "PubTator"
          if (args.length > 2) Format = args(2)

          val medWords = ListBuffer.empty[(String, String)]

          // retieve ids and get data
          for (line <- Source.fromFile(Inputfile).getLines) {
            val data = get("https://www.ncbi.nlm.nih.gov/CBBresearch/Lu/Demo/RESTful/tmTool.cgi/" + Bioconcept + "/" + line + "/" + Format + "/")
            val lines = data.flatMap(line => {line.split("\n")}).drop(2)

            // drop unused parts of output
            val words = lines.map(word => word.split("\t").drop(3).dropRight(1).mkString(",")).toArray

            // places word and designation in tuple
            for(i <- 0 until (words.length - 1))
            {
              val splitWord = words(i).split(",")
              val work = (splitWord.head, splitWord.last)
              medWords += work
            } // end loop
          } // end loop

          val medData = sc.parallelize(medWords.toList)
          val flatMed = medData.map(word => (word._1.toLowerCase + ", " + word._2.toLowerCase, 1))
          val medType = medData.map(word => (word._2.toLowerCase, 1))
          val wordMed = medData.map(word => word._1.toLowerCase)
          val medSeq = wordMed.map(word => List(word))

          val tf_idf = TF_IDF(medSeq, sc)

          tf_idf.coalesce(1, shuffle = true).saveAsTextFile("Output/out/" + "outMedTFIDF")

          val outMed = flatMed.reduceByKey(_+_)
          outMed.coalesce(1, shuffle = true).saveAsTextFile("Output/out/" + "outMed")

          val outMedType = medType.reduceByKey(_+_)
          outMedType.coalesce(1, shuffle = true).saveAsTextFile("Output/out/" + "outMedType")

          medWordTotal = flatMed.count()
        } // end if
      }
    })

    // start thread for medical data
    medThread.start()

    val tf_idf = TF_IDF(lemmatizedSeq, sc)
    tf_idf.coalesce(1, shuffle = true).saveAsTextFile("Output/out/" + "outLemmaTFIDF")

    val tf_idf_ngram2 = TF_IDF(ngram2LemmaSeq, sc)
    tf_idf_ngram2.coalesce(1, shuffle = true).saveAsTextFile("Output/out/" + "outNgram2TFIDF")

    val tf_idf_ngram3 = TF_IDF(ngram3LemmaSeq, sc)
    tf_idf_ngram3.coalesce(1, shuffle = true).saveAsTextFile("Output/out/" + "outNgram3TFIDF")

    val wNetCount = wordnetCount.reduceByKey(_+_)
    wNetCount.coalesce(1, shuffle = true).saveAsTextFile("Output/out/" + "outWordNetCount")

    val oPosCount = posCount.reduceByKey(_+_)
    oPosCount.coalesce(1, shuffle = true).saveAsTextFile("Output/out/" + "outPosCount")

    val output = wc.reduceByKey(_+_)
    output.coalesce(1, shuffle = true).saveAsTextFile("Output/out/" + "outCount")

    val outTotals = List(("WordCount", wcTotal), ("NGram2Count", ngram2Total.collect().head._2), ("NGram3Count", ngram3Total.collect().head._2), ("WordNetCount", wordnetCountTotal), ("MedWordCount", medWordTotal))
    sc.parallelize(outTotals).coalesce(1, shuffle = true).saveAsTextFile("Output/out/" + "totals")

    println("WordCount" + wcTotal)

    println("NGram2Count" + ngram2Total.collect().head._2)
    println("NGram3Count" + ngram3Total.collect().head._2)

    println("WordNetCount" + wordnetCountTotal)

    println("MedWordCount" + medWordTotal)
  } // end main

  // generates the NGrams based on the number input
  // reference: https://stackoverflow.com/questions/8258963/how-to-generate-n-grams-in-scala
  def getNGrams(sentence: String, n:Int): IndexedSeq[List[String]] = {
    val words = sentence
    //val ngrams = (for(i <- 2 to n) yield words.split(' ').sliding(i).map(word => word.toList)).flatten
    val ngrams = words.split(' ').sliding(n).map(word => word.toList).toIndexedSeq
    ngrams
  }

  // lemmatizes input string
  // code referenced from https://stackoverflow.com/questions/30222559/simplest-method-for-text-lemmatization-in-scala-and-spark
  def lemmatize(text: String, stopwords : Array[String]): ListBuffer[(String, String)] = {
    val props = new Properties()
    props.setProperty("annotators", "tokenize, ssplit, pos, lemma")
    val pipeline = new StanfordCoreNLP(props)
    val document = new Annotation(text.replaceAll("\\(.*?\\)", "").replaceAll("[',%/]", "").replaceAll("\\s[0-9]+\\s", " "))
    pipeline.annotate(document)
    //val stopwords = List("a", "an", "the", "and", "are", "as", "at", "be", "by", "for", "from", "has", "in", "is",
    //  "it", "its", "of", "on", "that", "to", "was", "were", "will", "with", "''", "``", "-lrb-", "-rrb-", "-lsb-",
    //  "-rsb-")

    val lemmas = ListBuffer.empty[(String, String)]
    val sentences = document.get(classOf[SentencesAnnotation])

    // for each token get the lemma and part of speech
    for (sentence <- sentences; token <- sentence.get(classOf[TokensAnnotation])) {
      val lemma = token.get(classOf[LemmaAnnotation])
      val pos = token.get(classOf[PartOfSpeechAnnotation])

      if (lemma.length > 2 && !stopwords.contains(lemma)) {
        lemmas += ((lemma.toLowerCase, pos.toLowerCase))
      } // end if
    } // end loop
    lemmas
  } // end lemmatize

  // gets data for medical words from URL
  def get(url: String): Iterator[String] = scala.io.Source.fromURL(url).getLines()

  def TF_IDF(data: RDD[List[String]], sc: SparkContext): RDD[(String, Double)] = {
    //Creating an object of HashingTF Class
    val hashingTF = new HashingTF()

    //Creating Term Frequency of the document
    val tf = hashingTF.transform(data)
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

    val documentData = data.flatMap(_.toList)

    val dd = documentData.map(f => {
      val i = hashingTF.indexOf(f)
      val h = mapp.value
      (f, h(i.toString))
    })

    val dd1 = dd.distinct().sortBy(_._2, ascending = false)

    dd1
  } // end TF_IDF
}
