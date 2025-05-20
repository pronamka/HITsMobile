package com.example.hit.blocks

enum class BlockType(val value: String) {

    VARIABLE_INITIALIZATION(""),

    VARIABLE_DECLARATION("variable_declaration"),
    ARRAY_DECLARATION("array_dec"),

    VARIABLE_ASSIGNMENT("variable_as"),
    ARRAY_ELEMENT_ASSIGNMENT("array_elem_as"),

    IF("if"),
    ELSE("else"),
    ELSE_IF("else if"),


    FOR("for"),
    WHILE("while"),

    PLUS("+"),
    MINUS("-"),
    DIVISION("/"),
    MULTY("*"),
    MOD("%"),

    AND("and"),
    OR("or"),
    NOT("not"),

    RETURN("return"),

    PRINT("print"),
    BREAK("break"),
    CONTINUE("continue"),

    BLOCK("block"),
    FUNCTION("function");
}