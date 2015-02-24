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
name|Iterator
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
name|index
operator|.
name|FreqProxTermsWriterPerField
operator|.
name|FreqProxPostingsArray
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
name|AttributeSource
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
name|BytesRefBuilder
import|;
end_import

begin_comment
comment|/** Implements limited (iterators only, no stats) {@link  *  Fields} interface over the in-RAM buffered  *  fields/terms/postings, to flush postings through the  *  PostingsFormat. */
end_comment

begin_class
DECL|class|FreqProxFields
class|class
name|FreqProxFields
extends|extends
name|Fields
block|{
DECL|field|fields
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|FreqProxTermsWriterPerField
argument_list|>
name|fields
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|FreqProxFields
specifier|public
name|FreqProxFields
parameter_list|(
name|List
argument_list|<
name|FreqProxTermsWriterPerField
argument_list|>
name|fieldList
parameter_list|)
block|{
comment|// NOTE: fields are already sorted by field name
for|for
control|(
name|FreqProxTermsWriterPerField
name|field
range|:
name|fieldList
control|)
block|{
name|fields
operator|.
name|put
argument_list|(
name|field
operator|.
name|fieldInfo
operator|.
name|name
argument_list|,
name|field
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|iterator
specifier|public
name|Iterator
argument_list|<
name|String
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|fields
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|terms
specifier|public
name|Terms
name|terms
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|FreqProxTermsWriterPerField
name|perField
init|=
name|fields
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
return|return
name|perField
operator|==
literal|null
condition|?
literal|null
else|:
operator|new
name|FreqProxTerms
argument_list|(
name|perField
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
comment|//return fields.size();
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
DECL|class|FreqProxTerms
specifier|private
specifier|static
class|class
name|FreqProxTerms
extends|extends
name|Terms
block|{
DECL|field|terms
specifier|final
name|FreqProxTermsWriterPerField
name|terms
decl_stmt|;
DECL|method|FreqProxTerms
specifier|public
name|FreqProxTerms
parameter_list|(
name|FreqProxTermsWriterPerField
name|terms
parameter_list|)
block|{
name|this
operator|.
name|terms
operator|=
name|terms
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|TermsEnum
name|iterator
parameter_list|(
name|TermsEnum
name|reuse
parameter_list|)
block|{
name|FreqProxTermsEnum
name|termsEnum
decl_stmt|;
if|if
condition|(
name|reuse
operator|instanceof
name|FreqProxTermsEnum
operator|&&
operator|(
operator|(
name|FreqProxTermsEnum
operator|)
name|reuse
operator|)
operator|.
name|terms
operator|==
name|this
operator|.
name|terms
condition|)
block|{
name|termsEnum
operator|=
operator|(
name|FreqProxTermsEnum
operator|)
name|reuse
expr_stmt|;
block|}
else|else
block|{
name|termsEnum
operator|=
operator|new
name|FreqProxTermsEnum
argument_list|(
name|terms
argument_list|)
expr_stmt|;
block|}
name|termsEnum
operator|.
name|reset
argument_list|()
expr_stmt|;
return|return
name|termsEnum
return|;
block|}
annotation|@
name|Override
DECL|method|size
specifier|public
name|long
name|size
parameter_list|()
block|{
comment|//return terms.termsHashPerField.bytesHash.size();
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|getSumTotalTermFreq
specifier|public
name|long
name|getSumTotalTermFreq
parameter_list|()
block|{
comment|//return terms.sumTotalTermFreq;
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|getSumDocFreq
specifier|public
name|long
name|getSumDocFreq
parameter_list|()
block|{
comment|//return terms.sumDocFreq;
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|getDocCount
specifier|public
name|int
name|getDocCount
parameter_list|()
block|{
comment|//return terms.docCount;
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|hasFreqs
specifier|public
name|boolean
name|hasFreqs
parameter_list|()
block|{
return|return
name|terms
operator|.
name|fieldInfo
operator|.
name|getIndexOptions
argument_list|()
operator|.
name|compareTo
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS
argument_list|)
operator|>=
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|hasOffsets
specifier|public
name|boolean
name|hasOffsets
parameter_list|()
block|{
comment|// NOTE: the in-memory buffer may have indexed offsets
comment|// because that's what FieldInfo said when we started,
comment|// but during indexing this may have been downgraded:
return|return
name|terms
operator|.
name|fieldInfo
operator|.
name|getIndexOptions
argument_list|()
operator|.
name|compareTo
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS
argument_list|)
operator|>=
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|hasPositions
specifier|public
name|boolean
name|hasPositions
parameter_list|()
block|{
comment|// NOTE: the in-memory buffer may have indexed positions
comment|// because that's what FieldInfo said when we started,
comment|// but during indexing this may have been downgraded:
return|return
name|terms
operator|.
name|fieldInfo
operator|.
name|getIndexOptions
argument_list|()
operator|.
name|compareTo
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
argument_list|)
operator|>=
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|hasPayloads
specifier|public
name|boolean
name|hasPayloads
parameter_list|()
block|{
return|return
name|terms
operator|.
name|sawPayloads
return|;
block|}
block|}
DECL|class|FreqProxTermsEnum
specifier|private
specifier|static
class|class
name|FreqProxTermsEnum
extends|extends
name|TermsEnum
block|{
DECL|field|terms
specifier|final
name|FreqProxTermsWriterPerField
name|terms
decl_stmt|;
DECL|field|sortedTermIDs
specifier|final
name|int
index|[]
name|sortedTermIDs
decl_stmt|;
DECL|field|postingsArray
specifier|final
name|FreqProxPostingsArray
name|postingsArray
decl_stmt|;
DECL|field|scratch
specifier|final
name|BytesRef
name|scratch
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
DECL|field|numTerms
specifier|final
name|int
name|numTerms
decl_stmt|;
DECL|field|ord
name|int
name|ord
decl_stmt|;
DECL|method|FreqProxTermsEnum
specifier|public
name|FreqProxTermsEnum
parameter_list|(
name|FreqProxTermsWriterPerField
name|terms
parameter_list|)
block|{
name|this
operator|.
name|terms
operator|=
name|terms
expr_stmt|;
name|this
operator|.
name|numTerms
operator|=
name|terms
operator|.
name|bytesHash
operator|.
name|size
argument_list|()
expr_stmt|;
name|sortedTermIDs
operator|=
name|terms
operator|.
name|sortedTermIDs
expr_stmt|;
assert|assert
name|sortedTermIDs
operator|!=
literal|null
assert|;
name|postingsArray
operator|=
operator|(
name|FreqProxPostingsArray
operator|)
name|terms
operator|.
name|postingsArray
expr_stmt|;
block|}
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|ord
operator|=
operator|-
literal|1
expr_stmt|;
block|}
DECL|method|seekCeil
specifier|public
name|SeekStatus
name|seekCeil
parameter_list|(
name|BytesRef
name|text
parameter_list|)
block|{
comment|// TODO: we could instead keep the BytesRefHash
comment|// intact so this is a hash lookup
comment|// binary search:
name|int
name|lo
init|=
literal|0
decl_stmt|;
name|int
name|hi
init|=
name|numTerms
operator|-
literal|1
decl_stmt|;
while|while
condition|(
name|hi
operator|>=
name|lo
condition|)
block|{
name|int
name|mid
init|=
operator|(
name|lo
operator|+
name|hi
operator|)
operator|>>>
literal|1
decl_stmt|;
name|int
name|textStart
init|=
name|postingsArray
operator|.
name|textStarts
index|[
name|sortedTermIDs
index|[
name|mid
index|]
index|]
decl_stmt|;
name|terms
operator|.
name|bytePool
operator|.
name|setBytesRef
argument_list|(
name|scratch
argument_list|,
name|textStart
argument_list|)
expr_stmt|;
name|int
name|cmp
init|=
name|scratch
operator|.
name|compareTo
argument_list|(
name|text
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmp
operator|<
literal|0
condition|)
block|{
name|lo
operator|=
name|mid
operator|+
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|cmp
operator|>
literal|0
condition|)
block|{
name|hi
operator|=
name|mid
operator|-
literal|1
expr_stmt|;
block|}
else|else
block|{
comment|// found:
name|ord
operator|=
name|mid
expr_stmt|;
return|return
name|SeekStatus
operator|.
name|FOUND
return|;
block|}
block|}
comment|// not found:
name|ord
operator|=
name|lo
operator|+
literal|1
expr_stmt|;
if|if
condition|(
name|ord
operator|>=
name|numTerms
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
name|int
name|textStart
init|=
name|postingsArray
operator|.
name|textStarts
index|[
name|sortedTermIDs
index|[
name|ord
index|]
index|]
decl_stmt|;
name|terms
operator|.
name|bytePool
operator|.
name|setBytesRef
argument_list|(
name|scratch
argument_list|,
name|textStart
argument_list|)
expr_stmt|;
return|return
name|SeekStatus
operator|.
name|NOT_FOUND
return|;
block|}
block|}
DECL|method|seekExact
specifier|public
name|void
name|seekExact
parameter_list|(
name|long
name|ord
parameter_list|)
block|{
name|this
operator|.
name|ord
operator|=
operator|(
name|int
operator|)
name|ord
expr_stmt|;
name|int
name|textStart
init|=
name|postingsArray
operator|.
name|textStarts
index|[
name|sortedTermIDs
index|[
name|this
operator|.
name|ord
index|]
index|]
decl_stmt|;
name|terms
operator|.
name|bytePool
operator|.
name|setBytesRef
argument_list|(
name|scratch
argument_list|,
name|textStart
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|next
specifier|public
name|BytesRef
name|next
parameter_list|()
block|{
name|ord
operator|++
expr_stmt|;
if|if
condition|(
name|ord
operator|>=
name|numTerms
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
name|int
name|textStart
init|=
name|postingsArray
operator|.
name|textStarts
index|[
name|sortedTermIDs
index|[
name|ord
index|]
index|]
decl_stmt|;
name|terms
operator|.
name|bytePool
operator|.
name|setBytesRef
argument_list|(
name|scratch
argument_list|,
name|textStart
argument_list|)
expr_stmt|;
return|return
name|scratch
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
name|scratch
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
name|ord
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
comment|// We do not store this per-term, and we cannot
comment|// implement this at merge time w/o an added pass
comment|// through the postings:
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|totalTermFreq
specifier|public
name|long
name|totalTermFreq
parameter_list|()
block|{
comment|// We do not store this per-term, and we cannot
comment|// implement this at merge time w/o an added pass
comment|// through the postings:
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|postings
specifier|public
name|PostingsEnum
name|postings
parameter_list|(
name|Bits
name|liveDocs
parameter_list|,
name|PostingsEnum
name|reuse
parameter_list|,
name|int
name|flags
parameter_list|)
block|{
if|if
condition|(
name|liveDocs
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"liveDocs must be null"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|(
name|flags
operator|&
name|PostingsEnum
operator|.
name|POSITIONS
operator|)
operator|>=
name|PostingsEnum
operator|.
name|POSITIONS
condition|)
block|{
name|FreqProxPostingsEnum
name|posEnum
decl_stmt|;
if|if
condition|(
operator|!
name|terms
operator|.
name|hasProx
condition|)
block|{
comment|// Caller wants positions but we didn't index them;
comment|// don't lie:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"did not index positions"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|terms
operator|.
name|hasOffsets
operator|&&
name|PostingsEnum
operator|.
name|featureRequested
argument_list|(
name|flags
argument_list|,
name|PostingsEnum
operator|.
name|OFFSETS
argument_list|)
condition|)
block|{
comment|// Caller wants offsets but we didn't index them;
comment|// don't lie:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"did not index offsets"
argument_list|)
throw|;
block|}
if|if
condition|(
name|reuse
operator|instanceof
name|FreqProxPostingsEnum
condition|)
block|{
name|posEnum
operator|=
operator|(
name|FreqProxPostingsEnum
operator|)
name|reuse
expr_stmt|;
if|if
condition|(
name|posEnum
operator|.
name|postingsArray
operator|!=
name|postingsArray
condition|)
block|{
name|posEnum
operator|=
operator|new
name|FreqProxPostingsEnum
argument_list|(
name|terms
argument_list|,
name|postingsArray
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|posEnum
operator|=
operator|new
name|FreqProxPostingsEnum
argument_list|(
name|terms
argument_list|,
name|postingsArray
argument_list|)
expr_stmt|;
block|}
name|posEnum
operator|.
name|reset
argument_list|(
name|sortedTermIDs
index|[
name|ord
index|]
argument_list|)
expr_stmt|;
return|return
name|posEnum
return|;
block|}
name|FreqProxDocsEnum
name|docsEnum
decl_stmt|;
if|if
condition|(
operator|!
name|terms
operator|.
name|hasFreq
operator|&&
name|PostingsEnum
operator|.
name|featureRequested
argument_list|(
name|flags
argument_list|,
name|PostingsEnum
operator|.
name|FREQS
argument_list|)
condition|)
block|{
comment|// Caller wants freqs but we didn't index them;
comment|// don't lie:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"did not index freq"
argument_list|)
throw|;
block|}
if|if
condition|(
name|reuse
operator|instanceof
name|FreqProxDocsEnum
condition|)
block|{
name|docsEnum
operator|=
operator|(
name|FreqProxDocsEnum
operator|)
name|reuse
expr_stmt|;
if|if
condition|(
name|docsEnum
operator|.
name|postingsArray
operator|!=
name|postingsArray
condition|)
block|{
name|docsEnum
operator|=
operator|new
name|FreqProxDocsEnum
argument_list|(
name|terms
argument_list|,
name|postingsArray
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|docsEnum
operator|=
operator|new
name|FreqProxDocsEnum
argument_list|(
name|terms
argument_list|,
name|postingsArray
argument_list|)
expr_stmt|;
block|}
name|docsEnum
operator|.
name|reset
argument_list|(
name|sortedTermIDs
index|[
name|ord
index|]
argument_list|)
expr_stmt|;
return|return
name|docsEnum
return|;
block|}
comment|/**      * Expert: Returns the TermsEnums internal state to position the TermsEnum      * without re-seeking the term dictionary.      *<p>      * NOTE: A seek by {@link TermState} might not capture the      * {@link AttributeSource}'s state. Callers must maintain the      * {@link AttributeSource} states separately      *       * @see TermState      * @see #seekExact(BytesRef, TermState)      */
DECL|method|termState
specifier|public
name|TermState
name|termState
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|TermState
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|copyFrom
parameter_list|(
name|TermState
name|other
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
return|;
block|}
block|}
DECL|class|FreqProxDocsEnum
specifier|private
specifier|static
class|class
name|FreqProxDocsEnum
extends|extends
name|PostingsEnum
block|{
DECL|field|terms
specifier|final
name|FreqProxTermsWriterPerField
name|terms
decl_stmt|;
DECL|field|postingsArray
specifier|final
name|FreqProxPostingsArray
name|postingsArray
decl_stmt|;
DECL|field|reader
specifier|final
name|ByteSliceReader
name|reader
init|=
operator|new
name|ByteSliceReader
argument_list|()
decl_stmt|;
DECL|field|readTermFreq
specifier|final
name|boolean
name|readTermFreq
decl_stmt|;
DECL|field|docID
name|int
name|docID
decl_stmt|;
DECL|field|freq
name|int
name|freq
decl_stmt|;
DECL|field|ended
name|boolean
name|ended
decl_stmt|;
DECL|field|termID
name|int
name|termID
decl_stmt|;
DECL|method|FreqProxDocsEnum
specifier|public
name|FreqProxDocsEnum
parameter_list|(
name|FreqProxTermsWriterPerField
name|terms
parameter_list|,
name|FreqProxPostingsArray
name|postingsArray
parameter_list|)
block|{
name|this
operator|.
name|terms
operator|=
name|terms
expr_stmt|;
name|this
operator|.
name|postingsArray
operator|=
name|postingsArray
expr_stmt|;
name|this
operator|.
name|readTermFreq
operator|=
name|terms
operator|.
name|hasFreq
expr_stmt|;
block|}
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|(
name|int
name|termID
parameter_list|)
block|{
name|this
operator|.
name|termID
operator|=
name|termID
expr_stmt|;
name|terms
operator|.
name|initReader
argument_list|(
name|reader
argument_list|,
name|termID
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|ended
operator|=
literal|false
expr_stmt|;
name|docID
operator|=
literal|0
expr_stmt|;
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
name|docID
return|;
block|}
annotation|@
name|Override
DECL|method|freq
specifier|public
name|int
name|freq
parameter_list|()
block|{
comment|// Don't lie here ... don't want codecs writings lots
comment|// of wasted 1s into the index:
if|if
condition|(
operator|!
name|readTermFreq
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"freq was not indexed"
argument_list|)
throw|;
block|}
else|else
block|{
return|return
name|freq
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|nextPosition
specifier|public
name|int
name|nextPosition
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|-
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|startOffset
specifier|public
name|int
name|startOffset
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|-
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|endOffset
specifier|public
name|int
name|endOffset
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|-
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|getPayload
specifier|public
name|BytesRef
name|getPayload
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|reader
operator|.
name|eof
argument_list|()
condition|)
block|{
if|if
condition|(
name|ended
condition|)
block|{
return|return
name|NO_MORE_DOCS
return|;
block|}
else|else
block|{
name|ended
operator|=
literal|true
expr_stmt|;
name|docID
operator|=
name|postingsArray
operator|.
name|lastDocIDs
index|[
name|termID
index|]
expr_stmt|;
if|if
condition|(
name|readTermFreq
condition|)
block|{
name|freq
operator|=
name|postingsArray
operator|.
name|termFreqs
index|[
name|termID
index|]
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|int
name|code
init|=
name|reader
operator|.
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|readTermFreq
condition|)
block|{
name|docID
operator|+=
name|code
expr_stmt|;
block|}
else|else
block|{
name|docID
operator|+=
name|code
operator|>>>
literal|1
expr_stmt|;
if|if
condition|(
operator|(
name|code
operator|&
literal|1
operator|)
operator|!=
literal|0
condition|)
block|{
name|freq
operator|=
literal|1
expr_stmt|;
block|}
else|else
block|{
name|freq
operator|=
name|reader
operator|.
name|readVInt
argument_list|()
expr_stmt|;
block|}
block|}
assert|assert
name|docID
operator|!=
name|postingsArray
operator|.
name|lastDocIDs
index|[
name|termID
index|]
assert|;
block|}
return|return
name|docID
return|;
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
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|cost
specifier|public
name|long
name|cost
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
DECL|class|FreqProxPostingsEnum
specifier|private
specifier|static
class|class
name|FreqProxPostingsEnum
extends|extends
name|PostingsEnum
block|{
DECL|field|terms
specifier|final
name|FreqProxTermsWriterPerField
name|terms
decl_stmt|;
DECL|field|postingsArray
specifier|final
name|FreqProxPostingsArray
name|postingsArray
decl_stmt|;
DECL|field|reader
specifier|final
name|ByteSliceReader
name|reader
init|=
operator|new
name|ByteSliceReader
argument_list|()
decl_stmt|;
DECL|field|posReader
specifier|final
name|ByteSliceReader
name|posReader
init|=
operator|new
name|ByteSliceReader
argument_list|()
decl_stmt|;
DECL|field|readOffsets
specifier|final
name|boolean
name|readOffsets
decl_stmt|;
DECL|field|docID
name|int
name|docID
decl_stmt|;
DECL|field|freq
name|int
name|freq
decl_stmt|;
DECL|field|pos
name|int
name|pos
decl_stmt|;
DECL|field|startOffset
name|int
name|startOffset
decl_stmt|;
DECL|field|endOffset
name|int
name|endOffset
decl_stmt|;
DECL|field|posLeft
name|int
name|posLeft
decl_stmt|;
DECL|field|termID
name|int
name|termID
decl_stmt|;
DECL|field|ended
name|boolean
name|ended
decl_stmt|;
DECL|field|hasPayload
name|boolean
name|hasPayload
decl_stmt|;
DECL|field|payload
name|BytesRefBuilder
name|payload
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
DECL|method|FreqProxPostingsEnum
specifier|public
name|FreqProxPostingsEnum
parameter_list|(
name|FreqProxTermsWriterPerField
name|terms
parameter_list|,
name|FreqProxPostingsArray
name|postingsArray
parameter_list|)
block|{
name|this
operator|.
name|terms
operator|=
name|terms
expr_stmt|;
name|this
operator|.
name|postingsArray
operator|=
name|postingsArray
expr_stmt|;
name|this
operator|.
name|readOffsets
operator|=
name|terms
operator|.
name|hasOffsets
expr_stmt|;
assert|assert
name|terms
operator|.
name|hasProx
assert|;
assert|assert
name|terms
operator|.
name|hasFreq
assert|;
block|}
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|(
name|int
name|termID
parameter_list|)
block|{
name|this
operator|.
name|termID
operator|=
name|termID
expr_stmt|;
name|terms
operator|.
name|initReader
argument_list|(
name|reader
argument_list|,
name|termID
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|terms
operator|.
name|initReader
argument_list|(
name|posReader
argument_list|,
name|termID
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|ended
operator|=
literal|false
expr_stmt|;
name|docID
operator|=
literal|0
expr_stmt|;
name|posLeft
operator|=
literal|0
expr_stmt|;
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
name|docID
return|;
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
name|freq
return|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
while|while
condition|(
name|posLeft
operator|!=
literal|0
condition|)
block|{
name|nextPosition
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|reader
operator|.
name|eof
argument_list|()
condition|)
block|{
if|if
condition|(
name|ended
condition|)
block|{
return|return
name|NO_MORE_DOCS
return|;
block|}
else|else
block|{
name|ended
operator|=
literal|true
expr_stmt|;
name|docID
operator|=
name|postingsArray
operator|.
name|lastDocIDs
index|[
name|termID
index|]
expr_stmt|;
name|freq
operator|=
name|postingsArray
operator|.
name|termFreqs
index|[
name|termID
index|]
expr_stmt|;
block|}
block|}
else|else
block|{
name|int
name|code
init|=
name|reader
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|docID
operator|+=
name|code
operator|>>>
literal|1
expr_stmt|;
if|if
condition|(
operator|(
name|code
operator|&
literal|1
operator|)
operator|!=
literal|0
condition|)
block|{
name|freq
operator|=
literal|1
expr_stmt|;
block|}
else|else
block|{
name|freq
operator|=
name|reader
operator|.
name|readVInt
argument_list|()
expr_stmt|;
block|}
assert|assert
name|docID
operator|!=
name|postingsArray
operator|.
name|lastDocIDs
index|[
name|termID
index|]
assert|;
block|}
name|posLeft
operator|=
name|freq
expr_stmt|;
name|pos
operator|=
literal|0
expr_stmt|;
name|startOffset
operator|=
literal|0
expr_stmt|;
return|return
name|docID
return|;
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
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|cost
specifier|public
name|long
name|cost
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|nextPosition
specifier|public
name|int
name|nextPosition
parameter_list|()
throws|throws
name|IOException
block|{
assert|assert
name|posLeft
operator|>
literal|0
assert|;
name|posLeft
operator|--
expr_stmt|;
name|int
name|code
init|=
name|posReader
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|pos
operator|+=
name|code
operator|>>>
literal|1
expr_stmt|;
if|if
condition|(
operator|(
name|code
operator|&
literal|1
operator|)
operator|!=
literal|0
condition|)
block|{
name|hasPayload
operator|=
literal|true
expr_stmt|;
comment|// has a payload
name|payload
operator|.
name|setLength
argument_list|(
name|posReader
operator|.
name|readVInt
argument_list|()
argument_list|)
expr_stmt|;
name|payload
operator|.
name|grow
argument_list|(
name|payload
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|posReader
operator|.
name|readBytes
argument_list|(
name|payload
operator|.
name|bytes
argument_list|()
argument_list|,
literal|0
argument_list|,
name|payload
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|hasPayload
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
name|readOffsets
condition|)
block|{
name|startOffset
operator|+=
name|posReader
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|endOffset
operator|=
name|startOffset
operator|+
name|posReader
operator|.
name|readVInt
argument_list|()
expr_stmt|;
block|}
return|return
name|pos
return|;
block|}
annotation|@
name|Override
DECL|method|startOffset
specifier|public
name|int
name|startOffset
parameter_list|()
block|{
if|if
condition|(
operator|!
name|readOffsets
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"offsets were not indexed"
argument_list|)
throw|;
block|}
return|return
name|startOffset
return|;
block|}
annotation|@
name|Override
DECL|method|endOffset
specifier|public
name|int
name|endOffset
parameter_list|()
block|{
if|if
condition|(
operator|!
name|readOffsets
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"offsets were not indexed"
argument_list|)
throw|;
block|}
return|return
name|endOffset
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
if|if
condition|(
name|hasPayload
condition|)
block|{
return|return
name|payload
operator|.
name|get
argument_list|()
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

