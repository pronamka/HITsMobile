package com.example.hit.language.parser

import com.example.hit.language.parser.exceptions.IncompatibleTypesException
import com.example.hit.language.parser.exceptions.InvalidOperationException
import com.example.hit.language.parser.exceptions.InvalidParametersAmountException
import com.example.hit.language.parser.exceptions.ReturnException
import com.example.hit.language.parser.exceptions.UnexpectedTypeException
import com.example.hit.language.parser.operations.IOperation
import com.example.hit.language.parser.operations.ValueOperation
import kotlin.reflect.KClass

abstract class Value<T>(
    val value: T
) {
    open fun asString(): String {
        return value.toString()
    }

    open fun callMethod(methodName: String, parameters: List<IOperation>): Value<*> {
        throw InvalidOperationException(
            "Error when calling method $methodName of ${this::class.java.simpleName}:" +
                    "method does not exist."
        )
    }
}

class NullValue : Value<Any>(0)

class Variable(
    val type: VariableType,
    value: IOperation? = null,
) : Value<IOperation?>(value) {
    fun toValue(): Value<*> {
        if (type is VariableType.ARRAY) {
            return ValueOperationFactory(
                ArrayToken(
                    type.size,
                    type.elementType,
                    value
                )
            ).create().evaluate()
        }
        if (value == null) {
            throw InvalidOperationException("Cannot initialize a variable with an empty value.")
        }
        val variableValue: Value<*> = value.evaluate()
        if (TypesManager.valueTypeCorresponds(type, variableValue)) {
            return variableValue
        }
        if (type is VariableType.DOUBLE && variableValue is IntValue) {
            return DoubleValue(variableValue.value.toDouble())
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

    override fun callMethod(methodName: String, parameters: List<IOperation>): Value<*> {
        return when (methodName) {
            "toString" -> StringValue(value.toString())
            else -> super.callMethod(methodName, parameters)
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

    override fun callMethod(methodName: String, parameters: List<IOperation>): Value<*> {
        return when (methodName) {
            "toString" -> StringValue(value.toString())
            else -> super.callMethod(methodName, parameters)
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

    override fun callMethod(methodName: String, parameters: List<IOperation>): Value<*> {
        return when (methodName) {
            "length" -> IntValue(value.length)
            "toInt" -> {
                val newValue = value.toIntOrNull()
                if (newValue == null) {
                    throw InvalidOperationException("Cannot convert string $value to Int.")
                }
                return IntValue(newValue)
            }

            "toDouble" -> {
                val newValue = value.toDoubleOrNull()
                if (newValue == null) {
                    throw InvalidOperationException("Cannot convert string $value to Int.")
                }
                return DoubleValue(newValue)
            }

            else -> super.callMethod(methodName, parameters)
        }
    }
}

class BoolValue(value: Boolean) : Value<Boolean>(value) {
    override fun toString(): String {
        return "BoolValue: $value"
    }

    override fun callMethod(methodName: String, parameters: List<IOperation>): Value<*> {
        return when (methodName) {
            "toString" -> StringValue(value.toString())
            else -> super.callMethod(methodName, parameters)
        }
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

    private fun getElementsString(): String {
        val stringRepresentation: StringBuilder = StringBuilder()
        for (element in value) {
            if (element == null) {
                stringRepresentation.append("null").append(", ")
                continue
            }
            stringRepresentation.append(element.toString()).append(", ")
        }
        return "[${stringRepresentation.toString().trimEnd().trimEnd(',')}]"
    }

    override fun toString(): String {

        return "Array Value: Size $size, Elements: ${getElementsString()}"
    }

    override fun asString(): String {
        return getElementsString()
    }

    fun toCollectionValue(): CollectionValue {
        return CollectionValue(value.toList())
    }

    override fun callMethod(methodName: String, parameters: List<IOperation>): Value<*> {
        return when (methodName) {
            "size" -> IntValue(value.size)
            "toString" -> StringValue(getElementsString())
            else -> super.callMethod(methodName, parameters)
        }
    }
}

abstract class CallableValue<T>(
    val parametersDeclarations: List<DeclarationStatement> = listOf(),
    value: T,
    val returnType: VariableType
) : Value<T>(value) {
    abstract fun call(parametersValues: List<IOperation> = listOf()): Value<*>
}

class FunctionValue(
    parametersDeclarations: List<DeclarationStatement>,
    value: BlockStatement,
    returnType: VariableType
) : CallableValue<BlockStatement>(parametersDeclarations, value, returnType) {
    override fun call(parametersValues: List<IOperation>): Value<*> {
        if (parametersDeclarations.size != parametersValues.size) {
            throw InvalidParametersAmountException(
                parametersDeclarations.size,
                parametersValues.size
            )
        }
        for (i in 0..parametersDeclarations.size - 1) {
            val parameterValue = ValueOperation(parametersValues[i].evaluate())
            parametersDeclarations[i].variableValue = parameterValue
            value.addStatement(i, parametersDeclarations[i])
        }
        try {
            value.evaluate()
            if (returnType !is VariableType.NULL) {
                throw UnexpectedTypeException(
                    "The function returned null, but ${returnType::class.java.simpleName} was expected."
                )
            }
            return NullValue()
        } catch (e: ReturnException) {
            if (!TypesManager.valueTypeCorresponds(returnType, e.returnValue)) {
                throw UnexpectedTypeException(
                    "The function returned a value of type ${e.returnValue::class.java.simpleName}," +
                            "but a value of type $returnType was expected."
                )
            }
            return e.returnValue
        }
    }
}