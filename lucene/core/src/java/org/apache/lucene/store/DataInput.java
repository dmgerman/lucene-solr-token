begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.store
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
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
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|BitUtil
import|;
end_import

begin_comment
comment|/**  * Abstract base class for performing read operations of Lucene's low-level  * data types.  *  *<p>{@code DataInput} may only be used from one thread, because it is not  * thread safe (it keeps internal state like file position). To allow  * multithreaded use, every {@code DataInput} instance must be cloned before  * used in another thread. Subclasses must therefore implement {@link #clone()},  * returning a new {@code DataInput} which operates on the same underlying  * resource, but positioned independently.  */
end_comment

begin_class
DECL|class|DataInput
specifier|public
specifier|abstract
class|class
name|DataInput
implements|implements
name|Cloneable
block|{
DECL|field|SKIP_BUFFER_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|SKIP_BUFFER_SIZE
init|=
literal|1024
decl_stmt|;
comment|/* This buffer is used to skip over bytes with the default implementation of    * skipBytes. The reason why we need to use an instance member instead of    * sharing a single instance across threads is that some delegating    * implementations of DataInput might want to reuse the provided buffer in    * order to eg. update the checksum. If we shared the same buffer across    * threads, then another thread might update the buffer while the checksum is    * being computed, making it invalid. See LUCENE-5583 for more information.    */
DECL|field|skipBuffer
specifier|private
name|byte
index|[]
name|skipBuffer
decl_stmt|;
comment|/** Reads and returns a single byte.    * @see DataOutput#writeByte(byte)    */
DECL|method|readByte
specifier|public
specifier|abstract
name|byte
name|readByte
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Reads a specified number of bytes into an array at the specified offset.    * @param b the array to read bytes into    * @param offset the offset in the array to start storing bytes    * @param len the number of bytes to read    * @see DataOutput#writeBytes(byte[],int)    */
DECL|method|readBytes
specifier|public
specifier|abstract
name|void
name|readBytes
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Reads a specified number of bytes into an array at the    * specified offset with control over whether the read    * should be buffered (callers who have their own buffer    * should pass in "false" for useBuffer).  Currently only    * {@link BufferedIndexInput} respects this parameter.    * @param b the array to read bytes into    * @param offset the offset in the array to start storing bytes    * @param len the number of bytes to read    * @param useBuffer set to false if the caller will handle    * buffering.    * @see DataOutput#writeBytes(byte[],int)    */
DECL|method|readBytes
specifier|public
name|void
name|readBytes
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|,
name|boolean
name|useBuffer
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Default to ignoring useBuffer entirely
name|readBytes
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
comment|/** Reads two bytes and returns a short.    * @see DataOutput#writeByte(byte)    */
DECL|method|readShort
specifier|public
name|short
name|readShort
parameter_list|()
throws|throws
name|IOException
block|{
return|return
call|(
name|short
call|)
argument_list|(
operator|(
operator|(
name|readByte
argument_list|()
operator|&
literal|0xFF
operator|)
operator|<<
literal|8
operator|)
operator||
operator|(
name|readByte
argument_list|()
operator|&
literal|0xFF
operator|)
argument_list|)
return|;
block|}
comment|/** Reads four bytes and returns an int.    * @see DataOutput#writeInt(int)    */
DECL|method|readInt
specifier|public
name|int
name|readInt
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|(
operator|(
name|readByte
argument_list|()
operator|&
literal|0xFF
operator|)
operator|<<
literal|24
operator|)
operator||
operator|(
operator|(
name|readByte
argument_list|()
operator|&
literal|0xFF
operator|)
operator|<<
literal|16
operator|)
operator||
operator|(
operator|(
name|readByte
argument_list|()
operator|&
literal|0xFF
operator|)
operator|<<
literal|8
operator|)
operator||
operator|(
name|readByte
argument_list|()
operator|&
literal|0xFF
operator|)
return|;
block|}
comment|/** Reads an int stored in variable-length format.  Reads between one and    * five bytes.  Smaller values take fewer bytes.  Negative numbers are not    * supported.    *<p>    * The format is described further in {@link DataOutput#writeVInt(int)}.    *     * @see DataOutput#writeVInt(int)    */
DECL|method|readVInt
specifier|public
name|int
name|readVInt
parameter_list|()
throws|throws
name|IOException
block|{
comment|/* This is the original code of this method,      * but a Hotspot bug (see LUCENE-2975) corrupts the for-loop if      * readByte() is inlined. So the loop was unwinded!     byte b = readByte();     int i = b& 0x7F;     for (int shift = 7; (b& 0x80) != 0; shift += 7) {       b = readByte();       i |= (b& 0x7F)<< shift;     }     return i;     */
name|byte
name|b
init|=
name|readByte
argument_list|()
decl_stmt|;
if|if
condition|(
name|b
operator|>=
literal|0
condition|)
return|return
name|b
return|;
name|int
name|i
init|=
name|b
operator|&
literal|0x7F
decl_stmt|;
name|b
operator|=
name|readByte
argument_list|()
expr_stmt|;
name|i
operator||=
operator|(
name|b
operator|&
literal|0x7F
operator|)
operator|<<
literal|7
expr_stmt|;
if|if
condition|(
name|b
operator|>=
literal|0
condition|)
return|return
name|i
return|;
name|b
operator|=
name|readByte
argument_list|()
expr_stmt|;
name|i
operator||=
operator|(
name|b
operator|&
literal|0x7F
operator|)
operator|<<
literal|14
expr_stmt|;
if|if
condition|(
name|b
operator|>=
literal|0
condition|)
return|return
name|i
return|;
name|b
operator|=
name|readByte
argument_list|()
expr_stmt|;
name|i
operator||=
operator|(
name|b
operator|&
literal|0x7F
operator|)
operator|<<
literal|21
expr_stmt|;
if|if
condition|(
name|b
operator|>=
literal|0
condition|)
return|return
name|i
return|;
name|b
operator|=
name|readByte
argument_list|()
expr_stmt|;
comment|// Warning: the next ands use 0x0F / 0xF0 - beware copy/paste errors:
name|i
operator||=
operator|(
name|b
operator|&
literal|0x0F
operator|)
operator|<<
literal|28
expr_stmt|;
if|if
condition|(
operator|(
name|b
operator|&
literal|0xF0
operator|)
operator|==
literal|0
condition|)
return|return
name|i
return|;
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Invalid vInt detected (too many bits)"
argument_list|)
throw|;
block|}
comment|/**    * Read a {@link BitUtil#zigZagDecode(int) zig-zag}-encoded    * {@link #readVInt() variable-length} integer.    * @see DataOutput#writeZInt(int)    */
DECL|method|readZInt
specifier|public
name|int
name|readZInt
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|BitUtil
operator|.
name|zigZagDecode
argument_list|(
name|readVInt
argument_list|()
argument_list|)
return|;
block|}
comment|/** Reads eight bytes and returns a long.    * @see DataOutput#writeLong(long)    */
DECL|method|readLong
specifier|public
name|long
name|readLong
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|(
operator|(
operator|(
name|long
operator|)
name|readInt
argument_list|()
operator|)
operator|<<
literal|32
operator|)
operator||
operator|(
name|readInt
argument_list|()
operator|&
literal|0xFFFFFFFFL
operator|)
return|;
block|}
comment|/** Reads a long stored in variable-length format.  Reads between one and    * nine bytes.  Smaller values take fewer bytes.  Negative numbers are not    * supported.    *<p>    * The format is described further in {@link DataOutput#writeVInt(int)}.    *     * @see DataOutput#writeVLong(long)    */
DECL|method|readVLong
specifier|public
name|long
name|readVLong
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|readVLong
argument_list|(
literal|false
argument_list|)
return|;
block|}
DECL|method|readVLong
specifier|private
name|long
name|readVLong
parameter_list|(
name|boolean
name|allowNegative
parameter_list|)
throws|throws
name|IOException
block|{
comment|/* This is the original code of this method,      * but a Hotspot bug (see LUCENE-2975) corrupts the for-loop if      * readByte() is inlined. So the loop was unwinded!     byte b = readByte();     long i = b& 0x7F;     for (int shift = 7; (b& 0x80) != 0; shift += 7) {       b = readByte();       i |= (b& 0x7FL)<< shift;     }     return i;     */
name|byte
name|b
init|=
name|readByte
argument_list|()
decl_stmt|;
if|if
condition|(
name|b
operator|>=
literal|0
condition|)
return|return
name|b
return|;
name|long
name|i
init|=
name|b
operator|&
literal|0x7FL
decl_stmt|;
name|b
operator|=
name|readByte
argument_list|()
expr_stmt|;
name|i
operator||=
operator|(
name|b
operator|&
literal|0x7FL
operator|)
operator|<<
literal|7
expr_stmt|;
if|if
condition|(
name|b
operator|>=
literal|0
condition|)
return|return
name|i
return|;
name|b
operator|=
name|readByte
argument_list|()
expr_stmt|;
name|i
operator||=
operator|(
name|b
operator|&
literal|0x7FL
operator|)
operator|<<
literal|14
expr_stmt|;
if|if
condition|(
name|b
operator|>=
literal|0
condition|)
return|return
name|i
return|;
name|b
operator|=
name|readByte
argument_list|()
expr_stmt|;
name|i
operator||=
operator|(
name|b
operator|&
literal|0x7FL
operator|)
operator|<<
literal|21
expr_stmt|;
if|if
condition|(
name|b
operator|>=
literal|0
condition|)
return|return
name|i
return|;
name|b
operator|=
name|readByte
argument_list|()
expr_stmt|;
name|i
operator||=
operator|(
name|b
operator|&
literal|0x7FL
operator|)
operator|<<
literal|28
expr_stmt|;
if|if
condition|(
name|b
operator|>=
literal|0
condition|)
return|return
name|i
return|;
name|b
operator|=
name|readByte
argument_list|()
expr_stmt|;
name|i
operator||=
operator|(
name|b
operator|&
literal|0x7FL
operator|)
operator|<<
literal|35
expr_stmt|;
if|if
condition|(
name|b
operator|>=
literal|0
condition|)
return|return
name|i
return|;
name|b
operator|=
name|readByte
argument_list|()
expr_stmt|;
name|i
operator||=
operator|(
name|b
operator|&
literal|0x7FL
operator|)
operator|<<
literal|42
expr_stmt|;
if|if
condition|(
name|b
operator|>=
literal|0
condition|)
return|return
name|i
return|;
name|b
operator|=
name|readByte
argument_list|()
expr_stmt|;
name|i
operator||=
operator|(
name|b
operator|&
literal|0x7FL
operator|)
operator|<<
literal|49
expr_stmt|;
if|if
condition|(
name|b
operator|>=
literal|0
condition|)
return|return
name|i
return|;
name|b
operator|=
name|readByte
argument_list|()
expr_stmt|;
name|i
operator||=
operator|(
name|b
operator|&
literal|0x7FL
operator|)
operator|<<
literal|56
expr_stmt|;
if|if
condition|(
name|b
operator|>=
literal|0
condition|)
return|return
name|i
return|;
if|if
condition|(
name|allowNegative
condition|)
block|{
name|b
operator|=
name|readByte
argument_list|()
expr_stmt|;
name|i
operator||=
operator|(
name|b
operator|&
literal|0x7FL
operator|)
operator|<<
literal|63
expr_stmt|;
if|if
condition|(
name|b
operator|==
literal|0
operator|||
name|b
operator|==
literal|1
condition|)
return|return
name|i
return|;
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Invalid vLong detected (more than 64 bits)"
argument_list|)
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Invalid vLong detected (negative values disallowed)"
argument_list|)
throw|;
block|}
block|}
comment|/**    * Read a {@link BitUtil#zigZagDecode(long) zig-zag}-encoded    * {@link #readVLong() variable-length} integer. Reads between one and ten    * bytes.    * @see DataOutput#writeZLong(long)    */
DECL|method|readZLong
specifier|public
name|long
name|readZLong
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|BitUtil
operator|.
name|zigZagDecode
argument_list|(
name|readVLong
argument_list|(
literal|true
argument_list|)
argument_list|)
return|;
block|}
comment|/** Reads a string.    * @see DataOutput#writeString(String)    */
DECL|method|readString
specifier|public
name|String
name|readString
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|length
init|=
name|readVInt
argument_list|()
decl_stmt|;
specifier|final
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
name|length
index|]
decl_stmt|;
name|readBytes
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
return|return
operator|new
name|String
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|length
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
return|;
block|}
comment|/** Returns a clone of this stream.    *    *<p>Clones of a stream access the same data, and are positioned at the same    * point as the stream they were cloned from.    *    *<p>Expert: Subclasses must ensure that clones may be positioned at    * different points in the input from each other and from the stream they    * were cloned from.    */
annotation|@
name|Override
DECL|method|clone
specifier|public
name|DataInput
name|clone
parameter_list|()
block|{
try|try
block|{
return|return
operator|(
name|DataInput
operator|)
name|super
operator|.
name|clone
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|CloneNotSupportedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|Error
argument_list|(
literal|"This cannot happen: Failing to clone DataInput"
argument_list|)
throw|;
block|}
block|}
comment|/** Reads a Map&lt;String,String&gt; previously written    *  with {@link DataOutput#writeStringStringMap(Map)}. */
DECL|method|readStringStringMap
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|readStringStringMap
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|int
name|count
init|=
name|readInt
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
operator|<
name|count
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|String
name|key
init|=
name|readString
argument_list|()
decl_stmt|;
specifier|final
name|String
name|val
init|=
name|readString
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
return|return
name|map
return|;
block|}
comment|/** Reads a Set&lt;String&gt; previously written    *  with {@link DataOutput#writeStringSet(Set)}. */
DECL|method|readStringSet
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|readStringSet
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|set
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|int
name|count
init|=
name|readInt
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
operator|<
name|count
condition|;
name|i
operator|++
control|)
block|{
name|set
operator|.
name|add
argument_list|(
name|readString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|set
return|;
block|}
comment|/**    * Skip over<code>numBytes</code> bytes. The contract on this method is that it    * should have the same behavior as reading the same number of bytes into a    * buffer and discarding its content. Negative values of<code>numBytes</code>    * are not supported.    */
DECL|method|skipBytes
specifier|public
name|void
name|skipBytes
parameter_list|(
specifier|final
name|long
name|numBytes
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|numBytes
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"numBytes must be>= 0, got "
operator|+
name|numBytes
argument_list|)
throw|;
block|}
if|if
condition|(
name|skipBuffer
operator|==
literal|null
condition|)
block|{
name|skipBuffer
operator|=
operator|new
name|byte
index|[
name|SKIP_BUFFER_SIZE
index|]
expr_stmt|;
block|}
assert|assert
name|skipBuffer
operator|.
name|length
operator|==
name|SKIP_BUFFER_SIZE
assert|;
for|for
control|(
name|long
name|skipped
init|=
literal|0
init|;
name|skipped
operator|<
name|numBytes
condition|;
control|)
block|{
specifier|final
name|int
name|step
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|min
argument_list|(
name|SKIP_BUFFER_SIZE
argument_list|,
name|numBytes
operator|-
name|skipped
argument_list|)
decl_stmt|;
name|readBytes
argument_list|(
name|skipBuffer
argument_list|,
literal|0
argument_list|,
name|step
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|skipped
operator|+=
name|step
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

