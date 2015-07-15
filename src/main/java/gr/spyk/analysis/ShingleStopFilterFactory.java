package gr.spyk.analysis;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.ResourceLoader;
import org.apache.lucene.analysis.util.ResourceLoaderAware;
import org.apache.lucene.analysis.util.TokenFilterFactory;

import java.io.IOException;
import java.util.Map;

import static org.apache.lucene.analysis.core.StopFilterFactory.FORMAT_SNOWBALL;
import static org.apache.lucene.analysis.core.StopFilterFactory.FORMAT_WORDSET;

/**
 * Created by Spyros on 20/6/2015.
 */
public class ShingleStopFilterFactory extends TokenFilterFactory implements ResourceLoaderAware {

    private CharArraySet stopwords;
    private final String stopwordFiles;

    private final boolean ignoreCase;
    private String resourceFormat;
    private final String tokenSeparator;

    public ShingleStopFilterFactory(Map<String, String> args) {
        super(args);

        stopwordFiles = get(args, "stopwords");
        resourceFormat = get(args, "resourceFormat", FORMAT_WORDSET);
        ignoreCase = getBoolean(args, "ignoreCase", false);
        tokenSeparator = get(args, "tokenSeparator", " ");

        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    @Override
    public void inform(ResourceLoader resourceLoader) throws IOException {
        if (FORMAT_WORDSET.equalsIgnoreCase(resourceFormat)) {
            stopwords = getWordSet(resourceLoader, stopwordFiles, ignoreCase);
        } else if (FORMAT_SNOWBALL.equalsIgnoreCase(resourceFormat)) {
            stopwords = getSnowballWordSet(resourceLoader, stopwordFiles, ignoreCase);
        } else {
            throw new IllegalArgumentException("Unknown 'format' specified for 'prefixes' file: " + resourceFormat);
        }
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        return new ShinglesStopFilter(tokenStream, stopwords, tokenSeparator);
    }
}
