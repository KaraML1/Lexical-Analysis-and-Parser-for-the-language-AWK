import java.util.HashMap;
import java.util.LinkedList;

public class Lexer {
	
	LinkedList<Token> Tokens = new LinkedList<Token>();
	private StringHandler strHlr;
	private int lineNumber = 0;
	private int linePosition = 0;
	
	Lexer (String data) throws Exception {
		strHlr = new StringHandler(data);
		createKeywords();
		Lex();
	}
	
	HashMap<String, Token.TokenTypes> keywords = new HashMap<>();
	HashMap<String, Token.TokenTypes> twoCharSym = new HashMap<>();
	HashMap<String, Token.TokenTypes> oneCharSym = new HashMap<>();

	
	private void createKeywords() {
		String[] keywords = {"WHILE", "IF", "DO", "FOR", "BREAK", "CONTINUE", "ELSE",
				"RETURN", "BEGIN", "END", "PRINT", "PRINTF", "NEXT", "IN", "DELETE",
				"GETLINE", "EXIT", "NEXTFILE", "FUNCTION"};
			
		for (int i = 0; i < keywords.length; i++) {
			this.keywords.put(keywords[i], Token.TokenTypes.valueOf(keywords[i]));
		}
		
	    twoCharSym.put(">=", Token.TokenTypes.GREATEQUAL);
	    twoCharSym.put("++", Token.TokenTypes.PLUSPLUS);
	    twoCharSym.put("--", Token.TokenTypes.MINUSMINUS);
	    twoCharSym.put("<=", Token.TokenTypes.LESSEQUAL);
	    twoCharSym.put("==", Token.TokenTypes.EQUALEQUAL);
	    twoCharSym.put("!=", Token.TokenTypes.NOTEQUAL);
	    twoCharSym.put("^=", Token.TokenTypes.EXPOEQUAL);
	    twoCharSym.put("%=", Token.TokenTypes.MODEQUAL);
	    twoCharSym.put("*=", Token.TokenTypes.MULTIPLYEQUAL);
	    twoCharSym.put("/=", Token.TokenTypes.DIVIDEEQUAL);
	    twoCharSym.put("+=", Token.TokenTypes.PLUSEQUAL);
	    twoCharSym.put("-=", Token.TokenTypes.MINUSEQUAL);
	    twoCharSym.put("!~", Token.TokenTypes.DOESNOTMATCH);
	    twoCharSym.put("&&", Token.TokenTypes.ANDAND);
	    twoCharSym.put(">>", Token.TokenTypes.APPEND);
	    twoCharSym.put("||", Token.TokenTypes.OROR);
	    
	    oneCharSym.put("{", Token.TokenTypes.OPENBRACKET);
	    oneCharSym.put("}", Token.TokenTypes.CLOSEBRACKET);
	    oneCharSym.put("[", Token.TokenTypes.OPENSQPAREN);
	    oneCharSym.put("]", Token.TokenTypes.CLOSESQPAREN);
	    oneCharSym.put("(", Token.TokenTypes.OPENPAREN);
	    oneCharSym.put(")", Token.TokenTypes.CLOSEPAREN);
	    oneCharSym.put("$", Token.TokenTypes.DOLLAR);
	    oneCharSym.put("~", Token.TokenTypes.TILDE);
	    oneCharSym.put("=", Token.TokenTypes.EQUAL);
	    oneCharSym.put("<", Token.TokenTypes.LESS);
	    oneCharSym.put(">", Token.TokenTypes.GREATER);
	    oneCharSym.put("!", Token.TokenTypes.EXCLAMATION);
	    oneCharSym.put("+", Token.TokenTypes.PLUS);
	    oneCharSym.put("^", Token.TokenTypes.CARET);
	    oneCharSym.put("-", Token.TokenTypes.MINUS);
	    oneCharSym.put("?", Token.TokenTypes.QUESTION);
	    oneCharSym.put(":", Token.TokenTypes.COLON);
	    oneCharSym.put("*", Token.TokenTypes.STAR);
	    oneCharSym.put("/", Token.TokenTypes.SLASH);
	    oneCharSym.put("%", Token.TokenTypes.PERCENT);
	    oneCharSym.put(";", Token.TokenTypes.SEPERATOR);
	    oneCharSym.put("\n", Token.TokenTypes.SEPERATOR);
	    oneCharSym.put("|", Token.TokenTypes.VERTICALBAR);
	    oneCharSym.put(",", Token.TokenTypes.COMMA);
	}

