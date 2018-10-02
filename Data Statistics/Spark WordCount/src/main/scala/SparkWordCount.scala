import org.apache.spark.{SparkConf, SparkContext}
import java.io.{File, PrintWriter}

object SparkWordCount {

  def main(args: Array[String]) {

    System.setProperty("hadoop.home.dir","D:\\winutils")

    val sparkConf = new SparkConf().setAppName("SparkWordCount").setMaster("local[*]").set("spark.executor.memory","4g")

    val sc=new SparkContext(sparkConf)

    //Input : Abstract Folder
    //Outut : Word Count of all the abstracts
    val inputf = sc.wholeTextFiles("data/abstracts", 4)

    //example on how to refer within wholeTextFiles
    //inputf.map(abs=>{
      //abs._1
      //abs._2
    //})

    val wc = inputf.flatMap(abs=>{
      abs._2.split(" ")
    }).map(word=> {
      if ((word.length() > 0) && (word.charAt(word.length() - 1) == '.')) {
        word.dropRight(1)
      } else {
        word
      }
      //word.replaceAll("\\.", "")
    }).map(word => {
      word.replaceAll(",","")
    }).map(word => {
      word.replaceAll("\\(","")
    }).map(word => {
      word.replaceAll("\\)","")
    }).map(word => {
      word.replaceAll(":","")
    }).map(word=>(word,1)).cache()

    //Input RDD[String] output RDD[(Sring,Int)]

    //val wc=input.flatMap(line=>{line.split(" ")}).map(word=>(word,1)).cache() //_.split will work as well

    val output=wc.reduceByKey(_+_)

    output.saveAsTextFile("output/WordCount")

    val o=output.collect()

    var s:String="Words\tCount \n"
    var c = 0
    o.foreach{case(word,count)=>{
      s+=word+"\t"+count+"\n"
      c+=count.toInt
    }}

    print(s)
    print("Total words: " + c + "\n")

    val pw = new PrintWriter(new File("output/wcTotal.txt"))
    pw.write("Total Words:\t"+c+"\n")
    pw.write(s)
    pw.close()

  }

}
