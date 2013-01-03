begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.classification
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|classification
package|;
end_package

begin_comment
comment|/**  * The result of a call to {@link Classifier#assignClass(String)} holding an assigned class of type<code>T</code> and a score.  * @lucene.experimental  */
end_comment

begin_class
DECL|class|ClassificationResult
specifier|public
class|class
name|ClassificationResult
parameter_list|<
name|T
parameter_list|>
block|{
DECL|field|assignedClass
specifier|private
name|T
name|assignedClass
decl_stmt|;
DECL|field|score
specifier|private
name|double
name|score
decl_stmt|;
comment|/**    * Constructor    * @param assignedClass the class<code>T</code> assigned by a {@link Classifier}    * @param score the score for the assignedClass as a<code>double</code>    */
DECL|method|ClassificationResult
specifier|public
name|ClassificationResult
parameter_list|(
name|T
name|assignedClass
parameter_list|,
name|double
name|score
parameter_list|)
block|{
name|this
operator|.
name|assignedClass
operator|=
name|assignedClass
expr_stmt|;
name|this
operator|.
name|score
operator|=
name|score
expr_stmt|;
block|}
comment|/**    * retrieve the result class    * @return a<code>T</code> representing an assigned class    */
DECL|method|getAssignedClass
specifier|public
name|T
name|getAssignedClass
parameter_list|()
block|{
return|return
name|assignedClass
return|;
block|}
comment|/**    * retrieve the result score    * @return a<code>double</code> representing a result score    */
DECL|method|getScore
specifier|public
name|double
name|getScore
parameter_list|()
block|{
return|return
name|score
return|;
block|}
block|}
end_class

end_unit

