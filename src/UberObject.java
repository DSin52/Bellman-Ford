
public class UberObject {

	private String latitude;
	private String longitude;
	
	public UberObject(String data, String data2) {
		this.setLatitude(data);
		this.setLongitude(data2);
	}

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

}
