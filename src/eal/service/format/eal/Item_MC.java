package eal.service.format.eal;

import java.util.ArrayList;
import java.util.List;


public class Item_MC extends Item {

	public class Answer {
		public String text;
		public int points_pos;
		public int points_neg;

		public Answer(String text) {
			super();
			this.text = text;
			this.points_pos = 0;
			this.points_neg = 0;
		}

	}

	public List<Answer> answers = new ArrayList<Answer>();
	public int minnumber = 0;
	public int maxnumber = 0;
	
	
	
	@Override
	public int getPoints() {
		int res = 0;
		for (Answer a: this.answers) {
			res += Math.max(a.points_pos, a.points_neg);
		}
		return res;
	}
}
