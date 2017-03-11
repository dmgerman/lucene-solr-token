begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.api
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|api
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletRequest
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletResponse
import|;
end_import

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
name|LinkedHashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableSet
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
name|cloud
operator|.
name|DocCollection
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
name|cloud
operator|.
name|ZkStateReader
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
name|util
operator|.
name|ValidatingJsonMap
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
name|PluginBag
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
name|logging
operator|.
name|MDCLoggingContext
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
name|request
operator|.
name|SolrRequestHandler
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
name|QueryResponseWriter
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
name|security
operator|.
name|AuthorizationContext
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
name|servlet
operator|.
name|HttpSolrCall
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
name|servlet
operator|.
name|SolrDispatchFilter
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
name|servlet
operator|.
name|SolrRequestParsers
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
name|JsonSchemaValidator
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
name|PathTrie
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

begin_import
import|import static
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
operator|.
name|JSON
import|;
end_import

begin_import
import|import static
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
operator|.
name|WT
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|servlet
operator|.
name|SolrDispatchFilter
operator|.
name|Action
operator|.
name|ADMIN
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|servlet
operator|.
name|SolrDispatchFilter
operator|.
name|Action
operator|.
name|PASSTHROUGH
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|servlet
operator|.
name|SolrDispatchFilter
operator|.
name|Action
operator|.
name|PROCESS
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|PathTrie
operator|.
name|getPathSegments
import|;
end_import

begin_comment
comment|// class that handle the '/v2' path
end_comment

