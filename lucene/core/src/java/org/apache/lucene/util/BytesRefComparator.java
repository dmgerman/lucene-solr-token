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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import

begin_comment
comment|/** Specialized {@link BytesRef} comparator that  * {@link FixedLengthBytesRefArray#iterator(Comparator)} has optimizations  * for.  * @lucene.internal */
end_comment

begin_class
DECL|class|BytesRefComparator
specifier|public
specifier|abstract
class|class
name|BytesRefComparator
implements|implements
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
block|{
DECL|field|comparedBytesCount
specifier|final
name|int
name|comparedBytesCount
decl_stmt|;
comment|/** Sole constructor.    * @param comparedBytesCount the maximum number of bytes to compare. */
DECL|method|BytesRefComparator
specifier|protected
name|BytesRefComparator
parameter_list|(
name|int
name|comparedBytesCount
parameter_list|)
block|{
name|this
operator|.
name|comparedBytesCount
operator|=
name|comparedBytesCount
expr_stmt|;
block|}
comment|/** Return the unsigned byte to use for comparison at index {@code i}, or    * {@code -1} if all bytes that are useful for comparisons are exhausted.    * This may only be called with a value of {@code i} between {@code 0}    * included and {@code comparedBytesCount} excluded. */
DECL|method|byteAt
specifier|protected
specifier|abstract
name|int
name|byteAt
parameter_list|(
name|BytesRef
name|ref
parameter_list|,
name|int
name|i
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|compare
specifier|public
name|int
name|compare
parameter_list|(
name|BytesRef
name|o1
parameter_list|,
name|BytesRef
name|o2
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|comparedBytesCount
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|int
name|b1
init|=
name|byteAt
argument_list|(
name|o1
argument_list|,
name|i
argument_list|)
decl_stmt|;
specifier|final
name|int
name|b2
init|=
name|byteAt
argument_list|(
name|o2
argument_list|,
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|b1
operator|!=
name|b2
condition|)
block|{
return|return
name|b1
operator|-
name|b2
return|;
block|}
elseif|else
if|if
condition|(
name|b1
operator|==
operator|-
literal|1
condition|)
block|{
break|break;
block|}
block|}
return|return
literal|0
return|;
block|}
block|}
end_class

end_unit

