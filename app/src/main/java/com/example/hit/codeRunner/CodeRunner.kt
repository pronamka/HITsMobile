package com.example.hit.codeRunner
import com.example.hit.blocks.description.BasicBlock
import com.example.hit.language.parser.IStatement

class CodeRunner(private val blocks : List<BasicBlock>) {
    fun run(){
        consoleOutput
        consoleOutput.clear()
        consoleOutput.add("Program execution started...")
        val statements = mutableListOf<IStatement>()
        for (block in blocks) {
            statements.add(block.execute())
        }
        for (statement in statements) {
            statement.evaluate()
        }
    }
}


//    fun runProgram(){
//        val ourBlocks = listOf<BasicBlock>()
//        val statements = mutableListOf<IStatement>()
//
//        consoleOutput.clear()
//        consoleOutput.add("Program execution started...")
//
//        try {
//            for (ourBlock in ourBlocks) {
//                val statement = ourBlock.execute()
//                statements.add(statement)
//                consoleOutput.add("Executed: ${statement.toString()}")
//            }
//            consoleOutput.add("Program execution completed successfully!")
//        } catch (e: Exception) {
//            consoleOutput.add("Error: ${e.message}")
//        }
//    }
