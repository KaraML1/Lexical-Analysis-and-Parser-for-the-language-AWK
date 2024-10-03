import java.util.HashMap;
import java.util.LinkedList;
import java.util.Optional;
import java.util.function.Function;

public class BuiltInFunctionDefinitionNode extends FunctionDefinitionNode {

	private boolean variadic = false; // accept any number of parameters
	public Function<HashMap<String,InterpreterDataType>, String> Execute; // Takes a Hashmap and returns a String

	 /* Accept a HM<String, IDT>, return string
	 * HM is the params to the function
	 * String is the return value
	 */
	BuiltInFunctionDefinitionNode(FunctionDefinitionNode node, boolean variadic) {
		super(node.functionName, node.paramName, node.block);
        this.variadic = variadic;
	}
	
    public boolean isVariadic() {
        return variadic;
    }

    public String Execute(HashMap<String, InterpreterDataType> parameters) {
    	
//    	this.paramName.forEach(param -> {
//    		parameters.put(String.valueOf(parameters.size() + 1), new InterpreterDataType(param.get().toString()));
//    	}); // Fill in function parameters based off (n, value);
        return Execute.apply(parameters);
    }
}
