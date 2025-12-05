
package util.nodes;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Map;
import util.ServiceLinkedList;
import util.Service;
import util.nodes.CustomDSA.NeighbourNode;
import util.nodes.CustomDSA.TreeNode;
import util.*;
import java.util.HashSet;
import java.util.ArrayList;

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
	protected Map<String, ServiceLinkedList> availableService;
	protected PriorityQueue<NeighbourNode> Neighbours = new PriorityQueue<>((a, b) -> Float.compare(a.getDist(), b.getDist()));
	protected Boolean Incident = true;
	protected Map<Integer, ArrayList<Incident>> incidents;
	
	public Node(int id, String LocationName) {
		this.id = id;
		this.LocationName = LocationName;
		incidents.put(1, new ArrayList<Incident>());
		incidents.put(2, new ArrayList<Incident>());
		incidents.put(3, new ArrayList<Incident>());
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
	
	public Map<String, ServiceLinkedList> getServices(){
		return this.availableService;
	}
	
	public void addService(Service x) {
		synchronized(this){
			if (this.availableService.containsKey(x.getServiceType())) {
				String Type = x.getServiceType();
				ServiceLinkedList list = this.availableService.get(Type);
				ServiceLinkedList ToAdd = new ServiceLinkedList(x);
				ToAdd.setNext(list);
				this.availableService.put(Type, ToAdd);
				
			}else {
				this.availableService.put(x.getServiceType(), new ServiceLinkedList(x));
			}
		}
	}
	
	
	public void setService(Map<String, ServiceLinkedList> x) {
		this.availableService = x; 
	}
	
	public Service popService(String Type) {
		synchronized(this) {
			if (this.availableService.containsKey(Type)) {
				ServiceLinkedList n = this.availableService.get(Type);
				this.availableService.put(Type, n!=null? n.getNext(): null);
				return n.getValue();
			}
			return null;
		}
	}
	
	public Map<String, ServiceLinkedList> getAvailableServices(){
		return this.availableService;
	}
	
	public void removeServiceById(Service node) {
		synchronized(this) {
			ServiceLinkedList dummy = this.availableService.get(node.getServiceType());
			if (dummy==null) return;
			if (dummy.getValue() == node || node.getID() == dummy.getValue().getID()) {
				this.availableService.put(dummy.getValue().getServiceType(),dummy.getNext());
			}
			else {
				while (dummy.getNext() != null) {
					Service n = dummy.getNext().getValue(); 
					if ( n == node || node.getID() == n.getID()) {
						dummy.setNext(dummy.getNext().getNext());
						break;
					}
					dummy = dummy.getNext();
				}
			}
		}
	}	
	
	// returns a the incident rate of each kind of incident.
	public Map<Integer, Float> getIncidentRate() {
		Map<Integer, Float> rate = new HashMap<>();
		for (int x: this.incidents.keySet()) {
			float number = this.incidents.get(x).size()/100; 
			rate.put(x, number);
		}
		return rate;
	}
	
}
