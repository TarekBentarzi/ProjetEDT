package modeles;

import java.util.ArrayList;

import code.barbot.Creneaux;

public class TimeTable {

	public enum TYPE {
	    EMPTY_BASED,
	    ADE_BASED,
	    EXTERN_BASED
	}
	private String name;
	private String path;
	private ArrayList<Creneaux> creneauxsList;
	private Boolean isValide;
	private String hashCode;
	private TYPE type;
	
	public TimeTable() {
		this.creneauxsList = new ArrayList<>();
	}
	
	public void addCreneaux(Creneaux cr) {
		this.creneauxsList.add(cr);
	}
	
	public TimeTable(String name ,String path, ArrayList<Creneaux> creneauxsList,TYPE type) {
		super();
		this.path = path;
		this.creneauxsList = creneauxsList;
		this.type=type;
		this.isValide=true;
		this.name=name;
	}
	
	public String getPath() {
		return path;
	}
	
	public ArrayList<String> getPathList() {
		System.out.println(path);
		ArrayList<String> list=new ArrayList<String>();
		
		for (String choose : path.replace("[", "").replace("]", "").split(",")) {
			list.add(choose.trim());
		}
		System.out.println(list);

		return list;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public TimeTable(String name,String path, ArrayList<Creneaux> creneauxsList, Boolean isValide, String hashCode, TYPE type) {
		super();
		this.path = path;
		this.creneauxsList = creneauxsList;
		this.isValide = isValide;
		this.hashCode = hashCode;
		this.type = type;
		this.name=name;
	}
	
	public TYPE getType() {
		return type;
	}
	
	public void setType(TYPE type) {
		this.type = type;
	}
	
	public ArrayList<Creneaux> getCreneauxsList() {
		return creneauxsList;
	}
	
	public void setCreneauxsList(ArrayList<Creneaux> creneauxsList) {
		this.creneauxsList = creneauxsList;
	}
	
	public Boolean getIsValide() {
		return isValide;
	}
	
	public void setIsValide(Boolean isValide) {
		this.isValide = isValide;
	}
	
	public String getHashCode() {
		return hashCode;
	}
	
	public void setHashCode(String hashCode) {
		this.hashCode = hashCode;
	}
	
	public  TimeTableV2 toTimeTableV2() {
		TimeTableV2 v2=new TimeTableV2();
		v2.setCreneauxsList(Creneaux.toCreneauxVersion2(creneauxsList));
		v2.setIsValide(isValide);
		v2.setPath(path);
		v2.setHashCode(hashCode);
		v2.setName(name);
		
		return v2;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((creneauxsList == null) ? 0 : creneauxsList.hashCode());
		result = prime * result + ((hashCode == null) ? 0 : hashCode.hashCode());
		result = prime * result + ((isValide == null) ? 0 : isValide.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		TimeTable other = (TimeTable) obj;
		if (creneauxsList == null) {
			if (other.creneauxsList != null) {
				return false;
			}
		} else if (!creneauxsList.equals(other.creneauxsList)) {
			return false;
		}
		if (hashCode == null) {
			if (other.hashCode != null) {
				return false;
			}
		} else if (!hashCode.equals(other.hashCode)) {
			return false;
		}
		if (isValide == null) {
			if (other.isValide != null) {
				return false;
			}
		} else if (!isValide.equals(other.isValide)) {
			return false;
		}
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path)) {
			return false;
		}

		return true;
	}
	
}
