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
name|Method
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * A utility for keeping backwards compatibility on previously abstract methods  * (or similar replacements).  *<p>Before the replacement method can be made abstract, the old method must kept deprecated.  * If somebody still overrides the deprecated method in a non-final class,  * you must keep track, of this and maybe delegate to the old method in the subclass.  * The cost of reflection is minimized by the following usage of this class:</p>  *<p>Define<strong>static final</strong> fields in the base class ({@code BaseClass}),  * where the old and new method are declared:</p>  *<pre>  *  static final VirtualMethod&lt;BaseClass&gt; newMethod =  *   new VirtualMethod&lt;BaseClass&gt;(BaseClass.class, "newName", parameters...);  *  static final VirtualMethod&lt;BaseClass&gt; oldMethod =  *   new VirtualMethod&lt;BaseClass&gt;(BaseClass.class, "oldName", parameters...);  *</pre>  *<p>This enforces the singleton status of these objects, as the maintenance of the cache would be too costly else.  * If you try to create a second instance of for the same method/{@code baseClass} combination, an exception is thrown.  *<p>To detect if e.g. the old method was overridden by a more far subclass on the inheritance path to the current  * instance's class, use a<strong>non-static</strong> field:</p>  *<pre>  *  final boolean isDeprecatedMethodOverridden =  *   oldMethod.getImplementationDistance(this.getClass())> newMethod.getImplementationDistance(this.getClass());  *  *<em>// alternatively (more readable):</em>  *  final boolean isDeprecatedMethodOverridden =  *   VirtualMethod.compareImplementationDistance(this.getClass(), oldMethod, newMethod)> 0  *</pre>   *<p>{@link #getImplementationDistance} returns the distance of the subclass that overrides this method.  * The one with the larger distance should be used preferable.  * This way also more complicated method rename scenarios can be handled  * (think of 2.9 {@code TokenStream} deprecations).</p>  *  * @lucene.internal  */
end_comment

