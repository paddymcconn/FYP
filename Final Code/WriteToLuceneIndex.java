import java.io.IOException;

import org.apache.lucene.index.CorruptIndexException;

public class WriteToLuceneIndex {
	// these are the only variables that need to be set:
	public static String pathToLuceneIndex = "C:/Users/u180384/luceneindexing";
	// saved lucene index location
	public static String pathToTheFilesToIndex = "C:/Users/u180384/Lucene-Data"; // a
																					// C:\Users\u180384\Lucene-Data\part2
	// particular //C:/Users/u180384/Lucene-Data
	// healt3100_12.dat // file
	// for
	// C:/Downloads/data/Data // testing
	// web files location
	public static boolean createNewIndex = true;
	// true for creating and writing, false for adding

	public static void main(String args[]) throws CorruptIndexException, IOException {
		String pathtofiles = pathToTheFilesToIndex;
		// 0) this is the code to run if want to index events into lucene ,
		// reading them from file:
		System.out.println("..indexing events into lucene..");
		WebDocs_Indexing WDI = new WebDocs_Indexing();
		// call the indexing process
		WDI.indexWithUrls(pathtofiles);

		System.out.println("..finished indexing events into lucene..");

	}
}
