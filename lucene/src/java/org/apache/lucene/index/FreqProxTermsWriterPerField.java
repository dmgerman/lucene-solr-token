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
name|document
operator|.
name|Fieldable
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
name|TermsHashConsumerPerField
implements|implements
name|Comparable
argument_list|<
name|FreqProxTermsWriterPerField
argument_list|>
block|{
DECL|field|perThread
specifier|final
name|FreqProxTermsWriterPerThread
name|perThread
decl_stmt|;
DECL|field|termsHashPerField
specifier|final
name|TermsHashPerField
name|termsHashPerField
decl_stmt|;
DECL|field|fieldInfo
specifier|final
name|FieldInfo
name|fieldInfo
decl_stmt|;
DECL|field|docState
specifier|final
name|DocumentsWriter
operator|.
name|DocState
name|docState
decl_stmt|;
DECL|field|fieldState
specifier|final
name|FieldInvertState
name|fieldState
decl_stmt|;
DECL|field|omitTermFreqAndPositions
name|boolean
name|omitTermFreqAndPositions
decl_stmt|;
DECL|field|payloadAttribute
name|PayloadAttribute
name|payloadAttribute
decl_stmt|;
DECL|method|FreqProxTermsWriterPerField
specifier|public
name|FreqProxTermsWriterPerField
parameter_list|(
name|TermsHashPerField
name|termsHashPerField
parameter_list|,
name|FreqProxTermsWriterPerThread
name|perThread
parameter_list|,
name|FieldInfo
name|fieldInfo
parameter_list|)
block|{
name|this
operator|.
name|termsHashPerField
operator|=
name|termsHashPerField
expr_stmt|;
name|this
operator|.
name|perThread
operator|=
name|perThread
expr_stmt|;
name|this
operator|.
name|fieldInfo
operator|=
name|fieldInfo
expr_stmt|;
name|docState
operator|=
name|termsHashPerField
operator|.
name|docState
expr_stmt|;
name|fieldState
operator|=
name|termsHashPerField
operator|.
name|fieldState
expr_stmt|;
name|omitTermFreqAndPositions
operator|=
name|fieldInfo
operator|.
name|omitTermFreqAndPositions
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getStreamCount
name|int
name|getStreamCount
parameter_list|()
block|{
if|if
condition|(
name|fieldInfo
operator|.
name|omitTermFreqAndPositions
condition|)
return|return
literal|1
return|;
else|else
return|return
literal|2
return|;
block|}
annotation|@
name|Override
DECL|method|finish
name|void
name|finish
parameter_list|()
block|{}
DECL|field|hasPayloads
name|boolean
name|hasPayloads
decl_stmt|;
annotation|@
name|Override
DECL|method|skippingLongTerm
name|void
name|skippingLongTerm
parameter_list|()
throws|throws
name|IOException
block|{}
DECL|method|compareTo
specifier|public
name|int
name|compareTo
parameter_list|(
name|FreqProxTermsWriterPerField
name|other
parameter_list|)
block|{
return|return
name|fieldInfo
operator|.
name|name
operator|.
name|compareTo
argument_list|(
name|other
operator|.
name|fieldInfo
operator|.
name|name
argument_list|)
return|;
block|}
DECL|method|reset
name|void
name|reset
parameter_list|()
block|{
comment|// Record, up front, whether our in-RAM format will be
comment|// with or without term freqs:
name|omitTermFreqAndPositions
operator|=
name|fieldInfo
operator|.
name|omitTermFreqAndPositions
expr_stmt|;
name|payloadAttribute
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|start
name|boolean
name|start
parameter_list|(
name|Fieldable
index|[]
name|fields
parameter_list|,
name|int
name|count
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|count
condition|;
name|i
operator|++
control|)
if|if
condition|(
name|fields
index|[
name|i
index|]
operator|.
name|isIndexed
argument_list|()
condition|)
return|return
literal|true
return|;
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|start
name|void
name|start
parameter_list|(
name|Fieldable
name|f
parameter_list|)
block|{
if|if
condition|(
name|fieldState
operator|.
name|attributeSource
operator|.
name|hasAttribute
argument_list|(
name|PayloadAttribute
operator|.
name|class
argument_list|)
condition|)
block|{
name|payloadAttribute
operator|=
name|fieldState
operator|.
name|attributeSource
operator|.
name|getAttribute
argument_list|(
name|PayloadAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|payloadAttribute
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|writeProx
name|void
name|writeProx
parameter_list|(
specifier|final
name|int
name|termID
parameter_list|,
name|int
name|proxCode
parameter_list|)
block|{
specifier|final
name|Payload
name|payload
decl_stmt|;
if|if
condition|(
name|payloadAttribute
operator|==
literal|null
condition|)
block|{
name|payload
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|payload
operator|=
name|payloadAttribute
operator|.
name|getPayload
argument_list|()
expr_stmt|;
block|}
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
name|termsHashPerField
operator|.
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
name|termsHashPerField
operator|.
name|writeVInt
argument_list|(
literal|1
argument_list|,
name|payload
operator|.
name|length
argument_list|)
expr_stmt|;
name|termsHashPerField
operator|.
name|writeBytes
argument_list|(
literal|1
argument_list|,
name|payload
operator|.
name|data
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
name|hasPayloads
operator|=
literal|true
expr_stmt|;
block|}
else|else
name|termsHashPerField
operator|.
name|writeVInt
argument_list|(
literal|1
argument_list|,
name|proxCode
operator|<<
literal|1
argument_list|)
expr_stmt|;
name|FreqProxPostingsArray
name|postings
init|=
operator|(
name|FreqProxPostingsArray
operator|)
name|termsHashPerField
operator|.
name|postingsArray
decl_stmt|;
name|postings
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
name|FreqProxPostingsArray
name|postings
init|=
operator|(
name|FreqProxPostingsArray
operator|)
name|termsHashPerField
operator|.
name|postingsArray
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
name|omitTermFreqAndPositions
condition|)
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
name|docFreqs
index|[
name|termID
index|]
operator|=
literal|1
expr_stmt|;
name|writeProx
argument_list|(
name|termID
argument_list|,
name|fieldState
operator|.
name|position
argument_list|)
expr_stmt|;
block|}
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
name|FreqProxPostingsArray
name|postings
init|=
operator|(
name|FreqProxPostingsArray
operator|)
name|termsHashPerField
operator|.
name|postingsArray
decl_stmt|;
assert|assert
name|omitTermFreqAndPositions
operator|||
name|postings
operator|.
name|docFreqs
index|[
name|termID
index|]
operator|>
literal|0
assert|;
if|if
condition|(
name|omitTermFreqAndPositions
condition|)
block|{
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
assert|;
name|termsHashPerField
operator|.
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
block|}
block|}
else|else
block|{
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
name|docFreqs
index|[
name|termID
index|]
condition|)
name|termsHashPerField
operator|.
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
else|else
block|{
name|termsHashPerField
operator|.
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
name|termsHashPerField
operator|.
name|writeVInt
argument_list|(
literal|0
argument_list|,
name|postings
operator|.
name|docFreqs
index|[
name|termID
index|]
argument_list|)
expr_stmt|;
block|}
name|postings
operator|.
name|docFreqs
index|[
name|termID
index|]
operator|=
literal|1
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
name|writeProx
argument_list|(
name|termID
argument_list|,
name|fieldState
operator|.
name|position
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|postings
operator|.
name|docFreqs
index|[
name|termID
index|]
operator|++
expr_stmt|;
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
block|}
block|}
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
return|return
operator|new
name|FreqProxPostingsArray
argument_list|(
name|size
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
parameter_list|)
block|{
name|super
argument_list|(
name|size
argument_list|)
expr_stmt|;
name|docFreqs
operator|=
operator|new
name|int
index|[
name|size
index|]
expr_stmt|;
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
name|lastPositions
operator|=
operator|new
name|int
index|[
name|size
index|]
expr_stmt|;
block|}
DECL|field|docFreqs
name|int
name|docFreqs
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
argument_list|)
return|;
block|}
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
name|docFreqs
argument_list|,
literal|0
argument_list|,
name|to
operator|.
name|docFreqs
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
annotation|@
name|Override
DECL|method|bytesPerPosting
name|int
name|bytesPerPosting
parameter_list|()
block|{
return|return
name|ParallelPostingsArray
operator|.
name|BYTES_PER_POSTING
operator|+
literal|4
operator|*
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
return|;
block|}
block|}
DECL|method|abort
specifier|public
name|void
name|abort
parameter_list|()
block|{}
block|}
end_class

end_unit

