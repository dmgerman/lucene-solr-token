begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.lucene50
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene50
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|CorruptIndexException
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
name|index
operator|.
name|SegmentInfo
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
name|ChecksumIndexInput
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
name|IOContext
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
name|Lock
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
name|Collections
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

begin_comment
comment|/**  * Class for accessing a compound stream.  * This class implements a directory, but is limited to only read operations.  * Directory methods that would normally modify data throw an exception.  * @lucene.experimental  */
end_comment

begin_class
DECL|class|Lucene50CompoundReader
specifier|final
class|class
name|Lucene50CompoundReader
extends|extends
name|Directory
block|{
comment|/** Offset/Length for a slice inside of a compound file */
DECL|class|FileEntry
specifier|public
specifier|static
specifier|final
class|class
name|FileEntry
block|{
DECL|field|offset
name|long
name|offset
decl_stmt|;
DECL|field|length
name|long
name|length
decl_stmt|;
block|}
DECL|field|directory
specifier|private
specifier|final
name|Directory
name|directory
decl_stmt|;
DECL|field|segmentName
specifier|private
specifier|final
name|String
name|segmentName
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
decl_stmt|;
DECL|field|handle
specifier|private
specifier|final
name|IndexInput
name|handle
decl_stmt|;
DECL|field|version
specifier|private
name|int
name|version
decl_stmt|;
comment|/**    * Create a new CompoundFileDirectory.    */
comment|// TODO: we should just pre-strip "entries" and append segment name up-front like simpletext?
comment|// this need not be a "general purpose" directory anymore (it only writes index files)
DECL|method|Lucene50CompoundReader
specifier|public
name|Lucene50CompoundReader
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|SegmentInfo
name|si
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|directory
operator|=
name|directory
expr_stmt|;
name|this
operator|.
name|segmentName
operator|=
name|si
operator|.
name|name
expr_stmt|;
name|String
name|dataFileName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segmentName
argument_list|,
literal|""
argument_list|,
name|Lucene50CompoundFormat
operator|.
name|DATA_EXTENSION
argument_list|)
decl_stmt|;
name|String
name|entriesFileName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segmentName
argument_list|,
literal|""
argument_list|,
name|Lucene50CompoundFormat
operator|.
name|ENTRIES_EXTENSION
argument_list|)
decl_stmt|;
name|this
operator|.
name|entries
operator|=
name|readEntries
argument_list|(
name|si
operator|.
name|getId
argument_list|()
argument_list|,
name|directory
argument_list|,
name|entriesFileName
argument_list|)
expr_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
name|handle
operator|=
name|directory
operator|.
name|openInput
argument_list|(
name|dataFileName
argument_list|,
name|context
argument_list|)
expr_stmt|;
try|try
block|{
name|CodecUtil
operator|.
name|checkIndexHeader
argument_list|(
name|handle
argument_list|,
name|Lucene50CompoundFormat
operator|.
name|DATA_CODEC
argument_list|,
name|version
argument_list|,
name|version
argument_list|,
name|si
operator|.
name|getId
argument_list|()
argument_list|,
literal|""
argument_list|)
expr_stmt|;
comment|// NOTE: data file is too costly to verify checksum against all the bytes on open,
comment|// but for now we at least verify proper structure of the checksum footer: which looks
comment|// for FOOTER_MAGIC + algorithmID. This is cheap and can detect some forms of corruption
comment|// such as file truncation.
name|CodecUtil
operator|.
name|retrieveChecksum
argument_list|(
name|handle
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
name|handle
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** Helper method that reads CFS entries from an input stream */
DECL|method|readEntries
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|FileEntry
argument_list|>
name|readEntries
parameter_list|(
name|byte
index|[]
name|segmentID
parameter_list|,
name|Directory
name|dir
parameter_list|,
name|String
name|entriesFileName
parameter_list|)
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|FileEntry
argument_list|>
name|mapping
init|=
literal|null
decl_stmt|;
try|try
init|(
name|ChecksumIndexInput
name|entriesStream
init|=
name|dir
operator|.
name|openChecksumInput
argument_list|(
name|entriesFileName
argument_list|,
name|IOContext
operator|.
name|READONCE
argument_list|)
init|)
block|{
name|Throwable
name|priorE
init|=
literal|null
decl_stmt|;
try|try
block|{
name|version
operator|=
name|CodecUtil
operator|.
name|checkIndexHeader
argument_list|(
name|entriesStream
argument_list|,
name|Lucene50CompoundFormat
operator|.
name|ENTRY_CODEC
argument_list|,
name|Lucene50CompoundFormat
operator|.
name|VERSION_START
argument_list|,
name|Lucene50CompoundFormat
operator|.
name|VERSION_CURRENT
argument_list|,
name|segmentID
argument_list|,
literal|""
argument_list|)
expr_stmt|;
specifier|final
name|int
name|numEntries
init|=
name|entriesStream
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|mapping
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|numEntries
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
name|numEntries
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|FileEntry
name|fileEntry
init|=
operator|new
name|FileEntry
argument_list|()
decl_stmt|;
specifier|final
name|String
name|id
init|=
name|entriesStream
operator|.
name|readString
argument_list|()
decl_stmt|;
name|FileEntry
name|previous
init|=
name|mapping
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|fileEntry
argument_list|)
decl_stmt|;
if|if
condition|(
name|previous
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"Duplicate cfs entry id="
operator|+
name|id
operator|+
literal|" in CFS "
argument_list|,
name|entriesStream
argument_list|)
throw|;
block|}
name|fileEntry
operator|.
name|offset
operator|=
name|entriesStream
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|fileEntry
operator|.
name|length
operator|=
name|entriesStream
operator|.
name|readLong
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|exception
parameter_list|)
block|{
name|priorE
operator|=
name|exception
expr_stmt|;
block|}
finally|finally
block|{
name|CodecUtil
operator|.
name|checkFooter
argument_list|(
name|entriesStream
argument_list|,
name|priorE
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|mapping
argument_list|)
return|;
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
name|IOUtils
operator|.
name|close
argument_list|(
name|handle
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|openInput
specifier|public
name|IndexInput
name|openInput
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
specifier|final
name|FileEntry
name|entry
init|=
name|entries
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|entry
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
literal|"No sub-file with id "
operator|+
name|id
operator|+
literal|" found (fileName="
operator|+
name|name
operator|+
literal|" files: "
operator|+
name|entries
operator|.
name|keySet
argument_list|()
operator|+
literal|")"
argument_list|)
throw|;
block|}
return|return
name|handle
operator|.
name|slice
argument_list|(
name|name
argument_list|,
name|entry
operator|.
name|offset
argument_list|,
name|entry
operator|.
name|length
argument_list|)
return|;
block|}
comment|/** Returns an array of strings, one for each file in the directory. */
annotation|@
name|Override
DECL|method|listAll
specifier|public
name|String
index|[]
name|listAll
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|String
index|[]
name|res
init|=
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
name|entries
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
comment|// Add the segment name
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|res
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|res
index|[
name|i
index|]
operator|=
name|segmentName
operator|+
name|res
index|[
name|i
index|]
expr_stmt|;
block|}
return|return
name|res
return|;
block|}
comment|/** Not implemented    * @throws UnsupportedOperationException always: not supported by CFS */
annotation|@
name|Override
DECL|method|deleteFile
specifier|public
name|void
name|deleteFile
parameter_list|(
name|String
name|name
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|/** Not implemented    * @throws UnsupportedOperationException always: not supported by CFS */
DECL|method|renameFile
specifier|public
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
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|/** Returns the length of a file in the directory.    * @throws IOException if the file does not exist */
annotation|@
name|Override
DECL|method|fileLength
specifier|public
name|long
name|fileLength
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|FileEntry
name|e
init|=
name|entries
operator|.
name|get
argument_list|(
name|IndexFileNames
operator|.
name|stripSegmentName
argument_list|(
name|name
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|e
operator|==
literal|null
condition|)
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
name|name
argument_list|)
throw|;
return|return
name|e
operator|.
name|length
return|;
block|}
annotation|@
name|Override
DECL|method|createOutput
specifier|public
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
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
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
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|obtainLock
specifier|public
name|Lock
name|obtainLock
parameter_list|(
name|String
name|name
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"CompoundFileDirectory(segment=\""
operator|+
name|segmentName
operator|+
literal|"\" in dir="
operator|+
name|directory
operator|+
literal|")"
return|;
block|}
block|}
end_class

end_unit

