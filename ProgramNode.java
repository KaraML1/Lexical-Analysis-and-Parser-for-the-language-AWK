import java.util.LinkedList;

public class ProgramNode extends Node {

	LinkedList<Node> beginNodes = new LinkedList<Node>();
	LinkedList<Node> endNodes = new LinkedList<Node>();
	LinkedList<Node> otherNodes = new LinkedList<Node>();
	LinkedList<FunctionDefinitionNode> functionNodes = new LinkedList<FunctionDefinitionNode>();

	
	@Override
	public String toString() {
		return "[beginNodes: " + beginNodes.toString() + ", endNodes: " + endNodes.toString() + ", otherNodes: " + otherNodes.toString() + ", functionNodes: " + functionNodes.toString() + "]";
	}

}
