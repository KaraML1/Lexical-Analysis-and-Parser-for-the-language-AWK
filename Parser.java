import java.util.LinkedList;
import java.util.Optional;

public class Parser {
	
	private TokenManager Manager;
	
	Parser(LinkedList<Token> Tokens) {
		this.Manager = new TokenManager(Tokens);
	}
	
	private boolean ParseFunction(ProgramNode programNode) throws Exception {
		if (Manager.MatchAndRemove(Token.TokenTypes.FUNCTION).isPresent()) { // If Present, a FUNCTION token has been popped and returned
			Optional<Token> name = Manager.MatchAndRemove(Token.TokenTypes.WORD); // Expect a function name
			if (name.isEmpty()) throw new Exception("Expected a WORD");
			LinkedList<Optional<Token>> params = new LinkedList<Optional<Token>>();
			if (Manager.MatchAndRemove(Token.TokenTypes.OPENPAREN).isEmpty()) throw new Exception("Expected a OPEN PAREN"); // Skip the expected open paren
			while (Manager.MatchAndRemove(Token.TokenTypes.CLOSEPAREN).isEmpty()) { // Keep going until expected Close Paren
				/* Keep track of the expected parameters. There can be 0, 1, or many.
				 *  Done to make sure that Optional.empty is not added to paramList
				 *  since the Lexer recognizes numbers and strings as different (NUMBER and WORD)
				 */		
				var param = Manager.MatchAndRemove(Token.TokenTypes.WORD);
				if (param.isEmpty()) Manager.MatchAndRemove(Token.TokenTypes.NUMBER);
				if (param.isEmpty()) throw new Exception("Expected a NUMBER or WORD Parameter");
				params.add(param);
				
				AcceptSeperators();
				if (Manager.MatchAndRemove(Token.TokenTypes.COMMA).isPresent()) {
					AcceptSeperators();
				}
			}
			programNode.functionNodes.add(new FunctionDefinitionNode(name.get(), params, ParseBlock()));
			AcceptSeperators();
			return true;
		} else return false; // Is not a Function Token
	}
	
	private boolean ParseAction(ProgramNode programNode) throws Exception {
		if (Manager.MoreTokens()) {
			if (Manager.MatchAndRemove(Token.TokenTypes.BEGIN).isPresent()) {
				programNode.beginNodes.add(ParseBlock());
				return true;
			} else if (Manager.MatchAndRemove(Token.TokenTypes.END).isPresent()) {
				programNode.endNodes.add(ParseBlock());
				return true;
			} else {
				programNode.otherNodes.add(ParseBlock());
				return true;
			}
		}
		return false; // No more Tokens so end loop
	}
	
	private boolean AcceptSeperators() {
		boolean flag = false;
		while (Manager.MoreTokens() && Manager.MatchAndRemove(Token.TokenTypes.SEPERATOR).isPresent()) {
			flag = true;
		}
		return flag;
	}
	
	private BlockNode ParseBlock() throws Exception { // Accepts either a single statement or multiple statements
		LinkedList<StatementNode> statementNodes = new LinkedList<StatementNode>();
		var condition = ParseOperation();
		if (Manager.MatchAndRemove(Token.TokenTypes.OPENBRACKET).isPresent()) { // Expect multiple statements
			while (Manager.MatchAndRemove(Token.TokenTypes.CLOSEBRACKET).isEmpty()) {
				AcceptSeperators();
				var node = ParseStatement();
				if (node.isPresent()) {
					statementNodes.add(node.get());
					AcceptSeperators();
				} else break;
			}
		} else {
			AcceptSeperators();
			statementNodes.add(ParseStatement().get()); // Single statement
		} 
		AcceptSeperators();
		return new BlockNode(condition, statementNodes);
	}
	
	private Optional<StatementNode> ParseStatement() throws Exception {
		Optional<StatementNode> node = ParseContinueBreak();
		if (node.isPresent()) return node;
		node = ParseIf();
		if (node.isPresent()) return node;
		node = ParseFor();
		if (node.isPresent()) return node;
		node = ParseWhile();
		if (node.isPresent()) return node;
		node = ParseDoWhile();
		if (node.isPresent()) return node;
		node = ParseDelete();
		if (node.isPresent()) return node;
		node = ParseReturn();
		if (node.isPresent()) return node;
		var temp = ParseAssignment(); // Statements could be AssignmentNodes, PostDec, PreDec, PostInc, PreInc, and FunctionCallNodes
		if (temp.isPresent()) {
			if (temp.get() instanceof StatementNode) {
				return Optional.of((StatementNode) temp.get());
			}
		}
		return Optional.empty();
	}
	
