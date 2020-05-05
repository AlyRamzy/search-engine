import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;


public class IndexerThread implements Runnable {
	
	// Each Thread must write the inverted File to the DB when Reaching certain Memory Limit
	private final int MEMORY_LIMIT = 640000;
	
	/* Inverted File Dictionaries */
	private Map <String, List<Integer>> termDictionary;
	private Map<String, Map<Integer, List<Integer>>> termDocumentDictionary;
	
	/* The Documents that this Thread should Process */
	private Map.Entry<Integer, String>[] documentsURLs;
	private int docStartIndex;
	private int docEndIndex;

	/* Constructor */
	public IndexerThread(Map.Entry<Integer, String>[] docsURLs, int docStIdx, int docEndIdx ){

		this.docStartIndex = docStIdx;
		this.docEndIndex = docEndIdx;
		this.documentsURLs = docsURLs;
		
		this.termDictionary = new LinkedHashMap<String, List<Integer>>();
		this.termDocumentDictionary = new LinkedHashMap<String, Map<Integer, List<Integer>>>();

	}
	
	public void run() {
		this.constructIndex();
	}

	/* The Main Function To Loop Through documents and Construct the Index */
	public void constructIndex() {
		
		/* The Processed Document must be set in this Variable */
		HTMLDocument document; 
		
		/* Calculate The Free Memory before start Processing */ 
		int FreeMemory = (int) java.lang.Runtime.getRuntime().freeMemory();
		int consumedMemory =  0;
		
		/* Iterate Through Portion of The Documents that Assigned to that Thread */
		for(int i = docStartIndex ;i < docEndIndex; i++) 
		{
			/* Calculate The Free Memory after Processing Each Document To Check that It isn't exceeded the Limit */ 
			int currentMemory = (int) java.lang.Runtime.getRuntime().freeMemory();
			consumedMemory = FreeMemory - currentMemory;
			
			/* Check if The Inverted File Exceeded The Memory Limit */
			if (consumedMemory < this.MEMORY_LIMIT)
			{
				/* Write The Inverted File to the DB, Remove It from Memory then Continue To Process Documents */ 
				SortIndex();
				StoreDictonaries();
				
				termDictionary.clear();
				termDocumentDictionary.clear();
			}
			
			/* The Processed Document ID with URL */
			Map.Entry<Integer, String> documentURL = documentsURLs[i];
			
			/* Invoke HTMLDocument constrictor to tokenize html  */
			document = new HTMLDocument(i , documentURL.getValue());
						
			/* The Processed Document ID with Its Terms */
			List<String> terms = document.getTerms();
			int documentID = document.getDocID();
			
			/* Variable Used To Track Each Term Position in the Document */
			int termPosition = 0;
			
			/* Loop Through All Terms in The File */
			for (String term : terms) 
			{
				List<Integer> termDocumentsIDs = null;
				List<Integer> termDocumentPositions= null;

				/* Check If This Term is already appeared in other Document */
				if (termDictionary.get(term) == null) 
				{
					/* Make a New List For This Term */
					termDocumentsIDs = new ArrayList<Integer>();
					termDictionary.put(term, termDocumentsIDs);
				}
				else 
					/* Get This Term List */
					termDocumentsIDs = termDictionary.get(term);
				
				/* Add This Document To the Term List */ 
				termDocumentsIDs.add(documentID);
				
				/* Check If This Terms is already appeared in This Document */
				if (termDocumentDictionary.get(term).get(documentID) == null)
				{
					/* Make a New List For This Term in That Document with Its Position */
					Map<Integer, List<Integer>> secondMap = new LinkedHashMap<Integer, List<Integer>>();
					termDocumentPositions = new ArrayList<Integer>();					
					secondMap.put(documentID, termDocumentPositions);
					termDocumentDictionary.put(term, secondMap);
				}
				else
					/* Get This Term Positions List */
					termDocumentPositions = termDocumentDictionary.get(term).get(documentID);
				
				/* Add This Position To the Document Term List */ 
				termDocumentPositions.add(termPosition);
				
				/* Go To The Next Position */
				termPosition++;
			}
		}
	}
	
	private void SortIndex() {
	/* TODO : It may done here or using DB */
		
	}

	
	private void StoreDictonaries() {
		DbManager DBManager = DbManager.getInstance();
		DBManager.saveTermCollection(termDictionary);
		DBManager.saveDocumentCollection(termDocumentDictionary);
	}
}
	
	
	