package com.example.hit.blocks

import com.example.hit.blocks.exeptions.InvalidNameException
import com.example.hit.blocks.exeptions.InvalidTypeException
import com.example.hit.language.parser.Lexer
import com.example.hit.language.parser.Parser
import com.example.hit.language.parser.operations.IOperation

class StringInputField{
    var value: String? = null
    fun get(): String{
        if (value == null){
            throw Exception("")
        }
        return value as String
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
        if (name in keywords) return false

        return true
    }

    fun getName(): String {
        val name = inputFiled.get()
        if (!isValidName(name)) {
            throw InvalidNameException(name)
        }
        return name
    }
}

class OperationInputField{
    private val inputFiled: StringInputField = StringInputField()

    fun getOperation(): IOperation {
        val s = inputFiled.get()
        return Parser(Lexer(s).tokenize()).parse()[0]
    }
}