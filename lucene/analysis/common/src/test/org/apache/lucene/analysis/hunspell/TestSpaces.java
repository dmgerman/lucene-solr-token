begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.hunspell
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|hunspell
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_class
DECL|class|TestSpaces
specifier|public
class|class
name|TestSpaces
extends|extends
name|StemmerTestBase
block|{
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|init
argument_list|(
literal|"spaces.aff"
argument_list|,
literal|"spaces.dic"
argument_list|)
expr_stmt|;
block|}
DECL|method|testStemming
specifier|public
name|void
name|testStemming
parameter_list|()
block|{
name|assertStemsTo
argument_list|(
literal|"four"
argument_list|,
literal|"four"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"fours"
argument_list|,
literal|"four"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"five"
argument_list|,
literal|"five"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"forty four"
argument_list|,
literal|"forty four"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"forty fours"
argument_list|,
literal|"forty four"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"forty five"
argument_list|,
literal|"forty five"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"fifty"
argument_list|,
literal|"50"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"fiftys"
argument_list|,
literal|"50"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"sixty"
argument_list|,
literal|"60"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"sixty four"
argument_list|,
literal|"64"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"fifty four"
argument_list|,
literal|"54"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"fifty fours"
argument_list|,
literal|"54"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

