import java.util.HashMap;
import java.util.Map;


public class ExpressionParser {

	private Lexer lexer;
	private Map<String, Variable> global=new HashMap<String, Variable>();
	private boolean varNeedDeclare=false;
	private Interpreter interpreter;
	
	public ExpressionParser(Lexer lexer){
		this.lexer=lexer;
	}
	public Object parse(){
		lexer.nextToken();
		if(lexer.getToken()==null) ParseError.NO_EXP.error();
		if(lexer.getTokenValue().equals(";")) return 0;
		return parseAssignment();
	}
	
	private Object parseAssignment(){
		if(lexer.getTokenType()==TokenType.IDENTIFIER){
			Token token=lexer.getToken();
			String var=lexer.getTokenValue();
			lexer.nextToken();
			if("=".equals(lexer.getTokenValue())){
				if(varNeedDeclare){
					Variable v=getVar(var);
					if(v==null) ParseError.NOT_VAR.error();
					lexer.nextToken();
					Object value=parseAssignment();
					v.setValue(value);
					return value;
				}else{
					lexer.nextToken();
					Object value=parseAssignment();
					addGlobalVar(new Variable(var,value,"int"));
					return value;
				}

			}else{
				lexer.putback();
				lexer.setToken(token);
			}
		}
		return parseRelation();
	}
	
	private Object parseRelation(){
		Object value=ParseAddSubtract();
		lexer.nextToken();
		if(lexer.getToken()!=null&&lexer.getTokenType()==TokenType.RELATION_OP){
			int value1=(Integer)value;
			boolean bool=false;
			String s=lexer.getTokenValue();
			lexer.nextToken();
			int value2=(Integer) ParseAddSubtract();
			if(">".equals(s)){
				bool=value1>value2;
			}
			if(">=".equals(s)){
				bool=value1>=value2;
			}
			if("<".equals(s)){
				bool=value1<value2;
			}
			if("<=".equals(s)){
				bool=value1<=value2;
			}
			if("!=".equals(s)){
				bool=value1!=value2;
			}
			if("==".equals(s)){
				bool=value1==value2;
			}
			if(bool) return 1;
			else return 0;
		}else{
			return value;
		}
	}
	
	private Object ParseAddSubtract(){
		Object value=parseMultiDivide();
		int value1=0;
		try{
			value1=(Integer)value;
		}catch(ClassCastException e){
			return value;
		}
		while(true){
			lexer.nextToken();
			if(lexer.getToken()==null) break;
			if(lexer.getTokenValue().equals("+")){
				lexer.nextToken();
				int value2=(Integer) parseMultiDivide();
				value1+=value2;
			}else if(lexer.getTokenValue().equals("-")){
				lexer.nextToken();
				int value2=(Integer) parseMultiDivide();
				value1-=value2;
			}else{
				lexer.putback();
				break;
			}
		}
		return value1;
	}
	
	private Object parseMultiDivide(){
		Object value=parseUnary();
		int value1=0;
		try{
			value1=(Integer)value;
		}catch(ClassCastException e){
			return value;
		}
		while(true){
			lexer.nextToken();
			if(lexer.getToken()==null) break;
			if(lexer.getTokenValue().equals("*")){
				lexer.nextToken();
				int value2=(Integer)parseUnary();
				value1*=value2;
			}else if(lexer.getTokenValue().equals("/")){
				lexer.nextToken();
				int value2=(Integer)parseUnary();
				value1/=value2;
			}else if(lexer.getTokenValue().equals("%")){
				lexer.nextToken();
				int value2=(Integer)parseUnary();
				value1%=value2;
			}else{
				lexer.putback();
				break;
			}
		}
		return value1;
	}
	
	private Object parseUnary(){
		if(lexer.getTokenValue().equals("+")){
			lexer.nextToken();
			if(lexer.getTokenValue().equals("+")){
				lexer.nextToken();
				Variable var=getVar(lexer.getTokenValue());
				if(var==null) ParseError.NOT_VAR.error();
				int ret=(Integer) var.getValue();
				var.setValue(++ret);
				return ret;
			}else{
				return parseParenthesis();
			}
		}else if(lexer.getTokenValue().equals("-")){
			lexer.nextToken();
			if(lexer.getTokenValue().equals("-")){
				lexer.nextToken();
				Variable var=getVar(lexer.getTokenValue());
				if(var==null) ParseError.NOT_VAR.error();
				int ret=(Integer) var.getValue();
				var.setValue(--ret);
				return ret;
			}else{
				return -((Integer)parseParenthesis());
			}
		}else{
			return parseParenthesis();
		}
	}
	
	private Object parseParenthesis(){
		if(lexer.getTokenValue().equals("(")){
			lexer.nextToken();
			Object value = parseAssignment();
			if(lexer.getTokenValue().equals(")")){
				return value;
			}else{
				ParseError.UNBAL_PARENS.error();
			}
		}
		return atom();
	}
	
	
	private Object atom(){
		switch(lexer.getTokenType()){
			case NUMBER: return Integer.parseInt(lexer.getTokenValue());
			case STRING: return lexer.getTokenValue();
			case IDENTIFIER:{
				String name=lexer.getTokenValue();
				lexer.nextToken();
				if("(".equals(lexer.getTokenValue())){
					return interpreter.callFunction(name);
				}else{
					Variable var=getVar(name);
					if(var==null) ParseError.NOT_VAR.error();
					int ret=(Integer) var.getValue();
					if("+".equals(lexer.getTokenValue())){
						lexer.nextToken();
						if("+".equals(lexer.getTokenValue())){
							var.setValue(ret+1);
							return ret;
						}else{
							lexer.putback();
						}
					}else if("-".equals(lexer.getTokenValue())){
						lexer.nextToken();
						if("-".equals(lexer.getTokenValue())){
							var.setValue(ret-1);
							return ret;
						}else{
							lexer.putback();
						}
					}
					lexer.putback();
					return ret;
				}
			}
		}
		ParseError.SYNTAX.error();
		return 0;
	}
	
	public Variable getVar(String name){
		if(interpreter!=null){
			FunctionRuntime fr=interpreter.getStack().peek();
			if(fr!=null){
				Variable var=fr.getLocal().get(name);
				if(var!=null) return var;
			}
		}
		return global.get(name);
	}
	
	public void addGlobalVar(Variable var){
		global.put(var.getName(), var);
	}
	
	public Lexer getLexer() {
		return lexer;
	}
	public void setLexer(Lexer lexer) {
		this.lexer = lexer;
	}
	public boolean isVarNeedDeclare() {
		return varNeedDeclare;
	}
	public void setVarNeedDeclare(boolean varNeedDeclare) {
		this.varNeedDeclare = varNeedDeclare;
	}
	public Map<String, Variable> getGlobal() {
		return global;
	}
	public Interpreter getInterpreter() {
		return interpreter;
	}
	public void setInterpreter(Interpreter interpreter) {
		this.interpreter = interpreter;
	}
	
}
