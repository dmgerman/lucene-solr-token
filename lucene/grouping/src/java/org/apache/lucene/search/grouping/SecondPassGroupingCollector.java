begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search.grouping
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|grouping
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
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
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
name|index
operator|.
name|LeafReaderContext
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
name|search
operator|.
name|Scorer
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
name|search
operator|.
name|SimpleCollector
import|;
end_import

begin_comment
comment|/**  * SecondPassGroupingCollector runs over an already collected set of  * groups, further applying a {@link GroupReducer} to each group  *  * @see TopGroupsCollector  * @see DistinctValuesCollector  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|SecondPassGroupingCollector
specifier|public
class|class
name|SecondPassGroupingCollector
parameter_list|<
name|T
parameter_list|>
extends|extends
name|SimpleCollector
block|{
DECL|field|groupSelector
specifier|protected
specifier|final
name|GroupSelector
argument_list|<
name|T
argument_list|>
name|groupSelector
decl_stmt|;
DECL|field|groups
specifier|protected
specifier|final
name|Collection
argument_list|<
name|SearchGroup
argument_list|<
name|T
argument_list|>
argument_list|>
name|groups
decl_stmt|;
DECL|field|groupReducer
specifier|protected
specifier|final
name|GroupReducer
argument_list|<
name|T
argument_list|,
name|?
argument_list|>
name|groupReducer
decl_stmt|;
DECL|field|totalHitCount
specifier|protected
name|int
name|totalHitCount
decl_stmt|;
DECL|field|totalGroupedHitCount
specifier|protected
name|int
name|totalGroupedHitCount
decl_stmt|;
comment|/**    * Create a new SecondPassGroupingCollector    * @param groupSelector   the GroupSelector that defines groups for this search    * @param groups          the groups to collect documents for    * @param reducer         the reducer to apply to each group    */
DECL|method|SecondPassGroupingCollector
specifier|public
name|SecondPassGroupingCollector
parameter_list|(
name|GroupSelector
argument_list|<
name|T
argument_list|>
name|groupSelector
parameter_list|,
name|Collection
argument_list|<
name|SearchGroup
argument_list|<
name|T
argument_list|>
argument_list|>
name|groups
parameter_list|,
name|GroupReducer
argument_list|<
name|T
argument_list|,
name|?
argument_list|>
name|reducer
parameter_list|)
block|{
comment|//System.out.println("SP init");
if|if
condition|(
name|groups
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"no groups to collect (groups is empty)"
argument_list|)
throw|;
block|}
name|this
operator|.
name|groupSelector
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|groupSelector
argument_list|)
expr_stmt|;
name|this
operator|.
name|groupSelector
operator|.
name|setGroups
argument_list|(
name|groups
argument_list|)
expr_stmt|;
name|this
operator|.
name|groups
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|groups
argument_list|)
expr_stmt|;
name|this
operator|.
name|groupReducer
operator|=
name|reducer
expr_stmt|;
name|reducer
operator|.
name|setGroups
argument_list|(
name|groups
argument_list|)
expr_stmt|;
block|}
comment|/**    * @return the GroupSelector used in this collector    */
DECL|method|getGroupSelector
specifier|public
name|GroupSelector
argument_list|<
name|T
argument_list|>
name|getGroupSelector
parameter_list|()
block|{
return|return
name|groupSelector
return|;
block|}
annotation|@
name|Override
DECL|method|needsScores
specifier|public
name|boolean
name|needsScores
parameter_list|()
block|{
return|return
name|groupReducer
operator|.
name|needsScores
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setScorer
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
throws|throws
name|IOException
block|{
name|groupReducer
operator|.
name|setScorer
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|totalHitCount
operator|++
expr_stmt|;
if|if
condition|(
name|groupSelector
operator|.
name|advanceTo
argument_list|(
name|doc
argument_list|)
operator|==
name|GroupSelector
operator|.
name|State
operator|.
name|SKIP
condition|)
return|return;
name|totalGroupedHitCount
operator|++
expr_stmt|;
name|T
name|value
init|=
name|groupSelector
operator|.
name|currentValue
argument_list|()
decl_stmt|;
name|groupReducer
operator|.
name|collect
argument_list|(
name|value
argument_list|,
name|doc
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doSetNextReader
specifier|protected
name|void
name|doSetNextReader
parameter_list|(
name|LeafReaderContext
name|readerContext
parameter_list|)
throws|throws
name|IOException
block|{
name|groupReducer
operator|.
name|setNextReader
argument_list|(
name|readerContext
argument_list|)
expr_stmt|;
name|groupSelector
operator|.
name|setNextReader
argument_list|(
name|readerContext
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

