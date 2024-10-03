import java.util.Optional;

public class ForEachNode extends StatementNode {
	
	private Optional<Node> operation = Optional.empty();
	
	public ForEachNode(Optional<Node> parseOperation) {
		operation = parseOperation;
	}

	@Override
	public String toString() {
		return "[ForEachNode: " + operation.get().toString() +"]";
	}
}