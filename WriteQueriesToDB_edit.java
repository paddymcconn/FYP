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
	private static String connectionUrl = "F:\\CLEF2014_data\\Database\\CLEFlab.db"; // 2014
	// add the url for the database
	// Declare the JDBC objects.
	private Connection con;
	private Statement stmt;
	private ResultSet rs;

	public WriteQueriesToDB_edit(String queriesTableName) { // called queries in the other class

		// Declare the JDBC objects.
		con = null;
		stmt = null;
		rs = null;

		try {
			// Establish the connection - sqllite server:
			Class.forName("org.sqlite.JDBC");
			con = DriverManager.getConnection("jdbc:sqlite:" + connectionUrl);

			// sqllite server:
			String sql = "CREATE TABLE if not exists " + queriesTableName
					+ "(queryNum STRING, queryTerms STRING, description STRING, narr STRING, profile STRING, reportID STRING) ";

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
	// for writing the 2014 queries
	public void write2014(String pathtofiles, String tableName) {

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

			String line = "";

			line = in.nextLine();// xml first line
			line = in.nextLine();// <queries> tag

			int counter = 0;
			while (in.hasNextLine() && counter < 5) {

				counter++;
				System.out.println(counter);

				line = in.nextLine(); // <query> tag

				String id = in.nextLine().replace("<id>", "");
				id = id.replace("</id>", "");
				id = id.replace("QTRAIN", "");

				String reportID = in.nextLine().replace("<discharge_summary>", "");
				reportID = reportID.replace("</discharge_summary>", "");

				String title = in.nextLine().replace("<title>", "");
				title = title.replace("</title>", "");

				String desc = in.nextLine().replace("<desc>", "");
				desc = desc.replace("</desc>", "");

				String profile = in.nextLine().replace("<profile>", "");
				profile = profile.replace("</profile>", "");

				String narr = in.nextLine().replace("<narr>", "");
				narr = narr.replace("</narr>", "");

				line = in.nextLine(); // </query> tag
				// line = in.nextLine(); //blank line

				// write details to DB:
				String SQL1 = "INSERT INTO " + tableName
						+ " (queryNum, queryTerms, description, narr, profile, reportID) VALUES(?, ?, ?, ?, ?, ?);";

				PreparedStatement pstmt1 = con.prepareStatement(SQL1); // create
																		// a
																		// statement
				pstmt1.setString(1, id);
				pstmt1.setString(2, title);
				pstmt1.setString(3, desc);
				pstmt1.setString(4, narr);
				pstmt1.setString(5, profile);
				pstmt1.setString(6, reportID);

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
