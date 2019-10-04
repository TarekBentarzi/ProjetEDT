package code.barbot;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Element;

public class AdeWeek {
	public boolean isSelected;
	public int adeWeekNumber;
	public String week;
	
	private static final Pattern pid = Pattern.compile("javascript:([a-zA-Z]*)[(]([^),]*)");
	private static final Pattern pweek = Pattern.compile("[(][ ]*([0-9]*)[ ]*[)]");
	
	public AdeWeek(Element w){
			isSelected = w.attr("class").equals("pianoselected");
			String str = w.select("area").attr("href");
			Matcher m = pid.matcher(str);
			if(m.find()){
				adeWeekNumber = Integer.valueOf(str.substring(m.start(2), m.end(2)));
			}
			week = w.select("img").first().attr("alt");
			
	}
	
	public String toString(){
		return adeWeekNumber +">"+ week + ">" + isSelected;
	}

}
