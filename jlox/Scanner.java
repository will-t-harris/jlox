package jlox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static jlox.TokenType.*;

class Scanner {
	private final String source;
	private final List<Token> tokens = new ArrayList<>();
	// start/current index into the string (current token)
	private int start = 0;
	private int current = 0;
	// line tracks current source line
	private int line = 1;

	Scanner(String source) {
		this.source = source;
	}

	List<Token> scanTokens() {
		while (!isAtEnd()) {
			start = current;
			scanToken();
		}

		tokens.add(new Token(EOF, "", null, line));
		return tokens;
	}

	private void scanToken() {
		char c = advance();

		switch (c) {
		case '(':
			addToken(LEFT_PAREN);
			break;
		case ')':
			addToken(RIGHT_PAREN);
			break;
		case '{':
			addToken(LEFT_BRACE);
			break;
		case '}':
			addToken(RIGHT_BRACE);
			break;
		case ',':
			addToken(COMMA);
			break;
		case '.':
			addToken(DOT);
			break;
		case '-':
			addToken(MINUS);
			break;
		case '+':
			addToken(PLUS);
			break;
		case ';':
			addToken(SEMICOLON);
			break;
		case '*':
			addToken(STAR);
			break;

		// Possible one or two character lexemes
		case '!':
			addToken(match('=') ? BANG_EQUAL : BANG);
			break;
		case '=':
			addToken(match('=') ? EQUAL_EQUAL : EQUAL);
			break;
		case '<':
			addToken(match('=') ? LESS_EQUAL : LESS);
			break;
		case '>':
			addToken(match('=') ? GREATER_EQUAL : GREATER);
			break;
		case '/':
			if (match('/')) {
				while (peek() != '\n' && !isAtEnd()) {
					advance();
				}
			} else {
				addToken(SLASH);
			}
			break;

		case ' ':
		case '\r':
		case '\t':
			// ignore whitespace
			break;

		case '\n':
			line++;
			break;

		case '"':
			string();
			break;

		// Report error if there was no match
		default:
			if (isDigit(c)) {
				number();
			} else if (isAlpha(c)) {
				identifier();
			} else {
				Lox.error(line, "Unexpected character.");
				break;
			}
		}
	}

	private void identifier() {
		while (isAlphaNumeric(peek())) {
			advance();
		}

		addToken(IDENTIFIER);
	}

	private void number() {
		while (isDigit(peek())) {
			advance();
		}

		// look for fractional part of number
		if (peek() == '.' && isDigit(peekNext())) {
			// consume the period
			advance();

			while (isDigit(peek())) {
				advance();
			}
		}

		addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
	}

	private void string() {
		while (peek() != '"' && !isAtEnd()) {
			if (peek() == '\n') {
				line++;
			}

			advance();
		}

		if (isAtEnd()) {
			Lox.error(line, "Unterminated string");
			return;
		}

		advance(); // gets the closing '"'

		// trims the surrounding quotes
		String value = source.substring(start + 1, current - 1);

		addToken(STRING, value);
	}

	private boolean match(char expected) {
		if (isAtEnd()) {
			return false;
		}
		if (source.charAt(current) != expected) {
			return false;
		}

		// only consume the character if it's what we're looking for
		current++;
		return true;
	}

	// single character lookahead
	// similar to advance() but it doesn't consume the character
	private char peek() {
		if (isAtEnd()) {
			return '\0';
		}

		return source.charAt(current);
	}

	// two character lookahead
	private char peekNext() {
		if (current + 1 >= source.length()) {
			return '\0';
		}

		return source.charAt(current + 1);
	}

	private boolean isAlpha(char c) {
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
	}

	private boolean isAlphaNumeric(char c) {
		return isAlpha(c) || isDigit(c);
	}

	private boolean isDigit(char c) {
		return c >= '0' && c <= '9';
	}

	private boolean isAtEnd() {
		return current >= source.length();
	}

	// consume and return the next character in source file
	private char advance() {
		return source.charAt(current++);
	}

	private void addToken(TokenType type) {
		addToken(type, null);
	}

	private void addToken(TokenType type, Object literal) {
		String text = source.substring(start, current);
		tokens.add(new Token(type, text, literal, line));
	}
}