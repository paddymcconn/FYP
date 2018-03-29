import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
// java imports used.

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.jsoup.Jsoup;
import org.tartarus.snowball.ext.PorterStemmer;
// lucene imports used, + stemming used above.

/*
 * It stemms with the porter stemming function.
 * It removes the stop word by tokenising.
 * This file does all the heavy lifting, indexing the one million docs.
 *  */
public class WebDocs_Indexing {
	static final File INDEX_DIR = new File(WriteToLuceneIndex.pathToLuceneIndex);
	static final Path INDEX_DIR2 = Paths.get(WriteToLuceneIndex.pathToLuceneIndex);
	// getting the file we want to write to as a path.
	private Directory index;
	private IndexWriter writer;
	private int counter;
	// initialise the writer and counter.

	public WebDocs_Indexing() {
		counter = 0;
	}
	// simple method for counter.

	public void indexWithUrls(String rootpath) throws IOException {
		try {
			// Base class for Directory implementations that store index files in the file system.
			index = FSDirectory.open(INDEX_DIR2);
			// String word = "direct";
			// PorterStemmer stem = new PorterStemmer();
			// stem.setCurrent(word);
			// stem.stem();
			// String result = stem.getCurrent();
			// System.out.println(result);
			// testing the stemming function for behaviour purposes.

			EnglishAnalyzer engAnalyse = new EnglishAnalyzer();

			IndexWriterConfig config = new IndexWriterConfig(engAnalyse);
			config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
			writer = new IndexWriter(index, config);
			// set the analyser and config for the writer.

			File f = new File(rootpath);
			checkDirWithUrls(f);
			// if its a directory we need to deal with its children first.
			// important: needs to be closed after to not overwrite.
		} catch (IOException e) {
			e.printStackTrace();
		}
		writer.close();
		index.close();
	}

	public void parseFileWithUrls(File pathtofiles) {
		FileReader reader;
		// initialise file reader.

		try {
			reader = new FileReader(pathtofiles);
			Scanner in = new Scanner(reader);

			String line = "";

			while (in.hasNextLine()) {
				line = in.nextLine();
				line = line.trim();

				if (line.contains("#UID:")) {
					// in this if statement we are adding 1 webpage to the
					// Lucene Index.

					Document doc = new Document();
					// create document to write to.

					doc.add(new StringField("eventId", line.replace("#UID:", ""), Field.Store.YES));
					/*
					 * StringField : indexed but not tolkenised. Used for IDs or
					 * Countrys, exact matches. TextField : A field that is
					 * indexed and tokenized. Used for bodys of text.
					 */

					in.nextLine();
					// skip date, not needed for indexing.

					line = in.nextLine();
					// url

					doc.add(new StringField("URL:", line, Store.YES));

					in.nextLine();
					// should be #CONTENT.

					line = in.nextLine();
					// should be first line of webpage content.

					StringBuilder s = new StringBuilder();

					while (!line.equals("#EOR")) {
						// keep looping until have read all this webpages text.

						s.append(line);
						// add the line to the stringbuilder.
						line = in.nextLine();
					}
					String stripped_content = Jsoup.parse(s.toString()).text();
					stripped_content = stripped_content.replaceAll("\\<.*?>", "");
					stripped_content = stripped_content.replaceAll("\\\\", "");
					stripped_content = stripped_content.replaceAll(" \\+", " ");
					stripped_content = stripped_content.trim();
					// remove the html content from the webpage.

					stripped_content = stripped_content.toLowerCase();
					// needs to be lower case to allow stemming.

					stripped_content = RemovingStopWords(stripped_content);
					// removing the stop word with a method.
					stripped_content = stem(stripped_content);
					// Stemming the word.

					if (!stripped_content.equals(" +")) {
						// if there is content in the webpage
						doc.add(new TextField("Web Content", stripped_content, Store.YES));
						writer.addDocument(doc);
						// add doc.
					}
					s = null;
					stripped_content = "";
					doc = null;
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
				continue;
			// Ignore the self and parent aliases.
			if (child.isFile()) {
				counter++;
				System.out.println(counter);
				parseFileWithUrls(child);
				// call method.
			} else if (child.isDirectory()) {
				System.out.println(child);
				checkDirWithUrls(child);
			}
		}
	}

	public String stem(String all) throws Exception {
		PorterStemmer stem = new PorterStemmer();
		String arr[] = all.split("\\s+");
		// split by the white space character
		int len = 0;
		String result = "";
		while (arr.length != len) {
			stem.setCurrent(arr[len]);
			stem.stem();
			result += stem.getCurrent() + " ";
			len++;
		}
		return result;
	}

	public String RemovingStopWords(String term) throws Exception, IOException {
		Analyzer analyzer = new EnglishAnalyzer();
		TokenStream result = analyzer.tokenStream("Web Content", term);
		CharArraySet stopWords = EnglishAnalyzer.getDefaultStopSet();
		result = new StopFilter(result, stopWords);
		CharTermAttribute resultAttr = result.addAttribute(CharTermAttribute.class);

		result.reset();
		// back to the start of the stream.

		List<String> tokens = new ArrayList<>();

		// holder for the tokens of the long string.
		while (result.incrementToken()) {
			tokens.add(resultAttr.toString());
		}
		result.end();
		// read last token, close up shop.
		analyzer.close();
		result.close();
		return tokens.toString();
		// return the strings after removal of stop words.
	}
}
