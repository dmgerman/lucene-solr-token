begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.spatial.base.prefix
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|base
operator|.
name|prefix
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|base
operator|.
name|context
operator|.
name|simple
operator|.
name|SimpleSpatialContext
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
name|spatial
operator|.
name|base
operator|.
name|prefix
operator|.
name|geohash
operator|.
name|GeohashPrefixTree
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
name|spatial
operator|.
name|base
operator|.
name|shape
operator|.
name|Rectangle
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
name|spatial
operator|.
name|base
operator|.
name|shape
operator|.
name|Shape
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_class
DECL|class|SpatialPrefixTreeTest
specifier|public
class|class
name|SpatialPrefixTreeTest
block|{
comment|//TODO plug in others and test them
DECL|field|ctx
specifier|private
name|SimpleSpatialContext
name|ctx
decl_stmt|;
DECL|field|trie
specifier|private
name|SpatialPrefixTree
name|trie
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|ctx
operator|=
name|SimpleSpatialContext
operator|.
name|GEO_KM
expr_stmt|;
name|trie
operator|=
operator|new
name|GeohashPrefixTree
argument_list|(
name|ctx
argument_list|,
literal|4
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNodeTraverse
specifier|public
name|void
name|testNodeTraverse
parameter_list|()
block|{
name|Node
name|prevN
init|=
literal|null
decl_stmt|;
name|Node
name|n
init|=
name|trie
operator|.
name|getWorldNode
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|n
operator|.
name|getLevel
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ctx
operator|.
name|getWorldBounds
argument_list|()
argument_list|,
name|n
operator|.
name|getShape
argument_list|()
argument_list|)
expr_stmt|;
while|while
condition|(
name|n
operator|.
name|getLevel
argument_list|()
operator|<
name|trie
operator|.
name|getMaxLevels
argument_list|()
condition|)
block|{
name|prevN
operator|=
name|n
expr_stmt|;
name|n
operator|=
name|n
operator|.
name|getSubCells
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
expr_stmt|;
comment|//TODO random which one?
name|assertEquals
argument_list|(
name|prevN
operator|.
name|getLevel
argument_list|()
operator|+
literal|1
argument_list|,
name|n
operator|.
name|getLevel
argument_list|()
argument_list|)
expr_stmt|;
name|Rectangle
name|prevNShape
init|=
operator|(
name|Rectangle
operator|)
name|prevN
operator|.
name|getShape
argument_list|()
decl_stmt|;
name|Shape
name|s
init|=
name|n
operator|.
name|getShape
argument_list|()
decl_stmt|;
name|Rectangle
name|sbox
init|=
name|s
operator|.
name|getBoundingBox
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|prevNShape
operator|.
name|getWidth
argument_list|()
operator|>
name|sbox
operator|.
name|getWidth
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|prevNShape
operator|.
name|getHeight
argument_list|()
operator|>
name|sbox
operator|.
name|getHeight
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

