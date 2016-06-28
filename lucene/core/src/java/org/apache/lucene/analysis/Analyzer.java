begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|tokenattributes
operator|.
name|CharTermAttribute
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|tokenattributes
operator|.
name|OffsetAttribute
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|tokenattributes
operator|.
name|TermToBytesRefAttribute
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|AlreadyClosedException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|AttributeFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|BytesRef
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|CloseableThreadLocal
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Version
import|;
end_import

begin_comment
comment|/**  * An Analyzer builds TokenStreams, which analyze text.  It thus represents a  * policy for extracting index terms from text.  *<p>  * In order to define what analysis is done, subclasses must define their  * {@link TokenStreamComponents TokenStreamComponents} in {@link #createComponents(String)}.  * The components are then reused in each call to {@link #tokenStream(String, Reader)}.  *<p>  * Simple example:  *<pre class="prettyprint">  * Analyzer analyzer = new Analyzer() {  *  {@literal @Override}  *   protected TokenStreamComponents createComponents(String fieldName) {  *     Tokenizer source = new FooTokenizer(reader);  *     TokenStream filter = new FooFilter(source);  *     filter = new BarFilter(filter);  *     return new TokenStreamComponents(source, filter);  *   }  *   {@literal @Override}  *   protected TokenStream normalize(TokenStream in) {  *     // Assuming FooFilter is about normalization and BarFilter is about  *     // stemming, only FooFilter should be applied  *     return new FooFilter(in);  *   }  * };  *</pre>  * For more examples, see the {@link org.apache.lucene.analysis Analysis package documentation}.  *<p>  * For some concrete implementations bundled with Lucene, look in the analysis modules:  *<ul>  *<li><a href="{@docRoot}/../analyzers-common/overview-summary.html">Common</a>:  *       Analyzers for indexing content in different languages and domains.  *<li><a href="{@docRoot}/../analyzers-icu/overview-summary.html">ICU</a>:  *       Exposes functionality from ICU to Apache Lucene.   *<li><a href="{@docRoot}/../analyzers-kuromoji/overview-summary.html">Kuromoji</a>:  *       Morphological analyzer for Japanese text.  *<li><a href="{@docRoot}/../analyzers-morfologik/overview-summary.html">Morfologik</a>:  *       Dictionary-driven lemmatization for the Polish language.  *<li><a href="{@docRoot}/../analyzers-phonetic/overview-summary.html">Phonetic</a>:  *       Analysis for indexing phonetic signatures (for sounds-alike search).  *<li><a href="{@docRoot}/../analyzers-smartcn/overview-summary.html">Smart Chinese</a>:  *       Analyzer for Simplified Chinese, which indexes words.  *<li><a href="{@docRoot}/../analyzers-stempel/overview-summary.html">Stempel</a>:  *       Algorithmic Stemmer for the Polish Language.  *<li><a href="{@docRoot}/../analyzers-uima/overview-summary.html">UIMA</a>:   *       Analysis integration with Apache UIMA.   *</ul>  */
end_comment

