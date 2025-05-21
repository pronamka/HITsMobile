package com.example.hit.language.parser

import com.example.hit.language.parser.operations.IOperation

open class Token(
    val tokenType: TokenType,
    val tokenValue: String
){
    fun tokenTypeEquals(otherTokenType: TokenType): Boolean{
        return tokenType===otherTokenType
    }

    override fun toString(): String {
        return "Token: Type $tokenType; Value: $tokenValue;"
    }
}

class ArrayToken(
    val size: IOperation,
    val value: IOperation? = null
): Token(TokenType.ARRAY, "")