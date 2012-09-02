begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
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
name|store
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
name|document
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
name|analysis
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
name|index
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
name|*
import|;
end_import

begin_comment
comment|/** JUnit adaptation of an older test case SearchTest. */
end_comment

begin_class
DECL|class|TestSearch
specifier|public
class|class
name|TestSearch
extends|extends
name|LuceneTestCase
block|{
comment|/** This test performs a number of searches. It also compares output      *  of searches using multi-file index segments with single-file      *  index segments.      *      *  TODO: someone should check that the results of the searches are      *        still correct by adding assert statements. Right now, the test      *        passes if the results are the same between multi-file and      *        single-file formats, even if the results are wrong.      */
DECL|method|testSearch
specifier|public
name|void
name|testSearch
parameter_list|()
throws|throws
name|Exception
block|{
name|StringWriter
name|sw
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|PrintWriter
name|pw
init|=
operator|new
name|PrintWriter
argument_list|(
name|sw
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|doTestSearch
argument_list|(
name|random
argument_list|()
argument_list|,
name|pw
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|pw
operator|.
name|close
argument_list|()
expr_stmt|;
name|sw
operator|.
name|close
argument_list|()
expr_stmt|;
name|String
name|multiFileOutput
init|=
name|sw
operator|.
name|getBuffer
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
comment|//System.out.println(multiFileOutput);
name|sw
operator|=
operator|new
name|StringWriter
argument_list|()
expr_stmt|;
name|pw
operator|=
operator|new
name|PrintWriter
argument_list|(
name|sw
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|doTestSearch
argument_list|(
name|random
argument_list|()
argument_list|,
name|pw
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|pw
operator|.
name|close
argument_list|()
expr_stmt|;
name|sw
operator|.
name|close
argument_list|()
expr_stmt|;
name|String
name|singleFileOutput
init|=
name|sw
operator|.
name|getBuffer
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|multiFileOutput
argument_list|,
name|singleFileOutput
argument_list|)
expr_stmt|;
block|}
DECL|method|doTestSearch
specifier|private
name|void
name|doTestSearch
parameter_list|(
name|Random
name|random
parameter_list|,
name|PrintWriter
name|out
parameter_list|,
name|boolean
name|useCompoundFile
parameter_list|)
throws|throws
name|Exception
block|{
name|Directory
name|directory
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|Analyzer
name|analyzer
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|)
decl_stmt|;
name|IndexWriterConfig
name|conf
init|=
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|analyzer
argument_list|)
decl_stmt|;
name|MergePolicy
name|mp
init|=
name|conf
operator|.
name|getMergePolicy
argument_list|()
decl_stmt|;
if|if
condition|(
name|mp
operator|instanceof
name|LogMergePolicy
condition|)
block|{
operator|(
operator|(
name|LogMergePolicy
operator|)
name|mp
operator|)
operator|.
name|setUseCompoundFile
argument_list|(
name|useCompoundFile
argument_list|)
expr_stmt|;
block|}
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|String
index|[]
name|docs
init|=
block|{
literal|"a b c d e"
block|,
literal|"a b c d e a b c d e"
block|,
literal|"a b c d e f g h i j"
block|,
literal|"a c e"
block|,
literal|"e c a"
block|,
literal|"a c e a c e"
block|,
literal|"a c e a b c"
block|}
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|docs
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|Document
name|d
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|d
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"contents"
argument_list|,
name|docs
index|[
name|j
index|]
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
name|newStringField
argument_list|(
literal|"id"
argument_list|,
literal|""
operator|+
name|j
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|directory
argument_list|)
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|ScoreDoc
index|[]
name|hits
init|=
literal|null
decl_stmt|;
name|Sort
name|sort
init|=
operator|new
name|Sort
argument_list|(
name|SortField
operator|.
name|FIELD_SCORE
argument_list|,
operator|new
name|SortField
argument_list|(
literal|"id"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|INT
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|Query
name|query
range|:
name|buildQueries
argument_list|()
control|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"Query: "
operator|+
name|query
operator|.
name|toString
argument_list|(
literal|"contents"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: query="
operator|+
name|query
argument_list|)
expr_stmt|;
block|}
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
argument_list|,
name|sort
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
name|hits
operator|.
name|length
operator|+
literal|" total results"
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
operator|&&
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|StoredDocument
name|d
init|=
name|searcher
operator|.
name|doc
argument_list|(
name|hits
index|[
name|i
index|]
operator|.
name|doc
argument_list|)
decl_stmt|;
name|out
operator|.
name|println
argument_list|(
name|i
operator|+
literal|" "
operator|+
name|hits
index|[
name|i
index|]
operator|.
name|score
operator|+
literal|" "
operator|+
name|d
operator|.
name|get
argument_list|(
literal|"contents"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|buildQueries
specifier|private
name|List
argument_list|<
name|Query
argument_list|>
name|buildQueries
parameter_list|()
block|{
name|List
argument_list|<
name|Query
argument_list|>
name|queries
init|=
operator|new
name|ArrayList
argument_list|<
name|Query
argument_list|>
argument_list|()
decl_stmt|;
name|BooleanQuery
name|booleanAB
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|booleanAB
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"contents"
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
name|booleanAB
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"contents"
argument_list|,
literal|"b"
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
name|queries
operator|.
name|add
argument_list|(
name|booleanAB
argument_list|)
expr_stmt|;
name|PhraseQuery
name|phraseAB
init|=
operator|new
name|PhraseQuery
argument_list|()
decl_stmt|;
name|phraseAB
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"contents"
argument_list|,
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|phraseAB
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"contents"
argument_list|,
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
name|queries
operator|.
name|add
argument_list|(
name|phraseAB
argument_list|)
expr_stmt|;
name|PhraseQuery
name|phraseABC
init|=
operator|new
name|PhraseQuery
argument_list|()
decl_stmt|;
name|phraseABC
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"contents"
argument_list|,
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|phraseABC
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"contents"
argument_list|,
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
name|phraseABC
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"contents"
argument_list|,
literal|"c"
argument_list|)
argument_list|)
expr_stmt|;
name|queries
operator|.
name|add
argument_list|(
name|phraseABC
argument_list|)
expr_stmt|;
name|BooleanQuery
name|booleanAC
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|booleanAC
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"contents"
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
name|booleanAC
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"contents"
argument_list|,
literal|"c"
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
name|queries
operator|.
name|add
argument_list|(
name|booleanAC
argument_list|)
expr_stmt|;
name|PhraseQuery
name|phraseAC
init|=
operator|new
name|PhraseQuery
argument_list|()
decl_stmt|;
name|phraseAC
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"contents"
argument_list|,
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|phraseAC
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"contents"
argument_list|,
literal|"c"
argument_list|)
argument_list|)
expr_stmt|;
name|queries
operator|.
name|add
argument_list|(
name|phraseAC
argument_list|)
expr_stmt|;
name|PhraseQuery
name|phraseACE
init|=
operator|new
name|PhraseQuery
argument_list|()
decl_stmt|;
name|phraseACE
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"contents"
argument_list|,
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|phraseACE
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"contents"
argument_list|,
literal|"c"
argument_list|)
argument_list|)
expr_stmt|;
name|phraseACE
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"contents"
argument_list|,
literal|"e"
argument_list|)
argument_list|)
expr_stmt|;
name|queries
operator|.
name|add
argument_list|(
name|phraseACE
argument_list|)
expr_stmt|;
return|return
name|queries
return|;
block|}
block|}
end_class

end_unit

