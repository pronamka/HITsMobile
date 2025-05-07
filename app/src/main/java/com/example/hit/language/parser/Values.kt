package com.example.hit.language.parser

import com.example.hit.language.parser.exceptions.IncompatibleTypesException

interface IValue{}
abstract class Value<T>(
    val value: T
){
    abstract fun add(other: Value<*>): Value<*>
    abstract fun subtract(other: Value<*>): Value<*>
    abstract fun multiplyBy(other: Value<*>): Value<*>
    abstract fun divideBy(other: Value<*>): Value<*>
}
class IntValue(
    value: Int
) : Value<Int>(value){
    override fun toString(): String {
        return value.toString()
    }

    override fun add(other: Value<*>): Value<*> {
        return when(other){
            is IntValue -> IntValue(value+other.value)
            is DoubleValue -> DoubleValue(value+other.value)
            else -> throw IncompatibleTypesException("+", listOf(this, other))
        }
    }

    override fun subtract(other: Value<*>): Value<*> {
        return when(other){
            is IntValue -> IntValue(value-other.value)
            is DoubleValue -> DoubleValue(value-other.value)
            else -> throw IncompatibleTypesException("-", listOf(this, other))
        }
    }

    override fun multiplyBy(other: Value<*>): Value<*> {
        return when(other){
            is IntValue -> IntValue(value*other.value)
            is DoubleValue -> DoubleValue(value*other.value)
            is StringValue -> StringValue(other.value.repeat(value))
            else -> throw IncompatibleTypesException("*", listOf(this, other))
        }
    }

    override fun divideBy(other: Value<*>): Value<*> {
        return when(other){
            is IntValue -> IntValue(value/other.value)
            is DoubleValue -> DoubleValue(value/other.value)
            else -> throw IncompatibleTypesException("/", listOf(this, other))
        }
    }
}

class DoubleValue(
    value: Double
): Value<Double>(value) {
    override fun toString(): String {
        return value.toString()
    }

    override fun add(other: Value<*>): Value<*> {
        return when(other){
            is IntValue -> DoubleValue(value+other.value)
            is DoubleValue -> DoubleValue(value+other.value)
            else -> throw IncompatibleTypesException("+", listOf(this, other))
        }
    }

    override fun subtract(other: Value<*>): Value<*> {
        return when(other){
            is IntValue -> DoubleValue(value-other.value)
            is DoubleValue -> DoubleValue(value-other.value)
            else -> throw IncompatibleTypesException("-", listOf(this, other))
        }
    }

    override fun multiplyBy(other: Value<*>): Value<*> {
        return when(other){
            is IntValue -> DoubleValue(value*other.value)
            is DoubleValue -> DoubleValue(value*other.value)
            else -> throw IncompatibleTypesException("*", listOf(this, other))
        }
    }

    override fun divideBy(other: Value<*>): Value<*> {
        return when(other){
            is IntValue -> DoubleValue(value/other.value)
            is DoubleValue -> DoubleValue(value/other.value)
            else -> throw IncompatibleTypesException("/", listOf(this, other))
        }
    }
}

class StringValue(value: String) : Value<String>(value) {
    override fun toString(): String {
        return value
    }

    override fun add(other: Value<*>): Value<*> {
        return when(other){
            is StringValue -> StringValue(value+other.value)
            else -> throw IncompatibleTypesException("+", listOf(this, other))
        }
    }

    override fun subtract(other: Value<*>): Value<*> {
        throw IncompatibleTypesException("-", listOf(this, other))
    }

    override fun multiplyBy(other: Value<*>): Value<*> {
        return when(other){
            is IntValue -> StringValue(value.repeat(other.value))
            else -> throw IncompatibleTypesException("*", listOf(this, other))
        }
    }

    override fun divideBy(other: Value<*>): Value<*> {
        throw IncompatibleTypesException("/", listOf(this, other))
    }
}

