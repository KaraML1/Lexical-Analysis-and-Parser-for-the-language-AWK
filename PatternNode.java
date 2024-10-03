import java.util.Optional;

public class PatternNode extends Node {

	private Optional<Token> token;
	
	PatternNode(Optional<Token> token) {
		this.token = token;
	}
	
	@Override
	public String toString() {
		return "[Pattern Node: " + token.toString() + "]";
	}

}
