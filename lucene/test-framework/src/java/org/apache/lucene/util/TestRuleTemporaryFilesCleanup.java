begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
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
name|FileSystem
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
name|FileSystems
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
name|Files
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
name|spi
operator|.
name|FileSystemProvider
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
name|Arrays
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
name|Locale
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
name|Set
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
name|mockfile
operator|.
name|DisableFsyncFS
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
name|mockfile
operator|.
name|ExtrasFS
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
name|mockfile
operator|.
name|HandleLimitFS
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
name|mockfile
operator|.
name|LeakFS
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
name|mockfile
operator|.
name|ShuffleFS
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
name|mockfile
operator|.
name|VerboseFS
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
name|mockfile
operator|.
name|VirusCheckingFS
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
name|mockfile
operator|.
name|WindowsFS
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
name|LuceneTestCase
operator|.
name|SuppressFileSystems
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
name|LuceneTestCase
operator|.
name|SuppressFsync
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
name|LuceneTestCase
operator|.
name|SuppressTempFileChecks
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|RandomizedContext
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|rules
operator|.
name|TestRuleAdapter
import|;
end_import

begin_comment
comment|/**  * Checks and cleans up temporary files.  *   * @see LuceneTestCase#createTempDir()  * @see LuceneTestCase#createTempFile()  */
end_comment

