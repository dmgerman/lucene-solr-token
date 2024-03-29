begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.sql
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|sql
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|plan
operator|.
name|RelOptCluster
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|plan
operator|.
name|RelOptCost
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|plan
operator|.
name|RelOptPlanner
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|plan
operator|.
name|RelTraitSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|rel
operator|.
name|RelCollation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|rel
operator|.
name|RelFieldCollation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|rel
operator|.
name|RelNode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|rel
operator|.
name|core
operator|.
name|Sort
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|rel
operator|.
name|metadata
operator|.
name|RelMetadataQuery
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|rel
operator|.
name|type
operator|.
name|RelDataTypeField
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|rex
operator|.
name|RexLiteral
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|rex
operator|.
name|RexNode
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

begin_comment
comment|/**  * Implementation of {@link org.apache.calcite.rel.core.Sort} relational expression in Solr.  */
end_comment

begin_class
DECL|class|SolrSort
class|class
name|SolrSort
extends|extends
name|Sort
implements|implements
name|SolrRel
block|{
DECL|method|SolrSort
name|SolrSort
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traitSet
parameter_list|,
name|RelNode
name|child
parameter_list|,
name|RelCollation
name|collation
parameter_list|,
name|RexNode
name|offset
parameter_list|,
name|RexNode
name|fetch
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|traitSet
argument_list|,
name|child
argument_list|,
name|collation
argument_list|,
name|offset
argument_list|,
name|fetch
argument_list|)
expr_stmt|;
assert|assert
name|getConvention
argument_list|()
operator|==
name|SolrRel
operator|.
name|CONVENTION
assert|;
assert|assert
name|getConvention
argument_list|()
operator|==
name|child
operator|.
name|getConvention
argument_list|()
assert|;
block|}
annotation|@
name|Override
DECL|method|computeSelfCost
specifier|public
name|RelOptCost
name|computeSelfCost
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|planner
operator|.
name|getCostFactory
argument_list|()
operator|.
name|makeZeroCost
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|copy
specifier|public
name|Sort
name|copy
parameter_list|(
name|RelTraitSet
name|traitSet
parameter_list|,
name|RelNode
name|input
parameter_list|,
name|RelCollation
name|newCollation
parameter_list|,
name|RexNode
name|offset
parameter_list|,
name|RexNode
name|fetch
parameter_list|)
block|{
return|return
operator|new
name|SolrSort
argument_list|(
name|getCluster
argument_list|()
argument_list|,
name|traitSet
argument_list|,
name|input
argument_list|,
name|collation
argument_list|,
name|offset
argument_list|,
name|fetch
argument_list|)
return|;
block|}
DECL|method|implement
specifier|public
name|void
name|implement
parameter_list|(
name|Implementor
name|implementor
parameter_list|)
block|{
name|implementor
operator|.
name|visitChild
argument_list|(
literal|0
argument_list|,
name|getInput
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|RelFieldCollation
argument_list|>
name|sortCollations
init|=
name|collation
operator|.
name|getFieldCollations
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|sortCollations
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// Construct a series of order clauses from the desired collation
specifier|final
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|fields
init|=
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
decl_stmt|;
for|for
control|(
name|RelFieldCollation
name|fieldCollation
range|:
name|sortCollations
control|)
block|{
specifier|final
name|String
name|name
init|=
name|fields
operator|.
name|get
argument_list|(
name|fieldCollation
operator|.
name|getFieldIndex
argument_list|()
argument_list|)
operator|.
name|getName
argument_list|()
decl_stmt|;
name|String
name|direction
init|=
literal|"asc"
decl_stmt|;
if|if
condition|(
name|fieldCollation
operator|.
name|getDirection
argument_list|()
operator|.
name|equals
argument_list|(
name|RelFieldCollation
operator|.
name|Direction
operator|.
name|DESCENDING
argument_list|)
condition|)
block|{
name|direction
operator|=
literal|"desc"
expr_stmt|;
block|}
name|implementor
operator|.
name|addOrder
argument_list|(
name|name
argument_list|,
name|direction
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|fetch
operator|!=
literal|null
condition|)
block|{
name|implementor
operator|.
name|setLimit
argument_list|(
operator|(
operator|(
name|RexLiteral
operator|)
name|fetch
operator|)
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

