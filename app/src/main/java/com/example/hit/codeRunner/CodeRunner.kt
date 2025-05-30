package com.example.hit.codeRunner
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.ui.graphics.Color
import com.example.hit.blocks.BasicBlock
import com.example.hit.blocks.container.Container
import com.example.hit.language.parser.IStatement
import com.example.hit.language.parser.PrintStatement
import com.example.hit.language.parser.Scopes

class CodeRunner(private val container: Container, private val console: MutableList<String>) {
    fun run() {
        console.clear()
        console.add("Program execution started...")
        var errorIndex : Int? = null
        val blocks = container.getOrderedBlocks()
        val statements = mutableListOf<IStatement>()
        try {
            for (i in blocks.indices) {
                try{
                    statements.add(blocks[i].execute())
                }
                catch (e: Exception){
                    errorIndex = i
                    throw e
                }
            }
            for (i in blocks.indices) {
                try{
                    val statement = statements[i]
                    statement.evaluate()
                }
                catch (e: Exception) {
                    errorIndex = i
                    throw e
                }
            }
            console.add("Program execution completed successfully!")
        } catch (e: Exception) {
            blocks[errorIndex!!].color.value =  Color(0xFFFF0000)
            console.add("Error: ${e.message}")
        }
        finally{

            Scopes.reset()
        }
    }
}
