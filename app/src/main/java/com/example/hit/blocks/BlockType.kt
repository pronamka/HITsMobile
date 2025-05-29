package com.example.hit.blocks

enum class BlockType(val value: String) {

    INITIALIZATION("initialization"),

    DECLARATION("declaration"),

    ASSIGNMENT("assignment"),

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