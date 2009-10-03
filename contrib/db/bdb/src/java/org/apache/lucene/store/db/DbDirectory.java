begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.store.db
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|db
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
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInputStream
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
name|HashSet
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|IndexInput
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sleepycat
operator|.
name|db
operator|.
name|internal
operator|.
name|DbEnv
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sleepycat
operator|.
name|db
operator|.
name|internal
operator|.
name|Db
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sleepycat
operator|.
name|db
operator|.
name|internal
operator|.
name|DbConstants
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sleepycat
operator|.
name|db
operator|.
name|DatabaseEntry
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sleepycat
operator|.
name|db
operator|.
name|internal
operator|.
name|Dbc
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sleepycat
operator|.
name|db
operator|.
name|internal
operator|.
name|DbTxn
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sleepycat
operator|.
name|db
operator|.
name|DatabaseException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sleepycat
operator|.
name|db
operator|.
name|Database
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sleepycat
operator|.
name|db
operator|.
name|Transaction
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sleepycat
operator|.
name|db
operator|.
name|DbHandleExtractor
import|;
end_import

begin_comment
comment|/**  * A DbDirectory is a Berkeley DB 4.3 based implementation of   * {@link org.apache.lucene.store.Directory Directory}. It uses two  * {@link com.sleepycat.db.internal.Db Db} database handles, one for storing file  * records and another for storing file data blocks.  *  */
end_comment

