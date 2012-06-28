begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.spatial.vector
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|vector
package|;
end_package

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
name|SpatialFieldInfo
import|;
end_import

begin_comment
comment|/**  * @lucene.experimental  */
end_comment

begin_class
DECL|class|TwoDoublesFieldInfo
specifier|public
class|class
name|TwoDoublesFieldInfo
implements|implements
name|SpatialFieldInfo
block|{
DECL|field|SUFFIX_X
specifier|public
specifier|static
specifier|final
name|String
name|SUFFIX_X
init|=
literal|"__x"
decl_stmt|;
DECL|field|SUFFIX_Y
specifier|public
specifier|static
specifier|final
name|String
name|SUFFIX_Y
init|=
literal|"__y"
decl_stmt|;
DECL|field|fieldName
specifier|private
specifier|final
name|String
name|fieldName
decl_stmt|;
DECL|field|fieldNameX
specifier|private
specifier|final
name|String
name|fieldNameX
decl_stmt|;
DECL|field|fieldNameY
specifier|private
specifier|final
name|String
name|fieldNameY
decl_stmt|;
DECL|method|TwoDoublesFieldInfo
specifier|public
name|TwoDoublesFieldInfo
parameter_list|(
name|String
name|fieldNamePrefix
parameter_list|)
block|{
name|fieldName
operator|=
name|fieldNamePrefix
expr_stmt|;
name|fieldNameX
operator|=
name|fieldNamePrefix
operator|+
name|SUFFIX_X
expr_stmt|;
name|fieldNameY
operator|=
name|fieldNamePrefix
operator|+
name|SUFFIX_Y
expr_stmt|;
block|}
DECL|method|getFieldName
specifier|public
name|String
name|getFieldName
parameter_list|()
block|{
return|return
name|fieldName
return|;
block|}
DECL|method|getFieldNameX
specifier|public
name|String
name|getFieldNameX
parameter_list|()
block|{
return|return
name|fieldNameX
return|;
block|}
DECL|method|getFieldNameY
specifier|public
name|String
name|getFieldNameY
parameter_list|()
block|{
return|return
name|fieldNameY
return|;
block|}
block|}
end_class

end_unit

