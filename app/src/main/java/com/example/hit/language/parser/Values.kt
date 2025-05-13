package com.example.hit.language.parser

import com.example.hit.language.parser.exceptions.IncompatibleTypesException
import com.example.hit.language.parser.exceptions.InvalidOperationException
import com.example.hit.language.parser.exceptions.UnexpectedTypeException
import com.example.hit.language.parser.operations.IOperation
import kotlin.reflect.KClass

interface IValue {}

abstract class Value<T>(
    val value: T
)

class Variable(
    val type: VariableType,
    value: IOperation? = null,
) : Value<IOperation?>(value) {
    fun toValue(): Value<*> {
        if (type is VariableType.ARRAY && value == null) {
            return ArrayValue(type.size, VariableType.classMap[type.elementType]!!)
        }
        if (value == null) {
            throw InvalidOperationException("Cannot initialize a variable with an empty value.")
        }
        val variableValue: Value<*> = value.evaluate()
        if (type is VariableType.ARRAY) {
            if (variableValue !is CollectionValue) {
                throw IllegalArgumentException("Array can only be initialized with an array expression.")
            }
            return ValueOperationFactory(
                ArrayToken(
                    type.size,
                    type.elementType,
                    variableValue
                )
            ).create().evaluate()
        }
        val desiredType = VariableType.classMap[type]!!
        if (desiredType.isInstance(variableValue)) {
            return variableValue
        }
        throw UnexpectedTypeException(
            "Cannot assign a value of type " +
                    "${variableValue::class.java.simpleName} to a " +
                    "variable of type ${type::class.java.simpleName}"
        )
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

class ArrayValue<T : Value<*>>(
    val size: Int,
    val elementType: KClass<out T>,
    initialValue: MutableList<T>? = null
) : Value<Array<T>>(createEmptyArray(size, elementType)) {

    companion object {
        private fun <T : Value<*>> createEmptyArray(
            size: Int,
            elementType: KClass<out T>
        ): Array<T> {
            @Suppress("UNCHECKED_CAST")
            return java.lang.reflect.Array.newInstance(elementType.java, size) as Array<T>
        }
    }

    init {
        if (initialValue != null) {
            for (i in 0..size - 1) {
                value[i] = initialValue[i]
            }
        }
    }

    fun set(index: Int, element: Value<*>) {
        if (!elementType.isInstance(element)) {
            throw UnexpectedTypeException(
                "Expected type was ${elementType.java.simpleName}, " +
                        "but got an element $element"
            )
        }
        @Suppress("UNCHECKED_CAST")
        value[index] = element as T
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
            if (element == null) {
                stringRepresentation.append("null").append(", ")
                continue
            }
            stringRepresentation.append(element.toString()).append(", ")
        }
        return "Array Value: Size $size, Elements: [${
            stringRepresentation.toString().trimEnd().trimEnd(',')
        }]"
    }
}