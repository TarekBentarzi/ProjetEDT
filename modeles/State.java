package modeles;

import java.util.ArrayList;
/**
 * une classe reprsente un état de notre systéme 
 */
public class State { 

	ArrayList<TimeTableV2> list;
	int TabNumber;
	public ArrayList<TimeTableV2> getList() {
		return list;
	}
	public void setList(ArrayList<TimeTableV2> list) {
		this.list = list;
	}
	public int getTabNumber() {
		return TabNumber;
	}
	public void setTabNumber(int tabNumber) {
		TabNumber = tabNumber;
	}
	
	
}
