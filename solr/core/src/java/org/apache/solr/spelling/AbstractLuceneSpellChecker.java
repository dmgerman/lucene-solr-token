begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.spelling
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|spelling
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|spell
operator|.
name|StringDistance
import|;
end_import

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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|search
operator|.
name|spell
operator|.
name|SuggestMode
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
name|search
operator|.
name|spell
operator|.
name|SuggestWord
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
name|search
operator|.
name|spell
operator|.
name|SuggestWordFrequencyComparator
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
name|search
operator|.
name|spell
operator|.
name|SuggestWordQueue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|Token
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
name|WhitespaceAnalyzer
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
name|index
operator|.
name|IndexReader
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
name|index
operator|.
name|Term
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
name|search
operator|.
name|spell
operator|.
name|Dictionary
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
name|search
operator|.
name|spell
operator|.
name|LevensteinDistance
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
name|search
operator|.
name|spell
operator|.
name|SpellChecker
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
name|Directory
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
name|FSDirectory
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
name|RAMDirectory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
operator|.
name|ShardParams
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
operator|.
name|SolrParams
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|NamedList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
operator|.
name|SolrCore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
operator|.
name|FieldType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|SolrIndexSearcher
import|;
end_import

begin_comment
comment|/**  * Abstract base class for all Lucene-based spell checking implementations.  *   *<p>  * Refer to<a href="http://wiki.apache.org/solr/SpellCheckComponent">SpellCheckComponent</a>  * for more details.  *</p>  *   * @since solr 1.3  */
end_comment

