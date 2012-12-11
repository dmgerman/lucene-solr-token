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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|util
operator|.
name|Collection
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
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Queue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
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
name|codecs
operator|.
name|CodecUtil
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
name|IndexFileNames
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
name|IOUtils
import|;
end_import

begin_comment
comment|/**  * Combines multiple files into a single compound file.  *   * @see CompoundFileDirectory  * @lucene.internal  */
end_comment

begin_class
DECL|class|CompoundFileWriter
specifier|final
class|class
name|CompoundFileWriter
implements|implements
name|Closeable
block|{
DECL|class|FileEntry
specifier|private
specifier|static
specifier|final
class|class
name|FileEntry
block|{
comment|/** source file */
DECL|field|file
name|String
name|file
decl_stmt|;
DECL|field|length
name|long
name|length
decl_stmt|;
comment|/** temporary holder for the start of this file's data section */
DECL|field|offset
name|long
name|offset
decl_stmt|;
comment|/** the directory which contains the file. */
DECL|field|dir
name|Directory
name|dir
decl_stmt|;
block|}
comment|// versioning for the .cfs file
DECL|field|DATA_CODEC
specifier|static
specifier|final
name|String
name|DATA_CODEC
init|=
literal|"CompoundFileWriterData"
decl_stmt|;
DECL|field|VERSION_START
specifier|static
specifier|final
name|int
name|VERSION_START
init|=
literal|0
decl_stmt|;
DECL|field|VERSION_CURRENT
specifier|static
specifier|final
name|int
name|VERSION_CURRENT
init|=
name|VERSION_START
decl_stmt|;
comment|// versioning for the .cfe file
DECL|field|ENTRY_CODEC
specifier|static
specifier|final
name|String
name|ENTRY_CODEC
init|=
literal|"CompoundFileWriterEntries"
decl_stmt|;
DECL|field|directory
specifier|private
specifier|final
name|Directory
name|directory
decl_stmt|;
DECL|field|entries
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|FileEntry
argument_list|>
name|entries
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|FileEntry
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|seenIDs
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|seenIDs
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
comment|// all entries that are written to a sep. file but not yet moved into CFS
DECL|field|pendingEntries
specifier|private
specifier|final
name|Queue
argument_list|<
name|FileEntry
argument_list|>
name|pendingEntries
init|=
operator|new
name|LinkedList
argument_list|<
name|FileEntry
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|closed
specifier|private
name|boolean
name|closed
init|=
literal|false
decl_stmt|;
DECL|field|dataOut
specifier|private
name|IndexOutput
name|dataOut
decl_stmt|;
DECL|field|outputTaken
specifier|private
specifier|final
name|AtomicBoolean
name|outputTaken
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
DECL|field|entryTableName
specifier|final
name|String
name|entryTableName
decl_stmt|;
DECL|field|dataFileName
specifier|final
name|String
name|dataFileName
decl_stmt|;
comment|/**    * Create the compound stream in the specified file. The file name is the    * entire name (no extensions are added).    *     * @throws NullPointerException    *           if<code>dir</code> or<code>name</code> is null    */
DECL|method|CompoundFileWriter
name|CompoundFileWriter
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|dir
operator|==
literal|null
condition|)
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"directory cannot be null"
argument_list|)
throw|;
if|if
condition|(
name|name
operator|==
literal|null
condition|)
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"name cannot be null"
argument_list|)
throw|;
name|directory
operator|=
name|dir
expr_stmt|;
name|entryTableName
operator|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|IndexFileNames
operator|.
name|stripExtension
argument_list|(
name|name
argument_list|)
argument_list|,
literal|""
argument_list|,
name|IndexFileNames
operator|.
name|COMPOUND_FILE_ENTRIES_EXTENSION
argument_list|)
expr_stmt|;
name|dataFileName
operator|=
name|name
expr_stmt|;
block|}
DECL|method|getOutput
specifier|private
specifier|synchronized
name|IndexOutput
name|getOutput
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|dataOut
operator|==
literal|null
condition|)
block|{
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|dataOut
operator|=
name|directory
operator|.
name|createOutput
argument_list|(
name|dataFileName
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
name|CodecUtil
operator|.
name|writeHeader
argument_list|(
name|dataOut
argument_list|,
name|DATA_CODEC
argument_list|,
name|VERSION_CURRENT
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|dataOut
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|dataOut
return|;
block|}
comment|/** Returns the directory of the compound file. */
DECL|method|getDirectory
name|Directory
name|getDirectory
parameter_list|()
block|{
return|return
name|directory
return|;
block|}
comment|/** Returns the name of the compound file. */
DECL|method|getName
name|String
name|getName
parameter_list|()
block|{
return|return
name|dataFileName
return|;
block|}
comment|/**    * Closes all resources and writes the entry table    *     * @throws IllegalStateException    *           if close() had been called before or if no file has been added to    *           this object    */
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|closed
condition|)
block|{
return|return;
block|}
name|IOException
name|priorException
init|=
literal|null
decl_stmt|;
name|IndexOutput
name|entryTableOut
init|=
literal|null
decl_stmt|;
comment|// TODO this code should clean up after itself
comment|// (remove partial .cfs/.cfe)
try|try
block|{
if|if
condition|(
operator|!
name|pendingEntries
operator|.
name|isEmpty
argument_list|()
operator|||
name|outputTaken
operator|.
name|get
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"CFS has pending open files"
argument_list|)
throw|;
block|}
name|closed
operator|=
literal|true
expr_stmt|;
comment|// open the compound stream
name|getOutput
argument_list|()
expr_stmt|;
assert|assert
name|dataOut
operator|!=
literal|null
assert|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|priorException
operator|=
name|e
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|priorException
argument_list|,
name|dataOut
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|entryTableOut
operator|=
name|directory
operator|.
name|createOutput
argument_list|(
name|entryTableName
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
name|writeEntryTable
argument_list|(
name|entries
operator|.
name|values
argument_list|()
argument_list|,
name|entryTableOut
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|priorException
operator|=
name|e
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|priorException
argument_list|,
name|entryTableOut
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|ensureOpen
specifier|private
specifier|final
name|void
name|ensureOpen
parameter_list|()
block|{
if|if
condition|(
name|closed
condition|)
block|{
throw|throw
operator|new
name|AlreadyClosedException
argument_list|(
literal|"CFS Directory is already closed"
argument_list|)
throw|;
block|}
block|}
comment|/**    * Copy the contents of the file with specified extension into the provided    * output stream.    */
DECL|method|copyFileEntry
specifier|private
specifier|final
name|long
name|copyFileEntry
parameter_list|(
name|IndexOutput
name|dataOut
parameter_list|,
name|FileEntry
name|fileEntry
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|IndexInput
name|is
init|=
name|fileEntry
operator|.
name|dir
operator|.
name|openInput
argument_list|(
name|fileEntry
operator|.
name|file
argument_list|,
name|IOContext
operator|.
name|READONCE
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
specifier|final
name|long
name|startPtr
init|=
name|dataOut
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
specifier|final
name|long
name|length
init|=
name|fileEntry
operator|.
name|length
decl_stmt|;
name|dataOut
operator|.
name|copyBytes
argument_list|(
name|is
argument_list|,
name|length
argument_list|)
expr_stmt|;
comment|// Verify that the output length diff is equal to original file
name|long
name|endPtr
init|=
name|dataOut
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
name|long
name|diff
init|=
name|endPtr
operator|-
name|startPtr
decl_stmt|;
if|if
condition|(
name|diff
operator|!=
name|length
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Difference in the output file offsets "
operator|+
name|diff
operator|+
literal|" does not match the original file length "
operator|+
name|length
argument_list|)
throw|;
name|fileEntry
operator|.
name|offset
operator|=
name|startPtr
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
return|return
name|length
return|;
block|}
finally|finally
block|{
if|if
condition|(
name|success
condition|)
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|is
argument_list|)
expr_stmt|;
comment|// copy successful - delete file
name|fileEntry
operator|.
name|dir
operator|.
name|deleteFile
argument_list|(
name|fileEntry
operator|.
name|file
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|writeEntryTable
specifier|protected
name|void
name|writeEntryTable
parameter_list|(
name|Collection
argument_list|<
name|FileEntry
argument_list|>
name|entries
parameter_list|,
name|IndexOutput
name|entryOut
parameter_list|)
throws|throws
name|IOException
block|{
name|CodecUtil
operator|.
name|writeHeader
argument_list|(
name|entryOut
argument_list|,
name|ENTRY_CODEC
argument_list|,
name|VERSION_CURRENT
argument_list|)
expr_stmt|;
name|entryOut
operator|.
name|writeVInt
argument_list|(
name|entries
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|FileEntry
name|fe
range|:
name|entries
control|)
block|{
name|entryOut
operator|.
name|writeString
argument_list|(
name|IndexFileNames
operator|.
name|stripSegmentName
argument_list|(
name|fe
operator|.
name|file
argument_list|)
argument_list|)
expr_stmt|;
name|entryOut
operator|.
name|writeLong
argument_list|(
name|fe
operator|.
name|offset
argument_list|)
expr_stmt|;
name|entryOut
operator|.
name|writeLong
argument_list|(
name|fe
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|createOutput
name|IndexOutput
name|createOutput
parameter_list|(
name|String
name|name
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
name|boolean
name|outputLocked
init|=
literal|false
decl_stmt|;
try|try
block|{
assert|assert
name|name
operator|!=
literal|null
operator|:
literal|"name must not be null"
assert|;
if|if
condition|(
name|entries
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"File "
operator|+
name|name
operator|+
literal|" already exists"
argument_list|)
throw|;
block|}
specifier|final
name|FileEntry
name|entry
init|=
operator|new
name|FileEntry
argument_list|()
decl_stmt|;
name|entry
operator|.
name|file
operator|=
name|name
expr_stmt|;
name|entries
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|entry
argument_list|)
expr_stmt|;
specifier|final
name|String
name|id
init|=
name|IndexFileNames
operator|.
name|stripSegmentName
argument_list|(
name|name
argument_list|)
decl_stmt|;
assert|assert
operator|!
name|seenIDs
operator|.
name|contains
argument_list|(
name|id
argument_list|)
operator|:
literal|"file=\""
operator|+
name|name
operator|+
literal|"\" maps to id=\""
operator|+
name|id
operator|+
literal|"\", which was already written"
assert|;
name|seenIDs
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
specifier|final
name|DirectCFSIndexOutput
name|out
decl_stmt|;
if|if
condition|(
operator|(
name|outputLocked
operator|=
name|outputTaken
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
operator|)
condition|)
block|{
name|out
operator|=
operator|new
name|DirectCFSIndexOutput
argument_list|(
name|getOutput
argument_list|()
argument_list|,
name|entry
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|entry
operator|.
name|dir
operator|=
name|this
operator|.
name|directory
expr_stmt|;
if|if
condition|(
name|directory
operator|.
name|fileExists
argument_list|(
name|name
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"File "
operator|+
name|name
operator|+
literal|" already exists"
argument_list|)
throw|;
block|}
name|out
operator|=
operator|new
name|DirectCFSIndexOutput
argument_list|(
name|directory
operator|.
name|createOutput
argument_list|(
name|name
argument_list|,
name|context
argument_list|)
argument_list|,
name|entry
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|success
operator|=
literal|true
expr_stmt|;
return|return
name|out
return|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|entries
operator|.
name|remove
argument_list|(
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|outputLocked
condition|)
block|{
comment|// release the output lock if not successful
assert|assert
name|outputTaken
operator|.
name|get
argument_list|()
assert|;
name|releaseOutputLock
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|releaseOutputLock
specifier|final
name|void
name|releaseOutputLock
parameter_list|()
block|{
name|outputTaken
operator|.
name|compareAndSet
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|prunePendingEntries
specifier|private
specifier|final
name|void
name|prunePendingEntries
parameter_list|()
throws|throws
name|IOException
block|{
comment|// claim the output and copy all pending files in
if|if
condition|(
name|outputTaken
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
try|try
block|{
while|while
condition|(
operator|!
name|pendingEntries
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|FileEntry
name|entry
init|=
name|pendingEntries
operator|.
name|poll
argument_list|()
decl_stmt|;
name|copyFileEntry
argument_list|(
name|getOutput
argument_list|()
argument_list|,
name|entry
argument_list|)
expr_stmt|;
name|entries
operator|.
name|put
argument_list|(
name|entry
operator|.
name|file
argument_list|,
name|entry
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
specifier|final
name|boolean
name|compareAndSet
init|=
name|outputTaken
operator|.
name|compareAndSet
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
assert|assert
name|compareAndSet
assert|;
block|}
block|}
block|}
DECL|method|fileLength
name|long
name|fileLength
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|FileEntry
name|fileEntry
init|=
name|entries
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|fileEntry
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
name|name
operator|+
literal|" does not exist"
argument_list|)
throw|;
block|}
return|return
name|fileEntry
operator|.
name|length
return|;
block|}
DECL|method|fileExists
name|boolean
name|fileExists
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|entries
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
return|;
block|}
DECL|method|listAll
name|String
index|[]
name|listAll
parameter_list|()
block|{
return|return
name|entries
operator|.
name|keySet
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
return|;
block|}
DECL|class|DirectCFSIndexOutput
specifier|private
specifier|final
class|class
name|DirectCFSIndexOutput
extends|extends
name|IndexOutput
block|{
DECL|field|delegate
specifier|private
specifier|final
name|IndexOutput
name|delegate
decl_stmt|;
DECL|field|offset
specifier|private
specifier|final
name|long
name|offset
decl_stmt|;
DECL|field|closed
specifier|private
name|boolean
name|closed
decl_stmt|;
DECL|field|entry
specifier|private
name|FileEntry
name|entry
decl_stmt|;
DECL|field|writtenBytes
specifier|private
name|long
name|writtenBytes
decl_stmt|;
DECL|field|isSeparate
specifier|private
specifier|final
name|boolean
name|isSeparate
decl_stmt|;
DECL|method|DirectCFSIndexOutput
name|DirectCFSIndexOutput
parameter_list|(
name|IndexOutput
name|delegate
parameter_list|,
name|FileEntry
name|entry
parameter_list|,
name|boolean
name|isSeparate
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
name|this
operator|.
name|entry
operator|=
name|entry
expr_stmt|;
name|entry
operator|.
name|offset
operator|=
name|offset
operator|=
name|delegate
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
name|this
operator|.
name|isSeparate
operator|=
name|isSeparate
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|flush
specifier|public
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
name|delegate
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|closed
condition|)
block|{
name|closed
operator|=
literal|true
expr_stmt|;
name|entry
operator|.
name|length
operator|=
name|writtenBytes
expr_stmt|;
if|if
condition|(
name|isSeparate
condition|)
block|{
name|delegate
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// we are a separate file - push into the pending entries
name|pendingEntries
operator|.
name|add
argument_list|(
name|entry
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// we have been written into the CFS directly - release the lock
name|releaseOutputLock
argument_list|()
expr_stmt|;
block|}
comment|// now prune all pending entries and push them into the CFS
name|prunePendingEntries
argument_list|()
expr_stmt|;
block|}
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
name|delegate
operator|.
name|getFilePointer
argument_list|()
operator|-
name|offset
return|;
block|}
annotation|@
name|Override
DECL|method|length
specifier|public
name|long
name|length
parameter_list|()
throws|throws
name|IOException
block|{
assert|assert
operator|!
name|closed
assert|;
return|return
name|delegate
operator|.
name|length
argument_list|()
operator|-
name|offset
return|;
block|}
annotation|@
name|Override
DECL|method|writeByte
specifier|public
name|void
name|writeByte
parameter_list|(
name|byte
name|b
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
operator|!
name|closed
assert|;
name|writtenBytes
operator|++
expr_stmt|;
name|delegate
operator|.
name|writeByte
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeBytes
specifier|public
name|void
name|writeBytes
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
assert|assert
operator|!
name|closed
assert|;
name|writtenBytes
operator|+=
name|length
expr_stmt|;
name|delegate
operator|.
name|writeBytes
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

