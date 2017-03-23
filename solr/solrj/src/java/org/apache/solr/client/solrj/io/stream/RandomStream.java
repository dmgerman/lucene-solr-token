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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|stream
operator|.
name|Collectors
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
name|Explanation
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
name|Explanation
operator|.
name|ExpressionType
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
name|StreamExplanation
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
name|client
operator|.
name|solrj
operator|.
name|request
operator|.
name|QueryRequest
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
name|QueryResponse
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
name|SolrDocument
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
name|params
operator|.
name|ModifiableSolrParams
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
name|SORT
import|;
end_import

begin_comment
comment|/**  *  The RandomStream emits a stream of psuedo random Tuples that match the query parameters. Sample expression syntax:  *  random(collection, q="Hello word", rows="50", fl="title, body")  **/
end_comment

begin_class
DECL|class|RandomStream
specifier|public
class|class
name|RandomStream
extends|extends
name|TupleStream
implements|implements
name|Expressible
block|{
DECL|field|zkHost
specifier|private
name|String
name|zkHost
decl_stmt|;
DECL|field|props
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|props
decl_stmt|;
DECL|field|collection
specifier|private
name|String
name|collection
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
DECL|field|documentIterator
specifier|private
name|Iterator
argument_list|<
name|SolrDocument
argument_list|>
name|documentIterator
decl_stmt|;
DECL|method|RandomStream
specifier|public
name|RandomStream
parameter_list|(
name|String
name|zkHost
parameter_list|,
name|String
name|collection
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|props
parameter_list|)
throws|throws
name|IOException
block|{
name|init
argument_list|(
name|zkHost
argument_list|,
name|collection
argument_list|,
name|props
argument_list|)
expr_stmt|;
block|}
DECL|method|RandomStream
specifier|public
name|RandomStream
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
comment|// pull out known named params
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
literal|"buckets"
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
literal|"bucketSorts"
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
literal|"limit"
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
if|if
condition|(
name|zkHost
operator|==
literal|null
condition|)
block|{
name|zkHost
operator|=
name|factory
operator|.
name|getDefaultZkHost
argument_list|()
expr_stmt|;
block|}
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
name|zkHost
argument_list|,
name|collectionName
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
DECL|method|init
specifier|private
name|void
name|init
parameter_list|(
name|String
name|zkHost
parameter_list|,
name|String
name|collection
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|props
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
name|props
operator|=
name|props
expr_stmt|;
name|this
operator|.
name|collection
operator|=
name|collection
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
name|props
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
return|return
name|expression
return|;
block|}
annotation|@
name|Override
DECL|method|toExplanation
specifier|public
name|Explanation
name|toExplanation
parameter_list|(
name|StreamFactory
name|factory
parameter_list|)
throws|throws
name|IOException
block|{
name|StreamExplanation
name|explanation
init|=
operator|new
name|StreamExplanation
argument_list|(
name|getStreamNodeId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|explanation
operator|.
name|setFunctionName
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
expr_stmt|;
name|explanation
operator|.
name|setImplementingClass
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|explanation
operator|.
name|setExpressionType
argument_list|(
name|ExpressionType
operator|.
name|STREAM_SOURCE
argument_list|)
expr_stmt|;
name|explanation
operator|.
name|setExpression
argument_list|(
name|toExpression
argument_list|(
name|factory
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// child is a datastore so add it at this point
name|StreamExplanation
name|child
init|=
operator|new
name|StreamExplanation
argument_list|(
name|getStreamNodeId
argument_list|()
operator|+
literal|"-datastore"
argument_list|)
decl_stmt|;
name|child
operator|.
name|setFunctionName
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"solr (%s)"
argument_list|,
name|collection
argument_list|)
argument_list|)
expr_stmt|;
name|child
operator|.
name|setImplementingClass
argument_list|(
literal|"Solr/Lucene"
argument_list|)
expr_stmt|;
name|child
operator|.
name|setExpressionType
argument_list|(
name|ExpressionType
operator|.
name|DATASTORE
argument_list|)
expr_stmt|;
if|if
condition|(
literal|null
operator|!=
name|props
condition|)
block|{
name|child
operator|.
name|setExpression
argument_list|(
name|props
operator|.
name|entrySet
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|e
lambda|->
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"%s=%s"
argument_list|,
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
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|joining
argument_list|(
literal|","
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|explanation
operator|.
name|addChild
argument_list|(
name|child
argument_list|)
expr_stmt|;
return|return
name|explanation
return|;
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
name|cache
operator|=
name|context
operator|.
name|getSolrClientCache
argument_list|()
expr_stmt|;
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
name|List
argument_list|<
name|TupleStream
argument_list|>
name|l
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
return|return
name|l
return|;
block|}
DECL|method|open
specifier|public
name|void
name|open
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|cache
operator|!=
literal|null
condition|)
block|{
name|cloudSolrClient
operator|=
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
name|cloudSolrClient
operator|=
operator|new
name|CloudSolrClient
argument_list|(
name|zkHost
argument_list|)
expr_stmt|;
block|}
name|ModifiableSolrParams
name|params
init|=
name|getParams
argument_list|(
name|this
operator|.
name|props
argument_list|)
decl_stmt|;
name|params
operator|.
name|remove
argument_list|(
name|SORT
argument_list|)
expr_stmt|;
comment|//Override any sort.
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
name|int
name|seed
init|=
name|rand
operator|.
name|nextInt
argument_list|()
decl_stmt|;
name|String
name|sortField
init|=
literal|"random_"
operator|+
name|seed
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
name|SORT
argument_list|,
name|sortField
operator|+
literal|" asc"
argument_list|)
expr_stmt|;
name|QueryRequest
name|request
init|=
operator|new
name|QueryRequest
argument_list|(
name|params
argument_list|)
decl_stmt|;
try|try
block|{
name|QueryResponse
name|response
init|=
name|request
operator|.
name|process
argument_list|(
name|cloudSolrClient
argument_list|,
name|collection
argument_list|)
decl_stmt|;
name|SolrDocumentList
name|docs
init|=
name|response
operator|.
name|getResults
argument_list|()
decl_stmt|;
name|documentIterator
operator|=
name|docs
operator|.
name|iterator
argument_list|()
expr_stmt|;
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
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
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
DECL|method|read
specifier|public
name|Tuple
name|read
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|documentIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Map
name|map
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|SolrDocument
name|doc
init|=
name|documentIterator
operator|.
name|next
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|key
range|:
name|doc
operator|.
name|keySet
argument_list|()
control|)
block|{
name|map
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|doc
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|Tuple
argument_list|(
name|map
argument_list|)
return|;
block|}
else|else
block|{
name|Map
name|fields
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|fields
operator|.
name|put
argument_list|(
literal|"EOF"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Tuple
name|tuple
init|=
operator|new
name|Tuple
argument_list|(
name|fields
argument_list|)
decl_stmt|;
return|return
name|tuple
return|;
block|}
block|}
DECL|method|getParams
specifier|private
name|ModifiableSolrParams
name|getParams
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|props
parameter_list|)
block|{
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|key
range|:
name|props
operator|.
name|keySet
argument_list|()
control|)
block|{
name|String
name|value
init|=
name|props
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
return|return
name|params
return|;
block|}
DECL|method|getCost
specifier|public
name|int
name|getCost
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getStreamSort
specifier|public
name|StreamComparator
name|getStreamSort
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

