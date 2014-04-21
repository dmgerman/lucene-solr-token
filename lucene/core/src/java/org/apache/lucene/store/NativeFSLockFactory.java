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
name|nio
operator|.
name|channels
operator|.
name|FileChannel
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|FileLock
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|OverlappingFileLockException
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
name|StandardOpenOption
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
comment|/**  *<p>Implements {@link LockFactory} using native OS file  * locks.  Note that because this LockFactory relies on  * java.nio.* APIs for locking, any problems with those APIs  * will cause locking to fail.  Specifically, on certain NFS  * environments the java.nio.* locks will fail (the lock can  * incorrectly be double acquired) whereas {@link  * SimpleFSLockFactory} worked perfectly in those same  * environments.  For NFS based access to an index, it's  * recommended that you try {@link SimpleFSLockFactory}  * first and work around the one limitation that a lock file  * could be left when the JVM exits abnormally.</p>  *  *<p>The primary benefit of {@link NativeFSLockFactory} is  * that lock files will be properly removed (by the OS) if  * the JVM has an abnormal exit.</p>  *   *<p>Note that, unlike {@link SimpleFSLockFactory}, the existence of  * leftover lock files in the filesystem on exiting the JVM  * is fine because the OS will free the locks held against  * these files even though the files still remain.</p>  *  *<p>If you suspect that this or any other LockFactory is  * not working properly in your environment, you can easily  * test it by using {@link VerifyingLockFactory}, {@link  * LockVerifyServer} and {@link LockStressTest}.</p>  *  * @see LockFactory  */
end_comment