	private Optional<StatementNode> ParseContinueBreak() throws Exception { // Simple StatementNode with no body
		if (Manager.MatchAndRemove(Token.TokenTypes.CONTINUE).isPresent()) {
			return Optional.of(new ContinueNode());
		} else if (Manager.MatchAndRemove(Token.TokenTypes.BREAK).isPresent()) {
			return Optional.of(new BreakNode());
		}
		return Optional.empty(); // Not a ContinueBreak Statement
	}
	
	private Optional<StatementNode> ParseIf() throws Exception { // Custom Linked List implementation. Each If / Else If / Else is a chain in that list
		if (Manager.MatchAndRemove(Token.TokenTypes.IF).isPresent()) {// Is an If Statement
			boolean type = true;
			Optional<Node> operation = ParseOperation();
			var statements = ParseBlock();
			var ifNode = new IfNode();
			ifNode.add(statements, operation, type);
			while (true) {
				if (Manager.MatchAndRemove(Token.TokenTypes.ELSE).isPresent()) {
					if (Manager.MatchAndRemove(Token.TokenTypes.IF).isPresent()) {// Else If Statement 
						type = true;
					} else type = false; // Else Statement so change flag appropriately
					operation = ParseOperation();
					statements = ParseBlock();
					ifNode.add(statements, operation, type);
				} else break; // No more statements
			}
			return Optional.of(ifNode); // Return complete IfNode
		}
		return Optional.empty(); // Not a If Statement
	}
	
	private Optional<StatementNode> ParseFor() throws Exception {
		if (Manager.MatchAndRemove(Token.TokenTypes.FOR).isPresent()) {
			if (Manager.MatchAndRemove(Token.TokenTypes.OPENPAREN).isPresent()) {
				if (Manager.Peek(1).get().getTokenType().equals(Token.TokenTypes.IN)) { // A foreach loop
					return Optional.of(new ForEachNode(ParseOperation()));
				} else { // If not a foreach loop, must be a regular for(initialization;condition;afterExecution) loop
					Optional<Node> initialization = ParseOperation();
					if (Manager.MatchAndRemove(Token.TokenTypes.SEPERATOR).isEmpty()) throw new Exception("ParseFor: For Loop expected a seperator between arguments");
					Optional<Node> condition = ParseOperation();
					if (Manager.MatchAndRemove(Token.TokenTypes.SEPERATOR).isEmpty()) throw new Exception("ParseFor: For Loop expected a seperator between arguments");
					Optional<Node> afterExecution = ParseOperation();
					if (Manager.MatchAndRemove(Token.TokenTypes.CLOSEPAREN).isEmpty()) throw new Exception("ParseFor: For Loop expected a seperator between arguments");
					return Optional.of(new ForNode(initialization, condition.get(), afterExecution, ParseBlock()));
				}
			} else throw new Exception("PARSEFOR: Expected an OPENPAREN");
		}
		return Optional.empty();
	}
	
	private Optional<StatementNode> ParseWhile() throws Exception {
		if (Manager.MatchAndRemove(Token.TokenTypes.WHILE).isPresent()) {
			if (Manager.MatchAndRemove(Token.TokenTypes.OPENPAREN).isPresent()) {
				var whileOperation = ParseOperation(); // While Condition
				if (Manager.MatchAndRemove(Token.TokenTypes.CLOSEPAREN).isEmpty()) throw new Exception("ParseWhile: Expected a CLOSEPAREN");
				Optional<StatementNode> whileNode = Optional.of(new WhileNode(whileOperation.get(), ParseBlock())); // Has a condition and a Body Block
				return whileNode;
			} else throw new Exception("ParseWhile: Expected an OPENPAREN");
		}
		return Optional.empty();
	}
	
	private Optional<StatementNode> ParseDoWhile() throws Exception { // Similar to While Loop
		if (Manager.MatchAndRemove(Token.TokenTypes.DO).isPresent()) {
			var statements = ParseBlock();
			if (Manager.MatchAndRemove(Token.TokenTypes.WHILE).isEmpty()) throw new Exception("ParseDoWhile: Expected a WHILE"); 
			Optional<StatementNode> node = Optional.of(new DoWhileNode(ParseOperation().get(), statements));
				return node;
		}
		return Optional.empty();
	}

