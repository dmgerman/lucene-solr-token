begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.queries.function
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queries
operator|.
name|function
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
name|FieldType
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
name|FloatField
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
name|IntField
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
name|document
operator|.
name|SortedDocValuesField
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
name|queries
operator|.
name|function
operator|.
name|valuesource
operator|.
name|FloatFieldSource
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
name|queries
operator|.
name|function
operator|.
name|valuesource
operator|.
name|IntFieldSource
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
name|Ignore
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Setup for function tests  */
end_comment

begin_class
annotation|@
name|Ignore
DECL|class|FunctionTestSetup
specifier|public
specifier|abstract
class|class
name|FunctionTestSetup
extends|extends
name|LuceneTestCase
block|{
comment|/**    * Actual score computation order is slightly different than assumptios    * this allows for a small amount of variation    */
DECL|field|TEST_SCORE_TOLERANCE_DELTA
specifier|protected
specifier|static
name|float
name|TEST_SCORE_TOLERANCE_DELTA
init|=
literal|0.001f
decl_stmt|;
DECL|field|N_DOCS
specifier|protected
specifier|static
specifier|final
name|int
name|N_DOCS
init|=
literal|17
decl_stmt|;
comment|// select a primary number> 2
DECL|field|ID_FIELD
specifier|protected
specifier|static
specifier|final
name|String
name|ID_FIELD
init|=
literal|"id"
decl_stmt|;
DECL|field|TEXT_FIELD
specifier|protected
specifier|static
specifier|final
name|String
name|TEXT_FIELD
init|=
literal|"text"
decl_stmt|;
DECL|field|INT_FIELD
specifier|protected
specifier|static
specifier|final
name|String
name|INT_FIELD
init|=
literal|"iii"
decl_stmt|;
DECL|field|FLOAT_FIELD
specifier|protected
specifier|static
specifier|final
name|String
name|FLOAT_FIELD
init|=
literal|"fff"
decl_stmt|;
DECL|field|INT_VALUESOURCE
specifier|protected
name|ValueSource
name|INT_VALUESOURCE
init|=
operator|new
name|IntFieldSource
argument_list|(
name|INT_FIELD
argument_list|)
decl_stmt|;
DECL|field|FLOAT_VALUESOURCE
specifier|protected
name|ValueSource
name|FLOAT_VALUESOURCE
init|=
operator|new
name|FloatFieldSource
argument_list|(
name|FLOAT_FIELD
argument_list|)
decl_stmt|;
DECL|field|DOC_TEXT_LINES
specifier|private
specifier|static
specifier|final
name|String
name|DOC_TEXT_LINES
index|[]
init|=
block|{
literal|"Well, this is just some plain text we use for creating the "
block|,
literal|"test documents. It used to be a text from an online collection "
block|,
literal|"devoted to first aid, but if there was there an (online) lawyers "
block|,
literal|"first aid collection with legal advices, \"it\" might have quite "
block|,
literal|"probably advised one not to include \"it\"'s text or the text of "
block|,
literal|"any other online collection in one's code, unless one has money "
block|,
literal|"that one don't need and one is happy to donate for lawyers "
block|,
literal|"charity. Anyhow at some point, rechecking the usage of this text, "
block|,
literal|"it became uncertain that this text is free to use, because "
block|,
literal|"the web site in the disclaimer of he eBook containing that text "
block|,
literal|"was not responding anymore, and at the same time, in projGut, "
block|,
literal|"searching for first aid no longer found that eBook as well. "
block|,
literal|"So here we are, with a perhaps much less interesting "
block|,
literal|"text for the test, but oh much much safer. "
block|,   }
decl_stmt|;
DECL|field|dir
specifier|protected
specifier|static
name|Directory
name|dir
decl_stmt|;
DECL|field|anlzr
specifier|protected
specifier|static
name|Analyzer
name|anlzr
decl_stmt|;
annotation|@
name|AfterClass
DECL|method|afterClassFunctionTestSetup
specifier|public
specifier|static
name|void
name|afterClassFunctionTestSetup
parameter_list|()
throws|throws
name|Exception
block|{
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|=
literal|null
expr_stmt|;
name|anlzr
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|createIndex
specifier|protected
specifier|static
name|void
name|createIndex
parameter_list|(
name|boolean
name|doMultiSegment
parameter_list|)
throws|throws
name|Exception
block|{
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
literal|"TEST: setUp"
argument_list|)
expr_stmt|;
block|}
comment|// prepare a small index with just a few documents.
name|dir
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|anlzr
operator|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
expr_stmt|;
name|IndexWriterConfig
name|iwc
init|=
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|anlzr
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|doMultiSegment
condition|)
block|{
name|iwc
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
literal|2
argument_list|,
literal|7
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
argument_list|,
name|iwc
argument_list|)
decl_stmt|;
comment|// add docs not exactly in natural ID order, to verify we do check the order of docs by scores
name|int
name|remaining
init|=
name|N_DOCS
decl_stmt|;
name|boolean
name|done
index|[]
init|=
operator|new
name|boolean
index|[
name|N_DOCS
index|]
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|remaining
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|done
index|[
name|i
index|]
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"to set this test correctly N_DOCS="
operator|+
name|N_DOCS
operator|+
literal|" must be primary and greater than 2!"
argument_list|)
throw|;
block|}
name|addDoc
argument_list|(
name|iw
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|done
index|[
name|i
index|]
operator|=
literal|true
expr_stmt|;
name|i
operator|=
operator|(
name|i
operator|+
literal|4
operator|)
operator|%
name|N_DOCS
expr_stmt|;
name|remaining
operator|--
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|doMultiSegment
condition|)
block|{
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
literal|"TEST: setUp full merge"
argument_list|)
expr_stmt|;
block|}
name|iw
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|iw
operator|.
name|shutdown
argument_list|()
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
literal|"TEST: setUp done close"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addDoc
specifier|private
specifier|static
name|void
name|addDoc
parameter_list|(
name|RandomIndexWriter
name|iw
parameter_list|,
name|int
name|i
parameter_list|)
throws|throws
name|Exception
block|{
name|Document
name|d
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|Field
name|f
decl_stmt|;
name|int
name|scoreAndID
init|=
name|i
operator|+
literal|1
decl_stmt|;
name|FieldType
name|customType
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
decl_stmt|;
name|customType
operator|.
name|setTokenized
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|customType
operator|.
name|setOmitNorms
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|f
operator|=
name|newField
argument_list|(
name|ID_FIELD
argument_list|,
name|id2String
argument_list|(
name|scoreAndID
argument_list|)
argument_list|,
name|customType
argument_list|)
expr_stmt|;
comment|// for debug purposes
name|d
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|SortedDocValuesField
argument_list|(
name|ID_FIELD
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|id2String
argument_list|(
name|scoreAndID
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|FieldType
name|customType2
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_NOT_STORED
argument_list|)
decl_stmt|;
name|customType2
operator|.
name|setOmitNorms
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|f
operator|=
name|newField
argument_list|(
name|TEXT_FIELD
argument_list|,
literal|"text of doc"
operator|+
name|scoreAndID
operator|+
name|textLine
argument_list|(
name|i
argument_list|)
argument_list|,
name|customType2
argument_list|)
expr_stmt|;
comment|// for regular search
name|d
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|f
operator|=
operator|new
name|IntField
argument_list|(
name|INT_FIELD
argument_list|,
name|scoreAndID
argument_list|,
name|Store
operator|.
name|YES
argument_list|)
expr_stmt|;
comment|// for function scoring
name|d
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|NumericDocValuesField
argument_list|(
name|INT_FIELD
argument_list|,
name|scoreAndID
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|=
operator|new
name|FloatField
argument_list|(
name|FLOAT_FIELD
argument_list|,
name|scoreAndID
argument_list|,
name|Store
operator|.
name|YES
argument_list|)
expr_stmt|;
comment|// for function scoring
name|d
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|NumericDocValuesField
argument_list|(
name|FLOAT_FIELD
argument_list|,
name|Float
operator|.
name|floatToRawIntBits
argument_list|(
name|scoreAndID
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|log
argument_list|(
literal|"added: "
operator|+
name|d
argument_list|)
expr_stmt|;
block|}
comment|// 17 --> ID00017
DECL|method|id2String
specifier|protected
specifier|static
name|String
name|id2String
parameter_list|(
name|int
name|scoreAndID
parameter_list|)
block|{
name|String
name|s
init|=
literal|"000000000"
operator|+
name|scoreAndID
decl_stmt|;
name|int
name|n
init|=
operator|(
literal|""
operator|+
name|N_DOCS
operator|)
operator|.
name|length
argument_list|()
operator|+
literal|3
decl_stmt|;
name|int
name|k
init|=
name|s
operator|.
name|length
argument_list|()
operator|-
name|n
decl_stmt|;
return|return
literal|"ID"
operator|+
name|s
operator|.
name|substring
argument_list|(
name|k
argument_list|)
return|;
block|}
comment|// some text line for regular search
DECL|method|textLine
specifier|private
specifier|static
name|String
name|textLine
parameter_list|(
name|int
name|docNum
parameter_list|)
block|{
return|return
name|DOC_TEXT_LINES
index|[
name|docNum
operator|%
name|DOC_TEXT_LINES
operator|.
name|length
index|]
return|;
block|}
comment|// extract expected doc score from its ID Field: "ID7" --> 7.0
DECL|method|expectedFieldScore
specifier|protected
specifier|static
name|float
name|expectedFieldScore
parameter_list|(
name|String
name|docIDFieldVal
parameter_list|)
block|{
return|return
name|Float
operator|.
name|parseFloat
argument_list|(
name|docIDFieldVal
operator|.
name|substring
argument_list|(
literal|2
argument_list|)
argument_list|)
return|;
block|}
comment|// debug messages (change DBG to true for anything to print)
DECL|method|log
specifier|protected
specifier|static
name|void
name|log
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
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
name|o
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

