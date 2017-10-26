/**
 * 
 * This is the main class to use for indexing the crawl data 
 * 
 * Patrick --  Note, variables at the beginning of the code need to be set by you
 * 
 */

import java.io.IOException;

import org.apache.lucene.index.CorruptIndexException;

public class WriteToLuceneIndex {
	// these are the only variables that need to be set:
	public static String pathToLuceneIndex = "C:/Users/u180384/luceneindexing";
	// saved lucene index location
	public static String pathToTheFilesToIndex = "C:/Downloads/part6/part6/healt3100_12.dat"; // a
																								// particular
																								// file
																								// for
																								// testing
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
