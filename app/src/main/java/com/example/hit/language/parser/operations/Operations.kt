package com.example.hit.language.parser.operations

import com.example.hit.language.parser.BoolValue
import com.example.hit.language.parser.SupportsArithmetic
import com.example.hit.language.parser.SupportsComparison
import com.example.hit.language.parser.TokenType
import com.example.hit.language.parser.Value
import com.example.hit.language.parser.VariablesRepository
import com.example.hit.language.parser.exceptions.IncompatibleTypesException

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
    }

    val operationsMap: Map<TokenType, (SupportsArithmetic<*>) -> SupportsArithmetic<*>> = mapOf(
        TokenType.PLUS to { value -> value },
        TokenType.MINUS to { value -> value.changeSign() },
    )

    override fun evaluate(): SupportsArithmetic<*> {
        val value = operand.evaluate()
        if (value !is SupportsArithmetic) {
            throw IncompatibleTypesException(operationType.toString(), listOf(value))
        }
        return operationsMap[operationType]!!.invoke(value)
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
            TokenType.PLUS, TokenType.MINUS, TokenType.ASTERISK, TokenType.SLASH
        )
    }

    val operationsMap: Map<TokenType, (left: SupportsArithmetic<*>, right: SupportsArithmetic<*>) -> SupportsArithmetic<*>> =
        mapOf(
            TokenType.PLUS to { left, right -> left.add(right) },
            TokenType.MINUS to { left, right -> left.subtract(right) },
            TokenType.ASTERISK to { left, right -> left.multiplyBy(right) },
            TokenType.SLASH to { left, right -> left.divideBy(right) }
        )

    override fun evaluate(): SupportsArithmetic<*> {
        val first = left.evaluate()
        val second = right.evaluate()
        if (first !is SupportsArithmetic<*> || second !is SupportsArithmetic<*>) {
            throw IncompatibleTypesException(operationType.toString(), listOf(left, right))
        }
        return operationsMap[operationType]!!.invoke(first, second)
    }

    override fun toString(): String {
        return "BinaryOperation: $left $operationType $right"
    }
}

class VariableOperation(
    val variableName: String
) : IOperation {
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
) : IOperation {
    override fun evaluate(): Value<*> {
        VariablesRepository.add(variableName, variableValue.evaluate())
        return VariablesRepository.get(variableName)
    }

    override fun toString(): String {
        return "AssignmentOperation: $variableName = $variableValue"
    }
}

class ConditionalOperation(
    val left: IOperation,
    val right: IOperation,
    val operationType: ConditionalOperationType
) : IOperation {
    override fun evaluate(): BoolValue {
        val first = left.evaluate()
        val second = right.evaluate()
        if (first !is SupportsComparison<*> || second !is SupportsComparison<*>) {
            throw IncompatibleTypesException(
                operationType.toString(), listOf(
                    first, second
                )
            )
        }
        val comparisonResult = first.compareTo(second).value
        val operationResult = when (operationType) {
            ConditionalOperationType.EQUAL -> comparisonResult == 0
            ConditionalOperationType.NOT_EQUAL -> comparisonResult != 0
            ConditionalOperationType.LESS -> comparisonResult < 0
            ConditionalOperationType.LESS_OR_EQUAL -> comparisonResult <= 0
            ConditionalOperationType.GREATER -> comparisonResult > 0
            ConditionalOperationType.GREATER_OR_EQUAL -> comparisonResult >= 0
        }
        return BoolValue(operationResult)
    }
}