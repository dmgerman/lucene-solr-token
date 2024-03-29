begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
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
name|queryparser
operator|.
name|classic
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
name|queryparser
operator|.
name|complexPhrase
operator|.
name|ComplexPhraseQueryParser
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
name|MultiTermQuery
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
name|solr
operator|.
name|common
operator|.
name|params
operator|.
name|CommonParams
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
name|parser
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
name|solr
operator|.
name|request
operator|.
name|SolrQueryRequest
import|;
end_import

begin_comment
comment|/**  * Parse Solr's variant on the Lucene {@link org.apache.lucene.queryparser.complexPhrase.ComplexPhraseQueryParser} syntax.  *<p>  * Modified from {@link org.apache.solr.search.LuceneQParserPlugin} and {@link org.apache.solr.search.SurroundQParserPlugin}  */
end_comment

begin_class
DECL|class|ComplexPhraseQParserPlugin
specifier|public
class|class
name|ComplexPhraseQParserPlugin
extends|extends
name|QParserPlugin
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"complexphrase"
decl_stmt|;
DECL|field|inOrder
specifier|private
name|boolean
name|inOrder
init|=
literal|true
decl_stmt|;
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{
name|super
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
if|if
condition|(
name|args
operator|!=
literal|null
condition|)
block|{
name|Object
name|val
init|=
name|args
operator|.
name|get
argument_list|(
literal|"inOrder"
argument_list|)
decl_stmt|;
if|if
condition|(
name|val
operator|!=
literal|null
condition|)
block|{
name|inOrder
operator|=
name|StrUtils
operator|.
name|parseBool
argument_list|(
name|val
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|createParser
specifier|public
name|QParser
name|createParser
parameter_list|(
name|String
name|qstr
parameter_list|,
name|SolrParams
name|localParams
parameter_list|,
name|SolrParams
name|params
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|)
block|{
name|ComplexPhraseQParser
name|qParser
init|=
operator|new
name|ComplexPhraseQParser
argument_list|(
name|qstr
argument_list|,
name|localParams
argument_list|,
name|params
argument_list|,
name|req
argument_list|)
decl_stmt|;
name|qParser
operator|.
name|setInOrder
argument_list|(
name|inOrder
argument_list|)
expr_stmt|;
return|return
name|qParser
return|;
block|}
comment|/**    * Modified from {@link org.apache.solr.search.LuceneQParser} and {@link org.apache.solr.search.SurroundQParserPlugin.SurroundQParser}    */
DECL|class|ComplexPhraseQParser
specifier|static
class|class
name|ComplexPhraseQParser
extends|extends
name|QParser
block|{
DECL|class|SolrQueryParserDelegate
specifier|static
specifier|final
class|class
name|SolrQueryParserDelegate
extends|extends
name|SolrQueryParser
block|{
DECL|method|SolrQueryParserDelegate
specifier|private
name|SolrQueryParserDelegate
parameter_list|(
name|QParser
name|parser
parameter_list|,
name|String
name|defaultField
parameter_list|)
block|{
name|super
argument_list|(
name|parser
argument_list|,
name|defaultField
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getWildcardQuery
specifier|protected
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|Query
name|getWildcardQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|termStr
parameter_list|)
throws|throws
name|SyntaxError
block|{
return|return
name|super
operator|.
name|getWildcardQuery
argument_list|(
name|field
argument_list|,
name|termStr
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getRangeQuery
specifier|protected
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|Query
name|getRangeQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|part1
parameter_list|,
name|String
name|part2
parameter_list|,
name|boolean
name|startInclusive
parameter_list|,
name|boolean
name|endInclusive
parameter_list|)
throws|throws
name|SyntaxError
block|{
return|return
name|super
operator|.
name|getRangeQuery
argument_list|(
name|field
argument_list|,
name|part1
argument_list|,
name|part2
argument_list|,
name|startInclusive
argument_list|,
name|endInclusive
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|isRangeShouldBeProtectedFromReverse
specifier|protected
name|boolean
name|isRangeShouldBeProtectedFromReverse
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|part1
parameter_list|)
block|{
return|return
name|super
operator|.
name|isRangeShouldBeProtectedFromReverse
argument_list|(
name|field
argument_list|,
name|part1
argument_list|)
return|;
block|}
DECL|method|getLowerBoundForReverse
specifier|public
name|String
name|getLowerBoundForReverse
parameter_list|()
block|{
return|return
name|REVERSE_WILDCARD_LOWER_BOUND
return|;
block|}
block|}
DECL|field|lparser
name|ComplexPhraseQueryParser
name|lparser
decl_stmt|;
DECL|field|inOrder
name|boolean
name|inOrder
init|=
literal|true
decl_stmt|;
comment|/**      * When<code>inOrder</code> is true, the search terms must      * exists in the documents as the same order as in query.      *      * @param inOrder parameter to choose between ordered or un-ordered proximity search      */
DECL|method|setInOrder
specifier|public
name|void
name|setInOrder
parameter_list|(
specifier|final
name|boolean
name|inOrder
parameter_list|)
block|{
name|this
operator|.
name|inOrder
operator|=
name|inOrder
expr_stmt|;
block|}
DECL|method|ComplexPhraseQParser
specifier|public
name|ComplexPhraseQParser
parameter_list|(
name|String
name|qstr
parameter_list|,
name|SolrParams
name|localParams
parameter_list|,
name|SolrParams
name|params
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|)
block|{
name|super
argument_list|(
name|qstr
argument_list|,
name|localParams
argument_list|,
name|params
argument_list|,
name|req
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|parse
specifier|public
name|Query
name|parse
parameter_list|()
throws|throws
name|SyntaxError
block|{
name|String
name|qstr
init|=
name|getString
argument_list|()
decl_stmt|;
name|String
name|defaultField
init|=
name|getParam
argument_list|(
name|CommonParams
operator|.
name|DF
argument_list|)
decl_stmt|;
name|SolrQueryParserDelegate
name|reverseAwareParser
init|=
operator|new
name|SolrQueryParserDelegate
argument_list|(
name|this
argument_list|,
name|defaultField
argument_list|)
decl_stmt|;
name|lparser
operator|=
operator|new
name|ComplexPhraseQueryParser
argument_list|(
name|defaultField
argument_list|,
name|getReq
argument_list|()
operator|.
name|getSchema
argument_list|()
operator|.
name|getQueryAnalyzer
argument_list|()
argument_list|)
block|{
specifier|protected
name|Query
name|newWildcardQuery
parameter_list|(
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|Term
name|t
parameter_list|)
block|{
try|try
block|{
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|Query
name|wildcardQuery
init|=
name|reverseAwareParser
operator|.
name|getWildcardQuery
argument_list|(
name|t
operator|.
name|field
argument_list|()
argument_list|,
name|t
operator|.
name|text
argument_list|()
argument_list|)
decl_stmt|;
name|setRewriteMethod
argument_list|(
name|wildcardQuery
argument_list|)
expr_stmt|;
return|return
name|wildcardQuery
return|;
block|}
catch|catch
parameter_list|(
name|SyntaxError
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
block|}
specifier|private
name|Query
name|setRewriteMethod
parameter_list|(
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|Query
name|query
parameter_list|)
block|{
if|if
condition|(
name|query
operator|instanceof
name|MultiTermQuery
condition|)
block|{
operator|(
operator|(
name|MultiTermQuery
operator|)
name|query
operator|)
operator|.
name|setRewriteMethod
argument_list|(
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|MultiTermQuery
operator|.
name|SCORING_BOOLEAN_REWRITE
argument_list|)
expr_stmt|;
block|}
return|return
name|query
return|;
block|}
specifier|protected
name|Query
name|newRangeQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|part1
parameter_list|,
name|String
name|part2
parameter_list|,
name|boolean
name|startInclusive
parameter_list|,
name|boolean
name|endInclusive
parameter_list|)
block|{
name|boolean
name|reverse
init|=
name|reverseAwareParser
operator|.
name|isRangeShouldBeProtectedFromReverse
argument_list|(
name|field
argument_list|,
name|part1
argument_list|)
decl_stmt|;
return|return
name|super
operator|.
name|newRangeQuery
argument_list|(
name|field
argument_list|,
name|reverse
condition|?
name|reverseAwareParser
operator|.
name|getLowerBoundForReverse
argument_list|()
else|:
name|part1
argument_list|,
name|part2
argument_list|,
name|startInclusive
operator|||
name|reverse
argument_list|,
name|endInclusive
argument_list|)
return|;
block|}
block|}
expr_stmt|;
name|lparser
operator|.
name|setAllowLeadingWildcard
argument_list|(
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|localParams
operator|!=
literal|null
condition|)
block|{
name|inOrder
operator|=
name|localParams
operator|.
name|getBool
argument_list|(
literal|"inOrder"
argument_list|,
name|inOrder
argument_list|)
expr_stmt|;
block|}
name|lparser
operator|.
name|setInOrder
argument_list|(
name|inOrder
argument_list|)
expr_stmt|;
name|QueryParser
operator|.
name|Operator
name|defaultOperator
init|=
name|QueryParsing
operator|.
name|parseOP
argument_list|(
name|getParam
argument_list|(
name|QueryParsing
operator|.
name|OP
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|QueryParser
operator|.
name|Operator
operator|.
name|AND
operator|.
name|equals
argument_list|(
name|defaultOperator
argument_list|)
condition|)
name|lparser
operator|.
name|setDefaultOperator
argument_list|(
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|classic
operator|.
name|QueryParser
operator|.
name|Operator
operator|.
name|AND
argument_list|)
expr_stmt|;
else|else
name|lparser
operator|.
name|setDefaultOperator
argument_list|(
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|classic
operator|.
name|QueryParser
operator|.
name|Operator
operator|.
name|OR
argument_list|)
expr_stmt|;
try|try
block|{
return|return
name|lparser
operator|.
name|parse
argument_list|(
name|qstr
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|pe
parameter_list|)
block|{
throw|throw
operator|new
name|SyntaxError
argument_list|(
name|pe
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getDefaultHighlightFields
specifier|public
name|String
index|[]
name|getDefaultHighlightFields
parameter_list|()
block|{
return|return
name|lparser
operator|==
literal|null
condition|?
operator|new
name|String
index|[]
block|{}
else|:
operator|new
name|String
index|[]
block|{
name|lparser
operator|.
name|getField
argument_list|()
block|}
return|;
block|}
block|}
block|}
end_class

end_unit

