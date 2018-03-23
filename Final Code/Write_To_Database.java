/*
 * This class is used as a controller for writing to the database.
 * We are inputting queries into a database that have been translated.
 * 
 * @author:		Patrick Mc Connell
 * @version:	1.0
 */
public class Write_To_Database {
	public static void main(String[] args) {
		/*
		 * @param table The table we are inputting the data to.
		 * 
		 * @param path The directory where we are getting the file.
		 * 
		 * @see WriteQueriesToDB_edit.java.
		 * 
		 * @see write2017.
		 */

		String Path = "C:/Users/u180384/Dropbox/Final Year/Final Year Project/test_xml/English_With_Tags.txt";
		String Table = "English_Queries";
		WriteQueriesToDB_edit wqtdb = new WriteQueriesToDB_edit(Table);
		wqtdb.write2017(Path, Table);
	}
}