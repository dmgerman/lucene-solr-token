begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.pulsing
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|pulsing
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
name|List
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|BlockTermState
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
name|CodecUtil
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
name|PostingsWriterBase
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
name|TermStats
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
name|index
operator|.
name|IndexFileNames
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
name|SegmentWriteState
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
name|store
operator|.
name|DataOutput
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
name|store
operator|.
name|IndexOutput
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
name|store
operator|.
name|RAMOutputStream
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
name|IOUtils
import|;
end_import

begin_comment
comment|// TODO: we now inline based on total TF of the term,
end_comment

begin_comment
comment|// but it might be better to inline by "net bytes used"
end_comment

begin_comment
comment|// so that a term that has only 1 posting but a huge
end_comment

begin_comment
comment|// payload would not be inlined.  Though this is
end_comment

begin_comment
comment|// presumably rare in practice...
end_comment

begin_comment
comment|/**   * Writer for the pulsing format.   *<p>  * Wraps another postings implementation and decides   * (based on total number of occurrences), whether a terms   * postings should be inlined into the term dictionary,  * or passed through to the wrapped writer.  *  * @lucene.experimental */
end_comment

begin_class
DECL|class|PulsingPostingsWriter
specifier|public
specifier|final
class|class
name|PulsingPostingsWriter
extends|extends
name|PostingsWriterBase
block|{
DECL|field|CODEC
specifier|final
specifier|static
name|String
name|CODEC
init|=
literal|"PulsedPostingsWriter"
decl_stmt|;
comment|// recording field summary
DECL|field|SUMMARY_EXTENSION
specifier|final
specifier|static
name|String
name|SUMMARY_EXTENSION
init|=
literal|"smy"
decl_stmt|;
comment|// To add a new version, increment from the last one, and
comment|// change VERSION_CURRENT to point to your new version:
DECL|field|VERSION_START
specifier|final
specifier|static
name|int
name|VERSION_START
init|=
literal|0
decl_stmt|;
DECL|field|VERSION_META_ARRAY
specifier|final
specifier|static
name|int
name|VERSION_META_ARRAY
init|=
literal|1
decl_stmt|;
DECL|field|VERSION_CURRENT
specifier|final
specifier|static
name|int
name|VERSION_CURRENT
init|=
name|VERSION_META_ARRAY
decl_stmt|;
DECL|field|segmentState
specifier|private
name|SegmentWriteState
name|segmentState
decl_stmt|;
DECL|field|termsOut
specifier|private
name|IndexOutput
name|termsOut
decl_stmt|;
DECL|field|fields
specifier|private
name|List
argument_list|<
name|FieldMetaData
argument_list|>
name|fields
decl_stmt|;
DECL|field|indexOptions
specifier|private
name|IndexOptions
name|indexOptions
decl_stmt|;
DECL|field|storePayloads
specifier|private
name|boolean
name|storePayloads
decl_stmt|;
comment|// information for wrapped PF, in current field
DECL|field|longsSize
specifier|private
name|int
name|longsSize
decl_stmt|;
DECL|field|longs
specifier|private
name|long
index|[]
name|longs
decl_stmt|;
DECL|field|absolute
name|boolean
name|absolute
decl_stmt|;
DECL|class|PulsingTermState
specifier|private
specifier|static
class|class
name|PulsingTermState
extends|extends
name|BlockTermState
block|{
DECL|field|bytes
specifier|private
name|byte
index|[]
name|bytes
decl_stmt|;
DECL|field|wrappedState
specifier|private
name|BlockTermState
name|wrappedState
decl_stmt|;
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
if|if
condition|(
name|bytes
operator|!=
literal|null
condition|)
block|{
return|return
literal|"inlined"
return|;
block|}
else|else
block|{
return|return
literal|"not inlined wrapped="
operator|+
name|wrappedState
return|;
block|}
block|}
block|}
comment|// one entry per position
DECL|field|pending
specifier|private
specifier|final
name|Position
index|[]
name|pending
decl_stmt|;
DECL|field|pendingCount
specifier|private
name|int
name|pendingCount
init|=
literal|0
decl_stmt|;
comment|// -1 once we've hit too many positions
DECL|field|currentDoc
specifier|private
name|Position
name|currentDoc
decl_stmt|;
comment|// first Position entry of current doc
DECL|class|Position
specifier|private
specifier|static
specifier|final
class|class
name|Position
block|{
DECL|field|payload
name|BytesRef
name|payload
decl_stmt|;
DECL|field|termFreq
name|int
name|termFreq
decl_stmt|;
comment|// only incremented on first position for a given doc
DECL|field|pos
name|int
name|pos
decl_stmt|;
DECL|field|docID
name|int
name|docID
decl_stmt|;
DECL|field|startOffset
name|int
name|startOffset
decl_stmt|;
DECL|field|endOffset
name|int
name|endOffset
decl_stmt|;
block|}
DECL|class|FieldMetaData
specifier|private
specifier|static
specifier|final
class|class
name|FieldMetaData
block|{
DECL|field|fieldNumber
name|int
name|fieldNumber
decl_stmt|;
DECL|field|longsSize
name|int
name|longsSize
decl_stmt|;
DECL|method|FieldMetaData
name|FieldMetaData
parameter_list|(
name|int
name|number
parameter_list|,
name|int
name|size
parameter_list|)
block|{
name|fieldNumber
operator|=
name|number
expr_stmt|;
name|longsSize
operator|=
name|size
expr_stmt|;
block|}
block|}
comment|// TODO: -- lazy init this?  ie, if every single term
comment|// was inlined (eg for a "primary key" field) then we
comment|// never need to use this fallback?  Fallback writer for
comment|// non-inlined terms:
DECL|field|wrappedPostingsWriter
specifier|final
name|PostingsWriterBase
name|wrappedPostingsWriter
decl_stmt|;
comment|/** If the total number of positions (summed across all docs    *  for this term) is<= maxPositions, then the postings are    *  inlined into terms dict */
DECL|method|PulsingPostingsWriter
specifier|public
name|PulsingPostingsWriter
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|,
name|int
name|maxPositions
parameter_list|,
name|PostingsWriterBase
name|wrappedPostingsWriter
parameter_list|)
block|{
name|pending
operator|=
operator|new
name|Position
index|[
name|maxPositions
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|maxPositions
condition|;
name|i
operator|++
control|)
block|{
name|pending
index|[
name|i
index|]
operator|=
operator|new
name|Position
argument_list|()
expr_stmt|;
block|}
name|fields
operator|=
operator|new
name|ArrayList
argument_list|<
name|FieldMetaData
argument_list|>
argument_list|()
expr_stmt|;
comment|// We simply wrap another postings writer, but only call
comment|// on it when tot positions is>= the cutoff:
name|this
operator|.
name|wrappedPostingsWriter
operator|=
name|wrappedPostingsWriter
expr_stmt|;
name|this
operator|.
name|segmentState
operator|=
name|state
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|IndexOutput
name|termsOut
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|termsOut
operator|=
name|termsOut
expr_stmt|;
name|CodecUtil
operator|.
name|writeHeader
argument_list|(
name|termsOut
argument_list|,
name|CODEC
argument_list|,
name|VERSION_CURRENT
argument_list|)
expr_stmt|;
name|termsOut
operator|.
name|writeVInt
argument_list|(
name|pending
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// encode maxPositions in header
name|wrappedPostingsWriter
operator|.
name|init
argument_list|(
name|termsOut
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|newTermState
specifier|public
name|BlockTermState
name|newTermState
parameter_list|()
throws|throws
name|IOException
block|{
name|PulsingTermState
name|state
init|=
operator|new
name|PulsingTermState
argument_list|()
decl_stmt|;
name|state
operator|.
name|wrappedState
operator|=
name|wrappedPostingsWriter
operator|.
name|newTermState
argument_list|()
expr_stmt|;
return|return
name|state
return|;
block|}
annotation|@
name|Override
DECL|method|startTerm
specifier|public
name|void
name|startTerm
parameter_list|()
block|{
comment|//if (DEBUG) System.out.println("PW   startTerm");
assert|assert
name|pendingCount
operator|==
literal|0
assert|;
block|}
comment|// TODO: -- should we NOT reuse across fields?  would
comment|// be cleaner
comment|// Currently, this instance is re-used across fields, so
comment|// our parent calls setField whenever the field changes
annotation|@
name|Override
DECL|method|setField
specifier|public
name|int
name|setField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|)
block|{
name|this
operator|.
name|indexOptions
operator|=
name|fieldInfo
operator|.
name|getIndexOptions
argument_list|()
expr_stmt|;
comment|//if (DEBUG) System.out.println("PW field=" + fieldInfo.name + " indexOptions=" + indexOptions);
name|storePayloads
operator|=
name|fieldInfo
operator|.
name|hasPayloads
argument_list|()
expr_stmt|;
name|absolute
operator|=
literal|false
expr_stmt|;
name|longsSize
operator|=
name|wrappedPostingsWriter
operator|.
name|setField
argument_list|(
name|fieldInfo
argument_list|)
expr_stmt|;
name|longs
operator|=
operator|new
name|long
index|[
name|longsSize
index|]
expr_stmt|;
name|fields
operator|.
name|add
argument_list|(
operator|new
name|FieldMetaData
argument_list|(
name|fieldInfo
operator|.
name|number
argument_list|,
name|longsSize
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|0
return|;
comment|//DEBUG = BlockTreeTermsWriter.DEBUG;
block|}
DECL|field|DEBUG
specifier|private
name|boolean
name|DEBUG
decl_stmt|;
annotation|@
name|Override
DECL|method|startDoc
specifier|public
name|void
name|startDoc
parameter_list|(
name|int
name|docID
parameter_list|,
name|int
name|termDocFreq
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|docID
operator|>=
literal|0
operator|:
literal|"got docID="
operator|+
name|docID
assert|;
comment|/*     if (termID != -1) {       if (docID == 0) {         baseDocID = termID;       } else if (baseDocID + docID != termID) {         throw new RuntimeException("WRITE: baseDocID=" + baseDocID + " docID=" + docID + " termID=" + termID);       }     }     */
comment|//if (DEBUG) System.out.println("PW     doc=" + docID);
if|if
condition|(
name|pendingCount
operator|==
name|pending
operator|.
name|length
condition|)
block|{
name|push
argument_list|()
expr_stmt|;
comment|//if (DEBUG) System.out.println("PW: wrapped.finishDoc");
name|wrappedPostingsWriter
operator|.
name|finishDoc
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|pendingCount
operator|!=
operator|-
literal|1
condition|)
block|{
assert|assert
name|pendingCount
operator|<
name|pending
operator|.
name|length
assert|;
name|currentDoc
operator|=
name|pending
index|[
name|pendingCount
index|]
expr_stmt|;
name|currentDoc
operator|.
name|docID
operator|=
name|docID
expr_stmt|;
if|if
condition|(
name|indexOptions
operator|==
name|IndexOptions
operator|.
name|DOCS_ONLY
condition|)
block|{
name|pendingCount
operator|++
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|indexOptions
operator|==
name|IndexOptions
operator|.
name|DOCS_AND_FREQS
condition|)
block|{
name|pendingCount
operator|++
expr_stmt|;
name|currentDoc
operator|.
name|termFreq
operator|=
name|termDocFreq
expr_stmt|;
block|}
else|else
block|{
name|currentDoc
operator|.
name|termFreq
operator|=
name|termDocFreq
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// We've already seen too many docs for this term --
comment|// just forward to our fallback writer
name|wrappedPostingsWriter
operator|.
name|startDoc
argument_list|(
name|docID
argument_list|,
name|termDocFreq
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|addPosition
specifier|public
name|void
name|addPosition
parameter_list|(
name|int
name|position
parameter_list|,
name|BytesRef
name|payload
parameter_list|,
name|int
name|startOffset
parameter_list|,
name|int
name|endOffset
parameter_list|)
throws|throws
name|IOException
block|{
comment|//if (DEBUG) System.out.println("PW       pos=" + position + " payload=" + (payload == null ? "null" : payload.length + " bytes"));
if|if
condition|(
name|pendingCount
operator|==
name|pending
operator|.
name|length
condition|)
block|{
name|push
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|pendingCount
operator|==
operator|-
literal|1
condition|)
block|{
comment|// We've already seen too many docs for this term --
comment|// just forward to our fallback writer
name|wrappedPostingsWriter
operator|.
name|addPosition
argument_list|(
name|position
argument_list|,
name|payload
argument_list|,
name|startOffset
argument_list|,
name|endOffset
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// buffer up
specifier|final
name|Position
name|pos
init|=
name|pending
index|[
name|pendingCount
operator|++
index|]
decl_stmt|;
name|pos
operator|.
name|pos
operator|=
name|position
expr_stmt|;
name|pos
operator|.
name|startOffset
operator|=
name|startOffset
expr_stmt|;
name|pos
operator|.
name|endOffset
operator|=
name|endOffset
expr_stmt|;
name|pos
operator|.
name|docID
operator|=
name|currentDoc
operator|.
name|docID
expr_stmt|;
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
if|if
condition|(
name|pos
operator|.
name|payload
operator|==
literal|null
condition|)
block|{
name|pos
operator|.
name|payload
operator|=
name|BytesRef
operator|.
name|deepCopyOf
argument_list|(
name|payload
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|pos
operator|.
name|payload
operator|.
name|copyBytes
argument_list|(
name|payload
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|pos
operator|.
name|payload
operator|!=
literal|null
condition|)
block|{
name|pos
operator|.
name|payload
operator|.
name|length
operator|=
literal|0
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|finishDoc
specifier|public
name|void
name|finishDoc
parameter_list|()
throws|throws
name|IOException
block|{
comment|// if (DEBUG) System.out.println("PW     finishDoc");
if|if
condition|(
name|pendingCount
operator|==
operator|-
literal|1
condition|)
block|{
name|wrappedPostingsWriter
operator|.
name|finishDoc
argument_list|()
expr_stmt|;
block|}
block|}
DECL|field|buffer
specifier|private
specifier|final
name|RAMOutputStream
name|buffer
init|=
operator|new
name|RAMOutputStream
argument_list|()
decl_stmt|;
comment|// private int baseDocID;
comment|/** Called when we are done adding docs to this term */
annotation|@
name|Override
DECL|method|finishTerm
specifier|public
name|void
name|finishTerm
parameter_list|(
name|BlockTermState
name|_state
parameter_list|)
throws|throws
name|IOException
block|{
name|PulsingTermState
name|state
init|=
operator|(
name|PulsingTermState
operator|)
name|_state
decl_stmt|;
comment|// if (DEBUG) System.out.println("PW   finishTerm docCount=" + stats.docFreq + " pendingCount=" + pendingCount + " pendingTerms.size()=" + pendingTerms.size());
assert|assert
name|pendingCount
operator|>
literal|0
operator|||
name|pendingCount
operator|==
operator|-
literal|1
assert|;
if|if
condition|(
name|pendingCount
operator|==
operator|-
literal|1
condition|)
block|{
name|state
operator|.
name|wrappedState
operator|.
name|docFreq
operator|=
name|state
operator|.
name|docFreq
expr_stmt|;
name|state
operator|.
name|wrappedState
operator|.
name|totalTermFreq
operator|=
name|state
operator|.
name|totalTermFreq
expr_stmt|;
name|state
operator|.
name|bytes
operator|=
literal|null
expr_stmt|;
name|wrappedPostingsWriter
operator|.
name|finishTerm
argument_list|(
name|state
operator|.
name|wrappedState
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// There were few enough total occurrences for this
comment|// term, so we fully inline our postings data into
comment|// terms dict, now:
comment|// TODO: it'd be better to share this encoding logic
comment|// in some inner codec that knows how to write a
comment|// single doc / single position, etc.  This way if a
comment|// given codec wants to store other interesting
comment|// stuff, it could use this pulsing codec to do so
if|if
condition|(
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
condition|)
block|{
name|int
name|lastDocID
init|=
literal|0
decl_stmt|;
name|int
name|pendingIDX
init|=
literal|0
decl_stmt|;
name|int
name|lastPayloadLength
init|=
operator|-
literal|1
decl_stmt|;
name|int
name|lastOffsetLength
init|=
operator|-
literal|1
decl_stmt|;
while|while
condition|(
name|pendingIDX
operator|<
name|pendingCount
condition|)
block|{
specifier|final
name|Position
name|doc
init|=
name|pending
index|[
name|pendingIDX
index|]
decl_stmt|;
specifier|final
name|int
name|delta
init|=
name|doc
operator|.
name|docID
operator|-
name|lastDocID
decl_stmt|;
name|lastDocID
operator|=
name|doc
operator|.
name|docID
expr_stmt|;
comment|// if (DEBUG) System.out.println("  write doc=" + doc.docID + " freq=" + doc.termFreq);
if|if
condition|(
name|doc
operator|.
name|termFreq
operator|==
literal|1
condition|)
block|{
name|buffer
operator|.
name|writeVInt
argument_list|(
operator|(
name|delta
operator|<<
literal|1
operator|)
operator||
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|buffer
operator|.
name|writeVInt
argument_list|(
name|delta
operator|<<
literal|1
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|writeVInt
argument_list|(
name|doc
operator|.
name|termFreq
argument_list|)
expr_stmt|;
block|}
name|int
name|lastPos
init|=
literal|0
decl_stmt|;
name|int
name|lastOffset
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|posIDX
init|=
literal|0
init|;
name|posIDX
operator|<
name|doc
operator|.
name|termFreq
condition|;
name|posIDX
operator|++
control|)
block|{
specifier|final
name|Position
name|pos
init|=
name|pending
index|[
name|pendingIDX
operator|++
index|]
decl_stmt|;
assert|assert
name|pos
operator|.
name|docID
operator|==
name|doc
operator|.
name|docID
assert|;
specifier|final
name|int
name|posDelta
init|=
name|pos
operator|.
name|pos
operator|-
name|lastPos
decl_stmt|;
name|lastPos
operator|=
name|pos
operator|.
name|pos
expr_stmt|;
comment|// if (DEBUG) System.out.println("    write pos=" + pos.pos);
specifier|final
name|int
name|payloadLength
init|=
name|pos
operator|.
name|payload
operator|==
literal|null
condition|?
literal|0
else|:
name|pos
operator|.
name|payload
operator|.
name|length
decl_stmt|;
if|if
condition|(
name|storePayloads
condition|)
block|{
if|if
condition|(
name|payloadLength
operator|!=
name|lastPayloadLength
condition|)
block|{
name|buffer
operator|.
name|writeVInt
argument_list|(
operator|(
name|posDelta
operator|<<
literal|1
operator|)
operator||
literal|1
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|writeVInt
argument_list|(
name|payloadLength
argument_list|)
expr_stmt|;
name|lastPayloadLength
operator|=
name|payloadLength
expr_stmt|;
block|}
else|else
block|{
name|buffer
operator|.
name|writeVInt
argument_list|(
name|posDelta
operator|<<
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|buffer
operator|.
name|writeVInt
argument_list|(
name|posDelta
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
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
condition|)
block|{
comment|//System.out.println("write=" + pos.startOffset + "," + pos.endOffset);
name|int
name|offsetDelta
init|=
name|pos
operator|.
name|startOffset
operator|-
name|lastOffset
decl_stmt|;
name|int
name|offsetLength
init|=
name|pos
operator|.
name|endOffset
operator|-
name|pos
operator|.
name|startOffset
decl_stmt|;
if|if
condition|(
name|offsetLength
operator|!=
name|lastOffsetLength
condition|)
block|{
name|buffer
operator|.
name|writeVInt
argument_list|(
name|offsetDelta
operator|<<
literal|1
operator||
literal|1
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|writeVInt
argument_list|(
name|offsetLength
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|buffer
operator|.
name|writeVInt
argument_list|(
name|offsetDelta
operator|<<
literal|1
argument_list|)
expr_stmt|;
block|}
name|lastOffset
operator|=
name|pos
operator|.
name|startOffset
expr_stmt|;
name|lastOffsetLength
operator|=
name|offsetLength
expr_stmt|;
block|}
if|if
condition|(
name|payloadLength
operator|>
literal|0
condition|)
block|{
assert|assert
name|storePayloads
assert|;
name|buffer
operator|.
name|writeBytes
argument_list|(
name|pos
operator|.
name|payload
operator|.
name|bytes
argument_list|,
literal|0
argument_list|,
name|pos
operator|.
name|payload
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|indexOptions
operator|==
name|IndexOptions
operator|.
name|DOCS_AND_FREQS
condition|)
block|{
name|int
name|lastDocID
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|posIDX
init|=
literal|0
init|;
name|posIDX
operator|<
name|pendingCount
condition|;
name|posIDX
operator|++
control|)
block|{
specifier|final
name|Position
name|doc
init|=
name|pending
index|[
name|posIDX
index|]
decl_stmt|;
specifier|final
name|int
name|delta
init|=
name|doc
operator|.
name|docID
operator|-
name|lastDocID
decl_stmt|;
assert|assert
name|doc
operator|.
name|termFreq
operator|!=
literal|0
assert|;
if|if
condition|(
name|doc
operator|.
name|termFreq
operator|==
literal|1
condition|)
block|{
name|buffer
operator|.
name|writeVInt
argument_list|(
operator|(
name|delta
operator|<<
literal|1
operator|)
operator||
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|buffer
operator|.
name|writeVInt
argument_list|(
name|delta
operator|<<
literal|1
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|writeVInt
argument_list|(
name|doc
operator|.
name|termFreq
argument_list|)
expr_stmt|;
block|}
name|lastDocID
operator|=
name|doc
operator|.
name|docID
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|indexOptions
operator|==
name|IndexOptions
operator|.
name|DOCS_ONLY
condition|)
block|{
name|int
name|lastDocID
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|posIDX
init|=
literal|0
init|;
name|posIDX
operator|<
name|pendingCount
condition|;
name|posIDX
operator|++
control|)
block|{
specifier|final
name|Position
name|doc
init|=
name|pending
index|[
name|posIDX
index|]
decl_stmt|;
name|buffer
operator|.
name|writeVInt
argument_list|(
name|doc
operator|.
name|docID
operator|-
name|lastDocID
argument_list|)
expr_stmt|;
name|lastDocID
operator|=
name|doc
operator|.
name|docID
expr_stmt|;
block|}
block|}
name|state
operator|.
name|bytes
operator|=
operator|new
name|byte
index|[
operator|(
name|int
operator|)
name|buffer
operator|.
name|getFilePointer
argument_list|()
index|]
expr_stmt|;
name|buffer
operator|.
name|writeTo
argument_list|(
name|state
operator|.
name|bytes
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
name|pendingCount
operator|=
literal|0
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|encodeTerm
specifier|public
name|void
name|encodeTerm
parameter_list|(
name|long
index|[]
name|empty
parameter_list|,
name|DataOutput
name|out
parameter_list|,
name|FieldInfo
name|fieldInfo
parameter_list|,
name|BlockTermState
name|_state
parameter_list|,
name|boolean
name|absolute
parameter_list|)
throws|throws
name|IOException
block|{
name|PulsingTermState
name|state
init|=
operator|(
name|PulsingTermState
operator|)
name|_state
decl_stmt|;
assert|assert
name|empty
operator|.
name|length
operator|==
literal|0
assert|;
name|this
operator|.
name|absolute
operator|=
name|this
operator|.
name|absolute
operator|||
name|absolute
expr_stmt|;
if|if
condition|(
name|state
operator|.
name|bytes
operator|==
literal|null
condition|)
block|{
name|wrappedPostingsWriter
operator|.
name|encodeTerm
argument_list|(
name|longs
argument_list|,
name|buffer
argument_list|,
name|fieldInfo
argument_list|,
name|state
operator|.
name|wrappedState
argument_list|,
name|this
operator|.
name|absolute
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|longsSize
condition|;
name|i
operator|++
control|)
block|{
name|out
operator|.
name|writeVLong
argument_list|(
name|longs
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|buffer
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|reset
argument_list|()
expr_stmt|;
name|this
operator|.
name|absolute
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|writeVInt
argument_list|(
name|state
operator|.
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBytes
argument_list|(
name|state
operator|.
name|bytes
argument_list|,
literal|0
argument_list|,
name|state
operator|.
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
name|this
operator|.
name|absolute
operator|=
name|this
operator|.
name|absolute
operator|||
name|absolute
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|wrappedPostingsWriter
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|wrappedPostingsWriter
operator|instanceof
name|PulsingPostingsWriter
operator|||
name|VERSION_CURRENT
operator|<
name|VERSION_META_ARRAY
condition|)
block|{
return|return;
block|}
name|String
name|summaryFileName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segmentState
operator|.
name|segmentInfo
operator|.
name|name
argument_list|,
name|segmentState
operator|.
name|segmentSuffix
argument_list|,
name|SUMMARY_EXTENSION
argument_list|)
decl_stmt|;
name|IndexOutput
name|out
init|=
literal|null
decl_stmt|;
try|try
block|{
name|out
operator|=
name|segmentState
operator|.
name|directory
operator|.
name|createOutput
argument_list|(
name|summaryFileName
argument_list|,
name|segmentState
operator|.
name|context
argument_list|)
expr_stmt|;
name|CodecUtil
operator|.
name|writeHeader
argument_list|(
name|out
argument_list|,
name|CODEC
argument_list|,
name|VERSION_CURRENT
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|fields
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|FieldMetaData
name|field
range|:
name|fields
control|)
block|{
name|out
operator|.
name|writeVInt
argument_list|(
name|field
operator|.
name|fieldNumber
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|field
operator|.
name|longsSize
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Pushes pending positions to the wrapped codec
DECL|method|push
specifier|private
name|void
name|push
parameter_list|()
throws|throws
name|IOException
block|{
comment|// if (DEBUG) System.out.println("PW now push @ " + pendingCount + " wrapped=" + wrappedPostingsWriter);
assert|assert
name|pendingCount
operator|==
name|pending
operator|.
name|length
assert|;
name|wrappedPostingsWriter
operator|.
name|startTerm
argument_list|()
expr_stmt|;
comment|// Flush all buffered docs
if|if
condition|(
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
condition|)
block|{
name|Position
name|doc
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Position
name|pos
range|:
name|pending
control|)
block|{
if|if
condition|(
name|doc
operator|==
literal|null
condition|)
block|{
name|doc
operator|=
name|pos
expr_stmt|;
comment|// if (DEBUG) System.out.println("PW: wrapped.startDoc docID=" + doc.docID + " tf=" + doc.termFreq);
name|wrappedPostingsWriter
operator|.
name|startDoc
argument_list|(
name|doc
operator|.
name|docID
argument_list|,
name|doc
operator|.
name|termFreq
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|doc
operator|.
name|docID
operator|!=
name|pos
operator|.
name|docID
condition|)
block|{
assert|assert
name|pos
operator|.
name|docID
operator|>
name|doc
operator|.
name|docID
assert|;
comment|// if (DEBUG) System.out.println("PW: wrapped.finishDoc");
name|wrappedPostingsWriter
operator|.
name|finishDoc
argument_list|()
expr_stmt|;
name|doc
operator|=
name|pos
expr_stmt|;
comment|// if (DEBUG) System.out.println("PW: wrapped.startDoc docID=" + doc.docID + " tf=" + doc.termFreq);
name|wrappedPostingsWriter
operator|.
name|startDoc
argument_list|(
name|doc
operator|.
name|docID
argument_list|,
name|doc
operator|.
name|termFreq
argument_list|)
expr_stmt|;
block|}
comment|// if (DEBUG) System.out.println("PW:   wrapped.addPos pos=" + pos.pos);
name|wrappedPostingsWriter
operator|.
name|addPosition
argument_list|(
name|pos
operator|.
name|pos
argument_list|,
name|pos
operator|.
name|payload
argument_list|,
name|pos
operator|.
name|startOffset
argument_list|,
name|pos
operator|.
name|endOffset
argument_list|)
expr_stmt|;
block|}
comment|//wrappedPostingsWriter.finishDoc();
block|}
else|else
block|{
for|for
control|(
name|Position
name|doc
range|:
name|pending
control|)
block|{
name|wrappedPostingsWriter
operator|.
name|startDoc
argument_list|(
name|doc
operator|.
name|docID
argument_list|,
name|indexOptions
operator|==
name|IndexOptions
operator|.
name|DOCS_ONLY
condition|?
literal|0
else|:
name|doc
operator|.
name|termFreq
argument_list|)
expr_stmt|;
block|}
block|}
name|pendingCount
operator|=
operator|-
literal|1
expr_stmt|;
block|}
block|}
end_class

end_unit

