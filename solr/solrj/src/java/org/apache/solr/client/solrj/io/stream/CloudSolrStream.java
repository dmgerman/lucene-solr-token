begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.client.solrj.io.stream
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
name|io
operator|.
name|stream
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
name|ArrayList
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
name|Comparator
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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
name|Map
operator|.
name|Entry
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
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
name|Callable
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
name|ExecutorService
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
name|Future
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
name|CloudSolrClient
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
name|io
operator|.
name|SolrClientCache
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
name|io
operator|.
name|Tuple
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
name|io
operator|.
name|comp
operator|.
name|ComparatorOrder
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
name|io
operator|.
name|comp
operator|.
name|MultipleFieldComparator
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
name|io
operator|.
name|comp
operator|.
name|FieldComparator
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
name|io
operator|.
name|comp
operator|.
name|StreamComparator
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
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|Expressible
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
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|StreamExpression
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
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|StreamExpressionNamedParameter
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
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|StreamExpressionParameter
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
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|StreamExpressionValue
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
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|StreamFactory
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
name|ClusterState
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
name|Replica
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
name|Slice
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
name|ZkCoreNodeProps
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
name|util
operator|.
name|ExecutorUtil
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
name|SolrjNamedThreadFactory
import|;
end_import

begin_comment
comment|/**  * Connects to Zookeeper to pick replicas from a specific collection to send the query to.  * Under the covers the SolrStream instances send the query to the replicas.  * SolrStreams are opened using a thread pool, but a single thread is used  * to iterate and merge Tuples from each SolrStream.  **/
end_comment

