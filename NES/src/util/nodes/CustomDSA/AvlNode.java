package util.nodes.CustomDSA;

import util.nodes.Node;

public class AvlNode {
	int dist;
	Node node;
	int height = 1;
	AvlNode left, right;
	
	public AvlNode(Node node, int key) {
		this.node = node;
		this.dist = key;
	}
	
	
}
