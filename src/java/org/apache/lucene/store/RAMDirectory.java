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
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Vector
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Hashtable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Enumeration
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
name|InputStream
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
name|OutputStream
import|;
end_import

begin_comment
comment|/**  * A memory-resident {@link Directory} implementation.  *  * @version $Id$  */
end_comment

begin_class
DECL|class|RAMDirectory
specifier|public
specifier|final
class|class
name|RAMDirectory
extends|extends
name|Directory
block|{
DECL|field|files
name|Hashtable
name|files
init|=
operator|new
name|Hashtable
argument_list|()
decl_stmt|;
comment|/** Constructs an empty {@link Directory}. */
DECL|method|RAMDirectory
specifier|public
name|RAMDirectory
parameter_list|()
block|{   }
comment|/**    * Creates a new<code>RAMDirectory</code> instance from a different    *<code>Directory</code> implementation.  This can be used to load    * a disk-based index into memory.    *<P>    * This should be used only with indices that can fit into memory.    *    * @param dir a<code>Directory</code> value    * @exception IOException if an error occurs    */
DECL|method|RAMDirectory
specifier|public
name|RAMDirectory
parameter_list|(
name|Directory
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
index|[]
name|files
init|=
name|dir
operator|.
name|list
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
name|files
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
comment|// make place on ram disk
name|OutputStream
name|os
init|=
name|createFile
argument_list|(
name|files
index|[
name|i
index|]
argument_list|)
decl_stmt|;
comment|// read current file
name|InputStream
name|is
init|=
name|dir
operator|.
name|openFile
argument_list|(
name|files
index|[
name|i
index|]
argument_list|)
decl_stmt|;
comment|// and copy to ram disk
name|int
name|len
init|=
operator|(
name|int
operator|)
name|is
operator|.
name|length
argument_list|()
decl_stmt|;
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
name|len
index|]
decl_stmt|;
name|is
operator|.
name|readBytes
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|os
operator|.
name|writeBytes
argument_list|(
name|buf
argument_list|,
name|len
argument_list|)
expr_stmt|;
comment|// graceful cleanup
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Creates a new<code>RAMDirectory</code> instance from the {@link FSDirectory}.    *    * @param dir a<code>File</code> specifying the index directory    */
DECL|method|RAMDirectory
specifier|public
name|RAMDirectory
parameter_list|(
name|File
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|FSDirectory
operator|.
name|getDirectory
argument_list|(
name|dir
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new<code>RAMDirectory</code> instance from the {@link FSDirectory}.    *    * @param dir a<code>String</code> specifying the full index directory path    */
DECL|method|RAMDirectory
specifier|public
name|RAMDirectory
parameter_list|(
name|String
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|FSDirectory
operator|.
name|getDirectory
argument_list|(
name|dir
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Returns an array of strings, one for each file in the directory. */
DECL|method|list
specifier|public
specifier|final
name|String
index|[]
name|list
parameter_list|()
block|{
name|String
index|[]
name|result
init|=
operator|new
name|String
index|[
name|files
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
name|Enumeration
name|names
init|=
name|files
operator|.
name|keys
argument_list|()
decl_stmt|;
while|while
condition|(
name|names
operator|.
name|hasMoreElements
argument_list|()
condition|)
name|result
index|[
name|i
operator|++
index|]
operator|=
operator|(
name|String
operator|)
name|names
operator|.
name|nextElement
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
comment|/** Returns true iff the named file exists in this directory. */
DECL|method|fileExists
specifier|public
specifier|final
name|boolean
name|fileExists
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|RAMFile
name|file
init|=
operator|(
name|RAMFile
operator|)
name|files
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
return|return
name|file
operator|!=
literal|null
return|;
block|}
comment|/** Returns the time the named file was last modified. */
DECL|method|fileModified
specifier|public
specifier|final
name|long
name|fileModified
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|RAMFile
name|file
init|=
operator|(
name|RAMFile
operator|)
name|files
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
return|return
name|file
operator|.
name|lastModified
return|;
block|}
comment|/** Set the modified time of an existing file to now. */
DECL|method|touchFile
specifier|public
name|void
name|touchFile
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
comment|//     final boolean MONITOR = false;
name|int
name|count
init|=
literal|0
decl_stmt|;
name|RAMFile
name|file
init|=
operator|(
name|RAMFile
operator|)
name|files
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|long
name|ts2
decl_stmt|,
name|ts1
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
do|do
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{}
name|ts2
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
comment|//       if (MONITOR) {
comment|//         count++;
comment|//       }
block|}
do|while
condition|(
name|ts1
operator|==
name|ts2
condition|)
do|;
name|file
operator|.
name|lastModified
operator|=
name|ts2
expr_stmt|;
comment|//     if (MONITOR)
comment|//         System.out.println("SLEEP COUNT: " + count);
block|}
comment|/** Returns the length in bytes of a file in the directory. */
DECL|method|fileLength
specifier|public
specifier|final
name|long
name|fileLength
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|RAMFile
name|file
init|=
operator|(
name|RAMFile
operator|)
name|files
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
return|return
name|file
operator|.
name|length
return|;
block|}
comment|/** Removes an existing file in the directory. */
DECL|method|deleteFile
specifier|public
specifier|final
name|void
name|deleteFile
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|files
operator|.
name|remove
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
comment|/** Removes an existing file in the directory. */
DECL|method|renameFile
specifier|public
specifier|final
name|void
name|renameFile
parameter_list|(
name|String
name|from
parameter_list|,
name|String
name|to
parameter_list|)
block|{
name|RAMFile
name|file
init|=
operator|(
name|RAMFile
operator|)
name|files
operator|.
name|get
argument_list|(
name|from
argument_list|)
decl_stmt|;
name|files
operator|.
name|remove
argument_list|(
name|from
argument_list|)
expr_stmt|;
name|files
operator|.
name|put
argument_list|(
name|to
argument_list|,
name|file
argument_list|)
expr_stmt|;
block|}
comment|/** Creates a new, empty file in the directory with the given name.       Returns a stream writing this file. */
DECL|method|createFile
specifier|public
specifier|final
name|OutputStream
name|createFile
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|RAMFile
name|file
init|=
operator|new
name|RAMFile
argument_list|()
decl_stmt|;
name|files
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|file
argument_list|)
expr_stmt|;
return|return
operator|new
name|RAMOutputStream
argument_list|(
name|file
argument_list|)
return|;
block|}
comment|/** Returns a stream reading an existing file. */
DECL|method|openFile
specifier|public
specifier|final
name|InputStream
name|openFile
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|RAMFile
name|file
init|=
operator|(
name|RAMFile
operator|)
name|files
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
return|return
operator|new
name|RAMInputStream
argument_list|(
name|file
argument_list|)
return|;
block|}
comment|/** Construct a {@link Lock}.    * @param name the name of the lock file    */
DECL|method|makeLock
specifier|public
specifier|final
name|Lock
name|makeLock
parameter_list|(
specifier|final
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|Lock
argument_list|()
block|{
specifier|public
name|boolean
name|obtain
parameter_list|()
throws|throws
name|IOException
block|{
synchronized|synchronized
init|(
name|files
init|)
block|{
if|if
condition|(
operator|!
name|fileExists
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|createFile
argument_list|(
name|name
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
specifier|public
name|void
name|release
parameter_list|()
block|{
name|deleteFile
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|isLocked
parameter_list|()
block|{
return|return
name|fileExists
argument_list|(
name|name
argument_list|)
return|;
block|}
block|}
return|;
block|}
comment|/** Closes the store to future operations. */
DECL|method|close
specifier|public
specifier|final
name|void
name|close
parameter_list|()
block|{   }
block|}
end_class

end_unit

