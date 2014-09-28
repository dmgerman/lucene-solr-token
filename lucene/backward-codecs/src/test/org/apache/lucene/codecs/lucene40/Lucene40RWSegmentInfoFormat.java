begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.lucene40
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene40
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
name|SegmentInfoWriter
import|;
end_import

begin_comment
comment|/**  * Read-write version of 4.0 segmentinfo format for testing  * @deprecated for test purposes only  */
end_comment

begin_class
annotation|@
name|Deprecated
DECL|class|Lucene40RWSegmentInfoFormat
specifier|public
specifier|final
class|class
name|Lucene40RWSegmentInfoFormat
extends|extends
name|Lucene40SegmentInfoFormat
block|{
annotation|@
name|Override
DECL|method|getSegmentInfoWriter
specifier|public
name|SegmentInfoWriter
name|getSegmentInfoWriter
parameter_list|()
block|{
return|return
operator|new
name|Lucene40SegmentInfoWriter
argument_list|()
return|;
block|}
block|}
end_class

end_unit

