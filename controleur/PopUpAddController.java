package controleur;


import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;

import code.barbot.Parseur;
import code.barbot.Parseur.Level;
import modeles.Constants;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import modeles.Branch;
import modeles.Category;
import modeles.Project;
import modeles.Shortcut;


public class PopUpAddController implements Initializable{
	
	private Parseur parseur;
	
	@FXML
	private Label lblPath;
	
	@FXML
	private JFXListView<String> branchList;
	
	@FXML
	private JFXButton btnSave,btnCancel,btnPrevious;
	
	static int testBranch = 0;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		setShortuctListStyle();
		btnSave.setDisable(true);
	}
	
	/**
	 * Revenir � la s�lection pr�c�dente
	 * @param e
	 */
	@FXML
	private void previous(ActionEvent e) {
		ArrayList<String> list;
		
		switch (parseur.ParseurLevel) {
			case PROJECT:
			   parseur=null;
			   branchList.getItems().clear();
			   branchList.getItems().addAll(getUFRS().keySet());
			   return;		
       
			case CATEGORY:
				parseur=new Parseur(parseur.getLogin(), "");
				list= Project.tableToString(parseur.getProjectList());
				branchList.getItems().clear();
				branchList.getItems().addAll(list);
				return;
       
			case BRANCH:
				if(parseur.getProject().getSelectedCategory().getSelectedBranch()!=null) {
					parseur.setBranch_getChild(parseur.getProject().getSelectedCategory().getSelectedBranch());
					parseur.getProject().getSelectedCategory().getSelectedBranch().setSelected(false);
					// Cas de feuille
					list= Branch.tableToString(parseur.getProject().getSelectedCategory().getSelectedBranch().getBranches());	   
				} else {
					parseur.setCategory_getBranches(parseur.getProject().getSelectedCategory());
					parseur.getProject().getSelectedCategory().setIsSelected(false);
					parseur.ParseurLevel=Level.CATEGORY;
					list= Category.tableToString(parseur.getProject().getCategorys());	
				}
				branchList.getItems().clear();
				branchList.getItems().addAll(list);
		}
	}
	/**
	 * Enregistrer les branches s�lectionn�es
	 * @param e
	 */
	@FXML
    private void save(ActionEvent e) {
		
		TextInputDialog dialog = new TextInputDialog("");
		dialog.setTitle(Constants.SAVE_SHORTCUT);
		dialog.setContentText(Constants.NAME);
		
		int id=Shortcut.lastId;
		
		Optional<String> result = null ;
		while (dialog.getEditor().getText().equals("")) {
			dialog.getEditor().setText(Constants.SHORTCUT+id);
			result = dialog.showAndWait();
		}

		if(result != null && result.isPresent()) {
			// Enregistrer le raccourci
			Shortcut c=new Shortcut();
			
			for (String step : lblPath.getText().split(Shortcut.SEPARATOR)) {
				c.AddStep(step);
			}
			    
			c.setShortcutName(dialog.getEditor().getText());
			c.saveShortuct();
			    // Fermer ajouter pop up
			Stage stage = (Stage) btnSave.getScene().getWindow();
			    // D�finir le nouvel horaire
			try {
				MainScreenControleur.importedPathI=c.getPath();
				stage.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		
	}
	
	/**
	 * Fermer pop-up lorsque l'utilisateur clique sur Annuler
	 * @param e
	 */
	@FXML
    private void cancel(ActionEvent e) {
		 Stage stage = (Stage) btnCancel.getScene().getWindow();
		 stage.close();
    }

	/**
	 * G�rer les branches cliquez 
	 * quand l'utilisateur s�lectionne une branche ,
	 * Obtenir des sous-branches et les afficher dans une liste
	 *  
	 * @param e
	 */
	@FXML
	private void selectItem(MouseEvent e) {
		int index=branchList.getSelectionModel().getSelectedIndex();
		    // V�rifier si l'utilisateur clique sur un �l�ment 
		if(index>=0) {
			btnPrevious.setDisable(false);
			btnSave.setDisable(false);
			// Obtenir une sous-liste de l'�l�ment s�lectionn�
			// Juste tester 
			ArrayList<String > newList=getSubBranchesList(index);
			
			if(newList!=null) {
				branchList.getItems().clear();
				branchList.getItems().addAll(newList);
			}
		}
    }
	/**
	 * 
	 * Cr�er des styles dans la liste des branches 
	 * 
	 */
	private void setShortuctListStyle() {
		branchList.getItems().addAll(getUFRS().keySet());
		branchList.setExpanded(true);
		branchList.depthProperty().set(1);
		branchList.setVerticalGap(8.0);
		branchList.setPadding(new Insets(10));
		branchList.setStyle("-fx-fond-encart : 0;");
	}
	
	ArrayList<String > getSubBranchesList(int branche){
		if(parseur==null) {
			parseur=new Parseur(getUFRS().values().toArray()[branche].toString(), "");
			lblPath.setText(getUFRS().values().toArray()[branche].toString());
			return Project.tableToString(parseur.getProjectList());
		}
		
		switch (parseur.ParseurLevel) {
			case PROJECT:
				parseur.selctProject(parseur.getProjectList().get(branche).getId()+"");
				lblPath.setText(lblPath.getText()+Shortcut.SEPARATOR+parseur.getProjectList().get(branche).getName());
				return Category.tableToString(parseur.getProject().getCategorys());		
			case CATEGORY:
				parseur.setCategory_getBranches(parseur.getProject().getCategorys().get(branche));
				lblPath.setText(lblPath.getText()+Shortcut.SEPARATOR+parseur.getProject().getSelectedCategory().getName());
				return Branch.tableToString(parseur.getProject().getSelectedCategory().getBranches());
			
			case BRANCH:
				if(parseur.getProject().getSelectedCategory().getSelectedBranch()!=null) {
					parseur.setBranch_getChild(parseur.getProject().getSelectedCategory().getSelectedBranch().getBranches().get(branche));
					lblPath.setText(lblPath.getText()+Shortcut.SEPARATOR+parseur.getProject().getSelectedCategory().getSelectedBranch().getName());
      		// cas de feuille 
					if(parseur.getProject().getSelectedCategory().getSelectedBranch().isLeaf()) return null;
      			return Branch.tableToString(parseur.getProject().getSelectedCategory().getSelectedBranch().getBranches());	   
    	   }else {
    		   parseur.setBranch_getChild(parseur.getProject().getSelectedCategory().getBranches().get(branche));
    		   lblPath.setText(lblPath.getText()+Shortcut.SEPARATOR+parseur.getProject().getSelectedCategory().getSelectedBranch().getName());
    		   return Branch.tableToString(parseur.getProject().getSelectedCategory().getSelectedBranch().getBranches());	
    	   }
			default:
				return new ArrayList<>();
		}
	}
	
	static HashMap<String, String> getUFRS(){
		HashMap<String, String> ufrs=new HashMap<>();
		ufrs.put("AEI / IPAG (M2 uniquement)", "aei_m2_web");
		ufrs.put("Droit", "droit_web");
		ufrs.put("Lettres, langues, sciences humaines ", "LLSH_web");
		ufrs.put("Département Economie", "scieco_web");
		ufrs.put("ESIAG", "esiag_web");
		ufrs.put("IAE Gustave Eiffel ", "iae_web");
		ufrs.put("SESS / STAPS", "sess_staps_web");
		ufrs.put("Sciences et technologie", "sciences_web");
		ufrs.put("IUT Sénart-Fontainebleau", "etuiutsen");
		ufrs.put("Ergothérapie", "ergo_web");
		ufrs.put("ESPE", "iufm_web");
		return ufrs;
	}
	
	@FXML
	public void ImportADE(ActionEvent e) {
		
		try {
			ArrayList<String > path=new ArrayList<String>();
			path.add("");
			
			for (String step : lblPath.getText().split(Shortcut.SEPARATOR)) {
				step=step.trim();
				path.add(step);
			}
			  
			MainScreenControleur.importedPathI=path;
			Stage stage = (Stage) btnSave.getScene().getWindow();
			stage.close();
		} catch (Exception e1) {
			e1.printStackTrace();
		}

	}
}
