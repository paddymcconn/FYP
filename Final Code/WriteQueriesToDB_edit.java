import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;
// java imports.

/*
* used to write to the database tables.
*/

public class WriteQueriesToDB_edit {

	// for sqllite:
	private static String connectionUrl = "C:/Users/u180384/IR/sqlite/EClef.db";
	
	// Declare the JDBC objects.
	private Connection con;
	private Statement stmt;
	private ResultSet rs;

	public WriteQueriesToDB_edit(String queriesTableName) {
	// called queries in the other class

		// Instantiate the JDBC objects.
		con = null;
		stmt = null;
		rs = null;

		try {
			// Establish the connection - sqllite server:
			Class.forName("org.sqlite.JDBC");
			con = DriverManager.getConnection("jdbc:sqlite:" + connectionUrl);

			// sqllite server:
			String sql = "CREATE TABLE if not exists " + queriesTableName + "(queryNum STRING, queryTerms STRING) ";
			// create the table for the queries, number and terms.

			Statement stat = con.createStatement();
			stat.execute(sql);
		} catch (Exception e) {
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
	// writing queries to db
	public void writeToDB(String pathtofiles, String tableName) {
		
		// Instantiate the JDBC objects.
		con = null;
		stmt = null;
		rs = null;

		try {
			// Establish the connection - sqllite server:
			Class.forName("org.sqlite.JDBC");
			con = DriverManager.getConnection("jdbc:sqlite:" + connectionUrl);

			File child = new File(pathtofiles);

			FileReader reader = new FileReader(child);
			Scanner in = new Scanner(reader);

			while (in.hasNextLine()) {
				String Num = in.nextLine().replace("<num>", "");
				Num = Num.replace("</num>", "");

				String Query = in.nextLine().replace("<query>", "");
				Query = Query.replace("</query>", "");

				// write details to DB:
				String SQL1 = "INSERT INTO " + tableName + " (queryNum, queryTerms) VALUES(?, ?);";

				PreparedStatement pstmt1 = con.prepareStatement(SQL1)
				// create statement
				
				pstmt1.setString(1, Num);
				pstmt1.setString(2, Query);

				pstmt1.executeUpdate(); // execute insert statement
				pstmt1.close();
			}
			reader.close();
			in.close();
			
			// handle errors.
		} catch (Exception e) {
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
}
