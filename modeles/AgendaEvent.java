package modeles;

import java.util.ArrayList;

import code.barbot.Creneaux;

/**
 * 
 * Un AgendaEvent represente un evenement de journalisation des evenements. Lors d'une 
 * modification, un AgendaEvent est stocke dans le gestionnaire de journalisation,
 * afin de pouvoir eventuellement l'annuler
 * 
 * 
 *
 */

public class AgendaEvent {

	/**
	 * Le creneau sur lequel a lieu la modification
	 */
	private Creneaux creneaux;
	
	/**
	 * Agenda sur lequel a eu lieu la modification
	 */
	private AgendaCustom agenda;
	
	/**
	 * Le type de modification qui a eu lieu sur le creneau
	 */
	private TYPE type;
	
	/**
	 * Valeure effective avant la modification
	 */
	private Object old_value;
	
	/**
	 * Nouvelle valeure apres modification
	 */
	private Object new_Value;

	/**
	 * 
	 * Type de modification qui a eu lieu
	 *
	 */
	static public enum TYPE {
	    COURS,
	    SALLE,
	    PROF,
	    DATE,
	    GROUP,
	    ADD,
	    DELETE,
	    CAL,
	    START,
	    END,
	    WEEK
	}
	
	public AgendaEvent(Creneaux creneaux,AgendaEvent.TYPE type,Object old_value,Object new_value) {
		this.creneaux = creneaux;
		this.type=type;
		this.old_value=old_value;
		this.new_Value=new_value;
	}
	
	public AgendaEvent(AgendaCustom agenda,AgendaEvent.TYPE type,Object old_value,Object new_value) {
		this.agenda = agenda;
		this.type=type;
		this.old_value=old_value;
		this.new_Value=new_value;
	}
	
	/**
	 * Fonction permettant d'annuler une modification ( equivalent a CTRL + Z )
	 */
	@SuppressWarnings("unchecked")
	public void execut() {
		
		switch (type) {
			case ADD:
				creneaux.getParent().getAgenda().getTimeTable().getCreneauxsList().remove(creneaux);
				break;
		
			case DELETE:
				creneaux.getParent().getAgenda().getTimeTable().getCreneauxsList().add(creneaux);
				break;
		
			case WEEK:
				agenda.getTimeTable().setCreneauxsList((ArrayList<Creneaux>) old_value);
				break;

			default:
				creneaux.execut(type,old_value);
				break;
		}
	}
	
	/**
	 * Fonction permettant d'annuler une annulation ( equivalent de CTRL + Y )
	 */
	@SuppressWarnings("unchecked")
	public void unExecut() {
		
		switch (type) {
			
			case ADD:
				creneaux.getParent().getAgenda().getTimeTable().getCreneauxsList().add(creneaux);
				break;
			
			case DELETE:
				creneaux.getParent().getAgenda().getTimeTable().getCreneauxsList().remove(creneaux);
				break;
			
			case WEEK:
				agenda.getTimeTable().setCreneauxsList((ArrayList<Creneaux>) new_Value);
				break;

			default:
				creneaux.execut(type,new_Value);
				break;
		}
	}
	
	@Override
	public String toString() {
		return "event CR :"+creneaux+"event type :"+type+"event new value :"+new_Value+" old value :"+old_value;
	}
}
