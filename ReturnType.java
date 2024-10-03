
public class ReturnType {
	
	Types type;
	String data;
	
	
	enum Types {
		Normal, Break, Continue, Return
	}
	
	ReturnType(Types type) {
		this.type = type;
	}
	
	ReturnType(Types type, String data) {
		this.type = type;
		this.data = data;
	}
	
	public String getData() {
		if (data.equals(null)) {
			return null;
		} else return this.data;
	}
	
	@Override
	public String toString() {
		if (data != null) return data;
		else return this.type.toString();
	}

	public ReturnType.Types getType() {
		return this.type;
	}
}
