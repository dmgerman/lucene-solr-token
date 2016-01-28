begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
name|simple
operator|.
name|SimpleQueryParser
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
name|BoostQuery
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
name|FuzzyQuery
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
name|SimpleParams
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

begin_comment
comment|/**  * Create a query from the input value that will be parsed by Lucene's SimpleQueryParser.  * See {@link org.apache.lucene.queryparser.simple.SimpleQueryParser} for details on the exact syntax allowed  * to be used for queries.  *<br>  * The following options may be applied for parsing the query.  *<ul>  *<li>  *     q.operators - Used to enable specific operations for parsing.  The operations that can be enabled are  *                   and, not, or, prefix, phrase, precedence, escape, and whitespace.  By default all operations  *                   are enabled.  All operations can be disabled by passing in an empty string to this parameter.  *</li>  *<li>  *     q.op - Used to specify the operator to be used if whitespace is a delimiter. Either 'AND' or 'OR'  *            can be specified for this parameter.  Any other string will cause an exception to be thrown.  *            If this parameter is not specified 'OR' will be used by default.  *</li>  *<li>  *     qf - The list of query fields and boosts to use when building the simple query.  The format is the following:  *<code>fieldA^1.0 fieldB^2.2</code>.  A field can also be specified without a boost by simply listing the  *          field as<code>fieldA fieldB</code>.  Any field without a boost will default to use a boost of 1.0.  *</li>  *<li>  *     df - An override for the default field specified in the schema or a default field if one is not specified  *          in the schema.  If qf is not specified the default field will be used as the field to run the query  *          against.  *</li>  *</ul>  */
end_comment

