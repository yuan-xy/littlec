
import junit.framework.TestCase;


public class ExpressionParserTest extends TestCase {
	
	public void test0(){
		ExpressionParser p=new ExpressionParser(new Lexer(";"));
		assertEquals(0, p.parse());
		p=new ExpressionParser(new Lexer("3"));
		assertEquals(3, p.parse());
	}
	public void test1(){
		ExpressionParser p=new ExpressionParser(new Lexer("a=1"));
		assertEquals(1, p.parse());
		p=new ExpressionParser(new Lexer("a=1+22/2"));
		assertEquals(12, p.parse());
	}

	public void test2(){
		ExpressionParser p=new ExpressionParser(new Lexer("4>3"));
		assertEquals(1, p.parse());
	}
	
	public void test3(){
		ExpressionParser p=new ExpressionParser(new Lexer("1+2 +3"));
		assertEquals(6, p.parse());
		p=new ExpressionParser(new Lexer("1+3-2"));
		assertEquals(2, p.parse());
	}
	public void test4(){
		ExpressionParser p=new ExpressionParser(new Lexer("1+2*3"));
		assertEquals(7, p.parse());
	}
	
	public void test5(){
		ExpressionParser p=new ExpressionParser(new Lexer("a=1;2*3"));
		assertEquals(1, p.parse());
		p.getLexer().nextToken();
		assertEquals(p.getLexer().getTokenValue(), "2");
	}
	
	public void test6(){
		ExpressionParser p=new ExpressionParser(new Lexer("a=-1*3"));
		assertEquals(-3, p.parse());
	}
	
	public void test7(){
		ExpressionParser p=new ExpressionParser(new Lexer("a=-(1+3)*2"));
		assertEquals(-8, p.parse());
	}
	public void test8(){
		ExpressionParser p=new ExpressionParser(new Lexer("a=1+2*3;a+1-2"));
		assertEquals(7, p.parse());
		assertEquals(p.getLexer().getTokenValue(), ";");
		assertEquals(6, p.parse());
		try{
			p.parse();
			fail();
		}catch(RuntimeException e){
			assertEquals(e.getMessage(), ParseError.NO_EXP.toString());
		}
	}
	
	public void test9(){
		try{
			ExpressionParser p=new ExpressionParser(new Lexer("b+1"));
			p.parse();
			fail();
		}catch(RuntimeException e){
			assertEquals(e.getMessage(), ParseError.NOT_VAR.toString());
		}
	}
	
	public void test10(){
		ExpressionParser p=new ExpressionParser(new Lexer("a=1;a++;"));
		assertEquals(1, p.parse());
		assertEquals(1, p.parse());
		assertEquals(2, p.getVar("a").getValue());
	}
	public void test11(){
		ExpressionParser p=new ExpressionParser(new Lexer("a=1;++a"));
		assertEquals(1, p.parse());
		assertEquals(2, p.parse());
		assertEquals(2, p.getVar("a").getValue());
	}
	public void test12(){
		ExpressionParser p=new ExpressionParser(new Lexer("a=\"%10d\n\""));
		assertEquals("%10d\n", p.parse());
	}
	public void test14(){
		ExpressionParser p=new ExpressionParser(new Lexer("a=51;a=a-48;"));
		assertEquals(51, p.parse());
		assertEquals(51, p.getVar("a").getValue());
		assertEquals(3, p.parse());
		assertEquals(3, p.getVar("a").getValue());
	}
}
