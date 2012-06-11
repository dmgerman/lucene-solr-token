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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|ref
operator|.
name|WeakReference
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|Attribute
block|{
comment|/**    * Clears the values in this AttributeImpl and resets it to its     * default value. If this implementation implements more than one Attribute interface    * it clears all.    */
DECL|method|clear
specifier|public
specifier|abstract
name|void
name|clear
parameter_list|()
function_decl|;
comment|/**    * This method returns the current attribute values as a string in the following format    * by calling the {@link #reflectWith(AttributeReflector)} method:    *     *<ul>    *<li><em>iff {@code prependAttClass=true}:</em> {@code "AttributeClass#key=value,AttributeClass#key=value"}    *<li><em>iff {@code prependAttClass=false}:</em> {@code "key=value,key=value"}    *</ul>    *    * @see #reflectWith(AttributeReflector)    */
DECL|method|reflectAsString
specifier|public
specifier|final
name|String
name|reflectAsString
parameter_list|(
specifier|final
name|boolean
name|prependAttClass
parameter_list|)
block|{
specifier|final
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|reflectWith
argument_list|(
operator|new
name|AttributeReflector
argument_list|()
block|{
specifier|public
name|void
name|reflect
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Attribute
argument_list|>
name|attClass
parameter_list|,
name|String
name|key
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
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
name|prependAttClass
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
name|attClass
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|'#'
argument_list|)
expr_stmt|;
block|}
name|buffer
operator|.
name|append
argument_list|(
name|key
argument_list|)
operator|.
name|append
argument_list|(
literal|'='
argument_list|)
operator|.
name|append
argument_list|(
operator|(
name|value
operator|==
literal|null
operator|)
condition|?
literal|"null"
else|:
name|value
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * This method is for introspection of attributes, it should simply    * add the key/values this attribute holds to the given {@link AttributeReflector}.    *    *<p>The default implementation calls {@link AttributeReflector#reflect} for all    * non-static fields from the implementing class, using the field name as key    * and the field value as value. The Attribute class is also determined by reflection.    * Please note that the default implementation can only handle single-Attribute    * implementations.    *    *<p>Custom implementations look like this (e.g. for a combined attribute implementation):    *<pre>    *   public void reflectWith(AttributeReflector reflector) {    *     reflector.reflect(CharTermAttribute.class, "term", term());    *     reflector.reflect(PositionIncrementAttribute.class, "positionIncrement", getPositionIncrement());    *   }    *</pre>    *    *<p>If you implement this method, make sure that for each invocation, the same set of {@link Attribute}    * interfaces and keys are passed to {@link AttributeReflector#reflect} in the same order, but possibly    * different values. So don't automatically exclude e.g. {@code null} properties!    *    * @see #reflectAsString(boolean)    */
DECL|method|reflectWith
specifier|public
name|void
name|reflectWith
parameter_list|(
name|AttributeReflector
name|reflector
parameter_list|)
block|{
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|AttributeImpl
argument_list|>
name|clazz
init|=
name|this
operator|.
name|getClass
argument_list|()
decl_stmt|;
specifier|final
name|LinkedList
argument_list|<
name|WeakReference
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Attribute
argument_list|>
argument_list|>
argument_list|>
name|interfaces
init|=
name|AttributeSource
operator|.
name|getAttributeInterfaces
argument_list|(
name|clazz
argument_list|)
decl_stmt|;
if|if
condition|(
name|interfaces
operator|.
name|size
argument_list|()
operator|!=
literal|1
condition|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|clazz
operator|.
name|getName
argument_list|()
operator|+
literal|" implements more than one Attribute interface, the default reflectWith() implementation cannot handle this."
argument_list|)
throw|;
block|}
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|Attribute
argument_list|>
name|interf
init|=
name|interfaces
operator|.
name|getFirst
argument_list|()
operator|.
name|get
argument_list|()
decl_stmt|;
specifier|final
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
specifier|final
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
name|reflector
operator|.
name|reflect
argument_list|(
name|interf
argument_list|,
name|f
operator|.
name|getName
argument_list|()
argument_list|,
name|f
operator|.
name|get
argument_list|(
name|this
argument_list|)
argument_list|)
expr_stmt|;
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
block|}
comment|/**    * Copies the values from this Attribute into the passed-in    * target attribute. The target implementation must support all the    * Attributes this implementation supports.    */
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
annotation|@
name|Override
DECL|method|clone
specifier|public
name|AttributeImpl
name|clone
parameter_list|()
block|{
name|AttributeImpl
name|clone
init|=
literal|null
decl_stmt|;
try|try
block|{
name|clone
operator|=
operator|(
name|AttributeImpl
operator|)
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

