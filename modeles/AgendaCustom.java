package modeles;

import java.io.IOException;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import application.Main;
import code.barbot.Creneaux;
import controleur.PopUpController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import jfxtras.scene.control.agenda.Agenda;

/**
 * 
 * AgendaCustom est une classe heritant de la classe Agenda
 * de la bibliotheque Jfxtras.
 * 
 * Elle permet d'encapsuler un emploi du temps et de l'afficher
 *
 */

public class AgendaCustom extends Agenda{

	/**
	 * Emploi du temps contenu dans un agenda
	 */
	private TimeTable timeTable;
	
	/**
	 * Onglet dans lequel est affiche l'agenda
	 */
	private Tab parent;
	
	
	/**
	 * 
	 * Constructeur de l'agenda
	 * 
	 * @param t 
	 * 			Emploi du temps que l'agenda va contenir
	 */
	public AgendaCustom(TimeTable t) {
		super();
		agandaProp();
		newTimeTable(t);
		this.setAllowDragging(true);
		
		this.setAppointmentChangedCallback(new Callback<Agenda.Appointment, Void>() {
			@Override
			public Void call(Appointment ap) {
				Creneaux param=(Creneaux)ap;
				Boolean isDraged=true;
				
				for (Appointment app : appointments()) {
					Creneaux creneaux=(Creneaux) app;
					
					if(!app.equals(ap))
						if(!(creneaux.getEndTime().before(param.getStartTime())
							|| creneaux.getStartTime().after(param.getEndTime()))	
											)
							if(param.getProf().equalsIgnoreCase(creneaux.getProf())){
								((Creneaux)param).back();isDraged=false;
							} else if(param.getGroup().equalsIgnoreCase(creneaux.getGroup())){	
								((Creneaux)param).back();	isDraged=false;
							} else if(param.getSalle().equalsIgnoreCase(creneaux.getSalle())){
								((Creneaux)param).back();isDraged=false;
							}
						}

					if(isDraged)
					param.notify(new AgendaEvent(param, AgendaEvent.TYPE.DATE, new PairDate((Calendar) param.getOld_StartCal().clone(), (Calendar) param.getOld_EndCal().clone()),new PairDate((Calendar) param.getStartTime().clone(), (Calendar) param.getEndTime().clone())));

				return null;
			}
		});
	}

	/**
	 * Mutateur de l'onglet parent
	 * 
	 * @param parent
	 * 				Nouvel onglet
	 *				
	 */
	public void setParent(Tab parent) {
		this.parent=parent;
		for (Creneaux c : getTimeTable().getCreneauxsList()) {
			c.setParent(parent);
		}
	}
	
	/** 
	 * Accesseur de l'onglet parent
	 * 
	 * @return le noeud parent
	 * 			
	 */
	public Tab getparent() {
		return this.parent;
	}
	/**
	 * Mutateur de l'emploi du temps
	 * @param timeTable
	 * 			Met a jour l'emploi du temps
	 */
	public void setTimeTable(TimeTable timeTable) {
		this.timeTable = timeTable;
	}
	/**
	 * Accesseur de l'emploi du temps
	 * @return timeTable
	 */
	public TimeTable getTimeTable() {
		return timeTable;
	}
	
