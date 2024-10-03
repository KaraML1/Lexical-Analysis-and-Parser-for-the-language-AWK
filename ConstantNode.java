import java.util.Optional;

public class ConstantNode extends Node {

	private Token token;
	
	ConstantNode(Token node) {
		this.token = node;
	}
	
	public String getValue() {
		return token.toString();
	}
	
	@Override
	public String toString() {
		return "[Constant Node: " + token.toString() + "]";
	}

}
