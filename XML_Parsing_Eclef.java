import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;



public class XML_Parsing_Eclef {
	public static void main(String args[]) {
		System.setProperty("file.encoding", "UTF-8");
		try {
			File TranFile = new File("C://Users/u180384/Dropbox/Final Year/Final Year Project/test_xml/portuguese_With_Tags.txt");
			File fXmlFile = new File(
					"C://Users//u180384//Dropbox//Final Year//Final Year Project//test_xml//clef2015.test.queries-PT.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);

			doc.getDocumentElement().normalize();
			// Normalize to stop things being separated by the new line
			// character before tag.

			System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
			// root element should be topics for eClef
			NodeList nList = doc.getElementsByTagName("top");

			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);

				if (nNode.getNodeType() == Node.ELEMENT_NODE)
				{
					Element eElement = (Element) nNode;
					String Num = "<num>" + eElement.getElementsByTagName("num").item(0).getTextContent()+"</num>";
					String Query = "<query>" + eElement.getElementsByTagName("query").item(0).getTextContent()+"</query>";
					System.out.println(Num +"\r\n"+Query);
					BufferedWriter f = null;
			        try {
			            f = new BufferedWriter(new FileWriter(TranFile,true));
			            f.append(Num+",\r\n");
			            f.append(Query+",\r\n"); // to use , as a way to delimit the input 
			            							// onto different lines going into db
			        }
			        catch(IOException e) {
			            System.out.println(e);
			        }
			        finally {
			            if (f != null)
			                f.close();  
			        }
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}