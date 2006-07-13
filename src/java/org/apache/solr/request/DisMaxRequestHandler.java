begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright 2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.request
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|request
package|;
end_package

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
name|SolrInfoMBean
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
name|DocSet
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
name|DocListAndSet
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
name|SolrCache
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
name|SolrQueryParser
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
name|QueryParsing
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
name|CacheRegenerator
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
name|StandardRequestHandler
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
name|SolrQueryResponse
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
name|SolrRequestHandler
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
name|util
operator|.
name|StrUtils
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
name|util
operator|.
name|SolrPluginUtils
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
name|DisMaxParams
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
name|document
operator|.
name|Field
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
name|TermQuery
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
name|DisjunctionMaxQuery
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
name|BooleanQuery
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
name|BooleanClause
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
name|BooleanClause
operator|.
name|Occur
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
name|ConstantScoreRangeQuery
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
name|Sort
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
name|Explanation
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
name|queryParser
operator|.
name|QueryParser
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
name|queryParser
operator|.
name|ParseException
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
name|xmlpull
operator|.
name|v1
operator|.
name|XmlPullParserException
import|;
end_import

begin_comment
comment|/* this is the standard logging framework for Solr */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|Level
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|Handler
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
name|ArrayList
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
name|Collection
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
name|HashSet
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|net
operator|.
name|URL
import|;
end_import

begin_comment
comment|/**  *<p>  * A Generic query plugin designed to be given a simple query expression  * from a user, which it will then query agaisnt a variety of  * pre-configured fields, in a variety of ways, using BooleanQueries,  * DisjunctionMaxQueries, and PhraseQueries.  *</p>  *  *<p>  * All of the following options may be configured for this plugin  * in the solrconfig as defaults, and may be overriden as request parameters  *</p>  *  *<ul>  *<li>tie - (Tie breaker) float value to use as tiebreaker in  *           DisjunctionMaxQueries (should be something much less then 1)  *</li>  *<li> qf - (Query Fields) fields and boosts to use when building  *           DisjunctionMaxQueries from the users query.  Format is:  *           "<code>fieldA^1.0 fieldB^2.2</code>".  *</li>  *<li> mm - (Minimum Match) this supports a wide variety of  *           complex expressions.  *           read {@link SolrPluginUtils#setMinShouldMatch SolrPluginUtils.setMinShouldMatch} for full details.  *</li>  *<li> pf - (Phrase Fields) fields/boosts to make phrase queries out  *           of to boost  *           the users query for exact matches on the specified fields.  *           Format is: "<code>fieldA^1.0 fieldB^2.2</code>".  *</li>  *<li> ps - (Phrase Slop) amount of slop on phrase queries built for pf  *           fields.  *</li>  *<li> bq - (Boost Query) a raw lucene query that will be included in the   *           users query to influcene the score.  If this is a BooleanQuery  *           with a default boost (1.0f) then the individual clauses will be  *           added directly to the main query.  Otherwise the query will be  *           included as is.  *</li>  *<li> bf - (Boost Functions) functions (with optional boosts) that will be  *           included in the users query to influcene the score.  *           Format is: "<code>funcA(arg1,arg2)^1.2  *           funcB(arg3,arg4)^2.2</code>".  NOTE: Whitespace is not allowed  *           in the function arguments.  *</li>  *<li> fq - (Filter Query) a raw lucene query that can be used  *           to restrict the super set of products we are interested in - more  *           efficient then using bq, but doesn't influence score.  *</li>  *</ul>  *  *<p>  * The following options are only available as request params...  *</p>  *  *<ul>  *<li>   q - (Query) the raw unparsed, unescaped, query from the user.  *</li>  *<li>sort - (Order By) list of fields and direction to sort on.  *</li>  *</ul>  */
end_comment

