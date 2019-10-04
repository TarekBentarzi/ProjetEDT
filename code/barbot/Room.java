package code.barbot;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import modeles.RoomV2;

public class Room implements Comparable<Room>, Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5729197302183266145L;
	public  String name;
	public  String site;
	public  String type;
	public  String comment;
	
	public static final Pattern room = Pattern.compile(
			"[ ]?("
		   + "(CC [0-9A-Za-z]* [0-9A-Z]*)|"
           + "([A-D][0-9]? amphi A. Boulle[ ]?)|"
		   + "(B[ ]?[0-9]*([ ]?-[ ]?[+A-Z]*)?)|"
		   + "(PF [ 0-9A-Za-z]*[ ]?(CMC)?)|"
		   + "(PYR [ 0-9A-Za-z]*[ ]?)|"
		   + "([0-9]+ PYR(AMIDE)?[ ]?)|"
		   + "([0-9]+ ECO MAIL[ ]?)|"
		   + "([0-9]+ MLRI MAIL[ ]?)|"
		   + "([0-9]+ [A-Z] DRT[ ]?)|"
		   + "([0-9]+ MIEE MAIL)|"
		   + "(8 auditorium - MSE)|"
		   + "(Amphi MIEE( MAIL DES MECHES)?)|"
		   + "([0-9]* amphi [ 0-9A-Za-z]*-?[ ]?(([iT][0-9] CC)|(Eco))?)|"
		   + "(Amphi [A-Z] SITE DU MAIL DES MECHES - IAE)|"
           + "(Amphi [A-Z] MAIL DES MECHES( - IAE)?)|"
           + "(AMPHI [0-9] MED)|"
           + "(Amphi [A-Z][0-9])|"
           + "(Grand Amphithéâtre)|"
		   + "(nulle part pour l'instant - DSI)|"
		   + "(Demande de salle à DSI)"
//     	   + "([0-9]* [A-Z ]*)"		
			+ ")([ ]?[(]([^()]*)[)])?");

	public Room(String name,String comment){
		this.name = name;
		this.comment = comment != null ? comment : "";
		if (name.contains("CC") || name.contains("CMC")){
			this.site = "CC";
		}else if (name.contains("MAIL")){
			this.site = "MAIL";
		}else if (name.contains("PYR")){
			this.site = "PYR";
        }else if (name.contains("Boulle")){
            this.site ="Boulle";
        }else if (name.contains("MED")){
            this.site ="MED";
        }else this.site ="";
		
		if(name.contains("CC P4 044")
				|| name.contains("CC P4 047")
				|| name.contains("CC P4 051")
				|| name.contains("CC P4 038")
				|| name.contains("CC P4 042")
				|| name.contains("CC T3 304")
				|| name.contains("CC T3 305")
				|| name.contains("CC T3 306")
				|| name.contains("CC T3 307")
				|| name.contains("CC T3 255")
				|| name.contains("CC T3 258")
				|| name.contains("CC T3 267")
				|| name.contains("CC T3 268")
				|| name.contains("CC P4 018")
				|| name.contains("CC P4 019")
				){
			this.type = "TPInfo";
		}else if(name.contains("CC P1 120")
				|| name.contains("CC P1 128")){
			this.type = "TPChimie";
		}else if(name.contains("CC P4 108")){
			this.type = "TPChimieBio";
		}else this.type= (name.contains("Amphi") || name.contains("amphi") || name.contains("auditorium") ? "amphi" : "");
	}
	public Room(String name){
		this(name,"");
	}
	
	@Override
	public String toString(){
		return type + ":" + name/*+"("+comment+")"*/;
	}
	public String toXML() {
		return "<room name=\""+name+"\">"
				+ ((type != "")?("<type>"+type+"</type>"):"") 
				+ ((site != "")?("<site>"+site+"</site>"):"")
				+ "</room>";			
	}
	
	public static TreeSet<Room> parse(String s){
		TreeSet<Room> set = new TreeSet<>();
		int pos =0;
		Matcher m = room.matcher(s);
		while(m.lookingAt()){
			/*for(int i=0;i<8;i++){
				System.out.println("groupe"+i + ":"+m.group(i));
			}*/
			set.add(new Room(m.group(1),m.group(7)));
			pos += m.end(0);
			m.reset(s.substring(pos));		}
		if(!m.hitEnd()){
			String notparse = s.substring(pos);
			if(! notparse.startsWith("Demande"))System.err.println("Fail to parse:'"+ notparse +"' in '"+s+"'");
		}
		
		return set;
	}
	public Room() {
		
	}
	@Override
	public int compareTo(Room o) {
		int i = type.compareTo(o.type);
		if(i != 0)return i;
		i = site.compareTo(o.site);
		if(i != 0)return i;		
		return name.compareTo(o.name);
	}

	public static <V extends Comparable<V>,T extends Collection<V>> int compareLexico(T c1,T c2) {
		Iterator<V> it1 = c1.iterator();
		Iterator<V> it2 = c2.iterator();
		while(it1.hasNext()) {
			if( !it2.hasNext() )return 1;
			int c = it1.next().compareTo(it2.next());
			if(c != 0 )return c;
		}
		if(it2.hasNext())return -1;
		return 0;
	}
	
	public static void main(String[] args) {
		Collection<Room> ro = parse("CC PF 901 (préfabriqué)");
		ro.addAll(parse("CC P4 047 Demande de salle à DSI"));
		ro.addAll(parse("CC P2 P36 CC P2 P33"));
		ro.addAll(parse("205 MLRI MAIL"));
		ro.addAll(parse("312 ECO MAIL"));
		ro.addAll(parse("103 amphi SITE DU MAIL DES MECHES - Eco"));
		ro.addAll(parse("312 ECO MAIL"));
		ro.addAll(parse("CC i1 P28 (Pk bat i1)"));
		ro.addAll(parse("114 A DRT"));
		ro.addAll(parse("4 amphi - i2 CC (ex Gris)" ));//"4 amphi - i2 CC (ex Gris)" CC P2 P36 CC P2 P33
		ro.addAll(parse("B 501 - INFO+TNI"));
		ro.addAll(parse("B 201-VNI"));
		ro.addAll(parse("B 204 B 205 B 203-VNI"));
		ro.addAll(parse("Demande à LETTRES - CMC : salle de 31 à 40 places"));
        ro.addAll(parse("B2 amphi A. Boulle "));
        ro.addAll(parse("AMPHI 4 MED"));
        ro.addAll(parse("8 auditorium - MSE"));
        ro.addAll(parse("Amphi MIEE MAIL DES MECHES"));
        ro.addAll(parse("104 amphi MAIL DES MECHES - Eco 103 amphi MAIL DES MECHES - Eco CC P1 028 CC P1 021"));
        ro.addAll(parse("7 amphi - T2 CC (ex T500)"));
        System.out.println(ro);

	}
public RoomV2 YgetRoomV2() {
	RoomV2 rv2=new RoomV2();
	rv2.setName(name);
	rv2.setSite(site);
	rv2.setComment(comment);
	rv2.setType(type);
		return rv2;
	}
public static TreeSet< RoomV2> YgetRoomsV2(TreeSet<Room> list){
	TreeSet<RoomV2> listRoomsV2=new TreeSet<RoomV2>();
	for (Room room : list) {
		listRoomsV2.add(room.YgetRoomV2());
	}
	return listRoomsV2;
}
public static TreeSet< Room> YgetRooms(TreeSet<RoomV2> listV2){
	TreeSet<Room> listRooms=new TreeSet<Room>();
	for (RoomV2 roomv2 : listV2) {
		listRooms.add(roomv2.toRoom());
	}
	return listRooms;
}
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
}
