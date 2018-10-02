object NamedEntityRecognizer {

  def main(args: Array[String]): Unit = {
    val props: Properties = new Properties()
    props.put("annotators", "tokenize, ssplit, pos, lemma, ner")

    System.setProperty("hadoop.home.dir","D:\\winutils");

    val pipeline: StanfordCoreNLP = new StanfordCoreNLP(props)

    //  some text from a file
    //val inputFile: File = new File("src/test/resources/sample-content.txt")
    //val text: String = Files.toString(inputFile, Charset.forName("UTF-8"))
    val text = "This is some sample basic text"


    val sparkConf = new SparkConf().setAppName("SparkPOSCount").setMaster("local[*]").set("spark.executor.memory","4g")
    val sc=new SparkContext(sparkConf)

    val input = sc.textFile("abstractsSingleLine/1.txt", 4)
    //  blank annotator

    val document: Annotation = new Annotation(text)

    val posC = input.map(a=>{
      val doc: Annotation = new Annotation(a)
      pipeline.annotate(doc)
      val sen: List[CoreMap] = doc.get(classOf[SentencesAnnotation]).asScala.toList

      (for {
        sentence: CoreMap <- sen
        token: CoreLabel <- sentence.get(classOf[TokensAnnotation]).asScala.toList
        word: String = token.get(classOf[TextAnnotation])
        pos: String = token.get(classOf[PartOfSpeechAnnotation])
        lemma: String = token.get(classOf[LemmaAnnotation])
        ner: String = token.get(classOf[NamedEntityTagAnnotation])


      } yield (token, word, pos, lemma, ner)) foreach (t => println("token: " + t._1 + " word: " + t._2 + " pos: " + t._3 + " lemma: " + t._4 + " ner: " + t._5 ))


    })
    // running all Annotator - Tokenizer on this text
    pipeline.annotate(document)


    //posC.saveAsTextFile("test.txt")

    val sentences: List[CoreMap] = document.get(classOf[SentencesAnnotation]).asScala.toList

    (for {
      sentence: CoreMap <- sentences
      token: CoreLabel <- sentence.get(classOf[TokensAnnotation]).asScala.toList
      word: String = token.get(classOf[TextAnnotation])
      pos: String = token.get(classOf[PartOfSpeechAnnotation])
      lemma: String = token.get(classOf[LemmaAnnotation])
      ner: String = token.get(classOf[NamedEntityTagAnnotation])


    } yield (token, word, pos, lemma, ner)) foreach (t => println("token: " + t._1 + " word: " + t._2 + " pos: " + t._3 + " lemma: " + t._4 + " ner: " + t._5 ))


  }

}
