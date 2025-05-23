package com.example.hit.language.parser

import com.example.hit.language.parser.operations.IOperation

sealed class VariableType{
    companion object{
        val classMap = mapOf(
            VariableType.INT to IntValue::class,
            DOUBLE to DoubleValue::class,
            STRING to StringValue::class,
            BOOL to BoolValue::class
        )
    }

    object INT: VariableType()
    object DOUBLE: VariableType()
    object STRING: VariableType()
    object BOOL: VariableType()

    class ARRAY(
        val elementType: VariableType,
        val size: IOperation? = null
    ): VariableType()

    override fun toString(): String {
        return this::class.java.simpleName
    }
}