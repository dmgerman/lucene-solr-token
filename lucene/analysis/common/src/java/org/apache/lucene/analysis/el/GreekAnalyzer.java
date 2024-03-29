begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.el
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|el
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
name|standard
operator|.
name|StandardAnalyzer
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
name|StandardFilter
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
name|StandardTokenizer
import|;
end_import

begin_comment
comment|/**  * {@link Analyzer} for the Greek language.   *<p>  * Supports an external list of stopwords (words  * that will not be indexed at all).  * A default set of stopwords is used unless an alternative list is specified.  *</p>  *   *<p><b>NOTE</b>: This class uses the same {@link org.apache.lucene.util.Version}  * dependent settings as {@link StandardAnalyzer}.</p>  */
end_comment

begin_class
DECL|class|GreekAnalyzer
specifier|public
specifier|final
class|class
name|GreekAnalyzer
extends|extends
name|StopwordAnalyzerBase
block|{
comment|/** File containing default Greek stopwords. */
DECL|field|DEFAULT_STOPWORD_FILE
specifier|public
specifier|final
specifier|static
name|String
name|DEFAULT_STOPWORD_FILE
init|=
literal|"stopwords.txt"
decl_stmt|;
comment|/**    * Returns a set of default Greek-stopwords     * @return a set of default Greek-stopwords     */
DECL|method|getDefaultStopSet
specifier|public
specifier|static
specifier|final
name|CharArraySet
name|getDefaultStopSet
parameter_list|()
block|{
return|return
name|DefaultSetHolder
operator|.
name|DEFAULT_SET
return|;
block|}
DECL|class|DefaultSetHolder
specifier|private
specifier|static
class|class
name|DefaultSetHolder
block|{
DECL|field|DEFAULT_SET
specifier|private
specifier|static
specifier|final
name|CharArraySet
name|DEFAULT_SET
decl_stmt|;
static|static
block|{
try|try
block|{
name|DEFAULT_SET
operator|=
name|loadStopwordSet
argument_list|(
literal|false
argument_list|,
name|GreekAnalyzer
operator|.
name|class
argument_list|,
name|DEFAULT_STOPWORD_FILE
argument_list|,
literal|"#"
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
comment|/**    * Builds an analyzer with the default stop words.    */
DECL|method|GreekAnalyzer
specifier|public
name|GreekAnalyzer
parameter_list|()
block|{
name|this
argument_list|(
name|DefaultSetHolder
operator|.
name|DEFAULT_SET
argument_list|)
expr_stmt|;
block|}
comment|/**    * Builds an analyzer with the given stop words.     *<p>    *<b>NOTE:</b> The stopwords set should be pre-processed with the logic of     * {@link GreekLowerCaseFilter} for best results.    *      * @param stopwords a stopword set    */
DECL|method|GreekAnalyzer
specifier|public
name|GreekAnalyzer
parameter_list|(
name|CharArraySet
name|stopwords
parameter_list|)
block|{
name|super
argument_list|(
name|stopwords
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates    * {@link org.apache.lucene.analysis.Analyzer.TokenStreamComponents}    * used to tokenize all the text in the provided {@link Reader}.    *     * @return {@link org.apache.lucene.analysis.Analyzer.TokenStreamComponents}    *         built from a {@link StandardTokenizer} filtered with    *         {@link GreekLowerCaseFilter}, {@link StandardFilter},    *         {@link StopFilter}, and {@link GreekStemFilter}    */
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
name|StandardTokenizer
argument_list|()
decl_stmt|;
name|TokenStream
name|result
init|=
operator|new
name|GreekLowerCaseFilter
argument_list|(
name|source
argument_list|)
decl_stmt|;
name|result
operator|=
operator|new
name|StandardFilter
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|result
operator|=
operator|new
name|StopFilter
argument_list|(
name|result
argument_list|,
name|stopwords
argument_list|)
expr_stmt|;
name|result
operator|=
operator|new
name|GreekStemFilter
argument_list|(
name|result
argument_list|)
expr_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|source
argument_list|,
name|result
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
name|TokenStream
name|result
init|=
operator|new
name|StandardFilter
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|result
operator|=
operator|new
name|GreekLowerCaseFilter
argument_list|(
name|result
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

