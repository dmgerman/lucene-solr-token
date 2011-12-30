begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.update
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
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
name|request
operator|.
name|SolrQueryRequest
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|CommitUpdateCommand
specifier|public
class|class
name|CommitUpdateCommand
extends|extends
name|UpdateCommand
block|{
DECL|field|optimize
specifier|public
name|boolean
name|optimize
decl_stmt|;
DECL|field|waitSearcher
specifier|public
name|boolean
name|waitSearcher
init|=
literal|true
decl_stmt|;
DECL|field|expungeDeletes
specifier|public
name|boolean
name|expungeDeletes
init|=
literal|false
decl_stmt|;
DECL|field|softCommit
specifier|public
name|boolean
name|softCommit
init|=
literal|false
decl_stmt|;
DECL|field|prepareCommit
specifier|public
name|boolean
name|prepareCommit
init|=
literal|false
decl_stmt|;
comment|/**    * During optimize, optimize down to<= this many segments.  Must be>= 1    *    * @see org.apache.lucene.index.IndexWriter#forceMerge(int)    */
DECL|field|maxOptimizeSegments
specifier|public
name|int
name|maxOptimizeSegments
init|=
literal|1
decl_stmt|;
DECL|method|CommitUpdateCommand
specifier|public
name|CommitUpdateCommand
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|boolean
name|optimize
parameter_list|)
block|{
name|super
argument_list|(
literal|"commit"
argument_list|,
name|req
argument_list|)
expr_stmt|;
name|this
operator|.
name|optimize
operator|=
name|optimize
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|prepareCommit
condition|?
literal|"prepareCommit"
else|:
operator|(
literal|"commit(optimize="
operator|+
name|optimize
operator|+
literal|",waitSearcher="
operator|+
name|waitSearcher
operator|+
literal|",expungeDeletes="
operator|+
name|expungeDeletes
operator|+
literal|",softCommit="
operator|+
name|softCommit
operator|+
literal|')'
operator|)
return|;
block|}
block|}
end_class

end_unit

