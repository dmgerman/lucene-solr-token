begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
name|Arrays
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
name|java
operator|.
name|util
operator|.
name|List
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
name|util
operator|.
name|Version
import|;
end_import

begin_comment
comment|/** Filters {@link LetterTokenizer} with {@link LowerCaseFilter} and {@link StopFilter}.  *  *<a name="version"/>  *<p>You must specify the required {@link Version}  * compatibility when creating StopAnalyzer:  *<ul>  *<li> As of 3.1, StopFilter correctly handles Unicode 4.0  *         supplementary characters in stopwords  *<li> As of 2.9, position increments are preserved  *</ul> */
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
name|Set
argument_list|<
name|?
argument_list|>
name|ENGLISH_STOP_WORDS_SET
decl_stmt|;
static|static
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|stopWords
init|=
name|Arrays
operator|.
name|asList
argument_list|(
literal|"a"
argument_list|,
literal|"an"
argument_list|,
literal|"and"
argument_list|,
literal|"are"
argument_list|,
literal|"as"
argument_list|,
literal|"at"
argument_list|,
literal|"be"
argument_list|,
literal|"but"
argument_list|,
literal|"by"
argument_list|,
literal|"for"
argument_list|,
literal|"if"
argument_list|,
literal|"in"
argument_list|,
literal|"into"
argument_list|,
literal|"is"
argument_list|,
literal|"it"
argument_list|,
literal|"no"
argument_list|,
literal|"not"
argument_list|,
literal|"of"
argument_list|,
literal|"on"
argument_list|,
literal|"or"
argument_list|,
literal|"such"
argument_list|,
literal|"that"
argument_list|,
literal|"the"
argument_list|,
literal|"their"
argument_list|,
literal|"then"
argument_list|,
literal|"there"
argument_list|,
literal|"these"
argument_list|,
literal|"they"
argument_list|,
literal|"this"
argument_list|,
literal|"to"
argument_list|,
literal|"was"
argument_list|,
literal|"will"
argument_list|,
literal|"with"
argument_list|)
decl_stmt|;
specifier|final
name|CharArraySet
name|stopSet
init|=
operator|new
name|CharArraySet
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|,
name|stopWords
operator|.
name|size
argument_list|()
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|stopSet
operator|.
name|addAll
argument_list|(
name|stopWords
argument_list|)
expr_stmt|;
name|ENGLISH_STOP_WORDS_SET
operator|=
name|CharArraySet
operator|.
name|unmodifiableSet
argument_list|(
name|stopSet
argument_list|)
expr_stmt|;
block|}
comment|/** Builds an analyzer which removes words in    *  {@link #ENGLISH_STOP_WORDS_SET}.    * @param matchVersion See<a href="#version">above</a>    */
DECL|method|StopAnalyzer
specifier|public
name|StopAnalyzer
parameter_list|(
name|Version
name|matchVersion
parameter_list|)
block|{
name|this
argument_list|(
name|matchVersion
argument_list|,
name|ENGLISH_STOP_WORDS_SET
argument_list|)
expr_stmt|;
block|}
comment|/** Builds an analyzer with the stop words from the given set.    * @param matchVersion See<a href="#version">above</a>    * @param stopWords Set of stop words */
DECL|method|StopAnalyzer
specifier|public
name|StopAnalyzer
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
name|super
argument_list|(
name|matchVersion
argument_list|,
name|stopWords
argument_list|)
expr_stmt|;
block|}
comment|/** Builds an analyzer with the stop words from the given file.    * @see WordlistLoader#getWordSet(File)    * @param matchVersion See<a href="#version">above</a>    * @param stopwordsFile File to load stop words from */
DECL|method|StopAnalyzer
specifier|public
name|StopAnalyzer
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|File
name|stopwordsFile
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
name|stopwordsFile
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Builds an analyzer with the stop words from the given reader.    * @see WordlistLoader#getWordSet(Reader)    * @param matchVersion See<a href="#version">above</a>    * @param stopwords Reader to load stop words from */
DECL|method|StopAnalyzer
specifier|public
name|StopAnalyzer
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
comment|/**    * Creates {@link TokenStreamComponents} used to tokenize all the text in the provided {@link Reader}.    *    * @return {@link TokenStreamComponents} built from a {@link LowerCaseTokenizer} filtered with    *         {@link StopFilter}    */
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
name|LowerCaseTokenizer
argument_list|(
name|reader
argument_list|)
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
name|matchVersion
argument_list|,
name|source
argument_list|,
name|stopwords
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

