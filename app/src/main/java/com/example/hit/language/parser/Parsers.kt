package com.example.hit.language.parser

import com.example.hit.language.parser.exceptions.InvalidSyntaxException
import com.example.hit.language.parser.operations.ArrayElementOperation
import com.example.hit.language.parser.operations.BinaryOperation
import com.example.hit.language.parser.operations.ComparisonOperation
import com.example.hit.language.parser.operations.FunctionCallOperation
import com.example.hit.language.parser.operations.IOperation
import com.example.hit.language.parser.operations.LogicalNotOperation
import com.example.hit.language.parser.operations.LogicalOperation
import com.example.hit.language.parser.operations.MethodCallOperation
import com.example.hit.language.parser.operations.UnaryOperation
import com.example.hit.language.parser.operations.ValueOperation
import com.example.hit.language.parser.operations.VariableOperation

abstract class Parser(
    protected val tokens: List<Token>,
) {
    protected val EOF_TOKEN = Token(TokenType.EOF, "")
    var currentIndex = 0

    protected fun checkTokenType(index: Int, targetType: TokenType): Boolean {
        val targetToken = getToken(index)
        if (targetToken.tokenTypeEquals(targetType)) {
            return true
        }
        return false
    }

    protected fun checkCurrentTokenType(targetType: TokenType): Boolean {
        val currentToken = getCurrentToken()
        if (currentToken.tokenTypeEquals(targetType)) {
            currentIndex++
            return true
        }
        return false
    }

    protected fun checkCurrentTokenTypeIn(targetTypes: List<TokenType>): Boolean {
        val currentToken = getCurrentToken()
        for (tokenType in targetTypes) {
            if (currentToken.tokenTypeEquals(tokenType)) {
                currentIndex++
                return true
            }
        }
        return false
    }

    protected fun getToken(index: Int): Token {
        if (currentIndex + index >= tokens.size) return EOF_TOKEN
        return tokens[currentIndex + index]
    }

    protected fun getCurrentToken(): Token {
        if (currentIndex >= tokens.size) return EOF_TOKEN
        return tokens[currentIndex]
    }

    protected fun move(n: Int = 1) {
        currentIndex += n
    }
}