begin_class
DECL|class|VirtualMethod
specifier|public
specifier|final
class|class
name|VirtualMethod
parameter_list|<
name|C
parameter_list|>
block|{
DECL|field|singletonSet
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|Method
argument_list|>
name|singletonSet
init|=
name|Collections
operator|.
name|synchronizedSet
argument_list|(
operator|new
name|HashSet
argument_list|<
name|Method
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|baseClass
specifier|private
specifier|final
name|Class
argument_list|<
name|C
argument_list|>
name|baseClass
decl_stmt|;
DECL|field|method
specifier|private
specifier|final
name|String
name|method
decl_stmt|;
DECL|field|parameters
specifier|private
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
index|[]
name|parameters
decl_stmt|;
DECL|field|cache
specifier|private
specifier|final
name|WeakIdentityMap
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|C
argument_list|>
argument_list|,
name|Integer
argument_list|>
name|cache
init|=
name|WeakIdentityMap
operator|.
name|newConcurrentHashMap
argument_list|()
decl_stmt|;
comment|/**    * Creates a new instance for the given {@code baseClass} and method declaration.    * @throws UnsupportedOperationException if you create a second instance of the same    *  {@code baseClass} and method declaration combination. This enforces the singleton status.    * @throws IllegalArgumentException if {@code baseClass} does not declare the given method.    */
DECL|method|VirtualMethod
specifier|public
name|VirtualMethod
parameter_list|(
name|Class
argument_list|<
name|C
argument_list|>
name|baseClass
parameter_list|,
name|String
name|method
parameter_list|,
name|Class
argument_list|<
name|?
argument_list|>
modifier|...
name|parameters
parameter_list|)
block|{
name|this
operator|.
name|baseClass
operator|=
name|baseClass
expr_stmt|;
name|this
operator|.
name|method
operator|=
name|method
expr_stmt|;
name|this
operator|.
name|parameters
operator|=
name|parameters
expr_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|singletonSet
operator|.
name|add
argument_list|(
name|baseClass
operator|.
name|getDeclaredMethod
argument_list|(
name|method
argument_list|,
name|parameters
argument_list|)
argument_list|)
condition|)
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"VirtualMethod instances must be singletons and therefore "
operator|+
literal|"assigned to static final members in the same class, they use as baseClass ctor param."
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|nsme
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|baseClass
operator|.
name|getName
argument_list|()
operator|+
literal|" has no such method: "
operator|+
name|nsme
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|/**    * Returns the distance from the {@code baseClass} in which this method is overridden/implemented    * in the inheritance path between {@code baseClass} and the given subclass {@code subclazz}.    * @return 0 iff not overridden, else the distance to the base class    */
DECL|method|getImplementationDistance
specifier|public
name|int
name|getImplementationDistance
parameter_list|(
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|C
argument_list|>
name|subclazz
parameter_list|)
block|{
name|Integer
name|distance
init|=
name|cache
operator|.
name|get
argument_list|(
name|subclazz
argument_list|)
decl_stmt|;
if|if
condition|(
name|distance
operator|==
literal|null
condition|)
block|{
comment|// we have the slight chance that another thread may do the same, but who cares?
name|cache
operator|.
name|put
argument_list|(
name|subclazz
argument_list|,
name|distance
operator|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|reflectImplementationDistance
argument_list|(
name|subclazz
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|distance
operator|.
name|intValue
argument_list|()
return|;
block|}
comment|/**    * Returns, if this method is overridden/implemented in the inheritance path between    * {@code baseClass} and the given subclass {@code subclazz}.    *<p>You can use this method to detect if a method that should normally be final was overridden    * by the given instance's class.    * @return {@code false} iff not overridden    */
DECL|method|isOverriddenAsOf
specifier|public
name|boolean
name|isOverriddenAsOf
parameter_list|(
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|C
argument_list|>
name|subclazz
parameter_list|)
block|{
return|return
name|getImplementationDistance
argument_list|(
name|subclazz
argument_list|)
operator|>
literal|0
return|;
block|}
DECL|method|reflectImplementationDistance
specifier|private
name|int
name|reflectImplementationDistance
parameter_list|(
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|C
argument_list|>
name|subclazz
parameter_list|)
block|{
if|if
condition|(
operator|!
name|baseClass
operator|.
name|isAssignableFrom
argument_list|(
name|subclazz
argument_list|)
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|subclazz
operator|.
name|getName
argument_list|()
operator|+
literal|" is not a subclass of "
operator|+
name|baseClass
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
name|boolean
name|overridden
init|=
literal|false
decl_stmt|;
name|int
name|distance
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
init|=
name|subclazz
init|;
name|clazz
operator|!=
name|baseClass
operator|&&
name|clazz
operator|!=
literal|null
condition|;
name|clazz
operator|=
name|clazz
operator|.
name|getSuperclass
argument_list|()
control|)
block|{
comment|// lookup method, if success mark as overridden
if|if
condition|(
operator|!
name|overridden
condition|)
block|{
try|try
block|{
name|clazz
operator|.
name|getDeclaredMethod
argument_list|(
name|method
argument_list|,
name|parameters
argument_list|)
expr_stmt|;
name|overridden
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|nsme
parameter_list|)
block|{         }
block|}
comment|// increment distance if overridden
if|if
condition|(
name|overridden
condition|)
name|distance
operator|++
expr_stmt|;
block|}
return|return
name|distance
return|;
block|}
comment|/**    * Utility method that compares the implementation/override distance of two methods.    * @return<ul>    *<li>&gt; 1, iff {@code m1} is overridden/implemented in a subclass of the class overriding/declaring {@code m2}    *<li>&lt; 1, iff {@code m2} is overridden in a subclass of the class overriding/declaring {@code m1}    *<li>0, iff both methods are overridden in the same class (or are not overridden at all)    *</ul>    */
DECL|method|compareImplementationDistance
specifier|public
specifier|static
parameter_list|<
name|C
parameter_list|>
name|int
name|compareImplementationDistance
parameter_list|(
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|C
argument_list|>
name|clazz
parameter_list|,
specifier|final
name|VirtualMethod
argument_list|<
name|C
argument_list|>
name|m1
parameter_list|,
specifier|final
name|VirtualMethod
argument_list|<
name|C
argument_list|>
name|m2
parameter_list|)
block|{
return|return
name|Integer
operator|.
name|valueOf
argument_list|(
name|m1
operator|.
name|getImplementationDistance
argument_list|(
name|clazz
argument_list|)
argument_list|)
operator|.
name|compareTo
argument_list|(
name|m2
operator|.
name|getImplementationDistance
argument_list|(
name|clazz
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

