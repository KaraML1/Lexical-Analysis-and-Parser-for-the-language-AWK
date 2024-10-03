import java.util.Optional;

public class ForNode extends StatementNode {
	
	private Optional<Node> initialization = Optional.empty(); // The first part of a For Loop, Optional, can be empty
	private Node condition; // The second part, a FOR loop MUST have a condition to be valid
	private Optional<Node> afterExecution = Optional.empty(); // The third part, Optional, can be empty
	private BlockNode statements;
	
	ForNode (Optional<Node> initialization, Node condition, Optional<Node> afterExecution, BlockNode statements) {
		this.initialization = initialization;
		this.condition = condition;
		this.afterExecution = afterExecution;
		this.statements = statements;
	}
	
	Node getInitialization() {
		if (initialization.isPresent()) return this.initialization.get();
		else return null;
	}
	
	Node getCondition() {
		return this.condition;
	}
	
	Node getExecution() {
		if (afterExecution.isPresent()) return this.afterExecution.get();
		else return null;
	}
	
	BlockNode getStatements() {
		return this.statements;
	}
	
	@Override
	public String toString() {
		return "[ForNode: Initialization: " + initialization.toString() + ", Condition: " + condition.toString() + ", AfterExecution: " + afterExecution.toString() + "]";
	}
}
