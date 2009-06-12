begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.payloads
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|payloads
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
name|Payload
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
name|ArrayUtil
import|;
end_import

begin_comment
comment|/**  *  Encode a character array Integer as a {@link org.apache.lucene.index.Payload}.  *  **/
end_comment

begin_class
DECL|class|IntegerEncoder
specifier|public
class|class
name|IntegerEncoder
extends|extends
name|AbstractEncoder
implements|implements
name|PayloadEncoder
block|{
DECL|method|encode
specifier|public
name|Payload
name|encode
parameter_list|(
name|char
index|[]
name|buffer
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|Payload
name|result
init|=
operator|new
name|Payload
argument_list|()
decl_stmt|;
name|int
name|payload
init|=
name|ArrayUtil
operator|.
name|parseInt
argument_list|(
name|buffer
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
decl_stmt|;
comment|//TODO: improve this so that we don't have to new Strings
name|byte
index|[]
name|bytes
init|=
name|PayloadHelper
operator|.
name|encodeInt
argument_list|(
name|payload
argument_list|)
decl_stmt|;
name|result
operator|.
name|setData
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

