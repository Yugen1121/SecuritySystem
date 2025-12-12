package util.nodes.CustomDSA;

import util.nodes.Node;

/**
 * Represents a neighbouring node in a graph-like structure, typically used in
 * algorithms involving pathfinding, network traversal, or custom data structures.
 *
 * <p>
 * Each holds:
 * <ul>
 *     <li>A reference to the associated object</li>
 *     <li>A distance value (often used as a weight or cost)</li>
 *     <li>Status flags indicating whether the neighbour is open, congested,
 *         or in peak congestion</li>
 *     <li>A reference to its parent neighbour, enabling backtracking in 
 *         algorithms like Dijkstra or A*</li>
 * </ul>
 * </p>
 *
 * <p>
 * Thread safety:  
 * The methods that modify or read mutable boolean flags (`open` and `congested`)
 * are synchronized on this, ensuring safe access when used in concurrent
 * environments. Other fields parent are not synchronized and 
 * should be handled carefully if multithreading is involved.
 * </p>
 */

public class NeighbourNode {
	private float dist;
	private Node node;
	private Boolean open = true;
	private Boolean congested = false;
	private NeighbourNode parent = null;
	
	public NeighbourNode(NeighbourNode n) {
		dist = n.getDist();
		node = n.getNode();
		open = n.getOpen();
		congested = n.getCongested();
		parent = n.getParent();
	}
	
	public NeighbourNode(Node node, float key) {
		this.node = node;
		this.dist = key;
		
	}
	
	public void setDist(float d) {
		this.dist = d;
	}
	
	public float getDist() {
		return this.dist;
	}
	
	public Node getNode() {
		return this.node;
	}
	
	public Boolean getOpen() {
		synchronized(this) {
			return this.open;
		}
	}
	
	public void toggleOpen() {
		synchronized(this) {
			this.open = !this.open;
		}
	}
	
	public Boolean getCongested() {
		synchronized(this) {
			return this.congested;
		}
	}
	
	public void toggleCOngested() {
		synchronized(this) {
			this.congested = !this.congested;
		}
	}
	
	public NeighbourNode getParent() {
		return this.parent;
	}
	
	public void setParent(NeighbourNode parent) {
		this.parent = parent;
	}
}
