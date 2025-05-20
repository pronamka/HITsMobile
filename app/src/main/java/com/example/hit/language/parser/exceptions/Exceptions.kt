package com.example.hit.language.parser.exceptions

import com.example.hit.language.parser.Value

class NumberParseException(
    errorIndex: Int,
) : Exception("Conversion to number failed: Invalid syntax at index ${errorIndex}.")


class IncompatibleTypesException(
    operationName: String,
    elements: List<Any>
) : Exception(
    "Unsupported operand classes for operation $operationName: " +
            elements.joinToString(", ") { it::class.simpleName!! })


class ArrayInitializationException(
    message: String
) : Exception(message)

class UnexpectedTypeException(
    message: String
) : Exception(message)

class InvalidOperationException(message: String) : Exception(message)

class InvalidParametersAmountException(
    expectedAmount: Int,
    actualAmount: Int
) : Exception(
    "Invalid amount of parameters: expected $expectedAmount but got $actualAmount"
)

class StopIterationException() : Exception()

class ContinueIterationException() : Exception()

class ReturnException(
    val returnValue: Value<*>
): Exception()