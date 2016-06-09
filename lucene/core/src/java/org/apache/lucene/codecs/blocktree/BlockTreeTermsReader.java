begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.codecs.blocktree
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|blocktree
package|;
end_package

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
name|Collection
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
name|Iterator
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
name|TreeMap
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
name|FieldsProducer
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
name|PostingsReaderBase
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
name|SegmentReadState
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
name|search
operator|.
name|PrefixQuery
import|;
end_import

begin_comment
comment|// javadocs
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|TermRangeQuery
import|;
end_import

begin_comment
comment|// javadocs
end_comment

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
name|IndexInput
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
name|Accountable
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
name|Accountables
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
name|ByteSequenceOutputs
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
name|Outputs
import|;
end_import

begin_comment
comment|/** A block-based terms index and dictionary that assigns  *  terms to variable length blocks according to how they  *  share prefixes.  The terms index is a prefix trie  *  whose leaves are term blocks.  The advantage of this  *  approach is that seekExact is often able to  *  determine a term cannot exist without doing any IO, and  *  intersection with Automata is very fast.  Note that this  *  terms dictionary has its own fixed terms index (ie, it  *  does not support a pluggable terms index  *  implementation).  *  *<p><b>NOTE</b>: this terms dictionary supports  *  min/maxItemsPerBlock during indexing to control how  *  much memory the terms index uses.</p>  *  *<p>If auto-prefix terms were indexed (see  *  {@link BlockTreeTermsWriter}), then the {@link Terms#intersect}  *  implementation here will make use of these terms only if the  *  automaton has a binary sink state, i.e. an accept state  *  which has a transition to itself accepting all byte values.  *  For example, both {@link PrefixQuery} and {@link TermRangeQuery}  *  pass such automata to {@link Terms#intersect}.</p>  *  *<p>The data structure used by this implementation is very  *  similar to a burst trie  *  (http://citeseer.ist.psu.edu/viewdoc/summary?doi=10.1.1.18.3499),  *  but with added logic to break up too-large blocks of all  *  terms sharing a given prefix into smaller ones.</p>  *  *<p>Use {@link org.apache.lucene.index.CheckIndex} with the<code>-verbose</code>  *  option to see summary statistics on the blocks in the  *  dictionary.  *  *  See {@link BlockTreeTermsWriter}.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|BlockTreeTermsReader
specifier|public
specifier|final
class|class
name|BlockTreeTermsReader
extends|extends
name|FieldsProducer
block|{
DECL|field|FST_OUTPUTS
specifier|static
specifier|final
name|Outputs
argument_list|<
name|BytesRef
argument_list|>
name|FST_OUTPUTS
init|=
name|ByteSequenceOutputs
operator|.
name|getSingleton
argument_list|()
decl_stmt|;
DECL|field|NO_OUTPUT
specifier|static
specifier|final
name|BytesRef
name|NO_OUTPUT
init|=
name|FST_OUTPUTS
operator|.
name|getNoOutput
argument_list|()
decl_stmt|;
DECL|field|OUTPUT_FLAGS_NUM_BITS
specifier|static
specifier|final
name|int
name|OUTPUT_FLAGS_NUM_BITS
init|=
literal|2
decl_stmt|;
DECL|field|OUTPUT_FLAGS_MASK
specifier|static
specifier|final
name|int
name|OUTPUT_FLAGS_MASK
init|=
literal|0x3
decl_stmt|;
DECL|field|OUTPUT_FLAG_IS_FLOOR
specifier|static
specifier|final
name|int
name|OUTPUT_FLAG_IS_FLOOR
init|=
literal|0x1
decl_stmt|;
DECL|field|OUTPUT_FLAG_HAS_TERMS
specifier|static
specifier|final
name|int
name|OUTPUT_FLAG_HAS_TERMS
init|=
literal|0x2
decl_stmt|;
comment|/** Extension of terms file */
DECL|field|TERMS_EXTENSION
specifier|static
specifier|final
name|String
name|TERMS_EXTENSION
init|=
literal|"tim"
decl_stmt|;
DECL|field|TERMS_CODEC_NAME
specifier|final
specifier|static
name|String
name|TERMS_CODEC_NAME
init|=
literal|"BlockTreeTermsDict"
decl_stmt|;
comment|/** Initial terms format. */
DECL|field|VERSION_START
specifier|public
specifier|static
specifier|final
name|int
name|VERSION_START
init|=
literal|0
decl_stmt|;
comment|/** Auto-prefix terms. */
DECL|field|VERSION_AUTO_PREFIX_TERMS
specifier|public
specifier|static
specifier|final
name|int
name|VERSION_AUTO_PREFIX_TERMS
init|=
literal|1
decl_stmt|;
comment|/** Conditional auto-prefix terms: we record at write time whether    *  this field did write any auto-prefix terms. */
DECL|field|VERSION_AUTO_PREFIX_TERMS_COND
specifier|public
specifier|static
specifier|final
name|int
name|VERSION_AUTO_PREFIX_TERMS_COND
init|=
literal|2
decl_stmt|;
comment|/** Auto-prefix terms have been superseded by points. */
DECL|field|VERSION_AUTO_PREFIX_TERMS_REMOVED
specifier|public
specifier|static
specifier|final
name|int
name|VERSION_AUTO_PREFIX_TERMS_REMOVED
init|=
literal|3
decl_stmt|;
comment|/** Current terms format. */
DECL|field|VERSION_CURRENT
specifier|public
specifier|static
specifier|final
name|int
name|VERSION_CURRENT
init|=
name|VERSION_AUTO_PREFIX_TERMS_REMOVED
decl_stmt|;
comment|/** Extension of terms index file */
DECL|field|TERMS_INDEX_EXTENSION
specifier|static
specifier|final
name|String
name|TERMS_INDEX_EXTENSION
init|=
literal|"tip"
decl_stmt|;
DECL|field|TERMS_INDEX_CODEC_NAME
specifier|final
specifier|static
name|String
name|TERMS_INDEX_CODEC_NAME
init|=
literal|"BlockTreeTermsIndex"
decl_stmt|;
comment|// Open input to the main terms dict file (_X.tib)
DECL|field|termsIn
specifier|final
name|IndexInput
name|termsIn
decl_stmt|;
comment|//private static final boolean DEBUG = BlockTreeTermsWriter.DEBUG;
comment|// Reads the terms dict entries, to gather state to
comment|// produce DocsEnum on demand
DECL|field|postingsReader
specifier|final
name|PostingsReaderBase
name|postingsReader
decl_stmt|;
DECL|field|fields
specifier|private
specifier|final
name|TreeMap
argument_list|<
name|String
argument_list|,
name|FieldReader
argument_list|>
name|fields
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|/** File offset where the directory starts in the terms file. */
DECL|field|dirOffset
specifier|private
name|long
name|dirOffset
decl_stmt|;
comment|/** File offset where the directory starts in the index file. */
DECL|field|indexDirOffset
specifier|private
name|long
name|indexDirOffset
decl_stmt|;
DECL|field|segment
specifier|final
name|String
name|segment
decl_stmt|;
DECL|field|version
specifier|final
name|int
name|version
decl_stmt|;
DECL|field|anyAutoPrefixTerms
specifier|final
name|boolean
name|anyAutoPrefixTerms
decl_stmt|;
comment|/** Sole constructor. */
DECL|method|BlockTreeTermsReader
specifier|public
name|BlockTreeTermsReader
parameter_list|(
name|PostingsReaderBase
name|postingsReader
parameter_list|,
name|SegmentReadState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|success
init|=
literal|false
decl_stmt|;
name|IndexInput
name|indexIn
init|=
literal|null
decl_stmt|;
name|this
operator|.
name|postingsReader
operator|=
name|postingsReader
expr_stmt|;
name|this
operator|.
name|segment
operator|=
name|state
operator|.
name|segmentInfo
operator|.
name|name
expr_stmt|;
name|String
name|termsName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segment
argument_list|,
name|state
operator|.
name|segmentSuffix
argument_list|,
name|TERMS_EXTENSION
argument_list|)
decl_stmt|;
try|try
block|{
name|termsIn
operator|=
name|state
operator|.
name|directory
operator|.
name|openInput
argument_list|(
name|termsName
argument_list|,
name|state
operator|.
name|context
argument_list|)
expr_stmt|;
name|version
operator|=
name|CodecUtil
operator|.
name|checkIndexHeader
argument_list|(
name|termsIn
argument_list|,
name|TERMS_CODEC_NAME
argument_list|,
name|VERSION_START
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
if|if
condition|(
name|version
operator|<
name|VERSION_AUTO_PREFIX_TERMS
operator|||
name|version
operator|>=
name|VERSION_AUTO_PREFIX_TERMS_REMOVED
condition|)
block|{
comment|// Old (pre-5.2.0) or recent (6.2.0+) index, no auto-prefix terms:
name|this
operator|.
name|anyAutoPrefixTerms
operator|=
literal|false
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|version
operator|==
name|VERSION_AUTO_PREFIX_TERMS
condition|)
block|{
comment|// 5.2.x index, might have auto-prefix terms:
name|this
operator|.
name|anyAutoPrefixTerms
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
comment|// 5.3.x index, we record up front if we may have written any auto-prefix terms:
assert|assert
name|version
operator|==
name|VERSION_AUTO_PREFIX_TERMS_COND
assert|;
name|byte
name|b
init|=
name|termsIn
operator|.
name|readByte
argument_list|()
decl_stmt|;
if|if
condition|(
name|b
operator|==
literal|0
condition|)
block|{
name|this
operator|.
name|anyAutoPrefixTerms
operator|=
literal|false
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|b
operator|==
literal|1
condition|)
block|{
name|this
operator|.
name|anyAutoPrefixTerms
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"invalid anyAutoPrefixTerms: expected 0 or 1 but got "
operator|+
name|b
argument_list|,
name|termsIn
argument_list|)
throw|;
block|}
block|}
name|String
name|indexName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segment
argument_list|,
name|state
operator|.
name|segmentSuffix
argument_list|,
name|TERMS_INDEX_EXTENSION
argument_list|)
decl_stmt|;
name|indexIn
operator|=
name|state
operator|.
name|directory
operator|.
name|openInput
argument_list|(
name|indexName
argument_list|,
name|state
operator|.
name|context
argument_list|)
expr_stmt|;
name|CodecUtil
operator|.
name|checkIndexHeader
argument_list|(
name|indexIn
argument_list|,
name|TERMS_INDEX_CODEC_NAME
argument_list|,
name|version
argument_list|,
name|version
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
name|checksumEntireFile
argument_list|(
name|indexIn
argument_list|)
expr_stmt|;
comment|// Have PostingsReader init itself
name|postingsReader
operator|.
name|init
argument_list|(
name|termsIn
argument_list|,
name|state
argument_list|)
expr_stmt|;
comment|// NOTE: data file is too costly to verify checksum against all the bytes on open,
comment|// but for now we at least verify proper structure of the checksum footer: which looks
comment|// for FOOTER_MAGIC + algorithmID. This is cheap and can detect some forms of corruption
comment|// such as file truncation.
name|CodecUtil
operator|.
name|retrieveChecksum
argument_list|(
name|termsIn
argument_list|)
expr_stmt|;
comment|// Read per-field details
name|seekDir
argument_list|(
name|termsIn
argument_list|,
name|dirOffset
argument_list|)
expr_stmt|;
name|seekDir
argument_list|(
name|indexIn
argument_list|,
name|indexDirOffset
argument_list|)
expr_stmt|;
specifier|final
name|int
name|numFields
init|=
name|termsIn
operator|.
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|numFields
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"invalid numFields: "
operator|+
name|numFields
argument_list|,
name|termsIn
argument_list|)
throw|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numFields
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|int
name|field
init|=
name|termsIn
operator|.
name|readVInt
argument_list|()
decl_stmt|;
specifier|final
name|long
name|numTerms
init|=
name|termsIn
operator|.
name|readVLong
argument_list|()
decl_stmt|;
if|if
condition|(
name|numTerms
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"Illegal numTerms for field number: "
operator|+
name|field
argument_list|,
name|termsIn
argument_list|)
throw|;
block|}
specifier|final
name|int
name|numBytes
init|=
name|termsIn
operator|.
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|numBytes
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"invalid rootCode for field number: "
operator|+
name|field
operator|+
literal|", numBytes="
operator|+
name|numBytes
argument_list|,
name|termsIn
argument_list|)
throw|;
block|}
specifier|final
name|BytesRef
name|rootCode
init|=
operator|new
name|BytesRef
argument_list|(
operator|new
name|byte
index|[
name|numBytes
index|]
argument_list|)
decl_stmt|;
name|termsIn
operator|.
name|readBytes
argument_list|(
name|rootCode
operator|.
name|bytes
argument_list|,
literal|0
argument_list|,
name|numBytes
argument_list|)
expr_stmt|;
name|rootCode
operator|.
name|length
operator|=
name|numBytes
expr_stmt|;
specifier|final
name|FieldInfo
name|fieldInfo
init|=
name|state
operator|.
name|fieldInfos
operator|.
name|fieldInfo
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldInfo
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"invalid field number: "
operator|+
name|field
argument_list|,
name|termsIn
argument_list|)
throw|;
block|}
specifier|final
name|long
name|sumTotalTermFreq
init|=
name|fieldInfo
operator|.
name|getIndexOptions
argument_list|()
operator|==
name|IndexOptions
operator|.
name|DOCS
condition|?
operator|-
literal|1
else|:
name|termsIn
operator|.
name|readVLong
argument_list|()
decl_stmt|;
specifier|final
name|long
name|sumDocFreq
init|=
name|termsIn
operator|.
name|readVLong
argument_list|()
decl_stmt|;
specifier|final
name|int
name|docCount
init|=
name|termsIn
operator|.
name|readVInt
argument_list|()
decl_stmt|;
specifier|final
name|int
name|longsSize
init|=
name|termsIn
operator|.
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|longsSize
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"invalid longsSize for field: "
operator|+
name|fieldInfo
operator|.
name|name
operator|+
literal|", longsSize="
operator|+
name|longsSize
argument_list|,
name|termsIn
argument_list|)
throw|;
block|}
name|BytesRef
name|minTerm
init|=
name|readBytesRef
argument_list|(
name|termsIn
argument_list|)
decl_stmt|;
name|BytesRef
name|maxTerm
init|=
name|readBytesRef
argument_list|(
name|termsIn
argument_list|)
decl_stmt|;
if|if
condition|(
name|docCount
argument_list|<
literal|0
operator|||
name|docCount
argument_list|>
name|state
operator|.
name|segmentInfo
operator|.
name|maxDoc
argument_list|()
condition|)
block|{
comment|// #docs with field must be<= #docs
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"invalid docCount: "
operator|+
name|docCount
operator|+
literal|" maxDoc: "
operator|+
name|state
operator|.
name|segmentInfo
operator|.
name|maxDoc
argument_list|()
argument_list|,
name|termsIn
argument_list|)
throw|;
block|}
if|if
condition|(
name|sumDocFreq
operator|<
name|docCount
condition|)
block|{
comment|// #postings must be>= #docs with field
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"invalid sumDocFreq: "
operator|+
name|sumDocFreq
operator|+
literal|" docCount: "
operator|+
name|docCount
argument_list|,
name|termsIn
argument_list|)
throw|;
block|}
if|if
condition|(
name|sumTotalTermFreq
operator|!=
operator|-
literal|1
operator|&&
name|sumTotalTermFreq
operator|<
name|sumDocFreq
condition|)
block|{
comment|// #positions must be>= #postings
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"invalid sumTotalTermFreq: "
operator|+
name|sumTotalTermFreq
operator|+
literal|" sumDocFreq: "
operator|+
name|sumDocFreq
argument_list|,
name|termsIn
argument_list|)
throw|;
block|}
specifier|final
name|long
name|indexStartFP
init|=
name|indexIn
operator|.
name|readVLong
argument_list|()
decl_stmt|;
name|FieldReader
name|previous
init|=
name|fields
operator|.
name|put
argument_list|(
name|fieldInfo
operator|.
name|name
argument_list|,
operator|new
name|FieldReader
argument_list|(
name|this
argument_list|,
name|fieldInfo
argument_list|,
name|numTerms
argument_list|,
name|rootCode
argument_list|,
name|sumTotalTermFreq
argument_list|,
name|sumDocFreq
argument_list|,
name|docCount
argument_list|,
name|indexStartFP
argument_list|,
name|longsSize
argument_list|,
name|indexIn
argument_list|,
name|minTerm
argument_list|,
name|maxTerm
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|previous
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"duplicate field: "
operator|+
name|fieldInfo
operator|.
name|name
argument_list|,
name|termsIn
argument_list|)
throw|;
block|}
block|}
name|indexIn
operator|.
name|close
argument_list|()
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
comment|// this.close() will close in:
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|indexIn
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|readBytesRef
specifier|private
specifier|static
name|BytesRef
name|readBytesRef
parameter_list|(
name|IndexInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|BytesRef
name|bytes
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|bytes
operator|.
name|length
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|bytes
operator|.
name|bytes
operator|=
operator|new
name|byte
index|[
name|bytes
operator|.
name|length
index|]
expr_stmt|;
name|in
operator|.
name|readBytes
argument_list|(
name|bytes
operator|.
name|bytes
argument_list|,
literal|0
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
name|bytes
return|;
block|}
comment|/** Seek {@code input} to the directory offset. */
DECL|method|seekDir
specifier|private
name|void
name|seekDir
parameter_list|(
name|IndexInput
name|input
parameter_list|,
name|long
name|dirOffset
parameter_list|)
throws|throws
name|IOException
block|{
name|input
operator|.
name|seek
argument_list|(
name|input
operator|.
name|length
argument_list|()
operator|-
name|CodecUtil
operator|.
name|footerLength
argument_list|()
operator|-
literal|8
argument_list|)
expr_stmt|;
name|dirOffset
operator|=
name|input
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|input
operator|.
name|seek
argument_list|(
name|dirOffset
argument_list|)
expr_stmt|;
block|}
comment|// for debugging
comment|// private static String toHex(int v) {
comment|//   return "0x" + Integer.toHexString(v);
comment|// }
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
name|IOUtils
operator|.
name|close
argument_list|(
name|termsIn
argument_list|,
name|postingsReader
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
comment|// Clear so refs to terms index is GCable even if
comment|// app hangs onto us:
name|fields
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
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
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
name|fields
operator|.
name|keySet
argument_list|()
argument_list|)
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
assert|assert
name|field
operator|!=
literal|null
assert|;
return|return
name|fields
operator|.
name|get
argument_list|(
name|field
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
return|return
name|fields
operator|.
name|size
argument_list|()
return|;
block|}
comment|// for debugging
DECL|method|brToString
name|String
name|brToString
parameter_list|(
name|BytesRef
name|b
parameter_list|)
block|{
if|if
condition|(
name|b
operator|==
literal|null
condition|)
block|{
return|return
literal|"null"
return|;
block|}
else|else
block|{
try|try
block|{
return|return
name|b
operator|.
name|utf8ToString
argument_list|()
operator|+
literal|" "
operator|+
name|b
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
comment|// If BytesRef isn't actually UTF8, or it's eg a
comment|// prefix of UTF8 that ends mid-unicode-char, we
comment|// fallback to hex:
return|return
name|b
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
name|long
name|sizeInBytes
init|=
name|postingsReader
operator|.
name|ramBytesUsed
argument_list|()
decl_stmt|;
for|for
control|(
name|FieldReader
name|reader
range|:
name|fields
operator|.
name|values
argument_list|()
control|)
block|{
name|sizeInBytes
operator|+=
name|reader
operator|.
name|ramBytesUsed
argument_list|()
expr_stmt|;
block|}
return|return
name|sizeInBytes
return|;
block|}
annotation|@
name|Override
DECL|method|getChildResources
specifier|public
name|Collection
argument_list|<
name|Accountable
argument_list|>
name|getChildResources
parameter_list|()
block|{
name|List
argument_list|<
name|Accountable
argument_list|>
name|resources
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|resources
operator|.
name|addAll
argument_list|(
name|Accountables
operator|.
name|namedAccountables
argument_list|(
literal|"field"
argument_list|,
name|fields
argument_list|)
argument_list|)
expr_stmt|;
name|resources
operator|.
name|add
argument_list|(
name|Accountables
operator|.
name|namedAccountable
argument_list|(
literal|"delegate"
argument_list|,
name|postingsReader
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|resources
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|checkIntegrity
specifier|public
name|void
name|checkIntegrity
parameter_list|()
throws|throws
name|IOException
block|{
comment|// term dictionary
name|CodecUtil
operator|.
name|checksumEntireFile
argument_list|(
name|termsIn
argument_list|)
expr_stmt|;
comment|// postings
name|postingsReader
operator|.
name|checkIntegrity
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"(fields="
operator|+
name|fields
operator|.
name|size
argument_list|()
operator|+
literal|",delegate="
operator|+
name|postingsReader
operator|+
literal|")"
return|;
block|}
block|}
end_class

end_unit

