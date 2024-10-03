
public class TernaryNode extends Node {

	private Node boolExpression; // Condition
	private Node trueCase;
	private Node falseCase;
	
	TernaryNode(Node boolExpression, Node trueCase, Node falseCase) {
		this.boolExpression = boolExpression;
		this.trueCase = trueCase;
		this.falseCase = falseCase;
	}
	
	public Node getBoolExpression() {
		return boolExpression;
	}
	
	public Node getTrueCase() {
		return trueCase;
	}
	
	public Node getFalseCase() {
		return falseCase;
	}
	
	@Override
	public String toString() {
		return "[TernaryNode: BoolExpression: " + boolExpression.toString() + ", trueCase: " + trueCase.toString() + ", falseCase: " + falseCase.toString() + "]";
	}

}
