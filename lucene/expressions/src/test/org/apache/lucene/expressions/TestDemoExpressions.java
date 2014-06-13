begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.expressions
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|expressions
package|;
end_package

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
name|NumericDocValuesField
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
name|expressions
operator|.
name|js
operator|.
name|JavascriptCompiler
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
name|DirectoryReader
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
name|FieldDoc
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
name|MatchAllDocsQuery
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
name|Sort
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
name|SortField
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
name|TermQuery
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
name|TopFieldDocs
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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/** simple demo of using expressions */
end_comment

begin_class
DECL|class|TestDemoExpressions
specifier|public
class|class
name|TestDemoExpressions
extends|extends
name|LuceneTestCase
block|{
DECL|field|searcher
name|IndexSearcher
name|searcher
decl_stmt|;
DECL|field|reader
name|DirectoryReader
name|reader
decl_stmt|;
DECL|field|dir
name|Directory
name|dir
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
name|RandomIndexWriter
name|iw
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
name|newStringField
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"body"
argument_list|,
literal|"some contents and more contents"
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
name|NumericDocValuesField
argument_list|(
literal|"popularity"
argument_list|,
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|NumericDocValuesField
argument_list|(
literal|"latitude"
argument_list|,
name|Double
operator|.
name|doubleToRawLongBits
argument_list|(
literal|40.759011
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|NumericDocValuesField
argument_list|(
literal|"longitude"
argument_list|,
name|Double
operator|.
name|doubleToRawLongBits
argument_list|(
operator|-
literal|73.9844722
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|iw
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
name|newStringField
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"body"
argument_list|,
literal|"another document with different contents"
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
name|NumericDocValuesField
argument_list|(
literal|"popularity"
argument_list|,
literal|20
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|NumericDocValuesField
argument_list|(
literal|"latitude"
argument_list|,
name|Double
operator|.
name|doubleToRawLongBits
argument_list|(
literal|40.718266
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|NumericDocValuesField
argument_list|(
literal|"longitude"
argument_list|,
name|Double
operator|.
name|doubleToRawLongBits
argument_list|(
operator|-
literal|74.007819
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|iw
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
name|newStringField
argument_list|(
literal|"id"
argument_list|,
literal|"3"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"body"
argument_list|,
literal|"crappy contents"
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
name|NumericDocValuesField
argument_list|(
literal|"popularity"
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|NumericDocValuesField
argument_list|(
literal|"latitude"
argument_list|,
name|Double
operator|.
name|doubleToRawLongBits
argument_list|(
literal|40.7051157
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|NumericDocValuesField
argument_list|(
literal|"longitude"
argument_list|,
name|Double
operator|.
name|doubleToRawLongBits
argument_list|(
operator|-
literal|74.0088305
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|reader
operator|=
name|iw
operator|.
name|getReader
argument_list|()
expr_stmt|;
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|iw
operator|.
name|shutdown
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
comment|/** an example of how to rank by an expression */
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
comment|// compile an expression:
name|Expression
name|expr
init|=
name|JavascriptCompiler
operator|.
name|compile
argument_list|(
literal|"sqrt(_score) + ln(popularity)"
argument_list|)
decl_stmt|;
comment|// we use SimpleBindings: which just maps variables to SortField instances
name|SimpleBindings
name|bindings
init|=
operator|new
name|SimpleBindings
argument_list|()
decl_stmt|;
name|bindings
operator|.
name|add
argument_list|(
operator|new
name|SortField
argument_list|(
literal|"_score"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|SCORE
argument_list|)
argument_list|)
expr_stmt|;
name|bindings
operator|.
name|add
argument_list|(
operator|new
name|SortField
argument_list|(
literal|"popularity"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|INT
argument_list|)
argument_list|)
expr_stmt|;
comment|// create a sort field and sort by it (reverse order)
name|Sort
name|sort
init|=
operator|new
name|Sort
argument_list|(
name|expr
operator|.
name|getSortField
argument_list|(
name|bindings
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|Query
name|query
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"contents"
argument_list|)
argument_list|)
decl_stmt|;
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|null
argument_list|,
literal|3
argument_list|,
name|sort
argument_list|)
expr_stmt|;
block|}
comment|/** tests the returned sort values are correct */
DECL|method|testSortValues
specifier|public
name|void
name|testSortValues
parameter_list|()
throws|throws
name|Exception
block|{
name|Expression
name|expr
init|=
name|JavascriptCompiler
operator|.
name|compile
argument_list|(
literal|"sqrt(_score)"
argument_list|)
decl_stmt|;
name|SimpleBindings
name|bindings
init|=
operator|new
name|SimpleBindings
argument_list|()
decl_stmt|;
name|bindings
operator|.
name|add
argument_list|(
operator|new
name|SortField
argument_list|(
literal|"_score"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|SCORE
argument_list|)
argument_list|)
expr_stmt|;
name|Sort
name|sort
init|=
operator|new
name|Sort
argument_list|(
name|expr
operator|.
name|getSortField
argument_list|(
name|bindings
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|Query
name|query
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"contents"
argument_list|)
argument_list|)
decl_stmt|;
name|TopFieldDocs
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
literal|3
argument_list|,
name|sort
argument_list|,
literal|true
argument_list|,
literal|true
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
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|FieldDoc
name|d
init|=
operator|(
name|FieldDoc
operator|)
name|td
operator|.
name|scoreDocs
index|[
name|i
index|]
decl_stmt|;
name|float
name|expected
init|=
operator|(
name|float
operator|)
name|Math
operator|.
name|sqrt
argument_list|(
name|d
operator|.
name|score
argument_list|)
decl_stmt|;
name|float
name|actual
init|=
operator|(
operator|(
name|Double
operator|)
name|d
operator|.
name|fields
index|[
literal|0
index|]
operator|)
operator|.
name|floatValue
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|,
name|CheckHits
operator|.
name|explainToleranceDelta
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** tests same binding used more than once in an expression */
DECL|method|testTwoOfSameBinding
specifier|public
name|void
name|testTwoOfSameBinding
parameter_list|()
throws|throws
name|Exception
block|{
name|Expression
name|expr
init|=
name|JavascriptCompiler
operator|.
name|compile
argument_list|(
literal|"_score + _score"
argument_list|)
decl_stmt|;
name|SimpleBindings
name|bindings
init|=
operator|new
name|SimpleBindings
argument_list|()
decl_stmt|;
name|bindings
operator|.
name|add
argument_list|(
operator|new
name|SortField
argument_list|(
literal|"_score"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|SCORE
argument_list|)
argument_list|)
expr_stmt|;
name|Sort
name|sort
init|=
operator|new
name|Sort
argument_list|(
name|expr
operator|.
name|getSortField
argument_list|(
name|bindings
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|Query
name|query
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"contents"
argument_list|)
argument_list|)
decl_stmt|;
name|TopFieldDocs
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
literal|3
argument_list|,
name|sort
argument_list|,
literal|true
argument_list|,
literal|true
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
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|FieldDoc
name|d
init|=
operator|(
name|FieldDoc
operator|)
name|td
operator|.
name|scoreDocs
index|[
name|i
index|]
decl_stmt|;
name|float
name|expected
init|=
literal|2
operator|*
name|d
operator|.
name|score
decl_stmt|;
name|float
name|actual
init|=
operator|(
operator|(
name|Double
operator|)
name|d
operator|.
name|fields
index|[
literal|0
index|]
operator|)
operator|.
name|floatValue
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|,
name|CheckHits
operator|.
name|explainToleranceDelta
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Uses variables with $ */
DECL|method|testDollarVariable
specifier|public
name|void
name|testDollarVariable
parameter_list|()
throws|throws
name|Exception
block|{
name|Expression
name|expr
init|=
name|JavascriptCompiler
operator|.
name|compile
argument_list|(
literal|"$0+$score"
argument_list|)
decl_stmt|;
name|SimpleBindings
name|bindings
init|=
operator|new
name|SimpleBindings
argument_list|()
decl_stmt|;
name|bindings
operator|.
name|add
argument_list|(
operator|new
name|SortField
argument_list|(
literal|"$0"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|SCORE
argument_list|)
argument_list|)
expr_stmt|;
name|bindings
operator|.
name|add
argument_list|(
operator|new
name|SortField
argument_list|(
literal|"$score"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|SCORE
argument_list|)
argument_list|)
expr_stmt|;
name|Sort
name|sort
init|=
operator|new
name|Sort
argument_list|(
name|expr
operator|.
name|getSortField
argument_list|(
name|bindings
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|Query
name|query
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"contents"
argument_list|)
argument_list|)
decl_stmt|;
name|TopFieldDocs
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
literal|3
argument_list|,
name|sort
argument_list|,
literal|true
argument_list|,
literal|true
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
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|FieldDoc
name|d
init|=
operator|(
name|FieldDoc
operator|)
name|td
operator|.
name|scoreDocs
index|[
name|i
index|]
decl_stmt|;
name|float
name|expected
init|=
literal|2
operator|*
name|d
operator|.
name|score
decl_stmt|;
name|float
name|actual
init|=
operator|(
operator|(
name|Double
operator|)
name|d
operator|.
name|fields
index|[
literal|0
index|]
operator|)
operator|.
name|floatValue
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|,
name|CheckHits
operator|.
name|explainToleranceDelta
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** tests expression referring to another expression */
DECL|method|testExpressionRefersToExpression
specifier|public
name|void
name|testExpressionRefersToExpression
parameter_list|()
throws|throws
name|Exception
block|{
name|Expression
name|expr1
init|=
name|JavascriptCompiler
operator|.
name|compile
argument_list|(
literal|"_score"
argument_list|)
decl_stmt|;
name|Expression
name|expr2
init|=
name|JavascriptCompiler
operator|.
name|compile
argument_list|(
literal|"2*expr1"
argument_list|)
decl_stmt|;
name|SimpleBindings
name|bindings
init|=
operator|new
name|SimpleBindings
argument_list|()
decl_stmt|;
name|bindings
operator|.
name|add
argument_list|(
operator|new
name|SortField
argument_list|(
literal|"_score"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|SCORE
argument_list|)
argument_list|)
expr_stmt|;
name|bindings
operator|.
name|add
argument_list|(
literal|"expr1"
argument_list|,
name|expr1
argument_list|)
expr_stmt|;
name|Sort
name|sort
init|=
operator|new
name|Sort
argument_list|(
name|expr2
operator|.
name|getSortField
argument_list|(
name|bindings
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|Query
name|query
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"contents"
argument_list|)
argument_list|)
decl_stmt|;
name|TopFieldDocs
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
literal|3
argument_list|,
name|sort
argument_list|,
literal|true
argument_list|,
literal|true
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
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|FieldDoc
name|d
init|=
operator|(
name|FieldDoc
operator|)
name|td
operator|.
name|scoreDocs
index|[
name|i
index|]
decl_stmt|;
name|float
name|expected
init|=
literal|2
operator|*
name|d
operator|.
name|score
decl_stmt|;
name|float
name|actual
init|=
operator|(
operator|(
name|Double
operator|)
name|d
operator|.
name|fields
index|[
literal|0
index|]
operator|)
operator|.
name|floatValue
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|,
name|CheckHits
operator|.
name|explainToleranceDelta
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** tests huge amounts of variables in the expression */
DECL|method|testLotsOfBindings
specifier|public
name|void
name|testLotsOfBindings
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestLotsOfBindings
argument_list|(
name|Byte
operator|.
name|MAX_VALUE
operator|-
literal|1
argument_list|)
expr_stmt|;
name|doTestLotsOfBindings
argument_list|(
name|Byte
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|doTestLotsOfBindings
argument_list|(
name|Byte
operator|.
name|MAX_VALUE
operator|+
literal|1
argument_list|)
expr_stmt|;
comment|// TODO: ideally we'd test> Short.MAX_VALUE too, but compilation is currently recursive.
comment|// so if we want to test such huge expressions, we need to instead change parser to use an explicit Stack
block|}
DECL|method|doTestLotsOfBindings
specifier|private
name|void
name|doTestLotsOfBindings
parameter_list|(
name|int
name|n
parameter_list|)
throws|throws
name|Exception
block|{
name|SimpleBindings
name|bindings
init|=
operator|new
name|SimpleBindings
argument_list|()
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
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
name|n
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"+"
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|"x"
operator|+
name|i
argument_list|)
expr_stmt|;
name|bindings
operator|.
name|add
argument_list|(
operator|new
name|SortField
argument_list|(
literal|"x"
operator|+
name|i
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|SCORE
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Expression
name|expr
init|=
name|JavascriptCompiler
operator|.
name|compile
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|Sort
name|sort
init|=
operator|new
name|Sort
argument_list|(
name|expr
operator|.
name|getSortField
argument_list|(
name|bindings
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|Query
name|query
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"contents"
argument_list|)
argument_list|)
decl_stmt|;
name|TopFieldDocs
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
literal|3
argument_list|,
name|sort
argument_list|,
literal|true
argument_list|,
literal|true
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
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|FieldDoc
name|d
init|=
operator|(
name|FieldDoc
operator|)
name|td
operator|.
name|scoreDocs
index|[
name|i
index|]
decl_stmt|;
name|float
name|expected
init|=
name|n
operator|*
name|d
operator|.
name|score
decl_stmt|;
name|float
name|actual
init|=
operator|(
operator|(
name|Double
operator|)
name|d
operator|.
name|fields
index|[
literal|0
index|]
operator|)
operator|.
name|floatValue
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|,
name|CheckHits
operator|.
name|explainToleranceDelta
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testDistanceSort
specifier|public
name|void
name|testDistanceSort
parameter_list|()
throws|throws
name|Exception
block|{
name|Expression
name|distance
init|=
name|JavascriptCompiler
operator|.
name|compile
argument_list|(
literal|"haversin(40.7143528,-74.0059731,latitude,longitude)"
argument_list|)
decl_stmt|;
name|SimpleBindings
name|bindings
init|=
operator|new
name|SimpleBindings
argument_list|()
decl_stmt|;
name|bindings
operator|.
name|add
argument_list|(
operator|new
name|SortField
argument_list|(
literal|"latitude"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|DOUBLE
argument_list|)
argument_list|)
expr_stmt|;
name|bindings
operator|.
name|add
argument_list|(
operator|new
name|SortField
argument_list|(
literal|"longitude"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|DOUBLE
argument_list|)
argument_list|)
expr_stmt|;
name|Sort
name|sort
init|=
operator|new
name|Sort
argument_list|(
name|distance
operator|.
name|getSortField
argument_list|(
name|bindings
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|TopFieldDocs
name|td
init|=
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|3
argument_list|,
name|sort
argument_list|)
decl_stmt|;
name|FieldDoc
name|d
init|=
operator|(
name|FieldDoc
operator|)
name|td
operator|.
name|scoreDocs
index|[
literal|0
index|]
decl_stmt|;
name|assertEquals
argument_list|(
literal|0.4619D
argument_list|,
operator|(
name|Double
operator|)
name|d
operator|.
name|fields
index|[
literal|0
index|]
argument_list|,
literal|1E
operator|-
literal|4
argument_list|)
expr_stmt|;
name|d
operator|=
operator|(
name|FieldDoc
operator|)
name|td
operator|.
name|scoreDocs
index|[
literal|1
index|]
expr_stmt|;
name|assertEquals
argument_list|(
literal|1.0546D
argument_list|,
operator|(
name|Double
operator|)
name|d
operator|.
name|fields
index|[
literal|0
index|]
argument_list|,
literal|1E
operator|-
literal|4
argument_list|)
expr_stmt|;
name|d
operator|=
operator|(
name|FieldDoc
operator|)
name|td
operator|.
name|scoreDocs
index|[
literal|2
index|]
expr_stmt|;
name|assertEquals
argument_list|(
literal|5.2842D
argument_list|,
operator|(
name|Double
operator|)
name|d
operator|.
name|fields
index|[
literal|0
index|]
argument_list|,
literal|1E
operator|-
literal|4
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

