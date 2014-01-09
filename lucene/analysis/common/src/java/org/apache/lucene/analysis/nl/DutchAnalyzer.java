begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.nl
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|nl
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
name|SetKeywordMarkerFilter
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
name|StemmerOverrideFilter
operator|.
name|StemmerOverrideMap
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
name|miscellaneous
operator|.
name|StemmerOverrideFilter
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
name|snowball
operator|.
name|SnowballFilter
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
name|standard
operator|.
name|StandardAnalyzer
import|;
end_import

begin_comment
comment|// for javadoc
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
name|util
operator|.
name|CharArrayMap
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
name|CharacterUtils
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
name|CharsRef
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
name|IOUtils
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
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|fst
operator|.
name|FST
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
comment|/**  * {@link Analyzer} for Dutch language.   *<p>  * Supports an external list of stopwords (words that  * will not be indexed at all), an external list of exclusions (word that will  * not be stemmed, but indexed) and an external list of word-stem pairs that overrule  * the algorithm (dictionary stemming).  * A default set of stopwords is used unless an alternative list is specified, but the  * exclusion list is empty by default.  *</p>  *   *<p><b>NOTE</b>: This class uses the same {@link Version}  * dependent settings as {@link StandardAnalyzer}.</p>  */
end_comment

begin_class
DECL|class|DutchAnalyzer
specifier|public
specifier|final
class|class
name|DutchAnalyzer
extends|extends
name|Analyzer
block|{
comment|/** File containing default Dutch stopwords. */
DECL|field|DEFAULT_STOPWORD_FILE
specifier|public
specifier|final
specifier|static
name|String
name|DEFAULT_STOPWORD_FILE
init|=
literal|"dutch_stop.txt"
decl_stmt|;
comment|/**    * Returns an unmodifiable instance of the default stop-words set.    * @return an unmodifiable instance of the default stop-words set.    */
DECL|method|getDefaultStopSet
specifier|public
specifier|static
name|CharArraySet
name|getDefaultStopSet
parameter_list|()
block|{
return|return
name|DefaultSetHolder
operator|.
name|DEFAULT_STOP_SET
return|;
block|}
DECL|class|DefaultSetHolder
specifier|private
specifier|static
class|class
name|DefaultSetHolder
block|{
DECL|field|DEFAULT_STOP_SET
specifier|static
specifier|final
name|CharArraySet
name|DEFAULT_STOP_SET
decl_stmt|;
DECL|field|DEFAULT_STEM_DICT
specifier|static
specifier|final
name|CharArrayMap
argument_list|<
name|String
argument_list|>
name|DEFAULT_STEM_DICT
decl_stmt|;
static|static
block|{
try|try
block|{
name|DEFAULT_STOP_SET
operator|=
name|WordlistLoader
operator|.
name|getSnowballWordSet
argument_list|(
name|IOUtils
operator|.
name|getDecodingReader
argument_list|(
name|SnowballFilter
operator|.
name|class
argument_list|,
name|DEFAULT_STOPWORD_FILE
argument_list|,
name|IOUtils
operator|.
name|CHARSET_UTF_8
argument_list|)
argument_list|,
name|Version
operator|.
name|LUCENE_CURRENT
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
name|DEFAULT_STEM_DICT
operator|=
operator|new
name|CharArrayMap
argument_list|<
name|String
argument_list|>
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|,
literal|4
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|DEFAULT_STEM_DICT
operator|.
name|put
argument_list|(
literal|"fiets"
argument_list|,
literal|"fiets"
argument_list|)
expr_stmt|;
comment|//otherwise fiet
name|DEFAULT_STEM_DICT
operator|.
name|put
argument_list|(
literal|"bromfiets"
argument_list|,
literal|"bromfiets"
argument_list|)
expr_stmt|;
comment|//otherwise bromfiet
name|DEFAULT_STEM_DICT
operator|.
name|put
argument_list|(
literal|"ei"
argument_list|,
literal|"eier"
argument_list|)
expr_stmt|;
name|DEFAULT_STEM_DICT
operator|.
name|put
argument_list|(
literal|"kind"
argument_list|,
literal|"kinder"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Contains the stopwords used with the StopFilter.    */
DECL|field|stoptable
specifier|private
specifier|final
name|CharArraySet
name|stoptable
decl_stmt|;
comment|/**    * Contains words that should be indexed but not stemmed.    */
DECL|field|excltable
specifier|private
name|CharArraySet
name|excltable
init|=
name|CharArraySet
operator|.
name|EMPTY_SET
decl_stmt|;
DECL|field|stemdict
specifier|private
specifier|final
name|StemmerOverrideMap
name|stemdict
decl_stmt|;
DECL|field|matchVersion
specifier|private
specifier|final
name|Version
name|matchVersion
decl_stmt|;
comment|/**    * Builds an analyzer with the default stop words ({@link #getDefaultStopSet()})     * and a few default entries for the stem exclusion table.    *     */
DECL|method|DutchAnalyzer
specifier|public
name|DutchAnalyzer
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
argument_list|,
name|CharArraySet
operator|.
name|EMPTY_SET
argument_list|,
name|DefaultSetHolder
operator|.
name|DEFAULT_STEM_DICT
argument_list|)
expr_stmt|;
block|}
DECL|method|DutchAnalyzer
specifier|public
name|DutchAnalyzer
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|CharArraySet
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
argument_list|,
name|DefaultSetHolder
operator|.
name|DEFAULT_STEM_DICT
argument_list|)
expr_stmt|;
block|}
DECL|method|DutchAnalyzer
specifier|public
name|DutchAnalyzer
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|CharArraySet
name|stopwords
parameter_list|,
name|CharArraySet
name|stemExclusionTable
parameter_list|)
block|{
name|this
argument_list|(
name|matchVersion
argument_list|,
name|stopwords
argument_list|,
name|stemExclusionTable
argument_list|,
name|DefaultSetHolder
operator|.
name|DEFAULT_STEM_DICT
argument_list|)
expr_stmt|;
block|}
DECL|method|DutchAnalyzer
specifier|public
name|DutchAnalyzer
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|CharArraySet
name|stopwords
parameter_list|,
name|CharArraySet
name|stemExclusionTable
parameter_list|,
name|CharArrayMap
argument_list|<
name|String
argument_list|>
name|stemOverrideDict
parameter_list|)
block|{
name|this
operator|.
name|matchVersion
operator|=
name|matchVersion
expr_stmt|;
name|this
operator|.
name|stoptable
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
name|stopwords
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|excltable
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
name|stemExclusionTable
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|stemOverrideDict
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|this
operator|.
name|stemdict
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
comment|// we don't need to ignore case here since we lowercase in this analyzer anyway
name|StemmerOverrideFilter
operator|.
name|Builder
name|builder
init|=
operator|new
name|StemmerOverrideFilter
operator|.
name|Builder
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|CharArrayMap
argument_list|<
name|String
argument_list|>
operator|.
name|EntryIterator
name|iter
init|=
name|stemOverrideDict
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|CharsRef
name|spare
init|=
operator|new
name|CharsRef
argument_list|()
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|char
index|[]
name|nextKey
init|=
name|iter
operator|.
name|nextKey
argument_list|()
decl_stmt|;
name|spare
operator|.
name|copyChars
argument_list|(
name|nextKey
argument_list|,
literal|0
argument_list|,
name|nextKey
operator|.
name|length
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add
argument_list|(
name|spare
argument_list|,
name|iter
operator|.
name|currentValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|this
operator|.
name|stemdict
operator|=
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"can not build stem dict"
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
block|}
comment|/**    * Returns a (possibly reused) {@link TokenStream} which tokenizes all the     * text in the provided {@link Reader}.    *    * @return A {@link TokenStream} built from a {@link StandardTokenizer}    *   filtered with {@link StandardFilter}, {@link LowerCaseFilter},     *   {@link StopFilter}, {@link SetKeywordMarkerFilter} if a stem exclusion set is provided,    *   {@link StemmerOverrideFilter}, and {@link SnowballFilter}    */
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
argument_list|(
name|matchVersion
argument_list|)
decl_stmt|;
name|TokenStream
name|result
init|=
operator|new
name|StandardFilter
argument_list|(
name|matchVersion
argument_list|,
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
name|stoptable
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|excltable
operator|.
name|isEmpty
argument_list|()
condition|)
name|result
operator|=
operator|new
name|SetKeywordMarkerFilter
argument_list|(
name|result
argument_list|,
name|excltable
argument_list|)
expr_stmt|;
if|if
condition|(
name|stemdict
operator|!=
literal|null
condition|)
name|result
operator|=
operator|new
name|StemmerOverrideFilter
argument_list|(
name|result
argument_list|,
name|stemdict
argument_list|)
expr_stmt|;
name|result
operator|=
operator|new
name|SnowballFilter
argument_list|(
name|result
argument_list|,
operator|new
name|org
operator|.
name|tartarus
operator|.
name|snowball
operator|.
name|ext
operator|.
name|DutchStemmer
argument_list|()
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

