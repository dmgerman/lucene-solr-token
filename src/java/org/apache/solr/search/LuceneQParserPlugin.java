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
name|request
operator|.
name|SolrQueryRequest
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

begin_comment
comment|/**  * Parse Solr's variant on the Lucene QueryParser syntax.  *<br>Other parameters:<ul>  *<li>q.op - the default operator "OR" or "AND"</li>  *<li>df - the default field name</li>  *<li>df - the default field name</li>  *</ul>  *<br>Example:<code>&lt;!lucene q.op=AND df=text sort='price asc'&gt;myfield:foo +bar -baz</code>  */
end_comment

begin_class
DECL|class|LuceneQParserPlugin
specifier|public
class|class
name|LuceneQParserPlugin
extends|extends
name|QParserPlugin
block|{
DECL|field|NAME
specifier|public
specifier|static
name|String
name|NAME
init|=
literal|"lucene"
decl_stmt|;
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{   }
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
name|LuceneQParser
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
block|}
end_class

begin_class
DECL|class|LuceneQParser
class|class
name|LuceneQParser
extends|extends
name|QParser
block|{
DECL|field|sortStr
name|String
name|sortStr
decl_stmt|;
DECL|field|lparser
name|SolrQueryParser
name|lparser
decl_stmt|;
DECL|method|LuceneQParser
specifier|public
name|LuceneQParser
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
DECL|method|parse
specifier|public
name|Query
name|parse
parameter_list|()
throws|throws
name|ParseException
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
if|if
condition|(
name|defaultField
operator|==
literal|null
condition|)
block|{
name|defaultField
operator|=
name|getReq
argument_list|()
operator|.
name|getSchema
argument_list|()
operator|.
name|getDefaultSearchFieldName
argument_list|()
expr_stmt|;
block|}
name|lparser
operator|=
operator|new
name|SolrQueryParser
argument_list|(
name|this
argument_list|,
name|defaultField
argument_list|)
expr_stmt|;
comment|// these could either be checked& set here, or in the SolrQueryParser constructor
name|String
name|opParam
init|=
name|getParam
argument_list|(
name|QueryParsing
operator|.
name|OP
argument_list|)
decl_stmt|;
if|if
condition|(
name|opParam
operator|!=
literal|null
condition|)
block|{
name|lparser
operator|.
name|setDefaultOperator
argument_list|(
literal|"AND"
operator|.
name|equals
argument_list|(
name|opParam
argument_list|)
condition|?
name|QueryParser
operator|.
name|Operator
operator|.
name|AND
else|:
name|QueryParser
operator|.
name|Operator
operator|.
name|OR
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// try to get default operator from schema
name|String
name|operator
init|=
name|getReq
argument_list|()
operator|.
name|getSchema
argument_list|()
operator|.
name|getQueryParserDefaultOperator
argument_list|()
decl_stmt|;
name|lparser
operator|.
name|setDefaultOperator
argument_list|(
literal|"AND"
operator|.
name|equals
argument_list|(
name|operator
argument_list|)
condition|?
name|QueryParser
operator|.
name|Operator
operator|.
name|AND
else|:
name|QueryParser
operator|.
name|Operator
operator|.
name|OR
argument_list|)
expr_stmt|;
block|}
return|return
name|lparser
operator|.
name|parse
argument_list|(
name|qstr
argument_list|)
return|;
block|}
DECL|method|getDefaultHighlightFields
specifier|public
name|String
index|[]
name|getDefaultHighlightFields
parameter_list|()
block|{
return|return
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
end_class

begin_class
DECL|class|OldLuceneQParser
class|class
name|OldLuceneQParser
extends|extends
name|LuceneQParser
block|{
DECL|field|sortStr
name|String
name|sortStr
decl_stmt|;
DECL|method|OldLuceneQParser
specifier|public
name|OldLuceneQParser
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
DECL|method|parse
specifier|public
name|Query
name|parse
parameter_list|()
throws|throws
name|ParseException
block|{
comment|// handle legacy "query;sort" syntax
if|if
condition|(
name|getLocalParams
argument_list|()
operator|==
literal|null
condition|)
block|{
name|String
name|qstr
init|=
name|getString
argument_list|()
decl_stmt|;
name|sortStr
operator|=
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
name|CommonParams
operator|.
name|SORT
argument_list|)
expr_stmt|;
if|if
condition|(
name|sortStr
operator|==
literal|null
condition|)
block|{
comment|// sort may be legacy form, included in the query string
name|List
argument_list|<
name|String
argument_list|>
name|commands
init|=
name|StrUtils
operator|.
name|splitSmart
argument_list|(
name|qstr
argument_list|,
literal|';'
argument_list|)
decl_stmt|;
if|if
condition|(
name|commands
operator|.
name|size
argument_list|()
operator|==
literal|2
condition|)
block|{
name|qstr
operator|=
name|commands
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|sortStr
operator|=
name|commands
operator|.
name|get
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|commands
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
comment|// This is need to support the case where someone sends: "q=query;"
name|qstr
operator|=
name|commands
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|commands
operator|.
name|size
argument_list|()
operator|>
literal|2
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
literal|"If you want to use multiple ';' in the query, use the 'sort' param."
argument_list|)
throw|;
block|}
block|}
name|setString
argument_list|(
name|qstr
argument_list|)
expr_stmt|;
block|}
return|return
name|super
operator|.
name|parse
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getSort
specifier|public
name|QueryParsing
operator|.
name|SortSpec
name|getSort
parameter_list|(
name|boolean
name|useGlobal
parameter_list|)
throws|throws
name|ParseException
block|{
name|QueryParsing
operator|.
name|SortSpec
name|sort
init|=
name|super
operator|.
name|getSort
argument_list|(
name|useGlobal
argument_list|)
decl_stmt|;
if|if
condition|(
name|sortStr
operator|!=
literal|null
operator|&&
name|sortStr
operator|.
name|length
argument_list|()
operator|>
literal|0
operator|&&
name|sort
operator|.
name|getSort
argument_list|()
operator|==
literal|null
condition|)
block|{
name|QueryParsing
operator|.
name|SortSpec
name|oldSort
init|=
name|QueryParsing
operator|.
name|parseSort
argument_list|(
name|sortStr
argument_list|,
name|getReq
argument_list|()
operator|.
name|getSchema
argument_list|()
argument_list|)
decl_stmt|;
name|sort
operator|.
name|sort
operator|=
name|oldSort
operator|.
name|sort
expr_stmt|;
block|}
return|return
name|sort
return|;
block|}
block|}
end_class

end_unit

