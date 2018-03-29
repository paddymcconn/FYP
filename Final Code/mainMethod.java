/**
 * This code is used for doing a VSM (vector space model) run on the 5 test
 * queries & generating a TREC result file for them.
 * 
 * NOTE: it is necessary to set paths to lucene, db, file , etc in the
 * individual methods in this code.
 */

public class mainMethod {
	public static void main(String[] args) {
		// uncomment the ones you want to use.
		
		// testRetrieve_DirlechtSmoothing Ds = new
		// testRetrieve_DirlechtSmoothing();
		testRetrieve_vsm v = new testRetrieve_vsm();
		// testRetrieve_BM_25 bm = new testRetrieve_BM_25();
		// bm.getQs();
		v.getQs();
		// Ds.getQs();
		// create result list in the format required for use with trec eval
		System.out.println("");
		CreateTrecEvalResultList cc = new CreateTrecEvalResultList();
		cc.create("VSMresultsIR_German");

		System.out.println("<<<<<<<<<<<<<FINISHED>>>>>>>>>>>>");
	}
}
