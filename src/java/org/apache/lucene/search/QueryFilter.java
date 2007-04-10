begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/** Constrains search results to only match those which also match a provided  * query.  Results are cached, so that searches after the first on the same  * index using this filter are much faster.  *  * @version $Id$  * @deprecated use a CachingWrapperFilter with QueryWrapperFilter  */
end_comment

begin_class
DECL|class|QueryFilter
specifier|public
class|class
name|QueryFilter
extends|extends
name|QueryWrapperFilter
block|{
comment|/** Constructs a filter which only matches documents matching    *<code>query</code>.    */
DECL|method|QueryFilter
specifier|public
name|QueryFilter
parameter_list|(
name|Query
name|query
parameter_list|)
block|{
name|super
argument_list|(
name|query
argument_list|)
expr_stmt|;
block|}
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
return|return
name|super
operator|.
name|equals
argument_list|(
operator|(
name|QueryFilter
operator|)
name|o
argument_list|)
return|;
block|}
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|super
operator|.
name|hashCode
argument_list|()
operator|^
literal|0x923F64B9
return|;
block|}
block|}
end_class

end_unit

