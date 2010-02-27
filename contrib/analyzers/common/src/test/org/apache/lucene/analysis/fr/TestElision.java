begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.fr
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|fr
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
name|StringReader
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
name|HashSet
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
name|TokenFilter
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
name|standard
operator|.
name|StandardTokenizer
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

begin_comment
comment|/**  *   */
end_comment

begin_class
DECL|class|TestElision
specifier|public
class|class
name|TestElision
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|method|testElision
specifier|public
name|void
name|testElision
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|test
init|=
literal|"Plop, juste pour voir l'embrouille avec O'brian. M'enfin."
decl_stmt|;
name|Tokenizer
name|tokenizer
init|=
operator|new
name|StandardTokenizer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|StringReader
argument_list|(
name|test
argument_list|)
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|articles
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|articles
operator|.
name|add
argument_list|(
literal|"l"
argument_list|)
expr_stmt|;
name|articles
operator|.
name|add
argument_list|(
literal|"M"
argument_list|)
expr_stmt|;
name|TokenFilter
name|filter
init|=
operator|new
name|ElisionFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|tokenizer
argument_list|,
name|articles
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|tas
init|=
name|filter
argument_list|(
name|filter
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"embrouille"
argument_list|,
name|tas
operator|.
name|get
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"O'brian"
argument_list|,
name|tas
operator|.
name|get
argument_list|(
literal|6
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"enfin"
argument_list|,
name|tas
operator|.
name|get
argument_list|(
literal|7
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|filter
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|filter
parameter_list|(
name|TokenFilter
name|filter
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|String
argument_list|>
name|tas
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|TermAttribute
name|termAtt
init|=
name|filter
operator|.
name|getAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
while|while
condition|(
name|filter
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|tas
operator|.
name|add
argument_list|(
name|termAtt
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|tas
return|;
block|}
block|}
end_class

end_unit

