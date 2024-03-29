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
operator|.
name|Store
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
name|NumericDocValuesField
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
name|MockRandomMergePolicy
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
name|QueryTimeout
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
name|SerialMergeScheduler
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
name|TestUtil
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
name|generators
operator|.
name|RandomPicks
import|;
end_import

begin_class
DECL|class|TestEarlyTerminatingSortingCollector
specifier|public
class|class
name|TestEarlyTerminatingSortingCollector
extends|extends
name|LuceneTestCase
block|{
DECL|field|numDocs
specifier|private
name|int
name|numDocs
decl_stmt|;
DECL|field|terms
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|terms
decl_stmt|;
DECL|field|dir
specifier|private
name|Directory
name|dir
decl_stmt|;
DECL|field|sort
specifier|private
specifier|final
name|Sort
name|sort
init|=
operator|new
name|Sort
argument_list|(
operator|new
name|SortField
argument_list|(
literal|"ndv1"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|LONG
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|iw
specifier|private
name|RandomIndexWriter
name|iw
decl_stmt|;
DECL|field|reader
specifier|private
name|IndexReader
name|reader
decl_stmt|;
DECL|field|FORCE_MERGE_MAX_SEGMENT_COUNT
specifier|private
specifier|static
specifier|final
name|int
name|FORCE_MERGE_MAX_SEGMENT_COUNT
init|=
literal|5
decl_stmt|;
DECL|method|randomDocument
specifier|private
name|Document
name|randomDocument
parameter_list|()
block|{
specifier|final
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
name|NumericDocValuesField
argument_list|(
literal|"ndv1"
argument_list|,
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|NumericDocValuesField
argument_list|(
literal|"ndv2"
argument_list|,
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"s"
argument_list|,
name|RandomPicks
operator|.
name|randomFrom
argument_list|(
name|random
argument_list|()
argument_list|,
name|terms
argument_list|)
argument_list|,
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|doc
return|;
block|}
DECL|method|createRandomIndex
specifier|private
name|void
name|createRandomIndex
parameter_list|(
name|boolean
name|singleSortedSegment
parameter_list|)
throws|throws
name|IOException
block|{
name|dir
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|numDocs
operator|=
name|atLeast
argument_list|(
literal|150
argument_list|)
expr_stmt|;
specifier|final
name|int
name|numTerms
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
name|numDocs
operator|/
literal|5
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|randomTerms
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
while|while
condition|(
name|randomTerms
operator|.
name|size
argument_list|()
operator|<
name|numTerms
condition|)
block|{
name|randomTerms
operator|.
name|add
argument_list|(
name|TestUtil
operator|.
name|randomSimpleString
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|terms
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|randomTerms
argument_list|)
expr_stmt|;
specifier|final
name|long
name|seed
init|=
name|random
argument_list|()
operator|.
name|nextLong
argument_list|()
decl_stmt|;
specifier|final
name|IndexWriterConfig
name|iwc
init|=
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|iwc
operator|.
name|getMergePolicy
argument_list|()
operator|instanceof
name|MockRandomMergePolicy
condition|)
block|{
comment|// MockRandomMP randomly wraps the leaf readers which makes merging angry
name|iwc
operator|.
name|setMergePolicy
argument_list|(
name|newTieredMergePolicy
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|iwc
operator|.
name|setMergeScheduler
argument_list|(
operator|new
name|SerialMergeScheduler
argument_list|()
argument_list|)
expr_stmt|;
comment|// for reproducible tests
name|iwc
operator|.
name|setIndexSort
argument_list|(
name|sort
argument_list|)
expr_stmt|;
name|iw
operator|=
operator|new
name|RandomIndexWriter
argument_list|(
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
argument_list|,
name|dir
argument_list|,
name|iwc
argument_list|)
expr_stmt|;
name|iw
operator|.
name|setDoRandomForceMerge
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// don't do this, it may happen anyway with MockRandomMP
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numDocs
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|Document
name|doc
init|=
name|randomDocument
argument_list|()
decl_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|==
name|numDocs
operator|/
literal|2
operator|||
operator|(
name|i
operator|!=
name|numDocs
operator|-
literal|1
operator|&&
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|8
argument_list|)
operator|==
literal|0
operator|)
condition|)
block|{
name|iw
operator|.
name|commit
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
literal|15
argument_list|)
operator|==
literal|0
condition|)
block|{
specifier|final
name|String
name|term
init|=
name|RandomPicks
operator|.
name|randomFrom
argument_list|(
name|random
argument_list|()
argument_list|,
name|terms
argument_list|)
decl_stmt|;
name|iw
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"s"
argument_list|,
name|term
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|singleSortedSegment
condition|)
block|{
comment|// because of deletions, there might still be a single flush segment in
comment|// the index, although want want a sorted segment so it needs to be merged
name|iw
operator|.
name|getReader
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// refresh
name|iw
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
expr_stmt|;
name|iw
operator|.
name|commit
argument_list|()
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
expr_stmt|;
name|iw
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|iw
operator|.
name|forceMerge
argument_list|(
name|FORCE_MERGE_MAX_SEGMENT_COUNT
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
block|}
DECL|method|closeIndex
specifier|private
name|void
name|closeIndex
parameter_list|()
throws|throws
name|IOException
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|iw
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
DECL|method|testEarlyTermination
specifier|public
name|void
name|testEarlyTermination
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|int
name|iters
init|=
name|atLeast
argument_list|(
literal|8
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
name|iters
condition|;
operator|++
name|i
control|)
block|{
name|createRandomIndex
argument_list|(
literal|false
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|iters
condition|;
operator|++
name|j
control|)
block|{
specifier|final
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numHits
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
name|numDocs
argument_list|)
decl_stmt|;
specifier|final
name|Sort
name|sort
init|=
operator|new
name|Sort
argument_list|(
operator|new
name|SortField
argument_list|(
literal|"ndv1"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|LONG
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|fillFields
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
specifier|final
name|boolean
name|trackDocScores
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
specifier|final
name|boolean
name|trackMaxScore
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
specifier|final
name|TopFieldCollector
name|collector1
init|=
name|TopFieldCollector
operator|.
name|create
argument_list|(
name|sort
argument_list|,
name|numHits
argument_list|,
name|fillFields
argument_list|,
name|trackDocScores
argument_list|,
name|trackMaxScore
argument_list|)
decl_stmt|;
specifier|final
name|TopFieldCollector
name|collector2
init|=
name|TopFieldCollector
operator|.
name|create
argument_list|(
name|sort
argument_list|,
name|numHits
argument_list|,
name|fillFields
argument_list|,
name|trackDocScores
argument_list|,
name|trackMaxScore
argument_list|)
decl_stmt|;
specifier|final
name|Query
name|query
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
name|query
operator|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"s"
argument_list|,
name|RandomPicks
operator|.
name|randomFrom
argument_list|(
name|random
argument_list|()
argument_list|,
name|terms
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|query
operator|=
operator|new
name|MatchAllDocsQuery
argument_list|()
expr_stmt|;
block|}
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|collector1
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
operator|new
name|EarlyTerminatingSortingCollector
argument_list|(
name|collector2
argument_list|,
name|sort
argument_list|,
name|numHits
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|collector1
operator|.
name|getTotalHits
argument_list|()
operator|>=
name|collector2
operator|.
name|getTotalHits
argument_list|()
argument_list|)
expr_stmt|;
name|assertTopDocsEquals
argument_list|(
name|collector1
operator|.
name|topDocs
argument_list|()
operator|.
name|scoreDocs
argument_list|,
name|collector2
operator|.
name|topDocs
argument_list|()
operator|.
name|scoreDocs
argument_list|)
expr_stmt|;
block|}
name|closeIndex
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|testCanEarlyTerminate
specifier|public
name|void
name|testCanEarlyTerminate
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|EarlyTerminatingSortingCollector
operator|.
name|canEarlyTerminate
argument_list|(
operator|new
name|Sort
argument_list|(
operator|new
name|SortField
argument_list|(
literal|"a"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|LONG
argument_list|)
argument_list|)
argument_list|,
operator|new
name|Sort
argument_list|(
operator|new
name|SortField
argument_list|(
literal|"a"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|LONG
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|EarlyTerminatingSortingCollector
operator|.
name|canEarlyTerminate
argument_list|(
operator|new
name|Sort
argument_list|(
operator|new
name|SortField
argument_list|(
literal|"a"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|LONG
argument_list|)
argument_list|,
operator|new
name|SortField
argument_list|(
literal|"b"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|STRING
argument_list|)
argument_list|)
argument_list|,
operator|new
name|Sort
argument_list|(
operator|new
name|SortField
argument_list|(
literal|"a"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|LONG
argument_list|)
argument_list|,
operator|new
name|SortField
argument_list|(
literal|"b"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|STRING
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|EarlyTerminatingSortingCollector
operator|.
name|canEarlyTerminate
argument_list|(
operator|new
name|Sort
argument_list|(
operator|new
name|SortField
argument_list|(
literal|"a"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|LONG
argument_list|)
argument_list|)
argument_list|,
operator|new
name|Sort
argument_list|(
operator|new
name|SortField
argument_list|(
literal|"a"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|LONG
argument_list|)
argument_list|,
operator|new
name|SortField
argument_list|(
literal|"b"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|STRING
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|EarlyTerminatingSortingCollector
operator|.
name|canEarlyTerminate
argument_list|(
operator|new
name|Sort
argument_list|(
operator|new
name|SortField
argument_list|(
literal|"a"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|LONG
argument_list|,
literal|true
argument_list|)
argument_list|)
argument_list|,
operator|new
name|Sort
argument_list|(
operator|new
name|SortField
argument_list|(
literal|"a"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|LONG
argument_list|,
literal|false
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|EarlyTerminatingSortingCollector
operator|.
name|canEarlyTerminate
argument_list|(
operator|new
name|Sort
argument_list|(
operator|new
name|SortField
argument_list|(
literal|"a"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|LONG
argument_list|)
argument_list|,
operator|new
name|SortField
argument_list|(
literal|"b"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|STRING
argument_list|)
argument_list|)
argument_list|,
operator|new
name|Sort
argument_list|(
operator|new
name|SortField
argument_list|(
literal|"a"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|LONG
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|EarlyTerminatingSortingCollector
operator|.
name|canEarlyTerminate
argument_list|(
operator|new
name|Sort
argument_list|(
operator|new
name|SortField
argument_list|(
literal|"a"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|LONG
argument_list|)
argument_list|,
operator|new
name|SortField
argument_list|(
literal|"b"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|STRING
argument_list|)
argument_list|)
argument_list|,
operator|new
name|Sort
argument_list|(
operator|new
name|SortField
argument_list|(
literal|"a"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|LONG
argument_list|)
argument_list|,
operator|new
name|SortField
argument_list|(
literal|"c"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|STRING
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|EarlyTerminatingSortingCollector
operator|.
name|canEarlyTerminate
argument_list|(
operator|new
name|Sort
argument_list|(
operator|new
name|SortField
argument_list|(
literal|"a"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|LONG
argument_list|)
argument_list|,
operator|new
name|SortField
argument_list|(
literal|"b"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|STRING
argument_list|)
argument_list|)
argument_list|,
operator|new
name|Sort
argument_list|(
operator|new
name|SortField
argument_list|(
literal|"c"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|LONG
argument_list|)
argument_list|,
operator|new
name|SortField
argument_list|(
literal|"b"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|STRING
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testEarlyTerminationDifferentSorter
specifier|public
name|void
name|testEarlyTerminationDifferentSorter
parameter_list|()
throws|throws
name|IOException
block|{
name|createRandomIndex
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Sort
name|sort
init|=
operator|new
name|Sort
argument_list|(
operator|new
name|SortField
argument_list|(
literal|"ndv2"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|LONG
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|Collector
name|c
init|=
operator|new
name|EarlyTerminatingSortingCollector
argument_list|(
name|TopFieldCollector
operator|.
name|create
argument_list|(
name|sort
argument_list|,
literal|10
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|,
name|sort
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|Exception
name|e
init|=
name|expectThrows
argument_list|(
name|IllegalStateException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Cannot early terminate with sort order<long: \"ndv2\"> if segments are sorted with<long: \"ndv1\">"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|closeIndex
argument_list|()
expr_stmt|;
block|}
DECL|method|assertTopDocsEquals
specifier|private
specifier|static
name|void
name|assertTopDocsEquals
parameter_list|(
name|ScoreDoc
index|[]
name|scoreDocs1
parameter_list|,
name|ScoreDoc
index|[]
name|scoreDocs2
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|scoreDocs1
operator|.
name|length
argument_list|,
name|scoreDocs2
operator|.
name|length
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
name|scoreDocs1
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|ScoreDoc
name|scoreDoc1
init|=
name|scoreDocs1
index|[
name|i
index|]
decl_stmt|;
specifier|final
name|ScoreDoc
name|scoreDoc2
init|=
name|scoreDocs2
index|[
name|i
index|]
decl_stmt|;
name|assertEquals
argument_list|(
name|scoreDoc1
operator|.
name|doc
argument_list|,
name|scoreDoc2
operator|.
name|doc
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|scoreDoc1
operator|.
name|score
argument_list|,
name|scoreDoc2
operator|.
name|score
argument_list|,
literal|0.001f
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|TestTerminatedEarlySimpleCollector
specifier|private
specifier|static
class|class
name|TestTerminatedEarlySimpleCollector
extends|extends
name|SimpleCollector
block|{
DECL|field|collectedSomething
specifier|private
name|boolean
name|collectedSomething
decl_stmt|;
DECL|method|collectedSomething
specifier|public
name|boolean
name|collectedSomething
parameter_list|()
block|{
return|return
name|collectedSomething
return|;
block|}
annotation|@
name|Override
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|collectedSomething
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|needsScores
specifier|public
name|boolean
name|needsScores
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
DECL|class|TestEarlyTerminatingSortingcollectorQueryTimeout
specifier|private
specifier|static
class|class
name|TestEarlyTerminatingSortingcollectorQueryTimeout
implements|implements
name|QueryTimeout
block|{
DECL|field|shouldExit
specifier|final
specifier|private
name|boolean
name|shouldExit
decl_stmt|;
DECL|method|TestEarlyTerminatingSortingcollectorQueryTimeout
specifier|public
name|TestEarlyTerminatingSortingcollectorQueryTimeout
parameter_list|(
name|boolean
name|shouldExit
parameter_list|)
block|{
name|this
operator|.
name|shouldExit
operator|=
name|shouldExit
expr_stmt|;
block|}
DECL|method|shouldExit
specifier|public
name|boolean
name|shouldExit
parameter_list|()
block|{
return|return
name|shouldExit
return|;
block|}
block|}
DECL|method|testTerminatedEarly
specifier|public
name|void
name|testTerminatedEarly
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|int
name|iters
init|=
name|atLeast
argument_list|(
literal|8
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
name|iters
condition|;
operator|++
name|i
control|)
block|{
name|createRandomIndex
argument_list|(
literal|true
argument_list|)
expr_stmt|;
specifier|final
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
comment|// future TODO: use newSearcher(reader);
specifier|final
name|Query
name|query
init|=
operator|new
name|MatchAllDocsQuery
argument_list|()
decl_stmt|;
comment|// search for everything/anything
specifier|final
name|TestTerminatedEarlySimpleCollector
name|collector1
init|=
operator|new
name|TestTerminatedEarlySimpleCollector
argument_list|()
decl_stmt|;
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|collector1
argument_list|)
expr_stmt|;
specifier|final
name|TestTerminatedEarlySimpleCollector
name|collector2
init|=
operator|new
name|TestTerminatedEarlySimpleCollector
argument_list|()
decl_stmt|;
specifier|final
name|EarlyTerminatingSortingCollector
name|etsCollector
init|=
operator|new
name|EarlyTerminatingSortingCollector
argument_list|(
name|collector2
argument_list|,
name|sort
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|etsCollector
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"collector1="
operator|+
name|collector1
operator|.
name|collectedSomething
argument_list|()
operator|+
literal|" vs. collector2="
operator|+
name|collector2
operator|.
name|collectedSomething
argument_list|()
argument_list|,
name|collector1
operator|.
name|collectedSomething
argument_list|()
operator|==
name|collector2
operator|.
name|collectedSomething
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|collector1
operator|.
name|collectedSomething
argument_list|()
condition|)
block|{
comment|// we collected something and since we modestly asked for just one document we should have terminated early
name|assertTrue
argument_list|(
literal|"should have terminated early (searcher.reader="
operator|+
name|searcher
operator|.
name|reader
operator|+
literal|")"
argument_list|,
name|etsCollector
operator|.
name|terminatedEarly
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|closeIndex
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

