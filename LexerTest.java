import junit.framework.TestCase;


public class LexerTest extends TestCase {

	public void test0(){
		Lexer lexer=new Lexer(" ");
		lexer.nextToken();
		assertNull(lexer.getToken());
	}
	
	public void test1(){
		Lexer lexer=new Lexer("abc");
		lexer.nextToken();
		assertEquals(lexer.getTokenValue(), "abc");
		assertEquals(lexer.getTokenType(), TokenType.IDENTIFIER);
	}
	
	public void test2(){
		Lexer lexer=new Lexer("a+3.45  -b");
		
		lexer.nextToken();
		assertEquals(lexer.getTokenValue(), "a");
		assertEquals(lexer.getTokenType(), TokenType.IDENTIFIER);
		lexer.nextToken();
		assertEquals(lexer.getTokenValue(), "+");
		assertEquals(lexer.getTokenType(), TokenType.DELIMITER);
		lexer.nextToken();
		assertEquals(lexer.getTokenValue(), "3.45");
		assertEquals(lexer.getTokenType(), TokenType.NUMBER);
		lexer.nextToken();
		assertEquals(lexer.getTokenValue(), "-");
		assertEquals(lexer.getTokenType(), TokenType.DELIMITER);
		lexer.nextToken();
		assertEquals(lexer.getTokenValue(), "b");
		assertEquals(lexer.getTokenType(), TokenType.IDENTIFIER);
	}
	
	public void test3(){
		Lexer lexer=new Lexer("int index;");
		
		lexer.nextToken();
		assertEquals(lexer.getTokenValue(), "int");
		assertEquals(lexer.getTokenType(), TokenType.KEYWORD);
		lexer.nextToken();
		assertEquals(lexer.getTokenValue(), "index");
		assertEquals(lexer.getTokenType(), TokenType.IDENTIFIER);
		lexer.nextToken();
		assertEquals(lexer.getTokenValue(), ";");
		assertEquals(lexer.getTokenType(), TokenType.DELIMITER);
	}
	
	public void test4(){
		Lexer lexer=new Lexer("printf(\"%10d\n\",a)");
		lexer.nextToken();
		assertEquals(lexer.getTokenValue(), "printf");
		assertEquals(lexer.getTokenType(), TokenType.IDENTIFIER);
		lexer.nextToken();
		assertEquals(lexer.getTokenValue(), "(");
		assertEquals(lexer.getTokenType(), TokenType.DELIMITER);
		lexer.nextToken();
		assertEquals(lexer.getTokenValue(), "%10d\n");
		assertEquals(lexer.getTokenType(), TokenType.STRING);
		lexer.putback();
		lexer.nextToken();
		assertEquals(lexer.getTokenValue(), "%10d\n");
		lexer.nextToken();
		assertEquals(lexer.getTokenValue(), ",");
		assertEquals(lexer.getTokenType(), TokenType.DELIMITER);
		lexer.nextToken();
		assertEquals(lexer.getTokenValue(), "a");
		assertEquals(lexer.getTokenType(), TokenType.IDENTIFIER);
		lexer.nextToken();
		assertEquals(lexer.getTokenValue(), ")");
		assertEquals(lexer.getTokenType(), TokenType.DELIMITER);
	}
	
	public void test5(){
		Lexer lexer=new Lexer("/*this is a comment.*/ abc");
		
		lexer.nextToken();
		assertEquals(lexer.getTokenValue(), "abc");
		assertEquals(lexer.getTokenType(), TokenType.IDENTIFIER);
	}
	
	public void test6(){
		Lexer lexer=new Lexer("while(a==b){ a!=b }");
		
		lexer.nextToken();
		assertEquals(lexer.getTokenValue(), "while");
		assertEquals(lexer.getTokenType(), TokenType.KEYWORD);
		lexer.nextToken();
		assertEquals(lexer.getTokenValue(), "(");
		assertEquals(lexer.getTokenType(), TokenType.DELIMITER);
		lexer.nextToken();
		assertEquals(lexer.getTokenValue(), "a");
		assertEquals(lexer.getTokenType(), TokenType.IDENTIFIER);
		lexer.nextToken();
		assertEquals(lexer.getTokenValue(), "==");
		assertEquals(lexer.getTokenType(), TokenType.RELATION_OP);
		lexer.nextToken();
		assertEquals(lexer.getTokenValue(), "b");
		assertEquals(lexer.getTokenType(), TokenType.IDENTIFIER);
		lexer.nextToken();
		assertEquals(lexer.getTokenValue(), ")");
		assertEquals(lexer.getTokenType(), TokenType.DELIMITER);
		lexer.nextToken();
		assertEquals(lexer.getTokenValue(), "{");
		assertEquals(lexer.getTokenType(), TokenType.BLOCK);
		lexer.nextToken();
		assertEquals(lexer.getTokenValue(), "a");
		assertEquals(lexer.getTokenType(), TokenType.IDENTIFIER);
		lexer.nextToken();
		assertEquals(lexer.getTokenValue(), "!=");
		assertEquals(lexer.getTokenType(), TokenType.RELATION_OP);
		lexer.nextToken();
		assertEquals(lexer.getTokenValue(), "b");
		assertEquals(lexer.getTokenType(), TokenType.IDENTIFIER);
		lexer.nextToken();
		assertEquals(lexer.getTokenValue(), "}");
		assertEquals(lexer.getTokenType(), TokenType.BLOCK);
	}
	public void test7(){
		Lexer lexer=new Lexer("a=a-48");
		lexer.nextToken();
		assertEquals(lexer.getTokenValue(), "a");
		lexer.nextToken();
		assertEquals(lexer.getTokenValue(), "=");
		lexer.nextToken();
		assertEquals(lexer.getTokenValue(), "a");
		lexer.nextToken();
		assertEquals(lexer.getTokenValue(), "-");
		lexer.nextToken();
		assertEquals(lexer.getTokenValue(), "48");
		lexer.putback();
		lexer.putback();
		lexer.nextToken();
		assertEquals(lexer.getTokenValue(), "-");
	}
	
}
