import java.util.Optional;

public class MathOpNode extends Node {

	private Optional<Node> left;
	private Optional<Node> right;
	private Optional<Token> operation; // Can be [ * / + - ]
	
	MathOpNode(Optional<Node> left, Optional<Node> right, Optional<Token> operator) {
		this.left = left;
		this.right = right;
		this.operation = operator;
	}
	
	@Override
	public String toString() {
		return "[MathOpNode: Left: " + left.toString() + ", Right: " + right.toString() + ", Operation: " + operation.toString() + "]";
	}

}

