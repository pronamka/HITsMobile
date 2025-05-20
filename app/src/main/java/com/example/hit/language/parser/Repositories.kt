package com.example.hit.language.parser

interface IRepository {
    val elements: MutableMap<String, Value<*>>

    fun add(key: String, value: Value<*>)

    fun get(key: String): Value<*>

    fun exists(key: String): Boolean
}

class VariablesRepository : IRepository {
    override val elements: MutableMap<String, Value<*>> = mutableMapOf()

    init{
        elements["PI"] = DoubleValue(Math.PI)
        elements["E"] = DoubleValue(Math.E)
        elements["GOLDEN_RATIO"] = DoubleValue(1.618)
    }

    override fun add(key: String, value: Value<*>){
        elements[key] = value
    }

    override fun get(key: String): Value<*> {
        if (!elements.containsKey(key)) {
            throw IllegalArgumentException("Element $key is not present in the repository.")
        }
        return elements[key]!!
    }

    fun getValue(key: String): Value<*>{
        val value = get(key)
        if (value is Variable){
            throw IllegalStateException("Variable $key has not been initialized yet.")
        }
        return value
    }

    override fun exists(key: String): Boolean {
        return elements.containsKey(key)
    }

    fun copyState(): VariablesRepository{
        val state = VariablesRepository()
        state.elements.clear()
        state.elements.putAll(this.elements)
        return state
    }
}

object Scopes{
    val repositories: MutableList<VariablesRepository> = mutableListOf()
    var currentFunctionScope = repositories

    init{
        repositories.add(VariablesRepository())
    }

    fun add(repository: VariablesRepository) = repositories.add(repository)

    fun createNewScope(forFunction: Boolean = false){
        if (!forFunction){
            add(repositories.last().copyState())
            return
        }
        add(repositories.first().copyState())
    }

    fun remove(index: Int) = repositories.removeAt(index)

    fun removeLast() = repositories.removeAt(repositories.size - 1)

    fun getLast() = repositories[repositories.size - 1]

    fun get(index: Int) = repositories[index]

    fun addVariable(name: String, value: Value<*>){
        repositories.last().add(name, value)
    }

    fun getVariable(name: String): Value<*>{
        if (repositories.last().exists(name)){
            return repositories.last().get(name)
        }
        throw IllegalStateException("Variable $name is not declared.")
    }

    fun variableExists(name: String): Boolean{
        return repositories.last().exists(name)
    }

    fun reset(){
        repositories.clear()
        repositories.add(VariablesRepository())
    }
}