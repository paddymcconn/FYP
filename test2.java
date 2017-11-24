import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.jsoup.Jsoup;
import org.tartarus.snowball.ext.PorterStemmer;

/*
 * so far the thing seems to be doing the indexing
 * the override method is not working so we are using the standard analyser which might have a light stemming function
 * using string and text field instead of field seems to determine if it is index or not by default: see the documentation
 *  */
public class test2
{
	static final File INDEX_DIR = new File(WriteToLuceneIndex.pathToLuceneIndex);
	static final Path INDEX_DIR2 = Paths.get(WriteToLuceneIndex.pathToLuceneIndex);
	//getting the file we want to write to as a path.
	
	private IndexWriter writer;
	private int counter;
	// initialise the writer and counter.
	
	public test2()
	{
		counter = 0;
	}
	// simple method for counter.

	public void indexWithUrls(String rootpath) throws IOException
	{
		try
		{
			Directory index = FSDirectory.open(INDEX_DIR2);
			// using FSDirectory as it determines best method of indexing, stops problem with 32bit overflowing machines.
			
			String word = "opportunity";
			PorterStemmer stem = new PorterStemmer();
			stem.setCurrent(word);
			stem.stem();
			String result = stem.getCurrent();
			System.out.println(result);
			// testing the stemming function for behaviour purposes.
			
			EnglishAnalyzer engAnalyse = new EnglishAnalyzer();
			IndexWriterConfig config = new IndexWriterConfig(engAnalyse);
			config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
			writer = new IndexWriter(index, config);
			// set the analyser and config for the writer.
			
			File f = new File(rootpath);
			checkDirWithUrls(f); 
			// write to lucene index.
			
			writer.close();
			index.close();
			// important: needs to be closed after to not overwrite.
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void parseFileWithUrls(File pathtofiles)
	{
		FileReader reader;
		// initialise file reader.
		
		try
		{
			reader = new FileReader(pathtofiles);
			Scanner in = new Scanner(reader);

			String line = "";

			while (in.hasNextLine())
			{
				line = in.nextLine();
				line = line.trim();
				
				if (line.contains("#UID:"))
				{ 
				//in this if statement we are adding 1 webpage to the Lucene Index.
					
					Document doc = new Document();
					// create document to write to.
					
					doc.add(new StringField("eventId", line.replace("#UID:", ""), Field.Store.YES));
					/*
					 * String field is for storing things for exact matches,
					 * things that should not be shortened.
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

					while (!line.equals("#EOR"))
					{ 
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
					
					if (!stripped_content.equals(" +"))
					{
						// if there is content in the webpage

						doc.add(new TextField("Web Content", stripped_content, Store.YES));
						// should the stemming be done here before?
						
						writer.addDocument(doc);
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
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void checkDirWithUrls(File dir)
	{
		for (File child : dir.listFiles())
		{
			if (".".equals(child.getName()) || "..".equals(child.getName()))
				continue;
			// Ignore the self and parent aliases.
			if (child.isFile())
			{
				counter++;
				System.out.println(counter);
				parseFileWithUrls(child);
				// call method.
			}
			else if (child.isDirectory())
			{
				System.out.println(child);
				checkDirWithUrls(child);
			}
		}
	}
}
