package com.example.hit.blocks

import com.example.hit.blocks.exeptions.InvalidNameException
import com.example.hit.blocks.exeptions.InvalidTypeException
import com.example.hit.blocks.exeptions.NullInputFieldException
import com.example.hit.language.parser.Lexer
import com.example.hit.language.parser.Parser
import com.example.hit.language.parser.VariableType
import com.example.hit.language.parser.operations.IOperation
import androidx.compose.runtime.mutableStateOf
import com.example.hit.language.parser.AssignmentStatement
import com.example.hit.language.parser.DeclarationStatement
import com.example.hit.language.parser.OperationsParser
import com.example.hit.language.parser.StatementsParser


class StringInputField(initialValue: String = "") {
    private var _value = mutableStateOf(initialValue)

    fun set(newValue: String) {
        _value.value = newValue
    }

    fun get(): String {
        return _value.value
    }
}

abstract class BasicInputField {
    val inputFiled: StringInputField = StringInputField()

    fun set(newName : String) {
        inputFiled.set(newName)
    }

    fun getInputField(): String {
        return inputFiled.get()
    }
}


class NameInputField : BasicInputField() {
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

    fun getName(): String {
        val name = inputFiled.get()
        if (!isValidName(name)) {
            throw InvalidNameException(name)
        }
        return name
    }
}

class TypeInputField : BasicInputField(){
    fun getType(): VariableType {
        val s = inputFiled.get()
        return StatementsParser(Lexer(s).tokenize()).parseType()
    }
}

class OperationInputField : BasicInputField() {
    fun getOperation(): IOperation {
        val s = inputFiled.get()
        return OperationsParser(Lexer(s).tokenize()).parse()[0]
    }
}

class DeclarationStatementInputField : BasicInputField(){
    fun getDeclarationStatement(): DeclarationStatement {
        val s = inputFiled.get()
        return StatementsParser(Lexer(s).tokenize()).parseDeclaration()
    }
}

class AssignmentStatementInputField : BasicInputField(){
    fun getAssignmentStatement(): AssignmentStatement {
        val s = inputFiled.get()
        return StatementsParser(Lexer(s).tokenize()).parseAssignment()
    }
}

class FunctionParametersInputField : BasicInputField(){
    fun getFunctionParametersInputField(): List<DeclarationStatement> {
        val s = inputFiled.get()
        return StatementsParser(Lexer(s).tokenize()).parseFunctionParameters()
    }
}