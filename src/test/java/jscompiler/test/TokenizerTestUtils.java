package jscompiler.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jscompiler.Tokenizer;
import jscompiler.token.Token;
import jscompiler.token.TokenType;
import org.junit.Assert;

public class TokenizerTestUtils {
	
	private static Map<String, Token> tokensCache;
	
	static {
		tokensCache = new HashMap<String, Token>();
		
		for (TokenType type : TokenType.values()) {
			if (!type.isConstantRepresentation()) {
				continue;
			}
			tokensCache.put(type.getAsString(), new Token(type));
		}
	}

	public static void matchSequence(Tokenizer tokenizer, String... tokens) {
		List<Token> tokensExpected = buildTokens(tokens);
		int i = 0; 
		for (Token  token : tokensExpected) {
			matchTokens(tokenizer.next(), token);
			i++;
		}
		
		Assert.assertEquals(
				tokenizer.next().getType().compareTo(TokenType.EOF), 0);
			
	}

	private static void matchTokens(Token next, Token token) {
		Assert.assertEquals(token.getType().getCode(), next.getType().getCode());
		Assert.assertEquals( token.getValue(), next.getValue());
		
	}

	private static List<Token> buildTokens(String[] tokens) {
		
		List<Token> toReturn = new ArrayList<Token>();
		for (String token : tokens) {
			if (token.startsWith("@@@@")) {
				String name = token.substring(4);
				toReturn.add(new Token(TokenType.ERROR, name));
				continue;
			}
			if (token.startsWith("@@@")) {
				String name = token.substring(3);
				toReturn.add(new Token(TokenType.NUMBER_CONSTANT, name));
				continue;
			}
			if (token.startsWith("@@")) {
				String name = token.substring(2);
				toReturn.add(new Token(TokenType.STRING_CONSTANT, name));
				continue;
			}
			if (token.startsWith("@")) {
				String name = token.substring(1);
				toReturn.add(new Token(TokenType.IDENTIFIER, name));
				continue;
			}
			Token t = tokensCache.get(token);
			toReturn.add(t);
		}
		
		return toReturn;
	}
	
	

}
