package com.mono9rome.typst_note_app.parser

import arrow.core.toNonEmptyListOrNull
import com.mono9rome.typst_note_app.model.ContentBlock

/**
 * 試作。状態遷移機械としてのパーサー
 * */
class BlockParseMachine {

    fun run(source: String): List<ContentBlock> {
        val lines = source.lines().toNonEmptyListOrNull() ?: return emptyList()
        val state = BlockParserState.new(lines)
        return with(state) {
            parseBlocks(0)
        }
    }

    class Machine(private var lines: List<String>) {

        sealed interface State {
            data object Block : State
            data object List : State
            data object Paragraph : State
        }

        private var currentLineIndex = 0
        private var currentMachineState = State.Block

        fun start(): List<ContentBlock> {
            while (currentLineIndex < lines.size) {
                val line = lines[currentLineIndex]

                if (shouldSkip()) {
                    currentLineIndex++
                    continue
                }

                parseLine()

                changeStateIfNecessary()
            }
        }

        fun shouldSkip(): Boolean {
            when (currentMachineState) {
                is State.Block -> {
                    lines[currentLineIndex].isBlank()
                }
            }
        }

        fun parseLine() {}

        fun changeStateIfNecessary() {}

    }
}