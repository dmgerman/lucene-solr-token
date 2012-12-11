begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.search.aggregator.association
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|search
operator|.
name|aggregator
operator|.
name|association
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|enhancements
operator|.
name|association
operator|.
name|AssociationsPayloadIterator
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
name|params
operator|.
name|CategoryListParams
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
name|search
operator|.
name|aggregator
operator|.
name|Aggregator
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
name|IndexReader
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * An {@link Aggregator} which updates the weight of a category by summing the  * weights of the float association it finds for every document.  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|AssociationFloatSumAggregator
specifier|public
class|class
name|AssociationFloatSumAggregator
implements|implements
name|Aggregator
block|{
DECL|field|field
specifier|protected
specifier|final
name|String
name|field
decl_stmt|;
DECL|field|sumArray
specifier|protected
specifier|final
name|float
index|[]
name|sumArray
decl_stmt|;
DECL|field|associationsPayloadIterator
specifier|protected
specifier|final
name|AssociationsPayloadIterator
name|associationsPayloadIterator
decl_stmt|;
DECL|method|AssociationFloatSumAggregator
specifier|public
name|AssociationFloatSumAggregator
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|float
index|[]
name|sumArray
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|CategoryListParams
operator|.
name|DEFAULT_TERM
operator|.
name|field
argument_list|()
argument_list|,
name|reader
argument_list|,
name|sumArray
argument_list|)
expr_stmt|;
block|}
DECL|method|AssociationFloatSumAggregator
specifier|public
name|AssociationFloatSumAggregator
parameter_list|(
name|String
name|field
parameter_list|,
name|IndexReader
name|reader
parameter_list|,
name|float
index|[]
name|sumArray
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
name|associationsPayloadIterator
operator|=
operator|new
name|AssociationsPayloadIterator
argument_list|(
name|reader
argument_list|,
name|field
argument_list|)
expr_stmt|;
name|this
operator|.
name|sumArray
operator|=
name|sumArray
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|aggregate
specifier|public
name|void
name|aggregate
parameter_list|(
name|int
name|ordinal
parameter_list|)
block|{
name|long
name|association
init|=
name|associationsPayloadIterator
operator|.
name|getAssociation
argument_list|(
name|ordinal
argument_list|)
decl_stmt|;
if|if
condition|(
name|association
operator|!=
name|AssociationsPayloadIterator
operator|.
name|NO_ASSOCIATION
condition|)
block|{
name|sumArray
index|[
name|ordinal
index|]
operator|+=
name|Float
operator|.
name|intBitsToFloat
argument_list|(
operator|(
name|int
operator|)
name|association
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|==
literal|null
operator|||
name|obj
operator|.
name|getClass
argument_list|()
operator|!=
name|this
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|AssociationFloatSumAggregator
name|that
init|=
operator|(
name|AssociationFloatSumAggregator
operator|)
name|obj
decl_stmt|;
return|return
name|that
operator|.
name|field
operator|.
name|equals
argument_list|(
name|field
argument_list|)
operator|&&
name|that
operator|.
name|sumArray
operator|==
name|sumArray
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|field
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setNextDoc
specifier|public
name|void
name|setNextDoc
parameter_list|(
name|int
name|docid
parameter_list|,
name|float
name|score
parameter_list|)
throws|throws
name|IOException
block|{
name|associationsPayloadIterator
operator|.
name|setNextDoc
argument_list|(
name|docid
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

