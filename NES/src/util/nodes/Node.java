
package util.nodes;
import java.util.HashMap;
import java.util.Iterator;
import java.util.PriorityQueue;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.util.Map;
import util.Service;
import util.nodes.CustomDSA.NeighbourNode;
import util.*;
import util.Ambulance;
import util.FireTruck;
import util.Police;

/*
 * This is the base entity for anything related to Node/Location in the system.
 * 
 * <p>
 * This class provides attributes and function for all the locations has unique id and is associated
 * with ServiceLinkedList to keep track of services in the location.
 * </p>
 * 
 * <h2>
 * Responsibilities 
 * </h2>
 * <ul>
 * <li>Store the id of the Location.</li>
 * <li>Store and manage the current location and its services.</li>
 * <li>Enforce that all the Node subclass has their services.</li>
 * </ul>
 * 
 * <h2>Subclass requirement</h2>
 * <ul>
 * <li>Must implement the getNodeType().</li>
 * <li>Must assign id and LocationName.</li>
 * </ul>
 */

public abstract class Node{
	/** stores id of the node **/
	private final int id;
	
	/** stores location name **/
	protected String LocationName;
	
	/** stores all the neighbour of the node in a priority min heap using the distance between them **/
	protected PriorityQueue<NeighbourNode> Neighbours = new PriorityQueue<>((a, b) -> Float.compare(a.getDist(), b.getDist()));
	
	/** stores all the incidents in a max heap using their incident level  **/
	protected PriorityQueue<Incident> runningIncidents = new PriorityQueue<>((b, a) -> Integer.compare(a.getIncidentLevel(), b.getIncidentLevel()));
	
	/** a map of incident type pointing to a map of incident id pointing to incident **/
	protected ObservableMap<String, ObservableMap<Integer, Incident>> incidents = FXCollections.observableHashMap();
	
	/** a map of service type pointing to a map of integer pointing to service **/
	protected ObservableMap<String, ObservableMap<Integer, Service>> services = FXCollections.observableHashMap();
	
	/** a map of service type pointing to number of such type required **/
	public ObservableMap<String, Integer> requiredServices = FXCollections.observableHashMap();
	
	/**
	 * Constructor to initialise all the variables
	 * @param id node id
	 * @param LocationName location name
	 */
	public Node(int id, String LocationName) {
		this.id = id;
		this.LocationName = LocationName;
		incidents.put(Police.Type, FXCollections.observableHashMap());
		incidents.put(FireTruck.Type, FXCollections.observableHashMap());
		incidents.put(Ambulance.Type, FXCollections.observableHashMap());
		
		services.put(Police.Type, FXCollections.observableHashMap());
		services.put(FireTruck.Type, FXCollections.observableHashMap());
		services.put(Ambulance.Type, FXCollections.observableHashMap());
		
		requiredServices.put(Police.Type, 1);
		requiredServices.put(Ambulance.Type, 1);
		requiredServices.put(FireTruck.Type, 1);
	}
	
	/**
	 * returns all the incident
	 * @return incidents
	 */
	public ObservableMap<String, ObservableMap<Integer, Incident>> getIncidents(){
		return this.incidents;
	}
	
	/**
	 * adds a neighbour node
	 * @param node neighbour too add
	 */
	public void addNeighbor(NeighbourNode node) {
		this.Neighbours.add(node);
	}
	
	/**
	 * adds a incident to running incident
	 * @param i incident to add
	 */
	public void addToRunningIncident(Incident i) {
		this.runningIncidents.add(i);
	}
	
	/**
	 * returns a queue of running Incident
	 * @return returns running incident
	 */
	public PriorityQueue<Incident> getRunningIncident() {
		return this.runningIncidents;
	}
	
	/**
	 * removes a incident from running list
	 * @param inc incident to remove
	 */
	public void removeRunningIncident(Incident inc) {
		Iterator<Incident> itr = runningIncidents.iterator();
		while (itr.hasNext()) {
			Incident i = itr.next();
			if (i == inc) {
				itr.remove();
				break;
			}
		}
	}
	
	/**
	 * returns if the node has any running incident
	 * @return returns if the running incident is empty
	 */
	public boolean hasIncident() {
		return !this.runningIncidents.isEmpty();
	} 
	
	/**
	 * for children class
	 * @return the type of class
	 */
	abstract String getNodeType();
	
	/**
	 * @return location name of the node
	 */
	public String getLocationName() {
		synchronized(this) {
		return this.LocationName;
		}
	}
	
	/**
	 * 
	 * @return the id of the node
	 */
	public int getID() { 
		return this.id;
	}
	
	/**
	 * returns all the neighbour of the node
	 * @return the neighbours queue
	 */
	public PriorityQueue<NeighbourNode> getNeighbour(){
		return this.Neighbours;
	}
	
	/**
	 * returns all the services in the node
	 * @return the map of service type pointing to mpa of service id pointing to service
	 */
	public ObservableMap<String, ObservableMap<Integer, Service>> getServices(){
		return this.services;
	}
	
	/**
	 * add a service to the service map
	 * @param x service to add
	 */
	public void addService(Service x) {
		synchronized(this){
			Map<Integer, Service> type = this.services.get(x.getServiceType());
			if (type != null) {
				type.put(x.getID(), x);
			}
			else {
				this.services.put(x.getServiceType(), FXCollections.observableHashMap());
				this.services.get(x.getServiceType()).put(x.getID(), x);
			}
		}
	}
	
	/**
	 * removes the service from the node
	 * @param node service to remove
	 */
	public void removeService(Service node) {
		synchronized(this) {
			Map<Integer, Service> type = this.services.get(node.getServiceType());
			if (type != null) {
				type.remove(node.getID());
			}
			
		}
	}	
	
	// returns a the incident rate of each kind of incident.
	public Map<String, Float> getIncidentRate() {
		Map<String, Float> rate = new HashMap<>();
		for (String x: this.incidents.keySet()) {
			float number = this.incidents.get(x).size()/10; 
			rate.put(x, number);
		}
		return rate;
	}
	
	// re-caluclates the number of services required in the node
	public void recalculateServiceRequired() {
		Map<String, Float> incidentRate = this.getIncidentRate();
		
		for (String x: incidentRate.keySet()) {
			Float rate = incidentRate.get(x);
			if (rate < 0.3) {
				this.requiredServices.put(x, 1);
			}
			else if (rate < 1) {
				this.requiredServices.put(x, 2);
			}
			else if (rate < 2) {
				this.requiredServices.put(x, 3);
			}
			else {
				this.requiredServices.put(x, 4);
			}
		}
	}
	
	
	public void addToIncidents(Incident i) {
		if (i == null) return;
		String x = i.getType();
		this.incidents.get(x).put(i.getId(), i);
		
			
	}
	
	public Map<String, Integer> getNumberOfCurrentServices() {
		Map<String, Integer> re = new HashMap<String, Integer>();
		for (String x: this.services.keySet()) {
			re.put(x, this.services.get(x).size());
		}
		return re;
	}
	
	public Map<String, Integer> getRequiredServices(){
		return this.requiredServices;
	}
	
	public Map<String, Integer> getNeededServices(){
		Map<String, Integer> r = new HashMap<String, Integer>(this.requiredServices);
		Map<String, Integer> currentNum = this.getNumberOfCurrentServices();
		for (String i: currentNum.keySet()) {
			Integer num = currentNum.get(i);
			if (num != null) {
				
				int result = r.get(i) - num;
				if (result > 0) {
					r.put(i, result);
				}
				else {
					r.put(i, 0);
				}
			}
		}
		return r;
	}
}
