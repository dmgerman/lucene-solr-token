begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.ja
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|ja
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
name|FileNotFoundException
import|;
end_import

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
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|LineNumberReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
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
name|Tokenizer
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
name|ja
operator|.
name|JapaneseTokenizer
operator|.
name|Mode
import|;
end_import

begin_class
DECL|class|TestSearchMode
specifier|public
class|class
name|TestSearchMode
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|field|SEGMENTATION_FILENAME
specifier|private
specifier|final
specifier|static
name|String
name|SEGMENTATION_FILENAME
init|=
literal|"search-segmentation-tests.txt"
decl_stmt|;
DECL|field|analyzer
specifier|private
name|Analyzer
name|analyzer
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
name|analyzer
operator|=
operator|new
name|Analyzer
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|Tokenizer
name|tokenizer
init|=
operator|new
name|JapaneseTokenizer
argument_list|(
name|newAttributeFactory
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
name|Mode
operator|.
name|SEARCH
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|,
name|tokenizer
argument_list|)
return|;
block|}
block|}
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
name|analyzer
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
comment|/** Test search mode segmentation */
DECL|method|testSearchSegmentation
specifier|public
name|void
name|testSearchSegmentation
parameter_list|()
throws|throws
name|IOException
block|{
name|InputStream
name|is
init|=
name|TestSearchMode
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
name|SEGMENTATION_FILENAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|is
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
literal|"Cannot find "
operator|+
name|SEGMENTATION_FILENAME
operator|+
literal|" in test classpath"
argument_list|)
throw|;
block|}
try|try
block|{
name|LineNumberReader
name|reader
init|=
operator|new
name|LineNumberReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|is
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|line
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
comment|// Remove comments
name|line
operator|=
name|line
operator|.
name|replaceAll
argument_list|(
literal|"#.*$"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
comment|// Skip empty lines or comment lines
if|if
condition|(
name|line
operator|.
name|trim
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
continue|continue;
block|}
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
literal|"Line no. "
operator|+
name|reader
operator|.
name|getLineNumber
argument_list|()
operator|+
literal|": "
operator|+
name|line
argument_list|)
expr_stmt|;
block|}
name|String
index|[]
name|fields
init|=
name|line
operator|.
name|split
argument_list|(
literal|"\t"
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|String
name|sourceText
init|=
name|fields
index|[
literal|0
index|]
decl_stmt|;
name|String
index|[]
name|expectedTokens
init|=
name|fields
index|[
literal|1
index|]
operator|.
name|split
argument_list|(
literal|"\\s+"
argument_list|)
decl_stmt|;
name|int
index|[]
name|expectedPosIncrs
init|=
operator|new
name|int
index|[
name|expectedTokens
operator|.
name|length
index|]
decl_stmt|;
name|int
index|[]
name|expectedPosLengths
init|=
operator|new
name|int
index|[
name|expectedTokens
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|tokIDX
init|=
literal|0
init|;
name|tokIDX
operator|<
name|expectedTokens
operator|.
name|length
condition|;
name|tokIDX
operator|++
control|)
block|{
if|if
condition|(
name|expectedTokens
index|[
name|tokIDX
index|]
operator|.
name|endsWith
argument_list|(
literal|"/0"
argument_list|)
condition|)
block|{
name|expectedTokens
index|[
name|tokIDX
index|]
operator|=
name|expectedTokens
index|[
name|tokIDX
index|]
operator|.
name|replace
argument_list|(
literal|"/0"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|expectedPosLengths
index|[
name|tokIDX
index|]
operator|=
name|expectedTokens
operator|.
name|length
operator|-
literal|1
expr_stmt|;
block|}
else|else
block|{
name|expectedPosIncrs
index|[
name|tokIDX
index|]
operator|=
literal|1
expr_stmt|;
name|expectedPosLengths
index|[
name|tokIDX
index|]
operator|=
literal|1
expr_stmt|;
block|}
block|}
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
name|sourceText
argument_list|,
name|expectedTokens
argument_list|,
name|expectedPosIncrs
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

