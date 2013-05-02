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
name|ArrayList
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
name|Map
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
name|document
operator|.
name|IntField
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
name|StringField
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
name|StoredDocument
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
name|_TestUtil
import|;
end_import

begin_class
DECL|class|TestLiveFieldValues
specifier|public
class|class
name|TestLiveFieldValues
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
name|Directory
name|dir
init|=
name|newFSDirectory
argument_list|(
name|_TestUtil
operator|.
name|getTempDir
argument_list|(
literal|"livefieldupdates"
argument_list|)
argument_list|)
decl_stmt|;
name|IndexWriterConfig
name|iwc
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
name|SearcherManager
name|mgr
init|=
operator|new
name|SearcherManager
argument_list|(
name|w
argument_list|,
literal|true
argument_list|,
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
block|{
return|return
operator|new
name|IndexSearcher
argument_list|(
name|r
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
specifier|final
name|Integer
name|missing
init|=
operator|-
literal|1
decl_stmt|;
specifier|final
name|LiveFieldValues
argument_list|<
name|Integer
argument_list|>
name|rt
init|=
operator|new
name|LiveFieldValues
argument_list|<
name|Integer
argument_list|>
argument_list|(
name|mgr
argument_list|,
name|missing
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|Integer
name|lookupFromSearcher
parameter_list|(
name|IndexSearcher
name|s
parameter_list|,
name|String
name|id
parameter_list|)
throws|throws
name|IOException
block|{
name|TermQuery
name|tq
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
name|id
argument_list|)
argument_list|)
decl_stmt|;
name|TopDocs
name|hits
init|=
name|s
operator|.
name|search
argument_list|(
name|tq
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|hits
operator|.
name|totalHits
operator|<=
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|hits
operator|.
name|totalHits
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
name|StoredDocument
name|doc
init|=
name|s
operator|.
name|doc
argument_list|(
name|hits
operator|.
name|scoreDocs
index|[
literal|0
index|]
operator|.
name|doc
argument_list|)
decl_stmt|;
return|return
operator|(
name|Integer
operator|)
name|doc
operator|.
name|getField
argument_list|(
literal|"field"
argument_list|)
operator|.
name|numericValue
argument_list|()
return|;
block|}
block|}
block|}
decl_stmt|;
name|int
name|numThreads
init|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|2
argument_list|,
literal|5
argument_list|)
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
name|numThreads
operator|+
literal|" threads"
argument_list|)
expr_stmt|;
block|}
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
name|List
argument_list|<
name|Thread
argument_list|>
name|threads
init|=
operator|new
name|ArrayList
argument_list|<
name|Thread
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|int
name|iters
init|=
name|atLeast
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
specifier|final
name|int
name|idCount
init|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|100
argument_list|,
literal|10000
argument_list|)
decl_stmt|;
specifier|final
name|double
name|reopenChance
init|=
name|random
argument_list|()
operator|.
name|nextDouble
argument_list|()
operator|*
literal|0.01
decl_stmt|;
specifier|final
name|double
name|deleteChance
init|=
name|random
argument_list|()
operator|.
name|nextDouble
argument_list|()
operator|*
literal|0.25
decl_stmt|;
specifier|final
name|double
name|addChance
init|=
name|random
argument_list|()
operator|.
name|nextDouble
argument_list|()
operator|*
literal|0.5
decl_stmt|;
for|for
control|(
name|int
name|t
init|=
literal|0
init|;
name|t
operator|<
name|numThreads
condition|;
name|t
operator|++
control|)
block|{
specifier|final
name|int
name|threadID
init|=
name|t
decl_stmt|;
specifier|final
name|Random
name|threadRandom
init|=
operator|new
name|Random
argument_list|(
name|random
argument_list|()
operator|.
name|nextLong
argument_list|()
argument_list|)
decl_stmt|;
name|Thread
name|thread
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
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|values
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|allIDs
init|=
name|Collections
operator|.
name|synchronizedList
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
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
comment|// Add/update a document
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
comment|// Threads must not update the same id at the
comment|// same time:
if|if
condition|(
name|threadRandom
operator|.
name|nextDouble
argument_list|()
operator|<=
name|addChance
condition|)
block|{
name|String
name|id
init|=
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"%d_%04x"
argument_list|,
name|threadID
argument_list|,
name|threadRandom
operator|.
name|nextInt
argument_list|(
name|idCount
argument_list|)
argument_list|)
decl_stmt|;
name|Integer
name|field
init|=
name|threadRandom
operator|.
name|nextInt
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"id"
argument_list|,
name|id
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|IntField
argument_list|(
literal|"field"
argument_list|,
name|field
operator|.
name|intValue
argument_list|()
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|updateDocument
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
name|id
argument_list|)
argument_list|,
name|doc
argument_list|)
expr_stmt|;
name|rt
operator|.
name|add
argument_list|(
name|id
argument_list|,
name|field
argument_list|)
expr_stmt|;
if|if
condition|(
name|values
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|field
argument_list|)
operator|==
literal|null
condition|)
block|{
name|allIDs
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|allIDs
operator|.
name|size
argument_list|()
operator|>
literal|0
operator|&&
name|threadRandom
operator|.
name|nextDouble
argument_list|()
operator|<=
name|deleteChance
condition|)
block|{
name|String
name|randomID
init|=
name|allIDs
operator|.
name|get
argument_list|(
name|threadRandom
operator|.
name|nextInt
argument_list|(
name|allIDs
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|w
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
name|randomID
argument_list|)
argument_list|)
expr_stmt|;
name|rt
operator|.
name|delete
argument_list|(
name|randomID
argument_list|)
expr_stmt|;
name|values
operator|.
name|put
argument_list|(
name|randomID
argument_list|,
name|missing
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|threadRandom
operator|.
name|nextDouble
argument_list|()
operator|<=
name|reopenChance
operator|||
name|rt
operator|.
name|size
argument_list|()
operator|>
literal|10000
condition|)
block|{
comment|//System.out.println("refresh @ " + rt.size());
name|mgr
operator|.
name|maybeRefresh
argument_list|()
expr_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|IndexSearcher
name|s
init|=
name|mgr
operator|.
name|acquire
argument_list|()
decl_stmt|;
try|try
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: reopen "
operator|+
name|s
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|mgr
operator|.
name|release
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: "
operator|+
name|values
operator|.
name|size
argument_list|()
operator|+
literal|" values"
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|threadRandom
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
operator|==
literal|7
condition|)
block|{
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|rt
operator|.
name|get
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|allIDs
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|String
name|randomID
init|=
name|allIDs
operator|.
name|get
argument_list|(
name|threadRandom
operator|.
name|nextInt
argument_list|(
name|allIDs
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|Integer
name|expected
init|=
name|values
operator|.
name|get
argument_list|(
name|randomID
argument_list|)
decl_stmt|;
if|if
condition|(
name|expected
operator|==
name|missing
condition|)
block|{
name|expected
operator|=
literal|null
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"id="
operator|+
name|randomID
argument_list|,
name|expected
argument_list|,
name|rt
operator|.
name|get
argument_list|(
name|randomID
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|threads
operator|.
name|add
argument_list|(
name|thread
argument_list|)
expr_stmt|;
name|thread
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
name|mgr
operator|.
name|maybeRefresh
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|rt
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|rt
operator|.
name|close
argument_list|()
expr_stmt|;
name|mgr
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

