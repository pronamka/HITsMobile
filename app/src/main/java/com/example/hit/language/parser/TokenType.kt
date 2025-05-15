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

    EQUAL,
    LESS,
    GREATER,
    LESS_OR_EQUAL,
    GREATER_OR_EQUAL,
    NOT_EQUAL,

    AND,
    OR,
    NOT,

    LEFT_PARENTHESIS,
    RIGHT_PARENTHESIS,
    LEFT_BRACKET,
    RIGHT_BRACKET,
    COMMA,

    RETURN,

    ARRAY,

    AMPERSAND,
    VERTICAL_BAR,

    EOF;
}