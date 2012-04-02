begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.update.processor
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
operator|.
name|processor
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|List
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
name|SolrException
operator|.
name|ErrorCode
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
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|StrUtils
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
name|update
operator|.
name|AddUpdateCommand
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
name|schema
operator|.
name|SchemaField
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
name|plugin
operator|.
name|SolrCoreAware
import|;
end_import

begin_class
DECL|class|SignatureUpdateProcessorFactory
specifier|public
class|class
name|SignatureUpdateProcessorFactory
extends|extends
name|UpdateRequestProcessorFactory
implements|implements
name|SolrCoreAware
block|{
DECL|field|sigFields
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|sigFields
decl_stmt|;
DECL|field|signatureField
specifier|private
name|String
name|signatureField
decl_stmt|;
DECL|field|signatureTerm
specifier|private
name|Term
name|signatureTerm
decl_stmt|;
DECL|field|enabled
specifier|private
name|boolean
name|enabled
init|=
literal|true
decl_stmt|;
DECL|field|signatureClass
specifier|private
name|String
name|signatureClass
decl_stmt|;
DECL|field|overwriteDupes
specifier|private
name|boolean
name|overwriteDupes
decl_stmt|;
DECL|field|params
specifier|private
name|SolrParams
name|params
decl_stmt|;
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
specifier|final
name|NamedList
name|args
parameter_list|)
block|{
if|if
condition|(
name|args
operator|!=
literal|null
condition|)
block|{
name|SolrParams
name|params
init|=
name|SolrParams
operator|.
name|toSolrParams
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|boolean
name|enabled
init|=
name|params
operator|.
name|getBool
argument_list|(
literal|"enabled"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|this
operator|.
name|enabled
operator|=
name|enabled
expr_stmt|;
name|overwriteDupes
operator|=
name|params
operator|.
name|getBool
argument_list|(
literal|"overwriteDupes"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|signatureField
operator|=
name|params
operator|.
name|get
argument_list|(
literal|"signatureField"
argument_list|,
literal|"signatureField"
argument_list|)
expr_stmt|;
name|signatureClass
operator|=
name|params
operator|.
name|get
argument_list|(
literal|"signatureClass"
argument_list|,
literal|"org.apache.solr.update.processor.Lookup3Signature"
argument_list|)
expr_stmt|;
name|this
operator|.
name|params
operator|=
name|params
expr_stmt|;
name|Object
name|fields
init|=
name|args
operator|.
name|get
argument_list|(
literal|"fields"
argument_list|)
decl_stmt|;
name|sigFields
operator|=
name|fields
operator|==
literal|null
condition|?
literal|null
else|:
name|StrUtils
operator|.
name|splitSmart
argument_list|(
operator|(
name|String
operator|)
name|fields
argument_list|,
literal|","
argument_list|,
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|sigFields
operator|!=
literal|null
condition|)
block|{
name|Collections
operator|.
name|sort
argument_list|(
name|sigFields
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|inform
specifier|public
name|void
name|inform
parameter_list|(
name|SolrCore
name|core
parameter_list|)
block|{
specifier|final
name|SchemaField
name|field
init|=
name|core
operator|.
name|getSchema
argument_list|()
operator|.
name|getFieldOrNull
argument_list|(
name|getSignatureField
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|field
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Can't use signatureField which does not exist in schema: "
operator|+
name|getSignatureField
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|getOverwriteDupes
argument_list|()
operator|&&
operator|(
operator|!
name|field
operator|.
name|indexed
argument_list|()
operator|)
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Can't set overwriteDupes when signatureField is not indexed: "
operator|+
name|getSignatureField
argument_list|()
argument_list|)
throw|;
block|}
block|}
DECL|method|getSigFields
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getSigFields
parameter_list|()
block|{
return|return
name|sigFields
return|;
block|}
DECL|method|getSignatureField
specifier|public
name|String
name|getSignatureField
parameter_list|()
block|{
return|return
name|signatureField
return|;
block|}
DECL|method|isEnabled
specifier|public
name|boolean
name|isEnabled
parameter_list|()
block|{
return|return
name|enabled
return|;
block|}
DECL|method|getSignatureClass
specifier|public
name|String
name|getSignatureClass
parameter_list|()
block|{
return|return
name|signatureClass
return|;
block|}
DECL|method|getOverwriteDupes
specifier|public
name|boolean
name|getOverwriteDupes
parameter_list|()
block|{
return|return
name|overwriteDupes
return|;
block|}
annotation|@
name|Override
DECL|method|getInstance
specifier|public
name|UpdateRequestProcessor
name|getInstance
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|,
name|UpdateRequestProcessor
name|next
parameter_list|)
block|{
return|return
operator|new
name|SignatureUpdateProcessor
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|,
name|this
argument_list|,
name|next
argument_list|)
return|;
block|}
DECL|class|SignatureUpdateProcessor
class|class
name|SignatureUpdateProcessor
extends|extends
name|UpdateRequestProcessor
block|{
DECL|field|req
specifier|private
specifier|final
name|SolrQueryRequest
name|req
decl_stmt|;
DECL|method|SignatureUpdateProcessor
specifier|public
name|SignatureUpdateProcessor
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|,
name|SignatureUpdateProcessorFactory
name|factory
parameter_list|,
name|UpdateRequestProcessor
name|next
parameter_list|)
block|{
name|super
argument_list|(
name|next
argument_list|)
expr_stmt|;
name|this
operator|.
name|req
operator|=
name|req
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|processAdd
specifier|public
name|void
name|processAdd
parameter_list|(
name|AddUpdateCommand
name|cmd
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|enabled
condition|)
block|{
name|SolrInputDocument
name|doc
init|=
name|cmd
operator|.
name|getSolrInputDocument
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|currDocSigFields
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|sigFields
operator|==
literal|null
operator|||
name|sigFields
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|Collection
argument_list|<
name|String
argument_list|>
name|docFields
init|=
name|doc
operator|.
name|getFieldNames
argument_list|()
decl_stmt|;
name|currDocSigFields
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|docFields
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|currDocSigFields
operator|.
name|addAll
argument_list|(
name|docFields
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|currDocSigFields
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|currDocSigFields
operator|=
name|sigFields
expr_stmt|;
block|}
name|Signature
name|sig
init|=
operator|(
name|Signature
operator|)
name|req
operator|.
name|getCore
argument_list|()
operator|.
name|getResourceLoader
argument_list|()
operator|.
name|newInstance
argument_list|(
name|signatureClass
argument_list|)
decl_stmt|;
name|sig
operator|.
name|init
argument_list|(
name|params
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|field
range|:
name|currDocSigFields
control|)
block|{
name|SolrInputField
name|f
init|=
name|doc
operator|.
name|getField
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|f
operator|!=
literal|null
condition|)
block|{
name|sig
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|Object
name|o
init|=
name|f
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|o
operator|instanceof
name|Collection
condition|)
block|{
for|for
control|(
name|Object
name|oo
range|:
operator|(
name|Collection
operator|)
name|o
control|)
block|{
name|sig
operator|.
name|add
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|oo
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|sig
operator|.
name|add
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|o
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|byte
index|[]
name|signature
init|=
name|sig
operator|.
name|getSignature
argument_list|()
decl_stmt|;
name|char
index|[]
name|arr
init|=
operator|new
name|char
index|[
name|signature
operator|.
name|length
operator|<<
literal|1
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
name|signature
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|b
init|=
name|signature
index|[
name|i
index|]
decl_stmt|;
name|int
name|idx
init|=
name|i
operator|<<
literal|1
decl_stmt|;
name|arr
index|[
name|idx
index|]
operator|=
name|StrUtils
operator|.
name|HEX_DIGITS
index|[
operator|(
name|b
operator|>>
literal|4
operator|)
operator|&
literal|0xf
index|]
expr_stmt|;
name|arr
index|[
name|idx
operator|+
literal|1
index|]
operator|=
name|StrUtils
operator|.
name|HEX_DIGITS
index|[
name|b
operator|&
literal|0xf
index|]
expr_stmt|;
block|}
name|String
name|sigString
init|=
operator|new
name|String
argument_list|(
name|arr
argument_list|)
decl_stmt|;
name|doc
operator|.
name|addField
argument_list|(
name|signatureField
argument_list|,
name|sigString
argument_list|)
expr_stmt|;
if|if
condition|(
name|overwriteDupes
condition|)
block|{
name|cmd
operator|.
name|updateTerm
operator|=
operator|new
name|Term
argument_list|(
name|signatureField
argument_list|,
name|sigString
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|next
operator|!=
literal|null
condition|)
name|next
operator|.
name|processAdd
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
block|}
comment|// for testing
DECL|method|setEnabled
name|void
name|setEnabled
parameter_list|(
name|boolean
name|enabled
parameter_list|)
block|{
name|this
operator|.
name|enabled
operator|=
name|enabled
expr_stmt|;
block|}
block|}
end_class

end_unit

