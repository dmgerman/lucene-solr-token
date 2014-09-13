begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
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
name|BufferedReader
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
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|ZipFile
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
name|junit
operator|.
name|Assert
import|;
end_import

begin_comment
comment|/** Utility class for doing vocabulary-based stemming tests */
end_comment

begin_class
DECL|class|VocabularyAssert
specifier|public
class|class
name|VocabularyAssert
block|{
comment|/** Run a vocabulary test against two data files. */
DECL|method|assertVocabulary
specifier|public
specifier|static
name|void
name|assertVocabulary
parameter_list|(
name|Analyzer
name|a
parameter_list|,
name|InputStream
name|voc
parameter_list|,
name|InputStream
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|BufferedReader
name|vocReader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|voc
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
name|BufferedReader
name|outputReader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|out
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|inputWord
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|inputWord
operator|=
name|vocReader
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|String
name|expectedWord
init|=
name|outputReader
operator|.
name|readLine
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|expectedWord
argument_list|)
expr_stmt|;
name|BaseTokenStreamTestCase
operator|.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
name|inputWord
argument_list|,
name|expectedWord
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Run a vocabulary test against one file: tab separated. */
DECL|method|assertVocabulary
specifier|public
specifier|static
name|void
name|assertVocabulary
parameter_list|(
name|Analyzer
name|a
parameter_list|,
name|InputStream
name|vocOut
parameter_list|)
throws|throws
name|IOException
block|{
name|BufferedReader
name|vocReader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|vocOut
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|inputLine
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|inputLine
operator|=
name|vocReader
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|inputLine
operator|.
name|startsWith
argument_list|(
literal|"#"
argument_list|)
operator|||
name|inputLine
operator|.
name|trim
argument_list|()
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
continue|continue;
comment|/* comment */
name|String
name|words
index|[]
init|=
name|inputLine
operator|.
name|split
argument_list|(
literal|"\t"
argument_list|)
decl_stmt|;
name|BaseTokenStreamTestCase
operator|.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
name|words
index|[
literal|0
index|]
argument_list|,
name|words
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Run a vocabulary test against two data files inside a zip file */
DECL|method|assertVocabulary
specifier|public
specifier|static
name|void
name|assertVocabulary
parameter_list|(
name|Analyzer
name|a
parameter_list|,
name|Path
name|zipFile
parameter_list|,
name|String
name|voc
parameter_list|,
name|String
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|ZipFile
name|zip
init|=
operator|new
name|ZipFile
argument_list|(
name|zipFile
operator|.
name|toFile
argument_list|()
argument_list|)
decl_stmt|;
name|InputStream
name|v
init|=
name|zip
operator|.
name|getInputStream
argument_list|(
name|zip
operator|.
name|getEntry
argument_list|(
name|voc
argument_list|)
argument_list|)
decl_stmt|;
name|InputStream
name|o
init|=
name|zip
operator|.
name|getInputStream
argument_list|(
name|zip
operator|.
name|getEntry
argument_list|(
name|out
argument_list|)
argument_list|)
decl_stmt|;
name|assertVocabulary
argument_list|(
name|a
argument_list|,
name|v
argument_list|,
name|o
argument_list|)
expr_stmt|;
name|v
operator|.
name|close
argument_list|()
expr_stmt|;
name|o
operator|.
name|close
argument_list|()
expr_stmt|;
name|zip
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** Run a vocabulary test against a tab-separated data file inside a zip file */
DECL|method|assertVocabulary
specifier|public
specifier|static
name|void
name|assertVocabulary
parameter_list|(
name|Analyzer
name|a
parameter_list|,
name|Path
name|zipFile
parameter_list|,
name|String
name|vocOut
parameter_list|)
throws|throws
name|IOException
block|{
name|ZipFile
name|zip
init|=
operator|new
name|ZipFile
argument_list|(
name|zipFile
operator|.
name|toFile
argument_list|()
argument_list|)
decl_stmt|;
name|InputStream
name|vo
init|=
name|zip
operator|.
name|getInputStream
argument_list|(
name|zip
operator|.
name|getEntry
argument_list|(
name|vocOut
argument_list|)
argument_list|)
decl_stmt|;
name|assertVocabulary
argument_list|(
name|a
argument_list|,
name|vo
argument_list|)
expr_stmt|;
name|vo
operator|.
name|close
argument_list|()
expr_stmt|;
name|zip
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

