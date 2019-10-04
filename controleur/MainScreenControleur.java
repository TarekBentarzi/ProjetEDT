package controleur;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.EmptyStackException;
import java.util.Optional;
import java.util.ResourceBundle;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.jfoenix.controls.JFXSpinner;

import application.Main;
import code.barbot.Creneaux;
import code.barbot.Parseur;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import jfxtras.scene.control.agenda.Agenda;
import modeles.AgendaCustom;
import modeles.AgendaEvent;
import modeles.Branch;
import modeles.Category;
import modeles.Constants;
import modeles.JsonFileManager;
import modeles.Project;
import modeles.Shortcut;
import modeles.State;
import modeles.StateManager;
import modeles.Tab;
import modeles.TimeTable;
import modeles.TimeTableV2;

public class MainScreenControleur implements Initializable {
	
	private Screen screen = Screen.getPrimary();
    private Rectangle2D bounds = screen.getVisualBounds();
	
	public static final String Shortcut_FILE = "Shortcuts.txt";
	
	public static TabPane  tabPaneV2;
	public static ArrayList<String> importedPathI;
	
	private Parseur parseur;
	private static MainScreenControleur me;
	
	
	@FXML
	private TabPane  tabPane;

	@FXML
	private JFXSpinner spinner;
	
	@FXML 
	private ComboBox<String> projetField;
	
	@FXML 
	private ComboBox<String> categorieField;
	
	@FXML
	private Menu fav,myfavs, diff_fav;

	@FXML
	private DatePicker datePicker;
		
	@FXML
	private	Button undoButton;
	
	@FXML
	private Button redoButton;
	
	@FXML
	private Button diffButton;
	
	@FXML
	private Button openButton;

	@FXML
	private Button saveButton;	
	
	public static Button undoButtonS,redoButtonS;
	private static int  color=0;
	public MainScreenControleur() {
	
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
	
		me = this;
		
		tabPaneV2 = tabPane;
		redoButtonS = redoButton;
		undoButtonS = undoButton;
		
		setFavDiffItems();
		setFavMenuItems();	
		initState();
	    
		setAddTabeHandler();
	    initButtons();
		
	}
	
	
	/*
	 * Initialise les styles des boutons présents sur l'écran principal
	 */
    private void initButtons() {
    	int size=15;
    	ImageView openImage=new ImageView(Constants.PICS_OPEN);
		openImage.setFitHeight(size);
		openImage.setFitWidth(size);
		openButton.setGraphic(openImage);
		ImageView saveImage=new ImageView(Constants.PICS_SAVE);
		saveImage.setFitHeight(size);
		saveImage.setFitWidth(size);
		saveButton.setGraphic(saveImage);
    	ImageView undoImage=new ImageView(Constants.PICS_UNDO);
		undoImage.setFitHeight(size);
		undoImage.setFitWidth(size);
		undoButton.setGraphic(undoImage);
		ImageView redoImage=new ImageView(Constants.PICS_REDO);
		redoImage.setFitHeight(size);
		redoImage.setFitWidth(size);
		redoButton.setGraphic(redoImage);
		
    }
    
    private void diff(TimeTable compared) {
    	
    	TimeTable comparing = getSelectedTab().getAgenda().getTimeTable();
    	
    	if ( !comparing.equals(compared)) {
    		
			System.out.println("Comparaison");
			
			compared.getCreneauxsList().forEach( creneau -> {
				if ( !comparing.getCreneauxsList().contains(creneau)) {
					creneau.setAppointmentGroup(new Agenda.AppointmentGroupImpl().withStyleClass("group2"));
				}
			});
			
			AgendaCustom agenda = new AgendaCustom(compared);
	    	AgendaCustom agendadebase = getSelectedTab().getAgenda();
	    	
			if(agenda.getTimeTable().getCreneauxsList().size() > 0)
				agenda.setDisplayedLocalDateTime(agenda.getTimeTable().getCreneauxsList().get(0).getStartLocalDateTime());
			else {
				agenda.setDisplayedLocalDateTime(LocalDateTime.now());
			}
			
			agenda.disableAction();
			
	    	HBox hb= new HBox();
	    	VBox vb= new VBox();
	    	Button close = new Button();
	    	ImageView quitter = new ImageView(("pics/cancel.png"));
			quitter.setFitHeight(15);
			quitter.setFitWidth(15);
			close.setGraphic(quitter);
	    	
			hb.getChildren().addAll(close);
	    	hb.setAlignment(Pos.BASELINE_RIGHT);
	    	vb.getChildren().addAll(hb,agenda);
	    	HBox diffbox = new HBox();
	    	diffbox.getChildren().addAll(agendadebase, vb);
	    	getSelectedTab().setContent(diffbox);	
	    	
	    	close.setOnAction(new EventHandler<ActionEvent>() {
	    		@Override
				public void handle(ActionEvent event) {
					vb.getChildren().remove(agenda);
					hb.getChildren().remove(close);
				}
	    		
			});	
			
		}
    	 	
    }
    
