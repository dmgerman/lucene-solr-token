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
name|adapter
operator|.
name|enumerable
operator|.
name|EnumerableConvention
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
name|convert
operator|.
name|ConverterRule
import|;
end_import

begin_comment
comment|/**  * Rule to convert a relational expression from {@link SolrRel#CONVENTION} to {@link EnumerableConvention}.  */
end_comment

begin_class
DECL|class|SolrToEnumerableConverterRule
class|class
name|SolrToEnumerableConverterRule
extends|extends
name|ConverterRule
block|{
DECL|field|INSTANCE
specifier|static
specifier|final
name|ConverterRule
name|INSTANCE
init|=
operator|new
name|SolrToEnumerableConverterRule
argument_list|()
decl_stmt|;
DECL|method|SolrToEnumerableConverterRule
specifier|private
name|SolrToEnumerableConverterRule
parameter_list|()
block|{
name|super
argument_list|(
name|RelNode
operator|.
name|class
argument_list|,
name|SolrRel
operator|.
name|CONVENTION
argument_list|,
name|EnumerableConvention
operator|.
name|INSTANCE
argument_list|,
literal|"SolrToEnumerableConverterRule"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|convert
specifier|public
name|RelNode
name|convert
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
name|RelTraitSet
name|newTraitSet
init|=
name|rel
operator|.
name|getTraitSet
argument_list|()
operator|.
name|replace
argument_list|(
name|getOutConvention
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|SolrToEnumerableConverter
argument_list|(
name|rel
operator|.
name|getCluster
argument_list|()
argument_list|,
name|newTraitSet
argument_list|,
name|rel
argument_list|)
return|;
block|}
block|}
end_class

end_unit
