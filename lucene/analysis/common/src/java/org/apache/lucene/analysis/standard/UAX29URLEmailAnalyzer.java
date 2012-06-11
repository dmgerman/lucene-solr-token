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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|core
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
name|core
operator|.
name|StopAnalyzer
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
name|core
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
name|util
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
name|util
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

begin_comment
comment|/**  * Filters {@link org.apache.lucene.analysis.standard.UAX29URLEmailTokenizer}  * with {@link org.apache.lucene.analysis.standard.StandardFilter},  * {@link org.apache.lucene.analysis.core.LowerCaseFilter} and  * {@link org.apache.lucene.analysis.core.StopFilter}, using a list of  * English stop words.  *  *<a name="version"/>  *<p>  *   You must specify the required {@link org.apache.lucene.util.Version}  *   compatibility when creating UAX29URLEmailAnalyzer  *</p>  */
end_comment

begin_class
DECL|class|UAX29URLEmailAnalyzer
specifier|public
specifier|final
class|class
name|UAX29URLEmailAnalyzer
extends|extends
name|StopwordAnalyzerBase
block|{
comment|/** Default maximum allowed token length */
DECL|field|DEFAULT_MAX_TOKEN_LENGTH
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MAX_TOKEN_LENGTH
init|=
name|StandardAnalyzer
operator|.
name|DEFAULT_MAX_TOKEN_LENGTH
decl_stmt|;
DECL|field|maxTokenLength
specifier|private
name|int
name|maxTokenLength
init|=
name|DEFAULT_MAX_TOKEN_LENGTH
decl_stmt|;
comment|/** An unmodifiable set containing some common English words that are usually not   useful for searching. */
DECL|field|STOP_WORDS_SET
specifier|public
specifier|static
specifier|final
name|CharArraySet
name|STOP_WORDS_SET
init|=
name|StopAnalyzer
operator|.
name|ENGLISH_STOP_WORDS_SET
decl_stmt|;
comment|/** Builds an analyzer with the given stop words.    * @param matchVersion Lucene version to match See {@link    *<a href="#version">above</a>}    * @param stopWords stop words */
DECL|method|UAX29URLEmailAnalyzer
specifier|public
name|UAX29URLEmailAnalyzer
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|CharArraySet
name|stopWords
parameter_list|)
block|{
name|super
argument_list|(
name|matchVersion
argument_list|,
name|stopWords
argument_list|)
expr_stmt|;
block|}
comment|/** Builds an analyzer with the default stop words ({@link    * #STOP_WORDS_SET}).    * @param matchVersion Lucene version to match See {@link    *<a href="#version">above</a>}    */
DECL|method|UAX29URLEmailAnalyzer
specifier|public
name|UAX29URLEmailAnalyzer
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
comment|/** Builds an analyzer with the stop words from the given reader.    * @see org.apache.lucene.analysis.util.WordlistLoader#getWordSet(java.io.Reader, org.apache.lucene.util.Version)    * @param matchVersion Lucene version to match See {@link    *<a href="#version">above</a>}    * @param stopwords Reader to read stop words from */
DECL|method|UAX29URLEmailAnalyzer
specifier|public
name|UAX29URLEmailAnalyzer
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
name|loadStopwordSet
argument_list|(
name|stopwords
argument_list|,
name|matchVersion
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Set maximum allowed token length.  If a token is seen    * that exceeds this length then it is discarded.  This    * setting only takes effect the next time tokenStream or    * tokenStream is called.    */
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
DECL|method|createComponents
specifier|protected
name|TokenStreamComponents
name|createComponents
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
specifier|final
name|UAX29URLEmailTokenizer
name|src
init|=
operator|new
name|UAX29URLEmailTokenizer
argument_list|(
name|matchVersion
argument_list|,
name|reader
argument_list|)
decl_stmt|;
name|src
operator|.
name|setMaxTokenLength
argument_list|(
name|maxTokenLength
argument_list|)
expr_stmt|;
name|TokenStream
name|tok
init|=
operator|new
name|StandardFilter
argument_list|(
name|matchVersion
argument_list|,
name|src
argument_list|)
decl_stmt|;
name|tok
operator|=
operator|new
name|LowerCaseFilter
argument_list|(
name|matchVersion
argument_list|,
name|tok
argument_list|)
expr_stmt|;
name|tok
operator|=
operator|new
name|StopFilter
argument_list|(
name|matchVersion
argument_list|,
name|tok
argument_list|,
name|stopwords
argument_list|)
expr_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|src
argument_list|,
name|tok
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|reset
parameter_list|(
specifier|final
name|Reader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|src
operator|.
name|setMaxTokenLength
argument_list|(
name|UAX29URLEmailAnalyzer
operator|.
name|this
operator|.
name|maxTokenLength
argument_list|)
expr_stmt|;
name|super
operator|.
name|reset
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

