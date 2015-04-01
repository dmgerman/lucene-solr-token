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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|DocIdSetIterator
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
name|search
operator|.
name|TwoPhaseIterator
import|;
end_import

begin_comment
comment|/** Iterates through combinations of start/end positions per-doc.  *  Each start/end position represents a range of term positions within the current document.  *  These are enumerated in order, by increasing document number, within that by  *  increasing start position and finally by increasing end position.  */
end_comment

begin_class
DECL|class|Spans
specifier|public
specifier|abstract
class|class
name|Spans
extends|extends
name|DocIdSetIterator
block|{
DECL|field|NO_MORE_POSITIONS
specifier|public
specifier|static
specifier|final
name|int
name|NO_MORE_POSITIONS
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
comment|/**    * Returns the next start position for the current doc.    * There is always at least one start/end position per doc.    * After the last start/end position at the current doc this returns {@link #NO_MORE_POSITIONS}.    */
DECL|method|nextStartPosition
specifier|public
specifier|abstract
name|int
name|nextStartPosition
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns the start position in the current doc, or -1 when {@link #nextStartPosition} was not yet called on the current doc.    * After the last start/end position at the current doc this returns {@link #NO_MORE_POSITIONS}.    */
DECL|method|startPosition
specifier|public
specifier|abstract
name|int
name|startPosition
parameter_list|()
function_decl|;
comment|/**    * Returns the end position for the current start position, or -1 when {@link #nextStartPosition} was not yet called on the current doc.    * After the last start/end position at the current doc this returns {@link #NO_MORE_POSITIONS}.    */
DECL|method|endPosition
specifier|public
specifier|abstract
name|int
name|endPosition
parameter_list|()
function_decl|;
comment|/**    * Returns the payload data for the current start/end position.    * This is only valid after {@link #nextStartPosition()}    * returned an available start position.    * This method must not be called more than once after each call    * of {@link #nextStartPosition()}. However, most payloads are loaded lazily,    * so if the payload data for the current position is not needed,    * this method may not be called at all for performance reasons.    *<br>    * Note that the return type is a collection, thus the ordering should not be relied upon.    *<br>    * @lucene.experimental    *    * @return a List of byte arrays containing the data of this payload, otherwise null if isPayloadAvailable is false    * @throws IOException if there is a low-level I/O error    */
DECL|method|getPayload
specifier|public
specifier|abstract
name|Collection
argument_list|<
name|byte
index|[]
argument_list|>
name|getPayload
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Checks if a payload can be loaded at the current start/end position.    *<p>    * Payloads can only be loaded once per call to    * {@link #nextStartPosition()}.    *    * @return true if there is a payload available at this start/end position    *              that can be loaded    */
DECL|method|isPayloadAvailable
specifier|public
specifier|abstract
name|boolean
name|isPayloadAvailable
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Optional method: Return a {@link TwoPhaseIterator} view of this    * {@link Spans}. A return value of {@code null} indicates that    * two-phase iteration is not supported.    *    * Note that the returned {@link TwoPhaseIterator}'s    * {@link TwoPhaseIterator#approximation() approximation} must    * advance synchronously with this iterator: advancing the approximation must    * advance this iterator and vice-versa.    *    * Implementing this method is typically useful on {@link Spans}s    * that have a high per-document overhead in order to confirm matches.    *    * The default implementation returns {@code null}.    */
DECL|method|asTwoPhaseIterator
specifier|public
name|TwoPhaseIterator
name|asTwoPhaseIterator
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

