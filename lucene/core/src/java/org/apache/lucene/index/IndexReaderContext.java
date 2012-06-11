begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * A struct like class that represents a hierarchical relationship between  * {@link IndexReader} instances.   * @lucene.experimental  */
end_comment

begin_class
DECL|class|IndexReaderContext
specifier|public
specifier|abstract
class|class
name|IndexReaderContext
block|{
comment|/** The reader context for this reader's immediate parent, or null if none */
DECL|field|parent
specifier|public
specifier|final
name|CompositeReaderContext
name|parent
decl_stmt|;
comment|/**<code>true</code> if this context struct represents the top level reader within the hierarchical context */
DECL|field|isTopLevel
specifier|public
specifier|final
name|boolean
name|isTopLevel
decl_stmt|;
comment|/** the doc base for this reader in the parent,<tt>0</tt> if parent is null */
DECL|field|docBaseInParent
specifier|public
specifier|final
name|int
name|docBaseInParent
decl_stmt|;
comment|/** the ord for this reader in the parent,<tt>0</tt> if parent is null */
DECL|field|ordInParent
specifier|public
specifier|final
name|int
name|ordInParent
decl_stmt|;
DECL|method|IndexReaderContext
name|IndexReaderContext
parameter_list|(
name|CompositeReaderContext
name|parent
parameter_list|,
name|int
name|ordInParent
parameter_list|,
name|int
name|docBaseInParent
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|this
operator|instanceof
name|CompositeReaderContext
operator|||
name|this
operator|instanceof
name|AtomicReaderContext
operator|)
condition|)
throw|throw
operator|new
name|Error
argument_list|(
literal|"This class should never be extended by custom code!"
argument_list|)
throw|;
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
name|this
operator|.
name|docBaseInParent
operator|=
name|docBaseInParent
expr_stmt|;
name|this
operator|.
name|ordInParent
operator|=
name|ordInParent
expr_stmt|;
name|this
operator|.
name|isTopLevel
operator|=
name|parent
operator|==
literal|null
expr_stmt|;
block|}
comment|/** Returns the {@link IndexReader}, this context represents. */
DECL|method|reader
specifier|public
specifier|abstract
name|IndexReader
name|reader
parameter_list|()
function_decl|;
comment|/**    * Returns the context's leaves if this context is a top-level context    * otherwise<code>null</code>. For convenience, if this is an    * {@link AtomicReaderContext} this returns itsself as the only leaf.    *<p>Note: this is convenience method since leaves can always be obtained by    * walking the context tree.    *<p><b>Warning:</b> Don't modify the returned array!    * Doing so will corrupt the internal structure of this    * {@code IndexReaderContext}.    */
DECL|method|leaves
specifier|public
specifier|abstract
name|AtomicReaderContext
index|[]
name|leaves
parameter_list|()
function_decl|;
comment|/**    * Returns the context's children iff this context is a composite context    * otherwise<code>null</code>.    *<p><b>Warning:</b> Don't modify the returned array!    * Doing so will corrupt the internal structure of this    * {@code IndexReaderContext}.    */
DECL|method|children
specifier|public
specifier|abstract
name|IndexReaderContext
index|[]
name|children
parameter_list|()
function_decl|;
block|}
end_class

end_unit

