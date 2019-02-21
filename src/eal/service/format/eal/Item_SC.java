package eal.service.format.eal;

import java.util.ArrayList;
import java.util.List;

public class Item_SC extends Item {

	
	public class Answer {
		
		public String text;
		public int points;
		
		public Answer(String text) {
			super();
			this.text = text;
		}
		
		
	}
	
	public List<Item_SC.Answer> answers = new ArrayList<Item_SC.Answer>();
	
	

	@Override
	public int getPoints() {

		int res = 0;
		for (Answer a: this.answers) {
			res = Math.max(res, a.points);
		}
		return res;
	}
}
