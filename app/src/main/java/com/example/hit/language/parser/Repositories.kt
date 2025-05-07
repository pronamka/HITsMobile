package com.example.hit.language.parser

interface IRepository {
    val elements: MutableMap<String, Value<*>>

    fun add(key: String, value: Value<*>)

    fun get(key: String): Value<*>

    fun exists(key: String): Boolean
}

abstract class Repository : IRepository {
    override val elements: MutableMap<String, Value<*>> = mutableMapOf()

    override fun add(key: String, value: Value<*>){
        elements[key] = value
    }

    override fun get(key: String): Value<*> {
        if (!elements.containsKey(key)) {
            throw IllegalArgumentException("Element $key is not present in the repository.")
        }
        return elements[key]!!
    }

    override fun exists(key: String): Boolean {
        return elements.containsKey(key)
    }
}

object VariablesRepository: Repository(){
    init{
        elements["PI"] = DoubleValue(Math.PI)
    }
}