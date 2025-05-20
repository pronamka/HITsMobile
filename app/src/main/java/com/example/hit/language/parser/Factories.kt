package com.example.hit.language.parser

import com.example.hit.language.parser.exceptions.ArrayInitializationException
import com.example.hit.language.parser.exceptions.UnexpectedTypeException
import com.example.hit.language.parser.operations.ValueOperation

class ValueFactory(
    val token: Token
) {
    fun create(): Value<*> {
        return when (token.tokenType) {
            TokenType.INT -> IntValue(token.tokenValue.toInt())
            TokenType.DOUBLE -> DoubleValue(token.tokenValue.toDouble())
            TokenType.STRING -> StringValue(token.tokenValue)
            TokenType.TRUE -> BoolValue(true)
            TokenType.FALSE -> BoolValue(false)
            TokenType.ARRAY -> {
                if (token !is ArrayToken) {
                    throw ArrayInitializationException(
                        "Failed to initialize array: Array can only " +
                                "be initialized with an array expression."
                    )
                }
                val arraySize = token.size.evaluate()
                if (arraySize !is IntValue) {
                    throw UnexpectedTypeException(
                        "The size of an array must be an Int value, " +
                                "but got ${arraySize::class.java.simpleName}"
                    )
                }

                val desiredClass = VariableType.classMap[token.elementType]!!
                if (token.value == null) {
                    return ArrayValue(arraySize.value, desiredClass)
                }
                var arrayValue = token.value.evaluate()
                if (arrayValue is ArrayValue<*>) {
                    arrayValue = arrayValue.toCollectionValue()
                }
                if (arrayValue !is CollectionValue) {
                    throw IllegalArgumentException("Array can only be initialized with an array expression.")
                }
                val elements: MutableList<Value<*>> = arrayValue.toList().toMutableList()
                for (element in elements) {
                    if (!desiredClass.isInstance(element)) {
                        throw ArrayInitializationException(
                            "Failed to initialize array: Array element type was ${desiredClass.java.simpleName}," +
                                    "but the element $element has different type."
                        )
                    }
                }
                return ArrayValue(arraySize.value, desiredClass, elements)
            }

            else -> throw NotImplementedError("Token of type ${token.tokenType} cannot be parsed into a value.")
        }
    }
}

class ValueOperationFactory(
    val token: Token
) {
    fun create(): ValueOperation {
        return ValueOperation(ValueFactory(token).create())
    }
}