package modeles;

import java.util.ArrayList;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

/**
 * Une Branch encapsule les donnees issues des branches d'ADE
 *
 */

public class Branch {
	
	/**
	 * Le nom de la branche
	 */
	private String name;
	
	/**
	 * L'ID de la branche
	 */
	private String id;
	
	/**
	 * Liste des sous-branches de cette branche
	 */
	private ArrayList<Branch> branches;
	
	/**
	 * Defini si une branche est selectionnee ou non
	 */
	private boolean isselected;
	
	/**
	 * Defini si une branche est une feuille ou non
	 */
	private Boolean isLeaf;
	
	public Branch(String name, String id,Boolean isleaf) {
		this.name = name;
		this.id = id;
		this.isselected=false;
		this.isLeaf=isleaf;
		branches=new ArrayList<>();
	}
	
	/**
	 * Retourne le nom de la branche
	 * 
	 * @return Le nom de la branche sous forme de chaine de caractere
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Met a jour le nom de la branche 
	 * 
	 * @param name
	 * 			Le nouveau nom de la branche
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Retourne l'ID de la branche
	 * 
	 * @return L'ID de la branche sous forme d'entier
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Met a jour l'ID de la branche
	 * 
	 * @param id
	 * 			Le nouvel ID de la branche
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * Retourne la liste des sous-branches de la branche
	 * 
	 * @return La liste des sous-branches de la branche
	 */
	public ArrayList<Branch> getBranches() {
		return this.branches;
	}
	
	/**
	 * Met a jour la liste des sous-branches de la branche
	 * 
	 * @param branches
	 * 			La nouvelle liste de sous-branches
	 */
	public void setBranches(ArrayList<Branch> branches) {
		this.branches = branches;
	}
	
	/**
	 * Ajoute une nouvelle sous-branche a la liste des sous-branches
	 * 
	 * @param branche
	 * 			Branche a ajouter
	 */
	public void AddBranche(Branch branche) {
		this.branches.add(branche);
	}
	
	/**
	 * Supprime une branche a la liste des sous-branches
	 * 
	 * @param branche
	 * 			Branche a supprimer
	 */
	public void removBranche(Branch branche) {
		this.branches.remove(branche);
	}
	
	/**
	 * Retourne si une branche est une feuille ou non
	 * 
	 * @return si une branche est une feuille ou non
	 */
	public Boolean isLeaf() {
		return isLeaf;
	}
	
	/**
	 * Retourne si une branche est selectionnee ou non
	 * 
	 * @return si une branche est selectionnee ou non
	 */
	public boolean isIsselected() {
		return isselected;
	}
	
	/**
	 * Met a jour le champs de verification de selection
	 * 
	 * @param b
	 * 			Nouvel etat de la branche
	 */
	 public void setSelected(boolean b) {
		isselected = b;
	}
	
	/**
	 * Recupere les branches d'une categorie depuis un document HTML
	 *  
	 * @param htmlBranches
	 * 			Document HTML a parser
	 * @return la liste des branches recuperees
	 */
	public static ArrayList<Branch> parsBranch(Document htmlBranches){
		
		ArrayList<Branch> branches=new ArrayList<>();
		Elements HTMLBranches = htmlBranches.getElementsByAttributeValue(Constants.Attname,Constants.Value);
        int Max = 3;//the first Branch  has 3  &nbsp 
        for(Node n :HTMLBranches) { 
        	Element element=(Element)n;
        	if(n instanceof Element) {
        		int level=getNBSPCount(element);
				if(level>=Max) {
					if(level>Max){
						Max =getNBSPCount(element);
						branches.clear();
					}
					Element e=(Element) n.childNode(5).childNode(0);
					String str=e.attr("href");
					String id =str.substring(str.indexOf("(")+1, str.indexOf(","));
					Branch c=new Branch(e.ownText(),id,str.contains(Constants.leaf));
					branches.add(c);
				}
			}
        }
			return branches;
	 }
	
	 private static int getNBSPCount(Element n) {
		String html=n.html();
		return html.split(Constants.nbsp).length-1;
	}

	 
	 @Override
	public String toString() {
		return "name "+name+"  id:  "+id+" isSelected : "+isselected;
	}
	
	 /**
	  * Retourne la sous branche selectionnee
	  * 
	  * @return la sous branche selectionnee
	  */
	 public Branch getSelectedBranch() {
		for (Branch b : branches) {
			if(b.isIsselected())
				return b.getSelectedBranch();
			}
			return this;
	 }
	 
	 /**
	  * Convertie une liste de branch en une liste contenant
	  * seulement le nom des branches
	  * 
	  * @param listB
	  * 			Liste a convertir
	  * 
	  * @return Liste contenant les noms des branches
	  */
	 public static ArrayList<String > tableToString(ArrayList<Branch> listB){
		 ArrayList<String> newTab=new ArrayList<>();
		 for (Branch b : listB) {
			 newTab.add(b.name);
		 }
		 return newTab;
	}
	
	@Override
	public boolean equals(Object obj) {
		return this.getName().equals(((Branch) obj).getName())&&this.getId().equals(((Branch) obj).getId());
	}
	
}
