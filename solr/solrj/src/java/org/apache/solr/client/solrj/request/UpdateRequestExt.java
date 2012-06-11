begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
comment|// TODO: bake this into UpdateRequest
end_comment

begin_class
DECL|class|UpdateRequestExt
specifier|public
class|class
name|UpdateRequestExt
extends|extends
name|AbstractUpdateRequest
block|{
DECL|field|documents
specifier|private
name|List
argument_list|<
name|SolrDoc
argument_list|>
name|documents
init|=
literal|null
decl_stmt|;
DECL|field|deleteById
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Long
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
DECL|class|SolrDoc
specifier|private
class|class
name|SolrDoc
block|{
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"SolrDoc [document="
operator|+
name|document
operator|+
literal|", commitWithin="
operator|+
name|commitWithin
operator|+
literal|", overwrite="
operator|+
name|overwrite
operator|+
literal|"]"
return|;
block|}
DECL|field|document
name|SolrInputDocument
name|document
decl_stmt|;
DECL|field|commitWithin
name|int
name|commitWithin
decl_stmt|;
DECL|field|overwrite
name|boolean
name|overwrite
decl_stmt|;
block|}
DECL|method|UpdateRequestExt
specifier|public
name|UpdateRequestExt
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
DECL|method|UpdateRequestExt
specifier|public
name|UpdateRequestExt
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
name|UpdateRequestExt
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
name|ArrayList
argument_list|<
name|SolrDoc
argument_list|>
argument_list|(
literal|2
argument_list|)
expr_stmt|;
block|}
name|SolrDoc
name|solrDoc
init|=
operator|new
name|SolrDoc
argument_list|()
decl_stmt|;
name|solrDoc
operator|.
name|document
operator|=
name|doc
expr_stmt|;
name|solrDoc
operator|.
name|commitWithin
operator|=
operator|-
literal|1
expr_stmt|;
name|solrDoc
operator|.
name|overwrite
operator|=
literal|true
expr_stmt|;
name|documents
operator|.
name|add
argument_list|(
name|solrDoc
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|add
specifier|public
name|UpdateRequestExt
name|add
parameter_list|(
specifier|final
name|SolrInputDocument
name|doc
parameter_list|,
name|int
name|commitWithin
parameter_list|,
name|boolean
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
name|ArrayList
argument_list|<
name|SolrDoc
argument_list|>
argument_list|(
literal|2
argument_list|)
expr_stmt|;
block|}
name|SolrDoc
name|solrDoc
init|=
operator|new
name|SolrDoc
argument_list|()
decl_stmt|;
name|solrDoc
operator|.
name|document
operator|=
name|doc
expr_stmt|;
name|solrDoc
operator|.
name|commitWithin
operator|=
name|commitWithin
expr_stmt|;
name|solrDoc
operator|.
name|overwrite
operator|=
name|overwrite
expr_stmt|;
name|documents
operator|.
name|add
argument_list|(
name|solrDoc
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|deleteById
specifier|public
name|UpdateRequestExt
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
name|HashMap
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
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
name|UpdateRequestExt
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
name|HashMap
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|deleteById
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|version
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|deleteById
specifier|public
name|UpdateRequestExt
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
name|HashMap
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
argument_list|()
expr_stmt|;
block|}
else|else
block|{
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
block|}
return|return
name|this
return|;
block|}
DECL|method|deleteByQuery
specifier|public
name|UpdateRequestExt
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
argument_list|<
name|String
argument_list|>
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
name|String
name|xml
init|=
name|writer
operator|.
name|toString
argument_list|()
decl_stmt|;
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
name|List
argument_list|<
name|SolrDoc
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
name|List
argument_list|<
name|SolrDoc
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
name|SolrDoc
name|firstDoc
init|=
name|docs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|int
name|commitWithin
init|=
name|firstDoc
operator|.
name|commitWithin
operator|!=
operator|-
literal|1
condition|?
name|firstDoc
operator|.
name|commitWithin
else|:
name|this
operator|.
name|commitWithin
decl_stmt|;
name|boolean
name|overwrite
init|=
name|firstDoc
operator|.
name|overwrite
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
if|if
condition|(
name|documents
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|SolrDoc
name|doc
range|:
name|documents
control|)
block|{
if|if
condition|(
name|doc
operator|!=
literal|null
condition|)
block|{
name|ClientUtils
operator|.
name|writeXML
argument_list|(
name|doc
operator|.
name|document
argument_list|,
name|writer
argument_list|)
expr_stmt|;
block|}
block|}
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
name|writer
operator|.
name|append
argument_list|(
literal|"<delete>"
argument_list|)
expr_stmt|;
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
name|Long
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
name|Long
name|version
init|=
name|entry
operator|.
name|getValue
argument_list|()
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
DECL|method|getDocLists
specifier|private
name|List
argument_list|<
name|List
argument_list|<
name|SolrDoc
argument_list|>
argument_list|>
name|getDocLists
parameter_list|(
name|List
argument_list|<
name|SolrDoc
argument_list|>
name|documents
parameter_list|)
block|{
name|List
argument_list|<
name|List
argument_list|<
name|SolrDoc
argument_list|>
argument_list|>
name|docLists
init|=
operator|new
name|ArrayList
argument_list|<
name|List
argument_list|<
name|SolrDoc
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|documents
operator|==
literal|null
condition|)
block|{
return|return
name|docLists
return|;
block|}
name|boolean
name|lastOverwrite
init|=
literal|true
decl_stmt|;
name|int
name|lastCommitWithin
init|=
operator|-
literal|1
decl_stmt|;
name|List
argument_list|<
name|SolrDoc
argument_list|>
name|docList
init|=
literal|null
decl_stmt|;
for|for
control|(
name|SolrDoc
name|doc
range|:
name|this
operator|.
name|documents
control|)
block|{
if|if
condition|(
name|doc
operator|.
name|overwrite
operator|!=
name|lastOverwrite
operator|||
name|doc
operator|.
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
name|ArrayList
argument_list|<
name|SolrDoc
argument_list|>
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
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|lastCommitWithin
operator|=
name|doc
operator|.
name|commitWithin
expr_stmt|;
name|lastOverwrite
operator|=
name|doc
operator|.
name|overwrite
expr_stmt|;
block|}
return|return
name|docLists
return|;
block|}
DECL|method|getDeleteById
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|getDeleteById
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
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"UpdateRequestExt [documents="
operator|+
name|documents
operator|+
literal|", deleteById="
operator|+
name|deleteById
operator|+
literal|", deleteQuery="
operator|+
name|deleteQuery
operator|+
literal|"]"
return|;
block|}
block|}
end_class

end_unit

