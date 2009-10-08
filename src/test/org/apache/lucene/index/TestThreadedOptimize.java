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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|SimpleAnalyzer
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
name|Analyzer
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
name|FSDirectory
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
name|MockRAMDirectory
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
name|util
operator|.
name|_TestUtil
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
name|English
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
name|File
import|;
end_import

begin_class
DECL|class|TestThreadedOptimize
specifier|public
class|class
name|TestThreadedOptimize
extends|extends
name|LuceneTestCase
block|{
DECL|field|ANALYZER
specifier|private
specifier|static
specifier|final
name|Analyzer
name|ANALYZER
init|=
operator|new
name|SimpleAnalyzer
argument_list|()
decl_stmt|;
DECL|field|NUM_THREADS
specifier|private
specifier|final
specifier|static
name|int
name|NUM_THREADS
init|=
literal|3
decl_stmt|;
comment|//private final static int NUM_THREADS = 5;
DECL|field|NUM_ITER
specifier|private
specifier|final
specifier|static
name|int
name|NUM_ITER
init|=
literal|1
decl_stmt|;
comment|//private final static int NUM_ITER = 10;
DECL|field|NUM_ITER2
specifier|private
specifier|final
specifier|static
name|int
name|NUM_ITER2
init|=
literal|1
decl_stmt|;
comment|//private final static int NUM_ITER2 = 5;
DECL|field|failed
specifier|private
name|boolean
name|failed
decl_stmt|;
DECL|method|setFailed
specifier|private
name|void
name|setFailed
parameter_list|()
block|{
name|failed
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|runTest
specifier|public
name|void
name|runTest
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|MergeScheduler
name|merger
parameter_list|)
throws|throws
name|Exception
block|{
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
name|ANALYZER
argument_list|,
literal|true
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|UNLIMITED
argument_list|)
decl_stmt|;
name|writer
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|2
argument_list|)
expr_stmt|;
if|if
condition|(
name|merger
operator|!=
literal|null
condition|)
name|writer
operator|.
name|setMergeScheduler
argument_list|(
name|merger
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
name|NUM_ITER
condition|;
name|iter
operator|++
control|)
block|{
specifier|final
name|int
name|iterFinal
init|=
name|iter
decl_stmt|;
name|writer
operator|.
name|setMergeFactor
argument_list|(
literal|1000
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
literal|200
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|d
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|NOT_ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"contents"
argument_list|,
name|English
operator|.
name|intToEnglish
argument_list|(
name|i
argument_list|)
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
name|d
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|setMergeFactor
argument_list|(
literal|4
argument_list|)
expr_stmt|;
comment|//writer.setInfoStream(System.out);
name|Thread
index|[]
name|threads
init|=
operator|new
name|Thread
index|[
name|NUM_THREADS
index|]
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
name|NUM_THREADS
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|iFinal
init|=
name|i
decl_stmt|;
specifier|final
name|IndexWriter
name|writerFinal
init|=
name|writer
decl_stmt|;
name|threads
index|[
name|i
index|]
operator|=
operator|new
name|Thread
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|NUM_ITER2
condition|;
name|j
operator|++
control|)
block|{
name|writerFinal
operator|.
name|optimize
argument_list|(
literal|false
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
literal|17
operator|*
operator|(
literal|1
operator|+
name|iFinal
operator|)
condition|;
name|k
operator|++
control|)
block|{
name|Document
name|d
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"id"
argument_list|,
name|iterFinal
operator|+
literal|"_"
operator|+
name|iFinal
operator|+
literal|"_"
operator|+
name|j
operator|+
literal|"_"
operator|+
name|k
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|NOT_ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"contents"
argument_list|,
name|English
operator|.
name|intToEnglish
argument_list|(
name|iFinal
operator|+
name|k
argument_list|)
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
name|writerFinal
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
literal|9
operator|*
operator|(
literal|1
operator|+
name|iFinal
operator|)
condition|;
name|k
operator|++
control|)
name|writerFinal
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
name|iterFinal
operator|+
literal|"_"
operator|+
name|iFinal
operator|+
literal|"_"
operator|+
name|j
operator|+
literal|"_"
operator|+
name|k
argument_list|)
argument_list|)
expr_stmt|;
name|writerFinal
operator|.
name|optimize
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|setFailed
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|": hit exception"
argument_list|)
expr_stmt|;
name|t
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
block|}
block|}
block|}
expr_stmt|;
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
name|NUM_THREADS
condition|;
name|i
operator|++
control|)
name|threads
index|[
name|i
index|]
operator|.
name|start
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
name|NUM_THREADS
condition|;
name|i
operator|++
control|)
name|threads
index|[
name|i
index|]
operator|.
name|join
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
operator|!
name|failed
argument_list|)
expr_stmt|;
specifier|final
name|int
name|expectedDocCount
init|=
call|(
name|int
call|)
argument_list|(
operator|(
literal|1
operator|+
name|iter
operator|)
operator|*
operator|(
literal|200
operator|+
literal|8
operator|*
name|NUM_ITER2
operator|*
operator|(
name|NUM_THREADS
operator|/
literal|2.0
operator|)
operator|*
operator|(
literal|1
operator|+
name|NUM_THREADS
operator|)
operator|)
argument_list|)
decl_stmt|;
comment|// System.out.println("TEST: now index=" + writer.segString());
name|assertEquals
argument_list|(
name|expectedDocCount
argument_list|,
name|writer
operator|.
name|docCount
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|writer
operator|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
name|ANALYZER
argument_list|,
literal|false
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|UNLIMITED
argument_list|)
expr_stmt|;
name|writer
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|IndexReader
name|reader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|directory
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|reader
operator|.
name|isOptimized
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedDocCount
argument_list|,
name|reader
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/*     Run above stress test against RAMDirectory and then     FSDirectory.   */
DECL|method|testThreadedOptimize
specifier|public
name|void
name|testThreadedOptimize
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|directory
init|=
operator|new
name|MockRAMDirectory
argument_list|()
decl_stmt|;
name|runTest
argument_list|(
name|directory
argument_list|,
operator|new
name|SerialMergeScheduler
argument_list|()
argument_list|)
expr_stmt|;
name|runTest
argument_list|(
name|directory
argument_list|,
operator|new
name|ConcurrentMergeScheduler
argument_list|()
argument_list|)
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
name|String
name|tempDir
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"tempDir"
argument_list|)
decl_stmt|;
if|if
condition|(
name|tempDir
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"tempDir undefined, cannot run test"
argument_list|)
throw|;
name|String
name|dirName
init|=
name|tempDir
operator|+
literal|"/luceneTestThreadedOptimize"
decl_stmt|;
name|directory
operator|=
name|FSDirectory
operator|.
name|open
argument_list|(
operator|new
name|File
argument_list|(
name|dirName
argument_list|)
argument_list|)
expr_stmt|;
name|runTest
argument_list|(
name|directory
argument_list|,
operator|new
name|SerialMergeScheduler
argument_list|()
argument_list|)
expr_stmt|;
name|runTest
argument_list|(
name|directory
argument_list|,
operator|new
name|ConcurrentMergeScheduler
argument_list|()
argument_list|)
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
name|_TestUtil
operator|.
name|rmDir
argument_list|(
name|dirName
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

