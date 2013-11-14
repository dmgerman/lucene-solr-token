begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.params
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|params
package|;
end_package

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
name|facet
operator|.
name|taxonomy
operator|.
name|FacetLabel
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * A {@link CategoryListParams} which allow controlling the  * {@link CategoryListParams.OrdinalPolicy} used for each dimension. The  * dimension is specified as the first component in  * {@link FacetLabel#components}.  */
end_comment

begin_class
DECL|class|PerDimensionOrdinalPolicy
specifier|public
class|class
name|PerDimensionOrdinalPolicy
extends|extends
name|CategoryListParams
block|{
DECL|field|policies
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|OrdinalPolicy
argument_list|>
name|policies
decl_stmt|;
DECL|field|defaultOP
specifier|private
specifier|final
name|OrdinalPolicy
name|defaultOP
decl_stmt|;
DECL|method|PerDimensionOrdinalPolicy
specifier|public
name|PerDimensionOrdinalPolicy
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|OrdinalPolicy
argument_list|>
name|policies
parameter_list|)
block|{
name|this
argument_list|(
name|policies
argument_list|,
name|DEFAULT_ORDINAL_POLICY
argument_list|)
expr_stmt|;
block|}
DECL|method|PerDimensionOrdinalPolicy
specifier|public
name|PerDimensionOrdinalPolicy
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|OrdinalPolicy
argument_list|>
name|policies
parameter_list|,
name|OrdinalPolicy
name|defaultOP
parameter_list|)
block|{
name|this
operator|.
name|defaultOP
operator|=
name|defaultOP
expr_stmt|;
name|this
operator|.
name|policies
operator|=
name|policies
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getOrdinalPolicy
specifier|public
name|OrdinalPolicy
name|getOrdinalPolicy
parameter_list|(
name|String
name|dimension
parameter_list|)
block|{
name|OrdinalPolicy
name|op
init|=
name|policies
operator|.
name|get
argument_list|(
name|dimension
argument_list|)
decl_stmt|;
return|return
name|op
operator|==
literal|null
condition|?
name|defaultOP
else|:
name|op
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|super
operator|.
name|toString
argument_list|()
operator|+
literal|" policies="
operator|+
name|policies
return|;
block|}
block|}
end_class

end_unit

