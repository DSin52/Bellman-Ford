/**
 * Calculates the distance between two locations using the Haversine Formula.
 */
public class Distance {

	/**
	 * Calculates the distance between two locations using the Haversine
	 * Formula.
	 * 
	 * @param lat1
	 *            Latitude of object from Set A
	 * @param long1
	 *            Longitude of object from Set A
	 * @param lat2
	 *            Latitude of object from Set B
	 * @param long2
	 *            Longitude of object from Set A
	 * @return The distance using the Haversine formula
	 */
	public static double haversine(String lat1, String long1, String lat2,
			String long2) {
		// TODO Auto-generated method stub
		final int R = 6371; // Radious of the earth
		double doublelat1 = Double.parseDouble(lat1);
		double doublelon1 = Double.parseDouble(long1);
		double doublelat2 = Double.parseDouble(lat2);
		double doublelon2 = Double.parseDouble(long2);
		double latDistance = toRad(doublelat2 - doublelat1);
		double lonDistance = toRad(doublelon2 - doublelon1);
		double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
				+ Math.cos(toRad(doublelat1)) * Math.cos(toRad(doublelat2))
				* Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double distance = R * c;

		return distance;

	}

	/**
	 * Converts degrees to radians
	 * 
	 * @param d
	 *            Degree value
	 * @return Radian value
	 */
	private static double toRad(double d) {
		return d * Math.PI / 180;
	}

}
