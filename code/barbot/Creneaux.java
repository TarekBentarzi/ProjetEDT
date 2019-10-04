package code.barbot;


import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jfxtras.scene.control.agenda.Agenda;
import jfxtras.scene.control.agenda.Agenda.AppointmentGroup;
import jfxtras.scene.control.agenda.Agenda.LocalDateTimeRange;
import modeles.AgendaEvent;
import modeles.CreneauxVersion2;
import modeles.PairDate;
import modeles.Tab;

import org.jsoup.nodes.Element;


/**
 *
 *	Un creneaux represente un creneau horaire sur un agenda avec
 *
 */

public class Creneaux extends Agenda.AppointmentImpl implements Comparable<Creneaux>, Serializable ,Cloneable{


	private static final long serialVersionUID = 1136538406936247910L;

	private static  Pattern pid = Pattern.compile("javascript:([a-zA-Z]*)[(]([^),]*)");
	private static  Pattern pheure = Pattern.compile("([0-9][0-9])h([0-9][0-9])");
	private static  Pattern pdur = Pattern.compile("([0-9]+)h(([0-9][0-9])min)?");
	private static  SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

	/**
	 *  Nom de creneaux
	 */
	private String nom;

	/**
	 * Date du creneaux
	 */
	private Calendar cal;

	/**
	 * Cours du creneaux
	 */
	private String cours;

	/**
	 * Enseignant du creneaux
	 */
	private String prof;

	/**
	 * Groupes ayant cours
	 */
	private String group;

	/**
	 * Salle ou il y a cours
	 */
	private String salle;

	/**
	 * Type du cours ( CD / TD / TP )
	 */
	private String type;

	/**
	 * Code apogee du cours
	 */
	private String codeApoge;

	/**
	 * Code de la promo
	 */
	private String codePromo;

	/**
	 * ID du creneaux
	 */
	private String id;

	/**
	 * Determine si a un cours a lieu toute la journee
	 */
	private boolean wholeDay;

	/**
	 * Appointment sur lequel sera affiche le creneau
	 */
	private AppointmentGroup aptGroup;

	/**
	 * Localisation du cours
	 */
	private String location;

	/**
	 * Duree du cours
	 */
	private int duree;

	/**
	 * Liste des salles ou il y a cours
	 */
	private TreeSet<Room> rooms;

	private Calendar old_StartCal;

	private Calendar old_EndCal;

    private Tab parent;

	public Calendar getOld_StartCal() {
		return old_StartCal;
	}

	public Calendar getOld_EndCal() {
		return old_EndCal;
	}


	public static int minOfDuration(String s) {
		 int h = 0;
		 int m = 0;

		 Matcher ma = pdur.matcher(s);
		 if(ma.matches()) {
			 h = Integer.parseInt(ma.group(1));
			 if (ma.group(3) !=null ) {
				m =  Integer.parseInt(ma.group(3));
			 }
		 }
		 return 60*h+m;
	 }

	@Override
	public String toString() {
		final SimpleDateFormat sdf2 = new SimpleDateFormat("EEE dd/MM/YY HH:mm");
		if(rooms.isEmpty())
			return sdf2.format(cal.getTime())+"min :\n"+nom+":\n"+group;

		return sdf2.format(cal.getTime())+"min :\n"+rooms+"':\n"+nom+":\n"+group;
	}

	public String toXML() {
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		StringBuilder sb = new StringBuilder();
		sb.append("<timeslot id=\""+id+"\" start=\""+sdf.format(cal.getTime())+"\" duration=\""+ duree +"\">");
		for(Room r:rooms)sb.append(r.toXML());
		if(!prof.isEmpty())sb.append("<teacher>"+prof+"</teacher>");
		sb.append("<course>"+cours+"</course>");
		sb.append("<name>"+nom+"</name>");
		sb.append("</timeslot>");
		return sb.toString();
	}

	public String toShortString(){
		final SimpleDateFormat sdf2 = new SimpleDateFormat("EEE dd/MM/YY HH:mm");
		final SimpleDateFormat sdf3 = new SimpleDateFormat("HH:mm");
		Calendar end = (Calendar) cal.clone();
        end.add(Calendar.MINUTE,duree);
		return nom+ " from "+sdf2.format(cal.getTime())+ " to "+sdf3.format(end.getTime());
	}

