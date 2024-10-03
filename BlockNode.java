import java.util.LinkedList;
import java.util.Optional;

public class BlockNode extends Node {

	private LinkedList<StatementNode> statementNodes;
	private Optional<Node> condition;
	
	
	BlockNode(Optional<Node> condition, LinkedList<StatementNode> statementNodes) {
		this.condition = condition;
		this.statementNodes = statementNodes;
	}

	LinkedList<StatementNode> getStatements() {
		return this.statementNodes;
	}
	
	Node getCondition() {
		if (condition.isPresent()) return condition.get();
		else return null;
	}
	
	@Override
	public String toString() {
		return "[BlockNode - Condition: " + condition.toString() + ", Statements: " + statementNodes.toString() + "]";
	}
}
