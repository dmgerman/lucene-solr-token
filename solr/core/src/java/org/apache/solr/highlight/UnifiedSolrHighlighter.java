begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.highlight
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|highlight
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
name|text
operator|.
name|BreakIterator
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
name|java
operator|.
name|util
operator|.
name|Locale
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
name|function
operator|.
name|Predicate
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
name|document
operator|.
name|Document
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
name|DocIdSetIterator
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
name|Query
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
name|postingshighlight
operator|.
name|CustomSeparatorBreakIterator
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
name|postingshighlight
operator|.
name|WholeBreakIterator
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
name|uhighlight
operator|.
name|DefaultPassageFormatter
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
name|uhighlight
operator|.
name|LengthGoalBreakIterator
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
name|uhighlight
operator|.
name|PassageFormatter
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
name|uhighlight
operator|.
name|PassageScorer
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
name|uhighlight
operator|.
name|UnifiedHighlighter
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
name|SolrException
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
name|HighlightParams
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
name|common
operator|.
name|util
operator|.
name|SimpleOrderedMap
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
name|PluginInfo
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
name|request
operator|.
name|SolrQueryRequest
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
name|request
operator|.
name|SolrRequestInfo
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
name|IndexSchema
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
name|SchemaField
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
name|DocIterator
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
name|DocList
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
name|util
operator|.
name|RTimerTree
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
name|plugin
operator|.
name|PluginInfoInitialized
import|;
end_import

