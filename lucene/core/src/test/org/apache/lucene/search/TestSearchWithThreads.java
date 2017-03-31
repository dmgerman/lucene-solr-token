begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|AtomicLong
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
name|LuceneTestCase
operator|.
name|SuppressCodecs
import|;
end_import

begin_class
annotation|@
name|SuppressCodecs
argument_list|(
block|{
literal|"SimpleText"
block|,
literal|"Memory"
block|,
literal|"Direct"
block|}
argument_list|)
DECL|class|TestSearchWithThreads
specifier|public
class|class
name|TestSearchWithThreads
extends|extends
name|LuceneTestCase
block|{
DECL|field|NUM_DOCS
name|int
name|NUM_DOCS
decl_stmt|;
DECL|field|NUM_SEARCH_THREADS
specifier|static
specifier|final
name|int
name|NUM_SEARCH_THREADS
init|=
literal|5
decl_stmt|;
DECL|field|RUN_TIME_MSEC
name|int
name|RUN_TIME_MSEC
decl_stmt|;
annotation|@
name|Override
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|NUM_DOCS
operator|=
name|atLeast
argument_list|(
literal|10000
argument_list|)
expr_stmt|;
name|RUN_TIME_MSEC
operator|=
name|atLeast
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
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
specifier|final
name|long
name|startTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
comment|// TODO: replace w/ the @nightly test data; make this
comment|// into an optional @nightly stress test
specifier|final
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
specifier|final
name|Field
name|body
init|=
name|newTextField
argument_list|(
literal|"body"
argument_list|,
literal|""
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|body
argument_list|)
expr_stmt|;
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|docCount
init|=
literal|0
init|;
name|docCount
operator|<
name|NUM_DOCS
condition|;
name|docCount
operator|++
control|)
block|{
specifier|final
name|int
name|numTerms
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|termCount
init|=
literal|0
init|;
name|termCount
operator|<
name|numTerms
condition|;
name|termCount
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
literal|"aaa"
else|:
literal|"bbb"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
name|body
operator|.
name|setStringValue
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|sb
operator|.
name|delete
argument_list|(
literal|0
argument_list|,
name|sb
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|IndexReader
name|r
init|=
name|w
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
specifier|final
name|long
name|endTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"BUILD took "
operator|+
operator|(
name|endTime
operator|-
name|startTime
operator|)
argument_list|)
expr_stmt|;
specifier|final
name|IndexSearcher
name|s
init|=
name|newSearcher
argument_list|(
name|r
argument_list|)
decl_stmt|;
specifier|final
name|AtomicBoolean
name|failed
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
specifier|final
name|AtomicLong
name|netSearch
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
name|Thread
index|[]
name|threads
init|=
operator|new
name|Thread
index|[
name|NUM_SEARCH_THREADS
index|]
decl_stmt|;
for|for
control|(
name|int
name|threadID
init|=
literal|0
init|;
name|threadID
operator|<
name|NUM_SEARCH_THREADS
condition|;
name|threadID
operator|++
control|)
block|{
name|threads
index|[
name|threadID
index|]
operator|=
operator|new
name|Thread
argument_list|()
block|{
name|TotalHitCountCollector
name|col
init|=
operator|new
name|TotalHitCountCollector
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|long
name|totHits
init|=
literal|0
decl_stmt|;
name|long
name|totSearch
init|=
literal|0
decl_stmt|;
name|long
name|stopAt
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
name|RUN_TIME_MSEC
decl_stmt|;
while|while
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|<
name|stopAt
operator|&&
operator|!
name|failed
operator|.
name|get
argument_list|()
condition|)
block|{
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
literal|"aaa"
argument_list|)
argument_list|)
argument_list|,
name|col
argument_list|)
expr_stmt|;
name|totHits
operator|+=
name|col
operator|.
name|getTotalHits
argument_list|()
expr_stmt|;
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
literal|"bbb"
argument_list|)
argument_list|)
argument_list|,
name|col
argument_list|)
expr_stmt|;
name|totHits
operator|+=
name|col
operator|.
name|getTotalHits
argument_list|()
expr_stmt|;
name|totSearch
operator|++
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|totSearch
operator|>
literal|0
operator|&&
name|totHits
operator|>
literal|0
argument_list|)
expr_stmt|;
name|netSearch
operator|.
name|addAndGet
argument_list|(
name|totSearch
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|exc
parameter_list|)
block|{
name|failed
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|exc
argument_list|)
throw|;
block|}
block|}
block|}
expr_stmt|;
name|threads
index|[
name|threadID
index|]
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Thread
name|t
range|:
name|threads
control|)
block|{
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Thread
name|t
range|:
name|threads
control|)
block|{
name|t
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|VERBOSE
condition|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|NUM_SEARCH_THREADS
operator|+
literal|" threads did "
operator|+
name|netSearch
operator|.
name|get
argument_list|()
operator|+
literal|" searches"
argument_list|)
expr_stmt|;
name|r
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

