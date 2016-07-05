begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.util.bkd
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|bkd
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|PointValues
operator|.
name|IntersectVisitor
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

begin_class
DECL|class|DocIdsWriter
class|class
name|DocIdsWriter
block|{
DECL|method|DocIdsWriter
specifier|private
name|DocIdsWriter
parameter_list|()
block|{}
DECL|method|writeDocIds
specifier|static
name|void
name|writeDocIds
parameter_list|(
name|int
index|[]
name|docIds
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|count
parameter_list|,
name|IndexOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
comment|// docs can be sorted either when all docs in a block have the same value
comment|// or when a segment is sorted
name|boolean
name|sorted
init|=
literal|true
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|count
condition|;
operator|++
name|i
control|)
block|{
if|if
condition|(
name|docIds
index|[
name|start
operator|+
name|i
operator|-
literal|1
index|]
operator|>
name|docIds
index|[
name|start
operator|+
name|i
index|]
condition|)
block|{
name|sorted
operator|=
literal|false
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|sorted
condition|)
block|{
name|out
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|0
argument_list|)
expr_stmt|;
name|int
name|previous
init|=
literal|0
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
operator|++
name|i
control|)
block|{
name|int
name|doc
init|=
name|docIds
index|[
name|start
operator|+
name|i
index|]
decl_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|doc
operator|-
name|previous
argument_list|)
expr_stmt|;
name|previous
operator|=
name|doc
expr_stmt|;
block|}
block|}
else|else
block|{
name|long
name|max
init|=
literal|0
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
operator|++
name|i
control|)
block|{
name|max
operator||=
name|Integer
operator|.
name|toUnsignedLong
argument_list|(
name|docIds
index|[
name|start
operator|+
name|i
index|]
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|max
operator|<=
literal|0xffffff
condition|)
block|{
name|out
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|24
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
name|count
condition|;
operator|++
name|i
control|)
block|{
name|out
operator|.
name|writeShort
argument_list|(
call|(
name|short
call|)
argument_list|(
name|docIds
index|[
name|start
operator|+
name|i
index|]
operator|>>>
literal|8
argument_list|)
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
name|docIds
index|[
name|start
operator|+
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|out
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|32
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
name|count
condition|;
operator|++
name|i
control|)
block|{
name|out
operator|.
name|writeInt
argument_list|(
name|docIds
index|[
name|start
operator|+
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/** Read {@code count} integers into {@code docIDs}. */
DECL|method|readInts
specifier|static
name|void
name|readInts
parameter_list|(
name|IndexInput
name|in
parameter_list|,
name|int
name|count
parameter_list|,
name|int
index|[]
name|docIDs
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|bpv
init|=
name|in
operator|.
name|readByte
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|bpv
condition|)
block|{
case|case
literal|0
case|:
name|readDeltaVInts
argument_list|(
name|in
argument_list|,
name|count
argument_list|,
name|docIDs
argument_list|)
expr_stmt|;
break|break;
case|case
literal|32
case|:
name|readInts32
argument_list|(
name|in
argument_list|,
name|count
argument_list|,
name|docIDs
argument_list|)
expr_stmt|;
break|break;
case|case
literal|24
case|:
name|readInts24
argument_list|(
name|in
argument_list|,
name|count
argument_list|,
name|docIDs
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unsupported number of bits per value: "
operator|+
name|bpv
argument_list|)
throw|;
block|}
block|}
DECL|method|readDeltaVInts
specifier|private
specifier|static
name|void
name|readDeltaVInts
parameter_list|(
name|IndexInput
name|in
parameter_list|,
name|int
name|count
parameter_list|,
name|int
index|[]
name|docIDs
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|doc
init|=
literal|0
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
name|doc
operator|+=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|docIDs
index|[
name|i
index|]
operator|=
name|doc
expr_stmt|;
block|}
block|}
DECL|method|readInts32
specifier|static
parameter_list|<
name|T
parameter_list|>
name|void
name|readInts32
parameter_list|(
name|IndexInput
name|in
parameter_list|,
name|int
name|count
parameter_list|,
name|int
index|[]
name|docIDs
parameter_list|)
throws|throws
name|IOException
block|{
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
name|docIDs
index|[
name|i
index|]
operator|=
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|readInts24
specifier|private
specifier|static
name|void
name|readInts24
parameter_list|(
name|IndexInput
name|in
parameter_list|,
name|int
name|count
parameter_list|,
name|int
index|[]
name|docIDs
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|i
decl_stmt|;
for|for
control|(
name|i
operator|=
literal|0
init|;
name|i
operator|<
name|count
operator|-
literal|7
condition|;
name|i
operator|+=
literal|8
control|)
block|{
name|long
name|l1
init|=
name|in
operator|.
name|readLong
argument_list|()
decl_stmt|;
name|long
name|l2
init|=
name|in
operator|.
name|readLong
argument_list|()
decl_stmt|;
name|long
name|l3
init|=
name|in
operator|.
name|readLong
argument_list|()
decl_stmt|;
name|docIDs
index|[
name|i
index|]
operator|=
call|(
name|int
call|)
argument_list|(
name|l1
operator|>>>
literal|40
argument_list|)
expr_stmt|;
name|docIDs
index|[
name|i
operator|+
literal|1
index|]
operator|=
call|(
name|int
call|)
argument_list|(
name|l1
operator|>>>
literal|16
argument_list|)
operator|&
literal|0xffffff
expr_stmt|;
name|docIDs
index|[
name|i
operator|+
literal|2
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
operator|(
name|l1
operator|&
literal|0xffff
operator|)
operator|<<
literal|8
operator|)
operator||
operator|(
name|l2
operator|>>>
literal|56
operator|)
argument_list|)
expr_stmt|;
name|docIDs
index|[
name|i
operator|+
literal|3
index|]
operator|=
call|(
name|int
call|)
argument_list|(
name|l2
operator|>>>
literal|32
argument_list|)
operator|&
literal|0xffffff
expr_stmt|;
name|docIDs
index|[
name|i
operator|+
literal|4
index|]
operator|=
call|(
name|int
call|)
argument_list|(
name|l2
operator|>>>
literal|8
argument_list|)
operator|&
literal|0xffffff
expr_stmt|;
name|docIDs
index|[
name|i
operator|+
literal|5
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
operator|(
name|l2
operator|&
literal|0xff
operator|)
operator|<<
literal|16
operator|)
operator||
operator|(
name|l3
operator|>>>
literal|48
operator|)
argument_list|)
expr_stmt|;
name|docIDs
index|[
name|i
operator|+
literal|6
index|]
operator|=
call|(
name|int
call|)
argument_list|(
name|l3
operator|>>>
literal|24
argument_list|)
operator|&
literal|0xffffff
expr_stmt|;
name|docIDs
index|[
name|i
operator|+
literal|7
index|]
operator|=
operator|(
name|int
operator|)
name|l3
operator|&
literal|0xffffff
expr_stmt|;
block|}
for|for
control|(
init|;
name|i
operator|<
name|count
condition|;
operator|++
name|i
control|)
block|{
name|docIDs
index|[
name|i
index|]
operator|=
operator|(
name|Short
operator|.
name|toUnsignedInt
argument_list|(
name|in
operator|.
name|readShort
argument_list|()
argument_list|)
operator|<<
literal|8
operator|)
operator||
name|Byte
operator|.
name|toUnsignedInt
argument_list|(
name|in
operator|.
name|readByte
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Read {@code count} integers and feed the result directly to {@link IntersectVisitor#visit(int)}. */
DECL|method|readInts
specifier|static
name|void
name|readInts
parameter_list|(
name|IndexInput
name|in
parameter_list|,
name|int
name|count
parameter_list|,
name|IntersectVisitor
name|visitor
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|bpv
init|=
name|in
operator|.
name|readByte
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|bpv
condition|)
block|{
case|case
literal|0
case|:
name|readDeltaVInts
argument_list|(
name|in
argument_list|,
name|count
argument_list|,
name|visitor
argument_list|)
expr_stmt|;
break|break;
case|case
literal|32
case|:
name|readInts32
argument_list|(
name|in
argument_list|,
name|count
argument_list|,
name|visitor
argument_list|)
expr_stmt|;
break|break;
case|case
literal|24
case|:
name|readInts24
argument_list|(
name|in
argument_list|,
name|count
argument_list|,
name|visitor
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unsupported number of bits per value: "
operator|+
name|bpv
argument_list|)
throw|;
block|}
block|}
DECL|method|readDeltaVInts
specifier|private
specifier|static
name|void
name|readDeltaVInts
parameter_list|(
name|IndexInput
name|in
parameter_list|,
name|int
name|count
parameter_list|,
name|IntersectVisitor
name|visitor
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|doc
init|=
literal|0
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
name|doc
operator|+=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|visitor
operator|.
name|visit
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|readInts32
specifier|static
name|void
name|readInts32
parameter_list|(
name|IndexInput
name|in
parameter_list|,
name|int
name|count
parameter_list|,
name|IntersectVisitor
name|visitor
parameter_list|)
throws|throws
name|IOException
block|{
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
name|visitor
operator|.
name|visit
argument_list|(
name|in
operator|.
name|readInt
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|readInts24
specifier|private
specifier|static
name|void
name|readInts24
parameter_list|(
name|IndexInput
name|in
parameter_list|,
name|int
name|count
parameter_list|,
name|IntersectVisitor
name|visitor
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|i
decl_stmt|;
for|for
control|(
name|i
operator|=
literal|0
init|;
name|i
operator|<
name|count
operator|-
literal|7
condition|;
name|i
operator|+=
literal|8
control|)
block|{
name|long
name|l1
init|=
name|in
operator|.
name|readLong
argument_list|()
decl_stmt|;
name|long
name|l2
init|=
name|in
operator|.
name|readLong
argument_list|()
decl_stmt|;
name|long
name|l3
init|=
name|in
operator|.
name|readLong
argument_list|()
decl_stmt|;
name|visitor
operator|.
name|visit
argument_list|(
call|(
name|int
call|)
argument_list|(
name|l1
operator|>>>
literal|40
argument_list|)
argument_list|)
expr_stmt|;
name|visitor
operator|.
name|visit
argument_list|(
call|(
name|int
call|)
argument_list|(
name|l1
operator|>>>
literal|16
argument_list|)
operator|&
literal|0xffffff
argument_list|)
expr_stmt|;
name|visitor
operator|.
name|visit
argument_list|(
call|(
name|int
call|)
argument_list|(
operator|(
operator|(
name|l1
operator|&
literal|0xffff
operator|)
operator|<<
literal|8
operator|)
operator||
operator|(
name|l2
operator|>>>
literal|56
operator|)
argument_list|)
argument_list|)
expr_stmt|;
name|visitor
operator|.
name|visit
argument_list|(
call|(
name|int
call|)
argument_list|(
name|l2
operator|>>>
literal|32
argument_list|)
operator|&
literal|0xffffff
argument_list|)
expr_stmt|;
name|visitor
operator|.
name|visit
argument_list|(
call|(
name|int
call|)
argument_list|(
name|l2
operator|>>>
literal|8
argument_list|)
operator|&
literal|0xffffff
argument_list|)
expr_stmt|;
name|visitor
operator|.
name|visit
argument_list|(
call|(
name|int
call|)
argument_list|(
operator|(
operator|(
name|l2
operator|&
literal|0xff
operator|)
operator|<<
literal|16
operator|)
operator||
operator|(
name|l3
operator|>>>
literal|48
operator|)
argument_list|)
argument_list|)
expr_stmt|;
name|visitor
operator|.
name|visit
argument_list|(
call|(
name|int
call|)
argument_list|(
name|l3
operator|>>>
literal|24
argument_list|)
operator|&
literal|0xffffff
argument_list|)
expr_stmt|;
name|visitor
operator|.
name|visit
argument_list|(
operator|(
name|int
operator|)
name|l3
operator|&
literal|0xffffff
argument_list|)
expr_stmt|;
block|}
for|for
control|(
init|;
name|i
operator|<
name|count
condition|;
operator|++
name|i
control|)
block|{
name|visitor
operator|.
name|visit
argument_list|(
operator|(
name|Short
operator|.
name|toUnsignedInt
argument_list|(
name|in
operator|.
name|readShort
argument_list|()
argument_list|)
operator|<<
literal|8
operator|)
operator||
name|Byte
operator|.
name|toUnsignedInt
argument_list|(
name|in
operator|.
name|readByte
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit
