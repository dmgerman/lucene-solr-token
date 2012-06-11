begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.spans
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|spans
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
name|analysis
operator|.
name|MockTokenFilter
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
name|MockTokenizer
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
name|store
operator|.
name|Directory
import|;
end_import

begin_comment
comment|/*******************************************************************************  * Tests the span query bug in Lucene. It demonstrates that SpanTermQuerys don't  * work correctly in a BooleanQuery.  *   */
end_comment

begin_class
DECL|class|TestSpansAdvanced
specifier|public
class|class
name|TestSpansAdvanced
extends|extends
name|LuceneTestCase
block|{
comment|// location to the index
DECL|field|mDirectory
specifier|protected
name|Directory
name|mDirectory
decl_stmt|;
DECL|field|reader
specifier|protected
name|IndexReader
name|reader
decl_stmt|;
DECL|field|searcher
specifier|protected
name|IndexSearcher
name|searcher
decl_stmt|;
comment|// field names in the index
DECL|field|FIELD_ID
specifier|private
specifier|final
specifier|static
name|String
name|FIELD_ID
init|=
literal|"ID"
decl_stmt|;
DECL|field|FIELD_TEXT
specifier|protected
specifier|final
specifier|static
name|String
name|FIELD_TEXT
init|=
literal|"TEXT"
decl_stmt|;
comment|/**    * Initializes the tests by adding 4 identical documents to the index.    */
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
comment|// create test index
name|mDirectory
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
specifier|final
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|mDirectory
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
argument_list|,
name|MockTokenizer
operator|.
name|SIMPLE
argument_list|,
literal|true
argument_list|,
name|MockTokenFilter
operator|.
name|ENGLISH_STOPSET
argument_list|,
literal|true
argument_list|)
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
operator|.
name|setSimilarity
argument_list|(
operator|new
name|DefaultSimilarity
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|addDocument
argument_list|(
name|writer
argument_list|,
literal|"1"
argument_list|,
literal|"I think it should work."
argument_list|)
expr_stmt|;
name|addDocument
argument_list|(
name|writer
argument_list|,
literal|"2"
argument_list|,
literal|"I think it should work."
argument_list|)
expr_stmt|;
name|addDocument
argument_list|(
name|writer
argument_list|,
literal|"3"
argument_list|,
literal|"I think it should work."
argument_list|)
expr_stmt|;
name|addDocument
argument_list|(
name|writer
argument_list|,
literal|"4"
argument_list|,
literal|"I think it should work."
argument_list|)
expr_stmt|;
name|reader
operator|=
name|writer
operator|.
name|getReader
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|searcher
operator|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|setSimilarity
argument_list|(
operator|new
name|DefaultSimilarity
argument_list|()
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
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|mDirectory
operator|.
name|close
argument_list|()
expr_stmt|;
name|mDirectory
operator|=
literal|null
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
comment|/**    * Adds the document to the index.    *     * @param writer the Lucene index writer    * @param id the unique id of the document    * @param text the text of the document    * @throws IOException    */
DECL|method|addDocument
specifier|protected
name|void
name|addDocument
parameter_list|(
specifier|final
name|RandomIndexWriter
name|writer
parameter_list|,
specifier|final
name|String
name|id
parameter_list|,
specifier|final
name|String
name|text
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Document
name|document
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|document
operator|.
name|add
argument_list|(
name|newStringField
argument_list|(
name|FIELD_ID
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
name|document
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
name|FIELD_TEXT
argument_list|,
name|text
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests two span queries.    *     * @throws IOException    */
DECL|method|testBooleanQueryWithSpanQueries
specifier|public
name|void
name|testBooleanQueryWithSpanQueries
parameter_list|()
throws|throws
name|IOException
block|{
name|doTestBooleanQueryWithSpanQueries
argument_list|(
name|searcher
argument_list|,
literal|0.3884282f
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests two span queries.    *     * @throws IOException    */
DECL|method|doTestBooleanQueryWithSpanQueries
specifier|protected
name|void
name|doTestBooleanQueryWithSpanQueries
parameter_list|(
name|IndexSearcher
name|s
parameter_list|,
specifier|final
name|float
name|expectedScore
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Query
name|spanQuery
init|=
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD_TEXT
argument_list|,
literal|"work"
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|BooleanQuery
name|query
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|query
operator|.
name|add
argument_list|(
name|spanQuery
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
name|spanQuery
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
specifier|final
name|String
index|[]
name|expectedIds
init|=
operator|new
name|String
index|[]
block|{
literal|"1"
block|,
literal|"2"
block|,
literal|"3"
block|,
literal|"4"
block|}
decl_stmt|;
specifier|final
name|float
index|[]
name|expectedScores
init|=
operator|new
name|float
index|[]
block|{
name|expectedScore
block|,
name|expectedScore
block|,
name|expectedScore
block|,
name|expectedScore
block|}
decl_stmt|;
name|assertHits
argument_list|(
name|s
argument_list|,
name|query
argument_list|,
literal|"two span queries"
argument_list|,
name|expectedIds
argument_list|,
name|expectedScores
argument_list|)
expr_stmt|;
block|}
comment|/**    * Checks to see if the hits are what we expected.    *     * @param query the query to execute    * @param description the description of the search    * @param expectedIds the expected document ids of the hits    * @param expectedScores the expected scores of the hits    *     * @throws IOException    */
DECL|method|assertHits
specifier|protected
specifier|static
name|void
name|assertHits
parameter_list|(
name|IndexSearcher
name|s
parameter_list|,
name|Query
name|query
parameter_list|,
specifier|final
name|String
name|description
parameter_list|,
specifier|final
name|String
index|[]
name|expectedIds
parameter_list|,
specifier|final
name|float
index|[]
name|expectedScores
parameter_list|)
throws|throws
name|IOException
block|{
name|QueryUtils
operator|.
name|check
argument_list|(
name|random
argument_list|()
argument_list|,
name|query
argument_list|,
name|s
argument_list|)
expr_stmt|;
specifier|final
name|float
name|tolerance
init|=
literal|1e-5f
decl_stmt|;
comment|// Hits hits = searcher.search(query);
comment|// hits normalizes and throws things off if one score is greater than 1.0
name|TopDocs
name|topdocs
init|=
name|s
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|null
argument_list|,
literal|10000
argument_list|)
decl_stmt|;
comment|/*****      * // display the hits System.out.println(hits.length() +      * " hits for search: \"" + description + '\"'); for (int i = 0; i<      * hits.length(); i++) { System.out.println("  " + FIELD_ID + ':' +      * hits.doc(i).get(FIELD_ID) + " (score:" + hits.score(i) + ')'); }      *****/
comment|// did we get the hits we expected
name|assertEquals
argument_list|(
name|expectedIds
operator|.
name|length
argument_list|,
name|topdocs
operator|.
name|totalHits
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
name|topdocs
operator|.
name|totalHits
condition|;
name|i
operator|++
control|)
block|{
comment|// System.out.println(i + " exp: " + expectedIds[i]);
comment|// System.out.println(i + " field: " + hits.doc(i).get(FIELD_ID));
name|int
name|id
init|=
name|topdocs
operator|.
name|scoreDocs
index|[
name|i
index|]
operator|.
name|doc
decl_stmt|;
name|float
name|score
init|=
name|topdocs
operator|.
name|scoreDocs
index|[
name|i
index|]
operator|.
name|score
decl_stmt|;
name|Document
name|doc
init|=
name|s
operator|.
name|doc
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expectedIds
index|[
name|i
index|]
argument_list|,
name|doc
operator|.
name|get
argument_list|(
name|FIELD_ID
argument_list|)
argument_list|)
expr_stmt|;
name|boolean
name|scoreEq
init|=
name|Math
operator|.
name|abs
argument_list|(
name|expectedScores
index|[
name|i
index|]
operator|-
name|score
argument_list|)
operator|<
name|tolerance
decl_stmt|;
if|if
condition|(
operator|!
name|scoreEq
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|i
operator|+
literal|" warning, expected score: "
operator|+
name|expectedScores
index|[
name|i
index|]
operator|+
literal|", actual "
operator|+
name|score
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|s
operator|.
name|explain
argument_list|(
name|query
argument_list|,
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|expectedScores
index|[
name|i
index|]
argument_list|,
name|score
argument_list|,
name|tolerance
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|s
operator|.
name|explain
argument_list|(
name|query
argument_list|,
name|id
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|,
name|score
argument_list|,
name|tolerance
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

