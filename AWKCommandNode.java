/*
 * This Class creates a Node using one of the Token types created for the
 * built-in AWK command functions, such as getline, print, printf, exit, nextfile, next 
 * This file was created to simplify the Parser.ParseFunctionCallHelper
 */
public class AWKCommandNode extends Node {
	private Token type;
	
	AWKCommandNode (Token type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return this.type.toString();
	}

	public String getName() {
		return type.toString();
	}
	
	public Token getToken() {
		return this.type;
	}
	
}
