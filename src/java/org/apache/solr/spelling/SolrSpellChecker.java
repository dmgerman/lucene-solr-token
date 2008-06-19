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
name|core
operator|.
name|SolrResourceLoader
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
name|Collection
import|;
end_import

begin_comment
comment|/**  *<p>  * Refer to http://wiki.apache.org/solr/SpellCheckComponent for more details  *</p>  *   * @since solr 1.3  */
end_comment

begin_class
DECL|class|SolrSpellChecker
specifier|public
specifier|abstract
class|class
name|SolrSpellChecker
block|{
DECL|field|DICTIONARY_NAME
specifier|public
specifier|static
specifier|final
name|String
name|DICTIONARY_NAME
init|=
literal|"name"
decl_stmt|;
DECL|field|DEFAULT_DICTIONARY_NAME
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_DICTIONARY_NAME
init|=
literal|"default"
decl_stmt|;
DECL|field|name
specifier|protected
name|String
name|name
decl_stmt|;
DECL|field|analyzer
specifier|protected
name|Analyzer
name|analyzer
decl_stmt|;
DECL|method|init
specifier|public
name|String
name|init
parameter_list|(
name|NamedList
name|config
parameter_list|,
name|SolrResourceLoader
name|loader
parameter_list|)
block|{
name|name
operator|=
operator|(
name|String
operator|)
name|config
operator|.
name|get
argument_list|(
name|DICTIONARY_NAME
argument_list|)
expr_stmt|;
if|if
condition|(
name|name
operator|==
literal|null
condition|)
block|{
name|name
operator|=
name|DEFAULT_DICTIONARY_NAME
expr_stmt|;
block|}
return|return
name|name
return|;
block|}
DECL|method|getQueryAnalyzer
specifier|public
name|Analyzer
name|getQueryAnalyzer
parameter_list|()
block|{
return|return
name|analyzer
return|;
block|}
DECL|method|getDictionaryName
specifier|public
name|String
name|getDictionaryName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
comment|/**    * Reload the index.  Useful if an external process is responsible for building the spell checker.    *    * @throws java.io.IOException    */
DECL|method|reload
specifier|public
specifier|abstract
name|void
name|reload
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * (re)Build The Spelling index.  May be a NOOP if the ipmlementation doesn't require building, or can't be rebuilt    *    * @param core The SolrCore    */
DECL|method|build
specifier|public
specifier|abstract
name|void
name|build
parameter_list|(
name|SolrCore
name|core
parameter_list|)
function_decl|;
comment|/**    * Assumes count = 1, onlyMorePopular = false, extendedResults = false    *    * @see #getSuggestions(Collection, org.apache.lucene.index.IndexReader, int, boolean, boolean)    */
DECL|method|getSuggestions
specifier|public
name|SpellingResult
name|getSuggestions
parameter_list|(
name|Collection
argument_list|<
name|Token
argument_list|>
name|tokens
parameter_list|,
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getSuggestions
argument_list|(
name|tokens
argument_list|,
name|reader
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**    * Assumes onlyMorePopular = false, extendedResults = false    *    * @see #getSuggestions(Collection, org.apache.lucene.index.IndexReader, int, boolean, boolean)    */
DECL|method|getSuggestions
specifier|public
name|SpellingResult
name|getSuggestions
parameter_list|(
name|Collection
argument_list|<
name|Token
argument_list|>
name|tokens
parameter_list|,
name|IndexReader
name|reader
parameter_list|,
name|int
name|count
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getSuggestions
argument_list|(
name|tokens
argument_list|,
name|reader
argument_list|,
name|count
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**    * Assumes count = 1.    *    * @see #getSuggestions(Collection, org.apache.lucene.index.IndexReader, int, boolean, boolean)    */
DECL|method|getSuggestions
specifier|public
name|SpellingResult
name|getSuggestions
parameter_list|(
name|Collection
argument_list|<
name|Token
argument_list|>
name|tokens
parameter_list|,
name|IndexReader
name|reader
parameter_list|,
name|boolean
name|onlyMorePopular
parameter_list|,
name|boolean
name|extendedResults
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getSuggestions
argument_list|(
name|tokens
argument_list|,
name|reader
argument_list|,
literal|1
argument_list|,
name|onlyMorePopular
argument_list|,
name|extendedResults
argument_list|)
return|;
block|}
comment|/**    * Get suggestions for the given query.  Tokenizes the query using a field appropriate Analyzer.  The {@link SpellingResult#getSuggestions()} suggestions must be ordered by     * best suggestion first    *    * @param tokens          The Tokens to be spell checked.    * @param reader          The (optional) IndexReader.  If there is not IndexReader, than extendedResults are not possible    * @param count The maximum number of suggestions to return    * @param onlyMorePopular  TODO    * @param extendedResults  TODO    * @return    * @throws IOException    */
DECL|method|getSuggestions
specifier|public
specifier|abstract
name|SpellingResult
name|getSuggestions
parameter_list|(
name|Collection
argument_list|<
name|Token
argument_list|>
name|tokens
parameter_list|,
name|IndexReader
name|reader
parameter_list|,
name|int
name|count
parameter_list|,
name|boolean
name|onlyMorePopular
parameter_list|,
name|boolean
name|extendedResults
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

