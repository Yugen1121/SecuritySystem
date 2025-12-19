package util;
import util.nodes.Node;
import util.nodes.CustomDSA.NeighbourNode;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import java.util.ArrayList;

/**
 * The Environment class represents the core simulation controller.
 * <p>
 * It manages:
 * <ul>
 *   <li>All nodes in the system</li>
 *   <li>All services and their allocation</li>
 *   <li>Incident creation, prioritisation, and resolution</li>
 *   <li>Service dispatching and dynamic reallocation</li>
 * </ul>
 *
 * <p>
 * This class acts as the central coordinator, executing updates,
 * running service logic, and applying pathfinding based dispatch
 * strategies to allocate and reallocate services efficiently.
 */

public class Environment {
	/** Stores all the nodes in the system using their id **/
	private final ObservableMap<Integer, Node> nodes = FXCollections.observableHashMap();
	
	/** Stores all the Services in the system using their id **/
	private final ObservableMap<Integer, Service> services = FXCollections.observableHashMap();
	
	/** Stores all the Incident in the system using their id **/
	private final ObservableMap<Integer, Incident> incident = FXCollections.observableHashMap();
	
	/** 
	 * Stores all the running Incident in the system 
	 * in priority order using their incident level
	 * **/
	private PriorityQueue<Incident> runningIncidents = new PriorityQueue<Incident>(
			(a, b) -> Integer.compare(b.getIncidentLevel(), a.getIncidentLevel())
			);
	
	public Environment() {
		
	}
	
	/**
	 * Updates the environment state
	 * 
	 * @param updateAll if true, all the nodes recalculates the required services;
	 * other wise just updated the services 	
	 */
	public void update(Boolean updateAll) {
		if (updateAll) {
			for (Node n: this.nodes.values()) {
				n.recalculateServiceRequired();
			}
		}
		for (Service n: this.services.values()) {
			n.run();
		}
		return;
	}
	/**
	 * generates a new service id
	 * @return the next available service id
	 */
	public int getNewServiceId() {
		return services.size() + 1;
	}
	
	/**
	 * adds service s to the environment 
	 * @param s the service to add
	 */
	public void addService(Service s) {
		services.put(s.getID(), s);
	}
	
	/**
	 * returns all the nodes in the environment 
	 * @return returns the Map of node id to node
	 */
	public Map<Integer, Node> getNodes(){
		return this.nodes;
	}
	
	
	/**
	 * adds node using id
	 * @param id is used to map to the node
	 * @param node is the value id points to in the map
	 */
	public void addNode(int id, Node node) {
		this.nodes.put(id, node);
	}
	
