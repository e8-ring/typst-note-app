package com.mono9rome.typst_note_app.ui.preview

import com.mono9rome.typst_note_app.model.BulletList
import com.mono9rome.typst_note_app.model.ContentList
import com.mono9rome.typst_note_app.model.NumberedList
import com.mono9rome.typst_note_app.model.Paragraph
import com.mono9rome.typst_note_app.model.PlainNode
import com.mono9rome.typst_note_app.model.PlainText
import com.mono9rome.typst_note_app.ui.SourceCode

class SampleData {
    companion object {

        val sourceCode = """
            This is a test data.
            - This is a test item 1.
            - This is a test item 2:
              + This is a numbered item 1.
              + This is a numbered item 2.
            We can therefore ...
        """.trimIndent().let(::SourceCode)

        val contentBlocks = listOf(
            Paragraph(
                content = listOf(
                    PlainNode(
                        content = listOf(
                            PlainText("This is a test data.")
                        )
                    )
                )
            ),
            BulletList(
                items = listOf(
                    ContentList.Item(
                        blocks = listOf(
                            Paragraph(
                                content = listOf(
                                    PlainNode(
                                        content = listOf(
                                            PlainText("This is a test item 1.")
                                        )
                                    )
                                )
                            )
                        )
                    ),
                    ContentList.Item(
                        blocks = listOf(
                            Paragraph(
                                content = listOf(
                                    PlainNode(
                                        content = listOf(
                                            PlainText("This is a test item 2: lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.")
                                        )
                                    )
                                )
                            ),
                            NumberedList(
                                items = listOf(
                                    ContentList.Item(
                                        blocks = listOf(
                                            Paragraph(
                                                content = listOf(
                                                    PlainNode(
                                                        content = listOf(
                                                            PlainText("This is a numbered item 1.")
                                                        )
                                                    )
                                                )
                                            ),
                                            Paragraph(
                                                content = listOf(
                                                    PlainNode(
                                                        content = listOf(
                                                            PlainText("The second paragraph of a numbered item 1.")
                                                        )
                                                    )
                                                )
                                            )
                                        )
                                    ),
                                    ContentList.Item(
                                        blocks = listOf(
                                            Paragraph(
                                                content = listOf(
                                                    PlainNode(
                                                        content = listOf(
                                                            PlainText("This is a numbered item 2.")
                                                        )
                                                    )
                                                )
                                            )
                                        )
                                    )
                                )
                            )
                        )
                    ),
                )
            ),
            Paragraph(
                content = listOf(
                    PlainNode(
                        content = listOf(
                            PlainText("We can therefore ...")
                        )
                    )
                )
            )
        )

        // デフォルト : 18f
        val textSizeSp = 15f
    }
}