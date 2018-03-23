import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * This class is used to read the results for a query from a db table, and write
 * said results to a trec_eval result file.
 * 
 * NOTE: need to set the output path in this class (i.e. to the folder you want
 * the result files for use by trec_eval to be written to. & need to set the
 * path to the database in the below code.
 * 
 *
 */
public class CreateTrecEvalResultList {

	public String outputPath = "C:\\Users\\u180384\\IR\\Qrel_results"; // set
																		// this
																		// path
																		// to
																		// the
																		// location
																		// that
																		// you
																		// want
																		// the
																		// results
																		// files
																		// to
																		// be
																		// written
																		// to

	// for sqllite:
	private static String connectionUrl = "C:\\Users\\u180384\\IR\\sqlite\\EClef.db";

	// Declare the JDBC objects.
	private Connection con;
	private Statement stmt;
	private ResultSet rs;

	public CreateTrecEvalResultList() {
		// Declare the JDBC objects.
		con = null;
		stmt = null;
		rs = null;

	}

	public void create(String dbTableName) {
		try {
			PrintWriter writer = new PrintWriter(outputPath + dbTableName + ".test");

			try {
				// Establish the connection.
				Class.forName("org.sqlite.JDBC");
				con = DriverManager.getConnection("jdbc:sqlite:" + connectionUrl);

				// Create and execute an SQL statement that returns some data.
				String SQL = "SELECT * FROM " + dbTableName + " order by queryNum, relevanceScore DESC;";
				stmt = con.createStatement();
				rs = stmt.executeQuery(SQL);

				// Set the values for the 1st result returned by the SQL
				// statement: (these values will be printed to the first line of
				// the result file in the below while statement)
				int rank = 1; // rank is 1 for the document at the top of our
								// result list

				rs.next();
				String qid = rs.getString(1); // taskNum
				String iter = "Q0";
				String docno = rs.getString(2); // item_id

				String sim = rs.getString(3); // relevanceScore
				if (sim.length() > 8)
					sim = sim.substring(0, 8);
				String run_id = "STANDARD";

				String currentQID = qid;

				int counter = 0;

				while (rs.next()) {
					counter++;
					System.out.println(counter);

					writer.print(qid + " " + iter + " " + docno + " " + rank + " " + sim + " " + run_id + "\n"); // write
																													// a
																													// result
																													// line
																													// to
																													// file

					// eg of output format:
					// 030 Q0 ZF08-175-870 0 4238 prise1
					// qid iter docno rank sim run_id
					qid = rs.getString(1); // taskNum
					iter = "Q0";

					docno = rs.getString(2); // item_id

					sim = rs.getString(3); // relevanceScore
					if (sim.length() > 8)
						sim = sim.substring(0, 8);
					run_id = "STANDARD";

					if (qid.equals(currentQID)) {
						rank++;
					} else {
						rank = 1;
					}

					currentQID = qid;

				}

				// print the last line to file:
				writer.print(qid + " " + iter + " " + docno + " " + rank + " " + sim + " " + run_id + "\n");
				rank++;

				writer.flush();
				writer.close();
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
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
