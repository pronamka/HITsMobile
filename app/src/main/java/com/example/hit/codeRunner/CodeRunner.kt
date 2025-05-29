package com.example.hit.codeRunner
import androidx.compose.runtime.snapshots.Snapshot
import com.example.hit.blocks.BasicBlock
import com.example.hit.blocks.container.Container
import com.example.hit.language.parser.IStatement
import com.example.hit.language.parser.PrintStatement
import com.example.hit.language.parser.Scopes

class CodeRunner(private val container: Container, private val console: MutableList<String>) {
    fun run() {
        console.clear()
        console.add("Program execution started...")
        try {
            val blocks = container.getOrderedBlocks()
            val statements = mutableListOf<IStatement>()
            for (block in blocks) {
                statements.add(block.execute())
            }
            for (statement in statements) {
                statement.evaluate()
                if (statement is PrintStatement){
                    console.add(statement.outputValue!!)
                }
            }
            console.add("Program execution completed successfully!")
            Scopes.reset()
        } catch (e: Exception) {
            console.add("Error: ${e.message}")
        }
    }
}
