begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.index.attributes
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|index
operator|.
name|attributes
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
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
name|facet
operator|.
name|index
operator|.
name|CategoryContainer
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Property that can be added to {@link CategoryAttribute}s during indexing.  * Note that properties are put in a map and could be shallow copied during  * {@link CategoryAttributeImpl#clone()}, therefore reuse of  * {@link CategoryProperty} objects is not recommended. Also extends  * {@link Serializable}, making the {@link CategoryContainer} serialization more  * elegant.  *   * @lucene.experimental  */
end_comment

begin_interface
DECL|interface|CategoryProperty
specifier|public
interface|interface
name|CategoryProperty
extends|extends
name|Serializable
block|{
comment|/**    * When adding categories with properties to a certain document, it is    * possible that the same category will be added more than once with    * different instances of the same property. This method defined how to    * treat such cases, by merging the newly added property into the one    * previously added. Implementing classes can assume that this method will    * be called only with a property of the same class.    *     * @param other    *            The category property to merge.    * @throws UnsupportedOperationException    *             If merging is prohibited for this property.    */
DECL|method|merge
specifier|public
name|void
name|merge
parameter_list|(
name|CategoryProperty
name|other
parameter_list|)
throws|throws
name|UnsupportedOperationException
function_decl|;
block|}
end_interface

end_unit

