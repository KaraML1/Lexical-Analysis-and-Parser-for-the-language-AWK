
public class ReturnNode extends StatementNode {
		
	private Node param;
	
	ReturnNode(Node param) {
		this.param = param;
	}
	
	Node getData() {
		return this.param;
	}
	
	@Override
	public String toString() {
		return "[ReturnNode: Param: " + param.toString() + "]";
	}
}
