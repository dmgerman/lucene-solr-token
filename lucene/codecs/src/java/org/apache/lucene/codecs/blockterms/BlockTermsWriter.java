begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.blockterms
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|blockterms
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
name|Closeable
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
name|ArrayList
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
name|FieldInfos
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
name|Fields
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
name|index
operator|.
name|Terms
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
name|ArrayUtil
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
name|FixedBitSet
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
comment|// TODO: currently we encode all terms between two indexed
end_comment

begin_comment
comment|// terms as a block; but, we could decouple the two, ie
end_comment

begin_comment
comment|// allow several blocks in between two indexed terms
end_comment

begin_comment
comment|/**  * Writes terms dict, block-encoding (column stride) each  * term's metadata for each set of terms between two  * index terms.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|BlockTermsWriter
specifier|public
class|class
name|BlockTermsWriter
extends|extends
name|FieldsConsumer
implements|implements
name|Closeable
block|{
DECL|field|CODEC_NAME
specifier|final
specifier|static
name|String
name|CODEC_NAME
init|=
literal|"BlockTermsWriter"
decl_stmt|;
comment|// Initial format
DECL|field|VERSION_START
specifier|public
specifier|static
specifier|final
name|int
name|VERSION_START
init|=
literal|4
decl_stmt|;
DECL|field|VERSION_CURRENT
specifier|public
specifier|static
specifier|final
name|int
name|VERSION_CURRENT
init|=
name|VERSION_START
decl_stmt|;
comment|/** Extension of terms file */
DECL|field|TERMS_EXTENSION
specifier|static
specifier|final
name|String
name|TERMS_EXTENSION
init|=
literal|"tib"
decl_stmt|;
DECL|field|out
specifier|protected
name|IndexOutput
name|out
decl_stmt|;
DECL|field|postingsWriter
specifier|final
name|PostingsWriterBase
name|postingsWriter
decl_stmt|;
DECL|field|fieldInfos
specifier|final
name|FieldInfos
name|fieldInfos
decl_stmt|;
DECL|field|currentField
name|FieldInfo
name|currentField
decl_stmt|;
DECL|field|termsIndexWriter
specifier|private
specifier|final
name|TermsIndexWriterBase
name|termsIndexWriter
decl_stmt|;
DECL|field|maxDoc
specifier|private
specifier|final
name|int
name|maxDoc
decl_stmt|;
DECL|class|FieldMetaData
specifier|private
specifier|static
class|class
name|FieldMetaData
block|{
DECL|field|fieldInfo
specifier|public
specifier|final
name|FieldInfo
name|fieldInfo
decl_stmt|;
DECL|field|numTerms
specifier|public
specifier|final
name|long
name|numTerms
decl_stmt|;
DECL|field|termsStartPointer
specifier|public
specifier|final
name|long
name|termsStartPointer
decl_stmt|;
DECL|field|sumTotalTermFreq
specifier|public
specifier|final
name|long
name|sumTotalTermFreq
decl_stmt|;
DECL|field|sumDocFreq
specifier|public
specifier|final
name|long
name|sumDocFreq
decl_stmt|;
DECL|field|docCount
specifier|public
specifier|final
name|int
name|docCount
decl_stmt|;
DECL|field|longsSize
specifier|public
specifier|final
name|int
name|longsSize
decl_stmt|;
DECL|method|FieldMetaData
specifier|public
name|FieldMetaData
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|long
name|numTerms
parameter_list|,
name|long
name|termsStartPointer
parameter_list|,
name|long
name|sumTotalTermFreq
parameter_list|,
name|long
name|sumDocFreq
parameter_list|,
name|int
name|docCount
parameter_list|,
name|int
name|longsSize
parameter_list|)
block|{
assert|assert
name|numTerms
operator|>
literal|0
assert|;
name|this
operator|.
name|fieldInfo
operator|=
name|fieldInfo
expr_stmt|;
name|this
operator|.
name|termsStartPointer
operator|=
name|termsStartPointer
expr_stmt|;
name|this
operator|.
name|numTerms
operator|=
name|numTerms
expr_stmt|;
name|this
operator|.
name|sumTotalTermFreq
operator|=
name|sumTotalTermFreq
expr_stmt|;
name|this
operator|.
name|sumDocFreq
operator|=
name|sumDocFreq
expr_stmt|;
name|this
operator|.
name|docCount
operator|=
name|docCount
expr_stmt|;
name|this
operator|.
name|longsSize
operator|=
name|longsSize
expr_stmt|;
block|}
block|}
DECL|field|fields
specifier|private
specifier|final
name|List
argument_list|<
name|FieldMetaData
argument_list|>
name|fields
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// private final String segment;
DECL|method|BlockTermsWriter
specifier|public
name|BlockTermsWriter
parameter_list|(
name|TermsIndexWriterBase
name|termsIndexWriter
parameter_list|,
name|SegmentWriteState
name|state
parameter_list|,
name|PostingsWriterBase
name|postingsWriter
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|termsFileName
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
name|TERMS_EXTENSION
argument_list|)
decl_stmt|;
name|this
operator|.
name|termsIndexWriter
operator|=
name|termsIndexWriter
expr_stmt|;
name|maxDoc
operator|=
name|state
operator|.
name|segmentInfo
operator|.
name|getDocCount
argument_list|()
expr_stmt|;
name|out
operator|=
name|state
operator|.
name|directory
operator|.
name|createOutput
argument_list|(
name|termsFileName
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
try|try
block|{
name|fieldInfos
operator|=
name|state
operator|.
name|fieldInfos
expr_stmt|;
name|CodecUtil
operator|.
name|writeIndexHeader
argument_list|(
name|out
argument_list|,
name|CODEC_NAME
argument_list|,
name|VERSION_CURRENT
argument_list|,
name|state
operator|.
name|segmentInfo
operator|.
name|getId
argument_list|()
argument_list|,
name|state
operator|.
name|segmentSuffix
argument_list|)
expr_stmt|;
name|currentField
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|postingsWriter
operator|=
name|postingsWriter
expr_stmt|;
comment|// segment = state.segmentName;
comment|//System.out.println("BTW.init seg=" + state.segmentName);
name|postingsWriter
operator|.
name|init
argument_list|(
name|out
argument_list|,
name|state
argument_list|)
expr_stmt|;
comment|// have consumer write its format/header
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
name|out
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|Fields
name|fields
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|String
name|field
range|:
name|fields
control|)
block|{
name|Terms
name|terms
init|=
name|fields
operator|.
name|terms
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|TermsEnum
name|termsEnum
init|=
name|terms
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|TermsWriter
name|termsWriter
init|=
name|addField
argument_list|(
name|fieldInfos
operator|.
name|fieldInfo
argument_list|(
name|field
argument_list|)
argument_list|)
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|BytesRef
name|term
init|=
name|termsEnum
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|term
operator|==
literal|null
condition|)
block|{
break|break;
block|}
name|termsWriter
operator|.
name|write
argument_list|(
name|term
argument_list|,
name|termsEnum
argument_list|)
expr_stmt|;
block|}
name|termsWriter
operator|.
name|finish
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|addField
specifier|private
name|TermsWriter
name|addField
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
comment|//System.out.println("\nBTW.addField seg=" + segment + " field=" + field.name);
assert|assert
name|currentField
operator|==
literal|null
operator|||
name|currentField
operator|.
name|name
operator|.
name|compareTo
argument_list|(
name|field
operator|.
name|name
argument_list|)
operator|<
literal|0
assert|;
name|currentField
operator|=
name|field
expr_stmt|;
name|TermsIndexWriterBase
operator|.
name|FieldWriter
name|fieldIndexWriter
init|=
name|termsIndexWriter
operator|.
name|addField
argument_list|(
name|field
argument_list|,
name|out
operator|.
name|getFilePointer
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|TermsWriter
argument_list|(
name|fieldIndexWriter
argument_list|,
name|field
argument_list|,
name|postingsWriter
argument_list|)
return|;
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
if|if
condition|(
name|out
operator|!=
literal|null
condition|)
block|{
try|try
block|{
specifier|final
name|long
name|dirStart
init|=
name|out
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
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
name|fieldInfo
operator|.
name|number
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVLong
argument_list|(
name|field
operator|.
name|numTerms
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVLong
argument_list|(
name|field
operator|.
name|termsStartPointer
argument_list|)
expr_stmt|;
if|if
condition|(
name|field
operator|.
name|fieldInfo
operator|.
name|getIndexOptions
argument_list|()
operator|!=
name|IndexOptions
operator|.
name|DOCS
condition|)
block|{
name|out
operator|.
name|writeVLong
argument_list|(
name|field
operator|.
name|sumTotalTermFreq
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|writeVLong
argument_list|(
name|field
operator|.
name|sumDocFreq
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|field
operator|.
name|docCount
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
name|writeTrailer
argument_list|(
name|dirStart
argument_list|)
expr_stmt|;
name|CodecUtil
operator|.
name|writeFooter
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|out
argument_list|,
name|postingsWriter
argument_list|,
name|termsIndexWriter
argument_list|)
expr_stmt|;
name|out
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
DECL|method|writeTrailer
specifier|private
name|void
name|writeTrailer
parameter_list|(
name|long
name|dirStart
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeLong
argument_list|(
name|dirStart
argument_list|)
expr_stmt|;
block|}
DECL|class|TermEntry
specifier|private
specifier|static
class|class
name|TermEntry
block|{
DECL|field|term
specifier|public
specifier|final
name|BytesRefBuilder
name|term
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
DECL|field|state
specifier|public
name|BlockTermState
name|state
decl_stmt|;
block|}
DECL|class|TermsWriter
class|class
name|TermsWriter
block|{
DECL|field|fieldInfo
specifier|private
specifier|final
name|FieldInfo
name|fieldInfo
decl_stmt|;
DECL|field|postingsWriter
specifier|private
specifier|final
name|PostingsWriterBase
name|postingsWriter
decl_stmt|;
DECL|field|termsStartPointer
specifier|private
specifier|final
name|long
name|termsStartPointer
decl_stmt|;
DECL|field|numTerms
specifier|private
name|long
name|numTerms
decl_stmt|;
DECL|field|fieldIndexWriter
specifier|private
specifier|final
name|TermsIndexWriterBase
operator|.
name|FieldWriter
name|fieldIndexWriter
decl_stmt|;
DECL|field|docsSeen
specifier|private
specifier|final
name|FixedBitSet
name|docsSeen
decl_stmt|;
DECL|field|sumTotalTermFreq
name|long
name|sumTotalTermFreq
decl_stmt|;
DECL|field|sumDocFreq
name|long
name|sumDocFreq
decl_stmt|;
DECL|field|docCount
name|int
name|docCount
decl_stmt|;
DECL|field|longsSize
name|int
name|longsSize
decl_stmt|;
DECL|field|pendingTerms
specifier|private
name|TermEntry
index|[]
name|pendingTerms
decl_stmt|;
DECL|field|pendingCount
specifier|private
name|int
name|pendingCount
decl_stmt|;
DECL|method|TermsWriter
name|TermsWriter
parameter_list|(
name|TermsIndexWriterBase
operator|.
name|FieldWriter
name|fieldIndexWriter
parameter_list|,
name|FieldInfo
name|fieldInfo
parameter_list|,
name|PostingsWriterBase
name|postingsWriter
parameter_list|)
block|{
name|this
operator|.
name|fieldInfo
operator|=
name|fieldInfo
expr_stmt|;
name|this
operator|.
name|fieldIndexWriter
operator|=
name|fieldIndexWriter
expr_stmt|;
name|this
operator|.
name|docsSeen
operator|=
operator|new
name|FixedBitSet
argument_list|(
name|maxDoc
argument_list|)
expr_stmt|;
name|pendingTerms
operator|=
operator|new
name|TermEntry
index|[
literal|32
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
name|pendingTerms
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|pendingTerms
index|[
name|i
index|]
operator|=
operator|new
name|TermEntry
argument_list|()
expr_stmt|;
block|}
name|termsStartPointer
operator|=
name|out
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
name|this
operator|.
name|postingsWriter
operator|=
name|postingsWriter
expr_stmt|;
name|this
operator|.
name|longsSize
operator|=
name|postingsWriter
operator|.
name|setField
argument_list|(
name|fieldInfo
argument_list|)
expr_stmt|;
block|}
DECL|field|lastPrevTerm
specifier|private
specifier|final
name|BytesRefBuilder
name|lastPrevTerm
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
DECL|method|write
name|void
name|write
parameter_list|(
name|BytesRef
name|text
parameter_list|,
name|TermsEnum
name|termsEnum
parameter_list|)
throws|throws
name|IOException
block|{
name|BlockTermState
name|state
init|=
name|postingsWriter
operator|.
name|writeTerm
argument_list|(
name|text
argument_list|,
name|termsEnum
argument_list|,
name|docsSeen
argument_list|)
decl_stmt|;
if|if
condition|(
name|state
operator|==
literal|null
condition|)
block|{
comment|// No docs for this term:
return|return;
block|}
name|sumDocFreq
operator|+=
name|state
operator|.
name|docFreq
expr_stmt|;
name|sumTotalTermFreq
operator|+=
name|state
operator|.
name|totalTermFreq
expr_stmt|;
assert|assert
name|state
operator|.
name|docFreq
operator|>
literal|0
assert|;
comment|//System.out.println("BTW: finishTerm term=" + fieldInfo.name + ":" + text.utf8ToString() + " " + text + " seg=" + segment + " df=" + stats.docFreq);
name|TermStats
name|stats
init|=
operator|new
name|TermStats
argument_list|(
name|state
operator|.
name|docFreq
argument_list|,
name|state
operator|.
name|totalTermFreq
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|isIndexTerm
init|=
name|fieldIndexWriter
operator|.
name|checkIndexTerm
argument_list|(
name|text
argument_list|,
name|stats
argument_list|)
decl_stmt|;
if|if
condition|(
name|isIndexTerm
condition|)
block|{
if|if
condition|(
name|pendingCount
operator|>
literal|0
condition|)
block|{
comment|// Instead of writing each term, live, we gather terms
comment|// in RAM in a pending buffer, and then write the
comment|// entire block in between index terms:
name|flushBlock
argument_list|()
expr_stmt|;
block|}
name|fieldIndexWriter
operator|.
name|add
argument_list|(
name|text
argument_list|,
name|stats
argument_list|,
name|out
operator|.
name|getFilePointer
argument_list|()
argument_list|)
expr_stmt|;
comment|//System.out.println("  index term!");
block|}
if|if
condition|(
name|pendingTerms
operator|.
name|length
operator|==
name|pendingCount
condition|)
block|{
name|pendingTerms
operator|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|pendingTerms
argument_list|,
name|ArrayUtil
operator|.
name|oversize
argument_list|(
name|pendingCount
operator|+
literal|1
argument_list|,
name|RamUsageEstimator
operator|.
name|NUM_BYTES_OBJECT_REF
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
name|pendingCount
init|;
name|i
operator|<
name|pendingTerms
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|pendingTerms
index|[
name|i
index|]
operator|=
operator|new
name|TermEntry
argument_list|()
expr_stmt|;
block|}
block|}
specifier|final
name|TermEntry
name|te
init|=
name|pendingTerms
index|[
name|pendingCount
index|]
decl_stmt|;
name|te
operator|.
name|term
operator|.
name|copyBytes
argument_list|(
name|text
argument_list|)
expr_stmt|;
name|te
operator|.
name|state
operator|=
name|state
expr_stmt|;
name|pendingCount
operator|++
expr_stmt|;
name|numTerms
operator|++
expr_stmt|;
block|}
comment|// Finishes all terms in this field
DECL|method|finish
name|void
name|finish
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|pendingCount
operator|>
literal|0
condition|)
block|{
name|flushBlock
argument_list|()
expr_stmt|;
block|}
comment|// EOF marker:
name|out
operator|.
name|writeVInt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|fieldIndexWriter
operator|.
name|finish
argument_list|(
name|out
operator|.
name|getFilePointer
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|numTerms
operator|>
literal|0
condition|)
block|{
name|fields
operator|.
name|add
argument_list|(
operator|new
name|FieldMetaData
argument_list|(
name|fieldInfo
argument_list|,
name|numTerms
argument_list|,
name|termsStartPointer
argument_list|,
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
condition|?
name|sumTotalTermFreq
else|:
operator|-
literal|1
argument_list|,
name|sumDocFreq
argument_list|,
name|docsSeen
operator|.
name|cardinality
argument_list|()
argument_list|,
name|longsSize
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|sharedPrefix
specifier|private
name|int
name|sharedPrefix
parameter_list|(
name|BytesRef
name|term1
parameter_list|,
name|BytesRef
name|term2
parameter_list|)
block|{
assert|assert
name|term1
operator|.
name|offset
operator|==
literal|0
assert|;
assert|assert
name|term2
operator|.
name|offset
operator|==
literal|0
assert|;
name|int
name|pos1
init|=
literal|0
decl_stmt|;
name|int
name|pos1End
init|=
name|pos1
operator|+
name|Math
operator|.
name|min
argument_list|(
name|term1
operator|.
name|length
argument_list|,
name|term2
operator|.
name|length
argument_list|)
decl_stmt|;
name|int
name|pos2
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|pos1
operator|<
name|pos1End
condition|)
block|{
if|if
condition|(
name|term1
operator|.
name|bytes
index|[
name|pos1
index|]
operator|!=
name|term2
operator|.
name|bytes
index|[
name|pos2
index|]
condition|)
block|{
return|return
name|pos1
return|;
block|}
name|pos1
operator|++
expr_stmt|;
name|pos2
operator|++
expr_stmt|;
block|}
return|return
name|pos1
return|;
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
DECL|field|bufferWriter
specifier|private
specifier|final
name|RAMOutputStream
name|bufferWriter
init|=
operator|new
name|RAMOutputStream
argument_list|()
decl_stmt|;
DECL|method|flushBlock
specifier|private
name|void
name|flushBlock
parameter_list|()
throws|throws
name|IOException
block|{
comment|//System.out.println("BTW.flushBlock seg=" + segment + " pendingCount=" + pendingCount + " fp=" + out.getFilePointer());
comment|// First pass: compute common prefix for all terms
comment|// in the block, against term before first term in
comment|// this block:
name|int
name|commonPrefix
init|=
name|sharedPrefix
argument_list|(
name|lastPrevTerm
operator|.
name|get
argument_list|()
argument_list|,
name|pendingTerms
index|[
literal|0
index|]
operator|.
name|term
operator|.
name|get
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|termCount
init|=
literal|1
init|;
name|termCount
operator|<
name|pendingCount
condition|;
name|termCount
operator|++
control|)
block|{
name|commonPrefix
operator|=
name|Math
operator|.
name|min
argument_list|(
name|commonPrefix
argument_list|,
name|sharedPrefix
argument_list|(
name|lastPrevTerm
operator|.
name|get
argument_list|()
argument_list|,
name|pendingTerms
index|[
name|termCount
index|]
operator|.
name|term
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|writeVInt
argument_list|(
name|pendingCount
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|commonPrefix
argument_list|)
expr_stmt|;
comment|// 2nd pass: write suffixes, as separate byte[] blob
for|for
control|(
name|int
name|termCount
init|=
literal|0
init|;
name|termCount
operator|<
name|pendingCount
condition|;
name|termCount
operator|++
control|)
block|{
specifier|final
name|int
name|suffix
init|=
name|pendingTerms
index|[
name|termCount
index|]
operator|.
name|term
operator|.
name|length
argument_list|()
operator|-
name|commonPrefix
decl_stmt|;
comment|// TODO: cutover to better intblock codec, instead
comment|// of interleaving here:
name|bytesWriter
operator|.
name|writeVInt
argument_list|(
name|suffix
argument_list|)
expr_stmt|;
name|bytesWriter
operator|.
name|writeBytes
argument_list|(
name|pendingTerms
index|[
name|termCount
index|]
operator|.
name|term
operator|.
name|bytes
argument_list|()
argument_list|,
name|commonPrefix
argument_list|,
name|suffix
argument_list|)
expr_stmt|;
block|}
name|out
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
name|out
argument_list|)
expr_stmt|;
name|bytesWriter
operator|.
name|reset
argument_list|()
expr_stmt|;
comment|// 3rd pass: write the freqs as byte[] blob
comment|// TODO: cutover to better intblock codec.  simple64?
comment|// write prefix, suffix first:
for|for
control|(
name|int
name|termCount
init|=
literal|0
init|;
name|termCount
operator|<
name|pendingCount
condition|;
name|termCount
operator|++
control|)
block|{
specifier|final
name|BlockTermState
name|state
init|=
name|pendingTerms
index|[
name|termCount
index|]
operator|.
name|state
decl_stmt|;
assert|assert
name|state
operator|!=
literal|null
assert|;
name|bytesWriter
operator|.
name|writeVInt
argument_list|(
name|state
operator|.
name|docFreq
argument_list|)
expr_stmt|;
if|if
condition|(
name|fieldInfo
operator|.
name|getIndexOptions
argument_list|()
operator|!=
name|IndexOptions
operator|.
name|DOCS
condition|)
block|{
name|bytesWriter
operator|.
name|writeVLong
argument_list|(
name|state
operator|.
name|totalTermFreq
operator|-
name|state
operator|.
name|docFreq
argument_list|)
expr_stmt|;
block|}
block|}
name|out
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
name|out
argument_list|)
expr_stmt|;
name|bytesWriter
operator|.
name|reset
argument_list|()
expr_stmt|;
comment|// 4th pass: write the metadata
name|long
index|[]
name|longs
init|=
operator|new
name|long
index|[
name|longsSize
index|]
decl_stmt|;
name|boolean
name|absolute
init|=
literal|true
decl_stmt|;
for|for
control|(
name|int
name|termCount
init|=
literal|0
init|;
name|termCount
operator|<
name|pendingCount
condition|;
name|termCount
operator|++
control|)
block|{
specifier|final
name|BlockTermState
name|state
init|=
name|pendingTerms
index|[
name|termCount
index|]
operator|.
name|state
decl_stmt|;
name|postingsWriter
operator|.
name|encodeTerm
argument_list|(
name|longs
argument_list|,
name|bufferWriter
argument_list|,
name|fieldInfo
argument_list|,
name|state
argument_list|,
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
name|bytesWriter
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
name|bufferWriter
operator|.
name|writeTo
argument_list|(
name|bytesWriter
argument_list|)
expr_stmt|;
name|bufferWriter
operator|.
name|reset
argument_list|()
expr_stmt|;
name|absolute
operator|=
literal|false
expr_stmt|;
block|}
name|out
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
name|out
argument_list|)
expr_stmt|;
name|bytesWriter
operator|.
name|reset
argument_list|()
expr_stmt|;
name|lastPrevTerm
operator|.
name|copyBytes
argument_list|(
name|pendingTerms
index|[
name|pendingCount
operator|-
literal|1
index|]
operator|.
name|term
argument_list|)
expr_stmt|;
name|pendingCount
operator|=
literal|0
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

