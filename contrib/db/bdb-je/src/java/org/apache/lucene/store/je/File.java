begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.store.je
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|je
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
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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
name|io
operator|.
name|DataOutputStream
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
name|Random
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sleepycat
operator|.
name|je
operator|.
name|Cursor
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sleepycat
operator|.
name|je
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
name|je
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
name|je
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
name|je
operator|.
name|OperationStatus
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sleepycat
operator|.
name|je
operator|.
name|Transaction
import|;
end_import

begin_comment
comment|/**  * Port of Andi Vajda's DbDirectory to Java Edition of Berkeley Database  *   * @author Aaron Donovan  */
end_comment

begin_class
DECL|class|File
specifier|public
class|class
name|File
extends|extends
name|Object
block|{
DECL|field|random
specifier|static
specifier|protected
name|Random
name|random
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
DECL|field|key
DECL|field|data
specifier|protected
name|DatabaseEntry
name|key
decl_stmt|,
name|data
decl_stmt|;
DECL|field|length
DECL|field|timeModified
specifier|protected
name|long
name|length
decl_stmt|,
name|timeModified
decl_stmt|;
DECL|field|name
specifier|protected
name|String
name|name
decl_stmt|;
DECL|field|uuid
specifier|protected
name|byte
index|[]
name|uuid
decl_stmt|;
DECL|method|File
specifier|protected
name|File
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|setName
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|data
operator|=
operator|new
name|DatabaseEntry
argument_list|(
operator|new
name|byte
index|[
literal|32
index|]
argument_list|)
expr_stmt|;
block|}
DECL|method|File
specifier|protected
name|File
parameter_list|(
name|JEDirectory
name|directory
parameter_list|,
name|String
name|name
parameter_list|,
name|boolean
name|create
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|exists
argument_list|(
name|directory
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|create
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"File does not exist: "
operator|+
name|name
argument_list|)
throw|;
else|else
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
literal|24
index|]
argument_list|)
decl_stmt|;
name|DatabaseEntry
name|data
init|=
operator|new
name|DatabaseEntry
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|Database
name|blocks
init|=
name|directory
operator|.
name|blocks
decl_stmt|;
name|Transaction
name|txn
init|=
name|directory
operator|.
name|txn
decl_stmt|;
name|data
operator|.
name|setPartial
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|uuid
operator|=
operator|new
name|byte
index|[
literal|16
index|]
expr_stmt|;
try|try
block|{
do|do
block|{
comment|/* generate a v.4 random-uuid unique to this db */
name|random
operator|.
name|nextBytes
argument_list|(
name|uuid
argument_list|)
expr_stmt|;
name|uuid
index|[
literal|6
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
operator|(
name|byte
operator|)
literal|0x40
operator||
operator|(
name|uuid
index|[
literal|6
index|]
operator|&
operator|(
name|byte
operator|)
literal|0x0f
operator|)
argument_list|)
expr_stmt|;
name|uuid
index|[
literal|8
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
operator|(
name|byte
operator|)
literal|0x80
operator||
operator|(
name|uuid
index|[
literal|8
index|]
operator|&
operator|(
name|byte
operator|)
literal|0x3f
operator|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|uuid
argument_list|,
literal|0
argument_list|,
name|key
operator|.
name|getData
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|16
argument_list|)
expr_stmt|;
comment|// TODO check LockMode
block|}
do|while
condition|(
name|blocks
operator|.
name|get
argument_list|(
name|txn
argument_list|,
name|key
argument_list|,
name|data
argument_list|,
literal|null
argument_list|)
operator|!=
name|OperationStatus
operator|.
name|NOTFOUND
condition|)
do|;
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
block|}
block|}
elseif|else
if|if
condition|(
name|create
condition|)
name|length
operator|=
literal|0L
expr_stmt|;
block|}
DECL|method|getName
specifier|protected
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
DECL|method|setName
specifier|private
name|void
name|setName
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|ByteArrayOutputStream
name|buffer
init|=
operator|new
name|ByteArrayOutputStream
argument_list|(
literal|128
argument_list|)
decl_stmt|;
name|DataOutputStream
name|out
init|=
operator|new
name|DataOutputStream
argument_list|(
name|buffer
argument_list|)
decl_stmt|;
name|out
operator|.
name|writeUTF
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|key
operator|=
operator|new
name|DatabaseEntry
argument_list|(
name|buffer
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
DECL|method|getKey
specifier|protected
name|byte
index|[]
name|getKey
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|uuid
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Uninitialized file"
argument_list|)
throw|;
return|return
name|uuid
return|;
block|}
DECL|method|getLength
specifier|protected
name|long
name|getLength
parameter_list|()
block|{
return|return
name|length
return|;
block|}
DECL|method|getTimeModified
specifier|protected
name|long
name|getTimeModified
parameter_list|()
block|{
return|return
name|timeModified
return|;
block|}
DECL|method|exists
specifier|protected
name|boolean
name|exists
parameter_list|(
name|JEDirectory
name|directory
parameter_list|)
throws|throws
name|IOException
block|{
name|Database
name|files
init|=
name|directory
operator|.
name|files
decl_stmt|;
name|Transaction
name|txn
init|=
name|directory
operator|.
name|txn
decl_stmt|;
try|try
block|{
comment|// TODO check LockMode
if|if
condition|(
name|files
operator|.
name|get
argument_list|(
name|txn
argument_list|,
name|key
argument_list|,
name|data
argument_list|,
literal|null
argument_list|)
operator|==
name|OperationStatus
operator|.
name|NOTFOUND
condition|)
return|return
literal|false
return|;
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
name|byte
index|[]
name|bytes
init|=
name|data
operator|.
name|getData
argument_list|()
decl_stmt|;
name|ByteArrayInputStream
name|buffer
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|bytes
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
name|length
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|timeModified
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
name|uuid
operator|=
operator|new
name|byte
index|[
literal|16
index|]
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|bytes
argument_list|,
literal|16
argument_list|,
name|uuid
argument_list|,
literal|0
argument_list|,
literal|16
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
DECL|method|modify
specifier|protected
name|void
name|modify
parameter_list|(
name|JEDirectory
name|directory
parameter_list|,
name|long
name|length
parameter_list|,
name|long
name|timeModified
parameter_list|)
throws|throws
name|IOException
block|{
name|ByteArrayOutputStream
name|buffer
init|=
operator|new
name|ByteArrayOutputStream
argument_list|(
literal|32
argument_list|)
decl_stmt|;
name|DataOutputStream
name|out
init|=
operator|new
name|DataOutputStream
argument_list|(
name|buffer
argument_list|)
decl_stmt|;
name|Database
name|files
init|=
name|directory
operator|.
name|files
decl_stmt|;
name|Transaction
name|txn
init|=
name|directory
operator|.
name|txn
decl_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|length
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|timeModified
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|buffer
operator|.
name|toByteArray
argument_list|()
argument_list|,
literal|0
argument_list|,
name|data
operator|.
name|getData
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|32
argument_list|)
expr_stmt|;
try|try
block|{
name|files
operator|.
name|put
argument_list|(
name|txn
argument_list|,
name|key
argument_list|,
name|data
argument_list|)
expr_stmt|;
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
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
name|this
operator|.
name|timeModified
operator|=
name|timeModified
expr_stmt|;
block|}
DECL|method|delete
specifier|protected
name|void
name|delete
parameter_list|(
name|JEDirectory
name|directory
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|exists
argument_list|(
name|directory
argument_list|)
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"File does not exist: "
operator|+
name|getName
argument_list|()
argument_list|)
throw|;
name|Cursor
name|cursor
init|=
literal|null
decl_stmt|;
try|try
block|{
try|try
block|{
name|byte
index|[]
name|bytes
init|=
name|getKey
argument_list|()
decl_stmt|;
name|int
name|ulen
init|=
name|bytes
operator|.
name|length
operator|+
literal|8
decl_stmt|;
name|byte
index|[]
name|cursorBytes
init|=
operator|new
name|byte
index|[
name|ulen
index|]
decl_stmt|;
name|DatabaseEntry
name|cursorKey
init|=
operator|new
name|DatabaseEntry
argument_list|(
name|cursorBytes
argument_list|)
decl_stmt|;
name|DatabaseEntry
name|cursorData
init|=
operator|new
name|DatabaseEntry
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|Database
name|files
init|=
name|directory
operator|.
name|files
decl_stmt|;
name|Database
name|blocks
init|=
name|directory
operator|.
name|blocks
decl_stmt|;
name|Transaction
name|txn
init|=
name|directory
operator|.
name|txn
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|cursorBytes
argument_list|,
literal|0
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
name|cursorData
operator|.
name|setPartial
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|cursor
operator|=
name|blocks
operator|.
name|openCursor
argument_list|(
name|txn
argument_list|,
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|cursor
operator|.
name|getSearchKey
argument_list|(
name|cursorKey
argument_list|,
name|cursorData
argument_list|,
literal|null
argument_list|)
operator|!=
name|OperationStatus
operator|.
name|NOTFOUND
condition|)
block|{
name|cursor
operator|.
name|delete
argument_list|()
expr_stmt|;
name|advance
label|:
while|while
condition|(
name|cursor
operator|.
name|getNext
argument_list|(
name|cursorKey
argument_list|,
name|cursorData
argument_list|,
literal|null
argument_list|)
operator|!=
name|OperationStatus
operator|.
name|NOTFOUND
condition|)
block|{
name|byte
index|[]
name|temp
init|=
name|cursorKey
operator|.
name|getData
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
name|bytes
operator|.
name|length
condition|;
name|i
operator|++
control|)
if|if
condition|(
name|bytes
index|[
name|i
index|]
operator|!=
name|temp
index|[
name|i
index|]
condition|)
block|{
break|break
name|advance
break|;
block|}
name|cursor
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
name|files
operator|.
name|delete
argument_list|(
name|txn
argument_list|,
name|key
argument_list|)
expr_stmt|;
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
block|}
DECL|method|rename
specifier|protected
name|void
name|rename
parameter_list|(
name|JEDirectory
name|directory
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|exists
argument_list|(
name|directory
argument_list|)
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"File does not exist: "
operator|+
name|getName
argument_list|()
argument_list|)
throw|;
name|File
name|newFile
init|=
operator|new
name|File
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|newFile
operator|.
name|exists
argument_list|(
name|directory
argument_list|)
condition|)
name|newFile
operator|.
name|delete
argument_list|(
name|directory
argument_list|)
expr_stmt|;
try|try
block|{
name|Database
name|files
init|=
name|directory
operator|.
name|files
decl_stmt|;
name|Transaction
name|txn
init|=
name|directory
operator|.
name|txn
decl_stmt|;
name|files
operator|.
name|delete
argument_list|(
name|txn
argument_list|,
name|key
argument_list|)
expr_stmt|;
name|setName
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|files
operator|.
name|put
argument_list|(
name|txn
argument_list|,
name|key
argument_list|,
name|data
argument_list|)
expr_stmt|;
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
block|}
block|}
end_class

end_unit

