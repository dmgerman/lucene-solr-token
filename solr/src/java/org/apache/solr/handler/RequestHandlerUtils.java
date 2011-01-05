begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
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
name|HashMap
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
name|MapSolrParams
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
name|params
operator|.
name|UpdateParams
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
name|response
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
name|update
operator|.
name|CommitUpdateCommand
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
name|update
operator|.
name|RollbackUpdateCommand
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
name|update
operator|.
name|processor
operator|.
name|UpdateRequestProcessor
import|;
end_import

begin_comment
comment|/**  * Common helper functions for RequestHandlers  *   * @version $Id$  * @since solr 1.2  */
end_comment

begin_class
DECL|class|RequestHandlerUtils
specifier|public
class|class
name|RequestHandlerUtils
block|{
comment|/**    * A common way to mark the response format as experimental    */
DECL|method|addExperimentalFormatWarning
specifier|public
specifier|static
name|void
name|addExperimentalFormatWarning
parameter_list|(
name|SolrQueryResponse
name|rsp
parameter_list|)
block|{
name|rsp
operator|.
name|add
argument_list|(
literal|"WARNING"
argument_list|,
literal|"This response format is experimental.  It is likely to change in the future."
argument_list|)
expr_stmt|;
block|}
comment|/**    * Check the request parameters and decide if it should commit or optimize.    * If it does, it will check parameters for "waitFlush" and "waitSearcher"    *     * @deprecated Use {@link #handleCommit(UpdateRequestProcessor,SolrParams,boolean)}    *    * @since solr 1.2    */
annotation|@
name|Deprecated
DECL|method|handleCommit
specifier|public
specifier|static
name|boolean
name|handleCommit
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|,
name|boolean
name|force
parameter_list|)
throws|throws
name|IOException
block|{
name|SolrParams
name|params
init|=
name|req
operator|.
name|getParams
argument_list|()
decl_stmt|;
if|if
condition|(
name|params
operator|==
literal|null
condition|)
block|{
name|params
operator|=
operator|new
name|MapSolrParams
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|boolean
name|optimize
init|=
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
decl_stmt|;
name|boolean
name|commit
init|=
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
decl_stmt|;
if|if
condition|(
name|optimize
operator|||
name|commit
operator|||
name|force
condition|)
block|{
name|CommitUpdateCommand
name|cmd
init|=
operator|new
name|CommitUpdateCommand
argument_list|(
name|req
argument_list|,
name|optimize
argument_list|)
decl_stmt|;
name|cmd
operator|.
name|waitFlush
operator|=
name|params
operator|.
name|getBool
argument_list|(
name|UpdateParams
operator|.
name|WAIT_FLUSH
argument_list|,
name|cmd
operator|.
name|waitFlush
argument_list|)
expr_stmt|;
name|cmd
operator|.
name|waitSearcher
operator|=
name|params
operator|.
name|getBool
argument_list|(
name|UpdateParams
operator|.
name|WAIT_SEARCHER
argument_list|,
name|cmd
operator|.
name|waitSearcher
argument_list|)
expr_stmt|;
name|cmd
operator|.
name|expungeDeletes
operator|=
name|params
operator|.
name|getBool
argument_list|(
name|UpdateParams
operator|.
name|EXPUNGE_DELETES
argument_list|,
name|cmd
operator|.
name|expungeDeletes
argument_list|)
expr_stmt|;
name|cmd
operator|.
name|maxOptimizeSegments
operator|=
name|params
operator|.
name|getInt
argument_list|(
name|UpdateParams
operator|.
name|MAX_OPTIMIZE_SEGMENTS
argument_list|,
name|cmd
operator|.
name|maxOptimizeSegments
argument_list|)
expr_stmt|;
name|req
operator|.
name|getCore
argument_list|()
operator|.
name|getUpdateHandler
argument_list|()
operator|.
name|commit
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
comment|// Lets wait till after solr1.2 to define consistent output format
comment|//if( optimize ) {
comment|//  rsp.add( "optimize", true );
comment|//}
comment|//else {
comment|//  rsp.add( "commit", true );
comment|//}
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/**    * Check the request parameters and decide if it should commit or optimize.    * If it does, it will check parameters for "waitFlush" and "waitSearcher"    */
DECL|method|handleCommit
specifier|public
specifier|static
name|boolean
name|handleCommit
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|UpdateRequestProcessor
name|processor
parameter_list|,
name|SolrParams
name|params
parameter_list|,
name|boolean
name|force
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|params
operator|==
literal|null
condition|)
block|{
name|params
operator|=
operator|new
name|MapSolrParams
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|boolean
name|optimize
init|=
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
decl_stmt|;
name|boolean
name|commit
init|=
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
decl_stmt|;
if|if
condition|(
name|optimize
operator|||
name|commit
operator|||
name|force
condition|)
block|{
name|CommitUpdateCommand
name|cmd
init|=
operator|new
name|CommitUpdateCommand
argument_list|(
name|req
argument_list|,
name|optimize
argument_list|)
decl_stmt|;
name|cmd
operator|.
name|waitFlush
operator|=
name|params
operator|.
name|getBool
argument_list|(
name|UpdateParams
operator|.
name|WAIT_FLUSH
argument_list|,
name|cmd
operator|.
name|waitFlush
argument_list|)
expr_stmt|;
name|cmd
operator|.
name|waitSearcher
operator|=
name|params
operator|.
name|getBool
argument_list|(
name|UpdateParams
operator|.
name|WAIT_SEARCHER
argument_list|,
name|cmd
operator|.
name|waitSearcher
argument_list|)
expr_stmt|;
name|cmd
operator|.
name|expungeDeletes
operator|=
name|params
operator|.
name|getBool
argument_list|(
name|UpdateParams
operator|.
name|EXPUNGE_DELETES
argument_list|,
name|cmd
operator|.
name|expungeDeletes
argument_list|)
expr_stmt|;
name|cmd
operator|.
name|maxOptimizeSegments
operator|=
name|params
operator|.
name|getInt
argument_list|(
name|UpdateParams
operator|.
name|MAX_OPTIMIZE_SEGMENTS
argument_list|,
name|cmd
operator|.
name|maxOptimizeSegments
argument_list|)
expr_stmt|;
name|processor
operator|.
name|processCommit
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/**    * @since Solr 1.4    */
DECL|method|handleRollback
specifier|public
specifier|static
name|boolean
name|handleRollback
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|UpdateRequestProcessor
name|processor
parameter_list|,
name|SolrParams
name|params
parameter_list|,
name|boolean
name|force
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|params
operator|==
literal|null
condition|)
block|{
name|params
operator|=
operator|new
name|MapSolrParams
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|boolean
name|rollback
init|=
name|params
operator|.
name|getBool
argument_list|(
name|UpdateParams
operator|.
name|ROLLBACK
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|rollback
operator|||
name|force
condition|)
block|{
name|RollbackUpdateCommand
name|cmd
init|=
operator|new
name|RollbackUpdateCommand
argument_list|(
name|req
argument_list|)
decl_stmt|;
name|processor
operator|.
name|processRollback
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

