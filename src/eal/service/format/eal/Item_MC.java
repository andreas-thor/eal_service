package eal.service.format.eal;

import java.util.ArrayList;
import java.util.List;


public class Item_MC extends Item {

	private class Answer {
		private String text;
		private int[] points;	// [0=negative; 1=positive]

		private Answer(String text) {
			this.text = text;
			this.points = new int[] {0, 0};
		}

	}

	private List<Answer> answers = new ArrayList<Answer>();
	private int minNumber = 0;
	private int maxNumber = 0;
	
	
	
	public void addAnswer (String text) {
		this.answers.add(this.new Answer(text));
	}
	
	public String getAnswerText(int index) {
		return this.answers.get(index).text;
	}
	
	public int getAnswerPoints(int index, boolean positive) {
		return this.answers.get(index).points[positive?1:0];
	}

	
	public void setAnswerPoints (int index, boolean positive, String points) {
		try {
			this.answers.get(index).points[positive?1:0] = Integer.valueOf(points);
		} catch (NumberFormatException | IndexOutOfBoundsException e) {
		}
	}
	
	public int getNumberOfAnswers () {
		return this.answers.size();
	}
	
	
	public int getMinNumber() {
		return minNumber;
	}

	public void setMinNumber(String min) {
		try {
			this.minNumber = Integer.valueOf(min);
		} catch (NumberFormatException e) {
			this.minNumber = 0;
		}
	}

	public int getMaxNumber() {
		return maxNumber;
	}

	public void setMaxNumber(String max) {
		try {
			this.maxNumber = Integer.valueOf(max);
		} catch (NumberFormatException e) {
			this.maxNumber = this.answers.size();
		}	
	}

	@Override
	public int getPoints() {
		int res = 0;
		for (Answer a: this.answers) {
			res += Math.max(a.points[0], a.points[1]);
		}
		return res;
	}
}
