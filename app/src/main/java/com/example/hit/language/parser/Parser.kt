package com.example.hit.language.parser

import com.example.hit.language.parser.operations.AssignmentOperation
import com.example.hit.language.parser.operations.BinaryOperation
import com.example.hit.language.parser.operations.IOperation
import com.example.hit.language.parser.operations.UnaryOperation
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
        if (checkCurrentTokenType(listOf(TokenType.LEFT_BRACE))) {
            val result = atBottomLevel()
            checkCurrentTokenType(listOf(TokenType.RIGHT_BRACE))
            return result
        }
        if (checkCurrentTokenType(listOf(TokenType.WORD))) {
            return VariableOperation(currentToken.tokenValue)
        }
        if (checkCurrentTokenType(
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
        if (checkCurrentTokenType(UnaryOperation.availableTokenTypes)) {
            return UnaryOperation(atTopLevel(), tokenType)
        }
        return atTopLevel()
    }

    private fun atLevelMultiplicationDivision(): IOperation {
        var result = atLevelUnary()
        while (true) {
            val operationType = getCurrentToken()
            if (checkCurrentTokenType(listOf(TokenType.ASTERISK, TokenType.SLASH))) {
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

    private fun atLevelAdditionSubstraction(): IOperation {
        var result = atLevelMultiplicationDivision()
        while (true) {
            val operationType = getCurrentToken()
            if (checkCurrentTokenType(listOf(TokenType.PLUS, TokenType.MINUS))) {
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

    private fun atLevelAssignment(): IOperation {
        val currentToken = getCurrentToken()
        if (checkCurrentTokenType(listOf(TokenType.WORD)) &&
            checkCurrentTokenType(listOf(TokenType.EQUALS))
        ) {
            return AssignmentOperation(currentToken.tokenValue, atLevelAdditionSubstraction())
        }
        return atLevelAdditionSubstraction()
    }

    private fun atBottomLevel(): IOperation {
        return atLevelAssignment()
    }


    private fun checkCurrentTokenType(targetTypes: List<TokenType>): Boolean {
        val currentToken = getCurrentToken()
        for (tokenType in targetTypes) {
            if (currentToken.tokenTypeEquals(tokenType)) {
                currentIndex++
                return true
            }
        }
        return false
    }

    fun getCurrentToken(): Token {
        if (currentIndex >= tokens.size) return EOF_TOKEN
        return tokens[currentIndex]
    }
}

interface ITreeLevel {
    val allowedTokenTypes: List<TokenType>
    fun execute(): IOperation
}
