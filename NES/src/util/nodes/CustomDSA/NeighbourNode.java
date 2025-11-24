package util.nodes.CustomDSA;

import util.nodes.Node;

public class NeighbourNode {
	private int dist;
	private Node node;
	private Boolean Open = true;
	
	public NeighbourNode(Node node, int key) {
		this.node = node;
		this.dist = key;
	}
	
	public int getDist() {
		return this.dist;
	}
	
	public Node getNode() {
		return this.node;
	}
	
}
