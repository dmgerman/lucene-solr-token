begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.memory
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|memory
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
name|IntsRefBuilder
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
name|fst
operator|.
name|Builder
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
name|fst
operator|.
name|FST
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
name|fst
operator|.
name|PositiveIntOutputs
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
name|fst
operator|.
name|Util
import|;
end_import

begin_comment
comment|/**   * FST-based term dict, using ord as FST output.  *  * The FST holds the mapping between&lt;term, ord&gt;, and   * term's metadata is delta encoded into a single byte block.  *  * Typically the byte block consists of four parts:  * 1. term statistics: docFreq, totalTermFreq;  * 2. monotonic long[], e.g. the pointer to the postings list for that term;  * 3. generic byte[], e.g. other information customized by postings base.  * 4. single-level skip list to speed up metadata decoding by ord.  *  *<p>  * Files:  *<ul>  *<li><tt>.tix</tt>:<a href="#Termindex">Term Index</a></li>  *<li><tt>.tbk</tt>:<a href="#Termblock">Term Block</a></li>  *</ul>  *  *<a name="Termindex"></a>  *<h3>Term Index</h3>  *<p>  *  The .tix contains a list of FSTs, one for each field.  *  The FST maps a term to its corresponding order in current field.  *</p>  *   *<ul>  *<li>TermIndex(.tix) --&gt; Header, TermFST<sup>NumFields</sup>, Footer</li>  *<li>TermFST --&gt; {@link FST FST&lt;long&gt;}</li>  *<li>Header --&gt; {@link CodecUtil#writeIndexHeader IndexHeader}</li>  *<li>Footer --&gt; {@link CodecUtil#writeFooter CodecFooter}</li>  *</ul>  *  *<p>Notes:</p>  *<ul>  *<li>  *  Since terms are already sorted before writing to<a href="#Termblock">Term Block</a>,   *  their ords can directly used to seek term metadata from term block.  *</li>  *</ul>  *  *<a name="Termblock"></a>  *<h3>Term Block</h3>  *<p>  *  The .tbk contains all the statistics and metadata for terms, along with field summary (e.g.   *  per-field data like number of documents in current field). For each field, there are four blocks:  *<ul>  *<li>statistics bytes block: contains term statistics;</li>  *<li>metadata longs block: delta-encodes monotonic part of metadata;</li>  *<li>metadata bytes block: encodes other parts of metadata;</li>  *<li>skip block: contains skip data, to speed up metadata seeking and decoding</li>  *</ul>  *  *<p>File Format:</p>  *<ul>  *<li>TermBlock(.tbk) --&gt; Header,<i>PostingsHeader</i>, FieldSummary, DirOffset</li>  *<li>FieldSummary --&gt; NumFields,&lt;FieldNumber, NumTerms, SumTotalTermFreq?, SumDocFreq,  *                                         DocCount, LongsSize, DataBlock&gt;<sup>NumFields</sup>, Footer</li>  *  *<li>DataBlock --&gt; StatsBlockLength, MetaLongsBlockLength, MetaBytesBlockLength,   *                       SkipBlock, StatsBlock, MetaLongsBlock, MetaBytesBlock</li>  *<li>SkipBlock --&gt;&lt; StatsFPDelta, MetaLongsSkipFPDelta, MetaBytesSkipFPDelta,   *                            MetaLongsSkipDelta<sup>LongsSize</sup>&gt;<sup>NumTerms</sup>  *<li>StatsBlock --&gt;&lt; DocFreq[Same?], (TotalTermFreq-DocFreq) ?&gt;<sup>NumTerms</sup>  *<li>MetaLongsBlock --&gt;&lt; LongDelta<sup>LongsSize</sup>, BytesSize&gt;<sup>NumTerms</sup>  *<li>MetaBytesBlock --&gt; Byte<sup>MetaBytesBlockLength</sup>  *<li>Header --&gt; {@link CodecUtil#writeIndexHeader IndexHeader}</li>  *<li>DirOffset --&gt; {@link DataOutput#writeLong Uint64}</li>  *<li>NumFields, FieldNumber, DocCount, DocFreq, LongsSize,   *        FieldNumber, DocCount --&gt; {@link DataOutput#writeVInt VInt}</li>  *<li>NumTerms, SumTotalTermFreq, SumDocFreq, StatsBlockLength, MetaLongsBlockLength, MetaBytesBlockLength,  *        StatsFPDelta, MetaLongsSkipFPDelta, MetaBytesSkipFPDelta, MetaLongsSkipStart, TotalTermFreq,   *        LongDelta,--&gt; {@link DataOutput#writeVLong VLong}</li>  *<li>Footer --&gt; {@link CodecUtil#writeFooter CodecFooter}</li>  *</ul>  *<p>Notes:</p>  *<ul>  *<li>  *   The format of PostingsHeader and MetaBytes are customized by the specific postings implementation:  *   they contain arbitrary per-file data (such as parameters or versioning information), and per-term data   *   (non-monotonic ones like pulsed postings data).  *</li>  *<li>  *   During initialization the reader will load all the blocks into memory. SkipBlock will be decoded, so that during seek  *   term dict can lookup file pointers directly. StatsFPDelta, MetaLongsSkipFPDelta, etc. are file offset  *   for every SkipInterval's term. MetaLongsSkipDelta is the difference from previous one, which indicates  *   the value of preceding metadata longs for every SkipInterval's term.  *</li>  *<li>  *   DocFreq is the count of documents which contain the term. TotalTermFreq is the total number of occurrences of the term.   *   Usually these two values are the same for long tail terms, therefore one bit is stole from DocFreq to check this case,  *   so that encoding of TotalTermFreq may be omitted.  *</li>  *</ul>  *  * @lucene.experimental   */
end_comment

