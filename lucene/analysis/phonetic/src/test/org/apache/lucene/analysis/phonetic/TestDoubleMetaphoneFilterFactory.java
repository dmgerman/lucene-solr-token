begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.phonetic
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|phonetic
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
name|StringReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|phonetic
operator|.
name|DoubleMetaphoneFilter
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
name|CharTermAttribute
import|;
end_import

begin_class
DECL|class|TestDoubleMetaphoneFilterFactory
specifier|public
class|class
name|TestDoubleMetaphoneFilterFactory
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|method|testDefaults
specifier|public
name|void
name|testDefaults
parameter_list|()
throws|throws
name|Exception
block|{
name|DoubleMetaphoneFilterFactory
name|factory
init|=
operator|new
name|DoubleMetaphoneFilterFactory
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
name|TokenStream
name|inputStream
init|=
name|whitespaceMockTokenizer
argument_list|(
literal|"international"
argument_list|)
decl_stmt|;
name|TokenStream
name|filteredStream
init|=
name|factory
operator|.
name|create
argument_list|(
name|inputStream
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|DoubleMetaphoneFilter
operator|.
name|class
argument_list|,
name|filteredStream
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|filteredStream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"international"
block|,
literal|"ANTR"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testSettingSizeAndInject
specifier|public
name|void
name|testSettingSizeAndInject
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|parameters
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|parameters
operator|.
name|put
argument_list|(
literal|"inject"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|parameters
operator|.
name|put
argument_list|(
literal|"maxCodeLength"
argument_list|,
literal|"8"
argument_list|)
expr_stmt|;
name|DoubleMetaphoneFilterFactory
name|factory
init|=
operator|new
name|DoubleMetaphoneFilterFactory
argument_list|(
name|parameters
argument_list|)
decl_stmt|;
name|TokenStream
name|inputStream
init|=
name|whitespaceMockTokenizer
argument_list|(
literal|"international"
argument_list|)
decl_stmt|;
name|TokenStream
name|filteredStream
init|=
name|factory
operator|.
name|create
argument_list|(
name|inputStream
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|DoubleMetaphoneFilter
operator|.
name|class
argument_list|,
name|filteredStream
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|filteredStream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ANTRNXNL"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** Test that bogus arguments result in exception */
DECL|method|testBogusArguments
specifier|public
name|void
name|testBogusArguments
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
operator|new
name|DoubleMetaphoneFilterFactory
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
block|{
name|put
argument_list|(
literal|"bogusArg"
argument_list|,
literal|"bogusValue"
argument_list|)
expr_stmt|;
block|}
block|}
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
block|{
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Unknown parameters"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

