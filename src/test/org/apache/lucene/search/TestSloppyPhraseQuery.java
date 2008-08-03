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
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|index
operator|.
name|IndexWriter
operator|.
name|MaxFieldLength
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
name|store
operator|.
name|RAMDirectory
import|;
end_import

begin_class
DECL|class|TestSloppyPhraseQuery
specifier|public
class|class
name|TestSloppyPhraseQuery
extends|extends
name|TestCase
block|{
DECL|field|S_1
specifier|private
specifier|static
specifier|final
name|String
name|S_1
init|=
literal|"A A A"
decl_stmt|;
DECL|field|S_2
specifier|private
specifier|static
specifier|final
name|String
name|S_2
init|=
literal|"A 1 2 3 A 4 5 6 A"
decl_stmt|;
DECL|field|DOC_1
specifier|private
specifier|static
specifier|final
name|Document
name|DOC_1
init|=
name|makeDocument
argument_list|(
literal|"X "
operator|+
name|S_1
operator|+
literal|" Y"
argument_list|)
decl_stmt|;
DECL|field|DOC_2
specifier|private
specifier|static
specifier|final
name|Document
name|DOC_2
init|=
name|makeDocument
argument_list|(
literal|"X "
operator|+
name|S_2
operator|+
literal|" Y"
argument_list|)
decl_stmt|;
DECL|field|DOC_3
specifier|private
specifier|static
specifier|final
name|Document
name|DOC_3
init|=
name|makeDocument
argument_list|(
literal|"X "
operator|+
name|S_1
operator|+
literal|" A Y"
argument_list|)
decl_stmt|;
DECL|field|DOC_1_B
specifier|private
specifier|static
specifier|final
name|Document
name|DOC_1_B
init|=
name|makeDocument
argument_list|(
literal|"X "
operator|+
name|S_1
operator|+
literal|" Y N N N N "
operator|+
name|S_1
operator|+
literal|" Z"
argument_list|)
decl_stmt|;
DECL|field|DOC_2_B
specifier|private
specifier|static
specifier|final
name|Document
name|DOC_2_B
init|=
name|makeDocument
argument_list|(
literal|"X "
operator|+
name|S_2
operator|+
literal|" Y N N N N "
operator|+
name|S_2
operator|+
literal|" Z"
argument_list|)
decl_stmt|;
DECL|field|DOC_3_B
specifier|private
specifier|static
specifier|final
name|Document
name|DOC_3_B
init|=
name|makeDocument
argument_list|(
literal|"X "
operator|+
name|S_1
operator|+
literal|" A Y N N N N "
operator|+
name|S_1
operator|+
literal|" A Y"
argument_list|)
decl_stmt|;
DECL|field|DOC_4
specifier|private
specifier|static
specifier|final
name|Document
name|DOC_4
init|=
name|makeDocument
argument_list|(
literal|"A A X A X B A X B B A A X B A A"
argument_list|)
decl_stmt|;
DECL|field|QUERY_1
specifier|private
specifier|static
specifier|final
name|PhraseQuery
name|QUERY_1
init|=
name|makePhraseQuery
argument_list|(
name|S_1
argument_list|)
decl_stmt|;
DECL|field|QUERY_2
specifier|private
specifier|static
specifier|final
name|PhraseQuery
name|QUERY_2
init|=
name|makePhraseQuery
argument_list|(
name|S_2
argument_list|)
decl_stmt|;
DECL|field|QUERY_4
specifier|private
specifier|static
specifier|final
name|PhraseQuery
name|QUERY_4
init|=
name|makePhraseQuery
argument_list|(
literal|"X A A"
argument_list|)
decl_stmt|;
comment|/**    * Test DOC_4 and QUERY_4.    * QUERY_4 has a fuzzy (len=1) match to DOC_4, so all slop values> 0 should succeed.    * But only the 3rd sequence of A's in DOC_4 will do.    */
DECL|method|testDoc4_Query4_All_Slops_Should_match
specifier|public
name|void
name|testDoc4_Query4_All_Slops_Should_match
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|slop
init|=
literal|0
init|;
name|slop
operator|<
literal|30
condition|;
name|slop
operator|++
control|)
block|{
name|int
name|numResultsExpected
init|=
name|slop
operator|<
literal|1
condition|?
literal|0
else|:
literal|1
decl_stmt|;
name|checkPhraseQuery
argument_list|(
name|DOC_4
argument_list|,
name|QUERY_4
argument_list|,
name|slop
argument_list|,
name|numResultsExpected
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Test DOC_1 and QUERY_1.    * QUERY_1 has an exact match to DOC_1, so all slop values should succeed.    * Before LUCENE-1310, a slop value of 1 did not succeed.    */
DECL|method|testDoc1_Query1_All_Slops_Should_match
specifier|public
name|void
name|testDoc1_Query1_All_Slops_Should_match
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|slop
init|=
literal|0
init|;
name|slop
operator|<
literal|30
condition|;
name|slop
operator|++
control|)
block|{
name|float
name|score1
init|=
name|checkPhraseQuery
argument_list|(
name|DOC_1
argument_list|,
name|QUERY_1
argument_list|,
name|slop
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|float
name|score2
init|=
name|checkPhraseQuery
argument_list|(
name|DOC_1_B
argument_list|,
name|QUERY_1
argument_list|,
name|slop
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"slop="
operator|+
name|slop
operator|+
literal|" score2="
operator|+
name|score2
operator|+
literal|" should be greater than score1 "
operator|+
name|score1
argument_list|,
name|score2
operator|>
name|score1
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Test DOC_2 and QUERY_1.    * 6 should be the minimum slop to make QUERY_1 match DOC_2.    * Before LUCENE-1310, 7 was the minimum.    */
DECL|method|testDoc2_Query1_Slop_6_or_more_Should_match
specifier|public
name|void
name|testDoc2_Query1_Slop_6_or_more_Should_match
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|slop
init|=
literal|0
init|;
name|slop
operator|<
literal|30
condition|;
name|slop
operator|++
control|)
block|{
name|int
name|numResultsExpected
init|=
name|slop
operator|<
literal|6
condition|?
literal|0
else|:
literal|1
decl_stmt|;
name|float
name|score1
init|=
name|checkPhraseQuery
argument_list|(
name|DOC_2
argument_list|,
name|QUERY_1
argument_list|,
name|slop
argument_list|,
name|numResultsExpected
argument_list|)
decl_stmt|;
if|if
condition|(
name|numResultsExpected
operator|>
literal|0
condition|)
block|{
name|float
name|score2
init|=
name|checkPhraseQuery
argument_list|(
name|DOC_2_B
argument_list|,
name|QUERY_1
argument_list|,
name|slop
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"slop="
operator|+
name|slop
operator|+
literal|" score2="
operator|+
name|score2
operator|+
literal|" should be greater than score1 "
operator|+
name|score1
argument_list|,
name|score2
operator|>
name|score1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Test DOC_2 and QUERY_2.    * QUERY_2 has an exact match to DOC_2, so all slop values should succeed.    * Before LUCENE-1310, 0 succeeds, 1 through 7 fail, and 8 or greater succeeds.    */
DECL|method|testDoc2_Query2_All_Slops_Should_match
specifier|public
name|void
name|testDoc2_Query2_All_Slops_Should_match
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|slop
init|=
literal|0
init|;
name|slop
operator|<
literal|30
condition|;
name|slop
operator|++
control|)
block|{
name|float
name|score1
init|=
name|checkPhraseQuery
argument_list|(
name|DOC_2
argument_list|,
name|QUERY_2
argument_list|,
name|slop
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|float
name|score2
init|=
name|checkPhraseQuery
argument_list|(
name|DOC_2_B
argument_list|,
name|QUERY_2
argument_list|,
name|slop
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"slop="
operator|+
name|slop
operator|+
literal|" score2="
operator|+
name|score2
operator|+
literal|" should be greater than score1 "
operator|+
name|score1
argument_list|,
name|score2
operator|>
name|score1
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Test DOC_3 and QUERY_1.    * QUERY_1 has an exact match to DOC_3, so all slop values should succeed.    */
DECL|method|testDoc3_Query1_All_Slops_Should_match
specifier|public
name|void
name|testDoc3_Query1_All_Slops_Should_match
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|slop
init|=
literal|0
init|;
name|slop
operator|<
literal|30
condition|;
name|slop
operator|++
control|)
block|{
name|float
name|score1
init|=
name|checkPhraseQuery
argument_list|(
name|DOC_3
argument_list|,
name|QUERY_1
argument_list|,
name|slop
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|float
name|score2
init|=
name|checkPhraseQuery
argument_list|(
name|DOC_3_B
argument_list|,
name|QUERY_1
argument_list|,
name|slop
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"slop="
operator|+
name|slop
operator|+
literal|" score2="
operator|+
name|score2
operator|+
literal|" should be greater than score1 "
operator|+
name|score1
argument_list|,
name|score2
operator|>
name|score1
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|checkPhraseQuery
specifier|private
name|float
name|checkPhraseQuery
parameter_list|(
name|Document
name|doc
parameter_list|,
name|PhraseQuery
name|query
parameter_list|,
name|int
name|slop
parameter_list|,
name|int
name|expectedNumResults
parameter_list|)
throws|throws
name|Exception
block|{
name|query
operator|.
name|setSlop
argument_list|(
name|slop
argument_list|)
expr_stmt|;
name|RAMDirectory
name|ramDir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|WhitespaceAnalyzer
name|analyzer
init|=
operator|new
name|WhitespaceAnalyzer
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|ramDir
argument_list|,
name|analyzer
argument_list|,
name|MaxFieldLength
operator|.
name|UNLIMITED
argument_list|)
decl_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|writer
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
name|ramDir
argument_list|)
decl_stmt|;
name|TopDocs
name|td
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|null
argument_list|,
literal|10
argument_list|)
decl_stmt|;
comment|//System.out.println("slop: "+slop+"  query: "+query+"  doc: "+doc+"  Expecting number of hits: "+expectedNumResults+" maxScore="+td.getMaxScore());
name|assertEquals
argument_list|(
literal|"slop: "
operator|+
name|slop
operator|+
literal|"  query: "
operator|+
name|query
operator|+
literal|"  doc: "
operator|+
name|doc
operator|+
literal|"  Wrong number of hits"
argument_list|,
name|expectedNumResults
argument_list|,
name|td
operator|.
name|totalHits
argument_list|)
expr_stmt|;
comment|//QueryUtils.check(query,searcher);
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
name|ramDir
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|td
operator|.
name|getMaxScore
argument_list|()
return|;
block|}
DECL|method|makeDocument
specifier|private
specifier|static
name|Document
name|makeDocument
parameter_list|(
name|String
name|docText
parameter_list|)
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|Field
name|f
init|=
operator|new
name|Field
argument_list|(
literal|"f"
argument_list|,
name|docText
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
name|TOKENIZED
argument_list|)
decl_stmt|;
name|f
operator|.
name|setOmitNorms
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
return|return
name|doc
return|;
block|}
DECL|method|makePhraseQuery
specifier|private
specifier|static
name|PhraseQuery
name|makePhraseQuery
parameter_list|(
name|String
name|terms
parameter_list|)
block|{
name|PhraseQuery
name|query
init|=
operator|new
name|PhraseQuery
argument_list|()
decl_stmt|;
name|String
index|[]
name|t
init|=
name|terms
operator|.
name|split
argument_list|(
literal|" +"
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
name|t
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|query
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"f"
argument_list|,
name|t
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|query
return|;
block|}
block|}
end_class

end_unit

