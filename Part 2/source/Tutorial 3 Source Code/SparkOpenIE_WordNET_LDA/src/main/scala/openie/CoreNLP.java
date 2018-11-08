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
        List<Sentence> sentences = doc.sentences();
//        System.out.println(sentences);
//        System.out.println(sentences.size());
        int count = 0;
        String acc = "";
        for (Sentence sent : sentences) {  // Will iterate over two sentences
            acc+="[";
            count++;
            //System.out.println("for sentence: " +sent);
            Collection<Quadruple<String, String, String, Double>> s=sent.openie();
//            System.out.println("Sentence: "+sent);
            String temp = "";
            String longest = "";
            String shortest = "";

            String custom = "";

            String pred = "";
            String sub = "";
            String obj = "";

            String shortPredTemp = "";

            double maxValue = 0.0;



            String spSub = "";
            String spObj = "";
            double spVal = 0.0;
            for (int i = 0; i < s.toArray().length ; i++) {
                Object[] tempArr = s.toArray();
                Quadruple<String,String,String,Double> quad = (Quadruple<String, String, String, Double>) tempArr[i];
                temp = tempArr[i].toString();
                //System.out.println(temp);
//                String[] tempFields = temp.replaceAll("\\(", "").replaceAll("\\)", "").split(",");
                sub = quad.first;
                pred = quad.second;
                obj = quad.third;

                if(quad.fourth>=maxValue){
                    maxValue = quad.fourth;
                    if(shortPredTemp.length() == 0){
                        shortPredTemp = pred;
                        spSub = sub;
                        spObj = obj;
                        spVal = quad.fourth;
                        custom = "("+spSub+","+shortPredTemp+","+spObj+","+spVal+")";
                    }
                    if(pred.length() == shortPredTemp.length()){
                        if((sub.length()>spSub.length()&&obj.length()>=spObj.length())||(sub.length()>=spSub.length()&&obj.length()>spObj.length())){
                            shortPredTemp = pred;
                            spSub = sub;
                            spObj = obj;
                            spVal = quad.fourth;
                            custom = "("+spSub+","+shortPredTemp+","+spObj+","+spVal+")";
                        }
                    }
                    if(pred.length() < shortPredTemp.length()){
                        shortPredTemp = pred;
                        spSub = sub;
                        spObj = obj;
                        spVal = quad.fourth;
                        custom = "("+spSub+","+shortPredTemp+","+spObj+","+spVal+")";
                    }

                    if(shortest.length() == 0){
                        shortest = temp;
                    }
                    if(temp.length() < shortest.length()){
                        shortest = temp;
                    }
                    if(temp.length() > longest.length()){
                        longest = temp;
                    }
                }



//                System.out.println("Subject: "+tempFields[0]);
//                System.out.println("Predicate: "+tempFields[1]);
//                System.out.println("Object: "+tempFields[2]);
//                System.out.println("Score: "+tempFields[3]);
//
//                System.out.println("TripletAdded: "+s.toString());
//                acc += tempArr[i].toString()+", ";
            }
//            System.out.println("Shortest Sub: "+ shortSubTemp);
//            System.out.println("Longest Sub: "+longSubTemp);
//            System.out.println("Shortest Pred: "+shortPredTemp);
//            System.out.println("Longest Pred: "+longPredTemp);
//            System.out.println("Shortest obj: "+shortObjTemp);
//            System.out.println("Longest obj: "+longObjTemp);
//            System.out.println("Shortest overall: "+shortest);
//            System.out.println("Custom: ("+spSub+","+shortPredTemp+","+spObj+","+spVal+")");
//            System.out.println("Longest overall: "+longest);

            acc += shortest+", ";
            if(!custom.equals(shortest)){
                acc +=custom+", ";
            }
            if(!longest.equals(custom)){
                acc +=longest;
            }

//            System.out.println(acc.substring(acc.length()-2,acc.length()));
            if(acc.length()>2){
                if(acc.substring(acc.length()-2,acc.length()).equals(", ")){
                    acc = acc.substring(0,acc.length()-2);
                }
            }
            acc+="]";
//            System.out.println("Accumulated Triplets:  \n"+acc);
//            triplets+= s.toString();
//            System.out.println("All Triplets:  \n"+triplets);

        }
        return acc;
//        System.out.println(count);
//        System.out.println("Triplets Returned:  \n"+triplets);
//        return triplets;
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
