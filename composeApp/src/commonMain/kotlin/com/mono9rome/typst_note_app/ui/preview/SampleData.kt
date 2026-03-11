package com.mono9rome.typst_note_app.ui.preview

import com.mono9rome.typst_note_app.model.*

class SampleData {
    companion object {

        val source = """
            This is a test data.
            - This is a test item 1.
            - This is a test item 2:
              + This is a numbered item 1.
              + This is a numbered item 2.
            We can therefore ...
        """.trimIndent().let(Note::Source)

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
        const val TEXT_SIZE_SP = 15f
    }
}