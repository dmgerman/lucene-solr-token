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
comment|/**  * Copyright 2005 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
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
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|EnvironmentConfig
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
name|Environment
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
name|DatabaseConfig
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
name|DatabaseType
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

begin_comment
comment|/**  * Tests {@link DbDirectory}.  *  * Adapted from org.apache.lucene.StoreTest with larger files and random bytes.  * @author Andi Vajda  */
end_comment

begin_class
DECL|class|DbStoreTest
specifier|public
class|class
name|DbStoreTest
extends|extends
name|TestCase
block|{
DECL|field|dbHome
specifier|protected
name|File
name|dbHome
init|=
operator|new
name|File
argument_list|(
literal|"index"
argument_list|)
decl_stmt|;
DECL|field|env
specifier|protected
name|Environment
name|env
decl_stmt|;
DECL|field|index
DECL|field|blocks
specifier|protected
name|Database
name|index
decl_stmt|,
name|blocks
decl_stmt|;
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
name|dbHome
operator|.
name|exists
argument_list|()
condition|)
name|dbHome
operator|.
name|mkdir
argument_list|()
expr_stmt|;
else|else
block|{
name|File
index|[]
name|files
init|=
name|dbHome
operator|.
name|listFiles
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
name|String
name|name
init|=
name|files
index|[
name|i
index|]
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|startsWith
argument_list|(
literal|"__"
argument_list|)
operator|||
name|name
operator|.
name|startsWith
argument_list|(
literal|"log."
argument_list|)
condition|)
name|files
index|[
name|i
index|]
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
name|EnvironmentConfig
name|envConfig
init|=
operator|new
name|EnvironmentConfig
argument_list|()
decl_stmt|;
name|DatabaseConfig
name|dbConfig
init|=
operator|new
name|DatabaseConfig
argument_list|()
decl_stmt|;
name|envConfig
operator|.
name|setTransactional
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|envConfig
operator|.
name|setInitializeCache
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|envConfig
operator|.
name|setInitializeLocking
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|envConfig
operator|.
name|setInitializeLogging
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|envConfig
operator|.
name|setAllowCreate
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|envConfig
operator|.
name|setThreaded
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|dbConfig
operator|.
name|setAllowCreate
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|dbConfig
operator|.
name|setType
argument_list|(
name|DatabaseType
operator|.
name|BTREE
argument_list|)
expr_stmt|;
name|env
operator|=
operator|new
name|Environment
argument_list|(
name|dbHome
argument_list|,
name|envConfig
argument_list|)
expr_stmt|;
name|Transaction
name|txn
init|=
literal|null
decl_stmt|;
try|try
block|{
name|txn
operator|=
name|env
operator|.
name|beginTransaction
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|index
operator|=
name|env
operator|.
name|openDatabase
argument_list|(
name|txn
argument_list|,
literal|"__index__"
argument_list|,
literal|null
argument_list|,
name|dbConfig
argument_list|)
expr_stmt|;
name|blocks
operator|=
name|env
operator|.
name|openDatabase
argument_list|(
name|txn
argument_list|,
literal|"__blocks__"
argument_list|,
literal|null
argument_list|,
name|dbConfig
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DatabaseException
name|e
parameter_list|)
block|{
if|if
condition|(
name|txn
operator|!=
literal|null
condition|)
block|{
name|txn
operator|.
name|abort
argument_list|()
expr_stmt|;
name|txn
operator|=
literal|null
expr_stmt|;
block|}
name|index
operator|=
literal|null
expr_stmt|;
name|blocks
operator|=
literal|null
expr_stmt|;
throw|throw
name|e
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|txn
operator|!=
literal|null
condition|)
name|txn
operator|.
name|commit
argument_list|()
expr_stmt|;
name|txn
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|index
operator|!=
literal|null
condition|)
name|index
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|blocks
operator|!=
literal|null
condition|)
name|blocks
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|env
operator|!=
literal|null
condition|)
name|env
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testBytes
specifier|public
name|void
name|testBytes
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|count
init|=
literal|250
decl_stmt|;
specifier|final
name|int
name|LENGTH_MASK
init|=
literal|0xffff
decl_stmt|;
name|Random
name|gen
init|=
operator|new
name|Random
argument_list|(
literal|1251971
argument_list|)
decl_stmt|;
name|int
name|totalLength
init|=
literal|0
decl_stmt|;
name|int
name|duration
decl_stmt|;
name|Date
name|end
decl_stmt|;
name|Date
name|veryStart
init|=
operator|new
name|Date
argument_list|()
decl_stmt|;
name|Date
name|start
init|=
operator|new
name|Date
argument_list|()
decl_stmt|;
name|Transaction
name|txn
init|=
literal|null
decl_stmt|;
name|Directory
name|store
init|=
literal|null
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Writing files byte by byte"
argument_list|)
expr_stmt|;
try|try
block|{
name|txn
operator|=
name|env
operator|.
name|beginTransaction
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|store
operator|=
operator|new
name|DbDirectory
argument_list|(
name|txn
argument_list|,
name|index
argument_list|,
name|blocks
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|String
name|name
init|=
name|i
operator|+
literal|".dat"
decl_stmt|;
name|int
name|length
init|=
name|gen
operator|.
name|nextInt
argument_list|()
operator|&
name|LENGTH_MASK
decl_stmt|;
name|IndexOutput
name|file
init|=
name|store
operator|.
name|createOutput
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|totalLength
operator|+=
name|length
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|length
condition|;
name|j
operator|++
control|)
block|{
name|byte
name|b
init|=
call|(
name|byte
call|)
argument_list|(
name|gen
operator|.
name|nextInt
argument_list|()
operator|&
literal|0x7F
argument_list|)
decl_stmt|;
name|file
operator|.
name|writeByte
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
name|file
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
name|txn
operator|.
name|abort
argument_list|()
expr_stmt|;
name|txn
operator|=
literal|null
expr_stmt|;
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|DatabaseException
name|e
parameter_list|)
block|{
if|if
condition|(
name|txn
operator|!=
literal|null
condition|)
block|{
name|txn
operator|.
name|abort
argument_list|()
expr_stmt|;
name|txn
operator|=
literal|null
expr_stmt|;
block|}
throw|throw
name|e
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|txn
operator|!=
literal|null
condition|)
name|txn
operator|.
name|commit
argument_list|()
expr_stmt|;
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|end
operator|=
operator|new
name|Date
argument_list|()
expr_stmt|;
name|duration
operator|=
call|(
name|int
call|)
argument_list|(
name|end
operator|.
name|getTime
argument_list|()
operator|-
name|start
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
name|duration
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|" total milliseconds to create, "
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
name|totalLength
operator|/
name|duration
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|" kb/s"
argument_list|)
expr_stmt|;
try|try
block|{
name|txn
operator|=
name|env
operator|.
name|beginTransaction
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|store
operator|=
operator|new
name|DbDirectory
argument_list|(
name|txn
argument_list|,
name|index
argument_list|,
name|blocks
argument_list|)
expr_stmt|;
name|gen
operator|=
operator|new
name|Random
argument_list|(
literal|1251971
argument_list|)
expr_stmt|;
name|start
operator|=
operator|new
name|Date
argument_list|()
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|String
name|name
init|=
name|i
operator|+
literal|".dat"
decl_stmt|;
name|int
name|length
init|=
name|gen
operator|.
name|nextInt
argument_list|()
operator|&
name|LENGTH_MASK
decl_stmt|;
name|IndexInput
name|file
init|=
name|store
operator|.
name|openInput
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|file
operator|.
name|length
argument_list|()
operator|!=
name|length
condition|)
throw|throw
operator|new
name|Exception
argument_list|(
literal|"length incorrect"
argument_list|)
throw|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|length
condition|;
name|j
operator|++
control|)
block|{
name|byte
name|b
init|=
call|(
name|byte
call|)
argument_list|(
name|gen
operator|.
name|nextInt
argument_list|()
operator|&
literal|0x7F
argument_list|)
decl_stmt|;
if|if
condition|(
name|file
operator|.
name|readByte
argument_list|()
operator|!=
name|b
condition|)
throw|throw
operator|new
name|Exception
argument_list|(
literal|"contents incorrect"
argument_list|)
throw|;
block|}
name|file
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
name|txn
operator|.
name|abort
argument_list|()
expr_stmt|;
name|txn
operator|=
literal|null
expr_stmt|;
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|DatabaseException
name|e
parameter_list|)
block|{
if|if
condition|(
name|txn
operator|!=
literal|null
condition|)
block|{
name|txn
operator|.
name|abort
argument_list|()
expr_stmt|;
name|txn
operator|=
literal|null
expr_stmt|;
block|}
throw|throw
name|e
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|txn
operator|!=
literal|null
condition|)
name|txn
operator|.
name|commit
argument_list|()
expr_stmt|;
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|end
operator|=
operator|new
name|Date
argument_list|()
expr_stmt|;
name|duration
operator|=
call|(
name|int
call|)
argument_list|(
name|end
operator|.
name|getTime
argument_list|()
operator|-
name|start
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
name|duration
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|" total milliseconds to read, "
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
name|totalLength
operator|/
name|duration
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|" kb/s"
argument_list|)
expr_stmt|;
try|try
block|{
name|txn
operator|=
name|env
operator|.
name|beginTransaction
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|store
operator|=
operator|new
name|DbDirectory
argument_list|(
name|txn
argument_list|,
name|index
argument_list|,
name|blocks
argument_list|)
expr_stmt|;
name|gen
operator|=
operator|new
name|Random
argument_list|(
literal|1251971
argument_list|)
expr_stmt|;
name|start
operator|=
operator|new
name|Date
argument_list|()
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|String
name|name
init|=
name|i
operator|+
literal|".dat"
decl_stmt|;
name|store
operator|.
name|deleteFile
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|txn
operator|.
name|abort
argument_list|()
expr_stmt|;
name|txn
operator|=
literal|null
expr_stmt|;
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|DatabaseException
name|e
parameter_list|)
block|{
if|if
condition|(
name|txn
operator|!=
literal|null
condition|)
block|{
name|txn
operator|.
name|abort
argument_list|()
expr_stmt|;
name|txn
operator|=
literal|null
expr_stmt|;
block|}
throw|throw
name|e
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|txn
operator|!=
literal|null
condition|)
name|txn
operator|.
name|commit
argument_list|()
expr_stmt|;
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|end
operator|=
operator|new
name|Date
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
name|end
operator|.
name|getTime
argument_list|()
operator|-
name|start
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|" total milliseconds to delete"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
name|end
operator|.
name|getTime
argument_list|()
operator|-
name|veryStart
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|" total milliseconds"
argument_list|)
expr_stmt|;
block|}
DECL|method|testArrays
specifier|public
name|void
name|testArrays
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|count
init|=
literal|250
decl_stmt|;
specifier|final
name|int
name|LENGTH_MASK
init|=
literal|0xffff
decl_stmt|;
name|Random
name|gen
init|=
operator|new
name|Random
argument_list|(
literal|1251971
argument_list|)
decl_stmt|;
name|int
name|totalLength
init|=
literal|0
decl_stmt|;
name|int
name|duration
decl_stmt|;
name|Date
name|end
decl_stmt|;
name|Date
name|veryStart
init|=
operator|new
name|Date
argument_list|()
decl_stmt|;
name|Date
name|start
init|=
operator|new
name|Date
argument_list|()
decl_stmt|;
name|Transaction
name|txn
init|=
literal|null
decl_stmt|;
name|Directory
name|store
init|=
literal|null
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Writing files as one byte array"
argument_list|)
expr_stmt|;
try|try
block|{
name|txn
operator|=
name|env
operator|.
name|beginTransaction
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|store
operator|=
operator|new
name|DbDirectory
argument_list|(
name|txn
argument_list|,
name|index
argument_list|,
name|blocks
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|String
name|name
init|=
name|i
operator|+
literal|".dat"
decl_stmt|;
name|int
name|length
init|=
name|gen
operator|.
name|nextInt
argument_list|()
operator|&
name|LENGTH_MASK
decl_stmt|;
name|IndexOutput
name|file
init|=
name|store
operator|.
name|createOutput
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|length
index|]
decl_stmt|;
name|totalLength
operator|+=
name|length
expr_stmt|;
name|gen
operator|.
name|nextBytes
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|file
operator|.
name|writeBytes
argument_list|(
name|data
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|file
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
name|txn
operator|.
name|abort
argument_list|()
expr_stmt|;
name|txn
operator|=
literal|null
expr_stmt|;
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|DatabaseException
name|e
parameter_list|)
block|{
if|if
condition|(
name|txn
operator|!=
literal|null
condition|)
block|{
name|txn
operator|.
name|abort
argument_list|()
expr_stmt|;
name|txn
operator|=
literal|null
expr_stmt|;
block|}
throw|throw
name|e
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|txn
operator|!=
literal|null
condition|)
name|txn
operator|.
name|commit
argument_list|()
expr_stmt|;
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|end
operator|=
operator|new
name|Date
argument_list|()
expr_stmt|;
name|duration
operator|=
call|(
name|int
call|)
argument_list|(
name|end
operator|.
name|getTime
argument_list|()
operator|-
name|start
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
name|duration
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|" total milliseconds to create, "
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
name|totalLength
operator|/
name|duration
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|" kb/s"
argument_list|)
expr_stmt|;
try|try
block|{
name|txn
operator|=
name|env
operator|.
name|beginTransaction
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|store
operator|=
operator|new
name|DbDirectory
argument_list|(
name|txn
argument_list|,
name|index
argument_list|,
name|blocks
argument_list|)
expr_stmt|;
name|gen
operator|=
operator|new
name|Random
argument_list|(
literal|1251971
argument_list|)
expr_stmt|;
name|start
operator|=
operator|new
name|Date
argument_list|()
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|String
name|name
init|=
name|i
operator|+
literal|".dat"
decl_stmt|;
name|int
name|length
init|=
name|gen
operator|.
name|nextInt
argument_list|()
operator|&
name|LENGTH_MASK
decl_stmt|;
name|IndexInput
name|file
init|=
name|store
operator|.
name|openInput
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|file
operator|.
name|length
argument_list|()
operator|!=
name|length
condition|)
throw|throw
operator|new
name|Exception
argument_list|(
literal|"length incorrect"
argument_list|)
throw|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|length
index|]
decl_stmt|;
name|byte
index|[]
name|read
init|=
operator|new
name|byte
index|[
name|length
index|]
decl_stmt|;
name|gen
operator|.
name|nextBytes
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|file
operator|.
name|readBytes
argument_list|(
name|read
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|Arrays
operator|.
name|equals
argument_list|(
name|data
argument_list|,
name|read
argument_list|)
condition|)
throw|throw
operator|new
name|Exception
argument_list|(
literal|"contents incorrect"
argument_list|)
throw|;
name|file
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
name|txn
operator|.
name|abort
argument_list|()
expr_stmt|;
name|txn
operator|=
literal|null
expr_stmt|;
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|DatabaseException
name|e
parameter_list|)
block|{
if|if
condition|(
name|txn
operator|!=
literal|null
condition|)
block|{
name|txn
operator|.
name|abort
argument_list|()
expr_stmt|;
name|txn
operator|=
literal|null
expr_stmt|;
block|}
throw|throw
name|e
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|txn
operator|!=
literal|null
condition|)
name|txn
operator|.
name|commit
argument_list|()
expr_stmt|;
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|end
operator|=
operator|new
name|Date
argument_list|()
expr_stmt|;
name|duration
operator|=
call|(
name|int
call|)
argument_list|(
name|end
operator|.
name|getTime
argument_list|()
operator|-
name|start
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
name|duration
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|" total milliseconds to read, "
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
name|totalLength
operator|/
name|duration
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|" kb/s"
argument_list|)
expr_stmt|;
try|try
block|{
name|txn
operator|=
name|env
operator|.
name|beginTransaction
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|store
operator|=
operator|new
name|DbDirectory
argument_list|(
name|txn
argument_list|,
name|index
argument_list|,
name|blocks
argument_list|)
expr_stmt|;
name|gen
operator|=
operator|new
name|Random
argument_list|(
literal|1251971
argument_list|)
expr_stmt|;
name|start
operator|=
operator|new
name|Date
argument_list|()
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|String
name|name
init|=
name|i
operator|+
literal|".dat"
decl_stmt|;
name|store
operator|.
name|deleteFile
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|txn
operator|.
name|abort
argument_list|()
expr_stmt|;
name|txn
operator|=
literal|null
expr_stmt|;
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|DatabaseException
name|e
parameter_list|)
block|{
if|if
condition|(
name|txn
operator|!=
literal|null
condition|)
block|{
name|txn
operator|.
name|abort
argument_list|()
expr_stmt|;
name|txn
operator|=
literal|null
expr_stmt|;
block|}
throw|throw
name|e
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|txn
operator|!=
literal|null
condition|)
name|txn
operator|.
name|commit
argument_list|()
expr_stmt|;
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|end
operator|=
operator|new
name|Date
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
name|end
operator|.
name|getTime
argument_list|()
operator|-
name|start
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|" total milliseconds to delete"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
name|end
operator|.
name|getTime
argument_list|()
operator|-
name|veryStart
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|" total milliseconds"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

