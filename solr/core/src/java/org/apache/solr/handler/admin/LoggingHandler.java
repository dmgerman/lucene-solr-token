begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.admin
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|admin
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
import|;
end_import

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
name|Collections
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
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
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
name|SolrDocumentList
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
name|SimpleOrderedMap
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
name|core
operator|.
name|CoreContainer
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
name|core
operator|.
name|SolrCore
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
name|RequestHandlerBase
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
name|logging
operator|.
name|LogWatcher
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
name|logging
operator|.
name|LoggerInfo
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
name|util
operator|.
name|plugin
operator|.
name|SolrCoreAware
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
comment|/**  * A request handler to show which loggers are registered and allows you to set them  *  * @since 4.0  */
end_comment

begin_class
DECL|class|LoggingHandler
specifier|public
class|class
name|LoggingHandler
extends|extends
name|RequestHandlerBase
implements|implements
name|SolrCoreAware
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MethodHandles
operator|.
name|lookup
argument_list|()
operator|.
name|lookupClass
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|watcher
specifier|private
name|LogWatcher
name|watcher
decl_stmt|;
DECL|method|LoggingHandler
specifier|public
name|LoggingHandler
parameter_list|(
name|CoreContainer
name|cc
parameter_list|)
block|{
name|this
operator|.
name|watcher
operator|=
name|cc
operator|.
name|getLogging
argument_list|()
expr_stmt|;
block|}
DECL|method|LoggingHandler
specifier|public
name|LoggingHandler
parameter_list|()
block|{        }
annotation|@
name|Override
DECL|method|inform
specifier|public
name|void
name|inform
parameter_list|(
name|SolrCore
name|core
parameter_list|)
block|{
if|if
condition|(
name|watcher
operator|==
literal|null
condition|)
block|{
name|watcher
operator|=
name|core
operator|.
name|getCoreContainer
argument_list|()
operator|.
name|getLogging
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|handleRequestBody
specifier|public
name|void
name|handleRequestBody
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
throws|throws
name|Exception
block|{
comment|// Don't do anything if the framework is unknown
if|if
condition|(
name|watcher
operator|==
literal|null
condition|)
block|{
name|rsp
operator|.
name|add
argument_list|(
literal|"error"
argument_list|,
literal|"Logging Not Initialized"
argument_list|)
expr_stmt|;
return|return;
block|}
name|rsp
operator|.
name|add
argument_list|(
literal|"watcher"
argument_list|,
name|watcher
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
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
operator|.
name|get
argument_list|(
literal|"threshold"
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|watcher
operator|.
name|setThreshold
argument_list|(
name|params
operator|.
name|get
argument_list|(
literal|"threshold"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Write something at each level
if|if
condition|(
name|params
operator|.
name|get
argument_list|(
literal|"test"
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|log
operator|.
name|trace
argument_list|(
literal|"trace message"
argument_list|)
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"debug message"
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"info (with exception)"
argument_list|,
operator|new
name|RuntimeException
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|log
operator|.
name|warn
argument_list|(
literal|"warn (with exception)"
argument_list|,
operator|new
name|RuntimeException
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|log
operator|.
name|error
argument_list|(
literal|"error (with exception)"
argument_list|,
operator|new
name|RuntimeException
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|String
index|[]
name|set
init|=
name|params
operator|.
name|getParams
argument_list|(
literal|"set"
argument_list|)
decl_stmt|;
if|if
condition|(
name|set
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|pair
range|:
name|set
control|)
block|{
name|String
index|[]
name|split
init|=
name|pair
operator|.
name|split
argument_list|(
literal|":"
argument_list|)
decl_stmt|;
if|if
condition|(
name|split
operator|.
name|length
operator|!=
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
name|SERVER_ERROR
argument_list|,
literal|"Invalid format, expected level:value, got "
operator|+
name|pair
argument_list|)
throw|;
block|}
name|String
name|category
init|=
name|split
index|[
literal|0
index|]
decl_stmt|;
name|String
name|level
init|=
name|split
index|[
literal|1
index|]
decl_stmt|;
name|watcher
operator|.
name|setLogLevel
argument_list|(
name|category
argument_list|,
name|level
argument_list|)
expr_stmt|;
block|}
block|}
name|String
name|since
init|=
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
literal|"since"
argument_list|)
decl_stmt|;
if|if
condition|(
name|since
operator|!=
literal|null
condition|)
block|{
name|long
name|time
init|=
operator|-
literal|1
decl_stmt|;
try|try
block|{
name|time
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|since
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"invalid timestamp: "
operator|+
name|since
argument_list|)
throw|;
block|}
name|AtomicBoolean
name|found
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|SolrDocumentList
name|docs
init|=
name|watcher
operator|.
name|getHistory
argument_list|(
name|time
argument_list|,
name|found
argument_list|)
decl_stmt|;
if|if
condition|(
name|docs
operator|==
literal|null
condition|)
block|{
name|rsp
operator|.
name|add
argument_list|(
literal|"error"
argument_list|,
literal|"History not enabled"
argument_list|)
expr_stmt|;
return|return;
block|}
else|else
block|{
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|info
init|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|time
operator|>
literal|0
condition|)
block|{
name|info
operator|.
name|add
argument_list|(
literal|"since"
argument_list|,
name|time
argument_list|)
expr_stmt|;
name|info
operator|.
name|add
argument_list|(
literal|"found"
argument_list|,
name|found
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|info
operator|.
name|add
argument_list|(
literal|"levels"
argument_list|,
name|watcher
operator|.
name|getAllLevels
argument_list|()
argument_list|)
expr_stmt|;
comment|// show for the first request
block|}
name|info
operator|.
name|add
argument_list|(
literal|"last"
argument_list|,
name|watcher
operator|.
name|getLastEvent
argument_list|()
argument_list|)
expr_stmt|;
name|info
operator|.
name|add
argument_list|(
literal|"buffer"
argument_list|,
name|watcher
operator|.
name|getHistorySize
argument_list|()
argument_list|)
expr_stmt|;
name|info
operator|.
name|add
argument_list|(
literal|"threshold"
argument_list|,
name|watcher
operator|.
name|getThreshold
argument_list|()
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"info"
argument_list|,
name|info
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"history"
argument_list|,
name|docs
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|rsp
operator|.
name|add
argument_list|(
literal|"levels"
argument_list|,
name|watcher
operator|.
name|getAllLevels
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|LoggerInfo
argument_list|>
name|loggers
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|watcher
operator|.
name|getAllLoggers
argument_list|()
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|loggers
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|SimpleOrderedMap
argument_list|<
name|?
argument_list|>
argument_list|>
name|info
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|LoggerInfo
name|wrap
range|:
name|loggers
control|)
block|{
name|info
operator|.
name|add
argument_list|(
name|wrap
operator|.
name|getInfo
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|rsp
operator|.
name|add
argument_list|(
literal|"loggers"
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
name|rsp
operator|.
name|setHttpCaching
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
comment|// ////////////////////// SolrInfoMBeans methods //////////////////////
annotation|@
name|Override
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"Logging Handler"
return|;
block|}
annotation|@
name|Override
DECL|method|getCategory
specifier|public
name|Category
name|getCategory
parameter_list|()
block|{
return|return
name|Category
operator|.
name|ADMIN
return|;
block|}
block|}
end_class

end_unit

