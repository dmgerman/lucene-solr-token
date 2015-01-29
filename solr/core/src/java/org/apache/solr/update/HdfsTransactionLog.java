begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.update
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
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
name|List
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|FSDataInputStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|FSDataOutputStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|FileSystem
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrInputDocument
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|DataInputInputStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|FastInputStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|FastOutputStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|JavaBinCodec
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|ObjectReleaseTracker
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|FSHDFSUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  *  Log Format: List{Operation, Version, ...}  *  ADD, VERSION, DOC  *  DELETE, VERSION, ID_BYTES  *  DELETE_BY_QUERY, VERSION, String  *  *  TODO: keep two files, one for [operation, version, id] and the other for the actual  *  document data.  That way we could throw away document log files more readily  *  while retaining the smaller operation log files longer (and we can retrieve  *  the stored fields from the latest documents from the index).  *  *  This would require keeping all source fields stored of course.  *  *  This would also allow to not log document data for requests with commit=true  *  in them (since we know that if the request succeeds, all docs will be committed)  *  */
end_comment

begin_class
DECL|class|HdfsTransactionLog
specifier|public
class|class
name|HdfsTransactionLog
extends|extends
name|TransactionLog
block|{
DECL|field|log
specifier|public
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|HdfsTransactionLog
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|tlogFile
name|Path
name|tlogFile
decl_stmt|;
DECL|field|tlogOutStream
specifier|private
name|FSDataOutputStream
name|tlogOutStream
decl_stmt|;
DECL|field|fs
specifier|private
name|FileSystem
name|fs
decl_stmt|;
DECL|method|HdfsTransactionLog
name|HdfsTransactionLog
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|tlogFile
parameter_list|,
name|Collection
argument_list|<
name|String
argument_list|>
name|globalStrings
parameter_list|)
block|{
name|this
argument_list|(
name|fs
argument_list|,
name|tlogFile
argument_list|,
name|globalStrings
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|HdfsTransactionLog
name|HdfsTransactionLog
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|tlogFile
parameter_list|,
name|Collection
argument_list|<
name|String
argument_list|>
name|globalStrings
parameter_list|,
name|boolean
name|openExisting
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
name|this
operator|.
name|fs
operator|=
name|fs
expr_stmt|;
try|try
block|{
if|if
condition|(
name|debug
condition|)
block|{
comment|//log.debug("New TransactionLog file=" + tlogFile + ", exists=" + tlogFile.exists() + ", size=" + tlogFile.length() + ", openExisting=" + openExisting);
block|}
name|this
operator|.
name|tlogFile
operator|=
name|tlogFile
expr_stmt|;
if|if
condition|(
name|fs
operator|.
name|exists
argument_list|(
name|tlogFile
argument_list|)
operator|&&
name|openExisting
condition|)
block|{
name|FSHDFSUtils
operator|.
name|recoverFileLease
argument_list|(
name|fs
argument_list|,
name|tlogFile
argument_list|,
name|fs
operator|.
name|getConf
argument_list|()
argument_list|)
expr_stmt|;
name|tlogOutStream
operator|=
name|fs
operator|.
name|append
argument_list|(
name|tlogFile
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fs
operator|.
name|delete
argument_list|(
name|tlogFile
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|tlogOutStream
operator|=
name|fs
operator|.
name|create
argument_list|(
name|tlogFile
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|)
expr_stmt|;
name|tlogOutStream
operator|.
name|hsync
argument_list|()
expr_stmt|;
block|}
name|fos
operator|=
operator|new
name|FastOutputStream
argument_list|(
name|tlogOutStream
argument_list|,
operator|new
name|byte
index|[
literal|65536
index|]
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|long
name|start
init|=
name|tlogOutStream
operator|.
name|getPos
argument_list|()
decl_stmt|;
if|if
condition|(
name|openExisting
condition|)
block|{
if|if
condition|(
name|start
operator|>
literal|0
condition|)
block|{
name|readHeader
argument_list|(
literal|null
argument_list|)
expr_stmt|;
comment|// we should already be at the end
comment|// raf.seek(start);
comment|//  assert channel.position() == start;
name|fos
operator|.
name|setWritten
argument_list|(
name|start
argument_list|)
expr_stmt|;
comment|// reflect that we aren't starting at the beginning
comment|//assert fos.size() == channel.size();
block|}
else|else
block|{
name|addGlobalStrings
argument_list|(
name|globalStrings
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|start
operator|>
literal|0
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"New transaction log already exists:"
operator|+
name|tlogFile
operator|+
literal|" size="
operator|+
name|tlogOutStream
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|addGlobalStrings
argument_list|(
name|globalStrings
argument_list|)
expr_stmt|;
block|}
name|success
operator|=
literal|true
expr_stmt|;
assert|assert
name|ObjectReleaseTracker
operator|.
name|track
argument_list|(
name|this
argument_list|)
assert|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
operator|&&
name|tlogOutStream
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|tlogOutStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Error closing tlog file (after error opening)"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|endsWithCommit
specifier|public
name|boolean
name|endsWithCommit
parameter_list|()
throws|throws
name|IOException
block|{
name|long
name|size
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|fos
operator|.
name|flush
argument_list|()
expr_stmt|;
name|tlogOutStream
operator|.
name|hflush
argument_list|()
expr_stmt|;
name|size
operator|=
name|fos
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
comment|// the end of the file should have the end message (added during a commit) plus a 4 byte size
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
name|END_MESSAGE
operator|.
name|length
argument_list|()
index|]
decl_stmt|;
name|long
name|pos
init|=
name|size
operator|-
name|END_MESSAGE
operator|.
name|length
argument_list|()
operator|-
literal|4
decl_stmt|;
if|if
condition|(
name|pos
operator|<
literal|0
condition|)
return|return
literal|false
return|;
name|FSDataFastInputStream
name|dis
init|=
operator|new
name|FSDataFastInputStream
argument_list|(
name|fs
operator|.
name|open
argument_list|(
name|tlogFile
argument_list|)
argument_list|,
name|pos
argument_list|)
decl_stmt|;
try|try
block|{
comment|//ChannelFastInputStream is = new ChannelFastInputStream(channel, pos);
name|dis
operator|.
name|read
argument_list|(
name|buf
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
name|buf
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|buf
index|[
name|i
index|]
operator|!=
name|END_MESSAGE
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
condition|)
return|return
literal|false
return|;
block|}
block|}
finally|finally
block|{
name|dis
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
comment|// This could mess with any readers or reverse readers that are open, or anything that might try to do a log lookup.
comment|// This should only be used to roll back buffered updates, not actually applied updates.
annotation|@
name|Override
DECL|method|rollback
specifier|public
name|void
name|rollback
parameter_list|(
name|long
name|pos
parameter_list|)
throws|throws
name|IOException
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
assert|assert
name|snapshot_size
operator|==
name|pos
assert|;
name|fos
operator|.
name|flush
argument_list|()
expr_stmt|;
name|tlogOutStream
operator|.
name|hflush
argument_list|()
expr_stmt|;
comment|// TODO: how do we rollback with hdfs?? We need HDFS-3107
comment|//raf.setLength(pos);
name|fos
operator|.
name|setWritten
argument_list|(
name|pos
argument_list|)
expr_stmt|;
assert|assert
name|fos
operator|.
name|size
argument_list|()
operator|==
name|pos
assert|;
name|numRecords
operator|=
name|snapshot_numRecords
expr_stmt|;
block|}
block|}
DECL|method|readHeader
specifier|private
name|void
name|readHeader
parameter_list|(
name|FastInputStream
name|fis
parameter_list|)
throws|throws
name|IOException
block|{
comment|// read existing header
name|boolean
name|closeFis
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|fis
operator|==
literal|null
condition|)
name|closeFis
operator|=
literal|true
expr_stmt|;
name|fis
operator|=
name|fis
operator|!=
literal|null
condition|?
name|fis
else|:
operator|new
name|FSDataFastInputStream
argument_list|(
name|fs
operator|.
name|open
argument_list|(
name|tlogFile
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|Map
name|header
init|=
literal|null
decl_stmt|;
try|try
block|{
name|LogCodec
name|codec
init|=
operator|new
name|LogCodec
argument_list|(
name|resolver
argument_list|)
decl_stmt|;
name|header
operator|=
operator|(
name|Map
operator|)
name|codec
operator|.
name|unmarshal
argument_list|(
name|fis
argument_list|)
expr_stmt|;
name|fis
operator|.
name|readInt
argument_list|()
expr_stmt|;
comment|// skip size
block|}
finally|finally
block|{
if|if
condition|(
name|fis
operator|!=
literal|null
operator|&&
name|closeFis
condition|)
block|{
name|fis
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|// needed to read other records
synchronized|synchronized
init|(
name|this
init|)
block|{
name|globalStringList
operator|=
operator|(
name|List
argument_list|<
name|String
argument_list|>
operator|)
name|header
operator|.
name|get
argument_list|(
literal|"strings"
argument_list|)
expr_stmt|;
name|globalStringMap
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|globalStringList
operator|.
name|size
argument_list|()
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
name|globalStringList
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|globalStringMap
operator|.
name|put
argument_list|(
name|globalStringList
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|i
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|writeCommit
specifier|public
name|long
name|writeCommit
parameter_list|(
name|CommitUpdateCommand
name|cmd
parameter_list|,
name|int
name|flags
parameter_list|)
block|{
name|LogCodec
name|codec
init|=
operator|new
name|LogCodec
argument_list|(
name|resolver
argument_list|)
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
try|try
block|{
name|long
name|pos
init|=
name|fos
operator|.
name|size
argument_list|()
decl_stmt|;
comment|// if we had flushed, this should be equal to channel.position()
if|if
condition|(
name|pos
operator|==
literal|0
condition|)
block|{
name|writeLogHeader
argument_list|(
name|codec
argument_list|)
expr_stmt|;
name|pos
operator|=
name|fos
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
name|codec
operator|.
name|init
argument_list|(
name|fos
argument_list|)
expr_stmt|;
name|codec
operator|.
name|writeTag
argument_list|(
name|JavaBinCodec
operator|.
name|ARR
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|codec
operator|.
name|writeInt
argument_list|(
name|UpdateLog
operator|.
name|COMMIT
operator||
name|flags
argument_list|)
expr_stmt|;
comment|// should just take one byte
name|codec
operator|.
name|writeLong
argument_list|(
name|cmd
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
name|codec
operator|.
name|writeStr
argument_list|(
name|END_MESSAGE
argument_list|)
expr_stmt|;
comment|// ensure these bytes are (almost) last in the file
name|endRecord
argument_list|(
name|pos
argument_list|)
expr_stmt|;
name|fos
operator|.
name|flush
argument_list|()
expr_stmt|;
comment|// flush since this will be the last record in a log fill
name|tlogOutStream
operator|.
name|hflush
argument_list|()
expr_stmt|;
comment|//assert fos.size() == channel.size();
return|return
name|pos
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
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
comment|/* This method is thread safe */
annotation|@
name|Override
DECL|method|lookup
specifier|public
name|Object
name|lookup
parameter_list|(
name|long
name|pos
parameter_list|)
block|{
comment|// A negative position can result from a log replay (which does not re-log, but does
comment|// update the version map.  This is OK since the node won't be ACTIVE when this happens.
if|if
condition|(
name|pos
operator|<
literal|0
condition|)
return|return
literal|null
return|;
try|try
block|{
comment|// make sure any unflushed buffer has been flushed
synchronized|synchronized
init|(
name|this
init|)
block|{
comment|// TODO: optimize this by keeping track of what we have flushed up to
name|fos
operator|.
name|flushBuffer
argument_list|()
expr_stmt|;
comment|// flush to hdfs
name|tlogOutStream
operator|.
name|hflush
argument_list|()
expr_stmt|;
comment|/***          System.out.println("###flushBuffer to " + fos.size() + " raf.length()=" + raf.length() + " pos="+pos);         if (fos.size() != raf.length() || pos>= fos.size() ) {           throw new RuntimeException("ERROR" + "###flushBuffer to " + fos.size() + " raf.length()=" + raf.length() + " pos="+pos);         }         ***/
block|}
name|FSDataFastInputStream
name|dis
init|=
operator|new
name|FSDataFastInputStream
argument_list|(
name|fs
operator|.
name|open
argument_list|(
name|tlogFile
argument_list|)
argument_list|,
name|pos
argument_list|)
decl_stmt|;
try|try
block|{
name|dis
operator|.
name|seek
argument_list|(
name|pos
argument_list|)
expr_stmt|;
name|LogCodec
name|codec
init|=
operator|new
name|LogCodec
argument_list|(
name|resolver
argument_list|)
decl_stmt|;
return|return
name|codec
operator|.
name|readVal
argument_list|(
operator|new
name|FastInputStream
argument_list|(
name|dis
argument_list|)
argument_list|)
return|;
block|}
finally|finally
block|{
name|dis
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"pos="
operator|+
name|pos
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|finish
specifier|public
name|void
name|finish
parameter_list|(
name|UpdateLog
operator|.
name|SyncLevel
name|syncLevel
parameter_list|)
block|{
if|if
condition|(
name|syncLevel
operator|==
name|UpdateLog
operator|.
name|SyncLevel
operator|.
name|NONE
condition|)
return|return;
try|try
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
name|fos
operator|.
name|flushBuffer
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|syncLevel
operator|==
name|UpdateLog
operator|.
name|SyncLevel
operator|.
name|FSYNC
condition|)
block|{
name|tlogOutStream
operator|.
name|hsync
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|tlogOutStream
operator|.
name|hflush
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|close
specifier|protected
name|void
name|close
parameter_list|()
block|{
try|try
block|{
if|if
condition|(
name|debug
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Closing tlog"
operator|+
name|this
argument_list|)
expr_stmt|;
block|}
synchronized|synchronized
init|(
name|this
init|)
block|{
name|fos
operator|.
name|flushBuffer
argument_list|()
expr_stmt|;
block|}
name|tlogOutStream
operator|.
name|hflush
argument_list|()
expr_stmt|;
name|tlogOutStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Exception closing tlog."
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
assert|assert
name|ObjectReleaseTracker
operator|.
name|release
argument_list|(
name|this
argument_list|)
assert|;
if|if
condition|(
name|deleteOnClose
condition|)
block|{
try|try
block|{
name|fs
operator|.
name|delete
argument_list|(
name|tlogFile
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"hdfs tlog{file="
operator|+
name|tlogFile
operator|.
name|toString
argument_list|()
operator|+
literal|" refcount="
operator|+
name|refcount
operator|.
name|get
argument_list|()
operator|+
literal|"}"
return|;
block|}
comment|/** Returns a reader that can be used while a log is still in use.    * Currently only *one* LogReader may be outstanding, and that log may only    * be used from a single thread. */
annotation|@
name|Override
DECL|method|getReader
specifier|public
name|LogReader
name|getReader
parameter_list|(
name|long
name|startingPos
parameter_list|)
block|{
return|return
operator|new
name|HDFSLogReader
argument_list|(
name|startingPos
argument_list|)
return|;
block|}
comment|/** Returns a single threaded reverse reader */
annotation|@
name|Override
DECL|method|getReverseReader
specifier|public
name|ReverseReader
name|getReverseReader
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|HDFSReverseReader
argument_list|()
return|;
block|}
DECL|class|HDFSLogReader
specifier|public
class|class
name|HDFSLogReader
extends|extends
name|LogReader
block|{
DECL|field|fis
name|FSDataFastInputStream
name|fis
decl_stmt|;
DECL|field|codec
specifier|private
name|LogCodec
name|codec
init|=
operator|new
name|LogCodec
argument_list|(
name|resolver
argument_list|)
decl_stmt|;
DECL|field|sz
specifier|private
name|long
name|sz
decl_stmt|;
DECL|method|HDFSLogReader
specifier|public
name|HDFSLogReader
parameter_list|(
name|long
name|startingPos
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|incref
argument_list|()
expr_stmt|;
try|try
block|{
name|FSDataInputStream
name|fdis
init|=
name|fs
operator|.
name|open
argument_list|(
name|tlogFile
argument_list|)
decl_stmt|;
name|sz
operator|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|tlogFile
argument_list|)
operator|.
name|getLen
argument_list|()
expr_stmt|;
name|fis
operator|=
operator|new
name|FSDataFastInputStream
argument_list|(
name|fdis
argument_list|,
name|startingPos
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/** Returns the next object from the log, or null if none available.      *      * @return The log record, or null if EOF      * @throws IOException If there is a low-level I/O error.      */
DECL|method|next
specifier|public
name|Object
name|next
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|long
name|pos
init|=
name|fis
operator|.
name|position
argument_list|()
decl_stmt|;
synchronized|synchronized
init|(
name|HdfsTransactionLog
operator|.
name|this
init|)
block|{
if|if
condition|(
name|trace
condition|)
block|{
name|log
operator|.
name|trace
argument_list|(
literal|"Reading log record.  pos="
operator|+
name|pos
operator|+
literal|" currentSize="
operator|+
name|fos
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|pos
operator|>=
name|fos
operator|.
name|size
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|fos
operator|.
name|flushBuffer
argument_list|()
expr_stmt|;
block|}
comment|// we actually need a new reader to
comment|// see if any data was added by the writer
if|if
condition|(
name|fis
operator|.
name|position
argument_list|()
operator|>=
name|sz
condition|)
block|{
name|fis
operator|.
name|close
argument_list|()
expr_stmt|;
name|tlogOutStream
operator|.
name|hflush
argument_list|()
expr_stmt|;
try|try
block|{
name|FSDataInputStream
name|fdis
init|=
name|fs
operator|.
name|open
argument_list|(
name|tlogFile
argument_list|)
decl_stmt|;
name|fis
operator|=
operator|new
name|FSDataFastInputStream
argument_list|(
name|fdis
argument_list|,
name|pos
argument_list|)
expr_stmt|;
name|sz
operator|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|tlogFile
argument_list|)
operator|.
name|getLen
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|pos
operator|==
literal|0
condition|)
block|{
name|readHeader
argument_list|(
name|fis
argument_list|)
expr_stmt|;
comment|// shouldn't currently happen - header and first record are currently written at the same time
synchronized|synchronized
init|(
name|HdfsTransactionLog
operator|.
name|this
init|)
block|{
if|if
condition|(
name|fis
operator|.
name|position
argument_list|()
operator|>=
name|fos
operator|.
name|size
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|pos
operator|=
name|fis
operator|.
name|position
argument_list|()
expr_stmt|;
block|}
block|}
name|Object
name|o
init|=
name|codec
operator|.
name|readVal
argument_list|(
name|fis
argument_list|)
decl_stmt|;
comment|// skip over record size
name|int
name|size
init|=
name|fis
operator|.
name|readInt
argument_list|()
decl_stmt|;
assert|assert
name|size
operator|==
name|fis
operator|.
name|position
argument_list|()
operator|-
name|pos
operator|-
literal|4
assert|;
return|return
name|o
return|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
try|try
block|{
name|fis
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|decref
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
synchronized|synchronized
init|(
name|HdfsTransactionLog
operator|.
name|this
init|)
block|{
return|return
literal|"LogReader{"
operator|+
literal|"file="
operator|+
name|tlogFile
operator|+
literal|", position="
operator|+
name|fis
operator|.
name|position
argument_list|()
operator|+
literal|", end="
operator|+
name|fos
operator|.
name|size
argument_list|()
operator|+
literal|"}"
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|currentPos
specifier|public
name|long
name|currentPos
parameter_list|()
block|{
return|return
name|fis
operator|.
name|position
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|currentSize
specifier|public
name|long
name|currentSize
parameter_list|()
block|{
return|return
name|sz
return|;
block|}
block|}
DECL|class|HDFSReverseReader
specifier|public
class|class
name|HDFSReverseReader
extends|extends
name|ReverseReader
block|{
DECL|field|fis
name|FSDataFastInputStream
name|fis
decl_stmt|;
DECL|field|codec
specifier|private
name|LogCodec
name|codec
init|=
operator|new
name|LogCodec
argument_list|(
name|resolver
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|SolrInputDocument
name|readSolrInputDocument
parameter_list|(
name|DataInputInputStream
name|dis
parameter_list|)
block|{
comment|// Given that the SolrInputDocument is last in an add record, it's OK to just skip
comment|// reading it completely.
return|return
literal|null
return|;
block|}
block|}
decl_stmt|;
DECL|field|nextLength
name|int
name|nextLength
decl_stmt|;
comment|// length of the next record (the next one closer to the start of the log file)
DECL|field|prevPos
name|long
name|prevPos
decl_stmt|;
comment|// where we started reading from last time (so prevPos - nextLength == start of next record)
DECL|method|HDFSReverseReader
specifier|public
name|HDFSReverseReader
parameter_list|()
throws|throws
name|IOException
block|{
name|incref
argument_list|()
expr_stmt|;
name|long
name|sz
decl_stmt|;
synchronized|synchronized
init|(
name|HdfsTransactionLog
operator|.
name|this
init|)
block|{
name|fos
operator|.
name|flushBuffer
argument_list|()
expr_stmt|;
comment|// this must be an hflush
name|tlogOutStream
operator|.
name|hflush
argument_list|()
expr_stmt|;
name|sz
operator|=
name|fos
operator|.
name|size
argument_list|()
expr_stmt|;
comment|//assert sz == channel.size();
block|}
name|fis
operator|=
operator|new
name|FSDataFastInputStream
argument_list|(
name|fs
operator|.
name|open
argument_list|(
name|tlogFile
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
if|if
condition|(
name|sz
operator|>=
literal|4
condition|)
block|{
comment|// readHeader(fis);  // should not be needed
name|prevPos
operator|=
name|sz
operator|-
literal|4
expr_stmt|;
name|fis
operator|.
name|seek
argument_list|(
name|prevPos
argument_list|)
expr_stmt|;
name|nextLength
operator|=
name|fis
operator|.
name|readInt
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** Returns the next object from the log, or null if none available.      *      * @return The log record, or null if EOF      * @throws IOException If there is a low-level I/O error.      */
DECL|method|next
specifier|public
name|Object
name|next
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|prevPos
operator|<=
literal|0
condition|)
return|return
literal|null
return|;
name|long
name|endOfThisRecord
init|=
name|prevPos
decl_stmt|;
name|int
name|thisLength
init|=
name|nextLength
decl_stmt|;
name|long
name|recordStart
init|=
name|prevPos
operator|-
name|thisLength
decl_stmt|;
comment|// back up to the beginning of the next record
name|prevPos
operator|=
name|recordStart
operator|-
literal|4
expr_stmt|;
comment|// back up 4 more to read the length of the next record
if|if
condition|(
name|prevPos
operator|<=
literal|0
condition|)
return|return
literal|null
return|;
comment|// this record is the header
name|long
name|bufferPos
init|=
name|fis
operator|.
name|getBufferPos
argument_list|()
decl_stmt|;
if|if
condition|(
name|prevPos
operator|>=
name|bufferPos
condition|)
block|{
comment|// nothing to do... we're within the current buffer
block|}
else|else
block|{
comment|// Position buffer so that this record is at the end.
comment|// For small records, this will cause subsequent calls to next() to be within the buffer.
name|long
name|seekPos
init|=
name|endOfThisRecord
operator|-
name|fis
operator|.
name|getBufferSize
argument_list|()
decl_stmt|;
name|seekPos
operator|=
name|Math
operator|.
name|min
argument_list|(
name|seekPos
argument_list|,
name|prevPos
argument_list|)
expr_stmt|;
comment|// seek to the start of the record if it's larger then the block size.
name|seekPos
operator|=
name|Math
operator|.
name|max
argument_list|(
name|seekPos
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|fis
operator|.
name|seek
argument_list|(
name|seekPos
argument_list|)
expr_stmt|;
name|fis
operator|.
name|peek
argument_list|()
expr_stmt|;
comment|// cause buffer to be filled
block|}
name|fis
operator|.
name|seek
argument_list|(
name|prevPos
argument_list|)
expr_stmt|;
name|nextLength
operator|=
name|fis
operator|.
name|readInt
argument_list|()
expr_stmt|;
comment|// this is the length of the *next* record (i.e. closer to the beginning)
comment|// TODO: optionally skip document data
name|Object
name|o
init|=
name|codec
operator|.
name|readVal
argument_list|(
name|fis
argument_list|)
decl_stmt|;
comment|// assert fis.position() == prevPos + 4 + thisLength;  // this is only true if we read all the data (and we currently skip reading SolrInputDocument
return|return
name|o
return|;
block|}
comment|/* returns the position in the log file of the last record returned by next() */
DECL|method|position
specifier|public
name|long
name|position
parameter_list|()
block|{
return|return
name|prevPos
operator|+
literal|4
return|;
comment|// skip the length
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
try|try
block|{
name|fis
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|decref
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
synchronized|synchronized
init|(
name|HdfsTransactionLog
operator|.
name|this
init|)
block|{
return|return
literal|"LogReader{"
operator|+
literal|"file="
operator|+
name|tlogFile
operator|+
literal|", position="
operator|+
name|fis
operator|.
name|position
argument_list|()
operator|+
literal|", end="
operator|+
name|fos
operator|.
name|size
argument_list|()
operator|+
literal|"}"
return|;
block|}
block|}
block|}
block|}
end_class

begin_class
DECL|class|FSDataFastInputStream
class|class
name|FSDataFastInputStream
extends|extends
name|FastInputStream
block|{
DECL|field|fis
specifier|private
name|FSDataInputStream
name|fis
decl_stmt|;
DECL|method|FSDataFastInputStream
specifier|public
name|FSDataFastInputStream
parameter_list|(
name|FSDataInputStream
name|fis
parameter_list|,
name|long
name|chPosition
parameter_list|)
block|{
comment|// super(null, new byte[10],0,0);    // a small buffer size for testing purposes
name|super
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|fis
operator|=
name|fis
expr_stmt|;
name|super
operator|.
name|readFromStream
operator|=
name|chPosition
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|readWrappedStream
specifier|public
name|int
name|readWrappedStream
parameter_list|(
name|byte
index|[]
name|target
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|fis
operator|.
name|read
argument_list|(
name|readFromStream
argument_list|,
name|target
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
return|;
block|}
DECL|method|seek
specifier|public
name|void
name|seek
parameter_list|(
name|long
name|position
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|position
operator|<=
name|readFromStream
operator|&&
name|position
operator|>=
name|getBufferPos
argument_list|()
condition|)
block|{
comment|// seek within buffer
name|pos
operator|=
call|(
name|int
call|)
argument_list|(
name|position
operator|-
name|getBufferPos
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// long currSize = ch.size();   // not needed - underlying read should handle (unless read never done)
comment|// if (position> currSize) throw new EOFException("Read past EOF: seeking to " + position + " on file of size " + currSize + " file=" + ch);
name|readFromStream
operator|=
name|position
expr_stmt|;
name|end
operator|=
name|pos
operator|=
literal|0
expr_stmt|;
block|}
assert|assert
name|position
argument_list|()
operator|==
name|position
assert|;
block|}
comment|/** where is the start of the buffer relative to the whole file */
DECL|method|getBufferPos
specifier|public
name|long
name|getBufferPos
parameter_list|()
block|{
return|return
name|readFromStream
operator|-
name|end
return|;
block|}
DECL|method|getBufferSize
specifier|public
name|int
name|getBufferSize
parameter_list|()
block|{
return|return
name|buf
operator|.
name|length
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
name|fis
operator|.
name|close
argument_list|()
expr_stmt|;
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
literal|"readFromStream="
operator|+
name|readFromStream
operator|+
literal|" pos="
operator|+
name|pos
operator|+
literal|" end="
operator|+
name|end
operator|+
literal|" bufferPos="
operator|+
name|getBufferPos
argument_list|()
operator|+
literal|" position="
operator|+
name|position
argument_list|()
return|;
block|}
block|}
end_class

end_unit