begin_class
DECL|class|TestRuleTemporaryFilesCleanup
specifier|final
class|class
name|TestRuleTemporaryFilesCleanup
extends|extends
name|TestRuleAdapter
block|{
comment|/**    * Retry to create temporary file name this many times.    */
DECL|field|TEMP_NAME_RETRY_THRESHOLD
specifier|private
specifier|static
specifier|final
name|int
name|TEMP_NAME_RETRY_THRESHOLD
init|=
literal|9999
decl_stmt|;
comment|/**    * Writeable temporary base folder.     */
DECL|field|javaTempDir
specifier|private
name|Path
name|javaTempDir
decl_stmt|;
comment|/**    * Per-test class temporary folder.    */
DECL|field|tempDirBase
specifier|private
name|Path
name|tempDirBase
decl_stmt|;
comment|/**    * Per-test filesystem    */
DECL|field|fileSystem
specifier|private
name|FileSystem
name|fileSystem
decl_stmt|;
comment|/**    * Only set if the file system chain includes the VirusCheckingFS    */
DECL|field|virusCheckingFS
specifier|private
name|VirusCheckingFS
name|virusCheckingFS
decl_stmt|;
comment|/**    * Suite failure marker.    */
DECL|field|failureMarker
specifier|private
specifier|final
name|TestRuleMarkFailure
name|failureMarker
decl_stmt|;
comment|/**    * A queue of temporary resources to be removed after the    * suite completes.    * @see #registerToRemoveAfterSuite(Path)    */
DECL|field|cleanupQueue
specifier|private
specifier|final
specifier|static
name|List
argument_list|<
name|Path
argument_list|>
name|cleanupQueue
init|=
operator|new
name|ArrayList
argument_list|<
name|Path
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|TestRuleTemporaryFilesCleanup
specifier|public
name|TestRuleTemporaryFilesCleanup
parameter_list|(
name|TestRuleMarkFailure
name|failureMarker
parameter_list|)
block|{
name|this
operator|.
name|failureMarker
operator|=
name|failureMarker
expr_stmt|;
block|}
comment|/**    * Register temporary folder for removal after the suite completes.    */
DECL|method|registerToRemoveAfterSuite
name|void
name|registerToRemoveAfterSuite
parameter_list|(
name|Path
name|f
parameter_list|)
block|{
assert|assert
name|f
operator|!=
literal|null
assert|;
if|if
condition|(
name|LuceneTestCase
operator|.
name|LEAVE_TEMPORARY
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"INFO: Will leave temporary file: "
operator|+
name|f
operator|.
name|toAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
synchronized|synchronized
init|(
name|cleanupQueue
init|)
block|{
name|cleanupQueue
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|before
specifier|protected
name|void
name|before
parameter_list|()
throws|throws
name|Throwable
block|{
name|super
operator|.
name|before
argument_list|()
expr_stmt|;
assert|assert
name|tempDirBase
operator|==
literal|null
assert|;
name|fileSystem
operator|=
name|initializeFileSystem
argument_list|()
expr_stmt|;
name|javaTempDir
operator|=
name|initializeJavaTempDir
argument_list|()
expr_stmt|;
block|}
comment|// os/config-independent limit for too many open files
comment|// TODO: can we make this lower?
DECL|field|MAX_OPEN_FILES
specifier|private
specifier|static
specifier|final
name|int
name|MAX_OPEN_FILES
init|=
literal|2048
decl_stmt|;
DECL|method|allowed
specifier|private
name|boolean
name|allowed
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|avoid
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|FileSystemProvider
argument_list|>
name|clazz
parameter_list|)
block|{
if|if
condition|(
name|avoid
operator|.
name|contains
argument_list|(
literal|"*"
argument_list|)
operator|||
name|avoid
operator|.
name|contains
argument_list|(
name|clazz
operator|.
name|getSimpleName
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
return|return
literal|true
return|;
block|}
block|}
DECL|method|initializeFileSystem
specifier|private
name|FileSystem
name|initializeFileSystem
parameter_list|()
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|targetClass
init|=
name|RandomizedContext
operator|.
name|current
argument_list|()
operator|.
name|getTargetClass
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|avoid
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|targetClass
operator|.
name|isAnnotationPresent
argument_list|(
name|SuppressFileSystems
operator|.
name|class
argument_list|)
condition|)
block|{
name|SuppressFileSystems
name|a
init|=
name|targetClass
operator|.
name|getAnnotation
argument_list|(
name|SuppressFileSystems
operator|.
name|class
argument_list|)
decl_stmt|;
name|avoid
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|a
operator|.
name|value
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|FileSystem
name|fs
init|=
name|FileSystems
operator|.
name|getDefault
argument_list|()
decl_stmt|;
if|if
condition|(
name|LuceneTestCase
operator|.
name|VERBOSE
operator|&&
name|allowed
argument_list|(
name|avoid
argument_list|,
name|VerboseFS
operator|.
name|class
argument_list|)
condition|)
block|{
name|fs
operator|=
operator|new
name|VerboseFS
argument_list|(
name|fs
argument_list|,
operator|new
name|TestRuleSetupAndRestoreClassEnv
operator|.
name|ThreadNameFixingPrintStreamInfoStream
argument_list|(
name|System
operator|.
name|out
argument_list|)
argument_list|)
operator|.
name|getFileSystem
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
name|Random
name|random
init|=
name|RandomizedContext
operator|.
name|current
argument_list|()
operator|.
name|getRandom
argument_list|()
decl_stmt|;
comment|// speed up tests by omitting actual fsync calls to the hardware most of the time.
if|if
condition|(
name|targetClass
operator|.
name|isAnnotationPresent
argument_list|(
name|SuppressFsync
operator|.
name|class
argument_list|)
operator|||
name|random
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|allowed
argument_list|(
name|avoid
argument_list|,
name|DisableFsyncFS
operator|.
name|class
argument_list|)
condition|)
block|{
name|fs
operator|=
operator|new
name|DisableFsyncFS
argument_list|(
name|fs
argument_list|)
operator|.
name|getFileSystem
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
comment|// impacts test reproducibility across platforms.
if|if
condition|(
name|random
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|allowed
argument_list|(
name|avoid
argument_list|,
name|ShuffleFS
operator|.
name|class
argument_list|)
condition|)
block|{
name|fs
operator|=
operator|new
name|ShuffleFS
argument_list|(
name|fs
argument_list|,
name|random
operator|.
name|nextLong
argument_list|()
argument_list|)
operator|.
name|getFileSystem
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
comment|// otherwise, wrap with mockfilesystems for additional checks. some
comment|// of these have side effects (e.g. concurrency) so it doesn't always happen.
if|if
condition|(
name|random
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|allowed
argument_list|(
name|avoid
argument_list|,
name|LeakFS
operator|.
name|class
argument_list|)
condition|)
block|{
name|fs
operator|=
operator|new
name|LeakFS
argument_list|(
name|fs
argument_list|)
operator|.
name|getFileSystem
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|allowed
argument_list|(
name|avoid
argument_list|,
name|HandleLimitFS
operator|.
name|class
argument_list|)
condition|)
block|{
name|fs
operator|=
operator|new
name|HandleLimitFS
argument_list|(
name|fs
argument_list|,
name|MAX_OPEN_FILES
argument_list|)
operator|.
name|getFileSystem
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
comment|// windows is currently slow
if|if
condition|(
name|random
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
operator|==
literal|0
condition|)
block|{
comment|// don't try to emulate windows on windows: they don't get along
if|if
condition|(
operator|!
name|Constants
operator|.
name|WINDOWS
operator|&&
name|allowed
argument_list|(
name|avoid
argument_list|,
name|WindowsFS
operator|.
name|class
argument_list|)
condition|)
block|{
name|fs
operator|=
operator|new
name|WindowsFS
argument_list|(
name|fs
argument_list|)
operator|.
name|getFileSystem
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|allowed
argument_list|(
name|avoid
argument_list|,
name|ExtrasFS
operator|.
name|class
argument_list|)
condition|)
block|{
name|fs
operator|=
operator|new
name|ExtrasFS
argument_list|(
name|fs
argument_list|,
name|random
operator|.
name|nextInt
argument_list|(
literal|4
argument_list|)
operator|==
literal|0
argument_list|,
name|random
operator|.
name|nextBoolean
argument_list|()
argument_list|)
operator|.
name|getFileSystem
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
comment|// nocommit true:
if|if
condition|(
name|allowed
argument_list|(
name|avoid
argument_list|,
name|VirusCheckingFS
operator|.
name|class
argument_list|)
operator|&&
operator|(
literal|true
operator|||
name|random
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
operator|==
literal|1
operator|)
condition|)
block|{
comment|// 10% of the time we swap in virus checking (acts-like-windows) FS:
name|virusCheckingFS
operator|=
operator|new
name|VirusCheckingFS
argument_list|(
name|fs
argument_list|,
name|random
operator|.
name|nextLong
argument_list|()
argument_list|)
expr_stmt|;
name|fs
operator|=
name|virusCheckingFS
operator|.
name|getFileSystem
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|LuceneTestCase
operator|.
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"filesystem: "
operator|+
name|fs
operator|.
name|provider
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|fs
operator|.
name|provider
argument_list|()
operator|.
name|getFileSystem
argument_list|(
name|URI
operator|.
name|create
argument_list|(
literal|"file:///"
argument_list|)
argument_list|)
return|;
block|}
DECL|method|initializeJavaTempDir
specifier|private
name|Path
name|initializeJavaTempDir
parameter_list|()
throws|throws
name|IOException
block|{
name|Path
name|javaTempDir
init|=
name|fileSystem
operator|.
name|getPath
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"tempDir"
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.io.tmpdir"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|Files
operator|.
name|createDirectories
argument_list|(
name|javaTempDir
argument_list|)
expr_stmt|;
assert|assert
name|Files
operator|.
name|isDirectory
argument_list|(
name|javaTempDir
argument_list|)
operator|&&
name|Files
operator|.
name|isWritable
argument_list|(
name|javaTempDir
argument_list|)
assert|;
return|return
name|javaTempDir
operator|.
name|toRealPath
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|afterAlways
specifier|protected
name|void
name|afterAlways
parameter_list|(
name|List
argument_list|<
name|Throwable
argument_list|>
name|errors
parameter_list|)
throws|throws
name|Throwable
block|{
comment|// Drain cleanup queue and clear it.
specifier|final
name|Path
index|[]
name|everything
decl_stmt|;
specifier|final
name|String
name|tempDirBasePath
decl_stmt|;
synchronized|synchronized
init|(
name|cleanupQueue
init|)
block|{
name|tempDirBasePath
operator|=
operator|(
name|tempDirBase
operator|!=
literal|null
condition|?
name|tempDirBase
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
else|:
literal|null
operator|)
expr_stmt|;
name|tempDirBase
operator|=
literal|null
expr_stmt|;
name|Collections
operator|.
name|reverse
argument_list|(
name|cleanupQueue
argument_list|)
expr_stmt|;
name|everything
operator|=
operator|new
name|Path
index|[
name|cleanupQueue
operator|.
name|size
argument_list|()
index|]
expr_stmt|;
name|cleanupQueue
operator|.
name|toArray
argument_list|(
name|everything
argument_list|)
expr_stmt|;
name|cleanupQueue
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
comment|// Only check and throw an IOException on un-removable files if the test
comment|// was successful. Otherwise just report the path of temporary files
comment|// and leave them there.
if|if
condition|(
name|failureMarker
operator|.
name|wasSuccessful
argument_list|()
condition|)
block|{
if|if
condition|(
name|virusCheckingFS
operator|!=
literal|null
condition|)
block|{
comment|// Otherwise our IOUtils.rm below can fail:
name|virusCheckingFS
operator|.
name|disable
argument_list|()
expr_stmt|;
block|}
try|try
block|{
name|IOUtils
operator|.
name|rm
argument_list|(
name|everything
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|suiteClass
init|=
name|RandomizedContext
operator|.
name|current
argument_list|()
operator|.
name|getTargetClass
argument_list|()
decl_stmt|;
if|if
condition|(
name|suiteClass
operator|.
name|isAnnotationPresent
argument_list|(
name|SuppressTempFileChecks
operator|.
name|class
argument_list|)
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"WARNING: Leftover undeleted temporary files (bugUrl: "
operator|+
name|suiteClass
operator|.
name|getAnnotation
argument_list|(
name|SuppressTempFileChecks
operator|.
name|class
argument_list|)
operator|.
name|bugUrl
argument_list|()
operator|+
literal|"): "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
throw|throw
name|e
throw|;
block|}
if|if
condition|(
name|fileSystem
operator|!=
name|FileSystems
operator|.
name|getDefault
argument_list|()
condition|)
block|{
name|fileSystem
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|tempDirBasePath
operator|!=
literal|null
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"NOTE: leaving temporary files on disk at: "
operator|+
name|tempDirBasePath
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|getPerTestClassTempDir
name|Path
name|getPerTestClassTempDir
parameter_list|()
block|{
if|if
condition|(
name|tempDirBase
operator|==
literal|null
condition|)
block|{
name|RandomizedContext
name|ctx
init|=
name|RandomizedContext
operator|.
name|current
argument_list|()
decl_stmt|;
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
init|=
name|ctx
operator|.
name|getTargetClass
argument_list|()
decl_stmt|;
name|String
name|prefix
init|=
name|clazz
operator|.
name|getName
argument_list|()
decl_stmt|;
name|prefix
operator|=
name|prefix
operator|.
name|replaceFirst
argument_list|(
literal|"^org.apache.lucene."
argument_list|,
literal|"lucene."
argument_list|)
expr_stmt|;
name|prefix
operator|=
name|prefix
operator|.
name|replaceFirst
argument_list|(
literal|"^org.apache.solr."
argument_list|,
literal|"solr."
argument_list|)
expr_stmt|;
name|int
name|attempt
init|=
literal|0
decl_stmt|;
name|Path
name|f
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
do|do
block|{
if|if
condition|(
name|attempt
operator|++
operator|>=
name|TEMP_NAME_RETRY_THRESHOLD
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Failed to get a temporary name too many times, check your temp directory and consider manually cleaning it: "
operator|+
name|javaTempDir
operator|.
name|toAbsolutePath
argument_list|()
argument_list|)
throw|;
block|}
name|f
operator|=
name|javaTempDir
operator|.
name|resolve
argument_list|(
name|prefix
operator|+
literal|"_"
operator|+
name|ctx
operator|.
name|getRunnerSeedAsString
argument_list|()
operator|+
literal|"-"
operator|+
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|,
literal|"%03d"
argument_list|,
name|attempt
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|Files
operator|.
name|createDirectory
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ignore
parameter_list|)
block|{}
block|}
do|while
condition|(
operator|!
name|success
condition|)
do|;
name|tempDirBase
operator|=
name|f
expr_stmt|;
name|registerToRemoveAfterSuite
argument_list|(
name|tempDirBase
argument_list|)
expr_stmt|;
block|}
return|return
name|tempDirBase
return|;
block|}
comment|/**    * @see LuceneTestCase#createTempDir()    */
DECL|method|createTempDir
specifier|public
name|Path
name|createTempDir
parameter_list|(
name|String
name|prefix
parameter_list|)
block|{
name|Path
name|base
init|=
name|getPerTestClassTempDir
argument_list|()
decl_stmt|;
name|int
name|attempt
init|=
literal|0
decl_stmt|;
name|Path
name|f
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
do|do
block|{
if|if
condition|(
name|attempt
operator|++
operator|>=
name|TEMP_NAME_RETRY_THRESHOLD
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Failed to get a temporary name too many times, check your temp directory and consider manually cleaning it: "
operator|+
name|base
operator|.
name|toAbsolutePath
argument_list|()
argument_list|)
throw|;
block|}
name|f
operator|=
name|base
operator|.
name|resolve
argument_list|(
name|prefix
operator|+
literal|"-"
operator|+
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|,
literal|"%03d"
argument_list|,
name|attempt
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|Files
operator|.
name|createDirectory
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ignore
parameter_list|)
block|{}
block|}
do|while
condition|(
operator|!
name|success
condition|)
do|;
name|registerToRemoveAfterSuite
argument_list|(
name|f
argument_list|)
expr_stmt|;
return|return
name|f
return|;
block|}
comment|/**    * @see LuceneTestCase#createTempFile()    */
DECL|method|createTempFile
specifier|public
name|Path
name|createTempFile
parameter_list|(
name|String
name|prefix
parameter_list|,
name|String
name|suffix
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|base
init|=
name|getPerTestClassTempDir
argument_list|()
decl_stmt|;
name|int
name|attempt
init|=
literal|0
decl_stmt|;
name|Path
name|f
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
do|do
block|{
if|if
condition|(
name|attempt
operator|++
operator|>=
name|TEMP_NAME_RETRY_THRESHOLD
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Failed to get a temporary name too many times, check your temp directory and consider manually cleaning it: "
operator|+
name|base
operator|.
name|toAbsolutePath
argument_list|()
argument_list|)
throw|;
block|}
name|f
operator|=
name|base
operator|.
name|resolve
argument_list|(
name|prefix
operator|+
literal|"-"
operator|+
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|,
literal|"%03d"
argument_list|,
name|attempt
argument_list|)
operator|+
name|suffix
argument_list|)
expr_stmt|;
try|try
block|{
name|Files
operator|.
name|createFile
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ignore
parameter_list|)
block|{}
block|}
do|while
condition|(
operator|!
name|success
condition|)
do|;
name|registerToRemoveAfterSuite
argument_list|(
name|f
argument_list|)
expr_stmt|;
return|return
name|f
return|;
block|}
block|}
end_class

end_unit

