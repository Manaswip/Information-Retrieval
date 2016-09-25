import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class WordIndex {
	
	//public ArrayList<String,Integer> postings = new ArrayList<String,Integer>();
	public HashMap<String,PostingsList> vector = new HashMap<String,PostingsList>();
	public String folderName;
	public String docarray[] ;
	public HashMap<String,Double> lengthOfDocs = new HashMap<String,Double>(); 
	public HashMap<String,Double> scoreOfDocs;
	
	public WordIndex(String folderName)
	{
		this.folderName = folderName;
		this.docarray = allDocs();
		this.buildIndex();
		this.calculateLengths();
		
	}
	
	public void buildIndex()
	{
		for (int j = 0; j < docarray.length; j++)
		{
			ArrayList<String> terms = buildsetofterms(folderName + File.separator + docarray[j]);
			PostingsList list;
			HashMap<String,Integer> postings;
			int x;

			for (String term : terms) 
			{
				
				if (vector.containsKey(term))
				{
					list = vector.get(term);
					postings = new HashMap<String,Integer>(list.postings);
					
					if (postings.containsKey(docarray[j]))
					{
						x= postings.get(docarray[j]);
						x = x+1;
						postings.put(docarray[j], x);
					}
					
					else
					{
						list.noOfDocsHavingTerm = list.noOfDocsHavingTerm+1;
						postings.put(docarray[j], 1);
					}
					
					list.postings = postings;
					vector.put(term, list);
					

				} 
				else 
				{
				    list = new PostingsList();
					postings = new HashMap<String,Integer>();
					postings.put(docarray[j], 1);
					list.postings = postings;
					list.noOfDocsHavingTerm = 1;
					vector.put(term, list);
				}
			} 		
		
		}
	}
	
	public HashMap<String,Integer> postingsList(String t)
	{
		if (vector.containsKey(t.toLowerCase()))
		return vector.get(t.toLowerCase()).postings;
		else 
			return null;
	}
	
	public HashMap<String,Integer> postingsList1(String t)
	{
		if (vector.containsKey(t.toLowerCase()))
		return vector.get(t.toLowerCase()).postings;
		else 
			return null;
	}
	
	public void printPostingsList(String t)
	{
		HashMap<String,Integer> postings = new HashMap<String,Integer>();
		postings = this.postingsList(t);
		
		if (!(postings == null))
		{
		for (String key:postings.keySet() )
		{
			System.out.println("Document Name: " + key + " Term frequency of term in the document: "+ postings.get(key));
		}
		}
		else
		{
			System.out.println("There is no postings list for this term");
		}
	}
	
	public double weight(String t, String d)
	
	{
		int termFreq=0,docFreq=0;
		if (vector.get(t).postings.containsKey(d))
		termFreq=vector.get(t).postings.get(d);
		docFreq = vector.get(t).noOfDocsHavingTerm;
		return ((double)Math.log(1+termFreq)/(double)Math.log(2))* Math.log10((double)docarray.length/(double)docFreq);
	}
	
	public String[] allDocs() {
		File folderNames = new File(folderName);
        int i=0;
        File[] files = folderNames.listFiles();
        String[] documents = new String[files.length];
        for (int f=0;i<files.length;i++)
        {
        	documents[f] = files[i].getName();
            i++;
        }
        return folderNames.list();	
	}
	
	public ArrayList<String> buildsetofterms(String filename)
	{
		String readLine, terms[];
        ArrayList<String> fileTerms = new ArrayList<String>();
        try 
        {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            while ((readLine = reader.readLine()) != null)
            {
            	terms = readLine.toLowerCase().replaceAll("\\.|,|:|;|'","").split(" ");
                for (String term : terms)
                {
                    if (term.length() > 2 && !term.equals("the"))
                        fileTerms.add(term);
                }
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return fileTerms;
    }
	
	public void calculateLengths()
	{
		PostingsList list;
		HashMap<String,Integer> postings;
		
//		for (int i=0;i<docarray.length;i++)
//		{
//			double length=0;
//			for (String term: vector.keySet())
//			{
//					list = new PostingsList();
//					list = vector.get(term);
//					postings = new HashMap<String,Integer>(list.postings);
//					
//					if (postings.containsKey(docarray[i]))
//					{
//						length = length + weight(term, docarray[i])*weight(term, docarray[i]);
//					}
//				}
//			length = Math.sqrt(length);
//			lengthOfDocs.put(docarray[i], length);
//			//System.out.println("Doc Name:" + docarray[i] + "Lenght: "+ length);
//		}
		
		for(String term: vector.keySet())
		{
			list = new PostingsList();
			list = vector.get(term);
			postings = new HashMap<String,Integer>(list.postings);
			 
			for (String doc: postings.keySet())
			{
				if (lengthOfDocs.containsKey(doc))
				{
					double x = lengthOfDocs.get(doc);
					double weight = weight(term, doc);
					x = x+ weight*weight;
					lengthOfDocs.put(doc, x);
				}
				
				else
				{
					double weight = weight(term, doc);
					lengthOfDocs.put(doc, weight);
				}
			}
		}
	}
	public static void main(String args[]) 
	{
		WordIndex index = new WordIndex("/Users/manaswipodduturi/Downloads/pa4");
		index.printPostingsList("sales");
		//double x=index.weight("ticket", "doc1.txt");
		//System.out.println("Weight is:"+ x);
	}
}