begin_class
DECL|class|SimpleQParserPlugin
specifier|public
class|class
name|SimpleQParserPlugin
extends|extends
name|QParserPlugin
block|{
comment|/** The name that can be used to specify this plugin should be used to parse the query. */
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"simple"
decl_stmt|;
comment|/** Map of string operators to their int counterparts in SimpleQueryParser. */
DECL|field|OPERATORS
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|OPERATORS
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|/* Setup the map of possible operators. */
static|static
block|{
name|OPERATORS
operator|.
name|put
argument_list|(
name|SimpleParams
operator|.
name|AND_OPERATOR
argument_list|,
name|SimpleQueryParser
operator|.
name|AND_OPERATOR
argument_list|)
expr_stmt|;
name|OPERATORS
operator|.
name|put
argument_list|(
name|SimpleParams
operator|.
name|NOT_OPERATOR
argument_list|,
name|SimpleQueryParser
operator|.
name|NOT_OPERATOR
argument_list|)
expr_stmt|;
name|OPERATORS
operator|.
name|put
argument_list|(
name|SimpleParams
operator|.
name|OR_OPERATOR
argument_list|,
name|SimpleQueryParser
operator|.
name|OR_OPERATOR
argument_list|)
expr_stmt|;
name|OPERATORS
operator|.
name|put
argument_list|(
name|SimpleParams
operator|.
name|PREFIX_OPERATOR
argument_list|,
name|SimpleQueryParser
operator|.
name|PREFIX_OPERATOR
argument_list|)
expr_stmt|;
name|OPERATORS
operator|.
name|put
argument_list|(
name|SimpleParams
operator|.
name|PHRASE_OPERATOR
argument_list|,
name|SimpleQueryParser
operator|.
name|PHRASE_OPERATOR
argument_list|)
expr_stmt|;
name|OPERATORS
operator|.
name|put
argument_list|(
name|SimpleParams
operator|.
name|PRECEDENCE_OPERATORS
argument_list|,
name|SimpleQueryParser
operator|.
name|PRECEDENCE_OPERATORS
argument_list|)
expr_stmt|;
name|OPERATORS
operator|.
name|put
argument_list|(
name|SimpleParams
operator|.
name|ESCAPE_OPERATOR
argument_list|,
name|SimpleQueryParser
operator|.
name|ESCAPE_OPERATOR
argument_list|)
expr_stmt|;
name|OPERATORS
operator|.
name|put
argument_list|(
name|SimpleParams
operator|.
name|WHITESPACE_OPERATOR
argument_list|,
name|SimpleQueryParser
operator|.
name|WHITESPACE_OPERATOR
argument_list|)
expr_stmt|;
name|OPERATORS
operator|.
name|put
argument_list|(
name|SimpleParams
operator|.
name|FUZZY_OPERATOR
argument_list|,
name|SimpleQueryParser
operator|.
name|FUZZY_OPERATOR
argument_list|)
expr_stmt|;
name|OPERATORS
operator|.
name|put
argument_list|(
name|SimpleParams
operator|.
name|NEAR_OPERATOR
argument_list|,
name|SimpleQueryParser
operator|.
name|NEAR_OPERATOR
argument_list|)
expr_stmt|;
block|}
comment|/** Returns a QParser that will create a query by using Lucene's SimpleQueryParser. */
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
return|return
operator|new
name|SimpleQParser
argument_list|(
name|qstr
argument_list|,
name|localParams
argument_list|,
name|params
argument_list|,
name|req
argument_list|)
return|;
block|}
DECL|class|SimpleQParser
specifier|private
specifier|static
class|class
name|SimpleQParser
extends|extends
name|QParser
block|{
DECL|field|parser
specifier|private
name|SimpleQueryParser
name|parser
decl_stmt|;
DECL|method|SimpleQParser
specifier|public
name|SimpleQParser
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
comment|// Some of the parameters may come in through localParams, so combine them with params.
name|SolrParams
name|defaultParams
init|=
name|SolrParams
operator|.
name|wrapDefaults
argument_list|(
name|localParams
argument_list|,
name|params
argument_list|)
decl_stmt|;
comment|// This will be used to specify what fields and boosts will be used by SimpleQueryParser.
name|Map
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|queryFields
init|=
name|SolrPluginUtils
operator|.
name|parseFieldBoosts
argument_list|(
name|defaultParams
operator|.
name|get
argument_list|(
name|SimpleParams
operator|.
name|QF
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|queryFields
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// It qf is not specified setup up the queryFields map to use the defaultField.
name|String
name|defaultField
init|=
name|QueryParsing
operator|.
name|getDefaultField
argument_list|(
name|req
operator|.
name|getSchema
argument_list|()
argument_list|,
name|defaultParams
operator|.
name|get
argument_list|(
name|CommonParams
operator|.
name|DF
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|defaultField
operator|==
literal|null
condition|)
block|{
comment|// A query cannot be run without having a field or set of fields to run against.
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Neither "
operator|+
name|SimpleParams
operator|.
name|QF
operator|+
literal|", "
operator|+
name|CommonParams
operator|.
name|DF
operator|+
literal|", nor the default search field are present."
argument_list|)
throw|;
block|}
name|queryFields
operator|.
name|put
argument_list|(
name|defaultField
argument_list|,
literal|1.0F
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|queryField
range|:
name|queryFields
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|queryField
operator|.
name|getValue
argument_list|()
operator|==
literal|null
condition|)
block|{
comment|// Some fields may be specified without a boost, so default the boost to 1.0 since a null value
comment|// will not be accepted by SimpleQueryParser.
name|queryField
operator|.
name|setValue
argument_list|(
literal|1.0F
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// Setup the operations that are enabled for the query.
name|int
name|enabledOps
init|=
literal|0
decl_stmt|;
name|String
name|opParam
init|=
name|defaultParams
operator|.
name|get
argument_list|(
name|SimpleParams
operator|.
name|QO
argument_list|)
decl_stmt|;
if|if
condition|(
name|opParam
operator|==
literal|null
condition|)
block|{
comment|// All operations will be enabled.
name|enabledOps
operator|=
operator|-
literal|1
expr_stmt|;
block|}
else|else
block|{
comment|// Parse the specified enabled operations to be used by the query.
name|String
index|[]
name|operations
init|=
name|opParam
operator|.
name|split
argument_list|(
literal|","
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|operation
range|:
name|operations
control|)
block|{
name|Integer
name|enabledOp
init|=
name|OPERATORS
operator|.
name|get
argument_list|(
name|operation
operator|.
name|trim
argument_list|()
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|enabledOp
operator|!=
literal|null
condition|)
block|{
name|enabledOps
operator||=
name|enabledOp
expr_stmt|;
block|}
block|}
block|}
comment|// Create a SimpleQueryParser using the analyzer from the schema.
specifier|final
name|IndexSchema
name|schema
init|=
name|req
operator|.
name|getSchema
argument_list|()
decl_stmt|;
name|parser
operator|=
operator|new
name|SolrSimpleQueryParser
argument_list|(
name|req
operator|.
name|getSchema
argument_list|()
operator|.
name|getQueryAnalyzer
argument_list|()
argument_list|,
name|queryFields
argument_list|,
name|enabledOps
argument_list|,
name|this
argument_list|,
name|schema
argument_list|)
expr_stmt|;
comment|// Set the default operator to be either 'AND' or 'OR' for the query.
name|QueryParser
operator|.
name|Operator
name|defaultOp
init|=
name|QueryParsing
operator|.
name|getQueryParserDefaultOperator
argument_list|(
name|req
operator|.
name|getSchema
argument_list|()
argument_list|,
name|defaultParams
operator|.
name|get
argument_list|(
name|QueryParsing
operator|.
name|OP
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|defaultOp
operator|==
name|QueryParser
operator|.
name|Operator
operator|.
name|AND
condition|)
block|{
name|parser
operator|.
name|setDefaultOperator
argument_list|(
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
block|}
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
return|return
name|parser
operator|.
name|parse
argument_list|(
name|qstr
argument_list|)
return|;
block|}
block|}
DECL|class|SolrSimpleQueryParser
specifier|private
specifier|static
class|class
name|SolrSimpleQueryParser
extends|extends
name|SimpleQueryParser
block|{
DECL|field|qParser
name|QParser
name|qParser
decl_stmt|;
DECL|field|schema
name|IndexSchema
name|schema
decl_stmt|;
DECL|method|SolrSimpleQueryParser
specifier|public
name|SolrSimpleQueryParser
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|weights
parameter_list|,
name|int
name|flags
parameter_list|,
name|QParser
name|qParser
parameter_list|,
name|IndexSchema
name|schema
parameter_list|)
block|{
name|super
argument_list|(
name|analyzer
argument_list|,
name|weights
argument_list|,
name|flags
argument_list|)
expr_stmt|;
name|this
operator|.
name|qParser
operator|=
name|qParser
expr_stmt|;
name|this
operator|.
name|schema
operator|=
name|schema
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|newPrefixQuery
specifier|protected
name|Query
name|newPrefixQuery
parameter_list|(
name|String
name|text
parameter_list|)
block|{
name|BooleanQuery
operator|.
name|Builder
name|bq
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|bq
operator|.
name|setDisableCoord
argument_list|(
literal|true
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|entry
range|:
name|weights
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|field
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|FieldType
name|type
init|=
name|schema
operator|.
name|getFieldType
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|Query
name|prefix
decl_stmt|;
if|if
condition|(
name|type
operator|instanceof
name|TextField
condition|)
block|{
comment|// If the field type is a TextField then use the multi term analyzer.
name|Analyzer
name|analyzer
init|=
operator|(
operator|(
name|TextField
operator|)
name|type
operator|)
operator|.
name|getMultiTermAnalyzer
argument_list|()
decl_stmt|;
name|String
name|term
init|=
name|TextField
operator|.
name|analyzeMultiTerm
argument_list|(
name|field
argument_list|,
name|text
argument_list|,
name|analyzer
argument_list|)
operator|.
name|utf8ToString
argument_list|()
decl_stmt|;
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
name|prefix
operator|=
name|sf
operator|.
name|getType
argument_list|()
operator|.
name|getPrefixQuery
argument_list|(
name|qParser
argument_list|,
name|sf
argument_list|,
name|term
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// If the type is *not* a TextField don't do any analysis.
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
name|prefix
operator|=
name|type
operator|.
name|getPrefixQuery
argument_list|(
name|qParser
argument_list|,
name|sf
argument_list|,
name|text
argument_list|)
expr_stmt|;
block|}
name|float
name|boost
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|boost
operator|!=
literal|1f
condition|)
block|{
name|prefix
operator|=
operator|new
name|BoostQuery
argument_list|(
name|prefix
argument_list|,
name|boost
argument_list|)
expr_stmt|;
block|}
name|bq
operator|.
name|add
argument_list|(
name|prefix
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
block|}
return|return
name|simplify
argument_list|(
name|bq
operator|.
name|build
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|newFuzzyQuery
specifier|protected
name|Query
name|newFuzzyQuery
parameter_list|(
name|String
name|text
parameter_list|,
name|int
name|fuzziness
parameter_list|)
block|{
name|BooleanQuery
operator|.
name|Builder
name|bq
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|bq
operator|.
name|setDisableCoord
argument_list|(
literal|true
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|entry
range|:
name|weights
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|field
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|FieldType
name|type
init|=
name|schema
operator|.
name|getFieldType
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|Query
name|fuzzy
decl_stmt|;
if|if
condition|(
name|type
operator|instanceof
name|TextField
condition|)
block|{
comment|// If the field type is a TextField then use the multi term analyzer.
name|Analyzer
name|analyzer
init|=
operator|(
operator|(
name|TextField
operator|)
name|type
operator|)
operator|.
name|getMultiTermAnalyzer
argument_list|()
decl_stmt|;
name|String
name|term
init|=
name|TextField
operator|.
name|analyzeMultiTerm
argument_list|(
name|field
argument_list|,
name|text
argument_list|,
name|analyzer
argument_list|)
operator|.
name|utf8ToString
argument_list|()
decl_stmt|;
name|fuzzy
operator|=
operator|new
name|FuzzyQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|term
argument_list|)
argument_list|,
name|fuzziness
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// If the type is *not* a TextField don't do any analysis.
name|fuzzy
operator|=
operator|new
name|FuzzyQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|text
argument_list|)
argument_list|,
name|fuzziness
argument_list|)
expr_stmt|;
block|}
name|float
name|boost
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|boost
operator|!=
literal|1f
condition|)
block|{
name|fuzzy
operator|=
operator|new
name|BoostQuery
argument_list|(
name|fuzzy
argument_list|,
name|boost
argument_list|)
expr_stmt|;
block|}
name|bq
operator|.
name|add
argument_list|(
name|fuzzy
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
block|}
return|return
name|simplify
argument_list|(
name|bq
operator|.
name|build
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