    @FXML
	private void executeDiffFromFavoris(ActionEvent e) {
		System.out.println("Enter diff from favs");
		diff_fav.getItems().forEach(i-> {  
			
			if(i.equals(e.getSource())){
				
				MenuItem ie=(MenuItem) e.getSource();
				
				Shortcut.getShortucts().forEach(s-> {
					if(s.getShortcutName().equals(ie.getText())) {
						select(s.getPath());
						
						try {
							diff(getDataFromShortcut(s.getPath()));
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				});
			}
		});
	}
    
	@FXML
	public void executeDiffFromEmpty() {
		diff(new TimeTable(Constants.NEW_TAB+tabPane.getTabs().size(), "NoPath", new ArrayList<Creneaux>(), TimeTable.TYPE.EMPTY_BASED));
	}
    
    @FXML
    private void executeDiffFromFile() {
    	
    	System.out.println("Starting difference : ");
    	
    	JFileChooser fileChooser=new JFileChooser(new File("."));
		FileNameExtensionFilter filter = new FileNameExtensionFilter(Constants.FORMAT_JSON, "json");
		fileChooser.setFileFilter(filter);
			
		if(fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			
			File file = fileChooser.getSelectedFile();
		
			TimeTable compared = JsonFileManager.getInstance().load(file);
			diff(compared);
		}
    }
    
    @FXML
	public void executeDiffFromADE(ActionEvent ee) {
    	
    	final Stage popUp = new Stage();
    	popUp.setTitle(Constants.IMPORT_ADE_PopUp);
    	popUp.initModality(Modality.APPLICATION_MODAL);
    	
    	BorderPane root;
		
    	try {
				
			root = (BorderPane)FXMLLoader.load(getClass().getResource(Constants.IMPORT_ADE_FXML));
			Scene scene = new Scene(root);
			popUp.setScene(scene);
			popUp.initOwner(Main.mainStage);
			popUp.setOnHidden(e->{
				select(importedPathI);
				
				try {
					TimeTable newTimeTable=new TimeTable(importedPathI.get(importedPathI.size()-1), importedPathI.toString(), parseur.getTimeTable(), TimeTable.TYPE.ADE_BASED);
					diff(newTimeTable);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
						
							
				});
				popUp.show();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
	}
    
	private TimeTable getDataFromShortcut(ArrayList<String> path) throws Exception, IOException {
		
		TimeTable newTimeTable = new TimeTable(path.get(path.size()-1), path.toString(), parseur.getTimeTable(), TimeTable.TYPE.ADE_BASED);
		return newTimeTable;
		
	}

    
	private void initState() {
		//getState 
		State state=StateManager.getInstance().load();
		
		if(state!=null) {
			
			ArrayList<TimeTable> list=new ArrayList<TimeTable>();
			
			for (TimeTableV2 timeTableV2 : state.getList()) {
				list.add(timeTableV2.toTimeTable());
			}
			
			AgendaCustom agenda;
			for (TimeTable timeTable : list) {
				
				agenda=new AgendaCustom(timeTable);
				
				// agenda.appointments().addAll(timeTable.getCreneauxsList());
				agenda.setParent(new modeles.Tab(agenda,this));
				tabPane.getTabs().add(agenda.getparent());
				
			}
			
			tabPane.getSelectionModel().select(state.getTabNumber());
			updateUndoRedoStatic((Tab) tabPaneV2.getSelectionModel().getSelectedItem());
		}
	}
	
	@FXML
	public void restoreAgenda(ActionEvent e) {
		getSelectedTab().getAgenda().refresh();	
	}
	
	@FXML
	public void show_Filter_PopUp(ActionEvent ee) {
		 final Stage popUp = new Stage();
		   popUp.setTitle("Filtrer les données");
	        popUp.initModality(Modality.APPLICATION_MODAL);
	        AnchorPane root;
			try {
				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(Constants.FILTER_POPUP));
				FilterControleur controller = new FilterControleur(getSelectedTab());
			    fxmlLoader.setController(controller);
			    root = fxmlLoader.load();
				Scene scene = new Scene(root);
				popUp.setScene(scene);
				popUp.initOwner(Main.mainStage);
			
				popUp.show();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
	}

	@FXML
	private void next() {
		
		try {
			getSelectedTab().next();
			updateUndoRedoButtons();
		} catch (EmptyStackException e) {
			System.out.println("EmptyStackException");
		}
	
	}
	
	@FXML
	private void back() {
		
		try {
			getSelectedTab().back();
			updateUndoRedoButtons();
		} catch (EmptyStackException e) {
			System.out.println("EmptyStackException");
		}
		
	}

	public void updateUndoRedoButtons() {
		
		if(!getSelectedTab().hasNext()) redoButton.setDisable(true);
		else redoButton.setDisable(false);
		
		if(!getSelectedTab().hasBack()) undoButton.setDisable(true);
		else undoButton.setDisable(false);
	}

	
	@FXML
	private void initDatePicker() {
		
		Calendar c=Calendar.getInstance();
		
		c.set(datePicker.getValue().getYear(), datePicker.getValue().getMonthValue()-1, datePicker.getValue().getDayOfMonth());
		
		if( getSelectedTab().getAgenda().getTimeTable().getType()==(TimeTable.TYPE.ADE_BASED)){
			
			try {
				select( getSelectedTab().getAgenda().getTimeTable().getPathList());
				parseur.setWeek(calculWeek(Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)), false);
				parseur.setWeek(calculWeek(c.get(Calendar.WEEK_OF_YEAR)), true);
				ArrayList<Creneaux> newTimeTbale=parseur.getTimeTable();
				ArrayList<Creneaux> oldTimeTbale=getSelectedTab().getAgenda().getTimeTable().getCreneauxsList();
				getSelectedTab().notifyChange(new AgendaEvent(getSelectedTab().getAgenda(), AgendaEvent.TYPE.WEEK, oldTimeTbale, newTimeTbale));
				getSelectedTab().getAgenda().getTimeTable().setCreneauxsList(newTimeTbale);
				getSelectedTab().getAgenda().setDisplayedLocalDateTime(LocalDateTime.of(datePicker.getValue(), LocalTime.now()));
				getSelectedTab().getAgenda().newTimeTable(getSelectedTab().getAgenda().getTimeTable());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

	public int calculWeek(int week) {
		return (week+18) %52;
	}
	
	public 	static void setAddTabeHandler() {
		   
		Tab newtab = new Tab("add",me); 
		ImageView addImage =new ImageView(Constants.PICS_ADD_TAB);
		
		addImage.setFitWidth(20);
		addImage.setFitHeight(20);
		newtab.setGraphic(addImage);
		newtab.setClosable(false);
		    // action event 
		
		EventHandler<Event> event = new EventHandler<Event>() { 
		        
			public void handle(Event e) { 
				if (newtab.isSelected()) {
					addNewTab(new AgendaCustom(new TimeTable(Constants.NEW_TAB+tabPaneV2.getTabs().size(),"Path", new ArrayList<Creneaux>(),TimeTable.TYPE.EMPTY_BASED))); 
				}
			}
		}; 
		  
		// set event handler to the tab 
		newtab.setOnSelectionChanged(event); 
		  
		// add newtab 
		tabPaneV2.getTabs().add(newtab); 
		tabPaneV2.getSelectionModel().selectedItemProperty().addListener((ov, oldTab, nTab) -> {
		    
			if( ( (Tab) nTab ) != null) updateUndoRedoStatic((Tab) nTab);
		 
		});
	}
	
	static void addNewTab(AgendaCustom agenda) {
		// create Tab 
		Tab tab = new Tab(agenda,me); 
		agenda.setParent(tab);

		// add tab 
		tabPaneV2.getTabs().add(tabPaneV2.getTabs().size() - 1, tab); 

		// select the last tab 
		tabPaneV2.getSelectionModel().select(tabPaneV2.getTabs().size() - 2);
		
	} 
	
	private Tab getSelectedTab(){
		return (Tab)tabPane.getSelectionModel().getSelectedItem();
	}
	    
	public static void updateUndoRedoStatic(Tab tab) {
		if(!tab.hasNext())
			redoButtonS.setDisable(true);
		else redoButtonS.setDisable(false);
		if(!tab.hasBack())
			undoButtonS.setDisable(true);
		else undoButtonS.setDisable(false);
	}
	
	public void taskList(ActionEvent event) throws IOException {
		Parent newParent=FXMLLoader.load(getClass().getResource(Constants.TACHE_LIST));
	    Scene newScene=new Scene(newParent);
	    Stage newStage= (Stage)(tabPane.getScene().getWindow() );
	       
	    newStage.setScene(newScene);
	    newStage.setWidth(bounds.getWidth());
	    newStage.setHeight(bounds.getHeight());
	    newStage.setMaximized(true);
	    newStage.show();
	}
	public boolean Dialog_Import_ADE( ) {
		boolean k=false;
	
	Alert alert=new Alert(Alert.AlertType.INFORMATION);
    alert.setContentText("Vous voulez charger cet emploi sur le même agenda");
	 ButtonType oui=new ButtonType("Oui");
	 ButtonType non=new ButtonType("Non");
	 alert.getButtonTypes().setAll(oui,non);
	 Optional<ButtonType> opt=alert.showAndWait();
	 if(opt.get()==oui) {k=true;} 

return k;
}	
	@FXML
	public void Show_Import_ADE_PopUp(ActionEvent ee) {
		
		final Stage popUp = new Stage();
		
		popUp.setTitle(Constants.IMPORT_ADE_PopUp);
	    popUp.initModality(Modality.APPLICATION_MODAL);
	    
	    BorderPane root;
	    
	    try {
	    	
	    	root = (BorderPane)FXMLLoader.load(getClass().getResource(Constants.IMPORT_ADE_FXML));
			
	    	Scene scene = new Scene(root);
			
	    	popUp.setScene(scene);
			popUp.initOwner(Main.mainStage);
			popUp.setOnHidden(e->{
				select(importedPathI);
				
				try {
					if(!Dialog_Import_ADE()) {
						getDataShowData(importedPathI);
						}
						else {
						getDataShowDataInSameTab(importedPathI);
				}

				} catch (Exception e1) {
					e1.printStackTrace();
				}
						
			});
			
			popUp.show();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	
	public void newTask(ActionEvent event) throws IOException {   
	       
		Parent newParent=FXMLLoader.load(getClass().getResource(Constants.NEW_TASKFXML));
		
		Scene newScene=new Scene(newParent);
		Stage newStage= (Stage)(tabPane.getScene().getWindow());
		
		newStage.setScene(newScene);
		newStage.show();
	}
	
	public void menuAction(ActionEvent e){
		
		//Shortcut selectedShortcut;
		myfavs.getItems().forEach(i-> {  
			
			if(i.equals(e.getSource())){
				
				MenuItem ie=(MenuItem) e.getSource();
				
				Shortcut.getShortucts().forEach(s-> {
					if(s.getShortcutName().equals(ie.getText())) {
						select(s.getPath());
						
						try {
							getDataShowData(s.getPath());
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				});
			}
		});
		
	}
	
	private void setFavDiffItems() {
		ArrayList<MenuItem> favorites=new ArrayList<>();
		
		Shortcut.getShortucts().forEach(e-> {
			favorites.add(new MenuItem(e.getShortcutName()));
			favorites.add(new SeparatorMenuItem());	
		});
		
		if(!favorites.isEmpty()) {
			favorites.remove(favorites.size()-1);
		}
		
		for ( MenuItem mi : favorites){ 
			mi.setOnAction( a->executeDiffFromFavoris(a));
		}
		
		diff_fav.getItems().addAll(favorites);
	}
	

	/**
	 * Add shortcuts to the menu ,
	 *  
	 * 
	 */
	private void setFavMenuItems() {
		ArrayList<MenuItem> favorites=new ArrayList<>();
		
		Shortcut.getShortucts().forEach(e-> {
			favorites.add(new MenuItem(e.getShortcutName()));
			favorites.add(new SeparatorMenuItem());	
		});
		
		if(!favorites.isEmpty()) {
			favorites.remove(favorites.size()-1);
		}
		
		for ( MenuItem mi : favorites){ 
			mi.setOnAction( a->menuAction(a));
		}
		
		myfavs.getItems().addAll(favorites);
	}

	/**
	 * Afficher un planning vide et permettre les modifications
	 * @param e
	 */

	@FXML
	private void Show_EmptyAgenda_PopUp(ActionEvent e) {
		addNewTab(new AgendaCustom(new TimeTable(Constants.NEW_TAB+tabPane.getTabs().size(), "NoPath", new ArrayList<Creneaux>(), TimeTable.TYPE.EMPTY_BASED)));
	}

	/**
	 * get the selected time table and set the list of Creneaux
	 * @param selectedShortcut
	 */
	private void select(ArrayList<String> path) {
		
		parseur = null;
		Collection<String> set = PopUpAddController.getUFRS().values();
		ArrayList<String> list = new ArrayList<>();
		 
		for (String b : set) {
			list.add(b);
		}
		
		for (int i = 1; i < path.size(); i++) {
			int index = 0;
			
			for (int j = 0; j < list.size(); j++) {
				if(list.get(j).equals(path.get(i))){
					index = j;
				}	
			}
			
			list = getSubBranchesList(index);
		}
		
	}

	private void getDataShowData(ArrayList<String> path) throws Exception, IOException {
		color=0;
		TimeTable newTimeTable = new TimeTable(path.get(path.size()-1), path.toString(), parseur.getTimeTable(), TimeTable.TYPE.ADE_BASED);
		AgendaCustom agenda = new AgendaCustom(newTimeTable);
		
		//adapter la date de l'agenda
		
		if(newTimeTable.getCreneauxsList().size() > 0)
			agenda.setDisplayedLocalDateTime(newTimeTable.getCreneauxsList().get(0).getStartLocalDateTime());
		else {
			agenda.setDisplayedLocalDateTime(LocalDateTime.now());
		}
		
		addNewTab(agenda);
		
	}
	private void getDataShowDataInSameTab(ArrayList<String> path) throws Exception, IOException {
		color++;
		TimeTable newTimeTable=new TimeTable(path.get(path.size()-1), path.toString(), parseur.getTimeTable(), TimeTable.TYPE.ADE_BASED);
		for (Creneaux c :newTimeTable.getCreneauxsList()) {
						c.setParent(getSelectedTab().getAgenda().getparent());
						c.withAppointmentGroup(new Agenda.AppointmentGroupImpl().withStyleClass("group"+color));

					}
		getSelectedTab().getAgenda().getTimeTable().getCreneauxsList().addAll(newTimeTable.getCreneauxsList());
		getSelectedTab().getAgenda().refresh();
		
	}

	/**
	 * Supprimer tous les créneaux
	 * Ajouter la liste des créneaux à l'agenda
	 * @param timeTable
	 */
	private void setCalenderData(TimeTable timeTable) {
		
		if(timeTable.getCreneauxsList().size()>0) {
			getSelectedTab().getAgenda().setDisplayedLocalDateTime(timeTable.getCreneauxsList().get(0).getStartLocalDateTime());
		}
		
		AgendaCustom agenda = new AgendaCustom(timeTable);
		addNewTab(agenda);
	}
	
	
	ArrayList<String > getSubBranchesList(int branche){
		
		if( parseur == null ) {
			parseur = new Parseur(PopUpAddController.getUFRS().values().toArray()[branche].toString(), "");
			return Project.tableToString(parseur.getProjectList());
		}
		
		switch (parseur.ParseurLevel) {
			case PROJECT:
				parseur.selctProject(parseur.getProjectList().get(branche).getId()+"");
				return Category.tableToString(parseur.getProject().getCategorys());		
			case CATEGORY:
				parseur.setCategory_getBranches(parseur.getProject().getCategorys().get(branche));
				return Branch.tableToString(parseur.getProject().getSelectedCategory().getBranches());
			
			case BRANCH:
				if(parseur.getProject().getSelectedCategory().getSelectedBranch()!=null) {
					//leaf case
					if(parseur.getProject().getSelectedCategory().getSelectedBranch().isLeaf()) return null;
					parseur.setBranch_getChild(parseur.getProject().getSelectedCategory().getSelectedBranch().getBranches().get(branche));
					return Branch.tableToString(parseur.getProject().getSelectedCategory().getSelectedBranch().getBranches());	   
				
				} else {
					parseur.setBranch_getChild(parseur.getProject().getSelectedCategory().getBranches().get(branche));
					return Branch.tableToString(parseur.getProject().getSelectedCategory().getSelectedBranch().getBranches());	
				}
				
			default:
				return new ArrayList<>();
		}
	}
	
	public Stage showDialog() {
		
		Stage stage = new Stage();
	    BorderPane g = new BorderPane();
	    ProgressIndicator p1 = new ProgressIndicator();
	    
	    g.getChildren().add(p1);
		
	    Scene scene = new Scene(g, 260, 80);
	    stage.setScene(scene);
	    stage.initOwner(Main.mainStage);
	    
	    return stage;
	}

	
	//file load save handler
	
	/**
	 * action graphique "menu"
	 *  Lancer le selecteur de fichier pour choisir le fichier
	 *  
	 * @param event
	 * 			Evenement
	 */
	@FXML
	public void open_file(ActionEvent event) {
		
		JFileChooser fileChooser = new JFileChooser(new File("."));
		FileNameExtensionFilter filter = new FileNameExtensionFilter(Constants.FORMAT_JSON, "json");
		fileChooser.setFileFilter(filter);
		
		if(fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
		
			setCalenderData(JsonFileManager.getInstance().load(file));
			updateUndoRedoButtons();
		}
	}
	/**
	 * action graphique "menu"
	 * Lancer le sélecteur de fichier pour choisir le chemin où nous allons enregistrer le fichier
	 * @param event
	 * 			Evenement 
	 */
	@FXML
	public void save_file(ActionEvent event) {
		
		JFileChooser fileChooser = new JFileChooser(new File(Constants.REP_OPEN_FILECHOSER));

	    fileChooser.setDialogTitle(Constants.SAVE_FILE);
	    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
	    if(fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
	    	JsonFileManager.getInstance().save(getSelectedTab().getAgenda().getTimeTable(),(fileChooser.getSelectedFile().getAbsolutePath()+"/"+getFileName()));
		}
	}
   
	/**
	 * Afficher le popup de gestion des favoris
	 * 
	 * @param ae
	 * 			ActionEvent
	 */
	
	@FXML
	public void favPopUp(ActionEvent ae) {   
		
		final Stage popUp = new Stage();
	    
		popUp.setTitle("Gerer");
	    popUp.initModality(Modality.APPLICATION_MODAL);
	    
	    AnchorPane root;
		
	    try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(Constants.FAV_POPUP));
			FavPopUpControleur controller = new FavPopUpControleur(myfavs,diff_fav ,this);
		    fxmlLoader.setController(controller);
			root = fxmlLoader.load();				
			Scene scene = new Scene(root);
			popUp.setScene(scene);
			popUp.initOwner(Main.mainStage);
			popUp.show();
		} catch (IOException e1) {
			e1.printStackTrace();
		}			
     }

	
	private String getFileName() {
		
		TextInputDialog dialog = new TextInputDialog("");
		int id = JsonFileManager.lastId;
		
		dialog.setTitle("Nom du fichier");
		dialog.setContentText("Nom du fichier :");
		

		while (dialog.getEditor().getText().equals("")) {
			dialog.getEditor().setText("TimeTable_"+id);
			dialog.showAndWait();
		}
		return dialog.getEditor().getText();
	}
	
}
