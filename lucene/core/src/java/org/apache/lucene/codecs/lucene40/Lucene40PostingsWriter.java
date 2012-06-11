begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.lucene40
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene40
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/** Consumes doc& freq, writing them using the current  *  index file format */
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
name|CorruptIndexException
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
name|util
operator|.
name|IOUtils
import|;
end_import

begin_comment
comment|/**  * Concrete class that writes the 4.0 frq/prx postings format.  *   * @see Lucene40PostingsFormat  * @lucene.experimental   */
end_comment

begin_class
DECL|class|Lucene40PostingsWriter
specifier|public
specifier|final
class|class
name|Lucene40PostingsWriter
extends|extends
name|PostingsWriterBase
block|{
DECL|field|TERMS_CODEC
specifier|final
specifier|static
name|String
name|TERMS_CODEC
init|=
literal|"Lucene40PostingsWriterTerms"
decl_stmt|;
DECL|field|FRQ_CODEC
specifier|final
specifier|static
name|String
name|FRQ_CODEC
init|=
literal|"Lucene40PostingsWriterFrq"
decl_stmt|;
DECL|field|PRX_CODEC
specifier|final
specifier|static
name|String
name|PRX_CODEC
init|=
literal|"Lucene40PostingsWriterPrx"
decl_stmt|;
comment|//private static boolean DEBUG = BlockTreeTermsWriter.DEBUG;
comment|// Increment version to change it:
DECL|field|VERSION_START
specifier|final
specifier|static
name|int
name|VERSION_START
init|=
literal|0
decl_stmt|;
DECL|field|VERSION_CURRENT
specifier|final
specifier|static
name|int
name|VERSION_CURRENT
init|=
name|VERSION_START
decl_stmt|;
DECL|field|freqOut
specifier|final
name|IndexOutput
name|freqOut
decl_stmt|;
DECL|field|proxOut
specifier|final
name|IndexOutput
name|proxOut
decl_stmt|;
DECL|field|skipListWriter
specifier|final
name|Lucene40SkipListWriter
name|skipListWriter
decl_stmt|;
comment|/** Expert: The fraction of TermDocs entries stored in skip tables,    * used to accelerate {@link DocsEnum#advance(int)}.  Larger values result in    * smaller indexes, greater acceleration, but fewer accelerable cases, while    * smaller values result in bigger indexes, less acceleration and more    * accelerable cases. More detailed experiments would be useful here. */
DECL|field|DEFAULT_SKIP_INTERVAL
specifier|static
specifier|final
name|int
name|DEFAULT_SKIP_INTERVAL
init|=
literal|16
decl_stmt|;
DECL|field|skipInterval
specifier|final
name|int
name|skipInterval
decl_stmt|;
comment|/**    * Expert: minimum docFreq to write any skip data at all    */
DECL|field|skipMinimum
specifier|final
name|int
name|skipMinimum
decl_stmt|;
comment|/** Expert: The maximum number of skip levels. Smaller values result in     * slightly smaller indexes, but slower skipping in big posting lists.    */
DECL|field|maxSkipLevels
specifier|final
name|int
name|maxSkipLevels
init|=
literal|10
decl_stmt|;
DECL|field|totalNumDocs
specifier|final
name|int
name|totalNumDocs
decl_stmt|;
DECL|field|termsOut
name|IndexOutput
name|termsOut
decl_stmt|;
DECL|field|indexOptions
name|IndexOptions
name|indexOptions
decl_stmt|;
DECL|field|storePayloads
name|boolean
name|storePayloads
decl_stmt|;
DECL|field|storeOffsets
name|boolean
name|storeOffsets
decl_stmt|;
comment|// Starts a new term
DECL|field|freqStart
name|long
name|freqStart
decl_stmt|;
DECL|field|proxStart
name|long
name|proxStart
decl_stmt|;
DECL|field|fieldInfo
name|FieldInfo
name|fieldInfo
decl_stmt|;
DECL|field|lastPayloadLength
name|int
name|lastPayloadLength
decl_stmt|;
DECL|field|lastOffsetLength
name|int
name|lastOffsetLength
decl_stmt|;
DECL|field|lastPosition
name|int
name|lastPosition
decl_stmt|;
DECL|field|lastOffset
name|int
name|lastOffset
decl_stmt|;
comment|// private String segment;
DECL|method|Lucene40PostingsWriter
specifier|public
name|Lucene40PostingsWriter
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|state
argument_list|,
name|DEFAULT_SKIP_INTERVAL
argument_list|)
expr_stmt|;
block|}
DECL|method|Lucene40PostingsWriter
specifier|public
name|Lucene40PostingsWriter
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|,
name|int
name|skipInterval
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|skipInterval
operator|=
name|skipInterval
expr_stmt|;
name|this
operator|.
name|skipMinimum
operator|=
name|skipInterval
expr_stmt|;
comment|/* set to the same for now */
comment|// this.segment = state.segmentName;
name|String
name|fileName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|state
operator|.
name|segmentInfo
operator|.
name|name
argument_list|,
name|state
operator|.
name|segmentSuffix
argument_list|,
name|Lucene40PostingsFormat
operator|.
name|FREQ_EXTENSION
argument_list|)
decl_stmt|;
name|freqOut
operator|=
name|state
operator|.
name|directory
operator|.
name|createOutput
argument_list|(
name|fileName
argument_list|,
name|state
operator|.
name|context
argument_list|)
expr_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
name|IndexOutput
name|proxOut
init|=
literal|null
decl_stmt|;
try|try
block|{
name|CodecUtil
operator|.
name|writeHeader
argument_list|(
name|freqOut
argument_list|,
name|FRQ_CODEC
argument_list|,
name|VERSION_CURRENT
argument_list|)
expr_stmt|;
comment|// TODO: this is a best effort, if one of these fields has no postings
comment|// then we make an empty prx file, same as if we are wrapped in
comment|// per-field postingsformat. maybe... we shouldn't
comment|// bother w/ this opto?  just create empty prx file...?
if|if
condition|(
name|state
operator|.
name|fieldInfos
operator|.
name|hasProx
argument_list|()
condition|)
block|{
comment|// At least one field does not omit TF, so create the
comment|// prox file
name|fileName
operator|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|state
operator|.
name|segmentInfo
operator|.
name|name
argument_list|,
name|state
operator|.
name|segmentSuffix
argument_list|,
name|Lucene40PostingsFormat
operator|.
name|PROX_EXTENSION
argument_list|)
expr_stmt|;
name|proxOut
operator|=
name|state
operator|.
name|directory
operator|.
name|createOutput
argument_list|(
name|fileName
argument_list|,
name|state
operator|.
name|context
argument_list|)
expr_stmt|;
name|CodecUtil
operator|.
name|writeHeader
argument_list|(
name|proxOut
argument_list|,
name|PRX_CODEC
argument_list|,
name|VERSION_CURRENT
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Every field omits TF so we will write no prox file
name|proxOut
operator|=
literal|null
expr_stmt|;
block|}
name|this
operator|.
name|proxOut
operator|=
name|proxOut
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
operator|!
name|success
condition|)
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|freqOut
argument_list|,
name|proxOut
argument_list|)
expr_stmt|;
block|}
block|}
name|totalNumDocs
operator|=
name|state
operator|.
name|segmentInfo
operator|.
name|getDocCount
argument_list|()
expr_stmt|;
name|skipListWriter
operator|=
operator|new
name|Lucene40SkipListWriter
argument_list|(
name|skipInterval
argument_list|,
name|maxSkipLevels
argument_list|,
name|totalNumDocs
argument_list|,
name|freqOut
argument_list|,
name|proxOut
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|start
specifier|public
name|void
name|start
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
name|TERMS_CODEC
argument_list|,
name|VERSION_CURRENT
argument_list|)
expr_stmt|;
name|termsOut
operator|.
name|writeInt
argument_list|(
name|skipInterval
argument_list|)
expr_stmt|;
comment|// write skipInterval
name|termsOut
operator|.
name|writeInt
argument_list|(
name|maxSkipLevels
argument_list|)
expr_stmt|;
comment|// write maxSkipLevels
name|termsOut
operator|.
name|writeInt
argument_list|(
name|skipMinimum
argument_list|)
expr_stmt|;
comment|// write skipMinimum
block|}
annotation|@
name|Override
DECL|method|startTerm
specifier|public
name|void
name|startTerm
parameter_list|()
block|{
name|freqStart
operator|=
name|freqOut
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
comment|//if (DEBUG) System.out.println("SPW: startTerm freqOut.fp=" + freqStart);
if|if
condition|(
name|proxOut
operator|!=
literal|null
condition|)
block|{
name|proxStart
operator|=
name|proxOut
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
block|}
comment|// force first payload to write its length
name|lastPayloadLength
operator|=
operator|-
literal|1
expr_stmt|;
comment|// force first offset to write its length
name|lastOffsetLength
operator|=
operator|-
literal|1
expr_stmt|;
name|skipListWriter
operator|.
name|resetSkip
argument_list|()
expr_stmt|;
block|}
comment|// Currently, this instance is re-used across fields, so
comment|// our parent calls setField whenever the field changes
annotation|@
name|Override
DECL|method|setField
specifier|public
name|void
name|setField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|)
block|{
comment|//System.out.println("SPW: setField");
comment|/*     if (BlockTreeTermsWriter.DEBUG&& fieldInfo.name.equals("id")) {       DEBUG = true;     } else {       DEBUG = false;     }     */
name|this
operator|.
name|fieldInfo
operator|=
name|fieldInfo
expr_stmt|;
name|indexOptions
operator|=
name|fieldInfo
operator|.
name|getIndexOptions
argument_list|()
expr_stmt|;
name|storeOffsets
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
name|storePayloads
operator|=
name|fieldInfo
operator|.
name|hasPayloads
argument_list|()
expr_stmt|;
comment|//System.out.println("  set init blockFreqStart=" + freqStart);
comment|//System.out.println("  set init blockProxStart=" + proxStart);
block|}
DECL|field|lastDocID
name|int
name|lastDocID
decl_stmt|;
DECL|field|df
name|int
name|df
decl_stmt|;
comment|/** Adds a new doc in this term.  If this returns null    *  then we just skip consuming positions/payloads. */
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
comment|// if (DEBUG) System.out.println("SPW:   startDoc seg=" + segment + " docID=" + docID + " tf=" + termDocFreq + " freqOut.fp=" + freqOut.getFilePointer());
specifier|final
name|int
name|delta
init|=
name|docID
operator|-
name|lastDocID
decl_stmt|;
if|if
condition|(
name|docID
operator|<
literal|0
operator|||
operator|(
name|df
operator|>
literal|0
operator|&&
name|delta
operator|<=
literal|0
operator|)
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"docs out of order ("
operator|+
name|docID
operator|+
literal|"<= "
operator|+
name|lastDocID
operator|+
literal|" ) (freqOut: "
operator|+
name|freqOut
operator|+
literal|")"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|(
operator|++
name|df
operator|%
name|skipInterval
operator|)
operator|==
literal|0
condition|)
block|{
name|skipListWriter
operator|.
name|setSkipData
argument_list|(
name|lastDocID
argument_list|,
name|storePayloads
argument_list|,
name|lastPayloadLength
argument_list|,
name|storeOffsets
argument_list|,
name|lastOffsetLength
argument_list|)
expr_stmt|;
name|skipListWriter
operator|.
name|bufferSkip
argument_list|(
name|df
argument_list|)
expr_stmt|;
block|}
assert|assert
name|docID
operator|<
name|totalNumDocs
operator|:
literal|"docID="
operator|+
name|docID
operator|+
literal|" totalNumDocs="
operator|+
name|totalNumDocs
assert|;
name|lastDocID
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
name|freqOut
operator|.
name|writeVInt
argument_list|(
name|delta
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|1
operator|==
name|termDocFreq
condition|)
block|{
name|freqOut
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
name|freqOut
operator|.
name|writeVInt
argument_list|(
name|delta
operator|<<
literal|1
argument_list|)
expr_stmt|;
name|freqOut
operator|.
name|writeVInt
argument_list|(
name|termDocFreq
argument_list|)
expr_stmt|;
block|}
name|lastPosition
operator|=
literal|0
expr_stmt|;
name|lastOffset
operator|=
literal|0
expr_stmt|;
block|}
comment|/** Add a new position& payload */
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
comment|//if (DEBUG) System.out.println("SPW:     addPos pos=" + position + " payload=" + (payload == null ? "null" : (payload.length + " bytes")) + " proxFP=" + proxOut.getFilePointer());
assert|assert
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
operator|:
literal|"invalid indexOptions: "
operator|+
name|indexOptions
assert|;
assert|assert
name|proxOut
operator|!=
literal|null
assert|;
specifier|final
name|int
name|delta
init|=
name|position
operator|-
name|lastPosition
decl_stmt|;
assert|assert
name|delta
operator|>=
literal|0
operator|:
literal|"position="
operator|+
name|position
operator|+
literal|" lastPosition="
operator|+
name|lastPosition
assert|;
comment|// not quite right (if pos=0 is repeated twice we don't catch it)
name|lastPosition
operator|=
name|position
expr_stmt|;
name|int
name|payloadLength
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|storePayloads
condition|)
block|{
name|payloadLength
operator|=
name|payload
operator|==
literal|null
condition|?
literal|0
else|:
name|payload
operator|.
name|length
expr_stmt|;
if|if
condition|(
name|payloadLength
operator|!=
name|lastPayloadLength
condition|)
block|{
name|lastPayloadLength
operator|=
name|payloadLength
expr_stmt|;
name|proxOut
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
name|proxOut
operator|.
name|writeVInt
argument_list|(
name|payloadLength
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|proxOut
operator|.
name|writeVInt
argument_list|(
name|delta
operator|<<
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|proxOut
operator|.
name|writeVInt
argument_list|(
name|delta
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|storeOffsets
condition|)
block|{
comment|// don't use startOffset - lastEndOffset, because this creates lots of negative vints for synonyms,
comment|// and the numbers aren't that much smaller anyways.
name|int
name|offsetDelta
init|=
name|startOffset
operator|-
name|lastOffset
decl_stmt|;
name|int
name|offsetLength
init|=
name|endOffset
operator|-
name|startOffset
decl_stmt|;
if|if
condition|(
name|offsetLength
operator|!=
name|lastOffsetLength
condition|)
block|{
name|proxOut
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
name|proxOut
operator|.
name|writeVInt
argument_list|(
name|offsetLength
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|proxOut
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
name|proxOut
operator|.
name|writeBytes
argument_list|(
name|payload
operator|.
name|bytes
argument_list|,
name|payload
operator|.
name|offset
argument_list|,
name|payloadLength
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|finishDoc
specifier|public
name|void
name|finishDoc
parameter_list|()
block|{   }
DECL|class|PendingTerm
specifier|private
specifier|static
class|class
name|PendingTerm
block|{
DECL|field|freqStart
specifier|public
specifier|final
name|long
name|freqStart
decl_stmt|;
DECL|field|proxStart
specifier|public
specifier|final
name|long
name|proxStart
decl_stmt|;
DECL|field|skipOffset
specifier|public
specifier|final
name|int
name|skipOffset
decl_stmt|;
DECL|method|PendingTerm
specifier|public
name|PendingTerm
parameter_list|(
name|long
name|freqStart
parameter_list|,
name|long
name|proxStart
parameter_list|,
name|int
name|skipOffset
parameter_list|)
block|{
name|this
operator|.
name|freqStart
operator|=
name|freqStart
expr_stmt|;
name|this
operator|.
name|proxStart
operator|=
name|proxStart
expr_stmt|;
name|this
operator|.
name|skipOffset
operator|=
name|skipOffset
expr_stmt|;
block|}
block|}
DECL|field|pendingTerms
specifier|private
specifier|final
name|List
argument_list|<
name|PendingTerm
argument_list|>
name|pendingTerms
init|=
operator|new
name|ArrayList
argument_list|<
name|PendingTerm
argument_list|>
argument_list|()
decl_stmt|;
comment|/** Called when we are done adding docs to this term */
annotation|@
name|Override
DECL|method|finishTerm
specifier|public
name|void
name|finishTerm
parameter_list|(
name|TermStats
name|stats
parameter_list|)
throws|throws
name|IOException
block|{
comment|// if (DEBUG) System.out.println("SPW: finishTerm seg=" + segment + " freqStart=" + freqStart);
assert|assert
name|stats
operator|.
name|docFreq
operator|>
literal|0
assert|;
comment|// TODO: wasteful we are counting this (counting # docs
comment|// for this term) in two places?
assert|assert
name|stats
operator|.
name|docFreq
operator|==
name|df
assert|;
specifier|final
name|int
name|skipOffset
decl_stmt|;
if|if
condition|(
name|df
operator|>=
name|skipMinimum
condition|)
block|{
name|skipOffset
operator|=
call|(
name|int
call|)
argument_list|(
name|skipListWriter
operator|.
name|writeSkip
argument_list|(
name|freqOut
argument_list|)
operator|-
name|freqStart
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|skipOffset
operator|=
operator|-
literal|1
expr_stmt|;
block|}
name|pendingTerms
operator|.
name|add
argument_list|(
operator|new
name|PendingTerm
argument_list|(
name|freqStart
argument_list|,
name|proxStart
argument_list|,
name|skipOffset
argument_list|)
argument_list|)
expr_stmt|;
name|lastDocID
operator|=
literal|0
expr_stmt|;
name|df
operator|=
literal|0
expr_stmt|;
block|}
DECL|field|bytesWriter
specifier|private
specifier|final
name|RAMOutputStream
name|bytesWriter
init|=
operator|new
name|RAMOutputStream
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|flushTermsBlock
specifier|public
name|void
name|flushTermsBlock
parameter_list|(
name|int
name|start
parameter_list|,
name|int
name|count
parameter_list|)
throws|throws
name|IOException
block|{
comment|//if (DEBUG) System.out.println("SPW: flushTermsBlock start=" + start + " count=" + count + " left=" + (pendingTerms.size()-count) + " pendingTerms.size()=" + pendingTerms.size());
if|if
condition|(
name|count
operator|==
literal|0
condition|)
block|{
name|termsOut
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|0
argument_list|)
expr_stmt|;
return|return;
block|}
assert|assert
name|start
operator|<=
name|pendingTerms
operator|.
name|size
argument_list|()
assert|;
assert|assert
name|count
operator|<=
name|start
assert|;
specifier|final
name|int
name|limit
init|=
name|pendingTerms
operator|.
name|size
argument_list|()
operator|-
name|start
operator|+
name|count
decl_stmt|;
specifier|final
name|PendingTerm
name|firstTerm
init|=
name|pendingTerms
operator|.
name|get
argument_list|(
name|limit
operator|-
name|count
argument_list|)
decl_stmt|;
comment|// First term in block is abs coded:
name|bytesWriter
operator|.
name|writeVLong
argument_list|(
name|firstTerm
operator|.
name|freqStart
argument_list|)
expr_stmt|;
if|if
condition|(
name|firstTerm
operator|.
name|skipOffset
operator|!=
operator|-
literal|1
condition|)
block|{
assert|assert
name|firstTerm
operator|.
name|skipOffset
operator|>
literal|0
assert|;
name|bytesWriter
operator|.
name|writeVInt
argument_list|(
name|firstTerm
operator|.
name|skipOffset
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
name|DOCS_AND_FREQS_AND_POSITIONS
argument_list|)
operator|>=
literal|0
condition|)
block|{
name|bytesWriter
operator|.
name|writeVLong
argument_list|(
name|firstTerm
operator|.
name|proxStart
argument_list|)
expr_stmt|;
block|}
name|long
name|lastFreqStart
init|=
name|firstTerm
operator|.
name|freqStart
decl_stmt|;
name|long
name|lastProxStart
init|=
name|firstTerm
operator|.
name|proxStart
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
name|limit
operator|-
name|count
operator|+
literal|1
init|;
name|idx
operator|<
name|limit
condition|;
name|idx
operator|++
control|)
block|{
specifier|final
name|PendingTerm
name|term
init|=
name|pendingTerms
operator|.
name|get
argument_list|(
name|idx
argument_list|)
decl_stmt|;
comment|//if (DEBUG) System.out.println("  write term freqStart=" + term.freqStart);
comment|// The rest of the terms term are delta coded:
name|bytesWriter
operator|.
name|writeVLong
argument_list|(
name|term
operator|.
name|freqStart
operator|-
name|lastFreqStart
argument_list|)
expr_stmt|;
name|lastFreqStart
operator|=
name|term
operator|.
name|freqStart
expr_stmt|;
if|if
condition|(
name|term
operator|.
name|skipOffset
operator|!=
operator|-
literal|1
condition|)
block|{
assert|assert
name|term
operator|.
name|skipOffset
operator|>
literal|0
assert|;
name|bytesWriter
operator|.
name|writeVInt
argument_list|(
name|term
operator|.
name|skipOffset
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
name|DOCS_AND_FREQS_AND_POSITIONS
argument_list|)
operator|>=
literal|0
condition|)
block|{
name|bytesWriter
operator|.
name|writeVLong
argument_list|(
name|term
operator|.
name|proxStart
operator|-
name|lastProxStart
argument_list|)
expr_stmt|;
name|lastProxStart
operator|=
name|term
operator|.
name|proxStart
expr_stmt|;
block|}
block|}
name|termsOut
operator|.
name|writeVInt
argument_list|(
operator|(
name|int
operator|)
name|bytesWriter
operator|.
name|getFilePointer
argument_list|()
argument_list|)
expr_stmt|;
name|bytesWriter
operator|.
name|writeTo
argument_list|(
name|termsOut
argument_list|)
expr_stmt|;
name|bytesWriter
operator|.
name|reset
argument_list|()
expr_stmt|;
comment|// Remove the terms we just wrote:
name|pendingTerms
operator|.
name|subList
argument_list|(
name|limit
operator|-
name|count
argument_list|,
name|limit
argument_list|)
operator|.
name|clear
argument_list|()
expr_stmt|;
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
try|try
block|{
name|freqOut
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|proxOut
operator|!=
literal|null
condition|)
block|{
name|proxOut
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