	private Optional<StatementNode> ParseDelete() throws Exception {
		if (Manager.MatchAndRemove(Token.TokenTypes.DELETE).isPresent()) {
			var name = ParseOperation();
			if (Manager.MatchAndRemove(Token.TokenTypes.OPENSQPAREN).isEmpty()) return Optional.of(new DeleteNode(name.get(), Optional.empty())); // No Params
			else {
				Optional<LinkedList<Node>> parameters = Optional.of(new LinkedList<Node>()); // Parameters of Function Call
				parameters.get().add(ParseOperation().get());
				while (Manager.MatchAndRemove(Token.TokenTypes.COMMA).isPresent()) {
					parameters.get().add(ParseOperation().get());
				}
				if (Manager.MatchAndRemove(Token.TokenTypes.CLOSESQPAREN).isEmpty()) throw new Exception("ParseDelete: Expected an CLOSESQPAREN");
				return Optional.of(new DeleteNode(name.get(), parameters));
			}
		}
		return Optional.empty();
	}
	
	private Optional<StatementNode> ParseReturn() throws Exception { // Has a operation to return
		if (Manager.MatchAndRemove(Token.TokenTypes.RETURN).isPresent()) {
			return Optional.of(new ReturnNode(ParseOperation().get()));
		}
		return Optional.empty();
	}
	
	private Optional<Node> ParseFunctionCall() throws Exception {
			switch (Manager.Peek(0).get().getTokenType()) { // Check for different function calls
				case WORD: { // If Word, expect a paren
					if (Manager.Peek(1).get().getTokenType().equals(Token.TokenTypes.OPENPAREN)) return ParseFunctionCallHelper(Token.TokenTypes.WORD);
					break;
				}
				case PRINT:{ 
					// Else just expect function without paren
					// Note: It is valid AWK to use one of these functions with paren, even if they don't require it
					return ParseFunctionCallHelper(Token.TokenTypes.PRINT);
				}
				case GETLINE:{
					return ParseFunctionCallHelper(Token.TokenTypes.GETLINE);
				}
				case PRINTF:{
					return ParseFunctionCallHelper(Token.TokenTypes.PRINTF);
				}
				case EXIT:{
					return ParseFunctionCallHelper(Token.TokenTypes.EXIT);
				}
				case NEXTFILE:{
					return ParseFunctionCallHelper(Token.TokenTypes.NEXTFILE);
				}
				case NEXT:{
					return ParseFunctionCallHelper(Token.TokenTypes.NEXT);
				}
			default: // Not one of the main functions
				break;
			}
		return Optional.empty();
	}
	
	/*
	 * Helper method to ParseFunctionCall(). Created to simplify code as this block is used multiple times. 
	 */
	private Optional<Node> ParseFunctionCallHelper(Token.TokenTypes token) throws Exception {
			var name = Manager.MatchAndRemove(token);
			Manager.MatchAndRemove(Token.TokenTypes.OPENPAREN);
			if (Manager.MatchAndRemove(Token.TokenTypes.CLOSEPAREN).isPresent()) {
				if (token.equals(Token.TokenTypes.WORD)) {
					return Optional.of(new FunctionCallNode(new VariableReferenceNode(name.get(), Optional.empty()), Optional.empty())); // Expect no params
				} else return Optional.of(new FunctionCallNode(new AWKCommandNode(name.get()), Optional.empty())); // Expect no params
			}
			
			Optional<LinkedList<Node>> parameters = Optional.of(new LinkedList<Node>()); // Parameters of Function Call
			var param = ParseOperation();
			if (param.isPresent()) parameters.get().add(param.get());
			while (Manager.MatchAndRemove(Token.TokenTypes.COMMA).isPresent()) {
				param = ParseOperation();
				if (param.isPresent()) parameters.get().add(param.get());
			}
			Manager.MatchAndRemove(Token.TokenTypes.CLOSEPAREN);
			if (token.equals(Token.TokenTypes.WORD)) {
				return Optional.of(new FunctionCallNode(new VariableReferenceNode(name.get(), Optional.empty()), parameters)); // Expect no params
			}
			return Optional.of(new FunctionCallNode(new AWKCommandNode(name.get()), parameters));
	}

