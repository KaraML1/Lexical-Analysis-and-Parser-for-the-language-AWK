
public class Token {
	
	private int lineNumber = -1; // The Column Position
	private int charPos = -1; // The Row Position
	private TokenTypes type;
	private String value = null;
	
	Token (TokenTypes Type, int LineNumber, int Position) {
		this.type = Type;
		this.lineNumber = LineNumber;
		this.charPos = Position;
	}
	
	Token (TokenTypes Type, int LineNumber, int Position, String Value) {
		this.type = Type;
		this.lineNumber = LineNumber;
		this.charPos = Position;
		this.value = Value;
	}
	
	enum TokenTypes {
		WORD, NUMBER, SEPERATOR, WHILE, IF, DO, FOR, BREAK, CONTINUE,
		ELSE, RETURN, BEGIN, END, PRINT, PRINTF, NEXT, IN, DELETE, 
		GETLINE, EXIT, NEXTFILE, FUNCTION, STRINGLITERAL, PATTERN,
		CLOSEPAREN, OPENPAREN, CLOSESQPAREN, OPENSQPAREN, OPENBRACKET, 
		CLOSEBRACKET, DOLLAR, TILDE, EQUAL, GREATER, LESS, EXCLAMATION,
		PLUS, CARET, MINUS, QUESTION, COLON, STAR,
		SLASH, PERCENT,VERTICALBAR, COMMA, GREATEQUAL, PLUSPLUS, MINUSMINUS, LESSEQUAL,
		NOTEQUAL, EQUALEQUAL, EXPOEQUAL, OROR, APPEND, ANDAND, DOESNOTMATCH,
		MINUSEQUAL, PLUSEQUAL, DIVIDEEQUAL, MULTIPLYEQUAL, MODEQUAL;
	}
	
	public TokenTypes getTokenType() {
		return this.type;
	}
	
	@Override
	public String toString() {
		if (value != null) return (value);
		else return (type.toString());
	}
}
