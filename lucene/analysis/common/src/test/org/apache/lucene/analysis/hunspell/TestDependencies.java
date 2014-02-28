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
DECL|class|TestDependencies
specifier|public
class|class
name|TestDependencies
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
literal|"dependencies.aff"
argument_list|,
literal|"dependencies.dic"
argument_list|)
expr_stmt|;
block|}
DECL|method|testDependencies
specifier|public
name|void
name|testDependencies
parameter_list|()
block|{
name|assertStemsTo
argument_list|(
literal|"drink"
argument_list|,
literal|"drink"
argument_list|,
literal|"drink"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"drinks"
argument_list|,
literal|"drink"
argument_list|,
literal|"drink"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"drinkable"
argument_list|,
literal|"drink"
argument_list|)
expr_stmt|;
comment|// TODO: BUG! assertStemsTo("drinkables", "drink");
name|assertStemsTo
argument_list|(
literal|"undrinkable"
argument_list|,
literal|"drink"
argument_list|)
expr_stmt|;
comment|// TODO: BUG! assertStemsTo("undrinkables", "drink");
name|assertStemsTo
argument_list|(
literal|"undrink"
argument_list|)
expr_stmt|;
comment|// TODO: BUG! assertStemsTo("undrinks");
block|}
block|}
end_class

end_unit

