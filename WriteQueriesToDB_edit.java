import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

public class WriteQueriesToDB_edit {

	// for sqllite:
	private static String connectionUrl = "C:/Users/u180384/sqlite/EClef.db";
	// point to the right database
	// Declare the JDBC objects.
	private Connection con;
	private Statement stmt;
	private ResultSet rs;

	public WriteQueriesToDB_edit(String queriesTableName) { // called queries in
															// the other class

		// Declare the JDBC objects.
		con = null;
		stmt = null;
		rs = null;

		try {
			// Establish the connection - sqllite server:
			Class.forName("org.sqlite.JDBC");
			con = DriverManager.getConnection("jdbc:sqlite:" + connectionUrl);

			// sqllite server:
			String sql = "CREATE TABLE if not exists " + queriesTableName + "(queryNum STRING, queryTerms STRING) ";
			// what we want in the table, test numbers and the translations

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
	public void write2017(String pathtofiles, String tableName) {

		// Declare the JDBC objects.
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

			// String line = "";

			// line = in.nextLine();// xml first line
			// line = in.nextLine();// <queries> tag

			while (in.hasNextLine()) {
				// line = in.nextLine(); // <query> tag
				String Num = in.nextLine().replace("<num>", "");
				Num = Num.replace("</num>", "");

				String Query = in.nextLine().replace("<query>", "");
				Query = Query.replace("</query>", "");

				// line = in.nextLine(); // </query> tag
				// line = in.nextLine(); //blank line

				// write details to DB:
				String SQL1 = "INSERT INTO " + tableName + " (queryNum, queryTerms) VALUES(?, ?);";

				PreparedStatement pstmt1 = con.prepareStatement(SQL1); // create
																		// a
																		// statement
				pstmt1.setString(1, Num);
				pstmt1.setString(2, Query);

				pstmt1.executeUpdate(); // execute insert statement
				pstmt1.close();

			}

			reader.close();
			in.close();

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
