begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
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
name|BufferedIndexInput
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
name|util
operator|.
name|MathUtil
import|;
end_import

begin_comment
comment|/**  * This abstract class reads skip lists with multiple levels.  *   * See {@link MultiLevelSkipListWriter} for the information about the encoding   * of the multi level skip lists.   *   * Subclasses must implement the abstract method {@link #readSkipData(int, IndexInput)}  * which defines the actual format of the skip data.  * @lucene.experimental  */
end_comment

begin_class
DECL|class|MultiLevelSkipListReader
specifier|public
specifier|abstract
class|class
name|MultiLevelSkipListReader
block|{
comment|// the maximum number of skip levels possible for this index
DECL|field|maxNumberOfSkipLevels
specifier|protected
name|int
name|maxNumberOfSkipLevels
decl_stmt|;
comment|// number of levels in this skip list
DECL|field|numberOfSkipLevels
specifier|private
name|int
name|numberOfSkipLevels
decl_stmt|;
comment|// Expert: defines the number of top skip levels to buffer in memory.
comment|// Reducing this number results in less memory usage, but possibly
comment|// slower performance due to more random I/Os.
comment|// Please notice that the space each level occupies is limited by
comment|// the skipInterval. The top level can not contain more than
comment|// skipLevel entries, the second top level can not contain more
comment|// than skipLevel^2 entries and so forth.
DECL|field|numberOfLevelsToBuffer
specifier|private
name|int
name|numberOfLevelsToBuffer
init|=
literal|1
decl_stmt|;
DECL|field|docCount
specifier|private
name|int
name|docCount
decl_stmt|;
DECL|field|haveSkipped
specifier|private
name|boolean
name|haveSkipped
decl_stmt|;
DECL|field|skipStream
specifier|private
name|IndexInput
index|[]
name|skipStream
decl_stmt|;
comment|// skipStream for each level
DECL|field|skipPointer
specifier|private
name|long
name|skipPointer
index|[]
decl_stmt|;
comment|// the start pointer of each skip level
DECL|field|skipInterval
specifier|private
name|int
name|skipInterval
index|[]
decl_stmt|;
comment|// skipInterval of each level
DECL|field|numSkipped
specifier|private
name|int
index|[]
name|numSkipped
decl_stmt|;
comment|// number of docs skipped per level
DECL|field|skipDoc
specifier|private
name|int
index|[]
name|skipDoc
decl_stmt|;
comment|// doc id of current skip entry per level
DECL|field|lastDoc
specifier|private
name|int
name|lastDoc
decl_stmt|;
comment|// doc id of last read skip entry with docId<= target
DECL|field|childPointer
specifier|private
name|long
index|[]
name|childPointer
decl_stmt|;
comment|// child pointer of current skip entry per level
DECL|field|lastChildPointer
specifier|private
name|long
name|lastChildPointer
decl_stmt|;
comment|// childPointer of last read skip entry with docId<= target
DECL|field|inputIsBuffered
specifier|private
name|boolean
name|inputIsBuffered
decl_stmt|;
DECL|method|MultiLevelSkipListReader
specifier|public
name|MultiLevelSkipListReader
parameter_list|(
name|IndexInput
name|skipStream
parameter_list|,
name|int
name|maxSkipLevels
parameter_list|,
name|int
name|skipInterval
parameter_list|)
block|{
name|this
operator|.
name|skipStream
operator|=
operator|new
name|IndexInput
index|[
name|maxSkipLevels
index|]
expr_stmt|;
name|this
operator|.
name|skipPointer
operator|=
operator|new
name|long
index|[
name|maxSkipLevels
index|]
expr_stmt|;
name|this
operator|.
name|childPointer
operator|=
operator|new
name|long
index|[
name|maxSkipLevels
index|]
expr_stmt|;
name|this
operator|.
name|numSkipped
operator|=
operator|new
name|int
index|[
name|maxSkipLevels
index|]
expr_stmt|;
name|this
operator|.
name|maxNumberOfSkipLevels
operator|=
name|maxSkipLevels
expr_stmt|;
name|this
operator|.
name|skipInterval
operator|=
operator|new
name|int
index|[
name|maxSkipLevels
index|]
expr_stmt|;
name|this
operator|.
name|skipStream
index|[
literal|0
index|]
operator|=
name|skipStream
expr_stmt|;
name|this
operator|.
name|inputIsBuffered
operator|=
operator|(
name|skipStream
operator|instanceof
name|BufferedIndexInput
operator|)
expr_stmt|;
name|this
operator|.
name|skipInterval
index|[
literal|0
index|]
operator|=
name|skipInterval
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|maxSkipLevels
condition|;
name|i
operator|++
control|)
block|{
comment|// cache skip intervals
name|this
operator|.
name|skipInterval
index|[
name|i
index|]
operator|=
name|this
operator|.
name|skipInterval
index|[
name|i
operator|-
literal|1
index|]
operator|*
name|skipInterval
expr_stmt|;
block|}
name|skipDoc
operator|=
operator|new
name|int
index|[
name|maxSkipLevels
index|]
expr_stmt|;
block|}
comment|/** Returns the id of the doc to which the last call of {@link #skipTo(int)}    *  has skipped.  */
DECL|method|getDoc
specifier|public
name|int
name|getDoc
parameter_list|()
block|{
return|return
name|lastDoc
return|;
block|}
comment|/** Skips entries to the first beyond the current whose document number is    *  greater than or equal to<i>target</i>. Returns the current doc count.     */
DECL|method|skipTo
specifier|public
name|int
name|skipTo
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|haveSkipped
condition|)
block|{
comment|// first time, load skip levels
name|loadSkipLevels
argument_list|()
expr_stmt|;
name|haveSkipped
operator|=
literal|true
expr_stmt|;
block|}
comment|// walk up the levels until highest level is found that has a skip
comment|// for this target
name|int
name|level
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|level
argument_list|<
name|numberOfSkipLevels
operator|-
literal|1
operator|&&
name|target
argument_list|>
name|skipDoc
index|[
name|level
operator|+
literal|1
index|]
condition|)
block|{
name|level
operator|++
expr_stmt|;
block|}
while|while
condition|(
name|level
operator|>=
literal|0
condition|)
block|{
if|if
condition|(
name|target
operator|>
name|skipDoc
index|[
name|level
index|]
condition|)
block|{
if|if
condition|(
operator|!
name|loadNextSkip
argument_list|(
name|level
argument_list|)
condition|)
block|{
continue|continue;
block|}
block|}
else|else
block|{
comment|// no more skips on this level, go down one level
if|if
condition|(
name|level
operator|>
literal|0
operator|&&
name|lastChildPointer
operator|>
name|skipStream
index|[
name|level
operator|-
literal|1
index|]
operator|.
name|getFilePointer
argument_list|()
condition|)
block|{
name|seekChild
argument_list|(
name|level
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|level
operator|--
expr_stmt|;
block|}
block|}
return|return
name|numSkipped
index|[
literal|0
index|]
operator|-
name|skipInterval
index|[
literal|0
index|]
operator|-
literal|1
return|;
block|}
DECL|method|loadNextSkip
specifier|private
name|boolean
name|loadNextSkip
parameter_list|(
name|int
name|level
parameter_list|)
throws|throws
name|IOException
block|{
comment|// we have to skip, the target document is greater than the current
comment|// skip list entry
name|setLastSkipData
argument_list|(
name|level
argument_list|)
expr_stmt|;
name|numSkipped
index|[
name|level
index|]
operator|+=
name|skipInterval
index|[
name|level
index|]
expr_stmt|;
if|if
condition|(
name|numSkipped
index|[
name|level
index|]
operator|>
name|docCount
condition|)
block|{
comment|// this skip list is exhausted
name|skipDoc
index|[
name|level
index|]
operator|=
name|Integer
operator|.
name|MAX_VALUE
expr_stmt|;
if|if
condition|(
name|numberOfSkipLevels
operator|>
name|level
condition|)
name|numberOfSkipLevels
operator|=
name|level
expr_stmt|;
return|return
literal|false
return|;
block|}
comment|// read next skip entry
name|skipDoc
index|[
name|level
index|]
operator|+=
name|readSkipData
argument_list|(
name|level
argument_list|,
name|skipStream
index|[
name|level
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
name|level
operator|!=
literal|0
condition|)
block|{
comment|// read the child pointer if we are not on the leaf level
name|childPointer
index|[
name|level
index|]
operator|=
name|skipStream
index|[
name|level
index|]
operator|.
name|readVLong
argument_list|()
operator|+
name|skipPointer
index|[
name|level
operator|-
literal|1
index|]
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
comment|/** Seeks the skip entry on the given level */
DECL|method|seekChild
specifier|protected
name|void
name|seekChild
parameter_list|(
name|int
name|level
parameter_list|)
throws|throws
name|IOException
block|{
name|skipStream
index|[
name|level
index|]
operator|.
name|seek
argument_list|(
name|lastChildPointer
argument_list|)
expr_stmt|;
name|numSkipped
index|[
name|level
index|]
operator|=
name|numSkipped
index|[
name|level
operator|+
literal|1
index|]
operator|-
name|skipInterval
index|[
name|level
operator|+
literal|1
index|]
expr_stmt|;
name|skipDoc
index|[
name|level
index|]
operator|=
name|lastDoc
expr_stmt|;
if|if
condition|(
name|level
operator|>
literal|0
condition|)
block|{
name|childPointer
index|[
name|level
index|]
operator|=
name|skipStream
index|[
name|level
index|]
operator|.
name|readVLong
argument_list|()
operator|+
name|skipPointer
index|[
name|level
operator|-
literal|1
index|]
expr_stmt|;
block|}
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|skipStream
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|skipStream
index|[
name|i
index|]
operator|!=
literal|null
condition|)
block|{
name|skipStream
index|[
name|i
index|]
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/** initializes the reader */
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|long
name|skipPointer
parameter_list|,
name|int
name|df
parameter_list|)
block|{
name|this
operator|.
name|skipPointer
index|[
literal|0
index|]
operator|=
name|skipPointer
expr_stmt|;
name|this
operator|.
name|docCount
operator|=
name|df
expr_stmt|;
assert|assert
name|skipPointer
operator|>=
literal|0
operator|&&
name|skipPointer
operator|<=
name|skipStream
index|[
literal|0
index|]
operator|.
name|length
argument_list|()
operator|:
literal|"invalid skip pointer: "
operator|+
name|skipPointer
operator|+
literal|", length="
operator|+
name|skipStream
index|[
literal|0
index|]
operator|.
name|length
argument_list|()
assert|;
name|Arrays
operator|.
name|fill
argument_list|(
name|skipDoc
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|numSkipped
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|childPointer
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|haveSkipped
operator|=
literal|false
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|numberOfSkipLevels
condition|;
name|i
operator|++
control|)
block|{
name|skipStream
index|[
name|i
index|]
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|/** Loads the skip levels  */
DECL|method|loadSkipLevels
specifier|private
name|void
name|loadSkipLevels
parameter_list|()
throws|throws
name|IOException
block|{
name|numberOfSkipLevels
operator|=
name|MathUtil
operator|.
name|log
argument_list|(
name|docCount
argument_list|,
name|skipInterval
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
name|numberOfSkipLevels
operator|>
name|maxNumberOfSkipLevels
condition|)
block|{
name|numberOfSkipLevels
operator|=
name|maxNumberOfSkipLevels
expr_stmt|;
block|}
name|skipStream
index|[
literal|0
index|]
operator|.
name|seek
argument_list|(
name|skipPointer
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|int
name|toBuffer
init|=
name|numberOfLevelsToBuffer
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|numberOfSkipLevels
operator|-
literal|1
init|;
name|i
operator|>
literal|0
condition|;
name|i
operator|--
control|)
block|{
comment|// the length of the current level
name|long
name|length
init|=
name|skipStream
index|[
literal|0
index|]
operator|.
name|readVLong
argument_list|()
decl_stmt|;
comment|// the start pointer of the current level
name|skipPointer
index|[
name|i
index|]
operator|=
name|skipStream
index|[
literal|0
index|]
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
if|if
condition|(
name|toBuffer
operator|>
literal|0
condition|)
block|{
comment|// buffer this level
name|skipStream
index|[
name|i
index|]
operator|=
operator|new
name|SkipBuffer
argument_list|(
name|skipStream
index|[
literal|0
index|]
argument_list|,
operator|(
name|int
operator|)
name|length
argument_list|)
expr_stmt|;
name|toBuffer
operator|--
expr_stmt|;
block|}
else|else
block|{
comment|// clone this stream, it is already at the start of the current level
name|skipStream
index|[
name|i
index|]
operator|=
operator|(
name|IndexInput
operator|)
name|skipStream
index|[
literal|0
index|]
operator|.
name|clone
argument_list|()
expr_stmt|;
if|if
condition|(
name|inputIsBuffered
operator|&&
name|length
operator|<
name|BufferedIndexInput
operator|.
name|BUFFER_SIZE
condition|)
block|{
operator|(
operator|(
name|BufferedIndexInput
operator|)
name|skipStream
index|[
name|i
index|]
operator|)
operator|.
name|setBufferSize
argument_list|(
operator|(
name|int
operator|)
name|length
argument_list|)
expr_stmt|;
block|}
comment|// move base stream beyond the current level
name|skipStream
index|[
literal|0
index|]
operator|.
name|seek
argument_list|(
name|skipStream
index|[
literal|0
index|]
operator|.
name|getFilePointer
argument_list|()
operator|+
name|length
argument_list|)
expr_stmt|;
block|}
block|}
comment|// use base stream for the lowest level
name|skipPointer
index|[
literal|0
index|]
operator|=
name|skipStream
index|[
literal|0
index|]
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
block|}
comment|/**    * Subclasses must implement the actual skip data encoding in this method.    *      * @param level the level skip data shall be read from    * @param skipStream the skip stream to read from    */
DECL|method|readSkipData
specifier|protected
specifier|abstract
name|int
name|readSkipData
parameter_list|(
name|int
name|level
parameter_list|,
name|IndexInput
name|skipStream
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Copies the values of the last read skip entry on this level */
DECL|method|setLastSkipData
specifier|protected
name|void
name|setLastSkipData
parameter_list|(
name|int
name|level
parameter_list|)
block|{
name|lastDoc
operator|=
name|skipDoc
index|[
name|level
index|]
expr_stmt|;
name|lastChildPointer
operator|=
name|childPointer
index|[
name|level
index|]
expr_stmt|;
block|}
comment|/** used to buffer the top skip levels */
DECL|class|SkipBuffer
specifier|private
specifier|final
specifier|static
class|class
name|SkipBuffer
extends|extends
name|IndexInput
block|{
DECL|field|data
specifier|private
name|byte
index|[]
name|data
decl_stmt|;
DECL|field|pointer
specifier|private
name|long
name|pointer
decl_stmt|;
DECL|field|pos
specifier|private
name|int
name|pos
decl_stmt|;
DECL|method|SkipBuffer
name|SkipBuffer
parameter_list|(
name|IndexInput
name|input
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
literal|"SkipBuffer on "
operator|+
name|input
argument_list|)
expr_stmt|;
name|data
operator|=
operator|new
name|byte
index|[
name|length
index|]
expr_stmt|;
name|pointer
operator|=
name|input
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
name|input
operator|.
name|readBytes
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
name|data
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getFilePointer
specifier|public
name|long
name|getFilePointer
parameter_list|()
block|{
return|return
name|pointer
operator|+
name|pos
return|;
block|}
annotation|@
name|Override
DECL|method|length
specifier|public
name|long
name|length
parameter_list|()
block|{
return|return
name|data
operator|.
name|length
return|;
block|}
annotation|@
name|Override
DECL|method|readByte
specifier|public
name|byte
name|readByte
parameter_list|()
block|{
return|return
name|data
index|[
name|pos
operator|++
index|]
return|;
block|}
annotation|@
name|Override
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
parameter_list|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|data
argument_list|,
name|pos
argument_list|,
name|b
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|pos
operator|+=
name|len
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|seek
specifier|public
name|void
name|seek
parameter_list|(
name|long
name|pos
parameter_list|)
block|{
name|this
operator|.
name|pos
operator|=
call|(
name|int
call|)
argument_list|(
name|pos
operator|-
name|pointer
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

