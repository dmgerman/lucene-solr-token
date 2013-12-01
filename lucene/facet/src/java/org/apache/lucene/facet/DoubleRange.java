begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|DoubleDocValuesField
import|;
end_import

begin_comment
comment|// javadocs
end_comment

begin_comment
comment|/** Represents a range over double values. */
end_comment

begin_class
DECL|class|DoubleRange
specifier|public
specifier|final
class|class
name|DoubleRange
extends|extends
name|Range
block|{
DECL|field|minIncl
specifier|private
specifier|final
name|double
name|minIncl
decl_stmt|;
DECL|field|maxIncl
specifier|private
specifier|final
name|double
name|maxIncl
decl_stmt|;
DECL|field|min
specifier|public
specifier|final
name|double
name|min
decl_stmt|;
DECL|field|max
specifier|public
specifier|final
name|double
name|max
decl_stmt|;
DECL|field|minInclusive
specifier|public
specifier|final
name|boolean
name|minInclusive
decl_stmt|;
DECL|field|maxInclusive
specifier|public
specifier|final
name|boolean
name|maxInclusive
decl_stmt|;
comment|/** Create a DoubleRange. */
DECL|method|DoubleRange
specifier|public
name|DoubleRange
parameter_list|(
name|String
name|label
parameter_list|,
name|double
name|min
parameter_list|,
name|boolean
name|minInclusive
parameter_list|,
name|double
name|max
parameter_list|,
name|boolean
name|maxInclusive
parameter_list|)
block|{
name|super
argument_list|(
name|label
argument_list|)
expr_stmt|;
name|this
operator|.
name|min
operator|=
name|min
expr_stmt|;
name|this
operator|.
name|max
operator|=
name|max
expr_stmt|;
name|this
operator|.
name|minInclusive
operator|=
name|minInclusive
expr_stmt|;
name|this
operator|.
name|maxInclusive
operator|=
name|maxInclusive
expr_stmt|;
comment|// TODO: if DoubleDocValuesField used
comment|// NumericUtils.doubleToSortableLong format (instead of
comment|// Double.doubleToRawLongBits) we could do comparisons
comment|// in long space
if|if
condition|(
name|Double
operator|.
name|isNaN
argument_list|(
name|min
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"min cannot be NaN"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|minInclusive
condition|)
block|{
name|min
operator|=
name|Math
operator|.
name|nextUp
argument_list|(
name|min
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|Double
operator|.
name|isNaN
argument_list|(
name|max
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"max cannot be NaN"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|maxInclusive
condition|)
block|{
comment|// Why no Math.nextDown?
name|max
operator|=
name|Math
operator|.
name|nextAfter
argument_list|(
name|max
argument_list|,
name|Double
operator|.
name|NEGATIVE_INFINITY
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|minIncl
operator|=
name|min
expr_stmt|;
name|this
operator|.
name|maxIncl
operator|=
name|max
expr_stmt|;
block|}
DECL|method|accept
specifier|public
name|boolean
name|accept
parameter_list|(
name|double
name|value
parameter_list|)
block|{
return|return
name|value
operator|>=
name|minIncl
operator|&&
name|value
operator|<=
name|maxIncl
return|;
block|}
block|}
end_class

end_unit

