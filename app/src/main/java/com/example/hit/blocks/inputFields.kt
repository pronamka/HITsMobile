package com.example.hit.blocks

import com.example.hit.blocks.exeptions.InvalidNameException
import com.example.hit.blocks.exeptions.InvalidTypeException
import com.example.hit.blocks.exeptions.NullInputFieldException
import com.example.hit.language.parser.Lexer
import com.example.hit.language.parser.Parser
import com.example.hit.language.parser.VariableType
import com.example.hit.language.parser.operations.IOperation
import androidx.compose.runtime.mutableStateOf


class StringInputField(initialValue: String = "") {
    private var _value = mutableStateOf(initialValue)

    fun set(newValue: String) {
        _value.value = newValue
    }

    fun get(): String {
        return _value.value
    }
}


class NameInputField{
    private val inputFiled: StringInputField = StringInputField()

    private fun isValidName(name: String): Boolean {
        if (name.isEmpty()) return false
        val firstChar = name[0]
        if (firstChar != '_' && firstChar != '$' && !firstChar.isLetter()) {
            return false
        }

        for (char in name.substring(1)) {
            if (char != '_' && !char.isLetterOrDigit()) {
                return false
            }
        }

        val keywords = setOf(
            "as", "break", "class", "continue", "do", "else", "false", "for",
            "fun", "if", "in", "interface", "is", "null", "object", "package",
            "return", "super", "this", "throw", "true", "try", "typealias",
            "val", "var", "when", "while"
        )
        return name !in keywords
    }
    fun setName(newName : String) {
        inputFiled.set(newName)
    }

    fun getInputField(): String {
        return inputFiled.get()
    }

    fun getName(): String {
        val name = inputFiled.get()
        if (!isValidName(name)) {
            throw InvalidNameException(name)
        }
        return name
    }
}

class TypeInputField{
    private val inputFiled: StringInputField = StringInputField()

    private val stringToTypeMap = mapOf(
        "Int" to VariableType.INT,
        "String" to VariableType.STRING,
        "Bool" to VariableType.BOOL,
        "Double" to VariableType.DOUBLE
    )

    fun setType(newType : String) {
        inputFiled.set(newType)
    }

    fun getInputField(): String {
        return inputFiled.get()
    }

    fun getType(): VariableType {
        val type = inputFiled.get()
        if (!stringToTypeMap.containsKey(type)) {
            throw InvalidTypeException(type)
        }
        return stringToTypeMap[type]!!
    }
}

class OperationInputField{
    private val inputFiled: StringInputField = StringInputField()

    fun setOperation(newOperation : String) {
        inputFiled.set(newOperation)
    }

    fun getInputField(): String {
        return inputFiled.get()
    }

    fun getOperation(): IOperation {
        val s = inputFiled.get()
        return Parser(Lexer(s).tokenize()).parse()[0]
    }
}