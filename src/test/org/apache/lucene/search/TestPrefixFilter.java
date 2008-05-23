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
comment|/**  * Tests {@link PrefixFilter} class.  *  * @author Yura Smolsky  * @author yonik  */
end_comment

begin_class
DECL|class|TestPrefixFilter
specifier|public
class|class
name|TestPrefixFilter
extends|extends
name|LuceneTestCase
block|{
DECL|method|testPrefixFilter
specifier|public
name|void
name|testPrefixFilter
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
literal|"/Computers/Linux"
block|,
literal|"/Computers/Mac/One"
block|,
literal|"/Computers/Mac/Two"
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
name|WhitespaceAnalyzer
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
name|UN_TOKENIZED
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
comment|// PrefixFilter combined with ConstantScoreQuery
name|PrefixFilter
name|filter
init|=
operator|new
name|PrefixFilter
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
name|Query
name|query
init|=
operator|new
name|ConstantScoreQuery
argument_list|(
name|filter
argument_list|)
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|directory
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
literal|4
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// test middle of values
name|filter
operator|=
operator|new
name|PrefixFilter
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
name|query
operator|=
operator|new
name|ConstantScoreQuery
argument_list|(
name|filter
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
literal|2
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// test start of values
name|filter
operator|=
operator|new
name|PrefixFilter
argument_list|(
operator|new
name|Term
argument_list|(
literal|"category"
argument_list|,
literal|"/Computers/Linux"
argument_list|)
argument_list|)
expr_stmt|;
name|query
operator|=
operator|new
name|ConstantScoreQuery
argument_list|(
name|filter
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
literal|1
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// test end of values
name|filter
operator|=
operator|new
name|PrefixFilter
argument_list|(
operator|new
name|Term
argument_list|(
literal|"category"
argument_list|,
literal|"/Computers/Windows"
argument_list|)
argument_list|)
expr_stmt|;
name|query
operator|=
operator|new
name|ConstantScoreQuery
argument_list|(
name|filter
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
literal|1
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// test non-existant
name|filter
operator|=
operator|new
name|PrefixFilter
argument_list|(
operator|new
name|Term
argument_list|(
literal|"category"
argument_list|,
literal|"/Computers/ObsoleteOS"
argument_list|)
argument_list|)
expr_stmt|;
name|query
operator|=
operator|new
name|ConstantScoreQuery
argument_list|(
name|filter
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
literal|0
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// test non-existant, before values
name|filter
operator|=
operator|new
name|PrefixFilter
argument_list|(
operator|new
name|Term
argument_list|(
literal|"category"
argument_list|,
literal|"/Computers/AAA"
argument_list|)
argument_list|)
expr_stmt|;
name|query
operator|=
operator|new
name|ConstantScoreQuery
argument_list|(
name|filter
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
literal|0
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// test non-existant, after values
name|filter
operator|=
operator|new
name|PrefixFilter
argument_list|(
operator|new
name|Term
argument_list|(
literal|"category"
argument_list|,
literal|"/Computers/ZZZ"
argument_list|)
argument_list|)
expr_stmt|;
name|query
operator|=
operator|new
name|ConstantScoreQuery
argument_list|(
name|filter
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
literal|0
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// test zero length prefix
name|filter
operator|=
operator|new
name|PrefixFilter
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
name|query
operator|=
operator|new
name|ConstantScoreQuery
argument_list|(
name|filter
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
literal|4
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// test non existent field
name|filter
operator|=
operator|new
name|PrefixFilter
argument_list|(
operator|new
name|Term
argument_list|(
literal|"nonexistantfield"
argument_list|,
literal|"/Computers"
argument_list|)
argument_list|)
expr_stmt|;
name|query
operator|=
operator|new
name|ConstantScoreQuery
argument_list|(
name|filter
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
literal|0
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

