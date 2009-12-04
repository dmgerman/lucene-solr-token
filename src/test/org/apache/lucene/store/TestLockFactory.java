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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|WhitespaceAnalyzer
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
name|document
operator|.
name|Document
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
name|document
operator|.
name|Field
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
name|IndexWriter
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
name|Term
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
name|search
operator|.
name|IndexSearcher
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
name|search
operator|.
name|Query
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
name|search
operator|.
name|ScoreDoc
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
name|search
operator|.
name|TermQuery
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
name|_TestUtil
import|;
end_import

begin_class
DECL|class|TestLockFactory
specifier|public
class|class
name|TestLockFactory
extends|extends
name|LuceneTestCase
block|{
comment|// Verify: we can provide our own LockFactory implementation, the right
comment|// methods are called at the right time, locks are created, etc.
DECL|method|testCustomLockFactory
specifier|public
name|void
name|testCustomLockFactory
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|MockLockFactory
name|lf
init|=
operator|new
name|MockLockFactory
argument_list|()
decl_stmt|;
name|dir
operator|.
name|setLockFactory
argument_list|(
name|lf
argument_list|)
expr_stmt|;
comment|// Lock prefix should have been set:
name|assertTrue
argument_list|(
literal|"lock prefix was not set by the RAMDirectory"
argument_list|,
name|lf
operator|.
name|lockPrefixSet
argument_list|)
expr_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|LIMITED
argument_list|)
decl_stmt|;
comment|// add 100 documents (so that commit lock is used)
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|addDoc
argument_list|(
name|writer
argument_list|)
expr_stmt|;
block|}
comment|// Both write lock and commit lock should have been created:
name|assertEquals
argument_list|(
literal|"# of unique locks created (after instantiating IndexWriter)"
argument_list|,
literal|1
argument_list|,
name|lf
operator|.
name|locksCreated
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"# calls to makeLock is 0 (after instantiating IndexWriter)"
argument_list|,
name|lf
operator|.
name|makeLockCount
operator|>=
literal|1
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|String
name|lockName
range|:
name|lf
operator|.
name|locksCreated
operator|.
name|keySet
argument_list|()
control|)
block|{
name|MockLockFactory
operator|.
name|MockLock
name|lock
init|=
operator|(
name|MockLockFactory
operator|.
name|MockLock
operator|)
name|lf
operator|.
name|locksCreated
operator|.
name|get
argument_list|(
name|lockName
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"# calls to Lock.obtain is 0 (after instantiating IndexWriter)"
argument_list|,
name|lock
operator|.
name|lockAttempts
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// Verify: we can use the NoLockFactory with RAMDirectory w/ no
comment|// exceptions raised:
comment|// Verify: NoLockFactory allows two IndexWriters
DECL|method|testRAMDirectoryNoLocking
specifier|public
name|void
name|testRAMDirectoryNoLocking
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|dir
operator|.
name|setLockFactory
argument_list|(
name|NoLockFactory
operator|.
name|getNoLockFactory
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"RAMDirectory.setLockFactory did not take"
argument_list|,
name|NoLockFactory
operator|.
name|class
operator|.
name|isInstance
argument_list|(
name|dir
operator|.
name|getLockFactory
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|LIMITED
argument_list|)
decl_stmt|;
comment|// Create a 2nd IndexWriter.  This is normally not allowed but it should run through since we're not
comment|// using any locks:
name|IndexWriter
name|writer2
init|=
literal|null
decl_stmt|;
try|try
block|{
name|writer2
operator|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|,
literal|false
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|LIMITED
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should not have hit an IOException with no locking"
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|writer2
operator|!=
literal|null
condition|)
block|{
name|writer2
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|// Verify: SingleInstanceLockFactory is the default lock for RAMDirectory
comment|// Verify: RAMDirectory does basic locking correctly (can't create two IndexWriters)
DECL|method|testDefaultRAMDirectory
specifier|public
name|void
name|testDefaultRAMDirectory
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"RAMDirectory did not use correct LockFactory: got "
operator|+
name|dir
operator|.
name|getLockFactory
argument_list|()
argument_list|,
name|SingleInstanceLockFactory
operator|.
name|class
operator|.
name|isInstance
argument_list|(
name|dir
operator|.
name|getLockFactory
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|LIMITED
argument_list|)
decl_stmt|;
comment|// Create a 2nd IndexWriter.  This should fail:
name|IndexWriter
name|writer2
init|=
literal|null
decl_stmt|;
try|try
block|{
name|writer2
operator|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|,
literal|false
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|LIMITED
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should have hit an IOException with two IndexWriters on default SingleInstanceLockFactory"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{         }
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|writer2
operator|!=
literal|null
condition|)
block|{
name|writer2
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|testSimpleFSLockFactory
specifier|public
name|void
name|testSimpleFSLockFactory
parameter_list|()
throws|throws
name|IOException
block|{
comment|// test string file instantiation
operator|new
name|SimpleFSLockFactory
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
block|}
comment|// Verify: do stress test, by opening IndexReaders and
comment|// IndexWriters over& over in 2 threads and making sure
comment|// no unexpected exceptions are raised:
DECL|method|testStressLocks
specifier|public
name|void
name|testStressLocks
parameter_list|()
throws|throws
name|Exception
block|{
name|_testStressLocks
argument_list|(
literal|null
argument_list|,
name|_TestUtil
operator|.
name|getTempDir
argument_list|(
literal|"index.TestLockFactory6"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Verify: do stress test, by opening IndexReaders and
comment|// IndexWriters over& over in 2 threads and making sure
comment|// no unexpected exceptions are raised, but use
comment|// NativeFSLockFactory:
DECL|method|testStressLocksNativeFSLockFactory
specifier|public
name|void
name|testStressLocksNativeFSLockFactory
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|dir
init|=
name|_TestUtil
operator|.
name|getTempDir
argument_list|(
literal|"index.TestLockFactory7"
argument_list|)
decl_stmt|;
name|_testStressLocks
argument_list|(
operator|new
name|NativeFSLockFactory
argument_list|(
name|dir
argument_list|)
argument_list|,
name|dir
argument_list|)
expr_stmt|;
block|}
DECL|method|_testStressLocks
specifier|public
name|void
name|_testStressLocks
parameter_list|(
name|LockFactory
name|lockFactory
parameter_list|,
name|File
name|indexDir
parameter_list|)
throws|throws
name|Exception
block|{
name|FSDirectory
name|fs1
init|=
name|FSDirectory
operator|.
name|open
argument_list|(
name|indexDir
argument_list|,
name|lockFactory
argument_list|)
decl_stmt|;
comment|// First create a 1 doc index:
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|fs1
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|LIMITED
argument_list|)
decl_stmt|;
name|addDoc
argument_list|(
name|w
argument_list|)
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|WriterThread
name|writer
init|=
operator|new
name|WriterThread
argument_list|(
literal|100
argument_list|,
name|fs1
argument_list|)
decl_stmt|;
name|SearcherThread
name|searcher
init|=
operator|new
name|SearcherThread
argument_list|(
literal|100
argument_list|,
name|fs1
argument_list|)
decl_stmt|;
name|writer
operator|.
name|start
argument_list|()
expr_stmt|;
name|searcher
operator|.
name|start
argument_list|()
expr_stmt|;
while|while
condition|(
name|writer
operator|.
name|isAlive
argument_list|()
operator|||
name|searcher
operator|.
name|isAlive
argument_list|()
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"IndexWriter hit unexpected exceptions"
argument_list|,
operator|!
name|writer
operator|.
name|hitException
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"IndexSearcher hit unexpected exceptions"
argument_list|,
operator|!
name|searcher
operator|.
name|hitException
argument_list|)
expr_stmt|;
comment|// Cleanup
name|_TestUtil
operator|.
name|rmDir
argument_list|(
name|indexDir
argument_list|)
expr_stmt|;
block|}
comment|// Verify: NativeFSLockFactory works correctly
DECL|method|testNativeFSLockFactory
specifier|public
name|void
name|testNativeFSLockFactory
parameter_list|()
throws|throws
name|IOException
block|{
name|NativeFSLockFactory
name|f
init|=
operator|new
name|NativeFSLockFactory
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"tempDir"
argument_list|)
argument_list|)
decl_stmt|;
name|f
operator|.
name|setLockPrefix
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|Lock
name|l
init|=
name|f
operator|.
name|makeLock
argument_list|(
literal|"commit"
argument_list|)
decl_stmt|;
name|Lock
name|l2
init|=
name|f
operator|.
name|makeLock
argument_list|(
literal|"commit"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"failed to obtain lock"
argument_list|,
name|l
operator|.
name|obtain
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"succeeded in obtaining lock twice"
argument_list|,
operator|!
name|l2
operator|.
name|obtain
argument_list|()
argument_list|)
expr_stmt|;
name|l
operator|.
name|release
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"failed to obtain 2nd lock after first one was freed"
argument_list|,
name|l2
operator|.
name|obtain
argument_list|()
argument_list|)
expr_stmt|;
name|l2
operator|.
name|release
argument_list|()
expr_stmt|;
comment|// Make sure we can obtain first one again, test isLocked():
name|assertTrue
argument_list|(
literal|"failed to obtain lock"
argument_list|,
name|l
operator|.
name|obtain
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|l
operator|.
name|isLocked
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|l2
operator|.
name|isLocked
argument_list|()
argument_list|)
expr_stmt|;
name|l
operator|.
name|release
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|l
operator|.
name|isLocked
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|l2
operator|.
name|isLocked
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Verify: NativeFSLockFactory assigns null as lockPrefix if the lockDir is inside directory
DECL|method|testNativeFSLockFactoryPrefix
specifier|public
name|void
name|testNativeFSLockFactoryPrefix
parameter_list|()
throws|throws
name|IOException
block|{
name|File
name|fdir1
init|=
name|_TestUtil
operator|.
name|getTempDir
argument_list|(
literal|"TestLockFactory.8"
argument_list|)
decl_stmt|;
name|File
name|fdir2
init|=
name|_TestUtil
operator|.
name|getTempDir
argument_list|(
literal|"TestLockFactory.8.Lockdir"
argument_list|)
decl_stmt|;
name|Directory
name|dir1
init|=
name|FSDirectory
operator|.
name|open
argument_list|(
name|fdir1
argument_list|,
operator|new
name|NativeFSLockFactory
argument_list|(
name|fdir1
argument_list|)
argument_list|)
decl_stmt|;
comment|// same directory, but locks are stored somewhere else. The prefix of the lock factory should != null
name|Directory
name|dir2
init|=
name|FSDirectory
operator|.
name|open
argument_list|(
name|fdir1
argument_list|,
operator|new
name|NativeFSLockFactory
argument_list|(
name|fdir2
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|prefix1
init|=
name|dir1
operator|.
name|getLockFactory
argument_list|()
operator|.
name|getLockPrefix
argument_list|()
decl_stmt|;
name|assertNull
argument_list|(
literal|"Lock prefix for lockDir same as directory should be null"
argument_list|,
name|prefix1
argument_list|)
expr_stmt|;
name|String
name|prefix2
init|=
name|dir2
operator|.
name|getLockFactory
argument_list|()
operator|.
name|getLockPrefix
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Lock prefix for lockDir outside of directory should be not null"
argument_list|,
name|prefix2
argument_list|)
expr_stmt|;
name|_TestUtil
operator|.
name|rmDir
argument_list|(
name|fdir1
argument_list|)
expr_stmt|;
name|_TestUtil
operator|.
name|rmDir
argument_list|(
name|fdir2
argument_list|)
expr_stmt|;
block|}
comment|// Verify: default LockFactory has no prefix (ie
comment|// write.lock is stored in index):
DECL|method|testDefaultFSLockFactoryPrefix
specifier|public
name|void
name|testDefaultFSLockFactoryPrefix
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Make sure we get null prefix:
name|File
name|dirName
init|=
name|_TestUtil
operator|.
name|getTempDir
argument_list|(
literal|"TestLockFactory.10"
argument_list|)
decl_stmt|;
name|Directory
name|dir
init|=
name|FSDirectory
operator|.
name|open
argument_list|(
name|dirName
argument_list|)
decl_stmt|;
name|String
name|prefix
init|=
name|dir
operator|.
name|getLockFactory
argument_list|()
operator|.
name|getLockPrefix
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Default lock prefix should be null"
argument_list|,
literal|null
operator|==
name|prefix
argument_list|)
expr_stmt|;
name|_TestUtil
operator|.
name|rmDir
argument_list|(
name|dirName
argument_list|)
expr_stmt|;
block|}
DECL|class|WriterThread
specifier|private
class|class
name|WriterThread
extends|extends
name|Thread
block|{
DECL|field|dir
specifier|private
name|Directory
name|dir
decl_stmt|;
DECL|field|numIteration
specifier|private
name|int
name|numIteration
decl_stmt|;
DECL|field|hitException
specifier|public
name|boolean
name|hitException
init|=
literal|false
decl_stmt|;
DECL|method|WriterThread
specifier|public
name|WriterThread
parameter_list|(
name|int
name|numIteration
parameter_list|,
name|Directory
name|dir
parameter_list|)
block|{
name|this
operator|.
name|numIteration
operator|=
name|numIteration
expr_stmt|;
name|this
operator|.
name|dir
operator|=
name|dir
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
name|WhitespaceAnalyzer
name|analyzer
init|=
operator|new
name|WhitespaceAnalyzer
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
literal|null
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
name|this
operator|.
name|numIteration
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|writer
operator|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|analyzer
argument_list|,
literal|false
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|LIMITED
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|toString
argument_list|()
operator|.
name|indexOf
argument_list|(
literal|" timed out:"
argument_list|)
operator|==
operator|-
literal|1
condition|)
block|{
name|hitException
operator|=
literal|true
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Stress Test Index Writer: creation hit unexpected IOException: "
operator|+
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// lock obtain timed out
comment|// NOTE: we should at some point
comment|// consider this a failure?  The lock
comment|// obtains, across IndexReader&
comment|// IndexWriters should be "fair" (ie
comment|// FIFO).
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|hitException
operator|=
literal|true
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Stress Test Index Writer: creation hit unexpected exception: "
operator|+
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
break|break;
block|}
if|if
condition|(
name|writer
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|addDoc
argument_list|(
name|writer
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|hitException
operator|=
literal|true
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Stress Test Index Writer: addDoc hit unexpected exception: "
operator|+
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
break|break;
block|}
try|try
block|{
name|writer
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
name|hitException
operator|=
literal|true
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Stress Test Index Writer: close hit unexpected exception: "
operator|+
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
break|break;
block|}
name|writer
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|class|SearcherThread
specifier|private
class|class
name|SearcherThread
extends|extends
name|Thread
block|{
DECL|field|dir
specifier|private
name|Directory
name|dir
decl_stmt|;
DECL|field|numIteration
specifier|private
name|int
name|numIteration
decl_stmt|;
DECL|field|hitException
specifier|public
name|boolean
name|hitException
init|=
literal|false
decl_stmt|;
DECL|method|SearcherThread
specifier|public
name|SearcherThread
parameter_list|(
name|int
name|numIteration
parameter_list|,
name|Directory
name|dir
parameter_list|)
block|{
name|this
operator|.
name|numIteration
operator|=
name|numIteration
expr_stmt|;
name|this
operator|.
name|dir
operator|=
name|dir
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
name|IndexSearcher
name|searcher
init|=
literal|null
decl_stmt|;
name|Query
name|query
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"aaa"
argument_list|)
argument_list|)
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
name|this
operator|.
name|numIteration
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|dir
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|hitException
operator|=
literal|true
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Stress Test Index Searcher: create hit unexpected exception: "
operator|+
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
break|break;
block|}
if|if
condition|(
name|searcher
operator|!=
literal|null
condition|)
block|{
name|ScoreDoc
index|[]
name|hits
init|=
literal|null
decl_stmt|;
try|try
block|{
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|hitException
operator|=
literal|true
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Stress Test Index Searcher: search hit unexpected exception: "
operator|+
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
break|break;
block|}
comment|// System.out.println(hits.length() + " total results");
try|try
block|{
name|searcher
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
name|hitException
operator|=
literal|true
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Stress Test Index Searcher: close hit unexpected exception: "
operator|+
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
break|break;
block|}
name|searcher
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|class|MockLockFactory
specifier|public
class|class
name|MockLockFactory
extends|extends
name|LockFactory
block|{
DECL|field|lockPrefixSet
specifier|public
name|boolean
name|lockPrefixSet
decl_stmt|;
DECL|field|locksCreated
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Lock
argument_list|>
name|locksCreated
init|=
name|Collections
operator|.
name|synchronizedMap
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Lock
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|makeLockCount
specifier|public
name|int
name|makeLockCount
init|=
literal|0
decl_stmt|;
annotation|@
name|Override
DECL|method|setLockPrefix
specifier|public
name|void
name|setLockPrefix
parameter_list|(
name|String
name|lockPrefix
parameter_list|)
block|{
name|super
operator|.
name|setLockPrefix
argument_list|(
name|lockPrefix
argument_list|)
expr_stmt|;
name|lockPrefixSet
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|makeLock
specifier|synchronized
specifier|public
name|Lock
name|makeLock
parameter_list|(
name|String
name|lockName
parameter_list|)
block|{
name|Lock
name|lock
init|=
operator|new
name|MockLock
argument_list|()
decl_stmt|;
name|locksCreated
operator|.
name|put
argument_list|(
name|lockName
argument_list|,
name|lock
argument_list|)
expr_stmt|;
name|makeLockCount
operator|++
expr_stmt|;
return|return
name|lock
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
name|specificLockName
parameter_list|)
block|{}
DECL|class|MockLock
specifier|public
class|class
name|MockLock
extends|extends
name|Lock
block|{
DECL|field|lockAttempts
specifier|public
name|int
name|lockAttempts
decl_stmt|;
annotation|@
name|Override
DECL|method|obtain
specifier|public
name|boolean
name|obtain
parameter_list|()
block|{
name|lockAttempts
operator|++
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|release
specifier|public
name|void
name|release
parameter_list|()
block|{
comment|// do nothing
block|}
annotation|@
name|Override
DECL|method|isLocked
specifier|public
name|boolean
name|isLocked
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
DECL|method|addDoc
specifier|private
name|void
name|addDoc
parameter_list|(
name|IndexWriter
name|writer
parameter_list|)
throws|throws
name|IOException
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"content"
argument_list|,
literal|"aaa"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

