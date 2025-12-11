
package util.nodes;
import java.util.HashMap;
import java.util.PriorityQueue;
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
	private int id;
	protected String LocationName;
	protected PriorityQueue<NeighbourNode> Neighbours = new PriorityQueue<>((a, b) -> Float.compare(a.getDist(), b.getDist()));
	protected Boolean Incident = true;
	protected Map<String, Map<Integer, Incident>> incidents;
	protected Map<String, Map<Integer, Service>> services;
	protected Map<String, Integer> requiredServices;
	
	public Node(int id, String LocationName) {
		this.id = id;
		this.LocationName = LocationName;
		incidents.put(Police.Type, new HashMap<Integer, Incident>());
		incidents.put(FireTruck.Type, new HashMap<Integer, Incident>());
		incidents.put(Ambulance.Type, new HashMap<Integer, Incident>());
		
		services.put(Police.Type, new HashMap<Integer, Service>());
		services.put(FireTruck.Type, new HashMap<Integer, Service>());
		services.put(Ambulance.Type, new HashMap<Integer, Service>());
	}

	abstract String getNodeType();
	
	public String getLocation() {
		return this.LocationName;
	}
	
	public void setID(int id) {
		this.id = id;
	}
	
	public int getID() { 
		return this.id;
	}
	
	public PriorityQueue<NeighbourNode> getNeighbour(){
		return this.Neighbours;
	}
	
	public Map<String, Map<Integer, Service>> getServices(){
		return this.services;
	}
	
	public void addService(Service x) {
		synchronized(this){
			Map<Integer, Service> type = this.services.get(x.getServiceType());
			if (type != null) {
				type.put(x.getID(), x);
			}
			else {
				this.services.put(x.getServiceType(), new HashMap<Integer, Service>());
				this.services.get(x.getServiceType()).put(x.getID(), x);
			}
		}
	}
	
	
	public void setService(Map<String, Map<Integer, Service>> x) {
		this.services = x; 
	}
	

	
	
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
			float number = this.incidents.get(x).size()/100; 
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
		int x = i.getType();
		this.incidents.get(x).put(i.getId(), i);
		
			
	}
	
	public Map<String, Integer> numberOfCurrentServices() {
		Map<String, Integer> re = new HashMap<String, Integer>();
		for (String x: this.services.keySet()) {
			re.put(x, this.services.get(x).size());
		}
		return re;
	}
	
	public Map<String, Integer> getRequiredServices(){
		return this.requiredServices;
	}
}
