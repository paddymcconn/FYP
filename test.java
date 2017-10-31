import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.PorterStemFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.jsoup.Jsoup;

public class test {
	// set the path to where the Lucene index will be saved:
	static final File INDEX_DIR = new File(WriteToLuceneIndex.pathToLuceneIndex);
	Path INDEX_DIR2 = Paths.get(WriteToLuceneIndex.pathToLuceneIndex);
	/***
	 * Patrick - the code here might need changing. What it is doing is creating
	 * an IndexWriter in preparation for writing to the Lucene Index
	 */
	private IndexWriter writer;
	private int counter;

	public test() {
		counter = 0;
	}

	public void indexWithUrls(String rootpath) throws IOException {
		try {
			Directory index = FSDirectory.open(INDEX_DIR2);
			// the location of the index we are using
			// use fs as it chooses the best method to use for writing

			Analyzer analyzer = new Analyzer() {
				@Override
				protected TokenStreamComponents createComponents(String fieldName) {
					TokenStreamComponents ts = new TokenStreamComponents(new StandardTokenizer());
					ts = new TokenStreamComponents(ts.getTokenizer(), new LowerCaseFilter(ts.getTokenStream()));
					// to lower case as needed for porter stemming
					ts = new TokenStreamComponents(ts.getTokenizer(),
							new StopFilter(ts.getTokenStream(), StandardAnalyzer.ENGLISH_STOP_WORDS_SET));
					// remove the stop words option
					ts = new TokenStreamComponents(ts.getTokenizer(), new PorterStemFilter(ts.getTokenStream()));
					// apply the stemming
					return ts;
				}
			}; // will this override work ?
			IndexWriterConfig config = new IndexWriterConfig(analyzer);
			// our created analyser
			config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
			writer = new IndexWriter(index, config);
			File f = new File(rootpath);

			checkDirWithUrls(f); // calls below method to loop through all
			// webpage files in the folder & write them
			// to the Lucene Index
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // writeToLuceneIndex.createNewIndex will either be true or false;
			// true = create a new index

	}

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
					FieldType ID = new FieldType();
					ID.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
					ID.setStored(true); // true
					ID.setTokenized(true); // true
					// this above section is used to concatinate stored and
					// tolkenised as the below line only takes 3 arguments

					Document doc = new Document();
					doc.add(new Field("eventId", line.replace("#UID:", ""), ID));
					/**
					 * End Lucene part
					 */

					in.nextLine(); // skip date

					line = in.nextLine(); // url

					/**
					 * Patrick - the code here might need changing. What it is
					 * doing is adding the webpages URL to the Lucene index.
					 */

					FieldType URL = new FieldType();
					URL.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
					URL.setStored(true); // true
					URL.setTokenized(false); // false
					// this above section is used to concatinate stored and
					// tolkenised as the below line only takes 3 arguments

					doc.add(new Field("contents1", line, URL));

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

						FieldType WebPage = new FieldType();
						WebPage.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
						WebPage.setStored(true); // true
						WebPage.setTokenized(false); // false
						// this above section is used to concatinate stored and
						// tolkenised as the below line only takes 3 arguments

						doc.add(new Field("contents0", stripped_content, WebPage));
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
