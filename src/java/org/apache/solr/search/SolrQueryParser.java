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
name|reverse
operator|.
name|ReverseStringFilter
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
name|TrieField
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
comment|// TODO: implement the analysis of simple fields with
end_comment

begin_comment
comment|// FieldType.toInternal() instead of going through the
end_comment

begin_comment
comment|// analyzer.  Should lead to faster query parsing.
end_comment

begin_comment
comment|/**  * A variation on the Lucene QueryParser which knows about the field   * types and query time analyzers configured in Solr's schema.xml.  *  *<p>  * This class also deviates from the Lucene QueryParser by using   * ConstantScore versions of RangeQuery and PrefixQuery to prevent   * TooManyClauses exceptions.  *</p>   *  *<p>  * If the magic field name "<code>_val_</code>" is used in a term or   * phrase query, the value is parsed as a function.  *</p>  *  * @see QueryParsing#parseFunction  * @see ConstantScoreRangeQuery  * @see ConstantScorePrefixQuery  */
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
DECL|field|leadingWildcards
specifier|protected
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|ReversedWildcardFilterFactory
argument_list|>
name|leadingWildcards
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|ReversedWildcardFilterFactory
argument_list|>
argument_list|()
decl_stmt|;
comment|/**    * Constructs a SolrQueryParser using the schema to understand the    * formats and datatypes of each field.  Only the defaultSearchField    * will be used from the IndexSchema (unless overridden),    *&lt;solrQueryParser&gt; will not be used.    *     * @param schema Used for default search field name if defaultField is null and field information is used for analysis    * @param defaultField default field used for unspecified search terms.  if null, the schema default field is used    * @see IndexSchema#getDefaultSearchFieldName()    */
DECL|method|SolrQueryParser
specifier|public
name|SolrQueryParser
parameter_list|(
name|IndexSchema
name|schema
parameter_list|,
name|String
name|defaultField
parameter_list|)
block|{
name|super
argument_list|(
name|defaultField
operator|==
literal|null
condition|?
name|schema
operator|.
name|getDefaultSearchFieldName
argument_list|()
else|:
name|defaultField
argument_list|,
name|schema
operator|.
name|getQueryAnalyzer
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|schema
operator|=
name|schema
expr_stmt|;
name|this
operator|.
name|parser
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|defaultField
operator|=
name|defaultField
expr_stmt|;
name|setLowercaseExpandedTerms
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|setEnablePositionIncrements
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|checkAllowLeadingWildcards
argument_list|()
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
name|setLowercaseExpandedTerms
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|setEnablePositionIncrements
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|checkAllowLeadingWildcards
argument_list|()
expr_stmt|;
block|}
DECL|method|checkAllowLeadingWildcards
specifier|protected
name|void
name|checkAllowLeadingWildcards
parameter_list|()
block|{
name|boolean
name|allow
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|FieldType
argument_list|>
name|e
range|:
name|schema
operator|.
name|getFieldTypes
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Analyzer
name|a
init|=
name|e
operator|.
name|getValue
argument_list|()
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
name|allow
operator|=
literal|true
expr_stmt|;
name|leadingWildcards
operator|.
name|put
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
operator|(
name|ReversedWildcardFilterFactory
operator|)
name|factory
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|// XXX should be enabled on a per-field basis
if|if
condition|(
name|allow
condition|)
block|{
name|setAllowLeadingWildcard
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
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
if|if
condition|(
name|parser
operator|==
literal|null
condition|)
block|{
return|return
name|QueryParsing
operator|.
name|parseFunction
argument_list|(
name|queryText
argument_list|,
name|schema
argument_list|)
return|;
block|}
else|else
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
comment|//Intercept poly fields, as they get expanded by default to an OR clause of
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
comment|//TODO: is there anyway to avoid this instance of check?
if|if
condition|(
name|sf
operator|!=
literal|null
operator|&&
operator|!
operator|(
name|sf
operator|.
name|getType
argument_list|()
operator|instanceof
name|TextField
operator|)
condition|)
block|{
comment|//we have a poly field, deal with it specially by delegating to the FieldType
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
comment|// default to a normal field query
return|return
name|super
operator|.
name|getFieldQuery
argument_list|(
name|field
argument_list|,
name|queryText
argument_list|)
return|;
block|}
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
name|inclusive
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
literal|"*"
operator|.
name|equals
argument_list|(
name|part1
argument_list|)
condition|?
literal|null
else|:
name|part1
argument_list|,
literal|"*"
operator|.
name|equals
argument_list|(
name|part2
argument_list|)
condition|?
literal|null
else|:
name|part2
argument_list|,
name|inclusive
argument_list|,
name|inclusive
argument_list|)
return|;
block|}
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
if|if
condition|(
name|getLowercaseExpandedTerms
argument_list|()
condition|)
block|{
name|termStr
operator|=
name|termStr
operator|.
name|toLowerCase
argument_list|()
expr_stmt|;
block|}
comment|// TODO: toInternal() won't necessarily work on partial
comment|// values, so it looks like we need a getPrefix() function
comment|// on fieldtype?  Or at the minimum, a method on fieldType
comment|// that can tell me if I should lowercase or not...
comment|// Schema could tell if lowercase filter is in the chain,
comment|// but a more sure way would be to run something through
comment|// the first time and check if it got lowercased.
comment|// TODO: throw exception if field type doesn't support prefixes?
comment|// (sortable numeric types don't do prefixes, but can do range queries)
name|Term
name|t
init|=
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|termStr
argument_list|)
decl_stmt|;
name|PrefixQuery
name|prefixQuery
init|=
operator|new
name|PrefixQuery
argument_list|(
name|t
argument_list|)
decl_stmt|;
return|return
name|prefixQuery
return|;
block|}
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
comment|// can we use reversed wildcards in this field?
name|String
name|type
init|=
name|schema
operator|.
name|getFieldType
argument_list|(
name|field
argument_list|)
operator|.
name|getTypeName
argument_list|()
decl_stmt|;
name|ReversedWildcardFilterFactory
name|factory
init|=
name|leadingWildcards
operator|.
name|get
argument_list|(
name|type
argument_list|)
decl_stmt|;
if|if
condition|(
name|factory
operator|!=
literal|null
operator|&&
name|factory
operator|.
name|shouldReverse
argument_list|(
name|termStr
argument_list|)
condition|)
block|{
name|int
name|len
init|=
name|termStr
operator|.
name|length
argument_list|()
decl_stmt|;
name|char
index|[]
name|chars
init|=
operator|new
name|char
index|[
name|len
operator|+
literal|1
index|]
decl_stmt|;
name|chars
index|[
literal|0
index|]
operator|=
name|factory
operator|.
name|getMarkerChar
argument_list|()
expr_stmt|;
name|termStr
operator|.
name|getChars
argument_list|(
literal|0
argument_list|,
name|len
argument_list|,
name|chars
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|ReversedWildcardFilter
operator|.
name|reverse
argument_list|(
name|chars
argument_list|,
literal|1
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|termStr
operator|=
operator|new
name|String
argument_list|(
name|chars
argument_list|)
expr_stmt|;
block|}
name|Query
name|q
init|=
name|super
operator|.
name|getWildcardQuery
argument_list|(
name|field
argument_list|,
name|termStr
argument_list|)
decl_stmt|;
if|if
condition|(
name|q
operator|instanceof
name|WildcardQuery
condition|)
block|{
comment|// use a constant score query to avoid overflowing clauses
name|WildcardQuery
name|wildcardQuery
init|=
operator|new
name|WildcardQuery
argument_list|(
operator|(
operator|(
name|WildcardQuery
operator|)
name|q
operator|)
operator|.
name|getTerm
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|wildcardQuery
return|;
block|}
return|return
name|q
return|;
block|}
block|}
end_class

end_unit

