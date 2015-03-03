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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|analysis
operator|.
name|MockTokenizer
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
name|SortedSetDocValuesField
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
name|BytesRef
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
name|TestUtil
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
name|automaton
operator|.
name|AutomatonTestUtil
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
name|automaton
operator|.
name|RegExp
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
name|UnicodeUtil
import|;
end_import

begin_comment
comment|/**  * Tests the DocValuesRewriteMethod  */
end_comment

begin_class
DECL|class|TestDocValuesRewriteMethod
specifier|public
class|class
name|TestDocValuesRewriteMethod
extends|extends
name|LuceneTestCase
block|{
DECL|field|searcher1
specifier|protected
name|IndexSearcher
name|searcher1
decl_stmt|;
DECL|field|searcher2
specifier|protected
name|IndexSearcher
name|searcher2
decl_stmt|;
DECL|field|reader
specifier|private
name|IndexReader
name|reader
decl_stmt|;
DECL|field|dir
specifier|private
name|Directory
name|dir
decl_stmt|;
DECL|field|fieldName
specifier|protected
name|String
name|fieldName
decl_stmt|;
annotation|@
name|Override
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|dir
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|fieldName
operator|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
literal|"field"
else|:
literal|""
expr_stmt|;
comment|// sometimes use an empty string as field name
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|,
name|MockTokenizer
operator|.
name|KEYWORD
argument_list|,
literal|false
argument_list|)
argument_list|)
operator|.
name|setMaxBufferedDocs
argument_list|(
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|50
argument_list|,
literal|1000
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|terms
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|num
init|=
name|atLeast
argument_list|(
literal|200
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
name|num
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
name|newStringField
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|numTerms
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|4
argument_list|)
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
name|numTerms
condition|;
name|j
operator|++
control|)
block|{
name|String
name|s
init|=
name|TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newStringField
argument_list|(
name|fieldName
argument_list|,
name|s
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|SortedSetDocValuesField
argument_list|(
name|fieldName
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|s
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|terms
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|VERBOSE
condition|)
block|{
comment|// utf16 order
name|Collections
operator|.
name|sort
argument_list|(
name|terms
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"UTF16 order:"
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|s
range|:
name|terms
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  "
operator|+
name|UnicodeUtil
operator|.
name|toHexString
argument_list|(
name|s
argument_list|)
operator|+
literal|" "
operator|+
name|s
argument_list|)
expr_stmt|;
block|}
block|}
name|int
name|numDeletions
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|num
operator|/
literal|10
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
name|numDeletions
condition|;
name|i
operator|++
control|)
block|{
name|writer
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|num
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|reader
operator|=
name|writer
operator|.
name|getReader
argument_list|()
expr_stmt|;
name|searcher1
operator|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|searcher2
operator|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
comment|/** test a bunch of random regular expressions */
DECL|method|testRegexps
specifier|public
name|void
name|testRegexps
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|num
init|=
name|atLeast
argument_list|(
literal|1000
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
name|num
condition|;
name|i
operator|++
control|)
block|{
name|String
name|reg
init|=
name|AutomatonTestUtil
operator|.
name|randomRegexp
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
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
literal|"TEST: regexp="
operator|+
name|reg
argument_list|)
expr_stmt|;
block|}
name|assertSame
argument_list|(
name|reg
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** check that the # of hits is the same as if the query    * is run against the inverted index    */
DECL|method|assertSame
specifier|protected
name|void
name|assertSame
parameter_list|(
name|String
name|regexp
parameter_list|)
throws|throws
name|IOException
block|{
name|RegexpQuery
name|docValues
init|=
operator|new
name|RegexpQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|fieldName
argument_list|,
name|regexp
argument_list|)
argument_list|,
name|RegExp
operator|.
name|NONE
argument_list|)
decl_stmt|;
name|docValues
operator|.
name|setRewriteMethod
argument_list|(
operator|new
name|DocValuesRewriteMethod
argument_list|()
argument_list|)
expr_stmt|;
name|RegexpQuery
name|inverted
init|=
operator|new
name|RegexpQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|fieldName
argument_list|,
name|regexp
argument_list|)
argument_list|,
name|RegExp
operator|.
name|NONE
argument_list|)
decl_stmt|;
name|TopDocs
name|invertedDocs
init|=
name|searcher1
operator|.
name|search
argument_list|(
name|inverted
argument_list|,
literal|25
argument_list|)
decl_stmt|;
name|TopDocs
name|docValuesDocs
init|=
name|searcher2
operator|.
name|search
argument_list|(
name|docValues
argument_list|,
literal|25
argument_list|)
decl_stmt|;
name|CheckHits
operator|.
name|checkEqual
argument_list|(
name|inverted
argument_list|,
name|invertedDocs
operator|.
name|scoreDocs
argument_list|,
name|docValuesDocs
operator|.
name|scoreDocs
argument_list|)
expr_stmt|;
block|}
DECL|method|testEquals
specifier|public
name|void
name|testEquals
parameter_list|()
throws|throws
name|Exception
block|{
name|RegexpQuery
name|a1
init|=
operator|new
name|RegexpQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|fieldName
argument_list|,
literal|"[aA]"
argument_list|)
argument_list|,
name|RegExp
operator|.
name|NONE
argument_list|)
decl_stmt|;
name|RegexpQuery
name|a2
init|=
operator|new
name|RegexpQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|fieldName
argument_list|,
literal|"[aA]"
argument_list|)
argument_list|,
name|RegExp
operator|.
name|NONE
argument_list|)
decl_stmt|;
name|RegexpQuery
name|b
init|=
operator|new
name|RegexpQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|fieldName
argument_list|,
literal|"[bB]"
argument_list|)
argument_list|,
name|RegExp
operator|.
name|NONE
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|a1
argument_list|,
name|a2
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|a1
operator|.
name|equals
argument_list|(
name|b
argument_list|)
argument_list|)
expr_stmt|;
name|a1
operator|.
name|setRewriteMethod
argument_list|(
operator|new
name|DocValuesRewriteMethod
argument_list|()
argument_list|)
expr_stmt|;
name|a2
operator|.
name|setRewriteMethod
argument_list|(
operator|new
name|DocValuesRewriteMethod
argument_list|()
argument_list|)
expr_stmt|;
name|b
operator|.
name|setRewriteMethod
argument_list|(
operator|new
name|DocValuesRewriteMethod
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|a1
argument_list|,
name|a2
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|a1
operator|.
name|equals
argument_list|(
name|b
argument_list|)
argument_list|)
expr_stmt|;
name|QueryUtils
operator|.
name|check
argument_list|(
name|a1
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