class OperationsParser(
    tokens: List<Token>,
) : Parser(tokens) {

    private val comparisonOperators = listOf(
        TokenType.EQUAL, TokenType.NOT_EQUAL,
        TokenType.LESS, TokenType.GREATER,
        TokenType.LESS_OR_EQUAL, TokenType.GREATER_OR_EQUAL
    )

    fun parse(): List<IOperation> {
        val results: MutableList<IOperation> = mutableListOf()
        while (currentIndex < tokens.size) {
            results.add(atBottomLevel())
        }
        if (results.isEmpty()) {
            throw InvalidSyntaxException("Expected an operation, but got null.")
        }
        return results
    }

    private fun atTopLevel(): IOperation {
        val currentToken = getCurrentToken()
        if (checkCurrentTokenType(TokenType.RETURN)) {
            return atBottomLevel()
        }
        if (checkCurrentTokenType(TokenType.LEFT_BRACKET)) {
            val values: MutableList<Value<*>> = mutableListOf()
            while (!checkCurrentTokenType(TokenType.RIGHT_BRACKET)) {
                if (checkCurrentTokenType(TokenType.COMMA)) {
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
        if (checkCurrentTokenType(TokenType.WORD)) {
            return VariableOperation(currentToken.tokenValue)
        }
        if (checkCurrentTokenTypeIn(
                listOf(
                    TokenType.INT,
                    TokenType.DOUBLE,
                    TokenType.STRING,
                    TokenType.TRUE,
                    TokenType.FALSE
                )
            )
        ) {
            return ValueOperationFactory(currentToken).create()
        }
        throw InvalidSyntaxException("Unexpected $currentToken")
    }

    private fun atLevelVariableOperation(): IOperation {
        var result = atTopLevel()
        while (true) {
            if (checkCurrentTokenType(TokenType.LEFT_BRACKET)) {
                result = ArrayElementOperation(result, atBottomLevel())
                if (!checkCurrentTokenType(TokenType.RIGHT_BRACKET)) {
                    throw InvalidSyntaxException("Bracket not closed when getting array element.")
                }
                continue
            }
            if (checkCurrentTokenType(TokenType.LEFT_PARENTHESIS)) {
                val values = getParameters()
                result = FunctionCallOperation(result, values)
                continue
            }
            if (checkCurrentTokenType(TokenType.DOT)) {
                val methodName = getCurrentToken()
                if (!checkCurrentTokenType(TokenType.WORD)) {
                    throw InvalidSyntaxException(
                        "Expected method name after method call operator '.'"
                    )
                }
                if (!checkCurrentTokenType(TokenType.LEFT_PARENTHESIS)) {
                    throw InvalidSyntaxException(
                        "Method ${methodName.tokenValue} must be invoked using ()."
                    )
                }
                val values = getParameters()
                result = MethodCallOperation(result, methodName.tokenValue, values)
                continue
            }
            break
        }
        return result
    }

    private fun atLevelUnary(): IOperation {
        val tokenType = getCurrentToken().tokenType
        if (checkCurrentTokenTypeIn(UnaryOperation.availableTokenTypes)) {
            return UnaryOperation(atLevelVariableOperation(), tokenType)
        }
        return atLevelVariableOperation()
    }

    private fun atLevelMultiplicationDivision(): IOperation {
        var result = atLevelUnary()
        while (true) {
            val operationType = getCurrentToken()
            if (checkCurrentTokenTypeIn(listOf(TokenType.ASTERISK, TokenType.SLASH))) {
                result = BinaryOperation(
                    result, atBottomLevel(),
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

    private fun atLevelComparison(): IOperation {
        var result = atLevelAdditionSubtraction()
        while (true) {
            val operationType = getCurrentToken()
            if (checkCurrentTokenTypeIn(comparisonOperators)) {
                result = ComparisonOperation(
                    result, atLevelAdditionSubtraction(),
                    operationType.tokenType
                )
                continue
            }
            break
        }
        return result
    }

    private fun atLevelLogicalNot(): IOperation {
        if (checkCurrentTokenType(TokenType.NOT)) {
            return LogicalNotOperation(atLevelComparison())
        }
        return atLevelComparison()
    }

    private fun atLevelLogical(): IOperation {
        var result = atLevelLogicalNot()
        while (true) {
            val operationType = getCurrentToken()
            if (checkCurrentTokenTypeIn(listOf(TokenType.AND, TokenType.OR))) {
                result = LogicalOperation(
                    result, atLevelLogicalNot(),
                    operationType.tokenType
                )
                continue
            }
            break
        }
        return result
    }

    fun atBottomLevel(): IOperation {
        return atLevelLogical()
    }

    private fun getParameters(): List<IOperation> {
        val values: MutableList<IOperation> = mutableListOf()
        while (!checkCurrentTokenType(TokenType.RIGHT_PARENTHESIS)) {
            if (checkCurrentTokenType(TokenType.COMMA)) {
                continue
            }
            if (checkCurrentTokenType(TokenType.EOF)) {
                throw InvalidSyntaxException(
                    "Parenthesis not closed."
                )
            }
            values.add(atBottomLevel())
        }
        return values
    }


}

class StatementsParser(
    tokens: List<Token>
) : Parser(tokens) {
    private val typeKeywords = listOf(
        TokenType.INT_KEYWORD, TokenType.DOUBLE_KEYWORD,
        TokenType.STRING_KEYWORD, TokenType.BOOL_KEYWORD
    )

    private val typesMap = mapOf(
        TokenType.INT_KEYWORD to VariableType.INT,
        TokenType.DOUBLE_KEYWORD to VariableType.DOUBLE,
        TokenType.STRING_KEYWORD to VariableType.STRING,
        TokenType.BOOL_KEYWORD to VariableType.BOOL
    )

    private fun parseOperation(): IOperation {
        val parser = OperationsParser(tokens.subList(currentIndex, tokens.size))
        val result = parser.atBottomLevel()
        currentIndex += parser.currentIndex
        return result
    }

    private fun parseArrayIndex(): Pair<Boolean, IOperation?> {
        val isArray = checkCurrentTokenType(TokenType.LEFT_BRACKET)
        var arrayLength: IOperation? = null
        if (isArray) {
            if (!checkCurrentTokenType(TokenType.RIGHT_BRACKET)) {
                arrayLength = parseOperation()
                if (!checkCurrentTokenType(TokenType.RIGHT_BRACKET)) {
                    throw InvalidSyntaxException("Expected ], but got ${getCurrentToken()}")
                }
            }
        }
        return Pair(isArray, arrayLength)
    }

    fun parseType(): VariableType {
        val variableType = getCurrentToken()
        if (!checkCurrentTokenTypeIn(typeKeywords)) {
            throw InvalidSyntaxException("Expected variable type, but got ${getCurrentToken()}")
        }

        val isArray = parseArrayIndex()
        if (isArray.first) {
            return VariableType.ARRAY(typesMap[variableType.tokenType]!!, isArray.second)
        }

        return typesMap[variableType.tokenType]!!
    }

    fun parseAssignment(): AssignmentStatement {
        val variableName = getCurrentToken()
        if (!checkCurrentTokenType(TokenType.WORD)) {
            throw InvalidSyntaxException("Expected variable name, but got ${getCurrentToken()}")
        }

        val isArrayIndex = parseArrayIndex()
        if (!checkCurrentTokenType(TokenType.EQUALS)) {
            throw InvalidSyntaxException("Expected equals operator(=), but got ${getCurrentToken()}")
        }

        val value = parseOperation()
        if (isArrayIndex.first) {
            if (isArrayIndex.second == null) {
                throw InvalidSyntaxException("Expected an integer index when assigning to array element, but got empty brackets.s")
            }
            return ArrayElementAssignmentStatement(
                variableName.tokenValue,
                value,
                isArrayIndex.second!!
            )
        }

        return VariableAssignmentStatement(variableName.tokenValue, value)
    }

    fun parseDeclaration(): DeclarationStatement {
        val variableName = getCurrentToken()
        if (!checkCurrentTokenType(TokenType.WORD)) {
            throw InvalidSyntaxException("Expected variable name, but got ${getCurrentToken()}")
        }

        val type = parseType()

        var variableValue: IOperation? = null
        if (checkCurrentTokenType(TokenType.EQUALS)) {
            variableValue = parseOperation()
        }

        return DeclarationStatement(type, variableName.tokenValue, variableValue)
    }

    fun parseFunctionParameters(): List<DeclarationStatement> {
        val results: MutableList<DeclarationStatement> = mutableListOf()
        while (currentIndex < tokens.size) {
            results.add(parseDeclaration())
            if (getCurrentToken() != EOF_TOKEN && !checkCurrentTokenType(TokenType.COMMA)) {
                throw InvalidSyntaxException("Expected comma after function parameter declaration, but got ${getCurrentToken()}")
            }
        }
        return results
    }
}
