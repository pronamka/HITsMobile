package com.example.hit.codeRunner
import androidx.compose.runtime.snapshots.Snapshot
import com.example.hit.blocks.BasicBlock
import com.example.hit.language.parser.IStatement

class CodeRunner(private val blocks : List<BasicBlock>, private val console: MutableList<String>) {
    fun run(){
        console.clear()
        console.add("Program execution started...")
        try {
            val statements = mutableListOf<IStatement>()
            for (block in blocks) {
                statements.add(block.execute())
            }
            for (statement in statements) {
                statement.evaluate()
            }
            console.add("Program execution completed successfully!")
        } catch (e: Exception) {
            console.add("Error: ${e.message}")
        }
    }
}
