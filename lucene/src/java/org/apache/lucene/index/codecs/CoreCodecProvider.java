begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index.codecs
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|codecs
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|codecs
operator|.
name|preflex
operator|.
name|PreFlexCodec
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
name|codecs
operator|.
name|pulsing
operator|.
name|PulsingCodec
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
name|codecs
operator|.
name|simpletext
operator|.
name|SimpleTextCodec
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
name|codecs
operator|.
name|standard
operator|.
name|StandardCodec
import|;
end_import

begin_comment
comment|/**  * A CodecProvider that registers all core codecs that ship  * with Lucene.  This will not register any user codecs, but  * you can easily instantiate this class and register them  * yourself and specify per-field codecs:  *   *<pre>  *   CodecProvider cp = new CoreCodecProvider();  *   cp.register(new MyFastCodec());  *   cp.setDefaultFieldCodec("Standard");  *   cp.setFieldCodec("id", "Pulsing");  *   cp.setFieldCodec("body", "MyFastCodec");  *   IndexWriterConfig iwc = new IndexWriterConfig(analyzer);  *   iwc.setCodecProvider(cp);  *</pre>  */
end_comment

begin_class
DECL|class|CoreCodecProvider
specifier|public
class|class
name|CoreCodecProvider
extends|extends
name|CodecProvider
block|{
DECL|method|CoreCodecProvider
name|CoreCodecProvider
parameter_list|()
block|{
name|register
argument_list|(
operator|new
name|StandardCodec
argument_list|()
argument_list|)
expr_stmt|;
name|register
argument_list|(
operator|new
name|PreFlexCodec
argument_list|()
argument_list|)
expr_stmt|;
name|register
argument_list|(
operator|new
name|PulsingCodec
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|register
argument_list|(
operator|new
name|SimpleTextCodec
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

