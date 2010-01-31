begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.ar
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|ar
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|util
operator|.
name|Hashtable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|Analyzer
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
name|LowerCaseFilter
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
name|ReusableAnalyzerBase
operator|.
name|TokenStreamComponents
import|;
end_import

begin_comment
comment|// javadoc @link
end_comment

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
name|CharArraySet
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
name|KeywordMarkerTokenFilter
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
name|StopFilter
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
name|StopwordAnalyzerBase
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
name|TokenStream
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
name|Tokenizer
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
name|WordlistLoader
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
comment|/**  * {@link Analyzer} for Arabic.   *<p>  * This analyzer implements light-stemming as specified by:  *<i>  * Light Stemming for Arabic Information Retrieval  *</i>      * http://www.mtholyoke.edu/~lballest/Pubs/arab_stem05.pdf  *<p>  * The analysis package contains three primary components:  *<ul>  *<li>{@link ArabicNormalizationFilter}: Arabic orthographic normalization.  *<li>{@link ArabicStemFilter}: Arabic light stemming  *<li>Arabic stop words file: a set of default Arabic stop words.  *</ul>  *   */
end_comment

begin_class
DECL|class|ArabicAnalyzer
specifier|public
specifier|final
class|class
name|ArabicAnalyzer
extends|extends
name|StopwordAnalyzerBase
block|{
comment|/**    * File containing default Arabic stopwords.    *     * Default stopword list is from http://members.unine.ch/jacques.savoy/clef/index.html    * The stopword list is BSD-Licensed.    */
DECL|field|DEFAULT_STOPWORD_FILE
specifier|public
specifier|final
specifier|static
name|String
name|DEFAULT_STOPWORD_FILE
init|=
literal|"stopwords.txt"
decl_stmt|;
comment|/**    * The comment character in the stopwords file.  All lines prefixed with this will be ignored    * @deprecated use {@link WordlistLoader#getWordSet(File, String)} directly      */
comment|// TODO make this private
annotation|@
name|Deprecated
DECL|field|STOPWORDS_COMMENT
specifier|public
specifier|static
specifier|final
name|String
name|STOPWORDS_COMMENT
init|=
literal|"#"
decl_stmt|;
comment|/**    * Returns an unmodifiable instance of the default stop-words set.    * @return an unmodifiable instance of the default stop-words set.    */
DECL|method|getDefaultStopSet
specifier|public
specifier|static
name|Set
argument_list|<
name|?
argument_list|>
name|getDefaultStopSet
parameter_list|()
block|{
return|return
name|DefaultSetHolder
operator|.
name|DEFAULT_STOP_SET
return|;
block|}
comment|/**    * Atomically loads the DEFAULT_STOP_SET in a lazy fashion once the outer class     * accesses the static final set the first time.;    */
DECL|class|DefaultSetHolder
specifier|private
specifier|static
class|class
name|DefaultSetHolder
block|{
DECL|field|DEFAULT_STOP_SET
specifier|static
specifier|final
name|Set
argument_list|<
name|?
argument_list|>
name|DEFAULT_STOP_SET
decl_stmt|;
static|static
block|{
try|try
block|{
name|DEFAULT_STOP_SET
operator|=
name|loadStopwordSet
argument_list|(
literal|false
argument_list|,
name|ArabicAnalyzer
operator|.
name|class
argument_list|,
name|DEFAULT_STOPWORD_FILE
argument_list|,
name|STOPWORDS_COMMENT
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
comment|// default set should always be present as it is part of the
comment|// distribution (JAR)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unable to load default stopword set"
argument_list|)
throw|;
block|}
block|}
block|}
DECL|field|stemExclusionSet
specifier|private
specifier|final
name|Set
argument_list|<
name|?
argument_list|>
name|stemExclusionSet
decl_stmt|;
comment|/**    * Builds an analyzer with the default stop words: {@link #DEFAULT_STOPWORD_FILE}.    */
DECL|method|ArabicAnalyzer
specifier|public
name|ArabicAnalyzer
parameter_list|(
name|Version
name|matchVersion
parameter_list|)
block|{
name|this
argument_list|(
name|matchVersion
argument_list|,
name|DefaultSetHolder
operator|.
name|DEFAULT_STOP_SET
argument_list|)
expr_stmt|;
block|}
comment|/**    * Builds an analyzer with the given stop words    *     * @param matchVersion    *          lucene compatibility version    * @param stopwords    *          a stopword set    */
DECL|method|ArabicAnalyzer
specifier|public
name|ArabicAnalyzer
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|Set
argument_list|<
name|?
argument_list|>
name|stopwords
parameter_list|)
block|{
name|this
argument_list|(
name|matchVersion
argument_list|,
name|stopwords
argument_list|,
name|CharArraySet
operator|.
name|EMPTY_SET
argument_list|)
expr_stmt|;
block|}
comment|/**    * Builds an analyzer with the given stop word. If a none-empty stem exclusion set is    * provided this analyzer will add a {@link KeywordMarkerTokenFilter} before    * {@link ArabicStemFilter}.    *     * @param matchVersion    *          lucene compatibility version    * @param stopwords    *          a stopword set    * @param stemExclusionSet    *          a set of terms not to be stemmed    */
DECL|method|ArabicAnalyzer
specifier|public
name|ArabicAnalyzer
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|Set
argument_list|<
name|?
argument_list|>
name|stopwords
parameter_list|,
name|Set
argument_list|<
name|?
argument_list|>
name|stemExclusionSet
parameter_list|)
block|{
name|super
argument_list|(
name|matchVersion
argument_list|,
name|stopwords
argument_list|)
expr_stmt|;
name|this
operator|.
name|stemExclusionSet
operator|=
name|CharArraySet
operator|.
name|unmodifiableSet
argument_list|(
name|CharArraySet
operator|.
name|copy
argument_list|(
name|matchVersion
argument_list|,
name|stemExclusionSet
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Builds an analyzer with the given stop words.    * @deprecated use {@link #ArabicAnalyzer(Version, Set)} instead    */
annotation|@
name|Deprecated
DECL|method|ArabicAnalyzer
specifier|public
name|ArabicAnalyzer
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|String
modifier|...
name|stopwords
parameter_list|)
block|{
name|this
argument_list|(
name|matchVersion
argument_list|,
name|StopFilter
operator|.
name|makeStopSet
argument_list|(
name|matchVersion
argument_list|,
name|stopwords
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Builds an analyzer with the given stop words.    * @deprecated use {@link #ArabicAnalyzer(Version, Set)} instead    */
annotation|@
name|Deprecated
DECL|method|ArabicAnalyzer
specifier|public
name|ArabicAnalyzer
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|Hashtable
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|stopwords
parameter_list|)
block|{
name|this
argument_list|(
name|matchVersion
argument_list|,
name|stopwords
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Builds an analyzer with the given stop words.  Lines can be commented out using {@link #STOPWORDS_COMMENT}    * @deprecated use {@link #ArabicAnalyzer(Version, Set)} instead    */
annotation|@
name|Deprecated
DECL|method|ArabicAnalyzer
specifier|public
name|ArabicAnalyzer
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|File
name|stopwords
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|matchVersion
argument_list|,
name|WordlistLoader
operator|.
name|getWordSet
argument_list|(
name|stopwords
argument_list|,
name|STOPWORDS_COMMENT
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates {@link TokenStreamComponents} used to tokenize all the text in the provided {@link Reader}.    *    * @return {@link TokenStreamComponents} built from an {@link ArabicLetterTokenizer} filtered with    * 			{@link LowerCaseFilter}, {@link StopFilter}, {@link ArabicNormalizationFilter},    *      {@link KeywordMarkerTokenFilter} if a stem exclusion set is provided    *            and {@link ArabicStemFilter}.    */
annotation|@
name|Override
DECL|method|createComponents
specifier|protected
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
specifier|final
name|Tokenizer
name|source
init|=
operator|new
name|ArabicLetterTokenizer
argument_list|(
name|matchVersion
argument_list|,
name|reader
argument_list|)
decl_stmt|;
name|TokenStream
name|result
init|=
operator|new
name|LowerCaseFilter
argument_list|(
name|matchVersion
argument_list|,
name|source
argument_list|)
decl_stmt|;
comment|// the order here is important: the stopword list is not normalized!
name|result
operator|=
operator|new
name|StopFilter
argument_list|(
name|matchVersion
argument_list|,
name|result
argument_list|,
name|stopwords
argument_list|)
expr_stmt|;
comment|// TODO maybe we should make ArabicNormalization filter also KeywordAttribute aware?!
name|result
operator|=
operator|new
name|ArabicNormalizationFilter
argument_list|(
name|result
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|stemExclusionSet
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|result
operator|=
operator|new
name|KeywordMarkerTokenFilter
argument_list|(
name|result
argument_list|,
name|stemExclusionSet
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|source
argument_list|,
operator|new
name|ArabicStemFilter
argument_list|(
name|result
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