begin_class
DECL|class|V2HttpCall
specifier|public
class|class
name|V2HttpCall
extends|extends
name|HttpSolrCall
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
DECL|field|api
specifier|private
name|Api
name|api
decl_stmt|;
DECL|field|pieces
name|List
argument_list|<
name|String
argument_list|>
name|pieces
decl_stmt|;
DECL|field|prefix
specifier|private
name|String
name|prefix
decl_stmt|;
DECL|field|parts
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|parts
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|knownPrefixes
specifier|static
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|knownPrefixes
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"cluster"
argument_list|,
literal|"node"
argument_list|,
literal|"collections"
argument_list|,
literal|"cores"
argument_list|,
literal|"c"
argument_list|)
decl_stmt|;
DECL|method|V2HttpCall
specifier|public
name|V2HttpCall
parameter_list|(
name|SolrDispatchFilter
name|solrDispatchFilter
parameter_list|,
name|CoreContainer
name|cc
parameter_list|,
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|,
name|boolean
name|retry
parameter_list|)
block|{
name|super
argument_list|(
name|solrDispatchFilter
argument_list|,
name|cc
argument_list|,
name|request
argument_list|,
name|response
argument_list|,
name|retry
argument_list|)
expr_stmt|;
block|}
DECL|method|init
specifier|protected
name|void
name|init
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|path
init|=
name|this
operator|.
name|path
decl_stmt|;
name|String
name|fullPath
init|=
name|path
operator|=
name|path
operator|.
name|substring
argument_list|(
literal|7
argument_list|)
decl_stmt|;
comment|//strip off '/____v2'
try|try
block|{
name|pieces
operator|=
name|getPathSegments
argument_list|(
name|path
argument_list|)
expr_stmt|;
if|if
condition|(
name|pieces
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|prefix
operator|=
literal|"c"
expr_stmt|;
name|path
operator|=
literal|"/c"
expr_stmt|;
block|}
else|else
block|{
name|prefix
operator|=
name|pieces
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|boolean
name|isCompositeApi
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|knownPrefixes
operator|.
name|contains
argument_list|(
name|prefix
argument_list|)
condition|)
block|{
name|api
operator|=
name|getApiInfo
argument_list|(
name|cores
operator|.
name|getRequestHandlers
argument_list|()
argument_list|,
name|path
argument_list|,
name|req
operator|.
name|getMethod
argument_list|()
argument_list|,
name|fullPath
argument_list|,
name|parts
argument_list|)
expr_stmt|;
if|if
condition|(
name|api
operator|!=
literal|null
condition|)
block|{
name|isCompositeApi
operator|=
name|api
operator|instanceof
name|CompositeApi
expr_stmt|;
if|if
condition|(
operator|!
name|isCompositeApi
condition|)
block|{
name|initAdminRequest
argument_list|(
name|path
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
block|}
if|if
condition|(
literal|"c"
operator|.
name|equals
argument_list|(
name|prefix
argument_list|)
operator|||
literal|"collections"
operator|.
name|equals
argument_list|(
name|prefix
argument_list|)
condition|)
block|{
name|String
name|collectionName
init|=
name|origCorename
operator|=
name|corename
operator|=
name|pieces
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|DocCollection
name|collection
init|=
name|getDocCollection
argument_list|(
name|collectionName
argument_list|)
decl_stmt|;
if|if
condition|(
name|collection
operator|==
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|path
operator|.
name|endsWith
argument_list|(
name|ApiBag
operator|.
name|INTROSPECT
argument_list|)
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
literal|"no such collection or alias"
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|boolean
name|isPreferLeader
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|path
operator|.
name|endsWith
argument_list|(
literal|"/update"
argument_list|)
operator|||
name|path
operator|.
name|contains
argument_list|(
literal|"/update/"
argument_list|)
condition|)
block|{
name|isPreferLeader
operator|=
literal|true
expr_stmt|;
block|}
name|core
operator|=
name|getCoreByCollection
argument_list|(
name|collection
operator|.
name|getName
argument_list|()
argument_list|,
name|isPreferLeader
argument_list|)
expr_stmt|;
if|if
condition|(
name|core
operator|==
literal|null
condition|)
block|{
comment|//this collection exists , but this node does not have a replica for that collection
comment|//todo find a better way to compute remote
name|extractRemotePath
argument_list|(
name|corename
argument_list|,
name|origCorename
argument_list|,
literal|0
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
block|}
elseif|else
if|if
condition|(
literal|"cores"
operator|.
name|equals
argument_list|(
name|prefix
argument_list|)
condition|)
block|{
name|origCorename
operator|=
name|corename
operator|=
name|pieces
operator|.
name|get
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|core
operator|=
name|cores
operator|.
name|getCore
argument_list|(
name|corename
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|core
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|">> path: '"
operator|+
name|path
operator|+
literal|"'"
argument_list|)
expr_stmt|;
if|if
condition|(
name|path
operator|.
name|endsWith
argument_list|(
name|ApiBag
operator|.
name|INTROSPECT
argument_list|)
condition|)
block|{
name|initAdminRequest
argument_list|(
name|path
argument_list|)
expr_stmt|;
return|return;
block|}
else|else
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|NOT_FOUND
argument_list|,
literal|"no core retrieved for "
operator|+
name|corename
argument_list|)
throw|;
block|}
block|}
name|this
operator|.
name|path
operator|=
name|path
operator|=
name|path
operator|.
name|substring
argument_list|(
name|prefix
operator|.
name|length
argument_list|()
operator|+
name|pieces
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|length
argument_list|()
operator|+
literal|2
argument_list|)
expr_stmt|;
name|Api
name|apiInfo
init|=
name|getApiInfo
argument_list|(
name|core
operator|.
name|getRequestHandlers
argument_list|()
argument_list|,
name|path
argument_list|,
name|req
operator|.
name|getMethod
argument_list|()
argument_list|,
name|fullPath
argument_list|,
name|parts
argument_list|)
decl_stmt|;
if|if
condition|(
name|isCompositeApi
operator|&&
name|apiInfo
operator|instanceof
name|CompositeApi
condition|)
block|{
operator|(
operator|(
name|CompositeApi
operator|)
name|this
operator|.
name|api
operator|)
operator|.
name|add
argument_list|(
name|apiInfo
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|api
operator|=
name|apiInfo
operator|==
literal|null
condition|?
name|api
else|:
name|apiInfo
expr_stmt|;
block|}
name|MDCLoggingContext
operator|.
name|setCore
argument_list|(
name|core
argument_list|)
expr_stmt|;
name|parseRequest
argument_list|()
expr_stmt|;
if|if
condition|(
name|usingAliases
condition|)
block|{
name|processAliases
argument_list|(
name|aliases
argument_list|,
name|collectionsList
argument_list|)
expr_stmt|;
block|}
name|action
operator|=
name|PROCESS
expr_stmt|;
comment|// we are done with a valid handler
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|rte
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Error in init()"
argument_list|,
name|rte
argument_list|)
expr_stmt|;
throw|throw
name|rte
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|api
operator|==
literal|null
condition|)
name|action
operator|=
name|PASSTHROUGH
expr_stmt|;
if|if
condition|(
name|solrReq
operator|!=
literal|null
condition|)
name|solrReq
operator|.
name|getContext
argument_list|()
operator|.
name|put
argument_list|(
name|CommonParams
operator|.
name|PATH
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|initAdminRequest
specifier|private
name|void
name|initAdminRequest
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|Exception
block|{
name|solrReq
operator|=
name|SolrRequestParsers
operator|.
name|DEFAULT
operator|.
name|parse
argument_list|(
literal|null
argument_list|,
name|path
argument_list|,
name|req
argument_list|)
expr_stmt|;
name|solrReq
operator|.
name|getContext
argument_list|()
operator|.
name|put
argument_list|(
name|CoreContainer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|cores
argument_list|)
expr_stmt|;
name|requestType
operator|=
name|AuthorizationContext
operator|.
name|RequestType
operator|.
name|ADMIN
expr_stmt|;
name|action
operator|=
name|ADMIN
expr_stmt|;
block|}
DECL|method|parseRequest
specifier|protected
name|void
name|parseRequest
parameter_list|()
throws|throws
name|Exception
block|{
name|config
operator|=
name|core
operator|.
name|getSolrConfig
argument_list|()
expr_stmt|;
comment|// get or create/cache the parser for the core
name|SolrRequestParsers
name|parser
init|=
name|config
operator|.
name|getRequestParsers
argument_list|()
decl_stmt|;
comment|// With a valid handler and a valid core...
if|if
condition|(
name|solrReq
operator|==
literal|null
condition|)
name|solrReq
operator|=
name|parser
operator|.
name|parse
argument_list|(
name|core
argument_list|,
name|path
argument_list|,
name|req
argument_list|)
expr_stmt|;
block|}
DECL|method|getDocCollection
specifier|protected
name|DocCollection
name|getDocCollection
parameter_list|(
name|String
name|collectionName
parameter_list|)
block|{
if|if
condition|(
operator|!
name|cores
operator|.
name|isZooKeeperAware
argument_list|()
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
literal|"Solr not running in cloud mode "
argument_list|)
throw|;
block|}
name|ZkStateReader
name|zkStateReader
init|=
name|cores
operator|.
name|getZkController
argument_list|()
operator|.
name|getZkStateReader
argument_list|()
decl_stmt|;
name|DocCollection
name|collection
init|=
name|zkStateReader
operator|.
name|getClusterState
argument_list|()
operator|.
name|getCollectionOrNull
argument_list|(
name|collectionName
argument_list|)
decl_stmt|;
if|if
condition|(
name|collection
operator|==
literal|null
condition|)
block|{
name|collectionName
operator|=
name|corename
operator|=
name|lookupAliases
argument_list|(
name|collectionName
argument_list|)
expr_stmt|;
name|collection
operator|=
name|zkStateReader
operator|.
name|getClusterState
argument_list|()
operator|.
name|getCollectionOrNull
argument_list|(
name|collectionName
argument_list|)
expr_stmt|;
block|}
return|return
name|collection
return|;
block|}
DECL|method|getApiInfo
specifier|public
specifier|static
name|Api
name|getApiInfo
parameter_list|(
name|PluginBag
argument_list|<
name|SolrRequestHandler
argument_list|>
name|requestHandlers
parameter_list|,
name|String
name|path
parameter_list|,
name|String
name|method
parameter_list|,
name|String
name|fullPath
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|parts
parameter_list|)
block|{
name|fullPath
operator|=
name|fullPath
operator|==
literal|null
condition|?
name|path
else|:
name|fullPath
expr_stmt|;
name|Api
name|api
init|=
name|requestHandlers
operator|.
name|v2lookup
argument_list|(
name|path
argument_list|,
name|method
argument_list|,
name|parts
argument_list|)
decl_stmt|;
if|if
condition|(
name|api
operator|==
literal|null
operator|&&
name|path
operator|.
name|endsWith
argument_list|(
name|ApiBag
operator|.
name|INTROSPECT
argument_list|)
condition|)
block|{
comment|// the particular http method does not have any ,
comment|// just try if any other method has this path
name|api
operator|=
name|requestHandlers
operator|.
name|v2lookup
argument_list|(
name|path
argument_list|,
literal|null
argument_list|,
name|parts
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|api
operator|==
literal|null
condition|)
block|{
return|return
name|getSubPathApi
argument_list|(
name|requestHandlers
argument_list|,
name|path
argument_list|,
name|fullPath
argument_list|,
operator|new
name|CompositeApi
argument_list|(
literal|null
argument_list|)
argument_list|)
return|;
block|}
if|if
condition|(
name|api
operator|instanceof
name|ApiBag
operator|.
name|IntrospectApi
condition|)
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Api
argument_list|>
name|apis
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|m
range|:
name|SolrRequest
operator|.
name|SUPPORTED_METHODS
control|)
block|{
name|Api
name|x
init|=
name|requestHandlers
operator|.
name|v2lookup
argument_list|(
name|path
argument_list|,
name|m
argument_list|,
name|parts
argument_list|)
decl_stmt|;
if|if
condition|(
name|x
operator|!=
literal|null
condition|)
name|apis
operator|.
name|put
argument_list|(
name|m
argument_list|,
name|x
argument_list|)
expr_stmt|;
block|}
name|api
operator|=
operator|new
name|CompositeApi
argument_list|(
operator|new
name|Api
argument_list|(
name|ApiBag
operator|.
name|EMPTY_SPEC
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|call
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
block|{
name|String
name|method
init|=
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
literal|"method"
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|Api
argument_list|>
name|added
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Api
argument_list|>
name|e
range|:
name|apis
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|method
operator|==
literal|null
operator|||
name|e
operator|.
name|getKey
argument_list|()
operator|.
name|equals
argument_list|(
name|method
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|added
operator|.
name|contains
argument_list|(
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
condition|)
block|{
name|e
operator|.
name|getValue
argument_list|()
operator|.
name|call
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|added
operator|.
name|add
argument_list|(
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|getSubPathApi
argument_list|(
name|requestHandlers
argument_list|,
name|path
argument_list|,
name|fullPath
argument_list|,
operator|(
name|CompositeApi
operator|)
name|api
argument_list|)
expr_stmt|;
block|}
return|return
name|api
return|;
block|}
DECL|method|getSubPathApi
specifier|private
specifier|static
name|CompositeApi
name|getSubPathApi
parameter_list|(
name|PluginBag
argument_list|<
name|SolrRequestHandler
argument_list|>
name|requestHandlers
parameter_list|,
name|String
name|path
parameter_list|,
name|String
name|fullPath
parameter_list|,
name|CompositeApi
name|compositeApi
parameter_list|)
block|{
name|String
name|newPath
init|=
name|path
operator|.
name|endsWith
argument_list|(
name|ApiBag
operator|.
name|INTROSPECT
argument_list|)
condition|?
name|path
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|path
operator|.
name|length
argument_list|()
operator|-
name|ApiBag
operator|.
name|INTROSPECT
operator|.
name|length
argument_list|()
argument_list|)
else|:
name|path
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
name|subpaths
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|getSubPaths
argument_list|(
name|newPath
argument_list|,
name|requestHandlers
operator|.
name|getApiBag
argument_list|()
argument_list|,
name|subpaths
argument_list|)
expr_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|subPaths
init|=
name|subpaths
decl_stmt|;
if|if
condition|(
name|subPaths
operator|.
name|isEmpty
argument_list|()
condition|)
return|return
literal|null
return|;
return|return
name|compositeApi
operator|.
name|add
argument_list|(
operator|new
name|Api
argument_list|(
parameter_list|()
lambda|->
name|ValidatingJsonMap
operator|.
name|EMPTY
argument_list|)
block|{       @
name|Override
specifier|public
name|void
name|call
argument_list|(
name|SolrQueryRequest
name|req1
argument_list|,
name|SolrQueryResponse
name|rsp
argument_list|)
block|{
name|String
name|prefix
operator|=
literal|null
block|;
name|prefix
operator|=
name|fullPath
operator|.
name|endsWith
argument_list|(
name|ApiBag
operator|.
name|INTROSPECT
argument_list|)
condition|?
name|fullPath
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|fullPath
operator|.
name|length
argument_list|()
operator|-
name|ApiBag
operator|.
name|INTROSPECT
operator|.
name|length
argument_list|()
argument_list|)
else|:
name|fullPath
block|;
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|result
operator|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|(
name|subPaths
operator|.
name|size
argument_list|()
argument_list|)
block|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|e
range|:
name|subPaths
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|e
operator|.
name|getKey
argument_list|()
operator|.
name|endsWith
argument_list|(
name|ApiBag
operator|.
name|INTROSPECT
argument_list|)
condition|)
continue|continue;
name|result
operator|.
name|put
argument_list|(
name|prefix
operator|+
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Map
name|m
operator|=
operator|(
name|Map
operator|)
name|rsp
operator|.
name|getValues
argument_list|()
operator|.
name|get
argument_list|(
literal|"availableSubPaths"
argument_list|)
block|;
if|if
condition|(
name|m
operator|!=
literal|null
condition|)
block|{
name|m
operator|.
name|putAll
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|rsp
operator|.
name|add
argument_list|(
literal|"availableSubPaths"
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

begin_empty_stmt
unit|)
empty_stmt|;
end_empty_stmt

begin_function
unit|}    private
DECL|method|getSubPaths
specifier|static
name|void
name|getSubPaths
parameter_list|(
name|String
name|path
parameter_list|,
name|ApiBag
name|bag
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
name|pathsVsMethod
parameter_list|)
block|{
for|for
control|(
name|SolrRequest
operator|.
name|METHOD
name|m
range|:
name|SolrRequest
operator|.
name|METHOD
operator|.
name|values
argument_list|()
control|)
block|{
name|PathTrie
argument_list|<
name|Api
argument_list|>
name|registry
init|=
name|bag
operator|.
name|getRegistry
argument_list|(
name|m
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|registry
operator|!=
literal|null
condition|)
block|{
name|HashSet
argument_list|<
name|String
argument_list|>
name|subPaths
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|registry
operator|.
name|lookup
argument_list|(
name|path
argument_list|,
operator|new
name|HashMap
argument_list|<>
argument_list|()
argument_list|,
name|subPaths
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|subPath
range|:
name|subPaths
control|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|supportedMethods
init|=
name|pathsVsMethod
operator|.
name|get
argument_list|(
name|subPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|supportedMethods
operator|==
literal|null
condition|)
name|pathsVsMethod
operator|.
name|put
argument_list|(
name|subPath
argument_list|,
name|supportedMethods
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
name|supportedMethods
operator|.
name|add
argument_list|(
name|m
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_function

begin_class
DECL|class|CompositeApi
specifier|public
specifier|static
class|class
name|CompositeApi
extends|extends
name|Api
block|{
DECL|field|apis
specifier|private
name|LinkedList
argument_list|<
name|Api
argument_list|>
name|apis
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|CompositeApi
specifier|public
name|CompositeApi
parameter_list|(
name|Api
name|api
parameter_list|)
block|{
name|super
argument_list|(
name|ApiBag
operator|.
name|EMPTY_SPEC
argument_list|)
expr_stmt|;
if|if
condition|(
name|api
operator|!=
literal|null
condition|)
name|apis
operator|.
name|add
argument_list|(
name|api
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|call
specifier|public
name|void
name|call
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
block|{
for|for
control|(
name|Api
name|api
range|:
name|apis
control|)
block|{
name|api
operator|.
name|call
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|add
specifier|public
name|CompositeApi
name|add
parameter_list|(
name|Api
name|api
parameter_list|)
block|{
name|apis
operator|.
name|add
argument_list|(
name|api
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
end_class

begin_function
annotation|@
name|Override
DECL|method|handleAdmin
specifier|protected
name|void
name|handleAdmin
parameter_list|(
name|SolrQueryResponse
name|solrResp
parameter_list|)
block|{
name|api
operator|.
name|call
argument_list|(
name|this
operator|.
name|solrReq
argument_list|,
name|solrResp
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Override
DECL|method|execute
specifier|protected
name|void
name|execute
parameter_list|(
name|SolrQueryResponse
name|rsp
parameter_list|)
block|{
try|try
block|{
name|api
operator|.
name|call
argument_list|(
name|solrReq
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
throw|throw
name|e
throw|;
block|}
block|}
end_function

begin_function
annotation|@
name|Override
DECL|method|_getHandler
specifier|protected
name|Object
name|_getHandler
parameter_list|()
block|{
return|return
name|api
return|;
block|}
end_function

begin_function
DECL|method|getUrlParts
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getUrlParts
parameter_list|()
block|{
return|return
name|parts
return|;
block|}
end_function

begin_function
annotation|@
name|Override
DECL|method|getResponseWriter
specifier|protected
name|QueryResponseWriter
name|getResponseWriter
parameter_list|()
block|{
name|String
name|wt
init|=
name|solrReq
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
name|WT
argument_list|,
name|JSON
argument_list|)
decl_stmt|;
if|if
condition|(
name|core
operator|!=
literal|null
condition|)
return|return
name|core
operator|.
name|getResponseWriters
argument_list|()
operator|.
name|get
argument_list|(
name|wt
argument_list|)
return|;
return|return
name|SolrCore
operator|.
name|DEFAULT_RESPONSE_WRITERS
operator|.
name|get
argument_list|(
name|wt
argument_list|)
return|;
block|}
end_function

begin_function
annotation|@
name|Override
DECL|method|getSpec
specifier|protected
name|ValidatingJsonMap
name|getSpec
parameter_list|()
block|{
return|return
name|api
operator|==
literal|null
condition|?
literal|null
else|:
name|api
operator|.
name|getSpec
argument_list|()
return|;
block|}
end_function

begin_function
annotation|@
name|Override
DECL|method|getValidators
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|JsonSchemaValidator
argument_list|>
name|getValidators
parameter_list|()
block|{
return|return
name|api
operator|==
literal|null
condition|?
literal|null
else|:
name|api
operator|.
name|getCommandSchema
argument_list|()
return|;
block|}
end_function

unit|}
end_unit

