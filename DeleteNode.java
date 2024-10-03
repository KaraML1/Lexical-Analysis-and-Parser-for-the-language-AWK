import java.util.LinkedList;
import java.util.Optional;


public class DeleteNode extends StatementNode {

	private Node name;
	private Optional<LinkedList<Node>> indices; // Could have no indices
	
	DeleteNode (Node name, Optional<LinkedList<Node>> indices) {
		this.name = name;
		if (indices.isPresent()) {
			this.indices = indices;
		} else indices = Optional.empty();
	}
	
	Node getReference() {
		return name;
	}
	
	@Override
	public String toString() {
		return "[DeleteNode: Name: " + name.toString() + ", Indices: " + indices.get().toString() + "]";
	}

	public LinkedList<Node> getIndices() {
		if (indices.isPresent()) return this.indices.get();
		else return null;
	}
}
