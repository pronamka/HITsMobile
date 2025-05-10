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
        val variableValue = value!!.evaluate().value.toString()
        return when (type) {
            VariableType.INT -> IntValue(variableValue.toInt())
            VariableType.DOUBLE -> DoubleValue(variableValue.toDouble())
            VariableType.STRING -> StringValue(variableValue)
            else -> throw NotImplementedError("Variable with type $type cannot be created yet.")
        }
    }

    override fun toString(): String{
        return "Variable type $type and value $value"
    }
}

abstract class SupportsArithmetic<T>(
    value: T
) : Value<T>(value) {
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
}

class BoolValue(value: Boolean) : Value<Boolean>(value) {

}
