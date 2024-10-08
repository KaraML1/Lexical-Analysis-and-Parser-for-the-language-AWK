public class WhileNode extends StatementNode {
	
	private Node condition;
	private BlockNode statements;
	
	WhileNode (Node condition, BlockNode statements) {
		this.condition = condition;
		this.statements = statements;
	}
	
	public Node getCondition() {
		return condition;
	}
	
	public BlockNode getStatements() {
		return statements;
	}
			
	@Override
	public String toString() {
		return "[WhileNode: Condition: " + condition.toString() + ", Statements: " + statements.toString() + "]";
	}
}
