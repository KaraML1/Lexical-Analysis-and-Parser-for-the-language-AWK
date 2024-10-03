import java.util.LinkedList;
import java.util.Optional;

public class FunctionCallNode extends StatementNode {
	
	private Node value;
	private Optional<LinkedList<Node>> parameters = Optional.of(new LinkedList<Node>());
	
	public FunctionCallNode(Node value, Optional<LinkedList<Node>> param) {
		this.value = value; // A function Call can only be a Variable Reference or a AWKCommandNode
		this.parameters = param;
	}
	
	
	
	public String getName() throws Exception {
		if (value instanceof VariableReferenceNode) return ((VariableReferenceNode) value).getName();
//		else if (name instanceof FunctionCallNode) return ((FunctionCallNode) name).getName();
		else if (value instanceof AWKCommandNode) return ((AWKCommandNode) value).getName();
		else throw new Exception("FunctionCallNode: Expected a VariableReferenceNode or a Function Call Node");
	}
	
	public Node getValue() {
		return value;
	}
	
	public LinkedList<Node> getParameters() {
		if (parameters.isPresent()) {
			return this.parameters.get();
		} else return null;
	}

	public String toString() {
		return "FunctionCallNode: Name: " + value.toString() + ", Parameters: " + parameters.toString();
	}
}