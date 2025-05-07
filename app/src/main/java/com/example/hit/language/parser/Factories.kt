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
){
    fun create(): ValueOperation {
        return ValueOperation(ValueFactory(token).create())
    }
}