begin_class
DECL|class|DbDirectory
specifier|public
class|class
name|DbDirectory
extends|extends
name|Directory
block|{
DECL|field|openFiles
specifier|protected
name|Set
name|openFiles
init|=
name|Collections
operator|.
name|synchronizedSet
argument_list|(
operator|new
name|HashSet
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|files
DECL|field|blocks
specifier|protected
name|Db
name|files
decl_stmt|,
name|blocks
decl_stmt|;
DECL|field|txn
specifier|protected
name|DbTxn
name|txn
decl_stmt|;
DECL|field|flags
specifier|protected
name|int
name|flags
decl_stmt|;
comment|/**      * Instantiate a DbDirectory. The same threading rules that apply to      * Berkeley DB handles apply to instances of DbDirectory.      *      * @param txn a transaction handle that is going to be used for all db      * operations done by this instance. This parameter may be      *<code>null</code>.      * @param files a db handle to store file records.      * @param blocks a db handle to store file data blocks.      * @param flags flags used for db read operations.      */
DECL|method|DbDirectory
specifier|public
name|DbDirectory
parameter_list|(
name|DbTxn
name|txn
parameter_list|,
name|Db
name|files
parameter_list|,
name|Db
name|blocks
parameter_list|,
name|int
name|flags
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|txn
operator|=
name|txn
expr_stmt|;
name|this
operator|.
name|files
operator|=
name|files
expr_stmt|;
name|this
operator|.
name|blocks
operator|=
name|blocks
expr_stmt|;
name|this
operator|.
name|flags
operator|=
name|flags
expr_stmt|;
block|}
DECL|method|DbDirectory
specifier|public
name|DbDirectory
parameter_list|(
name|Transaction
name|txn
parameter_list|,
name|Database
name|files
parameter_list|,
name|Database
name|blocks
parameter_list|,
name|int
name|flags
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|txn
operator|=
name|txn
operator|!=
literal|null
condition|?
name|DbHandleExtractor
operator|.
name|getDbTxn
argument_list|(
name|txn
argument_list|)
else|:
literal|null
expr_stmt|;
name|this
operator|.
name|files
operator|=
name|DbHandleExtractor
operator|.
name|getDb
argument_list|(
name|files
argument_list|)
expr_stmt|;
name|this
operator|.
name|blocks
operator|=
name|DbHandleExtractor
operator|.
name|getDb
argument_list|(
name|blocks
argument_list|)
expr_stmt|;
name|this
operator|.
name|flags
operator|=
name|flags
expr_stmt|;
block|}
DECL|method|DbDirectory
specifier|public
name|DbDirectory
parameter_list|(
name|Transaction
name|txn
parameter_list|,
name|Database
name|files
parameter_list|,
name|Database
name|blocks
parameter_list|)
block|{
name|this
argument_list|(
name|txn
argument_list|,
name|files
argument_list|,
name|blocks
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|flush
argument_list|()
expr_stmt|;
block|}
comment|/**      * Flush the currently open files. After they have been flushed it is      * safe to commit the transaction without closing this DbDirectory      * instance first.      * @see #setTransaction      */
DECL|method|flush
specifier|public
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
name|Iterator
name|iterator
init|=
name|openFiles
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
operator|(
operator|(
name|IndexOutput
operator|)
name|iterator
operator|.
name|next
argument_list|()
operator|)
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
DECL|method|createOutput
specifier|public
name|IndexOutput
name|createOutput
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|DbIndexOutput
argument_list|(
name|this
argument_list|,
name|name
argument_list|,
literal|true
argument_list|)
return|;
block|}
DECL|method|deleteFile
specifier|public
name|void
name|deleteFile
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
operator|new
name|File
argument_list|(
name|name
argument_list|)
operator|.
name|delete
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
DECL|method|fileExists
specifier|public
name|boolean
name|fileExists
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|File
argument_list|(
name|name
argument_list|)
operator|.
name|exists
argument_list|(
name|this
argument_list|)
return|;
block|}
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
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|file
operator|.
name|exists
argument_list|(
name|this
argument_list|)
condition|)
return|return
name|file
operator|.
name|getLength
argument_list|()
return|;
throw|throw
operator|new
name|IOException
argument_list|(
literal|"File does not exist: "
operator|+
name|name
argument_list|)
throw|;
block|}
DECL|method|fileModified
specifier|public
name|long
name|fileModified
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|file
operator|.
name|exists
argument_list|(
name|this
argument_list|)
condition|)
return|return
name|file
operator|.
name|getTimeModified
argument_list|()
return|;
throw|throw
operator|new
name|IOException
argument_list|(
literal|"File does not exist: "
operator|+
name|name
argument_list|)
throw|;
block|}
DECL|method|listAll
specifier|public
name|String
index|[]
name|listAll
parameter_list|()
throws|throws
name|IOException
block|{
name|Dbc
name|cursor
init|=
literal|null
decl_stmt|;
name|List
name|list
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
try|try
block|{
try|try
block|{
name|DatabaseEntry
name|key
init|=
operator|new
name|DatabaseEntry
argument_list|(
operator|new
name|byte
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|DatabaseEntry
name|data
init|=
operator|new
name|DatabaseEntry
argument_list|(
operator|(
name|byte
index|[]
operator|)
literal|null
argument_list|)
decl_stmt|;
name|data
operator|.
name|setPartial
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|cursor
operator|=
name|files
operator|.
name|cursor
argument_list|(
name|txn
argument_list|,
name|flags
argument_list|)
expr_stmt|;
if|if
condition|(
name|cursor
operator|.
name|get
argument_list|(
name|key
argument_list|,
name|data
argument_list|,
name|DbConstants
operator|.
name|DB_SET_RANGE
operator||
name|flags
argument_list|)
operator|!=
name|DbConstants
operator|.
name|DB_NOTFOUND
condition|)
block|{
name|ByteArrayInputStream
name|buffer
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|key
operator|.
name|getData
argument_list|()
argument_list|)
decl_stmt|;
name|DataInputStream
name|in
init|=
operator|new
name|DataInputStream
argument_list|(
name|buffer
argument_list|)
decl_stmt|;
name|String
name|name
init|=
name|in
operator|.
name|readUTF
argument_list|()
decl_stmt|;
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
while|while
condition|(
name|cursor
operator|.
name|get
argument_list|(
name|key
argument_list|,
name|data
argument_list|,
name|DbConstants
operator|.
name|DB_NEXT
operator||
name|flags
argument_list|)
operator|!=
name|DbConstants
operator|.
name|DB_NOTFOUND
condition|)
block|{
name|buffer
operator|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|key
operator|.
name|getData
argument_list|()
argument_list|)
expr_stmt|;
name|in
operator|=
operator|new
name|DataInputStream
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
name|name
operator|=
name|in
operator|.
name|readUTF
argument_list|()
expr_stmt|;
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|cursor
operator|!=
literal|null
condition|)
name|cursor
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|DatabaseException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
return|return
operator|(
name|String
index|[]
operator|)
name|list
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|list
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
DECL|method|openInput
specifier|public
name|IndexInput
name|openInput
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|DbIndexInput
argument_list|(
name|this
argument_list|,
name|name
argument_list|)
return|;
block|}
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
operator|new
name|DbLock
argument_list|()
return|;
block|}
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
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|long
name|length
init|=
literal|0L
decl_stmt|;
if|if
condition|(
name|file
operator|.
name|exists
argument_list|(
name|this
argument_list|)
condition|)
name|length
operator|=
name|file
operator|.
name|getLength
argument_list|()
expr_stmt|;
name|file
operator|.
name|modify
argument_list|(
name|this
argument_list|,
name|length
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Once a transaction handle was committed it is no longer valid. In      * order to continue using this DbDirectory instance after a commit, the      * transaction handle has to be replaced.      * @param txn the new transaction handle to use      */
DECL|method|setTransaction
specifier|public
name|void
name|setTransaction
parameter_list|(
name|Transaction
name|txn
parameter_list|)
block|{
name|setTransaction
argument_list|(
name|txn
operator|!=
literal|null
condition|?
name|DbHandleExtractor
operator|.
name|getDbTxn
argument_list|(
name|txn
argument_list|)
else|:
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**      * Once a transaction handle was committed it is no longer valid. In      * order to continue using this DbDirectory instance after a commit, the      * transaction handle has to be replaced.      * @param txn the new transaction handle to use      */
DECL|method|setTransaction
specifier|public
name|void
name|setTransaction
parameter_list|(
name|DbTxn
name|txn
parameter_list|)
block|{
name|this
operator|.
name|txn
operator|=
name|txn
expr_stmt|;
block|}
block|}
end_class

end_unit

