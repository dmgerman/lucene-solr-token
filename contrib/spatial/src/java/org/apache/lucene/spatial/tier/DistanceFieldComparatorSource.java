begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.spatial.tier
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|tier
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
name|index
operator|.
name|IndexReader
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
name|Filter
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
name|FieldComparator
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
name|FieldComparatorSource
import|;
end_import

begin_comment
comment|/**  *<p><font color="red"><b>NOTE:</b> This API is still in  * flux and might change in incompatible ways in the next  * release.</font>  */
end_comment

begin_class
DECL|class|DistanceFieldComparatorSource
specifier|public
class|class
name|DistanceFieldComparatorSource
extends|extends
name|FieldComparatorSource
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
DECL|field|distanceFilter
specifier|private
name|DistanceFilter
name|distanceFilter
decl_stmt|;
DECL|field|dsdlc
specifier|private
name|DistanceScoreDocLookupComparator
name|dsdlc
decl_stmt|;
DECL|method|DistanceFieldComparatorSource
specifier|public
name|DistanceFieldComparatorSource
parameter_list|(
name|Filter
name|distanceFilter
parameter_list|)
block|{
name|this
operator|.
name|distanceFilter
operator|=
operator|(
name|DistanceFilter
operator|)
name|distanceFilter
expr_stmt|;
block|}
DECL|method|cleanUp
specifier|public
name|void
name|cleanUp
parameter_list|()
block|{
name|distanceFilter
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|dsdlc
operator|!=
literal|null
condition|)
name|dsdlc
operator|.
name|cleanUp
argument_list|()
expr_stmt|;
name|dsdlc
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|newComparator
specifier|public
name|FieldComparator
name|newComparator
parameter_list|(
name|String
name|fieldname
parameter_list|,
name|int
name|numHits
parameter_list|,
name|int
name|sortPos
parameter_list|,
name|boolean
name|reversed
parameter_list|)
throws|throws
name|IOException
block|{
name|dsdlc
operator|=
operator|new
name|DistanceScoreDocLookupComparator
argument_list|(
name|distanceFilter
argument_list|,
name|numHits
argument_list|)
expr_stmt|;
return|return
name|dsdlc
return|;
block|}
DECL|class|DistanceScoreDocLookupComparator
specifier|private
class|class
name|DistanceScoreDocLookupComparator
extends|extends
name|FieldComparator
block|{
DECL|field|distanceFilter
specifier|private
name|DistanceFilter
name|distanceFilter
decl_stmt|;
DECL|field|values
specifier|private
name|double
index|[]
name|values
decl_stmt|;
DECL|field|bottom
specifier|private
name|double
name|bottom
decl_stmt|;
DECL|field|offset
specifier|private
name|int
name|offset
init|=
literal|0
decl_stmt|;
DECL|method|DistanceScoreDocLookupComparator
specifier|public
name|DistanceScoreDocLookupComparator
parameter_list|(
name|DistanceFilter
name|distanceFilter
parameter_list|,
name|int
name|numHits
parameter_list|)
block|{
name|this
operator|.
name|distanceFilter
operator|=
name|distanceFilter
expr_stmt|;
name|values
operator|=
operator|new
name|double
index|[
name|numHits
index|]
expr_stmt|;
return|return;
block|}
annotation|@
name|Override
DECL|method|compare
specifier|public
name|int
name|compare
parameter_list|(
name|int
name|slot1
parameter_list|,
name|int
name|slot2
parameter_list|)
block|{
name|double
name|a
init|=
name|values
index|[
name|slot1
index|]
decl_stmt|;
name|double
name|b
init|=
name|values
index|[
name|slot2
index|]
decl_stmt|;
if|if
condition|(
name|a
operator|>
name|b
condition|)
return|return
literal|1
return|;
if|if
condition|(
name|a
operator|<
name|b
condition|)
return|return
operator|-
literal|1
return|;
return|return
literal|0
return|;
block|}
DECL|method|cleanUp
specifier|public
name|void
name|cleanUp
parameter_list|()
block|{
name|distanceFilter
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|compareBottom
specifier|public
name|int
name|compareBottom
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|double
name|v2
init|=
name|distanceFilter
operator|.
name|getDistance
argument_list|(
name|doc
operator|+
name|offset
argument_list|)
decl_stmt|;
if|if
condition|(
name|bottom
operator|>
name|v2
condition|)
block|{
return|return
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|bottom
operator|<
name|v2
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|copy
specifier|public
name|void
name|copy
parameter_list|(
name|int
name|slot
parameter_list|,
name|int
name|doc
parameter_list|)
block|{
name|values
index|[
name|slot
index|]
operator|=
name|distanceFilter
operator|.
name|getDistance
argument_list|(
name|doc
operator|+
name|offset
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setBottom
specifier|public
name|void
name|setBottom
parameter_list|(
name|int
name|slot
parameter_list|)
block|{
name|this
operator|.
name|bottom
operator|=
name|values
index|[
name|slot
index|]
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setNextReader
specifier|public
name|void
name|setNextReader
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|int
name|docBase
parameter_list|)
throws|throws
name|IOException
block|{
comment|// each reader in a segmented base
comment|// has an offset based on the maxDocs of previous readers
name|offset
operator|=
name|docBase
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|value
specifier|public
name|Comparable
argument_list|<
name|Double
argument_list|>
name|value
parameter_list|(
name|int
name|slot
parameter_list|)
block|{
return|return
name|values
index|[
name|slot
index|]
return|;
block|}
block|}
block|}
end_class

end_unit

