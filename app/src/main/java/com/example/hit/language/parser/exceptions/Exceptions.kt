package com.example.hit.language.parser.exceptions

class NumberParseException(
    errorIndex: Int,
) : Exception("Conversion to number failed: Invalid syntax at index ${errorIndex}.")


class IncompatibleTypesException(
    operationName: String,
    elements: List<Any>
): Exception("Unsupported operand classes for operation $operationName: " +
        elements.joinToString(", ") { it::class.simpleName!! })