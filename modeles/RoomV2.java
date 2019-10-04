package modeles;

import code.barbot.Room;

public class RoomV2 implements Comparable<RoomV2>{

	/**
	 * 
	 */
	public  String name;
	public  String site;
	public  String type;
	public  String comment;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSite() {
		return site;
	}
	public void setSite(String site) {
		this.site = site;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public Room toRoom() {
		Room room=new Room(name,comment);
		return room;
	}
	
	@Override
	public int compareTo(RoomV2 o) {
		
		int i = type.compareTo(o.type);
		
		if(i != 0)return i;
		
		i = site.compareTo(o.site);
		
		if(i != 0)return i;		
		
		return name.compareTo(o.name);
	}
	@Override
	public String toString(){
		return type + ":" + name/*+"("+comment+")"*/;
	}


	
}
