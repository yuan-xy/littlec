import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Map;

import junit.framework.TestCase;


public class InterpreterTest extends TestCase {

	public void testPrescan1(){
		Interpreter itp=new Interpreter("int a,b;char c;{int d;}");
		itp.prescan();
		Map<String, Variable> global=itp.getParser().getGlobal();
		assertEquals(3, global.size());
		assertEquals("int", global.get("a").getType());
		assertEquals("int", global.get("b").getType());
		assertEquals("char", global.get("c").getType());
	}
	public void testPrescan2(){
		Interpreter itp=new Interpreter("int max(int a,int b);");
		itp.prescan();
		Map<String, Function> fTable=itp.getFunctionTable();
		assertEquals(1, fTable.size());
		Function func=fTable.get("max");
		assertEquals("int", func.getReturnType());
		Lexer lexer=itp.getLexer();
		lexer.setIndex(func.getLocation());
		lexer.nextToken();
		assertEquals("int", lexer.getTokenValue());
	}
	
	public void testRun(){
		Interpreter itp=new Interpreter("int i;");
		try{
			itp.run();
			fail();
		}catch(RuntimeException e){
			assertEquals(e.getMessage(), ParseError.FUNC_UNDEF.toString());
		}
	}
	
	public void testRun2(){
		Interpreter itp=new Interpreter("int main(){return 1+3;}");
		itp.run();
		assertEquals(4, itp.getReturnValue());
	}
	
	public void testRun3(){
		Interpreter itp=new Interpreter("int main(){return sum(1,2);} int sum(int a,int b){return a+b;}");
		itp.run();
		assertEquals(3, itp.getReturnValue());
	}
	
	public void testRun4(){
		Interpreter itp=new Interpreter("int main(){return sum(1);} int sum(int a,int b){return a+b;}");
		try{
			itp.run();
			fail();
		}catch(RuntimeException e){
			assertEquals(ParseError.PARAM_NOT_EQUAL.toString(), e.getMessage());
		}

	}
	public void testRun5(){
		Interpreter itp=new Interpreter("int main(){int a=1,b=2;return a+b;}");
		itp.run();
		assertEquals(3, itp.getReturnValue());
	}
	public void testRun6(){
		Interpreter itp=new Interpreter("int main(){int b=2;return a+b;}int a=1;");
		itp.run();
		assertEquals(3, itp.getReturnValue());
	}
	public void testRun7(){
		Interpreter itp=new Interpreter("int main(){int a=1,b=2;return sum(a,a+b);} int sum(int a,int b){return a+b;}");
		itp.run();
		assertEquals(4, itp.getReturnValue());
		itp=new Interpreter("int main(){return three();} int three(){return 3;}");
		itp.run();
		assertEquals(3, itp.getReturnValue());
	}
	public void testRun8(){
		Interpreter itp=new Interpreter("int main(){if(0){return 1;}else{return 2;}}");
		itp.run();
		assertEquals(2, itp.getReturnValue());
	}
	public void testRun9(){
		Interpreter itp=new Interpreter("int a;int main(){if(2>1){a=3;}return a;}");
		itp.run();
		assertEquals(3, itp.getReturnValue());
	}
	public void testRun10(){
		Interpreter itp=new Interpreter("int a=1;int main(){int i=3; while(i>0){a=a*i;i=i-1;} return a;}");
		itp.run();
		assertEquals(6, itp.getReturnValue());
	}
	public void testRun11(){
		Interpreter itp=new Interpreter("int a=1;int main(){int i=3; do{a=a*i;}while(--i>0); return a;}");
		itp.run();
		assertEquals(6, itp.getReturnValue());
	}
	public void testRun12(){
		Interpreter itp=new Interpreter("int a=1;int main(){int i;for(i=3;i>0;i--){a=a*i;} return a;}");
		itp.run();
		assertEquals(6, itp.getReturnValue());
	}
	public void testRun13(){
		ByteArrayOutputStream bout=new ByteArrayOutputStream();
		PrintStream out=new PrintStream(bout);
		Interpreter itp=new Interpreter("int a=1234;int main(){printf(\"%5d\n\",a);return a;}");
		itp.getLib().setOut(out);
		itp.run();
		assertEquals(1234, itp.getReturnValue());
		assertEquals(" 1234\n", bout.toString());
	}
	public void testRun14(){
		byte buf[]={(byte)64};
		ByteArrayInputStream in=new ByteArrayInputStream(buf);
		Interpreter itp=new Interpreter("int main(){int a=getchar();return a;}");
		itp.getLib().setIn(in);
		itp.run();
		assertEquals(64, itp.getReturnValue());
	}
	
}
