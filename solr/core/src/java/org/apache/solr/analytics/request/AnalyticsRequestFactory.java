begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.analytics.request
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analytics
operator|.
name|request
package|;
end_package

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
name|HashMap
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
name|Iterator
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
name|regex
operator|.
name|Matcher
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
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analytics
operator|.
name|request
operator|.
name|FieldFacetRequest
operator|.
name|FacetSortSpecification
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
name|analytics
operator|.
name|util
operator|.
name|AnalyticsParams
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
name|SolrException
operator|.
name|ErrorCode
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
name|FacetParams
operator|.
name|FacetRangeInclude
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
name|FacetParams
operator|.
name|FacetRangeOther
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
name|schema
operator|.
name|IndexSchema
import|;
end_import

begin_comment
comment|/**  * Parses the SolrParams to create a list of analytics requests.  */
end_comment

begin_class
DECL|class|AnalyticsRequestFactory
specifier|public
class|class
name|AnalyticsRequestFactory
implements|implements
name|AnalyticsParams
block|{
DECL|field|statPattern
specifier|public
specifier|static
specifier|final
name|Pattern
name|statPattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^o(?:lap)?\\.([^\\.]+)\\.(?:"
operator|+
name|EXPRESSION
operator|+
literal|")\\.([^\\.]+)$"
argument_list|,
name|Pattern
operator|.
name|CASE_INSENSITIVE
argument_list|)
decl_stmt|;
DECL|field|hiddenStatPattern
specifier|public
specifier|static
specifier|final
name|Pattern
name|hiddenStatPattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^o(?:lap)?\\.([^\\.]+)\\.(?:"
operator|+
name|HIDDEN_EXPRESSION
operator|+
literal|")\\.([^\\.]+)$"
argument_list|,
name|Pattern
operator|.
name|CASE_INSENSITIVE
argument_list|)
decl_stmt|;
DECL|field|fieldFacetPattern
specifier|public
specifier|static
specifier|final
name|Pattern
name|fieldFacetPattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^o(?:lap)?\\.([^\\.]+)\\.(?:"
operator|+
name|FIELD_FACET
operator|+
literal|")$"
argument_list|,
name|Pattern
operator|.
name|CASE_INSENSITIVE
argument_list|)
decl_stmt|;
DECL|field|fieldFacetParamPattern
specifier|public
specifier|static
specifier|final
name|Pattern
name|fieldFacetParamPattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^o(?:lap)?\\.([^\\.]+)\\.(?:"
operator|+
name|FIELD_FACET
operator|+
literal|")\\.([^\\.]+)\\.("
operator|+
name|LIMIT
operator|+
literal|"|"
operator|+
name|OFFSET
operator|+
literal|"|"
operator|+
name|HIDDEN
operator|+
literal|"|"
operator|+
name|SHOW_MISSING
operator|+
literal|"|"
operator|+
name|SORT_STATISTIC
operator|+
literal|"|"
operator|+
name|SORT_DIRECTION
operator|+
literal|")$"
argument_list|,
name|Pattern
operator|.
name|CASE_INSENSITIVE
argument_list|)
decl_stmt|;
DECL|field|rangeFacetPattern
specifier|public
specifier|static
specifier|final
name|Pattern
name|rangeFacetPattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^o(?:lap)?\\.([^\\.]+)\\.(?:"
operator|+
name|RANGE_FACET
operator|+
literal|")$"
argument_list|,
name|Pattern
operator|.
name|CASE_INSENSITIVE
argument_list|)
decl_stmt|;
DECL|field|rangeFacetParamPattern
specifier|public
specifier|static
specifier|final
name|Pattern
name|rangeFacetParamPattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^o(?:lap)?\\.([^\\.]+)\\.(?:"
operator|+
name|RANGE_FACET
operator|+
literal|")\\.([^\\.]+)\\.("
operator|+
name|START
operator|+
literal|"|"
operator|+
name|END
operator|+
literal|"|"
operator|+
name|GAP
operator|+
literal|"|"
operator|+
name|HARDEND
operator|+
literal|"|"
operator|+
name|INCLUDE_BOUNDARY
operator|+
literal|"|"
operator|+
name|OTHER_RANGE
operator|+
literal|")$"
argument_list|,
name|Pattern
operator|.
name|CASE_INSENSITIVE
argument_list|)
decl_stmt|;
DECL|field|queryFacetPattern
specifier|public
specifier|static
specifier|final
name|Pattern
name|queryFacetPattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^o(?:lap)?\\.([^\\.]+)\\.(?:"
operator|+
name|QUERY_FACET
operator|+
literal|")$"
argument_list|,
name|Pattern
operator|.
name|CASE_INSENSITIVE
argument_list|)
decl_stmt|;
DECL|field|queryFacetParamPattern
specifier|public
specifier|static
specifier|final
name|Pattern
name|queryFacetParamPattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^o(?:lap)?\\.([^\\.]+)\\.(?:"
operator|+
name|QUERY_FACET
operator|+
literal|")\\.([^\\.]+)\\.("
operator|+
name|QUERY
operator|+
literal|"|"
operator|+
name|DEPENDENCY
operator|+
literal|")$"
argument_list|,
name|Pattern
operator|.
name|CASE_INSENSITIVE
argument_list|)
decl_stmt|;
DECL|method|parse
specifier|public
specifier|static
name|List
argument_list|<
name|AnalyticsRequest
argument_list|>
name|parse
parameter_list|(
name|IndexSchema
name|schema
parameter_list|,
name|SolrParams
name|params
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|AnalyticsRequest
argument_list|>
name|requestMap
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|AnalyticsRequest
argument_list|>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|FieldFacetRequest
argument_list|>
argument_list|>
name|fieldFacetMap
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|FieldFacetRequest
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|fieldFacetSet
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|RangeFacetRequest
argument_list|>
argument_list|>
name|rangeFacetMap
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|RangeFacetRequest
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|rangeFacetSet
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|QueryFacetRequest
argument_list|>
argument_list|>
name|queryFacetMap
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|QueryFacetRequest
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|queryFacetSet
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|AnalyticsRequest
argument_list|>
name|requestList
init|=
operator|new
name|ArrayList
argument_list|<
name|AnalyticsRequest
argument_list|>
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|paramsIterator
init|=
name|params
operator|.
name|getParameterNamesIterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|paramsIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|param
init|=
name|paramsIterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|CharSequence
name|paramSequence
init|=
name|param
operator|.
name|subSequence
argument_list|(
literal|0
argument_list|,
name|param
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
comment|// Check if stat
name|Matcher
name|m
init|=
name|statPattern
operator|.
name|matcher
argument_list|(
name|paramSequence
argument_list|)
decl_stmt|;
if|if
condition|(
name|m
operator|.
name|matches
argument_list|()
condition|)
block|{
name|makeExpression
argument_list|(
name|requestMap
argument_list|,
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|,
name|m
operator|.
name|group
argument_list|(
literal|2
argument_list|)
argument_list|,
name|params
operator|.
name|get
argument_list|(
name|param
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Check if hidden stat
name|m
operator|=
name|hiddenStatPattern
operator|.
name|matcher
argument_list|(
name|paramSequence
argument_list|)
expr_stmt|;
if|if
condition|(
name|m
operator|.
name|matches
argument_list|()
condition|)
block|{
name|makeHiddenExpression
argument_list|(
name|requestMap
argument_list|,
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|,
name|m
operator|.
name|group
argument_list|(
literal|2
argument_list|)
argument_list|,
name|params
operator|.
name|get
argument_list|(
name|param
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Check if field facet
name|m
operator|=
name|fieldFacetPattern
operator|.
name|matcher
argument_list|(
name|paramSequence
argument_list|)
expr_stmt|;
if|if
condition|(
name|m
operator|.
name|matches
argument_list|()
condition|)
block|{
name|makeFieldFacet
argument_list|(
name|schema
argument_list|,
name|fieldFacetMap
argument_list|,
name|fieldFacetSet
argument_list|,
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|,
name|params
operator|.
name|getParams
argument_list|(
name|param
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Check if field facet parameter
name|m
operator|=
name|fieldFacetParamPattern
operator|.
name|matcher
argument_list|(
name|paramSequence
argument_list|)
expr_stmt|;
if|if
condition|(
name|m
operator|.
name|matches
argument_list|()
condition|)
block|{
name|setFieldFacetParam
argument_list|(
name|schema
argument_list|,
name|fieldFacetMap
argument_list|,
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|,
name|m
operator|.
name|group
argument_list|(
literal|2
argument_list|)
argument_list|,
name|m
operator|.
name|group
argument_list|(
literal|3
argument_list|)
argument_list|,
name|params
operator|.
name|getParams
argument_list|(
name|param
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Check if range facet
name|m
operator|=
name|rangeFacetPattern
operator|.
name|matcher
argument_list|(
name|paramSequence
argument_list|)
expr_stmt|;
if|if
condition|(
name|m
operator|.
name|matches
argument_list|()
condition|)
block|{
name|makeRangeFacet
argument_list|(
name|schema
argument_list|,
name|rangeFacetSet
argument_list|,
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|,
name|params
operator|.
name|getParams
argument_list|(
name|param
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Check if range facet parameter
name|m
operator|=
name|rangeFacetParamPattern
operator|.
name|matcher
argument_list|(
name|paramSequence
argument_list|)
expr_stmt|;
if|if
condition|(
name|m
operator|.
name|matches
argument_list|()
condition|)
block|{
name|setRangeFacetParam
argument_list|(
name|schema
argument_list|,
name|rangeFacetMap
argument_list|,
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|,
name|m
operator|.
name|group
argument_list|(
literal|2
argument_list|)
argument_list|,
name|m
operator|.
name|group
argument_list|(
literal|3
argument_list|)
argument_list|,
name|params
operator|.
name|getParams
argument_list|(
name|param
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Check if query facet
name|m
operator|=
name|queryFacetPattern
operator|.
name|matcher
argument_list|(
name|paramSequence
argument_list|)
expr_stmt|;
if|if
condition|(
name|m
operator|.
name|matches
argument_list|()
condition|)
block|{
name|makeQueryFacet
argument_list|(
name|schema
argument_list|,
name|queryFacetSet
argument_list|,
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|,
name|params
operator|.
name|getParams
argument_list|(
name|param
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Check if query
name|m
operator|=
name|queryFacetParamPattern
operator|.
name|matcher
argument_list|(
name|paramSequence
argument_list|)
expr_stmt|;
if|if
condition|(
name|m
operator|.
name|matches
argument_list|()
condition|)
block|{
name|setQueryFacetParam
argument_list|(
name|schema
argument_list|,
name|queryFacetMap
argument_list|,
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|,
name|m
operator|.
name|group
argument_list|(
literal|2
argument_list|)
argument_list|,
name|m
operator|.
name|group
argument_list|(
literal|3
argument_list|)
argument_list|,
name|params
operator|.
name|getParams
argument_list|(
name|param
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
block|}
block|}
block|}
for|for
control|(
name|String
name|reqName
range|:
name|requestMap
operator|.
name|keySet
argument_list|()
control|)
block|{
name|AnalyticsRequest
name|ar
init|=
name|requestMap
operator|.
name|get
argument_list|(
name|reqName
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|FieldFacetRequest
argument_list|>
name|ffrs
init|=
operator|new
name|ArrayList
argument_list|<
name|FieldFacetRequest
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|fieldFacetSet
operator|.
name|get
argument_list|(
name|reqName
argument_list|)
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|field
range|:
name|fieldFacetSet
operator|.
name|get
argument_list|(
name|reqName
argument_list|)
control|)
block|{
name|ffrs
operator|.
name|add
argument_list|(
name|fieldFacetMap
operator|.
name|get
argument_list|(
name|reqName
argument_list|)
operator|.
name|get
argument_list|(
name|field
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|ar
operator|.
name|setFieldFacets
argument_list|(
name|ffrs
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|RangeFacetRequest
argument_list|>
name|rfrs
init|=
operator|new
name|ArrayList
argument_list|<
name|RangeFacetRequest
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|rangeFacetSet
operator|.
name|get
argument_list|(
name|reqName
argument_list|)
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|field
range|:
name|rangeFacetSet
operator|.
name|get
argument_list|(
name|reqName
argument_list|)
control|)
block|{
name|RangeFacetRequest
name|rfr
init|=
name|rangeFacetMap
operator|.
name|get
argument_list|(
name|reqName
argument_list|)
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|rfr
operator|!=
literal|null
condition|)
block|{
name|rfrs
operator|.
name|add
argument_list|(
name|rfr
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|ar
operator|.
name|setRangeFacets
argument_list|(
name|rfrs
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|QueryFacetRequest
argument_list|>
name|qfrs
init|=
operator|new
name|ArrayList
argument_list|<
name|QueryFacetRequest
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|queryFacetSet
operator|.
name|get
argument_list|(
name|reqName
argument_list|)
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|name
range|:
name|queryFacetSet
operator|.
name|get
argument_list|(
name|reqName
argument_list|)
control|)
block|{
name|QueryFacetRequest
name|qfr
init|=
name|queryFacetMap
operator|.
name|get
argument_list|(
name|reqName
argument_list|)
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|qfr
operator|!=
literal|null
condition|)
block|{
name|addQueryFacet
argument_list|(
name|qfrs
argument_list|,
name|qfr
argument_list|)
expr_stmt|;
block|}
block|}
block|}
for|for
control|(
name|QueryFacetRequest
name|qfr
range|:
name|qfrs
control|)
block|{
if|if
condition|(
name|qfr
operator|.
name|getDependencies
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"The query facet dependencies "
operator|+
name|qfr
operator|.
name|getDependencies
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|" either do not exist or are defined in a dependency looop."
argument_list|)
throw|;
block|}
block|}
name|ar
operator|.
name|setQueryFacets
argument_list|(
name|qfrs
argument_list|)
expr_stmt|;
name|requestList
operator|.
name|add
argument_list|(
name|ar
argument_list|)
expr_stmt|;
block|}
return|return
name|requestList
return|;
block|}
DECL|method|makeFieldFacet
specifier|private
specifier|static
name|void
name|makeFieldFacet
parameter_list|(
name|IndexSchema
name|schema
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|FieldFacetRequest
argument_list|>
argument_list|>
name|fieldFacetMap
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|fieldFacetSet
parameter_list|,
name|String
name|requestName
parameter_list|,
name|String
index|[]
name|fields
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|FieldFacetRequest
argument_list|>
name|facetMap
init|=
name|fieldFacetMap
operator|.
name|get
argument_list|(
name|requestName
argument_list|)
decl_stmt|;
if|if
condition|(
name|facetMap
operator|==
literal|null
condition|)
block|{
name|facetMap
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|FieldFacetRequest
argument_list|>
argument_list|()
expr_stmt|;
name|fieldFacetMap
operator|.
name|put
argument_list|(
name|requestName
argument_list|,
name|facetMap
argument_list|)
expr_stmt|;
block|}
name|Set
argument_list|<
name|String
argument_list|>
name|set
init|=
name|fieldFacetSet
operator|.
name|get
argument_list|(
name|requestName
argument_list|)
decl_stmt|;
if|if
condition|(
name|set
operator|==
literal|null
condition|)
block|{
name|set
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|fieldFacetSet
operator|.
name|put
argument_list|(
name|requestName
argument_list|,
name|set
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|field
range|:
name|fields
control|)
block|{
if|if
condition|(
name|facetMap
operator|.
name|get
argument_list|(
name|field
argument_list|)
operator|==
literal|null
condition|)
block|{
name|facetMap
operator|.
name|put
argument_list|(
name|field
argument_list|,
operator|new
name|FieldFacetRequest
argument_list|(
name|schema
operator|.
name|getField
argument_list|(
name|field
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|set
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|setFieldFacetParam
specifier|private
specifier|static
name|void
name|setFieldFacetParam
parameter_list|(
name|IndexSchema
name|schema
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|FieldFacetRequest
argument_list|>
argument_list|>
name|fieldFacetMap
parameter_list|,
name|String
name|requestName
parameter_list|,
name|String
name|field
parameter_list|,
name|String
name|paramType
parameter_list|,
name|String
index|[]
name|params
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|FieldFacetRequest
argument_list|>
name|facetMap
init|=
name|fieldFacetMap
operator|.
name|get
argument_list|(
name|requestName
argument_list|)
decl_stmt|;
if|if
condition|(
name|facetMap
operator|==
literal|null
condition|)
block|{
name|facetMap
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|FieldFacetRequest
argument_list|>
argument_list|()
expr_stmt|;
name|fieldFacetMap
operator|.
name|put
argument_list|(
name|requestName
argument_list|,
name|facetMap
argument_list|)
expr_stmt|;
block|}
name|FieldFacetRequest
name|fr
init|=
name|facetMap
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|fr
operator|==
literal|null
condition|)
block|{
name|fr
operator|=
operator|new
name|FieldFacetRequest
argument_list|(
name|schema
operator|.
name|getField
argument_list|(
name|field
argument_list|)
argument_list|)
expr_stmt|;
name|facetMap
operator|.
name|put
argument_list|(
name|field
argument_list|,
name|fr
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|paramType
operator|.
name|equals
argument_list|(
literal|"limit"
argument_list|)
operator|||
name|paramType
operator|.
name|equals
argument_list|(
literal|"l"
argument_list|)
condition|)
block|{
name|fr
operator|.
name|setLimit
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|params
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|paramType
operator|.
name|equals
argument_list|(
literal|"offset"
argument_list|)
operator|||
name|paramType
operator|.
name|equals
argument_list|(
literal|"off"
argument_list|)
condition|)
block|{
name|fr
operator|.
name|setOffset
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|params
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|paramType
operator|.
name|equals
argument_list|(
literal|"hidden"
argument_list|)
operator|||
name|paramType
operator|.
name|equals
argument_list|(
literal|"h"
argument_list|)
condition|)
block|{
name|fr
operator|.
name|setHidden
argument_list|(
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|params
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|paramType
operator|.
name|equals
argument_list|(
literal|"showmissing"
argument_list|)
operator|||
name|paramType
operator|.
name|equals
argument_list|(
literal|"sm"
argument_list|)
condition|)
block|{
name|fr
operator|.
name|showMissing
argument_list|(
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|params
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|paramType
operator|.
name|equals
argument_list|(
literal|"sortstatistic"
argument_list|)
operator|||
name|paramType
operator|.
name|equals
argument_list|(
literal|"sortstat"
argument_list|)
operator|||
name|paramType
operator|.
name|equals
argument_list|(
literal|"ss"
argument_list|)
condition|)
block|{
name|fr
operator|.
name|setSort
argument_list|(
operator|new
name|FacetSortSpecification
argument_list|(
name|params
index|[
literal|0
index|]
argument_list|,
name|fr
operator|.
name|getDirection
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|paramType
operator|.
name|equals
argument_list|(
literal|"sortdirection"
argument_list|)
operator|||
name|paramType
operator|.
name|equals
argument_list|(
literal|"sd"
argument_list|)
condition|)
block|{
name|fr
operator|.
name|setDirection
argument_list|(
name|params
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|makeRangeFacet
specifier|private
specifier|static
name|void
name|makeRangeFacet
parameter_list|(
name|IndexSchema
name|schema
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|rangeFacetSet
parameter_list|,
name|String
name|requestName
parameter_list|,
name|String
index|[]
name|fields
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|set
init|=
name|rangeFacetSet
operator|.
name|get
argument_list|(
name|requestName
argument_list|)
decl_stmt|;
if|if
condition|(
name|set
operator|==
literal|null
condition|)
block|{
name|set
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|rangeFacetSet
operator|.
name|put
argument_list|(
name|requestName
argument_list|,
name|set
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|field
range|:
name|fields
control|)
block|{
name|set
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|setRangeFacetParam
specifier|private
specifier|static
name|void
name|setRangeFacetParam
parameter_list|(
name|IndexSchema
name|schema
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|RangeFacetRequest
argument_list|>
argument_list|>
name|rangeFacetMap
parameter_list|,
name|String
name|requestName
parameter_list|,
name|String
name|field
parameter_list|,
name|String
name|paramType
parameter_list|,
name|String
index|[]
name|params
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|RangeFacetRequest
argument_list|>
name|facetMap
init|=
name|rangeFacetMap
operator|.
name|get
argument_list|(
name|requestName
argument_list|)
decl_stmt|;
if|if
condition|(
name|facetMap
operator|==
literal|null
condition|)
block|{
name|facetMap
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|RangeFacetRequest
argument_list|>
argument_list|()
expr_stmt|;
name|rangeFacetMap
operator|.
name|put
argument_list|(
name|requestName
argument_list|,
name|facetMap
argument_list|)
expr_stmt|;
block|}
name|RangeFacetRequest
name|rr
init|=
name|facetMap
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|rr
operator|==
literal|null
condition|)
block|{
name|rr
operator|=
operator|new
name|RangeFacetRequest
argument_list|(
name|schema
operator|.
name|getField
argument_list|(
name|field
argument_list|)
argument_list|)
expr_stmt|;
name|facetMap
operator|.
name|put
argument_list|(
name|field
argument_list|,
name|rr
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|paramType
operator|.
name|equals
argument_list|(
literal|"start"
argument_list|)
operator|||
name|paramType
operator|.
name|equals
argument_list|(
literal|"st"
argument_list|)
condition|)
block|{
name|rr
operator|.
name|setStart
argument_list|(
name|params
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|paramType
operator|.
name|equals
argument_list|(
literal|"end"
argument_list|)
operator|||
name|paramType
operator|.
name|equals
argument_list|(
literal|"e"
argument_list|)
condition|)
block|{
name|rr
operator|.
name|setEnd
argument_list|(
name|params
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|paramType
operator|.
name|equals
argument_list|(
literal|"gap"
argument_list|)
operator|||
name|paramType
operator|.
name|equals
argument_list|(
literal|"g"
argument_list|)
condition|)
block|{
name|rr
operator|.
name|setGaps
argument_list|(
name|params
index|[
literal|0
index|]
operator|.
name|split
argument_list|(
literal|","
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|paramType
operator|.
name|equals
argument_list|(
literal|"hardend"
argument_list|)
operator|||
name|paramType
operator|.
name|equals
argument_list|(
literal|"he"
argument_list|)
condition|)
block|{
name|rr
operator|.
name|setHardEnd
argument_list|(
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|params
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|paramType
operator|.
name|equals
argument_list|(
literal|"includebound"
argument_list|)
operator|||
name|paramType
operator|.
name|equals
argument_list|(
literal|"ib"
argument_list|)
condition|)
block|{
for|for
control|(
name|String
name|param
range|:
name|params
control|)
block|{
name|rr
operator|.
name|addInclude
argument_list|(
name|FacetRangeInclude
operator|.
name|get
argument_list|(
name|param
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|paramType
operator|.
name|equals
argument_list|(
literal|"otherrange"
argument_list|)
operator|||
name|paramType
operator|.
name|equals
argument_list|(
literal|"or"
argument_list|)
condition|)
block|{
for|for
control|(
name|String
name|param
range|:
name|params
control|)
block|{
name|rr
operator|.
name|addOther
argument_list|(
name|FacetRangeOther
operator|.
name|get
argument_list|(
name|param
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|makeQueryFacet
specifier|private
specifier|static
name|void
name|makeQueryFacet
parameter_list|(
name|IndexSchema
name|schema
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|queryFacetSet
parameter_list|,
name|String
name|requestName
parameter_list|,
name|String
index|[]
name|names
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|set
init|=
name|queryFacetSet
operator|.
name|get
argument_list|(
name|requestName
argument_list|)
decl_stmt|;
if|if
condition|(
name|set
operator|==
literal|null
condition|)
block|{
name|set
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|queryFacetSet
operator|.
name|put
argument_list|(
name|requestName
argument_list|,
name|set
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|name
range|:
name|names
control|)
block|{
name|set
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|setQueryFacetParam
specifier|private
specifier|static
name|void
name|setQueryFacetParam
parameter_list|(
name|IndexSchema
name|schema
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|QueryFacetRequest
argument_list|>
argument_list|>
name|queryFacetMap
parameter_list|,
name|String
name|requestName
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|paramType
parameter_list|,
name|String
index|[]
name|params
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|QueryFacetRequest
argument_list|>
name|facetMap
init|=
name|queryFacetMap
operator|.
name|get
argument_list|(
name|requestName
argument_list|)
decl_stmt|;
if|if
condition|(
name|facetMap
operator|==
literal|null
condition|)
block|{
name|facetMap
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|QueryFacetRequest
argument_list|>
argument_list|()
expr_stmt|;
name|queryFacetMap
operator|.
name|put
argument_list|(
name|requestName
argument_list|,
name|facetMap
argument_list|)
expr_stmt|;
block|}
name|QueryFacetRequest
name|qr
init|=
name|facetMap
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|qr
operator|==
literal|null
condition|)
block|{
name|qr
operator|=
operator|new
name|QueryFacetRequest
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|facetMap
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|qr
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|paramType
operator|.
name|equals
argument_list|(
literal|"query"
argument_list|)
operator|||
name|paramType
operator|.
name|equals
argument_list|(
literal|"q"
argument_list|)
condition|)
block|{
for|for
control|(
name|String
name|query
range|:
name|params
control|)
block|{
name|qr
operator|.
name|addQuery
argument_list|(
name|query
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|paramType
operator|.
name|equals
argument_list|(
literal|"dependency"
argument_list|)
operator|||
name|paramType
operator|.
name|equals
argument_list|(
literal|"d"
argument_list|)
condition|)
block|{
for|for
control|(
name|String
name|depend
range|:
name|params
control|)
block|{
name|qr
operator|.
name|addDependency
argument_list|(
name|depend
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|makeHiddenExpression
specifier|private
specifier|static
name|void
name|makeHiddenExpression
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|AnalyticsRequest
argument_list|>
name|requestMap
parameter_list|,
name|String
name|requestName
parameter_list|,
name|String
name|expressionName
parameter_list|,
name|String
name|expression
parameter_list|)
block|{
name|AnalyticsRequest
name|req
init|=
name|requestMap
operator|.
name|get
argument_list|(
name|requestName
argument_list|)
decl_stmt|;
if|if
condition|(
name|req
operator|==
literal|null
condition|)
block|{
name|req
operator|=
operator|new
name|AnalyticsRequest
argument_list|(
name|requestName
argument_list|)
expr_stmt|;
name|requestMap
operator|.
name|put
argument_list|(
name|requestName
argument_list|,
name|req
argument_list|)
expr_stmt|;
block|}
name|req
operator|.
name|addHiddenExpression
argument_list|(
operator|new
name|ExpressionRequest
argument_list|(
name|expressionName
argument_list|,
name|expression
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|makeExpression
specifier|private
specifier|static
name|void
name|makeExpression
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|AnalyticsRequest
argument_list|>
name|requestMap
parameter_list|,
name|String
name|requestName
parameter_list|,
name|String
name|expressionName
parameter_list|,
name|String
name|expression
parameter_list|)
block|{
name|AnalyticsRequest
name|req
init|=
name|requestMap
operator|.
name|get
argument_list|(
name|requestName
argument_list|)
decl_stmt|;
if|if
condition|(
name|req
operator|==
literal|null
condition|)
block|{
name|req
operator|=
operator|new
name|AnalyticsRequest
argument_list|(
name|requestName
argument_list|)
expr_stmt|;
name|requestMap
operator|.
name|put
argument_list|(
name|requestName
argument_list|,
name|req
argument_list|)
expr_stmt|;
block|}
name|req
operator|.
name|addExpression
argument_list|(
operator|new
name|ExpressionRequest
argument_list|(
name|expressionName
argument_list|,
name|expression
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|addQueryFacet
specifier|private
specifier|static
name|void
name|addQueryFacet
parameter_list|(
name|List
argument_list|<
name|QueryFacetRequest
argument_list|>
name|currentList
parameter_list|,
name|QueryFacetRequest
name|queryFacet
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|depends
init|=
name|queryFacet
operator|.
name|getDependencies
argument_list|()
decl_stmt|;
name|int
name|place
init|=
literal|0
decl_stmt|;
for|for
control|(
name|QueryFacetRequest
name|qfr
range|:
name|currentList
control|)
block|{
if|if
condition|(
name|qfr
operator|.
name|getDependencies
argument_list|()
operator|.
name|remove
argument_list|(
name|queryFacet
argument_list|)
condition|)
block|{
break|break;
block|}
name|place
operator|++
expr_stmt|;
name|depends
operator|.
name|remove
argument_list|(
name|qfr
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|currentList
operator|.
name|add
argument_list|(
name|place
argument_list|,
name|queryFacet
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|count
init|=
name|place
operator|+
literal|1
init|;
name|count
operator|<
name|currentList
operator|.
name|size
argument_list|()
condition|;
name|count
operator|++
control|)
block|{
name|currentList
operator|.
name|get
argument_list|(
name|count
argument_list|)
operator|.
name|getDependencies
argument_list|()
operator|.
name|remove
argument_list|(
name|queryFacet
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

