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
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
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
name|search
operator|.
name|TopDocs
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
name|TestUtil
import|;
end_import

begin_class
DECL|class|TestStressDeletes
specifier|public
class|class
name|TestStressDeletes
extends|extends
name|LuceneTestCase
block|{
comment|/** Make sure that order of adds/deletes across threads is respected as    *  long as each ID is only changed by one thread at a time. */
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|numIDs
init|=
name|atLeast
argument_list|(
literal|100
argument_list|)
decl_stmt|;
specifier|final
name|Object
index|[]
name|locks
init|=
operator|new
name|Object
index|[
name|numIDs
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
name|locks
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|locks
index|[
name|i
index|]
operator|=
operator|new
name|Object
argument_list|()
expr_stmt|;
block|}
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriterConfig
name|iwc
init|=
operator|new
name|IndexWriterConfig
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
specifier|final
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|iwc
argument_list|)
decl_stmt|;
specifier|final
name|int
name|iters
init|=
name|atLeast
argument_list|(
literal|2000
argument_list|)
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|Boolean
argument_list|>
name|exists
init|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|Thread
index|[]
name|threads
init|=
operator|new
name|Thread
index|[
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|2
argument_list|,
literal|6
argument_list|)
index|]
decl_stmt|;
specifier|final
name|CountDownLatch
name|startingGun
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
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
name|threads
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|threads
index|[
name|i
index|]
operator|=
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
name|startingGun
operator|.
name|await
argument_list|()
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
name|iters
condition|;
name|iter
operator|++
control|)
block|{
name|int
name|id
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|numIDs
argument_list|)
decl_stmt|;
synchronized|synchronized
init|(
name|locks
index|[
name|id
index|]
init|)
block|{
name|Boolean
name|v
init|=
name|exists
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|==
literal|null
operator|||
name|v
operator|.
name|booleanValue
argument_list|()
operator|==
literal|false
condition|)
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
name|newStringField
argument_list|(
literal|"id"
argument_list|,
literal|""
operator|+
name|id
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|exists
operator|.
name|put
argument_list|(
name|id
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|w
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|""
operator|+
name|id
argument_list|)
argument_list|)
expr_stmt|;
name|exists
operator|.
name|put
argument_list|(
name|id
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|500
argument_list|)
operator|==
literal|2
condition|)
block|{
name|DirectoryReader
operator|.
name|open
argument_list|(
name|w
argument_list|,
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|500
argument_list|)
operator|==
literal|2
condition|)
block|{
name|w
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
expr_stmt|;
name|threads
index|[
name|i
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|startingGun
operator|.
name|countDown
argument_list|()
expr_stmt|;
for|for
control|(
name|Thread
name|thread
range|:
name|threads
control|)
block|{
name|thread
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
name|IndexReader
name|r
init|=
name|w
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|IndexSearcher
name|s
init|=
name|newSearcher
argument_list|(
name|r
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Integer
argument_list|,
name|Boolean
argument_list|>
name|ent
range|:
name|exists
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|int
name|id
init|=
name|ent
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|TopDocs
name|hits
init|=
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
literal|"id"
argument_list|,
literal|""
operator|+
name|id
argument_list|)
argument_list|)
argument_list|,
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|ent
operator|.
name|getValue
argument_list|()
condition|)
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|hits
operator|.
name|totalHits
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|hits
operator|.
name|totalHits
argument_list|)
expr_stmt|;
block|}
block|}
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|w
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

