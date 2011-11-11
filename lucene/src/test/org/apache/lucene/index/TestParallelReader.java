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
name|Collection
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
name|search
operator|.
name|BooleanClause
operator|.
name|Occur
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
name|similarities
operator|.
name|DefaultSimilarity
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
name|*
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

begin_class
DECL|class|TestParallelReader
specifier|public
class|class
name|TestParallelReader
extends|extends
name|LuceneTestCase
block|{
DECL|field|parallel
specifier|private
name|IndexSearcher
name|parallel
decl_stmt|;
DECL|field|single
specifier|private
name|IndexSearcher
name|single
decl_stmt|;
DECL|field|dir
DECL|field|dir1
DECL|field|dir2
specifier|private
name|Directory
name|dir
decl_stmt|,
name|dir1
decl_stmt|,
name|dir2
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
name|single
operator|=
name|single
argument_list|(
name|random
argument_list|)
expr_stmt|;
name|parallel
operator|=
name|parallel
argument_list|(
name|random
argument_list|)
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
name|single
operator|.
name|getIndexReader
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
name|single
operator|.
name|close
argument_list|()
expr_stmt|;
name|parallel
operator|.
name|getIndexReader
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
name|parallel
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir1
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir2
operator|.
name|close
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|testQueries
specifier|public
name|void
name|testQueries
parameter_list|()
throws|throws
name|Exception
block|{
name|queryTest
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"f1"
argument_list|,
literal|"v1"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|queryTest
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"f1"
argument_list|,
literal|"v2"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|queryTest
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"f2"
argument_list|,
literal|"v1"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|queryTest
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"f2"
argument_list|,
literal|"v2"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|queryTest
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"f3"
argument_list|,
literal|"v1"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|queryTest
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"f3"
argument_list|,
literal|"v2"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|queryTest
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"f4"
argument_list|,
literal|"v1"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|queryTest
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"f4"
argument_list|,
literal|"v2"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|BooleanQuery
name|bq1
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|bq1
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"f1"
argument_list|,
literal|"v1"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|bq1
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"f4"
argument_list|,
literal|"v1"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|queryTest
argument_list|(
name|bq1
argument_list|)
expr_stmt|;
block|}
DECL|method|testFieldNames
specifier|public
name|void
name|testFieldNames
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir1
init|=
name|getDir1
argument_list|(
name|random
argument_list|)
decl_stmt|;
name|Directory
name|dir2
init|=
name|getDir2
argument_list|(
name|random
argument_list|)
decl_stmt|;
name|ParallelReader
name|pr
init|=
operator|new
name|ParallelReader
argument_list|()
decl_stmt|;
name|pr
operator|.
name|add
argument_list|(
name|IndexReader
operator|.
name|open
argument_list|(
name|dir1
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|pr
operator|.
name|add
argument_list|(
name|IndexReader
operator|.
name|open
argument_list|(
name|dir2
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|Collection
argument_list|<
name|String
argument_list|>
name|fieldNames
init|=
name|pr
operator|.
name|getFieldNames
argument_list|(
name|IndexReader
operator|.
name|FieldOption
operator|.
name|ALL
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|fieldNames
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fieldNames
operator|.
name|contains
argument_list|(
literal|"f1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fieldNames
operator|.
name|contains
argument_list|(
literal|"f2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fieldNames
operator|.
name|contains
argument_list|(
literal|"f3"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fieldNames
operator|.
name|contains
argument_list|(
literal|"f4"
argument_list|)
argument_list|)
expr_stmt|;
name|pr
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir1
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir2
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testIncompatibleIndexes
specifier|public
name|void
name|testIncompatibleIndexes
parameter_list|()
throws|throws
name|IOException
block|{
comment|// two documents:
name|Directory
name|dir1
init|=
name|getDir1
argument_list|(
name|random
argument_list|)
decl_stmt|;
comment|// one document only:
name|Directory
name|dir2
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|w2
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir2
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|Document
name|d3
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|d3
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"f3"
argument_list|,
literal|"v1"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|w2
operator|.
name|addDocument
argument_list|(
name|d3
argument_list|)
expr_stmt|;
name|w2
operator|.
name|close
argument_list|()
expr_stmt|;
name|ParallelReader
name|pr
init|=
operator|new
name|ParallelReader
argument_list|()
decl_stmt|;
name|pr
operator|.
name|add
argument_list|(
name|IndexReader
operator|.
name|open
argument_list|(
name|dir1
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|IndexReader
name|ir
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir2
argument_list|,
literal|false
argument_list|)
decl_stmt|;
try|try
block|{
name|pr
operator|.
name|add
argument_list|(
name|ir
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't get exptected exception: indexes don't have same number of documents"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// expected exception
block|}
name|pr
operator|.
name|close
argument_list|()
expr_stmt|;
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir1
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir2
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testIsCurrent
specifier|public
name|void
name|testIsCurrent
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|dir1
init|=
name|getDir1
argument_list|(
name|random
argument_list|)
decl_stmt|;
name|Directory
name|dir2
init|=
name|getDir2
argument_list|(
name|random
argument_list|)
decl_stmt|;
name|ParallelReader
name|pr
init|=
operator|new
name|ParallelReader
argument_list|()
decl_stmt|;
name|pr
operator|.
name|add
argument_list|(
name|IndexReader
operator|.
name|open
argument_list|(
name|dir1
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|pr
operator|.
name|add
argument_list|(
name|IndexReader
operator|.
name|open
argument_list|(
name|dir2
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pr
operator|.
name|isCurrent
argument_list|()
argument_list|)
expr_stmt|;
name|IndexReader
name|modifier
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir1
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|DefaultSimilarity
name|sim
init|=
operator|new
name|DefaultSimilarity
argument_list|()
decl_stmt|;
name|modifier
operator|.
name|setNorm
argument_list|(
literal|0
argument_list|,
literal|"f1"
argument_list|,
name|sim
operator|.
name|encodeNormValue
argument_list|(
literal|100f
argument_list|)
argument_list|)
expr_stmt|;
name|modifier
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// one of the two IndexReaders which ParallelReader is using
comment|// is not current anymore
name|assertFalse
argument_list|(
name|pr
operator|.
name|isCurrent
argument_list|()
argument_list|)
expr_stmt|;
name|modifier
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir2
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|modifier
operator|.
name|setNorm
argument_list|(
literal|0
argument_list|,
literal|"f3"
argument_list|,
name|sim
operator|.
name|encodeNormValue
argument_list|(
literal|100f
argument_list|)
argument_list|)
expr_stmt|;
name|modifier
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// now both are not current anymore
name|assertFalse
argument_list|(
name|pr
operator|.
name|isCurrent
argument_list|()
argument_list|)
expr_stmt|;
name|pr
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir1
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir2
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|queryTest
specifier|private
name|void
name|queryTest
parameter_list|(
name|Query
name|query
parameter_list|)
throws|throws
name|IOException
block|{
name|ScoreDoc
index|[]
name|parallelHits
init|=
name|parallel
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
decl_stmt|;
name|ScoreDoc
index|[]
name|singleHits
init|=
name|single
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
decl_stmt|;
name|assertEquals
argument_list|(
name|parallelHits
operator|.
name|length
argument_list|,
name|singleHits
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
name|parallelHits
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|parallelHits
index|[
name|i
index|]
operator|.
name|score
argument_list|,
name|singleHits
index|[
name|i
index|]
operator|.
name|score
argument_list|,
literal|0.001f
argument_list|)
expr_stmt|;
name|Document
name|docParallel
init|=
name|parallel
operator|.
name|doc
argument_list|(
name|parallelHits
index|[
name|i
index|]
operator|.
name|doc
argument_list|)
decl_stmt|;
name|Document
name|docSingle
init|=
name|single
operator|.
name|doc
argument_list|(
name|singleHits
index|[
name|i
index|]
operator|.
name|doc
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|docParallel
operator|.
name|get
argument_list|(
literal|"f1"
argument_list|)
argument_list|,
name|docSingle
operator|.
name|get
argument_list|(
literal|"f1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|docParallel
operator|.
name|get
argument_list|(
literal|"f2"
argument_list|)
argument_list|,
name|docSingle
operator|.
name|get
argument_list|(
literal|"f2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|docParallel
operator|.
name|get
argument_list|(
literal|"f3"
argument_list|)
argument_list|,
name|docSingle
operator|.
name|get
argument_list|(
literal|"f3"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|docParallel
operator|.
name|get
argument_list|(
literal|"f4"
argument_list|)
argument_list|,
name|docSingle
operator|.
name|get
argument_list|(
literal|"f4"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Fields 1-4 indexed together:
DECL|method|single
specifier|private
name|IndexSearcher
name|single
parameter_list|(
name|Random
name|random
parameter_list|)
throws|throws
name|IOException
block|{
name|dir
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|Document
name|d1
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|d1
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"f1"
argument_list|,
literal|"v1"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|d1
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"f2"
argument_list|,
literal|"v1"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|d1
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"f3"
argument_list|,
literal|"v1"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|d1
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"f4"
argument_list|,
literal|"v1"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|d1
argument_list|)
expr_stmt|;
name|Document
name|d2
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|d2
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"f1"
argument_list|,
literal|"v2"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|d2
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"f2"
argument_list|,
literal|"v2"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|d2
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"f3"
argument_list|,
literal|"v2"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|d2
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"f4"
argument_list|,
literal|"v2"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|d2
argument_list|)
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexReader
name|ir
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|,
literal|false
argument_list|)
decl_stmt|;
return|return
name|newSearcher
argument_list|(
name|ir
argument_list|)
return|;
block|}
comment|// Fields 1& 2 in one index, 3& 4 in other, with ParallelReader:
DECL|method|parallel
specifier|private
name|IndexSearcher
name|parallel
parameter_list|(
name|Random
name|random
parameter_list|)
throws|throws
name|IOException
block|{
name|dir1
operator|=
name|getDir1
argument_list|(
name|random
argument_list|)
expr_stmt|;
name|dir2
operator|=
name|getDir2
argument_list|(
name|random
argument_list|)
expr_stmt|;
name|ParallelReader
name|pr
init|=
operator|new
name|ParallelReader
argument_list|()
decl_stmt|;
name|pr
operator|.
name|add
argument_list|(
name|IndexReader
operator|.
name|open
argument_list|(
name|dir1
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|pr
operator|.
name|add
argument_list|(
name|IndexReader
operator|.
name|open
argument_list|(
name|dir2
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|newSearcher
argument_list|(
name|pr
argument_list|)
return|;
block|}
DECL|method|getDir1
specifier|private
name|Directory
name|getDir1
parameter_list|(
name|Random
name|random
parameter_list|)
throws|throws
name|IOException
block|{
name|Directory
name|dir1
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|w1
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir1
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|Document
name|d1
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|d1
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"f1"
argument_list|,
literal|"v1"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|d1
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"f2"
argument_list|,
literal|"v1"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|w1
operator|.
name|addDocument
argument_list|(
name|d1
argument_list|)
expr_stmt|;
name|Document
name|d2
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|d2
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"f1"
argument_list|,
literal|"v2"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|d2
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"f2"
argument_list|,
literal|"v2"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|w1
operator|.
name|addDocument
argument_list|(
name|d2
argument_list|)
expr_stmt|;
name|w1
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|dir1
return|;
block|}
DECL|method|getDir2
specifier|private
name|Directory
name|getDir2
parameter_list|(
name|Random
name|random
parameter_list|)
throws|throws
name|IOException
block|{
name|Directory
name|dir2
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|w2
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir2
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|Document
name|d3
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|d3
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"f3"
argument_list|,
literal|"v1"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|d3
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"f4"
argument_list|,
literal|"v1"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|w2
operator|.
name|addDocument
argument_list|(
name|d3
argument_list|)
expr_stmt|;
name|Document
name|d4
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|d4
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"f3"
argument_list|,
literal|"v2"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|d4
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"f4"
argument_list|,
literal|"v2"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|w2
operator|.
name|addDocument
argument_list|(
name|d4
argument_list|)
expr_stmt|;
name|w2
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|dir2
return|;
block|}
block|}
end_class

end_unit

