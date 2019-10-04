package modeles;

import java.util.ArrayList;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

public class Category {
	private String name;
	private String id;
	private ArrayList<Branch> branches;
	private Boolean isSelected=false;
	
	
	public Category(String name, String id) {
		
		this.name = name;
		this.id = id;
		branches=new ArrayList<>();
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
	public ArrayList<Branch> getBranches() {
		return this.branches;
	}
	public void setBranch(ArrayList<Branch> branches) {
		this.branches = branches;
	}
	public void AddBranch(Branch branche) {
		this.branches.add(branche);
	}
	public void removBranch(Branch branche) {
		this.branches.remove(branche);
	}
	/*
	 * get the selected branch if exist
	 * if the selected branch has  selected branch then the return will be the selected child 
	 * 
	 */
	public Branch getSelectedBranch() {
		for (Branch b : branches) {
			if(b.isIsselected())
				return b.getSelectedBranch();
		}
		return null;
	}

	
	public static ArrayList<Category> parsCategory(Document categoryhPage){
		ArrayList<Category> list=new ArrayList<>();
		Elements HTMLCategorys = categoryhPage.getElementsByAttributeValue(Constants.Attname,Constants.Value);
		
		for(Node n :HTMLCategorys) {
			if(n.attributes().get("class").equals("treeline"))
			if(n instanceof Element) {
				Element e=(Element) n.childNode(1).childNode(0);
				Category c=new Category(e.ownText(),e.attr("href").substring(26, e.attr("href").length()-2));
				list.add(c);
			}
		}
		 
		return list;
	 }
	
	public Boolean isSelected() {
		return isSelected;
	}
	public void setIsSelected(Boolean isSelected) {
		this.isSelected = isSelected;
	}
	
	@Override
	public String toString() {
		return "name "+name+"  id:  "+id;
	}
	public static ArrayList<String > tableToString(ArrayList<Category> listC){
		ArrayList<String> newTab=new ArrayList<>();
		for (Category p : listC) {
			newTab.add(p.name);
		}
		return newTab;
	}
	
}
