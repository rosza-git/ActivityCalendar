/**
 * XML util
 * 
 * @author Szalay Roland
 * 
 */
/* XML help:
    http://www.mkyong.com/java/how-to-create-xml-file-in-java-dom/
    http://stackoverflow.com/questions/6445828/how-do-i-append-a-node-to-an-existing-xml-file-in-java
*/
package rosza.activitycalendar;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.joda.time.DateTime;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLUtil {
  /**
   * Add new "activity" to file.
   * If the file does not exists, creates a new one.
   * 
   * @param activity Activity class to add to the xml
   * @return true if the opertion was successful, otherwise false
   */
  public static boolean addActivity(Activity activity) {
    try {
      DocumentBuilderFactory docBuildFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = docBuildFactory.newDocumentBuilder();
      Document doc;
      try {
        // get "activities.xml"
        doc = docBuilder.parse(Constant.XML_ACTIVITIES_FILE);
      }
      catch(SAXException e) {
        JOptionPane.showMessageDialog(null, e.getMessage(), "Error - SAXException", JOptionPane.ERROR_MESSAGE);
        return false;
      }
      catch (IOException e) {
        // no "activities.xml"
        // adding root element (application name) to the document
        doc = docBuilder.newDocument();
        Element rootElement = doc.createElement(Constant.APP_NAME + Constant.XML_ACTIVITIES_ROOT);
        doc.appendChild(rootElement);

        // create new activites.xml
        if(!createActivityXML(doc)) {
          // error happened
          return false;
        }
        try {
          // now we must have "activities.xml", so get it
          doc = docBuilder.parse(Constant.XML_ACTIVITIES_FILE);
        }
        catch(SAXException ex) {
          JOptionPane.showMessageDialog(null, ex.getMessage(), "Error - SAXException", JOptionPane.ERROR_MESSAGE);
          return false;
        }
        catch(IOException ex) {
          // still no luck with "activities.xml", giving up
          JOptionPane.showMessageDialog(null, ex.getMessage(), "Error - IOException", JOptionPane.ERROR_MESSAGE);
          return false;
        }
      }

      Element root = doc.getDocumentElement();
      // Root Element
      Element rootElement = doc.getDocumentElement();

      // get new activity ID
      int newID = getLastID(rootElement, Constant.XML_ACTIVITY_TAG);
      newID++;

      // add "activity" element and set the "id"
      Element aElement = doc.createElement(Constant.XML_ACTIVITY_TAG);
      rootElement.appendChild(aElement);
      aElement.setAttribute(Constant.XML_ID, Integer.toString(newID));

      // add elements from "activity" parameter
      for(String[] s : activity.getFields()) {
        if(!s[0].equals(Constant.XML_ID)) {
          Element element = doc.createElement(s[0]);
          element.appendChild(doc.createTextNode(s[1]));
          aElement.appendChild(element);
        }
      }

      root.appendChild(aElement);

      createActivityXML(doc);

      return true;
    }
    catch(ParserConfigurationException e) {
      JOptionPane.showMessageDialog(null, e.getMessage(), "Error - ParserConfigurationException", JOptionPane.ERROR_MESSAGE);
    }
    catch(NumberFormatException e) {
      JOptionPane.showMessageDialog(null, e.getMessage(), "Error - NumberFormatException", JOptionPane.ERROR_MESSAGE);
    }
    catch(DOMException e) {
      JOptionPane.showMessageDialog(null, e.getMessage(), "Error - DOMException", JOptionPane.ERROR_MESSAGE);
    }


    return false;
  }

  /**
   * Update activity (identified by ID) in file.
   * 
   * @param activity activity to update to
   * @return true if the operation was successful, otherwise false
   */
  public static boolean updateActivity(Activity activity) {
    try {
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
      Document doc = dBuilder.parse(new File(Constant.XML_ACTIVITIES_FILE));

      //optional, but recommended
      //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
      doc.getDocumentElement().normalize();

      NodeList nodeList = doc.getElementsByTagName(Constant.XML_ACTIVITY_TAG);

      for(int i = 0, h = nodeList.getLength(); i < h; i++) {
        Node node = nodeList.item(i);
        if(node.getNodeType() == Node.ELEMENT_NODE) {
          Element element = (Element)node;
          if(element.getAttribute(Constant.XML_ID).equals(Integer.toString(activity.getID()))) {
            for(String[] field : activity.getFields()) {
              Node n = element.getElementsByTagName(field[0]).item(0);
              if(n != null) {
                n.setTextContent(field[1]);
              }
            }
            return createActivityXML(doc);
          }
        }
      }
    }
    catch(ParserConfigurationException e) {
      JOptionPane.showMessageDialog(null, e.getMessage(), "Error - ParserConfigurationException", JOptionPane.ERROR_MESSAGE);
    }
    catch(SAXException e) {
      JOptionPane.showMessageDialog(null, e.getMessage(), "Error - SAXException", JOptionPane.ERROR_MESSAGE);
    }
    catch(IOException e) {
      JOptionPane.showMessageDialog(null, e.getMessage(), "Error - IOException", JOptionPane.ERROR_MESSAGE);
    }
    catch(NumberFormatException e) {
      JOptionPane.showMessageDialog(null, e.getMessage(), "Error - NumberFormatException", JOptionPane.ERROR_MESSAGE);
    }
    catch(DOMException e) {
      JOptionPane.showMessageDialog(null, e.getMessage(), "Error - DOMException", JOptionPane.ERROR_MESSAGE);
    }

    return false;
  }

  /**
   * Remove activity (identified by ID) from file.
   * Invokes removeActivity(int id) after getting the ID from "activity".
   * 
   * @param activity activity to remove
   * @return true if the operation was successful, otherwise false
   */
  public static boolean removeActivity(Activity activity) {
    return removeActivity(activity.getID());
  }

  /**
   * Remove activity (identified by ID) from file.
   * 
   * @param id ID of activity to remove
   * @return true if the operation was successful, otherwise false
   */
  public static boolean removeActivity(int id) {
    try {
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
      Document doc = dBuilder.parse(new File(Constant.XML_ACTIVITIES_FILE));

      //optional, but recommended
      //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
      doc.getDocumentElement().normalize();

      NodeList nodeList = doc.getElementsByTagName(Constant.XML_ACTIVITY_TAG);

      for(int i = 0, h = nodeList.getLength(); i < h; i++) {
        Node node = nodeList.item(i);
        if(node.getNodeType() == Node.ELEMENT_NODE) {
          Element element = (Element)node;
          if(element.getAttribute(Constant.XML_ID).equals(Integer.toString(id))) {
            element.getParentNode().removeChild(element);
            return createActivityXML(doc);
          }
        }
      }
    }
    catch(ParserConfigurationException e) {
      JOptionPane.showMessageDialog(null, e.getMessage(), "Error - ParserConfigurationException", JOptionPane.ERROR_MESSAGE);
    }
    catch(SAXException e) {
      JOptionPane.showMessageDialog(null, e.getMessage(), "Error - SAXException", JOptionPane.ERROR_MESSAGE);
    }
    catch(IOException e) {
      //System.out.println("nincs fájl, nincs olvasás se");
      //e.printStackTrace();
    }
    catch(NumberFormatException e) {
      JOptionPane.showMessageDialog(null, e.getMessage(), "Error - NumberFormatException", JOptionPane.ERROR_MESSAGE);
    }
    catch(DOMException e) {
      JOptionPane.showMessageDialog(null, e.getMessage(), "Error - DOMException", JOptionPane.ERROR_MESSAGE);
    }

    return false;
  }

  /**
   * Get "activity" from file by (start) date.
   * 
   * @param year desired year
   * @param month desired month
   * @param day desired day of month
   * @return new Activity class if the operation was successful, otherwise null
   */
  public static ArrayList<Activity> getActivityByDate(int year, int month, int day) {
    ArrayList<Activity> activityList = new ArrayList<>();

    try {
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
      Document doc = dBuilder.parse(new File(Constant.XML_ACTIVITIES_FILE));

      //optional, but recommended
      //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
      doc.getDocumentElement().normalize();

      NodeList nodeList = doc.getElementsByTagName(Constant.XML_ACTIVITY_TAG);

      for(int i = 0, h = nodeList.getLength(); i < h; i++) {
        Node node = nodeList.item(i);
        if(node.getNodeType() == Node.ELEMENT_NODE) {
          Element element = (Element)node;
          DateTime d = new DateTime(element.getElementsByTagName(Constant.XML_START).item(0).getTextContent());
          if((d.getYear() == year) & (d.getMonthOfYear() == month) & (d.getDayOfMonth() == day)) {
            activityList.add(element2Activity(element));
          }
        }
      }
      return activityList;
    }
    catch(ParserConfigurationException e) {
      JOptionPane.showMessageDialog(null, e.getMessage(), "Error - ParserConfigurationException", JOptionPane.ERROR_MESSAGE);
    }
    catch(SAXException e) {
      JOptionPane.showMessageDialog(null, e.getMessage(), "Error - SAXException", JOptionPane.ERROR_MESSAGE);
    }
    catch(IOException e) {
    }
    catch(NumberFormatException e) {
      JOptionPane.showMessageDialog(null, e.getMessage(), "Error - NumberFormatException", JOptionPane.ERROR_MESSAGE);
    }
    catch(DOMException e) {
      JOptionPane.showMessageDialog(null, e.getMessage(), "Error - DOMException", JOptionPane.ERROR_MESSAGE);
    }

    return null;
  }

  /**
   * Get "activity" from file by ID.
   * Invokes getActivityByString(String s, String field) after converting "s" to String
   * and field = Constant.XML_ID.
   * 
   * @param id the ID of the desired activity
   * @return new Activity class if the opertion was successful, otherwise null
   */
  public static Activity getActivityByID(int id) {
    return getActivityByString(Integer.toString(id), Constant.XML_ID).get(0);
  }

  /**
   * Get "activity" from file depending on the input string and field combination.
   * 
   * @param s this is what we are looking for in "field". Should be activity id, comment or category
   * @param field this is where we are looking for "s". Should be XML_ID, XML_COMMENT, XML_CATEGORY
   * @return new Activity class if the opertion was successful, otherwise null
   */
  public static ArrayList<Activity> getActivityByString(String s, String field) {
    ArrayList<Activity> activityList = new ArrayList<>();

    try {
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
      Document doc = dBuilder.parse(new File(Constant.XML_ACTIVITIES_FILE));

      //optional, but recommended
      //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
      doc.getDocumentElement().normalize();

      NodeList nodeList = doc.getElementsByTagName(Constant.XML_ACTIVITY_TAG);

      for(int i = 0, h = nodeList.getLength(); i < h; i++) {
        Node node = nodeList.item(i);
        if(node.getNodeType() == Node.ELEMENT_NODE) {
          Element element = (Element)node;
          switch(field) {
            case Constant.XML_ID:
              if(element.getAttribute(Constant.XML_ID).equals(s)) {
                activityList.add(element2Activity(element));
                return activityList;
              }
            case Constant.XML_COMMENT:
              if(element.getElementsByTagName(Constant.XML_COMMENT).item(0).getTextContent().equals(s)) {
                activityList.add(element2Activity(element));
              }
              break;
            case Constant.XML_CATEGORY:
              if(element.getElementsByTagName(Constant.XML_CATEGORY).item(0).getTextContent().equals(s)) {
                activityList.add(element2Activity(element));
              }
              break;
            default:
              return null;
          }
        }
      }
      return activityList;
    }
    catch(ParserConfigurationException e) {
      JOptionPane.showMessageDialog(null, e.getMessage(), "Error - ParserConfigurationException", JOptionPane.ERROR_MESSAGE);
    }
    catch(SAXException e) {
      JOptionPane.showMessageDialog(null, e.getMessage(), "Error - SAXException", JOptionPane.ERROR_MESSAGE);
    }
    catch(IOException e) {
      //System.out.println("nincs fájl, nincs olvasás se");
      //e.printStackTrace();
    }
    catch(NumberFormatException e) {
      JOptionPane.showMessageDialog(null, e.getMessage(), "Error - NumberFormatException", JOptionPane.ERROR_MESSAGE);
    }
    catch(DOMException e) {
      JOptionPane.showMessageDialog(null, e.getMessage(), "Error - DOMException", JOptionPane.ERROR_MESSAGE);
    }

    return null;
  }

  /**
   * Creates an Activity class based on the given XML element.
   * 
   * @param element element to convert to Activity class
   * @return new Activity class
   */
  private static Activity element2Activity(Element element) {
    int activityID = Integer.parseInt(element.getAttribute(Constant.XML_ID));
    String comment = element.getElementsByTagName(Constant.XML_COMMENT).item(0).getTextContent();
    int categoryID = Integer.parseInt(element.getElementsByTagName(Constant.XML_CATEGORY).item(0).getTextContent());
    DateTime start = new DateTime(element.getElementsByTagName(Constant.XML_START).item(0).getTextContent());
    DateTime end = new DateTime(element.getElementsByTagName(Constant.XML_END).item(0).getTextContent());
    Category category = getCategoryByID(categoryID);
    Activity a = new Activity(activityID, comment, category, start, end);

    return a;
  }

  /**
   * Create file with the given XML data.
   * 
   * @param doc XML data
   * @return true if the opertion was successful, otherwise false
   */
  private static boolean createActivityXML(Document doc) {
     // write the "doc" to xml file
    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    Transformer transformer;
    try {
      transformer = transformerFactory.newTransformer();
    }
    catch(TransformerConfigurationException e) {
      JOptionPane.showMessageDialog(null, e.getMessage(), "Error - TransformerConfigurationException", JOptionPane.ERROR_MESSAGE);
      return false;
    }
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

    DOMSource source = new DOMSource(doc);

    StreamResult result = new StreamResult(new File(Constant.XML_ACTIVITIES_FILE));

    try {
      transformer.transform(source, result);
      return true;
    }
    catch(TransformerException e) {
      JOptionPane.showMessageDialog(null, e.getMessage(), "Error - TransformerException", JOptionPane.ERROR_MESSAGE);
      return false;
    }
  }

  /**
   * Creates the categories.xml file from pre-defined categories.
   * 
   * @return true if the opertion was successful, otherwise false
   */
  public static boolean createCategoriesXML() {
    return createCategoriesXML(Category.getDefaultCategories());
  }

  /**
   * Creates the categories.xml from the given Category class.
   * 
   * @param c from this Category class will be the XML document generated
   * @return true if the opertion was successful, otherwise false
   */
  public static boolean createCategoriesXML(Category c) {
    if(c == null) {
      c = Category.getDefaultCategories();
    }

    try {
      DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

      // root elements
      Document doc = docBuilder.newDocument();
      Element rootElement = doc.createElement(Constant.APP_NAME + Constant.XML_CATEGORIES_ROOT);
      doc.appendChild(rootElement);

      // category elements
      categories2document(c, doc, rootElement);

      // write the "doc" to xml file
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer;
      try {
        transformer = transformerFactory.newTransformer();
      }
      catch(TransformerConfigurationException e) {
        JOptionPane.showMessageDialog(null, e.getMessage(), "Error - TransformerConfigurationException", JOptionPane.ERROR_MESSAGE);
        return false;
      }
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

      DOMSource source = new DOMSource(doc);

      StreamResult result = new StreamResult(new File(Constant.XML_CATEGORIES_FILE));

      try {
        transformer.transform(source, result);
        return true;
      }
      catch(TransformerException e) {
        JOptionPane.showMessageDialog(null, e.getMessage(), "Error - TransformerException", JOptionPane.ERROR_MESSAGE);
        return false;
      }
    }
    catch(ParserConfigurationException e) {
      JOptionPane.showMessageDialog(null, e.getMessage(), "Error - ParserConfigurationException", JOptionPane.ERROR_MESSAGE);
    }
    catch(DOMException e) {
      JOptionPane.showMessageDialog(null, e.getMessage(), "Error - DOMException", JOptionPane.ERROR_MESSAGE);
    }

    return false;
  }

  /**
   * Generates XML document from a given Category class.
   * 
   * @param c from this Category class will be the XML document generated
   * @param doc this is the Document to generate to
   * @param element this will be the parent element for further elements
   * @return xml document
   */
  private static Document categories2document(Category c, Document doc, Element element) {
    Element category = doc.createElement(Constant.XML_CATEGORY_TAG);
    element.appendChild(category);

    category.setAttribute(Constant.XML_ID, Integer.toString(c.getID()));
    category.setAttribute(Constant.XML_NAME, c.getName());
    category.setAttribute(Constant.XML_COLOR, Category.color2hex(c.getColor()));
    category.setAttribute(Constant.XML_PREDEFINED, (c.isPredefined() ? "true" : "false"));
    if(c.getID() != 0) {
      category.setAttribute(Constant.XML_PARENT, Integer.toString(c.getParentCategory().getID()));
    }

    for(int i = 0, size = c.getSubCount(); i < size; i++) {
      categories2document(c.getSubAt(i), doc, category);
    }

    return doc;
  }

  /**
   * Get Category by ID.
   * 
   * @param id the ID we are looking for
   * @return new Category class
   */
  public static Category getCategoryByID(int id) {
    try {
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
      Document doc = dBuilder.parse(new File(Constant.XML_CATEGORIES_FILE));

      //optional, but recommended
      //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
      doc.getDocumentElement().normalize();

      NodeList nodeList = doc.getElementsByTagName(Constant.XML_CATEGORY_TAG);

      for(int i = 0, h = nodeList.getLength(); i < h; i++) {
        Node node = nodeList.item(i);
        if(node.getNodeType() == Node.ELEMENT_NODE) {
          Element element = (Element)node;
          if(element.getAttribute(Constant.XML_ID).equals(Integer.toString(id))) {
            Category c = new Category(Integer.parseInt(element.getAttribute(Constant.XML_ID)),
                                      element.getAttribute(Constant.XML_NAME),
                                      Color.decode(element.getAttribute(Constant.XML_COLOR)),
                                      element.getAttribute(Constant.XML_PREDEFINED).equals("true"));
            return  c;
          }
        }
      }
    }
    catch(ParserConfigurationException e) {
      JOptionPane.showMessageDialog(null, e.getMessage(), "Error - ParserConfigurationException", JOptionPane.ERROR_MESSAGE);
    }
    catch(SAXException e) {
      JOptionPane.showMessageDialog(null, e.getMessage(), "Error - SAXException", JOptionPane.ERROR_MESSAGE);
    }
    catch(IOException e) {
      if(createCategoriesXML(Category.getDefaultCategories())) {
        return getCategoryByID(id);
      }
    }
    catch(NumberFormatException e) {
      JOptionPane.showMessageDialog(null, e.getMessage(), "Error - NumberFormatException", JOptionPane.ERROR_MESSAGE);
    }
    catch(DOMException e) {
      JOptionPane.showMessageDialog(null, e.getMessage(), "Error - DOMException", JOptionPane.ERROR_MESSAGE);
    }

    return null;

  }

  /**
   * Get all category from file.
   * 
   * @return new Category[] class
   */
  public static Category getCategories() {
    try {
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
      Document doc = dBuilder.parse(new File(Constant.XML_CATEGORIES_FILE));

      //optional, but recommended
      //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
      doc.getDocumentElement().normalize();

      Element element = doc.getDocumentElement();

      Category c;
      if(element.hasChildNodes()) {
        NodeList children = element.getChildNodes();
        if(element.getTagName().equals(Constant.APP_NAME + Constant.XML_CATEGORIES_ROOT)) {
          for(int i = 0, l = children.getLength(); i < l; i++) {
            Node child = children.item(i);
            if(child.getNodeName().equals(Constant.XML_CATEGORY_TAG)) {
              c = getSubCategories(child)[0];
              return c;
            }
          }
        }
      }

      return null;
    }
    catch(ParserConfigurationException e) {
      JOptionPane.showMessageDialog(null, e.getMessage(), "Error - ParserConfigurationException", JOptionPane.ERROR_MESSAGE);
    }
    catch(SAXException e) {
      JOptionPane.showMessageDialog(null, e.getMessage(), "Error - SAXException", JOptionPane.ERROR_MESSAGE);
    }
    catch(IOException e) {
      if(createCategoriesXML(Category.getDefaultCategories())) {
        return getCategories();
      }
    }
    catch(NumberFormatException e) {
      JOptionPane.showMessageDialog(null, e.getMessage(), "Error - NumberFormatException", JOptionPane.ERROR_MESSAGE);
    }
    catch(DOMException e) {
      JOptionPane.showMessageDialog(null, e.getMessage(), "Error - DOMException", JOptionPane.ERROR_MESSAGE);
    }

    return null;

  }

  /**
   * Get sub-category and it's children.
   * 
   * @param child 
   * @return Category array
   */
  private static Category[] getSubCategories(Node child) {
    ArrayList<Category> alc = new ArrayList<>();

    if(child.getNodeType() == Node.ELEMENT_NODE){
      Element element = (Element)child;
      Category c = new Category(Integer.parseInt(element.getAttribute(Constant.XML_ID)),
                                element.getAttribute(Constant.XML_NAME),
                                Color.decode(element.getAttribute(Constant.XML_COLOR)),
                                element.getAttribute(Constant.XML_PREDEFINED).equals("true"));

      alc.add(c);

      if(element.hasChildNodes()){
        NodeList list = element.getChildNodes();
        Category[] tempCat;
        for(int i = 0, l = list.getLength(); i < l; i++){
          tempCat = getSubCategories(list.item(i));
          Category.linkCategories(c, tempCat);
        }
      }
    }

    Category[] tempCat = alc.toArray(new Category[alc.size()]);

    return tempCat;
  }

  /**
   * Returns the last ID in the XML file.
   * 
   * @param rootElement root XML element in which we are searching for ids
   * @param tag looking for IDs in this kind of element
   * @return last (largest) ID
   */
  protected static int getLastID(Element rootElement, String tag) {
    int currentID = 0;
    NodeList children = rootElement.getChildNodes();
    Node current;
    for(int i = 0, count = children.getLength(); i < count; i++) {
      current = children.item(i);
      if(current.getNodeType() == Node.ELEMENT_NODE) {
        Element element = (Element)current;
        if(element.getTagName().equals(tag)) {
          int tempID = Integer.parseInt(element.getAttribute(Constant.XML_ID));
          currentID = getMax(currentID, tempID);
        }
      }
    }

    return currentID;
  }

  /**
   * Returns the greater number between currentID and newID.
   * 
   * @param currentID number to compare
   * @param newID number to compare
   * @return returns currentID if it is larger than newID, otherwise returns newID
   */
  private static int getMax(int currentID, int newID) {
    if(currentID > newID) {
      return currentID;
    }

    return newID;
  }
}
