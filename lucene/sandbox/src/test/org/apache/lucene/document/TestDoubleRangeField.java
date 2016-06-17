begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.document
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
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
name|LuceneTestCase
import|;
end_import

begin_comment
comment|/**  * Random testing for RangeField type.  **/
end_comment

begin_class
DECL|class|TestDoubleRangeField
specifier|public
class|class
name|TestDoubleRangeField
extends|extends
name|LuceneTestCase
block|{
DECL|field|FIELD_NAME
specifier|private
specifier|static
specifier|final
name|String
name|FIELD_NAME
init|=
literal|"rangeField"
decl_stmt|;
comment|/** test illegal NaN range values */
DECL|method|testIllegalNaNValues
specifier|public
name|void
name|testIllegalNaNValues
parameter_list|()
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|IllegalArgumentException
name|expected
decl_stmt|;
name|expected
operator|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|doc
operator|.
name|add
argument_list|(
operator|new
name|DoubleRangeField
argument_list|(
name|FIELD_NAME
argument_list|,
operator|new
name|double
index|[]
block|{
name|Double
operator|.
name|NaN
block|}
argument_list|,
operator|new
name|double
index|[]
block|{
literal|5
block|}
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"invalid min value"
argument_list|)
argument_list|)
expr_stmt|;
name|expected
operator|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|doc
operator|.
name|add
argument_list|(
operator|new
name|DoubleRangeField
argument_list|(
name|FIELD_NAME
argument_list|,
operator|new
name|double
index|[]
block|{
literal|5
block|}
argument_list|,
operator|new
name|double
index|[]
block|{
name|Double
operator|.
name|NaN
block|}
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"invalid max value"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** min/max array sizes must agree */
DECL|method|testUnevenArrays
specifier|public
name|void
name|testUnevenArrays
parameter_list|()
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|IllegalArgumentException
name|expected
decl_stmt|;
name|expected
operator|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|doc
operator|.
name|add
argument_list|(
operator|new
name|DoubleRangeField
argument_list|(
name|FIELD_NAME
argument_list|,
operator|new
name|double
index|[]
block|{
literal|5
block|,
literal|6
block|}
argument_list|,
operator|new
name|double
index|[]
block|{
literal|5
block|}
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"min/max ranges must agree"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** dimensions greater than 4 not supported */
DECL|method|testOversizeDimensions
specifier|public
name|void
name|testOversizeDimensions
parameter_list|()
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|IllegalArgumentException
name|expected
decl_stmt|;
name|expected
operator|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|doc
operator|.
name|add
argument_list|(
operator|new
name|DoubleRangeField
argument_list|(
name|FIELD_NAME
argument_list|,
operator|new
name|double
index|[]
block|{
literal|1
block|,
literal|2
block|,
literal|3
block|,
literal|4
block|,
literal|5
block|}
argument_list|,
operator|new
name|double
index|[]
block|{
literal|5
block|}
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"does not support greater than 4 dimensions"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** min cannot be greater than max */
DECL|method|testMinGreaterThanMax
specifier|public
name|void
name|testMinGreaterThanMax
parameter_list|()
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|IllegalArgumentException
name|expected
decl_stmt|;
name|expected
operator|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|doc
operator|.
name|add
argument_list|(
operator|new
name|DoubleRangeField
argument_list|(
name|FIELD_NAME
argument_list|,
operator|new
name|double
index|[]
block|{
literal|3
block|,
literal|4
block|}
argument_list|,
operator|new
name|double
index|[]
block|{
literal|1
block|,
literal|2
block|}
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"is greater than max value"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
