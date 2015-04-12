
public class Distance {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//-73.978165	40.757977	-73.989838	40.751171
		//-73.989937	40.756775	-73.86525	40.77063
		haversine("40.756775","-73.989937", "40.77063", "-73.86525");
	}
	
    /**
     * @param args
     * arg 1- latitude 1
     * arg 2 - latitude 2
     * arg 3 - longitude 1
     * arg 4 - longitude 2
     */
    public static double haversine(String lat1, String long1, String lat2, String long2) {
        // TODO Auto-generated method stub
        final int R = 6371; // Radious of the earth
        double doublelat1 = Double.parseDouble(lat1);
        double doublelon1 = Double.parseDouble(long1);
        double doublelat2 = Double.parseDouble(lat2);
        double doublelon2 = Double.parseDouble(long2);
        double latDistance = toRad(doublelat2-doublelat1);
        double lonDistance = toRad(doublelon2-doublelon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + 
                   Math.cos(toRad(doublelat1)) * Math.cos(toRad(doublelat2)) * 
                   Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double distance = R * c;
        
        return distance;
 
    }
     
    private static double toRad(double d) {
        return d * Math.PI / 180;
    }

}
