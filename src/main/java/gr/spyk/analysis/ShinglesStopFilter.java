package gr.spyk.analysis;

/**
 * Created by Spyros on 20/6/2015.
 */

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.CharArraySet;

import java.io.IOException;

/**
 * Removes any stop word(s) at the end of the 'shingled' token
 */
public class ShinglesStopFilter extends TokenFilter {

    private final CharArraySet stopwords;

    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);

    private int minTokenLength = 3;

    private char tokenSeparator = '_';

    private final boolean removeAtStart;

    public ShinglesStopFilter(TokenStream input, CharArraySet stopwords, String tokenSeparator, boolean removeAtStart) {
        super(input);
        this.stopwords = stopwords;
        this.tokenSeparator = tokenSeparator.toCharArray()[0];
        this.removeAtStart = removeAtStart;
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
            if (removeAtStart) {
                int firstShingleSeparator;
                while ((firstShingleSeparator = firstIndexOf(termBuffer, tokenSeparator, termAtt.length())) != -1) {

                    if (this.stopwords.contains(termBuffer, 0, firstShingleSeparator)) {
                        int cuttedLength = termAtt.length() - firstShingleSeparator - 1;
                        termAtt.copyBuffer(termBuffer, firstShingleSeparator + 1, cuttedLength < 0 ? 0 : cuttedLength);
                    } else {
                        break;
                    }
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

    private static int firstIndexOf(char[] array, char c, int arrayLength) {
        if (arrayLength > array.length) {
            arrayLength = array.length;
        }
        for (int i = 0; i <= arrayLength; i++) {
            if (c == array[i]) {
                return i;
            }
        }

        return -1;
    }
}
