package com.example.hit.language.parser

import com.example.hit.language.parser.exceptions.ArrayInitializationException
import com.example.hit.language.parser.operations.IOperation
import kotlin.reflect.KClass

sealed class VariableType {

    object INT : VariableType()
    object DOUBLE : VariableType()
    object STRING : VariableType()
    object BOOL : VariableType()
    object NULL : VariableType()

    class ARRAY(
        val elementType: VariableType,
        val size: IOperation? = null
    ) : VariableType()

    override fun toString(): String {
        return this::class.java.simpleName
    }
}

object TypesManager {

    fun getCorrespondingValue(type: VariableType): KClass<out Value<*>> {
        return when (type) {
            is VariableType.INT -> IntValue::class
            is VariableType.DOUBLE -> DoubleValue::class
            is VariableType.STRING -> StringValue::class
            is VariableType.BOOL -> BoolValue::class
            is VariableType.NULL -> NullValue::class
            is VariableType.ARRAY -> ArrayValue::class
        }
    }

    fun checkElementTypes(
        type: VariableType,
        elements: List<Value<*>>,
        throwException: Boolean = false
    ): Boolean {
        val targetType = getCorrespondingValue(type)
        for (element in elements) {
            if (!targetType.isInstance(element)) {
                if (throwException) {
                    throw ArrayInitializationException(
                        "Failed to initialize array: Array element type was $type," +
                                "but the element $element has different type."
                    )
                }
                return false
            }
        }
        return true
    }

    fun valueTypeCorresponds(type: VariableType, value: Value<*>): Boolean {
        if (type is VariableType.ARRAY) {
            if (value !is ArrayValue<*>) {
                return false
            }
            return checkElementTypes(type.elementType, value.value.toList())
        }
        return getCorrespondingValue(type) == value::class
    }
}