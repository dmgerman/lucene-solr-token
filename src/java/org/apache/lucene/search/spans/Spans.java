begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.spans
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|spans
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_comment
comment|/** Expert: an enumeration of span matches.  Used to implement span searching.  * Each span represents a range of term positions within a document.  Matches  * are enumerated in order, by increasing document number, within that by  * increasing start position and finally by increasing end position. */
end_comment

begin_interface
DECL|interface|Spans
specifier|public
interface|interface
name|Spans
block|{
comment|/** Move to the next match, returning true iff any such exists. */
DECL|method|next
name|boolean
name|next
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Skips to the first match beyond the current, whose document number is    * greater than or equal to<i>target</i>.<p>Returns true iff there is such    * a match.<p>Behaves as if written:<pre>    *   boolean skipTo(int target) {    *     do {    *       if (!next())    * 	     return false;    *     } while (target> doc());    *     return true;    *   }    *</pre>    * Most implementations are considerably more efficient than that.    */
DECL|method|skipTo
name|boolean
name|skipTo
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Returns the document number of the current match.  Initially invalid. */
DECL|method|doc
name|int
name|doc
parameter_list|()
function_decl|;
comment|/** Returns the start position of the current match.  Initially invalid. */
DECL|method|start
name|int
name|start
parameter_list|()
function_decl|;
comment|/** Returns the end position of the current match.  Initially invalid. */
DECL|method|end
name|int
name|end
parameter_list|()
function_decl|;
comment|/**    * Returns the payload data for the current span.    * This is invalid until {@link #next()} is called for    * the first time.    * This method must not be called more than once after each call    * of {@link #next()}. However, most payloads are loaded lazily,    * so if the payload data for the current position is not needed,    * this method may not be called at all for performance reasons. An ordered    * SpanQuery does not lazy load, so if you have payloads in your index and    * you do not want ordered SpanNearQuerys to collect payloads, you can    * disable collection with a constructor option.<br>    *<br>     * Note that the return type is a collection, thus the ordering should not be relied upon.     *<br/>    *<p><font color="#FF0000">    * WARNING: The status of the<b>Payloads</b> feature is experimental.    * The APIs introduced here might change in the future and will not be    * supported anymore in such a case.</font>    *    * @return a List of byte arrays containing the data of this payload, otherwise null if isPayloadAvailable is false    * @throws java.io.IOException     */
comment|// TODO: Remove warning after API has been finalized
DECL|method|getPayload
name|Collection
comment|/*<byte[]>*/
name|getPayload
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Checks if a payload can be loaded at this position.    *<p/>    * Payloads can only be loaded once per call to    * {@link #next()}.    *<p/>    *<p><font color="#FF0000">    * WARNING: The status of the<b>Payloads</b> feature is experimental.    * The APIs introduced here might change in the future and will not be    * supported anymore in such a case.</font>    *    * @return true if there is a payload available at this position that can be loaded    */
comment|// TODO: Remove warning after API has been finalized
DECL|method|isPayloadAvailable
specifier|public
name|boolean
name|isPayloadAvailable
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

