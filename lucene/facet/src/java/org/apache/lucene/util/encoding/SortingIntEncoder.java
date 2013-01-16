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
name|java
operator|.
name|util
operator|.
name|Arrays
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
comment|/**  * An {@link IntEncoderFilter} which sorts the values to encode in ascending  * order before encoding them.  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|SortingIntEncoder
specifier|public
specifier|final
class|class
name|SortingIntEncoder
extends|extends
name|IntEncoderFilter
block|{
comment|/** Initializes with the given encoder. */
DECL|method|SortingIntEncoder
specifier|public
name|SortingIntEncoder
parameter_list|(
name|IntEncoder
name|encoder
parameter_list|)
block|{
name|super
argument_list|(
name|encoder
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|encode
specifier|public
name|void
name|encode
parameter_list|(
name|IntsRef
name|values
parameter_list|,
name|BytesRef
name|buf
parameter_list|)
block|{
name|Arrays
operator|.
name|sort
argument_list|(
name|values
operator|.
name|ints
argument_list|,
name|values
operator|.
name|offset
argument_list|,
name|values
operator|.
name|offset
operator|+
name|values
operator|.
name|length
argument_list|)
expr_stmt|;
name|encoder
operator|.
name|encode
argument_list|(
name|values
argument_list|,
name|buf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createMatchingDecoder
specifier|public
name|IntDecoder
name|createMatchingDecoder
parameter_list|()
block|{
return|return
name|encoder
operator|.
name|createMatchingDecoder
argument_list|()
return|;
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
literal|"Sorting("
operator|+
name|encoder
operator|.
name|toString
argument_list|()
operator|+
literal|")"
return|;
block|}
block|}
end_class

end_unit

