begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.vectorhighlight
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|vectorhighlight
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|io
operator|.
name|Reader
import|;
end_import

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
name|Analyzer
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
name|Token
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
name|TokenStream
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
name|tokenattributes
operator|.
name|TermAttribute
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
name|util
operator|.
name|AttributeImpl
import|;
end_import

begin_class
DECL|class|IndexTimeSynonymTest
specifier|public
class|class
name|IndexTimeSynonymTest
extends|extends
name|AbstractTestCase
block|{
DECL|method|testFieldTermStackIndex1wSearch1term
specifier|public
name|void
name|testFieldTermStackIndex1wSearch1term
parameter_list|()
throws|throws
name|Exception
block|{
name|makeIndex1w
argument_list|()
expr_stmt|;
name|FieldQuery
name|fq
init|=
operator|new
name|FieldQuery
argument_list|(
name|tq
argument_list|(
literal|"Mac"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|FieldTermStack
name|stack
init|=
operator|new
name|FieldTermStack
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|F
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|stack
operator|.
name|termList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Mac(11,20,3)"
argument_list|,
name|stack
operator|.
name|pop
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testFieldTermStackIndex1wSearch2terms
specifier|public
name|void
name|testFieldTermStackIndex1wSearch2terms
parameter_list|()
throws|throws
name|Exception
block|{
name|makeIndex1w
argument_list|()
expr_stmt|;
name|BooleanQuery
name|bq
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|bq
operator|.
name|add
argument_list|(
name|tq
argument_list|(
literal|"Mac"
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|bq
operator|.
name|add
argument_list|(
name|tq
argument_list|(
literal|"MacBook"
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|FieldQuery
name|fq
init|=
operator|new
name|FieldQuery
argument_list|(
name|bq
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|FieldTermStack
name|stack
init|=
operator|new
name|FieldTermStack
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|F
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|stack
operator|.
name|termList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|expectedSet
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|expectedSet
operator|.
name|add
argument_list|(
literal|"Mac(11,20,3)"
argument_list|)
expr_stmt|;
name|expectedSet
operator|.
name|add
argument_list|(
literal|"MacBook(11,20,3)"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expectedSet
operator|.
name|contains
argument_list|(
name|stack
operator|.
name|pop
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expectedSet
operator|.
name|contains
argument_list|(
name|stack
operator|.
name|pop
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testFieldTermStackIndex1w2wSearch1term
specifier|public
name|void
name|testFieldTermStackIndex1w2wSearch1term
parameter_list|()
throws|throws
name|Exception
block|{
name|makeIndex1w2w
argument_list|()
expr_stmt|;
name|FieldQuery
name|fq
init|=
operator|new
name|FieldQuery
argument_list|(
name|tq
argument_list|(
literal|"pc"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|FieldTermStack
name|stack
init|=
operator|new
name|FieldTermStack
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|F
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|stack
operator|.
name|termList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"pc(3,5,1)"
argument_list|,
name|stack
operator|.
name|pop
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testFieldTermStackIndex1w2wSearch1phrase
specifier|public
name|void
name|testFieldTermStackIndex1w2wSearch1phrase
parameter_list|()
throws|throws
name|Exception
block|{
name|makeIndex1w2w
argument_list|()
expr_stmt|;
name|FieldQuery
name|fq
init|=
operator|new
name|FieldQuery
argument_list|(
name|pqF
argument_list|(
literal|"personal"
argument_list|,
literal|"computer"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|FieldTermStack
name|stack
init|=
operator|new
name|FieldTermStack
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|F
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|stack
operator|.
name|termList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"personal(3,5,1)"
argument_list|,
name|stack
operator|.
name|pop
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"computer(3,5,2)"
argument_list|,
name|stack
operator|.
name|pop
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testFieldTermStackIndex1w2wSearch1partial
specifier|public
name|void
name|testFieldTermStackIndex1w2wSearch1partial
parameter_list|()
throws|throws
name|Exception
block|{
name|makeIndex1w2w
argument_list|()
expr_stmt|;
name|FieldQuery
name|fq
init|=
operator|new
name|FieldQuery
argument_list|(
name|tq
argument_list|(
literal|"computer"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|FieldTermStack
name|stack
init|=
operator|new
name|FieldTermStack
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|F
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|stack
operator|.
name|termList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"computer(3,5,2)"
argument_list|,
name|stack
operator|.
name|pop
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testFieldTermStackIndex1w2wSearch1term1phrase
specifier|public
name|void
name|testFieldTermStackIndex1w2wSearch1term1phrase
parameter_list|()
throws|throws
name|Exception
block|{
name|makeIndex1w2w
argument_list|()
expr_stmt|;
name|BooleanQuery
name|bq
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|bq
operator|.
name|add
argument_list|(
name|tq
argument_list|(
literal|"pc"
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|bq
operator|.
name|add
argument_list|(
name|pqF
argument_list|(
literal|"personal"
argument_list|,
literal|"computer"
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|FieldQuery
name|fq
init|=
operator|new
name|FieldQuery
argument_list|(
name|bq
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|FieldTermStack
name|stack
init|=
operator|new
name|FieldTermStack
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|F
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|stack
operator|.
name|termList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|expectedSet
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|expectedSet
operator|.
name|add
argument_list|(
literal|"pc(3,5,1)"
argument_list|)
expr_stmt|;
name|expectedSet
operator|.
name|add
argument_list|(
literal|"personal(3,5,1)"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expectedSet
operator|.
name|contains
argument_list|(
name|stack
operator|.
name|pop
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expectedSet
operator|.
name|contains
argument_list|(
name|stack
operator|.
name|pop
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"computer(3,5,2)"
argument_list|,
name|stack
operator|.
name|pop
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testFieldTermStackIndex2w1wSearch1term
specifier|public
name|void
name|testFieldTermStackIndex2w1wSearch1term
parameter_list|()
throws|throws
name|Exception
block|{
name|makeIndex2w1w
argument_list|()
expr_stmt|;
name|FieldQuery
name|fq
init|=
operator|new
name|FieldQuery
argument_list|(
name|tq
argument_list|(
literal|"pc"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|FieldTermStack
name|stack
init|=
operator|new
name|FieldTermStack
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|F
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|stack
operator|.
name|termList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"pc(3,20,1)"
argument_list|,
name|stack
operator|.
name|pop
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testFieldTermStackIndex2w1wSearch1phrase
specifier|public
name|void
name|testFieldTermStackIndex2w1wSearch1phrase
parameter_list|()
throws|throws
name|Exception
block|{
name|makeIndex2w1w
argument_list|()
expr_stmt|;
name|FieldQuery
name|fq
init|=
operator|new
name|FieldQuery
argument_list|(
name|pqF
argument_list|(
literal|"personal"
argument_list|,
literal|"computer"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|FieldTermStack
name|stack
init|=
operator|new
name|FieldTermStack
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|F
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|stack
operator|.
name|termList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"personal(3,20,1)"
argument_list|,
name|stack
operator|.
name|pop
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"computer(3,20,2)"
argument_list|,
name|stack
operator|.
name|pop
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testFieldTermStackIndex2w1wSearch1partial
specifier|public
name|void
name|testFieldTermStackIndex2w1wSearch1partial
parameter_list|()
throws|throws
name|Exception
block|{
name|makeIndex2w1w
argument_list|()
expr_stmt|;
name|FieldQuery
name|fq
init|=
operator|new
name|FieldQuery
argument_list|(
name|tq
argument_list|(
literal|"computer"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|FieldTermStack
name|stack
init|=
operator|new
name|FieldTermStack
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|F
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|stack
operator|.
name|termList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"computer(3,20,2)"
argument_list|,
name|stack
operator|.
name|pop
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testFieldTermStackIndex2w1wSearch1term1phrase
specifier|public
name|void
name|testFieldTermStackIndex2w1wSearch1term1phrase
parameter_list|()
throws|throws
name|Exception
block|{
name|makeIndex2w1w
argument_list|()
expr_stmt|;
name|BooleanQuery
name|bq
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|bq
operator|.
name|add
argument_list|(
name|tq
argument_list|(
literal|"pc"
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|bq
operator|.
name|add
argument_list|(
name|pqF
argument_list|(
literal|"personal"
argument_list|,
literal|"computer"
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|FieldQuery
name|fq
init|=
operator|new
name|FieldQuery
argument_list|(
name|bq
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|FieldTermStack
name|stack
init|=
operator|new
name|FieldTermStack
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|F
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|stack
operator|.
name|termList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|expectedSet
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|expectedSet
operator|.
name|add
argument_list|(
literal|"pc(3,20,1)"
argument_list|)
expr_stmt|;
name|expectedSet
operator|.
name|add
argument_list|(
literal|"personal(3,20,1)"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expectedSet
operator|.
name|contains
argument_list|(
name|stack
operator|.
name|pop
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expectedSet
operator|.
name|contains
argument_list|(
name|stack
operator|.
name|pop
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"computer(3,20,2)"
argument_list|,
name|stack
operator|.
name|pop
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testFieldPhraseListIndex1w2wSearch1phrase
specifier|public
name|void
name|testFieldPhraseListIndex1w2wSearch1phrase
parameter_list|()
throws|throws
name|Exception
block|{
name|makeIndex1w2w
argument_list|()
expr_stmt|;
name|FieldQuery
name|fq
init|=
operator|new
name|FieldQuery
argument_list|(
name|pqF
argument_list|(
literal|"personal"
argument_list|,
literal|"computer"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|FieldTermStack
name|stack
init|=
operator|new
name|FieldTermStack
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|F
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|FieldPhraseList
name|fpl
init|=
operator|new
name|FieldPhraseList
argument_list|(
name|stack
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|fpl
operator|.
name|phraseList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"personalcomputer(1.0)((3,5))"
argument_list|,
name|fpl
operator|.
name|phraseList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|fpl
operator|.
name|phraseList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getStartOffset
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|fpl
operator|.
name|phraseList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getEndOffset
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testFieldPhraseListIndex1w2wSearch1partial
specifier|public
name|void
name|testFieldPhraseListIndex1w2wSearch1partial
parameter_list|()
throws|throws
name|Exception
block|{
name|makeIndex1w2w
argument_list|()
expr_stmt|;
name|FieldQuery
name|fq
init|=
operator|new
name|FieldQuery
argument_list|(
name|tq
argument_list|(
literal|"computer"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|FieldTermStack
name|stack
init|=
operator|new
name|FieldTermStack
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|F
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|FieldPhraseList
name|fpl
init|=
operator|new
name|FieldPhraseList
argument_list|(
name|stack
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|fpl
operator|.
name|phraseList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"computer(1.0)((3,5))"
argument_list|,
name|fpl
operator|.
name|phraseList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|fpl
operator|.
name|phraseList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getStartOffset
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|fpl
operator|.
name|phraseList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getEndOffset
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testFieldPhraseListIndex1w2wSearch1term1phrase
specifier|public
name|void
name|testFieldPhraseListIndex1w2wSearch1term1phrase
parameter_list|()
throws|throws
name|Exception
block|{
name|makeIndex1w2w
argument_list|()
expr_stmt|;
name|BooleanQuery
name|bq
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|bq
operator|.
name|add
argument_list|(
name|tq
argument_list|(
literal|"pc"
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|bq
operator|.
name|add
argument_list|(
name|pqF
argument_list|(
literal|"personal"
argument_list|,
literal|"computer"
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|FieldQuery
name|fq
init|=
operator|new
name|FieldQuery
argument_list|(
name|bq
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|FieldTermStack
name|stack
init|=
operator|new
name|FieldTermStack
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|F
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|FieldPhraseList
name|fpl
init|=
operator|new
name|FieldPhraseList
argument_list|(
name|stack
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|fpl
operator|.
name|phraseList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fpl
operator|.
name|phraseList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
operator|.
name|indexOf
argument_list|(
literal|"(1.0)((3,5))"
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|fpl
operator|.
name|phraseList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getStartOffset
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|fpl
operator|.
name|phraseList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getEndOffset
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testFieldPhraseListIndex2w1wSearch1term
specifier|public
name|void
name|testFieldPhraseListIndex2w1wSearch1term
parameter_list|()
throws|throws
name|Exception
block|{
name|makeIndex2w1w
argument_list|()
expr_stmt|;
name|FieldQuery
name|fq
init|=
operator|new
name|FieldQuery
argument_list|(
name|tq
argument_list|(
literal|"pc"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|FieldTermStack
name|stack
init|=
operator|new
name|FieldTermStack
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|F
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|FieldPhraseList
name|fpl
init|=
operator|new
name|FieldPhraseList
argument_list|(
name|stack
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|fpl
operator|.
name|phraseList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"pc(1.0)((3,20))"
argument_list|,
name|fpl
operator|.
name|phraseList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|fpl
operator|.
name|phraseList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getStartOffset
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|20
argument_list|,
name|fpl
operator|.
name|phraseList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getEndOffset
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testFieldPhraseListIndex2w1wSearch1phrase
specifier|public
name|void
name|testFieldPhraseListIndex2w1wSearch1phrase
parameter_list|()
throws|throws
name|Exception
block|{
name|makeIndex2w1w
argument_list|()
expr_stmt|;
name|FieldQuery
name|fq
init|=
operator|new
name|FieldQuery
argument_list|(
name|pqF
argument_list|(
literal|"personal"
argument_list|,
literal|"computer"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|FieldTermStack
name|stack
init|=
operator|new
name|FieldTermStack
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|F
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|FieldPhraseList
name|fpl
init|=
operator|new
name|FieldPhraseList
argument_list|(
name|stack
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|fpl
operator|.
name|phraseList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"personalcomputer(1.0)((3,20))"
argument_list|,
name|fpl
operator|.
name|phraseList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|fpl
operator|.
name|phraseList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getStartOffset
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|20
argument_list|,
name|fpl
operator|.
name|phraseList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getEndOffset
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testFieldPhraseListIndex2w1wSearch1partial
specifier|public
name|void
name|testFieldPhraseListIndex2w1wSearch1partial
parameter_list|()
throws|throws
name|Exception
block|{
name|makeIndex2w1w
argument_list|()
expr_stmt|;
name|FieldQuery
name|fq
init|=
operator|new
name|FieldQuery
argument_list|(
name|tq
argument_list|(
literal|"computer"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|FieldTermStack
name|stack
init|=
operator|new
name|FieldTermStack
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|F
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|FieldPhraseList
name|fpl
init|=
operator|new
name|FieldPhraseList
argument_list|(
name|stack
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|fpl
operator|.
name|phraseList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"computer(1.0)((3,20))"
argument_list|,
name|fpl
operator|.
name|phraseList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|fpl
operator|.
name|phraseList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getStartOffset
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|20
argument_list|,
name|fpl
operator|.
name|phraseList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getEndOffset
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testFieldPhraseListIndex2w1wSearch1term1phrase
specifier|public
name|void
name|testFieldPhraseListIndex2w1wSearch1term1phrase
parameter_list|()
throws|throws
name|Exception
block|{
name|makeIndex2w1w
argument_list|()
expr_stmt|;
name|BooleanQuery
name|bq
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|bq
operator|.
name|add
argument_list|(
name|tq
argument_list|(
literal|"pc"
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|bq
operator|.
name|add
argument_list|(
name|pqF
argument_list|(
literal|"personal"
argument_list|,
literal|"computer"
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|FieldQuery
name|fq
init|=
operator|new
name|FieldQuery
argument_list|(
name|bq
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|FieldTermStack
name|stack
init|=
operator|new
name|FieldTermStack
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|F
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|FieldPhraseList
name|fpl
init|=
operator|new
name|FieldPhraseList
argument_list|(
name|stack
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|fpl
operator|.
name|phraseList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fpl
operator|.
name|phraseList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
operator|.
name|indexOf
argument_list|(
literal|"(1.0)((3,20))"
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|fpl
operator|.
name|phraseList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getStartOffset
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|20
argument_list|,
name|fpl
operator|.
name|phraseList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getEndOffset
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|makeIndex1w
specifier|private
name|void
name|makeIndex1w
parameter_list|()
throws|throws
name|Exception
block|{
comment|//           11111111112
comment|// 012345678901234567890
comment|// I'll buy a Macintosh
comment|//            Mac
comment|//            MacBook
comment|// 0    1   2 3
name|makeSynonymIndex
argument_list|(
literal|"I'll buy a Macintosh"
argument_list|,
name|t
argument_list|(
literal|"I'll"
argument_list|,
literal|0
argument_list|,
literal|4
argument_list|)
argument_list|,
name|t
argument_list|(
literal|"buy"
argument_list|,
literal|5
argument_list|,
literal|8
argument_list|)
argument_list|,
name|t
argument_list|(
literal|"a"
argument_list|,
literal|9
argument_list|,
literal|10
argument_list|)
argument_list|,
name|t
argument_list|(
literal|"Macintosh"
argument_list|,
literal|11
argument_list|,
literal|20
argument_list|)
argument_list|,
name|t
argument_list|(
literal|"Mac"
argument_list|,
literal|11
argument_list|,
literal|20
argument_list|,
literal|0
argument_list|)
argument_list|,
name|t
argument_list|(
literal|"MacBook"
argument_list|,
literal|11
argument_list|,
literal|20
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|makeIndex1w2w
specifier|private
name|void
name|makeIndex1w2w
parameter_list|()
throws|throws
name|Exception
block|{
comment|//           1111111
comment|// 01234567890123456
comment|// My pc was broken
comment|//    personal computer
comment|// 0  1  2   3
name|makeSynonymIndex
argument_list|(
literal|"My pc was broken"
argument_list|,
name|t
argument_list|(
literal|"My"
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|)
argument_list|,
name|t
argument_list|(
literal|"pc"
argument_list|,
literal|3
argument_list|,
literal|5
argument_list|)
argument_list|,
name|t
argument_list|(
literal|"personal"
argument_list|,
literal|3
argument_list|,
literal|5
argument_list|,
literal|0
argument_list|)
argument_list|,
name|t
argument_list|(
literal|"computer"
argument_list|,
literal|3
argument_list|,
literal|5
argument_list|)
argument_list|,
name|t
argument_list|(
literal|"was"
argument_list|,
literal|6
argument_list|,
literal|9
argument_list|)
argument_list|,
name|t
argument_list|(
literal|"broken"
argument_list|,
literal|10
argument_list|,
literal|16
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|makeIndex2w1w
specifier|private
name|void
name|makeIndex2w1w
parameter_list|()
throws|throws
name|Exception
block|{
comment|//           1111111111222222222233
comment|// 01234567890123456789012345678901
comment|// My personal computer was broken
comment|//    pc
comment|// 0  1        2        3   4
name|makeSynonymIndex
argument_list|(
literal|"My personal computer was broken"
argument_list|,
name|t
argument_list|(
literal|"My"
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|)
argument_list|,
name|t
argument_list|(
literal|"personal"
argument_list|,
literal|3
argument_list|,
literal|20
argument_list|)
argument_list|,
name|t
argument_list|(
literal|"pc"
argument_list|,
literal|3
argument_list|,
literal|20
argument_list|,
literal|0
argument_list|)
argument_list|,
name|t
argument_list|(
literal|"computer"
argument_list|,
literal|3
argument_list|,
literal|20
argument_list|)
argument_list|,
name|t
argument_list|(
literal|"was"
argument_list|,
literal|21
argument_list|,
literal|24
argument_list|)
argument_list|,
name|t
argument_list|(
literal|"broken"
argument_list|,
literal|25
argument_list|,
literal|31
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|makeSynonymIndex
name|void
name|makeSynonymIndex
parameter_list|(
name|String
name|value
parameter_list|,
name|Token
modifier|...
name|tokens
parameter_list|)
throws|throws
name|Exception
block|{
name|Analyzer
name|analyzer
init|=
operator|new
name|TokenArrayAnalyzer
argument_list|(
name|tokens
argument_list|)
decl_stmt|;
name|make1dmfIndex
argument_list|(
name|analyzer
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
DECL|method|t
specifier|public
specifier|static
name|Token
name|t
parameter_list|(
name|String
name|text
parameter_list|,
name|int
name|startOffset
parameter_list|,
name|int
name|endOffset
parameter_list|)
block|{
return|return
name|t
argument_list|(
name|text
argument_list|,
name|startOffset
argument_list|,
name|endOffset
argument_list|,
literal|1
argument_list|)
return|;
block|}
DECL|method|t
specifier|public
specifier|static
name|Token
name|t
parameter_list|(
name|String
name|text
parameter_list|,
name|int
name|startOffset
parameter_list|,
name|int
name|endOffset
parameter_list|,
name|int
name|positionIncrement
parameter_list|)
block|{
name|Token
name|token
init|=
operator|new
name|Token
argument_list|(
name|text
argument_list|,
name|startOffset
argument_list|,
name|endOffset
argument_list|)
decl_stmt|;
name|token
operator|.
name|setPositionIncrement
argument_list|(
name|positionIncrement
argument_list|)
expr_stmt|;
return|return
name|token
return|;
block|}
DECL|class|TokenArrayAnalyzer
specifier|public
specifier|static
class|class
name|TokenArrayAnalyzer
extends|extends
name|Analyzer
block|{
DECL|field|tokens
name|Token
index|[]
name|tokens
decl_stmt|;
DECL|method|TokenArrayAnalyzer
specifier|public
name|TokenArrayAnalyzer
parameter_list|(
name|Token
modifier|...
name|tokens
parameter_list|)
block|{
name|this
operator|.
name|tokens
operator|=
name|tokens
expr_stmt|;
block|}
DECL|method|tokenStream
specifier|public
name|TokenStream
name|tokenStream
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
name|TokenStream
name|ts
init|=
operator|new
name|TokenStream
argument_list|(
name|Token
operator|.
name|TOKEN_ATTRIBUTE_FACTORY
argument_list|)
block|{
specifier|final
name|AttributeImpl
name|reusableToken
init|=
operator|(
name|AttributeImpl
operator|)
name|addAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|int
name|p
init|=
literal|0
decl_stmt|;
specifier|public
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|p
operator|>=
name|tokens
operator|.
name|length
condition|)
return|return
literal|false
return|;
name|clearAttributes
argument_list|()
expr_stmt|;
name|tokens
index|[
name|p
operator|++
index|]
operator|.
name|copyTo
argument_list|(
name|reusableToken
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
decl_stmt|;
return|return
name|ts
return|;
block|}
block|}
block|}
end_class

end_unit

