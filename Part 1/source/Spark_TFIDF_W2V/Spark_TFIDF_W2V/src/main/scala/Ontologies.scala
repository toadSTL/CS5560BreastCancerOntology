import java.net.URLEncoder

import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.JavaConversions._

object Ontologies {

  private val REST_URL = "https://data.bioontology.org"
  private val API_KEY = "ec511abb-8761-41a6-a094-e6f931afa672"
  private val mapper: ObjectMapper = new ObjectMapper


  def main(args: Array[String]): Unit = {

    System.setProperty("hadoop.home.dir","D:\\winutils")

    val sparkConf = new SparkConf().setAppName("SparkWordCount").setMaster("local[*]")
    val sc = new SparkContext(sparkConf)

    val in = sc.textFile("data/justMedWords.txt")

    val medWords = in.flatMap(a =>{
      val r = a.split("\n")
      r
    })

    val medString = medWords.collect().distinct.mkString(" ") // string for ontologies

    val textToAnnotate = URLEncoder.encode(medString, "ISO-8859-1")

    // Get just annotations of data for ontologies
    val urlParameters = "text=" + textToAnnotate
    val annotations: JsonNode = BioportalAPI.jsonToNode(BioportalAPI.get(REST_URL + "/annotator?" + urlParameters))

    println(REST_URL + "/annotator?" + urlParameters)

    // get ontologies
    val ontData = annotations.map(annotation => {
      annotation.get("annotatedClass").get("links").get("self").asText
      //BioportalAPI.slow()
      val cd = BioportalAPI.getClassDetails(annotation.get("annotatedClass").get("links").get("self").asText)
      cd
    }).flatMap(list => list)

    //val ontWords = ontData.flatMap(list => list)
    val ontCount = ontData.toList.map(list => {
      println(list._1)
      (list._1, 1)
    })
    val ontologies = ontData.toList.map(list => {
      println(list._1+", "+list._2)
      (list._1, list._2)
    }).distinct
    sc.parallelize(ontologies).saveAsTextFile("/test/OntOut.txt")

    val outOnt = sc.parallelize(ontCount).reduceByKey(_+_)
    outOnt.saveAsTextFile("/test/OntCount.txt")
  }


}
