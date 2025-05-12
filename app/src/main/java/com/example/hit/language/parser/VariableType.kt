package com.example.hit.language.parser

sealed class VariableType{
    object INT: VariableType()
    object DOUBLE: VariableType()
    object STRING: VariableType()
    object BOOL: VariableType()

    class ARRAY(
        val elementType: VariableType,
        val size: Int
    ): VariableType()
}