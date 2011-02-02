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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Executors
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
name|MultiReader
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
name|queryParser
operator|.
name|QueryParser
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
name|NamedThreadFactory
import|;
end_import

begin_class
DECL|class|TestBooleanQuery
specifier|public
class|class
name|TestBooleanQuery
extends|extends
name|LuceneTestCase
block|{
DECL|method|testEquality
specifier|public
name|void
name|testEquality
parameter_list|()
throws|throws
name|Exception
block|{
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
literal|"field"
argument_list|,
literal|"value1"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
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
literal|"field"
argument_list|,
literal|"value2"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|BooleanQuery
name|nested1
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|nested1
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"nestedvalue1"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|nested1
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"nestedvalue2"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|bq1
operator|.
name|add
argument_list|(
name|nested1
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|BooleanQuery
name|bq2
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|bq2
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"value1"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|bq2
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"value2"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|BooleanQuery
name|nested2
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|nested2
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"nestedvalue1"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|nested2
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"nestedvalue2"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|bq2
operator|.
name|add
argument_list|(
name|nested2
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|bq1
argument_list|,
name|bq2
argument_list|)
expr_stmt|;
block|}
DECL|method|testException
specifier|public
name|void
name|testException
parameter_list|()
block|{
try|try
block|{
name|BooleanQuery
operator|.
name|setMaxClauseCount
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// okay
block|}
block|}
comment|// LUCENE-1630
DECL|method|testNullOrSubScorer
specifier|public
name|void
name|testNullOrSubScorer
parameter_list|()
throws|throws
name|Throwable
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|w
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|,
name|dir
argument_list|)
decl_stmt|;
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
name|newField
argument_list|(
literal|"field"
argument_list|,
literal|"a b c d"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
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
name|BooleanQuery
name|q
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"a"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
comment|// LUCENE-2617: make sure that a term not in the index still contributes to the score via coord factor
name|float
name|score
init|=
name|s
operator|.
name|search
argument_list|(
name|q
argument_list|,
literal|10
argument_list|)
operator|.
name|getMaxScore
argument_list|()
decl_stmt|;
name|Query
name|subQuery
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"not_in_index"
argument_list|)
argument_list|)
decl_stmt|;
name|subQuery
operator|.
name|setBoost
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
name|subQuery
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|float
name|score2
init|=
name|s
operator|.
name|search
argument_list|(
name|q
argument_list|,
literal|10
argument_list|)
operator|.
name|getMaxScore
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|score
operator|*
literal|.5
argument_list|,
name|score2
argument_list|,
literal|1e-6
argument_list|)
expr_stmt|;
comment|// LUCENE-2617: make sure that a clause not in the index still contributes to the score via coord factor
name|BooleanQuery
name|qq
init|=
operator|(
name|BooleanQuery
operator|)
name|q
operator|.
name|clone
argument_list|()
decl_stmt|;
name|PhraseQuery
name|phrase
init|=
operator|new
name|PhraseQuery
argument_list|()
decl_stmt|;
name|phrase
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"not_in_index"
argument_list|)
argument_list|)
expr_stmt|;
name|phrase
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"another_not_in_index"
argument_list|)
argument_list|)
expr_stmt|;
name|phrase
operator|.
name|setBoost
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|qq
operator|.
name|add
argument_list|(
name|phrase
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|score2
operator|=
name|s
operator|.
name|search
argument_list|(
name|qq
argument_list|,
literal|10
argument_list|)
operator|.
name|getMaxScore
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|score
operator|*
operator|(
literal|1.0
operator|/
literal|3
operator|)
argument_list|,
name|score2
argument_list|,
literal|1e-6
argument_list|)
expr_stmt|;
comment|// now test BooleanScorer2
name|subQuery
operator|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
name|subQuery
operator|.
name|setBoost
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
name|subQuery
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|score2
operator|=
name|s
operator|.
name|search
argument_list|(
name|q
argument_list|,
literal|10
argument_list|)
operator|.
name|getMaxScore
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|score
operator|*
operator|(
literal|2.0
operator|/
literal|3
operator|)
argument_list|,
name|score2
argument_list|,
literal|1e-6
argument_list|)
expr_stmt|;
comment|// PhraseQuery w/ no terms added returns a null scorer
name|PhraseQuery
name|pq
init|=
operator|new
name|PhraseQuery
argument_list|()
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
name|pq
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|s
operator|.
name|search
argument_list|(
name|q
argument_list|,
literal|10
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
comment|// A required clause which returns null scorer should return null scorer to
comment|// IndexSearcher.
name|q
operator|=
operator|new
name|BooleanQuery
argument_list|()
expr_stmt|;
name|pq
operator|=
operator|new
name|PhraseQuery
argument_list|()
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"a"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
name|pq
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|s
operator|.
name|search
argument_list|(
name|q
argument_list|,
literal|10
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|DisjunctionMaxQuery
name|dmq
init|=
operator|new
name|DisjunctionMaxQuery
argument_list|(
literal|1.0f
argument_list|)
decl_stmt|;
name|dmq
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"a"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|dmq
operator|.
name|add
argument_list|(
name|pq
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|s
operator|.
name|search
argument_list|(
name|dmq
argument_list|,
literal|10
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|s
operator|.
name|close
argument_list|()
expr_stmt|;
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
DECL|method|testDeMorgan
specifier|public
name|void
name|testDeMorgan
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir1
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|iw1
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|,
name|dir1
argument_list|)
decl_stmt|;
name|Document
name|doc1
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc1
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"field"
argument_list|,
literal|"foo bar"
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
name|iw1
operator|.
name|addDocument
argument_list|(
name|doc1
argument_list|)
expr_stmt|;
name|IndexReader
name|reader1
init|=
name|iw1
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|iw1
operator|.
name|close
argument_list|()
expr_stmt|;
name|Directory
name|dir2
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|iw2
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|,
name|dir2
argument_list|)
decl_stmt|;
name|Document
name|doc2
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc2
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"field"
argument_list|,
literal|"foo baz"
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
name|iw2
operator|.
name|addDocument
argument_list|(
name|doc2
argument_list|)
expr_stmt|;
name|IndexReader
name|reader2
init|=
name|iw2
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|iw2
operator|.
name|close
argument_list|()
expr_stmt|;
name|QueryParser
name|qp
init|=
operator|new
name|QueryParser
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
literal|"field"
argument_list|,
operator|new
name|MockAnalyzer
argument_list|()
argument_list|)
decl_stmt|;
name|qp
operator|.
name|setMultiTermRewriteMethod
argument_list|(
name|MultiTermQuery
operator|.
name|SCORING_BOOLEAN_QUERY_REWRITE
argument_list|)
expr_stmt|;
name|MultiReader
name|multireader
init|=
operator|new
name|MultiReader
argument_list|(
name|reader1
argument_list|,
name|reader2
argument_list|)
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|multireader
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|searcher
operator|.
name|search
argument_list|(
name|qp
operator|.
name|parse
argument_list|(
literal|"+foo -ba*"
argument_list|)
argument_list|,
literal|10
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
specifier|final
name|ExecutorService
name|es
init|=
name|Executors
operator|.
name|newCachedThreadPool
argument_list|(
operator|new
name|NamedThreadFactory
argument_list|(
literal|"NRT search threads"
argument_list|)
argument_list|)
decl_stmt|;
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|multireader
argument_list|,
name|es
argument_list|)
expr_stmt|;
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
literal|"rewritten form: "
operator|+
name|searcher
operator|.
name|rewrite
argument_list|(
name|qp
operator|.
name|parse
argument_list|(
literal|"+foo -ba*"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|searcher
operator|.
name|search
argument_list|(
name|qp
operator|.
name|parse
argument_list|(
literal|"+foo -ba*"
argument_list|)
argument_list|,
literal|10
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|es
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|es
operator|.
name|awaitTermination
argument_list|(
literal|1
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|multireader
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader1
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader2
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
block|}
end_class

end_unit

