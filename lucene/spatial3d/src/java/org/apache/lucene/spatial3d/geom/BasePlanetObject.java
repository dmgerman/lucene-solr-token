begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.spatial3d.geom
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial3d
operator|.
name|geom
package|;
end_package

begin_comment
comment|/**  * All Geo3D shapes can derive from this base class, which furnishes  * some common code  *  * @lucene.internal  */
end_comment

begin_class
DECL|class|BasePlanetObject
specifier|public
specifier|abstract
class|class
name|BasePlanetObject
block|{
comment|/** This is the planet model embedded in all objects derived from this    * class. */
DECL|field|planetModel
specifier|protected
specifier|final
name|PlanetModel
name|planetModel
decl_stmt|;
comment|/** Constructor creating class instance given a planet model.    * @param planetModel is the planet model.    */
DECL|method|BasePlanetObject
specifier|public
name|BasePlanetObject
parameter_list|(
specifier|final
name|PlanetModel
name|planetModel
parameter_list|)
block|{
name|this
operator|.
name|planetModel
operator|=
name|planetModel
expr_stmt|;
block|}
comment|/** Returns the {@link PlanetModel} provided when this shape was created. */
DECL|method|getPlanetModel
specifier|public
name|PlanetModel
name|getPlanetModel
parameter_list|()
block|{
return|return
name|planetModel
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
name|planetModel
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
specifier|final
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|BasePlanetObject
operator|)
condition|)
return|return
literal|false
return|;
return|return
name|planetModel
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|BasePlanetObject
operator|)
name|o
operator|)
operator|.
name|planetModel
argument_list|)
return|;
block|}
block|}
end_class

end_unit

