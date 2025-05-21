package com.example.hit.language.parser

import com.example.hit.language.parser.exceptions.IncompatibleTypesException
import com.example.hit.language.parser.exceptions.InvalidOperationException
import com.example.hit.language.parser.exceptions.InvalidParametersAmountException
import com.example.hit.language.parser.exceptions.ReturnException
import com.example.hit.language.parser.exceptions.UnexpectedTypeException
import com.example.hit.language.parser.operations.IOperation
import com.example.hit.language.parser.operations.ValueOperation
import kotlin.reflect.KClass

interface IValue {}

abstract class Value<T>(
    val value: T
)

class NullValue : Value<Any>(0)

abstract class SupportsComparison<T : Comparable<T>>(
    value: T
) : Value<T>(value) {
    abstract fun compareTo(other: SupportsComparison<*>): IntValue
}

abstract class SupportsArithmetic<T : Comparable<T>>(
    value: T
) : SupportsComparison<T>(value) {
    abstract fun add(other: SupportsArithmetic<*>): SupportsArithmetic<*>
    abstract fun subtract(other: SupportsArithmetic<*>): SupportsArithmetic<*>
    abstract fun multiplyBy(other: SupportsArithmetic<*>): SupportsArithmetic<*>
    abstract fun divideBy(other: SupportsArithmetic<*>): SupportsArithmetic<*>
    abstract fun changeSign(): SupportsArithmetic<*>
}


class IntValue(
    value: Int
) : SupportsArithmetic<Int>(value) {
    override fun toString(): String {
        return value.toString()
    }

    override fun add(other: SupportsArithmetic<*>): SupportsArithmetic<*> {
        return when (other) {
            is IntValue -> IntValue(value + other.value)
            is DoubleValue -> DoubleValue(value + other.value)
            else -> throw IncompatibleTypesException("+", listOf(this, other))
        }
    }

    override fun subtract(other: SupportsArithmetic<*>): SupportsArithmetic<*> {
        return when (other) {
            is IntValue -> IntValue(value - other.value)
            is DoubleValue -> DoubleValue(value - other.value)
            else -> throw IncompatibleTypesException("-", listOf(this, other))
        }
    }

    override fun multiplyBy(other: SupportsArithmetic<*>): SupportsArithmetic<*> {
        return when (other) {
            is IntValue -> IntValue(value * other.value)
            is DoubleValue -> DoubleValue(value * other.value)
            is StringValue -> StringValue(other.value.repeat(value))
            else -> throw IncompatibleTypesException("*", listOf(this, other))
        }
    }

    override fun divideBy(other: SupportsArithmetic<*>): SupportsArithmetic<*> {
        return when (other) {
            is IntValue -> IntValue(value / other.value)
            is DoubleValue -> DoubleValue(value / other.value)
            else -> throw IncompatibleTypesException("/", listOf(this, other))
        }
    }

    override fun changeSign(): SupportsArithmetic<*> {
        return IntValue(-this.value)
    }

    override fun compareTo(other: SupportsComparison<*>): IntValue {
        return when (other) {
            is IntValue -> IntValue(value.compareTo(other.value))
            is DoubleValue -> IntValue(value.compareTo(other.value))
            else -> throw IncompatibleTypesException("comparison", listOf(this, other))
        }
    }
}

class DoubleValue(
    value: Double
) : SupportsArithmetic<Double>(value) {
    override fun toString(): String {
        return value.toString()
    }

    override fun add(other: SupportsArithmetic<*>): SupportsArithmetic<*> {
        return when (other) {
            is IntValue -> DoubleValue(value + other.value)
            is DoubleValue -> DoubleValue(value + other.value)
            else -> throw IncompatibleTypesException("+", listOf(this, other))
        }
    }

    override fun subtract(other: SupportsArithmetic<*>): SupportsArithmetic<*> {
        return when (other) {
            is IntValue -> DoubleValue(value - other.value)
            is DoubleValue -> DoubleValue(value - other.value)
            else -> throw IncompatibleTypesException("-", listOf(this, other))
        }
    }

    override fun multiplyBy(other: SupportsArithmetic<*>): SupportsArithmetic<*> {
        return when (other) {
            is IntValue -> DoubleValue(value * other.value)
            is DoubleValue -> DoubleValue(value * other.value)
            else -> throw IncompatibleTypesException("*", listOf(this, other))
        }
    }

    override fun divideBy(other: SupportsArithmetic<*>): SupportsArithmetic<*> {
        return when (other) {
            is IntValue -> DoubleValue(value / other.value)
            is DoubleValue -> DoubleValue(value / other.value)
            else -> throw IncompatibleTypesException("/", listOf(this, other))
        }
    }

    override fun changeSign(): SupportsArithmetic<*> {
        return DoubleValue(-this.value)
    }

    override fun compareTo(other: SupportsComparison<*>): IntValue {
        return when (other) {
            is IntValue -> IntValue(value.compareTo(other.value))
            is DoubleValue -> IntValue(value.compareTo(other.value))
            else -> throw IncompatibleTypesException("comparison", listOf(this, other))
        }
    }
}

