package com.example.hit.blocks

enum class BlockType(val value: String) {

    VARIABLE_INITIALIZATION(""),

    VARIABLE_DECLARATION("variable_declaration"),
    ARRAY_DECLARATION("array_dec"),

    VARIABLE_ASSIGNMENT("variable_as"),
    ARRAY_ELEMENT_ASSIGNMENT("array_elem_as"),

    IF("if"),

    FOR("for"),
    WHILE("while"),

    RETURN("return"),

    PRINT("print"),
    BREAK("break"),
    CONTINUE("continue"),

    BLOCK("block"),
    FUNCTION("function");
}