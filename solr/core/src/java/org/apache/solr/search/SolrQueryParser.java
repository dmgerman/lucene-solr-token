begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
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
name|classic
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
name|search
operator|.
name|*
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
name|ToStringUtils
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
name|automaton
operator|.
name|Automaton
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
name|automaton
operator|.
name|BasicAutomata
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
name|automaton
operator|.
name|BasicOperations
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
name|automaton
operator|.
name|SpecialOperations
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
name|solr
operator|.
name|analysis
operator|.
name|*
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
name|schema
operator|.
name|TextField
import|;
end_import

begin_comment
comment|/**  * A variation on the Lucene QueryParser which knows about the field   * types and query time analyzers configured in Solr's schema.xml.  *  *<p>  * This class also deviates from the Lucene QueryParser by using   * ConstantScore versions of RangeQuery and PrefixQuery to prevent   * TooManyClauses exceptions.  *</p>   *  *<p>  * If the magic field name "<code>_val_</code>" is used in a term or   * phrase query, the value is parsed as a function.  *</p>  */
end_comment

begin_class
DECL|class|SolrQueryParser
specifier|public
class|class
name|SolrQueryParser
extends|extends
name|QueryParser
block|{
DECL|field|schema
specifier|protected
specifier|final
name|IndexSchema
name|schema
decl_stmt|;
DECL|field|parser
specifier|protected
specifier|final
name|QParser
name|parser
decl_stmt|;
DECL|field|defaultField
specifier|protected
specifier|final
name|String
name|defaultField
decl_stmt|;
comment|// implementation detail - caching ReversedWildcardFilterFactory based on type
DECL|field|leadingWildcards
specifier|private
name|Map
argument_list|<
name|FieldType
argument_list|,
name|ReversedWildcardFilterFactory
argument_list|>
name|leadingWildcards
decl_stmt|;
DECL|method|SolrQueryParser
specifier|public
name|SolrQueryParser
parameter_list|(
name|QParser
name|parser
parameter_list|,
name|String
name|defaultField
parameter_list|)
block|{
name|this
argument_list|(
name|parser
argument_list|,
name|defaultField
argument_list|,
name|parser
operator|.
name|getReq
argument_list|()
operator|.
name|getSchema
argument_list|()
operator|.
name|getQueryAnalyzer
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|SolrQueryParser
specifier|public
name|SolrQueryParser
parameter_list|(
name|QParser
name|parser
parameter_list|,
name|String
name|defaultField
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|)
block|{
name|super
argument_list|(
name|parser
operator|.
name|getReq
argument_list|()
operator|.
name|getCore
argument_list|()
operator|.
name|getSolrConfig
argument_list|()
operator|.
name|luceneMatchVersion
argument_list|,
name|defaultField
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
name|this
operator|.
name|schema
operator|=
name|parser
operator|.
name|getReq
argument_list|()
operator|.
name|getSchema
argument_list|()
expr_stmt|;
name|this
operator|.
name|parser
operator|=
name|parser
expr_stmt|;
name|this
operator|.
name|defaultField
operator|=
name|defaultField
expr_stmt|;
name|setEnablePositionIncrements
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|setLowercaseExpandedTerms
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|setAllowLeadingWildcard
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|getReversedWildcardFilterFactory
specifier|protected
name|ReversedWildcardFilterFactory
name|getReversedWildcardFilterFactory
parameter_list|(
name|FieldType
name|fieldType
parameter_list|)
block|{
if|if
condition|(
name|leadingWildcards
operator|==
literal|null
condition|)
name|leadingWildcards
operator|=
operator|new
name|HashMap
argument_list|<
name|FieldType
argument_list|,
name|ReversedWildcardFilterFactory
argument_list|>
argument_list|()
expr_stmt|;
name|ReversedWildcardFilterFactory
name|fac
init|=
name|leadingWildcards
operator|.
name|get
argument_list|(
name|fieldType
argument_list|)
decl_stmt|;
if|if
condition|(
name|fac
operator|==
literal|null
operator|&&
name|leadingWildcards
operator|.
name|containsKey
argument_list|(
name|fac
argument_list|)
condition|)
block|{
return|return
name|fac
return|;
block|}
name|Analyzer
name|a
init|=
name|fieldType
operator|.
name|getAnalyzer
argument_list|()
decl_stmt|;
if|if
condition|(
name|a
operator|instanceof
name|TokenizerChain
condition|)
block|{
comment|// examine the indexing analysis chain if it supports leading wildcards
name|TokenizerChain
name|tc
init|=
operator|(
name|TokenizerChain
operator|)
name|a
decl_stmt|;
name|TokenFilterFactory
index|[]
name|factories
init|=
name|tc
operator|.
name|getTokenFilterFactories
argument_list|()
decl_stmt|;
for|for
control|(
name|TokenFilterFactory
name|factory
range|:
name|factories
control|)
block|{
if|if
condition|(
name|factory
operator|instanceof
name|ReversedWildcardFilterFactory
condition|)
block|{
name|fac
operator|=
operator|(
name|ReversedWildcardFilterFactory
operator|)
name|factory
expr_stmt|;
break|break;
block|}
block|}
block|}
name|leadingWildcards
operator|.
name|put
argument_list|(
name|fieldType
argument_list|,
name|fac
argument_list|)
expr_stmt|;
return|return
name|fac
return|;
block|}
DECL|method|checkNullField
specifier|private
name|void
name|checkNullField
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|SolrException
block|{
if|if
condition|(
name|field
operator|==
literal|null
operator|&&
name|defaultField
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
literal|"no field name specified in query and no defaultSearchField defined in schema.xml"
argument_list|)
throw|;
block|}
block|}
DECL|method|analyzeIfMultitermTermText
specifier|protected
name|String
name|analyzeIfMultitermTermText
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|part
parameter_list|,
name|FieldType
name|fieldType
parameter_list|)
block|{
if|if
condition|(
name|part
operator|==
literal|null
condition|)
return|return
name|part
return|;
name|SchemaField
name|sf
init|=
name|schema
operator|.
name|getFieldOrNull
argument_list|(
operator|(
name|field
operator|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|sf
operator|==
literal|null
operator|||
operator|!
operator|(
name|fieldType
operator|instanceof
name|TextField
operator|)
condition|)
return|return
name|part
return|;
name|String
name|out
init|=
name|TextField
operator|.
name|analyzeMultiTerm
argument_list|(
name|field
argument_list|,
name|part
argument_list|,
operator|(
operator|(
name|TextField
operator|)
name|fieldType
operator|)
operator|.
name|getMultiTermAnalyzer
argument_list|()
argument_list|)
operator|.
name|utf8ToString
argument_list|()
decl_stmt|;
comment|// System.out.println("INPUT="+part + " OUTPUT="+out);
return|return
name|out
return|;
block|}
annotation|@
name|Override
DECL|method|getFieldQuery
specifier|protected
name|Query
name|getFieldQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|queryText
parameter_list|,
name|boolean
name|quoted
parameter_list|)
throws|throws
name|ParseException
block|{
name|checkNullField
argument_list|(
name|field
argument_list|)
expr_stmt|;
comment|// intercept magic field name of "_" to use as a hook for our
comment|// own functions.
if|if
condition|(
name|field
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'_'
condition|)
block|{
if|if
condition|(
literal|"_val_"
operator|.
name|equals
argument_list|(
name|field
argument_list|)
condition|)
block|{
name|QParser
name|nested
init|=
name|parser
operator|.
name|subQuery
argument_list|(
name|queryText
argument_list|,
literal|"func"
argument_list|)
decl_stmt|;
return|return
name|nested
operator|.
name|getQuery
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
literal|"_query_"
operator|.
name|equals
argument_list|(
name|field
argument_list|)
operator|&&
name|parser
operator|!=
literal|null
condition|)
block|{
return|return
name|parser
operator|.
name|subQuery
argument_list|(
name|queryText
argument_list|,
literal|null
argument_list|)
operator|.
name|getQuery
argument_list|()
return|;
block|}
block|}
name|SchemaField
name|sf
init|=
name|schema
operator|.
name|getFieldOrNull
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|sf
operator|!=
literal|null
condition|)
block|{
name|FieldType
name|ft
init|=
name|sf
operator|.
name|getType
argument_list|()
decl_stmt|;
comment|// delegate to type for everything except tokenized fields
if|if
condition|(
name|ft
operator|.
name|isTokenized
argument_list|()
condition|)
block|{
return|return
name|super
operator|.
name|getFieldQuery
argument_list|(
name|field
argument_list|,
name|queryText
argument_list|,
name|quoted
operator|||
operator|(
name|ft
operator|instanceof
name|TextField
operator|&&
operator|(
operator|(
name|TextField
operator|)
name|ft
operator|)
operator|.
name|getAutoGeneratePhraseQueries
argument_list|()
operator|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|sf
operator|.
name|getType
argument_list|()
operator|.
name|getFieldQuery
argument_list|(
name|parser
argument_list|,
name|sf
argument_list|,
name|queryText
argument_list|)
return|;
block|}
block|}
comment|// default to a normal field query
return|return
name|super
operator|.
name|getFieldQuery
argument_list|(
name|field
argument_list|,
name|queryText
argument_list|,
name|quoted
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getRangeQuery
specifier|protected
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
name|ParseException
block|{
name|checkNullField
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|SchemaField
name|sf
init|=
name|schema
operator|.
name|getField
argument_list|(
name|field
argument_list|)
decl_stmt|;
return|return
name|sf
operator|.
name|getType
argument_list|()
operator|.
name|getRangeQuery
argument_list|(
name|parser
argument_list|,
name|sf
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
DECL|method|getPrefixQuery
specifier|protected
name|Query
name|getPrefixQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|termStr
parameter_list|)
throws|throws
name|ParseException
block|{
name|checkNullField
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|termStr
operator|=
name|analyzeIfMultitermTermText
argument_list|(
name|field
argument_list|,
name|termStr
argument_list|,
name|schema
operator|.
name|getFieldType
argument_list|(
name|field
argument_list|)
argument_list|)
expr_stmt|;
comment|// Solr has always used constant scoring for prefix queries.  This should return constant scoring by default.
return|return
name|newPrefixQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|termStr
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getWildcardQuery
specifier|protected
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
name|ParseException
block|{
comment|// *:* -> MatchAllDocsQuery
if|if
condition|(
literal|"*"
operator|.
name|equals
argument_list|(
name|field
argument_list|)
operator|&&
literal|"*"
operator|.
name|equals
argument_list|(
name|termStr
argument_list|)
condition|)
block|{
return|return
name|newMatchAllDocsQuery
argument_list|()
return|;
block|}
name|FieldType
name|fieldType
init|=
name|schema
operator|.
name|getFieldType
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|termStr
operator|=
name|analyzeIfMultitermTermText
argument_list|(
name|field
argument_list|,
name|termStr
argument_list|,
name|fieldType
argument_list|)
expr_stmt|;
comment|// can we use reversed wildcards in this field?
name|ReversedWildcardFilterFactory
name|factory
init|=
name|getReversedWildcardFilterFactory
argument_list|(
name|fieldType
argument_list|)
decl_stmt|;
if|if
condition|(
name|factory
operator|!=
literal|null
condition|)
block|{
name|Term
name|term
init|=
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|termStr
argument_list|)
decl_stmt|;
comment|// fsa representing the query
name|Automaton
name|automaton
init|=
name|WildcardQuery
operator|.
name|toAutomaton
argument_list|(
name|term
argument_list|)
decl_stmt|;
comment|// TODO: we should likely use the automaton to calculate shouldReverse, too.
if|if
condition|(
name|factory
operator|.
name|shouldReverse
argument_list|(
name|termStr
argument_list|)
condition|)
block|{
name|automaton
operator|=
name|BasicOperations
operator|.
name|concatenate
argument_list|(
name|automaton
argument_list|,
name|BasicAutomata
operator|.
name|makeChar
argument_list|(
name|factory
operator|.
name|getMarkerChar
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|SpecialOperations
operator|.
name|reverse
argument_list|(
name|automaton
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// reverse wildcardfilter is active: remove false positives
comment|// fsa representing false positives (markerChar*)
name|Automaton
name|falsePositives
init|=
name|BasicOperations
operator|.
name|concatenate
argument_list|(
name|BasicAutomata
operator|.
name|makeChar
argument_list|(
name|factory
operator|.
name|getMarkerChar
argument_list|()
argument_list|)
argument_list|,
name|BasicAutomata
operator|.
name|makeAnyString
argument_list|()
argument_list|)
decl_stmt|;
comment|// subtract these away
name|automaton
operator|=
name|BasicOperations
operator|.
name|minus
argument_list|(
name|automaton
argument_list|,
name|falsePositives
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|AutomatonQuery
argument_list|(
name|term
argument_list|,
name|automaton
argument_list|)
block|{
comment|// override toString so its completely transparent
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|getField
argument_list|()
operator|.
name|equals
argument_list|(
name|field
argument_list|)
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
name|getField
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|":"
argument_list|)
expr_stmt|;
block|}
name|buffer
operator|.
name|append
argument_list|(
name|term
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|ToStringUtils
operator|.
name|boost
argument_list|(
name|getBoost
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
return|;
block|}
comment|// Solr has always used constant scoring for wildcard queries.  This should return constant scoring by default.
return|return
name|newWildcardQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|termStr
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getRegexpQuery
specifier|protected
name|Query
name|getRegexpQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|termStr
parameter_list|)
throws|throws
name|ParseException
block|{
name|termStr
operator|=
name|analyzeIfMultitermTermText
argument_list|(
name|field
argument_list|,
name|termStr
argument_list|,
name|schema
operator|.
name|getFieldType
argument_list|(
name|field
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|newRegexpQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|termStr
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

