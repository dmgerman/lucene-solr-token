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
name|PhraseQuery
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
name|SearchEquivalenceTestBase
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
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|spans
operator|.
name|SpanTestUtil
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Basic equivalence tests for span queries  */
end_comment

begin_class
DECL|class|TestSpanSearchEquivalence
specifier|public
class|class
name|TestSpanSearchEquivalence
extends|extends
name|SearchEquivalenceTestBase
block|{
comment|// TODO: we could go a little crazy for a lot of these,
comment|// but these are just simple minimal cases in case something
comment|// goes horribly wrong. Put more intense tests elsewhere.
comment|/** SpanTermQuery(A) = TermQuery(A) */
DECL|method|testSpanTermVersusTerm
specifier|public
name|void
name|testSpanTermVersusTerm
parameter_list|()
throws|throws
name|Exception
block|{
name|Term
name|t1
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|assertSameSet
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|t1
argument_list|)
argument_list|,
name|spanQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
name|t1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** SpanOrQuery(A, B) = (A B) */
DECL|method|testSpanOrVersusBoolean
specifier|public
name|void
name|testSpanOrVersusBoolean
parameter_list|()
throws|throws
name|Exception
block|{
name|Term
name|t1
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|Term
name|t2
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|BooleanQuery
name|q1
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|q1
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|t1
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|q1
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|t2
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|SpanQuery
name|q2
init|=
name|spanQuery
argument_list|(
operator|new
name|SpanOrQuery
argument_list|(
name|spanQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
name|t1
argument_list|)
argument_list|)
argument_list|,
name|spanQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
name|t2
argument_list|)
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertSameSet
argument_list|(
name|q1
argument_list|,
name|q2
argument_list|)
expr_stmt|;
block|}
comment|/** SpanNotQuery(A, B) â SpanTermQuery(A) */
DECL|method|testSpanNotVersusSpanTerm
specifier|public
name|void
name|testSpanNotVersusSpanTerm
parameter_list|()
throws|throws
name|Exception
block|{
name|Term
name|t1
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|Term
name|t2
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|assertSubsetOf
argument_list|(
name|spanQuery
argument_list|(
operator|new
name|SpanNotQuery
argument_list|(
name|spanQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
name|t1
argument_list|)
argument_list|)
argument_list|,
name|spanQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
name|t2
argument_list|)
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|spanQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
name|t1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** SpanNotQuery(A, [B C]) â SpanTermQuery(A) */
DECL|method|testSpanNotNearVersusSpanTerm
specifier|public
name|void
name|testSpanNotNearVersusSpanTerm
parameter_list|()
throws|throws
name|Exception
block|{
name|Term
name|t1
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|Term
name|t2
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|Term
name|t3
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|SpanQuery
name|near
init|=
name|spanQuery
argument_list|(
operator|new
name|SpanNearQuery
argument_list|(
operator|new
name|SpanQuery
index|[]
block|{
name|spanQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
name|t2
argument_list|)
argument_list|)
block|,
name|spanQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
name|t3
argument_list|)
argument_list|)
block|}
argument_list|,
literal|10
argument_list|,
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|assertSubsetOf
argument_list|(
name|spanQuery
argument_list|(
operator|new
name|SpanNotQuery
argument_list|(
name|spanQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
name|t1
argument_list|)
argument_list|)
argument_list|,
name|near
argument_list|)
argument_list|)
argument_list|,
name|spanQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
name|t1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** SpanNotQuery([A B], C) â SpanNearQuery([A B]) */
DECL|method|testSpanNotVersusSpanNear
specifier|public
name|void
name|testSpanNotVersusSpanNear
parameter_list|()
throws|throws
name|Exception
block|{
name|Term
name|t1
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|Term
name|t2
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|Term
name|t3
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|SpanQuery
name|near
init|=
name|spanQuery
argument_list|(
operator|new
name|SpanNearQuery
argument_list|(
operator|new
name|SpanQuery
index|[]
block|{
name|spanQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
name|t1
argument_list|)
argument_list|)
block|,
name|spanQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
name|t2
argument_list|)
argument_list|)
block|}
argument_list|,
literal|10
argument_list|,
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|assertSubsetOf
argument_list|(
name|spanQuery
argument_list|(
operator|new
name|SpanNotQuery
argument_list|(
name|near
argument_list|,
name|spanQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
name|t3
argument_list|)
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|near
argument_list|)
expr_stmt|;
block|}
comment|/** SpanNotQuery([A B], [C D]) â SpanNearQuery([A B]) */
DECL|method|testSpanNotNearVersusSpanNear
specifier|public
name|void
name|testSpanNotNearVersusSpanNear
parameter_list|()
throws|throws
name|Exception
block|{
name|Term
name|t1
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|Term
name|t2
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|Term
name|t3
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|Term
name|t4
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|SpanQuery
name|near1
init|=
name|spanQuery
argument_list|(
operator|new
name|SpanNearQuery
argument_list|(
operator|new
name|SpanQuery
index|[]
block|{
name|spanQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
name|t1
argument_list|)
argument_list|)
block|,
name|spanQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
name|t2
argument_list|)
argument_list|)
block|}
argument_list|,
literal|10
argument_list|,
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|SpanQuery
name|near2
init|=
name|spanQuery
argument_list|(
operator|new
name|SpanNearQuery
argument_list|(
operator|new
name|SpanQuery
index|[]
block|{
name|spanQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
name|t3
argument_list|)
argument_list|)
block|,
name|spanQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
name|t4
argument_list|)
argument_list|)
block|}
argument_list|,
literal|10
argument_list|,
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|assertSubsetOf
argument_list|(
name|spanQuery
argument_list|(
operator|new
name|SpanNotQuery
argument_list|(
name|near1
argument_list|,
name|near2
argument_list|)
argument_list|)
argument_list|,
name|near1
argument_list|)
expr_stmt|;
block|}
comment|/** SpanFirstQuery(A, 10) â SpanTermQuery(A) */
DECL|method|testSpanFirstVersusSpanTerm
specifier|public
name|void
name|testSpanFirstVersusSpanTerm
parameter_list|()
throws|throws
name|Exception
block|{
name|Term
name|t1
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|assertSubsetOf
argument_list|(
name|spanQuery
argument_list|(
operator|new
name|SpanFirstQuery
argument_list|(
name|spanQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
name|t1
argument_list|)
argument_list|)
argument_list|,
literal|10
argument_list|)
argument_list|)
argument_list|,
name|spanQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
name|t1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** SpanNearQuery([A, B], 0, true) = "A B" */
DECL|method|testSpanNearVersusPhrase
specifier|public
name|void
name|testSpanNearVersusPhrase
parameter_list|()
throws|throws
name|Exception
block|{
name|Term
name|t1
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|Term
name|t2
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|SpanQuery
name|subquery
index|[]
init|=
operator|new
name|SpanQuery
index|[]
block|{
name|spanQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
name|t1
argument_list|)
argument_list|)
block|,
name|spanQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
name|t2
argument_list|)
argument_list|)
block|}
decl_stmt|;
name|SpanQuery
name|q1
init|=
name|spanQuery
argument_list|(
operator|new
name|SpanNearQuery
argument_list|(
name|subquery
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|PhraseQuery
name|q2
init|=
operator|new
name|PhraseQuery
argument_list|()
decl_stmt|;
name|q2
operator|.
name|add
argument_list|(
name|t1
argument_list|)
expr_stmt|;
name|q2
operator|.
name|add
argument_list|(
name|t2
argument_list|)
expr_stmt|;
name|assertSameSet
argument_list|(
name|q1
argument_list|,
name|q2
argument_list|)
expr_stmt|;
block|}
comment|/** SpanNearQuery([A, B], â, false) = +A +B */
DECL|method|testSpanNearVersusBooleanAnd
specifier|public
name|void
name|testSpanNearVersusBooleanAnd
parameter_list|()
throws|throws
name|Exception
block|{
name|Term
name|t1
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|Term
name|t2
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|SpanQuery
name|subquery
index|[]
init|=
operator|new
name|SpanQuery
index|[]
block|{
name|spanQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
name|t1
argument_list|)
argument_list|)
block|,
name|spanQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
name|t2
argument_list|)
argument_list|)
block|}
decl_stmt|;
name|SpanQuery
name|q1
init|=
name|spanQuery
argument_list|(
operator|new
name|SpanNearQuery
argument_list|(
name|subquery
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|BooleanQuery
name|q2
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|q2
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|t1
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|q2
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|t2
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|assertSameSet
argument_list|(
name|q1
argument_list|,
name|q2
argument_list|)
expr_stmt|;
block|}
comment|/** SpanNearQuery([A B], 0, false) â SpanNearQuery([A B], 1, false) */
DECL|method|testSpanNearVersusSloppySpanNear
specifier|public
name|void
name|testSpanNearVersusSloppySpanNear
parameter_list|()
throws|throws
name|Exception
block|{
name|Term
name|t1
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|Term
name|t2
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|SpanQuery
name|subquery
index|[]
init|=
operator|new
name|SpanQuery
index|[]
block|{
name|spanQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
name|t1
argument_list|)
argument_list|)
block|,
name|spanQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
name|t2
argument_list|)
argument_list|)
block|}
decl_stmt|;
name|SpanQuery
name|q1
init|=
name|spanQuery
argument_list|(
operator|new
name|SpanNearQuery
argument_list|(
name|subquery
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|SpanQuery
name|q2
init|=
name|spanQuery
argument_list|(
operator|new
name|SpanNearQuery
argument_list|(
name|subquery
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|assertSubsetOf
argument_list|(
name|q1
argument_list|,
name|q2
argument_list|)
expr_stmt|;
block|}
comment|/** SpanNearQuery([A B], 3, true) â SpanNearQuery([A B], 3, false) */
DECL|method|testSpanNearInOrderVersusOutOfOrder
specifier|public
name|void
name|testSpanNearInOrderVersusOutOfOrder
parameter_list|()
throws|throws
name|Exception
block|{
name|Term
name|t1
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|Term
name|t2
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|SpanQuery
name|subquery
index|[]
init|=
operator|new
name|SpanQuery
index|[]
block|{
name|spanQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
name|t1
argument_list|)
argument_list|)
block|,
name|spanQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
name|t2
argument_list|)
argument_list|)
block|}
decl_stmt|;
name|SpanQuery
name|q1
init|=
name|spanQuery
argument_list|(
operator|new
name|SpanNearQuery
argument_list|(
name|subquery
argument_list|,
literal|3
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|SpanQuery
name|q2
init|=
name|spanQuery
argument_list|(
operator|new
name|SpanNearQuery
argument_list|(
name|subquery
argument_list|,
literal|3
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|assertSubsetOf
argument_list|(
name|q1
argument_list|,
name|q2
argument_list|)
expr_stmt|;
block|}
comment|/** SpanNearQuery([A B], N, false) â SpanNearQuery([A B], N+1, false) */
DECL|method|testSpanNearIncreasingSloppiness
specifier|public
name|void
name|testSpanNearIncreasingSloppiness
parameter_list|()
throws|throws
name|Exception
block|{
name|Term
name|t1
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|Term
name|t2
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|SpanQuery
name|subquery
index|[]
init|=
operator|new
name|SpanQuery
index|[]
block|{
name|spanQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
name|t1
argument_list|)
argument_list|)
block|,
name|spanQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
name|t2
argument_list|)
argument_list|)
block|}
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|SpanQuery
name|q1
init|=
name|spanQuery
argument_list|(
operator|new
name|SpanNearQuery
argument_list|(
name|subquery
argument_list|,
name|i
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|SpanQuery
name|q2
init|=
name|spanQuery
argument_list|(
operator|new
name|SpanNearQuery
argument_list|(
name|subquery
argument_list|,
name|i
operator|+
literal|1
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|assertSubsetOf
argument_list|(
name|q1
argument_list|,
name|q2
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** SpanNearQuery([A B C], N, false) â SpanNearQuery([A B C], N+1, false) */
DECL|method|testSpanNearIncreasingSloppiness3
specifier|public
name|void
name|testSpanNearIncreasingSloppiness3
parameter_list|()
throws|throws
name|Exception
block|{
name|Term
name|t1
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|Term
name|t2
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|Term
name|t3
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|SpanQuery
name|subquery
index|[]
init|=
operator|new
name|SpanQuery
index|[]
block|{
name|spanQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
name|t1
argument_list|)
argument_list|)
block|,
name|spanQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
name|t2
argument_list|)
argument_list|)
block|,
name|spanQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
name|t3
argument_list|)
argument_list|)
block|}
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|SpanQuery
name|q1
init|=
name|spanQuery
argument_list|(
operator|new
name|SpanNearQuery
argument_list|(
name|subquery
argument_list|,
name|i
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|SpanQuery
name|q2
init|=
name|spanQuery
argument_list|(
operator|new
name|SpanNearQuery
argument_list|(
name|subquery
argument_list|,
name|i
operator|+
literal|1
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|assertSubsetOf
argument_list|(
name|q1
argument_list|,
name|q2
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** SpanNearQuery([A B], N, true) â SpanNearQuery([A B], N+1, true) */
DECL|method|testSpanNearIncreasingOrderedSloppiness
specifier|public
name|void
name|testSpanNearIncreasingOrderedSloppiness
parameter_list|()
throws|throws
name|Exception
block|{
name|Term
name|t1
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|Term
name|t2
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|SpanQuery
name|subquery
index|[]
init|=
operator|new
name|SpanQuery
index|[]
block|{
name|spanQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
name|t1
argument_list|)
argument_list|)
block|,
name|spanQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
name|t2
argument_list|)
argument_list|)
block|}
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|SpanQuery
name|q1
init|=
name|spanQuery
argument_list|(
operator|new
name|SpanNearQuery
argument_list|(
name|subquery
argument_list|,
name|i
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|SpanQuery
name|q2
init|=
name|spanQuery
argument_list|(
operator|new
name|SpanNearQuery
argument_list|(
name|subquery
argument_list|,
name|i
operator|+
literal|1
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|assertSubsetOf
argument_list|(
name|q1
argument_list|,
name|q2
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** SpanNearQuery([A B C], N, true) â SpanNearQuery([A B C], N+1, true) */
DECL|method|testSpanNearIncreasingOrderedSloppiness3
specifier|public
name|void
name|testSpanNearIncreasingOrderedSloppiness3
parameter_list|()
throws|throws
name|Exception
block|{
name|Term
name|t1
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|Term
name|t2
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|Term
name|t3
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|SpanQuery
name|subquery
index|[]
init|=
operator|new
name|SpanQuery
index|[]
block|{
name|spanQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
name|t1
argument_list|)
argument_list|)
block|,
name|spanQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
name|t2
argument_list|)
argument_list|)
block|,
name|spanQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
name|t3
argument_list|)
argument_list|)
block|}
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|SpanQuery
name|q1
init|=
name|spanQuery
argument_list|(
operator|new
name|SpanNearQuery
argument_list|(
name|subquery
argument_list|,
name|i
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|SpanQuery
name|q2
init|=
name|spanQuery
argument_list|(
operator|new
name|SpanNearQuery
argument_list|(
name|subquery
argument_list|,
name|i
operator|+
literal|1
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|assertSubsetOf
argument_list|(
name|q1
argument_list|,
name|q2
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** SpanPositionRangeQuery(A, M, N) â TermQuery(A) */
DECL|method|testSpanRangeTerm
specifier|public
name|void
name|testSpanRangeTerm
parameter_list|()
throws|throws
name|Exception
block|{
name|Term
name|t1
init|=
name|randomTerm
argument_list|()
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
literal|5
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
literal|5
condition|;
name|j
operator|++
control|)
block|{
name|Query
name|q1
init|=
name|spanQuery
argument_list|(
operator|new
name|SpanPositionRangeQuery
argument_list|(
name|spanQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
name|t1
argument_list|)
argument_list|)
argument_list|,
name|i
argument_list|,
name|i
operator|+
name|j
argument_list|)
argument_list|)
decl_stmt|;
name|Query
name|q2
init|=
operator|new
name|TermQuery
argument_list|(
name|t1
argument_list|)
decl_stmt|;
name|assertSubsetOf
argument_list|(
name|q1
argument_list|,
name|q2
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** SpanPositionRangeQuery(A, M, N) â SpanFirstQuery(A, M, N+1) */
DECL|method|testSpanRangeTermIncreasingEnd
specifier|public
name|void
name|testSpanRangeTermIncreasingEnd
parameter_list|()
throws|throws
name|Exception
block|{
name|Term
name|t1
init|=
name|randomTerm
argument_list|()
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
literal|5
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
literal|5
condition|;
name|j
operator|++
control|)
block|{
name|Query
name|q1
init|=
name|spanQuery
argument_list|(
operator|new
name|SpanPositionRangeQuery
argument_list|(
name|spanQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
name|t1
argument_list|)
argument_list|)
argument_list|,
name|i
argument_list|,
name|i
operator|+
name|j
argument_list|)
argument_list|)
decl_stmt|;
name|Query
name|q2
init|=
name|spanQuery
argument_list|(
operator|new
name|SpanPositionRangeQuery
argument_list|(
name|spanQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
name|t1
argument_list|)
argument_list|)
argument_list|,
name|i
argument_list|,
name|i
operator|+
name|j
operator|+
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|assertSubsetOf
argument_list|(
name|q1
argument_list|,
name|q2
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** SpanPositionRangeQuery(A, 0, â) = TermQuery(A) */
DECL|method|testSpanRangeTermEverything
specifier|public
name|void
name|testSpanRangeTermEverything
parameter_list|()
throws|throws
name|Exception
block|{
name|Term
name|t1
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|Query
name|q1
init|=
name|spanQuery
argument_list|(
operator|new
name|SpanPositionRangeQuery
argument_list|(
name|spanQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
name|t1
argument_list|)
argument_list|)
argument_list|,
literal|0
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
argument_list|)
decl_stmt|;
name|Query
name|q2
init|=
operator|new
name|TermQuery
argument_list|(
name|t1
argument_list|)
decl_stmt|;
name|assertSameSet
argument_list|(
name|q1
argument_list|,
name|q2
argument_list|)
expr_stmt|;
block|}
comment|/** SpanPositionRangeQuery([A B], M, N) â SpanNearQuery([A B]) */
DECL|method|testSpanRangeNear
specifier|public
name|void
name|testSpanRangeNear
parameter_list|()
throws|throws
name|Exception
block|{
name|Term
name|t1
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|Term
name|t2
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|SpanQuery
name|subquery
index|[]
init|=
operator|new
name|SpanQuery
index|[]
block|{
name|spanQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
name|t1
argument_list|)
argument_list|)
block|,
name|spanQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
name|t2
argument_list|)
argument_list|)
block|}
decl_stmt|;
name|SpanQuery
name|nearQuery
init|=
name|spanQuery
argument_list|(
operator|new
name|SpanNearQuery
argument_list|(
name|subquery
argument_list|,
literal|10
argument_list|,
literal|true
argument_list|)
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
literal|5
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
literal|5
condition|;
name|j
operator|++
control|)
block|{
name|Query
name|q1
init|=
name|spanQuery
argument_list|(
operator|new
name|SpanPositionRangeQuery
argument_list|(
name|nearQuery
argument_list|,
name|i
argument_list|,
name|i
operator|+
name|j
argument_list|)
argument_list|)
decl_stmt|;
name|Query
name|q2
init|=
name|nearQuery
decl_stmt|;
name|assertSubsetOf
argument_list|(
name|q1
argument_list|,
name|q2
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** SpanPositionRangeQuery([A B], M, N) â SpanFirstQuery([A B], M, N+1) */
DECL|method|testSpanRangeNearIncreasingEnd
specifier|public
name|void
name|testSpanRangeNearIncreasingEnd
parameter_list|()
throws|throws
name|Exception
block|{
name|Term
name|t1
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|Term
name|t2
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|SpanQuery
name|subquery
index|[]
init|=
operator|new
name|SpanQuery
index|[]
block|{
name|spanQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
name|t1
argument_list|)
argument_list|)
block|,
name|spanQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
name|t2
argument_list|)
argument_list|)
block|}
decl_stmt|;
name|SpanQuery
name|nearQuery
init|=
name|spanQuery
argument_list|(
operator|new
name|SpanNearQuery
argument_list|(
name|subquery
argument_list|,
literal|10
argument_list|,
literal|true
argument_list|)
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
literal|5
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
literal|5
condition|;
name|j
operator|++
control|)
block|{
name|Query
name|q1
init|=
name|spanQuery
argument_list|(
operator|new
name|SpanPositionRangeQuery
argument_list|(
name|nearQuery
argument_list|,
name|i
argument_list|,
name|i
operator|+
name|j
argument_list|)
argument_list|)
decl_stmt|;
name|Query
name|q2
init|=
name|spanQuery
argument_list|(
operator|new
name|SpanPositionRangeQuery
argument_list|(
name|nearQuery
argument_list|,
name|i
argument_list|,
name|i
operator|+
name|j
operator|+
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|assertSubsetOf
argument_list|(
name|q1
argument_list|,
name|q2
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** SpanPositionRangeQuery([A B], â) = SpanNearQuery([A B]) */
DECL|method|testSpanRangeNearEverything
specifier|public
name|void
name|testSpanRangeNearEverything
parameter_list|()
throws|throws
name|Exception
block|{
name|Term
name|t1
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|Term
name|t2
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|SpanQuery
name|subquery
index|[]
init|=
operator|new
name|SpanQuery
index|[]
block|{
name|spanQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
name|t1
argument_list|)
argument_list|)
block|,
name|spanQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
name|t2
argument_list|)
argument_list|)
block|}
decl_stmt|;
name|SpanQuery
name|nearQuery
init|=
name|spanQuery
argument_list|(
operator|new
name|SpanNearQuery
argument_list|(
name|subquery
argument_list|,
literal|10
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|Query
name|q1
init|=
name|spanQuery
argument_list|(
operator|new
name|SpanPositionRangeQuery
argument_list|(
name|nearQuery
argument_list|,
literal|0
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
argument_list|)
decl_stmt|;
name|Query
name|q2
init|=
name|nearQuery
decl_stmt|;
name|assertSameSet
argument_list|(
name|q1
argument_list|,
name|q2
argument_list|)
expr_stmt|;
block|}
comment|/** SpanFirstQuery(A, N) â TermQuery(A) */
DECL|method|testSpanFirstTerm
specifier|public
name|void
name|testSpanFirstTerm
parameter_list|()
throws|throws
name|Exception
block|{
name|Term
name|t1
init|=
name|randomTerm
argument_list|()
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|Query
name|q1
init|=
name|spanQuery
argument_list|(
operator|new
name|SpanFirstQuery
argument_list|(
name|spanQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
name|t1
argument_list|)
argument_list|)
argument_list|,
name|i
argument_list|)
argument_list|)
decl_stmt|;
name|Query
name|q2
init|=
operator|new
name|TermQuery
argument_list|(
name|t1
argument_list|)
decl_stmt|;
name|assertSubsetOf
argument_list|(
name|q1
argument_list|,
name|q2
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** SpanFirstQuery(A, N) â SpanFirstQuery(A, N+1) */
DECL|method|testSpanFirstTermIncreasing
specifier|public
name|void
name|testSpanFirstTermIncreasing
parameter_list|()
throws|throws
name|Exception
block|{
name|Term
name|t1
init|=
name|randomTerm
argument_list|()
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|Query
name|q1
init|=
name|spanQuery
argument_list|(
operator|new
name|SpanFirstQuery
argument_list|(
name|spanQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
name|t1
argument_list|)
argument_list|)
argument_list|,
name|i
argument_list|)
argument_list|)
decl_stmt|;
name|Query
name|q2
init|=
name|spanQuery
argument_list|(
operator|new
name|SpanFirstQuery
argument_list|(
name|spanQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
name|t1
argument_list|)
argument_list|)
argument_list|,
name|i
operator|+
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|assertSubsetOf
argument_list|(
name|q1
argument_list|,
name|q2
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** SpanFirstQuery(A, â) = TermQuery(A) */
DECL|method|testSpanFirstTermEverything
specifier|public
name|void
name|testSpanFirstTermEverything
parameter_list|()
throws|throws
name|Exception
block|{
name|Term
name|t1
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|Query
name|q1
init|=
name|spanQuery
argument_list|(
operator|new
name|SpanFirstQuery
argument_list|(
name|spanQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
name|t1
argument_list|)
argument_list|)
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
argument_list|)
decl_stmt|;
name|Query
name|q2
init|=
operator|new
name|TermQuery
argument_list|(
name|t1
argument_list|)
decl_stmt|;
name|assertSameSet
argument_list|(
name|q1
argument_list|,
name|q2
argument_list|)
expr_stmt|;
block|}
comment|/** SpanFirstQuery([A B], N) â SpanNearQuery([A B]) */
DECL|method|testSpanFirstNear
specifier|public
name|void
name|testSpanFirstNear
parameter_list|()
throws|throws
name|Exception
block|{
name|Term
name|t1
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|Term
name|t2
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|SpanQuery
name|subquery
index|[]
init|=
operator|new
name|SpanQuery
index|[]
block|{
name|spanQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
name|t1
argument_list|)
argument_list|)
block|,
name|spanQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
name|t2
argument_list|)
argument_list|)
block|}
decl_stmt|;
name|SpanQuery
name|nearQuery
init|=
name|spanQuery
argument_list|(
operator|new
name|SpanNearQuery
argument_list|(
name|subquery
argument_list|,
literal|10
argument_list|,
literal|true
argument_list|)
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|Query
name|q1
init|=
name|spanQuery
argument_list|(
operator|new
name|SpanFirstQuery
argument_list|(
name|nearQuery
argument_list|,
name|i
argument_list|)
argument_list|)
decl_stmt|;
name|Query
name|q2
init|=
name|nearQuery
decl_stmt|;
name|assertSubsetOf
argument_list|(
name|q1
argument_list|,
name|q2
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** SpanFirstQuery([A B], N) â SpanFirstQuery([A B], N+1) */
DECL|method|testSpanFirstNearIncreasing
specifier|public
name|void
name|testSpanFirstNearIncreasing
parameter_list|()
throws|throws
name|Exception
block|{
name|Term
name|t1
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|Term
name|t2
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|SpanQuery
name|subquery
index|[]
init|=
operator|new
name|SpanQuery
index|[]
block|{
name|spanQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
name|t1
argument_list|)
argument_list|)
block|,
name|spanQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
name|t2
argument_list|)
argument_list|)
block|}
decl_stmt|;
name|SpanQuery
name|nearQuery
init|=
name|spanQuery
argument_list|(
operator|new
name|SpanNearQuery
argument_list|(
name|subquery
argument_list|,
literal|10
argument_list|,
literal|true
argument_list|)
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|Query
name|q1
init|=
name|spanQuery
argument_list|(
operator|new
name|SpanFirstQuery
argument_list|(
name|nearQuery
argument_list|,
name|i
argument_list|)
argument_list|)
decl_stmt|;
name|Query
name|q2
init|=
name|spanQuery
argument_list|(
operator|new
name|SpanFirstQuery
argument_list|(
name|nearQuery
argument_list|,
name|i
operator|+
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|assertSubsetOf
argument_list|(
name|q1
argument_list|,
name|q2
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** SpanFirstQuery([A B], â) = SpanNearQuery([A B]) */
DECL|method|testSpanFirstNearEverything
specifier|public
name|void
name|testSpanFirstNearEverything
parameter_list|()
throws|throws
name|Exception
block|{
name|Term
name|t1
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|Term
name|t2
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|SpanQuery
name|subquery
index|[]
init|=
operator|new
name|SpanQuery
index|[]
block|{
name|spanQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
name|t1
argument_list|)
argument_list|)
block|,
name|spanQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
name|t2
argument_list|)
argument_list|)
block|}
decl_stmt|;
name|SpanQuery
name|nearQuery
init|=
name|spanQuery
argument_list|(
operator|new
name|SpanNearQuery
argument_list|(
name|subquery
argument_list|,
literal|10
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|Query
name|q1
init|=
name|spanQuery
argument_list|(
operator|new
name|SpanFirstQuery
argument_list|(
name|nearQuery
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
argument_list|)
decl_stmt|;
name|Query
name|q2
init|=
name|nearQuery
decl_stmt|;
name|assertSameSet
argument_list|(
name|q1
argument_list|,
name|q2
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

