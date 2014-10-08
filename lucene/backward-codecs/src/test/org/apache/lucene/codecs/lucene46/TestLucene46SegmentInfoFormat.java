begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.lucene46
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene46
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
name|codecs
operator|.
name|Codec
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
name|index
operator|.
name|BaseSegmentInfoFormatTestCase
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
name|Version
import|;
end_import

begin_comment
comment|/**  * Tests Lucene46InfoFormat  */
end_comment

begin_class
DECL|class|TestLucene46SegmentInfoFormat
specifier|public
class|class
name|TestLucene46SegmentInfoFormat
extends|extends
name|BaseSegmentInfoFormatTestCase
block|{
annotation|@
name|Override
DECL|method|getVersions
specifier|protected
name|Version
index|[]
name|getVersions
parameter_list|()
block|{
comment|// NOTE: some of these bugfix releases we never actually "wrote",
comment|// but staying on the safe side...
return|return
operator|new
name|Version
index|[]
block|{
name|Version
operator|.
name|LUCENE_4_6_0
block|,
name|Version
operator|.
name|LUCENE_4_6_1
block|,
name|Version
operator|.
name|LUCENE_4_7_0
block|,
name|Version
operator|.
name|LUCENE_4_7_1
block|,
name|Version
operator|.
name|LUCENE_4_7_2
block|,
name|Version
operator|.
name|LUCENE_4_8_0
block|,
name|Version
operator|.
name|LUCENE_4_8_1
block|,
name|Version
operator|.
name|LUCENE_4_9_0
block|,
name|Version
operator|.
name|LUCENE_4_10_0
block|,
name|Version
operator|.
name|LUCENE_4_10_1
block|}
return|;
block|}
annotation|@
name|Override
annotation|@
name|Deprecated
DECL|method|assertIDEquals
specifier|protected
name|void
name|assertIDEquals
parameter_list|(
name|byte
index|[]
name|expected
parameter_list|,
name|byte
index|[]
name|actual
parameter_list|)
block|{
name|assertNull
argument_list|(
name|actual
argument_list|)
expr_stmt|;
comment|// we don't support IDs
block|}
annotation|@
name|Override
DECL|method|getCodec
specifier|protected
name|Codec
name|getCodec
parameter_list|()
block|{
return|return
operator|new
name|Lucene46RWCodec
argument_list|()
return|;
block|}
block|}
end_class

end_unit
