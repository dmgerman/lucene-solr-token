begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.asserting
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|asserting
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
name|Comparator
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
name|PostingsConsumer
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
name|PostingsFormat
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
name|codecs
operator|.
name|TermsConsumer
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
name|lucene40
operator|.
name|Lucene40PostingsFormat
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
name|AssertingAtomicReader
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
name|OpenBitSet
import|;
end_import

begin_comment
comment|/**  * Just like {@link Lucene40PostingsFormat} but with additional asserts.  */
end_comment

begin_class
DECL|class|AssertingPostingsFormat
specifier|public
class|class
name|AssertingPostingsFormat
extends|extends
name|PostingsFormat
block|{
DECL|field|in
specifier|private
specifier|final
name|PostingsFormat
name|in
init|=
operator|new
name|Lucene40PostingsFormat
argument_list|()
decl_stmt|;
DECL|method|AssertingPostingsFormat
specifier|public
name|AssertingPostingsFormat
parameter_list|()
block|{
name|super
argument_list|(
literal|"Asserting"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|fieldsConsumer
specifier|public
name|FieldsConsumer
name|fieldsConsumer
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|AssertingFieldsConsumer
argument_list|(
name|in
operator|.
name|fieldsConsumer
argument_list|(
name|state
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|fieldsProducer
specifier|public
name|FieldsProducer
name|fieldsProducer
parameter_list|(
name|SegmentReadState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|AssertingFieldsProducer
argument_list|(
name|in
operator|.
name|fieldsProducer
argument_list|(
name|state
argument_list|)
argument_list|)
return|;
block|}
DECL|class|AssertingFieldsProducer
specifier|static
class|class
name|AssertingFieldsProducer
extends|extends
name|FieldsProducer
block|{
DECL|field|in
specifier|private
specifier|final
name|FieldsProducer
name|in
decl_stmt|;
DECL|method|AssertingFieldsProducer
name|AssertingFieldsProducer
parameter_list|(
name|FieldsProducer
name|in
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|in
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
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
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
name|Iterator
argument_list|<
name|String
argument_list|>
name|iterator
init|=
name|in
operator|.
name|iterator
argument_list|()
decl_stmt|;
assert|assert
name|iterator
operator|!=
literal|null
assert|;
return|return
name|iterator
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
name|Terms
name|terms
init|=
name|in
operator|.
name|terms
argument_list|(
name|field
argument_list|)
decl_stmt|;
return|return
name|terms
operator|==
literal|null
condition|?
literal|null
else|:
operator|new
name|AssertingAtomicReader
operator|.
name|AssertingTerms
argument_list|(
name|terms
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
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getUniqueTermCount
specifier|public
name|long
name|getUniqueTermCount
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|getUniqueTermCount
argument_list|()
return|;
block|}
block|}
DECL|class|AssertingFieldsConsumer
specifier|static
class|class
name|AssertingFieldsConsumer
extends|extends
name|FieldsConsumer
block|{
DECL|field|in
specifier|private
specifier|final
name|FieldsConsumer
name|in
decl_stmt|;
DECL|method|AssertingFieldsConsumer
name|AssertingFieldsConsumer
parameter_list|(
name|FieldsConsumer
name|in
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|addField
specifier|public
name|TermsConsumer
name|addField
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|TermsConsumer
name|consumer
init|=
name|in
operator|.
name|addField
argument_list|(
name|field
argument_list|)
decl_stmt|;
assert|assert
name|consumer
operator|!=
literal|null
assert|;
return|return
operator|new
name|AssertingTermsConsumer
argument_list|(
name|consumer
argument_list|,
name|field
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
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|enum|TermsConsumerState
DECL|enum constant|INITIAL
DECL|enum constant|START
DECL|enum constant|FINISHED
specifier|static
enum|enum
name|TermsConsumerState
block|{
name|INITIAL
block|,
name|START
block|,
name|FINISHED
block|}
empty_stmt|;
DECL|class|AssertingTermsConsumer
specifier|static
class|class
name|AssertingTermsConsumer
extends|extends
name|TermsConsumer
block|{
DECL|field|in
specifier|private
specifier|final
name|TermsConsumer
name|in
decl_stmt|;
DECL|field|fieldInfo
specifier|private
specifier|final
name|FieldInfo
name|fieldInfo
decl_stmt|;
DECL|field|lastTerm
specifier|private
name|BytesRef
name|lastTerm
init|=
literal|null
decl_stmt|;
DECL|field|state
specifier|private
name|TermsConsumerState
name|state
init|=
name|TermsConsumerState
operator|.
name|INITIAL
decl_stmt|;
DECL|field|lastPostingsConsumer
specifier|private
name|AssertingPostingsConsumer
name|lastPostingsConsumer
init|=
literal|null
decl_stmt|;
DECL|field|sumTotalTermFreq
specifier|private
name|long
name|sumTotalTermFreq
init|=
literal|0
decl_stmt|;
DECL|field|sumDocFreq
specifier|private
name|long
name|sumDocFreq
init|=
literal|0
decl_stmt|;
DECL|field|visitedDocs
specifier|private
name|OpenBitSet
name|visitedDocs
init|=
operator|new
name|OpenBitSet
argument_list|()
decl_stmt|;
DECL|method|AssertingTermsConsumer
name|AssertingTermsConsumer
parameter_list|(
name|TermsConsumer
name|in
parameter_list|,
name|FieldInfo
name|fieldInfo
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
name|this
operator|.
name|fieldInfo
operator|=
name|fieldInfo
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|startTerm
specifier|public
name|PostingsConsumer
name|startTerm
parameter_list|(
name|BytesRef
name|text
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|state
operator|==
name|TermsConsumerState
operator|.
name|INITIAL
operator|||
name|state
operator|==
name|TermsConsumerState
operator|.
name|START
operator|&&
name|lastPostingsConsumer
operator|.
name|docFreq
operator|==
literal|0
assert|;
name|state
operator|=
name|TermsConsumerState
operator|.
name|START
expr_stmt|;
assert|assert
name|lastTerm
operator|==
literal|null
operator|||
name|in
operator|.
name|getComparator
argument_list|()
operator|.
name|compare
argument_list|(
name|text
argument_list|,
name|lastTerm
argument_list|)
operator|>
literal|0
assert|;
name|lastTerm
operator|=
name|BytesRef
operator|.
name|deepCopyOf
argument_list|(
name|text
argument_list|)
expr_stmt|;
return|return
name|lastPostingsConsumer
operator|=
operator|new
name|AssertingPostingsConsumer
argument_list|(
name|in
operator|.
name|startTerm
argument_list|(
name|text
argument_list|)
argument_list|,
name|fieldInfo
argument_list|,
name|visitedDocs
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|finishTerm
specifier|public
name|void
name|finishTerm
parameter_list|(
name|BytesRef
name|text
parameter_list|,
name|TermStats
name|stats
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|state
operator|==
name|TermsConsumerState
operator|.
name|START
assert|;
name|state
operator|=
name|TermsConsumerState
operator|.
name|INITIAL
expr_stmt|;
assert|assert
name|text
operator|.
name|equals
argument_list|(
name|lastTerm
argument_list|)
assert|;
assert|assert
name|stats
operator|.
name|docFreq
operator|>
literal|0
assert|;
comment|// otherwise, this method should not be called.
assert|assert
name|stats
operator|.
name|docFreq
operator|==
name|lastPostingsConsumer
operator|.
name|docFreq
assert|;
name|sumDocFreq
operator|+=
name|stats
operator|.
name|docFreq
expr_stmt|;
if|if
condition|(
name|fieldInfo
operator|.
name|getIndexOptions
argument_list|()
operator|==
name|IndexOptions
operator|.
name|DOCS_ONLY
condition|)
block|{
assert|assert
name|stats
operator|.
name|totalTermFreq
operator|==
operator|-
literal|1
assert|;
block|}
else|else
block|{
assert|assert
name|stats
operator|.
name|totalTermFreq
operator|==
name|lastPostingsConsumer
operator|.
name|totalTermFreq
assert|;
name|sumTotalTermFreq
operator|+=
name|stats
operator|.
name|totalTermFreq
expr_stmt|;
block|}
name|in
operator|.
name|finishTerm
argument_list|(
name|text
argument_list|,
name|stats
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
assert|assert
name|state
operator|==
name|TermsConsumerState
operator|.
name|INITIAL
operator|||
name|state
operator|==
name|TermsConsumerState
operator|.
name|START
operator|&&
name|lastPostingsConsumer
operator|.
name|docFreq
operator|==
literal|0
assert|;
name|state
operator|=
name|TermsConsumerState
operator|.
name|FINISHED
expr_stmt|;
assert|assert
name|docCount
operator|>=
literal|0
assert|;
assert|assert
name|docCount
operator|==
name|visitedDocs
operator|.
name|cardinality
argument_list|()
assert|;
assert|assert
name|sumDocFreq
operator|>=
name|docCount
assert|;
assert|assert
name|sumDocFreq
operator|==
name|this
operator|.
name|sumDocFreq
assert|;
if|if
condition|(
name|fieldInfo
operator|.
name|getIndexOptions
argument_list|()
operator|==
name|IndexOptions
operator|.
name|DOCS_ONLY
condition|)
block|{
assert|assert
name|sumTotalTermFreq
operator|==
operator|-
literal|1
assert|;
block|}
else|else
block|{
assert|assert
name|sumTotalTermFreq
operator|>=
name|sumDocFreq
assert|;
assert|assert
name|sumTotalTermFreq
operator|==
name|this
operator|.
name|sumTotalTermFreq
assert|;
block|}
name|in
operator|.
name|finish
argument_list|(
name|sumTotalTermFreq
argument_list|,
name|sumDocFreq
argument_list|,
name|docCount
argument_list|)
expr_stmt|;
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
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|getComparator
argument_list|()
return|;
block|}
block|}
DECL|enum|PostingsConsumerState
DECL|enum constant|INITIAL
DECL|enum constant|START
specifier|static
enum|enum
name|PostingsConsumerState
block|{
name|INITIAL
block|,
name|START
block|}
empty_stmt|;
DECL|class|AssertingPostingsConsumer
specifier|static
class|class
name|AssertingPostingsConsumer
extends|extends
name|PostingsConsumer
block|{
DECL|field|in
specifier|private
specifier|final
name|PostingsConsumer
name|in
decl_stmt|;
DECL|field|fieldInfo
specifier|private
specifier|final
name|FieldInfo
name|fieldInfo
decl_stmt|;
DECL|field|visitedDocs
specifier|private
specifier|final
name|OpenBitSet
name|visitedDocs
decl_stmt|;
DECL|field|state
specifier|private
name|PostingsConsumerState
name|state
init|=
name|PostingsConsumerState
operator|.
name|INITIAL
decl_stmt|;
DECL|field|freq
specifier|private
name|int
name|freq
decl_stmt|;
DECL|field|positionCount
specifier|private
name|int
name|positionCount
decl_stmt|;
DECL|field|lastPosition
specifier|private
name|int
name|lastPosition
init|=
literal|0
decl_stmt|;
DECL|field|lastStartOffset
specifier|private
name|int
name|lastStartOffset
init|=
literal|0
decl_stmt|;
DECL|field|docFreq
name|int
name|docFreq
init|=
literal|0
decl_stmt|;
DECL|field|totalTermFreq
name|long
name|totalTermFreq
init|=
literal|0
decl_stmt|;
DECL|method|AssertingPostingsConsumer
name|AssertingPostingsConsumer
parameter_list|(
name|PostingsConsumer
name|in
parameter_list|,
name|FieldInfo
name|fieldInfo
parameter_list|,
name|OpenBitSet
name|visitedDocs
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
name|this
operator|.
name|fieldInfo
operator|=
name|fieldInfo
expr_stmt|;
name|this
operator|.
name|visitedDocs
operator|=
name|visitedDocs
expr_stmt|;
block|}
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
name|freq
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|state
operator|==
name|PostingsConsumerState
operator|.
name|INITIAL
assert|;
name|state
operator|=
name|PostingsConsumerState
operator|.
name|START
expr_stmt|;
assert|assert
name|docID
operator|>=
literal|0
assert|;
if|if
condition|(
name|fieldInfo
operator|.
name|getIndexOptions
argument_list|()
operator|==
name|IndexOptions
operator|.
name|DOCS_ONLY
condition|)
block|{
assert|assert
name|freq
operator|==
operator|-
literal|1
assert|;
name|this
operator|.
name|freq
operator|=
literal|0
expr_stmt|;
comment|// we don't expect any positions here
block|}
else|else
block|{
assert|assert
name|freq
operator|>
literal|0
assert|;
name|this
operator|.
name|freq
operator|=
name|freq
expr_stmt|;
name|totalTermFreq
operator|+=
name|freq
expr_stmt|;
block|}
name|this
operator|.
name|positionCount
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|lastPosition
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|lastStartOffset
operator|=
literal|0
expr_stmt|;
name|docFreq
operator|++
expr_stmt|;
name|visitedDocs
operator|.
name|set
argument_list|(
name|docID
argument_list|)
expr_stmt|;
name|in
operator|.
name|startDoc
argument_list|(
name|docID
argument_list|,
name|freq
argument_list|)
expr_stmt|;
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
assert|assert
name|state
operator|==
name|PostingsConsumerState
operator|.
name|START
assert|;
assert|assert
name|positionCount
operator|<
name|freq
assert|;
name|positionCount
operator|++
expr_stmt|;
assert|assert
name|position
operator|>=
name|lastPosition
operator|||
name|position
operator|==
operator|-
literal|1
assert|;
comment|/* we still allow -1 from old 3.x indexes */
name|lastPosition
operator|=
name|position
expr_stmt|;
if|if
condition|(
name|fieldInfo
operator|.
name|getIndexOptions
argument_list|()
operator|==
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS
condition|)
block|{
assert|assert
name|startOffset
operator|>=
literal|0
assert|;
assert|assert
name|startOffset
operator|>=
name|lastStartOffset
assert|;
name|lastStartOffset
operator|=
name|startOffset
expr_stmt|;
assert|assert
name|endOffset
operator|>=
name|startOffset
assert|;
block|}
else|else
block|{
assert|assert
name|startOffset
operator|==
operator|-
literal|1
assert|;
assert|assert
name|endOffset
operator|==
operator|-
literal|1
assert|;
block|}
if|if
condition|(
name|payload
operator|!=
literal|null
condition|)
block|{
assert|assert
name|fieldInfo
operator|.
name|hasPayloads
argument_list|()
assert|;
block|}
name|in
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
assert|assert
name|state
operator|==
name|PostingsConsumerState
operator|.
name|START
assert|;
name|state
operator|=
name|PostingsConsumerState
operator|.
name|INITIAL
expr_stmt|;
if|if
condition|(
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
operator|<
literal|0
condition|)
block|{
assert|assert
name|positionCount
operator|==
literal|0
assert|;
comment|// we should not have fed any positions!
block|}
else|else
block|{
assert|assert
name|positionCount
operator|==
name|freq
assert|;
block|}
name|in
operator|.
name|finishDoc
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

