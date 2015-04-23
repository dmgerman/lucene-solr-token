begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.schema
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|spatial
operator|.
name|prefix
operator|.
name|RecursivePrefixTreeStrategy
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
name|prefix
operator|.
name|tree
operator|.
name|PackedQuadPrefixTree
import|;
end_import

begin_comment
comment|/**  * @see RecursivePrefixTreeStrategy  * @lucene.experimental  */
end_comment

begin_class
DECL|class|SpatialRecursivePrefixTreeFieldType
specifier|public
class|class
name|SpatialRecursivePrefixTreeFieldType
extends|extends
name|AbstractSpatialPrefixTreeFieldType
argument_list|<
name|RecursivePrefixTreeStrategy
argument_list|>
block|{
comment|/** @see RecursivePrefixTreeStrategy#setPrefixGridScanLevel(int) */
DECL|field|PREFIX_GRID_SCAN_LEVEL
specifier|public
specifier|static
specifier|final
name|String
name|PREFIX_GRID_SCAN_LEVEL
init|=
literal|"prefixGridScanLevel"
decl_stmt|;
DECL|field|prefixGridScanLevel
specifier|private
name|Integer
name|prefixGridScanLevel
decl_stmt|;
annotation|@
name|Override
DECL|method|init
specifier|protected
name|void
name|init
parameter_list|(
name|IndexSchema
name|schema
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|)
block|{
name|super
operator|.
name|init
argument_list|(
name|schema
argument_list|,
name|args
argument_list|)
expr_stmt|;
name|String
name|v
init|=
name|args
operator|.
name|remove
argument_list|(
name|PREFIX_GRID_SCAN_LEVEL
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|!=
literal|null
condition|)
name|prefixGridScanLevel
operator|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|v
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|newPrefixTreeStrategy
specifier|protected
name|RecursivePrefixTreeStrategy
name|newPrefixTreeStrategy
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|RecursivePrefixTreeStrategy
name|strategy
init|=
operator|new
name|RecursivePrefixTreeStrategy
argument_list|(
name|grid
argument_list|,
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|prefixGridScanLevel
operator|!=
literal|null
condition|)
name|strategy
operator|.
name|setPrefixGridScanLevel
argument_list|(
name|prefixGridScanLevel
argument_list|)
expr_stmt|;
if|if
condition|(
name|grid
operator|instanceof
name|PackedQuadPrefixTree
condition|)
block|{
comment|// This grid has a (usually) better prune leafy branch implementation
operator|(
operator|(
name|PackedQuadPrefixTree
operator|)
name|grid
operator|)
operator|.
name|setPruneLeafyBranches
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|strategy
operator|.
name|setPruneLeafyBranches
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
return|return
name|strategy
return|;
block|}
block|}
end_class

end_unit

