begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|Socket
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
name|Paths
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|SuppressForbidden
import|;
end_import

begin_comment
comment|/**  * Simple standalone tool that forever acquires and releases a  * lock using a specific LockFactory.  Run without any args  * to see usage.  *  * @see VerifyingLockFactory  * @see LockVerifyServer  */
end_comment

begin_class
DECL|class|LockStressTest
specifier|public
class|class
name|LockStressTest
block|{
DECL|field|LOCK_FILE_NAME
specifier|static
specifier|final
name|String
name|LOCK_FILE_NAME
init|=
literal|"test.lock"
decl_stmt|;
annotation|@
name|SuppressForbidden
argument_list|(
name|reason
operator|=
literal|"System.out required: command line tool"
argument_list|)
annotation|@
name|SuppressWarnings
argument_list|(
literal|"try"
argument_list|)
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|args
operator|.
name|length
operator|!=
literal|7
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Usage: java org.apache.lucene.store.LockStressTest myID verifierHost verifierPort lockFactoryClassName lockDirName sleepTimeMS count\n"
operator|+
literal|"\n"
operator|+
literal|"  myID = int from 0 .. 255 (should be unique for test process)\n"
operator|+
literal|"  verifierHost = hostname that LockVerifyServer is listening on\n"
operator|+
literal|"  verifierPort = port that LockVerifyServer is listening on\n"
operator|+
literal|"  lockFactoryClassName = primary FSLockFactory class that we will use\n"
operator|+
literal|"  lockDirName = path to the lock directory\n"
operator|+
literal|"  sleepTimeMS = milliseconds to pause betweeen each lock obtain/release\n"
operator|+
literal|"  count = number of locking tries\n"
operator|+
literal|"\n"
operator|+
literal|"You should run multiple instances of this process, each with its own\n"
operator|+
literal|"unique ID, and each pointing to the same lock directory, to verify\n"
operator|+
literal|"that locking is working correctly.\n"
operator|+
literal|"\n"
operator|+
literal|"Make sure you are first running LockVerifyServer."
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|int
name|arg
init|=
literal|0
decl_stmt|;
specifier|final
name|int
name|myID
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|args
index|[
name|arg
operator|++
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|myID
argument_list|<
literal|0
operator|||
name|myID
argument_list|>
literal|255
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"myID must be a unique int 0..255"
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
specifier|final
name|String
name|verifierHost
init|=
name|args
index|[
name|arg
operator|++
index|]
decl_stmt|;
specifier|final
name|int
name|verifierPort
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|args
index|[
name|arg
operator|++
index|]
argument_list|)
decl_stmt|;
specifier|final
name|String
name|lockFactoryClassName
init|=
name|args
index|[
name|arg
operator|++
index|]
decl_stmt|;
specifier|final
name|Path
name|lockDirPath
init|=
name|Paths
operator|.
name|get
argument_list|(
name|args
index|[
name|arg
operator|++
index|]
argument_list|)
decl_stmt|;
specifier|final
name|int
name|sleepTimeMS
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|args
index|[
name|arg
operator|++
index|]
argument_list|)
decl_stmt|;
specifier|final
name|int
name|count
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|args
index|[
name|arg
operator|++
index|]
argument_list|)
decl_stmt|;
specifier|final
name|LockFactory
name|lockFactory
init|=
name|getNewLockFactory
argument_list|(
name|lockFactoryClassName
argument_list|)
decl_stmt|;
comment|// we test the lock factory directly, so we don't need it on the directory itsself (the directory is just for testing)
specifier|final
name|FSDirectory
name|lockDir
init|=
operator|new
name|SimpleFSDirectory
argument_list|(
name|lockDirPath
argument_list|,
name|NoLockFactory
operator|.
name|INSTANCE
argument_list|)
decl_stmt|;
specifier|final
name|InetSocketAddress
name|addr
init|=
operator|new
name|InetSocketAddress
argument_list|(
name|verifierHost
argument_list|,
name|verifierPort
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Connecting to server "
operator|+
name|addr
operator|+
literal|" and registering as client "
operator|+
name|myID
operator|+
literal|"..."
argument_list|)
expr_stmt|;
try|try
init|(
name|Socket
name|socket
init|=
operator|new
name|Socket
argument_list|()
init|)
block|{
name|socket
operator|.
name|setReuseAddress
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|socket
operator|.
name|connect
argument_list|(
name|addr
argument_list|,
literal|500
argument_list|)
expr_stmt|;
specifier|final
name|OutputStream
name|out
init|=
name|socket
operator|.
name|getOutputStream
argument_list|()
decl_stmt|;
specifier|final
name|InputStream
name|in
init|=
name|socket
operator|.
name|getInputStream
argument_list|()
decl_stmt|;
name|out
operator|.
name|write
argument_list|(
name|myID
argument_list|)
expr_stmt|;
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
name|LockFactory
name|verifyLF
init|=
operator|new
name|VerifyingLockFactory
argument_list|(
name|lockFactory
argument_list|,
name|in
argument_list|,
name|out
argument_list|)
decl_stmt|;
specifier|final
name|Random
name|rnd
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
comment|// wait for starting gun
if|if
condition|(
name|in
operator|.
name|read
argument_list|()
operator|!=
literal|43
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Protocol violation"
argument_list|)
throw|;
block|}
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
try|try
init|(
specifier|final
name|Lock
name|l
init|=
name|verifyLF
operator|.
name|obtainLock
argument_list|(
name|lockDir
argument_list|,
name|LOCK_FILE_NAME
argument_list|)
init|)
block|{
if|if
condition|(
name|rnd
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|rnd
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|verifyLF
operator|=
operator|new
name|VerifyingLockFactory
argument_list|(
name|getNewLockFactory
argument_list|(
name|lockFactoryClassName
argument_list|)
argument_list|,
name|in
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
try|try
init|(
specifier|final
name|Lock
name|secondLock
init|=
name|verifyLF
operator|.
name|obtainLock
argument_list|(
name|lockDir
argument_list|,
name|LOCK_FILE_NAME
argument_list|)
init|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Double obtain"
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|LockObtainFailedException
name|loe
parameter_list|)
block|{
comment|// pass
block|}
block|}
name|Thread
operator|.
name|sleep
argument_list|(
name|sleepTimeMS
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|LockObtainFailedException
name|loe
parameter_list|)
block|{
comment|// obtain failed
block|}
if|if
condition|(
name|i
operator|%
literal|500
operator|==
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
operator|(
name|i
operator|*
literal|100.
operator|/
name|count
operator|)
operator|+
literal|"% done."
argument_list|)
expr_stmt|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
name|sleepTimeMS
argument_list|)
expr_stmt|;
block|}
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Finished "
operator|+
name|count
operator|+
literal|" tries."
argument_list|)
expr_stmt|;
block|}
DECL|method|getNewLockFactory
specifier|private
specifier|static
name|FSLockFactory
name|getNewLockFactory
parameter_list|(
name|String
name|lockFactoryClassName
parameter_list|)
throws|throws
name|IOException
block|{
comment|// try to get static INSTANCE field of class
try|try
block|{
return|return
operator|(
name|FSLockFactory
operator|)
name|Class
operator|.
name|forName
argument_list|(
name|lockFactoryClassName
argument_list|)
operator|.
name|getField
argument_list|(
literal|"INSTANCE"
argument_list|)
operator|.
name|get
argument_list|(
literal|null
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ReflectiveOperationException
name|e
parameter_list|)
block|{
comment|// fall-through
block|}
comment|// try to create a new instance
try|try
block|{
return|return
name|Class
operator|.
name|forName
argument_list|(
name|lockFactoryClassName
argument_list|)
operator|.
name|asSubclass
argument_list|(
name|FSLockFactory
operator|.
name|class
argument_list|)
operator|.
name|newInstance
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|ReflectiveOperationException
decl||
name|ClassCastException
name|e
parameter_list|)
block|{
comment|// fall-through
block|}
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Cannot get lock factory singleton of "
operator|+
name|lockFactoryClassName
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

