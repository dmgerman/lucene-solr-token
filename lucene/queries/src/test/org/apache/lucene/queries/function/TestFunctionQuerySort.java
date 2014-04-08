begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.queries.function
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queries
operator|.
name|function
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
name|queries
operator|.
name|function
operator|.
name|valuesource
operator|.
name|IntFieldSource
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
name|FieldDoc
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
name|MatchAllDocsQuery
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
name|Sort
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
name|SortField
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

begin_comment
comment|/** Test that functionquery's getSortField() actually works */
end_comment

begin_class
DECL|class|TestFunctionQuerySort
specifier|public
class|class
name|TestFunctionQuerySort
extends|extends
name|LuceneTestCase
block|{
DECL|method|testSearchAfterWhenSortingByFunctionValues
specifier|public
name|void
name|testSearchAfterWhenSortingByFunctionValues
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriterConfig
name|iwc
init|=
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|iwc
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
expr_stmt|;
comment|// depends on docid order
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|,
name|iwc
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|Field
name|field
init|=
operator|new
name|IntField
argument_list|(
literal|"value"
argument_list|,
literal|0
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
comment|// Save docs unsorted (decreasing value n, n-1, ...)
specifier|final
name|int
name|NUM_VALS
init|=
literal|5
decl_stmt|;
for|for
control|(
name|int
name|val
init|=
name|NUM_VALS
init|;
name|val
operator|>
literal|0
condition|;
name|val
operator|--
control|)
block|{
name|field
operator|.
name|setIntValue
argument_list|(
name|val
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
comment|// Open index
name|IndexReader
name|reader
init|=
name|writer
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|writer
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
comment|// Get ValueSource from FieldCache
name|IntFieldSource
name|src
init|=
operator|new
name|IntFieldSource
argument_list|(
literal|"value"
argument_list|)
decl_stmt|;
comment|// ...and make it a sort criterion
name|SortField
name|sf
init|=
name|src
operator|.
name|getSortField
argument_list|(
literal|false
argument_list|)
operator|.
name|rewrite
argument_list|(
name|searcher
argument_list|)
decl_stmt|;
name|Sort
name|orderBy
init|=
operator|new
name|Sort
argument_list|(
name|sf
argument_list|)
decl_stmt|;
comment|// Get hits sorted by our FunctionValues (ascending values)
name|Query
name|q
init|=
operator|new
name|MatchAllDocsQuery
argument_list|()
decl_stmt|;
name|TopDocs
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|,
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|,
name|orderBy
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|NUM_VALS
argument_list|,
name|hits
operator|.
name|scoreDocs
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// Verify that sorting works in general
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|ScoreDoc
name|hit
range|:
name|hits
operator|.
name|scoreDocs
control|)
block|{
name|int
name|valueFromDoc
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|reader
operator|.
name|document
argument_list|(
name|hit
operator|.
name|doc
argument_list|)
operator|.
name|get
argument_list|(
literal|"value"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
operator|++
name|i
argument_list|,
name|valueFromDoc
argument_list|)
expr_stmt|;
block|}
comment|// Now get hits after hit #2 using IS.searchAfter()
name|int
name|afterIdx
init|=
literal|1
decl_stmt|;
name|FieldDoc
name|afterHit
init|=
operator|(
name|FieldDoc
operator|)
name|hits
operator|.
name|scoreDocs
index|[
name|afterIdx
index|]
decl_stmt|;
name|hits
operator|=
name|searcher
operator|.
name|searchAfter
argument_list|(
name|afterHit
argument_list|,
name|q
argument_list|,
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|,
name|orderBy
argument_list|)
expr_stmt|;
comment|// Expected # of hits: NUM_VALS - 2
name|assertEquals
argument_list|(
name|NUM_VALS
operator|-
operator|(
name|afterIdx
operator|+
literal|1
operator|)
argument_list|,
name|hits
operator|.
name|scoreDocs
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// Verify that hits are actually "after"
name|int
name|afterValue
init|=
operator|(
operator|(
name|Double
operator|)
name|afterHit
operator|.
name|fields
index|[
literal|0
index|]
operator|)
operator|.
name|intValue
argument_list|()
decl_stmt|;
for|for
control|(
name|ScoreDoc
name|hit
range|:
name|hits
operator|.
name|scoreDocs
control|)
block|{
name|int
name|val
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|reader
operator|.
name|document
argument_list|(
name|hit
operator|.
name|doc
argument_list|)
operator|.
name|get
argument_list|(
literal|"value"
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|afterValue
operator|<=
name|val
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|hit
operator|.
name|doc
operator|==
name|afterHit
operator|.
name|doc
argument_list|)
expr_stmt|;
block|}
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
block|}
end_class

end_unit

