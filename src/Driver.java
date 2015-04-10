import java.util.ArrayList;


public class Driver {

	public static void main(String[] args) {
		
		//Build the cost matrix of the graph
//		int [][] costMatrix = new int[Integer.parseInt(args[0])][Integer.parseInt(args[0])];
		int[][] costMatrix = {{5, 1, 7}, {13, 1, 2}, {6, 1, 9}};
		
//		buildElementMatrix(costMatrix);
		
		ArrayList<Element> matched = new ArrayList<Element>();
		ArrayList<Element> unmatched = buildElementMatrix(costMatrix);
		
		//1st step: get smallest weight from 
		

	}
	
	public static ArrayList<Element> buildElementMatrix(int [][] costMatrix) {
		
		ArrayList<Element> elementList = new ArrayList<Element>();
		
		for (int i = 0; i < costMatrix.length; i++) {
			for (int j = 0; j < costMatrix[0].length; j++) {
				Element element = new Element(i, j, costMatrix[i][j]);
				elementList.add(element);
			}
		}
		return elementList;
	}
}
