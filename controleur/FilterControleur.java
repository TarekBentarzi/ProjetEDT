package controleur;

import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

import org.controlsfx.control.textfield.TextFields;

import code.barbot.Creneaux;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import jfxtras.scene.control.agenda.Agenda.Appointment;
import modeles.Tab;



public class FilterControleur implements Initializable {
	
	private Tab selectedTab;
	private Set<String> cours;
	private Set<String> profs;
	private Set<String> salles;
	private Set<String> groups;
	
	@FXML
	TextField coursFilterField;
	@FXML
	TextField profFilterField;
	@FXML
	TextField salleFilterField;
	@FXML
	TextField groupFilterField;
	
	/**
	 * Contructeur de la classe
	 * @param tab
	 */
	public FilterControleur(Tab tab) {
		selectedTab=tab;
		cours=new HashSet<>();
		profs=new HashSet<>();
		salles=new HashSet<>();
		groups=new HashSet<>();
		for(Appointment c:selectedTab.getAgenda().appointments())
		{   
			
			cours.add(((Creneaux) c).getCours());
			profs.add(((Creneaux) c).getProf());
			salles.add(((Creneaux) c).getSalle());
			groups.add(((Creneaux) c).getGroup());
		}
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		TextFields.bindAutoCompletion(coursFilterField, cours);
		TextFields.bindAutoCompletion(profFilterField, profs);
		TextFields.bindAutoCompletion(salleFilterField, salles);
		TextFields.bindAutoCompletion(groupFilterField, groups);
	}
	
	/**
	 * Filtrer selon les parametres entrés
	 */
	public void filter()
	{
		selectedTab.getAgenda().filter(coursFilterField.getText(), profFilterField.getText(), salleFilterField.getText(), groupFilterField.getText());
		this.close();
	   
	}
	
	/**
	 * Fermé le popup
	 */
	public void close() {
		Stage stage = (Stage) coursFilterField.getScene().getWindow();
		stage.close();
	}

}