	/**
	 * makes a incident request
	 * adds the incident to incident map and runningIncident
	 * checks the incident level and incident type and assigns the number
	 * of each services required to the incident
	 * Searches for the required service to allocate them to the incident
	 * @param incidentType used to determine the type of incident
	 * @param EmergencyLevel used to indicate the severity of the incident
	 * @param node used to know which node the incidet took place
	 */
	public void MakeRequest(int incidentType, int EmergencyLevel, Node node) {
		Incident New = new Incident(incident.size()+1, node, EmergencyLevel, incidentType);
		this.incident.put(New.getId(), New);
		this.runningIncidents.add(New);
		node.addToIncidents(New);
		node.addToRunningIncident(New);
		
		// Service requirement base of incident type and EmergencyLevel
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
	
	/**
	 * Used to recalculate the total distance from node 1 to end node
	 * input: endNode<x-node1<y+x-node2
	 * right now the starting holds the total distance to travel 
	 * the algorithm flips it and makes the end node the total distance to travel
	 * and recalculates the total distance from curr node to each node in the path
	 * 
	 * @param node linkedlist to recalculate the distance
	 * @return recalculated distance 
	 */
	public NeighbourNode flipStartPos(NeighbourNode node) {
		ArrayList<NeighbourNode> ls = new ArrayList<>();
		ArrayList<Float> ds = new ArrayList<>();
		
		while (node != null) {
			//[n1,n2,n2,n3,dest
			ls.add(new NeighbourNode(node));
			//[ 43,33,22,11,0]
			ds.add(node.getDist());
			node = node.getParent();
		}
		
		for (int i = 0; i < ls.size() - 1; i++) {
	        ls.get(i).setParent(ls.get(i + 1));
	    }
	    ls.get(ls.size() - 1).setParent(null);
		
		// start from the end 
		float prev = 0;
		for (int i = 1; i < (ls.size()); i++) {
			NeighbourNode n = ls.get(i);
			float x = ds.get(i);
			float y = ds.get(i-1);
			float newDist = y - x + prev;
			prev = newDist;
			n.setDist(newDist);
		}
		
		NeighbourNode head = ls.get(0); 
		head.setDist(0);
		return head;
	}
	
	/**
	 * Flips the path
	 * input: start<-node1<-node2<-end
	 * output: start->node1->node2->end
	 * @param node  linked list to flip 
	 * @return
	 */
	public NeighbourNode flipPath(NeighbourNode node) {
		NeighbourNode head = new NeighbourNode(node);
		NeighbourNode ptr = new NeighbourNode(head.getParent());
		head.setParent(null);
		while (ptr != null ) {
			NeighbourNode nxt = ptr.getParent();
			ptr.setParent(head);
			head = ptr;
			ptr = nxt;
		}
		return head;
		
	}
	
	public Map<String, Integer> DispatchServices(Map<String, Integer> s, NeighbourNode node, Incident incident) {
		
		Map<String, Map<Integer, Service>> mainMap = new HashMap<>(node.getNode().getServices());
		for (String st: mainMap.keySet()) {
			mainMap.put(st, new HashMap<>(mainMap.get(st)));
		}
		
		for (String key : s.keySet()) {
			// checks if the node has the services
			
				if (mainMap.containsKey(key)) {
					Map<Integer, Service> services = mainMap.get(key);
					// get the map of type of incident
					Iterator<Integer> it = services.keySet().iterator();
					
					while (it.hasNext() && s.get(key) > 0){
						Service curr = services.get(it.next()); 
						if (curr.available) {
							
							curr.allocate(flipStartPos(node), incident.getIncidentLevel());
							
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
	
	private PriorityQueue<NeighbourNode> makeDuplicate(NeighbourNode start, PriorityQueue<NeighbourNode> heap) {
		NeighbourNode parent = new NeighbourNode(start);
		// add the copy to the heap with increased distance 
		for (NeighbourNode i:  new PriorityQueue<>(parent.getNode().getNeighbour())) {
			// makes sure the closed road is avoided
			if (i.getOpen() == true) {
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
	
	public void incidentDelt(Incident inc) {
		inc.getNode().removeRunningIncident(inc);
		Map<String, ArrayList<Integer>> dsList = inc.getDispatchList();
		for (String s: dsList.keySet()) {
			for (Integer i: dsList.get(s)) {
				Service serv = this.services.get(i);
				serv.setAvailable();
				serv.setPath(null);
			}
		}
		this.reallocateServices(inc);
		inc.offRunning();
	}
	
	// dynamic reassignment of the services after the incident is resolved
	// The idea here is to check the number of services required in the current node. 
	// if the node has just enough services for it currently then no need to reassign.
	// if the node has excess then make a table an use the dijkster's algorithm similar the one used to look for closest services.
	//check if the current node has excess services
	private void reallocateServices(Incident inc) {
		Node node = inc.getNode();
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
			Integer num = required.get(s);
			if (num == null || num <= 0) continue;
			while (iterServ.hasNext() && num > 0) {
				Service service = serv.get(iterServ.next());
				if (service.getAvailability()) {
					
					iterServ.remove();
					num--;
				}
			}
			if (serv.isEmpty()) {
				iterService.remove();
			}
			
		}
		

		// A deep copy of the neighbouring node priority queue
		PriorityQueue<NeighbourNode> hp = new PriorityQueue<>(
				(a, b) -> Float.compare(a.getDist(), b.getDist())
				);
		PriorityQueue<NeighbourNode> queue = makeDuplicate(new NeighbourNode(node, 0), hp);
		// HashSet to keep track of the visited nodes
		HashSet<Integer> visited = new HashSet<>();
		// loop should end wither when all the services are allocated or when the queue is empty
		while (!queue.isEmpty() && !services.isEmpty()) {
			// pull a node from the queue 
			NeighbourNode node1 = queue.poll();
			// check if its already in the visited list
			if (visited.contains(node1.getNode().getID())) continue;
			// add to visisted
			visited.add(node1.getNode().getID());
			// get the needed services 
			Map<String, Integer> req = node1.getNode().getNeededServices();
			// loop through the Map of list of services
			for (String s: req.keySet()) {
					Map<Integer, Service> serv = services.get(s);
					if (serv == null) continue;
					Iterator<Integer> iter = serv.keySet().iterator();
					int num = req.get(s);
					while(iter.hasNext() && num > 0) {
						// get an service from serv then removee it 
						int key = iter.next();
						Service curr = serv.get(key);
						if (!curr.getAvailability()) continue;
						curr.reallocate(flipPath(node1));
						iter.remove();
						// decrement the req.get(s) by 1
						num --;
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
