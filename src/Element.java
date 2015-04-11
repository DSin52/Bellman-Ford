import java.util.ArrayList;


public class Element {

	private int x;
	private int y;
	private int weight;
	
	public Element(int x, int y, int weight) {
		this.setX(x);
		this.setY(y);
		this.setWeight(weight);
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
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
		return "(" + this.getX() + ", " + this.getY() + ")";
	}

}
