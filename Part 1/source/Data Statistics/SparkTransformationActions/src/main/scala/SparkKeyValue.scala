import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by Mayanka on 01-Sep-16.
  */
object SparkKeyValue {
  def main(args: Array[String]): Unit = {

    System.setProperty("hadoop.home.dir", "D:\\winutils");

    val sparkConf = new SparkConf().setAppName("SparkActions").setMaster("local[*]")

    val sc = new SparkContext(sparkConf)

    val pets = sc.parallelize(Array(("cat", 1), ("dog", 1), ("cat", 2)))

    pets.reduceByKey((x, y) => x + y) // => {(cat, 3), (dog, 1)}
    pets.groupByKey() // => {(cat, Seq(1, 2)), (dog, Seq(1)}
    pets.sortByKey() // => {(cat, 1), (cat, 2), (dog, 1)}

  }

}
