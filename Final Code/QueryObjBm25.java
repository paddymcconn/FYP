/*
 * Getter and setters for the Bm25 retrieval objects.
 * */
public class QueryObjBm25 {

	String field;
	String text;
	public QueryObjBm25(String f, String t) {
		field = f;
		text = t;
	}

	public QueryObjBm25() {

	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