begin_class
DECL|class|FSTOrdTermsWriter
specifier|public
class|class
name|FSTOrdTermsWriter
extends|extends
name|FieldsConsumer
block|{
DECL|field|TERMS_INDEX_EXTENSION
specifier|static
specifier|final
name|String
name|TERMS_INDEX_EXTENSION
init|=
literal|"tix"
decl_stmt|;
DECL|field|TERMS_BLOCK_EXTENSION
specifier|static
specifier|final
name|String
name|TERMS_BLOCK_EXTENSION
init|=
literal|"tbk"
decl_stmt|;
DECL|field|TERMS_CODEC_NAME
specifier|static
specifier|final
name|String
name|TERMS_CODEC_NAME
init|=
literal|"FSTOrdTerms"
decl_stmt|;
DECL|field|TERMS_INDEX_CODEC_NAME
specifier|static
specifier|final
name|String
name|TERMS_INDEX_CODEC_NAME
init|=
literal|"FSTOrdIndex"
decl_stmt|;
DECL|field|VERSION_START
specifier|public
specifier|static
specifier|final
name|int
name|VERSION_START
init|=
literal|2
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
DECL|field|SKIP_INTERVAL
specifier|public
specifier|static
specifier|final
name|int
name|SKIP_INTERVAL
init|=
literal|8
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
DECL|field|maxDoc
specifier|final
name|int
name|maxDoc
decl_stmt|;
DECL|field|fields
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
DECL|field|blockOut
name|IndexOutput
name|blockOut
init|=
literal|null
decl_stmt|;
DECL|field|indexOut
name|IndexOutput
name|indexOut
init|=
literal|null
decl_stmt|;
DECL|method|FSTOrdTermsWriter
specifier|public
name|FSTOrdTermsWriter
parameter_list|(
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
name|termsIndexFileName
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
name|TERMS_INDEX_EXTENSION
argument_list|)
decl_stmt|;
specifier|final
name|String
name|termsBlockFileName
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
name|TERMS_BLOCK_EXTENSION
argument_list|)
decl_stmt|;
name|this
operator|.
name|postingsWriter
operator|=
name|postingsWriter
expr_stmt|;
name|this
operator|.
name|fieldInfos
operator|=
name|state
operator|.
name|fieldInfos
expr_stmt|;
name|this
operator|.
name|maxDoc
operator|=
name|state
operator|.
name|segmentInfo
operator|.
name|getDocCount
argument_list|()
expr_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|this
operator|.
name|indexOut
operator|=
name|state
operator|.
name|directory
operator|.
name|createOutput
argument_list|(
name|termsIndexFileName
argument_list|,
name|state
operator|.
name|context
argument_list|)
expr_stmt|;
name|this
operator|.
name|blockOut
operator|=
name|state
operator|.
name|directory
operator|.
name|createOutput
argument_list|(
name|termsBlockFileName
argument_list|,
name|state
operator|.
name|context
argument_list|)
expr_stmt|;
name|CodecUtil
operator|.
name|writeIndexHeader
argument_list|(
name|indexOut
argument_list|,
name|TERMS_INDEX_CODEC_NAME
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
name|CodecUtil
operator|.
name|writeIndexHeader
argument_list|(
name|blockOut
argument_list|,
name|TERMS_CODEC_NAME
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
name|this
operator|.
name|postingsWriter
operator|.
name|init
argument_list|(
name|blockOut
argument_list|,
name|state
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
operator|!
name|success
condition|)
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|indexOut
argument_list|,
name|blockOut
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
name|FieldInfo
name|fieldInfo
init|=
name|fieldInfos
operator|.
name|fieldInfo
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|boolean
name|hasFreq
init|=
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
decl_stmt|;
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
operator|new
name|TermsWriter
argument_list|(
name|fieldInfo
argument_list|)
decl_stmt|;
name|long
name|sumTotalTermFreq
init|=
literal|0
decl_stmt|;
name|long
name|sumDocFreq
init|=
literal|0
decl_stmt|;
name|FixedBitSet
name|docsSeen
init|=
operator|new
name|FixedBitSet
argument_list|(
name|maxDoc
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
name|BlockTermState
name|termState
init|=
name|postingsWriter
operator|.
name|writeTerm
argument_list|(
name|term
argument_list|,
name|termsEnum
argument_list|,
name|docsSeen
argument_list|)
decl_stmt|;
if|if
condition|(
name|termState
operator|!=
literal|null
condition|)
block|{
name|termsWriter
operator|.
name|finishTerm
argument_list|(
name|term
argument_list|,
name|termState
argument_list|)
expr_stmt|;
name|sumTotalTermFreq
operator|+=
name|termState
operator|.
name|totalTermFreq
expr_stmt|;
name|sumDocFreq
operator|+=
name|termState
operator|.
name|docFreq
expr_stmt|;
block|}
block|}
name|termsWriter
operator|.
name|finish
argument_list|(
name|hasFreq
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
argument_list|)
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
if|if
condition|(
name|blockOut
operator|!=
literal|null
condition|)
block|{
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
specifier|final
name|long
name|blockDirStart
init|=
name|blockOut
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
comment|// write field summary
name|blockOut
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
name|blockOut
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
name|blockOut
operator|.
name|writeVLong
argument_list|(
name|field
operator|.
name|numTerms
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
name|blockOut
operator|.
name|writeVLong
argument_list|(
name|field
operator|.
name|sumTotalTermFreq
argument_list|)
expr_stmt|;
block|}
name|blockOut
operator|.
name|writeVLong
argument_list|(
name|field
operator|.
name|sumDocFreq
argument_list|)
expr_stmt|;
name|blockOut
operator|.
name|writeVInt
argument_list|(
name|field
operator|.
name|docCount
argument_list|)
expr_stmt|;
name|blockOut
operator|.
name|writeVInt
argument_list|(
name|field
operator|.
name|longsSize
argument_list|)
expr_stmt|;
name|blockOut
operator|.
name|writeVLong
argument_list|(
name|field
operator|.
name|statsOut
operator|.
name|getFilePointer
argument_list|()
argument_list|)
expr_stmt|;
name|blockOut
operator|.
name|writeVLong
argument_list|(
name|field
operator|.
name|metaLongsOut
operator|.
name|getFilePointer
argument_list|()
argument_list|)
expr_stmt|;
name|blockOut
operator|.
name|writeVLong
argument_list|(
name|field
operator|.
name|metaBytesOut
operator|.
name|getFilePointer
argument_list|()
argument_list|)
expr_stmt|;
name|field
operator|.
name|skipOut
operator|.
name|writeTo
argument_list|(
name|blockOut
argument_list|)
expr_stmt|;
name|field
operator|.
name|statsOut
operator|.
name|writeTo
argument_list|(
name|blockOut
argument_list|)
expr_stmt|;
name|field
operator|.
name|metaLongsOut
operator|.
name|writeTo
argument_list|(
name|blockOut
argument_list|)
expr_stmt|;
name|field
operator|.
name|metaBytesOut
operator|.
name|writeTo
argument_list|(
name|blockOut
argument_list|)
expr_stmt|;
name|field
operator|.
name|dict
operator|.
name|save
argument_list|(
name|indexOut
argument_list|)
expr_stmt|;
block|}
name|writeTrailer
argument_list|(
name|blockOut
argument_list|,
name|blockDirStart
argument_list|)
expr_stmt|;
name|CodecUtil
operator|.
name|writeFooter
argument_list|(
name|indexOut
argument_list|)
expr_stmt|;
name|CodecUtil
operator|.
name|writeFooter
argument_list|(
name|blockOut
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
name|blockOut
argument_list|,
name|indexOut
argument_list|,
name|postingsWriter
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|blockOut
argument_list|,
name|indexOut
argument_list|,
name|postingsWriter
argument_list|)
expr_stmt|;
block|}
name|blockOut
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
name|IndexOutput
name|out
parameter_list|,
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
DECL|class|FieldMetaData
specifier|private
specifier|static
class|class
name|FieldMetaData
block|{
DECL|field|fieldInfo
specifier|public
name|FieldInfo
name|fieldInfo
decl_stmt|;
DECL|field|numTerms
specifier|public
name|long
name|numTerms
decl_stmt|;
DECL|field|sumTotalTermFreq
specifier|public
name|long
name|sumTotalTermFreq
decl_stmt|;
DECL|field|sumDocFreq
specifier|public
name|long
name|sumDocFreq
decl_stmt|;
DECL|field|docCount
specifier|public
name|int
name|docCount
decl_stmt|;
DECL|field|longsSize
specifier|public
name|int
name|longsSize
decl_stmt|;
DECL|field|dict
specifier|public
name|FST
argument_list|<
name|Long
argument_list|>
name|dict
decl_stmt|;
comment|// TODO: block encode each part
comment|// vint encode next skip point (fully decoded when reading)
DECL|field|skipOut
specifier|public
name|RAMOutputStream
name|skipOut
decl_stmt|;
comment|// vint encode df, (ttf-df)
DECL|field|statsOut
specifier|public
name|RAMOutputStream
name|statsOut
decl_stmt|;
comment|// vint encode monotonic long[] and length for corresponding byte[]
DECL|field|metaLongsOut
specifier|public
name|RAMOutputStream
name|metaLongsOut
decl_stmt|;
comment|// generic byte[]
DECL|field|metaBytesOut
specifier|public
name|RAMOutputStream
name|metaBytesOut
decl_stmt|;
block|}
DECL|class|TermsWriter
specifier|final
class|class
name|TermsWriter
block|{
DECL|field|builder
specifier|private
specifier|final
name|Builder
argument_list|<
name|Long
argument_list|>
name|builder
decl_stmt|;
DECL|field|outputs
specifier|private
specifier|final
name|PositiveIntOutputs
name|outputs
decl_stmt|;
DECL|field|fieldInfo
specifier|private
specifier|final
name|FieldInfo
name|fieldInfo
decl_stmt|;
DECL|field|longsSize
specifier|private
specifier|final
name|int
name|longsSize
decl_stmt|;
DECL|field|numTerms
specifier|private
name|long
name|numTerms
decl_stmt|;
DECL|field|scratchTerm
specifier|private
specifier|final
name|IntsRefBuilder
name|scratchTerm
init|=
operator|new
name|IntsRefBuilder
argument_list|()
decl_stmt|;
DECL|field|statsOut
specifier|private
specifier|final
name|RAMOutputStream
name|statsOut
init|=
operator|new
name|RAMOutputStream
argument_list|()
decl_stmt|;
DECL|field|metaLongsOut
specifier|private
specifier|final
name|RAMOutputStream
name|metaLongsOut
init|=
operator|new
name|RAMOutputStream
argument_list|()
decl_stmt|;
DECL|field|metaBytesOut
specifier|private
specifier|final
name|RAMOutputStream
name|metaBytesOut
init|=
operator|new
name|RAMOutputStream
argument_list|()
decl_stmt|;
DECL|field|skipOut
specifier|private
specifier|final
name|RAMOutputStream
name|skipOut
init|=
operator|new
name|RAMOutputStream
argument_list|()
decl_stmt|;
DECL|field|lastBlockStatsFP
specifier|private
name|long
name|lastBlockStatsFP
decl_stmt|;
DECL|field|lastBlockMetaLongsFP
specifier|private
name|long
name|lastBlockMetaLongsFP
decl_stmt|;
DECL|field|lastBlockMetaBytesFP
specifier|private
name|long
name|lastBlockMetaBytesFP
decl_stmt|;
DECL|field|lastBlockLongs
specifier|private
name|long
index|[]
name|lastBlockLongs
decl_stmt|;
DECL|field|lastLongs
specifier|private
name|long
index|[]
name|lastLongs
decl_stmt|;
DECL|field|lastMetaBytesFP
specifier|private
name|long
name|lastMetaBytesFP
decl_stmt|;
DECL|method|TermsWriter
name|TermsWriter
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|)
block|{
name|this
operator|.
name|numTerms
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|fieldInfo
operator|=
name|fieldInfo
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
name|this
operator|.
name|outputs
operator|=
name|PositiveIntOutputs
operator|.
name|getSingleton
argument_list|()
expr_stmt|;
name|this
operator|.
name|builder
operator|=
operator|new
name|Builder
argument_list|<>
argument_list|(
name|FST
operator|.
name|INPUT_TYPE
operator|.
name|BYTE1
argument_list|,
name|outputs
argument_list|)
expr_stmt|;
name|this
operator|.
name|lastBlockStatsFP
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|lastBlockMetaLongsFP
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|lastBlockMetaBytesFP
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|lastBlockLongs
operator|=
operator|new
name|long
index|[
name|longsSize
index|]
expr_stmt|;
name|this
operator|.
name|lastLongs
operator|=
operator|new
name|long
index|[
name|longsSize
index|]
expr_stmt|;
name|this
operator|.
name|lastMetaBytesFP
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|finishTerm
specifier|public
name|void
name|finishTerm
parameter_list|(
name|BytesRef
name|text
parameter_list|,
name|BlockTermState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|numTerms
operator|>
literal|0
operator|&&
name|numTerms
operator|%
name|SKIP_INTERVAL
operator|==
literal|0
condition|)
block|{
name|bufferSkip
argument_list|()
expr_stmt|;
block|}
comment|// write term meta data into fst
specifier|final
name|long
name|longs
index|[]
init|=
operator|new
name|long
index|[
name|longsSize
index|]
decl_stmt|;
specifier|final
name|long
name|delta
init|=
name|state
operator|.
name|totalTermFreq
operator|-
name|state
operator|.
name|docFreq
decl_stmt|;
if|if
condition|(
name|state
operator|.
name|totalTermFreq
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|delta
operator|==
literal|0
condition|)
block|{
name|statsOut
operator|.
name|writeVInt
argument_list|(
name|state
operator|.
name|docFreq
operator|<<
literal|1
operator||
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|statsOut
operator|.
name|writeVInt
argument_list|(
name|state
operator|.
name|docFreq
operator|<<
literal|1
argument_list|)
expr_stmt|;
name|statsOut
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
else|else
block|{
name|statsOut
operator|.
name|writeVInt
argument_list|(
name|state
operator|.
name|docFreq
argument_list|)
expr_stmt|;
block|}
name|postingsWriter
operator|.
name|encodeTerm
argument_list|(
name|longs
argument_list|,
name|metaBytesOut
argument_list|,
name|fieldInfo
argument_list|,
name|state
argument_list|,
literal|true
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
name|metaLongsOut
operator|.
name|writeVLong
argument_list|(
name|longs
index|[
name|i
index|]
operator|-
name|lastLongs
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|lastLongs
index|[
name|i
index|]
operator|=
name|longs
index|[
name|i
index|]
expr_stmt|;
block|}
name|metaLongsOut
operator|.
name|writeVLong
argument_list|(
name|metaBytesOut
operator|.
name|getFilePointer
argument_list|()
operator|-
name|lastMetaBytesFP
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add
argument_list|(
name|Util
operator|.
name|toIntsRef
argument_list|(
name|text
argument_list|,
name|scratchTerm
argument_list|)
argument_list|,
name|numTerms
argument_list|)
expr_stmt|;
name|numTerms
operator|++
expr_stmt|;
name|lastMetaBytesFP
operator|=
name|metaBytesOut
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
block|}
DECL|method|finish
specifier|public
name|void
name|finish
parameter_list|(
name|long
name|sumTotalTermFreq
parameter_list|,
name|long
name|sumDocFreq
parameter_list|,
name|int
name|docCount
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|numTerms
operator|>
literal|0
condition|)
block|{
specifier|final
name|FieldMetaData
name|metadata
init|=
operator|new
name|FieldMetaData
argument_list|()
decl_stmt|;
name|metadata
operator|.
name|fieldInfo
operator|=
name|fieldInfo
expr_stmt|;
name|metadata
operator|.
name|numTerms
operator|=
name|numTerms
expr_stmt|;
name|metadata
operator|.
name|sumTotalTermFreq
operator|=
name|sumTotalTermFreq
expr_stmt|;
name|metadata
operator|.
name|sumDocFreq
operator|=
name|sumDocFreq
expr_stmt|;
name|metadata
operator|.
name|docCount
operator|=
name|docCount
expr_stmt|;
name|metadata
operator|.
name|longsSize
operator|=
name|longsSize
expr_stmt|;
name|metadata
operator|.
name|skipOut
operator|=
name|skipOut
expr_stmt|;
name|metadata
operator|.
name|statsOut
operator|=
name|statsOut
expr_stmt|;
name|metadata
operator|.
name|metaLongsOut
operator|=
name|metaLongsOut
expr_stmt|;
name|metadata
operator|.
name|metaBytesOut
operator|=
name|metaBytesOut
expr_stmt|;
name|metadata
operator|.
name|dict
operator|=
name|builder
operator|.
name|finish
argument_list|()
expr_stmt|;
name|fields
operator|.
name|add
argument_list|(
name|metadata
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|bufferSkip
specifier|private
name|void
name|bufferSkip
parameter_list|()
throws|throws
name|IOException
block|{
name|skipOut
operator|.
name|writeVLong
argument_list|(
name|statsOut
operator|.
name|getFilePointer
argument_list|()
operator|-
name|lastBlockStatsFP
argument_list|)
expr_stmt|;
name|skipOut
operator|.
name|writeVLong
argument_list|(
name|metaLongsOut
operator|.
name|getFilePointer
argument_list|()
operator|-
name|lastBlockMetaLongsFP
argument_list|)
expr_stmt|;
name|skipOut
operator|.
name|writeVLong
argument_list|(
name|metaBytesOut
operator|.
name|getFilePointer
argument_list|()
operator|-
name|lastBlockMetaBytesFP
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
name|skipOut
operator|.
name|writeVLong
argument_list|(
name|lastLongs
index|[
name|i
index|]
operator|-
name|lastBlockLongs
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|lastBlockStatsFP
operator|=
name|statsOut
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
name|lastBlockMetaLongsFP
operator|=
name|metaLongsOut
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
name|lastBlockMetaBytesFP
operator|=
name|metaBytesOut
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|lastLongs
argument_list|,
literal|0
argument_list|,
name|lastBlockLongs
argument_list|,
literal|0
argument_list|,
name|longsSize
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

