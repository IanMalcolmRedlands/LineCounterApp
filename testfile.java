package dictionaryapp;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;


//single comment 1
	//single comment 2




	/*
	 * Properly
	 * multi-line
	 * comment
	 * /
	 */


/* Single line multi-line comment 2 */

public class RBTree {
	private RBNode root;
	
	public RBTree() {
		root = null;
	}
	
	public void fakeFunction();
void   fakeFunction2 ();
	
	public void regexTest  (Animal thingy, int test, String test2)
	{
		
		Apple apple =   new   Apple();
		Banana banana=new Banana();
		Charles charles = new  Charles();
		
		switch(1) {
		case 1:
			return;
		default:
			return;
		}
		
		for (char character : test2) {
			return;
		}
		
		if (iansucks) {
			return;
		}
		else if (ianreallysucks) {
			return;
		}
		else
			return;
		
	}
	
	
	public boolean isEmpty() {
		return root==null;
	}
	
	private void setRoot(RBNode node) {
		node.setParentToNull();
		root = node;
	}
	

	public void insert(String word) {
		word = word.toLowerCase();
		word = word.strip();
		
		if (this.isEmpty()) {
			root = new RBNode(word, false);
			return;
		}
		
		RBNode parent = findNode(word);
		RBNode newNode = new RBNode(word);
		
		if (parent.equals(newNode)) {
			return;
		}
		else if (parent.getWord().compareTo(word) > 0) {
			parent.setRightChild(newNode);
		}
		else {
			parent.setLeftChild(newNode);
		}
		
		fixUpInsertion(newNode);
		
		assert this.rootHasNoParent();
		assert this.containsNoDuplicatesOrCycles();
	}
	
	/**
	 * Checks that a word exists in the dictionary.
	 * @param word
	 * @return
	 */
	public boolean containsWord(String word) {
		if (this.isEmpty()) {
			return false;
		}

		word = word.toLowerCase();
		word = word.strip();
		
		return this.findNode(word).getWord().equals(word);
	}
	
	private void rrotate(RBNode top) {
		if (top == null) {
			return;
		}
		
		RBNode newTop = top.getLeftChild();
		if (top.getParent() != null)
			top.getParent().switchChild(top, newTop);
		else
			this.setRoot(newTop);
		top.setLeftChild(newTop.getRightChild());
		newTop.setRightChild(top);
	}
	
	private void lrotate(RBNode top) {
		if (top == null) {
			return;
		}
		
		RBNode newTop = top.getRightChild();
		if (top.getParent() != null)
			top.getParent().switchChild(top, newTop);
		else
			this.setRoot(newTop);
		top.setRightChild(newTop.getLeftChild());
		newTop.setLeftChild(top);
	}
	
	
	/**
	 * Returns either the node containing the input string, or the parent node of where the containing node would be.
	 * Returns null if the tree is empty.
	 * @param word
	 * @return
	 */
	private RBNode findNode(String word) {
		if (this.isEmpty()) {
			return null;
		}
		
		RBNode curr = root;
		
		while ( !word.equals(curr.getWord()) ) {
			if (curr.getWord().compareTo(word)>0) {
				if (curr.getRightChild()==null)
					break;
					
				else
					curr = curr.getRightChild();
			}
			else {
				if (curr.getLeftChild()==null)
					break;
				
				else
					curr = curr.getLeftChild();
			}
		}
		
		return curr;
	}

	
	/**
	 * Returns the tree to balance after an insertion.
	 * @param inserted the newly-inserted node
	 */
	private void fixUpInsertion(RBNode inserted) {
		RBNode target = inserted;
		while(RBNode.isRed(target.getParent())) {
			RBNode parent = target.getParent();
			if (parent.equals(parent.getParent().getLeftChild())) {
				RBNode uncle = parent.getParent().getRightChild();
				if (RBNode.isRed(uncle)) {
					RBNode.makeBlack(parent);
					RBNode.makeBlack(uncle);
					RBNode.makeRed(parent.getParent());
					target = parent.getParent();
				}
				else {
					if (target.equals(parent.getRightChild())) {
						target = parent;
						lrotate(target);
						parent = target.getParent();
					}
					RBNode.makeBlack(parent);
					RBNode.makeRed(parent.getParent());
					rrotate(parent.getParent());
				}
			}
			else {
				RBNode uncle = parent.getParent().getLeftChild();
				if (RBNode.isRed(uncle)) {
					RBNode.makeBlack(parent);
					RBNode.makeBlack(uncle);
					RBNode.makeRed(parent.getParent());
					target = parent.getParent();
				}
				else {
					if (target.equals(parent.getLeftChild())) {
						target = parent;
						rrotate(target);
						parent = target.getParent();
					}
					RBNode.makeBlack(parent);
					RBNode.makeRed(parent.getParent());
					lrotate(parent.getParent());
				}
			}
			
			if (target.equals(root)) {
				break;
			}
		}
		
		RBNode.makeBlack(root);
	}
	
	
	public void breadthFirstSearch() {
		if (this.isEmpty())
			return;
		
		ArrayList<RBNode> nodes = new ArrayList<RBNode>();
		boolean nodesIsEmpty = false;
		
		nodes.add(root);
		
		while (!nodesIsEmpty) {
			nodesIsEmpty = true;
			ArrayList<RBNode> subNodes = new ArrayList<RBNode>();
			for (RBNode node : nodes) {
				if (node==null) {
					System.out.print("     x     ");
					
					subNodes.add(null);
					subNodes.add(null);
				}
				else {
					System.out.print(String.format("%1$11s", node.getWord()));

					subNodes.add(node.getLeftChild());
					subNodes.add(node.getRightChild());
					
					nodesIsEmpty = false;
				}
			}
			nodes = subNodes;
			System.out.println();
		}
		System.out.println();
	}

	
	private boolean containsNoDuplicatesOrCycles() {
		ArrayList<RBNode> traversedNodes = new ArrayList<RBNode>();
		Deque<RBNode> nextNodes = new LinkedList<RBNode>();
		
		nextNodes.add(root);
		
		while(!nextNodes.isEmpty() ) {
			RBNode curr = nextNodes.pop();
			
			for (RBNode node : traversedNodes) {
				if (node.equals(curr))
					return false;
			}
			
			if (curr.getLeftChild()!=null)
				nextNodes.add(curr.getLeftChild());
			if (curr.getRightChild()!=null)
				nextNodes.add(curr.getRightChild());
		}
		
		return true;
	}
	
	private boolean rootHasNoParent() {
		return root.getParent() == null;
	}
};