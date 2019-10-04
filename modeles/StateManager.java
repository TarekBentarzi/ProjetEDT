package modeles;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import com.fasterxml.jackson.databind.ObjectMapper;

import controleur.MainScreenControleur;
import javafx.collections.ObservableList;
import javafx.scene.control.Tab;

public class StateManager {

	/**
	 * une classe utiliser pour sauvegrder l'état de systéme aprés que l'utilisateur ferme l'application
	 * et recharger l'état à la prochaine ouverture
	 */
	
	static ObjectMapper objectMapper;
	static StateManager stateManager;
	private static final String path="StateFileSave";


	/**
	 *utilisation de design singleton dans Constructeur pour l
assuré une seule instociation de StateManager et ObjectMapper
 */
	public static StateManager getInstance() {
		if(stateManager==null)
			stateManager=new StateManager();
		if(objectMapper==null)
			objectMapper=new ObjectMapper();
		return stateManager;
	}
	
	/**
	 * récupérer et sauvegarder l'état de systéme dans un fichier 
	 */
	public void saveState() {
		ArrayList<TimeTableV2> list=new ArrayList<TimeTableV2>();
		ObservableList<Tab> tabs=MainScreenControleur.tabPaneV2.getTabs();
		
		for(int i =0 ; i<tabs.size()-1;i++) {
			modeles.Tab tt=(modeles.Tab)tabs.get(i);
			list.add(tt.getAgenda().getTimeTable().toTimeTableV2());
		}

		State state=new State();
		state.setList(list);
		state.setTabNumber(MainScreenControleur.tabPaneV2.getSelectionModel().getSelectedIndex());
		
		try {
			objectMapper.writeValue( new File(path+".json"),state );
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,modeles.Constants.errSaveFile+e.getMessage() ,modeles.Constants.errMssg, JOptionPane.ERROR_MESSAGE);				
		}
		
	}
	/**
	 * recharger l'état de systéme en lisant le fichier 
	 */
	public State load() {
		
		State st;
		
		try {
			st=objectMapper.readValue( new File(path+".json"),State.class);
		} catch (IOException e) {
			return null;
		}
		
		return st ;
	}


}
