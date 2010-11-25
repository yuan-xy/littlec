
public enum ParseError {
SYNTAX, UNBAL_PARENS, NO_EXP, EQUALS_EXPECTED,
     NOT_VAR, PARAM_ERR,PARAM_NOT_EQUAL, SEMI_EXPECTED,
     UNBAL_BRACES, FUNC_UNDEF, TYPE_EXPECTED,
     NEST_FUNC, RET_NOCALL, PAREN_EXPECTED,
     WHILE_EXPECTED, QUOTE_EXPECTED, NOT_TEMP,
     TOO_MANY_LVARS, DIV_BY_ZERO;

	@Override
	public String toString() {
		switch(this) { 
			case SYNTAX: return "syntax error";
			case UNBAL_PARENS: return "()没有匹配";
			case NO_EXP: return "没有表达式";
			case NOT_VAR: return "不是变量";
			case PARAM_ERR: return "参数错误";
			case PARAM_NOT_EQUAL: return "形参和实参个数不匹配";
			case SEMI_EXPECTED: return "缺少;号";
			case UNBAL_BRACES: return "{}没有匹配";
			case FUNC_UNDEF: return "函数未定义";
			case TYPE_EXPECTED: return "syntax error";
			case NEST_FUNC: return "syntax error";
		}
		return super.toString();
	}

	void error(){
		throw new RuntimeException(toString());
	}
     
     
}
