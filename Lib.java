import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;


public class Lib {
	
	private PrintStream out=System.out;
	private InputStream in=System.in;
	
	private static String[] functionNames={"printf","getchar"};
	
	public static boolean isLibFunction(String name){
		for(String s : functionNames){
			if(s.equals(name)) return true;
		}
		return false;
	}

	public Object call(FunctionRuntime fr) {
		if(fr.getFunction().getName().equals("printf")){
			List<Variable> list=fr.getArguments();
			List<Object> args=new ArrayList<Object>();
			for(int i=1;i<list.size();i++){
				args.add(list.get(i).getValue());
			}
			out.printf(list.get(0).getValue().toString(),args.toArray());
			fr.setReturnValue(0);
		}if(fr.getFunction().getName().equals("getchar")){
			try {
				fr.setReturnValue(in.read());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return fr.getReturnValue();
	}

	public InputStream getIn() {
		return in;
	}

	public void setIn(InputStream in) {
		this.in = in;
	}

	public PrintStream getOut() {
		return out;
	}

	public void setOut(PrintStream out) {
		this.out = out;
	}
	
}
