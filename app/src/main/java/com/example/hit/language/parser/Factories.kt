package com.example.hit.language.parser

import com.example.hit.language.parser.operations.ValueOperation

class ValueFactory(
    val token: Token
) {
    fun create(): Value<*> {
        if (token.tokenTypeEquals(TokenType.INT)) {
            return IntValue(token.tokenValue.toInt())
        }
        if (token.tokenTypeEquals(TokenType.DOUBLE)) {
            return DoubleValue(token.tokenValue.toDouble())
        }
        if (token.tokenTypeEquals(TokenType.STRING)) {
            return StringValue(token.tokenValue)
        }
        throw NotImplementedError("Token of type ${token.tokenType} cannot be parsed into a value yet.")
    }
}

class ValueOperationFactory(
    val token: Token
) {
    fun create(): ValueOperation {
        return ValueOperation(ValueFactory(token).create())
    }
}

class ArrayValueFactory(
    val size: Int,
    val elementsType: VariableType,
    val values: String
) {
    fun create(): ArrayValue<*> {
        val elementsStrings = values.split(",")
        val elements: MutableList<Value<*>> = mutableListOf()

        for (elementString in elementsStrings) {
            val value =
                Variable(elementsType, Parser(Lexer(elementString).tokenize()).parse()[0]).toValue()
            elements.add(value)
        }
        return ArrayValue(size, elements)
    }
}