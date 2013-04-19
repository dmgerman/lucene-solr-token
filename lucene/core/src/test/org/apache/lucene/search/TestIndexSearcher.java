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
name|LinkedBlockingQueue
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
name|ThreadPoolExecutor
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
name|TimeUnit
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
name|BytesRef
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
name|NamedThreadFactory
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
DECL|class|TestIndexSearcher
specifier|public
class|class
name|TestIndexSearcher
extends|extends
name|LuceneTestCase
block|{
DECL|field|dir
name|Directory
name|dir
decl_stmt|;
DECL|field|reader
name|IndexReader
name|reader
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
name|dir
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|RandomIndexWriter
name|iw
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
literal|"field"
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
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newStringField
argument_list|(
literal|"field2"
argument_list|,
name|Boolean
operator|.
name|toString
argument_list|(
name|i
operator|%
literal|2
operator|==
literal|0
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|reader
operator|=
name|iw
operator|.
name|getReader
argument_list|()
expr_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
name|reader
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
comment|// should not throw exception
DECL|method|testHugeN
specifier|public
name|void
name|testHugeN
parameter_list|()
throws|throws
name|Exception
block|{
name|ExecutorService
name|service
init|=
operator|new
name|ThreadPoolExecutor
argument_list|(
literal|4
argument_list|,
literal|4
argument_list|,
literal|0L
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|,
operator|new
name|LinkedBlockingQueue
argument_list|<
name|Runnable
argument_list|>
argument_list|()
argument_list|,
operator|new
name|NamedThreadFactory
argument_list|(
literal|"TestIndexSearcher"
argument_list|)
argument_list|)
decl_stmt|;
name|IndexSearcher
name|searchers
index|[]
init|=
operator|new
name|IndexSearcher
index|[]
block|{
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
block|,
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|,
name|service
argument_list|)
block|}
decl_stmt|;
name|Query
name|queries
index|[]
init|=
operator|new
name|Query
index|[]
block|{
operator|new
name|MatchAllDocsQuery
argument_list|()
block|,
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
block|}
decl_stmt|;
name|Sort
name|sorts
index|[]
init|=
operator|new
name|Sort
index|[]
block|{
literal|null
block|,
operator|new
name|Sort
argument_list|(
operator|new
name|SortField
argument_list|(
literal|"field2"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|STRING
argument_list|)
argument_list|)
block|}
decl_stmt|;
name|Filter
name|filters
index|[]
init|=
operator|new
name|Filter
index|[]
block|{
literal|null
block|,
operator|new
name|QueryWrapperFilter
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field2"
argument_list|,
literal|"true"
argument_list|)
argument_list|)
argument_list|)
block|}
decl_stmt|;
name|ScoreDoc
name|afters
index|[]
init|=
operator|new
name|ScoreDoc
index|[]
block|{
literal|null
block|,
operator|new
name|FieldDoc
argument_list|(
literal|0
argument_list|,
literal|0f
argument_list|,
operator|new
name|Object
index|[]
block|{
operator|new
name|BytesRef
argument_list|(
literal|"boo!"
argument_list|)
block|}
argument_list|)
block|}
decl_stmt|;
for|for
control|(
name|IndexSearcher
name|searcher
range|:
name|searchers
control|)
block|{
for|for
control|(
name|ScoreDoc
name|after
range|:
name|afters
control|)
block|{
for|for
control|(
name|Query
name|query
range|:
name|queries
control|)
block|{
for|for
control|(
name|Sort
name|sort
range|:
name|sorts
control|)
block|{
for|for
control|(
name|Filter
name|filter
range|:
name|filters
control|)
block|{
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|searchAfter
argument_list|(
name|after
argument_list|,
name|query
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|filter
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|searchAfter
argument_list|(
name|after
argument_list|,
name|query
argument_list|,
name|filter
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
if|if
condition|(
name|sort
operator|!=
literal|null
condition|)
block|{
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|sort
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|filter
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|sort
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|filter
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|sort
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|filter
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|sort
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|filter
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|sort
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|filter
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|sort
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|searchAfter
argument_list|(
name|after
argument_list|,
name|query
argument_list|,
name|filter
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|sort
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|searchAfter
argument_list|(
name|after
argument_list|,
name|query
argument_list|,
name|filter
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|sort
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|searchAfter
argument_list|(
name|after
argument_list|,
name|query
argument_list|,
name|filter
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|sort
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|searchAfter
argument_list|(
name|after
argument_list|,
name|query
argument_list|,
name|filter
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|sort
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|searchAfter
argument_list|(
name|after
argument_list|,
name|query
argument_list|,
name|filter
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|sort
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
name|_TestUtil
operator|.
name|shutdownExecutorService
argument_list|(
name|service
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

