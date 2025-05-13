package com.example.hit.language.parser

enum class TokenType {
    INT,
    DOUBLE,
    STRING,
    BOOL,
    TRUE,
    FALSE,

    PLUS,
    MINUS,
    ASTERISK,
    SLASH,

    WORD,
    EQUALS,

    LEFT_PARENTHESIS,
    RIGHT_PARENTHESIS,
    LEFT_BRACKET,
    RIGHT_BRACKET,
    COMMA,

    ARRAY,

    EOF;
}