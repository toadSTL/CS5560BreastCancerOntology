import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.CoreMap;

import java.util.*;

public class CoreNLP {
    public static Map<String, String> getPOS(String data) {

        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        Annotation doc = new Annotation(data);
        pipeline.annotate(doc);

        List<CoreMap> sentences = doc.get(CoreAnnotations.SentencesAnnotation.class);

        Map<String,String> retPOS = new HashMap<String, String>();


        int i = 0;

        for(CoreMap sentence : sentences) {
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                i++;

                String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                if (lemma.length() > 1) {
                    retPOS.put(lemma, pos);
                }
            }
        }
        System.out.println(retPOS);

        return retPOS;
    }
}
