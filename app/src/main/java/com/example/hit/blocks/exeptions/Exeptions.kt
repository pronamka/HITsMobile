package com.example.hit.blocks.exeptions

class NullInputFieldException(
) : Exception("input field parsing failed: Null input field.")

class InvalidNameException(
    invalidName: String,
) : Exception("Name recognition failed: Invalid name ${invalidName}.")

class InvalidTypeException(
    invalidType: String,
) : Exception("Type recognition failed: Invalid type ${invalidType}.")