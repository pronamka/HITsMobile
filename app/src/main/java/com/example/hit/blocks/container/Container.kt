package com.example.hit.blocks.container

import androidx.compose.runtime.mutableStateListOf
import com.example.hit.blocks.BasicBlock

class Container {
    val blocks = mutableStateListOf<BasicBlock>()

    fun addBlock(block : BasicBlock) {
        blocks.add(block)
    }

    fun getOrderedBlocks() : List<BasicBlock> {
        return blocks.sortedBy { it.logicalPosition }
    }
}