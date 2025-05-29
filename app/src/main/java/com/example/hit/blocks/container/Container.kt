package com.example.hit.blocks.container

import com.example.hit.blocks.BasicBlock

class Container(private val blocks : MutableList<BasicBlock>) {

    fun isValidBlockArrangement() : Boolean {
        val roots = blocks.filter { it.topConnection == null }
        if (roots.size != 1) {
            return false
        }

        val orderedBlocks = mutableListOf<BasicBlock>()
        var current: BasicBlock? = roots.first()

        while (current != null) {
            orderedBlocks.add(current)
            current = current.bottomConnection
        }

        if (orderedBlocks.size != blocks.size) {
            return false
        }
        return true
    }

    fun getOrderedBlocks(): List<BasicBlock> {
        if (blocks.isEmpty()) return emptyList()

        val roots = blocks.filter { it.topConnection == null }

        val orderedBlocks = mutableListOf<BasicBlock>()
        var current: BasicBlock? = roots.first()

        while (current != null) {
            orderedBlocks.add(current)
            current = current.bottomConnection
        }

        return orderedBlocks
    }
}