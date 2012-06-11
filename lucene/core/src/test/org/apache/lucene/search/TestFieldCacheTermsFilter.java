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
name|store
operator|.
name|Directory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * A basic unit test for FieldCacheTermsFilter  *  * @see org.apache.lucene.search.FieldCacheTermsFilter  */
end_comment

begin_class
DECL|class|TestFieldCacheTermsFilter
specifier|public
class|class
name|TestFieldCacheTermsFilter
extends|extends
name|LuceneTestCase
block|{
DECL|method|testMissingTerms
specifier|public
name|void
name|testMissingTerms
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|fieldName
init|=
literal|"field1"
decl_stmt|;
name|Directory
name|rd
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
argument_list|()
argument_list|,
name|rd
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
literal|100
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
name|int
name|term
init|=
name|i
operator|*
literal|10
decl_stmt|;
comment|//terms are units of 10;
name|doc
operator|.
name|add
argument_list|(
name|newStringField
argument_list|(
name|fieldName
argument_list|,
literal|""
operator|+
name|term
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
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
block|}
name|IndexReader
name|reader
init|=
name|w
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|w
operator|.
name|close
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
name|int
name|numDocs
init|=
name|reader
operator|.
name|numDocs
argument_list|()
decl_stmt|;
name|ScoreDoc
index|[]
name|results
decl_stmt|;
name|MatchAllDocsQuery
name|q
init|=
operator|new
name|MatchAllDocsQuery
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|terms
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|terms
operator|.
name|add
argument_list|(
literal|"5"
argument_list|)
expr_stmt|;
name|results
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|,
operator|new
name|FieldCacheTermsFilter
argument_list|(
name|fieldName
argument_list|,
name|terms
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
argument_list|)
argument_list|,
name|numDocs
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Must match nothing"
argument_list|,
literal|0
argument_list|,
name|results
operator|.
name|length
argument_list|)
expr_stmt|;
name|terms
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|terms
operator|.
name|add
argument_list|(
literal|"10"
argument_list|)
expr_stmt|;
name|results
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|,
operator|new
name|FieldCacheTermsFilter
argument_list|(
name|fieldName
argument_list|,
name|terms
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
argument_list|)
argument_list|,
name|numDocs
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Must match 1"
argument_list|,
literal|1
argument_list|,
name|results
operator|.
name|length
argument_list|)
expr_stmt|;
name|terms
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|terms
operator|.
name|add
argument_list|(
literal|"10"
argument_list|)
expr_stmt|;
name|terms
operator|.
name|add
argument_list|(
literal|"20"
argument_list|)
expr_stmt|;
name|results
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|,
operator|new
name|FieldCacheTermsFilter
argument_list|(
name|fieldName
argument_list|,
name|terms
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
argument_list|)
argument_list|,
name|numDocs
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Must match 2"
argument_list|,
literal|2
argument_list|,
name|results
operator|.
name|length
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|rd
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

