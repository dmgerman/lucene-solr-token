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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|tokenattributes
operator|.
name|OffsetAttribute
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|PayloadAttribute
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
name|FieldInfo
operator|.
name|IndexOptions
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
name|RamUsageEstimator
import|;
end_import

begin_comment
comment|// TODO: break into separate freq and prox writers as
end_comment

begin_comment
comment|// codecs; make separate container (tii/tis/skip/*) that can
end_comment

begin_comment
comment|// be configured as any number of files 1..N
end_comment

begin_class
DECL|class|FreqProxTermsWriterPerField
specifier|final
class|class
name|FreqProxTermsWriterPerField
extends|extends
name|TermsHashPerField
block|{
DECL|field|freqProxPostingsArray
specifier|private
name|FreqProxPostingsArray
name|freqProxPostingsArray
decl_stmt|;
DECL|field|hasFreq
specifier|final
name|boolean
name|hasFreq
decl_stmt|;
DECL|field|hasProx
specifier|final
name|boolean
name|hasProx
decl_stmt|;
DECL|field|hasOffsets
specifier|final
name|boolean
name|hasOffsets
decl_stmt|;
DECL|field|payloadAttribute
name|PayloadAttribute
name|payloadAttribute
decl_stmt|;
DECL|field|offsetAttribute
name|OffsetAttribute
name|offsetAttribute
decl_stmt|;
DECL|field|sumTotalTermFreq
name|long
name|sumTotalTermFreq
decl_stmt|;
DECL|field|sumDocFreq
name|long
name|sumDocFreq
decl_stmt|;
comment|// How many docs have this field:
DECL|field|docCount
name|int
name|docCount
decl_stmt|;
comment|/** Set to true if any token had a payload in the current    *  segment. */
DECL|field|sawPayloads
name|boolean
name|sawPayloads
decl_stmt|;
DECL|method|FreqProxTermsWriterPerField
specifier|public
name|FreqProxTermsWriterPerField
parameter_list|(
name|FieldInvertState
name|invertState
parameter_list|,
name|TermsHash
name|termsHash
parameter_list|,
name|FieldInfo
name|fieldInfo
parameter_list|,
name|TermsHashPerField
name|nextPerField
parameter_list|)
block|{
name|super
argument_list|(
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
condition|?
literal|2
else|:
literal|1
argument_list|,
name|invertState
argument_list|,
name|termsHash
argument_list|,
name|nextPerField
argument_list|,
name|fieldInfo
argument_list|)
expr_stmt|;
name|IndexOptions
name|indexOptions
init|=
name|fieldInfo
operator|.
name|getIndexOptions
argument_list|()
decl_stmt|;
assert|assert
name|indexOptions
operator|!=
literal|null
assert|;
name|hasFreq
operator|=
name|indexOptions
operator|.
name|compareTo
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS
argument_list|)
operator|>=
literal|0
expr_stmt|;
name|hasProx
operator|=
name|indexOptions
operator|.
name|compareTo
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
argument_list|)
operator|>=
literal|0
expr_stmt|;
name|hasOffsets
operator|=
name|indexOptions
operator|.
name|compareTo
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS
argument_list|)
operator|>=
literal|0
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|finish
name|void
name|finish
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|finish
argument_list|()
expr_stmt|;
name|sumDocFreq
operator|+=
name|fieldState
operator|.
name|uniqueTermCount
expr_stmt|;
name|sumTotalTermFreq
operator|+=
name|fieldState
operator|.
name|length
expr_stmt|;
if|if
condition|(
name|fieldState
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|docCount
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|sawPayloads
condition|)
block|{
name|fieldInfo
operator|.
name|setStorePayloads
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|start
name|boolean
name|start
parameter_list|(
name|IndexableField
name|f
parameter_list|,
name|boolean
name|first
parameter_list|)
block|{
name|super
operator|.
name|start
argument_list|(
name|f
argument_list|,
name|first
argument_list|)
expr_stmt|;
name|payloadAttribute
operator|=
name|fieldState
operator|.
name|payloadAttribute
expr_stmt|;
name|offsetAttribute
operator|=
name|fieldState
operator|.
name|offsetAttribute
expr_stmt|;
return|return
literal|true
return|;
block|}
DECL|method|writeProx
name|void
name|writeProx
parameter_list|(
name|int
name|termID
parameter_list|,
name|int
name|proxCode
parameter_list|)
block|{
if|if
condition|(
name|payloadAttribute
operator|==
literal|null
condition|)
block|{
name|writeVInt
argument_list|(
literal|1
argument_list|,
name|proxCode
operator|<<
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|BytesRef
name|payload
init|=
name|payloadAttribute
operator|.
name|getPayload
argument_list|()
decl_stmt|;
if|if
condition|(
name|payload
operator|!=
literal|null
operator|&&
name|payload
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|writeVInt
argument_list|(
literal|1
argument_list|,
operator|(
name|proxCode
operator|<<
literal|1
operator|)
operator||
literal|1
argument_list|)
expr_stmt|;
name|writeVInt
argument_list|(
literal|1
argument_list|,
name|payload
operator|.
name|length
argument_list|)
expr_stmt|;
name|writeBytes
argument_list|(
literal|1
argument_list|,
name|payload
operator|.
name|bytes
argument_list|,
name|payload
operator|.
name|offset
argument_list|,
name|payload
operator|.
name|length
argument_list|)
expr_stmt|;
name|sawPayloads
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|writeVInt
argument_list|(
literal|1
argument_list|,
name|proxCode
operator|<<
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
assert|assert
name|postingsArray
operator|==
name|freqProxPostingsArray
assert|;
name|freqProxPostingsArray
operator|.
name|lastPositions
index|[
name|termID
index|]
operator|=
name|fieldState
operator|.
name|position
expr_stmt|;
block|}
DECL|method|writeOffsets
name|void
name|writeOffsets
parameter_list|(
name|int
name|termID
parameter_list|,
name|int
name|offsetAccum
parameter_list|)
block|{
specifier|final
name|int
name|startOffset
init|=
name|offsetAccum
operator|+
name|offsetAttribute
operator|.
name|startOffset
argument_list|()
decl_stmt|;
specifier|final
name|int
name|endOffset
init|=
name|offsetAccum
operator|+
name|offsetAttribute
operator|.
name|endOffset
argument_list|()
decl_stmt|;
assert|assert
name|startOffset
operator|-
name|freqProxPostingsArray
operator|.
name|lastOffsets
index|[
name|termID
index|]
operator|>=
literal|0
assert|;
name|writeVInt
argument_list|(
literal|1
argument_list|,
name|startOffset
operator|-
name|freqProxPostingsArray
operator|.
name|lastOffsets
index|[
name|termID
index|]
argument_list|)
expr_stmt|;
name|writeVInt
argument_list|(
literal|1
argument_list|,
name|endOffset
operator|-
name|startOffset
argument_list|)
expr_stmt|;
name|freqProxPostingsArray
operator|.
name|lastOffsets
index|[
name|termID
index|]
operator|=
name|startOffset
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|newTerm
name|void
name|newTerm
parameter_list|(
specifier|final
name|int
name|termID
parameter_list|)
block|{
comment|// First time we're seeing this term since the last
comment|// flush
assert|assert
name|docState
operator|.
name|testPoint
argument_list|(
literal|"FreqProxTermsWriterPerField.newTerm start"
argument_list|)
assert|;
specifier|final
name|FreqProxPostingsArray
name|postings
init|=
name|freqProxPostingsArray
decl_stmt|;
name|postings
operator|.
name|lastDocIDs
index|[
name|termID
index|]
operator|=
name|docState
operator|.
name|docID
expr_stmt|;
if|if
condition|(
operator|!
name|hasFreq
condition|)
block|{
assert|assert
name|postings
operator|.
name|termFreqs
operator|==
literal|null
assert|;
name|postings
operator|.
name|lastDocCodes
index|[
name|termID
index|]
operator|=
name|docState
operator|.
name|docID
expr_stmt|;
block|}
else|else
block|{
name|postings
operator|.
name|lastDocCodes
index|[
name|termID
index|]
operator|=
name|docState
operator|.
name|docID
operator|<<
literal|1
expr_stmt|;
name|postings
operator|.
name|termFreqs
index|[
name|termID
index|]
operator|=
literal|1
expr_stmt|;
if|if
condition|(
name|hasProx
condition|)
block|{
name|writeProx
argument_list|(
name|termID
argument_list|,
name|fieldState
operator|.
name|position
argument_list|)
expr_stmt|;
if|if
condition|(
name|hasOffsets
condition|)
block|{
name|writeOffsets
argument_list|(
name|termID
argument_list|,
name|fieldState
operator|.
name|offset
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
assert|assert
operator|!
name|hasOffsets
assert|;
block|}
block|}
name|fieldState
operator|.
name|maxTermFrequency
operator|=
name|Math
operator|.
name|max
argument_list|(
literal|1
argument_list|,
name|fieldState
operator|.
name|maxTermFrequency
argument_list|)
expr_stmt|;
name|fieldState
operator|.
name|uniqueTermCount
operator|++
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|addTerm
name|void
name|addTerm
parameter_list|(
specifier|final
name|int
name|termID
parameter_list|)
block|{
assert|assert
name|docState
operator|.
name|testPoint
argument_list|(
literal|"FreqProxTermsWriterPerField.addTerm start"
argument_list|)
assert|;
specifier|final
name|FreqProxPostingsArray
name|postings
init|=
name|freqProxPostingsArray
decl_stmt|;
assert|assert
operator|!
name|hasFreq
operator|||
name|postings
operator|.
name|termFreqs
index|[
name|termID
index|]
operator|>
literal|0
assert|;
if|if
condition|(
operator|!
name|hasFreq
condition|)
block|{
assert|assert
name|postings
operator|.
name|termFreqs
operator|==
literal|null
assert|;
if|if
condition|(
name|docState
operator|.
name|docID
operator|!=
name|postings
operator|.
name|lastDocIDs
index|[
name|termID
index|]
condition|)
block|{
comment|// New document; now encode docCode for previous doc:
assert|assert
name|docState
operator|.
name|docID
operator|>
name|postings
operator|.
name|lastDocIDs
index|[
name|termID
index|]
assert|;
name|writeVInt
argument_list|(
literal|0
argument_list|,
name|postings
operator|.
name|lastDocCodes
index|[
name|termID
index|]
argument_list|)
expr_stmt|;
name|postings
operator|.
name|lastDocCodes
index|[
name|termID
index|]
operator|=
name|docState
operator|.
name|docID
operator|-
name|postings
operator|.
name|lastDocIDs
index|[
name|termID
index|]
expr_stmt|;
name|postings
operator|.
name|lastDocIDs
index|[
name|termID
index|]
operator|=
name|docState
operator|.
name|docID
expr_stmt|;
name|fieldState
operator|.
name|uniqueTermCount
operator|++
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|docState
operator|.
name|docID
operator|!=
name|postings
operator|.
name|lastDocIDs
index|[
name|termID
index|]
condition|)
block|{
assert|assert
name|docState
operator|.
name|docID
operator|>
name|postings
operator|.
name|lastDocIDs
index|[
name|termID
index|]
operator|:
literal|"id: "
operator|+
name|docState
operator|.
name|docID
operator|+
literal|" postings ID: "
operator|+
name|postings
operator|.
name|lastDocIDs
index|[
name|termID
index|]
operator|+
literal|" termID: "
operator|+
name|termID
assert|;
comment|// Term not yet seen in the current doc but previously
comment|// seen in other doc(s) since the last flush
comment|// Now that we know doc freq for previous doc,
comment|// write it& lastDocCode
if|if
condition|(
literal|1
operator|==
name|postings
operator|.
name|termFreqs
index|[
name|termID
index|]
condition|)
block|{
name|writeVInt
argument_list|(
literal|0
argument_list|,
name|postings
operator|.
name|lastDocCodes
index|[
name|termID
index|]
operator||
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|writeVInt
argument_list|(
literal|0
argument_list|,
name|postings
operator|.
name|lastDocCodes
index|[
name|termID
index|]
argument_list|)
expr_stmt|;
name|writeVInt
argument_list|(
literal|0
argument_list|,
name|postings
operator|.
name|termFreqs
index|[
name|termID
index|]
argument_list|)
expr_stmt|;
block|}
comment|// Init freq for the current document
name|postings
operator|.
name|termFreqs
index|[
name|termID
index|]
operator|=
literal|1
expr_stmt|;
name|fieldState
operator|.
name|maxTermFrequency
operator|=
name|Math
operator|.
name|max
argument_list|(
literal|1
argument_list|,
name|fieldState
operator|.
name|maxTermFrequency
argument_list|)
expr_stmt|;
name|postings
operator|.
name|lastDocCodes
index|[
name|termID
index|]
operator|=
operator|(
name|docState
operator|.
name|docID
operator|-
name|postings
operator|.
name|lastDocIDs
index|[
name|termID
index|]
operator|)
operator|<<
literal|1
expr_stmt|;
name|postings
operator|.
name|lastDocIDs
index|[
name|termID
index|]
operator|=
name|docState
operator|.
name|docID
expr_stmt|;
if|if
condition|(
name|hasProx
condition|)
block|{
name|writeProx
argument_list|(
name|termID
argument_list|,
name|fieldState
operator|.
name|position
argument_list|)
expr_stmt|;
if|if
condition|(
name|hasOffsets
condition|)
block|{
name|postings
operator|.
name|lastOffsets
index|[
name|termID
index|]
operator|=
literal|0
expr_stmt|;
name|writeOffsets
argument_list|(
name|termID
argument_list|,
name|fieldState
operator|.
name|offset
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
assert|assert
operator|!
name|hasOffsets
assert|;
block|}
name|fieldState
operator|.
name|uniqueTermCount
operator|++
expr_stmt|;
block|}
else|else
block|{
name|fieldState
operator|.
name|maxTermFrequency
operator|=
name|Math
operator|.
name|max
argument_list|(
name|fieldState
operator|.
name|maxTermFrequency
argument_list|,
operator|++
name|postings
operator|.
name|termFreqs
index|[
name|termID
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
name|hasProx
condition|)
block|{
name|writeProx
argument_list|(
name|termID
argument_list|,
name|fieldState
operator|.
name|position
operator|-
name|postings
operator|.
name|lastPositions
index|[
name|termID
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
name|hasOffsets
condition|)
block|{
name|writeOffsets
argument_list|(
name|termID
argument_list|,
name|fieldState
operator|.
name|offset
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|newPostingsArray
specifier|public
name|void
name|newPostingsArray
parameter_list|()
block|{
name|freqProxPostingsArray
operator|=
operator|(
name|FreqProxPostingsArray
operator|)
name|postingsArray
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createPostingsArray
name|ParallelPostingsArray
name|createPostingsArray
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|IndexOptions
name|indexOptions
init|=
name|fieldInfo
operator|.
name|getIndexOptions
argument_list|()
decl_stmt|;
assert|assert
name|indexOptions
operator|!=
literal|null
assert|;
name|boolean
name|hasFreq
init|=
name|indexOptions
operator|.
name|compareTo
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS
argument_list|)
operator|>=
literal|0
decl_stmt|;
name|boolean
name|hasProx
init|=
name|indexOptions
operator|.
name|compareTo
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
argument_list|)
operator|>=
literal|0
decl_stmt|;
name|boolean
name|hasOffsets
init|=
name|indexOptions
operator|.
name|compareTo
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS
argument_list|)
operator|>=
literal|0
decl_stmt|;
return|return
operator|new
name|FreqProxPostingsArray
argument_list|(
name|size
argument_list|,
name|hasFreq
argument_list|,
name|hasProx
argument_list|,
name|hasOffsets
argument_list|)
return|;
block|}
DECL|class|FreqProxPostingsArray
specifier|static
specifier|final
class|class
name|FreqProxPostingsArray
extends|extends
name|ParallelPostingsArray
block|{
DECL|method|FreqProxPostingsArray
specifier|public
name|FreqProxPostingsArray
parameter_list|(
name|int
name|size
parameter_list|,
name|boolean
name|writeFreqs
parameter_list|,
name|boolean
name|writeProx
parameter_list|,
name|boolean
name|writeOffsets
parameter_list|)
block|{
name|super
argument_list|(
name|size
argument_list|)
expr_stmt|;
if|if
condition|(
name|writeFreqs
condition|)
block|{
name|termFreqs
operator|=
operator|new
name|int
index|[
name|size
index|]
expr_stmt|;
block|}
name|lastDocIDs
operator|=
operator|new
name|int
index|[
name|size
index|]
expr_stmt|;
name|lastDocCodes
operator|=
operator|new
name|int
index|[
name|size
index|]
expr_stmt|;
if|if
condition|(
name|writeProx
condition|)
block|{
name|lastPositions
operator|=
operator|new
name|int
index|[
name|size
index|]
expr_stmt|;
if|if
condition|(
name|writeOffsets
condition|)
block|{
name|lastOffsets
operator|=
operator|new
name|int
index|[
name|size
index|]
expr_stmt|;
block|}
block|}
else|else
block|{
assert|assert
operator|!
name|writeOffsets
assert|;
block|}
comment|//System.out.println("PA init freqs=" + writeFreqs + " pos=" + writeProx + " offs=" + writeOffsets);
block|}
DECL|field|termFreqs
name|int
name|termFreqs
index|[]
decl_stmt|;
comment|// # times this term occurs in the current doc
DECL|field|lastDocIDs
name|int
name|lastDocIDs
index|[]
decl_stmt|;
comment|// Last docID where this term occurred
DECL|field|lastDocCodes
name|int
name|lastDocCodes
index|[]
decl_stmt|;
comment|// Code for prior doc
DECL|field|lastPositions
name|int
name|lastPositions
index|[]
decl_stmt|;
comment|// Last position where this term occurred
DECL|field|lastOffsets
name|int
name|lastOffsets
index|[]
decl_stmt|;
comment|// Last endOffset where this term occurred
annotation|@
name|Override
DECL|method|newInstance
name|ParallelPostingsArray
name|newInstance
parameter_list|(
name|int
name|size
parameter_list|)
block|{
return|return
operator|new
name|FreqProxPostingsArray
argument_list|(
name|size
argument_list|,
name|termFreqs
operator|!=
literal|null
argument_list|,
name|lastPositions
operator|!=
literal|null
argument_list|,
name|lastOffsets
operator|!=
literal|null
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|copyTo
name|void
name|copyTo
parameter_list|(
name|ParallelPostingsArray
name|toArray
parameter_list|,
name|int
name|numToCopy
parameter_list|)
block|{
assert|assert
name|toArray
operator|instanceof
name|FreqProxPostingsArray
assert|;
name|FreqProxPostingsArray
name|to
init|=
operator|(
name|FreqProxPostingsArray
operator|)
name|toArray
decl_stmt|;
name|super
operator|.
name|copyTo
argument_list|(
name|toArray
argument_list|,
name|numToCopy
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|lastDocIDs
argument_list|,
literal|0
argument_list|,
name|to
operator|.
name|lastDocIDs
argument_list|,
literal|0
argument_list|,
name|numToCopy
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|lastDocCodes
argument_list|,
literal|0
argument_list|,
name|to
operator|.
name|lastDocCodes
argument_list|,
literal|0
argument_list|,
name|numToCopy
argument_list|)
expr_stmt|;
if|if
condition|(
name|lastPositions
operator|!=
literal|null
condition|)
block|{
assert|assert
name|to
operator|.
name|lastPositions
operator|!=
literal|null
assert|;
name|System
operator|.
name|arraycopy
argument_list|(
name|lastPositions
argument_list|,
literal|0
argument_list|,
name|to
operator|.
name|lastPositions
argument_list|,
literal|0
argument_list|,
name|numToCopy
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|lastOffsets
operator|!=
literal|null
condition|)
block|{
assert|assert
name|to
operator|.
name|lastOffsets
operator|!=
literal|null
assert|;
name|System
operator|.
name|arraycopy
argument_list|(
name|lastOffsets
argument_list|,
literal|0
argument_list|,
name|to
operator|.
name|lastOffsets
argument_list|,
literal|0
argument_list|,
name|numToCopy
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|termFreqs
operator|!=
literal|null
condition|)
block|{
assert|assert
name|to
operator|.
name|termFreqs
operator|!=
literal|null
assert|;
name|System
operator|.
name|arraycopy
argument_list|(
name|termFreqs
argument_list|,
literal|0
argument_list|,
name|to
operator|.
name|termFreqs
argument_list|,
literal|0
argument_list|,
name|numToCopy
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|bytesPerPosting
name|int
name|bytesPerPosting
parameter_list|()
block|{
name|int
name|bytes
init|=
name|ParallelPostingsArray
operator|.
name|BYTES_PER_POSTING
operator|+
literal|2
operator|*
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
decl_stmt|;
if|if
condition|(
name|lastPositions
operator|!=
literal|null
condition|)
block|{
name|bytes
operator|+=
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
expr_stmt|;
block|}
if|if
condition|(
name|lastOffsets
operator|!=
literal|null
condition|)
block|{
name|bytes
operator|+=
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
expr_stmt|;
block|}
if|if
condition|(
name|termFreqs
operator|!=
literal|null
condition|)
block|{
name|bytes
operator|+=
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
expr_stmt|;
block|}
return|return
name|bytes
return|;
block|}
block|}
block|}
end_class

end_unit

