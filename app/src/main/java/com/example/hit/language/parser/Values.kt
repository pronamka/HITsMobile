package com.example.hit.language.parser

import com.example.hit.language.parser.exceptions.IncompatibleTypesException
import com.example.hit.language.parser.operations.IOperation

interface IValue {}

abstract class Value<T>(
    val value: T
)

class Variable(
    val type: VariableType,
    value: IOperation? = null,
) : Value<IOperation?>(value) {
    fun toValue(): Value<*> {
        val variableValue = value!!.evaluate()
        val variableValueString = variableValue.value.toString()
        return when (type) {
            is VariableType.INT -> IntValue(variableValueString.toInt())
            is VariableType.DOUBLE -> DoubleValue(variableValueString.toDouble())
            is VariableType.STRING -> StringValue(variableValueString)
            is VariableType.BOOL -> {
                return when (variableValueString) {
                    "true" -> BoolValue(true)
                    "false" -> BoolValue(false)
                    else -> throw IllegalArgumentException(
                        "Cannot initialize a variable of " +
                                "type BoolValue with value $variableValueString"
                    )
                }
            }

            is VariableType.ARRAY -> ArrayValueFactory(
                type.size,
                type.elementType,
                variableValueString
            ).create()
        }
    }

    override fun toString(): String {
        return "Variable type $type and value $value"
    }
}

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

class ArrayValue<T : Value<*>>(
    val size: Int,
    value: MutableList<T>
) : Value<MutableList<T>>(value) {
    fun set(index: Int, element: T) {
        value[index] = element
    }

    fun get(index: Int): T {
        if (index < 0 || index >= size) {
            throw IndexOutOfBoundsException(
                "Array index out of " +
                        "range: the size of the array is :$size, but " +
                        "the element index was $index"
            )
        }
        return value[index]
    }

    override fun toString(): String {
        val stringRepresentation: StringBuilder = StringBuilder()
        for (element in value) {
            stringRepresentation.append(element.toString()).append(", ")
        }
        return "Array Value: Size $size, Elements: [${stringRepresentation.toString().trimEnd().trimEnd(',')}]"
    }
}