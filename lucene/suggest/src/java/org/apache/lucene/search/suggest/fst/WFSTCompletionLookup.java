begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.suggest.fst
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
name|fst
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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
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
name|Collections
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
name|search
operator|.
name|spell
operator|.
name|TermFreqIterator
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
name|suggest
operator|.
name|Lookup
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
name|suggest
operator|.
name|SortedTermFreqIteratorWrapper
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
name|suggest
operator|.
name|fst
operator|.
name|Sort
operator|.
name|ByteSequencesWriter
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
name|ByteArrayDataInput
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
name|ByteArrayDataOutput
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
name|InputStreamDataInput
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
name|OutputStreamDataOutput
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
name|CharsRef
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
name|IntsRef
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
name|UnicodeUtil
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
name|FST
operator|.
name|Arc
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
operator|.
name|BytesReader
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
operator|.
name|MinResult
import|;
end_import

begin_comment
comment|/**  * Suggester based on a weighted FST: it first traverses the prefix,   * then walks the<i>n</i> shortest paths to retrieve top-ranked  * suggestions.  *<p>  *<b>NOTE</b>:  * Input weights must be between 0 and {@link Integer#MAX_VALUE}, any  * other values will be rejected.  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|WFSTCompletionLookup
specifier|public
class|class
name|WFSTCompletionLookup
extends|extends
name|Lookup
block|{
comment|/**    * FST<Long>, weights are encoded as costs: (Integer.MAX_VALUE-weight)    */
comment|// NOTE: like FSTSuggester, this is really a WFSA, if you want to
comment|// customize the code to add some output you should use PairOutputs.
DECL|field|fst
specifier|private
name|FST
argument_list|<
name|Long
argument_list|>
name|fst
init|=
literal|null
decl_stmt|;
comment|/**     * True if exact match suggestions should always be returned first.    */
DECL|field|exactFirst
specifier|private
specifier|final
name|boolean
name|exactFirst
decl_stmt|;
comment|/**    * Calls {@link #WFSTCompletionLookup(boolean) WFSTCompletionLookup(true)}    */
DECL|method|WFSTCompletionLookup
specifier|public
name|WFSTCompletionLookup
parameter_list|()
block|{
name|this
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new suggester.    *     * @param exactFirst<code>true</code> if suggestions that match the     *        prefix exactly should always be returned first, regardless    *        of score. This has no performance impact, but could result    *        in low-quality suggestions.    */
DECL|method|WFSTCompletionLookup
specifier|public
name|WFSTCompletionLookup
parameter_list|(
name|boolean
name|exactFirst
parameter_list|)
block|{
name|this
operator|.
name|exactFirst
operator|=
name|exactFirst
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|build
specifier|public
name|void
name|build
parameter_list|(
name|TermFreqIterator
name|iterator
parameter_list|)
throws|throws
name|IOException
block|{
name|BytesRef
name|scratch
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|TermFreqIterator
name|iter
init|=
operator|new
name|WFSTTermFreqIteratorWrapper
argument_list|(
name|iterator
argument_list|,
name|BytesRef
operator|.
name|getUTF8SortedAsUnicodeComparator
argument_list|()
argument_list|)
decl_stmt|;
name|IntsRef
name|scratchInts
init|=
operator|new
name|IntsRef
argument_list|()
decl_stmt|;
name|BytesRef
name|previous
init|=
literal|null
decl_stmt|;
name|PositiveIntOutputs
name|outputs
init|=
name|PositiveIntOutputs
operator|.
name|getSingleton
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|Builder
argument_list|<
name|Long
argument_list|>
name|builder
init|=
operator|new
name|Builder
argument_list|<
name|Long
argument_list|>
argument_list|(
name|FST
operator|.
name|INPUT_TYPE
operator|.
name|BYTE1
argument_list|,
name|outputs
argument_list|)
decl_stmt|;
while|while
condition|(
operator|(
name|scratch
operator|=
name|iter
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|long
name|cost
init|=
name|iter
operator|.
name|weight
argument_list|()
decl_stmt|;
if|if
condition|(
name|previous
operator|==
literal|null
condition|)
block|{
name|previous
operator|=
operator|new
name|BytesRef
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|scratch
operator|.
name|equals
argument_list|(
name|previous
argument_list|)
condition|)
block|{
continue|continue;
comment|// for duplicate suggestions, the best weight is actually
comment|// added
block|}
name|Util
operator|.
name|toIntsRef
argument_list|(
name|scratch
argument_list|,
name|scratchInts
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add
argument_list|(
name|scratchInts
argument_list|,
name|cost
argument_list|)
expr_stmt|;
name|previous
operator|.
name|copyBytes
argument_list|(
name|scratch
argument_list|)
expr_stmt|;
block|}
name|fst
operator|=
name|builder
operator|.
name|finish
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|store
specifier|public
name|boolean
name|store
parameter_list|(
name|OutputStream
name|output
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
if|if
condition|(
name|fst
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
name|fst
operator|.
name|save
argument_list|(
operator|new
name|OutputStreamDataOutput
argument_list|(
name|output
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|output
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|load
specifier|public
name|boolean
name|load
parameter_list|(
name|InputStream
name|input
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|this
operator|.
name|fst
operator|=
operator|new
name|FST
argument_list|<
name|Long
argument_list|>
argument_list|(
operator|new
name|InputStreamDataInput
argument_list|(
name|input
argument_list|)
argument_list|,
name|PositiveIntOutputs
operator|.
name|getSingleton
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|lookup
specifier|public
name|List
argument_list|<
name|LookupResult
argument_list|>
name|lookup
parameter_list|(
name|CharSequence
name|key
parameter_list|,
name|boolean
name|onlyMorePopular
parameter_list|,
name|int
name|num
parameter_list|)
block|{
assert|assert
name|num
operator|>
literal|0
assert|;
name|BytesRef
name|scratch
init|=
operator|new
name|BytesRef
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|int
name|prefixLength
init|=
name|scratch
operator|.
name|length
decl_stmt|;
name|Arc
argument_list|<
name|Long
argument_list|>
name|arc
init|=
operator|new
name|Arc
argument_list|<
name|Long
argument_list|>
argument_list|()
decl_stmt|;
comment|// match the prefix portion exactly
name|Long
name|prefixOutput
init|=
literal|null
decl_stmt|;
try|try
block|{
name|prefixOutput
operator|=
name|lookupPrefix
argument_list|(
name|scratch
argument_list|,
name|arc
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|bogus
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|bogus
argument_list|)
throw|;
block|}
if|if
condition|(
name|prefixOutput
operator|==
literal|null
condition|)
block|{
return|return
name|Collections
operator|.
expr|<
name|LookupResult
operator|>
name|emptyList
argument_list|()
return|;
block|}
name|List
argument_list|<
name|LookupResult
argument_list|>
name|results
init|=
operator|new
name|ArrayList
argument_list|<
name|LookupResult
argument_list|>
argument_list|(
name|num
argument_list|)
decl_stmt|;
name|CharsRef
name|spare
init|=
operator|new
name|CharsRef
argument_list|()
decl_stmt|;
if|if
condition|(
name|exactFirst
operator|&&
name|arc
operator|.
name|isFinal
argument_list|()
condition|)
block|{
name|spare
operator|.
name|grow
argument_list|(
name|scratch
operator|.
name|length
argument_list|)
expr_stmt|;
name|UnicodeUtil
operator|.
name|UTF8toUTF16
argument_list|(
name|scratch
argument_list|,
name|spare
argument_list|)
expr_stmt|;
name|results
operator|.
name|add
argument_list|(
operator|new
name|LookupResult
argument_list|(
name|spare
operator|.
name|toString
argument_list|()
argument_list|,
name|decodeWeight
argument_list|(
name|prefixOutput
operator|+
name|arc
operator|.
name|nextFinalOutput
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|--
name|num
operator|==
literal|0
condition|)
block|{
return|return
name|results
return|;
comment|// that was quick
block|}
block|}
comment|// complete top-N
name|MinResult
argument_list|<
name|Long
argument_list|>
name|completions
index|[]
init|=
literal|null
decl_stmt|;
try|try
block|{
name|completions
operator|=
name|Util
operator|.
name|shortestPaths
argument_list|(
name|fst
argument_list|,
name|arc
argument_list|,
name|prefixOutput
argument_list|,
name|weightComparator
argument_list|,
name|num
argument_list|,
operator|!
name|exactFirst
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|bogus
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|bogus
argument_list|)
throw|;
block|}
name|BytesRef
name|suffix
init|=
operator|new
name|BytesRef
argument_list|(
literal|8
argument_list|)
decl_stmt|;
for|for
control|(
name|MinResult
argument_list|<
name|Long
argument_list|>
name|completion
range|:
name|completions
control|)
block|{
name|scratch
operator|.
name|length
operator|=
name|prefixLength
expr_stmt|;
comment|// append suffix
name|Util
operator|.
name|toBytesRef
argument_list|(
name|completion
operator|.
name|input
argument_list|,
name|suffix
argument_list|)
expr_stmt|;
name|scratch
operator|.
name|append
argument_list|(
name|suffix
argument_list|)
expr_stmt|;
name|spare
operator|.
name|grow
argument_list|(
name|scratch
operator|.
name|length
argument_list|)
expr_stmt|;
name|UnicodeUtil
operator|.
name|UTF8toUTF16
argument_list|(
name|scratch
argument_list|,
name|spare
argument_list|)
expr_stmt|;
name|results
operator|.
name|add
argument_list|(
operator|new
name|LookupResult
argument_list|(
name|spare
operator|.
name|toString
argument_list|()
argument_list|,
name|decodeWeight
argument_list|(
name|completion
operator|.
name|output
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|results
return|;
block|}
DECL|method|lookupPrefix
specifier|private
name|Long
name|lookupPrefix
parameter_list|(
name|BytesRef
name|scratch
parameter_list|,
name|Arc
argument_list|<
name|Long
argument_list|>
name|arc
parameter_list|)
throws|throws
comment|/*Bogus*/
name|IOException
block|{
assert|assert
literal|0
operator|==
name|fst
operator|.
name|outputs
operator|.
name|getNoOutput
argument_list|()
operator|.
name|longValue
argument_list|()
assert|;
name|long
name|output
init|=
literal|0
decl_stmt|;
name|BytesReader
name|bytesReader
init|=
name|fst
operator|.
name|getBytesReader
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|fst
operator|.
name|getFirstArc
argument_list|(
name|arc
argument_list|)
expr_stmt|;
name|byte
index|[]
name|bytes
init|=
name|scratch
operator|.
name|bytes
decl_stmt|;
name|int
name|pos
init|=
name|scratch
operator|.
name|offset
decl_stmt|;
name|int
name|end
init|=
name|pos
operator|+
name|scratch
operator|.
name|length
decl_stmt|;
while|while
condition|(
name|pos
operator|<
name|end
condition|)
block|{
if|if
condition|(
name|fst
operator|.
name|findTargetArc
argument_list|(
name|bytes
index|[
name|pos
operator|++
index|]
operator|&
literal|0xff
argument_list|,
name|arc
argument_list|,
name|arc
argument_list|,
name|bytesReader
argument_list|)
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
name|output
operator|+=
name|arc
operator|.
name|output
operator|.
name|longValue
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|output
return|;
block|}
comment|/**    * Returns the weight associated with an input string,    * or null if it does not exist.    */
DECL|method|get
specifier|public
name|Object
name|get
parameter_list|(
name|CharSequence
name|key
parameter_list|)
block|{
name|Arc
argument_list|<
name|Long
argument_list|>
name|arc
init|=
operator|new
name|Arc
argument_list|<
name|Long
argument_list|>
argument_list|()
decl_stmt|;
name|Long
name|result
init|=
literal|null
decl_stmt|;
try|try
block|{
name|result
operator|=
name|lookupPrefix
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|key
argument_list|)
argument_list|,
name|arc
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|bogus
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|bogus
argument_list|)
throw|;
block|}
if|if
condition|(
name|result
operator|==
literal|null
operator|||
operator|!
name|arc
operator|.
name|isFinal
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
name|Integer
operator|.
name|valueOf
argument_list|(
name|decodeWeight
argument_list|(
name|result
operator|+
name|arc
operator|.
name|nextFinalOutput
argument_list|)
argument_list|)
return|;
block|}
block|}
comment|/** cost -> weight */
DECL|method|decodeWeight
specifier|private
specifier|static
name|int
name|decodeWeight
parameter_list|(
name|long
name|encoded
parameter_list|)
block|{
return|return
call|(
name|int
call|)
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
operator|-
name|encoded
argument_list|)
return|;
block|}
comment|/** weight -> cost */
DECL|method|encodeWeight
specifier|private
specifier|static
name|int
name|encodeWeight
parameter_list|(
name|long
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
argument_list|<
literal|0
operator|||
name|value
argument_list|>
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"cannot encode value: "
operator|+
name|value
argument_list|)
throw|;
block|}
return|return
name|Integer
operator|.
name|MAX_VALUE
operator|-
operator|(
name|int
operator|)
name|value
return|;
block|}
DECL|class|WFSTTermFreqIteratorWrapper
specifier|private
specifier|final
class|class
name|WFSTTermFreqIteratorWrapper
extends|extends
name|SortedTermFreqIteratorWrapper
block|{
DECL|method|WFSTTermFreqIteratorWrapper
name|WFSTTermFreqIteratorWrapper
parameter_list|(
name|TermFreqIterator
name|source
parameter_list|,
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comparator
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|source
argument_list|,
name|comparator
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|encode
specifier|protected
name|void
name|encode
parameter_list|(
name|ByteSequencesWriter
name|writer
parameter_list|,
name|ByteArrayDataOutput
name|output
parameter_list|,
name|byte
index|[]
name|buffer
parameter_list|,
name|BytesRef
name|spare
parameter_list|,
name|long
name|weight
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|spare
operator|.
name|length
operator|+
literal|5
operator|>=
name|buffer
operator|.
name|length
condition|)
block|{
name|buffer
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|buffer
argument_list|,
name|spare
operator|.
name|length
operator|+
literal|5
argument_list|)
expr_stmt|;
block|}
name|output
operator|.
name|reset
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeBytes
argument_list|(
name|spare
operator|.
name|bytes
argument_list|,
name|spare
operator|.
name|offset
argument_list|,
name|spare
operator|.
name|length
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|0
argument_list|)
expr_stmt|;
comment|// separator: not used, just for sort order
name|output
operator|.
name|writeInt
argument_list|(
name|encodeWeight
argument_list|(
name|weight
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|output
operator|.
name|getPosition
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|decode
specifier|protected
name|long
name|decode
parameter_list|(
name|BytesRef
name|scratch
parameter_list|,
name|ByteArrayDataInput
name|tmpInput
parameter_list|)
block|{
name|tmpInput
operator|.
name|reset
argument_list|(
name|scratch
operator|.
name|bytes
argument_list|)
expr_stmt|;
name|tmpInput
operator|.
name|skipBytes
argument_list|(
name|scratch
operator|.
name|length
operator|-
literal|4
argument_list|)
expr_stmt|;
comment|// suggestion + separator
name|scratch
operator|.
name|length
operator|-=
literal|5
expr_stmt|;
comment|// sep + long
return|return
name|tmpInput
operator|.
name|readInt
argument_list|()
return|;
block|}
block|}
DECL|field|weightComparator
specifier|static
specifier|final
name|Comparator
argument_list|<
name|Long
argument_list|>
name|weightComparator
init|=
operator|new
name|Comparator
argument_list|<
name|Long
argument_list|>
argument_list|()
block|{
specifier|public
name|int
name|compare
parameter_list|(
name|Long
name|left
parameter_list|,
name|Long
name|right
parameter_list|)
block|{
return|return
name|left
operator|.
name|compareTo
argument_list|(
name|right
argument_list|)
return|;
block|}
block|}
decl_stmt|;
block|}
end_class

end_unit

