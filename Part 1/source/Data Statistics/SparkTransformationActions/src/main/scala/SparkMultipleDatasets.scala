import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by Mayanka on 01-Sep-16.
  */
object SparkMultipleDatasets {
  def main(args: Array[String]): Unit = {

    System.setProperty("hadoop.home.dir", "D:\\winutils");

    val sparkConf = new SparkConf().setAppName("SparkActions").setMaster("local[*]")

    val sc = new SparkContext(sparkConf)

    val visits = sc.parallelize(Array(("index.html", "1.2.3.4"),("about.html", "3.4.5.6"),("index.html", "1.3.3.1")))
    val pageNames = sc.parallelize(Array(("index.html", "Home"), ("about.html", "About")))
    visits.join(pageNames)
    // ("index.html", ("1.2.3.4", "Home"))
    // ("index.html", ("1.3.3.1", "Home"))
    // ("about.html", ("3.4.5.6", "About"))
    visits.cogroup(pageNames)
    // ("index.html", (Seq("1.2.3.4", "1.3.3.1"), Seq("Home")))
    // ("about.html", (Seq("3.4.5.6"), Seq("About")))

  }
}
