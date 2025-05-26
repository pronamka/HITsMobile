package com.example.hit.blocks.container

import com.example.hit.blocks.BasicBlock

class Container(private val blocks : MutableList<BasicBlock>) {

    fun getOrderedBlocks(): List<BasicBlock> {
        if (blocks.isEmpty()) return emptyList()

        // Find root (exactly one block with no top connection)
        val roots = blocks.filter { it.topConnection == null }
        if (roots.size != 1) {
            throw IllegalStateException("Incorrect block arrangement")
        }

        // Traverse the chain
        val orderedBlocks = mutableListOf<BasicBlock>()
        var current: BasicBlock? = roots.first()

        while (current != null) {
            orderedBlocks.add(current)
            current = current.bottomConnection
        }

        // Verify all blocks were visited
        if (orderedBlocks.size != blocks.size) {
            throw IllegalStateException("Incorrect block arrangement")
        }

        return orderedBlocks
    }
}