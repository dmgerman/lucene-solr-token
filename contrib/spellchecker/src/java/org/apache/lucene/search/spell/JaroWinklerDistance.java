begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.spell
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|spell
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_class
DECL|class|JaroWinklerDistance
specifier|public
class|class
name|JaroWinklerDistance
implements|implements
name|StringDistance
block|{
DECL|field|threshold
specifier|private
name|float
name|threshold
init|=
literal|0.7f
decl_stmt|;
DECL|method|matches
specifier|private
name|int
index|[]
name|matches
parameter_list|(
name|String
name|s1
parameter_list|,
name|String
name|s2
parameter_list|)
block|{
name|String
name|max
decl_stmt|,
name|min
decl_stmt|;
if|if
condition|(
name|s1
operator|.
name|length
argument_list|()
operator|>
name|s2
operator|.
name|length
argument_list|()
condition|)
block|{
name|max
operator|=
name|s1
expr_stmt|;
name|min
operator|=
name|s2
expr_stmt|;
block|}
else|else
block|{
name|max
operator|=
name|s2
expr_stmt|;
name|min
operator|=
name|s1
expr_stmt|;
block|}
name|int
name|range
init|=
name|Math
operator|.
name|max
argument_list|(
name|max
operator|.
name|length
argument_list|()
operator|/
literal|2
operator|-
literal|1
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|int
index|[]
name|matchIndexes
init|=
operator|new
name|int
index|[
name|min
operator|.
name|length
argument_list|()
index|]
decl_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|matchIndexes
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|boolean
index|[]
name|matchFlags
init|=
operator|new
name|boolean
index|[
name|max
operator|.
name|length
argument_list|()
index|]
decl_stmt|;
name|int
name|matches
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|mi
init|=
literal|0
init|;
name|mi
operator|<
name|min
operator|.
name|length
argument_list|()
condition|;
name|mi
operator|++
control|)
block|{
name|char
name|c1
init|=
name|min
operator|.
name|charAt
argument_list|(
name|mi
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|xi
init|=
name|Math
operator|.
name|max
argument_list|(
name|mi
operator|-
name|range
argument_list|,
literal|0
argument_list|)
init|,
name|xn
init|=
name|Math
operator|.
name|min
argument_list|(
name|mi
operator|+
name|range
operator|+
literal|1
argument_list|,
name|max
operator|.
name|length
argument_list|()
argument_list|)
init|;
name|xi
operator|<
name|xn
condition|;
name|xi
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|matchFlags
index|[
name|xi
index|]
operator|&&
name|c1
operator|==
name|max
operator|.
name|charAt
argument_list|(
name|xi
argument_list|)
condition|)
block|{
name|matchIndexes
index|[
name|mi
index|]
operator|=
name|xi
expr_stmt|;
name|matchFlags
index|[
name|xi
index|]
operator|=
literal|true
expr_stmt|;
name|matches
operator|++
expr_stmt|;
break|break;
block|}
block|}
block|}
name|char
index|[]
name|ms1
init|=
operator|new
name|char
index|[
name|matches
index|]
decl_stmt|;
name|char
index|[]
name|ms2
init|=
operator|new
name|char
index|[
name|matches
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|,
name|si
init|=
literal|0
init|;
name|i
operator|<
name|min
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|matchIndexes
index|[
name|i
index|]
operator|!=
operator|-
literal|1
condition|)
block|{
name|ms1
index|[
name|si
index|]
operator|=
name|min
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|si
operator|++
expr_stmt|;
block|}
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|,
name|si
init|=
literal|0
init|;
name|i
operator|<
name|max
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|matchFlags
index|[
name|i
index|]
condition|)
block|{
name|ms2
index|[
name|si
index|]
operator|=
name|max
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|si
operator|++
expr_stmt|;
block|}
block|}
name|int
name|transpositions
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|mi
init|=
literal|0
init|;
name|mi
operator|<
name|ms1
operator|.
name|length
condition|;
name|mi
operator|++
control|)
block|{
if|if
condition|(
name|ms1
index|[
name|mi
index|]
operator|!=
name|ms2
index|[
name|mi
index|]
condition|)
block|{
name|transpositions
operator|++
expr_stmt|;
block|}
block|}
name|int
name|prefix
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|mi
init|=
literal|0
init|;
name|mi
operator|<
name|min
operator|.
name|length
argument_list|()
condition|;
name|mi
operator|++
control|)
block|{
if|if
condition|(
name|s1
operator|.
name|charAt
argument_list|(
name|mi
argument_list|)
operator|==
name|s2
operator|.
name|charAt
argument_list|(
name|mi
argument_list|)
condition|)
block|{
name|prefix
operator|++
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
return|return
operator|new
name|int
index|[]
block|{
name|matches
block|,
name|transpositions
operator|/
literal|2
block|,
name|prefix
block|,
name|max
operator|.
name|length
argument_list|()
block|}
return|;
block|}
DECL|method|getDistance
specifier|public
name|float
name|getDistance
parameter_list|(
name|String
name|s1
parameter_list|,
name|String
name|s2
parameter_list|)
block|{
name|int
index|[]
name|mtp
init|=
name|matches
argument_list|(
name|s1
argument_list|,
name|s2
argument_list|)
decl_stmt|;
name|float
name|m
init|=
operator|(
name|float
operator|)
name|mtp
index|[
literal|0
index|]
decl_stmt|;
if|if
condition|(
name|m
operator|==
literal|0
condition|)
block|{
return|return
literal|0f
return|;
block|}
name|float
name|j
init|=
operator|(
operator|(
name|m
operator|/
name|s1
operator|.
name|length
argument_list|()
operator|+
name|m
operator|/
name|s2
operator|.
name|length
argument_list|()
operator|+
operator|(
name|m
operator|-
name|mtp
index|[
literal|1
index|]
operator|)
operator|/
name|m
operator|)
operator|)
operator|/
literal|3
decl_stmt|;
name|float
name|jw
init|=
name|j
operator|<
name|getThreshold
argument_list|()
condition|?
name|j
else|:
name|j
operator|+
name|Math
operator|.
name|min
argument_list|(
literal|0.1f
argument_list|,
literal|1f
operator|/
name|mtp
index|[
literal|3
index|]
argument_list|)
operator|*
name|mtp
index|[
literal|2
index|]
operator|*
operator|(
literal|1
operator|-
name|j
operator|)
decl_stmt|;
return|return
name|jw
return|;
block|}
comment|/**    * Sets the threshold used to determine when Winkler bonus should be used.    * Set to a negative value to get the Jaro distance.    * @param threshold the new value of the threshold    */
DECL|method|setThreshold
specifier|public
name|void
name|setThreshold
parameter_list|(
name|float
name|threshold
parameter_list|)
block|{
name|this
operator|.
name|threshold
operator|=
name|threshold
expr_stmt|;
block|}
comment|/**    * Returns the current value of the threshold used for adding the Winkler bonus.    * The default value is 0.7.    * @return the current value of the threshold    */
DECL|method|getThreshold
specifier|public
name|float
name|getThreshold
parameter_list|()
block|{
return|return
name|threshold
return|;
block|}
block|}
end_class

end_unit

