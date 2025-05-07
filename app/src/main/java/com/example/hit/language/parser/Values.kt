package com.example.hit.language.parser

import com.example.hit.language.parser.exceptions.IncompatibleTypesException

interface IValue{}

abstract class Value<T>(
    val value: T
)

abstract class SupportsArithmetic<T>(
    value: T
): Value<T>(value) {
    abstract fun add(other: SupportsArithmetic<*>): SupportsArithmetic<*>
    abstract fun subtract(other: SupportsArithmetic<*>): SupportsArithmetic<*>
    abstract fun multiplyBy(other: SupportsArithmetic<*>): SupportsArithmetic<*>
    abstract fun divideBy(other: SupportsArithmetic<*>): SupportsArithmetic<*>
    abstract fun changeSign(): SupportsArithmetic<*>
}


class IntValue(
    value: Int
) : SupportsArithmetic<Int>(value){
    override fun toString(): String {
        return value.toString()
    }

    override fun add(other: SupportsArithmetic<*>): SupportsArithmetic<*> {
        return when(other){
            is IntValue -> IntValue(value+other.value)
            is DoubleValue -> DoubleValue(value+other.value)
            else -> throw IncompatibleTypesException("+", listOf(this, other))
        }
    }

    override fun subtract(other: SupportsArithmetic<*>): SupportsArithmetic<*> {
        return when(other){
            is IntValue -> IntValue(value-other.value)
            is DoubleValue -> DoubleValue(value-other.value)
            else -> throw IncompatibleTypesException("-", listOf(this, other))
        }
    }

    override fun multiplyBy(other: SupportsArithmetic<*>): SupportsArithmetic<*> {
        return when(other){
            is IntValue -> IntValue(value*other.value)
            is DoubleValue -> DoubleValue(value*other.value)
            is StringValue -> StringValue(other.value.repeat(value))
            else -> throw IncompatibleTypesException("*", listOf(this, other))
        }
    }

    override fun divideBy(other: SupportsArithmetic<*>): SupportsArithmetic<*> {
        return when(other){
            is IntValue -> IntValue(value/other.value)
            is DoubleValue -> DoubleValue(value/other.value)
            else -> throw IncompatibleTypesException("/", listOf(this, other))
        }
    }

    override fun changeSign(): SupportsArithmetic<*> {
        return IntValue(-this.value)
    }
}

class DoubleValue(
    value: Double
): SupportsArithmetic<Double>(value) {
    override fun toString(): String {
        return value.toString()
    }

    override fun add(other: SupportsArithmetic<*>): SupportsArithmetic<*> {
        return when(other){
            is IntValue -> DoubleValue(value+other.value)
            is DoubleValue -> DoubleValue(value+other.value)
            else -> throw IncompatibleTypesException("+", listOf(this, other))
        }
    }

    override fun subtract(other: SupportsArithmetic<*>): SupportsArithmetic<*> {
        return when(other){
            is IntValue -> DoubleValue(value-other.value)
            is DoubleValue -> DoubleValue(value-other.value)
            else -> throw IncompatibleTypesException("-", listOf(this, other))
        }
    }

    override fun multiplyBy(other: SupportsArithmetic<*>): SupportsArithmetic<*> {
        return when(other){
            is IntValue -> DoubleValue(value*other.value)
            is DoubleValue -> DoubleValue(value*other.value)
            else -> throw IncompatibleTypesException("*", listOf(this, other))
        }
    }

    override fun divideBy(other: SupportsArithmetic<*>): SupportsArithmetic<*> {
        return when(other){
            is IntValue -> DoubleValue(value/other.value)
            is DoubleValue -> DoubleValue(value/other.value)
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
        return when(other){
            is StringValue -> StringValue(value+other.value)
            else -> throw IncompatibleTypesException("+", listOf(this, other))
        }
    }

    override fun subtract(other: SupportsArithmetic<*>): SupportsArithmetic<*> {
        throw IncompatibleTypesException("-", listOf(this, other))
    }

    override fun multiplyBy(other: SupportsArithmetic<*>): SupportsArithmetic<*> {
        return when(other){
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

class BoolValue(value: Boolean): Value<Boolean>(value){

}
