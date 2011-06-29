begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.index.streaming
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|index
operator|.
name|streaming
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
name|Set
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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
name|facet
operator|.
name|index
operator|.
name|CategoryContainerTestBase
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
name|facet
operator|.
name|index
operator|.
name|attributes
operator|.
name|CategoryAttribute
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
name|facet
operator|.
name|index
operator|.
name|attributes
operator|.
name|CategoryAttributeImpl
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
name|facet
operator|.
name|index
operator|.
name|streaming
operator|.
name|CategoryAttributesStream
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
name|facet
operator|.
name|taxonomy
operator|.
name|CategoryPath
import|;
end_import

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_class
DECL|class|CategoryAttributesStreamTest
specifier|public
class|class
name|CategoryAttributesStreamTest
extends|extends
name|CategoryContainerTestBase
block|{
comment|/**    * Verifies that a {@link CategoryAttributesStream} accepts    * {@link CategoryAttribute} and passes them on as tokens.    *     * @throws IOException    */
annotation|@
name|Test
DECL|method|testStream
specifier|public
name|void
name|testStream
parameter_list|()
throws|throws
name|IOException
block|{
name|ArrayList
argument_list|<
name|CategoryAttribute
argument_list|>
name|attributesList
init|=
operator|new
name|ArrayList
argument_list|<
name|CategoryAttribute
argument_list|>
argument_list|()
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
name|initialCatgeories
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|attributesList
operator|.
name|add
argument_list|(
operator|new
name|CategoryAttributeImpl
argument_list|(
name|initialCatgeories
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// test number of tokens
name|CategoryAttributesStream
name|stream
init|=
operator|new
name|CategoryAttributesStream
argument_list|(
name|attributesList
argument_list|)
decl_stmt|;
name|int
name|nTokens
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|stream
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|nTokens
operator|++
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"Wrong number of tokens"
argument_list|,
literal|3
argument_list|,
name|nTokens
argument_list|)
expr_stmt|;
comment|// test reset
name|stream
operator|.
name|reset
argument_list|()
expr_stmt|;
name|nTokens
operator|=
literal|0
expr_stmt|;
while|while
condition|(
name|stream
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|nTokens
operator|++
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"Wrong number of tokens"
argument_list|,
literal|3
argument_list|,
name|nTokens
argument_list|)
expr_stmt|;
comment|// test reset and contents
name|Set
argument_list|<
name|CategoryPath
argument_list|>
name|pathsSet
init|=
operator|new
name|HashSet
argument_list|<
name|CategoryPath
argument_list|>
argument_list|()
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
name|initialCatgeories
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|pathsSet
operator|.
name|add
argument_list|(
name|initialCatgeories
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|stream
operator|.
name|reset
argument_list|()
expr_stmt|;
while|while
condition|(
name|stream
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|CategoryAttribute
name|fromStream
init|=
name|stream
operator|.
name|getAttribute
argument_list|(
name|CategoryAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|pathsSet
operator|.
name|remove
argument_list|(
name|fromStream
operator|.
name|getCategoryPath
argument_list|()
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"Unexpected category path: "
operator|+
name|fromStream
operator|.
name|getCategoryPath
argument_list|()
operator|.
name|toString
argument_list|(
literal|':'
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
literal|"all category paths should have been found"
argument_list|,
name|pathsSet
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

