begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.spelling.suggest
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|spelling
operator|.
name|suggest
package|;
end_package

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
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|UnsupportedEncodingException
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
name|search
operator|.
name|SolrIndexSearcher
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
name|spelling
operator|.
name|SolrSpellChecker
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
name|spelling
operator|.
name|SpellingOptions
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
name|spelling
operator|.
name|SpellingResult
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
name|spelling
operator|.
name|suggest
operator|.
name|Lookup
operator|.
name|LookupResult
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
name|spelling
operator|.
name|suggest
operator|.
name|jaspell
operator|.
name|JaspellLookup
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
name|util
operator|.
name|HighFrequencyDictionary
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

begin_class
DECL|class|Suggester
specifier|public
class|class
name|Suggester
extends|extends
name|SolrSpellChecker
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|Suggester
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/** Location of the source data - either a path to a file, or null for the    * current IndexReader.    */
DECL|field|LOCATION
specifier|public
specifier|static
specifier|final
name|String
name|LOCATION
init|=
literal|"sourceLocation"
decl_stmt|;
comment|/** Field to use as the source of terms if using IndexReader. */
DECL|field|FIELD
specifier|public
specifier|static
specifier|final
name|String
name|FIELD
init|=
literal|"field"
decl_stmt|;
comment|/** Fully-qualified class of the {@link Lookup} implementation. */
DECL|field|LOOKUP_IMPL
specifier|public
specifier|static
specifier|final
name|String
name|LOOKUP_IMPL
init|=
literal|"lookupImpl"
decl_stmt|;
comment|/**    * Minimum frequency of terms to consider when building the dictionary.    */
DECL|field|THRESHOLD_TOKEN_FREQUENCY
specifier|public
specifier|static
specifier|final
name|String
name|THRESHOLD_TOKEN_FREQUENCY
init|=
literal|"threshold"
decl_stmt|;
comment|/**    * Name of the location where to persist the dictionary. If this location    * is relative then the data will be stored under the core's dataDir. If this    * is null the storing will be disabled.    */
DECL|field|STORE_DIR
specifier|public
specifier|static
specifier|final
name|String
name|STORE_DIR
init|=
literal|"storeDir"
decl_stmt|;
DECL|field|sourceLocation
specifier|protected
name|String
name|sourceLocation
decl_stmt|;
DECL|field|storeDir
specifier|protected
name|File
name|storeDir
decl_stmt|;
DECL|field|field
specifier|protected
name|String
name|field
decl_stmt|;
DECL|field|threshold
specifier|protected
name|float
name|threshold
decl_stmt|;
DECL|field|dictionary
specifier|protected
name|Dictionary
name|dictionary
decl_stmt|;
DECL|field|reader
specifier|protected
name|IndexReader
name|reader
decl_stmt|;
DECL|field|lookup
specifier|protected
name|Lookup
name|lookup
decl_stmt|;
DECL|field|lookupImpl
specifier|protected
name|String
name|lookupImpl
decl_stmt|;
DECL|field|core
specifier|protected
name|SolrCore
name|core
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
name|LOG
operator|.
name|info
argument_list|(
literal|"init: "
operator|+
name|config
argument_list|)
expr_stmt|;
name|String
name|name
init|=
name|super
operator|.
name|init
argument_list|(
name|config
argument_list|,
name|core
argument_list|)
decl_stmt|;
name|threshold
operator|=
name|config
operator|.
name|get
argument_list|(
name|THRESHOLD_TOKEN_FREQUENCY
argument_list|)
operator|==
literal|null
condition|?
literal|0.0f
else|:
operator|(
name|Float
operator|)
name|config
operator|.
name|get
argument_list|(
name|THRESHOLD_TOKEN_FREQUENCY
argument_list|)
expr_stmt|;
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
name|field
operator|=
operator|(
name|String
operator|)
name|config
operator|.
name|get
argument_list|(
name|FIELD
argument_list|)
expr_stmt|;
name|lookupImpl
operator|=
operator|(
name|String
operator|)
name|config
operator|.
name|get
argument_list|(
name|LOOKUP_IMPL
argument_list|)
expr_stmt|;
if|if
condition|(
name|lookupImpl
operator|==
literal|null
condition|)
block|{
name|lookupImpl
operator|=
name|JaspellLookup
operator|.
name|class
operator|.
name|getName
argument_list|()
expr_stmt|;
block|}
name|lookup
operator|=
operator|(
name|Lookup
operator|)
name|core
operator|.
name|getResourceLoader
argument_list|()
operator|.
name|newInstance
argument_list|(
name|lookupImpl
argument_list|)
expr_stmt|;
name|lookup
operator|.
name|init
argument_list|(
name|config
argument_list|,
name|core
argument_list|)
expr_stmt|;
name|String
name|store
init|=
operator|(
name|String
operator|)
name|config
operator|.
name|get
argument_list|(
name|STORE_DIR
argument_list|)
decl_stmt|;
if|if
condition|(
name|store
operator|!=
literal|null
condition|)
block|{
name|storeDir
operator|=
operator|new
name|File
argument_list|(
name|store
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|storeDir
operator|.
name|isAbsolute
argument_list|()
condition|)
block|{
name|storeDir
operator|=
operator|new
name|File
argument_list|(
name|core
operator|.
name|getDataDir
argument_list|()
operator|+
name|File
operator|.
name|separator
operator|+
name|storeDir
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|storeDir
operator|.
name|exists
argument_list|()
condition|)
block|{
name|storeDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// attempt reload of the stored lookup
try|try
block|{
name|lookup
operator|.
name|load
argument_list|(
name|storeDir
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Loading stored lookup data failed"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|name
return|;
block|}
annotation|@
name|Override
DECL|method|build
specifier|public
name|void
name|build
parameter_list|(
name|SolrCore
name|core
parameter_list|,
name|SolrIndexSearcher
name|searcher
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"build()"
argument_list|)
expr_stmt|;
if|if
condition|(
name|sourceLocation
operator|==
literal|null
condition|)
block|{
name|reader
operator|=
name|searcher
operator|.
name|getIndexReader
argument_list|()
expr_stmt|;
name|dictionary
operator|=
operator|new
name|HighFrequencyDictionary
argument_list|(
name|reader
argument_list|,
name|field
argument_list|,
name|threshold
argument_list|)
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
name|dictionary
operator|=
operator|new
name|FileDictionary
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|core
operator|.
name|getResourceLoader
argument_list|()
operator|.
name|openResource
argument_list|(
name|sourceLocation
argument_list|)
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
comment|// should not happen
name|LOG
operator|.
name|error
argument_list|(
literal|"should not happen"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
try|try
block|{
name|lookup
operator|.
name|build
argument_list|(
name|dictionary
argument_list|)
expr_stmt|;
if|if
condition|(
name|storeDir
operator|!=
literal|null
condition|)
block|{
name|lookup
operator|.
name|store
argument_list|(
name|storeDir
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error while building or storing Suggester data"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
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
name|LOG
operator|.
name|info
argument_list|(
literal|"reload()"
argument_list|)
expr_stmt|;
if|if
condition|(
name|dictionary
operator|==
literal|null
operator|&&
name|storeDir
operator|!=
literal|null
condition|)
block|{
comment|// this may be a firstSearcher event, try loading it
if|if
condition|(
name|lookup
operator|.
name|load
argument_list|(
name|storeDir
argument_list|)
condition|)
block|{
return|return;
comment|// loaded ok
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"load failed, need to build Lookup again"
argument_list|)
expr_stmt|;
block|}
comment|// loading was unsuccessful - build it again
name|build
argument_list|(
name|core
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
block|}
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|String
name|query
parameter_list|,
name|int
name|numHits
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"add "
operator|+
name|query
operator|+
literal|", "
operator|+
name|numHits
argument_list|)
expr_stmt|;
name|lookup
operator|.
name|add
argument_list|(
name|query
argument_list|,
operator|new
name|Integer
argument_list|(
name|numHits
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|field|EMPTY_RESULT
specifier|static
name|SpellingResult
name|EMPTY_RESULT
init|=
operator|new
name|SpellingResult
argument_list|()
decl_stmt|;
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
name|LOG
operator|.
name|debug
argument_list|(
literal|"getSuggestions: "
operator|+
name|options
operator|.
name|tokens
argument_list|)
expr_stmt|;
if|if
condition|(
name|lookup
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Lookup is null - invoke spellchecker.build first"
argument_list|)
expr_stmt|;
return|return
name|EMPTY_RESULT
return|;
block|}
name|SpellingResult
name|res
init|=
operator|new
name|SpellingResult
argument_list|()
decl_stmt|;
for|for
control|(
name|Token
name|t
range|:
name|options
operator|.
name|tokens
control|)
block|{
name|String
name|term
init|=
operator|new
name|String
argument_list|(
name|t
operator|.
name|buffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|t
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|LookupResult
argument_list|>
name|suggestions
init|=
name|lookup
operator|.
name|lookup
argument_list|(
name|term
argument_list|,
name|options
operator|.
name|onlyMorePopular
argument_list|,
name|options
operator|.
name|count
argument_list|)
decl_stmt|;
if|if
condition|(
name|suggestions
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
operator|!
name|options
operator|.
name|onlyMorePopular
condition|)
block|{
name|Collections
operator|.
name|sort
argument_list|(
name|suggestions
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|LookupResult
name|lr
range|:
name|suggestions
control|)
block|{
name|res
operator|.
name|add
argument_list|(
name|t
argument_list|,
name|lr
operator|.
name|key
argument_list|,
operator|(
operator|(
name|Number
operator|)
name|lr
operator|.
name|value
operator|)
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|res
return|;
block|}
block|}
end_class

end_unit

