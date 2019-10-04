package modeles;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;


/**
 * 
 * @author djamelsoualmi
 * Class represent shortcut ,
 * The class save the path to a specific TimeTable
 */
public class Shortcut {

	public static final String SEPARATOR = "%";
	private static final String SRC_SHORTCUTS = "Shortcuts.txt";
	/**
	 * an integer used to difference the shortcuts
	 */
	private int id;
	/**
	 * static variable to save the laset id 
	 * , used to know the next id
	 */
	public static int lastId=0;
	/**
	 * list contians path to the specifc time table
	 */
	private ArrayList<String> path;
	/**
	 *  shortcut name 
	 */
	private String shortcutName="shortcut "+lastId;
	/**
	 * create an empty Shortcut with no path
	 */
	
	public Shortcut() {
	this.id=lastId++;
	path=new ArrayList<>();
	}
	
	/**
	 * Creer des raccourcis a partir d'un chemin d'acces
	 * 
	 * @param path
	 * 			Chemin auquel mene le raccourcis
	 */
	public Shortcut(ArrayList<String> path) {
		
		this.path = path;
	}
	public ArrayList<String> getPath() {
		return path;
	}

	public void AddStep(String step) {
		this.path.add(step);
	}
	public int getId() {
		return id;
	}
	
	public String getShortcutName() {
		return shortcutName;
	}
	public void setShortcutName(String shortcutName) {
		this.shortcutName = shortcutName;
	}
	/**
	 * get path as String 
	 */
	@Override
	public String toString() {
		StringBuilder p=new StringBuilder();
		path.forEach(e-> p.append(Shortcut.SEPARATOR+e));
		return p.toString();
	}
	public static int getlastId() {
		
		return lastId++;
	}
	/**
	 * save the shortcut in the file
	 */
	public  void saveShortuct() {
		try {

			File file =new File(SRC_SHORTCUTS); 	
				  if(!file.exists())
						file.createNewFile();

		    PrintWriter out = new PrintWriter
		    		(new BufferedWriter(new FileWriter(file.getName(), true)));
		    out.println(this.getShortcutName()+":"+this.toString());
		    out.close();
		} catch (IOException e) {
		}	}
	
	/**
	 * get the existing Shortcuts from file 
	 * @return list of Shortcuts
	 */
	public static ArrayList<Shortcut> getShortucts() {
        ArrayList<Shortcut> list=new ArrayList<>();
		File file =new File(SRC_SHORTCUTS); 	
		  try {
			  if(!file.exists())
					file.createNewFile();
		  BufferedReader br = new BufferedReader(new FileReader(file)); 
		  String st; 
		
			while ((st = br.readLine()) != null) {
			    Shortcut c=new Shortcut();
			    c.setShortcutName(st.split(":")[0]);
			    st=st.split(":")[1];
			    for (String step : st.split(SEPARATOR)) {
					c.AddStep(step);
				}
			    list.add(c);
			  }
			
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	
		 return list;
	}
}
