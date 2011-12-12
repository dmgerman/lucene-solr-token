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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|TextField
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
name|search
operator|.
name|CheckHits
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
name|QueryUtils
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

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_class
DECL|class|TestFieldMaskingSpanQuery
specifier|public
class|class
name|TestFieldMaskingSpanQuery
extends|extends
name|LuceneTestCase
block|{
DECL|method|doc
specifier|protected
specifier|static
name|Document
name|doc
parameter_list|(
name|Field
index|[]
name|fields
parameter_list|)
block|{
name|Document
name|doc
init|=
operator|new
name|Document
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
name|fields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|doc
operator|.
name|add
argument_list|(
name|fields
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|doc
return|;
block|}
DECL|method|field
specifier|protected
specifier|static
name|Field
name|field
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
return|return
name|newField
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
name|TextField
operator|.
name|TYPE_UNSTORED
argument_list|)
return|;
block|}
DECL|field|searcher
specifier|protected
specifier|static
name|IndexSearcher
name|searcher
decl_stmt|;
DECL|field|directory
specifier|protected
specifier|static
name|Directory
name|directory
decl_stmt|;
DECL|field|reader
specifier|protected
specifier|static
name|IndexReader
name|reader
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|directory
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|,
name|directory
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|)
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|(
operator|new
name|Field
index|[]
block|{
name|field
argument_list|(
literal|"id"
argument_list|,
literal|"0"
argument_list|)
block|,
name|field
argument_list|(
literal|"gender"
argument_list|,
literal|"male"
argument_list|)
block|,
name|field
argument_list|(
literal|"first"
argument_list|,
literal|"james"
argument_list|)
block|,
name|field
argument_list|(
literal|"last"
argument_list|,
literal|"jones"
argument_list|)
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|(
operator|new
name|Field
index|[]
block|{
name|field
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|)
block|,
name|field
argument_list|(
literal|"gender"
argument_list|,
literal|"male"
argument_list|)
block|,
name|field
argument_list|(
literal|"first"
argument_list|,
literal|"james"
argument_list|)
block|,
name|field
argument_list|(
literal|"last"
argument_list|,
literal|"smith"
argument_list|)
block|,
name|field
argument_list|(
literal|"gender"
argument_list|,
literal|"female"
argument_list|)
block|,
name|field
argument_list|(
literal|"first"
argument_list|,
literal|"sally"
argument_list|)
block|,
name|field
argument_list|(
literal|"last"
argument_list|,
literal|"jones"
argument_list|)
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|(
operator|new
name|Field
index|[]
block|{
name|field
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|)
block|,
name|field
argument_list|(
literal|"gender"
argument_list|,
literal|"female"
argument_list|)
block|,
name|field
argument_list|(
literal|"first"
argument_list|,
literal|"greta"
argument_list|)
block|,
name|field
argument_list|(
literal|"last"
argument_list|,
literal|"jones"
argument_list|)
block|,
name|field
argument_list|(
literal|"gender"
argument_list|,
literal|"female"
argument_list|)
block|,
name|field
argument_list|(
literal|"first"
argument_list|,
literal|"sally"
argument_list|)
block|,
name|field
argument_list|(
literal|"last"
argument_list|,
literal|"smith"
argument_list|)
block|,
name|field
argument_list|(
literal|"gender"
argument_list|,
literal|"male"
argument_list|)
block|,
name|field
argument_list|(
literal|"first"
argument_list|,
literal|"james"
argument_list|)
block|,
name|field
argument_list|(
literal|"last"
argument_list|,
literal|"jones"
argument_list|)
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|(
operator|new
name|Field
index|[]
block|{
name|field
argument_list|(
literal|"id"
argument_list|,
literal|"3"
argument_list|)
block|,
name|field
argument_list|(
literal|"gender"
argument_list|,
literal|"female"
argument_list|)
block|,
name|field
argument_list|(
literal|"first"
argument_list|,
literal|"lisa"
argument_list|)
block|,
name|field
argument_list|(
literal|"last"
argument_list|,
literal|"jones"
argument_list|)
block|,
name|field
argument_list|(
literal|"gender"
argument_list|,
literal|"male"
argument_list|)
block|,
name|field
argument_list|(
literal|"first"
argument_list|,
literal|"bob"
argument_list|)
block|,
name|field
argument_list|(
literal|"last"
argument_list|,
literal|"costas"
argument_list|)
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|(
operator|new
name|Field
index|[]
block|{
name|field
argument_list|(
literal|"id"
argument_list|,
literal|"4"
argument_list|)
block|,
name|field
argument_list|(
literal|"gender"
argument_list|,
literal|"female"
argument_list|)
block|,
name|field
argument_list|(
literal|"first"
argument_list|,
literal|"sally"
argument_list|)
block|,
name|field
argument_list|(
literal|"last"
argument_list|,
literal|"smith"
argument_list|)
block|,
name|field
argument_list|(
literal|"gender"
argument_list|,
literal|"female"
argument_list|)
block|,
name|field
argument_list|(
literal|"first"
argument_list|,
literal|"linda"
argument_list|)
block|,
name|field
argument_list|(
literal|"last"
argument_list|,
literal|"dixit"
argument_list|)
block|,
name|field
argument_list|(
literal|"gender"
argument_list|,
literal|"male"
argument_list|)
block|,
name|field
argument_list|(
literal|"first"
argument_list|,
literal|"bubba"
argument_list|)
block|,
name|field
argument_list|(
literal|"last"
argument_list|,
literal|"jones"
argument_list|)
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|reader
operator|=
name|writer
operator|.
name|getReader
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|searcher
operator|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|afterClass
specifier|public
specifier|static
name|void
name|afterClass
parameter_list|()
throws|throws
name|Exception
block|{
name|searcher
operator|=
literal|null
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
operator|=
literal|null
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
name|directory
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|check
specifier|protected
name|void
name|check
parameter_list|(
name|SpanQuery
name|q
parameter_list|,
name|int
index|[]
name|docs
parameter_list|)
throws|throws
name|Exception
block|{
name|CheckHits
operator|.
name|checkHitCollector
argument_list|(
name|random
argument_list|,
name|q
argument_list|,
literal|null
argument_list|,
name|searcher
argument_list|,
name|docs
argument_list|)
expr_stmt|;
block|}
DECL|method|testRewrite0
specifier|public
name|void
name|testRewrite0
parameter_list|()
throws|throws
name|Exception
block|{
name|SpanQuery
name|q
init|=
operator|new
name|FieldMaskingSpanQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"last"
argument_list|,
literal|"sally"
argument_list|)
argument_list|)
argument_list|,
literal|"first"
argument_list|)
decl_stmt|;
name|q
operator|.
name|setBoost
argument_list|(
literal|8.7654321f
argument_list|)
expr_stmt|;
name|SpanQuery
name|qr
init|=
operator|(
name|SpanQuery
operator|)
name|searcher
operator|.
name|rewrite
argument_list|(
name|q
argument_list|)
decl_stmt|;
name|QueryUtils
operator|.
name|checkEqual
argument_list|(
name|q
argument_list|,
name|qr
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|Term
argument_list|>
name|terms
init|=
operator|new
name|HashSet
argument_list|<
name|Term
argument_list|>
argument_list|()
decl_stmt|;
name|qr
operator|.
name|extractTerms
argument_list|(
name|terms
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|terms
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testRewrite1
specifier|public
name|void
name|testRewrite1
parameter_list|()
throws|throws
name|Exception
block|{
comment|// mask an anon SpanQuery class that rewrites to something else.
name|SpanQuery
name|q
init|=
operator|new
name|FieldMaskingSpanQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"last"
argument_list|,
literal|"sally"
argument_list|)
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Query
name|rewrite
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
block|{
return|return
operator|new
name|SpanOrQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"first"
argument_list|,
literal|"sally"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"first"
argument_list|,
literal|"james"
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
block|}
argument_list|,
literal|"first"
argument_list|)
decl_stmt|;
name|SpanQuery
name|qr
init|=
operator|(
name|SpanQuery
operator|)
name|searcher
operator|.
name|rewrite
argument_list|(
name|q
argument_list|)
decl_stmt|;
name|QueryUtils
operator|.
name|checkUnequal
argument_list|(
name|q
argument_list|,
name|qr
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|Term
argument_list|>
name|terms
init|=
operator|new
name|HashSet
argument_list|<
name|Term
argument_list|>
argument_list|()
decl_stmt|;
name|qr
operator|.
name|extractTerms
argument_list|(
name|terms
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|terms
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testRewrite2
specifier|public
name|void
name|testRewrite2
parameter_list|()
throws|throws
name|Exception
block|{
name|SpanQuery
name|q1
init|=
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"last"
argument_list|,
literal|"smith"
argument_list|)
argument_list|)
decl_stmt|;
name|SpanQuery
name|q2
init|=
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"last"
argument_list|,
literal|"jones"
argument_list|)
argument_list|)
decl_stmt|;
name|SpanQuery
name|q
init|=
operator|new
name|SpanNearQuery
argument_list|(
operator|new
name|SpanQuery
index|[]
block|{
name|q1
block|,
operator|new
name|FieldMaskingSpanQuery
argument_list|(
name|q2
argument_list|,
literal|"last"
argument_list|)
block|}
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Query
name|qr
init|=
name|searcher
operator|.
name|rewrite
argument_list|(
name|q
argument_list|)
decl_stmt|;
name|QueryUtils
operator|.
name|checkEqual
argument_list|(
name|q
argument_list|,
name|qr
argument_list|)
expr_stmt|;
name|HashSet
argument_list|<
name|Term
argument_list|>
name|set
init|=
operator|new
name|HashSet
argument_list|<
name|Term
argument_list|>
argument_list|()
decl_stmt|;
name|qr
operator|.
name|extractTerms
argument_list|(
name|set
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|set
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testEquality1
specifier|public
name|void
name|testEquality1
parameter_list|()
block|{
name|SpanQuery
name|q1
init|=
operator|new
name|FieldMaskingSpanQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"last"
argument_list|,
literal|"sally"
argument_list|)
argument_list|)
argument_list|,
literal|"first"
argument_list|)
decl_stmt|;
name|SpanQuery
name|q2
init|=
operator|new
name|FieldMaskingSpanQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"last"
argument_list|,
literal|"sally"
argument_list|)
argument_list|)
argument_list|,
literal|"first"
argument_list|)
decl_stmt|;
name|SpanQuery
name|q3
init|=
operator|new
name|FieldMaskingSpanQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"last"
argument_list|,
literal|"sally"
argument_list|)
argument_list|)
argument_list|,
literal|"XXXXX"
argument_list|)
decl_stmt|;
name|SpanQuery
name|q4
init|=
operator|new
name|FieldMaskingSpanQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"last"
argument_list|,
literal|"XXXXX"
argument_list|)
argument_list|)
argument_list|,
literal|"first"
argument_list|)
decl_stmt|;
name|SpanQuery
name|q5
init|=
operator|new
name|FieldMaskingSpanQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"xXXX"
argument_list|,
literal|"sally"
argument_list|)
argument_list|)
argument_list|,
literal|"first"
argument_list|)
decl_stmt|;
name|QueryUtils
operator|.
name|checkEqual
argument_list|(
name|q1
argument_list|,
name|q2
argument_list|)
expr_stmt|;
name|QueryUtils
operator|.
name|checkUnequal
argument_list|(
name|q1
argument_list|,
name|q3
argument_list|)
expr_stmt|;
name|QueryUtils
operator|.
name|checkUnequal
argument_list|(
name|q1
argument_list|,
name|q4
argument_list|)
expr_stmt|;
name|QueryUtils
operator|.
name|checkUnequal
argument_list|(
name|q1
argument_list|,
name|q5
argument_list|)
expr_stmt|;
name|SpanQuery
name|qA
init|=
operator|new
name|FieldMaskingSpanQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"last"
argument_list|,
literal|"sally"
argument_list|)
argument_list|)
argument_list|,
literal|"first"
argument_list|)
decl_stmt|;
name|qA
operator|.
name|setBoost
argument_list|(
literal|9f
argument_list|)
expr_stmt|;
name|SpanQuery
name|qB
init|=
operator|new
name|FieldMaskingSpanQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"last"
argument_list|,
literal|"sally"
argument_list|)
argument_list|)
argument_list|,
literal|"first"
argument_list|)
decl_stmt|;
name|QueryUtils
operator|.
name|checkUnequal
argument_list|(
name|qA
argument_list|,
name|qB
argument_list|)
expr_stmt|;
name|qB
operator|.
name|setBoost
argument_list|(
literal|9f
argument_list|)
expr_stmt|;
name|QueryUtils
operator|.
name|checkEqual
argument_list|(
name|qA
argument_list|,
name|qB
argument_list|)
expr_stmt|;
block|}
DECL|method|testNoop0
specifier|public
name|void
name|testNoop0
parameter_list|()
throws|throws
name|Exception
block|{
name|SpanQuery
name|q1
init|=
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"last"
argument_list|,
literal|"sally"
argument_list|)
argument_list|)
decl_stmt|;
name|SpanQuery
name|q
init|=
operator|new
name|FieldMaskingSpanQuery
argument_list|(
name|q1
argument_list|,
literal|"first"
argument_list|)
decl_stmt|;
name|check
argument_list|(
name|q
argument_list|,
operator|new
name|int
index|[]
block|{
comment|/* :EMPTY: */
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testNoop1
specifier|public
name|void
name|testNoop1
parameter_list|()
throws|throws
name|Exception
block|{
name|SpanQuery
name|q1
init|=
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"last"
argument_list|,
literal|"smith"
argument_list|)
argument_list|)
decl_stmt|;
name|SpanQuery
name|q2
init|=
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"last"
argument_list|,
literal|"jones"
argument_list|)
argument_list|)
decl_stmt|;
name|SpanQuery
name|q
init|=
operator|new
name|SpanNearQuery
argument_list|(
operator|new
name|SpanQuery
index|[]
block|{
name|q1
block|,
operator|new
name|FieldMaskingSpanQuery
argument_list|(
name|q2
argument_list|,
literal|"last"
argument_list|)
block|}
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|check
argument_list|(
name|q
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|2
block|}
argument_list|)
expr_stmt|;
name|q
operator|=
operator|new
name|SpanNearQuery
argument_list|(
operator|new
name|SpanQuery
index|[]
block|{
operator|new
name|FieldMaskingSpanQuery
argument_list|(
name|q1
argument_list|,
literal|"last"
argument_list|)
block|,
operator|new
name|FieldMaskingSpanQuery
argument_list|(
name|q2
argument_list|,
literal|"last"
argument_list|)
block|}
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|check
argument_list|(
name|q
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|2
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testSimple1
specifier|public
name|void
name|testSimple1
parameter_list|()
throws|throws
name|Exception
block|{
name|SpanQuery
name|q1
init|=
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"first"
argument_list|,
literal|"james"
argument_list|)
argument_list|)
decl_stmt|;
name|SpanQuery
name|q2
init|=
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"last"
argument_list|,
literal|"jones"
argument_list|)
argument_list|)
decl_stmt|;
name|SpanQuery
name|q
init|=
operator|new
name|SpanNearQuery
argument_list|(
operator|new
name|SpanQuery
index|[]
block|{
name|q1
block|,
operator|new
name|FieldMaskingSpanQuery
argument_list|(
name|q2
argument_list|,
literal|"first"
argument_list|)
block|}
argument_list|,
operator|-
literal|1
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|check
argument_list|(
name|q
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|2
block|}
argument_list|)
expr_stmt|;
name|q
operator|=
operator|new
name|SpanNearQuery
argument_list|(
operator|new
name|SpanQuery
index|[]
block|{
operator|new
name|FieldMaskingSpanQuery
argument_list|(
name|q2
argument_list|,
literal|"first"
argument_list|)
block|,
name|q1
block|}
argument_list|,
operator|-
literal|1
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|check
argument_list|(
name|q
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|2
block|}
argument_list|)
expr_stmt|;
name|q
operator|=
operator|new
name|SpanNearQuery
argument_list|(
operator|new
name|SpanQuery
index|[]
block|{
name|q2
block|,
operator|new
name|FieldMaskingSpanQuery
argument_list|(
name|q1
argument_list|,
literal|"last"
argument_list|)
block|}
argument_list|,
operator|-
literal|1
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|check
argument_list|(
name|q
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|2
block|}
argument_list|)
expr_stmt|;
name|q
operator|=
operator|new
name|SpanNearQuery
argument_list|(
operator|new
name|SpanQuery
index|[]
block|{
operator|new
name|FieldMaskingSpanQuery
argument_list|(
name|q1
argument_list|,
literal|"last"
argument_list|)
block|,
name|q2
block|}
argument_list|,
operator|-
literal|1
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|check
argument_list|(
name|q
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|2
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testSimple2
specifier|public
name|void
name|testSimple2
parameter_list|()
throws|throws
name|Exception
block|{
name|SpanQuery
name|q1
init|=
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"gender"
argument_list|,
literal|"female"
argument_list|)
argument_list|)
decl_stmt|;
name|SpanQuery
name|q2
init|=
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"last"
argument_list|,
literal|"smith"
argument_list|)
argument_list|)
decl_stmt|;
name|SpanQuery
name|q
init|=
operator|new
name|SpanNearQuery
argument_list|(
operator|new
name|SpanQuery
index|[]
block|{
name|q1
block|,
operator|new
name|FieldMaskingSpanQuery
argument_list|(
name|q2
argument_list|,
literal|"gender"
argument_list|)
block|}
argument_list|,
operator|-
literal|1
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|check
argument_list|(
name|q
argument_list|,
operator|new
name|int
index|[]
block|{
literal|2
block|,
literal|4
block|}
argument_list|)
expr_stmt|;
name|q
operator|=
operator|new
name|SpanNearQuery
argument_list|(
operator|new
name|SpanQuery
index|[]
block|{
operator|new
name|FieldMaskingSpanQuery
argument_list|(
name|q1
argument_list|,
literal|"id"
argument_list|)
block|,
operator|new
name|FieldMaskingSpanQuery
argument_list|(
name|q2
argument_list|,
literal|"id"
argument_list|)
block|}
argument_list|,
operator|-
literal|1
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|check
argument_list|(
name|q
argument_list|,
operator|new
name|int
index|[]
block|{
literal|2
block|,
literal|4
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testSpans0
specifier|public
name|void
name|testSpans0
parameter_list|()
throws|throws
name|Exception
block|{
name|SpanQuery
name|q1
init|=
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"gender"
argument_list|,
literal|"female"
argument_list|)
argument_list|)
decl_stmt|;
name|SpanQuery
name|q2
init|=
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"first"
argument_list|,
literal|"james"
argument_list|)
argument_list|)
decl_stmt|;
name|SpanQuery
name|q
init|=
operator|new
name|SpanOrQuery
argument_list|(
name|q1
argument_list|,
operator|new
name|FieldMaskingSpanQuery
argument_list|(
name|q2
argument_list|,
literal|"gender"
argument_list|)
argument_list|)
decl_stmt|;
name|check
argument_list|(
name|q
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|,
literal|4
block|}
argument_list|)
expr_stmt|;
name|Spans
name|span
init|=
name|MultiSpansWrapper
operator|.
name|wrap
argument_list|(
name|searcher
operator|.
name|getTopReaderContext
argument_list|()
argument_list|,
name|q
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|span
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|s
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|,
name|s
argument_list|(
name|span
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|span
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|s
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|,
name|s
argument_list|(
name|span
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|span
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|s
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|)
argument_list|,
name|s
argument_list|(
name|span
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|span
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|s
argument_list|(
literal|2
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|,
name|s
argument_list|(
name|span
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|span
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|s
argument_list|(
literal|2
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|)
argument_list|,
name|s
argument_list|(
name|span
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|span
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|s
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|,
literal|3
argument_list|)
argument_list|,
name|s
argument_list|(
name|span
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|span
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|s
argument_list|(
literal|3
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|,
name|s
argument_list|(
name|span
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|span
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|s
argument_list|(
literal|4
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|,
name|s
argument_list|(
name|span
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|span
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|s
argument_list|(
literal|4
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|)
argument_list|,
name|s
argument_list|(
name|span
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|span
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testSpans1
specifier|public
name|void
name|testSpans1
parameter_list|()
throws|throws
name|Exception
block|{
name|SpanQuery
name|q1
init|=
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"first"
argument_list|,
literal|"sally"
argument_list|)
argument_list|)
decl_stmt|;
name|SpanQuery
name|q2
init|=
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"first"
argument_list|,
literal|"james"
argument_list|)
argument_list|)
decl_stmt|;
name|SpanQuery
name|qA
init|=
operator|new
name|SpanOrQuery
argument_list|(
name|q1
argument_list|,
name|q2
argument_list|)
decl_stmt|;
name|SpanQuery
name|qB
init|=
operator|new
name|FieldMaskingSpanQuery
argument_list|(
name|qA
argument_list|,
literal|"id"
argument_list|)
decl_stmt|;
name|check
argument_list|(
name|qA
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|4
block|}
argument_list|)
expr_stmt|;
name|check
argument_list|(
name|qB
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|4
block|}
argument_list|)
expr_stmt|;
name|Spans
name|spanA
init|=
name|MultiSpansWrapper
operator|.
name|wrap
argument_list|(
name|searcher
operator|.
name|getTopReaderContext
argument_list|()
argument_list|,
name|qA
argument_list|)
decl_stmt|;
name|Spans
name|spanB
init|=
name|MultiSpansWrapper
operator|.
name|wrap
argument_list|(
name|searcher
operator|.
name|getTopReaderContext
argument_list|()
argument_list|,
name|qB
argument_list|)
decl_stmt|;
while|while
condition|(
name|spanA
operator|.
name|next
argument_list|()
condition|)
block|{
name|assertTrue
argument_list|(
literal|"spanB not still going"
argument_list|,
name|spanB
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"spanA not equal spanB"
argument_list|,
name|s
argument_list|(
name|spanA
argument_list|)
argument_list|,
name|s
argument_list|(
name|spanB
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"spanB still going even tough spanA is done"
argument_list|,
operator|!
operator|(
name|spanB
operator|.
name|next
argument_list|()
operator|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testSpans2
specifier|public
name|void
name|testSpans2
parameter_list|()
throws|throws
name|Exception
block|{
name|SpanQuery
name|qA1
init|=
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"gender"
argument_list|,
literal|"female"
argument_list|)
argument_list|)
decl_stmt|;
name|SpanQuery
name|qA2
init|=
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"first"
argument_list|,
literal|"james"
argument_list|)
argument_list|)
decl_stmt|;
name|SpanQuery
name|qA
init|=
operator|new
name|SpanOrQuery
argument_list|(
name|qA1
argument_list|,
operator|new
name|FieldMaskingSpanQuery
argument_list|(
name|qA2
argument_list|,
literal|"gender"
argument_list|)
argument_list|)
decl_stmt|;
name|SpanQuery
name|qB
init|=
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"last"
argument_list|,
literal|"jones"
argument_list|)
argument_list|)
decl_stmt|;
name|SpanQuery
name|q
init|=
operator|new
name|SpanNearQuery
argument_list|(
operator|new
name|SpanQuery
index|[]
block|{
operator|new
name|FieldMaskingSpanQuery
argument_list|(
name|qA
argument_list|,
literal|"id"
argument_list|)
block|,
operator|new
name|FieldMaskingSpanQuery
argument_list|(
name|qB
argument_list|,
literal|"id"
argument_list|)
block|}
argument_list|,
operator|-
literal|1
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|check
argument_list|(
name|q
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
name|Spans
name|span
init|=
name|MultiSpansWrapper
operator|.
name|wrap
argument_list|(
name|searcher
operator|.
name|getTopReaderContext
argument_list|()
argument_list|,
name|q
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|span
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|s
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|,
name|s
argument_list|(
name|span
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|span
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|s
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|)
argument_list|,
name|s
argument_list|(
name|span
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|span
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|s
argument_list|(
literal|2
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|,
name|s
argument_list|(
name|span
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|span
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|s
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|,
literal|3
argument_list|)
argument_list|,
name|s
argument_list|(
name|span
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|span
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|s
argument_list|(
literal|3
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|,
name|s
argument_list|(
name|span
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|span
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|s
specifier|public
name|String
name|s
parameter_list|(
name|Spans
name|span
parameter_list|)
block|{
return|return
name|s
argument_list|(
name|span
operator|.
name|doc
argument_list|()
argument_list|,
name|span
operator|.
name|start
argument_list|()
argument_list|,
name|span
operator|.
name|end
argument_list|()
argument_list|)
return|;
block|}
DECL|method|s
specifier|public
name|String
name|s
parameter_list|(
name|int
name|doc
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
block|{
return|return
literal|"s("
operator|+
name|doc
operator|+
literal|","
operator|+
name|start
operator|+
literal|","
operator|+
name|end
operator|+
literal|")"
return|;
block|}
block|}
end_class

end_unit