	public Creneaux() {
	}

	public Creneaux(LocalDateTimeRange l){
		LocalDateTime start=l.getStartLocalDateTime();
		Calendar c=Calendar.getInstance();
		c.set(start.getYear(), start.getMonthValue(), start.getDayOfMonth(), start.getHour(), start.getMinute());
		setCal(c);

		this.nom = "";
    	this.cours= "";
    	this.prof= "";
    	this.group = "";
    	this.salle="";
    	this.type="";
    	this.id="";
    	this.duree=l.getEndLocalDateTime().getSecond()-l.getStartLocalDateTime().getSecond();
    	this.rooms=new TreeSet<Room>();
    	this.codeApoge="";
    	this.codePromo="";

		this.setStartLocalDateTime(l.getStartLocalDateTime());
		this.setEndLocalDateTime(l.getEndLocalDateTime());

	}

	public Creneaux(Creneaux c) {
		this.nom = c.nom;
    	this.cal = c.cal;
    	this.cours= c.cours;
    	this.prof= c.prof;
    	this.group = c.group;
    	this.salle= c.salle;
    	this.type= c.type;
    	this.id= c.id;
    	this.duree= c.duree;
    	this.rooms= c.rooms;
    	this.codeApoge= c.codeApoge;
    	this.codePromo= c.codePromo;

    	setOppoitementDates();
	}

    public Creneaux(String nom, Calendar cal, String cours, String prof, String group, String salle, String type, String id, int duree, TreeSet<Room> rooms, String codeApoge, String codePromo){
    	this.nom = nom;
    	this.cal = cal;
    	this.cours= cours;
    	this.prof= prof;
    	this.group = group;
    	this.salle=salle;
    	this.type=type;
    	this.id=id;
    	this.duree=duree;
    	this.rooms=rooms;
    	this.codeApoge=codeApoge;
    	this.codePromo=codePromo;
        setOppoitementDates();
	}

    public Creneaux(String nom, Calendar cal, String cours, String id, int duree, TreeSet<Room> rooms) {
    	this(nom, cal, cours, "", "","","",id,duree,rooms,"","");
    }

    public Date getEnd() {
    	Calendar end = (Calendar) cal.clone();
        end.add(Calendar.MINUTE,duree);
        return end.getTime();
    }

	public Creneaux(Element n) throws ParseException {

		cal = Calendar.getInstance();

		Date d = sdf.parse(n.child(0).text());
		cal.setTime(d);

		nom = n.child(1).text();
		String str = n.child(1).toString();
		Matcher m = pid.matcher(str);
		if(m.find()){
			id = m.group(2);
		}else {
			id = "";
		}

		str = n.child(2).text();
		m = pheure.matcher(str);
		if(m.find()){
			int heure = Integer.parseInt(m.group(1));
			int minute = Integer.parseInt(m.group(2));
			cal.set(Calendar.HOUR_OF_DAY, heure);
			cal.set(Calendar.MINUTE, minute);
		}

		duree = minOfDuration(n.child(3).text());
	    codeApoge = n.child(4).text();
	    type = n.child(5).text();
		cours = n.child(7).text();
		codePromo = n.child(8).text();

		str = n.child(11).toString();
		group=n.child(11).text();


		prof = n.child(12).text();
		salle = n.child(13).text();
		rooms = Room.parse(salle);

         //set appoitemment dates
        setOppoitementDates();
	}


	private void setOppoitementDates() {
		this .setStartLocalDateTime(LocalDateTime.ofInstant(this.getCal().toInstant(), this.getCal().getTimeZone().toZoneId()));
		this.setEndLocalDateTime(LocalDateTime.ofInstant(this.getCal().toInstant().plusSeconds(duree*60), this.getCal().getTimeZone().toZoneId()));
	}

	@Override
	public int compareTo(Creneaux o) {
		int c = cal.compareTo(o.cal);
		if(c != 0 )return c;
		c = id.compareTo(o.id);
		if(c != 0 )return c;
		c = duree - o.duree;
		if(c != 0 )return c;
		c = Room.compareLexico(rooms, o.rooms);
		if(c != 0 )return c;
		return 0;
	}

