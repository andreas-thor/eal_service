package eal.service.format.eal;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Item_SC extends Item {

	
	private class Answer {
		
		private String text;
		private int points;
		
		private Answer(String text) {
			super();
			this.text = text;
			this.points = 0;
		}
		
	}
	
	
	private List<Item_SC.Answer> answers = new ArrayList<Item_SC.Answer>();
	
	
	public void addAnswer (String text) {
		this.answers.add(this.new Answer(text));
	}
	
	public String getAnswerText(int index) {
		return this.answers.get(index).text;
	}
	
	public int getAnswerPoints(int index) {
		return this.answers.get(index).points;
	}
	
	public void setAnswerPoints (int index, String points) {
		try {
			this.answers.get(index).points = Integer.valueOf(points);
		} catch (NumberFormatException e) {
		}
	}
	
	public int getNumberOfAnswers () {
		return this.answers.size();
	}
	



	
	
	public Stream<Answer> getAnswers () {
		return answers.stream();
	}
	

	@Override
	public int getPoints() {

		int res = 0;
		for (Answer a: this.answers) {
			res = Math.max(res, a.points);
		}
		return res;
	}
}
