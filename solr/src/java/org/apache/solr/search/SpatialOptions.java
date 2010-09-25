begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
operator|.
name|SchemaField
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
name|geometry
operator|.
name|DistanceUnits
import|;
end_import

begin_comment
comment|/**  *  *  **/
end_comment

begin_class
DECL|class|SpatialOptions
specifier|public
class|class
name|SpatialOptions
block|{
DECL|field|pointStr
specifier|public
name|String
name|pointStr
decl_stmt|;
DECL|field|distance
specifier|public
name|double
name|distance
decl_stmt|;
DECL|field|field
specifier|public
name|SchemaField
name|field
decl_stmt|;
DECL|field|measStr
specifier|public
name|String
name|measStr
decl_stmt|;
DECL|field|radius
specifier|public
name|double
name|radius
decl_stmt|;
DECL|field|units
specifier|public
name|DistanceUnits
name|units
decl_stmt|;
comment|/** Just do a "bounding box" - or any other quicker method / shape that    * still encompasses all of the points of interest, but may also encompass    * points outside.    */
DECL|field|bbox
specifier|public
name|boolean
name|bbox
decl_stmt|;
DECL|method|SpatialOptions
specifier|public
name|SpatialOptions
parameter_list|()
block|{   }
DECL|method|SpatialOptions
specifier|public
name|SpatialOptions
parameter_list|(
name|String
name|pointStr
parameter_list|,
name|double
name|dist
parameter_list|,
name|SchemaField
name|sf
parameter_list|,
name|String
name|measStr
parameter_list|,
name|double
name|radius
parameter_list|)
block|{
name|this
argument_list|(
name|pointStr
argument_list|,
name|dist
argument_list|,
name|sf
argument_list|,
name|measStr
argument_list|,
name|radius
argument_list|,
name|DistanceUnits
operator|.
name|MILES
argument_list|)
expr_stmt|;
block|}
DECL|method|SpatialOptions
specifier|public
name|SpatialOptions
parameter_list|(
name|String
name|pointStr
parameter_list|,
name|double
name|dist
parameter_list|,
name|SchemaField
name|sf
parameter_list|,
name|String
name|measStr
parameter_list|,
name|double
name|radius
parameter_list|,
name|DistanceUnits
name|units
parameter_list|)
block|{
name|this
operator|.
name|pointStr
operator|=
name|pointStr
expr_stmt|;
name|this
operator|.
name|distance
operator|=
name|dist
expr_stmt|;
name|this
operator|.
name|field
operator|=
name|sf
expr_stmt|;
name|this
operator|.
name|measStr
operator|=
name|measStr
expr_stmt|;
name|this
operator|.
name|radius
operator|=
name|radius
expr_stmt|;
name|this
operator|.
name|units
operator|=
name|units
expr_stmt|;
block|}
block|}
end_class

end_unit

