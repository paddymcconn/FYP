
public class TrecEvalObject implements Comparable {

	private String qid;
	private String iter;
	private String docno;
	private double sim;

	public TrecEvalObject(String q, String i, String d, double s) {
		qid = q;
		iter = i;
		docno = d;
		sim = s;
	}

	public int compareTo(Object otherObject) {
		TrecEvalObject other = (TrecEvalObject) otherObject;
		if (getSim() > other.getSim())
			return -1;
		if (getSim() == other.getSim())
			return 0;
		return 1;
	}

	public void setQid(String b) {
		qid = b;
	}

	public void setIter(String b) {
		iter = b;
	}

	public void setDocno(String b) {
		docno = b;
	}

	public void setSim(double b) {
		sim = b;
	}

	public String getQid() {
		return qid;
	}

	public String getIter() {
		return iter;
	}

	public String getDocno() {
		return docno;
	}

	public double getSim() {
		return sim;
	}
}
