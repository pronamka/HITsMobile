package com.example.hit.language.parser

import com.example.hit.language.parser.operations.ArrayElementOperation
import com.example.hit.language.parser.operations.BinaryOperation
import com.example.hit.language.parser.operations.IOperation
import com.example.hit.language.parser.operations.UnaryOperation
import com.example.hit.language.parser.operations.ValueOperation
import com.example.hit.language.parser.operations.VariableOperation

class Parser(
    private val tokens: List<Token>,
) {
    private val EOF_TOKEN = Token(TokenType.EOF, "")
    private var currentIndex = 0

    fun parse(): List<IOperation> {
        val results: MutableList<IOperation> = mutableListOf()
        while (currentIndex < tokens.size) {
            results.add(atLevelUnary())
        }
        return results
    }

    private fun atTopLevel(): IOperation {
        val currentToken = getCurrentToken()
        if (checkCurrentTokenType(TokenType.LEFT_BRACKET)){
            val values: MutableList<Value<*>> = mutableListOf()
            while (!checkCurrentTokenType(TokenType.RIGHT_BRACKET)){
                if (checkCurrentTokenType(TokenType.COMMA)){
                    continue
                }
                values.add(ValueFactory(getCurrentToken()).create())
                move()
            }
            val collectionValue = CollectionValue(values)
            return ValueOperation(collectionValue)
        }
        if (checkCurrentTokenType(TokenType.LEFT_PARENTHESIS)) {
            val result = atBottomLevel()
            move()
            return result
        }
        if (checkTokenType(0, TokenType.WORD) && checkTokenType(1, TokenType.LEFT_BRACKET)){
            val arrayName = getCurrentToken().tokenValue
            move(2)
            val operation = ArrayElementOperation(arrayName, atBottomLevel())
            move()
            return operation
        }
        if (checkCurrentTokenType(TokenType.WORD)) {
            return VariableOperation(currentToken.tokenValue)
        }
        if (checkCurrentTokenTypeIn(
                listOf(
                    TokenType.INT, TokenType.DOUBLE, TokenType.STRING
                )
            )
        ) {
            return ValueOperationFactory(currentToken).create()
        }
        throw RuntimeException("Something went wrong")
    }

    private fun atLevelUnary(): IOperation {
        val tokenType = getCurrentToken().tokenType
        if (checkCurrentTokenTypeIn(UnaryOperation.availableTokenTypes)) {
            return UnaryOperation(atTopLevel(), tokenType)
        }
        return atTopLevel()
    }

    private fun atLevelMultiplicationDivision(): IOperation {
        var result = atLevelUnary()
        while (true) {
            val operationType = getCurrentToken()
            if (checkCurrentTokenTypeIn(listOf(TokenType.ASTERISK, TokenType.SLASH))) {
                result = BinaryOperation(
                    result, atLevelUnary(),
                    operationType.tokenType
                )
                continue
            }
            break
        }
        return result
    }

    private fun atLevelAdditionSubtraction(): IOperation {
        var result = atLevelMultiplicationDivision()
        while (true) {
            val operationType = getCurrentToken()
            if (checkCurrentTokenTypeIn(listOf(TokenType.PLUS, TokenType.MINUS))) {
                result = BinaryOperation(
                    result, atLevelMultiplicationDivision(),
                    operationType.tokenType
                )
                continue
            }
            break
        }
        return result
    }

    private fun atBottomLevel(): IOperation{
        return atLevelUnary()
    }

    private fun checkTokenType(index: Int, targetType: TokenType): Boolean{
        val targetToken = getToken(index)
        if (targetToken.tokenTypeEquals(targetType)) {
            return true
        }
        return false
    }

    private fun checkCurrentTokenType(targetType: TokenType): Boolean {
        val currentToken = getCurrentToken()
        if (currentToken.tokenTypeEquals(targetType)) {
            currentIndex++
            return true
        }
        return false
    }

    private fun checkCurrentTokenTypeIn(targetTypes: List<TokenType>): Boolean {
        val currentToken = getCurrentToken()
        for (tokenType in targetTypes) {
            if (currentToken.tokenTypeEquals(tokenType)) {
                currentIndex++
                return true
            }
        }
        return false
    }

    private fun getToken(index: Int): Token{
        if (currentIndex+index >= tokens.size) return EOF_TOKEN
        return tokens[currentIndex+index]
    }

    private fun getCurrentToken(): Token {
        if (currentIndex >= tokens.size) return EOF_TOKEN
        return tokens[currentIndex]
    }

    private fun move(n: Int = 1){
        currentIndex += n
    }
}

interface ITreeLevel {
    val allowedTokenTypes: List<TokenType>
    fun execute(): IOperation
}
