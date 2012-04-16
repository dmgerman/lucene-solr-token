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
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
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
name|AtomicInteger
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
name|_TestUtil
import|;
end_import

begin_class
DECL|class|TestForceMergeForever
specifier|public
class|class
name|TestForceMergeForever
extends|extends
name|LuceneTestCase
block|{
comment|// Just counts how many merges are done
DECL|class|MyIndexWriter
specifier|private
specifier|static
class|class
name|MyIndexWriter
extends|extends
name|IndexWriter
block|{
DECL|field|mergeCount
name|AtomicInteger
name|mergeCount
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
DECL|field|first
specifier|private
name|boolean
name|first
decl_stmt|;
DECL|method|MyIndexWriter
specifier|public
name|MyIndexWriter
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|IndexWriterConfig
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
name|super
argument_list|(
name|dir
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|merge
specifier|public
name|void
name|merge
parameter_list|(
name|MergePolicy
operator|.
name|OneMerge
name|merge
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
if|if
condition|(
name|merge
operator|.
name|maxNumSegments
operator|!=
operator|-
literal|1
operator|&&
operator|(
name|first
operator|||
name|merge
operator|.
name|segments
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|)
condition|)
block|{
name|first
operator|=
literal|false
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
literal|"TEST: maxNumSegments merge"
argument_list|)
expr_stmt|;
block|}
name|mergeCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
name|super
operator|.
name|merge
argument_list|(
name|merge
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Directory
name|d
init|=
name|newDirectory
argument_list|()
decl_stmt|;
specifier|final
name|MyIndexWriter
name|w
init|=
operator|new
name|MyIndexWriter
argument_list|(
name|d
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
decl_stmt|;
comment|// Try to make an index that requires merging:
name|w
operator|.
name|getConfig
argument_list|()
operator|.
name|setMaxBufferedDocs
argument_list|(
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|2
argument_list|,
literal|11
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|int
name|numStartDocs
init|=
name|atLeast
argument_list|(
literal|20
argument_list|)
decl_stmt|;
specifier|final
name|LineFileDocs
name|docs
init|=
operator|new
name|LineFileDocs
argument_list|(
name|random
argument_list|()
argument_list|,
name|defaultCodecSupportsDocValues
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|docIDX
init|=
literal|0
init|;
name|docIDX
operator|<
name|numStartDocs
condition|;
name|docIDX
operator|++
control|)
block|{
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
name|MergePolicy
name|mp
init|=
name|w
operator|.
name|getConfig
argument_list|()
operator|.
name|getMergePolicy
argument_list|()
decl_stmt|;
specifier|final
name|int
name|mergeAtOnce
init|=
literal|1
operator|+
name|w
operator|.
name|segmentInfos
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|mp
operator|instanceof
name|TieredMergePolicy
condition|)
block|{
operator|(
operator|(
name|TieredMergePolicy
operator|)
name|mp
operator|)
operator|.
name|setMaxMergeAtOnce
argument_list|(
name|mergeAtOnce
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|mp
operator|instanceof
name|LogMergePolicy
condition|)
block|{
operator|(
operator|(
name|LogMergePolicy
operator|)
name|mp
operator|)
operator|.
name|setMergeFactor
argument_list|(
name|mergeAtOnce
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// skip test
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|d
operator|.
name|close
argument_list|()
expr_stmt|;
return|return;
block|}
specifier|final
name|AtomicBoolean
name|doStop
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
name|w
operator|.
name|getConfig
argument_list|()
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|Thread
name|t
init|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
while|while
condition|(
operator|!
name|doStop
operator|.
name|get
argument_list|()
condition|)
block|{
name|w
operator|.
name|updateDocument
argument_list|(
operator|new
name|Term
argument_list|(
literal|"docid"
argument_list|,
literal|""
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|numStartDocs
argument_list|)
argument_list|)
argument_list|,
name|docs
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
comment|// Force deletes to apply
name|w
operator|.
name|getReader
argument_list|()
operator|.
name|close
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
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|t
argument_list|)
throw|;
block|}
block|}
block|}
decl_stmt|;
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
name|w
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|doStop
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|t
operator|.
name|join
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"merge count is "
operator|+
name|w
operator|.
name|mergeCount
operator|.
name|get
argument_list|()
argument_list|,
name|w
operator|.
name|mergeCount
operator|.
name|get
argument_list|()
operator|<=
literal|1
argument_list|)
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|d
operator|.
name|close
argument_list|()
expr_stmt|;
name|docs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

