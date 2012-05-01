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
name|io
operator|.
name|FileDescriptor
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_comment
comment|/**  * Provides JNI access to native methods such as madvise() for  * {@link NativeUnixDirectory}  */
end_comment

begin_class
DECL|class|NativePosixUtil
specifier|public
specifier|final
class|class
name|NativePosixUtil
block|{
DECL|field|NORMAL
specifier|public
specifier|final
specifier|static
name|int
name|NORMAL
init|=
literal|0
decl_stmt|;
DECL|field|SEQUENTIAL
specifier|public
specifier|final
specifier|static
name|int
name|SEQUENTIAL
init|=
literal|1
decl_stmt|;
DECL|field|RANDOM
specifier|public
specifier|final
specifier|static
name|int
name|RANDOM
init|=
literal|2
decl_stmt|;
DECL|field|WILLNEED
specifier|public
specifier|final
specifier|static
name|int
name|WILLNEED
init|=
literal|3
decl_stmt|;
DECL|field|DONTNEED
specifier|public
specifier|final
specifier|static
name|int
name|DONTNEED
init|=
literal|4
decl_stmt|;
DECL|field|NOREUSE
specifier|public
specifier|final
specifier|static
name|int
name|NOREUSE
init|=
literal|5
decl_stmt|;
static|static
block|{
name|System
operator|.
name|loadLibrary
argument_list|(
literal|"NativePosixUtil"
argument_list|)
expr_stmt|;
block|}
DECL|method|posix_fadvise
specifier|private
specifier|static
specifier|native
name|int
name|posix_fadvise
parameter_list|(
name|FileDescriptor
name|fd
parameter_list|,
name|long
name|offset
parameter_list|,
name|long
name|len
parameter_list|,
name|int
name|advise
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|posix_madvise
specifier|public
specifier|static
specifier|native
name|int
name|posix_madvise
parameter_list|(
name|ByteBuffer
name|buf
parameter_list|,
name|int
name|advise
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|madvise
specifier|public
specifier|static
specifier|native
name|int
name|madvise
parameter_list|(
name|ByteBuffer
name|buf
parameter_list|,
name|int
name|advise
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|open_direct
specifier|public
specifier|static
specifier|native
name|FileDescriptor
name|open_direct
parameter_list|(
name|String
name|filename
parameter_list|,
name|boolean
name|read
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|pread
specifier|public
specifier|static
specifier|native
name|long
name|pread
parameter_list|(
name|FileDescriptor
name|fd
parameter_list|,
name|long
name|pos
parameter_list|,
name|ByteBuffer
name|byteBuf
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|advise
specifier|public
specifier|static
name|void
name|advise
parameter_list|(
name|FileDescriptor
name|fd
parameter_list|,
name|long
name|offset
parameter_list|,
name|long
name|len
parameter_list|,
name|int
name|advise
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|code
init|=
name|posix_fadvise
argument_list|(
name|fd
argument_list|,
name|offset
argument_list|,
name|len
argument_list|,
name|advise
argument_list|)
decl_stmt|;
if|if
condition|(
name|code
operator|!=
literal|0
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"posix_fadvise failed code="
operator|+
name|code
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

