begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.core
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|core
package|;
end_package

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
name|nio
operator|.
name|file
operator|.
name|Path
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
name|analysis
operator|.
name|standard
operator|.
name|StandardAnalyzer
import|;
end_import

begin_comment
comment|/**   * Filters {@link LetterTokenizer} with {@link LowerCaseFilter} and {@link StopFilter}.  */
end_comment

begin_class
DECL|class|StopAnalyzer
specifier|public
specifier|final
class|class
name|StopAnalyzer
extends|extends
name|StopwordAnalyzerBase
block|{
comment|/** An unmodifiable set containing some common English words that are not usually useful   for searching.*/
DECL|field|ENGLISH_STOP_WORDS_SET
specifier|public
specifier|static
specifier|final
name|CharArraySet
name|ENGLISH_STOP_WORDS_SET
init|=
name|StandardAnalyzer
operator|.
name|ENGLISH_STOP_WORDS_SET
decl_stmt|;
comment|/** Builds an analyzer which removes words in    *  {@link #ENGLISH_STOP_WORDS_SET}.    */
DECL|method|StopAnalyzer
specifier|public
name|StopAnalyzer
parameter_list|()
block|{
name|this
argument_list|(
name|ENGLISH_STOP_WORDS_SET
argument_list|)
expr_stmt|;
block|}
comment|/** Builds an analyzer with the stop words from the given set.    * @param stopWords Set of stop words */
DECL|method|StopAnalyzer
specifier|public
name|StopAnalyzer
parameter_list|(
name|CharArraySet
name|stopWords
parameter_list|)
block|{
name|super
argument_list|(
name|stopWords
argument_list|)
expr_stmt|;
block|}
comment|/** Builds an analyzer with the stop words from the given path.    * @see WordlistLoader#getWordSet(Reader)    * @param stopwordsFile File to load stop words from */
DECL|method|StopAnalyzer
specifier|public
name|StopAnalyzer
parameter_list|(
name|Path
name|stopwordsFile
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|loadStopwordSet
argument_list|(
name|stopwordsFile
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Builds an analyzer with the stop words from the given reader.    * @see WordlistLoader#getWordSet(Reader)    * @param stopwords Reader to load stop words from */
DECL|method|StopAnalyzer
specifier|public
name|StopAnalyzer
parameter_list|(
name|Reader
name|stopwords
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|loadStopwordSet
argument_list|(
name|stopwords
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates    * {@link org.apache.lucene.analysis.Analyzer.TokenStreamComponents}    * used to tokenize all the text in the provided {@link Reader}.    *     * @return {@link org.apache.lucene.analysis.Analyzer.TokenStreamComponents}    *         built from a {@link LowerCaseTokenizer} filtered with    *         {@link StopFilter}    */
annotation|@
name|Override
DECL|method|createComponents
specifier|protected
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
specifier|final
name|Tokenizer
name|source
init|=
operator|new
name|LowerCaseTokenizer
argument_list|()
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|source
argument_list|,
operator|new
name|StopFilter
argument_list|(
name|source
argument_list|,
name|stopwords
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
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
operator|new
name|LowerCaseFilter
argument_list|(
name|in
argument_list|)
return|;
block|}
block|}
end_class

end_unit

