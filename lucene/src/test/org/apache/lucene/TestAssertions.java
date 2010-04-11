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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|TokenStream
import|;
end_import

begin_class
DECL|class|TestAssertions
specifier|public
class|class
name|TestAssertions
extends|extends
name|LuceneTestCase
block|{
DECL|method|testBasics
specifier|public
name|void
name|testBasics
parameter_list|()
block|{
try|try
block|{
assert|assert
name|Boolean
operator|.
name|FALSE
operator|.
name|booleanValue
argument_list|()
assert|;
name|fail
argument_list|(
literal|"assertions are not enabled!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AssertionError
name|e
parameter_list|)
block|{
assert|assert
name|Boolean
operator|.
name|TRUE
operator|.
name|booleanValue
argument_list|()
assert|;
block|}
block|}
DECL|class|TestAnalyzer1
specifier|static
class|class
name|TestAnalyzer1
extends|extends
name|Analyzer
block|{
DECL|method|tokenStream
specifier|public
specifier|final
name|TokenStream
name|tokenStream
parameter_list|(
name|String
name|s
parameter_list|,
name|Reader
name|r
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
DECL|method|reusableTokenStream
specifier|public
specifier|final
name|TokenStream
name|reusableTokenStream
parameter_list|(
name|String
name|s
parameter_list|,
name|Reader
name|r
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
DECL|class|TestAnalyzer2
specifier|static
specifier|final
class|class
name|TestAnalyzer2
extends|extends
name|Analyzer
block|{
DECL|method|tokenStream
specifier|public
name|TokenStream
name|tokenStream
parameter_list|(
name|String
name|s
parameter_list|,
name|Reader
name|r
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
DECL|method|reusableTokenStream
specifier|public
name|TokenStream
name|reusableTokenStream
parameter_list|(
name|String
name|s
parameter_list|,
name|Reader
name|r
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
DECL|class|TestAnalyzer3
specifier|static
class|class
name|TestAnalyzer3
extends|extends
name|Analyzer
block|{
DECL|method|tokenStream
specifier|public
name|TokenStream
name|tokenStream
parameter_list|(
name|String
name|s
parameter_list|,
name|Reader
name|r
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
DECL|method|reusableTokenStream
specifier|public
name|TokenStream
name|reusableTokenStream
parameter_list|(
name|String
name|s
parameter_list|,
name|Reader
name|r
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
DECL|class|TestAnalyzer4
specifier|static
class|class
name|TestAnalyzer4
extends|extends
name|Analyzer
block|{
DECL|method|tokenStream
specifier|public
specifier|final
name|TokenStream
name|tokenStream
parameter_list|(
name|String
name|s
parameter_list|,
name|Reader
name|r
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
DECL|method|reusableTokenStream
specifier|public
name|TokenStream
name|reusableTokenStream
parameter_list|(
name|String
name|s
parameter_list|,
name|Reader
name|r
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
DECL|class|TestTokenStream1
specifier|static
class|class
name|TestTokenStream1
extends|extends
name|TokenStream
block|{
DECL|method|incrementToken
specifier|public
specifier|final
name|boolean
name|incrementToken
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
DECL|class|TestTokenStream2
specifier|static
specifier|final
class|class
name|TestTokenStream2
extends|extends
name|TokenStream
block|{
DECL|method|incrementToken
specifier|public
name|boolean
name|incrementToken
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
DECL|class|TestTokenStream3
specifier|static
class|class
name|TestTokenStream3
extends|extends
name|TokenStream
block|{
DECL|method|incrementToken
specifier|public
name|boolean
name|incrementToken
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
DECL|method|testTokenStreams
specifier|public
name|void
name|testTokenStreams
parameter_list|()
block|{
operator|new
name|TestAnalyzer1
argument_list|()
expr_stmt|;
operator|new
name|TestAnalyzer2
argument_list|()
expr_stmt|;
try|try
block|{
operator|new
name|TestAnalyzer3
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"TestAnalyzer3 should fail assertion"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AssertionError
name|e
parameter_list|)
block|{     }
try|try
block|{
operator|new
name|TestAnalyzer4
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"TestAnalyzer4 should fail assertion"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AssertionError
name|e
parameter_list|)
block|{     }
operator|new
name|TestTokenStream1
argument_list|()
expr_stmt|;
operator|new
name|TestTokenStream2
argument_list|()
expr_stmt|;
try|try
block|{
operator|new
name|TestTokenStream3
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"TestTokenStream3 should fail assertion"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AssertionError
name|e
parameter_list|)
block|{     }
block|}
block|}
end_class

end_unit

