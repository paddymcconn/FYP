import java.util.*;

public class Write_To_Database {

	public static void main(String[] args) {

		// write the 5 queries [2014] to db table:

		WriteQueriesToDB_edit wqtdb = new WriteQueriesToDB_edit("Queries");
		wqtdb.write2014("F:\\CLEF2014_data\\Queries\\queries.clef2014ehealth.1-5.train.en_forDistribution.xml",
				"Queries");

	}

}