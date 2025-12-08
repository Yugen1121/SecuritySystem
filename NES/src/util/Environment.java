package util;
import util.nodes.Node;
import util.nodes.CustomDSA.NeighbourNode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.ArrayList;
import util.Service;
public class Environment {
	private Map<Integer, Node> nodes;
	private Map<Integer, Service> services;
	private Map<Integer, Incident> incident;
	private PriorityQueue<Incident> runningIncidents = new PriorityQueue<Incident>(
			(a, b) -> Integer.compare(b.getIncidentLevel(), a.getIncidentLevel())
			);
	
	
	public void addNode(int id, Node node) {
		this.nodes.put(id, node);
	}
	
	public void addService(Service service) {
		
	}
	
	public void MakeRequest(int incidentType, int EmergencyLevel, Node node) {
		Incident New = new Incident(incident.size()+1, node, EmergencyLevel, incidentType);
		this.incident.put(New.getId(), New);
		
		Map<String, Integer> requestedServices = new HashMap();
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
		this.SearchForServices(requestedServices, New);
		
	}
	
	public Map<String, Integer> DispatchServices(Map<String, Integer> s, NeighbourNode node, Incident incident) {
		Map<String, Map<Integer, Service>> mainMap = node.getNode().getServices();
		for (String key : s.keySet()) {
			// checks if the node has the services
			
				if (mainMap.containsKey(key)) {
					Map<Integer, Service> services = mainMap.get(key);
					// get the map of type of incident
					Iterator<Integer> it = services.keySet().iterator();
					
					while (!services.keySet().isEmpty() && it.hasNext() && s.get(key) > 0){
						Service curr = services.get(it.next()); 
						if (curr.available) {
							curr.setPath(node);
							curr.setUnAvailable();
							incident.addToDispatchList(key, curr.getID());
							it.remove();
							s.put(key, s.get(key)-1);
						}
						
					}
				}
		}

			
		
		return s;
	}
	
		
	public void SearchForServices(Map<String, Integer> s, Incident incident) {
		// check if the node itself has the services needed
		// iterating through the list of services that are needed
		Node n = incident.getNode();
		s = this.DispatchServices(s, new NeighbourNode(n, 0), incident);	
		if (!s.isEmpty()) {
			PriorityQueue<NeighbourNode> heap = this.makeDuplicate(new NeighbourNode(n, 0), n.getNeighbour());
			NeighbourNode node = heap.poll();
			HashSet<Integer> visited = new HashSet<>();
			visited.add(n.getID());
			while (!s.isEmpty() && node != null) {
				if (!visited.contains(node.getNode().getID())) {
					visited.add(node.getNode().getID());
					s = this.DispatchServices(s, node, incident);
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
		for (NeighbourNode i:  new PriorityQueue<>(parent.getNode().getNeighbour())) {
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
	
	
	// dynamic reassignment of the services after the incident is resolved
	// The idea here is to check the number of services required in the current node. 
	// if the node has just enough services for it currently then no need to reassign.
	// if the node has excess then make a table an use the dijkster's algorithm similar the one used to look for closest services.
	
	
	//check if the current node has excess services
	private void reallocateServices(Node node) {
		
	}
	
}
