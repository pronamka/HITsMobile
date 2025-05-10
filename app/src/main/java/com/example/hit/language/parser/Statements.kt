package com.example.hit.language.parser

import com.example.hit.language.parser.operations.IOperation

class DeclarationStatement(
    val variableType: VariableType,
    val variableName: String,
    val variableValue: IOperation? = null
): IOperation{
    override fun evaluate(): Value<*> {
        if (VariablesRepository.exists(variableName)){
            throw IllegalStateException("Variable $variableName has already been declared.")
        }
        val variable = Variable(variableType, variableValue)
        val value: Value<*>
        if (variableValue!=null){
            value = variable.toValue()
        }
        else{
            value = variable
        }
        VariablesRepository.add(variableName, value)
        return value
    }
}

class AssignmentStatement(
    val variableName: String,
    val variableValue: IOperation
): IOperation{
    override fun evaluate(): Value<*> {
        if (!VariablesRepository.exists(variableName)){
            throw IllegalStateException("Variable $variableName is not declared.")
        }
        val variable = VariablesRepository.get(variableName)
        val value: Value<*>
        if (variable is Variable){
            value = Variable(variable.type, variableValue).toValue()
        }
        else{
            value = variable
        }
        VariablesRepository.add(variableName, value)
        return value
    }
}

