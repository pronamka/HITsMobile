package com.example.hit.language.parser

import com.example.hit.language.parser.exceptions.ContinueIterationException
import com.example.hit.language.parser.exceptions.StopIterationException
import com.example.hit.language.parser.exceptions.UnexpectedTypeException
import com.example.hit.language.parser.operations.ComparisonOperation
import com.example.hit.language.parser.operations.IOperation
import com.example.hit.language.parser.operations.ReturnOperation

interface IStatement {
    fun evaluate()
}

class DeclarationStatement(
    val variableType: VariableType,
    val variableName: String,
    var variableValue: IOperation? = null
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

class FunctionDeclarationStatement(
    val name: String,
    val parameters: List<DeclarationStatement> = listOf(),
    val body: BlockStatement
): IStatement{
    override fun evaluate() {
        if (Scopes.getLast().exists(name)) {
            throw IllegalStateException("Function $name has already been declared.")
        }
        val function = FunctionValue(parameters, body)
        Scopes.getLast().add(name, function)
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
    var outputValue: String? = null
    override fun evaluate() {
        outputValue = toPrint.evaluate().toString()
        println(outputValue)
    }

    override fun toString(): String {
        return "Print statement: Printing $toPrint"
    }
}

class ReturnStatement(
    val returnOperation: ReturnOperation
): IStatement{
    var returnValue: IOperation? = null
    override fun evaluate() {
        returnValue = returnOperation
    }
}

class BlockStatement(
    val statements: MutableList<IStatement>
) : IStatement {
    var outputValue: IOperation? = null
    override fun evaluate() {
        Scopes.add(VariablesRepository())
        for (statement in statements) {
            if (statement is ReturnStatement){
                statement.evaluate()
                outputValue = statement.returnValue
                return
            }
            statement.evaluate()
        }

        Scopes.removeLast()
    }

    fun addStatement(index: Int = 0, statement: IStatement){
        statements.add(index, statement)
    }
}

class IfElseStatement(
    val blocks: List<Pair<IOperation, BlockStatement>>,
    val defaultBlock: BlockStatement? = null,
) : IStatement {
    override fun evaluate() {
        for ((condition, block) in blocks) {
            val conditionValue = condition.evaluate()
            if (conditionValue !is BoolValue){
                throw UnexpectedTypeException("Expected a BoolValue, but got ${conditionValue::class.java.simpleName}")
            }
            if (conditionValue.value) {
                block.evaluate()
                return
            }
        }
        defaultBlock?.evaluate()
    }
}

abstract class Loop(
    val condition: IOperation,
    val block: BlockStatement,
): IStatement{
    fun checkCondition(): Boolean{
        val value = condition.evaluate()
        if (value !is BoolValue){
            throw UnexpectedTypeException("")
        }
        return value.value
    }
}

class WhileLoop(
    condition: IOperation,
    block: BlockStatement
) : Loop(condition, block) {
    override fun evaluate() {
        while (checkCondition()) {
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
    condition: IOperation,
    val stateChange: IStatement,
    block: BlockStatement
) : Loop(condition, block) {
    override fun evaluate() {
        initializer.evaluate()
        while (checkCondition()) {
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