class StringValue(value: String) : SupportsArithmetic<String>(value) {
    override fun toString(): String {
        return value
    }

    override fun add(other: SupportsArithmetic<*>): SupportsArithmetic<*> {
        return when (other) {
            is StringValue -> StringValue(value + other.value)
            else -> throw IncompatibleTypesException("+", listOf(this, other))
        }
    }

    override fun subtract(other: SupportsArithmetic<*>): SupportsArithmetic<*> {
        throw IncompatibleTypesException("-", listOf(this, other))
    }

    override fun multiplyBy(other: SupportsArithmetic<*>): SupportsArithmetic<*> {
        return when (other) {
            is IntValue -> StringValue(value.repeat(other.value))
            else -> throw IncompatibleTypesException("*", listOf(this, other))
        }
    }

    override fun divideBy(other: SupportsArithmetic<*>): SupportsArithmetic<*> {
        throw IncompatibleTypesException("/", listOf(this, other))
    }

    override fun changeSign(): SupportsArithmetic<*> {
        throw IncompatibleTypesException("-", listOf(this))
    }

    override fun compareTo(other: SupportsComparison<*>): IntValue {
        return when (other) {
            is StringValue -> IntValue(value.compareTo(other.value))
            else -> throw IncompatibleTypesException("comparison", listOf(this, other))
        }
    }
}

class BoolValue(value: Boolean) : Value<Boolean>(value) {
    override fun toString(): String {
        return "BoolValue: $value"
    }
}

class CollectionValue(
    value: List<Value<*>>
) : Value<List<Value<*>>>(value) {
    fun toList(): List<Value<*>> {
        return value
    }

    fun size(): Int {
        return value.size
    }
}

class ArrayValue(
    val size: Int,
    value: MutableList<Value<*>>
) : Value<MutableList<Value<*>>>(value) {

    fun checkIndexInBounds(index: Int){
        if (index < 0 || index >= size) {
            throw IndexOutOfBoundsException(
                "Array index out of " +
                        "range: the size of the array is :$size, but " +
                        "the element index was $index"
            )
        }
    }

    fun set(index: Int, element: Value<*>) {
        checkIndexInBounds(index)
        value[index] = element
    }

    fun get(index: Int): Value<*> {
        checkIndexInBounds(index)
        return value[index]
    }

    override fun toString(): String {
        val stringRepresentation: StringBuilder = StringBuilder()
        for (element in value) {
            stringRepresentation.append(element.toString()).append(", ")
        }
        return "Array Value: Size $size, Elements: [${
            stringRepresentation.toString().trimEnd().trimEnd(',')
        }]"
    }

    fun toCollectionValue(): CollectionValue {
        return CollectionValue(value.toList())
    }
}

abstract class CallableValue<T>(
    val parametersDeclarations: List<String> = listOf(),
    value: T
) : Value<T>(value) {
    abstract fun call(parametersValues: List<IOperation> = listOf()): Value<*>
}

class FunctionValue(
    parametersDeclarations: List<String>,
    value: BlockStatement
) : CallableValue<BlockStatement>(parametersDeclarations, value) {
    override fun call(parametersValues: List<IOperation>): Value<*> {
        if (parametersDeclarations.size != parametersValues.size) {
            throw InvalidParametersAmountException(
                parametersDeclarations.size,
                parametersValues.size
            )
        }
        for (i in 0..parametersDeclarations.size - 1) {
            val parameterValue = ValueOperation(parametersValues[i].evaluate())
            value.addStatement(
                i,
                VariableAssignmentStatement(parametersDeclarations[i], parameterValue)
            )
        }
        try {
            value.evaluate()
            return NullValue()
        } catch (e: ReturnException) {
            return e.returnValue
        }
    }
}