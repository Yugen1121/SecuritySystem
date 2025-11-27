package util;
import util.nodes.*;
import util.nodes.CustomDSA.NeighbourNode;
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
	protected final Integer id;
	protected Node place;
	protected boolean available;
	protected int incidentLevel = 0;
	protected NeighbourNode path;
	protected int speed;
	protected float distanceTravelled;
	public Service(Node place, int id) {
		this.place = place;
		this.id = id;
	}
	
	public String getlocation() {
		return this.place.getLocation();
	}
	
	public void setLocation(Node place) {
		this.place = place;
	}
	
	public int getID() {
		return this.id;
	}
	
	public void setPath(NeighbourNode Path) {
		this.path = Path;
	}
	
	public void run() {
		/*
		 * As we have the variable called path we want the main function to check if the path 
		 * variable is null or not if its not null we want the service to run at a speed per second/frame
		 * say we have a said speed for each of the services and distance tracker. Each frame increases
		 * the distance by the speed. if the distance reaches a (path.dist - path.parent.dist) then change
		 * path to its parent. We continue this until the path becomes null.
		 */
		
		/*
		 * Psudo code
		 * update the distance 
		 * if the parent is not null
		 * 	check the distance if its greater than the current node dist - parent node dist 
		 * 	if less than or equal to 0 
		 * 	than update
		 * else 
		 * 	current node dist - dist
		 * 	if less than or equal to 0
		 * 		then update
		 * 		dist = 0
		 *  
		 */
		this.distanceTravelled += this.speed;
		if (this.path.getParent() != null) {
			float difference = this.path.getDist()-this.path.getParent().getDist();
			if (difference - this.distanceTravelled <= 0) {
				this.distanceTravelled = this.distanceTravelled - difference;
				this.path.getNode().removeServiceById(this);
				this.path = this.path.getParent();
				this.path.getNode().addService(this);
				this.place = this.path.getNode();
			}
		}
		else {
			if((this.path.getDist() - this.distanceTravelled) <= 0) {
				this.path = null;
				this.distanceTravelled = 0;
			}
		}
	}
	
	public boolean getAvailability() {
		return this.available;
	}
	
	public int getIncidentLevel() {
		return this.incidentLevel;
	}
	
	public abstract String getServiceType();
	
	
	
		
}