begin_comment
comment|/**  * Highlighter impl that uses {@link UnifiedHighlighter}  *<p>  * Example configuration with default values:  *<pre class="prettyprint">  *&lt;requestHandler name="/select" class="solr.SearchHandler"&gt;  *&lt;lst name="defaults"&gt;  *&lt;str name="hl.method"&gt;unified&lt;/str&gt;  *&lt;int name="hl.snippets"&gt;1&lt;/int&gt;  *&lt;str name="hl.tag.pre"&gt;&amp;lt;em&amp;gt;&lt;/str&gt;  *&lt;str name="hl.tag.post"&gt;&amp;lt;/em&amp;gt;&lt;/str&gt;  *&lt;str name="hl.simple.pre"&gt;&amp;lt;em&amp;gt;&lt;/str&gt;  *&lt;str name="hl.simple.post"&gt;&amp;lt;/em&amp;gt;&lt;/str&gt;  *&lt;str name="hl.tag.ellipsis"&gt;(internal/unspecified)&lt;/str&gt;  *&lt;bool name="hl.defaultSummary"&gt;false&lt;/bool&gt;  *&lt;str name="hl.encoder"&gt;simple&lt;/str&gt;  *&lt;float name="hl.score.k1"&gt;1.2&lt;/float&gt;  *&lt;float name="hl.score.b"&gt;0.75&lt;/float&gt;  *&lt;float name="hl.score.pivot"&gt;87&lt;/float&gt;  *&lt;str name="hl.bs.language"&gt;&lt;/str&gt;  *&lt;str name="hl.bs.country"&gt;&lt;/str&gt;  *&lt;str name="hl.bs.variant"&gt;&lt;/str&gt;  *&lt;str name="hl.bs.type"&gt;SENTENCE&lt;/str&gt;  *&lt;int name="hl.maxAnalyzedChars"&gt;51200&lt;/int&gt;  *&lt;bool name="hl.highlightMultiTerm"&gt;true&lt;/bool&gt;  *&lt;bool name="hl.usePhraseHighlighter"&gt;true&lt;/bool&gt;  *&lt;int name="hl.cacheFieldValCharsThreshold"&gt;524288&lt;/int&gt;  *&lt;str name="hl.offsetSource"&gt;&lt;/str&gt;  *&lt;/lst&gt;  *&lt;/requestHandler&gt;  *</pre>  *<p>  * Notes:  *<ul>  *<li>hl.q (string) can specify the query  *<li>hl.fl (string) specifies the field list.  *<li>hl.snippets (int) specifies how many snippets to return.  *<li>hl.tag.pre (string) specifies text which appears before a highlighted term.  *<li>hl.tag.post (string) specifies text which appears after a highlighted term.  *<li>hl.simple.pre (string) specifies text which appears before a highlighted term. (prefer hl.tag.pre)  *<li>hl.simple.post (string) specifies text which appears before a highlighted term. (prefer hl.tag.post)  *<li>hl.tag.ellipsis (string) specifies text which joins non-adjacent passages. The default is to retain each  * value in a list without joining them.  *<li>hl.defaultSummary (bool) specifies if a field should have a default summary of the leading text.  *<li>hl.encoder (string) can be 'html' (html escapes content) or 'simple' (no escaping).  *<li>hl.score.k1 (float) specifies bm25 scoring parameter 'k1'  *<li>hl.score.b (float) specifies bm25 scoring parameter 'b'  *<li>hl.score.pivot (float) specifies bm25 scoring parameter 'avgdl'  *<li>hl.bs.type (string) specifies how to divide text into passages: [SENTENCE, LINE, WORD, CHAR, WHOLE]  *<li>hl.bs.language (string) specifies language code for BreakIterator. default is empty string (root locale)  *<li>hl.bs.country (string) specifies country code for BreakIterator. default is empty string (root locale)  *<li>hl.bs.variant (string) specifies country code for BreakIterator. default is empty string (root locale)  *<li>hl.maxAnalyzedChars (int) specifies how many characters at most will be processed in a document for any one field.  *<li>hl.highlightMultiTerm (bool) enables highlighting for range/wildcard/fuzzy/prefix queries at some cost. default is true  *<li>hl.usePhraseHighlighter (bool) enables phrase highlighting. default is true  *<li>hl.cacheFieldValCharsThreshold (int) controls how many characters from a field are cached. default is 524288 (1MB in 2 byte chars)  *<li>hl.offsetSource (string) specifies which offset source to use, prefers postings, but will use what's available if not specified  *</ul>  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|UnifiedSolrHighlighter
specifier|public
class|class
name|UnifiedSolrHighlighter
extends|extends
name|SolrHighlighter
implements|implements
name|PluginInfoInitialized
block|{
DECL|field|SNIPPET_SEPARATOR
specifier|protected
specifier|static
specifier|final
name|String
name|SNIPPET_SEPARATOR
init|=
literal|"\u0000"
decl_stmt|;
DECL|field|ZERO_LEN_STR_ARRAY
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|ZERO_LEN_STR_ARRAY
init|=
operator|new
name|String
index|[
literal|0
index|]
decl_stmt|;
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|PluginInfo
name|info
parameter_list|)
block|{   }
annotation|@
name|Override
DECL|method|doHighlighting
specifier|public
name|NamedList
argument_list|<
name|Object
argument_list|>
name|doHighlighting
parameter_list|(
name|DocList
name|docs
parameter_list|,
name|Query
name|query
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|,
name|String
index|[]
name|defaultFields
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|SolrParams
name|params
init|=
name|req
operator|.
name|getParams
argument_list|()
decl_stmt|;
comment|// if highlighting isn't enabled, then why call doHighlighting?
if|if
condition|(
operator|!
name|isHighlightingEnabled
argument_list|(
name|params
argument_list|)
condition|)
return|return
literal|null
return|;
name|int
index|[]
name|docIDs
init|=
name|toDocIDs
argument_list|(
name|docs
argument_list|)
decl_stmt|;
comment|// fetch the unique keys
name|String
index|[]
name|keys
init|=
name|getUniqueKeys
argument_list|(
name|req
operator|.
name|getSearcher
argument_list|()
argument_list|,
name|docIDs
argument_list|)
decl_stmt|;
comment|// query-time parameters
name|String
index|[]
name|fieldNames
init|=
name|getHighlightFields
argument_list|(
name|query
argument_list|,
name|req
argument_list|,
name|defaultFields
argument_list|)
decl_stmt|;
name|int
name|maxPassages
index|[]
init|=
operator|new
name|int
index|[
name|fieldNames
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|fieldNames
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|maxPassages
index|[
name|i
index|]
operator|=
name|params
operator|.
name|getFieldInt
argument_list|(
name|fieldNames
index|[
name|i
index|]
argument_list|,
name|HighlightParams
operator|.
name|SNIPPETS
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
name|UnifiedHighlighter
name|highlighter
init|=
name|getHighlighter
argument_list|(
name|req
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|snippets
init|=
name|highlighter
operator|.
name|highlightFields
argument_list|(
name|fieldNames
argument_list|,
name|query
argument_list|,
name|docIDs
argument_list|,
name|maxPassages
argument_list|)
decl_stmt|;
return|return
name|encodeSnippets
argument_list|(
name|keys
argument_list|,
name|fieldNames
argument_list|,
name|snippets
argument_list|)
return|;
block|}
comment|/**    * Creates an instance of the Lucene {@link UnifiedHighlighter}. Provided for subclass extension so that    * a subclass can return a subclass of {@link SolrExtendedUnifiedHighlighter}.    */
DECL|method|getHighlighter
specifier|protected
name|UnifiedHighlighter
name|getHighlighter
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|)
block|{
return|return
operator|new
name|SolrExtendedUnifiedHighlighter
argument_list|(
name|req
argument_list|)
return|;
block|}
comment|/**    * Encodes the resulting snippets into a namedlist    *    * @param keys       the document unique keys    * @param fieldNames field names to highlight in the order    * @param snippets   map from field name to snippet array for the docs    * @return encoded namedlist of summaries    */
DECL|method|encodeSnippets
specifier|protected
name|NamedList
argument_list|<
name|Object
argument_list|>
name|encodeSnippets
parameter_list|(
name|String
index|[]
name|keys
parameter_list|,
name|String
index|[]
name|fieldNames
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|snippets
parameter_list|)
block|{
name|NamedList
argument_list|<
name|Object
argument_list|>
name|list
init|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|keys
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|NamedList
argument_list|<
name|Object
argument_list|>
name|summary
init|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|field
range|:
name|fieldNames
control|)
block|{
name|String
name|snippet
init|=
name|snippets
operator|.
name|get
argument_list|(
name|field
argument_list|)
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|snippet
operator|==
literal|null
condition|)
block|{
comment|//TODO reuse logic of DefaultSolrHighlighter.alternateField
name|summary
operator|.
name|add
argument_list|(
name|field
argument_list|,
name|ZERO_LEN_STR_ARRAY
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// we used a special snippet separator char and we can now split on it.
name|summary
operator|.
name|add
argument_list|(
name|field
argument_list|,
name|snippet
operator|.
name|split
argument_list|(
name|SNIPPET_SEPARATOR
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|list
operator|.
name|add
argument_list|(
name|keys
index|[
name|i
index|]
argument_list|,
name|summary
argument_list|)
expr_stmt|;
block|}
return|return
name|list
return|;
block|}
comment|/**    * Converts solr's DocList to the int[] docIDs    */
DECL|method|toDocIDs
specifier|protected
name|int
index|[]
name|toDocIDs
parameter_list|(
name|DocList
name|docs
parameter_list|)
block|{
name|int
index|[]
name|docIDs
init|=
operator|new
name|int
index|[
name|docs
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|DocIterator
name|iterator
init|=
name|docs
operator|.
name|iterator
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|docIDs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
name|docIDs
index|[
name|i
index|]
operator|=
name|iterator
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
return|return
name|docIDs
return|;
block|}
comment|/**    * Retrieves the unique keys for the topdocs to key the results    */
DECL|method|getUniqueKeys
specifier|protected
name|String
index|[]
name|getUniqueKeys
parameter_list|(
name|SolrIndexSearcher
name|searcher
parameter_list|,
name|int
index|[]
name|docIDs
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexSchema
name|schema
init|=
name|searcher
operator|.
name|getSchema
argument_list|()
decl_stmt|;
name|SchemaField
name|keyField
init|=
name|schema
operator|.
name|getUniqueKeyField
argument_list|()
decl_stmt|;
if|if
condition|(
name|keyField
operator|!=
literal|null
condition|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|selector
init|=
name|Collections
operator|.
name|singleton
argument_list|(
name|keyField
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|String
index|[]
name|uniqueKeys
init|=
operator|new
name|String
index|[
name|docIDs
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|docIDs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|docid
init|=
name|docIDs
index|[
name|i
index|]
decl_stmt|;
name|Document
name|doc
init|=
name|searcher
operator|.
name|doc
argument_list|(
name|docid
argument_list|,
name|selector
argument_list|)
decl_stmt|;
name|String
name|id
init|=
name|schema
operator|.
name|printableUniqueKey
argument_list|(
name|doc
argument_list|)
decl_stmt|;
name|uniqueKeys
index|[
name|i
index|]
operator|=
name|id
expr_stmt|;
block|}
return|return
name|uniqueKeys
return|;
block|}
else|else
block|{
return|return
operator|new
name|String
index|[
name|docIDs
operator|.
name|length
index|]
return|;
block|}
block|}
comment|/**    * From {@link #getHighlighter(org.apache.solr.request.SolrQueryRequest)}.    */
DECL|class|SolrExtendedUnifiedHighlighter
specifier|protected
specifier|static
class|class
name|SolrExtendedUnifiedHighlighter
extends|extends
name|UnifiedHighlighter
block|{
DECL|field|NOT_REQUIRED_FIELD_MATCH_PREDICATE
specifier|protected
specifier|final
specifier|static
name|Predicate
argument_list|<
name|String
argument_list|>
name|NOT_REQUIRED_FIELD_MATCH_PREDICATE
init|=
name|s
lambda|->
literal|true
decl_stmt|;
DECL|field|params
specifier|protected
specifier|final
name|SolrParams
name|params
decl_stmt|;
DECL|field|schema
specifier|protected
specifier|final
name|IndexSchema
name|schema
decl_stmt|;
DECL|field|loadFieldValuesTimer
specifier|protected
specifier|final
name|RTimerTree
name|loadFieldValuesTimer
decl_stmt|;
DECL|method|SolrExtendedUnifiedHighlighter
specifier|public
name|SolrExtendedUnifiedHighlighter
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|)
block|{
name|super
argument_list|(
name|req
operator|.
name|getSearcher
argument_list|()
argument_list|,
name|req
operator|.
name|getSchema
argument_list|()
operator|.
name|getIndexAnalyzer
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|params
operator|=
name|req
operator|.
name|getParams
argument_list|()
expr_stmt|;
name|this
operator|.
name|schema
operator|=
name|req
operator|.
name|getSchema
argument_list|()
expr_stmt|;
name|this
operator|.
name|setMaxLength
argument_list|(
name|params
operator|.
name|getInt
argument_list|(
name|HighlightParams
operator|.
name|MAX_CHARS
argument_list|,
name|DEFAULT_MAX_CHARS
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|setCacheFieldValCharsThreshold
argument_list|(
name|params
operator|.
name|getInt
argument_list|(
name|HighlightParams
operator|.
name|CACHE_FIELD_VAL_CHARS_THRESHOLD
argument_list|,
name|DEFAULT_CACHE_CHARS_THRESHOLD
argument_list|)
argument_list|)
expr_stmt|;
comment|// SolrRequestInfo is a thread-local singleton providing access to the ResponseBuilder to code that
comment|//   otherwise can't get it in a nicer way.
name|SolrQueryRequest
name|request
init|=
name|SolrRequestInfo
operator|.
name|getRequestInfo
argument_list|()
operator|.
name|getReq
argument_list|()
decl_stmt|;
specifier|final
name|RTimerTree
name|timerTree
decl_stmt|;
if|if
condition|(
name|request
operator|.
name|getRequestTimer
argument_list|()
operator|!=
literal|null
condition|)
block|{
comment|//It may be null if not used in a search context.
name|timerTree
operator|=
name|request
operator|.
name|getRequestTimer
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|timerTree
operator|=
operator|new
name|RTimerTree
argument_list|()
expr_stmt|;
comment|// since null checks are annoying
block|}
name|loadFieldValuesTimer
operator|=
name|timerTree
operator|.
name|sub
argument_list|(
literal|"loadFieldValues"
argument_list|)
expr_stmt|;
comment|// we assume a new timer, state of STARTED
name|loadFieldValuesTimer
operator|.
name|pause
argument_list|()
expr_stmt|;
comment|// state of PAUSED now with about zero time. Will fail if state isn't STARTED.
block|}
annotation|@
name|Override
DECL|method|getOffsetSource
specifier|protected
name|OffsetSource
name|getOffsetSource
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|String
name|sourceStr
init|=
name|params
operator|.
name|getFieldParam
argument_list|(
name|field
argument_list|,
name|HighlightParams
operator|.
name|OFFSET_SOURCE
argument_list|)
decl_stmt|;
if|if
condition|(
name|sourceStr
operator|!=
literal|null
condition|)
block|{
return|return
name|OffsetSource
operator|.
name|valueOf
argument_list|(
name|sourceStr
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|getOffsetSource
argument_list|(
name|field
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getMaxNoHighlightPassages
specifier|public
name|int
name|getMaxNoHighlightPassages
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|boolean
name|defaultSummary
init|=
name|params
operator|.
name|getFieldBool
argument_list|(
name|field
argument_list|,
name|HighlightParams
operator|.
name|DEFAULT_SUMMARY
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|defaultSummary
condition|)
block|{
return|return
operator|-
literal|1
return|;
comment|// signifies return first hl.snippets passages worth of the content
block|}
else|else
block|{
return|return
literal|0
return|;
comment|// will return null
block|}
block|}
annotation|@
name|Override
DECL|method|getFormatter
specifier|protected
name|PassageFormatter
name|getFormatter
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|String
name|preTag
init|=
name|params
operator|.
name|getFieldParam
argument_list|(
name|fieldName
argument_list|,
name|HighlightParams
operator|.
name|TAG_PRE
argument_list|,
name|params
operator|.
name|getFieldParam
argument_list|(
name|fieldName
argument_list|,
name|HighlightParams
operator|.
name|SIMPLE_PRE
argument_list|,
literal|"<em>"
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|postTag
init|=
name|params
operator|.
name|getFieldParam
argument_list|(
name|fieldName
argument_list|,
name|HighlightParams
operator|.
name|TAG_POST
argument_list|,
name|params
operator|.
name|getFieldParam
argument_list|(
name|fieldName
argument_list|,
name|HighlightParams
operator|.
name|SIMPLE_POST
argument_list|,
literal|"</em>"
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|ellipsis
init|=
name|params
operator|.
name|getFieldParam
argument_list|(
name|fieldName
argument_list|,
name|HighlightParams
operator|.
name|TAG_ELLIPSIS
argument_list|,
name|SNIPPET_SEPARATOR
argument_list|)
decl_stmt|;
name|String
name|encoder
init|=
name|params
operator|.
name|getFieldParam
argument_list|(
name|fieldName
argument_list|,
name|HighlightParams
operator|.
name|ENCODER
argument_list|,
literal|"simple"
argument_list|)
decl_stmt|;
return|return
operator|new
name|DefaultPassageFormatter
argument_list|(
name|preTag
argument_list|,
name|postTag
argument_list|,
name|ellipsis
argument_list|,
literal|"html"
operator|.
name|equals
argument_list|(
name|encoder
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getScorer
specifier|protected
name|PassageScorer
name|getScorer
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|float
name|k1
init|=
name|params
operator|.
name|getFieldFloat
argument_list|(
name|fieldName
argument_list|,
name|HighlightParams
operator|.
name|SCORE_K1
argument_list|,
literal|1.2f
argument_list|)
decl_stmt|;
name|float
name|b
init|=
name|params
operator|.
name|getFieldFloat
argument_list|(
name|fieldName
argument_list|,
name|HighlightParams
operator|.
name|SCORE_B
argument_list|,
literal|0.75f
argument_list|)
decl_stmt|;
name|float
name|pivot
init|=
name|params
operator|.
name|getFieldFloat
argument_list|(
name|fieldName
argument_list|,
name|HighlightParams
operator|.
name|SCORE_PIVOT
argument_list|,
literal|87f
argument_list|)
decl_stmt|;
return|return
operator|new
name|PassageScorer
argument_list|(
name|k1
argument_list|,
name|b
argument_list|,
name|pivot
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getBreakIterator
specifier|protected
name|BreakIterator
name|getBreakIterator
parameter_list|(
name|String
name|field
parameter_list|)
block|{
comment|// Use a default fragsize the same as the regex Fragmenter (original Highlighter) since we're
comment|//  both likely shooting for sentence-like patterns.
name|int
name|fragsize
init|=
name|params
operator|.
name|getFieldInt
argument_list|(
name|field
argument_list|,
name|HighlightParams
operator|.
name|FRAGSIZE
argument_list|,
name|LuceneRegexFragmenter
operator|.
name|DEFAULT_FRAGMENT_SIZE
argument_list|)
decl_stmt|;
name|String
name|type
init|=
name|params
operator|.
name|getFieldParam
argument_list|(
name|field
argument_list|,
name|HighlightParams
operator|.
name|BS_TYPE
argument_list|)
decl_stmt|;
if|if
condition|(
name|fragsize
operator|==
literal|0
operator|||
literal|"WHOLE"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
comment|// 0 is special value; no fragmenting
return|return
operator|new
name|WholeBreakIterator
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
literal|"SEPARATOR"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
name|char
name|customSep
init|=
name|parseBiSepChar
argument_list|(
name|params
operator|.
name|getFieldParam
argument_list|(
name|field
argument_list|,
name|HighlightParams
operator|.
name|BS_SEP
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|CustomSeparatorBreakIterator
argument_list|(
name|customSep
argument_list|)
return|;
block|}
name|String
name|language
init|=
name|params
operator|.
name|getFieldParam
argument_list|(
name|field
argument_list|,
name|HighlightParams
operator|.
name|BS_LANGUAGE
argument_list|)
decl_stmt|;
name|String
name|country
init|=
name|params
operator|.
name|getFieldParam
argument_list|(
name|field
argument_list|,
name|HighlightParams
operator|.
name|BS_COUNTRY
argument_list|)
decl_stmt|;
name|String
name|variant
init|=
name|params
operator|.
name|getFieldParam
argument_list|(
name|field
argument_list|,
name|HighlightParams
operator|.
name|BS_VARIANT
argument_list|)
decl_stmt|;
name|Locale
name|locale
init|=
name|parseLocale
argument_list|(
name|language
argument_list|,
name|country
argument_list|,
name|variant
argument_list|)
decl_stmt|;
name|BreakIterator
name|baseBI
init|=
name|parseBreakIterator
argument_list|(
name|type
argument_list|,
name|locale
argument_list|)
decl_stmt|;
if|if
condition|(
name|fragsize
operator|<=
literal|1
condition|)
block|{
comment|// no real minimum size
return|return
name|baseBI
return|;
block|}
return|return
name|LengthGoalBreakIterator
operator|.
name|createMinLength
argument_list|(
name|baseBI
argument_list|,
name|fragsize
argument_list|)
return|;
comment|// TODO option for using createClosestToLength()
block|}
comment|/**      * parse custom separator char for {@link CustomSeparatorBreakIterator}      */
DECL|method|parseBiSepChar
specifier|protected
name|char
name|parseBiSepChar
parameter_list|(
name|String
name|sepChar
parameter_list|)
block|{
if|if
condition|(
name|sepChar
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|HighlightParams
operator|.
name|BS_SEP
operator|+
literal|" not passed"
argument_list|)
throw|;
block|}
if|if
condition|(
name|sepChar
operator|.
name|length
argument_list|()
operator|!=
literal|1
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|HighlightParams
operator|.
name|BS_SEP
operator|+
literal|" must be a single char but got: '"
operator|+
name|sepChar
operator|+
literal|"'"
argument_list|)
throw|;
block|}
return|return
name|sepChar
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
return|;
block|}
comment|/**      * parse a break iterator type for the specified locale      */
DECL|method|parseBreakIterator
specifier|protected
name|BreakIterator
name|parseBreakIterator
parameter_list|(
name|String
name|type
parameter_list|,
name|Locale
name|locale
parameter_list|)
block|{
if|if
condition|(
name|type
operator|==
literal|null
operator|||
literal|"SENTENCE"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
return|return
name|BreakIterator
operator|.
name|getSentenceInstance
argument_list|(
name|locale
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
literal|"LINE"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
return|return
name|BreakIterator
operator|.
name|getLineInstance
argument_list|(
name|locale
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
literal|"WORD"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
return|return
name|BreakIterator
operator|.
name|getWordInstance
argument_list|(
name|locale
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
literal|"CHARACTER"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
return|return
name|BreakIterator
operator|.
name|getCharacterInstance
argument_list|(
name|locale
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown "
operator|+
name|HighlightParams
operator|.
name|BS_TYPE
operator|+
literal|": "
operator|+
name|type
argument_list|)
throw|;
block|}
block|}
comment|/**      * parse a locale from a language+country+variant spec      */
DECL|method|parseLocale
specifier|protected
name|Locale
name|parseLocale
parameter_list|(
name|String
name|language
parameter_list|,
name|String
name|country
parameter_list|,
name|String
name|variant
parameter_list|)
block|{
if|if
condition|(
name|language
operator|==
literal|null
operator|&&
name|country
operator|==
literal|null
operator|&&
name|variant
operator|==
literal|null
condition|)
block|{
return|return
name|Locale
operator|.
name|ROOT
return|;
block|}
elseif|else
if|if
condition|(
name|language
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"language is required if country or variant is specified"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|country
operator|==
literal|null
operator|&&
name|variant
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"To specify variant, country is required"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|country
operator|!=
literal|null
operator|&&
name|variant
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|Locale
argument_list|(
name|language
argument_list|,
name|country
argument_list|,
name|variant
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|country
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|Locale
argument_list|(
name|language
argument_list|,
name|country
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|Locale
argument_list|(
name|language
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|loadFieldValues
specifier|protected
name|List
argument_list|<
name|CharSequence
index|[]
argument_list|>
name|loadFieldValues
parameter_list|(
name|String
index|[]
name|fields
parameter_list|,
name|DocIdSetIterator
name|docIter
parameter_list|,
name|int
name|cacheCharsThreshold
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Time loading field values.  It can be an expensive part of highlighting.
name|loadFieldValuesTimer
operator|.
name|resume
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|super
operator|.
name|loadFieldValues
argument_list|(
name|fields
argument_list|,
name|docIter
argument_list|,
name|cacheCharsThreshold
argument_list|)
return|;
block|}
finally|finally
block|{
name|loadFieldValuesTimer
operator|.
name|pause
argument_list|()
expr_stmt|;
comment|// note: doesn't need to be "stopped"; pause is fine.
block|}
block|}
annotation|@
name|Override
DECL|method|shouldHandleMultiTermQuery
specifier|protected
name|boolean
name|shouldHandleMultiTermQuery
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
name|params
operator|.
name|getFieldBool
argument_list|(
name|field
argument_list|,
name|HighlightParams
operator|.
name|HIGHLIGHT_MULTI_TERM
argument_list|,
literal|true
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|shouldHighlightPhrasesStrictly
specifier|protected
name|boolean
name|shouldHighlightPhrasesStrictly
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
name|params
operator|.
name|getFieldBool
argument_list|(
name|field
argument_list|,
name|HighlightParams
operator|.
name|USE_PHRASE_HIGHLIGHTER
argument_list|,
literal|true
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getFieldMatcher
specifier|protected
name|Predicate
argument_list|<
name|String
argument_list|>
name|getFieldMatcher
parameter_list|(
name|String
name|field
parameter_list|)
block|{
comment|// TODO define hl.queryFieldPattern as a more advanced alternative to hl.requireFieldMatch.
comment|// note that the UH& PH at Lucene level default to effectively "true"
if|if
condition|(
name|params
operator|.
name|getFieldBool
argument_list|(
name|field
argument_list|,
name|HighlightParams
operator|.
name|FIELD_MATCH
argument_list|,
literal|false
argument_list|)
condition|)
block|{
return|return
name|field
operator|::
name|equals
return|;
comment|// requireFieldMatch
block|}
else|else
block|{
return|return
name|NOT_REQUIRED_FIELD_MATCH_PREDICATE
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

