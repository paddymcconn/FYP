public class Write_To_Database {

	public static void main(String[] args) {

		// write the 5 queries [2014] to db table
		String Table = "Arabic_Queries";
		WriteQueriesToDB_edit wqtdb = new WriteQueriesToDB_edit(Table);
		wqtdb.write2014("C:/Users/u180384/Dropbox/Final Year/Final Year Project/test_xml/arabic_With_Tags.txt", Table);
		// write the given file to the table we call Queries.

	}

}