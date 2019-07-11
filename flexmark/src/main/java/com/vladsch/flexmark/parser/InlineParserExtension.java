package com.vladsch.flexmark.parser;

public interface InlineParserExtension {
    void finalizeDocument(final InlineParser inlineParser);
    void finalizeBlock(final InlineParser inlineParser);

    /**
     * Parse input
     *
     * @return true if character input was processed
     * @param inlineParser
     */
    boolean parse(LightInlineParser inlineParser);
}
