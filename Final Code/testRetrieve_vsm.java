
//for VSM content only retrieval - i.e. just contents0 field

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;

public class testRetrieve_vsm {

	// for sqllite:
	private static String connectionUrl = "C:\\Users\\u180384\\IR\\sqlite\\EClef.db"; // database
	// queries
	// are
	// stored
	// in

	// Declare the JDBC objects.
	private Connection con;
	private Statement stmt;
	private ResultSet rs;
	private String Table;
	private String keywords;

	public testRetrieve_vsm() {
		keywords = "";

		// Declare the JDBC objects.
		con = null;
		stmt = null;
		rs = null;
	}

	public void getQs() {
		Table = "German_Queries";// change each time
		try {
			// Establish the connection.
			Class.forName("org.sqlite.JDBC");
			con = DriverManager.getConnection("jdbc:sqlite:" + connectionUrl);

			String SQL = "SELECT * FROM " + Table + " order by queryNum;"; // queries
																			// should
																			// be
																			// the
																			// table
																			// names,
																			// eg.
																			// French_Queries
			stmt = con.createStatement();
			rs = stmt.executeQuery(SQL);

			while (rs.next()) {
				String qid = rs.getString(1);
				System.out.println("Query being processed = " + qid);

				keywords = rs.getString(2); // gets the keywords (query) from
											// sqlite database for search in
											// String format
				keywords = keywords.replace("?", "");

				// RUN THE QUERY:
				QueryObjVsm q2 = new QueryObjVsm("Web Content",
						keywords); /**
									 * Patrick see the QueryObjBm25.java file
									 * that I also gave you attached to the
									 * email
									 **/

				LinkedList<QueryObjVsm> query = new LinkedList<QueryObjVsm>();
				query.add(q2);// keywords

				// call searchEvents to get the docs that match this query (it
				// will return a linked list of the docs that match this query).
				SearchEventsContent_vsm search = new SearchEventsContent_vsm(
						query); /**
								 * Patrick see the SearchEventsContent_vsm.java
								 * file also attached to this email --
								 * SearchEventsContent_vsm is where the query is
								 * queries against the Lucene index using the
								 * Vector Space Model of retrieval
								 **/
				ArrayList<HitsObj> resultsC = search.search(); // the array list
																// holds all
																// item ids and
																// their
																// relevance to
																// query score
				/**
				 * Patrick for the above line of code: see HitsObj.java file
				 * also sent with the email -- it just creates an Object to
				 * store the results
				 **/

				// put all the returned scores in an array and then normalise
				// them:
				double[] score_vals = new double[resultsC.size()];
				for (int a = 0; a < resultsC.size(); a++) {
					score_vals[a] = resultsC.get(a).getScore();

				}
				normalNormalisation(score_vals);
				// put the normalised scores in results:
				for (int y = 0; y < resultsC.size(); y++) {
					double ans = score_vals[y];

					HitsObj h = new HitsObj(resultsC.get(y).getId(), ans);
					resultsC.set(y, h);
				}

				// CREATE results table if it doesn't already exist:
				String sql = "CREATE TABLE IF NOT EXISTS VSMresultsIR_German (queryNum INT, item_id STRING, relevanceScore STRING)";

				Statement stat = con.createStatement();
				stat.execute(sql);

				stat.close();

				// INSERT results into the results table in db:

				for (int a = 0; a < resultsC.size(); a++) {
					String key = resultsC.get(a).getId();
					Double value = resultsC.get(a).getScore();

					// put in db table:
					String SQL1 = "INSERT INTO VSMresultsIR_German (queryNum, item_id, relevanceScore) VALUES(?, ?, ?);";

					PreparedStatement pstmt = con.prepareStatement(SQL1); // create
																			// a
																			// statement
					pstmt.setString(1, qid);
					pstmt.setString(2, key);
					pstmt.setDouble(3, value);

					pstmt.executeUpdate(); // execute insert statement
					pstmt.close();

				}

			}

		}
		// Handle any errors that may have occurred.
		catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (Exception e) {
				}
			if (stmt != null)
				try {
					stmt.close();
				} catch (Exception e) {
				}
			if (con != null)
				try {
					con.close();
				} catch (Exception e) {
				}
		}

	}

	public static void normalNormalisation(double[] input_array) { // i.e.
																	// divide
																	// all
																	// values by
																	// the
																	// maximum
																	// value
		double max_score = 0; // to help determine what the largest values is

		for (int c = 0; c < input_array.length; c++) { // go through all the
														// elements of the array
														// to determine what the
														// largest value is
			if (input_array[c] > max_score) // if this element is larger than
											// the current maximum
				max_score = input_array[c]; // update the maximum to be equal to
											// our input array
		} // end going thorough each element to get the largest score to
			// normalise against

		for (int c = 0; c < input_array.length; c++) { // then go through every
														// element and normalise
														// it, given we now know
														// the maximum value
			if (max_score > 0)
				input_array[c] = input_array[c] / max_score; // normalise this
																// element
																// against the
																// largest
																// element value
			else
				input_array[c] = 0.0;
		} // end going through each element to normalise it
	} // end method normal_normalisation

	public static void standardNormalisation(double[] input_array) { // i.e.
																		// shift
																		// min
																		// to 0,
																		// scale
																		// max
																		// to 1
																		// ..
		double min_score = 9999999.99;
		double max_score = 0; // to help determine what the largest values is

		for (int c = 0; c < input_array.length; c++) { // go through all the
														// elements of the array
														// to determine what the
														// largest value is
			if (input_array[c] > max_score) // if this element is larger than
											// the current maximum
				max_score = input_array[c]; // update the maximum to be equal to
											// our input array

			if (input_array[c] < min_score)
				min_score = input_array[c];
		} // end going thorough each element to get the largest score to
			// normalise against

		for (int c = 0; c < input_array.length; c++) { // then go through every
														// element and normalise
														// it, given we now know
														// the maximum value
			if (min_score == max_score)
				input_array[c] = 0.0;
			else if (max_score > 0)
				input_array[c] = (input_array[c] - min_score) / (max_score - min_score); // normalise
																							// this
																							// element
																							// against
																							// the
																							// largest
																							// element
																							// value
			else
				input_array[c] = 0.0;
		} // end going through each element to normalise it
	} // end method standard_normalisation

}
