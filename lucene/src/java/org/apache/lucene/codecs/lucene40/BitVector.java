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
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|CompoundFileDirectory
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
name|MutableBits
import|;
end_import

begin_comment
comment|/** Optimized implementation of a vector of bits.  This is more-or-less like  *  java.util.BitSet, but also includes the following:  *<ul>  *<li>a count() method, which efficiently computes the number of one bits;</li>  *<li>optimized read from and write to disk;</li>  *<li>inlinable get() method;</li>  *<li>store and load, as bit set or d-gaps, depending on sparseness;</li>   *</ul>  *  *  @lucene.internal  */
end_comment

begin_comment
comment|// pkg-private: if this thing is generally useful then it can go back in .util,
end_comment

begin_comment
comment|// but the serialization must be here underneath the codec.
end_comment

begin_class
DECL|class|BitVector
specifier|final
class|class
name|BitVector
implements|implements
name|Cloneable
implements|,
name|MutableBits
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
DECL|field|version
specifier|private
name|int
name|version
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
name|getNumBytes
argument_list|(
name|size
argument_list|)
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
DECL|method|getNumBytes
specifier|private
name|int
name|getNumBytes
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|int
name|bytesLength
init|=
name|size
operator|>>>
literal|3
decl_stmt|;
if|if
condition|(
operator|(
name|size
operator|&
literal|7
operator|)
operator|!=
literal|0
condition|)
block|{
name|bytesLength
operator|++
expr_stmt|;
block|}
return|return
name|bytesLength
return|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|BitVector
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
block|{
name|count
operator|++
expr_stmt|;
assert|assert
name|count
operator|<=
name|size
assert|;
block|}
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
DECL|method|getAndClear
specifier|public
specifier|final
name|boolean
name|getAndClear
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
operator|==
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
name|bits
index|[
name|pos
index|]
operator|&=
operator|~
name|flag
expr_stmt|;
if|if
condition|(
name|count
operator|!=
operator|-
literal|1
condition|)
block|{
name|count
operator|--
expr_stmt|;
assert|assert
name|count
operator|>=
literal|0
assert|;
block|}
return|return
literal|true
return|;
block|}
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
annotation|@
name|Override
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
block|{
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
block|}
name|count
operator|=
name|c
expr_stmt|;
block|}
assert|assert
name|count
operator|<=
name|size
operator|:
literal|"count="
operator|+
name|count
operator|+
literal|" size="
operator|+
name|size
assert|;
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
block|{
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
block|}
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
DECL|field|CODEC
specifier|private
specifier|static
name|String
name|CODEC
init|=
literal|"BitVector"
decl_stmt|;
comment|// Version before version tracking was added:
DECL|field|VERSION_PRE
specifier|public
specifier|final
specifier|static
name|int
name|VERSION_PRE
init|=
operator|-
literal|1
decl_stmt|;
comment|// First version:
DECL|field|VERSION_START
specifier|public
specifier|final
specifier|static
name|int
name|VERSION_START
init|=
literal|0
decl_stmt|;
comment|// Changed DGaps to encode gaps between cleared bits, not
comment|// set:
DECL|field|VERSION_DGAPS_CLEARED
specifier|public
specifier|final
specifier|static
name|int
name|VERSION_DGAPS_CLEARED
init|=
literal|1
decl_stmt|;
comment|// Increment version to change it:
DECL|field|VERSION_CURRENT
specifier|public
specifier|final
specifier|static
name|int
name|VERSION_CURRENT
init|=
name|VERSION_DGAPS_CLEARED
decl_stmt|;
DECL|method|getVersion
specifier|public
name|int
name|getVersion
parameter_list|()
block|{
return|return
name|version
return|;
block|}
comment|/** Writes this vector to the file<code>name</code> in Directory<code>d</code>, in a format that can be read by the constructor {@link     #BitVector(Directory, String, IOContext)}.  */
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
assert|assert
operator|!
operator|(
name|d
operator|instanceof
name|CompoundFileDirectory
operator|)
assert|;
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
name|output
operator|.
name|writeInt
argument_list|(
operator|-
literal|2
argument_list|)
expr_stmt|;
name|CodecUtil
operator|.
name|writeHeader
argument_list|(
name|output
argument_list|,
name|CODEC
argument_list|,
name|VERSION_CURRENT
argument_list|)
expr_stmt|;
if|if
condition|(
name|isSparse
argument_list|()
condition|)
block|{
comment|// sparse bit-set more efficiently saved as d-gaps.
name|writeClearedDgaps
argument_list|(
name|output
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|writeBits
argument_list|(
name|output
argument_list|)
expr_stmt|;
block|}
assert|assert
name|verifyCount
argument_list|()
assert|;
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
comment|/** Invert all bits */
DECL|method|invertAll
specifier|public
name|void
name|invertAll
parameter_list|()
block|{
if|if
condition|(
name|count
operator|!=
operator|-
literal|1
condition|)
block|{
name|count
operator|=
name|size
operator|-
name|count
expr_stmt|;
block|}
if|if
condition|(
name|bits
operator|.
name|length
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|bits
operator|.
name|length
condition|;
name|idx
operator|++
control|)
block|{
name|bits
index|[
name|idx
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
operator|~
name|bits
index|[
name|idx
index|]
argument_list|)
expr_stmt|;
block|}
name|clearUnusedBits
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|clearUnusedBits
specifier|private
name|void
name|clearUnusedBits
parameter_list|()
block|{
comment|// Take care not to invert the "unused" bits in the
comment|// last byte:
if|if
condition|(
name|bits
operator|.
name|length
operator|>
literal|0
condition|)
block|{
specifier|final
name|int
name|lastNBits
init|=
name|size
operator|&
literal|7
decl_stmt|;
if|if
condition|(
name|lastNBits
operator|!=
literal|0
condition|)
block|{
specifier|final
name|int
name|mask
init|=
operator|(
literal|1
operator|<<
name|lastNBits
operator|)
operator|-
literal|1
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
name|mask
expr_stmt|;
block|}
block|}
block|}
comment|/** Set all bits */
DECL|method|setAll
specifier|public
name|void
name|setAll
parameter_list|()
block|{
name|Arrays
operator|.
name|fill
argument_list|(
name|bits
argument_list|,
operator|(
name|byte
operator|)
literal|0xff
argument_list|)
expr_stmt|;
name|clearUnusedBits
argument_list|()
expr_stmt|;
name|count
operator|=
name|size
expr_stmt|;
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
DECL|method|writeClearedDgaps
specifier|private
name|void
name|writeClearedDgaps
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
name|numCleared
init|=
name|size
argument_list|()
operator|-
name|count
argument_list|()
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
name|bits
operator|.
name|length
operator|&&
name|numCleared
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
operator|(
name|byte
operator|)
literal|0xff
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
name|numCleared
operator|-=
operator|(
literal|8
operator|-
name|BYTE_COUNTS
index|[
name|bits
index|[
name|i
index|]
operator|&
literal|0xFF
index|]
operator|)
expr_stmt|;
assert|assert
name|numCleared
operator|>=
literal|0
operator|||
operator|(
name|i
operator|==
operator|(
name|bits
operator|.
name|length
operator|-
literal|1
operator|)
operator|&&
name|numCleared
operator|==
operator|-
operator|(
literal|8
operator|-
operator|(
name|size
operator|&
literal|7
operator|)
operator|)
operator|)
assert|;
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
specifier|final
name|int
name|clearedCount
init|=
name|size
argument_list|()
operator|-
name|count
argument_list|()
decl_stmt|;
if|if
condition|(
name|clearedCount
operator|==
literal|0
condition|)
block|{
return|return
literal|true
return|;
block|}
specifier|final
name|int
name|avgGapLength
init|=
name|bits
operator|.
name|length
operator|/
name|clearedCount
decl_stmt|;
comment|// expected number of bytes for vInt encoding of each gap
specifier|final
name|int
name|expectedDGapBytes
decl_stmt|;
if|if
condition|(
name|avgGapLength
operator|<=
operator|(
literal|1
operator|<<
literal|7
operator|)
condition|)
block|{
name|expectedDGapBytes
operator|=
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|avgGapLength
operator|<=
operator|(
literal|1
operator|<<
literal|14
operator|)
condition|)
block|{
name|expectedDGapBytes
operator|=
literal|2
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|avgGapLength
operator|<=
operator|(
literal|1
operator|<<
literal|21
operator|)
condition|)
block|{
name|expectedDGapBytes
operator|=
literal|3
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|avgGapLength
operator|<=
operator|(
literal|1
operator|<<
literal|28
operator|)
condition|)
block|{
name|expectedDGapBytes
operator|=
literal|4
expr_stmt|;
block|}
else|else
block|{
name|expectedDGapBytes
operator|=
literal|5
expr_stmt|;
block|}
comment|// +1 because we write the byte itself that contains the
comment|// set bit
specifier|final
name|int
name|bytesPerSetBit
init|=
name|expectedDGapBytes
operator|+
literal|1
decl_stmt|;
comment|// note: adding 32 because we start with ((int) -1) to indicate d-gaps format.
specifier|final
name|long
name|expectedBits
init|=
literal|32
operator|+
literal|8
operator|*
name|bytesPerSetBit
operator|*
name|clearedCount
decl_stmt|;
comment|// note: factor is for read/write of byte-arrays being faster than vints.
specifier|final
name|long
name|factor
init|=
literal|10
decl_stmt|;
return|return
name|factor
operator|*
name|expectedBits
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
specifier|final
name|int
name|firstInt
init|=
name|input
operator|.
name|readInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|firstInt
operator|==
operator|-
literal|2
condition|)
block|{
comment|// New format, with full header& version:
name|version
operator|=
name|CodecUtil
operator|.
name|checkHeader
argument_list|(
name|input
argument_list|,
name|CODEC
argument_list|,
name|VERSION_START
argument_list|,
name|VERSION_CURRENT
argument_list|)
expr_stmt|;
name|size
operator|=
name|input
operator|.
name|readInt
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|version
operator|=
name|VERSION_PRE
expr_stmt|;
name|size
operator|=
name|firstInt
expr_stmt|;
block|}
if|if
condition|(
name|size
operator|==
operator|-
literal|1
condition|)
block|{
if|if
condition|(
name|version
operator|>=
name|VERSION_DGAPS_CLEARED
condition|)
block|{
name|readClearedDgaps
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|readSetDgaps
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|readBits
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|version
operator|<
name|VERSION_DGAPS_CLEARED
condition|)
block|{
name|invertAll
argument_list|()
expr_stmt|;
block|}
assert|assert
name|verifyCount
argument_list|()
assert|;
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
comment|// asserts only
DECL|method|verifyCount
specifier|private
name|boolean
name|verifyCount
parameter_list|()
block|{
assert|assert
name|count
operator|!=
operator|-
literal|1
assert|;
specifier|final
name|int
name|countSav
init|=
name|count
decl_stmt|;
name|count
operator|=
operator|-
literal|1
expr_stmt|;
assert|assert
name|countSav
operator|==
name|count
argument_list|()
operator|:
literal|"saved count was "
operator|+
name|countSav
operator|+
literal|" but recomputed count is "
operator|+
name|count
assert|;
return|return
literal|true
return|;
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
name|getNumBytes
argument_list|(
name|size
argument_list|)
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
DECL|method|readSetDgaps
specifier|private
name|void
name|readSetDgaps
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
name|getNumBytes
argument_list|(
name|size
argument_list|)
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
assert|assert
name|n
operator|>=
literal|0
assert|;
block|}
block|}
comment|/** read as a d-gaps cleared bits list */
DECL|method|readClearedDgaps
specifier|private
name|void
name|readClearedDgaps
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
name|getNumBytes
argument_list|(
name|size
argument_list|)
index|]
expr_stmt|;
comment|// allocate bits
name|Arrays
operator|.
name|fill
argument_list|(
name|bits
argument_list|,
operator|(
name|byte
operator|)
literal|0xff
argument_list|)
expr_stmt|;
name|clearUnusedBits
argument_list|()
expr_stmt|;
name|int
name|last
init|=
literal|0
decl_stmt|;
name|int
name|numCleared
init|=
name|size
argument_list|()
operator|-
name|count
argument_list|()
decl_stmt|;
while|while
condition|(
name|numCleared
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
name|numCleared
operator|-=
literal|8
operator|-
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
assert|assert
name|numCleared
operator|>=
literal|0
operator|||
operator|(
name|last
operator|==
operator|(
name|bits
operator|.
name|length
operator|-
literal|1
operator|)
operator|&&
name|numCleared
operator|==
operator|-
operator|(
literal|8
operator|-
operator|(
name|size
operator|&
literal|7
operator|)
operator|)
operator|)
assert|;
block|}
block|}
block|}
end_class

end_unit

