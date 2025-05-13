package com.example.hit.language.parser

import com.example.hit.language.parser.exceptions.ArrayInitializationException
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
                if (token.value.size() != token.size) {
                    throw ArrayInitializationException(
                        "Failed to initialize array: Array size was ${token.size} " +
                                "but the array expression contains ${token.value.size()} elements"
                    )
                }
                val elements: MutableList<Value<*>> = token.value.toList().toMutableList()
                val desiredClass = VariableType.classMap[token.elementType]!!
                for (element in elements) {
                    if (!desiredClass.isInstance(element)) {
                        throw ArrayInitializationException(
                            "Failed to initialize array: Array element type was ${desiredClass.java.simpleName}," +
                                    "but the element $element has different type."
                        )
                    }
                }
                return ArrayValue(token.size, desiredClass, elements)
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