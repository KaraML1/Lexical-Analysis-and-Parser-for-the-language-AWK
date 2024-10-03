import java.util.LinkedList;

public class DoWhileNode extends StatementNode {
	
	private Node condition;
	private BlockNode statements;
			
	DoWhileNode (Node condition, BlockNode statements) {
		this.condition = condition;
		this.statements = statements;
	}
	
	Node getCondition() {
		return condition;
	}
	
	LinkedList<StatementNode> getStatements() {
		return statements.getStatements();
	}
	
	@Override
	public String toString() {
		return "[DoWhileNode: Condition: " + condition.toString() + ", Statements: " + statements.toString() + "]";
	}
}
