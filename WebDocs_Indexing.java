
/*
 * this class is used to index and store the webdocs after they have been indexed.
*/

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.PorterStemFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.jsoup.Jsoup;

public class WebDocs_Indexing {
	static final File INDEX_DIR = new File(WriteToLuceneIndex.pathToLuceneIndex);
	// change to point to a directory and create a small dir for testing
	// purposes?
	// the path that is defined in the index controller class.

	private IndexWriter writer;
	private IndexWriter w;
	// the indexer we are going to use. do we need to change the config of it ?
	private int counter;

	public void GetItemsFromDB_3() {
		counter = 0;
	}
	// just initialising the counter as 0 not returning anything soo can be void

	public void indexWithUrls(String rootpath) {
		try {

			/***
			 * Patrick - the code here might need changing. What it is doing is
			 * creating an IndexWriter in preparation for writing to the Lucene
			 * Index
			 */
			Analyzer analyzer = new Analyzer() {
				@Override
				protected TokenStreamComponents createComponents(String fieldName) {
					TokenStreamComponents ts = new TokenStreamComponents(new StandardTokenizer());
					ts = new TokenStreamComponents(ts.getTokenizer(), new LowerCaseFilter(ts.getTokenStream()));
					// to lower case as needed for porter stemming
					ts = new TokenStreamComponents(ts.getTokenizer(),
							new StopFilter(ts.getTokenStream(), StandardAnalyzer.ENGLISH_STOP_WORDS_SET));
					ts = new TokenStreamComponents(ts.getTokenizer(), new PorterStemFilter(ts.getTokenStream()));

					return ts;
				}
			};
			IndexWriterConfig config = new IndexWriterConfig(analyzer);
			// our created analyser
			config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
			// open so it overwrites what is in the current index dir
			writer = new IndexWriter(INDEX_DIR, config);

			// writer = new IndexWriter(INDEX_DIR,
			// AnalyzerUtil.getPorterStemmerAnalyzer(new StandardAnalyzer()),
			// WriteToLuceneIndex.createNewIndex);
			// could use englishanalyser as it already has a stemming function
			// built into it.
			/**
			 * END Lucene Part
			 */

			File f = new File(rootpath);

			checkDirWithUrls(f); // calls below method to loop through all
									// webpage files in the folder & write them
			// writer.optimize();
			// This method has been deprecated, as it is horribly inefficient
			// and very rarely justified.
			// Lucene's multi-segment search performance has improved over time,
			// and the default TieredMergePolicy now targets segments with
			// deletions.
			writer.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // writeToLuceneIndex.createNewIndex will either be true or false;
			// true = create a new index

	}

	/*
	 * This method is used when want to index the documents into lucene reading
	 * them from file (instead of reading them from db)
	 * 
	 * @param pathtofiles
	 */
	public void parseFileWithUrls(File pathtofiles) {

		FileReader reader;

		try {

			reader = new FileReader(pathtofiles);
			Scanner in = new Scanner(reader);

			String line = "";

			while (in.hasNextLine()) {

				line = in.nextLine();
				line = line.trim();

				if (line.contains("#UID:")) { // in this If statement we are
												// adding 1 webpage to the
												// Lucene Index

					/**
					 * Patrick - the code here might need changing. What it is
					 * doing is creating a new Lucene document for this webpage
					 * & adding the documents (ie the webpages) ID to the Lucene
					 * index.
					 */
					Document doc = new Document();
					doc.add(new Field("eventId", line.replace("#UID:", ""), Field.Store.YES, Field.Index.ANALYSED));
					/**
					 * End Lucene part
					 */

					in.nextLine(); // skip date

					line = in.nextLine(); // url

					/**
					 * Patrick - the code here might need changing. What it is
					 * doing is adding the webpages URL to the Lucene index.
					 */
					doc.add(new Field("contents1", line, Field.Store.YES, Field.Index.NOT_ANALYSED));
					// Deprecated. this has been renamed to ANALYZED
					/**
					 * End Lucene part
					 */

					in.nextLine(); // should be #CONTENT:

					line = in.nextLine(); // should be first line of webpage
											// content
					StringBuilder s = new StringBuilder();

					while (!line.equals("#EOR")) { // keep looping until have
													// read all this webpages
													// text

						s.append(line);

						line = in.nextLine();
					}
					// remove the html content from the webpage:
					String stripped_content = Jsoup.parse(s.toString()).text();
					stripped_content = stripped_content.replaceAll("\\<.*?>", "");
					stripped_content = stripped_content.replaceAll("\\\\", "");
					stripped_content = stripped_content.replaceAll(" \\+", " ");
					stripped_content = stripped_content.trim();

					if (!stripped_content.equals(" +")) { // if there is content
															// in the webpage

						/**
						 * Patrick - the code here might need changing. What it
						 * is doing is adding the webpage content to the Lucene
						 * index.
						 */
						doc.add(new Field("contents0", stripped_content, Field.Store.YES, Field.Index.ANALYSED));

						writer.addDocument(doc);
						/**
						 * End Lucene Part
						 */
					}

					s = null;
					stripped_content = "";
					doc = null; /**
								 * Patrick this is a Lucene part - here we are
								 * resetting the contents of doc
								 */
				}

			}

			line = "";

			reader.close();
			in.close();
		}

		// Handle any errors that may have occurred.
		catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * this method loops through all files in the folder (ie all files
	 * containing webpage content) & calls the above parseFileWithUrls method to
	 * read the file content and index it into Lucene.
	 */
	public void checkDirWithUrls(File dir) {

		for (File child : dir.listFiles()) {

			if (".".equals(child.getName()) || "..".equals(child.getName()))
				continue; // Ignore the self and parent aliases.
			if (child.isFile()) {
				counter++;
				System.out.println(counter);
				parseFileWithUrls(child); // do something - here we are calling
											// the above method
				// System.out.println(child);
			} else if (child.isDirectory()) {
				System.out.println(child);
				checkDirWithUrls(child);
			}
		}

	}
}
