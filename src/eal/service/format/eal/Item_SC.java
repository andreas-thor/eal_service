package eal.service.format.eal;

public class Item_SC extends Item_MC {

	
//	private class Answer {
//		
//		private String text;
//		private int points;
//		
//		private Answer(String text) {
//			super();
//			this.text = text;
//			this.points = 0;
//		}
//		
//	}
//	
//	
//	private List<Item_SC.Answer> answers = new ArrayList<Item_SC.Answer>();
	
	
//	public void addAnswer (String text) {
//		this.answers.add(this.new Answer(text));
//	}
//	
//	public String getAnswerText(int index) {
//		return this.answers.get(index).text;
//	}
	
	public int getAnswerPoints(int index) {
//		return this.answers.get(index).points;
		return this.getAnswerPoints(index, true);
	}
	
	public void setAnswerPoints (int index, String points) {
		
		this.setAnswerPoints(index, true, points);
		this.setAnswerPoints(index, false, "0");
		
//		try {
//			this.answers.get(index).points = Integer.valueOf(points);
//		} catch (NumberFormatException e) {
//		} catch (IndexOutOfBoundsException e) {
////			System.out.println("mm");
//		}
	}
	
//	public int getNumberOfAnswers () {
//		return this.answers.size();
//	}
	
	@Override
	public int getMinNumber() {
		return 1;
	}

	@Override
	public int getMaxNumber() {
		return 1;
	}

	
	
//	public Stream<Answer> getAnswers () {
//		return answers.stream();
//	}
	

	@Override
	public int getPoints() {

		int res = 0;
		for (int index=0; index<this.getNumberOfAnswers(); index++) {
			res = Math.max(res, this.getAnswerPoints(index));
		}
		return res;
	}
}
