begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.payloads
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|payloads
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Returns the maximum payload score seen, else 1 if there are no payloads on the doc.  *<p/>  * Is thread safe and completely reusable.  *  **/
end_comment

begin_class
DECL|class|MaxPayloadFunction
specifier|public
class|class
name|MaxPayloadFunction
extends|extends
name|PayloadFunction
block|{
DECL|method|currentScore
specifier|public
name|float
name|currentScore
parameter_list|(
name|int
name|docId
parameter_list|,
name|String
name|field
parameter_list|,
name|int
name|numPayloadsSeen
parameter_list|,
name|float
name|currentScore
parameter_list|,
name|float
name|currentPayloadScore
parameter_list|)
block|{
return|return
name|Math
operator|.
name|max
argument_list|(
name|currentPayloadScore
argument_list|,
name|currentScore
argument_list|)
return|;
block|}
DECL|method|docScore
specifier|public
name|float
name|docScore
parameter_list|(
name|int
name|docId
parameter_list|,
name|String
name|field
parameter_list|,
name|int
name|numPayloadsSeen
parameter_list|,
name|float
name|payloadScore
parameter_list|)
block|{
return|return
name|numPayloadsSeen
operator|>
literal|0
condition|?
name|payloadScore
else|:
literal|1
return|;
block|}
block|}
end_class

end_unit

