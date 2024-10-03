import java.util.Optional;

public class VariableReferenceNode extends Node {

	private Token name;
	private Optional<Node> index;
	
	VariableReferenceNode(Token name, Optional<Node> node) {
		this.name = name;
		this.index = node; // Node that is the expression for the index
	}
	
	String getName() {
		return name.toString();
	}
	
    Node getIndex() {
    	if (index.isPresent()) return index.get();
    	else return null;
    }

	@Override
	public String toString() {
		return "[VariableReferenceNode: Name: " + this.name + ", Index: " + this.index + "]";
	}

}
