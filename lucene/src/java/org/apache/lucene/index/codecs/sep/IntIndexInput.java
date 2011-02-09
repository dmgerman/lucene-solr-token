begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index.codecs.sep
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|codecs
operator|.
name|sep
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
name|Closeable
import|;
end_import

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
name|DataInput
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

begin_comment
comment|/** Defines basic API for writing ints to an IndexOutput.  *  IntBlockCodec interacts with this API. @see  *  IntBlockReader  *  * @lucene.experimental */
end_comment

begin_class
DECL|class|IntIndexInput
specifier|public
specifier|abstract
class|class
name|IntIndexInput
implements|implements
name|Closeable
block|{
DECL|method|reader
specifier|public
specifier|abstract
name|Reader
name|reader
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|close
specifier|public
specifier|abstract
name|void
name|close
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|index
specifier|public
specifier|abstract
name|Index
name|index
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|// TODO: -- can we simplify this?
DECL|class|Index
specifier|public
specifier|abstract
specifier|static
class|class
name|Index
block|{
DECL|method|read
specifier|public
specifier|abstract
name|void
name|read
parameter_list|(
name|DataInput
name|indexIn
parameter_list|,
name|boolean
name|absolute
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|read
specifier|public
specifier|abstract
name|void
name|read
parameter_list|(
name|IntIndexInput
operator|.
name|Reader
name|indexIn
parameter_list|,
name|boolean
name|absolute
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Seeks primary stream to the last read offset */
DECL|method|seek
specifier|public
specifier|abstract
name|void
name|seek
parameter_list|(
name|IntIndexInput
operator|.
name|Reader
name|stream
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|set
specifier|public
specifier|abstract
name|void
name|set
parameter_list|(
name|Index
name|other
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|clone
specifier|public
specifier|abstract
name|Object
name|clone
parameter_list|()
function_decl|;
block|}
DECL|class|Reader
specifier|public
specifier|abstract
specifier|static
class|class
name|Reader
block|{
comment|/** Reads next single int */
DECL|method|next
specifier|public
specifier|abstract
name|int
name|next
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Encodes as 1 or 2 ints, and can only use 61 of the 64      *  long bits. */
DECL|method|readVLong
specifier|public
name|long
name|readVLong
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|int
name|v
init|=
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|v
operator|&
literal|1
operator|)
operator|==
literal|0
condition|)
block|{
return|return
name|v
operator|>>
literal|1
return|;
block|}
else|else
block|{
specifier|final
name|long
name|v2
init|=
name|next
argument_list|()
decl_stmt|;
return|return
operator|(
name|v2
operator|<<
literal|30
operator|)
operator||
operator|(
name|v
operator|>>
literal|1
operator|)
return|;
block|}
block|}
comment|/** Reads next chunk of ints */
DECL|field|bulkResult
specifier|private
name|IntsRef
name|bulkResult
decl_stmt|;
comment|/** Read up to count ints. */
DECL|method|read
specifier|public
name|IntsRef
name|read
parameter_list|(
name|int
name|count
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|bulkResult
operator|==
literal|null
condition|)
block|{
name|bulkResult
operator|=
operator|new
name|IntsRef
argument_list|()
expr_stmt|;
name|bulkResult
operator|.
name|ints
operator|=
operator|new
name|int
index|[
name|count
index|]
expr_stmt|;
block|}
else|else
block|{
name|bulkResult
operator|.
name|grow
argument_list|(
name|count
argument_list|)
expr_stmt|;
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|bulkResult
operator|.
name|ints
index|[
name|i
index|]
operator|=
name|next
argument_list|()
expr_stmt|;
block|}
name|bulkResult
operator|.
name|length
operator|=
name|count
expr_stmt|;
return|return
name|bulkResult
return|;
block|}
block|}
block|}
end_class

end_unit

