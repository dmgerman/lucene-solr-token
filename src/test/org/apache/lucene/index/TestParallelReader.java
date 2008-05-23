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
name|Arrays
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|standard
operator|.
name|StandardAnalyzer
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
name|MapFieldSelector
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
name|BooleanQuery
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
name|Query
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
name|ScoreDoc
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
name|Searcher
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
name|store
operator|.
name|RAMDirectory
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
name|Searcher
name|parallel
decl_stmt|;
DECL|field|single
specifier|private
name|Searcher
name|single
decl_stmt|;
DECL|method|setUp
specifier|protected
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
argument_list|()
expr_stmt|;
name|parallel
operator|=
name|parallel
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
argument_list|()
decl_stmt|;
name|Directory
name|dir2
init|=
name|getDir2
argument_list|()
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
argument_list|)
argument_list|)
expr_stmt|;
name|Collection
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
block|}
DECL|method|testDocument
specifier|public
name|void
name|testDocument
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|dir1
init|=
name|getDir1
argument_list|()
decl_stmt|;
name|Directory
name|dir2
init|=
name|getDir2
argument_list|()
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
argument_list|)
argument_list|)
expr_stmt|;
name|Document
name|doc11
init|=
name|pr
operator|.
name|document
argument_list|(
literal|0
argument_list|,
operator|new
name|MapFieldSelector
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"f1"
block|}
argument_list|)
argument_list|)
decl_stmt|;
name|Document
name|doc24
init|=
name|pr
operator|.
name|document
argument_list|(
literal|1
argument_list|,
operator|new
name|MapFieldSelector
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"f4"
block|}
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|Document
name|doc223
init|=
name|pr
operator|.
name|document
argument_list|(
literal|1
argument_list|,
operator|new
name|MapFieldSelector
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"f2"
block|,
literal|"f3"
block|}
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|doc11
operator|.
name|getFields
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|doc24
operator|.
name|getFields
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|doc223
operator|.
name|getFields
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"v1"
argument_list|,
name|doc11
operator|.
name|get
argument_list|(
literal|"f1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"v2"
argument_list|,
name|doc24
operator|.
name|get
argument_list|(
literal|"f4"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"v2"
argument_list|,
name|doc223
operator|.
name|get
argument_list|(
literal|"f2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"v2"
argument_list|,
name|doc223
operator|.
name|get
argument_list|(
literal|"f3"
argument_list|)
argument_list|)
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
argument_list|()
decl_stmt|;
comment|// one document only:
name|Directory
name|dir2
init|=
operator|new
name|MockRAMDirectory
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
operator|new
name|StandardAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|LIMITED
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
operator|new
name|Field
argument_list|(
literal|"f3"
argument_list|,
literal|"v1"
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
name|TOKENIZED
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
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|pr
operator|.
name|add
argument_list|(
name|IndexReader
operator|.
name|open
argument_list|(
name|dir2
argument_list|)
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
argument_list|()
decl_stmt|;
name|Directory
name|dir2
init|=
name|getDir1
argument_list|()
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
argument_list|)
decl_stmt|;
name|modifier
operator|.
name|setNorm
argument_list|(
literal|0
argument_list|,
literal|"f1"
argument_list|,
literal|100
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
literal|100
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
block|}
DECL|method|testIsOptimized
specifier|public
name|void
name|testIsOptimized
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|dir1
init|=
name|getDir1
argument_list|()
decl_stmt|;
name|Directory
name|dir2
init|=
name|getDir1
argument_list|()
decl_stmt|;
comment|// add another document to ensure that the indexes are not optimized
name|IndexWriter
name|modifier
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir1
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|()
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|LIMITED
argument_list|)
decl_stmt|;
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
literal|"f1"
argument_list|,
literal|"v1"
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
name|TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
name|modifier
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|modifier
operator|.
name|close
argument_list|()
expr_stmt|;
name|modifier
operator|=
operator|new
name|IndexWriter
argument_list|(
name|dir2
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|()
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|LIMITED
argument_list|)
expr_stmt|;
name|d
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"f2"
argument_list|,
literal|"v2"
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
name|TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
name|modifier
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|modifier
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
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|pr
operator|.
name|isOptimized
argument_list|()
argument_list|)
expr_stmt|;
name|pr
operator|.
name|close
argument_list|()
expr_stmt|;
name|modifier
operator|=
operator|new
name|IndexWriter
argument_list|(
name|dir1
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|()
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|LIMITED
argument_list|)
expr_stmt|;
name|modifier
operator|.
name|optimize
argument_list|()
expr_stmt|;
name|modifier
operator|.
name|close
argument_list|()
expr_stmt|;
name|pr
operator|=
operator|new
name|ParallelReader
argument_list|()
expr_stmt|;
name|pr
operator|.
name|add
argument_list|(
name|IndexReader
operator|.
name|open
argument_list|(
name|dir1
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
argument_list|)
argument_list|)
expr_stmt|;
comment|// just one of the two indexes are optimized
name|assertFalse
argument_list|(
name|pr
operator|.
name|isOptimized
argument_list|()
argument_list|)
expr_stmt|;
name|pr
operator|.
name|close
argument_list|()
expr_stmt|;
name|modifier
operator|=
operator|new
name|IndexWriter
argument_list|(
name|dir2
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|()
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|LIMITED
argument_list|)
expr_stmt|;
name|modifier
operator|.
name|optimize
argument_list|()
expr_stmt|;
name|modifier
operator|.
name|close
argument_list|()
expr_stmt|;
name|pr
operator|=
operator|new
name|ParallelReader
argument_list|()
expr_stmt|;
name|pr
operator|.
name|add
argument_list|(
name|IndexReader
operator|.
name|open
argument_list|(
name|dir1
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
argument_list|)
argument_list|)
expr_stmt|;
comment|// now both indexes are optimized
name|assertTrue
argument_list|(
name|pr
operator|.
name|isOptimized
argument_list|()
argument_list|)
expr_stmt|;
name|pr
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
name|Searcher
name|single
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
operator|new
name|MockRAMDirectory
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
operator|new
name|StandardAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|LIMITED
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
operator|new
name|Field
argument_list|(
literal|"f1"
argument_list|,
literal|"v1"
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
name|TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
name|d1
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"f2"
argument_list|,
literal|"v1"
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
name|TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
name|d1
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"f3"
argument_list|,
literal|"v1"
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
name|TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
name|d1
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"f4"
argument_list|,
literal|"v1"
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
name|TOKENIZED
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
operator|new
name|Field
argument_list|(
literal|"f1"
argument_list|,
literal|"v2"
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
name|TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
name|d2
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"f2"
argument_list|,
literal|"v2"
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
name|TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
name|d2
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"f3"
argument_list|,
literal|"v2"
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
name|TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
name|d2
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"f4"
argument_list|,
literal|"v2"
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
name|TOKENIZED
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
return|return
operator|new
name|IndexSearcher
argument_list|(
name|dir
argument_list|)
return|;
block|}
comment|// Fields 1& 2 in one index, 3& 4 in other, with ParallelReader:
DECL|method|parallel
specifier|private
name|Searcher
name|parallel
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|dir1
init|=
name|getDir1
argument_list|()
decl_stmt|;
name|Directory
name|dir2
init|=
name|getDir2
argument_list|()
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
argument_list|)
argument_list|)
expr_stmt|;
return|return
operator|new
name|IndexSearcher
argument_list|(
name|pr
argument_list|)
return|;
block|}
DECL|method|getDir1
specifier|private
name|Directory
name|getDir1
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|dir1
init|=
operator|new
name|MockRAMDirectory
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
operator|new
name|StandardAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|LIMITED
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
operator|new
name|Field
argument_list|(
literal|"f1"
argument_list|,
literal|"v1"
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
name|TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
name|d1
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"f2"
argument_list|,
literal|"v1"
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
name|TOKENIZED
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
operator|new
name|Field
argument_list|(
literal|"f1"
argument_list|,
literal|"v2"
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
name|TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
name|d2
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"f2"
argument_list|,
literal|"v2"
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
name|TOKENIZED
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
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|dir2
init|=
operator|new
name|RAMDirectory
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
operator|new
name|StandardAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|LIMITED
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
operator|new
name|Field
argument_list|(
literal|"f3"
argument_list|,
literal|"v1"
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
name|TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
name|d3
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"f4"
argument_list|,
literal|"v1"
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
name|TOKENIZED
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
operator|new
name|Field
argument_list|(
literal|"f3"
argument_list|,
literal|"v2"
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
name|TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
name|d4
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"f4"
argument_list|,
literal|"v2"
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
name|TOKENIZED
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

