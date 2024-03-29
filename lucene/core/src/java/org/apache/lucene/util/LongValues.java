begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
package|;
end_package

begin_comment
comment|/** Abstraction over an array of longs.  *  @lucene.internal */
end_comment

begin_class
DECL|class|LongValues
specifier|public
specifier|abstract
class|class
name|LongValues
block|{
comment|/** An instance that returns the provided value. */
DECL|field|IDENTITY
specifier|public
specifier|static
specifier|final
name|LongValues
name|IDENTITY
init|=
operator|new
name|LongValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|long
name|get
parameter_list|(
name|long
name|index
parameter_list|)
block|{
return|return
name|index
return|;
block|}
block|}
decl_stmt|;
DECL|field|ZEROES
specifier|public
specifier|static
specifier|final
name|LongValues
name|ZEROES
init|=
operator|new
name|LongValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|long
name|get
parameter_list|(
name|long
name|index
parameter_list|)
block|{
return|return
literal|0
return|;
block|}
block|}
decl_stmt|;
comment|/** Get value at<code>index</code>. */
DECL|method|get
specifier|public
specifier|abstract
name|long
name|get
parameter_list|(
name|long
name|index
parameter_list|)
function_decl|;
block|}
end_class

end_unit

