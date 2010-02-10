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
name|SimpleAnalyzer
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
name|WhitespaceAnalyzer
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
name|Field
operator|.
name|Index
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
name|RAMDirectory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * TestWildcard tests the '*' and '?' wildcard characters.  */
end_comment

begin_class
DECL|class|TestWildcard
specifier|public
class|class
name|TestWildcard
extends|extends
name|LuceneTestCase
block|{
DECL|method|testEquals
specifier|public
name|void
name|testEquals
parameter_list|()
block|{
name|WildcardQuery
name|wq1
init|=
operator|new
name|WildcardQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"b*a"
argument_list|)
argument_list|)
decl_stmt|;
name|WildcardQuery
name|wq2
init|=
operator|new
name|WildcardQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"b*a"
argument_list|)
argument_list|)
decl_stmt|;
name|WildcardQuery
name|wq3
init|=
operator|new
name|WildcardQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"b*a"
argument_list|)
argument_list|)
decl_stmt|;
comment|// reflexive?
name|assertEquals
argument_list|(
name|wq1
argument_list|,
name|wq2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|wq2
argument_list|,
name|wq1
argument_list|)
expr_stmt|;
comment|// transitive?
name|assertEquals
argument_list|(
name|wq2
argument_list|,
name|wq3
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|wq1
argument_list|,
name|wq3
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|wq1
operator|.
name|equals
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|FuzzyQuery
name|fq
init|=
operator|new
name|FuzzyQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"b*a"
argument_list|)
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|wq1
operator|.
name|equals
argument_list|(
name|fq
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|fq
operator|.
name|equals
argument_list|(
name|wq1
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests if a WildcardQuery that has no wildcard in the term is rewritten to a single    * TermQuery. The boost should be preserved, and the rewrite should return    * a ConstantScoreQuery if the WildcardQuery had a ConstantScore rewriteMethod.    */
DECL|method|testTermWithoutWildcard
specifier|public
name|void
name|testTermWithoutWildcard
parameter_list|()
throws|throws
name|IOException
block|{
name|RAMDirectory
name|indexStore
init|=
name|getIndexStore
argument_list|(
literal|"field"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"nowildcard"
block|,
literal|"nowildcardx"
block|}
argument_list|)
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|indexStore
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|MultiTermQuery
name|wq
init|=
operator|new
name|WildcardQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"nowildcard"
argument_list|)
argument_list|)
decl_stmt|;
name|assertMatches
argument_list|(
name|searcher
argument_list|,
name|wq
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|wq
operator|.
name|setRewriteMethod
argument_list|(
name|MultiTermQuery
operator|.
name|SCORING_BOOLEAN_QUERY_REWRITE
argument_list|)
expr_stmt|;
name|wq
operator|.
name|setBoost
argument_list|(
literal|0.1F
argument_list|)
expr_stmt|;
name|Query
name|q
init|=
name|searcher
operator|.
name|rewrite
argument_list|(
name|wq
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|q
operator|instanceof
name|TermQuery
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|q
operator|.
name|getBoost
argument_list|()
argument_list|,
name|wq
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
name|wq
operator|.
name|setRewriteMethod
argument_list|(
name|MultiTermQuery
operator|.
name|CONSTANT_SCORE_FILTER_REWRITE
argument_list|)
expr_stmt|;
name|wq
operator|.
name|setBoost
argument_list|(
literal|0.2F
argument_list|)
expr_stmt|;
name|q
operator|=
name|searcher
operator|.
name|rewrite
argument_list|(
name|wq
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|q
operator|instanceof
name|ConstantScoreQuery
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|q
operator|.
name|getBoost
argument_list|()
argument_list|,
name|wq
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
name|wq
operator|.
name|setRewriteMethod
argument_list|(
name|MultiTermQuery
operator|.
name|CONSTANT_SCORE_AUTO_REWRITE_DEFAULT
argument_list|)
expr_stmt|;
name|wq
operator|.
name|setBoost
argument_list|(
literal|0.3F
argument_list|)
expr_stmt|;
name|q
operator|=
name|searcher
operator|.
name|rewrite
argument_list|(
name|wq
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|q
operator|instanceof
name|ConstantScoreQuery
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|q
operator|.
name|getBoost
argument_list|()
argument_list|,
name|wq
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
name|wq
operator|.
name|setRewriteMethod
argument_list|(
name|MultiTermQuery
operator|.
name|CONSTANT_SCORE_BOOLEAN_QUERY_REWRITE
argument_list|)
expr_stmt|;
name|wq
operator|.
name|setBoost
argument_list|(
literal|0.4F
argument_list|)
expr_stmt|;
name|q
operator|=
name|searcher
operator|.
name|rewrite
argument_list|(
name|wq
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|q
operator|instanceof
name|ConstantScoreQuery
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|q
operator|.
name|getBoost
argument_list|()
argument_list|,
name|wq
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests if a WildcardQuery with an empty term is rewritten to an empty BooleanQuery    */
DECL|method|testEmptyTerm
specifier|public
name|void
name|testEmptyTerm
parameter_list|()
throws|throws
name|IOException
block|{
name|RAMDirectory
name|indexStore
init|=
name|getIndexStore
argument_list|(
literal|"field"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"nowildcard"
block|,
literal|"nowildcardx"
block|}
argument_list|)
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|indexStore
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|MultiTermQuery
name|wq
init|=
operator|new
name|WildcardQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|""
argument_list|)
argument_list|)
decl_stmt|;
name|wq
operator|.
name|setRewriteMethod
argument_list|(
name|MultiTermQuery
operator|.
name|SCORING_BOOLEAN_QUERY_REWRITE
argument_list|)
expr_stmt|;
name|assertMatches
argument_list|(
name|searcher
argument_list|,
name|wq
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|Query
name|q
init|=
name|searcher
operator|.
name|rewrite
argument_list|(
name|wq
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|q
operator|instanceof
name|BooleanQuery
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
operator|(
operator|(
name|BooleanQuery
operator|)
name|q
operator|)
operator|.
name|clauses
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests if a WildcardQuery that has only a trailing * in the term is    * rewritten to a single PrefixQuery. The boost and rewriteMethod should be    * preserved.    */
DECL|method|testPrefixTerm
specifier|public
name|void
name|testPrefixTerm
parameter_list|()
throws|throws
name|IOException
block|{
name|RAMDirectory
name|indexStore
init|=
name|getIndexStore
argument_list|(
literal|"field"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"prefix"
block|,
literal|"prefixx"
block|}
argument_list|)
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|indexStore
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|MultiTermQuery
name|wq
init|=
operator|new
name|WildcardQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"prefix*"
argument_list|)
argument_list|)
decl_stmt|;
name|assertMatches
argument_list|(
name|searcher
argument_list|,
name|wq
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|MultiTermQuery
name|expected
init|=
operator|new
name|PrefixQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"prefix"
argument_list|)
argument_list|)
decl_stmt|;
name|wq
operator|.
name|setRewriteMethod
argument_list|(
name|MultiTermQuery
operator|.
name|SCORING_BOOLEAN_QUERY_REWRITE
argument_list|)
expr_stmt|;
name|wq
operator|.
name|setBoost
argument_list|(
literal|0.1F
argument_list|)
expr_stmt|;
name|expected
operator|.
name|setRewriteMethod
argument_list|(
name|wq
operator|.
name|getRewriteMethod
argument_list|()
argument_list|)
expr_stmt|;
name|expected
operator|.
name|setBoost
argument_list|(
name|wq
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|searcher
operator|.
name|rewrite
argument_list|(
name|expected
argument_list|)
argument_list|,
name|searcher
operator|.
name|rewrite
argument_list|(
name|wq
argument_list|)
argument_list|)
expr_stmt|;
name|wq
operator|.
name|setRewriteMethod
argument_list|(
name|MultiTermQuery
operator|.
name|CONSTANT_SCORE_FILTER_REWRITE
argument_list|)
expr_stmt|;
name|wq
operator|.
name|setBoost
argument_list|(
literal|0.2F
argument_list|)
expr_stmt|;
name|expected
operator|.
name|setRewriteMethod
argument_list|(
name|wq
operator|.
name|getRewriteMethod
argument_list|()
argument_list|)
expr_stmt|;
name|expected
operator|.
name|setBoost
argument_list|(
name|wq
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|searcher
operator|.
name|rewrite
argument_list|(
name|expected
argument_list|)
argument_list|,
name|searcher
operator|.
name|rewrite
argument_list|(
name|wq
argument_list|)
argument_list|)
expr_stmt|;
name|wq
operator|.
name|setRewriteMethod
argument_list|(
name|MultiTermQuery
operator|.
name|CONSTANT_SCORE_AUTO_REWRITE_DEFAULT
argument_list|)
expr_stmt|;
name|wq
operator|.
name|setBoost
argument_list|(
literal|0.3F
argument_list|)
expr_stmt|;
name|expected
operator|.
name|setRewriteMethod
argument_list|(
name|wq
operator|.
name|getRewriteMethod
argument_list|()
argument_list|)
expr_stmt|;
name|expected
operator|.
name|setBoost
argument_list|(
name|wq
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|searcher
operator|.
name|rewrite
argument_list|(
name|expected
argument_list|)
argument_list|,
name|searcher
operator|.
name|rewrite
argument_list|(
name|wq
argument_list|)
argument_list|)
expr_stmt|;
name|wq
operator|.
name|setRewriteMethod
argument_list|(
name|MultiTermQuery
operator|.
name|CONSTANT_SCORE_BOOLEAN_QUERY_REWRITE
argument_list|)
expr_stmt|;
name|wq
operator|.
name|setBoost
argument_list|(
literal|0.4F
argument_list|)
expr_stmt|;
name|expected
operator|.
name|setRewriteMethod
argument_list|(
name|wq
operator|.
name|getRewriteMethod
argument_list|()
argument_list|)
expr_stmt|;
name|expected
operator|.
name|setBoost
argument_list|(
name|wq
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|searcher
operator|.
name|rewrite
argument_list|(
name|expected
argument_list|)
argument_list|,
name|searcher
operator|.
name|rewrite
argument_list|(
name|wq
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests Wildcard queries with an asterisk.    */
DECL|method|testAsterisk
specifier|public
name|void
name|testAsterisk
parameter_list|()
throws|throws
name|IOException
block|{
name|RAMDirectory
name|indexStore
init|=
name|getIndexStore
argument_list|(
literal|"body"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"metal"
block|,
literal|"metals"
block|}
argument_list|)
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|indexStore
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Query
name|query1
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"metal"
argument_list|)
argument_list|)
decl_stmt|;
name|Query
name|query2
init|=
operator|new
name|WildcardQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"metal*"
argument_list|)
argument_list|)
decl_stmt|;
name|Query
name|query3
init|=
operator|new
name|WildcardQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"m*tal"
argument_list|)
argument_list|)
decl_stmt|;
name|Query
name|query4
init|=
operator|new
name|WildcardQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"m*tal*"
argument_list|)
argument_list|)
decl_stmt|;
name|Query
name|query5
init|=
operator|new
name|WildcardQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"m*tals"
argument_list|)
argument_list|)
decl_stmt|;
name|BooleanQuery
name|query6
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|query6
operator|.
name|add
argument_list|(
name|query5
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|BooleanQuery
name|query7
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|query7
operator|.
name|add
argument_list|(
name|query3
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|query7
operator|.
name|add
argument_list|(
name|query5
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
comment|// Queries do not automatically lower-case search terms:
name|Query
name|query8
init|=
operator|new
name|WildcardQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"M*tal*"
argument_list|)
argument_list|)
decl_stmt|;
name|assertMatches
argument_list|(
name|searcher
argument_list|,
name|query1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertMatches
argument_list|(
name|searcher
argument_list|,
name|query2
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertMatches
argument_list|(
name|searcher
argument_list|,
name|query3
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertMatches
argument_list|(
name|searcher
argument_list|,
name|query4
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertMatches
argument_list|(
name|searcher
argument_list|,
name|query5
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertMatches
argument_list|(
name|searcher
argument_list|,
name|query6
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertMatches
argument_list|(
name|searcher
argument_list|,
name|query7
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertMatches
argument_list|(
name|searcher
argument_list|,
name|query8
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertMatches
argument_list|(
name|searcher
argument_list|,
operator|new
name|WildcardQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"*tall"
argument_list|)
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertMatches
argument_list|(
name|searcher
argument_list|,
operator|new
name|WildcardQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"*tal"
argument_list|)
argument_list|)
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertMatches
argument_list|(
name|searcher
argument_list|,
operator|new
name|WildcardQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"*tal*"
argument_list|)
argument_list|)
argument_list|,
literal|2
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests Wildcard queries with a question mark.    *    * @throws IOException if an error occurs    */
DECL|method|testQuestionmark
specifier|public
name|void
name|testQuestionmark
parameter_list|()
throws|throws
name|IOException
block|{
name|RAMDirectory
name|indexStore
init|=
name|getIndexStore
argument_list|(
literal|"body"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"metal"
block|,
literal|"metals"
block|,
literal|"mXtals"
block|,
literal|"mXtXls"
block|}
argument_list|)
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|indexStore
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Query
name|query1
init|=
operator|new
name|WildcardQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"m?tal"
argument_list|)
argument_list|)
decl_stmt|;
name|Query
name|query2
init|=
operator|new
name|WildcardQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"metal?"
argument_list|)
argument_list|)
decl_stmt|;
name|Query
name|query3
init|=
operator|new
name|WildcardQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"metals?"
argument_list|)
argument_list|)
decl_stmt|;
name|Query
name|query4
init|=
operator|new
name|WildcardQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"m?t?ls"
argument_list|)
argument_list|)
decl_stmt|;
name|Query
name|query5
init|=
operator|new
name|WildcardQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"M?t?ls"
argument_list|)
argument_list|)
decl_stmt|;
name|Query
name|query6
init|=
operator|new
name|WildcardQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"meta??"
argument_list|)
argument_list|)
decl_stmt|;
name|assertMatches
argument_list|(
name|searcher
argument_list|,
name|query1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertMatches
argument_list|(
name|searcher
argument_list|,
name|query2
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertMatches
argument_list|(
name|searcher
argument_list|,
name|query3
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertMatches
argument_list|(
name|searcher
argument_list|,
name|query4
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|assertMatches
argument_list|(
name|searcher
argument_list|,
name|query5
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertMatches
argument_list|(
name|searcher
argument_list|,
name|query6
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// Query: 'meta??' matches 'metals' not 'metal'
block|}
DECL|method|getIndexStore
specifier|private
name|RAMDirectory
name|getIndexStore
parameter_list|(
name|String
name|field
parameter_list|,
name|String
index|[]
name|contents
parameter_list|)
throws|throws
name|IOException
block|{
name|RAMDirectory
name|indexStore
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|indexStore
argument_list|,
operator|new
name|SimpleAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|contents
operator|.
name|length
condition|;
operator|++
name|i
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
operator|new
name|Field
argument_list|(
name|field
argument_list|,
name|contents
index|[
name|i
index|]
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
name|ANALYZED
argument_list|)
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
name|writer
operator|.
name|optimize
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|indexStore
return|;
block|}
DECL|method|assertMatches
specifier|private
name|void
name|assertMatches
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|Query
name|q
parameter_list|,
name|int
name|expectedMatches
parameter_list|)
throws|throws
name|IOException
block|{
name|ScoreDoc
index|[]
name|result
init|=
name|searcher
operator|.
name|search
argument_list|(
name|q
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
name|expectedMatches
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test that wild card queries are parsed to the correct type and are searched correctly.    * This test looks at both parsing and execution of wildcard queries.    * Although placed here, it also tests prefix queries, verifying that    * prefix queries are not parsed into wild card queries, and viceversa.    * @throws Exception    */
DECL|method|testParsingAndSearching
specifier|public
name|void
name|testParsingAndSearching
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|field
init|=
literal|"content"
decl_stmt|;
name|boolean
name|dbg
init|=
literal|false
decl_stmt|;
name|QueryParser
name|qp
init|=
operator|new
name|QueryParser
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|field
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
argument_list|)
decl_stmt|;
name|qp
operator|.
name|setAllowLeadingWildcard
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|String
name|docs
index|[]
init|=
block|{
literal|"\\ abcdefg1"
block|,
literal|"\\79 hijklmn1"
block|,
literal|"\\\\ opqrstu1"
block|,     }
decl_stmt|;
comment|// queries that should find all docs
name|String
name|matchAll
index|[]
init|=
block|{
literal|"*"
block|,
literal|"*1"
block|,
literal|"**1"
block|,
literal|"*?"
block|,
literal|"*?1"
block|,
literal|"?*1"
block|,
literal|"**"
block|,
literal|"***"
block|,
literal|"\\\\*"
block|}
decl_stmt|;
comment|// queries that should find no docs
name|String
name|matchNone
index|[]
init|=
block|{
literal|"a*h"
block|,
literal|"a?h"
block|,
literal|"*a*h"
block|,
literal|"?a"
block|,
literal|"a?"
block|,     }
decl_stmt|;
comment|// queries that should be parsed to prefix queries
name|String
name|matchOneDocPrefix
index|[]
index|[]
init|=
block|{
block|{
literal|"a*"
block|,
literal|"ab*"
block|,
literal|"abc*"
block|, }
block|,
comment|// these should find only doc 0
block|{
literal|"h*"
block|,
literal|"hi*"
block|,
literal|"hij*"
block|,
literal|"\\\\7*"
block|}
block|,
comment|// these should find only doc 1
block|{
literal|"o*"
block|,
literal|"op*"
block|,
literal|"opq*"
block|,
literal|"\\\\\\\\*"
block|}
block|,
comment|// these should find only doc 2
block|}
decl_stmt|;
comment|// queries that should be parsed to wildcard queries
name|String
name|matchOneDocWild
index|[]
index|[]
init|=
block|{
block|{
literal|"*a*"
block|,
literal|"*ab*"
block|,
literal|"*abc**"
block|,
literal|"ab*e*"
block|,
literal|"*g?"
block|,
literal|"*f?1"
block|,
literal|"abc**"
block|}
block|,
comment|// these should find only doc 0
block|{
literal|"*h*"
block|,
literal|"*hi*"
block|,
literal|"*hij**"
block|,
literal|"hi*k*"
block|,
literal|"*n?"
block|,
literal|"*m?1"
block|,
literal|"hij**"
block|}
block|,
comment|// these should find only doc 1
block|{
literal|"*o*"
block|,
literal|"*op*"
block|,
literal|"*opq**"
block|,
literal|"op*q*"
block|,
literal|"*u?"
block|,
literal|"*t?1"
block|,
literal|"opq**"
block|}
block|,
comment|// these should find only doc 2
block|}
decl_stmt|;
comment|// prepare the index
name|RAMDirectory
name|dir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|iw
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|LIMITED
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
name|docs
operator|.
name|length
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
operator|new
name|Field
argument_list|(
name|field
argument_list|,
name|docs
index|[
name|i
index|]
argument_list|,
name|Store
operator|.
name|NO
argument_list|,
name|Index
operator|.
name|ANALYZED
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
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|dir
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|// test queries that must find all
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|matchAll
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|qtxt
init|=
name|matchAll
index|[
name|i
index|]
decl_stmt|;
name|Query
name|q
init|=
name|qp
operator|.
name|parse
argument_list|(
name|qtxt
argument_list|)
decl_stmt|;
if|if
condition|(
name|dbg
condition|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"matchAll: qtxt="
operator|+
name|qtxt
operator|+
literal|" q="
operator|+
name|q
operator|+
literal|" "
operator|+
name|q
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|ScoreDoc
index|[]
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|q
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
name|docs
operator|.
name|length
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
comment|// test queries that must find none
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|matchNone
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|qtxt
init|=
name|matchNone
index|[
name|i
index|]
decl_stmt|;
name|Query
name|q
init|=
name|qp
operator|.
name|parse
argument_list|(
name|qtxt
argument_list|)
decl_stmt|;
if|if
condition|(
name|dbg
condition|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"matchNone: qtxt="
operator|+
name|qtxt
operator|+
literal|" q="
operator|+
name|q
operator|+
literal|" "
operator|+
name|q
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|ScoreDoc
index|[]
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|q
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
literal|0
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
comment|// test queries that must be prefix queries and must find only one doc
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|matchOneDocPrefix
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|matchOneDocPrefix
index|[
name|i
index|]
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|String
name|qtxt
init|=
name|matchOneDocPrefix
index|[
name|i
index|]
index|[
name|j
index|]
decl_stmt|;
name|Query
name|q
init|=
name|qp
operator|.
name|parse
argument_list|(
name|qtxt
argument_list|)
decl_stmt|;
if|if
condition|(
name|dbg
condition|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"match 1 prefix: doc="
operator|+
name|docs
index|[
name|i
index|]
operator|+
literal|" qtxt="
operator|+
name|qtxt
operator|+
literal|" q="
operator|+
name|q
operator|+
literal|" "
operator|+
name|q
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|PrefixQuery
operator|.
name|class
argument_list|,
name|q
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|ScoreDoc
index|[]
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|q
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
literal|1
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|i
argument_list|,
name|hits
index|[
literal|0
index|]
operator|.
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
comment|// test queries that must be wildcard queries and must find only one doc
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|matchOneDocPrefix
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|matchOneDocWild
index|[
name|i
index|]
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|String
name|qtxt
init|=
name|matchOneDocWild
index|[
name|i
index|]
index|[
name|j
index|]
decl_stmt|;
name|Query
name|q
init|=
name|qp
operator|.
name|parse
argument_list|(
name|qtxt
argument_list|)
decl_stmt|;
if|if
condition|(
name|dbg
condition|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"match 1 wild: doc="
operator|+
name|docs
index|[
name|i
index|]
operator|+
literal|" qtxt="
operator|+
name|qtxt
operator|+
literal|" q="
operator|+
name|q
operator|+
literal|" "
operator|+
name|q
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|WildcardQuery
operator|.
name|class
argument_list|,
name|q
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|ScoreDoc
index|[]
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|q
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
literal|1
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|i
argument_list|,
name|hits
index|[
literal|0
index|]
operator|.
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

