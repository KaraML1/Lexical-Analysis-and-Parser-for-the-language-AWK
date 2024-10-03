import java.util.Optional;

public class AssignmentNode extends StatementNode {

	private Node target;
	private Node expression;
	
	AssignmentNode(Node target, Node expression) {
		this.target = target;
		this.expression = expression;
	}
	
	AssignmentNode(Node target, OperationNode opNode) {
		this.target = target;
		this.expression = opNode;
	}
	
	Node getTarget() {
		return target;
	}
	
	Node getExpression() {
		return expression;
	}
	
	@Override
	public String toString() {
		return "AssignmentNode: Target: " + target.toString() + ", Expression: " + expression.toString();
	}

}