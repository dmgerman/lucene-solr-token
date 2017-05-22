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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
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
name|Map
import|;
end_import

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
name|methods
operator|.
name|CloseableHttpResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|impl
operator|.
name|client
operator|.
name|CloseableHttpClient
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
name|SolrClient
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
name|client
operator|.
name|solrj
operator|.
name|SolrServerException
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
name|HttpSolrClient
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
name|InputStreamResponseParser
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
name|ModifiableSolrParams
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
name|NamedList
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
comment|/** *  Queries a single Solr instance and maps SolrDocs to a Stream of Tuples. **/
end_comment

begin_class
DECL|class|SolrStream
specifier|public
class|class
name|SolrStream
extends|extends
name|TupleStream
block|{
DECL|field|logger
specifier|private
specifier|static
specifier|final
name|Logger
name|logger
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
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1
decl_stmt|;
DECL|field|baseUrl
specifier|private
name|String
name|baseUrl
decl_stmt|;
DECL|field|params
specifier|private
name|SolrParams
name|params
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
DECL|field|tupleStreamParser
specifier|private
specifier|transient
name|TupleStreamParser
name|tupleStreamParser
decl_stmt|;
DECL|field|client
specifier|private
specifier|transient
name|HttpSolrClient
name|client
decl_stmt|;
DECL|field|cache
specifier|private
specifier|transient
name|SolrClientCache
name|cache
decl_stmt|;
DECL|field|slice
specifier|private
name|String
name|slice
decl_stmt|;
DECL|field|checkpoint
specifier|private
name|long
name|checkpoint
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|closeableHttpResponse
specifier|private
name|CloseableHttpResponse
name|closeableHttpResponse
decl_stmt|;
comment|/**    * @param baseUrl Base URL of the stream.    * @param params  Map&lt;String, String&gt; of parameters    * @deprecated, use the form that thakes SolrParams. Existing code can use    * new ModifiableSolrParams(SolrParams.toMultiMap(new NamedList(params)))    * for existing calls that use Map&lt;String, String&gt;    */
annotation|@
name|Deprecated
DECL|method|SolrStream
specifier|public
name|SolrStream
parameter_list|(
name|String
name|baseUrl
parameter_list|,
name|Map
name|params
parameter_list|)
block|{
name|this
operator|.
name|baseUrl
operator|=
name|baseUrl
expr_stmt|;
name|this
operator|.
name|params
operator|=
operator|new
name|ModifiableSolrParams
argument_list|(
operator|new
name|MapSolrParams
argument_list|(
name|params
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * @param baseUrl Base URL of the stream.    * @param params  Map&lt;String, String&gt; of parameters    */
DECL|method|SolrStream
specifier|public
name|SolrStream
parameter_list|(
name|String
name|baseUrl
parameter_list|,
name|SolrParams
name|params
parameter_list|)
block|{
name|this
operator|.
name|baseUrl
operator|=
name|baseUrl
expr_stmt|;
name|this
operator|.
name|params
operator|=
name|params
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
operator|new
name|ArrayList
argument_list|()
return|;
block|}
DECL|method|getBaseUrl
specifier|public
name|String
name|getBaseUrl
parameter_list|()
block|{
return|return
name|baseUrl
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
block|}
comment|/**   * Opens the stream to a single Solr instance.   **/
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
operator|==
literal|null
condition|)
block|{
name|client
operator|=
operator|new
name|HttpSolrClient
operator|.
name|Builder
argument_list|(
name|baseUrl
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|client
operator|=
name|cache
operator|.
name|getHttpSolrClient
argument_list|(
name|baseUrl
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|tupleStreamParser
operator|=
name|constructParser
argument_list|(
name|client
argument_list|,
name|loadParams
argument_list|(
name|params
argument_list|)
argument_list|)
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
literal|"params "
operator|+
name|params
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    *  Setting trace to true will include the "_CORE_" field in each Tuple emitted by the stream.    **/
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
DECL|method|setSlice
specifier|public
name|void
name|setSlice
parameter_list|(
name|String
name|slice
parameter_list|)
block|{
name|this
operator|.
name|slice
operator|=
name|slice
expr_stmt|;
block|}
DECL|method|setCheckpoint
specifier|public
name|void
name|setCheckpoint
parameter_list|(
name|long
name|checkpoint
parameter_list|)
block|{
name|this
operator|.
name|checkpoint
operator|=
name|checkpoint
expr_stmt|;
block|}
DECL|method|loadParams
specifier|private
name|SolrParams
name|loadParams
parameter_list|(
name|SolrParams
name|paramsIn
parameter_list|)
throws|throws
name|IOException
block|{
name|ModifiableSolrParams
name|solrParams
init|=
operator|new
name|ModifiableSolrParams
argument_list|(
name|paramsIn
argument_list|)
decl_stmt|;
if|if
condition|(
name|params
operator|.
name|get
argument_list|(
literal|"partitionKeys"
argument_list|)
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|params
operator|.
name|get
argument_list|(
literal|"partitionKeys"
argument_list|)
operator|.
name|equals
argument_list|(
literal|"none"
argument_list|)
condition|)
block|{
name|String
name|partitionFilter
init|=
name|getPartitionFilter
argument_list|()
decl_stmt|;
name|solrParams
operator|.
name|add
argument_list|(
literal|"fq"
argument_list|,
name|partitionFilter
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|numWorkers
operator|>
literal|1
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"When numWorkers> 1 partitionKeys must be set. Set partitionKeys=none to send the entire stream to each worker."
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|checkpoint
operator|>
literal|0
condition|)
block|{
name|solrParams
operator|.
name|add
argument_list|(
literal|"fq"
argument_list|,
literal|"{!frange cost=100 incl=false l="
operator|+
name|checkpoint
operator|+
literal|"}_version_"
argument_list|)
expr_stmt|;
block|}
return|return
name|solrParams
return|;
block|}
DECL|method|getPartitionFilter
specifier|private
name|String
name|getPartitionFilter
parameter_list|()
block|{
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"{!hash workers="
argument_list|)
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|this
operator|.
name|numWorkers
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|" worker="
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|this
operator|.
name|workerID
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
return|return
name|buf
operator|.
name|toString
argument_list|()
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
name|withFunctionName
argument_list|(
literal|"non-expressible"
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
name|STREAM_SOURCE
argument_list|)
operator|.
name|withExpression
argument_list|(
literal|"non-expressible"
argument_list|)
return|;
block|}
comment|/**   *  Closes the Stream to a single Solr Instance   * */
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|closeableHttpResponse
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|cache
operator|==
literal|null
condition|)
block|{
name|client
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**   * Reads a Tuple from the stream. The Stream is completed when Tuple.EOF == true.   **/
DECL|method|read
specifier|public
name|Tuple
name|read
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|Map
name|fields
init|=
name|tupleStreamParser
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|fields
operator|==
literal|null
condition|)
block|{
comment|//Return the EOF tuple.
name|Map
name|m
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
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
else|else
block|{
name|String
name|msg
init|=
operator|(
name|String
operator|)
name|fields
operator|.
name|get
argument_list|(
literal|"EXCEPTION"
argument_list|)
decl_stmt|;
if|if
condition|(
name|msg
operator|!=
literal|null
condition|)
block|{
name|HandledException
name|ioException
init|=
operator|new
name|HandledException
argument_list|(
name|msg
argument_list|)
decl_stmt|;
throw|throw
name|ioException
throw|;
block|}
if|if
condition|(
name|trace
condition|)
block|{
name|fields
operator|.
name|put
argument_list|(
literal|"_CORE_"
argument_list|,
name|this
operator|.
name|baseUrl
argument_list|)
expr_stmt|;
if|if
condition|(
name|slice
operator|!=
literal|null
condition|)
block|{
name|fields
operator|.
name|put
argument_list|(
literal|"_SLICE_"
argument_list|,
name|slice
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|fieldMappings
operator|!=
literal|null
condition|)
block|{
name|fields
operator|=
name|mapFields
argument_list|(
name|fields
argument_list|,
name|fieldMappings
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|Tuple
argument_list|(
name|fields
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
name|HandledException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"--> "
operator|+
name|this
operator|.
name|baseUrl
operator|+
literal|":"
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|//The Stream source did not provide an exception in a format that the SolrStream could propagate.
throw|throw
operator|new
name|IOException
argument_list|(
literal|"--> "
operator|+
name|this
operator|.
name|baseUrl
operator|+
literal|": An exception has occurred on the server, refer to server log for details."
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|class|HandledException
specifier|public
specifier|static
class|class
name|HandledException
extends|extends
name|IOException
block|{
DECL|method|HandledException
specifier|public
name|HandledException
parameter_list|(
name|String
name|msg
parameter_list|)
block|{
name|super
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** There is no known sort applied to a SolrStream */
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
DECL|method|mapFields
specifier|private
name|Map
name|mapFields
parameter_list|(
name|Map
name|fields
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|mappings
parameter_list|)
block|{
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|it
init|=
name|mappings
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|mapFrom
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|String
name|mapTo
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Object
name|o
init|=
name|fields
operator|.
name|get
argument_list|(
name|mapFrom
argument_list|)
decl_stmt|;
name|fields
operator|.
name|remove
argument_list|(
name|mapFrom
argument_list|)
expr_stmt|;
name|fields
operator|.
name|put
argument_list|(
name|mapTo
argument_list|,
name|o
argument_list|)
expr_stmt|;
block|}
return|return
name|fields
return|;
block|}
comment|// temporary...
DECL|method|constructParser
specifier|public
name|TupleStreamParser
name|constructParser
parameter_list|(
name|SolrClient
name|server
parameter_list|,
name|SolrParams
name|requestParams
parameter_list|)
throws|throws
name|IOException
throws|,
name|SolrServerException
block|{
name|String
name|p
init|=
name|requestParams
operator|.
name|get
argument_list|(
literal|"qt"
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|!=
literal|null
condition|)
block|{
name|ModifiableSolrParams
name|modifiableSolrParams
init|=
operator|(
name|ModifiableSolrParams
operator|)
name|requestParams
decl_stmt|;
name|modifiableSolrParams
operator|.
name|remove
argument_list|(
literal|"qt"
argument_list|)
expr_stmt|;
block|}
name|String
name|wt
init|=
name|requestParams
operator|.
name|get
argument_list|(
name|CommonParams
operator|.
name|WT
argument_list|,
literal|"json"
argument_list|)
decl_stmt|;
name|QueryRequest
name|query
init|=
operator|new
name|QueryRequest
argument_list|(
name|requestParams
argument_list|)
decl_stmt|;
name|query
operator|.
name|setPath
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|query
operator|.
name|setResponseParser
argument_list|(
operator|new
name|InputStreamResponseParser
argument_list|(
name|wt
argument_list|)
argument_list|)
expr_stmt|;
name|query
operator|.
name|setMethod
argument_list|(
name|SolrRequest
operator|.
name|METHOD
operator|.
name|POST
argument_list|)
expr_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|genericResponse
init|=
name|server
operator|.
name|request
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|InputStream
name|stream
init|=
operator|(
name|InputStream
operator|)
name|genericResponse
operator|.
name|get
argument_list|(
literal|"stream"
argument_list|)
decl_stmt|;
name|this
operator|.
name|closeableHttpResponse
operator|=
operator|(
name|CloseableHttpResponse
operator|)
name|genericResponse
operator|.
name|get
argument_list|(
literal|"closeableResponse"
argument_list|)
expr_stmt|;
if|if
condition|(
name|CommonParams
operator|.
name|JAVABIN
operator|.
name|equals
argument_list|(
name|wt
argument_list|)
condition|)
block|{
return|return
operator|new
name|JavabinTupleStreamParser
argument_list|(
name|stream
argument_list|,
literal|true
argument_list|)
return|;
block|}
else|else
block|{
name|InputStreamReader
name|reader
init|=
operator|new
name|InputStreamReader
argument_list|(
name|stream
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
return|return
operator|new
name|JSONTupleStream
argument_list|(
name|reader
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

