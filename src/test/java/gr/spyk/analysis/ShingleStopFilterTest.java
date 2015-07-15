package gr.spyk.analysis;

import org.apache.lucene.analysis.BaseTokenStreamTestCase;
import org.apache.lucene.analysis.MockTokenizer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.util.CharArraySet;

import java.io.IOException;
import java.io.StringReader;

/**
 * JUnit tests for ShingleStopFilter
 */
public class ShingleStopFilterTest extends BaseTokenStreamTestCase {

    CharArraySet stopwords = new CharArraySet(asSet("for", "the", "and", "a", "of", "on"), false);

    public void testPrefix() throws IOException {
        StringReader reader = new StringReader("test_and_of_for_the");

        final MockTokenizer in = new MockTokenizer(MockTokenizer.KEYWORD, false);
        in.setReader(reader);

        TokenStream stream = new ShinglesStopFilter(in, stopwords, "_");
        assertTokenStreamContents(stream, new String[]{"test"});
    }

    public void testStopAtSuffix() throws IOException {
        StringReader reader = new StringReader("the_test_and_of_trend_for_the");

        final MockTokenizer in = new MockTokenizer(MockTokenizer.KEYWORD, false);
        in.setReader(reader);

        TokenStream stream = new ShinglesStopFilter(in, stopwords, "_");
        assertTokenStreamContents(stream, new String[]{"the_test_and_of_trend"});
    }


}