begin_class
DECL|class|Analyzer
specifier|public
specifier|abstract
class|class
name|Analyzer
implements|implements
name|Closeable
block|{
DECL|field|reuseStrategy
specifier|private
specifier|final
name|ReuseStrategy
name|reuseStrategy
decl_stmt|;
DECL|field|version
specifier|private
name|Version
name|version
init|=
name|Version
operator|.
name|LATEST
decl_stmt|;
comment|// non final as it gets nulled if closed; pkg private for access by ReuseStrategy's final helper methods:
DECL|field|storedValue
name|CloseableThreadLocal
argument_list|<
name|Object
argument_list|>
name|storedValue
init|=
operator|new
name|CloseableThreadLocal
argument_list|<>
argument_list|()
decl_stmt|;
comment|/**    * Create a new Analyzer, reusing the same set of components per-thread    * across calls to {@link #tokenStream(String, Reader)}.     */
DECL|method|Analyzer
specifier|public
name|Analyzer
parameter_list|()
block|{
name|this
argument_list|(
name|GLOBAL_REUSE_STRATEGY
argument_list|)
expr_stmt|;
block|}
comment|/**    * Expert: create a new Analyzer with a custom {@link ReuseStrategy}.    *<p>    * NOTE: if you just want to reuse on a per-field basis, it's easier to    * use a subclass of {@link AnalyzerWrapper} such as     *<a href="{@docRoot}/../analyzers-common/org/apache/lucene/analysis/miscellaneous/PerFieldAnalyzerWrapper.html">    * PerFieldAnalyerWrapper</a> instead.    */
DECL|method|Analyzer
specifier|public
name|Analyzer
parameter_list|(
name|ReuseStrategy
name|reuseStrategy
parameter_list|)
block|{
name|this
operator|.
name|reuseStrategy
operator|=
name|reuseStrategy
expr_stmt|;
block|}
comment|/**    * Creates a new {@link TokenStreamComponents} instance for this analyzer.    *     * @param fieldName    *          the name of the fields content passed to the    *          {@link TokenStreamComponents} sink as a reader     * @return the {@link TokenStreamComponents} for this analyzer.    */
DECL|method|createComponents
specifier|protected
specifier|abstract
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|)
function_decl|;
comment|/**    * Wrap the given {@link TokenStream} in order to apply normalization filters.    * The default implementation returns the {@link TokenStream} as-is. This is    * used by {@link #normalize(String, String)}.    */
DECL|method|normalize
specifier|protected
name|TokenStream
name|normalize
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|TokenStream
name|in
parameter_list|)
block|{
return|return
name|in
return|;
block|}
comment|/**    * Returns a TokenStream suitable for<code>fieldName</code>, tokenizing    * the contents of<code>reader</code>.    *<p>    * This method uses {@link #createComponents(String)} to obtain an    * instance of {@link TokenStreamComponents}. It returns the sink of the    * components and stores the components internally. Subsequent calls to this    * method will reuse the previously stored components after resetting them    * through {@link TokenStreamComponents#setReader(Reader)}.    *<p>    *<b>NOTE:</b> After calling this method, the consumer must follow the     * workflow described in {@link TokenStream} to properly consume its contents.    * See the {@link org.apache.lucene.analysis Analysis package documentation} for    * some examples demonstrating this.    *     *<b>NOTE:</b> If your data is available as a {@code String}, use    * {@link #tokenStream(String, String)} which reuses a {@code StringReader}-like    * instance internally.    *     * @param fieldName the name of the field the created TokenStream is used for    * @param reader the reader the streams source reads from    * @return TokenStream for iterating the analyzed content of<code>reader</code>    * @throws AlreadyClosedException if the Analyzer is closed.    * @see #tokenStream(String, String)    */
DECL|method|tokenStream
specifier|public
specifier|final
name|TokenStream
name|tokenStream
parameter_list|(
specifier|final
name|String
name|fieldName
parameter_list|,
specifier|final
name|Reader
name|reader
parameter_list|)
block|{
name|TokenStreamComponents
name|components
init|=
name|reuseStrategy
operator|.
name|getReusableComponents
argument_list|(
name|this
argument_list|,
name|fieldName
argument_list|)
decl_stmt|;
specifier|final
name|Reader
name|r
init|=
name|initReader
argument_list|(
name|fieldName
argument_list|,
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|components
operator|==
literal|null
condition|)
block|{
name|components
operator|=
name|createComponents
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
name|reuseStrategy
operator|.
name|setReusableComponents
argument_list|(
name|this
argument_list|,
name|fieldName
argument_list|,
name|components
argument_list|)
expr_stmt|;
block|}
name|components
operator|.
name|setReader
argument_list|(
name|r
argument_list|)
expr_stmt|;
return|return
name|components
operator|.
name|getTokenStream
argument_list|()
return|;
block|}
comment|/**    * Returns a TokenStream suitable for<code>fieldName</code>, tokenizing    * the contents of<code>text</code>.    *<p>    * This method uses {@link #createComponents(String)} to obtain an    * instance of {@link TokenStreamComponents}. It returns the sink of the    * components and stores the components internally. Subsequent calls to this    * method will reuse the previously stored components after resetting them    * through {@link TokenStreamComponents#setReader(Reader)}.    *<p>    *<b>NOTE:</b> After calling this method, the consumer must follow the     * workflow described in {@link TokenStream} to properly consume its contents.    * See the {@link org.apache.lucene.analysis Analysis package documentation} for    * some examples demonstrating this.    *     * @param fieldName the name of the field the created TokenStream is used for    * @param text the String the streams source reads from    * @return TokenStream for iterating the analyzed content of<code>reader</code>    * @throws AlreadyClosedException if the Analyzer is closed.    * @see #tokenStream(String, Reader)    */
DECL|method|tokenStream
specifier|public
specifier|final
name|TokenStream
name|tokenStream
parameter_list|(
specifier|final
name|String
name|fieldName
parameter_list|,
specifier|final
name|String
name|text
parameter_list|)
block|{
name|TokenStreamComponents
name|components
init|=
name|reuseStrategy
operator|.
name|getReusableComponents
argument_list|(
name|this
argument_list|,
name|fieldName
argument_list|)
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"resource"
argument_list|)
specifier|final
name|ReusableStringReader
name|strReader
init|=
operator|(
name|components
operator|==
literal|null
operator|||
name|components
operator|.
name|reusableStringReader
operator|==
literal|null
operator|)
condition|?
operator|new
name|ReusableStringReader
argument_list|()
else|:
name|components
operator|.
name|reusableStringReader
decl_stmt|;
name|strReader
operator|.
name|setValue
argument_list|(
name|text
argument_list|)
expr_stmt|;
specifier|final
name|Reader
name|r
init|=
name|initReader
argument_list|(
name|fieldName
argument_list|,
name|strReader
argument_list|)
decl_stmt|;
if|if
condition|(
name|components
operator|==
literal|null
condition|)
block|{
name|components
operator|=
name|createComponents
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
name|reuseStrategy
operator|.
name|setReusableComponents
argument_list|(
name|this
argument_list|,
name|fieldName
argument_list|,
name|components
argument_list|)
expr_stmt|;
block|}
name|components
operator|.
name|setReader
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|components
operator|.
name|reusableStringReader
operator|=
name|strReader
expr_stmt|;
return|return
name|components
operator|.
name|getTokenStream
argument_list|()
return|;
block|}
comment|/**    * Normalize a string down to the representation that it would have in the    * index.    *<p>    * This is typically used by query parsers in order to generate a query on    * a given term, without tokenizing or stemming, which are undesirable if    * the string to analyze is a partial word (eg. in case of a wildcard or    * fuzzy query).    *<p>    * This method uses {@link #initReaderForNormalization(String, Reader)} in    * order to apply necessary character-level normalization and then    * {@link #normalize(String, TokenStream)} in order to apply the normalizing    * token filters.    */
DECL|method|normalize
specifier|public
specifier|final
name|BytesRef
name|normalize
parameter_list|(
specifier|final
name|String
name|fieldName
parameter_list|,
specifier|final
name|String
name|text
parameter_list|)
block|{
try|try
block|{
comment|// apply char filters
specifier|final
name|String
name|filteredText
decl_stmt|;
try|try
init|(
name|Reader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
name|text
argument_list|)
init|)
block|{
name|Reader
name|filterReader
init|=
name|initReaderForNormalization
argument_list|(
name|fieldName
argument_list|,
name|reader
argument_list|)
decl_stmt|;
name|char
index|[]
name|buffer
init|=
operator|new
name|char
index|[
literal|64
index|]
decl_stmt|;
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
specifier|final
name|int
name|read
init|=
name|filterReader
operator|.
name|read
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|buffer
operator|.
name|length
argument_list|)
decl_stmt|;
if|if
condition|(
name|read
operator|==
operator|-
literal|1
condition|)
block|{
break|break;
block|}
name|builder
operator|.
name|append
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|read
argument_list|)
expr_stmt|;
block|}
name|filteredText
operator|=
name|builder
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Normalization threw an unexpected exeption"
argument_list|,
name|e
argument_list|)
throw|;
block|}
specifier|final
name|AttributeFactory
name|attributeFactory
init|=
name|attributeFactory
argument_list|()
decl_stmt|;
try|try
init|(
name|TokenStream
name|ts
init|=
name|normalize
argument_list|(
name|fieldName
argument_list|,
operator|new
name|StringTokenStream
argument_list|(
name|attributeFactory
argument_list|,
name|filteredText
argument_list|,
name|text
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
init|)
block|{
specifier|final
name|TermToBytesRefAttribute
name|termAtt
init|=
name|ts
operator|.
name|addAttribute
argument_list|(
name|TermToBytesRefAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|ts
operator|.
name|reset
argument_list|()
expr_stmt|;
if|if
condition|(
name|ts
operator|.
name|incrementToken
argument_list|()
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"The normalization token stream is "
operator|+
literal|"expected to produce exactly 1 token, but got 0 for analyzer "
operator|+
name|this
operator|+
literal|" and input \""
operator|+
name|text
operator|+
literal|"\""
argument_list|)
throw|;
block|}
specifier|final
name|BytesRef
name|term
init|=
name|BytesRef
operator|.
name|deepCopyOf
argument_list|(
name|termAtt
operator|.
name|getBytesRef
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|ts
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"The normalization token stream is "
operator|+
literal|"expected to produce exactly 1 token, but got 2+ for analyzer "
operator|+
name|this
operator|+
literal|" and input \""
operator|+
name|text
operator|+
literal|"\""
argument_list|)
throw|;
block|}
name|ts
operator|.
name|end
argument_list|()
expr_stmt|;
return|return
name|term
return|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Normalization threw an unexpected exeption"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * Override this if you want to add a CharFilter chain.    *<p>    * The default implementation returns<code>reader</code>    * unchanged.    *     * @param fieldName IndexableField name being indexed    * @param reader original Reader    * @return reader, optionally decorated with CharFilter(s)    */
DECL|method|initReader
specifier|protected
name|Reader
name|initReader
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
return|return
name|reader
return|;
block|}
comment|/** Wrap the given {@link Reader} with {@link CharFilter}s that make sense    *  for normalization. This is typically a subset of the {@link CharFilter}s    *  that are applied in {@link #initReader(String, Reader)}. This is used by    *  {@link #normalize(String, String)}. */
DECL|method|initReaderForNormalization
specifier|protected
name|Reader
name|initReaderForNormalization
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
return|return
name|reader
return|;
block|}
comment|/** Return the {@link AttributeFactory} to be used for    *  {@link #tokenStream analysis} and    *  {@link #normalize(String, String) normalization}. The default    *  implementation returns {@link AttributeFactory#DEFAULT_ATTRIBUTE_FACTORY}. */
DECL|method|attributeFactory
specifier|protected
name|AttributeFactory
name|attributeFactory
parameter_list|()
block|{
return|return
name|AttributeFactory
operator|.
name|DEFAULT_ATTRIBUTE_FACTORY
return|;
block|}
comment|/**    * Invoked before indexing a IndexableField instance if    * terms have already been added to that field.  This allows custom    * analyzers to place an automatic position increment gap between    * IndexbleField instances using the same field name.  The default value    * position increment gap is 0.  With a 0 position increment gap and    * the typical default token position increment of 1, all terms in a field,    * including across IndexableField instances, are in successive positions, allowing    * exact PhraseQuery matches, for instance, across IndexableField instance boundaries.    *    * @param fieldName IndexableField name being indexed.    * @return position increment gap, added to the next token emitted from {@link #tokenStream(String,Reader)}.    *         This value must be {@code>= 0}.    */
DECL|method|getPositionIncrementGap
specifier|public
name|int
name|getPositionIncrementGap
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
return|return
literal|0
return|;
block|}
comment|/**    * Just like {@link #getPositionIncrementGap}, except for    * Token offsets instead.  By default this returns 1.    * This method is only called if the field    * produced at least one token for indexing.    *    * @param fieldName the field just indexed    * @return offset gap, added to the next token emitted from {@link #tokenStream(String,Reader)}.    *         This value must be {@code>= 0}.    */
DECL|method|getOffsetGap
specifier|public
name|int
name|getOffsetGap
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
return|return
literal|1
return|;
block|}
comment|/**    * Returns the used {@link ReuseStrategy}.    */
DECL|method|getReuseStrategy
specifier|public
specifier|final
name|ReuseStrategy
name|getReuseStrategy
parameter_list|()
block|{
return|return
name|reuseStrategy
return|;
block|}
comment|/**    * Set the version of Lucene this analyzer should mimic the behavior for for analysis.    */
DECL|method|setVersion
specifier|public
name|void
name|setVersion
parameter_list|(
name|Version
name|v
parameter_list|)
block|{
name|version
operator|=
name|v
expr_stmt|;
comment|// TODO: make write once?
block|}
comment|/**    * Return the version of Lucene this analyzer will mimic the behavior of for analysis.    */
DECL|method|getVersion
specifier|public
name|Version
name|getVersion
parameter_list|()
block|{
return|return
name|version
return|;
block|}
comment|/** Frees persistent resources used by this Analyzer */
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
if|if
condition|(
name|storedValue
operator|!=
literal|null
condition|)
block|{
name|storedValue
operator|.
name|close
argument_list|()
expr_stmt|;
name|storedValue
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|/**    * This class encapsulates the outer components of a token stream. It provides    * access to the source ({@link Tokenizer}) and the outer end (sink), an    * instance of {@link TokenFilter} which also serves as the    * {@link TokenStream} returned by    * {@link Analyzer#tokenStream(String, Reader)}.    */
DECL|class|TokenStreamComponents
specifier|public
specifier|static
class|class
name|TokenStreamComponents
block|{
comment|/**      * Original source of the tokens.      */
DECL|field|source
specifier|protected
specifier|final
name|Tokenizer
name|source
decl_stmt|;
comment|/**      * Sink tokenstream, such as the outer tokenfilter decorating      * the chain. This can be the source if there are no filters.      */
DECL|field|sink
specifier|protected
specifier|final
name|TokenStream
name|sink
decl_stmt|;
comment|/** Internal cache only used by {@link Analyzer#tokenStream(String, String)}. */
DECL|field|reusableStringReader
specifier|transient
name|ReusableStringReader
name|reusableStringReader
decl_stmt|;
comment|/**      * Creates a new {@link TokenStreamComponents} instance.      *       * @param source      *          the analyzer's tokenizer      * @param result      *          the analyzer's resulting token stream      */
DECL|method|TokenStreamComponents
specifier|public
name|TokenStreamComponents
parameter_list|(
specifier|final
name|Tokenizer
name|source
parameter_list|,
specifier|final
name|TokenStream
name|result
parameter_list|)
block|{
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
name|this
operator|.
name|sink
operator|=
name|result
expr_stmt|;
block|}
comment|/**      * Creates a new {@link TokenStreamComponents} instance.      *       * @param source      *          the analyzer's tokenizer      */
DECL|method|TokenStreamComponents
specifier|public
name|TokenStreamComponents
parameter_list|(
specifier|final
name|Tokenizer
name|source
parameter_list|)
block|{
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
name|this
operator|.
name|sink
operator|=
name|source
expr_stmt|;
block|}
comment|/**      * Resets the encapsulated components with the given reader. If the components      * cannot be reset, an Exception should be thrown.      *       * @param reader      *          a reader to reset the source component      */
DECL|method|setReader
specifier|protected
name|void
name|setReader
parameter_list|(
specifier|final
name|Reader
name|reader
parameter_list|)
block|{
name|source
operator|.
name|setReader
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns the sink {@link TokenStream}      *       * @return the sink {@link TokenStream}      */
DECL|method|getTokenStream
specifier|public
name|TokenStream
name|getTokenStream
parameter_list|()
block|{
return|return
name|sink
return|;
block|}
comment|/**      * Returns the component's {@link Tokenizer}      *      * @return Component's {@link Tokenizer}      */
DECL|method|getTokenizer
specifier|public
name|Tokenizer
name|getTokenizer
parameter_list|()
block|{
return|return
name|source
return|;
block|}
block|}
comment|/**    * Strategy defining how TokenStreamComponents are reused per call to    * {@link Analyzer#tokenStream(String, java.io.Reader)}.    */
DECL|class|ReuseStrategy
specifier|public
specifier|static
specifier|abstract
class|class
name|ReuseStrategy
block|{
comment|/** Sole constructor. (For invocation by subclass constructors, typically implicit.) */
DECL|method|ReuseStrategy
specifier|public
name|ReuseStrategy
parameter_list|()
block|{}
comment|/**      * Gets the reusable TokenStreamComponents for the field with the given name.      *      * @param analyzer Analyzer from which to get the reused components. Use      *        {@link #getStoredValue(Analyzer)} and {@link #setStoredValue(Analyzer, Object)}      *        to access the data on the Analyzer.      * @param fieldName Name of the field whose reusable TokenStreamComponents      *        are to be retrieved      * @return Reusable TokenStreamComponents for the field, or {@code null}      *         if there was no previous components for the field      */
DECL|method|getReusableComponents
specifier|public
specifier|abstract
name|TokenStreamComponents
name|getReusableComponents
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|,
name|String
name|fieldName
parameter_list|)
function_decl|;
comment|/**      * Stores the given TokenStreamComponents as the reusable components for the      * field with the give name.      *      * @param fieldName Name of the field whose TokenStreamComponents are being set      * @param components TokenStreamComponents which are to be reused for the field      */
DECL|method|setReusableComponents
specifier|public
specifier|abstract
name|void
name|setReusableComponents
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|TokenStreamComponents
name|components
parameter_list|)
function_decl|;
comment|/**      * Returns the currently stored value.      *      * @return Currently stored value or {@code null} if no value is stored      * @throws AlreadyClosedException if the Analyzer is closed.      */
DECL|method|getStoredValue
specifier|protected
specifier|final
name|Object
name|getStoredValue
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|)
block|{
if|if
condition|(
name|analyzer
operator|.
name|storedValue
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|AlreadyClosedException
argument_list|(
literal|"this Analyzer is closed"
argument_list|)
throw|;
block|}
return|return
name|analyzer
operator|.
name|storedValue
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**      * Sets the stored value.      *      * @param storedValue Value to store      * @throws AlreadyClosedException if the Analyzer is closed.      */
DECL|method|setStoredValue
specifier|protected
specifier|final
name|void
name|setStoredValue
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|,
name|Object
name|storedValue
parameter_list|)
block|{
if|if
condition|(
name|analyzer
operator|.
name|storedValue
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|AlreadyClosedException
argument_list|(
literal|"this Analyzer is closed"
argument_list|)
throw|;
block|}
name|analyzer
operator|.
name|storedValue
operator|.
name|set
argument_list|(
name|storedValue
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * A predefined {@link ReuseStrategy}  that reuses the same components for    * every field.    */
DECL|field|GLOBAL_REUSE_STRATEGY
specifier|public
specifier|static
specifier|final
name|ReuseStrategy
name|GLOBAL_REUSE_STRATEGY
init|=
operator|new
name|ReuseStrategy
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|TokenStreamComponents
name|getReusableComponents
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|,
name|String
name|fieldName
parameter_list|)
block|{
return|return
operator|(
name|TokenStreamComponents
operator|)
name|getStoredValue
argument_list|(
name|analyzer
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setReusableComponents
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|TokenStreamComponents
name|components
parameter_list|)
block|{
name|setStoredValue
argument_list|(
name|analyzer
argument_list|,
name|components
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
comment|/**    * A predefined {@link ReuseStrategy} that reuses components per-field by    * maintaining a Map of TokenStreamComponent per field name.    */
DECL|field|PER_FIELD_REUSE_STRATEGY
specifier|public
specifier|static
specifier|final
name|ReuseStrategy
name|PER_FIELD_REUSE_STRATEGY
init|=
operator|new
name|ReuseStrategy
argument_list|()
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
specifier|public
name|TokenStreamComponents
name|getReusableComponents
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|,
name|String
name|fieldName
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|TokenStreamComponents
argument_list|>
name|componentsPerField
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|TokenStreamComponents
argument_list|>
operator|)
name|getStoredValue
argument_list|(
name|analyzer
argument_list|)
decl_stmt|;
return|return
name|componentsPerField
operator|!=
literal|null
condition|?
name|componentsPerField
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
else|:
literal|null
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
specifier|public
name|void
name|setReusableComponents
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|TokenStreamComponents
name|components
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|TokenStreamComponents
argument_list|>
name|componentsPerField
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|TokenStreamComponents
argument_list|>
operator|)
name|getStoredValue
argument_list|(
name|analyzer
argument_list|)
decl_stmt|;
if|if
condition|(
name|componentsPerField
operator|==
literal|null
condition|)
block|{
name|componentsPerField
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|setStoredValue
argument_list|(
name|analyzer
argument_list|,
name|componentsPerField
argument_list|)
expr_stmt|;
block|}
name|componentsPerField
operator|.
name|put
argument_list|(
name|fieldName
argument_list|,
name|components
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
DECL|class|StringTokenStream
specifier|private
specifier|static
specifier|final
class|class
name|StringTokenStream
extends|extends
name|TokenStream
block|{
DECL|field|value
specifier|private
specifier|final
name|String
name|value
decl_stmt|;
DECL|field|length
specifier|private
specifier|final
name|int
name|length
decl_stmt|;
DECL|field|used
specifier|private
name|boolean
name|used
init|=
literal|true
decl_stmt|;
DECL|field|termAttribute
specifier|private
specifier|final
name|CharTermAttribute
name|termAttribute
init|=
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|offsetAttribute
specifier|private
specifier|final
name|OffsetAttribute
name|offsetAttribute
init|=
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|StringTokenStream
name|StringTokenStream
parameter_list|(
name|AttributeFactory
name|attributeFactory
parameter_list|,
name|String
name|value
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|super
argument_list|(
name|attributeFactory
argument_list|)
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|used
operator|=
literal|false
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
name|boolean
name|incrementToken
parameter_list|()
block|{
if|if
condition|(
name|used
condition|)
block|{
return|return
literal|false
return|;
block|}
name|clearAttributes
argument_list|()
expr_stmt|;
name|termAttribute
operator|.
name|append
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|offsetAttribute
operator|.
name|setOffset
argument_list|(
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|used
operator|=
literal|true
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|end
specifier|public
name|void
name|end
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|end
argument_list|()
expr_stmt|;
name|offsetAttribute
operator|.
name|setOffset
argument_list|(
name|length
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

