begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util.encoding
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|encoding
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
name|util
operator|.
name|BytesRef
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|IntsRef
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Decodes values encoded with {@link FourFlagsIntEncoder}.  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|FourFlagsIntDecoder
specifier|public
class|class
name|FourFlagsIntDecoder
extends|extends
name|IntDecoder
block|{
comment|/**    * Holds all combinations of<i>indicator</i> for fast decoding (saves time    * on real-time bit manipulation)    */
DECL|field|DECODE_TABLE
specifier|private
specifier|final
specifier|static
name|byte
index|[]
index|[]
name|DECODE_TABLE
init|=
operator|new
name|byte
index|[
literal|256
index|]
index|[
literal|4
index|]
decl_stmt|;
comment|/** Generating all combinations of<i>indicator</i> into separate flags. */
static|static
block|{
for|for
control|(
name|int
name|i
init|=
literal|256
init|;
name|i
operator|!=
literal|0
condition|;
control|)
block|{
operator|--
name|i
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|4
init|;
name|j
operator|!=
literal|0
condition|;
control|)
block|{
operator|--
name|j
expr_stmt|;
name|DECODE_TABLE
index|[
name|i
index|]
index|[
name|j
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
operator|(
name|i
operator|>>>
operator|(
name|j
operator|<<
literal|1
operator|)
operator|)
operator|&
literal|0x3
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|decode
specifier|public
name|void
name|decode
parameter_list|(
name|BytesRef
name|buf
parameter_list|,
name|IntsRef
name|values
parameter_list|)
block|{
name|values
operator|.
name|offset
operator|=
name|values
operator|.
name|length
operator|=
literal|0
expr_stmt|;
name|int
name|upto
init|=
name|buf
operator|.
name|offset
operator|+
name|buf
operator|.
name|length
decl_stmt|;
name|int
name|offset
init|=
name|buf
operator|.
name|offset
decl_stmt|;
while|while
condition|(
name|offset
operator|<
name|upto
condition|)
block|{
comment|// read indicator
name|int
name|indicator
init|=
name|buf
operator|.
name|bytes
index|[
name|offset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
name|int
name|ordinal
init|=
literal|0
decl_stmt|;
name|int
name|capacityNeeded
init|=
name|values
operator|.
name|length
operator|+
literal|4
decl_stmt|;
if|if
condition|(
name|values
operator|.
name|ints
operator|.
name|length
operator|<
name|capacityNeeded
condition|)
block|{
name|values
operator|.
name|grow
argument_list|(
name|capacityNeeded
argument_list|)
expr_stmt|;
block|}
while|while
condition|(
name|ordinal
operator|!=
literal|4
condition|)
block|{
name|byte
name|decodeVal
init|=
name|DECODE_TABLE
index|[
name|indicator
index|]
index|[
name|ordinal
operator|++
index|]
decl_stmt|;
if|if
condition|(
name|decodeVal
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|offset
operator|==
name|upto
condition|)
block|{
comment|// end of buffer
return|return;
block|}
comment|// it is better if the decoding is inlined like so, and not e.g.
comment|// in a utility method
name|int
name|value
init|=
literal|0
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|byte
name|b
init|=
name|buf
operator|.
name|bytes
index|[
name|offset
operator|++
index|]
decl_stmt|;
if|if
condition|(
name|b
operator|>=
literal|0
condition|)
block|{
name|values
operator|.
name|ints
index|[
name|values
operator|.
name|length
operator|++
index|]
operator|=
operator|(
operator|(
name|value
operator|<<
literal|7
operator|)
operator||
name|b
operator|)
operator|+
literal|4
expr_stmt|;
break|break;
block|}
else|else
block|{
name|value
operator|=
operator|(
name|value
operator|<<
literal|7
operator|)
operator||
operator|(
name|b
operator|&
literal|0x7F
operator|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|values
operator|.
name|ints
index|[
name|values
operator|.
name|length
operator|++
index|]
operator|=
name|decodeVal
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"FourFlags(VInt)"
return|;
block|}
block|}
end_class

end_unit

