begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.standard
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|standard
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|*
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
name|Set
import|;
end_import

begin_comment
comment|/**  * Filters {@link StandardTokenizer} with {@link StandardFilter}, {@link  * LowerCaseFilter} and {@link StopFilter}, using a list of  * English stop words.  *  *<a name="version"/>  *<p>You must specify the required {@link Version}  * compatibility when creating StandardAnalyzer:  *<ul>  *<li> As of 3.1, StopFilter correctly handles Unicode 4.0  *         supplementary characters in stopwords  *<li> As of 2.9, StopFilter preserves position  *        increments  *<li> As of 2.4, Tokens incorrectly identified as acronyms  *        are corrected (see<a href="https://issues.apache.org/jira/browse/LUCENE-1068">LUCENE-1068</a>)  *</ul>  */
end_comment

begin_class
DECL|class|StandardAnalyzer
specifier|public
class|class
name|StandardAnalyzer
extends|extends
name|Analyzer
block|{
DECL|field|stopSet
specifier|private
name|Set
argument_list|<
name|?
argument_list|>
name|stopSet
decl_stmt|;
comment|/**    * Specifies whether deprecated acronyms should be replaced with HOST type.    * See {@linkplain https://issues.apache.org/jira/browse/LUCENE-1068}    */
DECL|field|replaceInvalidAcronym
specifier|private
specifier|final
name|boolean
name|replaceInvalidAcronym
decl_stmt|;
comment|/** An unmodifiable set containing some common English words that are usually not   useful for searching. */
DECL|field|STOP_WORDS_SET
specifier|public
specifier|static
specifier|final
name|Set
argument_list|<
name|?
argument_list|>
name|STOP_WORDS_SET
init|=
name|StopAnalyzer
operator|.
name|ENGLISH_STOP_WORDS_SET
decl_stmt|;
DECL|field|matchVersion
specifier|private
specifier|final
name|Version
name|matchVersion
decl_stmt|;
comment|/** Builds an analyzer with the default stop words ({@link    * #STOP_WORDS_SET}).    * @param matchVersion Lucene version to match See {@link    *<a href="#version">above</a>}    */
DECL|method|StandardAnalyzer
specifier|public
name|StandardAnalyzer
parameter_list|(
name|Version
name|matchVersion
parameter_list|)
block|{
name|this
argument_list|(
name|matchVersion
argument_list|,
name|STOP_WORDS_SET
argument_list|)
expr_stmt|;
block|}
comment|/** Builds an analyzer with the given stop words.    * @param matchVersion Lucene version to match See {@link    *<a href="#version">above</a>}    * @param stopWords stop words */
DECL|method|StandardAnalyzer
specifier|public
name|StandardAnalyzer
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|Set
argument_list|<
name|?
argument_list|>
name|stopWords
parameter_list|)
block|{
name|stopSet
operator|=
name|stopWords
expr_stmt|;
name|setOverridesTokenStreamMethod
argument_list|(
name|StandardAnalyzer
operator|.
name|class
argument_list|)
expr_stmt|;
name|replaceInvalidAcronym
operator|=
name|matchVersion
operator|.
name|onOrAfter
argument_list|(
name|Version
operator|.
name|LUCENE_24
argument_list|)
expr_stmt|;
name|this
operator|.
name|matchVersion
operator|=
name|matchVersion
expr_stmt|;
block|}
comment|/** Builds an analyzer with the stop words from the given file.    * @see WordlistLoader#getWordSet(File)    * @param matchVersion Lucene version to match See {@link    *<a href="#version">above</a>}    * @param stopwords File to read stop words from */
DECL|method|StandardAnalyzer
specifier|public
name|StandardAnalyzer
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
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Builds an analyzer with the stop words from the given reader.    * @see WordlistLoader#getWordSet(Reader)    * @param matchVersion Lucene version to match See {@link    *<a href="#version">above</a>}    * @param stopwords Reader to read stop words from */
DECL|method|StandardAnalyzer
specifier|public
name|StandardAnalyzer
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|Reader
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
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Constructs a {@link StandardTokenizer} filtered by a {@link   StandardFilter}, a {@link LowerCaseFilter} and a {@link StopFilter}. */
annotation|@
name|Override
DECL|method|tokenStream
specifier|public
name|TokenStream
name|tokenStream
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
name|StandardTokenizer
name|tokenStream
init|=
operator|new
name|StandardTokenizer
argument_list|(
name|matchVersion
argument_list|,
name|reader
argument_list|)
decl_stmt|;
name|tokenStream
operator|.
name|setMaxTokenLength
argument_list|(
name|maxTokenLength
argument_list|)
expr_stmt|;
name|TokenStream
name|result
init|=
operator|new
name|StandardFilter
argument_list|(
name|tokenStream
argument_list|)
decl_stmt|;
name|result
operator|=
operator|new
name|LowerCaseFilter
argument_list|(
name|matchVersion
argument_list|,
name|result
argument_list|)
expr_stmt|;
name|result
operator|=
operator|new
name|StopFilter
argument_list|(
name|matchVersion
argument_list|,
name|result
argument_list|,
name|stopSet
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|class|SavedStreams
specifier|private
specifier|static
specifier|final
class|class
name|SavedStreams
block|{
DECL|field|tokenStream
name|StandardTokenizer
name|tokenStream
decl_stmt|;
DECL|field|filteredTokenStream
name|TokenStream
name|filteredTokenStream
decl_stmt|;
block|}
comment|/** Default maximum allowed token length */
DECL|field|DEFAULT_MAX_TOKEN_LENGTH
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MAX_TOKEN_LENGTH
init|=
literal|255
decl_stmt|;
DECL|field|maxTokenLength
specifier|private
name|int
name|maxTokenLength
init|=
name|DEFAULT_MAX_TOKEN_LENGTH
decl_stmt|;
comment|/**    * Set maximum allowed token length.  If a token is seen    * that exceeds this length then it is discarded.  This    * setting only takes effect the next time tokenStream or    * reusableTokenStream is called.    */
DECL|method|setMaxTokenLength
specifier|public
name|void
name|setMaxTokenLength
parameter_list|(
name|int
name|length
parameter_list|)
block|{
name|maxTokenLength
operator|=
name|length
expr_stmt|;
block|}
comment|/**    * @see #setMaxTokenLength    */
DECL|method|getMaxTokenLength
specifier|public
name|int
name|getMaxTokenLength
parameter_list|()
block|{
return|return
name|maxTokenLength
return|;
block|}
annotation|@
name|Override
DECL|method|reusableTokenStream
specifier|public
name|TokenStream
name|reusableTokenStream
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|overridesTokenStreamMethod
condition|)
block|{
comment|// LUCENE-1678: force fallback to tokenStream() if we
comment|// have been subclassed and that subclass overrides
comment|// tokenStream but not reusableTokenStream
return|return
name|tokenStream
argument_list|(
name|fieldName
argument_list|,
name|reader
argument_list|)
return|;
block|}
name|SavedStreams
name|streams
init|=
operator|(
name|SavedStreams
operator|)
name|getPreviousTokenStream
argument_list|()
decl_stmt|;
if|if
condition|(
name|streams
operator|==
literal|null
condition|)
block|{
name|streams
operator|=
operator|new
name|SavedStreams
argument_list|()
expr_stmt|;
name|setPreviousTokenStream
argument_list|(
name|streams
argument_list|)
expr_stmt|;
name|streams
operator|.
name|tokenStream
operator|=
operator|new
name|StandardTokenizer
argument_list|(
name|matchVersion
argument_list|,
name|reader
argument_list|)
expr_stmt|;
name|streams
operator|.
name|filteredTokenStream
operator|=
operator|new
name|StandardFilter
argument_list|(
name|streams
operator|.
name|tokenStream
argument_list|)
expr_stmt|;
name|streams
operator|.
name|filteredTokenStream
operator|=
operator|new
name|LowerCaseFilter
argument_list|(
name|matchVersion
argument_list|,
name|streams
operator|.
name|filteredTokenStream
argument_list|)
expr_stmt|;
name|streams
operator|.
name|filteredTokenStream
operator|=
operator|new
name|StopFilter
argument_list|(
name|matchVersion
argument_list|,
name|streams
operator|.
name|filteredTokenStream
argument_list|,
name|stopSet
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|streams
operator|.
name|tokenStream
operator|.
name|reset
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
name|streams
operator|.
name|tokenStream
operator|.
name|setMaxTokenLength
argument_list|(
name|maxTokenLength
argument_list|)
expr_stmt|;
name|streams
operator|.
name|tokenStream
operator|.
name|setReplaceInvalidAcronym
argument_list|(
name|replaceInvalidAcronym
argument_list|)
expr_stmt|;
return|return
name|streams
operator|.
name|filteredTokenStream
return|;
block|}
block|}
end_class

end_unit

