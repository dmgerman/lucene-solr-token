begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.client.solrj.request
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
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
name|client
operator|.
name|solrj
operator|.
name|SolrClient
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
name|client
operator|.
name|solrj
operator|.
name|SolrRequest
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
name|client
operator|.
name|solrj
operator|.
name|response
operator|.
name|UpdateResponse
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
name|ModifiableSolrParams
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
name|UpdateParams
import|;
end_import

begin_comment
comment|/**  *  *  **/
end_comment

begin_class
DECL|class|AbstractUpdateRequest
specifier|public
specifier|abstract
class|class
name|AbstractUpdateRequest
extends|extends
name|SolrRequest
argument_list|<
name|UpdateResponse
argument_list|>
implements|implements
name|IsUpdateRequest
block|{
DECL|field|params
specifier|protected
name|ModifiableSolrParams
name|params
decl_stmt|;
DECL|field|commitWithin
specifier|protected
name|int
name|commitWithin
init|=
operator|-
literal|1
decl_stmt|;
DECL|enum|ACTION
specifier|public
enum|enum
name|ACTION
block|{
DECL|enum constant|COMMIT
name|COMMIT
block|,
DECL|enum constant|OPTIMIZE
name|OPTIMIZE
block|}
DECL|method|AbstractUpdateRequest
specifier|public
name|AbstractUpdateRequest
parameter_list|(
name|METHOD
name|m
parameter_list|,
name|String
name|path
parameter_list|)
block|{
name|super
argument_list|(
name|m
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
comment|/** Sets appropriate parameters for the given ACTION */
DECL|method|setAction
specifier|public
name|AbstractUpdateRequest
name|setAction
parameter_list|(
name|ACTION
name|action
parameter_list|,
name|boolean
name|waitFlush
parameter_list|,
name|boolean
name|waitSearcher
parameter_list|)
block|{
return|return
name|setAction
argument_list|(
name|action
argument_list|,
name|waitFlush
argument_list|,
name|waitSearcher
argument_list|,
literal|1
argument_list|)
return|;
block|}
DECL|method|setAction
specifier|public
name|AbstractUpdateRequest
name|setAction
parameter_list|(
name|ACTION
name|action
parameter_list|,
name|boolean
name|waitFlush
parameter_list|,
name|boolean
name|waitSearcher
parameter_list|,
name|boolean
name|softCommit
parameter_list|)
block|{
return|return
name|setAction
argument_list|(
name|action
argument_list|,
name|waitFlush
argument_list|,
name|waitSearcher
argument_list|,
name|softCommit
argument_list|,
literal|1
argument_list|)
return|;
block|}
DECL|method|setAction
specifier|public
name|AbstractUpdateRequest
name|setAction
parameter_list|(
name|ACTION
name|action
parameter_list|,
name|boolean
name|waitFlush
parameter_list|,
name|boolean
name|waitSearcher
parameter_list|,
name|int
name|maxSegments
parameter_list|)
block|{
return|return
name|setAction
argument_list|(
name|action
argument_list|,
name|waitFlush
argument_list|,
name|waitSearcher
argument_list|,
literal|false
argument_list|,
name|maxSegments
argument_list|)
return|;
block|}
DECL|method|setAction
specifier|public
name|AbstractUpdateRequest
name|setAction
parameter_list|(
name|ACTION
name|action
parameter_list|,
name|boolean
name|waitFlush
parameter_list|,
name|boolean
name|waitSearcher
parameter_list|,
name|boolean
name|softCommit
parameter_list|,
name|int
name|maxSegments
parameter_list|)
block|{
if|if
condition|(
name|params
operator|==
literal|null
condition|)
name|params
operator|=
operator|new
name|ModifiableSolrParams
argument_list|()
expr_stmt|;
if|if
condition|(
name|action
operator|==
name|ACTION
operator|.
name|OPTIMIZE
condition|)
block|{
name|params
operator|.
name|set
argument_list|(
name|UpdateParams
operator|.
name|OPTIMIZE
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|UpdateParams
operator|.
name|MAX_OPTIMIZE_SEGMENTS
argument_list|,
name|maxSegments
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|action
operator|==
name|ACTION
operator|.
name|COMMIT
condition|)
block|{
name|params
operator|.
name|set
argument_list|(
name|UpdateParams
operator|.
name|COMMIT
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|UpdateParams
operator|.
name|SOFT_COMMIT
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|softCommit
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|params
operator|.
name|set
argument_list|(
name|UpdateParams
operator|.
name|WAIT_SEARCHER
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|waitSearcher
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setAction
specifier|public
name|AbstractUpdateRequest
name|setAction
parameter_list|(
name|ACTION
name|action
parameter_list|,
name|boolean
name|waitFlush
parameter_list|,
name|boolean
name|waitSearcher
parameter_list|,
name|int
name|maxSegments
parameter_list|,
name|boolean
name|softCommit
parameter_list|,
name|boolean
name|expungeDeletes
parameter_list|)
block|{
name|setAction
argument_list|(
name|action
argument_list|,
name|waitFlush
argument_list|,
name|waitSearcher
argument_list|,
name|softCommit
argument_list|,
name|maxSegments
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|UpdateParams
operator|.
name|EXPUNGE_DELETES
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|expungeDeletes
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setAction
specifier|public
name|AbstractUpdateRequest
name|setAction
parameter_list|(
name|ACTION
name|action
parameter_list|,
name|boolean
name|waitFlush
parameter_list|,
name|boolean
name|waitSearcher
parameter_list|,
name|int
name|maxSegments
parameter_list|,
name|boolean
name|expungeDeletes
parameter_list|)
block|{
return|return
name|setAction
argument_list|(
name|action
argument_list|,
name|waitFlush
argument_list|,
name|waitSearcher
argument_list|,
name|maxSegments
argument_list|,
literal|false
argument_list|,
name|expungeDeletes
argument_list|)
return|;
block|}
DECL|method|setAction
specifier|public
name|AbstractUpdateRequest
name|setAction
parameter_list|(
name|ACTION
name|action
parameter_list|,
name|boolean
name|waitFlush
parameter_list|,
name|boolean
name|waitSearcher
parameter_list|,
name|int
name|maxSegments
parameter_list|,
name|boolean
name|softCommit
parameter_list|,
name|boolean
name|expungeDeletes
parameter_list|,
name|boolean
name|openSearcher
parameter_list|)
block|{
name|setAction
argument_list|(
name|action
argument_list|,
name|waitFlush
argument_list|,
name|waitSearcher
argument_list|,
name|maxSegments
argument_list|,
name|softCommit
argument_list|,
name|expungeDeletes
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|UpdateParams
operator|.
name|OPEN_SEARCHER
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|openSearcher
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * @since Solr 1.4    */
DECL|method|rollback
specifier|public
name|AbstractUpdateRequest
name|rollback
parameter_list|()
block|{
if|if
condition|(
name|params
operator|==
literal|null
condition|)
name|params
operator|=
operator|new
name|ModifiableSolrParams
argument_list|()
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|UpdateParams
operator|.
name|ROLLBACK
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setParam
specifier|public
name|void
name|setParam
parameter_list|(
name|String
name|param
parameter_list|,
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
name|params
operator|==
literal|null
condition|)
name|params
operator|=
operator|new
name|ModifiableSolrParams
argument_list|()
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|param
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
comment|/** Sets the parameters for this update request, overwriting any previous */
DECL|method|setParams
specifier|public
name|void
name|setParams
parameter_list|(
name|ModifiableSolrParams
name|params
parameter_list|)
block|{
name|this
operator|.
name|params
operator|=
name|params
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getParams
specifier|public
name|ModifiableSolrParams
name|getParams
parameter_list|()
block|{
return|return
name|params
return|;
block|}
annotation|@
name|Override
DECL|method|createResponse
specifier|protected
name|UpdateResponse
name|createResponse
parameter_list|(
name|SolrClient
name|client
parameter_list|)
block|{
return|return
operator|new
name|UpdateResponse
argument_list|()
return|;
block|}
DECL|method|isWaitSearcher
specifier|public
name|boolean
name|isWaitSearcher
parameter_list|()
block|{
return|return
name|params
operator|!=
literal|null
operator|&&
name|params
operator|.
name|getBool
argument_list|(
name|UpdateParams
operator|.
name|WAIT_SEARCHER
argument_list|,
literal|false
argument_list|)
return|;
block|}
DECL|method|getAction
specifier|public
name|ACTION
name|getAction
parameter_list|()
block|{
if|if
condition|(
name|params
operator|==
literal|null
condition|)
return|return
literal|null
return|;
if|if
condition|(
name|params
operator|.
name|getBool
argument_list|(
name|UpdateParams
operator|.
name|COMMIT
argument_list|,
literal|false
argument_list|)
condition|)
return|return
name|ACTION
operator|.
name|COMMIT
return|;
if|if
condition|(
name|params
operator|.
name|getBool
argument_list|(
name|UpdateParams
operator|.
name|OPTIMIZE
argument_list|,
literal|false
argument_list|)
condition|)
return|return
name|ACTION
operator|.
name|OPTIMIZE
return|;
return|return
literal|null
return|;
block|}
DECL|method|setWaitSearcher
specifier|public
name|void
name|setWaitSearcher
parameter_list|(
name|boolean
name|waitSearcher
parameter_list|)
block|{
name|setParam
argument_list|(
name|UpdateParams
operator|.
name|WAIT_SEARCHER
argument_list|,
name|waitSearcher
operator|+
literal|""
argument_list|)
expr_stmt|;
block|}
DECL|method|getCommitWithin
specifier|public
name|int
name|getCommitWithin
parameter_list|()
block|{
return|return
name|commitWithin
return|;
block|}
DECL|method|setCommitWithin
specifier|public
name|void
name|setCommitWithin
parameter_list|(
name|int
name|commitWithin
parameter_list|)
block|{
name|this
operator|.
name|commitWithin
operator|=
name|commitWithin
expr_stmt|;
block|}
block|}
end_class

end_unit

