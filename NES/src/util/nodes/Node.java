
package util.nodes;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Map;
import util.ServiceLinkedList;
import util.Service;
import util.nodes.CustomDSA.NeighbourNode;
import util.*;
import java.util.HashSet;
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

public abstract class Node {
	private int id;
	protected String LocationName;
	protected Map<String, ServiceLinkedList> availableService;
	protected PriorityQueue<NeighbourNode> Neighbours = new PriorityQueue<>((a, b) -> Float.compare(a.getDist(), b.getDist()));
	protected Boolean Incident = true;
	
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
	
	public void addService(Service x) {
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
	
	
	public void setService(Map<String, ServiceLinkedList> x) {
		this.availableService = x; 
	}
	
	public ServiceLinkedList popService(String Type) {
		if (this.availableService.containsKey(Type)) {
			ServiceLinkedList OldHead = this.availableService.get(Type);
			ServiceLinkedList NewHead = OldHead.getNext();
			this.availableService.put(Type, NewHead);
			return OldHead;
			
		}
		return null;
	}
	
	
	private void MakeRequest(int incidentType, int EmergencyLevel) {
		Map<String, Integer> requestedServices = new HashMap<>();
		if (EmergencyLevel >= 9) {
			// Police 5
			requestedServices.put(Police.Type, 5);
			// If fire
			if (incidentType == 1) {
				requestedServices.put(FireTruck.Type, 2);
				requestedServices.put(Ambulance.Type, 3);
			}
			// if injury / attack
			else if(incidentType == 2) {
				requestedServices.put(Ambulance.Type, 3);
			}
		}
		else if(EmergencyLevel >= 7) {
			// Police 4
			requestedServices.put(Police.Type, 4);
			// If fire
			if (incidentType == 1) {
				requestedServices.put(FireTruck.Type, 2);
				requestedServices.put(Ambulance.Type, 2);
						}
			// if injury / attack
			else if(incidentType == 2) {
				requestedServices.put(Ambulance.Type, 3);
		}


		}
		else if(EmergencyLevel >= 5) {
			// Police 3
			requestedServices.put(Police.Type, 3);
			// If fire
			if (incidentType == 1) {
				requestedServices.put(FireTruck.Type, 1);
				requestedServices.put(Ambulance.Type, 2);
			}
			// if injury / attack
			else if(incidentType == 2) {
				requestedServices.put(Ambulance.Type, 2);
			}

		}
		else if(EmergencyLevel >= 3) {
			// police 2
			requestedServices.put(Police.Type, 2);
			// If fire
			if (incidentType == 1) {
				requestedServices.put(FireTruck.Type, 1);
				requestedServices.put(Ambulance.Type, 1);
			}
			// if injury / attack
			else if(incidentType == 2) {
				requestedServices.put(Ambulance.Type, 1);
			}

		}
		else {
			// Police 1
			requestedServices.put(Police.Type, 1);
			// If fire
			if (incidentType == 1) {
				requestedServices.put(FireTruck.Type, 1);
				requestedServices.put(Ambulance.Type, 1);
			}
			// if injury / attack
			else if(incidentType == 2) {
				requestedServices.put(Ambulance.Type, 1);
			}

		}
		this.SearchForServices(requestedServices);
		
	}
	
	public Map<String, Integer> DispatchServices(Map<String, Integer> s, NeighbourNode Node) {
		Node x = Node.getNode();
		for (String map : s.keySet()) {
			// checks if the node has the services
			if (s.containsKey(map)) {
				ServiceLinkedList ahead = x.availableService.get(map);
				ServiceLinkedList behind = x.availableService.get(map);
				ServiceLinkedList head = x.availableService.get(map);
				// checking if the services that is in this  node are available
				while (ahead != null || s.get(map) > 0) {
					// head is available then we remove it from the list
					if (ahead.getValue().getAvailability()) {
						if (ahead == head) {
							head = head.getNext();
							behind = head;
							ahead.removeNext();
							ahead = head;
						}
						else {
							behind.removeNext();
							behind.setNext(ahead.getNext());
							ahead.removeNext();
							ahead = behind.getNext();
						}
						s.put(map, s.get(map)-1);
					}
					else {
						if (behind == ahead) {
							ahead = ahead.getNext();
						}
						else {
							behind = ahead;
							ahead = ahead.getNext();
						}
					}
				}
				if (s.get(map) <= 0) {
					s.remove(map);
				}
				this.availableService.put(map, head);
			
			}
		}

		return s;
	}
	
	public Map<String, Integer> DispatchServices(Map<String, Integer> s) {
		for (String map : s.keySet()) {
			// checks if the node has the services
			if (s.containsKey(map)) {
				ServiceLinkedList ahead = this.availableService.get(map);
				ServiceLinkedList behind = this.availableService.get(map);
				ServiceLinkedList head = this.availableService.get(map);
				// checking if the services that is in this  node are available
				while (ahead != null && s.get(map) > 0) {
					// head is available then we remove it from the list
					if (ahead.getValue().getAvailability()) {
						if (ahead == head) {
							head = head.getNext();
							behind = head;
							ahead.removeNext();
							ahead = head;
						}
						else {
							behind.removeNext();
							behind.setNext(ahead.getNext());
							ahead.removeNext();
							ahead = behind.getNext();
						}
						s.put(map, s.get(map)-1);
					}
					else {
						if (behind == ahead) {
							ahead = ahead.getNext();
						}
						else {
							behind = ahead;
							ahead = ahead.getNext();
						}
					}
				}
				if (s.get(map) <= 0) {
					s.remove(map);
				}
				this.availableService.put(map, head);
			
			}
		}

		return s;
	}
	
	public void SearchForServices(Map<String, Integer> s) {
		// check if the node itself has the services needed
		// iterating through the list of services that are needed
		s = this.DispatchServices(s);	
		if (!s.isEmpty()) {
			PriorityQueue<NeighbourNode> heap = new PriorityQueue<>(this.Neighbours);
			NeighbourNode node = heap.poll();
			HashSet<Integer> visited = new HashSet<>();
			while (!s.isEmpty() && node != null) {
				if (!visited.contains(node.getNode().getID())) {
					visited.add(node.getNode().getID());
					s = this.DispatchServices(s, node);
					if(s.isEmpty()) {
						break;
					}
					
					this.makeDuplicate(node, heap);
				}
				node = heap.poll();
			}
			
		}
		
		return;
		
	};
	
	private PriorityQueue<NeighbourNode> makeDuplicate(NeighbourNode parent, PriorityQueue<NeighbourNode> heap) {
		// add the copy to the heap with increased distance 
		for (NeighbourNode i:  new PriorityQueue<>(parent.getNode().Neighbours)) {
			// makes sure the closed road is avoided
			if (i.getOpen()) {
				float dist = i.getDist()+parent.getDist();
				Node n = i.getNode();
				// if the road is congested then the distance is assumed to be 1.1 times the original
				if (i.getCongested()) {
					dist *= 1.1;
				}
				NeighbourNode y = new NeighbourNode(n, dist);
				y.setParent(parent);
				heap.add(y);
			}
		}
		return heap;
	}
	
	
	
}
