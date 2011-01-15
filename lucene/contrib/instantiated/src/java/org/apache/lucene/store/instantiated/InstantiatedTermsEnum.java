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
name|OrdTermState
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
name|lucene
operator|.
name|index
operator|.
name|TermState
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
name|TermsEnum
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
name|DocsEnum
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
name|DocsAndPositionsEnum
import|;
end_import

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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import

begin_class
DECL|class|InstantiatedTermsEnum
specifier|public
class|class
name|InstantiatedTermsEnum
extends|extends
name|TermsEnum
block|{
DECL|field|field
specifier|private
specifier|final
name|String
name|field
decl_stmt|;
DECL|field|terms
specifier|private
specifier|final
name|InstantiatedTerm
index|[]
name|terms
decl_stmt|;
DECL|field|br
specifier|private
specifier|final
name|BytesRef
name|br
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
DECL|field|start
specifier|private
specifier|final
name|int
name|start
decl_stmt|;
DECL|field|upto
specifier|private
name|int
name|upto
decl_stmt|;
DECL|method|InstantiatedTermsEnum
specifier|public
name|InstantiatedTermsEnum
parameter_list|(
name|InstantiatedTerm
index|[]
name|terms
parameter_list|,
name|int
name|start
parameter_list|,
name|String
name|field
parameter_list|)
block|{
name|this
operator|.
name|start
operator|=
name|start
expr_stmt|;
name|upto
operator|=
name|start
operator|-
literal|1
expr_stmt|;
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
name|this
operator|.
name|terms
operator|=
name|terms
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|seek
specifier|public
name|SeekStatus
name|seek
parameter_list|(
name|BytesRef
name|text
parameter_list|,
name|boolean
name|useCache
parameter_list|)
block|{
specifier|final
name|Term
name|t
init|=
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|text
argument_list|)
decl_stmt|;
name|int
name|loc
init|=
name|Arrays
operator|.
name|binarySearch
argument_list|(
name|terms
argument_list|,
name|t
argument_list|,
name|InstantiatedTerm
operator|.
name|termComparator
argument_list|)
decl_stmt|;
if|if
condition|(
name|loc
operator|<
literal|0
condition|)
block|{
name|upto
operator|=
operator|-
name|loc
operator|-
literal|1
expr_stmt|;
if|if
condition|(
name|upto
operator|>=
name|terms
operator|.
name|length
condition|)
block|{
return|return
name|SeekStatus
operator|.
name|END
return|;
block|}
else|else
block|{
name|br
operator|.
name|copy
argument_list|(
name|terms
index|[
name|upto
index|]
operator|.
name|getTerm
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|SeekStatus
operator|.
name|NOT_FOUND
return|;
block|}
block|}
else|else
block|{
name|upto
operator|=
name|loc
expr_stmt|;
name|br
operator|.
name|copy
argument_list|(
name|text
argument_list|)
expr_stmt|;
return|return
name|SeekStatus
operator|.
name|FOUND
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|seek
specifier|public
name|SeekStatus
name|seek
parameter_list|(
name|long
name|ord
parameter_list|)
block|{
name|upto
operator|=
name|start
operator|+
operator|(
name|int
operator|)
name|ord
expr_stmt|;
if|if
condition|(
name|upto
operator|>=
name|terms
operator|.
name|length
condition|)
block|{
return|return
name|SeekStatus
operator|.
name|END
return|;
block|}
if|if
condition|(
name|terms
index|[
name|upto
index|]
operator|.
name|field
argument_list|()
operator|==
name|field
condition|)
block|{
return|return
name|SeekStatus
operator|.
name|FOUND
return|;
block|}
else|else
block|{
comment|// make sure field was interned
assert|assert
operator|!
name|terms
index|[
name|upto
index|]
operator|.
name|field
argument_list|()
operator|.
name|equals
argument_list|(
name|field
argument_list|)
assert|;
return|return
name|SeekStatus
operator|.
name|END
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|next
specifier|public
name|BytesRef
name|next
parameter_list|()
block|{
name|upto
operator|++
expr_stmt|;
if|if
condition|(
name|upto
operator|>=
name|terms
operator|.
name|length
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|terms
index|[
name|upto
index|]
operator|.
name|field
argument_list|()
operator|==
name|field
condition|)
block|{
name|br
operator|.
name|copy
argument_list|(
name|terms
index|[
name|upto
index|]
operator|.
name|getTerm
argument_list|()
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|br
return|;
block|}
else|else
block|{
comment|// make sure field was interned
assert|assert
operator|!
name|terms
index|[
name|upto
index|]
operator|.
name|field
argument_list|()
operator|.
name|equals
argument_list|(
name|field
argument_list|)
assert|;
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|term
specifier|public
name|BytesRef
name|term
parameter_list|()
block|{
return|return
name|br
return|;
block|}
annotation|@
name|Override
DECL|method|ord
specifier|public
name|long
name|ord
parameter_list|()
block|{
return|return
name|upto
operator|-
name|start
return|;
block|}
annotation|@
name|Override
DECL|method|docFreq
specifier|public
name|int
name|docFreq
parameter_list|()
block|{
return|return
name|terms
index|[
name|upto
index|]
operator|.
name|getAssociatedDocuments
argument_list|()
operator|.
name|length
return|;
block|}
annotation|@
name|Override
DECL|method|totalTermFreq
specifier|public
name|long
name|totalTermFreq
parameter_list|()
block|{
specifier|final
name|long
name|v
init|=
name|terms
index|[
name|upto
index|]
operator|.
name|getTotalTermFreq
argument_list|()
decl_stmt|;
return|return
name|v
operator|==
literal|0
condition|?
operator|-
literal|1
else|:
name|v
return|;
block|}
annotation|@
name|Override
DECL|method|docs
specifier|public
name|DocsEnum
name|docs
parameter_list|(
name|Bits
name|skipDocs
parameter_list|,
name|DocsEnum
name|reuse
parameter_list|)
block|{
if|if
condition|(
name|reuse
operator|==
literal|null
operator|||
operator|!
operator|(
name|reuse
operator|instanceof
name|InstantiatedDocsEnum
operator|)
condition|)
block|{
name|reuse
operator|=
operator|new
name|InstantiatedDocsEnum
argument_list|()
expr_stmt|;
block|}
return|return
operator|(
operator|(
name|InstantiatedDocsEnum
operator|)
name|reuse
operator|)
operator|.
name|reset
argument_list|(
name|skipDocs
argument_list|,
name|terms
index|[
name|upto
index|]
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|docsAndPositions
specifier|public
name|DocsAndPositionsEnum
name|docsAndPositions
parameter_list|(
name|Bits
name|skipDocs
parameter_list|,
name|DocsAndPositionsEnum
name|reuse
parameter_list|)
block|{
if|if
condition|(
name|reuse
operator|==
literal|null
operator|||
operator|!
operator|(
name|reuse
operator|instanceof
name|InstantiatedDocsAndPositionsEnum
operator|)
condition|)
block|{
name|reuse
operator|=
operator|new
name|InstantiatedDocsAndPositionsEnum
argument_list|()
expr_stmt|;
block|}
return|return
operator|(
operator|(
name|InstantiatedDocsAndPositionsEnum
operator|)
name|reuse
operator|)
operator|.
name|reset
argument_list|(
name|skipDocs
argument_list|,
name|terms
index|[
name|upto
index|]
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getComparator
specifier|public
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|getComparator
parameter_list|()
block|{
return|return
name|BytesRef
operator|.
name|getUTF8SortedAsUnicodeComparator
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|termState
specifier|public
name|TermState
name|termState
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|OrdTermState
name|state
init|=
operator|new
name|OrdTermState
argument_list|()
decl_stmt|;
name|state
operator|.
name|ord
operator|=
name|upto
operator|-
name|start
expr_stmt|;
return|return
name|state
return|;
block|}
annotation|@
name|Override
DECL|method|seek
specifier|public
name|SeekStatus
name|seek
parameter_list|(
name|BytesRef
name|term
parameter_list|,
name|TermState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|state
operator|!=
literal|null
operator|&&
name|state
operator|instanceof
name|OrdTermState
assert|;
return|return
name|seek
argument_list|(
operator|(
operator|(
name|OrdTermState
operator|)
name|state
operator|)
operator|.
name|ord
argument_list|)
return|;
comment|// just use the ord for simplicity
block|}
block|}
end_class

end_unit

