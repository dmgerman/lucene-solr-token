begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.spatial3d
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial3d
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
name|SortField
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
name|spatial3d
operator|.
name|geom
operator|.
name|GeoOutsideDistance
import|;
end_import

begin_comment
comment|/**  * Sorts by outside distance from an origin location.  */
end_comment

begin_class
DECL|class|Geo3DPointOutsideSortField
specifier|final
class|class
name|Geo3DPointOutsideSortField
extends|extends
name|SortField
block|{
DECL|field|distanceShape
specifier|final
name|GeoOutsideDistance
name|distanceShape
decl_stmt|;
DECL|method|Geo3DPointOutsideSortField
name|Geo3DPointOutsideSortField
parameter_list|(
specifier|final
name|String
name|field
parameter_list|,
specifier|final
name|GeoOutsideDistance
name|distanceShape
parameter_list|)
block|{
name|super
argument_list|(
name|field
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|CUSTOM
argument_list|)
expr_stmt|;
if|if
condition|(
name|field
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"field must not be null"
argument_list|)
throw|;
block|}
if|if
condition|(
name|distanceShape
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"distanceShape must not be null"
argument_list|)
throw|;
block|}
name|this
operator|.
name|distanceShape
operator|=
name|distanceShape
expr_stmt|;
name|setMissingValue
argument_list|(
name|Double
operator|.
name|POSITIVE_INFINITY
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getComparator
specifier|public
name|FieldComparator
argument_list|<
name|?
argument_list|>
name|getComparator
parameter_list|(
name|int
name|numHits
parameter_list|,
name|int
name|sortPos
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|Geo3DPointOutsideDistanceComparator
argument_list|(
name|getField
argument_list|()
argument_list|,
name|distanceShape
argument_list|,
name|numHits
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getMissingValue
specifier|public
name|Double
name|getMissingValue
parameter_list|()
block|{
return|return
operator|(
name|Double
operator|)
name|super
operator|.
name|getMissingValue
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setMissingValue
specifier|public
name|void
name|setMissingValue
parameter_list|(
name|Object
name|missingValue
parameter_list|)
block|{
if|if
condition|(
name|Double
operator|.
name|valueOf
argument_list|(
name|Double
operator|.
name|POSITIVE_INFINITY
argument_list|)
operator|.
name|equals
argument_list|(
name|missingValue
argument_list|)
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Missing value can only be Double.POSITIVE_INFINITY (missing values last), but got "
operator|+
name|missingValue
argument_list|)
throw|;
block|}
name|this
operator|.
name|missingValue
operator|=
name|missingValue
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|result
init|=
name|super
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|long
name|temp
decl_stmt|;
name|temp
operator|=
name|distanceShape
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
call|(
name|int
call|)
argument_list|(
name|temp
operator|^
operator|(
name|temp
operator|>>>
literal|32
operator|)
argument_list|)
expr_stmt|;
return|return
name|result
return|;
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
name|this
operator|==
name|obj
condition|)
return|return
literal|true
return|;
if|if
condition|(
operator|!
name|super
operator|.
name|equals
argument_list|(
name|obj
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
specifier|final
name|Geo3DPointSortField
name|other
init|=
operator|(
name|Geo3DPointSortField
operator|)
name|obj
decl_stmt|;
return|return
name|distanceShape
operator|.
name|equals
argument_list|(
name|other
operator|.
name|distanceShape
argument_list|)
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
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"<outsideDistanceShape:"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|'"'
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|getField
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|'"'
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|" shape="
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|distanceShape
argument_list|)
expr_stmt|;
if|if
condition|(
name|Double
operator|.
name|POSITIVE_INFINITY
operator|!=
name|getMissingValue
argument_list|()
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
literal|" missingValue="
operator|+
name|getMissingValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|append
argument_list|(
literal|'>'
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

