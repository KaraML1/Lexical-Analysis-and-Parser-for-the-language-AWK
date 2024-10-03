import java.util.Optional;

public class IfNode extends StatementNode {
	
	private Optional<node> head = Optional.empty(); // Head of LinkedList
	
	public class node {
		/*
		 * Note: This is a custom built Linked List within If Node. The head is the first IF statement.
		 * The next on the chain is either an ELSE IF or the tail, ELSE, statement.
		 */
		private boolean type = false; // True if an "if or elif statement", else false if an "else statement"
		private Optional<node> neighbor = Optional.empty();
		private Optional<BlockNode> statements = Optional.empty();
		private Optional<Node> operation = Optional.empty();
		
		node(BlockNode statements, Optional<Node> operation, boolean ifType) {
			this.type = ifType;
			this.operation = operation;
			this.statements = Optional.of(statements);
		}
		
		Node getOperation() {
			if (operation.isPresent()) return operation.get();
			else return null;
		}
		
		node getNeighbor() {
			if (neighbor.isPresent()) return neighbor.get();
			else return null;
		}
		
		BlockNode getStatements() {
			if (statements.isPresent()) return statements.get();
			else return null;
		}
		
		Boolean getType() {
			return type;
		}
		
		@Override
		public String toString() { // Print the Type (If / Else), Condition, Statement Block, and Neighbor
			if (type) {
				return "[ifNodeData: Type: IF, Operation: " + operation.toString() + ", Statements: " + statements.toString() + ", Neighbor: " + neighbor.toString() + "]";
			} else return "[ifNodeData: Type: ELSE, Operation: " + operation.toString() + ", Statements: " + statements.toString() + ", Neighbor: " + neighbor.toString() + "]";
		}
	}
	
	node getHead() {
		if (head.isPresent()) return head.get();
		else return null;
	}
	
	/*
	 * Add new nodes to the Linked List
	 */
	public boolean add(BlockNode statements, Optional<Node> operation, boolean ifType) {
		if (head.isEmpty()) { // If head is empty (new IfNode)
			head = Optional.of(new node(statements, operation, ifType));
			return true;
		} else { // Head is not empty, search through LinkedList to find next place
			Optional<node> current = head;
			while (current.get().neighbor.isPresent()) {
				current = current.get().neighbor;
			}
			current.get().neighbor = Optional.of(new node(statements, operation, ifType));
			return true;
		}
	}
	
	@Override
	public String toString() {
		return "IfNode Head: " + head.toString();
	}
}
