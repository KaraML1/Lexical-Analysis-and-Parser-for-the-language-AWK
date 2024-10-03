import java.util.Optional;

public class OperationNode extends StatementNode {

	private Node left;
	private Optional<Node> right = Optional.empty();
	private Operations operation;
	
	// Binary Operation
	OperationNode (Node left, Optional<Node> right, Operations operation) {
		this.left = left;
		this.right = right;
		this.operation = operation;
	}
	
	// Unary Operation
	OperationNode (Node left, Operations operation) {
		this.left = left;
		this.operation = operation;
	}
	
	static enum Operations {
        EQ, NE, LT, LE, GT, GE, AND, OR, NOT, MATCH, NOTMATCH, DOLLAR,
        PREINC, POSTINC, PREDEC, POSTDEC, UNARYPOS, UNARYNEG, IN, 
        EXPONENT, ADD, SUBTRACT, MULTIPLY, DIVIDE, MODULO, CONCATENATION
	}
	
	@Override
	public String toString() {
		return "[OperationNode: Left Node: " + left.toString() + ", Right Node: " + right.toString() + ", Type: " + this.operation.toString() + "]";
	}

	public Node getLeft() {
		return left;
	}
	
	public Operations getOperation() {
		return operation;
	}
	
	public Node getRight() {
		if (right.isPresent()) return right.get();
		else return null;
	}

}
