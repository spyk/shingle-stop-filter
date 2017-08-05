# Description

A token filter that removes any trailing stopwords from shingled tokens. (see https://cwiki.apache.org/confluence/display/solr/Filter+Descriptions#FilterDescriptions-ShingleFilter for more details)

The main motivation is to provide better autocomplete functionality when using a suggester that uses shingled tokens by removing any trailing stopwords, as they usually don't provide any extra value and can be left out. It should usually be combined with a duplicate token removal filter. Please see below for installation instructions & a sample Solr configuration for autocomplete.

**Note:** The project depends on Lucene/Solr v.5.1.0, but has also been tested and works with v.4.10.x.

# Installation

- Clone project from github
- Build using maven: `mvn clean install`
- Solr: copy `target/shingle-stop-filter-1.0-SNAPSHOT.jar` to Solr's dist folder

# Parameters

The filter configuration parameters. Most are similar to the original StopFilter, see https://cwiki.apache.org/confluence/display/solr/Filter+Descriptions#FilterDescriptions-StopFilter for more details.
- **stopwords (required):** The path to the stopwords file(s)
- **resourceFormat(optional):** The stopwords resource format (normal or snowball)
- **ignoreCase(optional):** Ignore stopwords case
- **tokenSeparator (optional, default " "):** Should be the token separator used in the ShingleFilter, a single space by default.

# Sample Autocomplete Solr Configuration
A sample autocomplete configuration for Solr

# schema.xml

```xml

<field name="suggest" type="textSuggest" multiValued="true" />

<fieldType name="textSuggest" class="solr.TextField" positionIncrementGap="100" >
  <analyzer type="index">
    <tokenizer class="solr.WhitespaceTokenizerFactory"/>
    <filter class="solr.ApostropheFilterFactory"/>
    <filter class="solr.LowerCaseFilterFactory"/>
    <filter class="solr.WordDelimiterFilterFactory" />
    <filter class="solr.ShingleFilterFactory" maxShingleSize="5" outputUnigrams="true" />
    <filter class="gr.spyk.analysis.ShingleStopFilterFactory" stopwords="lang/stopwords_ff.txt" tokenSeparator=" " />
    <filter class="solr.StopFilterFactory" words="lang/stopwords_en.txt" ignoreCase="true" />
    <filter class="solr.RemoveDuplicatesTokenFilterFactory"/>
  </analyzer>
  <analyzer type="query">
    <tokenizer class="solr.KeywordTokenizerFactory"/>
    <filter class="solr.LowerCaseFilterFactory"/>
  </analyzer>
</fieldType>
```

# solrconfig.xml

```xml
<searchComponent name="spellSuggest" class="solr.SpellCheckComponent">
    <lst name="spellchecker">
      <str name="name">suggest</str>
      <str name="classname">org.apache.solr.spelling.suggest.Suggester</str>
      <str name="lookupImpl">org.apache.solr.spelling.suggest.tst.TSTLookup</str>
      <str name="field">suggest</str>
      <float name="threshold">0.001</float>
      <str name="buildOnCommit">true</str>
    </lst>
    <str name="queryAnalyzerFieldType">textSuggest</str>
</searchComponent>

<requestHandler class="org.apache.solr.handler.component.SearchHandler" name="/spellSuggest">
    <lst name="defaults">
        <str name="spellcheck">true</str>
        <str name="spellcheck.dictionary">suggest</str>
        <str name="spellcheck.onlyMorePopular">true</str>
        <str name="spellcheck.count">5</str>
        <str name="spellcheck.collate">false</str>
        <str name="spellcheck.maxCollations">5</str>
    </lst>
    <arr name="components">
        <str>spellSuggest</str>
    </arr>
</requestHandler>
```

# License

Copyright 2015 Spyros Kapnissis

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