	public static Creneaux getlast(TreeSet<Creneaux> cs, Date d) {
		Creneaux old = null;
		for(Creneaux c : cs) {
			if( c.cal.getTime().getTime() > d.getTime() )return old;
			old=c;
		}
		return old;
	}
	public static Creneaux getfirst(TreeSet<Creneaux> cs, Date d) {
		for(Creneaux c : cs) {
			if( c.cal.getTime().getTime() > d.getTime() )return c;
		}
		return null;
	}

	@Override
	public AppointmentGroup getAppointmentGroup() {
		return aptGroup;
	}

	@Override
	public String getDescription() {
		return nom;
	}

	@Override
	public String getLocation() {
		return this.location;
	}

	@Override
	public String getSummary() {
		return cours+"\n"+group+"\n"+salle+"\n"+prof ;
	}

	@Override
	public Boolean isWholeDay() {
		return wholeDay;
	}

	@Override
	public void setAppointmentGroup(AppointmentGroup arg0) {
		this.aptGroup=arg0;

	}

	@Override
	public void setDescription(String arg0) {

		super.setDescription(cours+"\n"+group+"\n"+salle+"\n"+prof);
		this.nom=arg0;
	}

	@Override
	public void setLocation(String arg0) {
		this.location=arg0;
	}

	@Override
	public void setSummary(String arg0) {
		this.nom=arg0;
	}

	@Override
	public void setWholeDay(Boolean arg0) {
		this.wholeDay=arg0;
	}

	public void setSalle(String salle) {
		this.salle=salle;

	}

	public String getSalle() {
		return this.salle;
	}

 public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public Calendar getCal() {
		return cal;
	}

	public void setCal(Calendar cal) {
		this.cal = cal;
	}

	public String getCours() {
		return cours;
	}

	public void setCours(String cours) {
		this.cours = cours;
	}

	public String getProf() {
		return prof;
	}

