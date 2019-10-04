package controleur;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;

import application.Main;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import modeles.Constants;
import modeles.Shortcut;

public class FavPopUpControleur implements Initializable{

	private static final String PICS_ADD = "pics/add_icon.png";
	private static final String PICS_EDIT = "pics/edit_icon.png";
	private static final String PICS_DELETE = "pics/delete_icon.png";
	private static final String PICS_SHORTUCT = "pics/shortuct1.png";

	private static final String ADD_SHORTCUT = "Ajouter";
	private static final String EDIT_SHORTCUT = "Modifier";

	public final static String Shortcut_FILE = "Shortcuts.txt";

	@FXML
	private JFXListView<Label> shortcutList;

	@FXML
	private JFXButton addButton, editButton, deleteButton;

	private Menu myfavs;
	private Menu diff_favs;
	private MainScreenControleur sc;

	public FavPopUpControleur(Menu myfavs, Menu diff, MainScreenControleur c) {
		this.myfavs=myfavs;
		this.diff_favs = diff;
		this.sc=c;
	}

	public void setMainControleur(MainScreenControleur c) {
		this.sc=c;
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		setScortuctMenuStyle();
		setShortuctListStyle();
		setFavShortuctListStyle();
		setListViewAction();
	}

	/**
	 * make some styles to the shortcut  buttons
	 * add actions to buttons
	 */
	private void setScortuctMenuStyle() {
		int size=30;
			ImageView addImage=new ImageView(PICS_ADD);
			addImage.setFitWidth(size);
			addImage.setFitHeight(size);
			addButton.setGraphic(addImage);
			ImageView editImage=new ImageView(PICS_EDIT);
			editImage.setFitWidth(size);
			editImage.setFitHeight(size);
			editButton.setGraphic(editImage);
			editButton.setOnAction(e->editPopUp());
			ImageView deleteImage=new ImageView(PICS_DELETE);
			deleteImage.setFitWidth(size);
			deleteImage.setFitHeight(size);
			deleteButton.setGraphic(deleteImage);
			editButton.setDisable(true);
			deleteButton.setDisable(true);
			deleteButton.setOnAction(e->deletePopUp());
	}

	/**
	 * Add shortcuts to the list ,
	 * make some styles to the shortcut list
	 *
	 */
	private void setShortuctListStyle() {
		int size=25;
		ArrayList<Label> lables=new ArrayList<>();
		ArrayList<MenuItem> favorites=new ArrayList<>();
		Shortcut.getShortucts().forEach(e->
				{
					ImageView shortuctIcon= new ImageView(PICS_SHORTUCT);
					shortuctIcon.setFitWidth(size);
					shortuctIcon.setFitHeight(size-6);
					favorites.add(new MenuItem(e.getShortcutName()));
					favorites.add(new SeparatorMenuItem());
					lables.add(new Label(e.getShortcutName(),shortuctIcon));

				});
		if( !favorites.isEmpty()) {
			favorites.remove(favorites.size()-1);
		}
		for ( MenuItem mi : favorites)
		 {
			mi.setOnAction( e -> sc.menuAction(e));
		 }
		myfavs.getItems().clear();
		myfavs.getItems().addAll(favorites);
		shortcutList.getItems().clear();
		shortcutList.getItems().addAll(lables);
		shortcutList.setExpanded(true);
		shortcutList.depthProperty().set(1);
		shortcutList.setVerticalGap(10.0);
		shortcutList.setStyle("-fx-background-insets : 0;");
	}

	private void setFavShortuctListStyle() {
		int size=25;
		ArrayList<Label> lables=new ArrayList<>();
		ArrayList<MenuItem> favorites=new ArrayList<>();
		Shortcut.getShortucts().forEach(e->
				{
					ImageView shortuctIcon= new ImageView(PICS_SHORTUCT);
					shortuctIcon.setFitWidth(size);
					shortuctIcon.setFitHeight(size-6);
					favorites.add(new MenuItem(e.getShortcutName()));
					favorites.add(new SeparatorMenuItem());
					lables.add(new Label(e.getShortcutName(),shortuctIcon));

				});
		if( !favorites.isEmpty()) {
			favorites.remove(favorites.size()-1);
		}
		for ( MenuItem mi : favorites)
		 {
			mi.setOnAction( e -> sc.menuAction(e));
		 }
		diff_favs.getItems().clear();
		diff_favs.getItems().addAll(favorites);
		shortcutList.getItems().clear();
		shortcutList.getItems().addAll(lables);
		shortcutList.setExpanded(true);
		shortcutList.depthProperty().set(1);
		shortcutList.setVerticalGap(10.0);
		shortcutList.setStyle("-fx-background-insets : 0;");
	}

	/**
	 * list view Action
	 * enable edit and delete buttons ,
	 * and change the time table
	 */
	private void setListViewAction() {
		shortcutList.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				int index=shortcutList.getSelectionModel().getSelectedIndex();
		//		Shortcut selectedShortcut=null;
				//check if the user click an item
				if(index>=0) {
					editButton.setDisable(false);
					deleteButton.setDisable(false);
				//	selectedShortcut =Shortcut. getShortucts().get(index);
				}
			}

		});
	}

	/**
	 * Show the add pop up
	 * @param Action event e
	 */
	@FXML
	private void addPopUp(ActionEvent ee) {
		 final Stage popUp = new Stage();
		   popUp.setTitle(ADD_SHORTCUT);
	        popUp.initModality(Modality.APPLICATION_MODAL);
	        BorderPane root;
			try {
				root = (BorderPane)FXMLLoader.load(getClass().getResource(Constants.ADDPOPUP_FXML));
				Scene scene = new Scene(root);
				popUp.setScene(scene);
				popUp.initOwner(Main.mainStage);
				 // Refresh the parent window after add shortcut
				popUp.setOnHidden(e->{
					//re-load shortcut list
					setShortuctListStyle();
					setFavShortuctListStyle();
					//setCalenderData(creneauxList);

				//	setTableData(creneauxList);
				});
				popUp.show();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
	}

	/**
	 * Show edit pop up
	 * @param e
	 */
	@FXML
	private void editPopUp() {
		   final Stage popUp = new Stage();
		    popUp.setTitle(EDIT_SHORTCUT);
	        popUp.initModality(Modality.APPLICATION_MODAL);
	        BorderPane root;
			try {
				root = (BorderPane)FXMLLoader.load(getClass().getResource(Constants.ADDPOPUP_FXML));
				Scene scene = new Scene(root);
				popUp.initOwner(Main.mainStage);
				popUp.setScene(scene);
				popUp.show();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
	}
	/**
	 * Show delete pop Up
	 * @param e
	 */
	@FXML
	private void deletePopUp() {
		Alert alert = new Alert(AlertType.CONFIRMATION, "Etes-vous sur de vouloir supprimer " + shortcutList.getSelectionModel().getSelectedItem().getText() + " ?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
		alert.showAndWait();
		if (alert.getResult() == ButtonType.YES) {
			deleteShortcutFromFIle( shortcutList.getSelectionModel().getSelectedItem().getText());
			setShortuctListStyle();
			setFavShortuctListStyle();
		}
	}


	/**
	 * delete shortcut from file
	 * @param name of shortcut
	 */
	private void deleteShortcutFromFIle(String name)  {

		File file = new File(Shortcut_FILE);
		List<String> out;

		try {

			out = Files.lines(file.toPath())
					.filter(line -> !line.contains(name))
					.collect(Collectors.toList());

			Files.write(file.toPath(), out, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}




}
