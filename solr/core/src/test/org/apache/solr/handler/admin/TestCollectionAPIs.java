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
name|io
operator|.
name|StringReader
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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|HashMap
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
name|solr
operator|.
name|SolrTestCaseJ4
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
name|cloud
operator|.
name|ZkNodeProps
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
name|CollectionParams
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
name|MultiMapSolrParams
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
name|Pair
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
name|Utils
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
name|common
operator|.
name|util
operator|.
name|CommandOperation
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
name|api
operator|.
name|Api
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
name|api
operator|.
name|ApiBag
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
name|client
operator|.
name|solrj
operator|.
name|SolrRequest
operator|.
name|METHOD
operator|.
name|DELETE
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
name|client
operator|.
name|solrj
operator|.
name|SolrRequest
operator|.
name|METHOD
operator|.
name|POST
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
name|cloud
operator|.
name|Overseer
operator|.
name|QUEUE_OPERATION
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
name|util
operator|.
name|Utils
operator|.
name|fromJSONString
import|;
end_import

begin_class
DECL|class|TestCollectionAPIs
specifier|public
class|class
name|TestCollectionAPIs
extends|extends
name|SolrTestCaseJ4
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
DECL|method|testCommands
specifier|public
name|void
name|testCommands
parameter_list|()
throws|throws
name|Exception
block|{
name|MockCollectionsHandler
name|collectionsHandler
init|=
operator|new
name|MockCollectionsHandler
argument_list|()
decl_stmt|;
name|ApiBag
name|apiBag
init|=
operator|new
name|ApiBag
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|Collection
argument_list|<
name|Api
argument_list|>
name|apis
init|=
name|collectionsHandler
operator|.
name|getApis
argument_list|()
decl_stmt|;
for|for
control|(
name|Api
name|api
range|:
name|apis
control|)
name|apiBag
operator|.
name|register
argument_list|(
name|api
argument_list|,
name|Collections
operator|.
name|EMPTY_MAP
argument_list|)
expr_stmt|;
comment|//test a simple create collection call
name|compareOutput
argument_list|(
name|apiBag
argument_list|,
literal|"/collections"
argument_list|,
name|POST
argument_list|,
literal|"{create:{name:'newcoll', config:'schemaless', numShards:2, replicationFactor:2 }}"
argument_list|,
literal|null
argument_list|,
literal|"{name:newcoll, fromApi:'true', replicationFactor:'2', collection.configName:schemaless, numShards:'2', stateFormat:'2', operation:create}"
argument_list|)
expr_stmt|;
comment|//test a create collection with custom properties
name|compareOutput
argument_list|(
name|apiBag
argument_list|,
literal|"/collections"
argument_list|,
name|POST
argument_list|,
literal|"{create:{name:'newcoll', config:'schemaless', numShards:2, replicationFactor:2, properties:{prop1:'prop1val', prop2: prop2val} }}"
argument_list|,
literal|null
argument_list|,
literal|"{name:newcoll, fromApi:'true', replicationFactor:'2', collection.configName:schemaless, numShards:'2', stateFormat:'2', operation:create, property.prop1:prop1val, property.prop2:prop2val}"
argument_list|)
expr_stmt|;
name|compareOutput
argument_list|(
name|apiBag
argument_list|,
literal|"/collections"
argument_list|,
name|POST
argument_list|,
literal|"{create-alias:{name: aliasName , collections:[c1,c2] }}"
argument_list|,
literal|null
argument_list|,
literal|"{operation : createalias, name: aliasName, collections:[c1,c2] }"
argument_list|)
expr_stmt|;
name|compareOutput
argument_list|(
name|apiBag
argument_list|,
literal|"/collections"
argument_list|,
name|POST
argument_list|,
literal|"{delete-alias:{ name: aliasName}}"
argument_list|,
literal|null
argument_list|,
literal|"{operation : deletealias, name: aliasName}"
argument_list|)
expr_stmt|;
name|compareOutput
argument_list|(
name|apiBag
argument_list|,
literal|"/collections/collName"
argument_list|,
name|POST
argument_list|,
literal|"{reload:{}}"
argument_list|,
literal|null
argument_list|,
literal|"{name:collName, operation :reload}"
argument_list|)
expr_stmt|;
name|compareOutput
argument_list|(
name|apiBag
argument_list|,
literal|"/collections/collName"
argument_list|,
name|DELETE
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|"{name:collName, operation :delete}"
argument_list|)
expr_stmt|;
name|compareOutput
argument_list|(
name|apiBag
argument_list|,
literal|"/collections/collName/shards/shard1"
argument_list|,
name|DELETE
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|"{collection:collName, shard: shard1 , operation :deleteshard }"
argument_list|)
expr_stmt|;
name|compareOutput
argument_list|(
name|apiBag
argument_list|,
literal|"/collections/collName/shards/shard1/replica1?deleteDataDir=true&onlyIfDown=true"
argument_list|,
name|DELETE
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|"{collection:collName, shard: shard1, replica :replica1 , deleteDataDir:'true', onlyIfDown: 'true', operation :deletereplica }"
argument_list|)
expr_stmt|;
name|compareOutput
argument_list|(
name|apiBag
argument_list|,
literal|"/collections/collName/shards"
argument_list|,
name|POST
argument_list|,
literal|"{split:{shard:shard1, ranges: '0-1f4,1f5-3e8,3e9-5dc', coreProperties : {prop1:prop1Val, prop2:prop2Val} }}"
argument_list|,
literal|null
argument_list|,
literal|"{collection: collName , shard : shard1, ranges :'0-1f4,1f5-3e8,3e9-5dc', operation : splitshard, property.prop1:prop1Val, property.prop2: prop2Val}"
argument_list|)
expr_stmt|;
name|compareOutput
argument_list|(
name|apiBag
argument_list|,
literal|"/collections/collName/shards"
argument_list|,
name|POST
argument_list|,
literal|"{add-replica:{shard: shard1, node: 'localhost_8978' , coreProperties : {prop1:prop1Val, prop2:prop2Val} }}"
argument_list|,
literal|null
argument_list|,
literal|"{collection: collName , shard : shard1, node :'localhost_8978', operation : addreplica, property.prop1:prop1Val, property.prop2: prop2Val}"
argument_list|)
expr_stmt|;
name|compareOutput
argument_list|(
name|apiBag
argument_list|,
literal|"/collections/collName/shards"
argument_list|,
name|POST
argument_list|,
literal|"{split:{ splitKey:id12345, coreProperties : {prop1:prop1Val, prop2:prop2Val} }}"
argument_list|,
literal|null
argument_list|,
literal|"{collection: collName , split.key : id12345 , operation : splitshard, property.prop1:prop1Val, property.prop2: prop2Val}"
argument_list|)
expr_stmt|;
name|compareOutput
argument_list|(
name|apiBag
argument_list|,
literal|"/collections/collName"
argument_list|,
name|POST
argument_list|,
literal|"{add-replica-property : {name:propA , value: VALA, shard: shard1, replica:replica1}}"
argument_list|,
literal|null
argument_list|,
literal|"{collection: collName, shard: shard1, replica : replica1 , property : propA , operation : addreplicaprop, property.value : 'VALA'}"
argument_list|)
expr_stmt|;
name|compareOutput
argument_list|(
name|apiBag
argument_list|,
literal|"/collections/collName"
argument_list|,
name|POST
argument_list|,
literal|"{delete-replica-property : {property: propA , shard: shard1, replica:replica1} }"
argument_list|,
literal|null
argument_list|,
literal|"{collection: collName, shard: shard1, replica : replica1 , property : propA , operation : deletereplicaprop}"
argument_list|)
expr_stmt|;
name|compareOutput
argument_list|(
name|apiBag
argument_list|,
literal|"/collections/collName"
argument_list|,
name|POST
argument_list|,
literal|"{modify : {rule : ['replica:*, cores:<5'], autoAddReplicas : false} }"
argument_list|,
literal|null
argument_list|,
literal|"{collection: collName, operation : modifycollection , autoAddReplicas : 'false', rule : [{replica: '*', cores : '<5' }]}"
argument_list|)
expr_stmt|;
name|compareOutput
argument_list|(
name|apiBag
argument_list|,
literal|"/cluster"
argument_list|,
name|POST
argument_list|,
literal|"{add-role : {role : overseer, node : 'localhost_8978'} }"
argument_list|,
literal|null
argument_list|,
literal|"{operation : addrole ,role : overseer, node : 'localhost_8978'}"
argument_list|)
expr_stmt|;
name|compareOutput
argument_list|(
name|apiBag
argument_list|,
literal|"/cluster"
argument_list|,
name|POST
argument_list|,
literal|"{remove-role : {role : overseer, node : 'localhost_8978'} }"
argument_list|,
literal|null
argument_list|,
literal|"{operation : removerole ,role : overseer, node : 'localhost_8978'}"
argument_list|)
expr_stmt|;
name|compareOutput
argument_list|(
name|apiBag
argument_list|,
literal|"/collections/coll1"
argument_list|,
name|POST
argument_list|,
literal|"{balance-shard-unique : {property: preferredLeader} }"
argument_list|,
literal|null
argument_list|,
literal|"{operation : balanceshardunique ,collection : coll1, property : preferredLeader}"
argument_list|)
expr_stmt|;
name|compareOutput
argument_list|(
name|apiBag
argument_list|,
literal|"/collections/coll1"
argument_list|,
name|POST
argument_list|,
literal|"{migrate-docs : {forwardTimeout: 1800, target: coll2, splitKey: 'a123!'} }"
argument_list|,
literal|null
argument_list|,
literal|"{operation : migrate ,collection : coll1, target.collection:coll2, forward.timeout:1800, split.key:'a123!'}"
argument_list|)
expr_stmt|;
block|}
DECL|method|compareOutput
specifier|static
name|ZkNodeProps
name|compareOutput
parameter_list|(
specifier|final
name|ApiBag
name|apiBag
parameter_list|,
specifier|final
name|String
name|path
parameter_list|,
specifier|final
name|SolrRequest
operator|.
name|METHOD
name|method
parameter_list|,
specifier|final
name|String
name|payload
parameter_list|,
specifier|final
name|CoreContainer
name|cc
parameter_list|,
name|String
name|expectedOutputMapJson
parameter_list|)
throws|throws
name|Exception
block|{
name|Pair
argument_list|<
name|SolrQueryRequest
argument_list|,
name|SolrQueryResponse
argument_list|>
name|ctx
init|=
name|makeCall
argument_list|(
name|apiBag
argument_list|,
name|path
argument_list|,
name|method
argument_list|,
name|payload
argument_list|,
name|cc
argument_list|)
decl_stmt|;
name|ZkNodeProps
name|output
init|=
operator|(
name|ZkNodeProps
operator|)
name|ctx
operator|.
name|second
argument_list|()
operator|.
name|getValues
argument_list|()
operator|.
name|get
argument_list|(
name|ZkNodeProps
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|Map
name|expected
init|=
operator|(
name|Map
operator|)
name|fromJSONString
argument_list|(
name|expectedOutputMapJson
argument_list|)
decl_stmt|;
name|assertMapEqual
argument_list|(
name|expected
argument_list|,
name|output
argument_list|)
expr_stmt|;
return|return
name|output
return|;
block|}
DECL|method|makeCall
specifier|public
specifier|static
name|Pair
argument_list|<
name|SolrQueryRequest
argument_list|,
name|SolrQueryResponse
argument_list|>
name|makeCall
parameter_list|(
specifier|final
name|ApiBag
name|apiBag
parameter_list|,
name|String
name|path
parameter_list|,
specifier|final
name|SolrRequest
operator|.
name|METHOD
name|method
parameter_list|,
specifier|final
name|String
name|payload
parameter_list|,
specifier|final
name|CoreContainer
name|cc
parameter_list|)
throws|throws
name|Exception
block|{
name|SolrParams
name|queryParams
init|=
operator|new
name|MultiMapSolrParams
argument_list|(
name|Collections
operator|.
name|EMPTY_MAP
argument_list|)
decl_stmt|;
if|if
condition|(
name|path
operator|.
name|indexOf
argument_list|(
literal|'?'
argument_list|)
operator|>
literal|0
condition|)
block|{
name|String
name|queryStr
init|=
name|path
operator|.
name|substring
argument_list|(
name|path
operator|.
name|indexOf
argument_list|(
literal|'?'
argument_list|)
operator|+
literal|1
argument_list|)
decl_stmt|;
name|path
operator|=
name|path
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|path
operator|.
name|indexOf
argument_list|(
literal|'?'
argument_list|)
argument_list|)
expr_stmt|;
name|queryParams
operator|=
name|SolrRequestParsers
operator|.
name|parseQueryString
argument_list|(
name|queryStr
argument_list|)
expr_stmt|;
block|}
specifier|final
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
name|Api
name|api
init|=
name|apiBag
operator|.
name|lookup
argument_list|(
name|path
argument_list|,
name|method
operator|.
name|toString
argument_list|()
argument_list|,
name|parts
argument_list|)
decl_stmt|;
if|if
condition|(
name|api
operator|==
literal|null
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"No handler at path :"
operator|+
name|path
argument_list|)
throw|;
name|SolrQueryResponse
name|rsp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
name|LocalSolrQueryRequest
name|req
init|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
literal|null
argument_list|,
name|queryParams
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|CommandOperation
argument_list|>
name|getCommands
parameter_list|(
name|boolean
name|validateInput
parameter_list|)
block|{
if|if
condition|(
name|payload
operator|==
literal|null
condition|)
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
return|return
name|ApiBag
operator|.
name|getCommandOperations
argument_list|(
operator|new
name|StringReader
argument_list|(
name|payload
argument_list|)
argument_list|,
name|api
operator|.
name|getCommandSchema
argument_list|()
argument_list|,
literal|true
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getPathTemplateValues
parameter_list|()
block|{
return|return
name|parts
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getHttpMethod
parameter_list|()
block|{
return|return
name|method
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
decl_stmt|;
try|try
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
catch|catch
parameter_list|(
name|ApiBag
operator|.
name|ExceptionWithErrObject
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|+
name|Utils
operator|.
name|toJSONString
argument_list|(
name|e
operator|.
name|getErrs
argument_list|()
argument_list|)
argument_list|,
name|e
argument_list|)
throw|;
block|}
return|return
operator|new
name|Pair
argument_list|<>
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
return|;
block|}
DECL|method|assertMapEqual
specifier|private
specifier|static
name|void
name|assertMapEqual
parameter_list|(
name|Map
name|expected
parameter_list|,
name|ZkNodeProps
name|actual
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|errorMessage
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
argument_list|,
name|expected
operator|.
name|size
argument_list|()
argument_list|,
name|actual
operator|.
name|getProperties
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Object
name|o
range|:
name|expected
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Map
operator|.
name|Entry
name|e
init|=
operator|(
name|Map
operator|.
name|Entry
operator|)
name|o
decl_stmt|;
name|Object
name|actualVal
init|=
name|actual
operator|.
name|get
argument_list|(
operator|(
name|String
operator|)
name|e
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|actualVal
operator|instanceof
name|String
index|[]
condition|)
block|{
name|actualVal
operator|=
name|Arrays
operator|.
name|asList
argument_list|(
operator|(
name|String
index|[]
operator|)
name|actualVal
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|errorMessage
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|actualVal
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|errorMessage
specifier|private
specifier|static
name|String
name|errorMessage
parameter_list|(
name|Map
name|expected
parameter_list|,
name|ZkNodeProps
name|actual
parameter_list|)
block|{
return|return
literal|"expected: "
operator|+
name|Utils
operator|.
name|toJSONString
argument_list|(
name|expected
argument_list|)
operator|+
literal|"\nactual: "
operator|+
name|Utils
operator|.
name|toJSONString
argument_list|(
name|actual
argument_list|)
return|;
block|}
DECL|class|MockCollectionsHandler
specifier|static
class|class
name|MockCollectionsHandler
extends|extends
name|CollectionsHandler
block|{
DECL|field|req
name|LocalSolrQueryRequest
name|req
decl_stmt|;
DECL|method|MockCollectionsHandler
name|MockCollectionsHandler
parameter_list|()
block|{     }
annotation|@
name|Override
DECL|method|invokeAction
name|void
name|invokeAction
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|,
name|CoreContainer
name|cores
parameter_list|,
name|CollectionParams
operator|.
name|CollectionAction
name|action
parameter_list|,
name|CollectionOperation
name|operation
parameter_list|)
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|result
init|=
name|operation
operator|.
name|execute
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|,
name|this
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
name|result
operator|.
name|put
argument_list|(
name|QUEUE_OPERATION
argument_list|,
name|operation
operator|.
name|action
operator|.
name|toLower
argument_list|()
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
name|ZkNodeProps
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
operator|new
name|ZkNodeProps
argument_list|(
name|result
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

