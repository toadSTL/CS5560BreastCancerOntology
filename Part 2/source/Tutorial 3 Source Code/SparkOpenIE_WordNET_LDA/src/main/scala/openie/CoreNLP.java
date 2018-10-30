package openie;

import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.util.Quadruple;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Mayanka on 27-Jun-16.
 */
public class CoreNLP {
    public static String returnTriplets(String sentence) {
        //System.out.println("this Happened");
        Document doc = new Document(sentence);
        String triplets="";
        for (Sentence sent : doc.sentences()) {  // Will iterate over two sentences
            //System.out.println("for sentence: " +sent);
            Collection<Quadruple<String, String, String, Double>> l=sent.openie();
            String temp = "";
            String longest = "";
            String shortest = "";

            String pred = "";
            String sub = "";
            String obj = "";

            String shortPredTemp = "";
            String longPredTemp = "";

            String shortSubTemp = "";
            String longSubTemp = "";

            String shortObjTemp = "";
            String longObjTemp = "";

            for (int i = 0; i < l.toArray().length ; i++) {
                Object[] tempArr = l.toArray();
                temp = tempArr[i].toString();
                System.out.println(temp);
                String[] tempFields = temp.replaceAll("\\(", "").replaceAll("\\)", "").split(",");
                sub = tempFields[0];
                pred = tempFields[1];
                obj = tempFields[2];
                System.out.println(tempFields[0]);
                System.out.println(tempFields[1]);
                System.out.println(tempFields[2]);
                System.out.println(tempFields[3]);
                triplets+= l.toString();
                //System.out.println(i);
                System.out.println(l.toString());
                //System.out.println("openie returned: " + lemma);
            }
            //System.out.println(lemma);
        }

        return triplets;
    }

    public static String returnLemma(String sentence) {

        Document doc = new Document(sentence);
        String lemma="";
        for (Sentence sent : doc.sentences()) {  // Will iterate over two sentences
            List<String> l=sent.lemmas();
            for (int i = 0; i < l.size() ; i++) {
                lemma+= l.get(i) +" ";
            }
            //System.out.println(lemma);
        }

        return lemma;
    }

    private static final String[] specialNames = {
            "",
            " thousand",
            " million",
            " billion",
            " trillion",
            " quadrillion",
            " quintillion"
    };

    private static final String[] tensNames = {
            "",
            " ten",
            " twenty",
            " thirty",
            " forty",
            " fifty",
            " sixty",
            " seventy",
            " eighty",
            " ninety"
    };

    private static final String[] numNames = {
            "",
            " one",
            " two",
            " three",
            " four",
            " five",
            " six",
            " seven",
            " eight",
            " nine",
            " ten",
            " eleven",
            " twelve",
            " thirteen",
            " fourteen",
            " fifteen",
            " sixteen",
            " seventeen",
            " eighteen",
            " nineteen"
    };

    private static String convertLessThanOneThousand(double number) {
        String current;
        String ret = "";

        if(number % 1 == 0){
            //it is an integer
            int num = (int) number;
            if (number % 100 < 20){
                current = numNames[num % 100];
                number /= 100;
            }
            else {
                current = numNames[num % 10];
                number /= 10;

                current = tensNames[num % 10] + current;
                number /= 10;
            }
            if (number == 0) return current;
            ret = numNames[num] + " hundred" + current;
        }else{

        }
        return ret;
    }

    public static String numberToWord(int number) {

        if (number == 0) { return "zero"; }

        String prefix = "";

        if (number < 0) {
            number = -number;
            prefix = "negative";
        }

        String current = "";
        int place = 0;

        do {
            int n = number % 1000;
            if (n != 0){
                String s = convertLessThanOneThousand(n);
                current = s + specialNames[place] + current;
            }
            place++;
            number /= 1000;
        } while (number > 0);

        return (prefix + current).trim();
    }

    public static void main(String[] args) {
        System.out.println("*** " + numberToWord(65431));
        System.out.println("*** " + numberToWord(-97164));
    }

    public static boolean isNumeric(String strNum) {
        return strNum.matches("-?\\d+(\\.\\d+)?");
    }

}
