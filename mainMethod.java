/**
 * This code is used for doing a VSM (vector space model) run on the 5 test queries & generating a 
 * TREC result file for them.
 * 
 * NOTE: it is necessary to set paths to lucene, db, file , etc in the individual methods in this code.
 * 
 *
 */

public class mainMethod {


	public static void main(String[] args) {
		
	
		//do IR for the queries against the index using Lucene's implementation of vector space model.
		testRetrieve_vsm v = new testRetrieve_vsm();
		v.getQs();


		//create result list in the format required for use with trec eval
		System.out.println("VSM");
		CreateTrecEvalResultList cc = new CreateTrecEvalResultList();
		cc.create("VSMresultsIR");
		
		
		
		
		System.out.println("<<<<<<<<<<<<<FINISHED>>>>>>>>>>>>");
	}

}
