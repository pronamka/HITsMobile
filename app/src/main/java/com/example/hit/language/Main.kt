package com.example.hit.language

import com.example.hit.language.parser.Lexer
import com.example.hit.language.parser.Parser


class Main{
    fun main(){
        val input =
        """
            a = 2.5
            b = 10
            c = a+b
            c = a + c
            d = "some value here"
            t = d*b
        """.trimIndent()
        val tokens = Lexer(input).tokenize()
        for (token in tokens){
            println(token)
        }
        val operations = Parser(tokens).parse()
        for (operation in operations){
            println(operation)
        }
        for (operation in operations){
            println(operation.evaluate())
        }
    }
}

fun main(args: Array<String>){
    Main().main()
}