	private void Lex() throws Exception {
		while (strHlr.IsDone() != true) { // Repeat until reach the end of data
			String ch = strHlr.PeekString(1);
			switch (ch) {
				case ("\s"): // Skip if it is a space or tab
				case ("\t"):
					linePosition++;
					strHlr.Swallow(1);
					break;
				case ("\n"): // Test if it is a new line
					Tokens.add(new Token(Token.TokenTypes.SEPERATOR, lineNumber, linePosition));
					linePosition = 0;
					lineNumber++;
					strHlr.Swallow(1);
					break;
				case ("\r"): // Skip if it is a carriage return
					linePosition++;
					strHlr.Swallow(1);
					break;
				case ("#"): // Skip the rest of the line if its a comment
					while (!strHlr.PeekString(1).equals("\n") && !strHlr.IsDone()) {
						linePosition++;
						strHlr.Swallow(1);
					}
					break;
				case ("\""): // If quotations, create String Literal
					linePosition++;
					strHlr.Swallow(1);
					Tokens.add(HandleStringLiteral());
					break;
				case ("`"): // if backtick, create a Pattern
					linePosition++;
					strHlr.Swallow(1);
					Tokens.add(HandlePattern());
					break;
				default: // Else test if it is a letter and number
					char test = ch.charAt(0);
					if (isLetter(test)) {  // If the character is a letter, process word
						Tokens.add(ProcessWord());
						break;
					}
					else if (isNumber(test)) { // If the character is a number, process number
						Tokens.add(ProcessNumber());
						break;
					} else {
						var token = ProcessSymbol();
						if (token.equals(null)) {
							throw new IllegalArgumentException("Character Not Recognized [ " + ch + " ] Line Number: " + lineNumber + ", at Position " + linePosition);
						}
						Tokens.add(token);
					} 
			}
		}
	}
	
	// Only accepts Letters, Underscores, Dashes, and Numbers
	private Token ProcessWord() {
        StringBuffer newWord = new StringBuffer(new String());
		// Check if done, a letter, underscore, dash, or number
		while (strHlr.IsDone() == false && (isLetter(strHlr.Peek(0)) || (strHlr.Peek(0) == '_') || (strHlr.Peek(0) == '-') || (isNumber(strHlr.Peek(0))))) {
			newWord.append(strHlr.GetChar());
			linePosition++;
		}
		
		if (keywords.containsKey(newWord.toString().toUpperCase())) { // Make a keyword Token if not a word
			return new Token(Token.TokenTypes.valueOf(newWord.toString().toUpperCase()), lineNumber, linePosition);
		}
		return new Token(Token.TokenTypes.WORD, lineNumber, linePosition, newWord.toString());
	}
	
	// Only accepts Numbers and dots
	private Token ProcessNumber() throws Exception {
        StringBuffer newWord = new StringBuffer(new String()); 
        boolean foundPoint = false;
        // Check if done, is a number, or a dot
		while (strHlr.IsDone() == false && (isNumber(strHlr.Peek(0)) || (strHlr.Peek(0) == '.'))) {
			linePosition++;
			char ch = strHlr.GetChar();
			newWord.append(ch); // Add new characters to the string as it gets verified
			if (ch == '.' && foundPoint == true) { // To make sure that proper formatting is followed
				throw new IllegalArgumentException("Character Not Allowed [ " + ch + " ] Line Number: " + lineNumber + ", at Position " + linePosition);
			} else if (ch == '.') { 
				foundPoint = true;
			}
		}
		// Add new Token value to Linked List
		return new Token(Token.TokenTypes.NUMBER, lineNumber, linePosition, newWord.toString());
	}
	
	private Token HandleStringLiteral() {
        StringBuffer newWord = new StringBuffer(new String(""));
		char ch;
		while (strHlr.PeekString(1).equals("\"") == false) { // Keep going until encounter end "
			ch = strHlr.GetChar();
			linePosition++;
			if (ch == '\\') {
				if (strHlr.Peek(0) == ('\"')) {
					ch = strHlr.GetChar();
				}
			}
			newWord.append(ch);
		}
		// Since end " was found, swallow it so no loop
		strHlr.Swallow(1);
		return new Token(Token.TokenTypes.STRINGLITERAL, lineNumber, linePosition, newWord.toString());
	}
	
	private Token HandlePattern() {
        StringBuffer newWord = new StringBuffer(new String(""));
		char ch;
		while (strHlr.PeekString(1).equals("`") == false) { // Keep going until encounter end "
			ch = strHlr.GetChar();
			linePosition++;
			newWord.append(ch);
		}
		// Since end ` was found, swallow it so no loop
		strHlr.Swallow(1);
		return new Token(Token.TokenTypes.PATTERN, lineNumber, linePosition, newWord.toString());
	}
	
	private Token ProcessSymbol() {
		String ch = strHlr.PeekString(2); 
		if (twoCharSym.containsKey(ch)) {
			linePosition+=2;
			strHlr.Swallow(2);
			return new Token(twoCharSym.get(ch), lineNumber, linePosition);
		}
		ch = strHlr.PeekString(1);
		if (oneCharSym.containsKey(ch)) {
			linePosition+=1;
			strHlr.Swallow(1);
			return new Token(oneCharSym.get(ch), lineNumber, linePosition);
		} 
		return null;
	}
	
	// Uses ASCII values to determine value
	private boolean isLetter(char data) {
        if (((data >= 'A' && data <= 'Z')) || ((data >= 'a' && data <= 'z'))) {
           return true;
        }
		return false;
	}
	private boolean isNumber(char data) {
        if (((data >= '0' && data <= '9')) || (data == '.')) {
            return true;
        }
		return false;	
	}
}
