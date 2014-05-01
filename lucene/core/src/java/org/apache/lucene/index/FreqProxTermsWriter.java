begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
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
name|lucene
operator|.
name|codecs
operator|.
name|FieldsConsumer
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
name|util
operator|.
name|CollectionUtil
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
name|util
operator|.
name|IOUtils
import|;
end_import

begin_class
DECL|class|FreqProxTermsWriter
specifier|final
class|class
name|FreqProxTermsWriter
extends|extends
name|TermsHash
block|{
DECL|method|FreqProxTermsWriter
specifier|public
name|FreqProxTermsWriter
parameter_list|(
name|DocumentsWriterPerThread
name|docWriter
parameter_list|,
name|TermsHash
name|termVectors
parameter_list|)
block|{
name|super
argument_list|(
name|docWriter
argument_list|,
literal|true
argument_list|,
name|termVectors
argument_list|)
expr_stmt|;
block|}
DECL|method|applyDeletes
specifier|private
name|void
name|applyDeletes
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|,
name|Fields
name|fields
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Process any pending Term deletes for this newly
comment|// flushed segment:
if|if
condition|(
name|state
operator|.
name|segUpdates
operator|!=
literal|null
operator|&&
name|state
operator|.
name|segUpdates
operator|.
name|terms
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|Map
argument_list|<
name|Term
argument_list|,
name|Integer
argument_list|>
name|segDeletes
init|=
name|state
operator|.
name|segUpdates
operator|.
name|terms
decl_stmt|;
name|List
argument_list|<
name|Term
argument_list|>
name|deleteTerms
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|segDeletes
operator|.
name|keySet
argument_list|()
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|deleteTerms
argument_list|)
expr_stmt|;
name|String
name|lastField
init|=
literal|null
decl_stmt|;
name|TermsEnum
name|termsEnum
init|=
literal|null
decl_stmt|;
name|DocsEnum
name|docsEnum
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Term
name|deleteTerm
range|:
name|deleteTerms
control|)
block|{
if|if
condition|(
name|deleteTerm
operator|.
name|field
argument_list|()
operator|.
name|equals
argument_list|(
name|lastField
argument_list|)
operator|==
literal|false
condition|)
block|{
name|lastField
operator|=
name|deleteTerm
operator|.
name|field
argument_list|()
expr_stmt|;
name|Terms
name|terms
init|=
name|fields
operator|.
name|terms
argument_list|(
name|lastField
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|!=
literal|null
condition|)
block|{
name|termsEnum
operator|=
name|terms
operator|.
name|iterator
argument_list|(
name|termsEnum
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|termsEnum
operator|=
literal|null
expr_stmt|;
block|}
block|}
if|if
condition|(
name|termsEnum
operator|!=
literal|null
operator|&&
name|termsEnum
operator|.
name|seekExact
argument_list|(
name|deleteTerm
operator|.
name|bytes
argument_list|()
argument_list|)
condition|)
block|{
name|docsEnum
operator|=
name|termsEnum
operator|.
name|docs
argument_list|(
literal|null
argument_list|,
name|docsEnum
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|int
name|delDocLimit
init|=
name|segDeletes
operator|.
name|get
argument_list|(
name|deleteTerm
argument_list|)
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|doc
init|=
name|docsEnum
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
if|if
condition|(
name|doc
operator|==
name|DocsEnum
operator|.
name|NO_MORE_DOCS
condition|)
block|{
break|break;
block|}
if|if
condition|(
name|doc
operator|<
name|delDocLimit
condition|)
block|{
if|if
condition|(
name|state
operator|.
name|liveDocs
operator|==
literal|null
condition|)
block|{
name|state
operator|.
name|liveDocs
operator|=
name|state
operator|.
name|segmentInfo
operator|.
name|getCodec
argument_list|()
operator|.
name|liveDocsFormat
argument_list|()
operator|.
name|newLiveDocs
argument_list|(
name|state
operator|.
name|segmentInfo
operator|.
name|getDocCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|state
operator|.
name|liveDocs
operator|.
name|get
argument_list|(
name|doc
argument_list|)
condition|)
block|{
name|state
operator|.
name|delCountOnFlush
operator|++
expr_stmt|;
name|state
operator|.
name|liveDocs
operator|.
name|clear
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
break|break;
block|}
block|}
block|}
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|flush
specifier|public
name|void
name|flush
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|TermsHashPerField
argument_list|>
name|fieldsToFlush
parameter_list|,
specifier|final
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|flush
argument_list|(
name|fieldsToFlush
argument_list|,
name|state
argument_list|)
expr_stmt|;
comment|// Gather all fields that saw any postings:
name|List
argument_list|<
name|FreqProxTermsWriterPerField
argument_list|>
name|allFields
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|TermsHashPerField
name|f
range|:
name|fieldsToFlush
operator|.
name|values
argument_list|()
control|)
block|{
specifier|final
name|FreqProxTermsWriterPerField
name|perField
init|=
operator|(
name|FreqProxTermsWriterPerField
operator|)
name|f
decl_stmt|;
if|if
condition|(
name|perField
operator|.
name|bytesHash
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|perField
operator|.
name|sortPostings
argument_list|()
expr_stmt|;
assert|assert
name|perField
operator|.
name|fieldInfo
operator|.
name|isIndexed
argument_list|()
assert|;
name|allFields
operator|.
name|add
argument_list|(
name|perField
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Sort by field name
name|CollectionUtil
operator|.
name|introSort
argument_list|(
name|allFields
argument_list|)
expr_stmt|;
name|Fields
name|fields
init|=
operator|new
name|FreqProxFields
argument_list|(
name|allFields
argument_list|)
decl_stmt|;
name|applyDeletes
argument_list|(
name|state
argument_list|,
name|fields
argument_list|)
expr_stmt|;
name|FieldsConsumer
name|consumer
init|=
name|state
operator|.
name|segmentInfo
operator|.
name|getCodec
argument_list|()
operator|.
name|postingsFormat
argument_list|()
operator|.
name|fieldsConsumer
argument_list|(
name|state
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|consumer
operator|.
name|write
argument_list|(
name|fields
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|success
condition|)
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|consumer
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|consumer
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|addField
specifier|public
name|TermsHashPerField
name|addField
parameter_list|(
name|FieldInvertState
name|invertState
parameter_list|,
name|FieldInfo
name|fieldInfo
parameter_list|)
block|{
return|return
operator|new
name|FreqProxTermsWriterPerField
argument_list|(
name|invertState
argument_list|,
name|this
argument_list|,
name|fieldInfo
argument_list|,
name|nextTermsHash
operator|.
name|addField
argument_list|(
name|invertState
argument_list|,
name|fieldInfo
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

