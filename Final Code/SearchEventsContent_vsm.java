/*
 * This java file does the querying against the Lucene index
 */

///This is the search Events to use for retrieval 

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;

/**
 * **NOTE: String index in the below code should be set to the path where the
 * indexed files you wish to search through are located.
 * 
 */

public class SearchEventsContent_vsm {
	static final Path Point_To_Index = Paths.get("C:/Users/u180384/IR/luceneindexing");
	private String noHits;
	private LinkedList<EventObj> results;
	private LinkedList<QueryObjVsm> query;

	SearchEventsContent_vsm(LinkedList<QueryObjVsm> event) { // this linked
																// list holds
																// one query,
																// each element
																// in the
																// linkedlist is
																// a different
																// field type
																// and the
																// queried words
																// for that
																// filed type
																// (eg a field
																// type could be
																// content or
																// date); in
																// this case we
																// are just
																// using
																// contents0
																// field

		query = event;
		noHits = "0";
		results = new LinkedList<EventObj>();
	}

	public String getNoHits() {
		return noHits;
	}

	public void setNoHits(String noHits) {
		this.noHits = noHits;
	}

	public LinkedList<EventObj> getResults() {
		return results;
	}

	public void setResults(LinkedList<EventObj> results) {
		this.results = results;
	}

	public ArrayList<HitsObj> search() throws IOException, IOException {

		ArrayList<HitsObj> hitsobj = new ArrayList<HitsObj>(); // Creates object
																// to hold score

		try {
			Directory Dir = new MMapDirectory(Point_To_Index);
			// the lucene index
			IndexReader reader = DirectoryReader.open(Dir);
			// open the index as readable
			IndexSearcher searcher = new IndexSearcher(reader);
			// DirectoryReader reader = DirectoryReader.open((Directory)
			// Point_To_Index);
			// IndexSearcher searcher = new IndexSearcher(reader);
			Analyzer analyzer = new EnglishAnalyzer(); /**
														 * // was changed to
														 * normal analysers. //
														 * debugger stops here,
														 * is there a prblem
														 * with the stemmer/
														 * This should be set to
														 * the same Analyzer as
														 * was used for indexing
														 **/

			Similarity similarity = new ClassicSimilarity(); // classic is vsm
			searcher.setSimilarity(similarity);// ****
			// if i dont set it it should be default as it needs to be non-null
			QueryParser parser = new QueryParser(query.getFirst().getField(), analyzer);
			parser.setDefaultOperator(QueryParser.OR_OPERATOR);// ****
			BooleanQuery b = new BooleanQuery.Builder()
					.add(parser.parse(query.getFirst().getText()), BooleanClause.Occur.SHOULD).build();// now
			System.out.println(b.toString()); // immutable,
			// needs
			// build
			// method/.
			// QueryParser parser = new QueryParser(query.getFirst().getField(),
			// analyzer);// ****
			// parser.setDefaultOperator(QueryParser.OR_OPERATOR);// ****
			// b.add(parser.parse(query.getFirst().getText()),
			// BooleanClause.Occur.SHOULD);// ****

			TopScoreDocCollector collector = TopScoreDocCollector.create(100);
			// depricated TopDocCollector collector = new TopDocCollector(100);

			searcher.search(b, collector);
			ScoreDoc[] hits = collector.topDocs().scoreDocs;

			for (int z = 0; z < hits.length; z++) {
				int docId = hits[z].doc;

				Document docu = searcher.doc(docId);

				String idof = docu.get("eventId");
				float sco = hits[z].score;

				// Store the result and relevance score
				HitsObj ho = new HitsObj(idof, sco);

				hitsobj.add(ho);

			}

			reader.close();
			((Closeable) searcher).close();
		} catch (Exception e) {
		}

		return hitsobj;

	}
}
