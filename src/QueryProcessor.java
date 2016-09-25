import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public class QueryProcessor {
	
	@SuppressWarnings("null")
	public static void main(String args[]) throws IOException 
	{

		String folderName;
		
		System.out.println("Enter the folder path");
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		folderName = reader.readLine();
		String q = "c";
		String query;
	
		
		System.out.println("Building the word Index...");
		WordIndex index = new WordIndex(folderName);
		System.out.println("Building the biword Index...");
		BiWordIndex biWordindex = new BiWordIndex(folderName);
			
		//WordIndex index = new WordIndex("/Users/manaswipodduturi/Downloads/pa4");
		//BiWordIndex biWordindex = new BiWordIndex("/Users/manaswipodduturi/Downloads/pa4");
		
		while (!q.equals("q"))
		{
		index.scoreOfDocs = new HashMap<String,Double>();
		String[] terms;
		ArrayList<String> fileTerms = new ArrayList<String>();
		ArrayList<String> biWordsInQuery = new ArrayList<String>();
		HashMap<String,Double> queryWeight = new HashMap<String,Double>() ;
		HashMap<String,Integer> termFreqInQuery = new HashMap<String,Integer>() ;
		HashMap<String,Integer> postings;
		double score =0,lengthOfQuery=0;
		String[] biWord = new String[2];	
		System.out.println("Enter the query");
		query = reader.readLine();
		System.out.println("Enter the integer k");
		int k = Integer.parseInt(reader.readLine());
		
		terms = query.toLowerCase().split("\\.|,|:|;|'|\\s+");
        for (String term : terms)
        {
            if (term.length() > 2 && !term.equals("the"))
                fileTerms.add(term);
        }
        
        for (int i=0; i< (fileTerms.size()-1); i++)
        {
        	biWord = new String[2];
        	biWord[0] = fileTerms.get(i);
        	biWord[1] = fileTerms.get(i+1);
        	biWordsInQuery.add(biWord[0]+" "+biWord[1] );
        }
        for (String term: fileTerms)
        {
        	if (termFreqInQuery.containsKey(term))
        	{
        		int x = termFreqInQuery.get(term);
        		x = x+1;
        		termFreqInQuery.put(term, x);
        	}
        	else
        	{
        		termFreqInQuery.put(term, 1);
        	}
        	
        }
        
        for (String term: fileTerms)
        {
    		
    		double weightOfQuery =  ((double)Math.log(1+termFreqInQuery.get(term))/(double)Math.log(2));
    		queryWeight.put(term, weightOfQuery);
        }
        
        for (String term: fileTerms)
        {
        	postings = new HashMap<String,Integer>();
        	postings = index.postingsList1(term);
        	if(postings!=null && postings.size()!=0)
        	{
        	for (String docName: postings.keySet())
        	{
        		if (index.scoreOfDocs.containsKey(docName))
        			score = index.scoreOfDocs.get(docName);
        		else
        			score =0;
        		score = score + (index.weight(term, docName))*(queryWeight.get(term));
        		
        		index.scoreOfDocs.put(docName, score);
        	}
        	}
        	lengthOfQuery = lengthOfQuery + (queryWeight.get(term))*(queryWeight.get(term));
        	
        }
        lengthOfQuery = Math.sqrt(lengthOfQuery);
        
        for (int i=0; i< index.docarray.length; i++ )
        {
        	double scoreDocs;
        	if(index.scoreOfDocs.containsKey(index.docarray[i]))
           scoreDocs =	index.scoreOfDocs.get(index.docarray[i]);
        	else
        		scoreDocs = 0;
          scoreDocs = ((double)scoreDocs)/((double)(Math.sqrt(index.lengthOfDocs.get(index.docarray[i])))*(lengthOfQuery));
          index.scoreOfDocs.put(index.docarray[i], scoreDocs);
        }
        
        List<HashMap.Entry<String,Double>> scoreSorting = new LinkedList<HashMap.Entry<String,Double>>(index.scoreOfDocs.entrySet());
		Collections.sort(scoreSorting, new Comparator<HashMap.Entry<String,Double>>() {
			public int compare(HashMap.Entry<String, Double> o1,
					HashMap.Entry<String, Double> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});
		
		HashMap<String,Double> resultInDegree = new LinkedHashMap<String,Double>();
        for (HashMap.Entry<String,Double> entry : scoreSorting)
        {
        	resultInDegree.put( entry.getKey(), entry.getValue() );
        }
        
        index.scoreOfDocs = resultInDegree;
        
        String[] topDocs = new String[2*k];
        HashMap<String,Integer> biWordScore = new HashMap<String,Integer>();
		Object[] allDocs = new String[index.scoreOfDocs.size()];
		allDocs = index.scoreOfDocs.keySet().toArray();
		int x;
        
        for ( int i=0; i< 2*k && i < index.scoreOfDocs.size() ; i++)
        {
        	topDocs[i]=(String) allDocs[i];
        }
        
        for (int i=0;i<topDocs.length; i++)
        {
        	for (String biWordTerm: biWordsInQuery)
        	{
        		if (biWordindex.vector.containsKey(biWordTerm))
        		{
        			if (biWordindex.vector.get(biWordTerm).postings.contains(topDocs[i]))
        			{
        				if (biWordScore.containsKey(topDocs[i]))
        				{
        					x = biWordScore.get(allDocs[i]);
        					x = x+1;
        					biWordScore.put(topDocs[i], x);
        				}
        				else
        				{
        					biWordScore.put(topDocs[i], 1);
        				}
        			}
        			else
        			{
        				biWordScore.put(topDocs[i], 0);
        			}
        		}
        		else
        		{
        			biWordScore.put(topDocs[i], 0);
        		}
        	}
        }
        
        List<HashMap.Entry<String,Integer>> lastSorting = new LinkedList<HashMap.Entry<String,Integer>>(biWordScore.entrySet());
		Collections.sort(lastSorting, new Comparator<HashMap.Entry<String,Integer>>() {
			public int compare(HashMap.Entry<String, Integer> o1,
					HashMap.Entry<String, Integer> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});
		
		HashMap<String,Integer> biWordSorting = new LinkedHashMap<String,Integer>();
        for (HashMap.Entry<String,Integer> entry : lastSorting)
        {
        	biWordSorting.put( entry.getKey(), entry.getValue() );
        }
        
        biWordScore = biWordSorting; 
        Object[] docs = new String[biWordScore.size()];
        String[] docNames = new String[biWordScore.size()];
        docs = biWordScore.keySet().toArray();
        for(int i=0;i<biWordScore.size();i++ )
		{
        	docNames[i]=(String) docs[i];
		}
       ArrayList<String> finalDocs = new ArrayList<String>();
        
        for (String term: biWordScore.keySet())
        {
        	HashMap<String,Double> sameScore = new HashMap<String,Double>();
        	
        	for (String term1: biWordScore.keySet())
        	{
        	   if( (biWordScore.get(term)) == biWordScore.get(term1))	
        	   {
        		   if (!finalDocs.contains(term1))
        		   sameScore.put(term1,index.scoreOfDocs.get(term1));
        	   }
        	}
        	
        	if (sameScore.size()!=1)
        	{
            List<HashMap.Entry<String,Double>> sameScoreSorting = new LinkedList<HashMap.Entry<String,Double>>(sameScore.entrySet());
    		Collections.sort(sameScoreSorting, new Comparator<HashMap.Entry<String,Double>>() {
    			public int compare(HashMap.Entry<String, Double> o1,
    					HashMap.Entry<String, Double> o2) {
    				return (o2.getValue()).compareTo(o1.getValue());
    			}
    		});
    		
    		HashMap<String,Double> sorting = new LinkedHashMap<String,Double>();
            for (HashMap.Entry<String,Double> entry : sameScoreSorting)
            {
            	sorting.put( entry.getKey(), entry.getValue() );
            }
            sameScore = sorting;
        	}
            for (String t: sameScore.keySet())
            {
            	finalDocs.add(t);
            }    	
        }
        
        if (!finalDocs.isEmpty())
        {
        
        for (int i=0;i<k;i++)
        {
        	System.out.println("Document Name:  "+ finalDocs.get(i) + "  Cosine Similarity:   "+ index.scoreOfDocs.get(finalDocs.get(i)));
        }
        }
        else
        {
        	for (int i=0;i<k;i++)
        	{
        	System.out.println("Document Name:  "+ topDocs[i] + "  Cosine Similarity:   "+ index.scoreOfDocs.get(topDocs[i]));
        	}
        }
        System.out.println("Enter q to quit or c to continue");
        q = reader.readLine();
 
	}
	}

}
