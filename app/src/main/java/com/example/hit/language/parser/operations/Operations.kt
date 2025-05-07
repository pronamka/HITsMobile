package com.example.hit.language.parser.operations

import com.example.hit.language.parser.DoubleValue
import com.example.hit.language.parser.IntValue
import com.example.hit.language.parser.Token
import com.example.hit.language.parser.TokenType
import com.example.hit.language.parser.Value
import com.example.hit.language.parser.VariablesRepository

interface IOperation {
    fun evaluate(): Value<*>
}

class ValueOperation(
    var value: Value<*>
) : IOperation {
    override fun evaluate(): Value<*> = value

    override fun toString(): String {
        return "ValueOperation: $value"
    }
}

interface IUnaryOperation : IOperation {
    val operand: IOperation
}

class UnaryOperation(
    override val operand: IOperation,
    val operationType: TokenType
) : IUnaryOperation {
    companion object {
        val availableTokenTypes = listOf(
            TokenType.PLUS, TokenType.MINUS,
        )
        fun canBeApplied(token: Token): Boolean{
            return availableTokenTypes.contains(token.tokenType)
        }
    }
    val operationsMap: Map<TokenType, (IOperation) -> UnaryLeafOperation> = mapOf(
        TokenType.PLUS to { value -> UnaryPlusOperation(value.evaluate()) },
        TokenType.MINUS to { value -> UnaryMinusOperation(value.evaluate()) },
    )

    override fun evaluate(): Value<*> = operationsMap[operationType]!!.invoke(operand).evaluate()
}

abstract class UnaryLeafOperation(
    val operand: Value<*>,
) : IOperation {
    abstract val operationSymbol: String
    override fun toString(): String {
        return "$operationSymbol $operand = ${evaluate()}"
    }
}

class UnaryPlusOperation(
    value: Value<*>,
) : UnaryLeafOperation(value) {
    override val operationSymbol = "+"
    override fun evaluate(): Value<*> = operand
}

class UnaryMinusOperation(
    value: Value<*>,
) : UnaryLeafOperation(value) {
    override val operationSymbol = "-"
    override fun evaluate(): Value<*> {
        if (operand is IntValue) {
            return IntValue(-operand.value)
        }
        if (operand is DoubleValue) {
            return DoubleValue(-operand.value)
        }

        throw IllegalArgumentException(
            "Operation '-' cannot be performed on a ${operand::class::qualifiedName} value: $operand"
        )
    }
}

interface IBinaryOperation : IOperation {
    val left: IOperation
    val right: IOperation
}

class BinaryOperation(
    override val left: IOperation,
    override val right: IOperation,
    val operationType: TokenType
) : IBinaryOperation {
    companion object {
        val availableTokenTypes = listOf(
            TokenType.PLUS, TokenType.MINUS,TokenType.ASTERISK, TokenType.SLASH
        )
        fun canBeApplied(token: Token): Boolean{
            return availableTokenTypes.contains(token.tokenType)
        }
    }
    val operationsMap: Map<TokenType, (left: IOperation, right: IOperation) -> Value<*>> = mapOf(
        TokenType.PLUS to { left, right -> left.evaluate().add(right.evaluate()) },
        TokenType.MINUS to { left, right -> left.evaluate().subtract(right.evaluate()) },
        TokenType.ASTERISK to { left, right -> left.evaluate().multiplyBy(right.evaluate()) },
        TokenType.SLASH to { left, right -> left.evaluate().divideBy(right.evaluate()) }
    )

    override fun evaluate(): Value<*> {
        return operationsMap[operationType]!!.invoke(left, right)
    }

    override fun toString(): String {
        return "BinaryOperation: ${left} $operationType ${right}"
    }
}

class VariableOperation(
    val variableName: String
): IOperation{
    override fun evaluate(): Value<*> {
        return VariablesRepository.get(variableName)
    }

    override fun toString(): String {
        return "VariableOperation: $variableName."
    }
}

class AssignmentOperation(
    val variableName: String,
    val variableValue: IOperation
): IOperation {
    override fun evaluate(): Value<*> {
        VariablesRepository.add(variableName, variableValue.evaluate())
        return VariablesRepository.get(variableName)
    }

    override fun toString(): String {
        return "AssignmentOperation: $variableName = $variableValue"
    }
}