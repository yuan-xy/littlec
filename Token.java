
public class Token {
	private String value;
	private TokenType type;
	
	public Token(String token, TokenType type) {
		this.value=token;
		this.type=type;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String token) {
		this.value = token;
	}
	public TokenType getType() {
		return type;
	}
	public void setType(TokenType type) {
		this.type = type;
	}
	
	
}
