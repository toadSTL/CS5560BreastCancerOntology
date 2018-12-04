/**
 * Author: Gregory Brown
 * E-mail: gbkhv@mail.umkc.edu
 * G-mail: gregory.dylan.brown@gmail.com
 */
import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.util.Quadruple;

import java.util.Collection;
import java.util.List;


public class CoreNLP {



    public static String returnTriplets(String text) {
        String acc = "";
        Document doc = new Document(text);
        List<Sentence> sentences = doc.sentences();
        for (Sentence sent : sentences) {
            //Get Collection of <Sub, Pred, Obj, Score> per Sentence 'sent' in sentences
            Collection<Quadruple<String, String, String, Double>> s=sent.openie();

            acc+="["; //Returned string should begin with '['

            //Intialize variables to be used in determining which triplets to return
            String trip = "", longT = "", shortT = "", custT = "";
            String sub = "", pred = "", obj = "";
            String custSub = "", custPred = "", custObj = "";
            double maxValue = 0.0, spVal = 0.0;

            Object[] arr = s.toArray();
            //For each quad in arr...
            for(int i = 0; i < arr.length ; i++){
                Quadruple<String,String,String,Double> quad = (Quadruple<String, String, String, Double>) arr[i];
                //Remove commas from <s,p,o> strings (otherwise cannot use as delimiters)
                quad.first = quad.first.replaceAll(",","");
                quad.second = quad.second.replaceAll(",","");
                quad.third = quad.third.replaceAll(",","");

                trip = quad.toString();

                sub = quad.first.replaceAll(",", "");
                pred = quad.second.replaceAll(",", "");
                obj = quad.third.replaceAll(",", "");

                if(quad.fourth>=maxValue){
                    maxValue = quad.fourth;
                    if(custPred.length() == 0){
                        custPred = pred;
                        custSub = sub;
                        custObj = obj;
                        spVal = quad.fourth;
                        custT = "("+custSub+","+custPred+","+custObj+","+spVal+")";
                    }
                    if(pred.length() == custPred.length()){
                        if((sub.length()>custSub.length()&&obj.length()>=custObj.length())||(sub.length()>=custSub.length()&&obj.length()>custObj.length())){
                            custPred = pred;
                            custSub = sub;
                            custObj = obj;
                            spVal = quad.fourth;
                            custT = "("+custSub+","+custPred+","+custObj+","+spVal+")";
                        }
                    }
                    if(pred.length() < custPred.length()){
                        custPred = pred;
                        custSub = sub;
                        custObj = obj;
                        spVal = quad.fourth;
                        custT = "("+custSub+","+custPred+","+custObj+","+spVal+")";
                    }

                    if(shortT.length() == 0){
                        shortT = trip;
                    }
                    if(trip.length() < shortT.length()){
                        shortT = trip;
                    }
                    if(trip.length() > longT.length()){
                        longT = trip;
                    }
                }

            }
            acc += shortT+", ";
            if(!custT.equals(shortT)){
                acc +=custT+", ";
            }
            if(!longT.equals(custT)){
                acc +=longT;
            }

//            System.out.println(acc.substring(acc.length()-2,acc.length()));
            if(acc.length()>2){
                if(acc.substring(acc.length()-2,acc.length()).equals(", ")){
                    acc = acc.substring(0,acc.length()-2);
                }
            }
            acc+="]";
        }
        return acc;
    }// End returnTriplets()
}