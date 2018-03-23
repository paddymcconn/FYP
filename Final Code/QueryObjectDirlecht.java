/*
 * Getter and setters for the DS retrieval objects.
 * 
 * */

public class QueryObjectDirlecht {

	String field;
	String text;

	public QueryObjectDirlecht(String f, String t) {
		field = f;
		text = t;

	}

	public QueryObjectDirlecht() {

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
