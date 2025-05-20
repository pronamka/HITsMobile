package com.example.hit.codeRunner
import com.example.hit.blocks.BasicBlock
import com.example.hit.language.parser.IStatement

class CodeRunner(private val blocks : List<BasicBlock>) {
    fun run(){
        val statements = mutableListOf<IStatement>()
        for (block in blocks) {
            statements.add(block.execute())
        }
    }
}

