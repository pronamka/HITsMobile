package com.example.hit.language.parser

import com.example.hit.language.parser.operations.ConditionOperation
import com.example.hit.language.parser.operations.IOperation

interface IStatement {
    fun evaluate()
}

class DeclarationStatement(
    val variableType: VariableType,
    val variableName: String,
    val variableValue: IOperation? = null
) : IStatement {
    override fun evaluate() {
        if (Scopes.getLast().exists(variableName)) {
            throw IllegalStateException("Variable $variableName has already been declared.")
        }
        val variable = Variable(variableType, variableValue)
        val value: Value<*>
        if (variableValue != null) {
            value = variable.toValue()
        } else {
            value = variable
        }
        Scopes.getLast().add(variableName, value)
    }

    override fun toString(): String {
        var s =
            "Declaration Statement: Declaring variable of " +
                    "type $variableType with name $variableName"
        if (variableValue != null) {
            s += "and value $variableValue"
        }
        return s
    }
}

class AssignmentStatement(
    val variableName: String,
    val variableValue: IOperation
) : IStatement {
    override fun evaluate() {
        if (!Scopes.variableExists(variableName)) {
            throw IllegalStateException("Variable $variableName is not declared.")
        }
        val repository = Scopes.getRepositoryWithVariable(variableName)
        val variable = repository.get(variableName)

        val value: Value<*>
        if (variable is Variable) {
            value = Variable(variable.type, variableValue).toValue()
        } else {
            val newValue = variableValue.evaluate()
            if (newValue::class != variable::class &&
                !(newValue is IntValue && variable is DoubleValue)
            ) {
                throw IllegalArgumentException(
                    "Cannot assign a value of type ${newValue::class.simpleName} " +
                            "to a variable of class ${variable::class.simpleName}"
                )
            }
            value = newValue
        }
        repository.add(variableName, value)
    }

    override fun toString(): String {
        return "Assignment Statement: Assigning value $variableValue to $variableName."
    }
}

class PrintStatement(
    val toPrint: IOperation
) : IStatement {
    override fun evaluate() {
        println(toPrint.evaluate())
    }

    override fun toString(): String {
        return "Print statement: Printing $toPrint"
    }
}

class BlockStatement(
    val statements: List<IStatement>
) : IStatement {

    override fun evaluate() {
        Scopes.add(VariablesRepository())
        for (statement in statements) {
            statement.evaluate()
        }

        Scopes.removeLast()
    }
}

class IfElseStatement(
    val blocks: List<Pair<ConditionOperation, BlockStatement>>,
    val defaultBlock: BlockStatement? = null,
) : IStatement {
    override fun evaluate() {
        for ((condition, block) in blocks){
            if (condition.evaluate().value) {
                block.evaluate()
                return
            }
        }
        defaultBlock?.evaluate()
    }
}

class WhileLoop(
    val condition: ConditionOperation,
    val block: BlockStatement
): IStatement{
    override fun evaluate() {
        while(condition.evaluate().value){
            block.evaluate()
        }
    }
}

class ForLoop(
    val initializer: IStatement,
    val condition: ConditionOperation,
    val stateChange: IStatement,
    val block: BlockStatement
): IStatement{
    override fun evaluate() {
        initializer.evaluate()
        while(condition.evaluate().value){
            block.evaluate()
            stateChange.evaluate()
        }
    }
}