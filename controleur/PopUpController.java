package controleur;

import java.net.URL;
import java.util.ResourceBundle;

import code.barbot.Creneaux;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import jfxtras.scene.control.LocalDateTimeTextField;
import jfxtras.scene.control.agenda.Agenda.Appointment;
import modeles.AgendaCustom;
import modeles.AgendaEvent;


public class PopUpController implements Initializable{
	
	private Creneaux creneaux;
	private AgendaCustom agenda;
	
    @FXML
    private Button save;
    
    @FXML
    private Button del;
    
    @FXML
    private LocalDateTimeTextField start;
    
    @FXML
    private LocalDateTimeTextField end;
    
    @FXML
    private TextField cours;
   
    @FXML
    private TextField prof;
    
    @FXML
    private TextField group;
    
    @FXML
    private TextField classroom;
    
    @FXML
    private Label labProf,labSalle,labGroup;
    
    public PopUpController(Appointment cr, AgendaCustom agenda) {
		this.creneaux= (Creneaux) cr;
		this.agenda=agenda;
	}
    
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		start.setLocalDateTime(creneaux.getStartLocalDateTime());
		end.setLocalDateTime(creneaux.getEndLocalDateTime());
		cours.setText(creneaux.getCours());
		prof.setText(creneaux.getProf());
		classroom.setText(creneaux.getSalle());
		group.setText(creneaux.getGroup());
		save.setOnAction(e->saveApt());
		del.setOnAction(e->delete());
		
	}
	
	/**
	 * Sauvegarder la modification apport�e � un creneau
	 */
	public void saveApt(){	
		  //Kenza job ...
		for (Appointment app : agenda.appointments()) {
			Creneaux creneaux=(Creneaux) app;
		
			if((creneaux.getStartTime().after(creneaux.getStartTime()) && creneaux.getStartTime().before(creneaux.getEndTime()))
				||
			(creneaux.getEndTime().after(creneaux.getStartTime()) && creneaux.getEndTime().before(creneaux.getEndTime()))	
				||
			(creneaux.getStartTime().before(creneaux.getStartTime())&& creneaux.getEndTime().after(creneaux.getEndTime()))){
				
				if(!Controle(creneaux.getProf(),prof.getText(),labProf)) return;
				if(!Controle(creneaux.getSalle(),classroom.getText(),labSalle)) return;
				if(!Controle(creneaux.getSalle(),group.getText(),labGroup)) return;
			}
		}
		
		
		if(!cours.getText().equals(creneaux.getCours())) creneaux.setCours(cours.getText());
		
		if(!classroom.getText().equals(creneaux.getSalle())) creneaux.setSalle(classroom.getText());
		
		if(!prof.getText().equals(creneaux.getProf())) creneaux.setProf(prof.getText());
		
		if(!group.getText().equals(creneaux.getGroup())) creneaux.setGroup(group.getText());
		// from localdatetime to cal
		if(!start.getLocalDateTime().equals(creneaux.getStartLocalDateTime())) creneaux.setStartLocalDateTime(start.getLocalDateTime());
		
		if(!end.getLocalDateTime().equals(creneaux.getEndLocalDateTime())) creneaux.setEndLocalDateTime(end.getLocalDateTime());
		
		Stage stage = (Stage) save.getScene().getWindow();
	    stage.close();	    
	}

	private Boolean Controle(String new_,String exist,Label lab) {
		if(exist.equalsIgnoreCase(new_)){
			if(!lab.getText().contains("*")) lab.setText(lab.getText()+"*");
				
			lab.setTextFill(Color.web("#A52A2A"));
			return false;
		}else {
			if(lab.getText().contains("*")) {
				lab.setText(lab.getText().replace("*", ""));
				lab.setTextFill(Color.web("#000000"));
			}
			return true;
		}
	}
	
	
	/**
	 * Supprimer un creneau
	 */
	public void delete()
	{
		//agenda.appointments().remove(creneaux);
		agenda.getTimeTable().getCreneauxsList().remove(creneaux);
		creneaux.notify(new AgendaEvent(creneaux,AgendaEvent.TYPE.DELETE, creneaux, null));
		// faire s�r
		Stage stage = (Stage) save.getScene().getWindow();
	    stage.close();
	}
	
	
}
