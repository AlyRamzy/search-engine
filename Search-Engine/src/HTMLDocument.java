import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HTMLDocument {

	private int documnetID;
	private List<String> terms;

	public HTMLDocument(int docID , String docIP) {
		terms = new ArrayList<String>();
		try {
			Document doc = Jsoup.connect("https://en.wikipedia.org/wiki/People").get();
			String docText = doc.text();
			setTerms(docText);
		} catch (IOException e) {
			/* Website refuse to connect */
			e.printStackTrace();
		}
		//this.terms = docIP.toString();
		this.documnetID = docID;
	}

	private void setTerms(String text){
		/* Remove any non alphanumeric charachter */
		text = text.replaceAll("[^a-zA-Z0-9 | \" *\"]", ""); // double quotes
		text = text.toLowerCase();
		//System.out.println(text);
		String[] temp = text.split(" "); // any number of spaces
		List<String> stopWords = StopWords.getStopWords();

		/* Stem words */
		Stemmer stemmer = new Stemmer();
		for (int i = 0 ; i < temp.length ; i++) {
			/* Remove stop words */
			if(!stopWords.contains(temp[i])) {
				stemmer.add(stringToChar(temp[i]), temp[i].length());
				stemmer.stem();
				terms.add(stemmer.toString());
				// stemmer.reset();
			}
		}
		//System.out.println(terms);
	}

	private char[] stringToChar(String str) {
		char[] ch = new char[str.length()];
		for (int i = 0; i < str.length(); i++) {
			ch[i] = str.charAt(i);
		}
		return ch;
	}

	public List<String> getTerms() {
		return terms;
	}

	public int getDocID() {
		return documnetID;
	}

}