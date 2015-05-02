/**
 * Represents an object (taxi/customer) from the Uber data set.
 */
public class UberObject {

	private String latitude;
	private String longitude;

	/**
	 * Constructor that builds a taxi or customer using their latitude/longitude
	 * location
	 * 
	 * @param latitude
	 *            Latitude coordinate of taxi/customer
	 * @param longitude
	 *            Longitude coordinate of taxi/customer
	 */
	public UberObject(String latitude, String longitude) {
		this.setLatitude(latitude);
		this.setLongitude(longitude);
	}

	/**
	 * Getters and Setters
	 */

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	
	public String toString() {
		return "(" + latitude + ", " + longitude + ")"; 
	}

}
