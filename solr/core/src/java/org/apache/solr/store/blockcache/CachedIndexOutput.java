begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.store.blockcache
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|store
operator|.
name|blockcache
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
name|store
operator|.
name|IndexOutput
import|;
end_import

begin_comment
comment|/**  * Cache the blocks as they are written. The cache file name is the name of  * the file until the file is closed, at which point the cache is updated  * to include the last modified date (which is unknown until that point).  * @lucene.experimental  */
end_comment

begin_class
DECL|class|CachedIndexOutput
specifier|public
class|class
name|CachedIndexOutput
extends|extends
name|ReusedBufferedIndexOutput
block|{
DECL|field|directory
specifier|private
specifier|final
name|BlockDirectory
name|directory
decl_stmt|;
DECL|field|dest
specifier|private
specifier|final
name|IndexOutput
name|dest
decl_stmt|;
DECL|field|blockSize
specifier|private
specifier|final
name|int
name|blockSize
decl_stmt|;
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|location
specifier|private
specifier|final
name|String
name|location
decl_stmt|;
DECL|field|cache
specifier|private
specifier|final
name|Cache
name|cache
decl_stmt|;
DECL|method|CachedIndexOutput
specifier|public
name|CachedIndexOutput
parameter_list|(
name|BlockDirectory
name|directory
parameter_list|,
name|IndexOutput
name|dest
parameter_list|,
name|int
name|blockSize
parameter_list|,
name|String
name|name
parameter_list|,
name|Cache
name|cache
parameter_list|,
name|int
name|bufferSize
parameter_list|)
block|{
name|super
argument_list|(
literal|"dest="
operator|+
name|dest
operator|+
literal|" name="
operator|+
name|name
argument_list|,
name|name
argument_list|,
name|bufferSize
argument_list|)
expr_stmt|;
name|this
operator|.
name|directory
operator|=
name|directory
expr_stmt|;
name|this
operator|.
name|dest
operator|=
name|dest
expr_stmt|;
name|this
operator|.
name|blockSize
operator|=
name|blockSize
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|location
operator|=
name|directory
operator|.
name|getFileCacheLocation
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|cache
operator|=
name|cache
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|closeInternal
specifier|public
name|void
name|closeInternal
parameter_list|()
throws|throws
name|IOException
block|{
name|dest
operator|.
name|close
argument_list|()
expr_stmt|;
name|cache
operator|.
name|renameCacheFile
argument_list|(
name|location
argument_list|,
name|directory
operator|.
name|getFileCacheName
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|writeBlock
specifier|private
name|int
name|writeBlock
parameter_list|(
name|long
name|position
parameter_list|,
name|byte
index|[]
name|b
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
comment|// read whole block into cache and then provide needed data
name|long
name|blockId
init|=
name|BlockDirectory
operator|.
name|getBlock
argument_list|(
name|position
argument_list|)
decl_stmt|;
name|int
name|blockOffset
init|=
operator|(
name|int
operator|)
name|BlockDirectory
operator|.
name|getPosition
argument_list|(
name|position
argument_list|)
decl_stmt|;
name|int
name|lengthToWriteInBlock
init|=
name|Math
operator|.
name|min
argument_list|(
name|length
argument_list|,
name|blockSize
operator|-
name|blockOffset
argument_list|)
decl_stmt|;
comment|// write the file and copy into the cache
name|dest
operator|.
name|writeBytes
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
name|lengthToWriteInBlock
argument_list|)
expr_stmt|;
name|cache
operator|.
name|update
argument_list|(
name|location
argument_list|,
name|blockId
argument_list|,
name|blockOffset
argument_list|,
name|b
argument_list|,
name|offset
argument_list|,
name|lengthToWriteInBlock
argument_list|)
expr_stmt|;
return|return
name|lengthToWriteInBlock
return|;
block|}
annotation|@
name|Override
DECL|method|writeInternal
specifier|public
name|void
name|writeInternal
parameter_list|(
name|byte
index|[]
name|b
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
name|long
name|position
init|=
name|getBufferStart
argument_list|()
decl_stmt|;
while|while
condition|(
name|length
operator|>
literal|0
condition|)
block|{
name|int
name|len
init|=
name|writeBlock
argument_list|(
name|position
argument_list|,
name|b
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
decl_stmt|;
name|position
operator|+=
name|len
expr_stmt|;
name|length
operator|-=
name|len
expr_stmt|;
name|offset
operator|+=
name|len
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getChecksum
specifier|public
name|long
name|getChecksum
parameter_list|()
throws|throws
name|IOException
block|{
name|flushBufferToCache
argument_list|()
expr_stmt|;
return|return
name|dest
operator|.
name|getChecksum
argument_list|()
return|;
block|}
block|}
end_class

end_unit

