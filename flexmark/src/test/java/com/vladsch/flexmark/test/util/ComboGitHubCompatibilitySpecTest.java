package com.vladsch.flexmark.test.util;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.parser.ParserEmulationProfile;
import com.vladsch.flexmark.test.spec.ResourceLocation;
import com.vladsch.flexmark.test.spec.SpecExample;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;
import org.jetbrains.annotations.NotNull;
import org.junit.runners.Parameterized;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

final public class ComboGitHubCompatibilitySpecTest extends CoreRendererSpecTest {
    private static final String SPEC_RESOURCE = "/core_gfm_doc_compatibility_spec.md";
    private static final DataHolder OPTIONS = new MutableDataSet()
            .setFrom(ParserEmulationProfile.GITHUB_DOC)

            //.set(Parser.THEMATIC_BREAK_RELAXED_START, true)
            .set(HtmlRenderer.INDENT_SIZE, 4)
            .set(HtmlRenderer.RENDER_HEADER_ID, true)
            .set(HtmlRenderer.SOFT_BREAK, " ")
            .toImmutable();

    private static final Map<String, DataHolder> optionsMap = new HashMap<>();
    static {
        optionsMap.put("no-loose-non-list-children", new MutableDataSet().set(Parser.LISTS_LOOSE_WHEN_HAS_NON_LIST_CHILDREN, false).set(Parser.LISTS_LOOSE_WHEN_BLANK_LINE_FOLLOWS_ITEM_PARAGRAPH, false));
    }
    public ComboGitHubCompatibilitySpecTest(@NotNull SpecExample example) {
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