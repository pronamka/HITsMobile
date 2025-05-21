package com.example.hit.language.parser.operations

import com.example.hit.language.parser.ArrayToken
import com.example.hit.language.parser.ArrayValue
import com.example.hit.language.parser.BoolValue
import com.example.hit.language.parser.CallableValue
import com.example.hit.language.parser.IntValue
import com.example.hit.language.parser.Scopes
import com.example.hit.language.parser.StringValue
import com.example.hit.language.parser.SupportsArithmetic
import com.example.hit.language.parser.SupportsComparison
import com.example.hit.language.parser.TokenType
import com.example.hit.language.parser.Value
import com.example.hit.language.parser.ValueOperationFactory
import com.example.hit.language.parser.exceptions.IncompatibleTypesException
import com.example.hit.language.parser.exceptions.InvalidOperationException
import com.example.hit.language.parser.exceptions.UnexpectedTypeException

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
        return Scopes.getVariable(variableName)
    }

    override fun toString(): String {
        return "VariableOperation: $variableName."
    }
}

class ArrayElementOperation(
    val variableName: String,
    val indexValue: IOperation
) : IOperation {
    override fun evaluate(): Value<*> {
        val array = VariableOperation(variableName).evaluate()
        val index = indexValue.evaluate()
        if (array !is ArrayValue) {
            throw InvalidOperationException("Cannot use get operator: $variableName is not an array.")
        }
        if (index !is IntValue) {
            throw UnexpectedTypeException("Array indices can only be an integer, but $index was given.")
        }
        return array.get(index.value)
    }
}

class ComparisonOperation(
    val left: IOperation,
    val right: IOperation,
    val operationType: TokenType
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
            TokenType.EQUAL -> comparisonResult == 0
            TokenType.NOT_EQUAL -> comparisonResult != 0
            TokenType.LESS -> comparisonResult < 0
            TokenType.LESS_OR_EQUAL -> comparisonResult <= 0
            TokenType.GREATER -> comparisonResult > 0
            TokenType.GREATER_OR_EQUAL -> comparisonResult >= 0
            else -> throw InvalidOperationException(
                "${operationType::class.java.simpleName} is not a " +
                        "comparison operator."
            )
        }
        return BoolValue(operationResult)
    }
}

class LogicalNotOperation(
    val left: IOperation
) : IOperation {
    override fun evaluate(): Value<*> {
        val value = left.evaluate()
        if (value !is BoolValue) {
            throw InvalidOperationException("Cannot perform logical not operation on a non-bool value.")
        }
        return BoolValue(!value.value)
    }
}

class LogicalOperation(
    val left: IOperation,
    val right: IOperation,
    val operationType: TokenType
) : IOperation {
    override fun evaluate(): BoolValue {
        val first = left.evaluate()
        val second = right.evaluate()
        if (first !is BoolValue || second !is BoolValue) {
            throw IncompatibleTypesException(operationType.toString(), listOf(first, second!!))
        }

        val result = when (operationType) {
            TokenType.OR -> first.value || second.value
            TokenType.AND -> first.value && second.value
            else -> throw InvalidOperationException(
                "${operationType::class.java.simpleName} is not a " +
                        "logical operator."
            )
        }
        return BoolValue(result)
    }
}

class FunctionCallOperation(
    val functionName: String,
    val parameters: List<IOperation> = listOf()
) : IOperation {
    override fun evaluate(): Value<*> {
        val function = Scopes.getVariable(functionName)
        if (function !is CallableValue<*>) {
            throw InvalidOperationException(
                "Variable of type ${function::class.java.simpleName} is not callable."
            )
        }
        return function.call(parameters)
    }
}

class CreateArrayOperation(
    val arraySize: IOperation
) : IOperation {
    override fun evaluate(): Value<*> {
        return ValueOperationFactory(ArrayToken(arraySize)).create().evaluate()
    }
}

class GetLengthOperation(
    val obj: IOperation
) : IOperation {
    override fun evaluate(): Value<*> {
        val value = obj.evaluate()
        return when (value) {
            is ArrayValue -> IntValue(value.size)
            is StringValue -> IntValue(value.value.length)
            else -> throw UnexpectedTypeException(
                "Cannot get length of variable with type ${value::class.java.simpleName}."
            )
        }
    }
}