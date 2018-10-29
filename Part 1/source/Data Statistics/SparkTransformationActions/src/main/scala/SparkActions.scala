import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by Mayanka on 01-Sep-16.
  */
object SparkActions {

  def main(args: Array[String]): Unit = {

    System.setProperty("hadoop.home.dir", "D:\\winutils");

    val sparkConf = new SparkConf().setAppName("SparkActions").setMaster("local[*]")

    val sc = new SparkContext(sparkConf)

    val nums = sc.parallelize(Array(1, 2, 3))
    // Retrieve RDD contents as a local collection
    nums.collect() // => [1, 2, 3]
    //Return first K elements
    nums.take(2) // => [1, 2]
    //Count number of elements
    nums.count() // => 3
    //Merge elements with an associative function
    nums.reduce((x, y) => (x + y)) // => 6
    //Write elements to a text file
    nums.saveAsTextFile("file.txt")

  }
}
