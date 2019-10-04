package modeles;

import java.util.Calendar;

public class PairDate {
/**
 * une classe représente 
 */
	private Calendar start,end;

	public PairDate(Calendar start, Calendar end) {
		super();
		this.start = start;
		this.end = end;
	}

	public Calendar getStart() {
		return start;
	}

	public void setStart(Calendar start) {
		this.start = start;
	}

	public Calendar getEnd() {
		return end;
	}

	public void setEnd(Calendar end) {
		this.end = end;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return start+"\n"+end;
	}
}
