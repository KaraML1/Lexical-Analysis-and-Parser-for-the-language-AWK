import java.util.LinkedList;
import java.util.Optional;

public class TokenManager {
	LinkedList<Token> Tokens;
	TokenManager(LinkedList<Token> Tokens) {
		this.Tokens = Tokens;
	}
	
	/*
	 * Peeks "j" tokens ahead and returns the token if we aren't at the end of the list
	 */
	Optional<Token> Peek(int j) {
		if (j < Tokens.size() && MoreTokens()) {
			return Optional.of(Tokens.get(j));
		} else return Optional.empty();
	}
	
	// Return False if List is empty, else True
	boolean MoreTokens() {
		return !Tokens.isEmpty();
	}
	
	/*
	 * Look at head of List, if head is same type as t pop and then return that token else return Optional.empty
	 */
	Optional<Token> MatchAndRemove(Token.TokenTypes t) {
		if (MoreTokens() && Tokens.peek().getTokenType().equals(t)) return Optional.of(Tokens.pop());
		else return Optional.empty();	
	}
}
