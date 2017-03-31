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
name|LeafReaderContext
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
name|MultiDocValues
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
name|MultiFields
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
name|NumericDocValues
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
name|Bits
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
name|FixedBitSet
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
import|;
end_import

begin_comment
comment|/**  * Abstract class to do basic tests for a RangeField query. Testing rigor inspired by {@code BaseGeoPointTestCase}  */
end_comment

begin_class
DECL|class|BaseRangeFieldQueryTestCase
specifier|public
specifier|abstract
class|class
name|BaseRangeFieldQueryTestCase
extends|extends
name|LuceneTestCase
block|{
DECL|method|newRangeField
specifier|protected
specifier|abstract
name|Field
name|newRangeField
parameter_list|(
name|Range
name|box
parameter_list|)
function_decl|;
DECL|method|newIntersectsQuery
specifier|protected
specifier|abstract
name|Query
name|newIntersectsQuery
parameter_list|(
name|Range
name|box
parameter_list|)
function_decl|;
DECL|method|newContainsQuery
specifier|protected
specifier|abstract
name|Query
name|newContainsQuery
parameter_list|(
name|Range
name|box
parameter_list|)
function_decl|;
DECL|method|newWithinQuery
specifier|protected
specifier|abstract
name|Query
name|newWithinQuery
parameter_list|(
name|Range
name|box
parameter_list|)
function_decl|;
DECL|method|newCrossesQuery
specifier|protected
specifier|abstract
name|Query
name|newCrossesQuery
parameter_list|(
name|Range
name|box
parameter_list|)
function_decl|;
DECL|method|nextRange
specifier|protected
specifier|abstract
name|Range
name|nextRange
parameter_list|(
name|int
name|dimensions
parameter_list|)
throws|throws
name|Exception
function_decl|;
DECL|method|dimension
specifier|protected
name|int
name|dimension
parameter_list|()
block|{
return|return
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|4
argument_list|)
operator|+
literal|1
return|;
block|}
DECL|method|testRandomTiny
specifier|public
name|void
name|testRandomTiny
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Make sure single-leaf-node case is OK:
name|doTestRandom
argument_list|(
literal|10
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|testRandomMedium
specifier|public
name|void
name|testRandomMedium
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestRandom
argument_list|(
literal|10000
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Nightly
DECL|method|testRandomBig
specifier|public
name|void
name|testRandomBig
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestRandom
argument_list|(
literal|200000
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|testMultiValued
specifier|public
name|void
name|testMultiValued
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestRandom
argument_list|(
literal|10000
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|doTestRandom
specifier|private
name|void
name|doTestRandom
parameter_list|(
name|int
name|count
parameter_list|,
name|boolean
name|multiValued
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|numDocs
init|=
name|atLeast
argument_list|(
name|count
argument_list|)
decl_stmt|;
name|int
name|dimensions
init|=
name|dimension
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
literal|"TEST: numDocs="
operator|+
name|numDocs
argument_list|)
expr_stmt|;
block|}
name|Range
index|[]
index|[]
name|ranges
init|=
operator|new
name|Range
index|[
name|numDocs
index|]
index|[]
decl_stmt|;
name|boolean
name|haveRealDoc
init|=
literal|true
decl_stmt|;
name|nextdoc
label|:
for|for
control|(
name|int
name|id
init|=
literal|0
init|;
name|id
operator|<
name|numDocs
condition|;
operator|++
name|id
control|)
block|{
name|int
name|x
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|20
argument_list|)
decl_stmt|;
if|if
condition|(
name|ranges
index|[
name|id
index|]
operator|==
literal|null
condition|)
block|{
name|ranges
index|[
name|id
index|]
operator|=
operator|new
name|Range
index|[]
block|{
name|nextRange
argument_list|(
name|dimensions
argument_list|)
block|}
expr_stmt|;
block|}
if|if
condition|(
name|x
operator|==
literal|17
condition|)
block|{
comment|// some docs don't have a box:
name|ranges
index|[
name|id
index|]
index|[
literal|0
index|]
operator|.
name|isMissing
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
literal|"  id="
operator|+
name|id
operator|+
literal|" is missing"
argument_list|)
expr_stmt|;
block|}
continue|continue;
block|}
if|if
condition|(
name|multiValued
operator|==
literal|true
operator|&&
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
comment|// randomly add multi valued documents (up to 2 fields)
name|int
name|n
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|2
argument_list|)
operator|+
literal|1
decl_stmt|;
name|ranges
index|[
name|id
index|]
operator|=
operator|new
name|Range
index|[
name|n
index|]
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
name|n
condition|;
operator|++
name|i
control|)
block|{
name|ranges
index|[
name|id
index|]
index|[
name|i
index|]
operator|=
name|nextRange
argument_list|(
name|dimensions
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|id
operator|>
literal|0
operator|&&
name|x
operator|<
literal|9
operator|&&
name|haveRealDoc
condition|)
block|{
name|int
name|oldID
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
comment|// don't step on missing ranges:
while|while
condition|(
literal|true
condition|)
block|{
name|oldID
operator|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|id
argument_list|)
expr_stmt|;
if|if
condition|(
name|ranges
index|[
name|oldID
index|]
index|[
literal|0
index|]
operator|.
name|isMissing
operator|==
literal|false
condition|)
block|{
break|break;
block|}
elseif|else
if|if
condition|(
operator|++
name|i
operator|>
name|id
condition|)
block|{
continue|continue
name|nextdoc
continue|;
block|}
block|}
if|if
condition|(
name|x
operator|==
name|dimensions
operator|*
literal|2
condition|)
block|{
comment|// Fully identical box (use first box in case current is multivalued but old is not)
for|for
control|(
name|int
name|d
init|=
literal|0
init|;
name|d
operator|<
name|dimensions
condition|;
operator|++
name|d
control|)
block|{
name|ranges
index|[
name|id
index|]
index|[
literal|0
index|]
operator|.
name|setMin
argument_list|(
name|d
argument_list|,
name|ranges
index|[
name|oldID
index|]
index|[
literal|0
index|]
operator|.
name|getMin
argument_list|(
name|d
argument_list|)
argument_list|)
expr_stmt|;
name|ranges
index|[
name|id
index|]
index|[
literal|0
index|]
operator|.
name|setMax
argument_list|(
name|d
argument_list|,
name|ranges
index|[
name|oldID
index|]
index|[
literal|0
index|]
operator|.
name|getMax
argument_list|(
name|d
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
literal|"  id="
operator|+
name|id
operator|+
literal|" box="
operator|+
name|ranges
index|[
name|id
index|]
operator|+
literal|" (same box as doc="
operator|+
name|oldID
operator|+
literal|")"
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
for|for
control|(
name|int
name|m
init|=
literal|0
init|,
name|even
init|=
name|dimensions
operator|%
literal|2
init|;
name|m
operator|<
name|dimensions
operator|*
literal|2
condition|;
operator|++
name|m
control|)
block|{
if|if
condition|(
name|x
operator|==
name|m
condition|)
block|{
name|int
name|d
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|floor
argument_list|(
name|m
operator|/
literal|2
argument_list|)
decl_stmt|;
comment|// current could be multivalue but old may not be, so use first box
if|if
condition|(
name|even
operator|==
literal|0
condition|)
block|{
name|ranges
index|[
name|id
index|]
index|[
literal|0
index|]
operator|.
name|setMin
argument_list|(
name|d
argument_list|,
name|ranges
index|[
name|oldID
index|]
index|[
literal|0
index|]
operator|.
name|getMin
argument_list|(
name|d
argument_list|)
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
literal|"  id="
operator|+
name|id
operator|+
literal|" box="
operator|+
name|ranges
index|[
name|id
index|]
operator|+
literal|" (same min["
operator|+
name|d
operator|+
literal|"] as doc="
operator|+
name|oldID
operator|+
literal|")"
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|ranges
index|[
name|id
index|]
index|[
literal|0
index|]
operator|.
name|setMax
argument_list|(
name|d
argument_list|,
name|ranges
index|[
name|oldID
index|]
index|[
literal|0
index|]
operator|.
name|getMax
argument_list|(
name|d
argument_list|)
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
literal|"  id="
operator|+
name|id
operator|+
literal|" box="
operator|+
name|ranges
index|[
name|id
index|]
operator|+
literal|" (same max["
operator|+
name|d
operator|+
literal|"] as doc="
operator|+
name|oldID
operator|+
literal|")"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
block|}
name|verify
argument_list|(
name|ranges
argument_list|)
expr_stmt|;
block|}
DECL|method|verify
specifier|private
name|void
name|verify
parameter_list|(
name|Range
index|[]
index|[]
name|ranges
parameter_list|)
throws|throws
name|Exception
block|{
name|IndexWriterConfig
name|iwc
init|=
name|newIndexWriterConfig
argument_list|()
decl_stmt|;
comment|// Else seeds may not reproduce:
name|iwc
operator|.
name|setMergeScheduler
argument_list|(
operator|new
name|SerialMergeScheduler
argument_list|()
argument_list|)
expr_stmt|;
comment|// Else we can get O(N^2) merging
name|int
name|mbd
init|=
name|iwc
operator|.
name|getMaxBufferedDocs
argument_list|()
decl_stmt|;
if|if
condition|(
name|mbd
operator|!=
operator|-
literal|1
operator|&&
name|mbd
operator|<
name|ranges
operator|.
name|length
operator|/
literal|100
condition|)
block|{
name|iwc
operator|.
name|setMaxBufferedDocs
argument_list|(
name|ranges
operator|.
name|length
operator|/
literal|100
argument_list|)
expr_stmt|;
block|}
name|Directory
name|dir
decl_stmt|;
if|if
condition|(
name|ranges
operator|.
name|length
operator|>
literal|50000
condition|)
block|{
name|dir
operator|=
name|newFSDirectory
argument_list|(
name|createTempDir
argument_list|(
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|dir
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
block|}
name|Set
argument_list|<
name|Integer
argument_list|>
name|deleted
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
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
for|for
control|(
name|int
name|id
init|=
literal|0
init|;
name|id
operator|<
name|ranges
operator|.
name|length
condition|;
operator|++
name|id
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
name|doc
operator|.
name|add
argument_list|(
operator|new
name|NumericDocValuesField
argument_list|(
literal|"id"
argument_list|,
name|id
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|ranges
index|[
name|id
index|]
index|[
literal|0
index|]
operator|.
name|isMissing
operator|==
literal|false
condition|)
block|{
for|for
control|(
name|int
name|n
init|=
literal|0
init|;
name|n
operator|<
name|ranges
index|[
name|id
index|]
operator|.
name|length
condition|;
operator|++
name|n
control|)
block|{
name|doc
operator|.
name|add
argument_list|(
name|newRangeField
argument_list|(
name|ranges
index|[
name|id
index|]
index|[
name|n
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
name|id
operator|>
literal|0
operator|&&
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|==
literal|1
condition|)
block|{
name|int
name|idToDelete
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|id
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
literal|""
operator|+
name|idToDelete
argument_list|)
argument_list|)
expr_stmt|;
name|deleted
operator|.
name|add
argument_list|(
name|idToDelete
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
literal|"  delete id="
operator|+
name|idToDelete
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|w
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
specifier|final
name|IndexReader
name|r
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|w
argument_list|)
decl_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexSearcher
name|s
init|=
name|newSearcher
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|int
name|dimensions
init|=
name|ranges
index|[
literal|0
index|]
index|[
literal|0
index|]
operator|.
name|numDimensions
argument_list|()
decl_stmt|;
name|int
name|iters
init|=
name|atLeast
argument_list|(
literal|25
argument_list|)
decl_stmt|;
name|Bits
name|liveDocs
init|=
name|MultiFields
operator|.
name|getLiveDocs
argument_list|(
name|s
operator|.
name|getIndexReader
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|maxDoc
init|=
name|s
operator|.
name|getIndexReader
argument_list|()
operator|.
name|maxDoc
argument_list|()
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
operator|++
name|iter
control|)
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
literal|"\nTEST: iter="
operator|+
name|iter
operator|+
literal|" s="
operator|+
name|s
argument_list|)
expr_stmt|;
block|}
comment|// occasionally test open ended bounding ranges
name|Range
name|queryRange
init|=
name|nextRange
argument_list|(
name|dimensions
argument_list|)
decl_stmt|;
name|int
name|rv
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|4
argument_list|)
decl_stmt|;
name|Query
name|query
decl_stmt|;
name|Range
operator|.
name|QueryType
name|queryType
decl_stmt|;
if|if
condition|(
name|rv
operator|==
literal|0
condition|)
block|{
name|queryType
operator|=
name|Range
operator|.
name|QueryType
operator|.
name|INTERSECTS
expr_stmt|;
name|query
operator|=
name|newIntersectsQuery
argument_list|(
name|queryRange
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|rv
operator|==
literal|1
condition|)
block|{
name|queryType
operator|=
name|Range
operator|.
name|QueryType
operator|.
name|CONTAINS
expr_stmt|;
name|query
operator|=
name|newContainsQuery
argument_list|(
name|queryRange
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|rv
operator|==
literal|2
condition|)
block|{
name|queryType
operator|=
name|Range
operator|.
name|QueryType
operator|.
name|WITHIN
expr_stmt|;
name|query
operator|=
name|newWithinQuery
argument_list|(
name|queryRange
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|queryType
operator|=
name|Range
operator|.
name|QueryType
operator|.
name|CROSSES
expr_stmt|;
name|query
operator|=
name|newCrossesQuery
argument_list|(
name|queryRange
argument_list|)
expr_stmt|;
block|}
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
literal|"  query="
operator|+
name|query
argument_list|)
expr_stmt|;
block|}
specifier|final
name|FixedBitSet
name|hits
init|=
operator|new
name|FixedBitSet
argument_list|(
name|maxDoc
argument_list|)
decl_stmt|;
name|s
operator|.
name|search
argument_list|(
name|query
argument_list|,
operator|new
name|SimpleCollector
argument_list|()
block|{
specifier|private
name|int
name|docBase
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|hits
operator|.
name|set
argument_list|(
name|docBase
operator|+
name|doc
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|doSetNextReader
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|docBase
operator|=
name|context
operator|.
name|docBase
expr_stmt|;
block|}
annotation|@
name|Override
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
argument_list|)
expr_stmt|;
name|NumericDocValues
name|docIDToID
init|=
name|MultiDocValues
operator|.
name|getNumericValues
argument_list|(
name|r
argument_list|,
literal|"id"
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|docID
init|=
literal|0
init|;
name|docID
operator|<
name|maxDoc
condition|;
operator|++
name|docID
control|)
block|{
name|assertEquals
argument_list|(
name|docID
argument_list|,
name|docIDToID
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|id
init|=
operator|(
name|int
operator|)
name|docIDToID
operator|.
name|longValue
argument_list|()
decl_stmt|;
name|boolean
name|expected
decl_stmt|;
if|if
condition|(
name|liveDocs
operator|!=
literal|null
operator|&&
name|liveDocs
operator|.
name|get
argument_list|(
name|docID
argument_list|)
operator|==
literal|false
condition|)
block|{
comment|// document is deleted
name|expected
operator|=
literal|false
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|ranges
index|[
name|id
index|]
index|[
literal|0
index|]
operator|.
name|isMissing
condition|)
block|{
name|expected
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|expected
operator|=
name|expectedResult
argument_list|(
name|queryRange
argument_list|,
name|ranges
index|[
name|id
index|]
argument_list|,
name|queryType
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|hits
operator|.
name|get
argument_list|(
name|docID
argument_list|)
operator|!=
name|expected
condition|)
block|{
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"FAIL (iter "
operator|+
name|iter
operator|+
literal|"): "
argument_list|)
expr_stmt|;
if|if
condition|(
name|expected
operator|==
literal|true
condition|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|"id="
operator|+
name|id
operator|+
operator|(
name|ranges
index|[
name|id
index|]
operator|.
name|length
operator|>
literal|1
condition|?
literal|" (MultiValue) "
else|:
literal|" "
operator|)
operator|+
literal|"should match but did not\n"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|b
operator|.
name|append
argument_list|(
literal|"id="
operator|+
name|id
operator|+
literal|" should not match but did\n"
argument_list|)
expr_stmt|;
block|}
name|b
operator|.
name|append
argument_list|(
literal|" queryRange="
operator|+
name|queryRange
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|" box"
operator|+
operator|(
operator|(
name|ranges
index|[
name|id
index|]
operator|.
name|length
operator|>
literal|1
operator|)
condition|?
literal|"es="
else|:
literal|"="
operator|)
operator|+
name|ranges
index|[
name|id
index|]
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|n
init|=
literal|1
init|;
name|n
operator|<
name|ranges
index|[
name|id
index|]
operator|.
name|length
condition|;
operator|++
name|n
control|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
name|ranges
index|[
name|id
index|]
index|[
name|n
index|]
argument_list|)
expr_stmt|;
block|}
name|b
operator|.
name|append
argument_list|(
literal|"\n queryType="
operator|+
name|queryType
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|" deleted?="
operator|+
operator|(
name|liveDocs
operator|!=
literal|null
operator|&&
name|liveDocs
operator|.
name|get
argument_list|(
name|docID
argument_list|)
operator|==
literal|false
operator|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"wrong hit (first of possibly more):\n\n"
operator|+
name|b
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|IOUtils
operator|.
name|close
argument_list|(
name|r
argument_list|,
name|dir
argument_list|)
expr_stmt|;
block|}
DECL|method|expectedResult
specifier|protected
name|boolean
name|expectedResult
parameter_list|(
name|Range
name|queryRange
parameter_list|,
name|Range
index|[]
name|range
parameter_list|,
name|Range
operator|.
name|QueryType
name|queryType
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|range
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
if|if
condition|(
name|expectedBBoxQueryResult
argument_list|(
name|queryRange
argument_list|,
name|range
index|[
name|i
index|]
argument_list|,
name|queryType
argument_list|)
operator|==
literal|true
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
DECL|method|expectedBBoxQueryResult
specifier|protected
name|boolean
name|expectedBBoxQueryResult
parameter_list|(
name|Range
name|queryRange
parameter_list|,
name|Range
name|range
parameter_list|,
name|Range
operator|.
name|QueryType
name|queryType
parameter_list|)
block|{
if|if
condition|(
name|queryRange
operator|.
name|isEqual
argument_list|(
name|range
argument_list|)
operator|&&
name|queryType
operator|!=
name|Range
operator|.
name|QueryType
operator|.
name|CROSSES
condition|)
block|{
return|return
literal|true
return|;
block|}
name|Range
operator|.
name|QueryType
name|relation
init|=
name|range
operator|.
name|relate
argument_list|(
name|queryRange
argument_list|)
decl_stmt|;
if|if
condition|(
name|queryType
operator|==
name|Range
operator|.
name|QueryType
operator|.
name|INTERSECTS
condition|)
block|{
return|return
name|relation
operator|!=
literal|null
return|;
block|}
elseif|else
if|if
condition|(
name|queryType
operator|==
name|Range
operator|.
name|QueryType
operator|.
name|CROSSES
condition|)
block|{
comment|// by definition, RangeFields that CONTAIN the query are also considered to cross
return|return
name|relation
operator|==
name|queryType
operator|||
name|relation
operator|==
name|Range
operator|.
name|QueryType
operator|.
name|CONTAINS
return|;
block|}
return|return
name|relation
operator|==
name|queryType
return|;
block|}
comment|/** base class for range verification */
DECL|class|Range
specifier|protected
specifier|abstract
specifier|static
class|class
name|Range
block|{
DECL|field|isMissing
specifier|protected
name|boolean
name|isMissing
init|=
literal|false
decl_stmt|;
comment|/** supported query relations */
DECL|enum|QueryType
DECL|enum constant|INTERSECTS
DECL|enum constant|WITHIN
DECL|enum constant|CONTAINS
DECL|enum constant|CROSSES
specifier|protected
enum|enum
name|QueryType
block|{
name|INTERSECTS
block|,
name|WITHIN
block|,
name|CONTAINS
block|,
name|CROSSES
block|}
DECL|method|numDimensions
specifier|protected
specifier|abstract
name|int
name|numDimensions
parameter_list|()
function_decl|;
DECL|method|getMin
specifier|protected
specifier|abstract
name|Object
name|getMin
parameter_list|(
name|int
name|dim
parameter_list|)
function_decl|;
DECL|method|setMin
specifier|protected
specifier|abstract
name|void
name|setMin
parameter_list|(
name|int
name|dim
parameter_list|,
name|Object
name|val
parameter_list|)
function_decl|;
DECL|method|getMax
specifier|protected
specifier|abstract
name|Object
name|getMax
parameter_list|(
name|int
name|dim
parameter_list|)
function_decl|;
DECL|method|setMax
specifier|protected
specifier|abstract
name|void
name|setMax
parameter_list|(
name|int
name|dim
parameter_list|,
name|Object
name|val
parameter_list|)
function_decl|;
DECL|method|isEqual
specifier|protected
specifier|abstract
name|boolean
name|isEqual
parameter_list|(
name|Range
name|other
parameter_list|)
function_decl|;
DECL|method|isDisjoint
specifier|protected
specifier|abstract
name|boolean
name|isDisjoint
parameter_list|(
name|Range
name|other
parameter_list|)
function_decl|;
DECL|method|isWithin
specifier|protected
specifier|abstract
name|boolean
name|isWithin
parameter_list|(
name|Range
name|other
parameter_list|)
function_decl|;
DECL|method|contains
specifier|protected
specifier|abstract
name|boolean
name|contains
parameter_list|(
name|Range
name|other
parameter_list|)
function_decl|;
DECL|method|relate
specifier|protected
name|QueryType
name|relate
parameter_list|(
name|Range
name|other
parameter_list|)
block|{
if|if
condition|(
name|isDisjoint
argument_list|(
name|other
argument_list|)
condition|)
block|{
comment|// if disjoint; return null:
return|return
literal|null
return|;
block|}
elseif|else
if|if
condition|(
name|isWithin
argument_list|(
name|other
argument_list|)
condition|)
block|{
return|return
name|QueryType
operator|.
name|WITHIN
return|;
block|}
elseif|else
if|if
condition|(
name|contains
argument_list|(
name|other
argument_list|)
condition|)
block|{
return|return
name|QueryType
operator|.
name|CONTAINS
return|;
block|}
return|return
name|QueryType
operator|.
name|CROSSES
return|;
block|}
block|}
block|}
end_class

end_unit
