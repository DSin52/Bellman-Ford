

public class Element {

	private int x;
	private int y;
	private double weight;
	
	public Element(int x, int y, double costMatrix2) {
		this.setX(x);
		this.setY(y);
		this.setWeight(costMatrix2);
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double costMatrix2) {
		this.weight = costMatrix2;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}
	
	public String toString() {
		return this.getWeight() + ": (" + this.getX() + ", " + this.getY() + ")";
	}

}
