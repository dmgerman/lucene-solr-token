begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|StringField
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

begin_class
DECL|class|TestSynonymQuery
specifier|public
class|class
name|TestSynonymQuery
extends|extends
name|LuceneTestCase
block|{
DECL|method|testEquals
specifier|public
name|void
name|testEquals
parameter_list|()
block|{
name|QueryUtils
operator|.
name|checkEqual
argument_list|(
operator|new
name|SynonymQuery
argument_list|()
argument_list|,
operator|new
name|SynonymQuery
argument_list|()
argument_list|)
expr_stmt|;
name|QueryUtils
operator|.
name|checkEqual
argument_list|(
operator|new
name|SynonymQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|SynonymQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|QueryUtils
operator|.
name|checkEqual
argument_list|(
operator|new
name|SynonymQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"a"
argument_list|,
literal|"a"
argument_list|)
argument_list|,
operator|new
name|Term
argument_list|(
literal|"a"
argument_list|,
literal|"b"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|SynonymQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"a"
argument_list|,
literal|"b"
argument_list|)
argument_list|,
operator|new
name|Term
argument_list|(
literal|"a"
argument_list|,
literal|"a"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testBogusParams
specifier|public
name|void
name|testBogusParams
parameter_list|()
block|{
try|try
block|{
operator|new
name|SynonymQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field1"
argument_list|,
literal|"a"
argument_list|)
argument_list|,
operator|new
name|Term
argument_list|(
literal|"field2"
argument_list|,
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|expected
parameter_list|)
block|{}
block|}
DECL|method|testToString
specifier|public
name|void
name|testToString
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"Synonym()"
argument_list|,
operator|new
name|SynonymQuery
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Term
name|t1
init|=
operator|new
name|Term
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Synonym(foo:bar)"
argument_list|,
operator|new
name|SynonymQuery
argument_list|(
name|t1
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Term
name|t2
init|=
operator|new
name|Term
argument_list|(
literal|"foo"
argument_list|,
literal|"baz"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Synonym(foo:bar foo:baz)"
argument_list|,
operator|new
name|SynonymQuery
argument_list|(
name|t1
argument_list|,
name|t2
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testScores
specifier|public
name|void
name|testScores
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
name|RandomIndexWriter
name|w
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
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
operator|new
name|StringField
argument_list|(
literal|"f"
argument_list|,
literal|"a"
argument_list|,
name|Store
operator|.
name|NO
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
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"f"
argument_list|,
literal|"b"
argument_list|,
name|Store
operator|.
name|NO
argument_list|)
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
literal|10
condition|;
operator|++
name|i
control|)
block|{
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
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|SynonymQuery
name|query
init|=
operator|new
name|SynonymQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"f"
argument_list|,
literal|"a"
argument_list|)
argument_list|,
operator|new
name|Term
argument_list|(
literal|"f"
argument_list|,
literal|"b"
argument_list|)
argument_list|)
decl_stmt|;
name|TopDocs
name|topDocs
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|20
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|11
argument_list|,
name|topDocs
operator|.
name|totalHits
argument_list|)
expr_stmt|;
comment|// All docs must have the same score
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|topDocs
operator|.
name|scoreDocs
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|assertEquals
argument_list|(
name|topDocs
operator|.
name|scoreDocs
index|[
literal|0
index|]
operator|.
name|score
argument_list|,
name|topDocs
operator|.
name|scoreDocs
index|[
name|i
index|]
operator|.
name|score
argument_list|,
literal|0.0f
argument_list|)
expr_stmt|;
block|}
name|reader
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
block|}
end_class

end_unit

