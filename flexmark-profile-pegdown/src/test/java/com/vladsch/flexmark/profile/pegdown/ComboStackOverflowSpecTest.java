package com.vladsch.flexmark.profile.pegdown;

import com.vladsch.flexmark.core.test.util.RendererSpecTest;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.PegdownExtensions;
import com.vladsch.flexmark.test.spec.ResourceLocation;
import com.vladsch.flexmark.test.spec.SpecExample;
import com.vladsch.flexmark.util.data.DataHolder;
import org.jetbrains.annotations.NotNull;
import org.junit.runners.Parameterized;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComboStackOverflowSpecTest extends RendererSpecTest {
    private static final String SPEC_RESOURCE = "/stack_overflow_spec.md";
    static final DataHolder OPTIONS = PegdownOptionsAdapter.flexmarkOptions((PegdownExtensions.ALL & ~PegdownExtensions.HARDWRAPS) | (PegdownExtensions.ALL_OPTIONALS & ~(PegdownExtensions.EXTANCHORLINKS | PegdownExtensions.EXTANCHORLINKS_WRAP))).toMutable()
            .set(HtmlRenderer.FENCED_CODE_LANGUAGE_CLASS_PREFIX, "")
            .set(HtmlRenderer.OBFUSCATE_EMAIL_RANDOM, false)
            .toImmutable();

    private static final Map<String, DataHolder> optionsMap = new HashMap<>();
    static {
        optionsMap.put("hard-breaks", PegdownOptionsAdapter.flexmarkOptions((PegdownExtensions.ALL & ~PegdownExtensions.HARDWRAPS) | (PegdownExtensions.ALL_OPTIONALS & ~(PegdownExtensions.EXTANCHORLINKS | PegdownExtensions.EXTANCHORLINKS_WRAP)) | PegdownExtensions.HARDWRAPS)/*.toMutable().remove(Parser.EXTENSIONS)*/);
        optionsMap.put("anchor-links", PegdownOptionsAdapter.flexmarkOptions((PegdownExtensions.ALL & ~PegdownExtensions.HARDWRAPS) | (PegdownExtensions.ALL_OPTIONALS & ~(PegdownExtensions.EXTANCHORLINKS | PegdownExtensions.EXTANCHORLINKS_WRAP)) | PegdownExtensions.ANCHORLINKS)/*.toMutable().remove(Parser.EXTENSIONS)*/);
    }
    public ComboStackOverflowSpecTest(@NotNull SpecExample example) {
        super(example, optionsMap, OPTIONS);
    }

    @Parameterized.Parameters(name = "{0}")
    public static List<Object[]> data() {
        return getTestData(SPEC_RESOURCE);
    }

    @Override
    public @NotNull ResourceLocation getSpecResourceLocation() {
        return ResourceLocation.of(SPEC_RESOURCE);
    }
}