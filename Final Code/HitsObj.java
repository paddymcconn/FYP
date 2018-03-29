/* This is used for the scoring part of the retrieval.
* Gets and sets the score, used in another class.
*/
public class HitsObj {

	private String id;
	private double score;

	public HitsObj(String i, double s) {
		id = i;
		score = s;
	}

	public String getId() {
		return id;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double s) {
		score = s;
	}
}