	public Optional<Node> ParseOperation() throws Exception { // Note: Made public for individualized testing as per instructions		
		var node = ParseAssignment();
		return node;
	}
	
	private Optional<Node> ParseBottomLevel() throws Exception {
		Token.TokenTypes type;
		if (Manager.Peek(0).isPresent()) {
			type = Manager.Peek(0).get().getTokenType();
		} else throw new Exception("ParseBottomLevel: Expected a Token of some kind");
		
		switch (type) {
			case STRINGLITERAL: {
				return Optional.of(new ConstantNode(Manager.MatchAndRemove(type).get()));
			}
			case NUMBER: {
				return Optional.of(new ConstantNode(Manager.MatchAndRemove(type).get()));
			}
			case PATTERN: {
				return Optional.of(new PatternNode(Manager.MatchAndRemove(type)));
			}
			case OPENPAREN: {
				Manager.MatchAndRemove(Token.TokenTypes.OPENPAREN);
				Optional<Node> node = ParseOperation();
				if (Manager.MatchAndRemove(Token.TokenTypes.CLOSEPAREN).isEmpty()) throw new Exception("ParseBottomLevel: Expected a CLOSEPAREN");
				return node;
			}
			case EXCLAMATION: { // NOT Symbol
				Manager.MatchAndRemove(Token.TokenTypes.EXCLAMATION);
				var node = ParseOperation();
				if (node.isEmpty()) throw new Exception("NOT: Expected something here");
				return Optional.of(new OperationNode(node.get(), OperationNode.Operations.NOT)); // Call ParseOperation again to get the rest of the NOT equation
			}
			case MINUS: {
				Manager.MatchAndRemove(Token.TokenTypes.MINUS);
				var node = ParseOperation();
				if (node.isEmpty()) throw new Exception("MINUS: Expected something here");
				return Optional.of(new OperationNode(node.get(), OperationNode.Operations.UNARYNEG));
			}
			case PLUS: {
				Manager.MatchAndRemove(Token.TokenTypes.PLUS);
				var node = ParseOperation();
				if (node.isEmpty()) throw new Exception("PLUS: Expected something here");
				return Optional.of(new OperationNode(node.get(), OperationNode.Operations.UNARYPOS));
			}
			case PLUSPLUS: { // ++lvalue
				Manager.MatchAndRemove(Token.TokenTypes.PLUSPLUS);
				var node = ParseOperation();
				if (node.isEmpty()) throw new Exception("PREINCREMENT: Expected something here");
				return Optional.of(new OperationNode(node.get(), OperationNode.Operations.PREINC));
			}
			case MINUSMINUS: { // --lvalue
				Manager.MatchAndRemove(Token.TokenTypes.MINUSMINUS);
				var node = ParseOperation();
				if (node.isEmpty()) throw new Exception("PREDECREMENT: Expected something here");
				return Optional.of(new OperationNode(node.get(), OperationNode.Operations.PREDEC));
			}
			default: {
				Optional<Node> node = ParseFunctionCall();
				if (node.isPresent()) return node;
				return ParseLValue();
			}
		}
	}
	
	private Optional<Node> ParseLValue() throws Exception {
		if (Manager.MatchAndRemove(Token.TokenTypes.DOLLAR).isPresent()) {
			var node = ParseOperation();
			if (node.isEmpty()) throw new Exception("PARSELVALUE: Expected something here");
			return Optional.of(new OperationNode(node.get(), OperationNode.Operations.DOLLAR));
		} else {
			var wordToken = Manager.MatchAndRemove(Token.TokenTypes.WORD);
			if (wordToken.isEmpty()) return Optional.empty(); // Failed to find anything. Likely BRACKETS are next
			if (Manager.MatchAndRemove(Token.TokenTypes.OPENPAREN).isPresent()) {
				var node = ParseOperation();
				if (node.isEmpty()) throw new Exception("PARSELVALUE: Expected a index value");
				if (Manager.MatchAndRemove(Token.TokenTypes.CLOSEPAREN).isEmpty()) throw new Exception("PARSELVALUE: Expected a CLOSEPAREN");
				return Optional.of(new VariableReferenceNode(wordToken.get(), node));
			} else {
				// Else just Word (and no OPENPAREN)
				return Optional.of(new VariableReferenceNode(wordToken.get(), Optional.empty()));
			}
		}		
	}
	
