import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;

public class Lucene_Create {

	public static final String files = "C:/TestLucene/files";
	public static final String index = "C:/TestLucene/index";

	public static void createIndex() {

		System.out.println("Creating indexes....");

		Analyzer analyzer = new StandardAnalyzer();
		try {
			// Store the index in file
			Directory index = FSDirectory.open(Paths.get("index-dir"));
			IndexWriterConfig config = new IndexWriterConfig(analyzer);
			IndexWriter writer = new IndexWriter(index, config);
			File direct = new File(files);
			File[] files = direct.listFiles();

			for (File file : files) {
				System.out.println(file.getPath());
				Document doc = new Document();

				doc.add(new Field("path", file.getPath(), Field.Store.YES,
						Field.index.ANALYZED));

				Reader reader = new FileReader(file.getCanonicalPath());

				doc.add(new Field("contents", reader));
				writer.addDocument(doc);
			}

			// optimise() has been deprecated
			writer.close();
		} catch (Exception e) {

			e.printStackTrace();
		}

	}

