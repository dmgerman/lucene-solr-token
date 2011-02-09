begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.store.instantiated
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|instantiated
package|;
end_package

begin_comment
comment|/**  * Copyright 2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|DocsAndPositionsEnum
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
name|Bits
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
name|BytesRef
import|;
end_import

begin_class
DECL|class|InstantiatedDocsAndPositionsEnum
specifier|public
class|class
name|InstantiatedDocsAndPositionsEnum
extends|extends
name|DocsAndPositionsEnum
block|{
DECL|field|upto
specifier|private
name|int
name|upto
decl_stmt|;
DECL|field|posUpto
specifier|private
name|int
name|posUpto
decl_stmt|;
DECL|field|skipDocs
specifier|private
name|Bits
name|skipDocs
decl_stmt|;
DECL|field|term
specifier|private
name|InstantiatedTerm
name|term
decl_stmt|;
DECL|field|currentDoc
specifier|protected
name|InstantiatedTermDocumentInformation
name|currentDoc
decl_stmt|;
DECL|field|payload
specifier|private
specifier|final
name|BytesRef
name|payload
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
DECL|method|reset
specifier|public
name|InstantiatedDocsAndPositionsEnum
name|reset
parameter_list|(
name|Bits
name|skipDocs
parameter_list|,
name|InstantiatedTerm
name|term
parameter_list|)
block|{
name|this
operator|.
name|skipDocs
operator|=
name|skipDocs
expr_stmt|;
name|this
operator|.
name|term
operator|=
name|term
expr_stmt|;
name|upto
operator|=
operator|-
literal|1
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|docID
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|currentDoc
operator|.
name|getDocument
argument_list|()
operator|.
name|getDocumentNumber
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
block|{
name|upto
operator|++
expr_stmt|;
if|if
condition|(
name|upto
operator|>=
name|term
operator|.
name|getAssociatedDocuments
argument_list|()
operator|.
name|length
condition|)
block|{
return|return
name|NO_MORE_DOCS
return|;
block|}
else|else
block|{
name|currentDoc
operator|=
name|term
operator|.
name|getAssociatedDocuments
argument_list|()
index|[
name|upto
index|]
expr_stmt|;
if|if
condition|(
name|skipDocs
operator|==
literal|null
operator|||
operator|!
name|skipDocs
operator|.
name|get
argument_list|(
name|currentDoc
operator|.
name|getDocument
argument_list|()
operator|.
name|getDocumentNumber
argument_list|()
argument_list|)
condition|)
block|{
name|posUpto
operator|=
operator|-
literal|1
expr_stmt|;
return|return
name|docID
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|nextDoc
argument_list|()
return|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|advance
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
block|{
if|if
condition|(
name|currentDoc
operator|.
name|getDocument
argument_list|()
operator|.
name|getDocumentNumber
argument_list|()
operator|>=
name|target
condition|)
block|{
return|return
name|nextDoc
argument_list|()
return|;
block|}
name|int
name|startOffset
init|=
name|upto
operator|>=
literal|0
condition|?
name|upto
else|:
literal|0
decl_stmt|;
name|upto
operator|=
name|term
operator|.
name|seekCeilingDocumentInformationIndex
argument_list|(
name|target
argument_list|,
name|startOffset
argument_list|)
expr_stmt|;
if|if
condition|(
name|upto
operator|==
operator|-
literal|1
condition|)
block|{
return|return
name|NO_MORE_DOCS
return|;
block|}
name|currentDoc
operator|=
name|term
operator|.
name|getAssociatedDocuments
argument_list|()
index|[
name|upto
index|]
expr_stmt|;
if|if
condition|(
name|skipDocs
operator|!=
literal|null
operator|&&
name|skipDocs
operator|.
name|get
argument_list|(
name|currentDoc
operator|.
name|getDocument
argument_list|()
operator|.
name|getDocumentNumber
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|nextDoc
argument_list|()
return|;
block|}
else|else
block|{
name|posUpto
operator|=
operator|-
literal|1
expr_stmt|;
return|return
name|docID
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|freq
specifier|public
name|int
name|freq
parameter_list|()
block|{
return|return
name|currentDoc
operator|.
name|getTermPositions
argument_list|()
operator|.
name|length
return|;
block|}
annotation|@
name|Override
DECL|method|nextPosition
specifier|public
name|int
name|nextPosition
parameter_list|()
block|{
return|return
name|currentDoc
operator|.
name|getTermPositions
argument_list|()
index|[
operator|++
name|posUpto
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|hasPayload
specifier|public
name|boolean
name|hasPayload
parameter_list|()
block|{
return|return
name|currentDoc
operator|.
name|getPayloads
argument_list|()
index|[
name|posUpto
index|]
operator|!=
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getPayload
specifier|public
name|BytesRef
name|getPayload
parameter_list|()
block|{
name|payload
operator|.
name|bytes
operator|=
name|currentDoc
operator|.
name|getPayloads
argument_list|()
index|[
name|posUpto
index|]
expr_stmt|;
name|payload
operator|.
name|length
operator|=
name|payload
operator|.
name|bytes
operator|.
name|length
expr_stmt|;
return|return
name|payload
return|;
block|}
block|}
end_class

end_unit

