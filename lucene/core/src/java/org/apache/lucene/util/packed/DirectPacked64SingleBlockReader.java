begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util.packed
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|packed
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
name|store
operator|.
name|IndexInput
import|;
end_import

begin_class
DECL|class|DirectPacked64SingleBlockReader
specifier|final
class|class
name|DirectPacked64SingleBlockReader
extends|extends
name|PackedInts
operator|.
name|ReaderImpl
block|{
DECL|field|in
specifier|private
specifier|final
name|IndexInput
name|in
decl_stmt|;
DECL|field|startPointer
specifier|private
specifier|final
name|long
name|startPointer
decl_stmt|;
DECL|field|valuesPerBlock
specifier|private
specifier|final
name|int
name|valuesPerBlock
decl_stmt|;
DECL|field|mask
specifier|private
specifier|final
name|long
name|mask
decl_stmt|;
DECL|method|DirectPacked64SingleBlockReader
name|DirectPacked64SingleBlockReader
parameter_list|(
name|int
name|bitsPerValue
parameter_list|,
name|int
name|valueCount
parameter_list|,
name|IndexInput
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|valueCount
argument_list|,
name|bitsPerValue
argument_list|)
expr_stmt|;
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
name|startPointer
operator|=
name|in
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
name|valuesPerBlock
operator|=
literal|64
operator|/
name|bitsPerValue
expr_stmt|;
name|mask
operator|=
operator|~
operator|(
operator|~
literal|0L
operator|<<
name|bitsPerValue
operator|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|long
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
specifier|final
name|int
name|blockOffset
init|=
name|index
operator|/
name|valuesPerBlock
decl_stmt|;
specifier|final
name|long
name|skip
init|=
operator|(
operator|(
name|long
operator|)
name|blockOffset
operator|)
operator|<<
literal|3
decl_stmt|;
try|try
block|{
name|in
operator|.
name|seek
argument_list|(
name|startPointer
operator|+
name|skip
argument_list|)
expr_stmt|;
name|long
name|block
init|=
name|in
operator|.
name|readLong
argument_list|()
decl_stmt|;
specifier|final
name|int
name|offsetInBlock
init|=
name|index
operator|%
name|valuesPerBlock
decl_stmt|;
return|return
operator|(
name|block
operator|>>>
operator|(
name|offsetInBlock
operator|*
name|bitsPerValue
operator|)
operator|)
operator|&
name|mask
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"failed"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

