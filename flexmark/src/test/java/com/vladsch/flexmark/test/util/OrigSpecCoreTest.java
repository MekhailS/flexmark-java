package com.vladsch.flexmark.test.util;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.test.spec.SpecExample;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class OrigSpecCoreTest extends FullSpecTestCase {
    static private final DataHolder OPTIONS = new MutableDataSet()
            .set(HtmlRenderer.PERCENT_ENCODE_URLS, true)
            .set(TestUtils.NO_FILE_EOL, false)
            .toImmutable();

    final private @Nullable DataHolder myDefaultOptions;

    public OrigSpecCoreTest(@Nullable DataHolder defaultOptions) {
        myDefaultOptions = combineOptions(OPTIONS, defaultOptions);
    }

    @NotNull
    @Override
    final public SpecExample getExample() {
        return SpecExample.NULL;
    }

    @Override
    final public @Nullable DataHolder options(String option) {
        return null;
    }

    @Override
    final public @NotNull SpecExampleRenderer getSpecExampleRenderer(@NotNull SpecExample example, @Nullable DataHolder exampleOptions) {
        DataHolder combineOptions = combineOptions(myDefaultOptions, exampleOptions);
        return new FlexmarkSpecExampleRenderer(example, combineOptions, Parser.builder(combineOptions).build(), HtmlRenderer.builder(combineOptions).build(), false);
    }
}