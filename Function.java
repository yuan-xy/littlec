
public class Function {
	private String name;
	private String returnType;
	private int location;
	
	public Function(String name,String returnType,int location){
		this.name=name;
		this.returnType=returnType;
		this.location=location;
	}
	
	public int getLocation() {
		return location;
	}
	public void setLocation(int location) {
		this.location = location;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getReturnType() {
		return returnType;
	}
	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}
	
	
}
