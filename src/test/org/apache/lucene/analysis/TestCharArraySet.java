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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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

begin_class
DECL|class|TestCharArraySet
specifier|public
class|class
name|TestCharArraySet
extends|extends
name|LuceneTestCase
block|{
DECL|method|testRehash
specifier|public
name|void
name|testRehash
parameter_list|()
throws|throws
name|Exception
block|{
name|CharArraySet
name|cas
init|=
operator|new
name|CharArraySet
argument_list|(
literal|0
argument_list|,
literal|true
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|StopAnalyzer
operator|.
name|ENGLISH_STOP_WORDS
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|cas
operator|.
name|add
argument_list|(
name|StopAnalyzer
operator|.
name|ENGLISH_STOP_WORDS
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|StopAnalyzer
operator|.
name|ENGLISH_STOP_WORDS
operator|.
name|length
argument_list|,
name|cas
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|StopAnalyzer
operator|.
name|ENGLISH_STOP_WORDS
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|assertTrue
argument_list|(
name|cas
operator|.
name|contains
argument_list|(
name|StopAnalyzer
operator|.
name|ENGLISH_STOP_WORDS
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testNonZeroOffset
specifier|public
name|void
name|testNonZeroOffset
parameter_list|()
block|{
name|String
index|[]
name|words
init|=
block|{
literal|"Hello"
block|,
literal|"World"
block|,
literal|"this"
block|,
literal|"is"
block|,
literal|"a"
block|,
literal|"test"
block|}
decl_stmt|;
name|char
index|[]
name|findme
init|=
literal|"xthisy"
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
name|CharArraySet
name|set
init|=
operator|new
name|CharArraySet
argument_list|(
literal|10
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|set
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|words
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|set
operator|.
name|contains
argument_list|(
name|findme
argument_list|,
literal|1
argument_list|,
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|set
operator|.
name|contains
argument_list|(
operator|new
name|String
argument_list|(
name|findme
argument_list|,
literal|1
argument_list|,
literal|4
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testObjectContains
specifier|public
name|void
name|testObjectContains
parameter_list|()
block|{
name|CharArraySet
name|set
init|=
operator|new
name|CharArraySet
argument_list|(
literal|10
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Integer
name|val
init|=
operator|new
name|Integer
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|set
operator|.
name|add
argument_list|(
name|val
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|set
operator|.
name|contains
argument_list|(
name|val
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|set
operator|.
name|contains
argument_list|(
operator|new
name|Integer
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

