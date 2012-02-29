begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.spatial.strategy.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|strategy
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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

begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|shape
operator|.
name|Shape
import|;
end_import

begin_class
DECL|class|ShapeFieldCache
specifier|public
class|class
name|ShapeFieldCache
parameter_list|<
name|T
extends|extends
name|Shape
parameter_list|>
block|{
DECL|field|cache
specifier|private
name|List
argument_list|<
name|T
argument_list|>
index|[]
name|cache
decl_stmt|;
DECL|field|defaultLength
specifier|public
name|int
name|defaultLength
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|}
argument_list|)
DECL|method|ShapeFieldCache
specifier|public
name|ShapeFieldCache
parameter_list|(
name|int
name|length
parameter_list|,
name|int
name|defaultLength
parameter_list|)
block|{
name|cache
operator|=
operator|new
name|List
index|[
name|length
index|]
expr_stmt|;
name|this
operator|.
name|defaultLength
operator|=
name|defaultLength
expr_stmt|;
block|}
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|int
name|docid
parameter_list|,
name|T
name|s
parameter_list|)
block|{
name|List
argument_list|<
name|T
argument_list|>
name|list
init|=
name|cache
index|[
name|docid
index|]
decl_stmt|;
if|if
condition|(
name|list
operator|==
literal|null
condition|)
block|{
name|list
operator|=
name|cache
index|[
name|docid
index|]
operator|=
operator|new
name|ArrayList
argument_list|<
name|T
argument_list|>
argument_list|(
name|defaultLength
argument_list|)
expr_stmt|;
block|}
name|list
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
DECL|method|getShapes
specifier|public
name|List
argument_list|<
name|T
argument_list|>
name|getShapes
parameter_list|(
name|int
name|docid
parameter_list|)
block|{
return|return
name|cache
index|[
name|docid
index|]
return|;
block|}
block|}
end_class

end_unit

