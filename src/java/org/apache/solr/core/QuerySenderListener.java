begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.core
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
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
name|search
operator|.
name|SolrIndexSearcher
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
name|search
operator|.
name|DocList
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
name|search
operator|.
name|DocIterator
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
name|request
operator|.
name|LocalSolrQueryRequest
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
name|SolrQueryResponse
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
comment|/**  * @version $Id$  */
end_comment

begin_class
DECL|class|QuerySenderListener
class|class
name|QuerySenderListener
extends|extends
name|AbstractSolrEventListener
block|{
DECL|method|QuerySenderListener
specifier|public
name|QuerySenderListener
parameter_list|(
name|SolrCore
name|core
parameter_list|)
block|{
name|super
argument_list|(
name|core
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|newSearcher
specifier|public
name|void
name|newSearcher
parameter_list|(
name|SolrIndexSearcher
name|newSearcher
parameter_list|,
name|SolrIndexSearcher
name|currentSearcher
parameter_list|)
block|{
specifier|final
name|SolrIndexSearcher
name|searcher
init|=
name|newSearcher
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"QuerySenderListener sending requests to "
operator|+
name|newSearcher
argument_list|)
expr_stmt|;
for|for
control|(
name|NamedList
name|nlst
range|:
operator|(
name|List
argument_list|<
name|NamedList
argument_list|>
operator|)
name|args
operator|.
name|get
argument_list|(
literal|"queries"
argument_list|)
control|)
block|{
try|try
block|{
comment|// bind the request to a particular searcher (the newSearcher)
name|LocalSolrQueryRequest
name|req
init|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|core
argument_list|,
name|nlst
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|SolrIndexSearcher
name|getSearcher
parameter_list|()
block|{
return|return
name|searcher
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{ }
block|}
decl_stmt|;
name|SolrQueryResponse
name|rsp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
name|core
operator|.
name|execute
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
comment|// Retrieve the Document instances (not just the ids) to warm
comment|// the OS disk cache, and any Solr document cache.  Only the top
comment|// level values in the NamedList are checked for DocLists.
name|NamedList
name|values
init|=
name|rsp
operator|.
name|getValues
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|values
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Object
name|o
init|=
name|values
operator|.
name|getVal
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|instanceof
name|DocList
condition|)
block|{
name|DocList
name|docs
init|=
operator|(
name|DocList
operator|)
name|o
decl_stmt|;
for|for
control|(
name|DocIterator
name|iter
init|=
name|docs
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|newSearcher
operator|.
name|doc
argument_list|(
name|iter
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// do nothing... we want to continue with the other requests.
comment|// the failure should have already been logged.
block|}
name|log
operator|.
name|info
argument_list|(
literal|"QuerySenderListener done."
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

