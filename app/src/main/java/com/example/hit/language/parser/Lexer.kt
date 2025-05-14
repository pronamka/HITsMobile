package com.example.hit.language.parser

import com.example.hit.language.parser.exceptions.NumberParseException

class Lexer(
    val inputString: String
) {
    private val operators: MutableMap<Char, TokenType> = mutableMapOf(
        '+' to TokenType.PLUS,
        '-' to TokenType.MINUS,
        '*' to TokenType.ASTERISK,
        '/' to TokenType.SLASH,
        '(' to TokenType.LEFT_PARENTHESIS,
        ')' to TokenType.RIGHT_PARENTHESIS,
        '[' to TokenType.LEFT_BRACKET,
        ']' to TokenType.RIGHT_BRACKET,
        ',' to TokenType.COMMA,
        '=' to TokenType.EQUALS,
    )

    private val keywords: Map<String, TokenType> = mapOf(
        "true" to TokenType.TRUE,
        "false" to TokenType.FALSE,
        "return" to TokenType.RETURN,
    )

    private val tokens: MutableList<Token> = mutableListOf()

    private var currentIndex = 0

    fun tokenize(): List<Token> {
        while (currentIndex < inputString.length) {
            val currentCharacter = peekAtIndex(0)
            if (currentCharacter.isDigit()) {
                tokenizeNumber()
            } else if (operators.containsKey(currentCharacter)) {
                addToken(operators[currentCharacter]!!, currentCharacter.toString())
                getNextChar()
            } else if (currentCharacter.isLetter() || currentCharacter == '_') {
                tokenizeWord()
            } else if (currentCharacter == '"') {
                tokenizeString()
                getNextChar()
            } else {
                getNextChar()
            }
        }
        return tokens
    }

    fun tokenizeNumber() {
        val startIndex = currentIndex
        var containsPoint = false
        var currentCharacter = peekAtIndex(0)
        while (true) {
            if (currentCharacter == '.') {
                if (containsPoint) {
                    throw NumberParseException(currentIndex)
                }
                containsPoint = true
            } else if (!currentCharacter.isDigit()) {
                break
            }
            currentCharacter = getNextChar()
        }
        if (containsPoint) {
            addToken(TokenType.DOUBLE, inputString.substring(startIndex..currentIndex - 1))
        } else {
            addToken(TokenType.INT, inputString.substring(startIndex..currentIndex - 1))
        }
    }

    fun tokenizeWord() {
        val startIndex = currentIndex
        var currentCharacter = peekAtIndex(0)
        while (true) {
            if (!currentCharacter.isLetterOrDigit() && (currentCharacter != '_') && (currentCharacter != '$')) {
                break
            }
            currentCharacter = getNextChar()
        }
        val word = inputString.substring(startIndex..currentIndex - 1)
        if (keywords.containsKey(word)){
            addToken(keywords[word]!!, word)
            return
        }
        addToken(TokenType.WORD, word)
    }

    fun tokenizeString() {
        var currentCharacter = getNextChar()
        val startIndex = currentIndex
        val stringBreakers = "\"\n\t"
        while (true) {
            if (currentCharacter in stringBreakers) {
                break
            }
            currentCharacter = getNextChar()
        }
        addToken(TokenType.STRING, inputString.substring(startIndex..currentIndex - 1))
    }

    fun addToken(tokenType: TokenType, tokenValue: String) {
        tokens.add(Token(tokenType, tokenValue))
    }

    fun getNextChar(): Char {
        currentIndex++
        return peekAtIndex(0)
    }

    fun peekAtIndex(relativeIndex: Int): Char {
        val absoluteIndex = currentIndex + relativeIndex
        if (absoluteIndex >= inputString.length) return '\u0000'
        return inputString[absoluteIndex]
    }
}