	private Optional<Node> postIncDecrement() throws Exception {
		var node = ParseBottomLevel();
		if (Manager.MatchAndRemove(Token.TokenTypes.PLUSPLUS).isPresent()) {
			return Optional.of(new OperationNode(node.get(), OperationNode.Operations.POSTINC));
		} else if (Manager.MatchAndRemove(Token.TokenTypes.MINUSMINUS).isPresent()) {
			return Optional.of((StatementNode) new OperationNode(node.get(), OperationNode.Operations.POSTDEC));
		} else return node; // Return the already Parsed BottomLevel
	}
	
	private Optional<Node> Exponentiation() throws Exception { // Right side associativity
		var left = postIncDecrement();
		if (Manager.MatchAndRemove(Token.TokenTypes.CARET).isPresent()) {
			return Optional.of(new OperationNode(left.get(), Exponentiation(), OperationNode.Operations.EXPONENT));
		}
		return left;
	}
	
	private Optional<Node> Factor() throws Exception {
		var left = Exponentiation();
		if (Manager.MatchAndRemove(Token.TokenTypes.OPENPAREN).isPresent()) {
			var expression = Expression();
			if (Manager.MatchAndRemove(Token.TokenTypes.CLOSEPAREN).isEmpty()) throw new Exception("Factor: Expected a CLOSEPAREN");
			return expression;
		}
		return left;
	}
	
	private Optional<Node> Term() throws Exception {
		var left = Factor();
		do {
			var operator = Manager.MatchAndRemove(Token.TokenTypes.STAR);
			if (operator.isEmpty()) operator = Manager.MatchAndRemove(Token.TokenTypes.SLASH);
			if (operator.isEmpty()) operator = Manager.MatchAndRemove(Token.TokenTypes.PERCENT);
			if (operator.isEmpty()) return left;	
			var right = Factor();
			left = Optional.of(new MathOpNode(left, right, operator));
		} while (true);
	}
	
	private Optional<Node> Expression() throws Exception {
		var left = Term();
		do {
			var operator = Manager.MatchAndRemove(Token.TokenTypes.PLUS);
			if (operator.isEmpty()) operator = Manager.MatchAndRemove(Token.TokenTypes.MINUS);
			if (operator.isEmpty()) return left;
			var right = Term();
			left = Optional.of(new MathOpNode(left, right, operator));
		} while (true);
	}
	
	private Optional<Node> Concatenation() throws Exception {
		var left = Expression();
//		var right = Concatenation();
//		if (right.isEmpty()) return Optional.of(new OperationNode(left.get(), right, OperationNode.Operations.CONCATENATION));
		return left;
	}
	
	private Optional<Node> BoolCompare() throws Exception {
		var left = Concatenation();
		if (Manager.MatchAndRemove(Token.TokenTypes.LESS).isPresent()) {
			return Optional.of(new OperationNode(left.get(), Concatenation(), OperationNode.Operations.LT));
		} else if (Manager.MatchAndRemove(Token.TokenTypes.LESSEQUAL).isPresent()) {
			return Optional.of(new OperationNode(left.get(), Concatenation(), OperationNode.Operations.LE));
		} else if (Manager.MatchAndRemove(Token.TokenTypes.NOTEQUAL).isPresent()) {
			return Optional.of(new OperationNode(left.get(), Concatenation(), OperationNode.Operations.NE));
		} else if (Manager.MatchAndRemove(Token.TokenTypes.EQUALEQUAL).isPresent()) {
			return Optional.of(new OperationNode(left.get(), Concatenation(), OperationNode.Operations.EQ));
		} else if (Manager.MatchAndRemove(Token.TokenTypes.GREATER).isPresent()) {
			return Optional.of(new OperationNode(left.get(), Concatenation(), OperationNode.Operations.GT));
		} else if (Manager.MatchAndRemove(Token.TokenTypes.GREATEQUAL).isPresent()) {
			return Optional.of(new OperationNode(left.get(), Concatenation(), OperationNode.Operations.GE));
		}
		return left;
	}
	
	private Optional<Node> Match() throws Exception {
		var left = BoolCompare();
		if (Manager.MatchAndRemove(Token.TokenTypes.TILDE).isPresent()) {
			return Optional.of(new OperationNode(left.get(), BoolCompare(), OperationNode.Operations.MATCH));
		} else if (Manager.MatchAndRemove(Token.TokenTypes.DOESNOTMATCH).isPresent()) {
			return Optional.of(new OperationNode(left.get(), BoolCompare(), OperationNode.Operations.NOTMATCH));
		} 
		return left;
	}
	
