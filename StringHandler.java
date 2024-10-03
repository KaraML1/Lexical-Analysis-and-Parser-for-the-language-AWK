
public class StringHandler {
	
	private String data;
	private int index = 0;
	
	StringHandler(String doc) {
		this.data = doc;
	}
	
	// Peeks i characters ahead
	char Peek(int i) {
		return data.charAt(index + i);	
	}
	
	// Peeks i characters ahead
	String PeekString(int i) {
		return data.substring(index, index + i);
	}
	
	// Returns Character at index
	char GetChar() {
		var ch = data.charAt(index);
		index++;
		return ch;
	}
	
	// Goes forward i values
	void Swallow(int i) {
		index += i;
	}
	
	boolean IsDone() {
		// Data length 
		return index > data.length() - 1; // Return True if the pointer is past the last value
	}
	
	String Remainder() { // Return the rest of the data excluding what has already been processed
		return data.substring(index);
	}
	

}
