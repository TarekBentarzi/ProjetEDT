package modeles;

import java.util.EmptyStackException;
import java.util.Stack;
/**
 * une classe représente un tableau en mode LIFO (dernier entré, premier sorti)
 */
public class UndoRedoManager<T> {

	private int indexOfNextAdd;
    private int limit;
    private Stack<T> lifo;

	public UndoRedoManager(int limit) {
		this.limit=limit;
		this.indexOfNextAdd=0;
		lifo=new Stack<T>();
	}

	/**
	 * tese si on peut ajouter des objets
	 */
	public Boolean canRedo() {
		return indexOfNextAdd<limit;
	}
	/**
	 * tese si on peut lire des objets
	 */
	public Boolean canUndo() {
		return indexOfNextAdd>0;
	}
	/**
	 * vider le tabeau 
	 */
	public void clear() {
		lifo.clear();
		indexOfNextAdd=0;
	}
	/**
	 *  lire l'objet de la tête 
	 */
	public  T undo() throws EmptyStackException  {
		if(canUndo())
		indexOfNextAdd--;
		return lifo.pop();
	}
	/**
	 *  écrir l'objet en param
	 */
	public  void Redo(T e) {
		if(!canRedo()) {
			lifo.remove(0);
			indexOfNextAdd--;
		}
		indexOfNextAdd++;
		lifo.add(e);
	}
}
