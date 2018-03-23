import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;

/*
 * simple lucene application for learning purposes, from the learnlucene website.
 * 
 * */
public class HelloLucene {
	public static void main(String[] args) throws IOException, ParseException {
		CharArraySet arr = EnglishAnalyzer.getDefaultStopSet();
		System.out.println(arr);
		CharArraySet standar = StandardAnalyzer.STOP_WORDS_SET;	
		System.out.println(standar);
		
		
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
				// System.out.println(result);
				len++;
			}
			return result;
		// both standard and english analyser have the same list of stop words,
		// do they tolkenize properly ?
		// a simple charsetarray with the default stop words to be removed
		// 0. Specify the analyzer for tokenizing text.
		// The same analyzer should be used for indexing and searching
		StandardAnalyzer analyzer = new StandardAnalyzer();
		// first thing we need is what is going to analysis the text.
		Path file = Paths.get("C:/Users/u180384/luceneindexing");
		Directory ine = new MMapDirectory(file);
		// 1. create the index
		// Directory index = new RAMDirectory();
		// directory of RAM to write our index to
		File F = new File("C:/Downloads/part6/part6/healt3100_12.dat", "1233342423");
		IndexWriterConfig config = new IndexWriterConfig(analyzer);

		IndexWriter w = new IndexWriter(ine, config);
		config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
		w.deleteAll(); // delete all before running the lucene query
		addDoc(w, "Lucene in Action", "193398817"); // query, num for eclef?
		addDoc(w, "Lucene for Dummies", "55320055Z");
		addDoc(w, "Managing Gigabytes", "55063554A");
		addDoc(w, "The Art of Computer Science", "9900333X");
		// addDoc(w,F,);

		// testing area for file
		String suffix = ".dat";
		indexDirectory(w, F, suffix);

		w.flush();
		w.close();
		// add the following documents to the lucene index writer, to be written

		// 2. query
		String querystr = args.length > 0 ? args[0] : "art";

		// the "title" arg specifies the default field to use
		// when no field is explicitly specified in the query.
		Query q = new QueryParser("title", analyzer).parse(querystr);

		// 3. search
		int hitsPerPage = 10;
		IndexReader reader = DirectoryReader.open(ine);
		IndexSearcher searcher = new IndexSearcher(reader);
		TopDocs docs = searcher.search(q, hitsPerPage);
		ScoreDoc[] hits = docs.scoreDocs;

		// 4. display results
		System.out.println("Found " + hits.length + " hits.");
		for (int i = 0; i < hits.length; ++i) {
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);
			System.out.println((i + 1) + ". " + d.get("isbn") + "\t" + d.get("title"));
		}
		// display the hits we get with the query, need to remove the stop words
		// and such i think
		reader.close();
	}

	private static void indexDirectory(IndexWriter indexWriter, File dataDir, String suffix) throws IOException {
		File[] files = dataDir.listFiles();
		for (int i = 0; i < files.length; i++) {
			File f = files[i];
			if (f.isDirectory()) {
				indexDirectory(indexWriter, f, suffix);
			} else {
				indexFileWithIndexWriter(indexWriter, f, suffix);
			}
		}
	}

	private static void indexFileWithIndexWriter(IndexWriter indexWriter, File f, String suffix) throws IOException {
		if (f.isHidden() || f.isDirectory() || !f.canRead() || !f.exists()) {
			return;
		}
		if (suffix != null && !f.getName().endsWith(suffix)) {
			return;
		}
		System.out.println("Indexing file " + f.getCanonicalPath());
		Document doc = new Document();
		doc.add(new Field("Title", new FileReader(f)));
		doc.add(new Field("filename", f.getCanonicalPath(), Field.Store.YES));
		indexWriter.addDocument(doc);
	}

	private static void addDoc(IndexWriter w, String title, String isbn) throws IOException {
		Document doc = new Document();
		doc.add(new TextField("title", title, Field.Store.YES));
		// textfield title here will be the title in the html pages.

		// use a string field for isbn because we don't want it tokenized
		doc.add(new StringField("isbn", isbn, Field.Store.YES));
		w.addDocument(doc);
	}
}