package com.example.hit.blocks.container

import com.example.hit.blocks.BasicBlock
import com.example.hit.blocks.ForBlock
import com.example.hit.blocks.FunctionBlock
import com.example.hit.blocks.IfElseBlock
import com.example.hit.blocks.WhileBlock

class Container(private val blocks : MutableList<BasicBlock>) {

    fun isValidBlockArrangement() : Boolean {
        if (blocks.size == 0) {
            return true
        }
        val roots = blocks.filter { it.topConnection == null }
        if (roots.size != 1) {
            return false
        }

        val orderedBlocks = mutableListOf<BasicBlock>()
        var current: BasicBlock? = roots.first()

        while (current != null) {
            orderedBlocks.add(current)
            if (current is IfElseBlock) {
                for ((_, bodyBlock) in current.blocksInput) {
                    if (!bodyBlock.isValidBlockArrangement()) {
                        return false
                    }
                }
            }
            if (current is ForBlock) {
                val bodyBlock = current.blocks
                if (!bodyBlock.isValidBlockArrangement()) {
                    return false
                }
            }
            if (current is WhileBlock) {
                val bodyBlock = current.blocks
                if (!bodyBlock.isValidBlockArrangement()) {
                    return false
                }
            }
            if (current is FunctionBlock) {
                val bodyBlock = current.blocks
                if (!bodyBlock.isValidBlockArrangement()) {
                    return false
                }
            }
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