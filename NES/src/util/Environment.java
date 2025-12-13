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
	private Map<Integer, Node> nodes = new HashMap<>();
	private Map<Integer, Service> services = new HashMap<>();
	private Map<Integer, Incident> incident = new HashMap<>();
	private PriorityQueue<Incident> runningIncidents = new PriorityQueue<Incident>(
			(a, b) -> Integer.compare(b.getIncidentLevel(), a.getIncidentLevel())
			);
	
	
	public void addNode(int id, Node node) {
		this.nodes.put(id, node);
	}
	
	
	public void MakeRequest(int incidentType, int EmergencyLevel, Node node) {
		Incident New = new Incident(incident.size()+1, node, EmergencyLevel, incidentType);
		this.incident.put(New.getId(), New);
		this.runningIncidents.add(New);
		
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
		this.SearchForServices(requestedServices, New);
		
	}
	
	
	public NeighbourNode flipStartPos(NeighbourNode node) {
		ArrayList<NeighbourNode> ls = new ArrayList<>();
		ArrayList<Float> ds = new ArrayList<>();
		while (node != null) {
			ls.add(new NeighbourNode(node));
			ds.add(node.getDist());
			node = node.getParent();
		}
		
		// linkes new nodes
		for (int i = 0; i < ls.size()-1; i++) {
			ls.get(i).setParent(ls.get(i+1));
		}
	
		float prev = 0;
		for (int i = ds.size()-2; i > -1; i--) {
			NeighbourNode n = ls.get(i);
			float x = ds.get(i);
			float y = ds.get(i+1);
			float newDist = y-x + prev;
			prev = newDist;
			n.setDist(newDist);
		}
		NeighbourNode head = ls.get(ls.size()-1); 
		head.setDist(0);
		return head;
	}
	
	public NeighbourNode flipPath(NeighbourNode node) {
		ArrayList<NeighbourNode>ls = new ArrayList<>();
		// make a copy of all the node in the path
		while (node != null) {
			// add to the list
			ls.add(new NeighbourNode(node));
			// move to parent node
			node = node.getParent();
		}
		// change the pointer node
		// iterate backwards
		for (int i = ls.size()-1; i > 0; i--) {
			// point the node at index i to node at index i-1
			ls.get(i).setParent(ls.get(i-1));
		}
		ls.get(0).setParent(null);
		return ls.get(ls.size()-1);
	}
	
	public Map<String, Integer> DispatchServices(Map<String, Integer> s, NeighbourNode node, Incident incident) {
		Map<String, Map<Integer, Service>> mainMap = node.getNode().getServices();
		for (String key : s.keySet()) {
			// checks if the node has the services
			
				if (mainMap.containsKey(key)) {
					Map<Integer, Service> services = mainMap.get(key);
					// get the map of type of incident
					Iterator<Integer> it = services.keySet().iterator();
					
					while (it.hasNext() && s.get(key) > 0){
						Service curr = services.get(it.next()); 
						if (curr.available) {
							curr.setPath(flipStartPos(node));
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
		PriorityQueue<NeighbourNode> hp = new PriorityQueue<>(
				(a, b) -> Float.compare(a.getDist(), b.getDist())
				);
		s = this.DispatchServices(s, new NeighbourNode(n, 0), incident);	
		if (!s.isEmpty()) {
			PriorityQueue<NeighbourNode> heap = this.makeDuplicate(new NeighbourNode(n, 0), hp);
			NeighbourNode node = heap.poll();
			HashSet<Integer> visited = new HashSet<>();
			visited.add(n.getID());
			while (!s.isEmpty() && node != null) {
				if (!node.getOpen()) {
					node = heap.poll();
					continue;
				}
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
		Map< String, Integer> required = new HashMap<>(node.getRequiredServices());
		Map<String, Map<Integer, Service>> services = new HashMap<>(node.getServices());
		for (String i: services.keySet()) {
			services.put(i, new HashMap<Integer, Service>(services.get(i)));
		}
		Iterator<String> iterService = services.keySet().iterator();
		while (iterService.hasNext()) {
			String s = iterService.next();
			Map<Integer, Service> serv = services.get(s);
			Iterator<Integer> iterServ = serv.keySet().iterator();
			while (iterServ.hasNext()) {
				Service service = serv.get(iterServ.next());
				if (service.getAvailability()) {
					int num = required.get(s);
					if (num > 0) {
						iterServ.remove();
						required.put(s, num-1);
					}
					else {
						break;
					}
				}
			}
			if (serv.isEmpty()) {
				iterService.remove();
			}
			
		}
		// initialises a starting node
		NeighbourNode start = new NeighbourNode(node, 0);
		// A deep copy of the neighbouring node priority queue
		PriorityQueue<NeighbourNode> queue = makeDuplicate(start, node.getNeighbour());
		// HashSet to keep track of the visited nodes
		HashSet<Integer> visited = new HashSet<>();
		// loop should end wither when all the services are allocated or when the queue is empty
		while (!queue.isEmpty() && !services.isEmpty()) {
			// pull a node from the queue 
			NeighbourNode node1 = queue.poll();
			// check if its already in the visited list
			if (visited.contains(node1.getNode().getID())) continue;
			// get the needed services 
			Map<String, Integer> req = node1.getNode().getNeededServices();
			// loop through the Map of list of services
			for (String s: req.keySet()) {
					Map<Integer, Service> serv = services.get(s);
					if (serv == null) continue;
					Iterator<Integer> iter = serv.keySet().iterator();
					while(iter.hasNext() && req.get(s) > 0) {
						
						// get an service from serv then removee it 
						int key = iter.next();
						Service curr = serv.get(key);
						curr.setPath(flipPath(node1));
						iter.remove();
						// decrement the req.get(s) by 1
						req.put(s, req.get(s)-1);
					}
					// checks if the services is empty after the iteration.
					if (serv.isEmpty()) {
						services.remove(s);
					}
				}
			// add the neighbour of current node and point the node1 to the new nodes
			this.makeDuplicate(node1, queue);
			
		}
		
	}
	
	
	
}