	public void setProf(String prof) {
		this.prof = prof;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCodeApoge() {
		return codeApoge;
	}

	public void setCodeApoge(String codeApoge) {
		this.codeApoge = codeApoge;
	}

	public String getCodePromo() {
		return codePromo;
	}

	public void setCodePromo(String codePromo) {
		this.codePromo = codePromo;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getDuree() {
		return duree;
	}

	public void setDuree(int duree) {
		this.duree = duree;
	}

	public TreeSet<Room> getRooms() {
		return rooms;
	}

	public void setRooms(TreeSet<Room> rooms) {
		this.rooms = rooms;
	}

	public CreneauxVersion2 getVersion2() {
		CreneauxVersion2 crV2=new CreneauxVersion2();
    	crV2.setNom(nom);
    	crV2.setProf(prof);
    	crV2.setGroup(group);
    	crV2.setSalle(salle);
    	crV2.setType(type);
    	crV2.setCodeApoge(codeApoge);
    	crV2.setCodePromo(codePromo);
    	crV2.setId(id);
    	crV2.setDuree(duree);
    	crV2.setStartDate(cal.getTime());
    	crV2.setRooms(Room.YgetRoomsV2(this.rooms));
    	return crV2;
     }

	@Override
	public void setEndLocalDateTime(LocalDateTime v) {
		super.setEndLocalDateTime(v);
		this.duree=(int) ChronoUnit.MINUTES.between( this.getStartLocalDateTime(),v);
	}


	/**
	 * modifier la date début du cours
	 */

	@Override
	public void setStartLocalDateTime(LocalDateTime v) {
		this.getCal().set(v.getYear(), v.getMonthValue()-1, v.getDayOfMonth(),v.getHour(), v.getMinute(), v.getSecond());
		super.setStartLocalDateTime(v);
		if(this.getEndLocalDateTime()!=null) this.duree=getEndLocalDateTime().getSecond()-v.getSecond();
	}

	//save the old value

	@Override
	public void setEndTime(Calendar value) {
		old_EndCal=getEndTime();
		super.setEndTime(value);
	}

	@Override
	public void setStartTime(Calendar value) {
		old_StartCal=getStartTime();
		super.setStartTime(value);
	}

	public void back() {
		super.setStartTime(old_StartCal);
		super.setEndTime(old_EndCal);
		this.cal=old_StartCal;
		super.setStartLocalDateTime(LocalDateTime.of(old_StartCal.get(Calendar.YEAR), old_StartCal.get(Calendar.MONTH)+1, old_StartCal.get(Calendar.DAY_OF_MONTH), old_StartCal.get(Calendar.HOUR_OF_DAY), old_StartCal.get(Calendar.MINUTE)));
		super.setEndLocalDateTime(LocalDateTime.of(old_EndCal.get(Calendar.YEAR), old_EndCal.get(Calendar.MONTH)+1, old_EndCal.get(Calendar.DAY_OF_MONTH), old_EndCal.get(Calendar.HOUR_OF_DAY), old_EndCal.get(Calendar.MINUTE)));
	}


	/**
	 * Convertie une liste de creneaux V2 en une liste de creneaux
	 *
	 * @param listV2
	 * 			La liste a convertir
	 * @return La liste convertie
	 */
	public static ArrayList<Creneaux> toCreneaux(ArrayList<CreneauxVersion2> listV2){
		ArrayList<Creneaux> list=new ArrayList<Creneaux>();

		for (CreneauxVersion2 v2 : listV2) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(v2.startDate);
			list.add(new Creneaux(v2.nom, cal, v2.nom, v2.prof, v2.group, v2.salle, v2.type, v2.id, v2.duree,Room.YgetRooms(v2.rooms), v2.codeApoge, v2.codePromo));
		}

		return list;
	}



	/**
	 * Convertie une liste de creneaux en creneaux V2
	 *
	 * @param list
	 * 			La liste a convertir
	 * @return La liste convertie
	 */
	public static ArrayList<CreneauxVersion2> toCreneauxVersion2(ArrayList<Creneaux> list){
		ArrayList<CreneauxVersion2> listV2=new ArrayList<CreneauxVersion2>();
		for (Creneaux creneaux : list) {
		listV2.add(creneaux.getVersion2());
			}
		return listV2;
	}

	public void setParent(Tab parent) {
	     this.parent = parent;
    }

	public Tab getParent() {
		return parent;
	}

	public void notify(AgendaEvent e) {
	  if(parent!=null) parent.notifyChange(e);
	  }

	public void execut(AgendaEvent.TYPE type, Object value) {
		switch (type) {
		case COURS:
			cours=((String) value);
			break;
		case SALLE:
			salle=((String)value);
			break;
		case PROF:
			prof=((String)value);
			break;
		case GROUP:
		    group=((String)value);
		    break;
		case START:
			super. setStartTime((Calendar) value);
		    setCal((Calendar) value);
		    super.setStartLocalDateTime(LocalDateTime.of((cal).get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE)));
		    this.duree=(int) (getEndTime().getTimeInMillis()-cal.getTimeInMillis())/60000;
		    break;
		case END:
			super.setEndTime((Calendar) value);
		    super.setEndLocalDateTime(LocalDateTime.of( ((Calendar)value).get(Calendar.YEAR),  ((Calendar)value).get(Calendar.MONTH)+1, ((Calendar)value).get(Calendar.DAY_OF_MONTH),  ((Calendar)value).get(Calendar.HOUR_OF_DAY), ((Calendar)value).get(Calendar.MINUTE)));
		    this.duree=(int) (getEndTime().getTimeInMillis()-cal.getTimeInMillis())/60000;
			break;
		case DATE://drag and drop back
			super.setStartTime(((PairDate) value).getStart());
			super.setEndTime(((PairDate) value).getEnd());
			setCal(((PairDate) value).getStart());
			super.setStartLocalDateTime(LocalDateTime.of(((PairDate) value).getStart().get(Calendar.YEAR), ((PairDate) value).getStart().get(Calendar.MONTH)+1, ((PairDate) value).getStart().get(Calendar.DAY_OF_MONTH), ((PairDate) value).getStart().get(Calendar.HOUR_OF_DAY), ((PairDate) value).getStart().get(Calendar.MINUTE)));
			super.setEndLocalDateTime(LocalDateTime.of(((PairDate) value).getEnd().get(Calendar.YEAR), ((PairDate) value).getEnd().get(Calendar.MONTH)+1, ((PairDate) value).getEnd().get(Calendar.DAY_OF_MONTH), ((PairDate) value).getEnd().get(Calendar.HOUR_OF_DAY), ((PairDate) value).getEnd().get(Calendar.MINUTE)));
			break;

		default:
			break;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codeApoge == null) ? 0 : codeApoge.hashCode());
		result = prime * result + ((codePromo == null) ? 0 : codePromo.hashCode());
		result = prime * result + ((cours == null) ? 0 : cours.hashCode());
		result = prime * result + duree;
		result = prime * result + ((group == null) ? 0 : group.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		result = prime * result + ((nom == null) ? 0 : nom.hashCode());
		result = prime * result + ((prof == null) ? 0 : prof.hashCode());
		result = prime * result + ((rooms == null) ? 0 : rooms.hashCode());
		result = prime * result + ((salle == null) ? 0 : salle.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + (wholeDay ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Creneaux other = (Creneaux) obj;
		if ( getStartTime() == null){
			if( other.getStartTime() == null) {
				return false;
			}
		} else if ( !getStartTime().getTime().equals(other.getStartTime().getTime())) {
			return false;
		}

		if (codeApoge == null) {
			if (other.codeApoge != null)
				return false;
		} else if (!codeApoge.equals(other.codeApoge)) {
			return false;
		}
		if (codePromo == null) {
			if (other.codePromo != null) {
				return false;
			}
		} else if (!codePromo.equals(other.codePromo)) {
			return false;
		}
		if (duree != other.duree) {
			return false;
		}
		if (group == null) {
			if (other.group != null) {
				return false;
			}
		} else if (!group.equals(other.group)) {
			return false;
		}
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (location == null) {
			if (other.location != null) {
				return false;
			}
		} else if (!location.equals(other.location)) {
			return false;
		}
		if (nom == null) {
			if (other.nom != null) {
				return false;
			}
		} else if (!nom.equals(other.nom)) {
			return false;
		}
		if (prof == null) {
			if (other.prof != null) {
				return false;
			}
		} else if (!prof.equals(other.prof)) {
			return false;
		}
		if (rooms == null) {
			if (other.rooms != null) {
				return false;
			}
		} else if (!rooms.equals(other.rooms)) {
			return false;
		}
		if (salle == null) {
			if (other.salle != null) {
				return false;
			}
		} else if (!salle.equals(other.salle)) {
			return false;
		}
		if (type == null) {
			if (other.type != null) {
				return false;
			}
		} else if (!type.equals(other.type)) {
			return false;
		}
		if (wholeDay != other.wholeDay) {
			return false;
		}
		return true;
	}

	public  ArrayList<AgendaEvent> diff(Creneaux creneaux) {
		ArrayList<AgendaEvent> events=new ArrayList<AgendaEvent>();
		if(!prof.equals(creneaux.getProf()))
			events.add(new AgendaEvent(creneaux, AgendaEvent.TYPE.PROF, prof, creneaux.getProf()));
		if(!salle.equals(creneaux.getSalle()))
			events.add(new AgendaEvent(creneaux, AgendaEvent.TYPE.SALLE, salle, creneaux.getSalle()));
		if(!group.equals(creneaux.getGroup()))
		events.add(new AgendaEvent(creneaux, AgendaEvent.TYPE.GROUP, group, creneaux.getGroup()));
		if(!cours.equals(creneaux.getCours()))
			events.add(new AgendaEvent(creneaux, AgendaEvent.TYPE.COURS, cours, creneaux.getCours()));


		if(Math.abs(getStartTime().getTimeInMillis()-creneaux.getStartTime().getTimeInMillis())>800)
		events.add(new AgendaEvent(creneaux, AgendaEvent.TYPE.START, getStartTime().clone(), creneaux.getStartTime().clone()));
		if(Math.abs(getEndTime().getTimeInMillis()-creneaux.getEndTime().getTimeInMillis())>800)
			events.add(new AgendaEvent(creneaux, AgendaEvent.TYPE.END, getEndTime().clone(), creneaux.getEndTime().clone()));

		return events;
	}

	@Override
	public Object clone()  {
		return new Creneaux(nom, cal, cours, prof, group, salle, type, id, duree, rooms, codeApoge, codePromo);
	}

}