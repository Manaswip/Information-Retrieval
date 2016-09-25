import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class BiWordIndex {
	
	public HashMap<String,BiWordPostingsList> vector = new HashMap<String,BiWordPostingsList>();
	public String folderName;
	public String docarray[] ;
	
	public BiWordIndex(String folderName)
	{
		this.folderName = folderName;
		this.docarray = allDocs();
		this.buildIndex();
	}
	
	public void buildIndex()
	{
		for (int j = 0; j < docarray.length; j++)
		{
			ArrayList<String[]> terms = buildsetofterms(folderName + File.separator + docarray[j]);
			BiWordPostingsList list;
			ArrayList<String> postings;
			int x;

			for (String[] term1 : terms) 
			{
			//	System.out.println(term[0]);
				String term = term1[0]+ " "+ term1[1];
				if (vector.containsKey(term))
				{
					list = new BiWordPostingsList();
					list = vector.get(term);
					postings = new ArrayList<String>(list.postings);
					
					if (!postings.contains(docarray[j]))
					{
					
						list.noOfDocsHavingTerm = list.noOfDocsHavingTerm+1;
						postings.add(docarray[j]);
					}
					
					list.postings = postings;
					vector.put(term, list);
					

				} 
				else 
				{
				    list = new BiWordPostingsList();
					postings = new ArrayList<String>();
					postings.add(docarray[j]);
					list.postings = postings;
					list.noOfDocsHavingTerm = 1;
					vector.put(term, list);
				}
			} 		
		
		}
	}
	
	public ArrayList<String> postingsList(String t)
	{
		String[] terms = t.split(" ");
		String term = terms[0]+ " "+terms[1];
		if (vector.containsKey(term.toLowerCase()))
		return vector.get(term.toLowerCase()).postings;
		else
	    return null;
	}
	
	public void printPostingsList(String t)
	{
		ArrayList<String> postings = new ArrayList<String>();
		postings = this.postingsList(t);
		
		if(!(postings == null))
		{
		for (String key:postings )
		{
			System.out.println("Document Name: " + key);
		}
		}
		else
		{
			System.out.println("There is no postings list for this biword");
		}
		
	}
	private ArrayList<String[]> buildsetofterms(String filename)
	{
		String readLine, terms[];
        ArrayList<String> fileTerms = new ArrayList<String>();
        ArrayList<String[]> biWordTerms = new ArrayList<String[]>();
        String[] biWords;
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
            
            for (int i=0;i<(fileTerms.size()-1);i++)
            {
            	biWords = new String[2];
            	biWords[0] = fileTerms.get(i);
            	biWords[1] = fileTerms.get(i+1);
            	biWordTerms.add(biWords);
            }
            
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return biWordTerms;
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
	
	public static void main(String args[]) 
	{
		BiWordIndex index = new BiWordIndex("/Users/manaswipodduturi/Downloads/pa4");
		index.printPostingsList("common sense");
	}

}
