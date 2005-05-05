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
comment|/**  * Copyright 2004-2005 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|java
operator|.
name|util
operator|.
name|TreeSet
import|;
end_import

begin_class
DECL|class|CheckHits
specifier|public
class|class
name|CheckHits
block|{
comment|/** Tests that a query has expected document number results.    */
DECL|method|checkHits
specifier|public
specifier|static
name|void
name|checkHits
parameter_list|(
name|Query
name|query
parameter_list|,
name|String
name|defaultFieldName
parameter_list|,
name|Searcher
name|searcher
parameter_list|,
name|int
index|[]
name|results
parameter_list|,
name|TestCase
name|testCase
parameter_list|)
throws|throws
name|IOException
block|{
name|Hits
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|Set
name|correct
init|=
operator|new
name|TreeSet
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
name|results
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|correct
operator|.
name|add
argument_list|(
operator|new
name|Integer
argument_list|(
name|results
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Set
name|actual
init|=
operator|new
name|TreeSet
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
name|hits
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|actual
operator|.
name|add
argument_list|(
operator|new
name|Integer
argument_list|(
name|hits
operator|.
name|id
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|TestCase
operator|.
name|assertEquals
argument_list|(
name|query
operator|.
name|toString
argument_list|(
name|defaultFieldName
argument_list|)
argument_list|,
name|correct
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
comment|/** Tests that a Hits has an expected order of documents */
DECL|method|checkDocIds
specifier|public
specifier|static
name|void
name|checkDocIds
parameter_list|(
name|String
name|mes
parameter_list|,
name|int
index|[]
name|results
parameter_list|,
name|Hits
name|hits
parameter_list|,
name|TestCase
name|testCase
parameter_list|)
throws|throws
name|IOException
block|{
name|TestCase
operator|.
name|assertEquals
argument_list|(
name|mes
operator|+
literal|" nr of hits"
argument_list|,
name|results
operator|.
name|length
argument_list|,
name|hits
operator|.
name|length
argument_list|()
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
name|results
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|TestCase
operator|.
name|assertEquals
argument_list|(
name|mes
operator|+
literal|" doc nrs for hit "
operator|+
name|i
argument_list|,
name|results
index|[
name|i
index|]
argument_list|,
name|hits
operator|.
name|id
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Tests that two queries have an expected order of documents,    * and that the two queries have the same score values.    */
DECL|method|checkHitsQuery
specifier|public
specifier|static
name|void
name|checkHitsQuery
parameter_list|(
name|Query
name|query
parameter_list|,
name|Hits
name|hits1
parameter_list|,
name|Hits
name|hits2
parameter_list|,
name|int
index|[]
name|results
parameter_list|,
name|TestCase
name|testCase
parameter_list|)
throws|throws
name|IOException
block|{
name|checkDocIds
argument_list|(
literal|"hits1"
argument_list|,
name|results
argument_list|,
name|hits1
argument_list|,
name|testCase
argument_list|)
expr_stmt|;
name|checkDocIds
argument_list|(
literal|"hits2"
argument_list|,
name|results
argument_list|,
name|hits2
argument_list|,
name|testCase
argument_list|)
expr_stmt|;
specifier|final
name|float
name|scoreTolerance
init|=
literal|1.0e-7f
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
name|results
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|Math
operator|.
name|abs
argument_list|(
name|hits1
operator|.
name|score
argument_list|(
name|i
argument_list|)
operator|-
name|hits2
operator|.
name|score
argument_list|(
name|i
argument_list|)
argument_list|)
operator|>
name|scoreTolerance
condition|)
block|{
name|TestCase
operator|.
name|fail
argument_list|(
literal|"Hit "
operator|+
name|i
operator|+
literal|", doc nrs "
operator|+
name|hits1
operator|.
name|id
argument_list|(
name|i
argument_list|)
operator|+
literal|" and "
operator|+
name|hits2
operator|.
name|id
argument_list|(
name|i
argument_list|)
operator|+
literal|"\nunequal scores: "
operator|+
name|hits1
operator|.
name|score
argument_list|(
name|i
argument_list|)
operator|+
literal|"\n           and: "
operator|+
name|hits2
operator|.
name|score
argument_list|(
name|i
argument_list|)
operator|+
literal|"\nfor query:"
operator|+
name|query
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|printDocNrs
specifier|public
specifier|static
name|void
name|printDocNrs
parameter_list|(
name|Hits
name|hits
parameter_list|)
throws|throws
name|IOException
block|{
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|"new int[] {"
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
name|hits
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
name|hits
operator|.
name|id
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|!=
name|hits
operator|.
name|length
argument_list|()
operator|-
literal|1
condition|)
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

