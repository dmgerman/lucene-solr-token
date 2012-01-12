begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.kuromoji
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|kuromoji
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

begin_class
DECL|class|TestKuromojiAnalyzer
specifier|public
class|class
name|TestKuromojiAnalyzer
extends|extends
name|BaseTokenStreamTestCase
block|{
comment|/** This test fails with NPE when the     * stopwords file is missing in classpath */
DECL|method|testResourcesAvailable
specifier|public
name|void
name|testResourcesAvailable
parameter_list|()
block|{
operator|new
name|KuromojiAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
expr_stmt|;
block|}
comment|/**    * An example sentence, test removal of particles, etc by POS,    * lemmatization with the basic form, and that position increments    * and offsets are correct.    */
DECL|method|testBasics
specifier|public
name|void
name|testBasics
parameter_list|()
throws|throws
name|IOException
block|{
name|assertAnalyzesTo
argument_list|(
operator|new
name|KuromojiAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
argument_list|,
literal|"å¤ãã®å­¦çãè©¦é¨ã«è½ã¡ãã"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"å¤ã"
block|,
literal|"å­¦ç"
block|,
literal|"è©¦é¨"
block|,
literal|"è½ã¡ã"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|3
block|,
literal|6
block|,
literal|9
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|2
block|,
literal|5
block|,
literal|8
block|,
literal|11
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|2
block|,
literal|2
block|,
literal|2
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * blast random strings against the analyzer    */
DECL|method|testRandom
specifier|public
name|void
name|testRandom
parameter_list|()
throws|throws
name|IOException
block|{
name|checkRandomData
argument_list|(
name|random
argument_list|,
operator|new
name|KuromojiAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
argument_list|,
name|atLeast
argument_list|(
literal|10000
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

