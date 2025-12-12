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
		if (path == null) return;
		if (path.getParent() == null) {
			path = null;
			this.distanceTravelled = 0;
		}
		// increase the distance
		this.distanceTravelled += this.speed;
		// check if the distance travelled is equal or greater than the nex node dist
		if (this.distanceTravelled >= path.getParent().getDist()) {
			// remove the service from current node
			path.getNode().removeService(this);
			// move to next node
			path = path.getParent();
			Node n = path.getNode();
			// update the position
			n.addService(this);
		}
		// if the current node is the end the stop
		if (this.path.getParent() == null) {
			path = null;
			this.distanceTravelled = 0;
		}
	}
	
	public boolean getAvailability() {
		synchronized(this) {
			return this.available;
		}
	}
	
	public void setUnAvailable() {
		this.available = false;
	}
	
	public void setAvailable() {
		this.available = true;
	}
	
	public int getIncidentLevel() {
		synchronized(this) {
			return this.incidentLevel;
		}
	}
	
	public abstract String getServiceType();
	
	
	
		
}