begin_class
DECL|class|NativeFSLockFactory
specifier|public
class|class
name|NativeFSLockFactory
extends|extends
name|FSLockFactory
block|{
comment|/**    * Create a NativeFSLockFactory instance, with null (unset)    * lock directory. When you pass this factory to a {@link FSDirectory}    * subclass, the lock directory is automatically set to the    * directory itself. Be sure to create one instance for each directory    * your create!    */
DECL|method|NativeFSLockFactory
specifier|public
name|NativeFSLockFactory
parameter_list|()
block|{
name|this
argument_list|(
operator|(
name|File
operator|)
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a NativeFSLockFactory instance, storing lock    * files into the specified lockDirName:    *    * @param lockDirName where lock files are created.    */
DECL|method|NativeFSLockFactory
specifier|public
name|NativeFSLockFactory
parameter_list|(
name|String
name|lockDirName
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|File
argument_list|(
name|lockDirName
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a NativeFSLockFactory instance, storing lock    * files into the specified lockDir:    *     * @param lockDir where lock files are created.    */
DECL|method|NativeFSLockFactory
specifier|public
name|NativeFSLockFactory
parameter_list|(
name|File
name|lockDir
parameter_list|)
block|{
name|setLockDir
argument_list|(
name|lockDir
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|makeLock
specifier|public
specifier|synchronized
name|Lock
name|makeLock
parameter_list|(
name|String
name|lockName
parameter_list|)
block|{
if|if
condition|(
name|lockPrefix
operator|!=
literal|null
condition|)
name|lockName
operator|=
name|lockPrefix
operator|+
literal|"-"
operator|+
name|lockName
expr_stmt|;
return|return
operator|new
name|NativeFSLock
argument_list|(
name|lockDir
argument_list|,
name|lockName
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|clearLock
specifier|public
name|void
name|clearLock
parameter_list|(
name|String
name|lockName
parameter_list|)
throws|throws
name|IOException
block|{
name|makeLock
argument_list|(
name|lockName
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

begin_class
DECL|class|NativeFSLock
class|class
name|NativeFSLock
extends|extends
name|Lock
block|{
DECL|field|channel
specifier|private
name|FileChannel
name|channel
decl_stmt|;
DECL|field|lock
specifier|private
name|FileLock
name|lock
decl_stmt|;
DECL|field|path
specifier|private
name|File
name|path
decl_stmt|;
DECL|field|lockDir
specifier|private
name|File
name|lockDir
decl_stmt|;
DECL|method|NativeFSLock
specifier|public
name|NativeFSLock
parameter_list|(
name|File
name|lockDir
parameter_list|,
name|String
name|lockFileName
parameter_list|)
block|{
name|this
operator|.
name|lockDir
operator|=
name|lockDir
expr_stmt|;
name|path
operator|=
operator|new
name|File
argument_list|(
name|lockDir
argument_list|,
name|lockFileName
argument_list|)
expr_stmt|;
block|}
DECL|method|lockExists
specifier|private
specifier|synchronized
name|boolean
name|lockExists
parameter_list|()
block|{
return|return
name|lock
operator|!=
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|obtain
specifier|public
specifier|synchronized
name|boolean
name|obtain
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|lockExists
argument_list|()
condition|)
block|{
comment|// Our instance is already locked:
return|return
literal|false
return|;
block|}
comment|// Ensure that lockDir exists and is a directory.
if|if
condition|(
operator|!
name|lockDir
operator|.
name|exists
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|lockDir
operator|.
name|mkdirs
argument_list|()
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Cannot create directory: "
operator|+
name|lockDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
operator|!
name|lockDir
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
comment|// TODO: NoSuchDirectoryException instead?
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Found regular file where directory expected: "
operator|+
name|lockDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
throw|;
block|}
name|channel
operator|=
name|FileChannel
operator|.
name|open
argument_list|(
name|path
operator|.
name|toPath
argument_list|()
argument_list|,
name|StandardOpenOption
operator|.
name|CREATE
argument_list|,
name|StandardOpenOption
operator|.
name|WRITE
argument_list|)
expr_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|lock
operator|=
name|channel
operator|.
name|tryLock
argument_list|()
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|OverlappingFileLockException
name|e
parameter_list|)
block|{
comment|// At least on OS X, we will sometimes get an
comment|// intermittent "Permission Denied" IOException,
comment|// which seems to simply mean "you failed to get
comment|// the lock".  But other IOExceptions could be
comment|// "permanent" (eg, locking is not supported via
comment|// the filesystem).  So, we record the failure
comment|// reason here; the timeout obtain (usually the
comment|// one calling us) will use this as "root cause"
comment|// if it fails to get the lock.
name|failureReason
operator|=
name|e
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
try|try
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|channel
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|channel
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
return|return
name|lockExists
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|lockExists
argument_list|()
condition|)
block|{
try|try
block|{
name|lock
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|lock
operator|=
literal|null
expr_stmt|;
try|try
block|{
name|channel
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|channel
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
comment|// if we don't hold the lock, and somebody still called release(), for
comment|// example as a result of calling IndexWriter.unlock(), we should attempt
comment|// to obtain the lock and release it. If the obtain fails, it means the
comment|// lock cannot be released, and we should throw a proper exception rather
comment|// than silently failing/not doing anything.
name|boolean
name|obtained
init|=
literal|false
decl_stmt|;
try|try
block|{
if|if
condition|(
operator|!
operator|(
name|obtained
operator|=
name|obtain
argument_list|()
operator|)
condition|)
block|{
throw|throw
operator|new
name|LockReleaseFailedException
argument_list|(
literal|"Cannot forcefully unlock a NativeFSLock which is held by another indexer component: "
operator|+
name|path
argument_list|)
throw|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|obtained
condition|)
block|{
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|isLocked
specifier|public
specifier|synchronized
name|boolean
name|isLocked
parameter_list|()
block|{
comment|// The test for is isLocked is not directly possible with native file locks:
comment|// First a shortcut, if a lock reference in this instance is available
if|if
condition|(
name|lockExists
argument_list|()
condition|)
return|return
literal|true
return|;
comment|// Look if lock file is present; if not, there can definitely be no lock!
if|if
condition|(
operator|!
name|path
operator|.
name|exists
argument_list|()
condition|)
return|return
literal|false
return|;
comment|// Try to obtain and release (if was locked) the lock
try|try
block|{
name|boolean
name|obtained
init|=
name|obtain
argument_list|()
decl_stmt|;
if|if
condition|(
name|obtained
condition|)
name|close
argument_list|()
expr_stmt|;
return|return
operator|!
name|obtained
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
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
literal|"NativeFSLock@"
operator|+
name|path
return|;
block|}
block|}
end_class

end_unit

