begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search.join
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|join
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
name|join
operator|.
name|ToParentBlockJoinQuery
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
name|ShardParams
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
name|handler
operator|.
name|component
operator|.
name|ResponseBuilder
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
name|handler
operator|.
name|component
operator|.
name|SearchComponent
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
name|handler
operator|.
name|component
operator|.
name|ShardRequest
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
name|handler
operator|.
name|component
operator|.
name|ShardResponse
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

begin_class
DECL|class|BlockJoinFacetComponentSupport
specifier|abstract
class|class
name|BlockJoinFacetComponentSupport
extends|extends
name|SearchComponent
block|{
DECL|field|CHILD_FACET_FIELD_PARAMETER
specifier|public
specifier|static
specifier|final
name|String
name|CHILD_FACET_FIELD_PARAMETER
init|=
literal|"child.facet.field"
decl_stmt|;
DECL|field|NO_TO_PARENT_BJQ_MESSAGE
specifier|public
specifier|static
specifier|final
name|String
name|NO_TO_PARENT_BJQ_MESSAGE
init|=
literal|"Block join faceting is allowed with ToParentBlockJoinQuery only"
decl_stmt|;
DECL|field|COLLECTOR_CONTEXT_PARAM
specifier|public
specifier|static
specifier|final
name|String
name|COLLECTOR_CONTEXT_PARAM
init|=
literal|"blockJoinFacetCollector"
decl_stmt|;
DECL|method|validateQuery
specifier|protected
name|void
name|validateQuery
parameter_list|(
name|Query
name|query
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|query
operator|instanceof
name|ToParentBlockJoinQuery
operator|)
condition|)
block|{
if|if
condition|(
name|query
operator|instanceof
name|BooleanQuery
condition|)
block|{
name|List
argument_list|<
name|BooleanClause
argument_list|>
name|clauses
init|=
operator|(
operator|(
name|BooleanQuery
operator|)
name|query
operator|)
operator|.
name|clauses
argument_list|()
decl_stmt|;
for|for
control|(
name|BooleanClause
name|clause
range|:
name|clauses
control|)
block|{
if|if
condition|(
name|clause
operator|.
name|getQuery
argument_list|()
operator|instanceof
name|ToParentBlockJoinQuery
condition|)
block|{
return|return;
block|}
block|}
block|}
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
name|NO_TO_PARENT_BJQ_MESSAGE
argument_list|)
throw|;
block|}
block|}
DECL|method|getChildFacetFields
specifier|static
name|String
index|[]
name|getChildFacetFields
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|)
block|{
return|return
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|getParams
argument_list|(
name|CHILD_FACET_FIELD_PARAMETER
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|process
specifier|public
name|void
name|process
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|getChildFacetFields
argument_list|(
name|rb
operator|.
name|req
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|BlockJoinFacetAccsHolder
name|blockJoinFacetCollector
init|=
operator|(
name|BlockJoinFacetAccsHolder
operator|)
name|rb
operator|.
name|req
operator|.
name|getContext
argument_list|()
operator|.
name|get
argument_list|(
name|COLLECTOR_CONTEXT_PARAM
argument_list|)
decl_stmt|;
assert|assert
name|blockJoinFacetCollector
operator|!=
literal|null
assert|;
name|NamedList
name|output
decl_stmt|;
if|if
condition|(
name|isShard
argument_list|(
name|rb
argument_list|)
condition|)
block|{
comment|// distributed search, put results into own cell in order not to clash with facet component
name|output
operator|=
name|getChildFacetFields
argument_list|(
name|rb
operator|.
name|rsp
operator|.
name|getValues
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// normal process, put results into standard response
name|output
operator|=
name|getFacetFieldsList
argument_list|(
name|rb
argument_list|)
expr_stmt|;
block|}
name|mergeFacets
argument_list|(
name|output
argument_list|,
name|blockJoinFacetCollector
operator|.
name|getFacets
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|isShard
specifier|private
name|boolean
name|isShard
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|)
block|{
return|return
literal|"true"
operator|.
name|equals
argument_list|(
name|rb
operator|.
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
name|ShardParams
operator|.
name|IS_SHARD
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getChildFacetFields
specifier|private
name|NamedList
name|getChildFacetFields
parameter_list|(
name|NamedList
name|responseValues
parameter_list|,
name|boolean
name|createIfAbsent
parameter_list|)
block|{
return|return
name|getNamedListFromList
argument_list|(
name|responseValues
argument_list|,
literal|"child_facet_fields"
argument_list|,
name|createIfAbsent
argument_list|)
return|;
block|}
DECL|method|mergeFacets
specifier|private
name|void
name|mergeFacets
parameter_list|(
name|NamedList
name|childFacetFields
parameter_list|,
name|NamedList
name|shardFacets
parameter_list|)
block|{
if|if
condition|(
name|shardFacets
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|NamedList
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|nextShardFacet
range|:
operator|(
name|Iterable
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|NamedList
argument_list|<
name|Integer
argument_list|>
argument_list|>
argument_list|>
operator|)
name|shardFacets
control|)
block|{
name|String
name|fieldName
init|=
name|nextShardFacet
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|NamedList
argument_list|<
name|Integer
argument_list|>
name|collectedFacet
init|=
operator|(
name|NamedList
argument_list|<
name|Integer
argument_list|>
operator|)
name|childFacetFields
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
name|NamedList
argument_list|<
name|Integer
argument_list|>
name|shardFacet
init|=
name|nextShardFacet
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|collectedFacet
operator|==
literal|null
condition|)
block|{
name|childFacetFields
operator|.
name|add
argument_list|(
name|fieldName
argument_list|,
name|shardFacet
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|mergeFacetValues
argument_list|(
name|collectedFacet
argument_list|,
name|shardFacet
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|mergeFacetValues
specifier|private
name|void
name|mergeFacetValues
parameter_list|(
name|NamedList
argument_list|<
name|Integer
argument_list|>
name|collectedFacetValue
parameter_list|,
name|NamedList
argument_list|<
name|Integer
argument_list|>
name|shardFacetValue
parameter_list|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|nextShardValue
range|:
name|shardFacetValue
control|)
block|{
name|String
name|facetValue
init|=
name|nextShardValue
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Integer
name|shardCount
init|=
name|nextShardValue
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|int
name|indexOfCollectedValue
init|=
name|collectedFacetValue
operator|.
name|indexOf
argument_list|(
name|facetValue
argument_list|,
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexOfCollectedValue
operator|==
operator|-
literal|1
condition|)
block|{
name|collectedFacetValue
operator|.
name|add
argument_list|(
name|facetValue
argument_list|,
name|shardCount
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|int
name|newCount
init|=
name|collectedFacetValue
operator|.
name|getVal
argument_list|(
name|indexOfCollectedValue
argument_list|)
operator|+
name|shardCount
decl_stmt|;
name|collectedFacetValue
operator|.
name|setVal
argument_list|(
name|indexOfCollectedValue
argument_list|,
name|newCount
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|getNamedListFromList
specifier|private
name|NamedList
name|getNamedListFromList
parameter_list|(
name|NamedList
name|parentList
parameter_list|,
name|String
name|name
parameter_list|,
name|boolean
name|createIfAbsent
parameter_list|)
block|{
name|NamedList
name|result
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|parentList
operator|!=
literal|null
condition|)
block|{
name|result
operator|=
operator|(
name|NamedList
operator|)
name|parentList
operator|.
name|get
argument_list|(
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|result
operator|==
literal|null
operator|&&
name|createIfAbsent
condition|)
block|{
name|result
operator|=
operator|new
name|NamedList
argument_list|()
expr_stmt|;
name|parentList
operator|.
name|add
argument_list|(
name|name
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|handleResponses
specifier|public
name|void
name|handleResponses
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|,
name|ShardRequest
name|sreq
parameter_list|)
block|{
name|NamedList
name|collectedChildFacetFields
init|=
name|getChildFacetFields
argument_list|(
name|rb
operator|.
name|rsp
operator|.
name|getValues
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|ShardResponse
argument_list|>
name|responses
init|=
name|sreq
operator|.
name|responses
decl_stmt|;
for|for
control|(
name|ShardResponse
name|shardResponse
range|:
name|responses
control|)
block|{
name|NamedList
name|shardChildFacetFields
init|=
name|getChildFacetFields
argument_list|(
name|shardResponse
operator|.
name|getSolrResponse
argument_list|()
operator|.
name|getResponse
argument_list|()
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|mergeFacets
argument_list|(
name|collectedChildFacetFields
argument_list|,
name|shardChildFacetFields
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|finishStage
specifier|public
name|void
name|finishStage
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|)
block|{
if|if
condition|(
name|rb
operator|.
name|stage
operator|!=
name|ResponseBuilder
operator|.
name|STAGE_GET_FIELDS
condition|)
return|return;
name|NamedList
name|childFacetFields
init|=
name|getChildFacetFields
argument_list|(
name|rb
operator|.
name|rsp
operator|.
name|getValues
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|NamedList
name|facetFields
init|=
name|getFacetFieldsList
argument_list|(
name|rb
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|NamedList
argument_list|>
name|childFacetField
range|:
operator|(
name|Iterable
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|NamedList
argument_list|>
argument_list|>
operator|)
name|childFacetFields
control|)
block|{
name|facetFields
operator|.
name|add
argument_list|(
name|childFacetField
operator|.
name|getKey
argument_list|()
argument_list|,
name|childFacetField
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|rb
operator|.
name|rsp
operator|.
name|getValues
argument_list|()
operator|.
name|remove
argument_list|(
literal|"child_facet_fields"
argument_list|)
expr_stmt|;
block|}
DECL|method|getFacetFieldsList
specifier|private
name|NamedList
name|getFacetFieldsList
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|)
block|{
name|NamedList
name|facetCounts
init|=
name|getNamedListFromList
argument_list|(
name|rb
operator|.
name|rsp
operator|.
name|getValues
argument_list|()
argument_list|,
literal|"facet_counts"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
return|return
name|getNamedListFromList
argument_list|(
name|facetCounts
argument_list|,
literal|"facet_fields"
argument_list|,
literal|true
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"BlockJoin facet component"
return|;
block|}
block|}
end_class

end_unit
