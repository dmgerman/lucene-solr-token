begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.client.solrj.request
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
name|request
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
name|StringWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
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
name|Set
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
name|LBHttpSolrServer
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
name|util
operator|.
name|ClientUtils
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
name|SolrInputDocument
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
name|DocRouter
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
name|util
operator|.
name|ContentStream
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
name|XML
import|;
end_import

begin_comment
comment|/**  *   *   * @since solr 1.3  */
end_comment

begin_class
DECL|class|UpdateRequest
specifier|public
class|class
name|UpdateRequest
extends|extends
name|AbstractUpdateRequest
block|{
DECL|field|REPFACT
specifier|public
specifier|static
specifier|final
name|String
name|REPFACT
init|=
literal|"rf"
decl_stmt|;
DECL|field|MIN_REPFACT
specifier|public
specifier|static
specifier|final
name|String
name|MIN_REPFACT
init|=
literal|"min_rf"
decl_stmt|;
DECL|field|VER
specifier|public
specifier|static
specifier|final
name|String
name|VER
init|=
literal|"ver"
decl_stmt|;
DECL|field|OVERWRITE
specifier|public
specifier|static
specifier|final
name|String
name|OVERWRITE
init|=
literal|"ow"
decl_stmt|;
DECL|field|COMMIT_WITHIN
specifier|public
specifier|static
specifier|final
name|String
name|COMMIT_WITHIN
init|=
literal|"cw"
decl_stmt|;
DECL|field|documents
specifier|private
name|Map
argument_list|<
name|SolrInputDocument
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|documents
init|=
literal|null
decl_stmt|;
DECL|field|docIterator
specifier|private
name|Iterator
argument_list|<
name|SolrInputDocument
argument_list|>
name|docIterator
init|=
literal|null
decl_stmt|;
DECL|field|deleteById
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|deleteById
init|=
literal|null
decl_stmt|;
DECL|field|deleteQuery
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|deleteQuery
init|=
literal|null
decl_stmt|;
DECL|method|UpdateRequest
specifier|public
name|UpdateRequest
parameter_list|()
block|{
name|super
argument_list|(
name|METHOD
operator|.
name|POST
argument_list|,
literal|"/update"
argument_list|)
expr_stmt|;
block|}
DECL|method|UpdateRequest
specifier|public
name|UpdateRequest
parameter_list|(
name|String
name|url
parameter_list|)
block|{
name|super
argument_list|(
name|METHOD
operator|.
name|POST
argument_list|,
name|url
argument_list|)
expr_stmt|;
block|}
comment|// ---------------------------------------------------------------------------
comment|// ---------------------------------------------------------------------------
comment|/**    * clear the pending documents and delete commands    */
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
if|if
condition|(
name|documents
operator|!=
literal|null
condition|)
block|{
name|documents
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|deleteById
operator|!=
literal|null
condition|)
block|{
name|deleteById
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|deleteQuery
operator|!=
literal|null
condition|)
block|{
name|deleteQuery
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
comment|// ---------------------------------------------------------------------------
comment|// ---------------------------------------------------------------------------
DECL|method|add
specifier|public
name|UpdateRequest
name|add
parameter_list|(
specifier|final
name|SolrInputDocument
name|doc
parameter_list|)
block|{
if|if
condition|(
name|documents
operator|==
literal|null
condition|)
block|{
name|documents
operator|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
name|documents
operator|.
name|put
argument_list|(
name|doc
argument_list|,
literal|null
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|add
specifier|public
name|UpdateRequest
name|add
parameter_list|(
specifier|final
name|SolrInputDocument
name|doc
parameter_list|,
name|Boolean
name|overwrite
parameter_list|)
block|{
return|return
name|add
argument_list|(
name|doc
argument_list|,
literal|null
argument_list|,
name|overwrite
argument_list|)
return|;
block|}
DECL|method|add
specifier|public
name|UpdateRequest
name|add
parameter_list|(
specifier|final
name|SolrInputDocument
name|doc
parameter_list|,
name|Integer
name|commitWithin
parameter_list|)
block|{
return|return
name|add
argument_list|(
name|doc
argument_list|,
name|commitWithin
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|add
specifier|public
name|UpdateRequest
name|add
parameter_list|(
specifier|final
name|SolrInputDocument
name|doc
parameter_list|,
name|Integer
name|commitWithin
parameter_list|,
name|Boolean
name|overwrite
parameter_list|)
block|{
if|if
condition|(
name|documents
operator|==
literal|null
condition|)
block|{
name|documents
operator|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|params
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
literal|2
argument_list|)
decl_stmt|;
if|if
condition|(
name|commitWithin
operator|!=
literal|null
condition|)
name|params
operator|.
name|put
argument_list|(
name|COMMIT_WITHIN
argument_list|,
name|commitWithin
argument_list|)
expr_stmt|;
if|if
condition|(
name|overwrite
operator|!=
literal|null
condition|)
name|params
operator|.
name|put
argument_list|(
name|OVERWRITE
argument_list|,
name|overwrite
argument_list|)
expr_stmt|;
name|documents
operator|.
name|put
argument_list|(
name|doc
argument_list|,
name|params
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|add
specifier|public
name|UpdateRequest
name|add
parameter_list|(
specifier|final
name|Collection
argument_list|<
name|SolrInputDocument
argument_list|>
name|docs
parameter_list|)
block|{
if|if
condition|(
name|documents
operator|==
literal|null
condition|)
block|{
name|documents
operator|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|SolrInputDocument
name|doc
range|:
name|docs
control|)
block|{
name|documents
operator|.
name|put
argument_list|(
name|doc
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
DECL|method|deleteById
specifier|public
name|UpdateRequest
name|deleteById
parameter_list|(
name|String
name|id
parameter_list|)
block|{
if|if
condition|(
name|deleteById
operator|==
literal|null
condition|)
block|{
name|deleteById
operator|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
name|deleteById
operator|.
name|put
argument_list|(
name|id
argument_list|,
literal|null
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|deleteById
specifier|public
name|UpdateRequest
name|deleteById
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|ids
parameter_list|)
block|{
if|if
condition|(
name|deleteById
operator|==
literal|null
condition|)
block|{
name|deleteById
operator|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|String
name|id
range|:
name|ids
control|)
block|{
name|deleteById
operator|.
name|put
argument_list|(
name|id
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
DECL|method|deleteById
specifier|public
name|UpdateRequest
name|deleteById
parameter_list|(
name|String
name|id
parameter_list|,
name|Long
name|version
parameter_list|)
block|{
if|if
condition|(
name|deleteById
operator|==
literal|null
condition|)
block|{
name|deleteById
operator|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|params
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|params
operator|.
name|put
argument_list|(
name|VER
argument_list|,
name|version
argument_list|)
expr_stmt|;
name|deleteById
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|params
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|deleteByQuery
specifier|public
name|UpdateRequest
name|deleteByQuery
parameter_list|(
name|String
name|q
parameter_list|)
block|{
if|if
condition|(
name|deleteQuery
operator|==
literal|null
condition|)
block|{
name|deleteQuery
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
block|}
name|deleteQuery
operator|.
name|add
argument_list|(
name|q
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * @param router to route updates with    * @param col DocCollection for the updates    * @param urlMap of the cluster    * @param params params to use    * @param idField the id field    * @return a Map of urls to requests    */
DECL|method|getRoutes
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|LBHttpSolrServer
operator|.
name|Req
argument_list|>
name|getRoutes
parameter_list|(
name|DocRouter
name|router
parameter_list|,
name|DocCollection
name|col
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|urlMap
parameter_list|,
name|ModifiableSolrParams
name|params
parameter_list|,
name|String
name|idField
parameter_list|)
block|{
if|if
condition|(
operator|(
name|documents
operator|==
literal|null
operator|||
name|documents
operator|.
name|size
argument_list|()
operator|==
literal|0
operator|)
operator|&&
operator|(
name|deleteById
operator|==
literal|null
operator|||
name|deleteById
operator|.
name|size
argument_list|()
operator|==
literal|0
operator|)
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|LBHttpSolrServer
operator|.
name|Req
argument_list|>
name|routes
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|documents
operator|!=
literal|null
condition|)
block|{
name|Set
argument_list|<
name|Entry
argument_list|<
name|SolrInputDocument
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|>
name|entries
init|=
name|documents
operator|.
name|entrySet
argument_list|()
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|SolrInputDocument
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|entry
range|:
name|entries
control|)
block|{
name|SolrInputDocument
name|doc
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Object
name|id
init|=
name|doc
operator|.
name|getFieldValue
argument_list|(
name|idField
argument_list|)
decl_stmt|;
if|if
condition|(
name|id
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Slice
name|slice
init|=
name|router
operator|.
name|getTargetSlice
argument_list|(
name|id
operator|.
name|toString
argument_list|()
argument_list|,
name|doc
argument_list|,
literal|null
argument_list|,
name|col
argument_list|)
decl_stmt|;
if|if
condition|(
name|slice
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|urls
init|=
name|urlMap
operator|.
name|get
argument_list|(
name|slice
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|leaderUrl
init|=
name|urls
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|LBHttpSolrServer
operator|.
name|Req
name|request
init|=
operator|(
name|LBHttpSolrServer
operator|.
name|Req
operator|)
name|routes
operator|.
name|get
argument_list|(
name|leaderUrl
argument_list|)
decl_stmt|;
if|if
condition|(
name|request
operator|==
literal|null
condition|)
block|{
name|UpdateRequest
name|updateRequest
init|=
operator|new
name|UpdateRequest
argument_list|()
decl_stmt|;
name|updateRequest
operator|.
name|setMethod
argument_list|(
name|getMethod
argument_list|()
argument_list|)
expr_stmt|;
name|updateRequest
operator|.
name|setCommitWithin
argument_list|(
name|getCommitWithin
argument_list|()
argument_list|)
expr_stmt|;
name|updateRequest
operator|.
name|setParams
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|updateRequest
operator|.
name|setPath
argument_list|(
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|request
operator|=
operator|new
name|LBHttpSolrServer
operator|.
name|Req
argument_list|(
name|updateRequest
argument_list|,
name|urls
argument_list|)
expr_stmt|;
name|routes
operator|.
name|put
argument_list|(
name|leaderUrl
argument_list|,
name|request
argument_list|)
expr_stmt|;
block|}
name|UpdateRequest
name|urequest
init|=
operator|(
name|UpdateRequest
operator|)
name|request
operator|.
name|getRequest
argument_list|()
decl_stmt|;
name|urequest
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Route the deleteById's
if|if
condition|(
name|deleteById
operator|!=
literal|null
condition|)
block|{
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|>
name|entries
init|=
name|deleteById
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|entries
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
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|entry
init|=
name|entries
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|deleteId
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Long
name|version
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|map
operator|!=
literal|null
condition|)
block|{
name|version
operator|=
operator|(
name|Long
operator|)
name|map
operator|.
name|get
argument_list|(
name|VER
argument_list|)
expr_stmt|;
block|}
name|Slice
name|slice
init|=
name|router
operator|.
name|getTargetSlice
argument_list|(
name|deleteId
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|col
argument_list|)
decl_stmt|;
if|if
condition|(
name|slice
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|urls
init|=
name|urlMap
operator|.
name|get
argument_list|(
name|slice
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|leaderUrl
init|=
name|urls
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|LBHttpSolrServer
operator|.
name|Req
name|request
init|=
name|routes
operator|.
name|get
argument_list|(
name|leaderUrl
argument_list|)
decl_stmt|;
if|if
condition|(
name|request
operator|!=
literal|null
condition|)
block|{
name|UpdateRequest
name|urequest
init|=
operator|(
name|UpdateRequest
operator|)
name|request
operator|.
name|getRequest
argument_list|()
decl_stmt|;
name|urequest
operator|.
name|deleteById
argument_list|(
name|deleteId
argument_list|,
name|version
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|UpdateRequest
name|urequest
init|=
operator|new
name|UpdateRequest
argument_list|()
decl_stmt|;
name|urequest
operator|.
name|setParams
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|urequest
operator|.
name|deleteById
argument_list|(
name|deleteId
argument_list|,
name|version
argument_list|)
expr_stmt|;
name|request
operator|=
operator|new
name|LBHttpSolrServer
operator|.
name|Req
argument_list|(
name|urequest
argument_list|,
name|urls
argument_list|)
expr_stmt|;
name|routes
operator|.
name|put
argument_list|(
name|leaderUrl
argument_list|,
name|request
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|routes
return|;
block|}
DECL|method|setDocIterator
specifier|public
name|void
name|setDocIterator
parameter_list|(
name|Iterator
argument_list|<
name|SolrInputDocument
argument_list|>
name|docIterator
parameter_list|)
block|{
name|this
operator|.
name|docIterator
operator|=
name|docIterator
expr_stmt|;
block|}
DECL|method|setDeleteQuery
specifier|public
name|void
name|setDeleteQuery
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|deleteQuery
parameter_list|)
block|{
name|this
operator|.
name|deleteQuery
operator|=
name|deleteQuery
expr_stmt|;
block|}
comment|// --------------------------------------------------------------------------
comment|// --------------------------------------------------------------------------
annotation|@
name|Override
DECL|method|getContentStreams
specifier|public
name|Collection
argument_list|<
name|ContentStream
argument_list|>
name|getContentStreams
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|ClientUtils
operator|.
name|toContentStreams
argument_list|(
name|getXML
argument_list|()
argument_list|,
name|ClientUtils
operator|.
name|TEXT_XML
argument_list|)
return|;
block|}
DECL|method|getXML
specifier|public
name|String
name|getXML
parameter_list|()
throws|throws
name|IOException
block|{
name|StringWriter
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|writeXML
argument_list|(
name|writer
argument_list|)
expr_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
comment|// If action is COMMIT or OPTIMIZE, it is sent with params
name|String
name|xml
init|=
name|writer
operator|.
name|toString
argument_list|()
decl_stmt|;
comment|// System.out.println( "SEND:"+xml );
return|return
operator|(
name|xml
operator|.
name|length
argument_list|()
operator|>
literal|0
operator|)
condition|?
name|xml
else|:
literal|null
return|;
block|}
DECL|method|getDocLists
specifier|private
name|List
argument_list|<
name|Map
argument_list|<
name|SolrInputDocument
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|>
name|getDocLists
parameter_list|(
name|Map
argument_list|<
name|SolrInputDocument
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|documents
parameter_list|)
block|{
name|List
argument_list|<
name|Map
argument_list|<
name|SolrInputDocument
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|>
name|docLists
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|SolrInputDocument
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|docList
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|documents
operator|!=
literal|null
condition|)
block|{
name|Boolean
name|lastOverwrite
init|=
literal|true
decl_stmt|;
name|Integer
name|lastCommitWithin
init|=
operator|-
literal|1
decl_stmt|;
name|Set
argument_list|<
name|Entry
argument_list|<
name|SolrInputDocument
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|>
name|entries
init|=
name|this
operator|.
name|documents
operator|.
name|entrySet
argument_list|()
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|SolrInputDocument
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|entry
range|:
name|entries
control|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Boolean
name|overwrite
init|=
literal|null
decl_stmt|;
name|Integer
name|commitWithin
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|map
operator|!=
literal|null
condition|)
block|{
name|overwrite
operator|=
operator|(
name|Boolean
operator|)
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|get
argument_list|(
name|OVERWRITE
argument_list|)
expr_stmt|;
name|commitWithin
operator|=
operator|(
name|Integer
operator|)
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|get
argument_list|(
name|COMMIT_WITHIN
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|overwrite
operator|!=
name|lastOverwrite
operator|||
name|commitWithin
operator|!=
name|lastCommitWithin
operator|||
name|docLists
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|docList
operator|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|docLists
operator|.
name|add
argument_list|(
name|docList
argument_list|)
expr_stmt|;
block|}
name|docList
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|lastCommitWithin
operator|=
name|commitWithin
expr_stmt|;
name|lastOverwrite
operator|=
name|overwrite
expr_stmt|;
block|}
block|}
if|if
condition|(
name|docIterator
operator|!=
literal|null
condition|)
block|{
name|docList
operator|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|docLists
operator|.
name|add
argument_list|(
name|docList
argument_list|)
expr_stmt|;
while|while
condition|(
name|docIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|SolrInputDocument
name|doc
init|=
name|docIterator
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|doc
operator|!=
literal|null
condition|)
block|{
name|docList
operator|.
name|put
argument_list|(
name|doc
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|docLists
return|;
block|}
comment|/**    * @since solr 1.4    */
DECL|method|writeXML
specifier|public
name|void
name|writeXML
parameter_list|(
name|Writer
name|writer
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|Map
argument_list|<
name|SolrInputDocument
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|>
name|getDocLists
init|=
name|getDocLists
argument_list|(
name|documents
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
argument_list|<
name|SolrInputDocument
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|docs
range|:
name|getDocLists
control|)
block|{
if|if
condition|(
operator|(
name|docs
operator|!=
literal|null
operator|&&
name|docs
operator|.
name|size
argument_list|()
operator|>
literal|0
operator|)
condition|)
block|{
name|Entry
argument_list|<
name|SolrInputDocument
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|firstDoc
init|=
name|docs
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
init|=
name|firstDoc
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Integer
name|cw
init|=
literal|null
decl_stmt|;
name|Boolean
name|ow
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|map
operator|!=
literal|null
condition|)
block|{
name|cw
operator|=
operator|(
name|Integer
operator|)
name|firstDoc
operator|.
name|getValue
argument_list|()
operator|.
name|get
argument_list|(
name|COMMIT_WITHIN
argument_list|)
expr_stmt|;
name|ow
operator|=
operator|(
name|Boolean
operator|)
name|firstDoc
operator|.
name|getValue
argument_list|()
operator|.
name|get
argument_list|(
name|OVERWRITE
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|ow
operator|==
literal|null
condition|)
name|ow
operator|=
literal|true
expr_stmt|;
name|int
name|commitWithin
init|=
operator|(
name|cw
operator|!=
literal|null
operator|&&
name|cw
operator|!=
operator|-
literal|1
operator|)
condition|?
name|cw
else|:
name|this
operator|.
name|commitWithin
decl_stmt|;
name|boolean
name|overwrite
init|=
name|ow
decl_stmt|;
if|if
condition|(
name|commitWithin
operator|>
operator|-
literal|1
operator|||
name|overwrite
operator|!=
literal|true
condition|)
block|{
name|writer
operator|.
name|write
argument_list|(
literal|"<add commitWithin=\""
operator|+
name|commitWithin
operator|+
literal|"\" "
operator|+
literal|"overwrite=\""
operator|+
name|overwrite
operator|+
literal|"\">"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|writer
operator|.
name|write
argument_list|(
literal|"<add>"
argument_list|)
expr_stmt|;
block|}
name|Set
argument_list|<
name|Entry
argument_list|<
name|SolrInputDocument
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|>
name|entries
init|=
name|docs
operator|.
name|entrySet
argument_list|()
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|SolrInputDocument
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|entry
range|:
name|entries
control|)
block|{
name|ClientUtils
operator|.
name|writeXML
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|writer
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|write
argument_list|(
literal|"</add>"
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Add the delete commands
name|boolean
name|deleteI
init|=
name|deleteById
operator|!=
literal|null
operator|&&
name|deleteById
operator|.
name|size
argument_list|()
operator|>
literal|0
decl_stmt|;
name|boolean
name|deleteQ
init|=
name|deleteQuery
operator|!=
literal|null
operator|&&
name|deleteQuery
operator|.
name|size
argument_list|()
operator|>
literal|0
decl_stmt|;
if|if
condition|(
name|deleteI
operator|||
name|deleteQ
condition|)
block|{
if|if
condition|(
name|commitWithin
operator|>
literal|0
condition|)
block|{
name|writer
operator|.
name|append
argument_list|(
literal|"<delete commitWithin=\""
operator|+
name|commitWithin
operator|+
literal|"\">"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|writer
operator|.
name|append
argument_list|(
literal|"<delete>"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|deleteI
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|entry
range|:
name|deleteById
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|writer
operator|.
name|append
argument_list|(
literal|"<id"
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|map
operator|!=
literal|null
condition|)
block|{
name|Long
name|version
init|=
operator|(
name|Long
operator|)
name|map
operator|.
name|get
argument_list|(
name|VER
argument_list|)
decl_stmt|;
if|if
condition|(
name|version
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|append
argument_list|(
literal|" version=\""
operator|+
name|version
operator|+
literal|"\""
argument_list|)
expr_stmt|;
block|}
block|}
name|writer
operator|.
name|append
argument_list|(
literal|">"
argument_list|)
expr_stmt|;
name|XML
operator|.
name|escapeCharData
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|writer
argument_list|)
expr_stmt|;
name|writer
operator|.
name|append
argument_list|(
literal|"</id>"
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|deleteQ
condition|)
block|{
for|for
control|(
name|String
name|q
range|:
name|deleteQuery
control|)
block|{
name|writer
operator|.
name|append
argument_list|(
literal|"<query>"
argument_list|)
expr_stmt|;
name|XML
operator|.
name|escapeCharData
argument_list|(
name|q
argument_list|,
name|writer
argument_list|)
expr_stmt|;
name|writer
operator|.
name|append
argument_list|(
literal|"</query>"
argument_list|)
expr_stmt|;
block|}
block|}
name|writer
operator|.
name|append
argument_list|(
literal|"</delete>"
argument_list|)
expr_stmt|;
block|}
block|}
comment|// --------------------------------------------------------------------------
comment|// --------------------------------------------------------------------------
comment|// --------------------------------------------------------------------------
comment|//
comment|// --------------------------------------------------------------------------
DECL|method|getDocuments
specifier|public
name|List
argument_list|<
name|SolrInputDocument
argument_list|>
name|getDocuments
parameter_list|()
block|{
if|if
condition|(
name|documents
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|List
argument_list|<
name|SolrInputDocument
argument_list|>
name|docs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|documents
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|docs
operator|.
name|addAll
argument_list|(
name|documents
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|docs
return|;
block|}
DECL|method|getDocumentsMap
specifier|public
name|Map
argument_list|<
name|SolrInputDocument
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|getDocumentsMap
parameter_list|()
block|{
return|return
name|documents
return|;
block|}
DECL|method|getDocIterator
specifier|public
name|Iterator
argument_list|<
name|SolrInputDocument
argument_list|>
name|getDocIterator
parameter_list|()
block|{
return|return
name|docIterator
return|;
block|}
DECL|method|getDeleteById
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getDeleteById
parameter_list|()
block|{
if|if
condition|(
name|deleteById
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|List
argument_list|<
name|String
argument_list|>
name|deletes
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|deleteById
operator|.
name|keySet
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|deletes
return|;
block|}
DECL|method|getDeleteByIdMap
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|getDeleteByIdMap
parameter_list|()
block|{
return|return
name|deleteById
return|;
block|}
DECL|method|getDeleteQuery
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getDeleteQuery
parameter_list|()
block|{
return|return
name|deleteQuery
return|;
block|}
block|}
end_class

end_unit

