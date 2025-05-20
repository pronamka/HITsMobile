package com.example.hit.blocks.container

import com.example.hit.blocks.BasicBlock

class Container {
    private val blocks = mutableListOf<BasicBlock>()

    fun addBlock(block : BasicBlock) {
        blocks.add(block)
    }

    fun getOrderedBlocks() : List<BasicBlock> {
        return blocks.sortedBy { it.logicalPosition }
    }
}