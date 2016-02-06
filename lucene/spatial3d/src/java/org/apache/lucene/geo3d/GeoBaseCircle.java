begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.geo3d
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|geo3d
package|;
end_package

begin_comment
comment|/**  * GeoCircles have all the characteristics of GeoBaseDistanceShapes, plus GeoSizeable.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|GeoBaseCircle
specifier|public
specifier|abstract
class|class
name|GeoBaseCircle
extends|extends
name|GeoBaseDistanceShape
implements|implements
name|GeoCircle
block|{
comment|/** Constructor.    *@param planetModel is the planet model to use.    */
DECL|method|GeoBaseCircle
specifier|public
name|GeoBaseCircle
parameter_list|(
specifier|final
name|PlanetModel
name|planetModel
parameter_list|)
block|{
name|super
argument_list|(
name|planetModel
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

