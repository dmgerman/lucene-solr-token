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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|CopyOption
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|FileSystem
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|attribute
operator|.
name|BasicFileAttributeView
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|attribute
operator|.
name|BasicFileAttributes
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
name|Map
import|;
end_import

begin_comment
comment|/**   * FileSystem that (imperfectly) acts like windows.   *<p>  * Currently this filesystem only prevents deletion of open files.  */
end_comment

begin_class
DECL|class|WindowsFS
specifier|public
class|class
name|WindowsFS
extends|extends
name|HandleTrackingFS
block|{
DECL|field|openFiles
specifier|private
specifier|final
name|Map
argument_list|<
name|Object
argument_list|,
name|Integer
argument_list|>
name|openFiles
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|// TODO: try to make this as realistic as possible... it depends e.g. how you
comment|// open files, if you map them, etc, if you can delete them (Uwe knows the rules)
comment|// TODO: add case-insensitivity
comment|/**    * Create a new instance, wrapping the delegate filesystem to    * act like Windows.    * @param delegate delegate filesystem to wrap.    */
DECL|method|WindowsFS
specifier|public
name|WindowsFS
parameter_list|(
name|FileSystem
name|delegate
parameter_list|)
block|{
name|super
argument_list|(
literal|"windows://"
argument_list|,
name|delegate
argument_list|)
expr_stmt|;
block|}
comment|/**     * Returns file "key" (e.g. inode) for the specified path     */
DECL|method|getKey
specifier|private
name|Object
name|getKey
parameter_list|(
name|Path
name|existing
parameter_list|)
throws|throws
name|IOException
block|{
name|BasicFileAttributeView
name|view
init|=
name|Files
operator|.
name|getFileAttributeView
argument_list|(
name|existing
argument_list|,
name|BasicFileAttributeView
operator|.
name|class
argument_list|)
decl_stmt|;
name|BasicFileAttributes
name|attributes
init|=
name|view
operator|.
name|readAttributes
argument_list|()
decl_stmt|;
return|return
name|attributes
operator|.
name|fileKey
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|onOpen
specifier|protected
name|void
name|onOpen
parameter_list|(
name|Path
name|path
parameter_list|,
name|Object
name|stream
parameter_list|)
throws|throws
name|IOException
block|{
name|Object
name|key
init|=
name|getKey
argument_list|(
name|path
argument_list|)
decl_stmt|;
synchronized|synchronized
init|(
name|openFiles
init|)
block|{
name|Integer
name|v
init|=
name|openFiles
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|!=
literal|null
condition|)
block|{
name|v
operator|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|v
operator|.
name|intValue
argument_list|()
operator|+
literal|1
argument_list|)
expr_stmt|;
name|openFiles
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|v
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|openFiles
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|onClose
specifier|protected
name|void
name|onClose
parameter_list|(
name|Path
name|path
parameter_list|,
name|Object
name|stream
parameter_list|)
throws|throws
name|IOException
block|{
name|Object
name|key
init|=
name|getKey
argument_list|(
name|path
argument_list|)
decl_stmt|;
synchronized|synchronized
init|(
name|openFiles
init|)
block|{
name|Integer
name|v
init|=
name|openFiles
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|v
operator|.
name|intValue
argument_list|()
operator|==
literal|1
condition|)
block|{
name|openFiles
operator|.
name|remove
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|v
operator|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|v
operator|.
name|intValue
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
name|openFiles
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|v
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/**     * Checks that its ok to delete {@code Path}. If the file    * is still open, it throws IOException("access denied").    */
DECL|method|checkDeleteAccess
specifier|private
name|void
name|checkDeleteAccess
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|Object
name|key
init|=
literal|null
decl_stmt|;
try|try
block|{
name|key
operator|=
name|getKey
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|ignore
parameter_list|)
block|{
comment|// we don't care if the file doesn't exist
block|}
if|if
condition|(
name|key
operator|!=
literal|null
condition|)
block|{
synchronized|synchronized
init|(
name|openFiles
init|)
block|{
if|if
condition|(
name|openFiles
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"access denied: "
operator|+
name|path
argument_list|)
throw|;
block|}
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|delete
specifier|public
name|void
name|delete
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|checkDeleteAccess
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|super
operator|.
name|delete
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|move
specifier|public
name|void
name|move
parameter_list|(
name|Path
name|source
parameter_list|,
name|Path
name|target
parameter_list|,
name|CopyOption
modifier|...
name|options
parameter_list|)
throws|throws
name|IOException
block|{
name|checkDeleteAccess
argument_list|(
name|source
argument_list|)
expr_stmt|;
name|super
operator|.
name|move
argument_list|(
name|source
argument_list|,
name|target
argument_list|,
name|options
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|deleteIfExists
specifier|public
name|boolean
name|deleteIfExists
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|checkDeleteAccess
argument_list|(
name|path
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|deleteIfExists
argument_list|(
name|path
argument_list|)
return|;
block|}
block|}
end_class

end_unit

