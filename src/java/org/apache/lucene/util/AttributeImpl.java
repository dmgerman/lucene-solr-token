begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
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
name|Serializable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Field
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Modifier
import|;
end_import

begin_comment
comment|/**  * Base class for Attributes that can be added to a   * {@link org.apache.lucene.util.AttributeSource}.  *<p>  * Attributes are used to add data in a dynamic, yet type-safe way to a source  * of usually streamed objects, e. g. a {@link org.apache.lucene.analysis.TokenStream}.  */
end_comment

begin_class
DECL|class|AttributeImpl
specifier|public
specifier|abstract
class|class
name|AttributeImpl
implements|implements
name|Cloneable
implements|,
name|Serializable
block|{
comment|/**    * Clears the values in this Attribute and resets it to its     * default value.    */
DECL|method|clear
specifier|public
specifier|abstract
name|void
name|clear
parameter_list|()
function_decl|;
comment|/**    * The default implementation of this method accesses all declared    * fields of this object and prints the values in the following syntax:    *     *<pre>    *   public String toString() {    *     return "start=" + startOffset + ",end=" + endOffset;    *   }    *</pre>    *     * This method may be overridden by subclasses.    */
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuffer
name|buffer
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|Class
name|clazz
init|=
name|this
operator|.
name|getClass
argument_list|()
decl_stmt|;
name|Field
index|[]
name|fields
init|=
name|clazz
operator|.
name|getDeclaredFields
argument_list|()
decl_stmt|;
try|try
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|fields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Field
name|f
init|=
name|fields
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|Modifier
operator|.
name|isStatic
argument_list|(
name|f
operator|.
name|getModifiers
argument_list|()
argument_list|)
condition|)
continue|continue;
name|f
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Object
name|value
init|=
name|f
operator|.
name|get
argument_list|(
name|this
argument_list|)
decl_stmt|;
if|if
condition|(
name|buffer
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
name|f
operator|.
name|getName
argument_list|()
operator|+
literal|"=null"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|buffer
operator|.
name|append
argument_list|(
name|f
operator|.
name|getName
argument_list|()
operator|+
literal|"="
operator|+
name|value
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
name|e
parameter_list|)
block|{
comment|// this should never happen, because we're just accessing fields
comment|// from 'this'
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Subclasses must implement this method and should compute    * a hashCode similar to this:    *<pre>    *   public int hashCode() {    *     int code = startOffset;    *     code = code * 31 + endOffset;    *     return code;    *   }    *</pre>     *     * see also {@link #equals(Object)}    */
DECL|method|hashCode
specifier|public
specifier|abstract
name|int
name|hashCode
parameter_list|()
function_decl|;
comment|/**    * All values used for computation of {@link #hashCode()}     * should be checked here for equality.    *     * see also {@link Object#equals(Object)}    */
DECL|method|equals
specifier|public
specifier|abstract
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
function_decl|;
comment|/**    * Copies the values from this Attribute into the passed-in    * target attribute. The type of the target must match the type    * of this attribute.     */
DECL|method|copyTo
specifier|public
specifier|abstract
name|void
name|copyTo
parameter_list|(
name|AttributeImpl
name|target
parameter_list|)
function_decl|;
comment|/**    * Shallow clone. Subclasses must override this if they     * need to clone any members deeply,    */
DECL|method|clone
specifier|public
name|Object
name|clone
parameter_list|()
block|{
name|Object
name|clone
init|=
literal|null
decl_stmt|;
try|try
block|{
name|clone
operator|=
name|super
operator|.
name|clone
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CloneNotSupportedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
comment|// shouldn't happen
block|}
return|return
name|clone
return|;
block|}
block|}
end_class

end_unit

