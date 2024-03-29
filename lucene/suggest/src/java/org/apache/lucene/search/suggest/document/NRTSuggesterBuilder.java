begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search.suggest.document
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
operator|.
name|document
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
name|PriorityQueue
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
name|PairOutputs
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

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
operator|.
name|document
operator|.
name|NRTSuggester
operator|.
name|encode
import|;
end_import

begin_comment
comment|/**  * Builder for {@link NRTSuggester}  *  */
end_comment

begin_class
DECL|class|NRTSuggesterBuilder
specifier|final
class|class
name|NRTSuggesterBuilder
block|{
comment|/**    * Label used to separate surface form and docID    * in the output    */
DECL|field|PAYLOAD_SEP
specifier|public
specifier|static
specifier|final
name|int
name|PAYLOAD_SEP
init|=
literal|'\u001F'
decl_stmt|;
comment|/**    * Marks end of the analyzed input and start of dedup    * byte.    */
DECL|field|END_BYTE
specifier|public
specifier|static
specifier|final
name|int
name|END_BYTE
init|=
literal|0x0
decl_stmt|;
DECL|field|outputs
specifier|private
specifier|final
name|PairOutputs
argument_list|<
name|Long
argument_list|,
name|BytesRef
argument_list|>
name|outputs
decl_stmt|;
DECL|field|builder
specifier|private
specifier|final
name|Builder
argument_list|<
name|PairOutputs
operator|.
name|Pair
argument_list|<
name|Long
argument_list|,
name|BytesRef
argument_list|>
argument_list|>
name|builder
decl_stmt|;
DECL|field|scratchInts
specifier|private
specifier|final
name|IntsRefBuilder
name|scratchInts
init|=
operator|new
name|IntsRefBuilder
argument_list|()
decl_stmt|;
DECL|field|analyzed
specifier|private
specifier|final
name|BytesRefBuilder
name|analyzed
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
DECL|field|entries
specifier|private
specifier|final
name|PriorityQueue
argument_list|<
name|Entry
argument_list|>
name|entries
decl_stmt|;
DECL|field|payloadSep
specifier|private
specifier|final
name|int
name|payloadSep
decl_stmt|;
DECL|field|endByte
specifier|private
specifier|final
name|int
name|endByte
decl_stmt|;
DECL|field|maxAnalyzedPathsPerOutput
specifier|private
name|int
name|maxAnalyzedPathsPerOutput
init|=
literal|0
decl_stmt|;
comment|/**    * Create a builder for {@link NRTSuggester}    */
DECL|method|NRTSuggesterBuilder
specifier|public
name|NRTSuggesterBuilder
parameter_list|()
block|{
name|this
operator|.
name|payloadSep
operator|=
name|PAYLOAD_SEP
expr_stmt|;
name|this
operator|.
name|endByte
operator|=
name|END_BYTE
expr_stmt|;
name|this
operator|.
name|outputs
operator|=
operator|new
name|PairOutputs
argument_list|<>
argument_list|(
name|PositiveIntOutputs
operator|.
name|getSingleton
argument_list|()
argument_list|,
name|ByteSequenceOutputs
operator|.
name|getSingleton
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|entries
operator|=
operator|new
name|PriorityQueue
argument_list|<>
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
block|}
comment|/**    * Initializes an FST input term to add entries against    */
DECL|method|startTerm
specifier|public
name|void
name|startTerm
parameter_list|(
name|BytesRef
name|analyzed
parameter_list|)
block|{
name|this
operator|.
name|analyzed
operator|.
name|copyBytes
argument_list|(
name|analyzed
argument_list|)
expr_stmt|;
name|this
operator|.
name|analyzed
operator|.
name|append
argument_list|(
operator|(
name|byte
operator|)
name|endByte
argument_list|)
expr_stmt|;
block|}
comment|/**    * Adds an entry for the latest input term, should be called after    * {@link #startTerm(org.apache.lucene.util.BytesRef)} on the desired input    */
DECL|method|addEntry
specifier|public
name|void
name|addEntry
parameter_list|(
name|int
name|docID
parameter_list|,
name|BytesRef
name|surfaceForm
parameter_list|,
name|long
name|weight
parameter_list|)
throws|throws
name|IOException
block|{
name|BytesRef
name|payloadRef
init|=
name|NRTSuggester
operator|.
name|PayLoadProcessor
operator|.
name|make
argument_list|(
name|surfaceForm
argument_list|,
name|docID
argument_list|,
name|payloadSep
argument_list|)
decl_stmt|;
name|entries
operator|.
name|add
argument_list|(
operator|new
name|Entry
argument_list|(
name|payloadRef
argument_list|,
name|encode
argument_list|(
name|weight
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Writes all the entries for the FST input term    */
DECL|method|finishTerm
specifier|public
name|void
name|finishTerm
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|numArcs
init|=
literal|0
decl_stmt|;
name|int
name|numDedupBytes
init|=
literal|1
decl_stmt|;
name|analyzed
operator|.
name|grow
argument_list|(
name|analyzed
operator|.
name|length
argument_list|()
operator|+
literal|1
argument_list|)
expr_stmt|;
name|analyzed
operator|.
name|setLength
argument_list|(
name|analyzed
operator|.
name|length
argument_list|()
operator|+
literal|1
argument_list|)
expr_stmt|;
for|for
control|(
name|Entry
name|entry
range|:
name|entries
control|)
block|{
if|if
condition|(
name|numArcs
operator|==
name|maxNumArcsForDedupByte
argument_list|(
name|numDedupBytes
argument_list|)
condition|)
block|{
name|analyzed
operator|.
name|setByteAt
argument_list|(
name|analyzed
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|,
call|(
name|byte
call|)
argument_list|(
name|numArcs
argument_list|)
argument_list|)
expr_stmt|;
name|analyzed
operator|.
name|grow
argument_list|(
name|analyzed
operator|.
name|length
argument_list|()
operator|+
literal|1
argument_list|)
expr_stmt|;
name|analyzed
operator|.
name|setLength
argument_list|(
name|analyzed
operator|.
name|length
argument_list|()
operator|+
literal|1
argument_list|)
expr_stmt|;
name|numArcs
operator|=
literal|0
expr_stmt|;
name|numDedupBytes
operator|++
expr_stmt|;
block|}
name|analyzed
operator|.
name|setByteAt
argument_list|(
name|analyzed
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|,
operator|(
name|byte
operator|)
name|numArcs
operator|++
argument_list|)
expr_stmt|;
name|Util
operator|.
name|toIntsRef
argument_list|(
name|analyzed
operator|.
name|get
argument_list|()
argument_list|,
name|scratchInts
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add
argument_list|(
name|scratchInts
operator|.
name|get
argument_list|()
argument_list|,
name|outputs
operator|.
name|newPair
argument_list|(
name|entry
operator|.
name|weight
argument_list|,
name|entry
operator|.
name|payload
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|maxAnalyzedPathsPerOutput
operator|=
name|Math
operator|.
name|max
argument_list|(
name|maxAnalyzedPathsPerOutput
argument_list|,
name|entries
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|entries
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
comment|/**    * Builds and stores a FST that can be loaded with    * {@link NRTSuggester#load(org.apache.lucene.store.IndexInput)}    */
DECL|method|store
specifier|public
name|boolean
name|store
parameter_list|(
name|DataOutput
name|output
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|FST
argument_list|<
name|PairOutputs
operator|.
name|Pair
argument_list|<
name|Long
argument_list|,
name|BytesRef
argument_list|>
argument_list|>
name|build
init|=
name|builder
operator|.
name|finish
argument_list|()
decl_stmt|;
if|if
condition|(
name|build
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
name|build
operator|.
name|save
argument_list|(
name|output
argument_list|)
expr_stmt|;
comment|/* write some more meta-info */
assert|assert
name|maxAnalyzedPathsPerOutput
operator|>
literal|0
assert|;
name|output
operator|.
name|writeVInt
argument_list|(
name|maxAnalyzedPathsPerOutput
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeVInt
argument_list|(
name|END_BYTE
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeVInt
argument_list|(
name|PAYLOAD_SEP
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|/**    * Num arcs for nth dedup byte:    * if n<= 5: 1 + (2 * n)    * else: (1 + (2 * n)) * n    *<p>    * TODO: is there a better way to make the fst built to be    * more TopNSearcher friendly?    */
DECL|method|maxNumArcsForDedupByte
specifier|private
specifier|static
name|int
name|maxNumArcsForDedupByte
parameter_list|(
name|int
name|currentNumDedupBytes
parameter_list|)
block|{
name|int
name|maxArcs
init|=
literal|1
operator|+
operator|(
literal|2
operator|*
name|currentNumDedupBytes
operator|)
decl_stmt|;
if|if
condition|(
name|currentNumDedupBytes
operator|>
literal|5
condition|)
block|{
name|maxArcs
operator|*=
name|currentNumDedupBytes
expr_stmt|;
block|}
return|return
name|Math
operator|.
name|min
argument_list|(
name|maxArcs
argument_list|,
literal|255
argument_list|)
return|;
block|}
DECL|class|Entry
specifier|private
specifier|final
specifier|static
class|class
name|Entry
implements|implements
name|Comparable
argument_list|<
name|Entry
argument_list|>
block|{
DECL|field|payload
specifier|final
name|BytesRef
name|payload
decl_stmt|;
DECL|field|weight
specifier|final
name|long
name|weight
decl_stmt|;
DECL|method|Entry
specifier|public
name|Entry
parameter_list|(
name|BytesRef
name|payload
parameter_list|,
name|long
name|weight
parameter_list|)
block|{
name|this
operator|.
name|payload
operator|=
name|payload
expr_stmt|;
name|this
operator|.
name|weight
operator|=
name|weight
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|compareTo
specifier|public
name|int
name|compareTo
parameter_list|(
name|Entry
name|o
parameter_list|)
block|{
return|return
name|Long
operator|.
name|compare
argument_list|(
name|weight
argument_list|,
name|o
operator|.
name|weight
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

