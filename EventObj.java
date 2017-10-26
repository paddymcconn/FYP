/*
 * event object that contains the web page items we want to use for indexing.
*/

public class EventObj {

	private String eventId;
	private String title;
	private String content;
	private String domain;
	private String url;
	private String htmlContent;

	public EventObj() {
		eventId = "";

		title = "";
		content = "";
		domain = "";
		url = "";
		htmlContent = "";

	}

	public String getEventID() {
		return eventId;
	}

	public String getTitle() {
		return title;
	}

	public String getContent() {
		return content;
	}

	public String getDomain() {
		return domain;
	}

	public String getUrl() {
		return url;
	}

	public String getHtmlContent() {
		return htmlContent;
	}

	public void setEventID(String e) {
		eventId = e;
	}

	public void setTitle(String e) {
		title = e;
	}

	public void setDomain(String e) {
		domain = e;
	}

	public void setContent(String e) {
		content = e;
	}

	public void setUrl(String e) {
		url = e;
	}

	public void setHtmlContent(String e) {
		htmlContent = e;
	}
}
