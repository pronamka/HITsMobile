package com.example.hit.language.parser

import com.example.hit.language.getOperation
import com.example.hit.language.parser.exceptions.UnexpectedTypeException
import com.example.hit.language.parser.operations.CreateArrayOperation
import com.example.hit.language.parser.operations.GetLengthOperation
import com.example.hit.language.parser.operations.IOperation
import com.example.hit.language.parser.operations.ToStringOperation
import com.example.hit.language.parser.operations.ValueOperation
import com.example.hit.language.parser.operations.VariableOperation

interface IRepository {
    val elements: MutableMap<String, Value<*>>

    fun add(key: String, value: Value<*>)

    fun get(key: String): Value<*>

    fun exists(key: String): Boolean
}

class VariablesRepository : IRepository {
    override val elements: MutableMap<String, Value<*>> = mutableMapOf()

    init {
        elements["PI"] = DoubleValue(Math.PI)
        elements["E"] = DoubleValue(Math.E)
        elements["GOLDEN_RATIO"] = DoubleValue(1.618)
        elements["max"] = FunctionValue(
            listOf("a", "b"),
            BlockStatement(
                mutableListOf(
                    IfElseStatement(
                        listOf(
                            Pair(
                                getOperation("a>=b"),
                                BlockStatement(
                                    mutableListOf(
                                        ReturnStatement(
                                            getOperation("a")
                                        )
                                    )
                                )
                            )
                        ),
                        BlockStatement(
                            mutableListOf(
                                ReturnStatement(
                                    getOperation("b")
                                )
                            )
                        )
                    )
                ),
                isFunctionBody = true
            )
        )
        elements["array"] = FunctionValue(
            listOf("size"),
            BlockStatement(
                mutableListOf(
                    ReturnStatement(
                        CreateArrayOperation(getOperation("size"))
                    )
                ),
                isFunctionBody = true
            )
        )
        elements["len"] = FunctionValue(
            listOf("arg"),
            BlockStatement(
                mutableListOf(
                    ReturnStatement(
                        GetLengthOperation(getOperation("arg"))
                    )
                ),
                isFunctionBody = true
            )
        )
        elements["toString"] = FunctionValue(
            listOf("arg"),
            BlockStatement(
                mutableListOf(
                    ReturnStatement(ToStringOperation(getOperation("arg")))
                ),
                isFunctionBody = true
            )
        )
    }

    override fun add(key: String, value: Value<*>) {
        elements[key] = value
    }

    override fun get(key: String): Value<*> {
        if (!elements.containsKey(key)) {
            throw IllegalArgumentException("Element $key is not present in the repository.")
        }
        return elements[key]!!
    }

    fun getValue(key: String): Value<*> {
        val value = get(key)
        return value
    }

    override fun exists(key: String): Boolean {
        return elements.containsKey(key)
    }

    fun copyState(): VariablesRepository {
        val state = VariablesRepository()
        state.elements.clear()
        state.elements.putAll(this.elements)
        return state
    }
}

object Scopes {
    val repositories: MutableList<VariablesRepository> = mutableListOf()
    var currentFunctionScope = repositories

    init {
        repositories.add(VariablesRepository())
    }

    fun add(repository: VariablesRepository) = repositories.add(repository)

    fun createNewScope(forFunction: Boolean = false) {
        if (!forFunction) {
            add(repositories.last().copyState())
            return
        }
        add(repositories.first().copyState())
    }

    fun remove(index: Int) = repositories.removeAt(index)

    fun removeLast() = repositories.removeAt(repositories.size - 1)

    fun getLast() = repositories[repositories.size - 1]

    fun get(index: Int) = repositories[index]

    fun addVariable(name: String, value: Value<*>) {
        repositories.last().add(name, value)
    }

    fun getVariable(name: String): Value<*> {
        if (repositories.last().exists(name)) {
            return repositories.last().get(name)
        }
        throw IllegalStateException("Variable $name is not declared.")
    }

    fun variableExists(name: String): Boolean {
        return repositories.last().exists(name)
    }

    fun reset() {
        repositories.clear()
        repositories.add(VariablesRepository())
    }
}