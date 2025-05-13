package com.example.hit.language.parser

import com.example.hit.language.parser.exceptions.ContinueIterationException
import com.example.hit.language.parser.exceptions.StopIterationException
import com.example.hit.language.parser.exceptions.UnexpectedTypeException
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
        if (variableValue != null || variableType is VariableType.ARRAY) {
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
            s += " and value $variableValue"
        }
        return s
    }
}

abstract class AssignmentStatement(
    val variableName: String,
    val variableValue: IOperation
) : IStatement {
    fun checkIfVariableDeclared() {
        if (!Scopes.variableExists(variableName)) {
            throw IllegalStateException("Variable $variableName is not declared.")
        }
    }
}

class VariableAssignmentStatement(
    variableName: String,
    variableValue: IOperation
) : AssignmentStatement(variableName, variableValue) {
    override fun evaluate() {
        checkIfVariableDeclared()
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

class ArrayElementAssignmentStatement(
    variableName: String,
    variableValue: IOperation,
    val indexValue: IOperation,
) : AssignmentStatement(variableName, variableValue) {
    override fun evaluate() {
        checkIfVariableDeclared()
        val repository = Scopes.getRepositoryWithVariable(variableName)
        val variable = repository.get(variableName)
        if (variable !is ArrayValue<*>) {
            throw RuntimeException("$variable is not an array.")
        }
        val index = indexValue.evaluate()
        if (index !is IntValue){
            throw UnexpectedTypeException("Array indices can only be an integer, but $index was given.")
        }

        variable.set(index.value, variableValue.evaluate())
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
        for ((condition, block) in blocks) {
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
) : IStatement {
    override fun evaluate() {
        while (condition.evaluate().value) {
            try {
                block.evaluate()
            } catch (e: StopIterationException) {
                break
            } catch (e: ContinueIterationException) {
                continue
            }
        }
    }
}

class ForLoop(
    val initializer: IStatement,
    val condition: ConditionOperation,
    val stateChange: IStatement,
    val block: BlockStatement
) : IStatement {
    override fun evaluate() {
        initializer.evaluate()
        while (condition.evaluate().value) {
            try {
                block.evaluate()
            } catch (e: StopIterationException) {
                break
            } catch (e: ContinueIterationException) {
                continue
            } finally {
                stateChange.evaluate()
            }
        }
    }
}

class BreakStatement : IStatement {
    override fun evaluate() {
        throw StopIterationException()
    }
}

class ContinueStatement : IStatement {
    override fun evaluate() {
        throw ContinueIterationException()
    }
}