	/**
	 * Initialisation des proprietes de l'agenda
	 * @param agenda
	 */
	private void agandaProp() {
		this.newAppointmentCallbackProperty().set( (localDateTimeRange) -> {
			Creneaux c=new Creneaux(localDateTimeRange);
			getTimeTable().getCreneauxsList().add(c);
			c.setParent(parent);
			c.notify(new AgendaEvent(c, AgendaEvent.TYPE.ADD, new PairDate(null, null),new PairDate((Calendar) c.getStartTime().clone(), (Calendar) c.getEndTime().clone())));
			          return c;	          
			      });

		
		this.setActionCallback( (appointment) -> {
			try {
				editPopUp(appointment);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		});
				
		this.setEditAppointmentCallback( (appointment) -> {
			try {
				editPopUp(appointment);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		});
		
	}
	
	/**
	 *Afficher le Pop up d'edition
	 *d'un creneau 
	 *@param cr
	 * @throws IOException 
	 */
	private void editPopUp(Appointment cr) throws IOException {   
	
		Creneaux newCR=(Creneaux)cr;
		Creneaux oldCR=(Creneaux) newCR.clone();
		final Stage popUp = new Stage();
		    
		popUp.setTitle("Edit");
	    popUp.initModality(Modality.APPLICATION_MODAL);
	    AnchorPane root;
		
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/vue/PopUpCR.fxml"));
		PopUpController controller = new PopUpController(cr,this);
		fxmlLoader.setController(controller);
		root = fxmlLoader.load();				
		Scene scene = new Scene(root);
		popUp.setScene(scene);
		popUp.initOwner(Main.mainStage);
		popUp.show();
		
		popUp.setOnHidden(ev->{
			for(AgendaEvent e:oldCR.diff(newCR)){
				newCR.notify(e);
			}

			refresh();
		});

	}
	
	/**
	 * Remplacer l'emploi du temps par un nouvel emploi du temps
	 * @param timeTable2
	 * 				Nouvel emploi du temps qui remplace le precedant
	 */
	public void newTimeTable(TimeTable timeTable2) {
		setTimeTable(timeTable2);
		this.appointments().clear();
		this.appointments().addAll(getTimeTable().getCreneauxsList());
		
		for (Creneaux c : timeTable2.getCreneauxsList()) {
			c.setParent(parent);
		}
	
		if(timeTable2.getCreneauxsList().size()>0) {
			this.setDisplayedLocalDateTime(timeTable2.getCreneauxsList().get(0).getStartLocalDateTime());
		}
	}
	
	@Override
	public void refresh() {
		appointments().clear();
		this.appointments().addAll(getTimeTable().getCreneauxsList());
		if(getTimeTable().getCreneauxsList().size()>0) {
			this.setDisplayedLocalDateTime(getTimeTable().getCreneauxsList().get(0).getStartLocalDateTime());
		}
		super.refresh();
	}
	
	/**
	 * 
	 * Fonction pour verifier si les parametres de filtres sont nulls ou non
	 * 
	 * @param str
	 * 				
	 * @param filterStr
	 * @return Retourne si les champs du filtre sont nulls ou non
	 */
	private static boolean compareNullableString(String str, String filterStr) {
		
	    return (filterStr == null || filterStr.trim().equals("")) ? true : str.toUpperCase().contains(filterStr.toUpperCase());

	}
	
	/**
	 * Fonction permettant d'effectuer des filtres sur les creneaux
	 * 
	 * @param cours
	 * 				Valeur contenu dans le champ cours du formulaire
	 * @param prof
	 * 				Valeur contenu dans le champs prof du formulaire
	 * @param salle
	 * 				Valeur contenu dans le champs salle du formulaire
	 * @param group
	 * 				Valeur contenu dans le champs groupe du formulaire
	 */
	public void filter(String cours,String prof,String salle,String group) {
		List<Appointment> list = appointments().stream()
					.filter(e->compareNullableString(((Creneaux)e).getProf(),prof))
					.filter(e->compareNullableString(((Creneaux)e).getCours(),cours))
					.filter(e->compareNullableString(((Creneaux)e).getSalle(),salle))
					.filter(e->compareNullableString(((Creneaux)e).getGroup(),group))
					.collect(Collectors.toList());
		this.appointments().clear();
		this.appointments().addAll(list);
			super.refresh();
		
	}	
	
	/**
	 * Fonction permettant de desactiver toutes les actions d'un emploi du temps
	 * afin de le rendre non modifiable
	 */
	public void disableAction() {
		setAllowDragging(false);
		
		newAppointmentCallbackProperty().set( (localDateTimeRange) -> null);
		setAppointmentChangedCallback(appointment -> null);
		setEditAppointmentCallback(appointment -> null);
		setActionCallback(appointment -> null);
		setOnMouseClicked(null);
	}

}
