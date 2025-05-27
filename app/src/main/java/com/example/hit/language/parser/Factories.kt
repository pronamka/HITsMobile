package com.example.hit.language.parser

import com.example.hit.language.parser.exceptions.ArrayInitializationException
import com.example.hit.language.parser.exceptions.UnexpectedTypeException
import com.example.hit.language.parser.operations.ValueOperation

class ArrayFactory(
    val token: ArrayToken
) {

    fun getArraySize(): Int {
        val arraySize = token.size!!.evaluate()
        if (arraySize !is IntValue) {
            throw UnexpectedTypeException(
                "The size of an array must be an Int value, " +
                        "but got ${arraySize::class.java.simpleName}"
            )
        }
        return arraySize.value
    }

    fun create(): Value<*> {
        val desiredClass = TypesManager.getCorrespondingValue(token.elementType)
        if (token.value == null) {
            if (token.size == null) {
                throw ArrayInitializationException(
                    "You must specify the size of the array."
                )
            }
            return ArrayValue(getArraySize(), desiredClass)
        }

        var arrayValue = token.value.evaluate()
        if (arrayValue is ArrayValue<*>) {
            arrayValue = arrayValue.toCollectionValue()
        }

        if (arrayValue !is CollectionValue) {
            throw IllegalArgumentException(
                "Array can only be initialized with an array expression."
            )
        }

        val elements: MutableList<Value<*>> = arrayValue.toList().toMutableList()
        TypesManager.checkElementTypes(token.elementType, elements, true)

        val arraySize = if (token.size == null) {
            elements.size
        } else {
            getArraySize()
        }
        return ArrayValue(arraySize, desiredClass, elements)
    }
}

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
                ArrayFactory(token).create()
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