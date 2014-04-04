begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
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
name|analysis
operator|.
name|MockAnalyzer
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
name|MockDirectoryWrapper
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
name|LineFileDocs
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
name|PrintStreamInfoStream
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
name|TestUtil
import|;
end_import

begin_class
DECL|class|TestIndexWriterOutOfFileDescriptors
specifier|public
class|class
name|TestIndexWriterOutOfFileDescriptors
extends|extends
name|LuceneTestCase
block|{
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|MockDirectoryWrapper
name|dir
init|=
name|newMockFSDirectory
argument_list|(
name|createTempDir
argument_list|(
literal|"TestIndexWriterOutOfFileDescriptors"
argument_list|)
argument_list|)
decl_stmt|;
name|dir
operator|.
name|setPreventDoubleWrite
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|double
name|rate
init|=
name|random
argument_list|()
operator|.
name|nextDouble
argument_list|()
operator|*
literal|0.01
decl_stmt|;
comment|//System.out.println("rate=" + rate);
name|dir
operator|.
name|setRandomIOExceptionRateOnOpen
argument_list|(
name|rate
argument_list|)
expr_stmt|;
name|int
name|iters
init|=
name|atLeast
argument_list|(
literal|20
argument_list|)
decl_stmt|;
name|LineFileDocs
name|docs
init|=
operator|new
name|LineFileDocs
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|IndexReader
name|r
init|=
literal|null
decl_stmt|;
name|DirectoryReader
name|r2
init|=
literal|null
decl_stmt|;
name|boolean
name|any
init|=
literal|false
decl_stmt|;
name|MockDirectoryWrapper
name|dirCopy
init|=
literal|null
decl_stmt|;
name|int
name|lastNumDocs
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
name|iters
condition|;
name|iter
operator|++
control|)
block|{
name|IndexWriter
name|w
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: iter="
operator|+
name|iter
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|MockAnalyzer
name|analyzer
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|analyzer
operator|.
name|setMaxTokenLength
argument_list|(
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
name|IndexWriter
operator|.
name|MAX_TERM_LENGTH
argument_list|)
argument_list|)
expr_stmt|;
name|IndexWriterConfig
name|iwc
init|=
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|analyzer
argument_list|)
decl_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
comment|// Do this ourselves instead of relying on LTC so
comment|// we see incrementing messageID:
name|iwc
operator|.
name|setInfoStream
argument_list|(
operator|new
name|PrintStreamInfoStream
argument_list|(
name|System
operator|.
name|out
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|MergeScheduler
name|ms
init|=
name|iwc
operator|.
name|getMergeScheduler
argument_list|()
decl_stmt|;
if|if
condition|(
name|ms
operator|instanceof
name|ConcurrentMergeScheduler
condition|)
block|{
operator|(
operator|(
name|ConcurrentMergeScheduler
operator|)
name|ms
operator|)
operator|.
name|setSuppressExceptions
argument_list|()
expr_stmt|;
block|}
name|w
operator|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|iwc
argument_list|)
expr_stmt|;
if|if
condition|(
name|r
operator|!=
literal|null
operator|&&
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|5
argument_list|)
operator|==
literal|3
condition|)
block|{
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: addIndexes IR[]"
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|addIndexes
argument_list|(
operator|new
name|IndexReader
index|[]
block|{
name|r
block|}
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: addIndexes Directory[]"
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|addIndexes
argument_list|(
operator|new
name|Directory
index|[]
block|{
name|dirCopy
block|}
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: addDocument"
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|addDocument
argument_list|(
name|docs
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|dir
operator|.
name|setRandomIOExceptionRateOnOpen
argument_list|(
literal|0.0
argument_list|)
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|w
operator|=
literal|null
expr_stmt|;
comment|// NOTE: This is O(N^2)!  Only enable for temporary debugging:
comment|//dir.setRandomIOExceptionRateOnOpen(0.0);
comment|//_TestUtil.checkIndex(dir);
comment|//dir.setRandomIOExceptionRateOnOpen(rate);
comment|// Verify numDocs only increases, to catch IndexWriter
comment|// accidentally deleting the index:
name|dir
operator|.
name|setRandomIOExceptionRateOnOpen
argument_list|(
literal|0.0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|DirectoryReader
operator|.
name|indexExists
argument_list|(
name|dir
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|r2
operator|==
literal|null
condition|)
block|{
name|r2
operator|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|DirectoryReader
name|r3
init|=
name|DirectoryReader
operator|.
name|openIfChanged
argument_list|(
name|r2
argument_list|)
decl_stmt|;
if|if
condition|(
name|r3
operator|!=
literal|null
condition|)
block|{
name|r2
operator|.
name|close
argument_list|()
expr_stmt|;
name|r2
operator|=
name|r3
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
literal|"before="
operator|+
name|lastNumDocs
operator|+
literal|" after="
operator|+
name|r2
operator|.
name|numDocs
argument_list|()
argument_list|,
name|r2
operator|.
name|numDocs
argument_list|()
operator|>=
name|lastNumDocs
argument_list|)
expr_stmt|;
name|lastNumDocs
operator|=
name|r2
operator|.
name|numDocs
argument_list|()
expr_stmt|;
comment|//System.out.println("numDocs=" + lastNumDocs);
name|dir
operator|.
name|setRandomIOExceptionRateOnOpen
argument_list|(
name|rate
argument_list|)
expr_stmt|;
name|any
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: iter="
operator|+
name|iter
operator|+
literal|": success"
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: iter="
operator|+
name|iter
operator|+
literal|": exception"
argument_list|)
expr_stmt|;
name|ioe
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|w
operator|!=
literal|null
condition|)
block|{
comment|// NOTE: leave random IO exceptions enabled here,
comment|// to verify that rollback does not try to write
comment|// anything:
name|w
operator|.
name|rollback
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|any
operator|&&
name|r
operator|==
literal|null
operator|&&
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
comment|// Make a copy of a non-empty index so we can use
comment|// it to addIndexes later:
name|dir
operator|.
name|setRandomIOExceptionRateOnOpen
argument_list|(
literal|0.0
argument_list|)
expr_stmt|;
name|r
operator|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|dirCopy
operator|=
name|newMockFSDirectory
argument_list|(
name|createTempDir
argument_list|(
literal|"TestIndexWriterOutOfFileDescriptors.copy"
argument_list|)
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|files
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|file
range|:
name|dir
operator|.
name|listAll
argument_list|()
control|)
block|{
name|dir
operator|.
name|copy
argument_list|(
name|dirCopy
argument_list|,
name|file
argument_list|,
name|file
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
name|files
operator|.
name|add
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
name|dirCopy
operator|.
name|sync
argument_list|(
name|files
argument_list|)
expr_stmt|;
comment|// Have IW kiss the dir so we remove any leftover
comment|// files ... we can easily have leftover files at
comment|// the time we take a copy because we are holding
comment|// open a reader:
operator|new
name|IndexWriter
argument_list|(
name|dirCopy
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|dirCopy
operator|.
name|setRandomIOExceptionRate
argument_list|(
name|rate
argument_list|)
expr_stmt|;
name|dir
operator|.
name|setRandomIOExceptionRateOnOpen
argument_list|(
name|rate
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|r2
operator|!=
literal|null
condition|)
block|{
name|r2
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|r
operator|!=
literal|null
condition|)
block|{
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|dirCopy
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

