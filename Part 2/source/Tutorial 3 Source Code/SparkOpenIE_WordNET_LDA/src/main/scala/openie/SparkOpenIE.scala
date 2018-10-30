package openie

import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkConf, SparkContext}
import java.io._
import scala.io.Source
import scala.collection.mutable.ListBuffer
/**
  * Created by Mayanka on 27-Jun-16.
  */
object SparkOpenIE {

  def main(args: Array[String]) {
    // Configuration
    System.setProperty("hadoop.home.dir", "D:\\winutils")

    val sparkConf = new SparkConf().setAppName("SparkWordCount").setMaster("local[*]")

    val sc = new SparkContext(sparkConf)

    // For Windows Users



    // Turn off Info Logger for Console
    Logger.getLogger("org").setLevel(Level.OFF)
    Logger.getLogger("akka").setLevel(Level.OFF)

    val input = sc.wholeTextFiles("data")

    val sentances = input.flatMap(doc => {
      val sen = doc._2.split("\\. ")
      sen
    })

    val triplets = input.flatMap(abs => {
      val t = CoreNLP.returnTriplets(abs._2)
      //println(t)
      //val c = t.replaceAll("\\[\\(|\\)\\]", "")
      //println(t)

      val a = t.split("\\), \\(|\\)\\]\\[\\(")
      //a.foreach(println)
      a
    }).map(trip => {
      val c = trip.replaceAll("\\)\\]", "").replaceAll("\\[\\(", "").replaceAll("%", "percecnt")
      val t = c.split(",")
      //if(CoreNLP.isNumeric(t(0))){
      //  t(0) = CoreNLP.numberToWord(t(0).toInt)
      //}
      (t(0),t(1),t(2))
      //t
    }).filter(trip => {
      (!trip._1.contains("\\d+"))&&(!trip._2.contains("\\d+"))&&(!trip._3.contains("\\d+"))&&(!trip._1.contains("[0-9]+"))&&(!trip._2.contains("[0-9]+"))&&(!trip._3.contains("[0-9]+"))
    })

    //println(sentances.collect().mkString("\n"))

   // val ret = sentances.flatMap(line => {
  //    val t = CoreNLP.returnTriplets(line)
      //println(t)
 //     val c = t.replaceAll("\\[\\(|\\)\\]", "")
//
     // val a = c.split("\\), \\(")
    //  //a.foreach(println)
    //  a
    //})

    //val triplets = ret.map(triplet => {
    //  val arr = triplet.split("")
    //})

    val subjects = triplets.map(t => t._1).distinct()
    val predicates = triplets.map(t => t._2).distinct()
    val objects = triplets.map(t => t._3).distinct()

    val predOut = predicates.map(s => prepString(s))

    val subOut = subjects.map(s => prepString(s))

    val objOut = objects.map(s => prepString(s))

    val tripletsOut = triplets.distinct().map( s => {
      val ret = prepString(s._1) + "," + prepString(s._2) + "," + prepString(s._3) + ",Obj"
      ret
    })

    val pwPred = new PrintWriter(new File("output\\Predicates.txt"))
    pwPred.write(predOut.collect().mkString("\n"))
    pwPred.close

    val pwSub = new PrintWriter(new File("output\\Subjects.txt"))
    pwSub.write(subOut.collect().mkString("\n"))
    pwSub.close

    val pwObj = new PrintWriter(new File("output\\Objects.txt"))
    pwObj.write(objOut.collect().mkString("\n"))
    pwObj.close

    val pwTrip = new PrintWriter(new File("output\\Triplets.txt"))
    pwTrip.write(tripletsOut.collect().mkString("\n"))
    pwTrip.close

    if (args.length < 2) {
      System.out.println("\n$ java RESTClientGet [Bioconcept] [Inputfile] [Format]")
      System.out.println("\nBioconcept: We support five kinds of bioconcepts, i.e., Gene, Disease, Chemical, Species, Mutation. When 'BioConcept' is used, all five are included.\n\tInputfile: a file with a pmid list\n\tFormat: PubTator (tab-delimited text file), BioC (xml), and JSON\n\n")
    }
    else {
      val Bioconcept = args(0)
      val Inputfile = args(1)
      var Format = "PubTator"
      if (args.length > 2) {
        Format = args(2)
      }
      val medWords = ListBuffer.empty[(String, String)]

      for (line <- Source.fromFile(Inputfile).getLines) {
        val data = scala.io.Source.fromURL("https://www.ncbi.nlm.nih.gov/CBBresearch/Lu/Demo/RESTful/tmTool.cgi/" + Bioconcept + "/" + line + "/" + Format + "/").getLines()

        val lines = data.flatMap(line => {
          line.split("\n")
        }).drop(2)
        //lines.foreach{l =>print(l+"\n")}

        val words = lines.flatMap(word => {
          word.split("\t").drop(3).dropRight(1)
        }).toArray
        //words.foreach{w =>print(w+"\n")}
        for (i <- 0 until words.length by 2) {
          if (i < words.length - 1) {
            val work = (words(i), words(i + 1))
            medWords += work
          }
        }
      }
      val mw = sc.parallelize(medWords.toList).distinct()

      val disW = mw.filter(w => w._2.equals("Disease")).map(w => w._1).collect.toSet
      val specW = mw.filter(w => w._2.equals("Species")).map(w => w._1).collect.toSet
      val geneW = mw.filter(w => w._2.equals("Gene")).map(w => w._1).collect.toSet
      val chemW = mw.filter(w => w._2.equals("Chemical")).map(w => w._1).collect.toSet
      val mutW = mw.filter(w => w._2.equals("Mutation")).map(w => w._1).collect.toSet

      val subDis = subjects.filter(s => {
        disW.contains(s)
      }).map(s => "Disease,"+prepString(s))
      val subSpec = subjects.filter(s => {
        specW.contains(s)
      }).map(s => "Species,"+prepString(s))
      val subGene = subjects.filter(s => {
        geneW.contains(s)
      }).map(s => "Gene,"+prepString(s))
      val subChem = subjects.filter(s => {
        chemW.contains(s)
      }).map(s => "Chemical,"+prepString(s))
      val subMut = subjects.filter(s => {
        mutW.contains(s)
      }).map(s => "Mutation,"+prepString(s))
      val subOth = subjects.filter(s => (!disW.contains(s))&&(!specW.contains(s))&&(!geneW.contains(s))&&(!chemW.contains(s))&&(!mutW.contains(s))).map(s => "Other,"+prepString(s))

      val pwSubDis = new PrintWriter(new File("output\\SubjectDisease.txt"))
      pwSubDis.write(subDis.collect().mkString("\n"))
      pwSubDis.close

      val pwSubSpec = new PrintWriter(new File("output\\SubjectSpecies.txt"))
      pwSubSpec.write(subSpec.collect().mkString("\n"))
      pwSubSpec.close

      val pwSubChem = new PrintWriter(new File("output\\SubjectChemical.txt"))
      pwSubChem.write(subChem.collect().mkString("\n"))
      pwSubChem.close

      val pwSubGene = new PrintWriter(new File("output\\SubjectGene.txt"))
      pwSubGene.write(subGene.collect().mkString("\n"))
      pwSubGene.close

      val pwSubMut = new PrintWriter(new File("output\\SubjectMutation.txt"))
      pwSubMut.write(subMut.collect().mkString("\n"))
      pwSubMut.close

      val pwSubOth = new PrintWriter(new File("output\\SubjectOther.txt"))
      pwSubOth.write(subOth.collect().mkString("\n"))
      pwSubOth.close

      val objDis = objects.filter(s => {
        disW.contains(s)
      }).map(s => "Disease,"+prepString(s))
      val objSpec = objects.filter(s => {
        specW.contains(s)
      }).map(s => "Species,"+prepString(s))
      val objGene = objects.filter(s => {
        geneW.contains(s)
      }).map(s => "Gene,"+prepString(s))
      val objChem = objects.filter(s => {
        chemW.contains(s)
      }).map(s => "Chemical,"+prepString(s))
      val objMut = objects.filter(s => {
        mutW.contains(s)
      }).map(s => "Mutation,"+prepString(s))
      val objOth = objects.filter(s => (!disW.contains(s))&&(!specW.contains(s))&&(!geneW.contains(s))&&(!chemW.contains(s))&&(!mutW.contains(s))).map(s => "Other,"+prepString(s))

      val pwObjDis = new PrintWriter(new File("output\\objectDisease.txt"))
      pwObjDis.write(objDis.collect().mkString("\n"))
      pwObjDis.close

      val pwObjSpec = new PrintWriter(new File("output\\objectSpecies.txt"))
      pwObjSpec.write(objSpec.collect().mkString("\n"))
      pwObjSpec.close

      val pwObjChem = new PrintWriter(new File("output\\objectChemical.txt"))
      pwObjChem.write(objChem.collect().mkString("\n"))
      pwObjChem.close

      val pwObjGene = new PrintWriter(new File("output\\objectGene.txt"))
      pwObjGene.write(objGene.collect().mkString("\n"))
      pwObjGene.close

      val pwObjMut = new PrintWriter(new File("output\\objectMutation.txt"))
      pwObjMut.write(objMut.collect().mkString("\n"))
      pwObjMut.close

      val pwObjOth = new PrintWriter(new File("output\\objectOther.txt"))
      pwObjOth.write(objOth.collect().mkString("\n"))
      pwObjOth.close


      val trips = triplets.distinct()

      val predDisDis = trips.filter(s => {
        disW.contains(s._1)&&disW.contains(s._3)
      }).map(s => prepString(s._2)+",Disease,Disease,Func");
      val predDisSpec = trips.filter(s => {
        disW.contains(s._1)&&specW.contains(s._3)
      }).map(s => prepString(s._2)+",Disease,Species,Func");
      val predDisGene = trips.filter(s => {
        disW.contains(s._1)&&geneW.contains(s._3)
      }).map(s => prepString(s._2)+",Disease,Gene,Func");
      val predDisChem = trips.filter(s => {
        disW.contains(s._1)&&chemW.contains(s._3)
      }).map(s => prepString(s._2)+",Disease,Chemical,Func");
      val predDisMut = trips.filter(s => {
        disW.contains(s._1)&&mutW.contains(s._3)
      }).map(s => prepString(s._2)+",Disease,Mutation,Func");
      val predDisOth = trips.filter(s => {
        disW.contains(s._1)&&(!disW.contains(s._3))&&(!specW.contains(s._3))&&(!geneW.contains(s._3))&&(!chemW.contains(s._3))&&(!mutW.contains(s._3))
      }).map(s => prepString(s._2)+",Disease,Other,Func");

      val predSpecSpec = trips.filter(s => {
        specW.contains(s._1)&&specW.contains(s._3)
      }).map(s => prepString(s._2)+",Species,Species,Func");
      val predSpecDis = trips.filter(s => {
        specW.contains(s._1)&&disW.contains(s._3)
      }).map(s => prepString(s._2)+",Species,Disease,Func");
      val predSpecGene = trips.filter(s => {
        specW.contains(s._1)&&geneW.contains(s._3)
      }).map(s => prepString(s._2)+",Species,Gene,Func");
      val predSpecChem = trips.filter(s => {
        specW.contains(s._1)&&chemW.contains(s._3)
      }).map(s => prepString(s._2)+",Species,Chemical,Func");
      val predSpecMut = trips.filter(s => {
        specW.contains(s._1)&&mutW.contains(s._3)
      }).map(s => prepString(s._2)+",Species,Mutation,Func");
      val predSpecOth = trips.filter(s => {
        specW.contains(s._1)&&(!disW.contains(s._3))&&(!specW.contains(s._3))&&(!geneW.contains(s._3))&&(!chemW.contains(s._3))&&(!mutW.contains(s._3))
      }).map(s => prepString(s._2)+",Species,Other,Func");


      val predGeneGene = trips.filter(s => {
        geneW.contains(s._1)&&geneW.contains(s._3)
      }).map(s => prepString(s._2)+",Gene,Gene,Func");
      val predGeneDis = trips.filter(s => {
        geneW.contains(s._1)&&disW.contains(s._3)
      }).map(s => prepString(s._2)+",Gene,Disease,Func");
      val predGeneSpec = trips.filter(s => {
        geneW.contains(s._1)&&specW.contains(s._3)
      }).map(s => prepString(s._2)+",Gene,Species,Func");
      val predGeneChem = trips.filter(s => {
        geneW.contains(s._1)&&chemW.contains(s._3)
      }).map(s => prepString(s._2)+",Gene,Chemical,Func");
      val predGeneMut = trips.filter(s => {
        geneW.contains(s._1)&&mutW.contains(s._3)
      }).map(s => prepString(s._2)+",Gene,Mutation,Func");
      val predGeneOth = trips.filter(s => {
        geneW.contains(s._1)&&(!disW.contains(s._3))&&(!specW.contains(s._3))&&(!geneW.contains(s._3))&&(!chemW.contains(s._3))&&(!mutW.contains(s._3))
      }).map(s => prepString(s._2)+",Gene,Other,Func");

      val predChemChem = trips.filter(s => {
        chemW.contains(s._1)&&chemW.contains(s._3)
      }).map(s => prepString(s._2)+",Chemical,Chemical,Func");
      val predChemDis = trips.filter(s => {
        chemW.contains(s._1)&&disW.contains(s._3)
      }).map(s => prepString(s._2)+",Chemical,Disease,Func");
      val predChemSpec = trips.filter(s => {
        chemW.contains(s._1)&&specW.contains(s._3)
      }).map(s => prepString(s._2)+",Chemical,Species,Func");
      val predChemGene = trips.filter(s => {
        chemW.contains(s._1)&&geneW.contains(s._3)
      }).map(s => prepString(s._2)+",Chemical,Gene,Func");
      val predChemMut = trips.filter(s => {
        chemW.contains(s._1)&&mutW.contains(s._3)
      }).map(s => prepString(s._2)+",Chemical,Mutation,Func");
      val predChemOth = trips.filter(s => {
        chemW.contains(s._1)&&(!disW.contains(s._3))&&(!specW.contains(s._3))&&(!geneW.contains(s._3))&&(!chemW.contains(s._3))&&(!mutW.contains(s._3))
      }).map(s => prepString(s._2)+",Chemical,Other,Func");

      val predMutMut = trips.filter(s => {
        mutW.contains(s._1)&&mutW.contains(s._3)
      }).map(s => prepString(s._2)+",Mutation,Mutation,Func");
      val predMutDis = trips.filter(s => {
        mutW.contains(s._1)&&disW.contains(s._3)
      }).map(s => prepString(s._2)+",Mutation,Disease,Func");
      val predMutSpec = trips.filter(s => {
        mutW.contains(s._1)&&specW.contains(s._3)
      }).map(s => prepString(s._2)+",Mutation,Species,Func");
      val predMutGene = trips.filter(s => {
        mutW.contains(s._1)&&geneW.contains(s._3)
      }).map(s => prepString(s._2)+",Mutation,Gene,Func");
      val predMutChem = trips.filter(s => {
        mutW.contains(s._1)&&chemW.contains(s._3)
      }).map(s => prepString(s._2)+",Mutation,Chemical,Func");
      val predMutOth = trips.filter(s => {
        mutW.contains(s._1)&&(!disW.contains(s._3))&&(!specW.contains(s._3))&&(!geneW.contains(s._3))&&(!chemW.contains(s._3))&&(!mutW.contains(s._3))
      }).map(s => prepString(s._2)+",Mutation,Other,Func");


      val predOthDis = trips.filter(s => {
        (!disW.contains(s._1))&&(!specW.contains(s._1))&&(!geneW.contains(s._1))&&(!chemW.contains(s._1))&&(!mutW.contains(s._1))&&disW.contains(s._3)
      }).map(s => prepString(s._2)+",Other,Disease,Func");
      val predOthSpec = trips.filter(s => {
        (!disW.contains(s._1))&&(!specW.contains(s._1))&&(!geneW.contains(s._1))&&(!chemW.contains(s._1))&&(!mutW.contains(s._1))&&specW.contains(s._3)
      }).map(s => prepString(s._2)+",Other,Species,Func");
      val predOthGene = trips.filter(s => {
        (!disW.contains(s._1))&&(!specW.contains(s._1))&&(!geneW.contains(s._1))&&(!chemW.contains(s._1))&&(!mutW.contains(s._1))&&geneW.contains(s._3)
      }).map(s => prepString(s._2)+",Other,Gene,Func");
      val predOthChem = trips.filter(s => {
        (!disW.contains(s._1))&&(!specW.contains(s._1))&&(!geneW.contains(s._1))&&(!chemW.contains(s._1))&&(!mutW.contains(s._1))&&chemW.contains(s._3)
      }).map(s => prepString(s._2)+",Other,Chemical,Func");
      val predOthMut = trips.filter(s => {
        (!disW.contains(s._1))&&(!specW.contains(s._1))&&(!geneW.contains(s._1))&&(!chemW.contains(s._1))&&(!mutW.contains(s._1))&&mutW.contains(s._3)
      }).map(s => prepString(s._2)+",Other,Mutation,Func");
      val predOthOth = trips.filter(s => {
        (!disW.contains(s._1))&&(!specW.contains(s._1))&&(!geneW.contains(s._1))&&(!chemW.contains(s._1))&&(!mutW.contains(s._1))&&(!disW.contains(s._3))&&(!specW.contains(s._3))&&(!geneW.contains(s._3))&&(!chemW.contains(s._3))&&(!mutW.contains(s._3))
      }).map(s => prepString(s._2)+",Other,Other,Func");


      val pwPredDis = new PrintWriter(new File("output\\predDis.txt"))
      pwPredDis.write(predDisDis.distinct().collect().mkString("\n"))
      pwPredDis.write(predDisSpec.distinct().collect().mkString("\n"))
      pwPredDis.write(predDisGene.distinct().collect().mkString("\n"))
      pwPredDis.write(predDisChem.distinct().collect().mkString("\n"))
      pwPredDis.write(predDisMut.distinct().collect().mkString("\n"))
      pwPredDis.write(predDisOth.distinct().collect().mkString("\n"))
      pwPredDis.close

      val pwPredSpec = new PrintWriter(new File("output\\predSpec.txt"))
      pwPredSpec.write(predSpecDis.distinct().collect().mkString("\n")) 
      pwPredSpec.write(predSpecSpec.distinct().collect().mkString("\n"))
      pwPredSpec.write(predSpecGene.distinct().collect().mkString("\n"))
      pwPredSpec.write(predSpecChem.distinct().collect().mkString("\n"))
      pwPredSpec.write(predSpecMut.distinct().collect().mkString("\n"))
      pwPredSpec.write(predSpecOth.distinct().collect().mkString("\n"))
      pwPredSpec.close

      val pwPredGene = new PrintWriter(new File("output\\predGene.txt"))
      pwPredGene.write(predGeneDis.distinct().collect().mkString("\n"))
      pwPredGene.write(predGeneSpec.distinct().collect().mkString("\n"))
      pwPredGene.write(predGeneGene.distinct().collect().mkString("\n"))
      pwPredGene.write(predGeneChem.distinct().collect().mkString("\n"))
      pwPredGene.write(predGeneMut.distinct().collect().mkString("\n"))
      pwPredGene.write(predGeneOth.distinct().collect().mkString("\n"))
      pwPredGene.close

      val pwPredChem = new PrintWriter(new File("output\\predChem.txt"))
      pwPredChem.write(predChemDis.distinct().collect().mkString("\n"))
      pwPredChem.write(predChemSpec.distinct().collect().mkString("\n"))
      pwPredChem.write(predChemGene.distinct().collect().mkString("\n"))
      pwPredChem.write(predChemChem.distinct().collect().mkString("\n"))
      pwPredChem.write(predChemMut.distinct().collect().mkString("\n"))
      pwPredChem.write(predChemOth.distinct().collect().mkString("\n"))
      pwPredChem.close

      val pwPredMut = new PrintWriter(new File("output\\predMut.txt"))
      pwPredMut.write(predMutDis.distinct().collect().mkString("\n"))
      pwPredMut.write(predMutSpec.distinct().collect().mkString("\n"))
      pwPredMut.write(predMutGene.distinct().collect().mkString("\n"))
      pwPredMut.write(predMutChem.distinct().collect().mkString("\n"))
      pwPredMut.write(predMutMut.distinct().collect().mkString("\n"))
      pwPredMut.write(predMutOth.distinct().collect().mkString("\n"))
      pwPredMut.close

      val pwPredOth = new PrintWriter(new File("output\\predOth.txt"))
      pwPredOth.write(predOthDis.distinct().collect().mkString("\n"))
      pwPredOth.write(predOthSpec.distinct().collect().mkString("\n"))
      pwPredOth.write(predOthGene.distinct().collect().mkString("\n"))
      pwPredOth.write(predOthChem.distinct().collect().mkString("\n"))
      pwPredOth.write(predOthMut.distinct().collect().mkString("\n"))
      pwPredOth.write(predOthOth.distinct().collect().mkString("\n"))
      pwPredOth.close




    }



    //      //\w{1,2}\b --> do we really want to remove all words with 1 or 2 characters?
  }

  def prepString(s: String): String = {
    var temp = s
    if(s.contains(" ")) {
      val words = s.toLowerCase().split(" ")
      for(i <- 1 until words.length)
      {
        words(i) = words(i).capitalize
      }
      temp = words.mkString("").replaceAll("[.]", "")
    }
    temp
  }


}
