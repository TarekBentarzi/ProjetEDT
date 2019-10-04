package modeles;

import java.util.ArrayList;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;


/*
 * Represent a project 
 * 2018/2019 
 */
public class Project {
	private String name;
	private String id;
	private ArrayList<Category> categorys;
	
	protected static final String Attname = "name";
	protected static final String ValueProjectId = "projectId";
	

	public Project(String name, String id) {
		this.name = name;
		this.id = id;
		categorys=new ArrayList<>();
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public ArrayList<Category> getCategorys() {
		return this.categorys;
	}
	
	public void setCategorys(ArrayList<Category> categorys) {
		this.categorys = categorys;
	}
	
	public void AddCategorys(Category category) {
		this.categorys.add(category);
	}
	
	public void removCategory(Category category) {
		this.categorys.remove(category);
	}
	
	/*
	 * get a list of project from Document (html code)
	 * params : Document object 
	 * ret :  list of projects 
	 */
	public static ArrayList<Project> parsProject(Document doc){
		 
		ArrayList<Project> list=new ArrayList<>();
		Element HTMLProjects = doc.getElementsByAttributeValue(Attname,ValueProjectId).first();
		
		for(Node project : HTMLProjects.childNodes()){
			if(project instanceof Element){
				Element e = (Element)project;
				list.add(new Project(e.ownText(), e.attr(Constants.value)));
			}
		}
		 return list;
	 }
	 
	 @Override
	public String toString() {
		 return "name is : "+name+" \n id is :"+id+"\n";
	}
		


	public Category getCategory(String idCategory) {
		for (Category cat : categorys) {
			if(cat.getId().equals(idCategory)) return cat;
		}
		return null;
	}
		
	public Category getSelectedCategory() {
	
		for (Category cat : categorys) {
			if(cat.isSelected()) return cat;
		}
		
		return null;
	}
		
	public static ArrayList<String > tableToString(ArrayList<Project> listP){
		
		ArrayList<String> newTab=new ArrayList<>();
		for (Project p : listP) {
			newTab.add(p.name);
		}
		return newTab;
	}
}
