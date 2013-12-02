begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.morphlines.solr
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|morphlines
operator|.
name|solr
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|client
operator|.
name|HttpClient
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
name|impl
operator|.
name|ConcurrentUpdateSolrServer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * ConcurrentUpdateSolrServer that propagates exceptions up to the submitter of  * requests on blockUntilFinished()  */
end_comment

begin_class
DECL|class|SafeConcurrentUpdateSolrServer
specifier|final
class|class
name|SafeConcurrentUpdateSolrServer
extends|extends
name|ConcurrentUpdateSolrServer
block|{
DECL|field|currentException
specifier|private
name|Throwable
name|currentException
init|=
literal|null
decl_stmt|;
DECL|field|myLock
specifier|private
specifier|final
name|Object
name|myLock
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
DECL|field|LOGGER
specifier|private
specifier|static
specifier|final
name|Logger
name|LOGGER
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SafeConcurrentUpdateSolrServer
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|SafeConcurrentUpdateSolrServer
specifier|public
name|SafeConcurrentUpdateSolrServer
parameter_list|(
name|String
name|solrServerUrl
parameter_list|,
name|int
name|queueSize
parameter_list|,
name|int
name|threadCount
parameter_list|)
block|{
name|this
argument_list|(
name|solrServerUrl
argument_list|,
literal|null
argument_list|,
name|queueSize
argument_list|,
name|threadCount
argument_list|)
expr_stmt|;
block|}
DECL|method|SafeConcurrentUpdateSolrServer
specifier|public
name|SafeConcurrentUpdateSolrServer
parameter_list|(
name|String
name|solrServerUrl
parameter_list|,
name|HttpClient
name|client
parameter_list|,
name|int
name|queueSize
parameter_list|,
name|int
name|threadCount
parameter_list|)
block|{
name|super
argument_list|(
name|solrServerUrl
argument_list|,
name|client
argument_list|,
name|queueSize
argument_list|,
name|threadCount
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|handleError
specifier|public
name|void
name|handleError
parameter_list|(
name|Throwable
name|ex
parameter_list|)
block|{
assert|assert
name|ex
operator|!=
literal|null
assert|;
synchronized|synchronized
init|(
name|myLock
init|)
block|{
name|currentException
operator|=
name|ex
expr_stmt|;
block|}
name|LOGGER
operator|.
name|error
argument_list|(
literal|"handleError"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|blockUntilFinished
specifier|public
name|void
name|blockUntilFinished
parameter_list|()
block|{
name|super
operator|.
name|blockUntilFinished
argument_list|()
expr_stmt|;
synchronized|synchronized
init|(
name|myLock
init|)
block|{
if|if
condition|(
name|currentException
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|currentException
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|clearException
specifier|public
name|void
name|clearException
parameter_list|()
block|{
synchronized|synchronized
init|(
name|myLock
init|)
block|{
name|currentException
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

