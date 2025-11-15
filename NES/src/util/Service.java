package util;
import util.nodes.*;

/*
 * This is the base entity for anything related to service in the system.
 * 
 * <p>
 * This class provides attributes and function for all the emergency or utility services has unique id and is associated
 * with Node class representing it's current location.
 * </p>
 * 
 * <h2>
 * Responsibilities 
 * </h2>
 * <ul>
 * <li>Store the id of the service.</li>
 * <li>Store and manage the current location of the service.</li>
 * <li>Enforce that all the service subclass has their service type.</li>
 * </ul>
 * 
 * <h2>Subclass requirement</h2>
 * <ul>
 * <li>Must implement the getServiceType().</li>
 * <li>Should assign the two variable.</li>
 * </ul>
 */


public abstract class Service {
	protected Integer id;
	protected Node place;
	
	public String getlocation() {
		return this.place.getLocation();
	}
	
	public void setLocation(Node place) {
		this.place = place;
	}
	
	public int getID() {
		return this.id;
	}

	
	public abstract String getServiceType();
	
	protected void setID(int id) {
		this.id = id;
	}	
		
}
