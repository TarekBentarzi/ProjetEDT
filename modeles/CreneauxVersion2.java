package modeles;

import java.util.Date;
import java.util.TreeSet;

/*
 * c'est la meme que la classe creneaux 
 * Pour satisfaire mes conditions de la bibliotheque jackson
 */

public class CreneauxVersion2 {

	public  String nom;
	public  String prof;
	public  String group;
	public  String salle;
	public  String type;
	public  String codeApoge;
	public  String codePromo;
	public  String id;
	public  int duree;
	public  TreeSet<RoomV2> rooms;
	public  Date startDate;
	

	
	
	
	public void setNom(String nom) {
		this.nom = nom;
	}
	public void setProf(String prof) {
		this.prof = prof;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public void setSalle(String salle) {
		this.salle = salle;
	}
	public void setType(String type) {
		this.type = type;
	}
	public void setCodeApoge(String codeApoge) {
		this.codeApoge = codeApoge;
	}
	public void setCodePromo(String codePromo) {
		this.codePromo = codePromo;
	}
	public void setId(String id) {
		this.id = id;
	}
	public void setDuree(int duree) {
		this.duree = duree;
	}
	public void setRooms(TreeSet<RoomV2> rooms) {
		this.rooms = rooms;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public String getNom() {
		return nom;
	}
	public String getProf() {
		return prof;
	}
	public String getGroup() {
		return group;
	}
	public String getSalle() {
		return salle;
	}
	public String getType() {
		return type;
	}
	public String getCodeApoge() {
		return codeApoge;
	}
	public String getCodePromo() {
		return codePromo;
	}
	public String getId() {
		return id;
	}
	public int getDuree() {
		return duree;
	}
	public TreeSet<RoomV2> getRooms() {
		return rooms;
	}
	public Date getStartDate() {
		return startDate;
	}
	
	
}
