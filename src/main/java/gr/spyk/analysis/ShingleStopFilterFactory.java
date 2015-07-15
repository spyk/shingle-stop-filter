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

    private CharArraySet prefixes;
    private final String prefixFiles;

    private CharArraySet suffixes;
    private final String suffixFiles;

    private final boolean ignoreCase;
    private String resourceFormat;
    private final String tokenSeparator;

    protected ShingleStopFilterFactory(Map<String, String> args) {
        super(args);

        prefixFiles = get(args, "prefixes");
        suffixFiles = get(args, "suffixes");

        resourceFormat = get(args, "resourceFormat", FORMAT_WORDSET);
        ignoreCase = getBoolean(args, "ignoreCase", false);
        tokenSeparator = get(args, "tokenSeparator", "");

        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    @Override
    public void inform(ResourceLoader resourceLoader) throws IOException {
        if (FORMAT_WORDSET.equalsIgnoreCase(resourceFormat)) {
            prefixes = getWordSet(resourceLoader, prefixFiles, ignoreCase);
            suffixes = getWordSet(resourceLoader, suffixFiles, ignoreCase);
        } else if (FORMAT_SNOWBALL.equalsIgnoreCase(resourceFormat)) {
            prefixes = getSnowballWordSet(resourceLoader, prefixFiles, ignoreCase);
            suffixes = getSnowballWordSet(resourceLoader, suffixFiles, ignoreCase);
        } else {
            throw new IllegalArgumentException("Unknown 'format' specified for 'prefixes' file: " + resourceFormat);
        }
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        return new ShinglesStopFilter(tokenStream, suffixes, tokenSeparator);
    }
}
