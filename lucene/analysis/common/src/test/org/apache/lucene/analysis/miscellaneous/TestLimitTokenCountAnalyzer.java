begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.miscellaneous
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|miscellaneous
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
name|BaseTokenStreamTestCase
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
name|TestUtil
import|;
end_import

begin_class
DECL|class|TestLimitTokenCountAnalyzer
specifier|public
class|class
name|TestLimitTokenCountAnalyzer
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|method|testLimitTokenCountAnalyzer
specifier|public
name|void
name|testLimitTokenCountAnalyzer
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|boolean
name|consumeAll
range|:
operator|new
name|boolean
index|[]
block|{
literal|true
block|,
literal|false
block|}
control|)
block|{
name|MockAnalyzer
name|mock
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
comment|// if we are consuming all tokens, we can use the checks,
comment|// otherwise we can't
name|mock
operator|.
name|setEnableChecks
argument_list|(
name|consumeAll
argument_list|)
expr_stmt|;
name|Analyzer
name|a
init|=
operator|new
name|LimitTokenCountAnalyzer
argument_list|(
name|mock
argument_list|,
literal|2
argument_list|,
name|consumeAll
argument_list|)
decl_stmt|;
comment|// dont use assertAnalyzesTo here, as the end offset is not the end of the string (unless consumeAll is true, in which case it's correct)!
name|assertTokenStreamContents
argument_list|(
name|a
operator|.
name|tokenStream
argument_list|(
literal|"dummy"
argument_list|,
literal|"1  2     3  4  5"
argument_list|)
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"1"
block|,
literal|"2"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|3
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|4
block|}
argument_list|,
name|consumeAll
condition|?
literal|16
else|:
literal|null
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|a
operator|.
name|tokenStream
argument_list|(
literal|"dummy"
argument_list|,
literal|"1 2 3 4 5"
argument_list|)
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"1"
block|,
literal|"2"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|2
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|3
block|}
argument_list|,
name|consumeAll
condition|?
literal|9
else|:
literal|null
argument_list|)
expr_stmt|;
comment|// less than the limit, ensure we behave correctly
name|assertTokenStreamContents
argument_list|(
name|a
operator|.
name|tokenStream
argument_list|(
literal|"dummy"
argument_list|,
literal|"1  "
argument_list|)
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"1"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|}
argument_list|,
name|consumeAll
condition|?
literal|3
else|:
literal|null
argument_list|)
expr_stmt|;
comment|// equal to limit
name|assertTokenStreamContents
argument_list|(
name|a
operator|.
name|tokenStream
argument_list|(
literal|"dummy"
argument_list|,
literal|"1  2  "
argument_list|)
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"1"
block|,
literal|"2"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|3
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|4
block|}
argument_list|,
name|consumeAll
condition|?
literal|6
else|:
literal|null
argument_list|)
expr_stmt|;
name|a
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|testLimitTokenCountIndexWriter
specifier|public
name|void
name|testLimitTokenCountIndexWriter
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|boolean
name|consumeAll
range|:
operator|new
name|boolean
index|[]
block|{
literal|true
block|,
literal|false
block|}
control|)
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|int
name|limit
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|50
argument_list|,
literal|101000
argument_list|)
decl_stmt|;
name|MockAnalyzer
name|mock
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
comment|// if we are consuming all tokens, we can use the checks,
comment|// otherwise we can't
name|mock
operator|.
name|setEnableChecks
argument_list|(
name|consumeAll
argument_list|)
expr_stmt|;
name|Analyzer
name|a
init|=
operator|new
name|LimitTokenCountAnalyzer
argument_list|(
name|mock
argument_list|,
name|limit
argument_list|,
name|consumeAll
argument_list|)
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|a
argument_list|)
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|StringBuilder
name|b
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
literal|1
init|;
name|i
operator|<
name|limit
condition|;
name|i
operator|++
control|)
name|b
operator|.
name|append
argument_list|(
literal|" a"
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|" x"
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|" z"
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"field"
argument_list|,
name|b
operator|.
name|toString
argument_list|()
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
name|doc
argument_list|)
expr_stmt|;
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
name|dir
argument_list|)
decl_stmt|;
name|Term
name|t
init|=
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"x"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|reader
operator|.
name|docFreq
argument_list|(
name|t
argument_list|)
argument_list|)
expr_stmt|;
name|t
operator|=
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"z"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|reader
operator|.
name|docFreq
argument_list|(
name|t
argument_list|)
argument_list|)
expr_stmt|;
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
name|a
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

