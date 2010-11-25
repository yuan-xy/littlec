import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;


public class Interpreter {
	private Lexer lexer;
	private ExpressionParser parser;
	private Map<String,Function> functionTable=new HashMap<String, Function>();
	private Stack<FunctionRuntime> stack=new Stack<FunctionRuntime>();
	private Lib lib;
	private Object returnValue;
	
	public Interpreter(String code){
		lexer=new Lexer(code);
		parser=new ExpressionParser(lexer);
		parser.setVarNeedDeclare(true);
		lib=new Lib();
		parser.setInterpreter(this);
	}
	public static Interpreter newInstance(FileReader fr){
		StringBuffer sb=new StringBuffer();
		int i;
		try {
			while((i=fr.read())!=-1) sb.append((char)i);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return new Interpreter(sb.toString());
	}
	
	Lib getLib(){
		return lib;
	}
	Lexer getLexer() {
		return lexer;
	}
	ExpressionParser getParser() {
		return parser;
	}
	Map<String, Function> getFunctionTable() {
		return functionTable;
	}
	Stack<FunctionRuntime> getStack(){
		return stack;
	}
	Object getReturnValue() {
		return returnValue;
	}


	void run(){
		prescan();
		returnValue=callFunction("main");
	}
	
	Object callFunction(String name) {
		if (Lib.isLibFunction(name)) {
			FunctionRuntime fr = new FunctionRuntime(new Function(name,"",0));
			parseArgs(fr);
			return lib.call(fr);
		}
		Function func = functionTable.get(name);
		if (func == null) ParseError.FUNC_UNDEF.error();
		FunctionRuntime fr = new FunctionRuntime(func);
		if(!"main".equals(name)) parseArgs(fr);
		stack.add(fr);
		lexer.setIndex(func.getLocation());
		setParam();
		InterpretBlock();
		return stack.pop().getReturnValue();
	}

	private void InterpretBlock() {
		lexer.nextToken();
		if(!lexer.getTokenValue().equals("{")) ParseError.SYNTAX.error();
		do{
			lexer.nextToken();
			if(lexer.getTokenType()==TokenType.IDENTIFIER){
				lexer.putback();
				parser.parse();
			}else if(lexer.getTokenType()==TokenType.KEYWORD){
				if(lexer.getTokenValue().equals("return")){
					FunctionRuntime fr=stack.peek();
					fr.setReturnValue(parser.parse());
					return;
				}
				if(lexer.getTokenValue().equals("int")){
					declareLocalVar("int");
				}
				if(lexer.getTokenValue().equals("if")){
					execIf();//if执行完后的token是},如果不改变token会导致程序直接返回。
					lexer.nextToken();
					lexer.putback();
				}
				if(lexer.getTokenValue().equals("else")){
					findEndOfBlock();
				}
				if(lexer.getTokenValue().equals("while")){
					execWhile();
				}
				if(lexer.getTokenValue().equals("do")){
					execDo();
				}
				if(lexer.getTokenValue().equals("for")){
					execFor();
				}
			}
		}while(!lexer.getTokenValue().equals("}"));
	}

	private void execFor() {
		lexer.nextToken();
		if(!lexer.getTokenValue().equals("(")) ParseError.PAREN_EXPECTED.error();
		parseCommaSeperatedExpression();
		int loc1=lexer.getIndex();
		while(true){
			int value=(Integer) parser.parse();
			if(value!=0){
				if(!lexer.getTokenValue().equals(";")) ParseError.SEMI_EXPECTED.error();
				lexer.nextToken();
				int loc2=lexer.getIndex();
				bypassForHead();
				lexer.putback();
				InterpretBlock();
				lexer.setIndex(loc2);
				parser.parse();
				lexer.setIndex(loc1);
			}else{
				bypassForHead();
				findEndOfBlock();
				lexer.nextToken();
				lexer.putback();
				return;
			}
		}
	}

	private void bypassForHead() {
		do {
			lexer.nextToken();
		}while(!lexer.getTokenValue().equals("{"));
	}

	private void parseCommaSeperatedExpression() {
		do{
			parser.parse();
		}while(lexer.getTokenValue().equals(","));
		if(!lexer.getTokenValue().equals(";")) ParseError.SEMI_EXPECTED.error();
	}

	private void execDo() {
		int location=lexer.getIndex();
		while(true){
			InterpretBlock();
			lexer.nextToken();
			if(!lexer.getTokenValue().equals("while")) ParseError.SYNTAX.error();
			int value=(Integer)parser.parse();
			if(value!=0){
				lexer.setIndex(location);
				continue;
			}
			break;
		}
	}

	private void execWhile() {
		int location=lexer.getIndex();
		while(true){
			int value=(Integer)parser.parse();
			if(value!=0){
				InterpretBlock();
				lexer.setIndex(location);
			}else{
				lexer.nextToken();
				findEndOfBlock();
				lexer.nextToken();
				lexer.putback();
				return;
			}
		}
	}

	private void execIf() {
		int value=(Integer)parser.parse();
		if(value!=0){
			InterpretBlock();
		}else{
			findEndOfBlock();
			lexer.nextToken();
			if(lexer.getTokenValue().equals("else")){
				InterpretBlock();
			}else{
				lexer.putback();
			}
		}
	}
	
	private void findEndOfBlock(){
		  int brace = 1;
		  do {
		    lexer.nextToken();
		    if(lexer.getTokenValue().equals("{")) brace++;
		    else if(lexer.getTokenValue().equals("}")) brace--;
		  } while(brace>0);
	}

	private void parseArgs(FunctionRuntime fr) {
		lexer.nextToken();
		if(lexer.getTokenValue().equals(")")) return;
		lexer.putback();
		do{
			fr.getArguments().add(new Variable("",parser.parse(),"int"));//类型处理
		}while(lexer.getTokenValue().equals(","));
		if(!lexer.getTokenValue().equals(")")) ParseError.PAREN_EXPECTED.error();
	}
	
	private void setParam() {
		FunctionRuntime fr=stack.peek();
		do{
			lexer.nextToken();
			if(lexer.getTokenValue().equals(")")) break;
			if(!lexer.getTokenValue().equals("int")) ParseError.SYNTAX.error();
			lexer.nextToken();
			Variable var=null;
			try{
				var=fr.getArguments().remove(0);
			}catch(IndexOutOfBoundsException e){ParseError.PARAM_NOT_EQUAL.error();}
			var.setName(lexer.getTokenValue());
			var.setType("int");
			fr.addLocalVar(var);
			lexer.nextToken();
		}while(lexer.getTokenValue().equals(","));
		if(fr.getArguments().size()>0) ParseError.PARAM_NOT_EQUAL.error();
	}

	/**
	 * Find the location of all functions in the program and store global variables.
	 *
	 */
	void prescan(){
		int brace=0;
		do{
			while(brace>0){
				lexer.nextToken();
				if(lexer.getTokenValue().equals("{")) brace++;
				if(lexer.getTokenValue().equals("}")) brace--;
			}
			lexer.nextToken();
			if(lexer.getToken()==null) return;
			if(lexer.getTokenValue().equals("int")
					||lexer.getTokenValue().equals("char")
					||lexer.getTokenValue().equals("float")
					||lexer.getTokenValue().equals("double")){
				String type=lexer.getTokenValue();
				lexer.nextToken();
				if(lexer.getTokenType()==TokenType.IDENTIFIER){
					String name=lexer.getTokenValue();
					lexer.nextToken();
					if(lexer.getTokenValue().equals("(")){
						functionTable.put(name, new Function(name,type,getLexer().getIndex()));
						do{
							lexer.nextToken();
						}while(!lexer.getTokenValue().equals(")"));
					}else{
						declareGlobalVar(type);
					}
				}else{
					ParseError.SYNTAX.error();
				}
			}else if(lexer.getTokenValue().equals("{")){
				brace++;
			}
		}while(true);
	}
	
	private void declareGlobalVar(String type) {
		lexer.putback();
		lexer.putback();
		do{
			lexer.nextToken();
			String name=lexer.getTokenValue();
			Variable var=new Variable(name,0,type);
			lexer.nextToken();
			if(lexer.getTokenValue().equals("=")) var.setValue(parser.parse());
			parser.addGlobalVar(var);
		}while(lexer.getTokenValue().equals(","));
		if(!lexer.getTokenValue().equals(";")) ParseError.SEMI_EXPECTED.error();
	}
	private void declareLocalVar(String type) {
		do{
			lexer.nextToken();
			String name=lexer.getTokenValue();
			Variable var=new Variable(name,0,type);
			stack.peek().addLocalVar(var);
			lexer.nextToken();
			if(lexer.getTokenValue().equals("=")) var.setValue(parser.parse());
		}while(lexer.getTokenValue().equals(","));
		if(!lexer.getTokenValue().equals(";")) ParseError.SEMI_EXPECTED.error();
	}
	
	public static void main(String[] args) throws IOException{
		if(args.length!=1){
			System.out.println("Usage: java Interpreter filename");
			System.exit(0);
		}
		Interpreter itp=Interpreter.newInstance(new FileReader(args[0]));
		System.out.println(itp.getLexer().getCode());
		itp.run();
	}

}
