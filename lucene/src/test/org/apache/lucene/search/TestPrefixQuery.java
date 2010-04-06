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

begin_comment
comment|/**  * Tests {@link PrefixQuery} class.  *  */
end_comment

begin_class
DECL|class|TestPrefixQuery
specifier|public
class|class
name|TestPrefixQuery
extends|extends
name|LuceneTestCase
block|{
DECL|method|testPrefixQuery
specifier|public
name|void
name|testPrefixQuery
parameter_list|()
throws|throws
name|Exception
block|{
name|RAMDirectory
name|directory
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|String
index|[]
name|categories
init|=
operator|new
name|String
index|[]
block|{
literal|"/Computers"
block|,
literal|"/Computers/Mac"
block|,
literal|"/Computers/Windows"
block|}
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
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
name|categories
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
literal|"category"
argument_list|,
name|categories
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
name|NOT_ANALYZED
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
name|close
argument_list|()
expr_stmt|;
name|PrefixQuery
name|query
init|=
operator|new
name|PrefixQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"category"
argument_list|,
literal|"/Computers"
argument_list|)
argument_list|)
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|directory
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|ScoreDoc
index|[]
name|hits
init|=
name|searcher
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
literal|"All documents in /Computers category and below"
argument_list|,
literal|3
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
name|query
operator|=
operator|new
name|PrefixQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"category"
argument_list|,
literal|"/Computers/Mac"
argument_list|)
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
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
expr_stmt|;
name|assertEquals
argument_list|(
literal|"One in /Computers/Mac"
argument_list|,
literal|1
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
name|query
operator|=
operator|new
name|PrefixQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"category"
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|query
operator|.
name|getTermsEnum
argument_list|(
name|searcher
operator|.
name|getIndexReader
argument_list|()
argument_list|)
operator|instanceof
name|PrefixTermsEnum
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
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
expr_stmt|;
name|assertEquals
argument_list|(
literal|"everything"
argument_list|,
literal|3
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Deprecated
DECL|method|testBackwardsLayer
specifier|public
name|void
name|testBackwardsLayer
parameter_list|()
block|{
name|assertTrue
argument_list|(
operator|new
name|PrefixQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"dummy"
argument_list|,
literal|"dummy"
argument_list|)
argument_list|)
operator|.
name|hasNewAPI
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

