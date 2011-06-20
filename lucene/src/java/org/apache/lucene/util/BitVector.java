begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
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
name|index
operator|.
name|IOContext
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
name|IOContext
operator|.
name|Context
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
name|MergePolicy
operator|.
name|OneMerge
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
name|Directory
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
name|store
operator|.
name|IndexOutput
import|;
end_import

begin_comment
comment|/** Optimized implementation of a vector of bits.  This is more-or-less like   java.util.BitSet, but also includes the following:<ul><li>a count() method, which efficiently computes the number of one bits;</li><li>optimized read from and write to disk;</li><li>inlinable get() method;</li><li>store and load, as bit set or d-gaps, depending on sparseness;</li></ul>   */
end_comment

begin_class
DECL|class|BitVector
specifier|public
specifier|final
class|class
name|BitVector
implements|implements
name|Cloneable
implements|,
name|Bits
block|{
DECL|field|bits
specifier|private
name|byte
index|[]
name|bits
decl_stmt|;
DECL|field|size
specifier|private
name|int
name|size
decl_stmt|;
DECL|field|count
specifier|private
name|int
name|count
decl_stmt|;
comment|/** Constructs a vector capable of holding<code>n</code> bits. */
DECL|method|BitVector
specifier|public
name|BitVector
parameter_list|(
name|int
name|n
parameter_list|)
block|{
name|size
operator|=
name|n
expr_stmt|;
name|bits
operator|=
operator|new
name|byte
index|[
operator|(
name|size
operator|>>
literal|3
operator|)
operator|+
literal|1
index|]
expr_stmt|;
name|count
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|BitVector
name|BitVector
parameter_list|(
name|byte
index|[]
name|bits
parameter_list|,
name|int
name|size
parameter_list|)
block|{
name|this
operator|.
name|bits
operator|=
name|bits
expr_stmt|;
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
name|count
operator|=
operator|-
literal|1
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|Object
name|clone
parameter_list|()
block|{
name|byte
index|[]
name|copyBits
init|=
operator|new
name|byte
index|[
name|bits
operator|.
name|length
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|bits
argument_list|,
literal|0
argument_list|,
name|copyBits
argument_list|,
literal|0
argument_list|,
name|bits
operator|.
name|length
argument_list|)
expr_stmt|;
name|BitVector
name|clone
init|=
operator|new
name|BitVector
argument_list|(
name|copyBits
argument_list|,
name|size
argument_list|)
decl_stmt|;
name|clone
operator|.
name|count
operator|=
name|count
expr_stmt|;
return|return
name|clone
return|;
block|}
comment|/** Sets the value of<code>bit</code> to one. */
DECL|method|set
specifier|public
specifier|final
name|void
name|set
parameter_list|(
name|int
name|bit
parameter_list|)
block|{
if|if
condition|(
name|bit
operator|>=
name|size
condition|)
block|{
throw|throw
operator|new
name|ArrayIndexOutOfBoundsException
argument_list|(
literal|"bit="
operator|+
name|bit
operator|+
literal|" size="
operator|+
name|size
argument_list|)
throw|;
block|}
name|bits
index|[
name|bit
operator|>>
literal|3
index|]
operator||=
literal|1
operator|<<
operator|(
name|bit
operator|&
literal|7
operator|)
expr_stmt|;
name|count
operator|=
operator|-
literal|1
expr_stmt|;
block|}
comment|/** Sets the value of<code>bit</code> to true, and    *  returns true if bit was already set */
DECL|method|getAndSet
specifier|public
specifier|final
name|boolean
name|getAndSet
parameter_list|(
name|int
name|bit
parameter_list|)
block|{
if|if
condition|(
name|bit
operator|>=
name|size
condition|)
block|{
throw|throw
operator|new
name|ArrayIndexOutOfBoundsException
argument_list|(
literal|"bit="
operator|+
name|bit
operator|+
literal|" size="
operator|+
name|size
argument_list|)
throw|;
block|}
specifier|final
name|int
name|pos
init|=
name|bit
operator|>>
literal|3
decl_stmt|;
specifier|final
name|int
name|v
init|=
name|bits
index|[
name|pos
index|]
decl_stmt|;
specifier|final
name|int
name|flag
init|=
literal|1
operator|<<
operator|(
name|bit
operator|&
literal|7
operator|)
decl_stmt|;
if|if
condition|(
operator|(
name|flag
operator|&
name|v
operator|)
operator|!=
literal|0
condition|)
return|return
literal|true
return|;
else|else
block|{
name|bits
index|[
name|pos
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|v
operator||
name|flag
argument_list|)
expr_stmt|;
if|if
condition|(
name|count
operator|!=
operator|-
literal|1
condition|)
name|count
operator|++
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
comment|/** Sets the value of<code>bit</code> to zero. */
DECL|method|clear
specifier|public
specifier|final
name|void
name|clear
parameter_list|(
name|int
name|bit
parameter_list|)
block|{
if|if
condition|(
name|bit
operator|>=
name|size
condition|)
block|{
throw|throw
operator|new
name|ArrayIndexOutOfBoundsException
argument_list|(
name|bit
argument_list|)
throw|;
block|}
name|bits
index|[
name|bit
operator|>>
literal|3
index|]
operator|&=
operator|~
operator|(
literal|1
operator|<<
operator|(
name|bit
operator|&
literal|7
operator|)
operator|)
expr_stmt|;
name|count
operator|=
operator|-
literal|1
expr_stmt|;
block|}
comment|/** Returns<code>true</code> if<code>bit</code> is one and<code>false</code> if it is zero. */
DECL|method|get
specifier|public
specifier|final
name|boolean
name|get
parameter_list|(
name|int
name|bit
parameter_list|)
block|{
assert|assert
name|bit
operator|>=
literal|0
operator|&&
name|bit
operator|<
name|size
operator|:
literal|"bit "
operator|+
name|bit
operator|+
literal|" is out of bounds 0.."
operator|+
operator|(
name|size
operator|-
literal|1
operator|)
assert|;
return|return
operator|(
name|bits
index|[
name|bit
operator|>>
literal|3
index|]
operator|&
operator|(
literal|1
operator|<<
operator|(
name|bit
operator|&
literal|7
operator|)
operator|)
operator|)
operator|!=
literal|0
return|;
block|}
comment|/** Returns the number of bits in this vector.  This is also one greater than     the number of the largest valid bit number. */
DECL|method|size
specifier|public
specifier|final
name|int
name|size
parameter_list|()
block|{
return|return
name|size
return|;
block|}
comment|// @Override -- not until Java 1.6
DECL|method|length
specifier|public
name|int
name|length
parameter_list|()
block|{
return|return
name|size
return|;
block|}
comment|/** Returns the total number of one bits in this vector.  This is efficiently     computed and cached, so that, if the vector is not changed, no     recomputation is done for repeated calls. */
DECL|method|count
specifier|public
specifier|final
name|int
name|count
parameter_list|()
block|{
comment|// if the vector has been modified
if|if
condition|(
name|count
operator|==
operator|-
literal|1
condition|)
block|{
name|int
name|c
init|=
literal|0
decl_stmt|;
name|int
name|end
init|=
name|bits
operator|.
name|length
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|end
condition|;
name|i
operator|++
control|)
name|c
operator|+=
name|BYTE_COUNTS
index|[
name|bits
index|[
name|i
index|]
operator|&
literal|0xFF
index|]
expr_stmt|;
comment|// sum bits per byte
name|count
operator|=
name|c
expr_stmt|;
block|}
return|return
name|count
return|;
block|}
comment|/** For testing */
DECL|method|getRecomputedCount
specifier|public
specifier|final
name|int
name|getRecomputedCount
parameter_list|()
block|{
name|int
name|c
init|=
literal|0
decl_stmt|;
name|int
name|end
init|=
name|bits
operator|.
name|length
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|end
condition|;
name|i
operator|++
control|)
name|c
operator|+=
name|BYTE_COUNTS
index|[
name|bits
index|[
name|i
index|]
operator|&
literal|0xFF
index|]
expr_stmt|;
comment|// sum bits per byte
return|return
name|c
return|;
block|}
DECL|field|BYTE_COUNTS
specifier|private
specifier|static
specifier|final
name|byte
index|[]
name|BYTE_COUNTS
init|=
block|{
comment|// table of bits/byte
literal|0
block|,
literal|1
block|,
literal|1
block|,
literal|2
block|,
literal|1
block|,
literal|2
block|,
literal|2
block|,
literal|3
block|,
literal|1
block|,
literal|2
block|,
literal|2
block|,
literal|3
block|,
literal|2
block|,
literal|3
block|,
literal|3
block|,
literal|4
block|,
literal|1
block|,
literal|2
block|,
literal|2
block|,
literal|3
block|,
literal|2
block|,
literal|3
block|,
literal|3
block|,
literal|4
block|,
literal|2
block|,
literal|3
block|,
literal|3
block|,
literal|4
block|,
literal|3
block|,
literal|4
block|,
literal|4
block|,
literal|5
block|,
literal|1
block|,
literal|2
block|,
literal|2
block|,
literal|3
block|,
literal|2
block|,
literal|3
block|,
literal|3
block|,
literal|4
block|,
literal|2
block|,
literal|3
block|,
literal|3
block|,
literal|4
block|,
literal|3
block|,
literal|4
block|,
literal|4
block|,
literal|5
block|,
literal|2
block|,
literal|3
block|,
literal|3
block|,
literal|4
block|,
literal|3
block|,
literal|4
block|,
literal|4
block|,
literal|5
block|,
literal|3
block|,
literal|4
block|,
literal|4
block|,
literal|5
block|,
literal|4
block|,
literal|5
block|,
literal|5
block|,
literal|6
block|,
literal|1
block|,
literal|2
block|,
literal|2
block|,
literal|3
block|,
literal|2
block|,
literal|3
block|,
literal|3
block|,
literal|4
block|,
literal|2
block|,
literal|3
block|,
literal|3
block|,
literal|4
block|,
literal|3
block|,
literal|4
block|,
literal|4
block|,
literal|5
block|,
literal|2
block|,
literal|3
block|,
literal|3
block|,
literal|4
block|,
literal|3
block|,
literal|4
block|,
literal|4
block|,
literal|5
block|,
literal|3
block|,
literal|4
block|,
literal|4
block|,
literal|5
block|,
literal|4
block|,
literal|5
block|,
literal|5
block|,
literal|6
block|,
literal|2
block|,
literal|3
block|,
literal|3
block|,
literal|4
block|,
literal|3
block|,
literal|4
block|,
literal|4
block|,
literal|5
block|,
literal|3
block|,
literal|4
block|,
literal|4
block|,
literal|5
block|,
literal|4
block|,
literal|5
block|,
literal|5
block|,
literal|6
block|,
literal|3
block|,
literal|4
block|,
literal|4
block|,
literal|5
block|,
literal|4
block|,
literal|5
block|,
literal|5
block|,
literal|6
block|,
literal|4
block|,
literal|5
block|,
literal|5
block|,
literal|6
block|,
literal|5
block|,
literal|6
block|,
literal|6
block|,
literal|7
block|,
literal|1
block|,
literal|2
block|,
literal|2
block|,
literal|3
block|,
literal|2
block|,
literal|3
block|,
literal|3
block|,
literal|4
block|,
literal|2
block|,
literal|3
block|,
literal|3
block|,
literal|4
block|,
literal|3
block|,
literal|4
block|,
literal|4
block|,
literal|5
block|,
literal|2
block|,
literal|3
block|,
literal|3
block|,
literal|4
block|,
literal|3
block|,
literal|4
block|,
literal|4
block|,
literal|5
block|,
literal|3
block|,
literal|4
block|,
literal|4
block|,
literal|5
block|,
literal|4
block|,
literal|5
block|,
literal|5
block|,
literal|6
block|,
literal|2
block|,
literal|3
block|,
literal|3
block|,
literal|4
block|,
literal|3
block|,
literal|4
block|,
literal|4
block|,
literal|5
block|,
literal|3
block|,
literal|4
block|,
literal|4
block|,
literal|5
block|,
literal|4
block|,
literal|5
block|,
literal|5
block|,
literal|6
block|,
literal|3
block|,
literal|4
block|,
literal|4
block|,
literal|5
block|,
literal|4
block|,
literal|5
block|,
literal|5
block|,
literal|6
block|,
literal|4
block|,
literal|5
block|,
literal|5
block|,
literal|6
block|,
literal|5
block|,
literal|6
block|,
literal|6
block|,
literal|7
block|,
literal|2
block|,
literal|3
block|,
literal|3
block|,
literal|4
block|,
literal|3
block|,
literal|4
block|,
literal|4
block|,
literal|5
block|,
literal|3
block|,
literal|4
block|,
literal|4
block|,
literal|5
block|,
literal|4
block|,
literal|5
block|,
literal|5
block|,
literal|6
block|,
literal|3
block|,
literal|4
block|,
literal|4
block|,
literal|5
block|,
literal|4
block|,
literal|5
block|,
literal|5
block|,
literal|6
block|,
literal|4
block|,
literal|5
block|,
literal|5
block|,
literal|6
block|,
literal|5
block|,
literal|6
block|,
literal|6
block|,
literal|7
block|,
literal|3
block|,
literal|4
block|,
literal|4
block|,
literal|5
block|,
literal|4
block|,
literal|5
block|,
literal|5
block|,
literal|6
block|,
literal|4
block|,
literal|5
block|,
literal|5
block|,
literal|6
block|,
literal|5
block|,
literal|6
block|,
literal|6
block|,
literal|7
block|,
literal|4
block|,
literal|5
block|,
literal|5
block|,
literal|6
block|,
literal|5
block|,
literal|6
block|,
literal|6
block|,
literal|7
block|,
literal|5
block|,
literal|6
block|,
literal|6
block|,
literal|7
block|,
literal|6
block|,
literal|7
block|,
literal|7
block|,
literal|8
block|}
decl_stmt|;
comment|/** Writes this vector to the file<code>name</code> in Directory<code>d</code>, in a format that can be read by the constructor {@link     #BitVector(Directory, String)}.  */
DECL|method|write
specifier|public
specifier|final
name|void
name|write
parameter_list|(
name|Directory
name|d
parameter_list|,
name|String
name|name
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexOutput
name|output
init|=
name|d
operator|.
name|createOutput
argument_list|(
name|name
argument_list|,
name|context
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
name|isSparse
argument_list|()
condition|)
block|{
name|writeDgaps
argument_list|(
name|output
argument_list|)
expr_stmt|;
comment|// sparse bit-set more efficiently saved as d-gaps.
block|}
else|else
block|{
name|writeBits
argument_list|(
name|output
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** Write as a bit set */
DECL|method|writeBits
specifier|private
name|void
name|writeBits
parameter_list|(
name|IndexOutput
name|output
parameter_list|)
throws|throws
name|IOException
block|{
name|output
operator|.
name|writeInt
argument_list|(
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// write size
name|output
operator|.
name|writeInt
argument_list|(
name|count
argument_list|()
argument_list|)
expr_stmt|;
comment|// write count
name|output
operator|.
name|writeBytes
argument_list|(
name|bits
argument_list|,
name|bits
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
comment|/** Write as a d-gaps list */
DECL|method|writeDgaps
specifier|private
name|void
name|writeDgaps
parameter_list|(
name|IndexOutput
name|output
parameter_list|)
throws|throws
name|IOException
block|{
name|output
operator|.
name|writeInt
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|// mark using d-gaps
name|output
operator|.
name|writeInt
argument_list|(
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// write size
name|output
operator|.
name|writeInt
argument_list|(
name|count
argument_list|()
argument_list|)
expr_stmt|;
comment|// write count
name|int
name|last
init|=
literal|0
decl_stmt|;
name|int
name|n
init|=
name|count
argument_list|()
decl_stmt|;
name|int
name|m
init|=
name|bits
operator|.
name|length
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
argument_list|<
name|m
operator|&&
name|n
argument_list|>
literal|0
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|bits
index|[
name|i
index|]
operator|!=
literal|0
condition|)
block|{
name|output
operator|.
name|writeVInt
argument_list|(
name|i
operator|-
name|last
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeByte
argument_list|(
name|bits
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|last
operator|=
name|i
expr_stmt|;
name|n
operator|-=
name|BYTE_COUNTS
index|[
name|bits
index|[
name|i
index|]
operator|&
literal|0xFF
index|]
expr_stmt|;
block|}
block|}
block|}
comment|/** Indicates if the bit vector is sparse and should be saved as a d-gaps list, or dense, and should be saved as a bit set. */
DECL|method|isSparse
specifier|private
name|boolean
name|isSparse
parameter_list|()
block|{
comment|// note: order of comparisons below set to favor smaller values (no binary range search.)
comment|// note: adding 4 because we start with ((int) -1) to indicate d-gaps format.
comment|// note: we write the d-gap for the byte number, and the byte (bits[i]) itself, therefore
comment|//       multiplying count by (8+8) or (8+16) or (8+24) etc.:
comment|//       - first 8 for writing bits[i] (1 byte vs. 1 bit), and
comment|//       - second part for writing the byte-number d-gap as vint.
comment|// note: factor is for read/write of byte-arrays being faster than vints.
name|int
name|factor
init|=
literal|10
decl_stmt|;
if|if
condition|(
name|bits
operator|.
name|length
operator|<
operator|(
literal|1
operator|<<
literal|7
operator|)
condition|)
return|return
name|factor
operator|*
operator|(
literal|4
operator|+
operator|(
literal|8
operator|+
literal|8
operator|)
operator|*
name|count
argument_list|()
operator|)
operator|<
name|size
argument_list|()
return|;
if|if
condition|(
name|bits
operator|.
name|length
operator|<
operator|(
literal|1
operator|<<
literal|14
operator|)
condition|)
return|return
name|factor
operator|*
operator|(
literal|4
operator|+
operator|(
literal|8
operator|+
literal|16
operator|)
operator|*
name|count
argument_list|()
operator|)
operator|<
name|size
argument_list|()
return|;
if|if
condition|(
name|bits
operator|.
name|length
operator|<
operator|(
literal|1
operator|<<
literal|21
operator|)
condition|)
return|return
name|factor
operator|*
operator|(
literal|4
operator|+
operator|(
literal|8
operator|+
literal|24
operator|)
operator|*
name|count
argument_list|()
operator|)
operator|<
name|size
argument_list|()
return|;
if|if
condition|(
name|bits
operator|.
name|length
operator|<
operator|(
literal|1
operator|<<
literal|28
operator|)
condition|)
return|return
name|factor
operator|*
operator|(
literal|4
operator|+
operator|(
literal|8
operator|+
literal|32
operator|)
operator|*
name|count
argument_list|()
operator|)
operator|<
name|size
argument_list|()
return|;
return|return
name|factor
operator|*
operator|(
literal|4
operator|+
operator|(
literal|8
operator|+
literal|40
operator|)
operator|*
name|count
argument_list|()
operator|)
operator|<
name|size
argument_list|()
return|;
block|}
comment|/** Constructs a bit vector from the file<code>name</code> in Directory<code>d</code>, as written by the {@link #write} method.     */
DECL|method|BitVector
specifier|public
name|BitVector
parameter_list|(
name|Directory
name|d
parameter_list|,
name|String
name|name
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexInput
name|input
init|=
name|d
operator|.
name|openInput
argument_list|(
name|name
argument_list|,
name|context
argument_list|)
decl_stmt|;
try|try
block|{
name|size
operator|=
name|input
operator|.
name|readInt
argument_list|()
expr_stmt|;
comment|// read size
if|if
condition|(
name|size
operator|==
operator|-
literal|1
condition|)
block|{
name|readDgaps
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|readBits
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** Read as a bit set */
DECL|method|readBits
specifier|private
name|void
name|readBits
parameter_list|(
name|IndexInput
name|input
parameter_list|)
throws|throws
name|IOException
block|{
name|count
operator|=
name|input
operator|.
name|readInt
argument_list|()
expr_stmt|;
comment|// read count
name|bits
operator|=
operator|new
name|byte
index|[
operator|(
name|size
operator|>>
literal|3
operator|)
operator|+
literal|1
index|]
expr_stmt|;
comment|// allocate bits
name|input
operator|.
name|readBytes
argument_list|(
name|bits
argument_list|,
literal|0
argument_list|,
name|bits
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
comment|/** read as a d-gaps list */
DECL|method|readDgaps
specifier|private
name|void
name|readDgaps
parameter_list|(
name|IndexInput
name|input
parameter_list|)
throws|throws
name|IOException
block|{
name|size
operator|=
name|input
operator|.
name|readInt
argument_list|()
expr_stmt|;
comment|// (re)read size
name|count
operator|=
name|input
operator|.
name|readInt
argument_list|()
expr_stmt|;
comment|// read count
name|bits
operator|=
operator|new
name|byte
index|[
operator|(
name|size
operator|>>
literal|3
operator|)
operator|+
literal|1
index|]
expr_stmt|;
comment|// allocate bits
name|int
name|last
init|=
literal|0
decl_stmt|;
name|int
name|n
init|=
name|count
argument_list|()
decl_stmt|;
while|while
condition|(
name|n
operator|>
literal|0
condition|)
block|{
name|last
operator|+=
name|input
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|bits
index|[
name|last
index|]
operator|=
name|input
operator|.
name|readByte
argument_list|()
expr_stmt|;
name|n
operator|-=
name|BYTE_COUNTS
index|[
name|bits
index|[
name|last
index|]
operator|&
literal|0xFF
index|]
expr_stmt|;
block|}
block|}
comment|/**    * Retrieve a subset of this BitVector.    *     * @param start    *            starting index, inclusive    * @param end    *            ending index, exclusive    * @return subset    */
DECL|method|subset
specifier|public
name|BitVector
name|subset
parameter_list|(
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
block|{
if|if
condition|(
name|start
argument_list|<
literal|0
operator|||
name|end
argument_list|>
name|size
argument_list|()
operator|||
name|end
operator|<
name|start
condition|)
throw|throw
operator|new
name|IndexOutOfBoundsException
argument_list|()
throw|;
comment|// Special case -- return empty vector is start == end
if|if
condition|(
name|end
operator|==
name|start
condition|)
return|return
operator|new
name|BitVector
argument_list|(
literal|0
argument_list|)
return|;
name|byte
index|[]
name|bits
init|=
operator|new
name|byte
index|[
operator|(
operator|(
name|end
operator|-
name|start
operator|-
literal|1
operator|)
operator|>>>
literal|3
operator|)
operator|+
literal|1
index|]
decl_stmt|;
name|int
name|s
init|=
name|start
operator|>>>
literal|3
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|bits
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|cur
init|=
literal|0xFF
operator|&
name|this
operator|.
name|bits
index|[
name|i
operator|+
name|s
index|]
decl_stmt|;
name|int
name|next
init|=
name|i
operator|+
name|s
operator|+
literal|1
operator|>=
name|this
operator|.
name|bits
operator|.
name|length
condition|?
literal|0
else|:
literal|0xFF
operator|&
name|this
operator|.
name|bits
index|[
name|i
operator|+
name|s
operator|+
literal|1
index|]
decl_stmt|;
name|bits
index|[
name|i
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
operator|(
name|cur
operator|>>>
operator|(
name|start
operator|&
literal|7
operator|)
operator|)
operator||
operator|(
operator|(
name|next
operator|<<
operator|(
literal|8
operator|-
operator|(
name|start
operator|&
literal|7
operator|)
operator|)
operator|)
operator|)
argument_list|)
expr_stmt|;
block|}
name|int
name|bitsToClear
init|=
operator|(
name|bits
operator|.
name|length
operator|*
literal|8
operator|-
operator|(
name|end
operator|-
name|start
operator|)
operator|)
operator|%
literal|8
decl_stmt|;
name|bits
index|[
name|bits
operator|.
name|length
operator|-
literal|1
index|]
operator|&=
operator|~
operator|(
literal|0xFF
operator|<<
operator|(
literal|8
operator|-
name|bitsToClear
operator|)
operator|)
expr_stmt|;
return|return
operator|new
name|BitVector
argument_list|(
name|bits
argument_list|,
name|end
operator|-
name|start
argument_list|)
return|;
block|}
block|}
end_class

end_unit

