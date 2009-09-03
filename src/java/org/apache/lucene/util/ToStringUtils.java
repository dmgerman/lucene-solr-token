begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Helper methods to ease implementing {@link Object#toString()}.  */
end_comment

begin_class
DECL|class|ToStringUtils
specifier|public
class|class
name|ToStringUtils
block|{
comment|/** for printing boost only if not 1.0 */
DECL|method|boost
specifier|public
specifier|static
name|String
name|boost
parameter_list|(
name|float
name|boost
parameter_list|)
block|{
if|if
condition|(
name|boost
operator|!=
literal|1.0f
condition|)
block|{
return|return
literal|"^"
operator|+
name|Float
operator|.
name|toString
argument_list|(
name|boost
argument_list|)
return|;
block|}
else|else
return|return
literal|""
return|;
block|}
block|}
end_class

end_unit