begin_class
DECL|class|AbstractLuceneSpellChecker
specifier|public
specifier|abstract
class|class
name|AbstractLuceneSpellChecker
extends|extends
name|SolrSpellChecker
block|{
DECL|field|log
specifier|public
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|AbstractLuceneSpellChecker
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|SPELLCHECKER_ARG_NAME
specifier|public
specifier|static
specifier|final
name|String
name|SPELLCHECKER_ARG_NAME
init|=
literal|"spellchecker"
decl_stmt|;
DECL|field|LOCATION
specifier|public
specifier|static
specifier|final
name|String
name|LOCATION
init|=
literal|"sourceLocation"
decl_stmt|;
DECL|field|INDEX_DIR
specifier|public
specifier|static
specifier|final
name|String
name|INDEX_DIR
init|=
literal|"spellcheckIndexDir"
decl_stmt|;
DECL|field|ACCURACY
specifier|public
specifier|static
specifier|final
name|String
name|ACCURACY
init|=
literal|"accuracy"
decl_stmt|;
DECL|field|STRING_DISTANCE
specifier|public
specifier|static
specifier|final
name|String
name|STRING_DISTANCE
init|=
literal|"distanceMeasure"
decl_stmt|;
DECL|field|COMPARATOR_CLASS
specifier|public
specifier|static
specifier|final
name|String
name|COMPARATOR_CLASS
init|=
literal|"comparatorClass"
decl_stmt|;
DECL|field|SCORE_COMP
specifier|public
specifier|static
specifier|final
name|String
name|SCORE_COMP
init|=
literal|"score"
decl_stmt|;
DECL|field|FREQ_COMP
specifier|public
specifier|static
specifier|final
name|String
name|FREQ_COMP
init|=
literal|"freq"
decl_stmt|;
DECL|field|spellChecker
specifier|protected
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|spell
operator|.
name|SpellChecker
name|spellChecker
decl_stmt|;
DECL|field|sourceLocation
specifier|protected
name|String
name|sourceLocation
decl_stmt|;
comment|/*   * The Directory containing the Spell checking index   * */
DECL|field|index
specifier|protected
name|Directory
name|index
decl_stmt|;
DECL|field|dictionary
specifier|protected
name|Dictionary
name|dictionary
decl_stmt|;
DECL|field|DEFAULT_SUGGESTION_COUNT
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_SUGGESTION_COUNT
init|=
literal|5
decl_stmt|;
DECL|field|indexDir
specifier|protected
name|String
name|indexDir
decl_stmt|;
DECL|field|accuracy
specifier|protected
name|float
name|accuracy
init|=
literal|0.5f
decl_stmt|;
DECL|field|FIELD
specifier|public
specifier|static
specifier|final
name|String
name|FIELD
init|=
literal|"field"
decl_stmt|;
DECL|field|sd
specifier|protected
name|StringDistance
name|sd
decl_stmt|;
annotation|@
name|Override
DECL|method|init
specifier|public
name|String
name|init
parameter_list|(
name|NamedList
name|config
parameter_list|,
name|SolrCore
name|core
parameter_list|)
block|{
name|super
operator|.
name|init
argument_list|(
name|config
argument_list|,
name|core
argument_list|)
expr_stmt|;
name|indexDir
operator|=
operator|(
name|String
operator|)
name|config
operator|.
name|get
argument_list|(
name|INDEX_DIR
argument_list|)
expr_stmt|;
name|String
name|accuracy
init|=
operator|(
name|String
operator|)
name|config
operator|.
name|get
argument_list|(
name|ACCURACY
argument_list|)
decl_stmt|;
comment|//If indexDir is relative then create index inside core.getDataDir()
if|if
condition|(
name|indexDir
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
operator|new
name|File
argument_list|(
name|indexDir
argument_list|)
operator|.
name|isAbsolute
argument_list|()
condition|)
block|{
name|indexDir
operator|=
name|core
operator|.
name|getDataDir
argument_list|()
operator|+
name|File
operator|.
name|separator
operator|+
name|indexDir
expr_stmt|;
block|}
block|}
name|sourceLocation
operator|=
operator|(
name|String
operator|)
name|config
operator|.
name|get
argument_list|(
name|LOCATION
argument_list|)
expr_stmt|;
name|String
name|compClass
init|=
operator|(
name|String
operator|)
name|config
operator|.
name|get
argument_list|(
name|COMPARATOR_CLASS
argument_list|)
decl_stmt|;
name|Comparator
argument_list|<
name|SuggestWord
argument_list|>
name|comp
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|compClass
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|compClass
operator|.
name|equalsIgnoreCase
argument_list|(
name|SCORE_COMP
argument_list|)
condition|)
block|{
name|comp
operator|=
name|SuggestWordQueue
operator|.
name|DEFAULT_COMPARATOR
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|compClass
operator|.
name|equalsIgnoreCase
argument_list|(
name|FREQ_COMP
argument_list|)
condition|)
block|{
name|comp
operator|=
operator|new
name|SuggestWordFrequencyComparator
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|//must be a FQCN
name|comp
operator|=
operator|(
name|Comparator
argument_list|<
name|SuggestWord
argument_list|>
operator|)
name|core
operator|.
name|getResourceLoader
argument_list|()
operator|.
name|newInstance
argument_list|(
name|compClass
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|comp
operator|=
name|SuggestWordQueue
operator|.
name|DEFAULT_COMPARATOR
expr_stmt|;
block|}
name|String
name|strDistanceName
init|=
operator|(
name|String
operator|)
name|config
operator|.
name|get
argument_list|(
name|STRING_DISTANCE
argument_list|)
decl_stmt|;
if|if
condition|(
name|strDistanceName
operator|!=
literal|null
condition|)
block|{
name|sd
operator|=
operator|(
name|StringDistance
operator|)
name|core
operator|.
name|getResourceLoader
argument_list|()
operator|.
name|newInstance
argument_list|(
name|strDistanceName
argument_list|)
expr_stmt|;
comment|//TODO: Figure out how to configure options.  Where's Spring when you need it?  Or at least BeanUtils...
block|}
else|else
block|{
name|sd
operator|=
operator|new
name|LevensteinDistance
argument_list|()
expr_stmt|;
block|}
try|try
block|{
name|initIndex
argument_list|()
expr_stmt|;
name|spellChecker
operator|=
operator|new
name|SpellChecker
argument_list|(
name|index
argument_list|,
name|sd
argument_list|,
name|comp
argument_list|)
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
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
if|if
condition|(
name|accuracy
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|this
operator|.
name|accuracy
operator|=
name|Float
operator|.
name|parseFloat
argument_list|(
name|accuracy
argument_list|)
expr_stmt|;
name|spellChecker
operator|.
name|setAccuracy
argument_list|(
name|this
operator|.
name|accuracy
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unparseable accuracy given for dictionary: "
operator|+
name|name
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
return|return
name|name
return|;
block|}
annotation|@
name|Override
DECL|method|getSuggestions
specifier|public
name|SpellingResult
name|getSuggestions
parameter_list|(
name|SpellingOptions
name|options
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|shardRequest
init|=
literal|false
decl_stmt|;
name|SolrParams
name|params
init|=
name|options
operator|.
name|customParams
decl_stmt|;
if|if
condition|(
name|params
operator|!=
literal|null
condition|)
block|{
name|shardRequest
operator|=
literal|"true"
operator|.
name|equals
argument_list|(
name|params
operator|.
name|get
argument_list|(
name|ShardParams
operator|.
name|IS_SHARD
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|SpellingResult
name|result
init|=
operator|new
name|SpellingResult
argument_list|(
name|options
operator|.
name|tokens
argument_list|)
decl_stmt|;
name|IndexReader
name|reader
init|=
name|determineReader
argument_list|(
name|options
operator|.
name|reader
argument_list|)
decl_stmt|;
name|Term
name|term
init|=
name|field
operator|!=
literal|null
condition|?
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|""
argument_list|)
else|:
literal|null
decl_stmt|;
name|float
name|theAccuracy
init|=
operator|(
name|options
operator|.
name|accuracy
operator|==
name|Float
operator|.
name|MIN_VALUE
operator|)
condition|?
name|spellChecker
operator|.
name|getAccuracy
argument_list|()
else|:
name|options
operator|.
name|accuracy
decl_stmt|;
name|int
name|count
init|=
name|Math
operator|.
name|max
argument_list|(
name|options
operator|.
name|count
argument_list|,
name|AbstractLuceneSpellChecker
operator|.
name|DEFAULT_SUGGESTION_COUNT
argument_list|)
decl_stmt|;
name|SuggestMode
name|mode
init|=
name|options
operator|.
name|onlyMorePopular
condition|?
name|SuggestMode
operator|.
name|SUGGEST_MORE_POPULAR
else|:
name|SuggestMode
operator|.
name|SUGGEST_WHEN_NOT_IN_INDEX
decl_stmt|;
for|for
control|(
name|Token
name|token
range|:
name|options
operator|.
name|tokens
control|)
block|{
name|String
name|tokenText
init|=
operator|new
name|String
argument_list|(
name|token
operator|.
name|buffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|token
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|String
index|[]
name|suggestions
init|=
name|spellChecker
operator|.
name|suggestSimilar
argument_list|(
name|tokenText
argument_list|,
name|count
argument_list|,
name|field
operator|!=
literal|null
condition|?
name|reader
else|:
literal|null
argument_list|,
comment|//workaround LUCENE-1295
name|field
argument_list|,
name|mode
argument_list|,
name|theAccuracy
argument_list|)
decl_stmt|;
if|if
condition|(
name|suggestions
operator|.
name|length
operator|==
literal|1
operator|&&
name|suggestions
index|[
literal|0
index|]
operator|.
name|equals
argument_list|(
name|tokenText
argument_list|)
condition|)
block|{
comment|//These are spelled the same, continue on
continue|continue;
block|}
if|if
condition|(
name|options
operator|.
name|extendedResults
operator|==
literal|true
operator|&&
name|reader
operator|!=
literal|null
operator|&&
name|field
operator|!=
literal|null
condition|)
block|{
name|term
operator|=
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|tokenText
argument_list|)
expr_stmt|;
name|result
operator|.
name|addFrequency
argument_list|(
name|token
argument_list|,
name|reader
operator|.
name|docFreq
argument_list|(
name|term
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|countLimit
init|=
name|Math
operator|.
name|min
argument_list|(
name|options
operator|.
name|count
argument_list|,
name|suggestions
operator|.
name|length
argument_list|)
decl_stmt|;
if|if
condition|(
name|countLimit
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|countLimit
condition|;
name|i
operator|++
control|)
block|{
name|term
operator|=
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|suggestions
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|token
argument_list|,
name|suggestions
index|[
name|i
index|]
argument_list|,
name|reader
operator|.
name|docFreq
argument_list|(
name|term
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|shardRequest
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|suggList
init|=
name|Collections
operator|.
name|emptyList
argument_list|()
decl_stmt|;
name|result
operator|.
name|add
argument_list|(
name|token
argument_list|,
name|suggList
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|suggestions
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|suggList
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|suggestions
argument_list|)
decl_stmt|;
if|if
condition|(
name|suggestions
operator|.
name|length
operator|>
name|options
operator|.
name|count
condition|)
block|{
name|suggList
operator|=
name|suggList
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
name|options
operator|.
name|count
argument_list|)
expr_stmt|;
block|}
name|result
operator|.
name|add
argument_list|(
name|token
argument_list|,
name|suggList
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|shardRequest
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|suggList
init|=
name|Collections
operator|.
name|emptyList
argument_list|()
decl_stmt|;
name|result
operator|.
name|add
argument_list|(
name|token
argument_list|,
name|suggList
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|result
return|;
block|}
DECL|method|determineReader
specifier|protected
name|IndexReader
name|determineReader
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
block|{
return|return
name|reader
return|;
block|}
annotation|@
name|Override
DECL|method|reload
specifier|public
name|void
name|reload
parameter_list|(
name|SolrCore
name|core
parameter_list|,
name|SolrIndexSearcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
name|spellChecker
operator|.
name|setSpellIndex
argument_list|(
name|index
argument_list|)
expr_stmt|;
block|}
comment|/**    * Initialize the {@link #index} variable based on the {@link #indexDir}.  Does not actually create the spelling index.    *    * @throws IOException    */
DECL|method|initIndex
specifier|protected
name|void
name|initIndex
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|indexDir
operator|!=
literal|null
condition|)
block|{
name|index
operator|=
name|FSDirectory
operator|.
name|open
argument_list|(
operator|new
name|File
argument_list|(
name|indexDir
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|index
operator|=
operator|new
name|RAMDirectory
argument_list|()
expr_stmt|;
block|}
block|}
comment|/*   * @return the Accuracy used for the Spellchecker   * */
DECL|method|getAccuracy
specifier|public
name|float
name|getAccuracy
parameter_list|()
block|{
return|return
name|accuracy
return|;
block|}
comment|/*   * @return the Field used   *   * */
DECL|method|getField
specifier|public
name|String
name|getField
parameter_list|()
block|{
return|return
name|field
return|;
block|}
comment|/*   *   * @return the FieldType name.   * */
DECL|method|getFieldTypeName
specifier|public
name|String
name|getFieldTypeName
parameter_list|()
block|{
return|return
name|fieldTypeName
return|;
block|}
comment|/*   * @return the Index directory   * */
DECL|method|getIndexDir
specifier|public
name|String
name|getIndexDir
parameter_list|()
block|{
return|return
name|indexDir
return|;
block|}
comment|/*   * @return the location of the source   * */
DECL|method|getSourceLocation
specifier|public
name|String
name|getSourceLocation
parameter_list|()
block|{
return|return
name|sourceLocation
return|;
block|}
DECL|method|getStringDistance
specifier|public
name|StringDistance
name|getStringDistance
parameter_list|()
block|{
return|return
name|sd
return|;
block|}
DECL|method|getSpellChecker
specifier|public
name|SpellChecker
name|getSpellChecker
parameter_list|()
block|{
return|return
name|spellChecker
return|;
block|}
block|}
end_class

end_unit

