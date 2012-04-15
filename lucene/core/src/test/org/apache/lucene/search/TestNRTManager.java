begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
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
name|concurrent
operator|.
name|CountDownLatch
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ExecutorService
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
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
name|TextField
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
name|CorruptIndexException
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
name|DirectoryReader
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
name|IndexReader
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
name|IndexWriterConfig
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
name|IndexableField
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
name|RandomIndexWriter
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
name|index
operator|.
name|ThreadedIndexingAndSearchingTestCase
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
name|LockObtainFailedException
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
name|NRTCachingDirectory
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
name|UseNoMemoryExpensiveCodec
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
name|ThreadInterruptedException
import|;
end_import

begin_class
annotation|@
name|UseNoMemoryExpensiveCodec
DECL|class|TestNRTManager
specifier|public
class|class
name|TestNRTManager
extends|extends
name|ThreadedIndexingAndSearchingTestCase
block|{
DECL|field|lastGens
specifier|private
specifier|final
name|ThreadLocal
argument_list|<
name|Long
argument_list|>
name|lastGens
init|=
operator|new
name|ThreadLocal
argument_list|<
name|Long
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|warmCalled
specifier|private
name|boolean
name|warmCalled
decl_stmt|;
DECL|method|testNRTManager
specifier|public
name|void
name|testNRTManager
parameter_list|()
throws|throws
name|Exception
block|{
name|runTest
argument_list|(
literal|"TestNRTManager"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getFinalSearcher
specifier|protected
name|IndexSearcher
name|getFinalSearcher
parameter_list|()
throws|throws
name|Exception
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
literal|"TEST: finalSearcher maxGen="
operator|+
name|maxGen
argument_list|)
expr_stmt|;
block|}
name|nrtDeletes
operator|.
name|waitForGeneration
argument_list|(
name|maxGen
argument_list|)
expr_stmt|;
return|return
name|nrtDeletes
operator|.
name|acquire
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getDirectory
specifier|protected
name|Directory
name|getDirectory
parameter_list|(
name|Directory
name|in
parameter_list|)
block|{
comment|// Randomly swap in NRTCachingDir
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
literal|"TEST: wrap NRTCachingDir"
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|NRTCachingDirectory
argument_list|(
name|in
argument_list|,
literal|5.0
argument_list|,
literal|60.0
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|in
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|updateDocuments
specifier|protected
name|void
name|updateDocuments
parameter_list|(
name|Term
name|id
parameter_list|,
name|List
argument_list|<
name|?
extends|extends
name|Iterable
argument_list|<
name|?
extends|extends
name|IndexableField
argument_list|>
argument_list|>
name|docs
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|long
name|gen
init|=
name|genWriter
operator|.
name|updateDocuments
argument_list|(
name|id
argument_list|,
name|docs
argument_list|)
decl_stmt|;
comment|// Randomly verify the update "took":
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|20
argument_list|)
operator|==
literal|2
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
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|": nrt: verify "
operator|+
name|id
argument_list|)
expr_stmt|;
block|}
name|nrtDeletes
operator|.
name|waitForGeneration
argument_list|(
name|gen
argument_list|)
expr_stmt|;
specifier|final
name|IndexSearcher
name|s
init|=
name|nrtDeletes
operator|.
name|acquire
argument_list|()
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
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|": nrt: got searcher="
operator|+
name|s
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|assertEquals
argument_list|(
name|docs
operator|.
name|size
argument_list|()
argument_list|,
name|s
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|id
argument_list|)
argument_list|,
literal|10
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|nrtDeletes
operator|.
name|release
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
block|}
name|lastGens
operator|.
name|set
argument_list|(
name|gen
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|addDocuments
specifier|protected
name|void
name|addDocuments
parameter_list|(
name|Term
name|id
parameter_list|,
name|List
argument_list|<
name|?
extends|extends
name|Iterable
argument_list|<
name|?
extends|extends
name|IndexableField
argument_list|>
argument_list|>
name|docs
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|long
name|gen
init|=
name|genWriter
operator|.
name|addDocuments
argument_list|(
name|docs
argument_list|)
decl_stmt|;
comment|// Randomly verify the add "took":
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|20
argument_list|)
operator|==
literal|2
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
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|": nrt: verify "
operator|+
name|id
argument_list|)
expr_stmt|;
block|}
name|nrtNoDeletes
operator|.
name|waitForGeneration
argument_list|(
name|gen
argument_list|)
expr_stmt|;
specifier|final
name|IndexSearcher
name|s
init|=
name|nrtNoDeletes
operator|.
name|acquire
argument_list|()
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
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|": nrt: got searcher="
operator|+
name|s
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|assertEquals
argument_list|(
name|docs
operator|.
name|size
argument_list|()
argument_list|,
name|s
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|id
argument_list|)
argument_list|,
literal|10
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|nrtNoDeletes
operator|.
name|release
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
block|}
name|lastGens
operator|.
name|set
argument_list|(
name|gen
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|addDocument
specifier|protected
name|void
name|addDocument
parameter_list|(
name|Term
name|id
parameter_list|,
name|Iterable
argument_list|<
name|?
extends|extends
name|IndexableField
argument_list|>
name|doc
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|long
name|gen
init|=
name|genWriter
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
decl_stmt|;
comment|// Randomly verify the add "took":
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|20
argument_list|)
operator|==
literal|2
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
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|": nrt: verify "
operator|+
name|id
argument_list|)
expr_stmt|;
block|}
name|nrtNoDeletes
operator|.
name|waitForGeneration
argument_list|(
name|gen
argument_list|)
expr_stmt|;
specifier|final
name|IndexSearcher
name|s
init|=
name|nrtNoDeletes
operator|.
name|acquire
argument_list|()
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
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|": nrt: got searcher="
operator|+
name|s
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|s
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|id
argument_list|)
argument_list|,
literal|10
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|nrtNoDeletes
operator|.
name|release
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
block|}
name|lastGens
operator|.
name|set
argument_list|(
name|gen
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|updateDocument
specifier|protected
name|void
name|updateDocument
parameter_list|(
name|Term
name|id
parameter_list|,
name|Iterable
argument_list|<
name|?
extends|extends
name|IndexableField
argument_list|>
name|doc
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|long
name|gen
init|=
name|genWriter
operator|.
name|updateDocument
argument_list|(
name|id
argument_list|,
name|doc
argument_list|)
decl_stmt|;
comment|// Randomly verify the udpate "took":
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|20
argument_list|)
operator|==
literal|2
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
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|": nrt: verify "
operator|+
name|id
argument_list|)
expr_stmt|;
block|}
name|nrtDeletes
operator|.
name|waitForGeneration
argument_list|(
name|gen
argument_list|)
expr_stmt|;
specifier|final
name|IndexSearcher
name|s
init|=
name|nrtDeletes
operator|.
name|acquire
argument_list|()
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
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|": nrt: got searcher="
operator|+
name|s
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|s
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|id
argument_list|)
argument_list|,
literal|10
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|nrtDeletes
operator|.
name|release
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
block|}
name|lastGens
operator|.
name|set
argument_list|(
name|gen
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|deleteDocuments
specifier|protected
name|void
name|deleteDocuments
parameter_list|(
name|Term
name|id
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|long
name|gen
init|=
name|genWriter
operator|.
name|deleteDocuments
argument_list|(
name|id
argument_list|)
decl_stmt|;
comment|// randomly verify the delete "took":
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|20
argument_list|)
operator|==
literal|7
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
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|": nrt: verify del "
operator|+
name|id
argument_list|)
expr_stmt|;
block|}
name|nrtDeletes
operator|.
name|waitForGeneration
argument_list|(
name|gen
argument_list|)
expr_stmt|;
specifier|final
name|IndexSearcher
name|s
init|=
name|nrtDeletes
operator|.
name|acquire
argument_list|()
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
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|": nrt: got searcher="
operator|+
name|s
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|s
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|id
argument_list|)
argument_list|,
literal|10
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|nrtDeletes
operator|.
name|release
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
block|}
name|lastGens
operator|.
name|set
argument_list|(
name|gen
argument_list|)
expr_stmt|;
block|}
comment|// Not guaranteed to reflect deletes:
DECL|field|nrtNoDeletes
specifier|private
name|NRTManager
name|nrtNoDeletes
decl_stmt|;
comment|// Is guaranteed to reflect deletes:
DECL|field|nrtDeletes
specifier|private
name|NRTManager
name|nrtDeletes
decl_stmt|;
DECL|field|genWriter
specifier|private
name|NRTManager
operator|.
name|TrackingIndexWriter
name|genWriter
decl_stmt|;
DECL|field|nrtDeletesThread
specifier|private
name|NRTManagerReopenThread
name|nrtDeletesThread
decl_stmt|;
DECL|field|nrtNoDeletesThread
specifier|private
name|NRTManagerReopenThread
name|nrtNoDeletesThread
decl_stmt|;
annotation|@
name|Override
DECL|method|doAfterWriter
specifier|protected
name|void
name|doAfterWriter
parameter_list|(
specifier|final
name|ExecutorService
name|es
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|double
name|minReopenSec
init|=
literal|0.01
operator|+
literal|0.05
operator|*
name|random
argument_list|()
operator|.
name|nextDouble
argument_list|()
decl_stmt|;
specifier|final
name|double
name|maxReopenSec
init|=
name|minReopenSec
operator|*
operator|(
literal|1.0
operator|+
literal|10
operator|*
name|random
argument_list|()
operator|.
name|nextDouble
argument_list|()
operator|)
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
literal|"TEST: make NRTManager maxReopenSec="
operator|+
name|maxReopenSec
operator|+
literal|" minReopenSec="
operator|+
name|minReopenSec
argument_list|)
expr_stmt|;
block|}
name|genWriter
operator|=
operator|new
name|NRTManager
operator|.
name|TrackingIndexWriter
argument_list|(
name|writer
argument_list|)
expr_stmt|;
specifier|final
name|SearcherFactory
name|sf
init|=
operator|new
name|SearcherFactory
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|IndexSearcher
name|newSearcher
parameter_list|(
name|IndexReader
name|r
parameter_list|)
throws|throws
name|IOException
block|{
name|TestNRTManager
operator|.
name|this
operator|.
name|warmCalled
operator|=
literal|true
expr_stmt|;
name|IndexSearcher
name|s
init|=
operator|new
name|IndexSearcher
argument_list|(
name|r
argument_list|,
name|es
argument_list|)
decl_stmt|;
name|s
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"united"
argument_list|)
argument_list|)
argument_list|,
literal|10
argument_list|)
expr_stmt|;
return|return
name|s
return|;
block|}
block|}
decl_stmt|;
name|nrtNoDeletes
operator|=
operator|new
name|NRTManager
argument_list|(
name|genWriter
argument_list|,
name|sf
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|nrtDeletes
operator|=
operator|new
name|NRTManager
argument_list|(
name|genWriter
argument_list|,
name|sf
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|nrtDeletesThread
operator|=
operator|new
name|NRTManagerReopenThread
argument_list|(
name|nrtDeletes
argument_list|,
name|maxReopenSec
argument_list|,
name|minReopenSec
argument_list|)
expr_stmt|;
name|nrtDeletesThread
operator|.
name|setName
argument_list|(
literal|"NRTDeletes Reopen Thread"
argument_list|)
expr_stmt|;
name|nrtDeletesThread
operator|.
name|setPriority
argument_list|(
name|Math
operator|.
name|min
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getPriority
argument_list|()
operator|+
literal|2
argument_list|,
name|Thread
operator|.
name|MAX_PRIORITY
argument_list|)
argument_list|)
expr_stmt|;
name|nrtDeletesThread
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|nrtDeletesThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|nrtNoDeletesThread
operator|=
operator|new
name|NRTManagerReopenThread
argument_list|(
name|nrtNoDeletes
argument_list|,
name|maxReopenSec
argument_list|,
name|minReopenSec
argument_list|)
expr_stmt|;
name|nrtNoDeletesThread
operator|.
name|setName
argument_list|(
literal|"NRTNoDeletes Reopen Thread"
argument_list|)
expr_stmt|;
name|nrtNoDeletesThread
operator|.
name|setPriority
argument_list|(
name|Math
operator|.
name|min
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getPriority
argument_list|()
operator|+
literal|2
argument_list|,
name|Thread
operator|.
name|MAX_PRIORITY
argument_list|)
argument_list|)
expr_stmt|;
name|nrtNoDeletesThread
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|nrtNoDeletesThread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doAfterIndexingThreadDone
specifier|protected
name|void
name|doAfterIndexingThreadDone
parameter_list|()
block|{
name|Long
name|gen
init|=
name|lastGens
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|gen
operator|!=
literal|null
condition|)
block|{
name|addMaxGen
argument_list|(
name|gen
argument_list|)
expr_stmt|;
block|}
block|}
DECL|field|maxGen
specifier|private
name|long
name|maxGen
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|addMaxGen
specifier|private
specifier|synchronized
name|void
name|addMaxGen
parameter_list|(
name|long
name|gen
parameter_list|)
block|{
name|maxGen
operator|=
name|Math
operator|.
name|max
argument_list|(
name|gen
argument_list|,
name|maxGen
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doSearching
specifier|protected
name|void
name|doSearching
parameter_list|(
name|ExecutorService
name|es
parameter_list|,
name|long
name|stopTime
parameter_list|)
throws|throws
name|Exception
block|{
name|runSearchThreads
argument_list|(
name|stopTime
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getCurrentSearcher
specifier|protected
name|IndexSearcher
name|getCurrentSearcher
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Test doesn't assert deletions until the end, so we
comment|// can randomize whether dels must be applied
specifier|final
name|NRTManager
name|nrt
decl_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|nrt
operator|=
name|nrtDeletes
expr_stmt|;
block|}
else|else
block|{
name|nrt
operator|=
name|nrtNoDeletes
expr_stmt|;
block|}
return|return
name|nrt
operator|.
name|acquire
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|releaseSearcher
specifier|protected
name|void
name|releaseSearcher
parameter_list|(
name|IndexSearcher
name|s
parameter_list|)
throws|throws
name|Exception
block|{
comment|// NOTE: a bit iffy... technically you should release
comment|// against the same NRT mgr you acquired from... but
comment|// both impls just decRef the underlying reader so we
comment|// can get away w/ cheating:
name|nrtNoDeletes
operator|.
name|release
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doClose
specifier|protected
name|void
name|doClose
parameter_list|()
throws|throws
name|Exception
block|{
name|assertTrue
argument_list|(
name|warmCalled
argument_list|)
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
literal|"TEST: now close NRTManager"
argument_list|)
expr_stmt|;
block|}
name|nrtDeletesThread
operator|.
name|close
argument_list|()
expr_stmt|;
name|nrtDeletes
operator|.
name|close
argument_list|()
expr_stmt|;
name|nrtNoDeletesThread
operator|.
name|close
argument_list|()
expr_stmt|;
name|nrtNoDeletes
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/*    * LUCENE-3528 - NRTManager hangs in certain situations     */
DECL|method|testThreadStarvationNoDeleteNRTReader
specifier|public
name|void
name|testThreadStarvationNoDeleteNRTReader
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|IndexWriterConfig
name|conf
init|=
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
decl_stmt|;
name|Directory
name|d
init|=
name|newDirectory
argument_list|()
decl_stmt|;
specifier|final
name|CountDownLatch
name|latch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|CountDownLatch
name|signal
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|LatchedIndexWriter
name|_writer
init|=
operator|new
name|LatchedIndexWriter
argument_list|(
name|d
argument_list|,
name|conf
argument_list|,
name|latch
argument_list|,
name|signal
argument_list|)
decl_stmt|;
specifier|final
name|NRTManager
operator|.
name|TrackingIndexWriter
name|writer
init|=
operator|new
name|NRTManager
operator|.
name|TrackingIndexWriter
argument_list|(
name|_writer
argument_list|)
decl_stmt|;
specifier|final
name|NRTManager
name|manager
init|=
operator|new
name|NRTManager
argument_list|(
name|writer
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
decl_stmt|;
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
name|newField
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|long
name|gen
init|=
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
decl_stmt|;
name|manager
operator|.
name|maybeRefresh
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|gen
operator|<
name|manager
operator|.
name|getCurrentSearchingGen
argument_list|()
argument_list|)
expr_stmt|;
name|Thread
name|t
init|=
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
name|signal
operator|.
name|await
argument_list|()
expr_stmt|;
name|manager
operator|.
name|maybeRefresh
argument_list|()
expr_stmt|;
name|writer
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"foo"
argument_list|,
literal|"barista"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|manager
operator|.
name|maybeRefresh
argument_list|()
expr_stmt|;
comment|// kick off another reopen so we inc. the internal gen
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
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
comment|// let the add below finish
block|}
block|}
block|}
decl_stmt|;
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
name|_writer
operator|.
name|waitAfterUpdate
operator|=
literal|true
expr_stmt|;
comment|// wait in addDocument to let some reopens go through
specifier|final
name|long
name|lastGen
init|=
name|writer
operator|.
name|updateDocument
argument_list|(
operator|new
name|Term
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|,
name|doc
argument_list|)
decl_stmt|;
comment|// once this returns the doc is already reflected in the last reopen
name|assertFalse
argument_list|(
name|manager
operator|.
name|isSearcherCurrent
argument_list|()
argument_list|)
expr_stmt|;
comment|// false since there is a delete in the queue
name|IndexSearcher
name|searcher
init|=
name|manager
operator|.
name|acquire
argument_list|()
decl_stmt|;
try|try
block|{
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|searcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|manager
operator|.
name|release
argument_list|(
name|searcher
argument_list|)
expr_stmt|;
block|}
name|NRTManagerReopenThread
name|thread
init|=
operator|new
name|NRTManagerReopenThread
argument_list|(
name|manager
argument_list|,
literal|0.01
argument_list|,
literal|0.01
argument_list|)
decl_stmt|;
name|thread
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// start reopening
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
literal|"waiting now for generation "
operator|+
name|lastGen
argument_list|)
expr_stmt|;
block|}
specifier|final
name|AtomicBoolean
name|finished
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|Thread
name|waiter
init|=
operator|new
name|Thread
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|manager
operator|.
name|waitForGeneration
argument_list|(
name|lastGen
argument_list|)
expr_stmt|;
name|finished
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|waiter
operator|.
name|start
argument_list|()
expr_stmt|;
name|manager
operator|.
name|maybeRefresh
argument_list|()
expr_stmt|;
name|waiter
operator|.
name|join
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|finished
operator|.
name|get
argument_list|()
condition|)
block|{
name|waiter
operator|.
name|interrupt
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"thread deadlocked on waitForGeneration"
argument_list|)
expr_stmt|;
block|}
name|thread
operator|.
name|close
argument_list|()
expr_stmt|;
name|thread
operator|.
name|join
argument_list|()
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|manager
argument_list|,
name|_writer
argument_list|,
name|d
argument_list|)
expr_stmt|;
block|}
DECL|class|LatchedIndexWriter
specifier|public
specifier|static
class|class
name|LatchedIndexWriter
extends|extends
name|IndexWriter
block|{
DECL|field|latch
specifier|private
name|CountDownLatch
name|latch
decl_stmt|;
DECL|field|waitAfterUpdate
name|boolean
name|waitAfterUpdate
init|=
literal|false
decl_stmt|;
DECL|field|signal
specifier|private
name|CountDownLatch
name|signal
decl_stmt|;
DECL|method|LatchedIndexWriter
specifier|public
name|LatchedIndexWriter
parameter_list|(
name|Directory
name|d
parameter_list|,
name|IndexWriterConfig
name|conf
parameter_list|,
name|CountDownLatch
name|latch
parameter_list|,
name|CountDownLatch
name|signal
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|LockObtainFailedException
throws|,
name|IOException
block|{
name|super
argument_list|(
name|d
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|this
operator|.
name|latch
operator|=
name|latch
expr_stmt|;
name|this
operator|.
name|signal
operator|=
name|signal
expr_stmt|;
block|}
DECL|method|updateDocument
specifier|public
name|void
name|updateDocument
parameter_list|(
name|Term
name|term
parameter_list|,
name|Iterable
argument_list|<
name|?
extends|extends
name|IndexableField
argument_list|>
name|doc
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|super
operator|.
name|updateDocument
argument_list|(
name|term
argument_list|,
name|doc
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
try|try
block|{
if|if
condition|(
name|waitAfterUpdate
condition|)
block|{
name|signal
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|latch
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ThreadInterruptedException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|testEvilSearcherFactory
specifier|public
name|void
name|testEvilSearcherFactory
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
specifier|final
name|RandomIndexWriter
name|w
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|)
decl_stmt|;
name|w
operator|.
name|commit
argument_list|()
expr_stmt|;
specifier|final
name|IndexReader
name|other
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
specifier|final
name|SearcherFactory
name|theEvilOne
init|=
operator|new
name|SearcherFactory
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|IndexSearcher
name|newSearcher
parameter_list|(
name|IndexReader
name|ignored
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|IndexSearcher
argument_list|(
name|other
argument_list|)
return|;
block|}
block|}
decl_stmt|;
try|try
block|{
operator|new
name|NRTManager
argument_list|(
operator|new
name|NRTManager
operator|.
name|TrackingIndexWriter
argument_list|(
name|w
operator|.
name|w
argument_list|)
argument_list|,
name|theEvilOne
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|ise
parameter_list|)
block|{
comment|// expected
block|}
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|other
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

