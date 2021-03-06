package Queries;
import java.util.*;
import TextProcessing.*;

public final class QueryProcessor {
    private static String query;
    private QueryProcessor() { query = ""; }

    public static void setQuery(String q){
        query = q;
    }

    public static String getQuery(){
        return query;
    }

    private static char[] stringToChar(String str) {
        char[] ch = new char[str.length()];
        for (int i = 0; i < str.length(); i++) {
            ch[i] = str.charAt(i);
        }
        return ch;
    }

    public static ArrayList<String> process(){
        String[] temp = query.split("\\s+");
        List<String> stopWords = StopWords.getStopWords();

        ArrayList<String> queryProcessed = new ArrayList<String>();
        Stemmer stemmer = new Stemmer();
        boolean phrase = false;
        String phraseWord = "";
        for (String s : temp) {
            if (s.startsWith("\"")){
                phrase = true;
                phraseWord = s.replaceAll("[^a-zA-Z0-9 ]", "");
                if(s.endsWith("\"")) {
                	phrase = false;
                    queryProcessed.add(phraseWord);
                    phraseWord = "";
                }
                continue;
            }
            if (phrase) {
                phraseWord = phraseWord + " " + s.replaceAll("[^a-zA-Z0-9 ]", "");
                if (s.endsWith("\"")){
                    phrase = false;
                    queryProcessed.add(phraseWord);
                    phraseWord = "";
                }
            }
            else if (!stopWords.contains(s.replaceAll("[^a-zA-Z0-9 ]", "").toLowerCase())) {
                s = s.toLowerCase();
                s = s.replaceAll("[^a-zA-Z0-9 ]", "");
                stemmer.add(stringToChar(s), s.length());
                stemmer.stem();
                queryProcessed.add(stemmer.toString());
            }
        }
        return queryProcessed;
    }

/*  FOR TESTING
    public static void main(String[] Args)
    {
        String query = "Hello man Swapping \"My Name IS Body\" my \"games is not easier.\" man.";
        QueryProcessor.setQuery(query);
        ArrayList<String> processed = QueryProcessor.process();
    }

 */
}
