package modeles;

import java.util.EmptyStackException;

import controleur.MainScreenControleur;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Pane;

public class Tab extends javafx.scene.control.Tab{
/**
 * une classe qui represente un onglet 
 */
	enum TYPE{
		MODIFICATION,
		CONSULTATION
	}
	
	private AgendaCustom agenda;
	private String name;
	private Tab me;
	private int limit=200;
	private MainScreenControleur parent;
	private UndoRedoManager<AgendaEvent> back,next;
	
	/**
	 * Constructeur de la classe Tab
	 * @param name
	 * 			Nom de l'onglet
	 * @param parent
	 * 			Noeud parent auquel est rattache le Tab
	 */
	public Tab(String name,MainScreenControleur parent) {
		super(name);
		this.name=name;
		me=this;
		contextMenu();
		back=new UndoRedoManager<AgendaEvent>(limit);
		next=new  UndoRedoManager<AgendaEvent>(limit);
		this.parent=parent;

	}
	
	/**
	 * Constructeur de la classe Tab
	 * @param agenda
	 * 			Agenda affiche dans l'onglet
	 * @param parent
	 * 			Noeud parent auquel est rattache le tab
	 */
	public Tab(AgendaCustom agenda,MainScreenControleur parent) {
		super(agenda.getTimeTable().getName(), agenda);
		this.agenda = agenda;
		agenda.newTimeTable(agenda.getTimeTable());
		me=this;
		contextMenu();
		back=new UndoRedoManager<AgendaEvent>(limit);
		next=new  UndoRedoManager<AgendaEvent>(limit);

		this.parent=parent;
		
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

/**
 * ajouter un menu pour aider l'utilisateur a fermer les onglets 
 */
	public void contextMenu(){
		
		MenuItem closeItem=new MenuItem(Constants.CLOSE);
		closeItem.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				
				MainScreenControleur.tabPaneV2.getTabs().remove(me);
			}
		});
		
		MenuItem closeRightItems = new MenuItem(Constants.CLOSE_RIGHT);
		
		closeRightItems.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				
				MainScreenControleur.tabPaneV2.getTabs().remove(MainScreenControleur.tabPaneV2.getTabs().indexOf(me)+1
						,MainScreenControleur.tabPaneV2.getTabs().size()-1);
			}
		});
		MenuItem closeAll=new MenuItem(Constants.CLOSE_ALL);
		
		closeAll.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
					MainScreenControleur.tabPaneV2.getTabs().clear();
					MainScreenControleur.setAddTabeHandler();
				
			}
		});
		

		ContextMenu	contextMenu = new ContextMenu();
		contextMenu.getItems().add(closeItem);
		contextMenu.getItems().add(closeAll);
		contextMenu.getItems().add(closeRightItems);
	        this.setContextMenu(contextMenu);

	      
	        Pane content = new Pane();
	        content.setOnContextMenuRequested(e -> {
	        	contextMenu.show(content, e.getScreenX(), e.getScreenY());
	        }
            );
	      //  this.setContent(content);

	}
	/**
	 * Accesseur de l'agenda 
	 * @return agenda
	 */
	public AgendaCustom getAgenda() {
		return agenda;
	}
	
	/**
	 * Mutateur de l'agenda
	 * @param agenda
	 * 			Met a jour l'agenda
	 */
	public void setAgenda(AgendaCustom agenda) {
		this.agenda = agenda;
	}
	

	public void back() throws EmptyStackException{
		AgendaEvent event=back.undo();
		next.Redo(event);
		event.execut();
		agenda.refresh();
	}
	
	public void next() throws EmptyStackException{
		AgendaEvent event=next.undo();
		back.Redo(event);
		event.unExecut();
		agenda.refresh();
	}

	public void notifyChange(AgendaEvent e) {
		back.Redo(e);
		next.clear();
		parent.updateUndoRedoButtons();
	}
	
	public boolean hasNext() {
		return next.canUndo();
	}
	
	public boolean hasBack() {
		return back.canUndo();
	}
}
