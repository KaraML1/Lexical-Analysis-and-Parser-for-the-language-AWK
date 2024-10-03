import java.util.LinkedList;
import java.util.Optional;

public class FunctionDefinitionNode extends Node {
	
	Token functionName;
	LinkedList<Optional<Token>> paramName;
	BlockNode block;
	
	LinkedList<Node> parameters = new LinkedList<Node>(); // Note: This is only for AWKCommandNodes. Everytime, a built-in function is called, a new function definition is created with those parameters
	
	
	FunctionDefinitionNode(Token name, LinkedList<Optional<Token>> params, BlockNode block) {
		this.functionName = name;
		this.block = block;
		this.paramName = params;
	}
	
	FunctionDefinitionNode(Token name, LinkedList<Node> parameters) {
		this.functionName = name;
		this.block = null;
		this.parameters = parameters;
	}

	public int getParamCount() {
		return paramName.size();
	}
	
	public LinkedList<Optional<Token>> getParamNames() {
		return this.paramName;
	}
	
	public String getFuncName() {
		return this.functionName.toString();
	}
	
	@Override
	public String toString() {
		if (parameters != null) return "[Function AWKCommand: " + functionName + ", Parameters: " + parameters.toString() + "]";
		return "[Function: " + functionName + ", Parameters: " + paramName.toString() + ", Block: " + block.toString() + "]";
	}
}
