import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;


public class Driver {

	public static void main(String[] args) {
		
		//Build the cost matrix of the graph
//		int [][] costMatrix = new int[Integer.parseInt(args[0])][Integer.parseInt(args[0])];
		int[][] costMatrix = {{5, 1, 7}, {13, 1, 2}, {6, 1, 9}};
				
		ArrayList<Element> matched = new ArrayList<Element>();
		ArrayList<Element> unmatched = buildElementMatrix(costMatrix);
		
		while (matched.size() < costMatrix.length) {
			int index = 0;
			while (index < unmatched.size()) {
				Element ele = unmatched.get(index);
				//add unique to matching (minimum (augment(index.getX())))
				HashMap<Integer, ArrayList<Element>> map = augment(ele.getX(), matched, unmatched);
//				HashMap<Integer, ArrayList<Element>> map = new HashMap<Integer, ArrayList<Element>>();
//				ArrayList<Element> test = new ArrayList<Element>();
//				ArrayList<Element> test2 = new ArrayList<Element>();
//				test.add(new Element(1, 1, 5));
//				test2.add(new Element(2, 2, 10));
//				map.put(5, test);
//				map.put(10, test2);
				ArrayList<Element> mappings = map.get(Collections.min(map.keySet()));
//				System.out.println(mappings.toString());
				for (int i = 0; i < mappings.size(); i++) {
					if (!matched.contains(mappings.get(i))) {
						matched.add(mappings.get(i));
					}
				}
				index += costMatrix.length;
			}
		}
		
//		//1st step, 1st iteration
//		Random rand = new Random();
//		int index = rand.nextInt(costMatrix.length);
//		ArrayList<Element> neighbors = findElements(index, unmatched, true);
//		//we would go through matching, but matching is none
//		//from the directed, add the smallest edge into the matching
//		Element minElement = min(neighbors);
//		matched.add(minElement);
//		unmatched.remove(minElement);
//		
//		System.out.println("Index: " + index);
//		
//		//1st step, 2nd iteration
//		//arbitrarily pick a node thats not already in the matching
//		while ((index = rand.nextInt(costMatrix.length)) == matched.get(0).getX()) {
//			;
//		}
//		
//		neighbors = findElements(index, unmatched, false);
//		
//		for (int i = 0; i < neighbors.size(); i++) {
//			calculatePath(neighbors.get(i), unmatched, matched);
//		}
	
	}
	
	public static HashMap<Integer, ArrayList<Element>> augment(int index, ArrayList<Element> matched, ArrayList<Element> unmatched) {
		HashMap<Integer, ArrayList<Element>> iterMap = new HashMap<Integer, ArrayList<Element>>();
		//Get neighbors of node in set A 
		ArrayList<Element> neighbors = findElements(index, unmatched, true);
		for (Element ele: neighbors) {
			if (unmatched.contains(ele)) {
				ArrayList<Element> temp = new ArrayList<Element>();
				temp.add(ele);
				iterMap.put(ele.getWeight(), temp);
			} else {
				ArrayList<Element> matchedValue = findElements(ele.getY(), matched, false);
				if (matchedValue.size() > 0) {
					HashMap<Integer, ArrayList<Element>> prevMap = augment(matchedValue.get(0).getX(), matched, unmatched);
					Integer minKey = Collections.min(prevMap.keySet());
					Integer tempKey = ele.getWeight() - matchedValue.get(0).getWeight() + minKey;
					ArrayList<Element> tempList = new ArrayList<Element>();
					tempList.add(ele);
					tempList.add(matchedValue.get(0));
					tempList.addAll(prevMap.get(tempKey));
					iterMap.put(tempKey, tempList);
				}
			}
		}
		
		//Remove all content in the map from unmatched
		return null;
		
	}
	
//	public static void calculateAllPaths(int index, ArrayList<Element> unmatched, ArrayList<Element> matched) {
//		ArrayList<Element> tempMatching = new ArrayList<Element>();
//		HashMap<Integer, ArrayList<Element>> map = new HashMap<Integer, ArrayList<Element>>();
//		
//		
//		//keep going until node is matched with another unmatched node
//		while (true) {
//			ArrayList<Element> neighbors = findElements(index, unmatched, false);
//			
//			for (int i = 0; i < neighbors.size(); i++) {
//				ArrayList<Element> test = new ArrayList<Element>();
//				test.add(neighbors.get(i));
//				map.put(neighbors.get(i).getWeight(), test);
//			}
//			
//			ArrayList<Element> matchedNeighbors = findElements(index, matched, false);
//			
//			
//
//		}
//		
//		
//	}
	
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
	
	public static ArrayList<Element> findElements(int toFind, ArrayList<Element> elements, boolean isX) {

		ArrayList<Element> firstLevelElements = new ArrayList<Element>();
		for (int i = 0; i < elements.size(); i++) {
			if (isX == true) {
				if (elements.get(i).getX() == toFind) {
					firstLevelElements.add(elements.get(i));
				}
			} else {
				if (elements.get(i).getY() == toFind) {
					firstLevelElements.add(elements.get(i));
				}
			}
		}
		return firstLevelElements;
	}
	
	public static boolean contains(int toFind, ArrayList<Element> elements, boolean isX) {
		for (int i = 0; i < elements.size(); i++) {
			if (isX == true) {
				if (elements.get(i).getX() == toFind) {
					return true;
				}
			} else {
				if (elements.get(i).getY() == toFind) {
					return true;
				}
			}
		}
		return false;
	}
	
	
	public static Element min(ArrayList<Element> elements) {
		Element minElement = elements.get(0);
		for (Element ele: elements) {
			if (ele.getWeight() < minElement.getWeight()) {
				minElement = ele;
			}
		}
		return minElement;
	}
	
}