begin_class
DECL|class|CloudSolrStream
specifier|public
class|class
name|CloudSolrStream
extends|extends
name|TupleStream
implements|implements
name|Expressible
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1
decl_stmt|;
DECL|field|zkHost
specifier|protected
name|String
name|zkHost
decl_stmt|;
DECL|field|collection
specifier|protected
name|String
name|collection
decl_stmt|;
DECL|field|params
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
decl_stmt|;
DECL|field|fieldMappings
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|fieldMappings
decl_stmt|;
DECL|field|comp
specifier|protected
name|StreamComparator
name|comp
decl_stmt|;
DECL|field|zkConnectTimeout
specifier|private
name|int
name|zkConnectTimeout
init|=
literal|10000
decl_stmt|;
DECL|field|zkClientTimeout
specifier|private
name|int
name|zkClientTimeout
init|=
literal|10000
decl_stmt|;
DECL|field|numWorkers
specifier|private
name|int
name|numWorkers
decl_stmt|;
DECL|field|workerID
specifier|private
name|int
name|workerID
decl_stmt|;
DECL|field|trace
specifier|private
name|boolean
name|trace
decl_stmt|;
DECL|field|eofTuples
specifier|protected
specifier|transient
name|Map
argument_list|<
name|String
argument_list|,
name|Tuple
argument_list|>
name|eofTuples
decl_stmt|;
DECL|field|cache
specifier|protected
specifier|transient
name|SolrClientCache
name|cache
decl_stmt|;
DECL|field|cloudSolrClient
specifier|protected
specifier|transient
name|CloudSolrClient
name|cloudSolrClient
decl_stmt|;
DECL|field|solrStreams
specifier|protected
specifier|transient
name|List
argument_list|<
name|TupleStream
argument_list|>
name|solrStreams
decl_stmt|;
DECL|field|tuples
specifier|protected
specifier|transient
name|TreeSet
argument_list|<
name|TupleWrapper
argument_list|>
name|tuples
decl_stmt|;
DECL|field|streamContext
specifier|protected
specifier|transient
name|StreamContext
name|streamContext
decl_stmt|;
comment|// Used by parallel stream
DECL|method|CloudSolrStream
specifier|protected
name|CloudSolrStream
parameter_list|()
block|{        }
DECL|method|CloudSolrStream
specifier|public
name|CloudSolrStream
parameter_list|(
name|String
name|zkHost
parameter_list|,
name|String
name|collectionName
parameter_list|,
name|Map
name|params
parameter_list|)
throws|throws
name|IOException
block|{
name|init
argument_list|(
name|collectionName
argument_list|,
name|zkHost
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
DECL|method|CloudSolrStream
specifier|public
name|CloudSolrStream
parameter_list|(
name|StreamExpression
name|expression
parameter_list|,
name|StreamFactory
name|factory
parameter_list|)
throws|throws
name|IOException
block|{
comment|// grab all parameters out
name|String
name|collectionName
init|=
name|factory
operator|.
name|getValueOperand
argument_list|(
name|expression
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|StreamExpressionNamedParameter
argument_list|>
name|namedParams
init|=
name|factory
operator|.
name|getNamedOperands
argument_list|(
name|expression
argument_list|)
decl_stmt|;
name|StreamExpressionNamedParameter
name|aliasExpression
init|=
name|factory
operator|.
name|getNamedOperand
argument_list|(
name|expression
argument_list|,
literal|"aliases"
argument_list|)
decl_stmt|;
name|StreamExpressionNamedParameter
name|zkHostExpression
init|=
name|factory
operator|.
name|getNamedOperand
argument_list|(
name|expression
argument_list|,
literal|"zkHost"
argument_list|)
decl_stmt|;
comment|// Validate there are no unknown parameters - zkHost and alias are namedParameter so we don't need to count it twice
if|if
condition|(
name|expression
operator|.
name|getParameters
argument_list|()
operator|.
name|size
argument_list|()
operator|!=
literal|1
operator|+
name|namedParams
operator|.
name|size
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"invalid expression %s - unknown operands found"
argument_list|,
name|expression
argument_list|)
argument_list|)
throw|;
block|}
comment|// Collection Name
if|if
condition|(
literal|null
operator|==
name|collectionName
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"invalid expression %s - collectionName expected as first operand"
argument_list|,
name|expression
argument_list|)
argument_list|)
throw|;
block|}
comment|// Named parameters - passed directly to solr as solrparams
if|if
condition|(
literal|0
operator|==
name|namedParams
operator|.
name|size
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"invalid expression %s - at least one named parameter expected. eg. 'q=*:*'"
argument_list|,
name|expression
argument_list|)
argument_list|)
throw|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|StreamExpressionNamedParameter
name|namedParam
range|:
name|namedParams
control|)
block|{
if|if
condition|(
operator|!
name|namedParam
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"zkHost"
argument_list|)
operator|&&
operator|!
name|namedParam
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"aliases"
argument_list|)
condition|)
block|{
name|params
operator|.
name|put
argument_list|(
name|namedParam
operator|.
name|getName
argument_list|()
argument_list|,
name|namedParam
operator|.
name|getParameter
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Aliases, optional, if provided then need to split
if|if
condition|(
literal|null
operator|!=
name|aliasExpression
operator|&&
name|aliasExpression
operator|.
name|getParameter
argument_list|()
operator|instanceof
name|StreamExpressionValue
condition|)
block|{
name|fieldMappings
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|mapping
range|:
operator|(
operator|(
name|StreamExpressionValue
operator|)
name|aliasExpression
operator|.
name|getParameter
argument_list|()
operator|)
operator|.
name|getValue
argument_list|()
operator|.
name|split
argument_list|(
literal|","
argument_list|)
control|)
block|{
name|String
index|[]
name|parts
init|=
name|mapping
operator|.
name|trim
argument_list|()
operator|.
name|split
argument_list|(
literal|"="
argument_list|)
decl_stmt|;
if|if
condition|(
literal|2
operator|==
name|parts
operator|.
name|length
condition|)
block|{
name|fieldMappings
operator|.
name|put
argument_list|(
name|parts
index|[
literal|0
index|]
argument_list|,
name|parts
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"invalid expression %s - alias expected of the format origName=newName"
argument_list|,
name|expression
argument_list|)
argument_list|)
throw|;
block|}
block|}
block|}
comment|// zkHost, optional - if not provided then will look into factory list to get
name|String
name|zkHost
init|=
literal|null
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|zkHostExpression
condition|)
block|{
name|zkHost
operator|=
name|factory
operator|.
name|getCollectionZkHost
argument_list|(
name|collectionName
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|zkHostExpression
operator|.
name|getParameter
argument_list|()
operator|instanceof
name|StreamExpressionValue
condition|)
block|{
name|zkHost
operator|=
operator|(
operator|(
name|StreamExpressionValue
operator|)
name|zkHostExpression
operator|.
name|getParameter
argument_list|()
operator|)
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
literal|null
operator|==
name|zkHost
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"invalid expression %s - zkHost not found for collection '%s'"
argument_list|,
name|expression
argument_list|,
name|collectionName
argument_list|)
argument_list|)
throw|;
block|}
comment|// We've got all the required items
name|init
argument_list|(
name|collectionName
argument_list|,
name|zkHost
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toExpression
specifier|public
name|StreamExpressionParameter
name|toExpression
parameter_list|(
name|StreamFactory
name|factory
parameter_list|)
throws|throws
name|IOException
block|{
comment|// functionName(collectionName, param1, param2, ..., paramN, sort="comp", [aliases="field=alias,..."])
comment|// function name
name|StreamExpression
name|expression
init|=
operator|new
name|StreamExpression
argument_list|(
name|factory
operator|.
name|getFunctionName
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
comment|// collection
name|expression
operator|.
name|addParameter
argument_list|(
name|collection
argument_list|)
expr_stmt|;
comment|// parameters
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|param
range|:
name|params
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|expression
operator|.
name|addParameter
argument_list|(
operator|new
name|StreamExpressionNamedParameter
argument_list|(
name|param
operator|.
name|getKey
argument_list|()
argument_list|,
name|param
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// zkHost
name|expression
operator|.
name|addParameter
argument_list|(
operator|new
name|StreamExpressionNamedParameter
argument_list|(
literal|"zkHost"
argument_list|,
name|zkHost
argument_list|)
argument_list|)
expr_stmt|;
comment|// aliases
if|if
condition|(
literal|null
operator|!=
name|fieldMappings
operator|&&
literal|0
operator|!=
name|fieldMappings
operator|.
name|size
argument_list|()
condition|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|mapping
range|:
name|fieldMappings
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|sb
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|mapping
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"="
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|mapping
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|expression
operator|.
name|addParameter
argument_list|(
operator|new
name|StreamExpressionNamedParameter
argument_list|(
literal|"aliases"
argument_list|,
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|expression
return|;
block|}
DECL|method|init
specifier|private
name|void
name|init
parameter_list|(
name|String
name|collectionName
parameter_list|,
name|String
name|zkHost
parameter_list|,
name|Map
name|params
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|zkHost
operator|=
name|zkHost
expr_stmt|;
name|this
operator|.
name|collection
operator|=
name|collectionName
expr_stmt|;
name|this
operator|.
name|params
operator|=
name|params
expr_stmt|;
comment|// If the comparator is null then it was not explicitly set so we will create one using the sort parameter
comment|// of the query. While doing this we will also take into account any aliases such that if we are sorting on
comment|// fieldA but fieldA is aliased to alias.fieldA then the comparater will be against alias.fieldA.
if|if
condition|(
operator|!
name|params
operator|.
name|containsKey
argument_list|(
literal|"fl"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"fl param expected for a stream"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|params
operator|.
name|containsKey
argument_list|(
literal|"sort"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"sort param expected for a stream"
argument_list|)
throw|;
block|}
name|this
operator|.
name|comp
operator|=
name|parseComp
argument_list|(
operator|(
name|String
operator|)
name|params
operator|.
name|get
argument_list|(
literal|"sort"
argument_list|)
argument_list|,
operator|(
name|String
operator|)
name|params
operator|.
name|get
argument_list|(
literal|"fl"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|setFieldMappings
specifier|public
name|void
name|setFieldMappings
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|fieldMappings
parameter_list|)
block|{
name|this
operator|.
name|fieldMappings
operator|=
name|fieldMappings
expr_stmt|;
block|}
DECL|method|setTrace
specifier|public
name|void
name|setTrace
parameter_list|(
name|boolean
name|trace
parameter_list|)
block|{
name|this
operator|.
name|trace
operator|=
name|trace
expr_stmt|;
block|}
DECL|method|setStreamContext
specifier|public
name|void
name|setStreamContext
parameter_list|(
name|StreamContext
name|context
parameter_list|)
block|{
name|this
operator|.
name|numWorkers
operator|=
name|context
operator|.
name|numWorkers
expr_stmt|;
name|this
operator|.
name|workerID
operator|=
name|context
operator|.
name|workerID
expr_stmt|;
name|this
operator|.
name|cache
operator|=
name|context
operator|.
name|getSolrClientCache
argument_list|()
expr_stmt|;
name|this
operator|.
name|streamContext
operator|=
name|context
expr_stmt|;
block|}
comment|/**   * Opens the CloudSolrStream   *   ***/
DECL|method|open
specifier|public
name|void
name|open
parameter_list|()
throws|throws
name|IOException
block|{
name|this
operator|.
name|tuples
operator|=
operator|new
name|TreeSet
argument_list|()
expr_stmt|;
name|this
operator|.
name|solrStreams
operator|=
operator|new
name|ArrayList
argument_list|()
expr_stmt|;
name|this
operator|.
name|eofTuples
operator|=
name|Collections
operator|.
name|synchronizedMap
argument_list|(
operator|new
name|HashMap
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|cache
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|cloudSolrClient
operator|=
name|this
operator|.
name|cache
operator|.
name|getCloudSolrClient
argument_list|(
name|zkHost
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|cloudSolrClient
operator|=
operator|new
name|CloudSolrClient
argument_list|(
name|zkHost
argument_list|)
expr_stmt|;
name|this
operator|.
name|cloudSolrClient
operator|.
name|connect
argument_list|()
expr_stmt|;
block|}
name|constructStreams
argument_list|()
expr_stmt|;
name|openStreams
argument_list|()
expr_stmt|;
block|}
DECL|method|getEofTuples
specifier|public
name|Map
name|getEofTuples
parameter_list|()
block|{
return|return
name|this
operator|.
name|eofTuples
return|;
block|}
DECL|method|children
specifier|public
name|List
argument_list|<
name|TupleStream
argument_list|>
name|children
parameter_list|()
block|{
return|return
name|solrStreams
return|;
block|}
DECL|method|parseComp
specifier|private
name|StreamComparator
name|parseComp
parameter_list|(
name|String
name|sort
parameter_list|,
name|String
name|fl
parameter_list|)
throws|throws
name|IOException
block|{
name|String
index|[]
name|fls
init|=
name|fl
operator|.
name|split
argument_list|(
literal|","
argument_list|)
decl_stmt|;
name|HashSet
name|fieldSet
init|=
operator|new
name|HashSet
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|f
range|:
name|fls
control|)
block|{
name|fieldSet
operator|.
name|add
argument_list|(
name|f
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
comment|//Handle spaces in the field list.
block|}
name|String
index|[]
name|sorts
init|=
name|sort
operator|.
name|split
argument_list|(
literal|","
argument_list|)
decl_stmt|;
name|StreamComparator
index|[]
name|comps
init|=
operator|new
name|StreamComparator
index|[
name|sorts
operator|.
name|length
index|]
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
name|sorts
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|s
init|=
name|sorts
index|[
name|i
index|]
decl_stmt|;
name|String
index|[]
name|spec
init|=
name|s
operator|.
name|trim
argument_list|()
operator|.
name|split
argument_list|(
literal|"\\s+"
argument_list|)
decl_stmt|;
comment|//This should take into account spaces in the sort spec.
name|String
name|fieldName
init|=
name|spec
index|[
literal|0
index|]
operator|.
name|trim
argument_list|()
decl_stmt|;
name|String
name|order
init|=
name|spec
index|[
literal|1
index|]
operator|.
name|trim
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|fieldSet
operator|.
name|contains
argument_list|(
name|spec
index|[
literal|0
index|]
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Fields in the sort spec must be included in the field list:"
operator|+
name|spec
index|[
literal|0
index|]
argument_list|)
throw|;
block|}
comment|// if there's an alias for the field then use the alias
if|if
condition|(
literal|null
operator|!=
name|fieldMappings
operator|&&
name|fieldMappings
operator|.
name|containsKey
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|fieldName
operator|=
name|fieldMappings
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
block|}
name|comps
index|[
name|i
index|]
operator|=
operator|new
name|FieldComparator
argument_list|(
name|fieldName
argument_list|,
name|order
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"asc"
argument_list|)
condition|?
name|ComparatorOrder
operator|.
name|ASCENDING
else|:
name|ComparatorOrder
operator|.
name|DESCENDING
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|comps
operator|.
name|length
operator|>
literal|1
condition|)
block|{
return|return
operator|new
name|MultipleFieldComparator
argument_list|(
name|comps
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|comps
index|[
literal|0
index|]
return|;
block|}
block|}
DECL|method|constructStreams
specifier|protected
name|void
name|constructStreams
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|ZkStateReader
name|zkStateReader
init|=
name|cloudSolrClient
operator|.
name|getZkStateReader
argument_list|()
decl_stmt|;
name|ClusterState
name|clusterState
init|=
name|zkStateReader
operator|.
name|getClusterState
argument_list|()
decl_stmt|;
comment|//System.out.println("Connected to zk an got cluster state.");
name|Collection
argument_list|<
name|Slice
argument_list|>
name|slices
init|=
name|clusterState
operator|.
name|getActiveSlices
argument_list|(
name|this
operator|.
name|collection
argument_list|)
decl_stmt|;
if|if
condition|(
name|slices
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"Collection not found:"
operator|+
name|this
operator|.
name|collection
argument_list|)
throw|;
block|}
name|params
operator|.
name|put
argument_list|(
literal|"distrib"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
comment|// We are the aggregator.
for|for
control|(
name|Slice
name|slice
range|:
name|slices
control|)
block|{
name|Collection
argument_list|<
name|Replica
argument_list|>
name|replicas
init|=
name|slice
operator|.
name|getReplicas
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Replica
argument_list|>
name|shuffler
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|Replica
name|replica
range|:
name|replicas
control|)
block|{
name|shuffler
operator|.
name|add
argument_list|(
name|replica
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|shuffle
argument_list|(
name|shuffler
argument_list|,
operator|new
name|Random
argument_list|()
argument_list|)
expr_stmt|;
name|Replica
name|rep
init|=
name|shuffler
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|ZkCoreNodeProps
name|zkProps
init|=
operator|new
name|ZkCoreNodeProps
argument_list|(
name|rep
argument_list|)
decl_stmt|;
name|String
name|url
init|=
name|zkProps
operator|.
name|getCoreUrl
argument_list|()
decl_stmt|;
name|SolrStream
name|solrStream
init|=
operator|new
name|SolrStream
argument_list|(
name|url
argument_list|,
name|params
argument_list|)
decl_stmt|;
if|if
condition|(
name|streamContext
operator|!=
literal|null
condition|)
block|{
name|solrStream
operator|.
name|setStreamContext
argument_list|(
name|streamContext
argument_list|)
expr_stmt|;
block|}
name|solrStream
operator|.
name|setFieldMappings
argument_list|(
name|this
operator|.
name|fieldMappings
argument_list|)
expr_stmt|;
name|solrStreams
operator|.
name|add
argument_list|(
name|solrStream
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|openStreams
specifier|private
name|void
name|openStreams
parameter_list|()
throws|throws
name|IOException
block|{
name|ExecutorService
name|service
init|=
name|ExecutorUtil
operator|.
name|newMDCAwareCachedThreadPool
argument_list|(
operator|new
name|SolrjNamedThreadFactory
argument_list|(
literal|"CloudSolrStream"
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|List
argument_list|<
name|Future
argument_list|<
name|TupleWrapper
argument_list|>
argument_list|>
name|futures
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|TupleStream
name|solrStream
range|:
name|solrStreams
control|)
block|{
name|StreamOpener
name|so
init|=
operator|new
name|StreamOpener
argument_list|(
operator|(
name|SolrStream
operator|)
name|solrStream
argument_list|,
name|comp
argument_list|)
decl_stmt|;
name|Future
argument_list|<
name|TupleWrapper
argument_list|>
name|future
init|=
name|service
operator|.
name|submit
argument_list|(
name|so
argument_list|)
decl_stmt|;
name|futures
operator|.
name|add
argument_list|(
name|future
argument_list|)
expr_stmt|;
block|}
try|try
block|{
for|for
control|(
name|Future
argument_list|<
name|TupleWrapper
argument_list|>
name|f
range|:
name|futures
control|)
block|{
name|TupleWrapper
name|w
init|=
name|f
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|w
operator|!=
literal|null
condition|)
block|{
name|tuples
operator|.
name|add
argument_list|(
name|w
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
finally|finally
block|{
name|service
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    *  Closes the CloudSolrStream    **/
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|TupleStream
name|solrStream
range|:
name|solrStreams
control|)
block|{
name|solrStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|cache
operator|==
literal|null
condition|)
block|{
name|cloudSolrClient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** Return the stream sort - ie, the order in which records are returned */
DECL|method|getStreamSort
specifier|public
name|StreamComparator
name|getStreamSort
parameter_list|()
block|{
return|return
name|comp
return|;
block|}
DECL|method|read
specifier|public
name|Tuple
name|read
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|_read
argument_list|()
return|;
block|}
DECL|method|_read
specifier|protected
name|Tuple
name|_read
parameter_list|()
throws|throws
name|IOException
block|{
name|TupleWrapper
name|tw
init|=
name|tuples
operator|.
name|pollFirst
argument_list|()
decl_stmt|;
if|if
condition|(
name|tw
operator|!=
literal|null
condition|)
block|{
name|Tuple
name|t
init|=
name|tw
operator|.
name|getTuple
argument_list|()
decl_stmt|;
if|if
condition|(
name|trace
condition|)
block|{
name|t
operator|.
name|put
argument_list|(
literal|"_COLLECTION_"
argument_list|,
name|this
operator|.
name|collection
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|tw
operator|.
name|next
argument_list|()
condition|)
block|{
name|tuples
operator|.
name|add
argument_list|(
name|tw
argument_list|)
expr_stmt|;
block|}
return|return
name|t
return|;
block|}
else|else
block|{
name|Map
name|m
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
if|if
condition|(
name|trace
condition|)
block|{
name|m
operator|.
name|put
argument_list|(
literal|"_COLLECTION_"
argument_list|,
name|this
operator|.
name|collection
argument_list|)
expr_stmt|;
block|}
name|m
operator|.
name|put
argument_list|(
literal|"EOF"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
return|return
operator|new
name|Tuple
argument_list|(
name|m
argument_list|)
return|;
block|}
block|}
DECL|class|TupleWrapper
specifier|protected
class|class
name|TupleWrapper
implements|implements
name|Comparable
argument_list|<
name|TupleWrapper
argument_list|>
block|{
DECL|field|tuple
specifier|private
name|Tuple
name|tuple
decl_stmt|;
DECL|field|stream
specifier|private
name|SolrStream
name|stream
decl_stmt|;
DECL|field|comp
specifier|private
name|Comparator
name|comp
decl_stmt|;
DECL|method|TupleWrapper
specifier|public
name|TupleWrapper
parameter_list|(
name|SolrStream
name|stream
parameter_list|,
name|Comparator
name|comp
parameter_list|)
block|{
name|this
operator|.
name|stream
operator|=
name|stream
expr_stmt|;
name|this
operator|.
name|comp
operator|=
name|comp
expr_stmt|;
block|}
DECL|method|compareTo
specifier|public
name|int
name|compareTo
parameter_list|(
name|TupleWrapper
name|w
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|w
condition|)
block|{
return|return
literal|0
return|;
block|}
name|int
name|i
init|=
name|comp
operator|.
name|compare
argument_list|(
name|tuple
argument_list|,
name|w
operator|.
name|tuple
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|==
literal|0
condition|)
block|{
return|return
literal|1
return|;
block|}
else|else
block|{
return|return
name|i
return|;
block|}
block|}
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
return|return
name|this
operator|==
name|o
return|;
block|}
DECL|method|getTuple
specifier|public
name|Tuple
name|getTuple
parameter_list|()
block|{
return|return
name|tuple
return|;
block|}
DECL|method|next
specifier|public
name|boolean
name|next
parameter_list|()
throws|throws
name|IOException
block|{
name|this
operator|.
name|tuple
operator|=
name|stream
operator|.
name|read
argument_list|()
expr_stmt|;
if|if
condition|(
name|tuple
operator|.
name|EOF
condition|)
block|{
name|eofTuples
operator|.
name|put
argument_list|(
name|stream
operator|.
name|getBaseUrl
argument_list|()
argument_list|,
name|tuple
argument_list|)
expr_stmt|;
block|}
return|return
operator|!
name|tuple
operator|.
name|EOF
return|;
block|}
block|}
DECL|class|StreamOpener
specifier|protected
class|class
name|StreamOpener
implements|implements
name|Callable
argument_list|<
name|TupleWrapper
argument_list|>
block|{
DECL|field|stream
specifier|private
name|SolrStream
name|stream
decl_stmt|;
DECL|field|comp
specifier|private
name|Comparator
argument_list|<
name|Tuple
argument_list|>
name|comp
decl_stmt|;
DECL|method|StreamOpener
specifier|public
name|StreamOpener
parameter_list|(
name|SolrStream
name|stream
parameter_list|,
name|Comparator
argument_list|<
name|Tuple
argument_list|>
name|comp
parameter_list|)
block|{
name|this
operator|.
name|stream
operator|=
name|stream
expr_stmt|;
name|this
operator|.
name|comp
operator|=
name|comp
expr_stmt|;
block|}
DECL|method|call
specifier|public
name|TupleWrapper
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|stream
operator|.
name|open
argument_list|()
expr_stmt|;
name|TupleWrapper
name|wrapper
init|=
operator|new
name|TupleWrapper
argument_list|(
name|stream
argument_list|,
name|comp
argument_list|)
decl_stmt|;
if|if
condition|(
name|wrapper
operator|.
name|next
argument_list|()
condition|)
block|{
return|return
name|wrapper
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

