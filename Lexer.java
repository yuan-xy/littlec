import java.util.Stack;


public class Lexer {
	
	public static final String[] keywords={"int","char","double","float","for","do","while","if","else","return"};

	private String code;
	private int index;
	private Token token;
	private Stack<Integer> stack=new Stack<Integer>();
	
	public Lexer(String code){
		this.code=code;
	}

	void setToken(Token token) {
		this.token = token;
	}
	public Token getToken() {
		return token;
	}
	
	public String getTokenValue(){
		return token.getValue();
	}
	public TokenType getTokenType(){
		return token.getType();
	}
	public String getCode() {
		return code;
	}
	
	public void nextToken(){
		stack.push(index);
		nextToken0();
	}

	public void nextToken0(){
		if(index>=code.length()){
			token=null;
			return;
		}
		while(Character.isWhitespace(code.charAt(index))){
			if(++index>=code.length()){
				token=null;
				return;
			}
		}
		if('/'==code.charAt(index)&&index+1<code.length()&&'*'==code.charAt(index+1)){
			index+=2;
			while(!"*/".equals(code.substring(index, index+2))){
				index++;
			}
			index+=2;
		}
		while(Character.isWhitespace(code.charAt(index))){
			if(++index>=code.length()){
				token=null;
				return;
			}
		}
		char ch=code.charAt(index);
		if("+-*^/%;(),'".indexOf(ch)!=-1){
			index++;
			token=new Token(ch+"",TokenType.DELIMITER);
			return;
		}
		if('"'==ch){
			int old=index;
			do{
				if(++index>=code.length()) break;
				ch=code.charAt(index);
			}while('"'!=ch);
			index++;
			token=new Token(code.substring(old+1, index-1),TokenType.STRING);
			return;
		}
		if(Character.isDigit(ch)){
			int old=index;
			do{
				if(++index>=code.length()) break;
				ch=code.charAt(index);
			}
			while(Character.isDigit(ch)||'.'==ch);
			token=new Token(code.substring(old, index),TokenType.NUMBER);
			return;
		}
		if(Character.isJavaIdentifierStart(ch)){
			int old=index;
			do{
				if(++index>=code.length()) break;
			}
			while(Character.isJavaIdentifierPart(code.charAt(index)));
			String s=code.substring(old, index);
			for(String key : keywords){
				if(key.equals(s)){
					token=new Token(s,TokenType.KEYWORD);
					return;
				}
			}
			token=new Token(s,TokenType.IDENTIFIER);
			return;
		}
		if("{}".indexOf(ch)!=-1){
			index++;
			token=new Token(ch+"",TokenType.BLOCK);
			return;
		}
		if('!'==ch){
			if('='==code.charAt(index+1)){
				index+=2;
				token=new Token("!=",TokenType.RELATION_OP);
				return;
			}else{
				index++;
				token=new Token("!",TokenType.RELATION_OP);
				return;
			}
		}
		if('>'==ch){
			if('='==code.charAt(index+1)){
				index+=2;
				token=new Token(">=",TokenType.RELATION_OP);
				return;
			}else{
				index++;
				token=new Token(">",TokenType.RELATION_OP);
				return;
			}
		}
		if('<'==ch){
			if('='==code.charAt(index+1)){
				index+=2;
				token=new Token("<=",TokenType.RELATION_OP);
				return;
			}else{
				index++;
				token=new Token("<",TokenType.RELATION_OP);
				return;
			}
		}
		if('='==ch){
			if('='==code.charAt(index+1)){
				index+=2;
				token=new Token("==",TokenType.RELATION_OP);
				return;
			}else{
				index++;
				token=new Token("=",TokenType.DELIMITER);
				return;
			}
		}
		throw new RuntimeException("Î´Öª·ûºÅ:"+ch);
	}

	public void putback() {
		index=stack.pop();
	}

	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	
	public String toString(){
		return getTokenValue()+" : "+index;
	}
	
}
