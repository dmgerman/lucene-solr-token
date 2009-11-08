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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|Map
import|;
end_import

begin_comment
comment|/**  * {@link Analyzer} for Dutch language.   *<p>  * Supports an external list of stopwords (words that  * will not be indexed at all), an external list of exclusions (word that will  * not be stemmed, but indexed) and an external list of word-stem pairs that overrule  * the algorithm (dictionary stemming).  * A default set of stopwords is used unless an alternative list is specified, but the  * exclusion list is empty by default.  *</p>  *  *<p><b>NOTE</b>: This class uses the same {@link Version}  * dependent settings as {@link StandardAnalyzer}.</p>  */
end_comment

begin_class
DECL|class|DutchAnalyzer
specifier|public
class|class
name|DutchAnalyzer
extends|extends
name|Analyzer
block|{
comment|/**    * List of typical Dutch stopwords.    */
DECL|field|DUTCH_STOP_WORDS
specifier|public
specifier|final
specifier|static
name|String
index|[]
name|DUTCH_STOP_WORDS
init|=
block|{
literal|"de"
block|,
literal|"en"
block|,
literal|"van"
block|,
literal|"ik"
block|,
literal|"te"
block|,
literal|"dat"
block|,
literal|"die"
block|,
literal|"in"
block|,
literal|"een"
block|,
literal|"hij"
block|,
literal|"het"
block|,
literal|"niet"
block|,
literal|"zijn"
block|,
literal|"is"
block|,
literal|"was"
block|,
literal|"op"
block|,
literal|"aan"
block|,
literal|"met"
block|,
literal|"als"
block|,
literal|"voor"
block|,
literal|"had"
block|,
literal|"er"
block|,
literal|"maar"
block|,
literal|"om"
block|,
literal|"hem"
block|,
literal|"dan"
block|,
literal|"zou"
block|,
literal|"of"
block|,
literal|"wat"
block|,
literal|"mijn"
block|,
literal|"men"
block|,
literal|"dit"
block|,
literal|"zo"
block|,
literal|"door"
block|,
literal|"over"
block|,
literal|"ze"
block|,
literal|"zich"
block|,
literal|"bij"
block|,
literal|"ook"
block|,
literal|"tot"
block|,
literal|"je"
block|,
literal|"mij"
block|,
literal|"uit"
block|,
literal|"der"
block|,
literal|"daar"
block|,
literal|"haar"
block|,
literal|"naar"
block|,
literal|"heb"
block|,
literal|"hoe"
block|,
literal|"heeft"
block|,
literal|"hebben"
block|,
literal|"deze"
block|,
literal|"u"
block|,
literal|"want"
block|,
literal|"nog"
block|,
literal|"zal"
block|,
literal|"me"
block|,
literal|"zij"
block|,
literal|"nu"
block|,
literal|"ge"
block|,
literal|"geen"
block|,
literal|"omdat"
block|,
literal|"iets"
block|,
literal|"worden"
block|,
literal|"toch"
block|,
literal|"al"
block|,
literal|"waren"
block|,
literal|"veel"
block|,
literal|"meer"
block|,
literal|"doen"
block|,
literal|"toen"
block|,
literal|"moet"
block|,
literal|"ben"
block|,
literal|"zonder"
block|,
literal|"kan"
block|,
literal|"hun"
block|,
literal|"dus"
block|,
literal|"alles"
block|,
literal|"onder"
block|,
literal|"ja"
block|,
literal|"eens"
block|,
literal|"hier"
block|,
literal|"wie"
block|,
literal|"werd"
block|,
literal|"altijd"
block|,
literal|"doch"
block|,
literal|"wordt"
block|,
literal|"wezen"
block|,
literal|"kunnen"
block|,
literal|"ons"
block|,
literal|"zelf"
block|,
literal|"tegen"
block|,
literal|"na"
block|,
literal|"reeds"
block|,
literal|"wil"
block|,
literal|"kon"
block|,
literal|"niets"
block|,
literal|"uw"
block|,
literal|"iemand"
block|,
literal|"geweest"
block|,
literal|"andere"
block|}
decl_stmt|;
comment|/**    * Contains the stopwords used with the StopFilter.    */
DECL|field|stoptable
specifier|private
name|Set
name|stoptable
init|=
operator|new
name|HashSet
argument_list|()
decl_stmt|;
comment|/**    * Contains words that should be indexed but not stemmed.    */
DECL|field|excltable
specifier|private
name|Set
name|excltable
init|=
operator|new
name|HashSet
argument_list|()
decl_stmt|;
DECL|field|stemdict
specifier|private
name|Map
name|stemdict
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
DECL|field|matchVersion
specifier|private
specifier|final
name|Version
name|matchVersion
decl_stmt|;
comment|/**    * Builds an analyzer with the default stop words ({@link #DUTCH_STOP_WORDS})     * and a few default entries for the stem exclusion table.    *     */
DECL|method|DutchAnalyzer
specifier|public
name|DutchAnalyzer
parameter_list|(
name|Version
name|matchVersion
parameter_list|)
block|{
name|setOverridesTokenStreamMethod
argument_list|(
name|DutchAnalyzer
operator|.
name|class
argument_list|)
expr_stmt|;
name|stoptable
operator|=
name|StopFilter
operator|.
name|makeStopSet
argument_list|(
name|DUTCH_STOP_WORDS
argument_list|)
expr_stmt|;
name|stemdict
operator|.
name|put
argument_list|(
literal|"fiets"
argument_list|,
literal|"fiets"
argument_list|)
expr_stmt|;
comment|//otherwise fiet
name|stemdict
operator|.
name|put
argument_list|(
literal|"bromfiets"
argument_list|,
literal|"bromfiets"
argument_list|)
expr_stmt|;
comment|//otherwise bromfiet
name|stemdict
operator|.
name|put
argument_list|(
literal|"ei"
argument_list|,
literal|"eier"
argument_list|)
expr_stmt|;
name|stemdict
operator|.
name|put
argument_list|(
literal|"kind"
argument_list|,
literal|"kinder"
argument_list|)
expr_stmt|;
name|this
operator|.
name|matchVersion
operator|=
name|matchVersion
expr_stmt|;
block|}
comment|/**    * Builds an analyzer with the given stop words.    *    * @param matchVersion    * @param stopwords    */
DECL|method|DutchAnalyzer
specifier|public
name|DutchAnalyzer
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|String
modifier|...
name|stopwords
parameter_list|)
block|{
name|setOverridesTokenStreamMethod
argument_list|(
name|DutchAnalyzer
operator|.
name|class
argument_list|)
expr_stmt|;
name|stoptable
operator|=
name|StopFilter
operator|.
name|makeStopSet
argument_list|(
name|stopwords
argument_list|)
expr_stmt|;
name|this
operator|.
name|matchVersion
operator|=
name|matchVersion
expr_stmt|;
block|}
comment|/**    * Builds an analyzer with the given stop words.    *    * @param stopwords    */
DECL|method|DutchAnalyzer
specifier|public
name|DutchAnalyzer
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|HashSet
name|stopwords
parameter_list|)
block|{
name|setOverridesTokenStreamMethod
argument_list|(
name|DutchAnalyzer
operator|.
name|class
argument_list|)
expr_stmt|;
name|stoptable
operator|=
name|stopwords
expr_stmt|;
name|this
operator|.
name|matchVersion
operator|=
name|matchVersion
expr_stmt|;
block|}
comment|/**    * Builds an analyzer with the given stop words.    *    * @param stopwords    */
DECL|method|DutchAnalyzer
specifier|public
name|DutchAnalyzer
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|File
name|stopwords
parameter_list|)
block|{
name|setOverridesTokenStreamMethod
argument_list|(
name|DutchAnalyzer
operator|.
name|class
argument_list|)
expr_stmt|;
try|try
block|{
name|stoptable
operator|=
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|WordlistLoader
operator|.
name|getWordSet
argument_list|(
name|stopwords
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// TODO: throw IOException
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|this
operator|.
name|matchVersion
operator|=
name|matchVersion
expr_stmt|;
block|}
comment|/**    * Builds an exclusionlist from an array of Strings.    *    * @param exclusionlist    */
DECL|method|setStemExclusionTable
specifier|public
name|void
name|setStemExclusionTable
parameter_list|(
name|String
modifier|...
name|exclusionlist
parameter_list|)
block|{
name|excltable
operator|=
name|StopFilter
operator|.
name|makeStopSet
argument_list|(
name|exclusionlist
argument_list|)
expr_stmt|;
name|setPreviousTokenStream
argument_list|(
literal|null
argument_list|)
expr_stmt|;
comment|// force a new stemmer to be created
block|}
comment|/**    * Builds an exclusionlist from a Hashtable.    */
DECL|method|setStemExclusionTable
specifier|public
name|void
name|setStemExclusionTable
parameter_list|(
name|HashSet
name|exclusionlist
parameter_list|)
block|{
name|excltable
operator|=
name|exclusionlist
expr_stmt|;
name|setPreviousTokenStream
argument_list|(
literal|null
argument_list|)
expr_stmt|;
comment|// force a new stemmer to be created
block|}
comment|/**    * Builds an exclusionlist from the words contained in the given file.    */
DECL|method|setStemExclusionTable
specifier|public
name|void
name|setStemExclusionTable
parameter_list|(
name|File
name|exclusionlist
parameter_list|)
block|{
try|try
block|{
name|excltable
operator|=
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|WordlistLoader
operator|.
name|getWordSet
argument_list|(
name|exclusionlist
argument_list|)
expr_stmt|;
name|setPreviousTokenStream
argument_list|(
literal|null
argument_list|)
expr_stmt|;
comment|// force a new stemmer to be created
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// TODO: throw IOException
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * Reads a stemdictionary file , that overrules the stemming algorithm    * This is a textfile that contains per line    *<tt>word<b>\t</b>stem</tt>, i.e: two tab seperated words    */
DECL|method|setStemDictionary
specifier|public
name|void
name|setStemDictionary
parameter_list|(
name|File
name|stemdictFile
parameter_list|)
block|{
try|try
block|{
name|stemdict
operator|=
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|WordlistLoader
operator|.
name|getStemDict
argument_list|(
name|stemdictFile
argument_list|)
expr_stmt|;
name|setPreviousTokenStream
argument_list|(
literal|null
argument_list|)
expr_stmt|;
comment|// force a new stemmer to be created
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// TODO: throw IOException
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * Creates a {@link TokenStream} which tokenizes all the text in the     * provided {@link Reader}.    *    * @return A {@link TokenStream} built from a {@link StandardTokenizer}    *   filtered with {@link StandardFilter}, {@link StopFilter},     *   and {@link DutchStemFilter}    */
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
name|TokenStream
name|result
init|=
operator|new
name|StandardTokenizer
argument_list|(
name|matchVersion
argument_list|,
name|reader
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
name|StopFilter
operator|.
name|getEnablePositionIncrementsVersionDefault
argument_list|(
name|matchVersion
argument_list|)
argument_list|,
name|result
argument_list|,
name|stoptable
argument_list|)
expr_stmt|;
name|result
operator|=
operator|new
name|DutchStemFilter
argument_list|(
name|result
argument_list|,
name|excltable
argument_list|,
name|stemdict
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|class|SavedStreams
specifier|private
class|class
name|SavedStreams
block|{
DECL|field|source
name|Tokenizer
name|source
decl_stmt|;
DECL|field|result
name|TokenStream
name|result
decl_stmt|;
block|}
empty_stmt|;
comment|/**    * Returns a (possibly reused) {@link TokenStream} which tokenizes all the     * text in the provided {@link Reader}.    *    * @return A {@link TokenStream} built from a {@link StandardTokenizer}    *   filtered with {@link StandardFilter}, {@link StopFilter},     *   and {@link DutchStemFilter}    */
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
name|streams
operator|.
name|source
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
name|result
operator|=
operator|new
name|StandardFilter
argument_list|(
name|streams
operator|.
name|source
argument_list|)
expr_stmt|;
name|streams
operator|.
name|result
operator|=
operator|new
name|StopFilter
argument_list|(
name|StopFilter
operator|.
name|getEnablePositionIncrementsVersionDefault
argument_list|(
name|matchVersion
argument_list|)
argument_list|,
name|streams
operator|.
name|result
argument_list|,
name|stoptable
argument_list|)
expr_stmt|;
name|streams
operator|.
name|result
operator|=
operator|new
name|DutchStemFilter
argument_list|(
name|streams
operator|.
name|result
argument_list|,
name|excltable
argument_list|,
name|stemdict
argument_list|)
expr_stmt|;
name|setPreviousTokenStream
argument_list|(
name|streams
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|streams
operator|.
name|source
operator|.
name|reset
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
return|return
name|streams
operator|.
name|result
return|;
block|}
block|}
end_class

end_unit