	private Optional<Node> ArrMembership() throws Exception {
		var left = Match();
		if (Manager.MatchAndRemove(Token.TokenTypes.IN).isPresent()) {
			var arr = ParseBottomLevel(); // Expect a WORD for the Array Name
			if (arr.isEmpty()) throw new Exception("Array Membership: Expected a WORD here");
			else return Optional.of(new OperationNode(left.get(), arr, OperationNode.Operations.IN));
		}
		return left;
	}
	
	private Optional<Node> ParseAND() throws Exception {
		var left = ArrMembership();
		if (Manager.MatchAndRemove(Token.TokenTypes.ANDAND).isPresent()) {
			return Optional.of(new OperationNode(left.get(), ArrMembership(), OperationNode.Operations.AND));
		}
		return left;
	}
	
	private Optional<Node> ParseOR() throws Exception {
		var left = ParseAND();
		if (Manager.MatchAndRemove(Token.TokenTypes.OROR).isPresent()) {
			return Optional.of(new OperationNode(left.get(), ParseAND(), OperationNode.Operations.OR));
		}
		return left;
	}
	
	private Optional<Node> ParseTernary() throws Exception { // Conditional Expression
		var boolExp = ParseOR();
		if (Manager.MatchAndRemove(Token.TokenTypes.QUESTION).isPresent()) {
			var trueCase = ParseTernary();
			if (Manager.MatchAndRemove(Token.TokenTypes.COLON).isEmpty()) throw new Exception("ParseTernary: Expected a COLON while looking for true and false cases");
			else return Optional.of(new TernaryNode(boolExp.get(), trueCase.get(), ParseTernary().get()));
		}
		return boolExp; 
	}
	
	private Optional<Node> ParseAssignment() throws Exception {
		var left = ParseTernary();
		// To parse an Assignment, lvalue MUST be a variable, array, or $reference.
		// Note: Since this function is Right Associativity, expect that it goes right to left. 
		if (Manager.MatchAndRemove(Token.TokenTypes.EQUAL).isPresent()) {
			return Optional.of(new AssignmentNode(left.get(), ParseAssignment().get()));
		} else if (Manager.MatchAndRemove(Token.TokenTypes.MINUSEQUAL).isPresent()) {
			return Optional.of(new AssignmentNode(left.get(), new OperationNode(left.get(), ParseAssignment(), OperationNode.Operations.SUBTRACT)));
		} else if (Manager.MatchAndRemove(Token.TokenTypes.PLUSEQUAL).isPresent()) {
			return Optional.of(new AssignmentNode(left.get(), new OperationNode(left.get(), ParseAssignment(), OperationNode.Operations.ADD)));
		} else if (Manager.MatchAndRemove(Token.TokenTypes.DIVIDEEQUAL).isPresent()) {
			return Optional.of(new AssignmentNode(left.get(), new OperationNode(left.get(), ParseAssignment(), OperationNode.Operations.DIVIDE)));
		} else if (Manager.MatchAndRemove(Token.TokenTypes.MULTIPLYEQUAL).isPresent()) {
			return Optional.of(new AssignmentNode(left.get(), new OperationNode(left.get(), ParseAssignment(), OperationNode.Operations.MULTIPLY)));
		} else if (Manager.MatchAndRemove(Token.TokenTypes.MODEQUAL).isPresent()) {
			return Optional.of(new AssignmentNode(left.get(), new OperationNode(left.get(), ParseAssignment(), OperationNode.Operations.MODULO)));
		} else if (Manager.MatchAndRemove(Token.TokenTypes.EXPOEQUAL).isPresent()) {
			return Optional.of(new AssignmentNode(left.get(), new OperationNode(left.get(), ParseAssignment(), OperationNode.Operations.EXPONENT)));
		} 
		return left;
	}
	
	Optional<ProgramNode> Parse() throws Exception {
		ProgramNode programNode = new ProgramNode();
		while (Manager.MoreTokens()) {
			if (!ParseFunction(programNode) && !ParseAction(programNode)) throw new Exception("PARSE ERROR");
			AcceptSeperators(); // Deal with leftover seperators.
		}
		return Optional.of(programNode);
	}
}
