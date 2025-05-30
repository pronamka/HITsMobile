package com.example.hit.codeRunner
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.ui.graphics.Color
import com.example.hit.blocks.BasicBlock
import com.example.hit.blocks.container.Container
import com.example.hit.language.parser.IStatement
import com.example.hit.language.parser.PrintStatement
import com.example.hit.language.parser.Scopes
import com.example.hit.language.parser.exceptions.ContinueIterationException
import com.example.hit.language.parser.exceptions.ReturnException
import com.example.hit.language.parser.exceptions.StopIterationException

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
            if (e is StopIterationException || e is ContinueIterationException) {
                console.add("Error: cannot use outside loop body")
            } else if (e is ReturnException){
                console.add("Error: cannot use return outside function body")
            } else {
                console.add("Error: ${e.message}")
            }
        }
        finally{
            Scopes.reset()
        }
    }
}
