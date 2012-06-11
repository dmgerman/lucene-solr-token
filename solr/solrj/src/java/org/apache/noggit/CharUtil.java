begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.noggit
package|package
name|org
operator|.
name|apache
operator|.
name|noggit
package|;
end_package

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|CharUtil
specifier|public
class|class
name|CharUtil
block|{
comment|// belongs in number utils or charutil?
DECL|method|parseLong
specifier|public
name|long
name|parseLong
parameter_list|(
name|char
index|[]
name|arr
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
block|{
name|long
name|x
init|=
literal|0
decl_stmt|;
name|boolean
name|negative
init|=
name|arr
index|[
name|start
index|]
operator|==
literal|'-'
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|negative
condition|?
name|start
operator|+
literal|1
else|:
name|start
init|;
name|i
operator|<
name|end
condition|;
name|i
operator|++
control|)
block|{
comment|// If constructing the largest negative number, this will overflow
comment|// to the largest negative number.  This is OK since the negation of
comment|// the largest negative number is itself in two's complement.
name|x
operator|=
name|x
operator|*
literal|10
operator|+
operator|(
name|arr
index|[
name|i
index|]
operator|-
literal|'0'
operator|)
expr_stmt|;
block|}
comment|// could replace conditional-move with multiplication of sign... not sure
comment|// which is faster.
return|return
name|negative
condition|?
operator|-
name|x
else|:
name|x
return|;
block|}
DECL|method|compare
specifier|public
name|int
name|compare
parameter_list|(
name|char
index|[]
name|a
parameter_list|,
name|int
name|a_start
parameter_list|,
name|int
name|a_end
parameter_list|,
name|char
index|[]
name|b
parameter_list|,
name|int
name|b_start
parameter_list|,
name|int
name|b_end
parameter_list|)
block|{
name|int
name|a_len
init|=
name|a_end
operator|-
name|a_start
decl_stmt|;
name|int
name|b_len
init|=
name|b_end
operator|-
name|b_start
decl_stmt|;
name|int
name|len
init|=
name|Math
operator|.
name|min
argument_list|(
name|a_len
argument_list|,
name|b_len
argument_list|)
decl_stmt|;
while|while
condition|(
operator|--
name|len
operator|>=
literal|0
condition|)
block|{
name|int
name|c
init|=
name|a
index|[
name|a_start
index|]
operator|-
name|b
index|[
name|b_start
index|]
decl_stmt|;
if|if
condition|(
name|c
operator|!=
literal|0
condition|)
return|return
name|c
return|;
name|a_start
operator|++
expr_stmt|;
name|b_start
operator|++
expr_stmt|;
block|}
return|return
name|a_len
operator|-
name|b_len
return|;
block|}
block|}
end_class

end_unit

