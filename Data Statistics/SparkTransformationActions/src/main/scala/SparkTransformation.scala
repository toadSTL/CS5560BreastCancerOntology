import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by Mayanka on 01-Sep-16.
  */
object SparkTransformation {
  def main(args: Array[String]): Unit = {


    System.setProperty("hadoop.home.dir", "D:\\winutils");

    val sparkConf = new SparkConf().setAppName("SparkTransformation").setMaster("local[*]")

    val sc = new SparkContext(sparkConf)

    val nums = sc.parallelize(Array(1, 2, 3))
    // Pass each element through a function
    val squares = nums.map(x => (x * x)) // => {1, 4, 9}

    // Keep elements passing a predicate
    val even = squares.filter(x => x % 2 == 0) // => {4}



    // Map each element to zero or more others
    val result = nums.flatMap(x => Array.range(0, x)) //=> {0, 0, 1, 0, 1, 2}

    result.foreach(println(_))

  }

}
