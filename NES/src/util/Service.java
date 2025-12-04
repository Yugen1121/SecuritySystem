package util;
import util.nodes.*;
import util.nodes.CustomDSA.NeighbourNode;
import util.nodes.CustomDSA.TreeNode;

/**
 * Represents the base abstract entity for all emergency or utility service
 * units in the system. A Service object models a moving agent such as
 * a police car, ambulance, fire truck, or utility repair vehicle.
 *
 * <p>
 * Each service has:
 * <ul>
 *     <li>A unique service ID</li>
 *     <li>A current location represented by a Node</li>
 *     <li>Availability and incident level indicators</li>
 *     <li>A path represented as a chain of NeighbourNode instances</li>
 *     <li>A speed and distance tracker for movement simulation</li>
 * </ul>
 * </p>
 *
 * <h2>Responsibilities</h2>
 * <ul>
 *     <li>Maintain core properties of a service unit (ID, location, status).</li>
 *     <li>Provide movement logic through the run() method.</li>
 *     <li>Ensure service subclasses define their service type through getServiceType().</li>
 * </ul>
 *
 * <h2>Subclass Requirements</h2>
 * <ul>
 *     <li>Must implement getServiceType() to define the service's category.</li>
 *     <li>Should initialize additional state values such as speed, availability, etc.</li>
 * </ul>
 *
 * <h2>Thread Safety</h2>
 * <p>
 * Critical fields that may be accessed by multiple threads (location, path, availability,
 * incident level) are protected via synchronized blocks to avoid inconsistent reads
 * and writes. Movement logic inside run() assumes invocation from a controlled loop.
 * </p>
 */

public abstract class Service{
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
		synchronized (this) {
			this.place = place;
	    }
		
	}
	
	public int getID() {
		return this.id;
	}
	
	public void setPath(NeighbourNode Path) {
		synchronized(this) {
			this.path = Path;
		}
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
		synchronized(this) {
			return this.available;
		}
	}
	
	public int getIncidentLevel() {
		synchronized(this) {
			return this.incidentLevel;
		}
	}
	
	public abstract String getServiceType();
	
	
	
		
}
