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
name|VERSION_FIELD
import|;
end_import

begin_comment
comment|/**  *  Iterates over a stream and fetches additional fields from a specified collection.  *  Fetches are done in batches.  *  *  Syntax:  *  *  fetch(collection, stream, on="a=b", fl="c,d,e", batchSize="50")  *  **/
end_comment

begin_class
DECL|class|FetchStream
specifier|public
class|class
name|FetchStream
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
DECL|field|stream
specifier|private
name|TupleStream
name|stream
decl_stmt|;
DECL|field|streamContext
specifier|private
name|StreamContext
name|streamContext
decl_stmt|;
DECL|field|tuples
specifier|private
name|Iterator
argument_list|<
name|Tuple
argument_list|>
name|tuples
decl_stmt|;
DECL|field|leftKey
specifier|private
name|String
name|leftKey
decl_stmt|;
DECL|field|rightKey
specifier|private
name|String
name|rightKey
decl_stmt|;
DECL|field|fieldList
specifier|private
name|String
name|fieldList
decl_stmt|;
DECL|field|fields
specifier|private
name|String
index|[]
name|fields
decl_stmt|;
DECL|field|collection
specifier|private
name|String
name|collection
decl_stmt|;
DECL|field|batchSize
specifier|private
name|int
name|batchSize
decl_stmt|;
DECL|field|appendVersion
specifier|private
name|boolean
name|appendVersion
init|=
literal|true
decl_stmt|;
DECL|field|appendKey
specifier|private
name|boolean
name|appendKey
init|=
literal|true
decl_stmt|;
DECL|method|FetchStream
specifier|public
name|FetchStream
parameter_list|(
name|String
name|zkHost
parameter_list|,
name|String
name|collection
parameter_list|,
name|TupleStream
name|tupleStream
parameter_list|,
name|String
name|on
parameter_list|,
name|String
name|fieldList
parameter_list|,
name|int
name|batchSize
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
name|tupleStream
argument_list|,
name|on
argument_list|,
name|fieldList
argument_list|,
name|batchSize
argument_list|)
expr_stmt|;
block|}
DECL|method|FetchStream
specifier|public
name|FetchStream
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
name|StreamExpression
argument_list|>
name|streamExpressions
init|=
name|factory
operator|.
name|getExpressionOperandsRepresentingTypes
argument_list|(
name|expression
argument_list|,
name|Expressible
operator|.
name|class
argument_list|,
name|TupleStream
operator|.
name|class
argument_list|)
decl_stmt|;
name|StreamExpressionNamedParameter
name|onParam
init|=
name|factory
operator|.
name|getNamedOperand
argument_list|(
name|expression
argument_list|,
literal|"on"
argument_list|)
decl_stmt|;
name|StreamExpressionNamedParameter
name|flParam
init|=
name|factory
operator|.
name|getNamedOperand
argument_list|(
name|expression
argument_list|,
literal|"fl"
argument_list|)
decl_stmt|;
name|StreamExpressionNamedParameter
name|batchSizeParam
init|=
name|factory
operator|.
name|getNamedOperand
argument_list|(
name|expression
argument_list|,
literal|"batchSize"
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
name|String
name|on
init|=
literal|null
decl_stmt|;
name|String
name|fl
init|=
literal|null
decl_stmt|;
name|int
name|batchSize
init|=
literal|50
decl_stmt|;
if|if
condition|(
name|onParam
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"on parameter cannot be null for the fetch expression"
argument_list|)
throw|;
block|}
else|else
block|{
name|on
operator|=
operator|(
operator|(
name|StreamExpressionValue
operator|)
name|onParam
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
name|flParam
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"fl parameter cannot be null for the fetch expression"
argument_list|)
throw|;
block|}
else|else
block|{
name|fl
operator|=
operator|(
operator|(
name|StreamExpressionValue
operator|)
name|flParam
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
name|batchSizeParam
operator|!=
literal|null
condition|)
block|{
name|batchSize
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
operator|(
operator|(
name|StreamExpressionValue
operator|)
name|batchSizeParam
operator|.
name|getParameter
argument_list|()
operator|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
literal|1
operator|!=
name|streamExpressions
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
literal|"Invalid expression %s - expecting a single stream but found %d"
argument_list|,
name|expression
argument_list|,
name|streamExpressions
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
name|TupleStream
name|stream
init|=
name|factory
operator|.
name|constructStream
argument_list|(
name|streamExpressions
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
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
name|init
argument_list|(
name|zkHost
argument_list|,
name|collectionName
argument_list|,
name|stream
argument_list|,
name|on
argument_list|,
name|fl
argument_list|,
name|batchSize
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
name|TupleStream
name|tupleStream
parameter_list|,
name|String
name|on
parameter_list|,
name|String
name|fieldList
parameter_list|,
name|int
name|batchSize
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
name|collection
expr_stmt|;
name|this
operator|.
name|stream
operator|=
name|tupleStream
expr_stmt|;
name|this
operator|.
name|batchSize
operator|=
name|batchSize
expr_stmt|;
name|this
operator|.
name|fields
operator|=
name|fieldList
operator|.
name|split
argument_list|(
literal|","
argument_list|)
expr_stmt|;
name|this
operator|.
name|fieldList
operator|=
name|fieldList
expr_stmt|;
if|if
condition|(
name|on
operator|.
name|indexOf
argument_list|(
literal|"="
argument_list|)
operator|>
operator|-
literal|1
condition|)
block|{
name|String
index|[]
name|leftright
init|=
name|on
operator|.
name|split
argument_list|(
literal|"="
argument_list|)
decl_stmt|;
name|leftKey
operator|=
name|leftright
index|[
literal|0
index|]
operator|.
name|trim
argument_list|()
expr_stmt|;
name|rightKey
operator|=
name|leftright
index|[
literal|1
index|]
operator|.
name|trim
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|leftKey
operator|=
name|rightKey
operator|=
name|on
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|fields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|fields
index|[
name|i
index|]
operator|=
name|fields
index|[
name|i
index|]
operator|.
name|trim
argument_list|()
expr_stmt|;
if|if
condition|(
name|fields
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
name|VERSION_FIELD
argument_list|)
condition|)
block|{
name|appendVersion
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
name|fields
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
name|rightKey
argument_list|)
condition|)
block|{
name|appendKey
operator|=
literal|false
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|toExpression
specifier|public
name|StreamExpression
name|toExpression
parameter_list|(
name|StreamFactory
name|factory
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|toExpression
argument_list|(
name|factory
argument_list|,
literal|true
argument_list|)
return|;
block|}
DECL|method|toExpression
specifier|private
name|StreamExpression
name|toExpression
parameter_list|(
name|StreamFactory
name|factory
parameter_list|,
name|boolean
name|includeStreams
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
name|expression
operator|.
name|addParameter
argument_list|(
name|collection
argument_list|)
expr_stmt|;
name|expression
operator|.
name|addParameter
argument_list|(
operator|new
name|StreamExpressionNamedParameter
argument_list|(
literal|"on"
argument_list|,
name|leftKey
operator|+
literal|"="
operator|+
name|rightKey
argument_list|)
argument_list|)
expr_stmt|;
name|expression
operator|.
name|addParameter
argument_list|(
operator|new
name|StreamExpressionNamedParameter
argument_list|(
literal|"fl"
argument_list|,
name|fieldList
argument_list|)
argument_list|)
expr_stmt|;
name|expression
operator|.
name|addParameter
argument_list|(
operator|new
name|StreamExpressionNamedParameter
argument_list|(
literal|"batchSize"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|batchSize
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// stream
if|if
condition|(
name|includeStreams
condition|)
block|{
if|if
condition|(
name|stream
operator|instanceof
name|Expressible
condition|)
block|{
name|expression
operator|.
name|addParameter
argument_list|(
operator|(
operator|(
name|Expressible
operator|)
name|stream
operator|)
operator|.
name|toExpression
argument_list|(
name|factory
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"The FetchStream contains a non-expressible TupleStream - it cannot be converted to an expression"
argument_list|)
throw|;
block|}
block|}
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
return|return
operator|new
name|StreamExplanation
argument_list|(
name|getStreamNodeId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|withChildren
argument_list|(
operator|new
name|Explanation
index|[]
block|{
name|stream
operator|.
name|toExplanation
argument_list|(
name|factory
argument_list|)
block|}
argument_list|)
operator|.
name|withFunctionName
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
operator|.
name|withImplementingClass
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|withExpressionType
argument_list|(
name|ExpressionType
operator|.
name|STREAM_DECORATOR
argument_list|)
operator|.
name|withExpression
argument_list|(
name|toExpression
argument_list|(
name|factory
argument_list|,
literal|false
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
DECL|method|setStreamContext
specifier|public
name|void
name|setStreamContext
parameter_list|(
name|StreamContext
name|streamContext
parameter_list|)
block|{
name|this
operator|.
name|streamContext
operator|=
name|streamContext
expr_stmt|;
name|this
operator|.
name|stream
operator|.
name|setStreamContext
argument_list|(
name|streamContext
argument_list|)
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
name|l
operator|.
name|add
argument_list|(
name|stream
argument_list|)
expr_stmt|;
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
name|tuples
operator|=
operator|new
name|ArrayList
argument_list|()
operator|.
name|iterator
argument_list|()
expr_stmt|;
name|stream
operator|.
name|open
argument_list|()
expr_stmt|;
block|}
DECL|method|fetchBatch
specifier|private
name|void
name|fetchBatch
parameter_list|()
throws|throws
name|IOException
block|{
name|Tuple
name|EOFTuple
init|=
literal|null
decl_stmt|;
name|List
argument_list|<
name|Tuple
argument_list|>
name|batch
init|=
operator|new
name|ArrayList
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
name|batchSize
condition|;
name|i
operator|++
control|)
block|{
name|Tuple
name|tuple
init|=
name|stream
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|tuple
operator|.
name|EOF
condition|)
block|{
name|EOFTuple
operator|=
name|tuple
expr_stmt|;
break|break;
block|}
else|else
block|{
name|batch
operator|.
name|add
argument_list|(
name|tuple
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|batch
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|rightKey
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|":("
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|batch
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
name|Tuple
name|tuple
init|=
name|batch
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|key
init|=
name|tuple
operator|.
name|getString
argument_list|(
name|leftKey
argument_list|)
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"q"
argument_list|,
name|buf
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"fl"
argument_list|,
name|fieldList
operator|+
name|appendFields
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"rows"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|batchSize
argument_list|)
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|SORT
argument_list|,
literal|"_version_ desc"
argument_list|)
expr_stmt|;
name|CloudSolrStream
name|cloudSolrStream
init|=
operator|new
name|CloudSolrStream
argument_list|(
name|zkHost
argument_list|,
name|collection
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|StreamContext
name|newContext
init|=
operator|new
name|StreamContext
argument_list|()
decl_stmt|;
name|newContext
operator|.
name|setSolrClientCache
argument_list|(
name|streamContext
operator|.
name|getSolrClientCache
argument_list|()
argument_list|)
expr_stmt|;
name|cloudSolrStream
operator|.
name|setStreamContext
argument_list|(
name|newContext
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Tuple
argument_list|>
name|fetched
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
try|try
block|{
name|cloudSolrStream
operator|.
name|open
argument_list|()
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|Tuple
name|t
init|=
name|cloudSolrStream
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|t
operator|.
name|EOF
condition|)
block|{
break|break;
block|}
else|else
block|{
name|String
name|rightValue
init|=
name|t
operator|.
name|getString
argument_list|(
name|rightKey
argument_list|)
decl_stmt|;
name|fetched
operator|.
name|put
argument_list|(
name|rightValue
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
name|cloudSolrStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|//Iterate the batch and add the fetched fields to the Tuples
for|for
control|(
name|Tuple
name|batchTuple
range|:
name|batch
control|)
block|{
name|Tuple
name|fetchedTuple
init|=
name|fetched
operator|.
name|get
argument_list|(
name|batchTuple
operator|.
name|getString
argument_list|(
name|leftKey
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|fetchedTuple
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|field
range|:
name|fields
control|)
block|{
name|Object
name|value
init|=
name|fetchedTuple
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
name|batchTuple
operator|.
name|put
argument_list|(
name|field
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
if|if
condition|(
name|EOFTuple
operator|!=
literal|null
condition|)
block|{
name|batch
operator|.
name|add
argument_list|(
name|EOFTuple
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|tuples
operator|=
name|batch
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
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
operator|!
name|tuples
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|fetchBatch
argument_list|()
expr_stmt|;
block|}
return|return
name|tuples
operator|.
name|next
argument_list|()
return|;
block|}
DECL|method|getStreamSort
specifier|public
name|StreamComparator
name|getStreamSort
parameter_list|()
block|{
return|return
name|stream
operator|.
name|getStreamSort
argument_list|()
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
DECL|method|appendFields
specifier|private
name|String
name|appendFields
parameter_list|()
block|{
name|StringBuffer
name|buf
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
if|if
condition|(
name|appendKey
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|rightKey
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|appendVersion
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|",_version_"
argument_list|)
expr_stmt|;
block|}
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

