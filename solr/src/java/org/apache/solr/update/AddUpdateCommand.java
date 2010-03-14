begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.update
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Document
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Field
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|Term
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
name|SolrInputField
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
name|schema
operator|.
name|IndexSchema
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
name|schema
operator|.
name|SchemaField
import|;
end_import

begin_comment
comment|/**  * @version $Id$  */
end_comment

begin_class
DECL|class|AddUpdateCommand
specifier|public
class|class
name|AddUpdateCommand
extends|extends
name|UpdateCommand
block|{
comment|// optional id in "internal" indexed form... if it is needed and not supplied,
comment|// it will be obtained from the doc.
DECL|field|indexedId
specifier|public
name|String
name|indexedId
decl_stmt|;
comment|// The Lucene document to be indexed
DECL|field|doc
specifier|public
name|Document
name|doc
decl_stmt|;
comment|// Higher level SolrInputDocument, normally used to construct the Lucene Document
comment|// to index.
DECL|field|solrDoc
specifier|public
name|SolrInputDocument
name|solrDoc
decl_stmt|;
DECL|field|allowDups
specifier|public
name|boolean
name|allowDups
decl_stmt|;
DECL|field|overwritePending
specifier|public
name|boolean
name|overwritePending
decl_stmt|;
DECL|field|overwriteCommitted
specifier|public
name|boolean
name|overwriteCommitted
decl_stmt|;
DECL|field|updateTerm
specifier|public
name|Term
name|updateTerm
decl_stmt|;
DECL|field|commitWithin
specifier|public
name|int
name|commitWithin
init|=
operator|-
literal|1
decl_stmt|;
comment|/** Reset state to reuse this object with a different document in the same request */
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|doc
operator|=
literal|null
expr_stmt|;
name|solrDoc
operator|=
literal|null
expr_stmt|;
name|indexedId
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|getSolrInputDocument
specifier|public
name|SolrInputDocument
name|getSolrInputDocument
parameter_list|()
block|{
return|return
name|solrDoc
return|;
block|}
DECL|method|getLuceneDocument
specifier|public
name|Document
name|getLuceneDocument
parameter_list|(
name|IndexSchema
name|schema
parameter_list|)
block|{
if|if
condition|(
name|doc
operator|==
literal|null
operator|&&
name|solrDoc
operator|!=
literal|null
condition|)
block|{
comment|// TODO??  build the doc from the SolrDocument?
block|}
return|return
name|doc
return|;
block|}
DECL|method|getIndexedId
specifier|public
name|String
name|getIndexedId
parameter_list|(
name|IndexSchema
name|schema
parameter_list|)
block|{
if|if
condition|(
name|indexedId
operator|==
literal|null
condition|)
block|{
name|SchemaField
name|sf
init|=
name|schema
operator|.
name|getUniqueKeyField
argument_list|()
decl_stmt|;
if|if
condition|(
name|sf
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|doc
operator|!=
literal|null
condition|)
block|{
name|schema
operator|.
name|getUniqueKeyField
argument_list|()
expr_stmt|;
name|Field
name|storedId
init|=
name|doc
operator|.
name|getField
argument_list|(
name|sf
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|indexedId
operator|=
name|sf
operator|.
name|getType
argument_list|()
operator|.
name|storedToIndexed
argument_list|(
name|storedId
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|solrDoc
operator|!=
literal|null
condition|)
block|{
name|SolrInputField
name|field
init|=
name|solrDoc
operator|.
name|getField
argument_list|(
name|sf
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|field
operator|!=
literal|null
condition|)
block|{
name|indexedId
operator|=
name|sf
operator|.
name|getType
argument_list|()
operator|.
name|toInternal
argument_list|(
name|field
operator|.
name|getFirstValue
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
name|indexedId
return|;
block|}
DECL|method|getPrintableId
specifier|public
name|String
name|getPrintableId
parameter_list|(
name|IndexSchema
name|schema
parameter_list|)
block|{
name|SchemaField
name|sf
init|=
name|schema
operator|.
name|getUniqueKeyField
argument_list|()
decl_stmt|;
if|if
condition|(
name|indexedId
operator|!=
literal|null
condition|)
block|{
return|return
name|schema
operator|.
name|getUniqueKeyField
argument_list|()
operator|.
name|getType
argument_list|()
operator|.
name|indexedToReadable
argument_list|(
name|indexedId
argument_list|)
return|;
block|}
if|if
condition|(
name|doc
operator|!=
literal|null
condition|)
block|{
return|return
name|schema
operator|.
name|printableUniqueKey
argument_list|(
name|doc
argument_list|)
return|;
block|}
if|if
condition|(
name|solrDoc
operator|!=
literal|null
condition|)
block|{
name|SolrInputField
name|field
init|=
name|solrDoc
operator|.
name|getField
argument_list|(
name|sf
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|field
operator|!=
literal|null
condition|)
block|{
return|return
name|field
operator|.
name|getFirstValue
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
return|return
literal|"(null)"
return|;
block|}
DECL|method|AddUpdateCommand
specifier|public
name|AddUpdateCommand
parameter_list|()
block|{
name|super
argument_list|(
literal|"add"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
name|commandName
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
if|if
condition|(
name|indexedId
operator|!=
literal|null
condition|)
name|sb
operator|.
name|append
argument_list|(
literal|"id="
argument_list|)
operator|.
name|append
argument_list|(
name|indexedId
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|",allowDups="
argument_list|)
operator|.
name|append
argument_list|(
name|allowDups
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|",overwritePending="
argument_list|)
operator|.
name|append
argument_list|(
name|overwritePending
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|",overwriteCommitted="
argument_list|)
operator|.
name|append
argument_list|(
name|overwriteCommitted
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

