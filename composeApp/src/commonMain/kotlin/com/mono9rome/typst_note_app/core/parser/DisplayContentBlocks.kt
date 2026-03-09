package com.mono9rome.typst_note_app.core.parser

import com.mono9rome.typst_note_app.model.*

fun printContentBlocks(blocks: List<ContentBlock>, indent: String = "") {
    for (block in blocks) {
        when (block) {
            is Paragraph -> {
                println("${indent}[Paragraph]")
                block.content.forEach { inline ->
                    println(
                        "$indent  [" + when (inline) {
                            is PlainNode -> "Plain"
                            is BoldNode -> "Bold"
                        } + "]"
                    )
                    inline.content.forEach { inlineElement ->
                        when (inlineElement) {
                            is PlainText -> println(
                                "$indent  Text: \"${
                                    inlineElement.content.replace(
                                        "\n",
                                        "\\n"
                                    )
                                }\""
                            )

                            is InlineMath -> println("$indent  InlineMath: \"(...)\"")
                            is LinkToNote -> println("$indent  LinkToNote: ${inlineElement.noteId.value}")
                        }
                    }
                }
            }

            is BulletList -> {
                println("${indent}[BulletList]")
                block.items.forEachIndexed { i, item ->
                    println("$indent  Item $i:")
                    printContentBlocks(item.blocks, "$indent    ")
                }
            }

            is NumberedList -> {
                println("${indent}[NumberedList]")
                block.items.forEachIndexed { i, item ->
                    println("$indent  Item $i:")
                    printContentBlocks(item.blocks, "$indent    ")
                }
            }

            is BlockMath -> {
                println("${indent}[BlockMath]")
                println("$indent  BlockMath: \"(...)\"")
            }

        }
    }
}