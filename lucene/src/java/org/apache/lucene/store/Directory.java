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
name|FileNotFoundException
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
name|io
operator|.
name|Closeable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_comment
comment|// for javadocs
end_comment

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
name|IOUtils
import|;
end_import

begin_comment
comment|/** A Directory is a flat list of files.  Files may be written once, when they  * are created.  Once a file is created it may only be opened for read, or  * deleted.  Random access is permitted both when reading and writing.  *  *<p> Java's i/o APIs not used directly, but rather all i/o is  * through this API.  This permits things such as:<ul>  *<li> implementation of RAM-based indices;  *<li> implementation indices stored in a database, via JDBC;  *<li> implementation of an index as a single file;  *</ul>  *  * Directory locking is implemented by an instance of {@link  * LockFactory}, and can be changed for each Directory  * instance using {@link #setLockFactory}.  *  */
end_comment

begin_class
DECL|class|Directory
specifier|public
specifier|abstract
class|class
name|Directory
implements|implements
name|Closeable
block|{
DECL|field|isOpen
specifier|volatile
specifier|protected
name|boolean
name|isOpen
init|=
literal|true
decl_stmt|;
comment|/** Holds the LockFactory instance (implements locking for    * this Directory instance). */
DECL|field|lockFactory
specifier|protected
name|LockFactory
name|lockFactory
decl_stmt|;
comment|/**    * Returns an array of strings, one for each file in the directory.    *     * @throws NoSuchDirectoryException if the directory is not prepared for any    *         write operations (such as {@link #createOutput(String)}).    * @throws IOException in case of other IO errors    */
DECL|method|listAll
specifier|public
specifier|abstract
name|String
index|[]
name|listAll
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Returns true iff a file with the given name exists. */
DECL|method|fileExists
specifier|public
specifier|abstract
name|boolean
name|fileExists
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Returns the time the named file was last modified. */
DECL|method|fileModified
specifier|public
specifier|abstract
name|long
name|fileModified
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Set the modified time of an existing file to now. */
DECL|method|touchFile
specifier|public
specifier|abstract
name|void
name|touchFile
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Removes an existing file in the directory. */
DECL|method|deleteFile
specifier|public
specifier|abstract
name|void
name|deleteFile
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns the length of a file in the directory. This method follows the    * following contract:    *<ul>    *<li>Throws {@link FileNotFoundException} if the file does not exist    *<li>Returns a value&ge;0 if the file exists, which specifies its length.    *</ul>    *     * @param name the name of the file for which to return the length.    * @throws FileNotFoundException if the file does not exist.    * @throws IOException if there was an IO error while retrieving the file's    *         length.    */
DECL|method|fileLength
specifier|public
specifier|abstract
name|long
name|fileLength
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Creates a new, empty file in the directory with the given name.       Returns a stream writing this file. */
DECL|method|createOutput
specifier|public
specifier|abstract
name|IndexOutput
name|createOutput
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Ensure that any writes to this file are moved to    * stable storage.  Lucene uses this to properly commit    * changes to the index, to prevent a machine/OS crash    * from corrupting the index.    * @deprecated use {@link #sync(Collection)} instead.    * For easy migration you can change your code to call    * sync(Collections.singleton(name))    */
annotation|@
name|Deprecated
DECL|method|sync
specifier|public
name|void
name|sync
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
comment|// TODO 4.0 kill me
block|}
comment|/**    * Ensure that any writes to these files are moved to    * stable storage.  Lucene uses this to properly commit    * changes to the index, to prevent a machine/OS crash    * from corrupting the index.<br/>    *<br/>    * NOTE: Clients may call this method for same files over    * and over again, so some impls might optimize for that.    * For other impls the operation can be a noop, for various    * reasons.    */
DECL|method|sync
specifier|public
name|void
name|sync
parameter_list|(
name|Collection
argument_list|<
name|String
argument_list|>
name|names
parameter_list|)
throws|throws
name|IOException
block|{
comment|// TODO 4.0 make me abstract
for|for
control|(
name|String
name|name
range|:
name|names
control|)
name|sync
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
comment|/** Returns a stream reading an existing file. */
DECL|method|openInput
specifier|public
specifier|abstract
name|IndexInput
name|openInput
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Returns a stream reading an existing file, with the    * specified read buffer size.  The particular Directory    * implementation may ignore the buffer size.  Currently    * the only Directory implementations that respect this    * parameter are {@link FSDirectory} and {@link    * org.apache.lucene.index.CompoundFileReader}.   */
DECL|method|openInput
specifier|public
name|IndexInput
name|openInput
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|bufferSize
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|openInput
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/** Construct a {@link Lock}.    * @param name the name of the lock file    */
DECL|method|makeLock
specifier|public
name|Lock
name|makeLock
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|lockFactory
operator|.
name|makeLock
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**    * Attempt to clear (forcefully unlock and remove) the    * specified lock.  Only call this at a time when you are    * certain this lock is no longer in use.    * @param name name of the lock to be cleared.    */
DECL|method|clearLock
specifier|public
name|void
name|clearLock
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|lockFactory
operator|!=
literal|null
condition|)
block|{
name|lockFactory
operator|.
name|clearLock
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Closes the store. */
DECL|method|close
specifier|public
specifier|abstract
name|void
name|close
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Set the LockFactory that this Directory instance should    * use for its locking implementation.  Each * instance of    * LockFactory should only be used for one directory (ie,    * do not share a single instance across multiple    * Directories).    *    * @param lockFactory instance of {@link LockFactory}.    */
DECL|method|setLockFactory
specifier|public
name|void
name|setLockFactory
parameter_list|(
name|LockFactory
name|lockFactory
parameter_list|)
block|{
assert|assert
name|lockFactory
operator|!=
literal|null
assert|;
name|this
operator|.
name|lockFactory
operator|=
name|lockFactory
expr_stmt|;
name|lockFactory
operator|.
name|setLockPrefix
argument_list|(
name|this
operator|.
name|getLockID
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get the LockFactory that this Directory instance is    * using for its locking implementation.  Note that this    * may be null for Directory implementations that provide    * their own locking implementation.    */
DECL|method|getLockFactory
specifier|public
name|LockFactory
name|getLockFactory
parameter_list|()
block|{
return|return
name|this
operator|.
name|lockFactory
return|;
block|}
comment|/**    * Return a string identifier that uniquely differentiates    * this Directory instance from other Directory instances.    * This ID should be the same if two Directory instances    * (even in different JVMs and/or on different machines)    * are considered "the same index".  This is how locking    * "scopes" to the right index.    */
DECL|method|getLockID
specifier|public
name|String
name|getLockID
parameter_list|()
block|{
return|return
name|this
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Copies the file<i>src</i> to {@link Directory}<i>to</i> under the new    * file name<i>dest</i>.    *<p>    * If you want to copy the entire source directory to the destination one, you    * can do so like this:    *     *<pre>    * Directory to; // the directory to copy to    * for (String file : dir.listAll()) {    *   dir.copy(to, file, newFile); // newFile can be either file, or a new name    * }    *</pre>    *<p>    *<b>NOTE:</b> this method does not check whether<i>dest<i> exist and will    * overwrite it if it does.    */
DECL|method|copy
specifier|public
name|void
name|copy
parameter_list|(
name|Directory
name|to
parameter_list|,
name|String
name|src
parameter_list|,
name|String
name|dest
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexOutput
name|os
init|=
name|to
operator|.
name|createOutput
argument_list|(
name|dest
argument_list|)
decl_stmt|;
name|IndexInput
name|is
init|=
name|openInput
argument_list|(
name|src
argument_list|)
decl_stmt|;
name|IOException
name|priorException
init|=
literal|null
decl_stmt|;
try|try
block|{
name|is
operator|.
name|copyBytes
argument_list|(
name|os
argument_list|,
name|is
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|priorException
operator|=
name|ioe
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeSafely
argument_list|(
name|priorException
argument_list|,
name|os
argument_list|,
name|is
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Copy contents of a directory src to a directory dest. If a file in src    * already exists in dest then the one in dest will be blindly overwritten.    *<p>    *<b>NOTE:</b> the source directory cannot change while this method is    * running. Otherwise the results are undefined and you could easily hit a    * FileNotFoundException.    *<p>    *<b>NOTE:</b> this method only copies files that look like index files (ie,    * have extensions matching the known extensions of index files).    *     * @param src source directory    * @param dest destination directory    * @param closeDirSrc if<code>true</code>, call {@link #close()} method on     *        source directory    * @deprecated should be replaced with calls to    *             {@link #copy(Directory, String, String)} for every file that    *             needs copying. You can use the following code:    *     *<pre>    * for (String file : src.listAll()) {    *   src.copy(dest, file, file);    * }    *</pre>    */
DECL|method|copy
specifier|public
specifier|static
name|void
name|copy
parameter_list|(
name|Directory
name|src
parameter_list|,
name|Directory
name|dest
parameter_list|,
name|boolean
name|closeDirSrc
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|String
name|file
range|:
name|src
operator|.
name|listAll
argument_list|()
control|)
block|{
name|src
operator|.
name|copy
argument_list|(
name|dest
argument_list|,
name|file
argument_list|,
name|file
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|closeDirSrc
condition|)
block|{
name|src
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * @throws AlreadyClosedException if this Directory is closed    */
DECL|method|ensureOpen
specifier|protected
specifier|final
name|void
name|ensureOpen
parameter_list|()
throws|throws
name|AlreadyClosedException
block|{
if|if
condition|(
operator|!
name|isOpen
condition|)
throw|throw
operator|new
name|AlreadyClosedException
argument_list|(
literal|"this Directory is closed"
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

