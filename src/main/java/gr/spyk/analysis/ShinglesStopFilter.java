package gr.spyk.analysis;

/**
 * Created by Spyros on 20/6/2015.
 */

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;

import java.io.IOException;

/**
 * Removes any stop word(s) at the end of the 'shingled' token
 */
public class ShinglesStopFilter extends TokenFilter {

    private final CharArraySet stopwords;

    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);

    private int minTokenLength = 3;

    private char tokenSeparator = '_';

    public ShinglesStopFilter(TokenStream input, CharArraySet stopwords, String tokenSeparator) {
        super(input);
        this.stopwords = stopwords;
        this.tokenSeparator = tokenSeparator.toCharArray()[0];
    }


    @Override
    public final boolean incrementToken() throws IOException {

        if (input.incrementToken()) {

            char[] termBuffer = termAtt.buffer();

            int lastShingleSeparator;
            while ((lastShingleSeparator = lastIndexOf(termBuffer, tokenSeparator, termAtt.length())) != -1 &&
                    lastShingleSeparator > minTokenLength) {

                if (this.stopwords.contains(termBuffer, lastShingleSeparator + 1,
                        termAtt.length() - lastShingleSeparator - 1)) {
                    termAtt.copyBuffer(termBuffer, 0, lastShingleSeparator);
                } else {
                    break;
                }
            }
            return true;
        }

        return false;

    }


    private static int lastIndexOf(char[] array, char c, int arrayLength) {
        if (arrayLength > array.length) {
            arrayLength = array.length;
        }
        for (int i = arrayLength - 1; i >= 0; --i) {
            if (c == array[i]) {
                return i;
            }
        }

        return -1;
    }
}
