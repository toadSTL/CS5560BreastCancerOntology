name := "SparkTransformationActions"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "2.3.1" % "provided",
  "org.apache.spark" %% "spark-streaming" % "2.3.1",
  "org.apache.spark" %% "spark-mllib" % "2.3.1",
  "edu.stanford.nlp" % "stanford-corenlp" % "3.9.1",
  "edu.stanford.nlp" % "stanford-corenlp" % "3.9.1" classifier "models",
  "edu.stanford.nlp" % "stanford-parser" % "3.9.1"
)