begin_class
DECL|class|DisMaxRequestHandler
specifier|public
class|class
name|DisMaxRequestHandler
implements|implements
name|SolrRequestHandler
implements|,
name|SolrInfoMBean
block|{
comment|/**    * A field we can't ever find in any schema, so we can safely tell    * DisjunctionMaxQueryParser to use it as our defaultField, and    * map aliases from it to any field in our schema.    */
DECL|field|IMPOSSIBLE_FIELD_NAME
specifier|private
specifier|static
name|String
name|IMPOSSIBLE_FIELD_NAME
init|=
literal|"\uFFFC\uFFFC\uFFFC"
decl_stmt|;
comment|// statistics
comment|// TODO: should we bother synchronizing these, or is an off-by-one error
comment|// acceptable every million requests or so?
DECL|field|numRequests
name|long
name|numRequests
decl_stmt|;
DECL|field|numErrors
name|long
name|numErrors
decl_stmt|;
comment|/** shorten the class referneces for utilities */
DECL|class|U
specifier|private
specifier|static
class|class
name|U
extends|extends
name|SolrPluginUtils
block|{
comment|/* :NOOP */
block|}
DECL|field|params
specifier|protected
specifier|final
name|DisMaxParams
name|params
init|=
operator|new
name|DisMaxParams
argument_list|()
decl_stmt|;
DECL|method|DisMaxRequestHandler
specifier|public
name|DisMaxRequestHandler
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/* returns URLs to the Wiki pages */
DECL|method|getDocs
specifier|public
name|URL
index|[]
name|getDocs
parameter_list|()
block|{
comment|/* :TODO: need docs */
return|return
operator|new
name|URL
index|[
literal|0
index|]
return|;
block|}
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
return|;
block|}
DECL|method|getStatistics
specifier|public
name|NamedList
name|getStatistics
parameter_list|()
block|{
name|NamedList
name|lst
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"requests"
argument_list|,
name|numRequests
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"errors"
argument_list|,
name|numErrors
argument_list|)
expr_stmt|;
return|return
name|lst
return|;
block|}
DECL|method|getVersion
specifier|public
name|String
name|getVersion
parameter_list|()
block|{
return|return
literal|"$Revision:$"
return|;
block|}
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"DisjunctionMax Request Handler: Does relevancy based queries "
operator|+
literal|"accross a variety of fields using configured boosts"
return|;
block|}
DECL|method|getCategory
specifier|public
name|Category
name|getCategory
parameter_list|()
block|{
return|return
name|Category
operator|.
name|QUERYHANDLER
return|;
block|}
DECL|method|getSourceId
specifier|public
name|String
name|getSourceId
parameter_list|()
block|{
return|return
literal|"$Id:$"
return|;
block|}
DECL|method|getSource
specifier|public
name|String
name|getSource
parameter_list|()
block|{
return|return
literal|"$URL:$"
return|;
block|}
comment|/** sets the default variables for any usefull info it finds in the config    * if a config option is not inthe format expected, logs an warning    * and ignores it..    */
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{
name|params
operator|.
name|setValues
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
DECL|method|handleRequest
specifier|public
name|void
name|handleRequest
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
block|{
name|numRequests
operator|++
expr_stmt|;
try|try
block|{
name|int
name|flags
init|=
literal|0
decl_stmt|;
name|SolrIndexSearcher
name|s
init|=
name|req
operator|.
name|getSearcher
argument_list|()
decl_stmt|;
name|IndexSchema
name|schema
init|=
name|req
operator|.
name|getSchema
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|queryFields
init|=
name|U
operator|.
name|parseFieldBoosts
argument_list|(
name|U
operator|.
name|getParam
argument_list|(
name|req
argument_list|,
name|params
operator|.
name|QF
argument_list|,
name|params
operator|.
name|qf
argument_list|)
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|phraseFields
init|=
name|U
operator|.
name|parseFieldBoosts
argument_list|(
name|U
operator|.
name|getParam
argument_list|(
name|req
argument_list|,
name|params
operator|.
name|PF
argument_list|,
name|params
operator|.
name|pf
argument_list|)
argument_list|)
decl_stmt|;
name|float
name|tiebreaker
init|=
name|U
operator|.
name|getNumberParam
argument_list|(
name|req
argument_list|,
name|params
operator|.
name|TIE
argument_list|,
name|params
operator|.
name|tiebreaker
argument_list|)
operator|.
name|floatValue
argument_list|()
decl_stmt|;
name|int
name|pslop
init|=
name|U
operator|.
name|getNumberParam
argument_list|(
name|req
argument_list|,
name|params
operator|.
name|PS
argument_list|,
name|params
operator|.
name|pslop
argument_list|)
operator|.
name|intValue
argument_list|()
decl_stmt|;
comment|/* a generic parser for parsing regular lucene queries */
name|QueryParser
name|p
init|=
operator|new
name|SolrQueryParser
argument_list|(
name|schema
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|/* a parser for dealing with user input, which will convert        * things to DisjunctionMaxQueries        */
name|U
operator|.
name|DisjunctionMaxQueryParser
name|up
init|=
operator|new
name|U
operator|.
name|DisjunctionMaxQueryParser
argument_list|(
name|schema
argument_list|,
name|IMPOSSIBLE_FIELD_NAME
argument_list|)
decl_stmt|;
name|up
operator|.
name|addAlias
argument_list|(
name|IMPOSSIBLE_FIELD_NAME
argument_list|,
name|tiebreaker
argument_list|,
name|queryFields
argument_list|)
expr_stmt|;
comment|/* for parsing slopy phrases using DisjunctionMaxQueries */
name|U
operator|.
name|DisjunctionMaxQueryParser
name|pp
init|=
operator|new
name|U
operator|.
name|DisjunctionMaxQueryParser
argument_list|(
name|schema
argument_list|,
name|IMPOSSIBLE_FIELD_NAME
argument_list|)
decl_stmt|;
name|pp
operator|.
name|addAlias
argument_list|(
name|IMPOSSIBLE_FIELD_NAME
argument_list|,
name|tiebreaker
argument_list|,
name|phraseFields
argument_list|)
expr_stmt|;
name|pp
operator|.
name|setPhraseSlop
argument_list|(
name|pslop
argument_list|)
expr_stmt|;
comment|/* * * Main User Query * * */
name|String
name|userQuery
init|=
name|U
operator|.
name|partialEscape
argument_list|(
name|U
operator|.
name|stripUnbalancedQuotes
argument_list|(
name|req
operator|.
name|getQueryString
argument_list|()
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
comment|/* the main query we will execute.  we disable the coord because        * this query is an artificial construct        */
name|BooleanQuery
name|query
init|=
operator|new
name|BooleanQuery
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|String
name|minShouldMatch
init|=
name|U
operator|.
name|getParam
argument_list|(
name|req
argument_list|,
name|params
operator|.
name|MM
argument_list|,
name|params
operator|.
name|mm
argument_list|)
decl_stmt|;
name|Query
name|dis
init|=
name|up
operator|.
name|parse
argument_list|(
name|userQuery
argument_list|)
decl_stmt|;
if|if
condition|(
name|dis
operator|instanceof
name|BooleanQuery
condition|)
block|{
name|BooleanQuery
name|t
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|U
operator|.
name|flattenBooleanQuery
argument_list|(
name|t
argument_list|,
operator|(
name|BooleanQuery
operator|)
name|dis
argument_list|)
expr_stmt|;
name|U
operator|.
name|setMinShouldMatch
argument_list|(
name|t
argument_list|,
name|minShouldMatch
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
name|t
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|query
operator|.
name|add
argument_list|(
name|dis
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
block|}
comment|/* * * Add on Phrases for the Query * * */
comment|/* build up phrase boosting queries */
comment|/* if the userQuery already has some quotes, stip them out.        * we've already done the phrases they asked for in the main        * part of the query, this is to boost docs that may not have        * matched those phrases but do match looser phrases.        */
name|String
name|userPhraseQuery
init|=
name|userQuery
operator|.
name|replace
argument_list|(
literal|"\""
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|Query
name|phrase
init|=
name|pp
operator|.
name|parse
argument_list|(
literal|"\""
operator|+
name|userPhraseQuery
operator|+
literal|"\""
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|phrase
condition|)
block|{
name|query
operator|.
name|add
argument_list|(
name|phrase
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
block|}
comment|/* * * Boosting Query * * */
name|String
name|boostQuery
init|=
name|U
operator|.
name|getParam
argument_list|(
name|req
argument_list|,
name|params
operator|.
name|BQ
argument_list|,
name|params
operator|.
name|bq
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|boostQuery
operator|&&
operator|!
name|boostQuery
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
condition|)
block|{
name|Query
name|tmp
init|=
name|p
operator|.
name|parse
argument_list|(
name|boostQuery
argument_list|)
decl_stmt|;
comment|/* if the default boost was used, and we've got a BooleanQuery          * extract the subqueries out and use them directly          */
if|if
condition|(
literal|1.0f
operator|==
name|tmp
operator|.
name|getBoost
argument_list|()
operator|&&
name|tmp
operator|instanceof
name|BooleanQuery
condition|)
block|{
for|for
control|(
name|BooleanClause
name|c
range|:
operator|(
operator|(
name|BooleanQuery
operator|)
name|tmp
operator|)
operator|.
name|getClauses
argument_list|()
control|)
block|{
name|query
operator|.
name|add
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|query
operator|.
name|add
argument_list|(
name|tmp
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
block|}
block|}
comment|/* * * Boosting Functions * * */
name|String
name|boostFunc
init|=
name|U
operator|.
name|getParam
argument_list|(
name|req
argument_list|,
name|params
operator|.
name|BF
argument_list|,
name|params
operator|.
name|bf
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|boostFunc
operator|&&
operator|!
name|boostFunc
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
condition|)
block|{
name|List
argument_list|<
name|Query
argument_list|>
name|funcs
init|=
name|U
operator|.
name|parseFuncs
argument_list|(
name|schema
argument_list|,
name|boostFunc
argument_list|)
decl_stmt|;
for|for
control|(
name|Query
name|f
range|:
name|funcs
control|)
block|{
name|query
operator|.
name|add
argument_list|(
name|f
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
block|}
block|}
comment|/* * * Restrict Results * * */
name|List
argument_list|<
name|Query
argument_list|>
name|restrictions
init|=
operator|new
name|ArrayList
argument_list|<
name|Query
argument_list|>
argument_list|(
literal|1
argument_list|)
decl_stmt|;
comment|/* User Restriction */
name|String
name|filterQueryString
init|=
name|U
operator|.
name|getParam
argument_list|(
name|req
argument_list|,
name|params
operator|.
name|FQ
argument_list|,
name|params
operator|.
name|fq
argument_list|)
decl_stmt|;
name|Query
name|filterQuery
init|=
literal|null
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|filterQueryString
operator|&&
operator|!
name|filterQueryString
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
condition|)
block|{
name|filterQuery
operator|=
name|p
operator|.
name|parse
argument_list|(
name|filterQueryString
argument_list|)
expr_stmt|;
name|restrictions
operator|.
name|add
argument_list|(
name|filterQuery
argument_list|)
expr_stmt|;
block|}
comment|/* * * Generate Main Results * * */
name|flags
operator||=
name|U
operator|.
name|setReturnFields
argument_list|(
name|U
operator|.
name|getParam
argument_list|(
name|req
argument_list|,
name|params
operator|.
name|FL
argument_list|,
name|params
operator|.
name|fl
argument_list|)
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|DocList
name|results
init|=
name|s
operator|.
name|getDocList
argument_list|(
name|query
argument_list|,
name|restrictions
argument_list|,
name|SolrPluginUtils
operator|.
name|getSort
argument_list|(
name|req
argument_list|)
argument_list|,
name|req
operator|.
name|getStart
argument_list|()
argument_list|,
name|req
operator|.
name|getLimit
argument_list|()
argument_list|,
name|flags
argument_list|)
decl_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"search-results"
argument_list|,
name|results
argument_list|)
expr_stmt|;
comment|/* * * Debugging Info * * */
try|try
block|{
name|NamedList
name|debug
init|=
name|U
operator|.
name|doStandardDebug
argument_list|(
name|req
argument_list|,
name|userQuery
argument_list|,
name|query
argument_list|,
name|results
argument_list|,
name|params
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|debug
condition|)
block|{
name|debug
operator|.
name|add
argument_list|(
literal|"boostquery"
argument_list|,
name|boostQuery
argument_list|)
expr_stmt|;
name|debug
operator|.
name|add
argument_list|(
literal|"boostfunc"
argument_list|,
name|boostFunc
argument_list|)
expr_stmt|;
name|debug
operator|.
name|add
argument_list|(
literal|"filterquery"
argument_list|,
name|filterQueryString
argument_list|)
expr_stmt|;
if|if
condition|(
literal|null
operator|!=
name|filterQuery
condition|)
block|{
name|debug
operator|.
name|add
argument_list|(
literal|"parsedfilterquery"
argument_list|,
name|QueryParsing
operator|.
name|toString
argument_list|(
name|filterQuery
argument_list|,
name|schema
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|rsp
operator|.
name|add
argument_list|(
literal|"debug"
argument_list|,
name|debug
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
name|SolrException
operator|.
name|logOnce
argument_list|(
name|SolrCore
operator|.
name|log
argument_list|,
literal|"Exception durring debug"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"exception_during_debug"
argument_list|,
name|SolrException
operator|.
name|toStr
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/* * * Highlighting/Summarizing  * * */
if|if
condition|(
name|U
operator|.
name|getBooleanParam
argument_list|(
name|req
argument_list|,
name|params
operator|.
name|HIGHLIGHT
argument_list|,
name|params
operator|.
name|highlight
argument_list|)
condition|)
block|{
name|BooleanQuery
name|highlightQuery
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|U
operator|.
name|flattenBooleanQuery
argument_list|(
name|highlightQuery
argument_list|,
name|query
argument_list|)
expr_stmt|;
name|NamedList
name|sumData
init|=
name|U
operator|.
name|doStandardHighlighting
argument_list|(
name|results
argument_list|,
name|highlightQuery
argument_list|,
name|req
argument_list|,
name|params
argument_list|,
name|queryFields
operator|.
name|keySet
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|sumData
operator|!=
literal|null
condition|)
name|rsp
operator|.
name|add
argument_list|(
literal|"highlighting"
argument_list|,
name|sumData
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
name|SolrException
operator|.
name|log
argument_list|(
name|SolrCore
operator|.
name|log
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|setException
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|numErrors
operator|++
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

