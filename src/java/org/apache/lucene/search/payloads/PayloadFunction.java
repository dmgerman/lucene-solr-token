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
name|Term
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
import|;
end_import

begin_comment
comment|/**  * An abstract class that defines a way for Boosting*Query instances  * to transform the cumulative effects of payload scores for a document.  *  * @see org.apache.lucene.search.payloads.BoostingFunctionTermQuery for more information  *  *<p/>  * This class and its derivations are experimental and subject to change  *  **/
end_comment

begin_class
DECL|class|PayloadFunction
specifier|public
specifier|abstract
class|class
name|PayloadFunction
implements|implements
name|Serializable
block|{
comment|/**    * Calculate the score up to this point for this doc and field    * @param docId The current doc    * @param field The field    * @param start The start position of the matching Span    * @param end The end position of the matching Span    * @param numPayloadsSeen The number of payloads seen so far    * @param currentScore The current score so far    * @param currentPayloadScore The score for the current payload    * @return The new current Score    *    * @see org.apache.lucene.search.spans.Spans    */
DECL|method|currentScore
specifier|public
specifier|abstract
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
name|start
parameter_list|,
name|int
name|end
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
function_decl|;
comment|/**    * Calculate the final score for all the payloads seen so far for this doc/field    * @param docId The current doc    * @param field The current field    * @param numPayloadsSeen The total number of payloads seen on this document    * @param payloadScore The raw score for those payloads    * @return The final score for the payloads    */
DECL|method|docScore
specifier|public
specifier|abstract
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
function_decl|;
block|}
end_class

end_unit

