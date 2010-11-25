import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FunctionRuntime {

	private Function function;
	private Object returnValue;
	private Map<String, Variable> local=new HashMap<String, Variable>();
	private List<Variable> arguments=new ArrayList<Variable>();
	
	public FunctionRuntime(Function function){
		this.function=function;
	}
	
	public void addLocalVar(Variable var){
		local.put(var.getName(), var);
	}
	
	public Function getFunction() {
		return function;
	}
	public void setFunction(Function function) {
		this.function = function;
	}
	public Map<String, Variable> getLocal() {
		return local;
	}
	public void setLocal(Map<String, Variable> local) {
		this.local = local;
	}
	public Object getReturnValue() {
		return returnValue;
	}
	public void setReturnValue(Object returnValue) {
		this.returnValue = returnValue;
	}

	public List<Variable> getArguments() {
		return arguments;
	}

	public void setArguments(List<Variable> arguments) {
		this.arguments = arguments;
	}
	
	
}
