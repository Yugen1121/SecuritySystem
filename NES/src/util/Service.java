package util;
import util.nodes.*;
import util.nodes.CustomDSA.NeighbourNode;

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
	/** stores the service id**/
	protected final Integer id;
	
	/** stores the node where the service is at **/
	protected Node place;
	
	/** store weather the service is available **/
	protected boolean available = true;
	
	/** stores the incident level it is assigned to**/
	protected int incidentLevel = 0;
	
	/** stores the linked list of path **/
	protected NeighbourNode path;
	
	/** stores the speed of the services **/
	protected float speed = 0.5f;
	
	/** stores the distace travelled **/
	protected float distanceTravelled;
	
	/**
	 * a constructor 
	 * @param place current location 
	 * @param id id of the service
	 */
	public Service(Node place, int id) {
		this.place = place;
		this.id = id;
	}
	
	/** 
	 * 
	 * @return the current location 
	 */
	public String getlocation() {
		return this.place.getLocationName();
	}
	
	/**
	 * sets current location
	 * @param place where the service is at
	 */
	public void setLocation(Node place) {
		synchronized (this) {
			this.place = place;
	    }
		
	}
	
	/**
	 * 
	 * @return the id of the service
	 */
	public int getID() {
		return this.id;
	}
	
	/**
	 * 
	 * @param Path sets the path 
	 */
	public void setPath(NeighbourNode Path) {
		synchronized(this) {
			this.path = Path;
		}
	}
	
	/**
	 * when it is reallocated to a node it sets the path and makes the service available
	 * @param path
	 */
	public void reallocate(NeighbourNode path) {
		
		this.incidentLevel = 0;
		this.setAvailable();
		setPath(path);
		
	}
	
	/**
	 * sets the path and incident level it is assigned to and sets unavailable
	 * @param path
	 * @param level
	 */
	public void allocate(NeighbourNode path, int level) {
		
			setPath(path);
			this.incidentLevel = level;
			this.setUnAvailable();
		
	}
	
	/**
	 * increase the distance and changes the position of the node and moves along the path 
	 */
	public void run() {
		if (path == null) return;
		if (path.getParent() == null) {
			if (this.incidentLevel == 0) {
				this.setAvailable();
			}
			path = null;
			this.distanceTravelled = 0;
			return;
		}
		// increase the distance
		this.distanceTravelled += this.speed;
		// check if the distance travelled is equal or greater than the nex node dist
		if (this.distanceTravelled >= path.getParent().getDist()) {
			System.out.println(this.id +": "+ "at location " + this.place.getLocationName());
			// remove the service from current node
			path.getNode().removeService(this);
			// move to next node
			path = path.getParent();
			Node n = path.getNode();
			// update the position
			n.addService(this);
			if (path != null) {
				this.place = path.getNode();
			}
		}
		
		if (path.getParent() == null) {
			System.out.println(this.id + " arrived at destination " + this.path.getNode().getLocationName());
			if (this.incidentLevel == 0) {
				this.setAvailable();
			}
			path = null;
			this.distanceTravelled = 0;
			return;
		}
		
	}
	
	/**
	 * 
	 * @return availability of the service 
	 */
	public boolean getAvailability() {
		synchronized(this) {
			return this.available;
		}
	}
	
	/**
	 * make the service unavailable
	 */
	public void setUnAvailable() {
		synchronized(this) {
			this.available = false;
		}
	}
	
	/** 
	 * make the service available
	 */
	
	public void setAvailable() {
		synchronized(this) {
			this.available = true;
		}
	}
	
	/**
	 * returns the incident level
	 * @return
	 */
	public int getIncidentLevel() {
		synchronized(this) {
			return this.incidentLevel;
		}
	}
	
	/**
	 * for children class to set 
	 * @return the service type
	 */
	public abstract String getServiceType();
	
	
	
		
}
