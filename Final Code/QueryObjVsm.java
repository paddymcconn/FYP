/*
 * Getter and setters for the VSM retrieval objects.
 * 
 * */

public class QueryObjVsm {

	String field;
	String text;

	public QueryObjVsm(String f, String t) {
		field = f;
		text = t;

	}

	public QueryObjVsm() {

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
