package com.example.hit.language.parser

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
    val size: Int,
    val elementType: VariableType,
    val value: CollectionValue
): Token(TokenType.ARRAY, "")