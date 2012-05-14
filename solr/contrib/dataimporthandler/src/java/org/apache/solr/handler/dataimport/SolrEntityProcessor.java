begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.handler.dataimport
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|http
operator|.
name|impl
operator|.
name|client
operator|.
name|DefaultHttpClient
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
name|conn
operator|.
name|tsccm
operator|.
name|ThreadSafeClientConnManager
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
name|SolrQuery
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
name|SolrServer
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
name|HttpSolrServer
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
name|XMLResponseParser
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
name|CommonParams
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
import|import
name|java
operator|.
name|net
operator|.
name|MalformedURLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
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
name|Map
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
name|handler
operator|.
name|dataimport
operator|.
name|DataImportHandlerException
operator|.
name|SEVERE
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
name|handler
operator|.
name|dataimport
operator|.
name|DataImportHandlerException
operator|.
name|wrapAndThrow
import|;
end_import

begin_comment
comment|/**  *<p>  * An implementation of {@link EntityProcessor} which fetches values from a  * separate Solr implementation using the SolrJ client library. Yield a row per  * Solr document.  *</p>  *<p>  * Limitations:   * All configuration is evaluated at the beginning;  * Only one query is walked;  *</p>  */
end_comment

begin_class
DECL|class|SolrEntityProcessor
specifier|public
class|class
name|SolrEntityProcessor
extends|extends
name|EntityProcessorBase
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SolrEntityProcessor
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|SOLR_SERVER
specifier|public
specifier|static
specifier|final
name|String
name|SOLR_SERVER
init|=
literal|"url"
decl_stmt|;
DECL|field|QUERY
specifier|public
specifier|static
specifier|final
name|String
name|QUERY
init|=
literal|"query"
decl_stmt|;
DECL|field|TIMEOUT
specifier|public
specifier|static
specifier|final
name|String
name|TIMEOUT
init|=
literal|"timeout"
decl_stmt|;
DECL|field|TIMEOUT_SECS
specifier|public
specifier|static
specifier|final
name|int
name|TIMEOUT_SECS
init|=
literal|5
operator|*
literal|60
decl_stmt|;
comment|// 5 minutes
DECL|field|ROWS_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|ROWS_DEFAULT
init|=
literal|50
decl_stmt|;
DECL|field|solrServer
specifier|private
name|SolrServer
name|solrServer
init|=
literal|null
decl_stmt|;
DECL|field|queryString
specifier|private
name|String
name|queryString
decl_stmt|;
DECL|field|rows
specifier|private
name|int
name|rows
init|=
name|ROWS_DEFAULT
decl_stmt|;
DECL|field|filterQueries
specifier|private
name|String
index|[]
name|filterQueries
decl_stmt|;
DECL|field|fields
specifier|private
name|String
index|[]
name|fields
decl_stmt|;
DECL|field|queryType
specifier|private
name|String
name|queryType
decl_stmt|;
DECL|field|timeout
specifier|private
name|int
name|timeout
init|=
name|TIMEOUT_SECS
decl_stmt|;
DECL|field|initDone
specifier|private
name|boolean
name|initDone
init|=
literal|false
decl_stmt|;
comment|/**    * Factory method that returns a {@link HttpClient} instance used for interfacing with a source Solr service.    * One can override this method to return a differently configured {@link HttpClient} instance.    * For example configure https and http authentication.    *    * @return a {@link HttpClient} instance used for interfacing with a source Solr service    */
DECL|method|getHttpClient
specifier|protected
name|HttpClient
name|getHttpClient
parameter_list|()
block|{
return|return
operator|new
name|DefaultHttpClient
argument_list|(
operator|new
name|ThreadSafeClientConnManager
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|firstInit
specifier|protected
name|void
name|firstInit
parameter_list|(
name|Context
name|context
parameter_list|)
block|{
name|super
operator|.
name|firstInit
argument_list|(
name|context
argument_list|)
expr_stmt|;
try|try
block|{
name|String
name|serverPath
init|=
name|context
operator|.
name|getResolvedEntityAttribute
argument_list|(
name|SOLR_SERVER
argument_list|)
decl_stmt|;
if|if
condition|(
name|serverPath
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|DataImportHandlerException
operator|.
name|SEVERE
argument_list|,
literal|"SolrEntityProcessor: parameter 'url' is required"
argument_list|)
throw|;
block|}
name|HttpClient
name|client
init|=
name|getHttpClient
argument_list|()
decl_stmt|;
name|URL
name|url
init|=
operator|new
name|URL
argument_list|(
name|serverPath
argument_list|)
decl_stmt|;
comment|// (wt="javabin|xml") default is javabin
if|if
condition|(
literal|"xml"
operator|.
name|equals
argument_list|(
name|context
operator|.
name|getResolvedEntityAttribute
argument_list|(
name|CommonParams
operator|.
name|WT
argument_list|)
argument_list|)
condition|)
block|{
name|solrServer
operator|=
operator|new
name|HttpSolrServer
argument_list|(
name|url
operator|.
name|toExternalForm
argument_list|()
argument_list|,
name|client
argument_list|,
operator|new
name|XMLResponseParser
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"using XMLResponseParser"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|solrServer
operator|=
operator|new
name|HttpSolrServer
argument_list|(
name|url
operator|.
name|toExternalForm
argument_list|()
argument_list|,
name|client
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"using BinaryResponseParser"
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|MalformedURLException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|DataImportHandlerException
operator|.
name|SEVERE
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|nextRow
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|nextRow
parameter_list|()
block|{
name|buildIterator
argument_list|()
expr_stmt|;
return|return
name|getNext
argument_list|()
return|;
block|}
comment|/**    * The following method changes the rowIterator mutable field. It requires    * external synchronization.     */
DECL|method|buildIterator
specifier|private
name|void
name|buildIterator
parameter_list|()
block|{
if|if
condition|(
name|rowIterator
operator|==
literal|null
condition|)
block|{
comment|// We could use an AtomicBoolean but there's no need since this method
comment|// would require anyway external synchronization
if|if
condition|(
operator|!
name|initDone
condition|)
block|{
name|initDone
operator|=
literal|true
expr_stmt|;
name|SolrDocumentList
name|solrDocumentList
init|=
name|doQuery
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|solrDocumentList
operator|!=
literal|null
condition|)
block|{
name|rowIterator
operator|=
operator|new
name|SolrDocumentListIterator
argument_list|(
name|solrDocumentList
argument_list|)
expr_stmt|;
block|}
block|}
return|return;
block|}
name|SolrDocumentListIterator
name|documentListIterator
init|=
operator|(
name|SolrDocumentListIterator
operator|)
name|rowIterator
decl_stmt|;
if|if
condition|(
operator|!
name|documentListIterator
operator|.
name|hasNext
argument_list|()
operator|&&
name|documentListIterator
operator|.
name|hasMoreRows
argument_list|()
condition|)
block|{
name|SolrDocumentList
name|solrDocumentList
init|=
name|doQuery
argument_list|(
name|documentListIterator
operator|.
name|getStart
argument_list|()
operator|+
name|documentListIterator
operator|.
name|getSize
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|solrDocumentList
operator|!=
literal|null
condition|)
block|{
name|rowIterator
operator|=
operator|new
name|SolrDocumentListIterator
argument_list|(
name|solrDocumentList
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|doQuery
specifier|protected
name|SolrDocumentList
name|doQuery
parameter_list|(
name|int
name|start
parameter_list|)
block|{
name|this
operator|.
name|queryString
operator|=
name|context
operator|.
name|getResolvedEntityAttribute
argument_list|(
name|QUERY
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|queryString
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|DataImportHandlerException
operator|.
name|SEVERE
argument_list|,
literal|"SolrEntityProcessor: parameter 'query' is required"
argument_list|)
throw|;
block|}
name|String
name|rowsP
init|=
name|context
operator|.
name|getResolvedEntityAttribute
argument_list|(
name|CommonParams
operator|.
name|ROWS
argument_list|)
decl_stmt|;
if|if
condition|(
name|rowsP
operator|!=
literal|null
condition|)
block|{
name|rows
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|rowsP
argument_list|)
expr_stmt|;
block|}
name|String
name|fqAsString
init|=
name|context
operator|.
name|getResolvedEntityAttribute
argument_list|(
name|CommonParams
operator|.
name|FQ
argument_list|)
decl_stmt|;
if|if
condition|(
name|fqAsString
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|filterQueries
operator|=
name|fqAsString
operator|.
name|split
argument_list|(
literal|","
argument_list|)
expr_stmt|;
block|}
name|String
name|fieldsAsString
init|=
name|context
operator|.
name|getResolvedEntityAttribute
argument_list|(
name|CommonParams
operator|.
name|FL
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldsAsString
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|fields
operator|=
name|fieldsAsString
operator|.
name|split
argument_list|(
literal|","
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|queryType
operator|=
name|context
operator|.
name|getResolvedEntityAttribute
argument_list|(
name|CommonParams
operator|.
name|QT
argument_list|)
expr_stmt|;
name|String
name|timeoutAsString
init|=
name|context
operator|.
name|getResolvedEntityAttribute
argument_list|(
name|TIMEOUT
argument_list|)
decl_stmt|;
if|if
condition|(
name|timeoutAsString
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|timeout
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|timeoutAsString
argument_list|)
expr_stmt|;
block|}
name|SolrQuery
name|solrQuery
init|=
operator|new
name|SolrQuery
argument_list|(
name|queryString
argument_list|)
decl_stmt|;
name|solrQuery
operator|.
name|setRows
argument_list|(
name|rows
argument_list|)
expr_stmt|;
name|solrQuery
operator|.
name|setStart
argument_list|(
name|start
argument_list|)
expr_stmt|;
if|if
condition|(
name|fields
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
name|solrQuery
operator|.
name|addField
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
block|}
name|solrQuery
operator|.
name|setQueryType
argument_list|(
name|queryType
argument_list|)
expr_stmt|;
name|solrQuery
operator|.
name|setFilterQueries
argument_list|(
name|filterQueries
argument_list|)
expr_stmt|;
name|solrQuery
operator|.
name|setTimeAllowed
argument_list|(
name|timeout
operator|*
literal|1000
argument_list|)
expr_stmt|;
name|QueryResponse
name|response
init|=
literal|null
decl_stmt|;
try|try
block|{
name|response
operator|=
name|solrServer
operator|.
name|query
argument_list|(
name|solrQuery
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrServerException
name|e
parameter_list|)
block|{
if|if
condition|(
name|ABORT
operator|.
name|equals
argument_list|(
name|onError
argument_list|)
condition|)
block|{
name|wrapAndThrow
argument_list|(
name|SEVERE
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|SKIP
operator|.
name|equals
argument_list|(
name|onError
argument_list|)
condition|)
block|{
name|wrapAndThrow
argument_list|(
name|DataImportHandlerException
operator|.
name|SKIP_ROW
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|response
operator|==
literal|null
condition|?
literal|null
else|:
name|response
operator|.
name|getResults
argument_list|()
return|;
block|}
DECL|class|SolrDocumentListIterator
specifier|private
specifier|static
class|class
name|SolrDocumentListIterator
implements|implements
name|Iterator
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
block|{
DECL|field|start
specifier|private
specifier|final
name|int
name|start
decl_stmt|;
DECL|field|size
specifier|private
specifier|final
name|int
name|size
decl_stmt|;
DECL|field|numFound
specifier|private
specifier|final
name|long
name|numFound
decl_stmt|;
DECL|field|solrDocumentIterator
specifier|private
specifier|final
name|Iterator
argument_list|<
name|SolrDocument
argument_list|>
name|solrDocumentIterator
decl_stmt|;
DECL|method|SolrDocumentListIterator
specifier|public
name|SolrDocumentListIterator
parameter_list|(
name|SolrDocumentList
name|solrDocumentList
parameter_list|)
block|{
name|this
operator|.
name|solrDocumentIterator
operator|=
name|solrDocumentList
operator|.
name|iterator
argument_list|()
expr_stmt|;
name|this
operator|.
name|numFound
operator|=
name|solrDocumentList
operator|.
name|getNumFound
argument_list|()
expr_stmt|;
comment|// SolrQuery has the start field of type int while SolrDocumentList of
comment|// type long. We are always querying with an int so we can't receive a
comment|// long as output. That's the reason why the following cast seems safe
name|this
operator|.
name|start
operator|=
operator|(
name|int
operator|)
name|solrDocumentList
operator|.
name|getStart
argument_list|()
expr_stmt|;
name|this
operator|.
name|size
operator|=
name|solrDocumentList
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hasNext
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|solrDocumentIterator
operator|.
name|hasNext
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|next
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|next
parameter_list|()
block|{
name|SolrDocument
name|solrDocument
init|=
name|solrDocumentIterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|Collection
argument_list|<
name|String
argument_list|>
name|fields
init|=
name|solrDocument
operator|.
name|getFieldNames
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|field
range|:
name|fields
control|)
block|{
name|Object
name|fieldValue
init|=
name|solrDocument
operator|.
name|getFieldValue
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|field
argument_list|,
name|fieldValue
argument_list|)
expr_stmt|;
block|}
return|return
name|map
return|;
block|}
DECL|method|getStart
specifier|public
name|int
name|getStart
parameter_list|()
block|{
return|return
name|start
return|;
block|}
DECL|method|getSize
specifier|public
name|int
name|getSize
parameter_list|()
block|{
return|return
name|size
return|;
block|}
DECL|method|hasMoreRows
specifier|public
name|boolean
name|hasMoreRows
parameter_list|()
block|{
return|return
name|numFound
operator|>
name|start
operator|+
name|size
return|;
block|}
annotation|@
name|Override
DECL|method|remove
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
block|}
end_class

end_unit

