begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.pl
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|pl
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
name|BufferedInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInputStream
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
name|InputStream
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
name|miscellaneous
operator|.
name|KeywordMarkerFilter
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
name|stempel
operator|.
name|StempelStemmer
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
name|stempel
operator|.
name|StempelFilter
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
name|analysis
operator|.
name|util
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

begin_import
import|import
name|org
operator|.
name|egothor
operator|.
name|stemmer
operator|.
name|Trie
import|;
end_import

begin_comment
comment|/**  * {@link Analyzer} for Polish.  */
end_comment

begin_class
DECL|class|PolishAnalyzer
specifier|public
specifier|final
class|class
name|PolishAnalyzer
extends|extends
name|StopwordAnalyzerBase
block|{
DECL|field|stemExclusionSet
specifier|private
specifier|final
name|Set
argument_list|<
name|?
argument_list|>
name|stemExclusionSet
decl_stmt|;
DECL|field|stemTable
specifier|private
specifier|final
name|Trie
name|stemTable
decl_stmt|;
comment|/** File containing default Polish stopwords. */
DECL|field|DEFAULT_STOPWORD_FILE
specifier|public
specifier|final
specifier|static
name|String
name|DEFAULT_STOPWORD_FILE
init|=
literal|"stopwords.txt"
decl_stmt|;
comment|/**    * Returns an unmodifiable instance of the default stop words set.    * @return default stop words set.    */
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
name|DefaultsHolder
operator|.
name|DEFAULT_STOP_SET
return|;
block|}
comment|/**    * Atomically loads the DEFAULT_STOP_SET in a lazy fashion once the outer class     * accesses the static final set the first time.;    */
DECL|class|DefaultsHolder
specifier|private
specifier|static
class|class
name|DefaultsHolder
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
DECL|field|DEFAULT_TABLE
specifier|static
specifier|final
name|Trie
name|DEFAULT_TABLE
decl_stmt|;
static|static
block|{
try|try
block|{
name|DEFAULT_STOP_SET
operator|=
name|WordlistLoader
operator|.
name|getWordSet
argument_list|(
name|PolishAnalyzer
operator|.
name|class
argument_list|,
name|DEFAULT_STOPWORD_FILE
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
argument_list|,
name|ex
argument_list|)
throw|;
block|}
name|InputStream
name|stream
init|=
name|PolishAnalyzer
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"stemmer_20000.tbl"
argument_list|)
decl_stmt|;
try|try
block|{
name|DataInputStream
name|in
init|=
operator|new
name|DataInputStream
argument_list|(
operator|new
name|BufferedInputStream
argument_list|(
name|stream
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|method
init|=
name|in
operator|.
name|readUTF
argument_list|()
operator|.
name|toUpperCase
argument_list|()
decl_stmt|;
if|if
condition|(
name|method
operator|.
name|indexOf
argument_list|(
literal|'M'
argument_list|)
operator|<
literal|0
condition|)
block|{
name|DEFAULT_TABLE
operator|=
operator|new
name|org
operator|.
name|egothor
operator|.
name|stemmer
operator|.
name|Trie
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|DEFAULT_TABLE
operator|=
operator|new
name|org
operator|.
name|egothor
operator|.
name|stemmer
operator|.
name|MultiTrie2
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
name|in
operator|.
name|close
argument_list|()
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
literal|"Unable to load default stemming tables"
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
block|}
comment|/**    * Builds an analyzer with the default stop words: {@link #DEFAULT_STOPWORD_FILE}.    */
DECL|method|PolishAnalyzer
specifier|public
name|PolishAnalyzer
parameter_list|(
name|Version
name|matchVersion
parameter_list|)
block|{
name|this
argument_list|(
name|matchVersion
argument_list|,
name|DefaultsHolder
operator|.
name|DEFAULT_STOP_SET
argument_list|)
expr_stmt|;
block|}
comment|/**    * Builds an analyzer with the given stop words.    *     * @param matchVersion lucene compatibility version    * @param stopwords a stopword set    */
DECL|method|PolishAnalyzer
specifier|public
name|PolishAnalyzer
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
comment|/**    * Builds an analyzer with the given stop words. If a non-empty stem exclusion set is    * provided this analyzer will add a {@link KeywordMarkerTokenFilter} before    * stemming.    *     * @param matchVersion lucene compatibility version    * @param stopwords a stopword set    * @param stemExclusionSet a set of terms not to be stemmed    */
DECL|method|PolishAnalyzer
specifier|public
name|PolishAnalyzer
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
name|stemTable
operator|=
name|DefaultsHolder
operator|.
name|DEFAULT_TABLE
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
comment|/**    * Creates a    * {@link org.apache.lucene.analysis.util.ReusableAnalyzerBase.TokenStreamComponents}    * which tokenizes all the text in the provided {@link Reader}.    *     * @return A    *         {@link org.apache.lucene.analysis.util.ReusableAnalyzerBase.TokenStreamComponents}    *         built from an {@link StandardTokenizer} filtered with    *         {@link StandardFilter}, {@link LowerCaseFilter}, {@link StopFilter}    *         , {@link KeywordMarkerFilter} if a stem exclusion set is    *         provided and {@link StempelFilter}.    */
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
name|StandardTokenizer
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
name|StandardFilter
argument_list|(
name|source
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
name|stopwords
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
name|result
operator|=
operator|new
name|KeywordMarkerFilter
argument_list|(
name|result
argument_list|,
name|stemExclusionSet
argument_list|)
expr_stmt|;
name|result
operator|=
operator|new
name|StempelFilter
argument_list|(
name|result
argument_list|,
operator|new
name|StempelStemmer
argument_list|(
name|stemTable
argument_list|)
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
block|}
end_class

end_unit

