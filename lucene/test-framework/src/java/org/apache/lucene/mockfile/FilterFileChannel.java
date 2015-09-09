begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.mockfile
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|mockfile
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
name|IOError
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
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
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

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|MappedByteBuffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|FileChannel
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|FileLock
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|ReadableByteChannel
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|WritableByteChannel
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|AccessController
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PrivilegedAction
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_comment
comment|/**    * A {@code FilterFileChannel} contains another   * {@code FileChannel}, which it uses as its basic   * source of data, possibly transforming the data along the   * way or providing additional functionality.   */
end_comment

begin_class
DECL|class|FilterFileChannel
specifier|public
class|class
name|FilterFileChannel
extends|extends
name|FileChannel
block|{
comment|/**     * The underlying {@code FileChannel} instance.     */
DECL|field|delegate
specifier|protected
specifier|final
name|FileChannel
name|delegate
decl_stmt|;
comment|/**    * Construct a {@code FilterFileChannel} based on     * the specified base channel.    *<p>    * Note that base channel is closed if this channel is closed.    * @param delegate specified base channel.    */
DECL|method|FilterFileChannel
specifier|public
name|FilterFileChannel
parameter_list|(
name|FileChannel
name|delegate
parameter_list|)
block|{
name|this
operator|.
name|delegate
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|delegate
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|read
specifier|public
name|int
name|read
parameter_list|(
name|ByteBuffer
name|dst
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|read
argument_list|(
name|dst
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|read
specifier|public
name|long
name|read
parameter_list|(
name|ByteBuffer
index|[]
name|dsts
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|read
argument_list|(
name|dsts
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|int
name|write
parameter_list|(
name|ByteBuffer
name|src
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|write
argument_list|(
name|src
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|long
name|write
parameter_list|(
name|ByteBuffer
index|[]
name|srcs
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|write
argument_list|(
name|srcs
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|position
specifier|public
name|long
name|position
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|position
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|position
specifier|public
name|FileChannel
name|position
parameter_list|(
name|long
name|newPosition
parameter_list|)
throws|throws
name|IOException
block|{
name|delegate
operator|.
name|position
argument_list|(
name|newPosition
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|size
specifier|public
name|long
name|size
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|truncate
specifier|public
name|FileChannel
name|truncate
parameter_list|(
name|long
name|size
parameter_list|)
throws|throws
name|IOException
block|{
name|delegate
operator|.
name|truncate
argument_list|(
name|size
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|force
specifier|public
name|void
name|force
parameter_list|(
name|boolean
name|metaData
parameter_list|)
throws|throws
name|IOException
block|{
name|delegate
operator|.
name|force
argument_list|(
name|metaData
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|transferTo
specifier|public
name|long
name|transferTo
parameter_list|(
name|long
name|position
parameter_list|,
name|long
name|count
parameter_list|,
name|WritableByteChannel
name|target
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|transferTo
argument_list|(
name|position
argument_list|,
name|count
argument_list|,
name|target
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|transferFrom
specifier|public
name|long
name|transferFrom
parameter_list|(
name|ReadableByteChannel
name|src
parameter_list|,
name|long
name|position
parameter_list|,
name|long
name|count
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|transferFrom
argument_list|(
name|src
argument_list|,
name|position
argument_list|,
name|count
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|read
specifier|public
name|int
name|read
parameter_list|(
name|ByteBuffer
name|dst
parameter_list|,
name|long
name|position
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|read
argument_list|(
name|dst
argument_list|,
name|position
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|int
name|write
parameter_list|(
name|ByteBuffer
name|src
parameter_list|,
name|long
name|position
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|write
argument_list|(
name|src
argument_list|,
name|position
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|map
specifier|public
name|MappedByteBuffer
name|map
parameter_list|(
name|MapMode
name|mode
parameter_list|,
name|long
name|position
parameter_list|,
name|long
name|size
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|map
argument_list|(
name|mode
argument_list|,
name|position
argument_list|,
name|size
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|lock
specifier|public
name|FileLock
name|lock
parameter_list|(
name|long
name|position
parameter_list|,
name|long
name|size
parameter_list|,
name|boolean
name|shared
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|lock
argument_list|(
name|position
argument_list|,
name|size
argument_list|,
name|shared
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|tryLock
specifier|public
name|FileLock
name|tryLock
parameter_list|(
name|long
name|position
parameter_list|,
name|long
name|size
parameter_list|,
name|boolean
name|shared
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|tryLock
argument_list|(
name|position
argument_list|,
name|size
argument_list|,
name|shared
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|implCloseChannel
specifier|protected
name|void
name|implCloseChannel
parameter_list|()
throws|throws
name|IOException
block|{
comment|// our only way to call delegate.implCloseChannel()
name|AccessController
operator|.
name|doPrivileged
argument_list|(
operator|new
name|PrivilegedAction
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|run
parameter_list|()
block|{
for|for
control|(
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
init|=
name|delegate
operator|.
name|getClass
argument_list|()
init|;
name|clazz
operator|!=
literal|null
condition|;
name|clazz
operator|=
name|clazz
operator|.
name|getSuperclass
argument_list|()
control|)
block|{
specifier|final
name|Method
name|method
decl_stmt|;
try|try
block|{
name|method
operator|=
name|clazz
operator|.
name|getDeclaredMethod
argument_list|(
literal|"implCloseChannel"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|e
parameter_list|)
block|{
continue|continue;
block|}
try|try
block|{
name|method
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|method
operator|.
name|invoke
argument_list|(
name|delegate
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
catch|catch
parameter_list|(
name|ReflectiveOperationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOError
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

